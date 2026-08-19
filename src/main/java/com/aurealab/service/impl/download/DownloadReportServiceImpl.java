package com.aurealab.service.impl.download;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.dto.response.UserTableResponseDTO;
import com.aurealab.service.DownloadReportService;
import com.aurealab.service.UserService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.NumberToText;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.DownloadException;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.ImageProvider;
import com.lowagie.text.pdf.PdfPCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;

import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.entity.OrderItemEntity;
import com.aurealab.model.inventory.entity.PurchasingEntity;
import com.aurealab.model.inventory.entity.PurchasingItemEntity;
import com.aurealab.model.aurea.entity.DocumentTemplateEntity;
import com.aurealab.model.inventory.repository.OrderRepository;
import com.aurealab.model.inventory.repository.PurchasingRepository;
import com.aurealab.model.aurea.repository.DocumentTemplateRepository;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import com.aurealab.dto.PrescriptionInventoryTableDTO;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;

@Service
@Transactional(readOnly = true)
public class DownloadReportServiceImpl implements DownloadReportService {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    Color lightGray = new Color(220, 220, 220); // Fondo de etiquetas
    Color darkGray = new Color(100, 100, 100);  // Texto de etiquetas
    Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
    Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
    Font bigTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PurchasingRepository purchasingRepository;

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private com.aurealab.service.Inventory.OrderService orderService;

    @Autowired
    private com.aurealab.service.Inventory.PurchasingService purchasingService;

    @Autowired
    private com.aurealab.service.Inventory.PurchasingRecipeService purchasingRecipeService;

    @Autowired
    private com.aurealab.service.Inventory.PrescriptionInventoryService prescriptionInventoryService;

    @Autowired
    private com.aurealab.service.Inventory.RecipeInventoryService recipeInventoryService;

    public ResponseEntity<InputStreamResource> downloadOrder(Long orderId) {
        // 1. Fetch order
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DownloadException("No se encontró la cotización con ID: " + orderId));

        // 2. Determine category
        String templateCategory = switch (order.getType()) {
            case constants.productTypes.Recipe -> "RECETARIOS";
            case constants.productTypes.SpecialControl -> "MEDICAMENTOS";
            case constants.productTypes.PublicHealth -> "MEDICAMENTOS_SP";
            default -> "RECETARIOS";
        };

        System.out.println("antes de busscar la plantilla");

        // 3. Fetch template
        DocumentTemplateEntity template = documentTemplateRepository.findByCategoryAndIsDefault(templateCategory, true)
                .orElseThrow(() -> new DownloadException("No se encontró una plantilla predeterminada para la categoría: " + templateCategory));

        System.out.println("depues de busscar la plantilla");

        // 4. Fetch company & user info (needed for company headers)
        UserDTO userDTO = userService.getUserById(jwtUtils.getCurrentUserId());
        System.out.println("despues de buscar el usuario");

        // 5. Replace variables in template HTML
        String html = template.getHtmlContent();

        // Company variables
        html = html.replace("{{ company.name }}", userDTO.getCompany().legalName() != null ? userDTO.getCompany().legalName() : "")
                .replace("{{ company.nit }}", userDTO.getCompany().nit() != null ? userDTO.getCompany().nit() : "")
                .replace("{{ company.address }}", userDTO.getCompany().address() != null ? userDTO.getCompany().address() : "")
                .replace("{{ company.phone }}", userDTO.getCompany().phone() != null ? userDTO.getCompany().phone() : "")
                .replace("{{ company.logoUrl }}", userDTO.getCompany().logoUrl() != null ? userDTO.getCompany().logoUrl() : "");

        // Order variables
        html = html.replace("{{ order.orderCode }}", order.getOrderCode() != null ? order.getOrderCode() : "")
                .replace("{{ order.createdAt }}", order.getCreatedAt() != null ? order.getCreatedAt().toLocalDate().toString() : "")
                .replace("{{ order.total }}", formatCurrency(order.getTotal()))
                .replace("{{ order.observations }}", order.getObservations() != null ? order.getObservations() : "");

        // Client variables
        if (order.getThirdParty() != null) {
            html = html.replace("{{ thirdParty.fullName }}", order.getThirdParty().getFullName() != null ? order.getThirdParty().getFullName() : "")
                    .replace("{{ thirdParty.documentNumber }}", order.getThirdParty().getDocumentNumber() != null ? order.getThirdParty().getDocumentNumber() : "")
                    .replace("{{ thirdParty.email }}", order.getThirdParty().getEmail() != null ? order.getThirdParty().getEmail() : "")
                    .replace("{{ thirdParty.phone }}", order.getThirdParty().getPhoneNumber() != null ? order.getThirdParty().getPhoneNumber() : "");
        } else {
            html = html.replace("{{ thirdParty.fullName }}", "")
                    .replace("{{ thirdParty.documentNumber }}", "")
                    .replace("{{ thirdParty.email }}", "")
                    .replace("{{ thirdParty.phone }}", "");
        }

        System.out.println("mitad de armar el html");

        // Table Items variables parsing
        int trIndex = html.indexOf("<tr");
        if (trIndex != -1 && order.getItems() != null) {
            StringBuilder tableRows = new StringBuilder();
            while (trIndex != -1) {
                int nextTrClose = html.indexOf("</tr>", trIndex);
                if (nextTrClose == -1) break;

                String trContent = html.substring(trIndex, nextTrClose + 5);
                if (trContent.contains("{{ item.")) {
                    for (OrderItemEntity item : order.getItems()) {
                        String rowHtml = trContent;

                        String productCode = "";
                        String productName = "";

                        if (order.getType().equals(constants.productTypes.Recipe)) {
                            productCode = "REC-001";
                            productName = "Recetario de Control Especial";
                        } else {
                            if (item.getInventory() != null && item.getInventory().getProduct() != null) {
                                productCode = item.getInventory().getProduct().getCode();
                                productName = item.getInventory().getProduct().getName();
                            }
                        }

                        rowHtml = rowHtml.replace("{{ item.product.code }}", productCode != null ? productCode : "")
                                .replace("{{ item.inventory.product.code }}", productCode != null ? productCode : "")
                                .replace("{{ item.product.name }}", productName != null ? productName : "")
                                .replace("{{ item.inventory.product.name }}", productName != null ? productName : "")
                                .replace("{{ item.units }}", String.valueOf(item.getUnits()))
                                .replace("{{ item.priceUnit }}", formatCurrency(item.getPriceUnit()))
                                .replace("{{ item.priceTotal }}", formatCurrency(item.getPriceTotal()));

                        tableRows.append(rowHtml).append("\n");
                    }

                    html = html.substring(0, trIndex) + tableRows.toString() + html.substring(nextTrClose + 5);
                    break;
                }
                trIndex = html.indexOf("<tr", nextTrClose);
            }
        }

        System.out.println("despues de parsear variables en el html");

        // 6. Generate PDF using HTMLWorker
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            java.util.HashMap<String, Object> providers = new java.util.HashMap<>();
            providers.put("img_provider", new ImageProvider() {
                @Override
                public Image getImage(String src, java.util.HashMap attrs, ChainedProperties chain, DocListener doc) {
                    try {
                        String base64Data = src;
                        if (src.startsWith("data:image")) {
                            int comma = src.indexOf("base64,");
                            if (comma != -1) {
                                base64Data = src.substring(comma + 7);
                            }
                        }
                        byte[] decoded = java.util.Base64.getDecoder().decode(base64Data.trim());
                        return Image.getInstance(decoded);
                    } catch (Exception e) {
                        return null;
                    }
                }
            });

            java.util.List<Element> elements = HTMLWorker.parseToList(new java.io.StringReader(html), null, providers);
            for (Element element : elements) {
                document.add(element);
            }
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DownloadException("Error al generar el PDF de la cotización: " + e.getMessage(), e);
        }

        System.out.println("termino de armar el html");

        byte[] pdfBytes = out.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);

        // 7. Configure response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=cotizacion-" + order.getOrderCode() + ".pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    public ByteArrayInputStream generateCashSessionReport(
            CashSessionDTO session,
            Set<CashMovementResponseDTO> movements,
            CashSessionSummaryDTO summary,
            byte[] logoImage) { // <--- Agregamos el logo como variable

        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- CONFIGURACIÓN DE COLORES TIPO MATERIAL ---
            Color materialPrimary = new Color(63, 81, 181); // Indigo 500
            Color materialAccent = new Color(245, 245, 245); // Light Gray
            Font whiteFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, materialPrimary);

            // --- 1. MEMBRETE (LOGO + TÍTULO) ---
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 2});

            // Celda del Logo
            if (logoImage != null && logoImage.length > 0) {
                Image logo = Image.getInstance(logoImage);
                logo.scaleToFit(80, 80);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(logoCell);
            } else {
                headerTable.addCell(new PdfPCell(new Phrase(" "))); // Celda vacía si no hay logo
            }

            // Celda del Título
            PdfPCell titleCell = new PdfPCell(new Paragraph("REPORTE DE CAJA", titleFont));
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(titleCell);

            document.add(headerTable);
            document.add(new Paragraph(" ")); // Espacio
            UserTableResponseDTO userDTO = userService.getSimplyUserById(session.openedBySystemUserId());
            // --- 2. INFORMACIÓN GENERAL (Estilo Card) ---
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            addStyledCell(infoTable, "Sesión numero:", session.id().toString(), materialAccent);
            addStyledCell(infoTable, "Fecha:", session.businessDate().toString(), materialAccent);
            addStyledCell(infoTable, "Estado:", session.status().equals(constants.configParam.statusOpen) ? "Abierto":"Cerrado", materialAccent);
            addStyledCell(infoTable, "Responsable:", userDTO.getFullName(), materialAccent);
            document.add(infoTable);
            document.add(new Paragraph(" "));

            // --- 3. RESUMEN FINANCIERO (Angular Material List Style) ---
            document.add(new Paragraph("RESUMEN DE TOTALES", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, materialPrimary)));
            document.add(new Paragraph("______________________________________________________________________________", bodyFont));

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            summaryTable.setSpacingBefore(10f);

            addSummaryRow(summaryTable, "Total Ingresos: ", formatCurrency(summary.totalIncome()));
            addSummaryRow(summaryTable, "Total Egresos: ", formatCurrency(summary.totalExpense()));
            addSummaryRow(summaryTable, "Balance Neto: ", formatCurrency(summary.netBalance()));
            addSummaryRow(summaryTable, "Efectivo Real: ", formatCurrency(summary.netCashBalance()));

            document.add(summaryTable);
            document.add(new Paragraph(" "));

            // --- 4. TABLA DE MOVIMIENTOS (Estilo Data-Table) ---
            PdfPTable table = new PdfPTable(6); // Ajustado a 6 columnas según tu nuevo código
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 1.5f, 2, 2, 1.5f});

            // Headers de la tabla
            String[] headers = {"Ref", "Cliente", "Tipo", "Producto", "Concepto", "Monto"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, whiteFont));
                cell.setBackgroundColor(materialPrimary);
                cell.setPadding(8);
                cell.setBorderColor(Color.WHITE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Filas de datos
            for (CashMovementResponseDTO move : movements) {
                table.addCell(createDataCell(move.referenceNumber(), bodyFont));
                table.addCell(createDataCell(move.customer().fullName(), bodyFont));

                String tipoLabel = move.type().equals(constants.configParam.expenseTransaction) ?
                        constants.configParam.expenseTransactionVar : constants.configParam.incomeTransactionVar;
                table.addCell(createDataCell(tipoLabel, bodyFont));

                table.addCell(createDataCell("productsStr", bodyFont));
                table.addCell(createDataCell(move.concept(), bodyFont));
                table.addCell(createDataCell(formatCurrency(move.receivedAmount()), bodyFont));
            }

            document.add(table);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // --- MÉTODOS AUXILIARES PARA ESTILIZADO ---

    private void addStyledCell(PdfPTable table, String label, String value, Color bgColor) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        cell.setBorderColor(Color.WHITE);
        cell.addElement(new Phrase(label, labelFont));
        cell.addElement(new Phrase(value, valueFont));
        table.addCell(cell);
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        PdfPCell c2 = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c1);
        table.addCell(c2);
    }

    private PdfPCell createDataCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setPadding(6);
        cell.setBorderColor(new Color(230, 230, 230));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private String formatCurrency(Object amount) {
        if (amount == null) return "$ 0";

        // Configuramos el formato para Colombia
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("es", "CO"));
        nf.setMaximumFractionDigits(0); // Eliminamos los decimales

        try {
            // Si el valor ya es numérico, lo formatea directamente
            return nf.format(amount).replace(",00", "");
        } catch (Exception e) {
            return "$ " + amount.toString();
        }
    }

    //---------- FACTURA -------------- //

    @Override
    public ResponseEntity<InputStreamResource> downloadInvoice(Long movementId) {
        // 1. Buscamos el movimiento específico (puedes crear este método en tu service)
        // Asumo que tienes un DTO que representa un solo movimiento
       // CashMovementResponseDTO move = cashMovementService.findById(movementId);
        UserDTO userDTO = userService.getUserById(jwtUtils.getCurrentUserId());
        // 2. Logo quemado o de DB
        byte[] logo = java.util.Base64.getDecoder().decode(userDTO.getCompany().logoUrl());

        // 3. Llamamos al método de generación que escribimos arriba

       // ByteArrayInputStream bis = generateInvoicePdf(move, logo, userDTO);
        ByteArrayInputStream bis = new ByteArrayInputStream( new byte[0], 1, 1);//TODO borrar


        // 4. Headers
        HttpHeaders headers = new HttpHeaders();
        //headers.add("Content-Disposition", "inline; filename=factura-" + move.referenceNumber() + ".pdf");
        headers.add("Content-Disposition", "inline; filename=factura-.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    public ByteArrayInputStream generateInvoicePdf(CashMovementResponseDTO move, byte[] logoImage, UserDTO userDTO) {
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- SECCIÓN 1: ENCABEZADO (LOGO + INFO EMPRESA + NRO) ---
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1.5f, 3f, 1.5f});

            // Logo
            if (logoImage != null) {
                Image logo = Image.getInstance(logoImage);
                logo.scaleToFit(100, 60);
                PdfPCell cell = new PdfPCell(logo);
                cell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(cell);
            } else { headerTable.addCell(createNoBorderCell("", valueFont)); }

            // Info Central (Empresa)
            PdfPCell centerCell = new PdfPCell();
            centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            centerCell.setBorder(Rectangle.NO_BORDER);
            Paragraph empName = new Paragraph(userDTO.getCompany().legalName(), bigTitleFont);
            empName.setAlignment(Element.ALIGN_CENTER);
            centerCell.addElement(empName);
            Paragraph empDetails = new Paragraph("NIT " + userDTO.getCompany().nit() + "\n"
                    + userDTO.getCompany().address() + "\n" + userDTO.getCompany().phone()+"\n" + userDTO.getCompany().email(),
                    FontFactory.getFont(FontFactory.HELVETICA, 8));
            empDetails.setAlignment(Element.ALIGN_CENTER);
            centerCell.addElement(empDetails);
            headerTable.addCell(centerCell);

            // Caja Número de Recibo (Estilo Imagen)
            PdfPCell boxCell = new PdfPCell();
            boxCell.setBackgroundColor(lightGray);
            boxCell.setPadding(0);
            PdfPTable boxInner = new PdfPTable(1);
            boxInner.setWidthPercentage(100);
            PdfPCell labelBox = new PdfPCell(new Phrase(Objects.equals(move.type(), constants.configParam.incomeTransaction) ?
                    constants.configParam.incomeTransactionPdf : constants.configParam.expenseTransactionPdf, labelFont));
            labelBox.setBackgroundColor(new Color(180, 180, 180));
            labelBox.setHorizontalAlignment(Element.ALIGN_CENTER);
            labelBox.setBorder(Rectangle.NO_BORDER);
            boxInner.addCell(labelBox);
            PdfPCell numBox = new PdfPCell(new Phrase("No. " + move.referenceNumber(), bigTitleFont));
            numBox.setHorizontalAlignment(Element.ALIGN_CENTER);
            numBox.setPadding(5);
            numBox.setBorder(Rectangle.NO_BORDER);
            boxInner.addCell(numBox);
            boxCell.addElement(boxInner);
            headerTable.addCell(boxCell);

            document.add(headerTable);
            document.add(new Paragraph(" "));

            // --- SECCIÓN 2: DATOS DEL CLIENTE (TABLA GRIS) ---
            PdfPTable clientInfo = new PdfPTable(4);
            clientInfo.setWidthPercentage(100);
            clientInfo.setWidths(new float[]{1f, 3f, 1f, 1.5f});

            addTableRow(clientInfo, "SEÑOR(ES)", move.customer().fullName(), "FECHA", move.createdAt().toLocalDate().toString(), lightGray);
            addTableRow(clientInfo, "DIRECCIÓN", "CALLE 3B 96 64", "", "", lightGray); // Ajustar según DTO
            addTableRow(clientInfo, "CIUDAD", "Cali", "", move.createdAt().toLocalDate().toString(), lightGray);

            // Fila Mixta (Teléfono, Método de Pago)
            addMixedClientRow(clientInfo, move, lightGray);

            document.add(clientInfo);
            document.add(new Paragraph(" "));

            // --- SECCIÓN 3: CONCEPTO Y VALOR ---
            PdfPTable detailTable = new PdfPTable(2);
            detailTable.setWidthPercentage(100);
            detailTable.setWidths(new float[]{4f, 1f});

            PdfPCell hConcept = new PdfPCell(new Phrase("CONCEPTO", labelFont));
            hConcept.setBackgroundColor(lightGray);
            hConcept.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailTable.addCell(hConcept);

            PdfPCell hValue = new PdfPCell(new Phrase("VALOR", labelFont));
            hValue.setBackgroundColor(lightGray);
            hValue.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailTable.addCell(hValue);

            PdfPCell contentCell = new PdfPCell(new Phrase("productsStr" + " - " + move.concept(), valueFont));
            contentCell.setMinimumHeight(150f); // Espacio para que se vea como la imagen
            detailTable.addCell(contentCell);

            PdfPCell priceCell = new PdfPCell(new Phrase(formatCurrency(move.receivedAmount()), valueFont));
            priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            detailTable.addCell(priceCell);

            document.add(detailTable);

            // --- SECCIÓN 4: TOTALES ---
            PdfPTable totalsTable = new PdfPTable(3);
            totalsTable.setWidthPercentage(100);
            totalsTable.setWidths(new float[]{4f, 1f, 1f});

            // Celda de observación (licencia...)
            PdfPCell obsCell = new PdfPCell(new Phrase(formatCurrency(new NumberToText().convertir(move.receivedAmount())) + " PESOS M/CTE",
                    FontFactory.getFont(FontFactory.HELVETICA, 7)));
            obsCell.setBackgroundColor(lightGray);
            totalsTable.addCell(obsCell);

            // Subtotal y Total
            totalsTable.addCell(new PdfPCell(new Phrase("Saldo", valueFont)));
            PdfPCell subV = new PdfPCell(new Phrase(formatCurrency(move.charge().balance()), valueFont));
            subV.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.addCell(subV);

            totalsTable.addCell(createNoBorderCell("", valueFont));
            PdfPCell totalL = new PdfPCell(new Phrase("Recibido", valueFont));
            totalL.setBackgroundColor(lightGray);
            totalsTable.addCell(totalL);
            PdfPCell totalV = new PdfPCell(new Phrase(formatCurrency(move.receivedAmount()), valueFont));
            totalV.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.addCell(totalV);

            document.add(totalsTable);

            document.close();
        } catch (Exception e) { e.printStackTrace(); }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Métodos de apoyo específicos para factura
    private void addInvoiceInfoCell(PdfPTable table, String label, String value, Font bFont, Font nFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        Phrase p = new Phrase();
        p.add(new Chunk(label + " ", bFont));
        p.add(new Chunk(value != null ? value : "N/A", nFont));
        cell.addElement(p);
        table.addCell(cell);
    }

    private PdfPCell createNoBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void addTableRow(PdfPTable table, String label1, String val1, String label2, String val2, Color gray) {
        PdfPCell l1 = new PdfPCell(new Phrase(label1, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7)));
        l1.setBackgroundColor(gray);
        l1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(l1);
        table.addCell(new PdfPCell(new Phrase(val1, FontFactory.getFont(FontFactory.HELVETICA, 8))));

        if (!label2.isEmpty()) {
            PdfPCell l2 = new PdfPCell(new Phrase(label2, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7)));
            l2.setBackgroundColor(gray);
            l2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(l2);
            table.addCell(new PdfPCell(new Phrase(val2, FontFactory.getFont(FontFactory.HELVETICA, 8))));
        } else {
            table.addCell(new PdfPCell(new Phrase("")));
            table.addCell(new PdfPCell(new Phrase("")));
        }
    }

    private void addMixedClientRow(PdfPTable table, CashMovementResponseDTO move, Color gray) {
        // Fila compleja de Teléfono / Método de Pago / Cuenta
        PdfPCell lTel = new PdfPCell(new Phrase("TELÉFONO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7)));
        lTel.setBackgroundColor(gray);
        lTel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(lTel);
        table.addCell(new PdfPCell(new Phrase("3102277740", FontFactory.getFont(FontFactory.HELVETICA, 8))));

        PdfPCell lMet = new PdfPCell(new Phrase("MÉTODO DE PAGO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7)));
        lMet.setBackgroundColor(gray);
        lMet.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(lMet);
        table.addCell(new PdfPCell(new Phrase("pmStr", FontFactory.getFont(FontFactory.HELVETICA, 8))));
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadSale(Long saleId) {
        // 1. Fetch sale (stored in orders table)
        OrderEntity order = orderRepository.findById(saleId)
                .orElseThrow(() -> new DownloadException("No se encontró la venta con ID: " + saleId));

        // 2. Determine category
        String templateCategory = switch (order.getType()) {
            case constants.productTypes.Recipe -> "RECETARIOS";
            case constants.productTypes.SpecialControl -> "MEDICAMENTOS";
            case constants.productTypes.PublicHealth -> "MEDICAMENTOS_SP";
            default -> "RECETARIOS";
        };

        // 3. Fetch template
        DocumentTemplateEntity template = documentTemplateRepository.findByDocumentTypeAndCategoryAndIsDefault("VENTA", templateCategory, true)
                .orElseThrow(() -> new DownloadException("No se encontró una plantilla predeterminada para la categoría: " + templateCategory + " y tipo VENTA"));

        // 4. Fetch company & user info
        UserDTO userDTO = userService.getUserById(jwtUtils.getCurrentUserId());

        // 5. Replace variables in template HTML
        String html = template.getHtmlContent();

        // Company variables
        html = html.replace("{{ company.name }}", userDTO.getCompany().legalName() != null ? userDTO.getCompany().legalName() : "")
                .replace("{{ company.nit }}", userDTO.getCompany().nit() != null ? userDTO.getCompany().nit() : "")
                .replace("{{ company.address }}", userDTO.getCompany().address() != null ? userDTO.getCompany().address() : "")
                .replace("{{ company.phone }}", userDTO.getCompany().phone() != null ? userDTO.getCompany().phone() : "")
                .replace("{{ company.logoUrl }}", userDTO.getCompany().logoUrl() != null ? userDTO.getCompany().logoUrl() : "");

        // Sale/Order variables
        String code = order.getSoldCode() != null ? order.getSoldCode() : (order.getOrderCode() != null ? order.getOrderCode() : "");
        html = html.replace("{{ order.orderCode }}", code)
                .replace("{{ order.soldCode }}", code)
                .replace("{{ order.createdAt }}", order.getCreatedAt() != null ? order.getCreatedAt().toLocalDate().toString() : "")
                .replace("{{ order.soldAt }}", order.getSoldAt() != null ? order.getSoldAt().toLocalDate().toString() : "")
                .replace("{{ order.total }}", formatCurrency(order.getTotal()))
                .replace("{{ order.observations }}", order.getObservations() != null ? order.getObservations() : "");

        // Client variables
        if (order.getThirdParty() != null) {
            html = html.replace("{{ thirdParty.fullName }}", order.getThirdParty().getFullName() != null ? order.getThirdParty().getFullName() : "")
                    .replace("{{ thirdParty.documentNumber }}", order.getThirdParty().getDocumentNumber() != null ? order.getThirdParty().getDocumentNumber() : "")
                    .replace("{{ thirdParty.email }}", order.getThirdParty().getEmail() != null ? order.getThirdParty().getEmail() : "")
                    .replace("{{ thirdParty.phone }}", order.getThirdParty().getPhoneNumber() != null ? order.getThirdParty().getPhoneNumber() : "");
        } else {
            html = html.replace("{{ thirdParty.fullName }}", "")
                    .replace("{{ thirdParty.documentNumber }}", "")
                    .replace("{{ thirdParty.email }}", "")
                    .replace("{{ thirdParty.phone }}", "");
        }

        // Table Items variables parsing
        int trIndex = html.indexOf("<tr");
        if (trIndex != -1 && order.getItems() != null) {
            StringBuilder tableRows = new StringBuilder();
            while (trIndex != -1) {
                int nextTrClose = html.indexOf("</tr>", trIndex);
                if (nextTrClose == -1) break;

                String trContent = html.substring(trIndex, nextTrClose + 5);
                if (trContent.contains("{{ item.")) {
                    for (OrderItemEntity item : order.getItems()) {
                        String rowHtml = trContent;

                        String productCode = "";
                        String productName = "";

                        if (order.getType().equals(constants.productTypes.Recipe)) {
                            productCode = "REC-001";
                            productName = "Recetario de Control Especial";
                        } else {
                            if (item.getInventory() != null && item.getInventory().getProduct() != null) {
                                productCode = item.getInventory().getProduct().getCode();
                                productName = item.getInventory().getProduct().getName();
                            }
                        }

                        rowHtml = rowHtml.replace("{{ item.product.code }}", productCode != null ? productCode : "")
                                .replace("{{ item.inventory.product.code }}", productCode != null ? productCode : "")
                                .replace("{{ item.product.name }}", productName != null ? productName : "")
                                .replace("{{ item.inventory.product.name }}", productName != null ? productName : "")
                                .replace("{{ item.units }}", String.valueOf(item.getUnits()))
                                .replace("{{ item.priceUnit }}", formatCurrency(item.getPriceUnit()))
                                .replace("{{ item.priceTotal }}", formatCurrency(item.getPriceTotal()));

                        tableRows.append(rowHtml).append("\n");
                    }

                    html = html.substring(0, trIndex) + tableRows.toString() + html.substring(nextTrClose + 5);
                    break;
                }
                trIndex = html.indexOf("<tr", nextTrClose);
            }
        }

        // Generate PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            java.util.HashMap<String, Object> providers = new java.util.HashMap<>();
            providers.put("img_provider", new ImageProvider() {
                @Override
                public Image getImage(String src, java.util.HashMap attrs, ChainedProperties chain, DocListener doc) {
                    try {
                        String base64Data = src;
                        if (src.startsWith("data:image")) {
                            int comma = src.indexOf("base64,");
                            if (comma != -1) {
                                base64Data = src.substring(comma + 7);
                            }
                        }
                        byte[] decoded = java.util.Base64.getDecoder().decode(base64Data.trim());
                        return Image.getInstance(decoded);
                    } catch (Exception e) {
                        return null;
                    }
                }
            });

            java.util.List<Element> elements = HTMLWorker.parseToList(new java.io.StringReader(html), null, providers);
            for (Element element : elements) {
                document.add(element);
            }
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DownloadException("Error al generar el PDF de la venta: " + e.getMessage(), e);
        }

        byte[] pdfBytes = out.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=venta-" + code + ".pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadPurchase(Long purchaseId) {
        // 1. Fetch purchase
        PurchasingEntity purchase = purchasingRepository.findById(purchaseId)
                .orElseThrow(() -> new DownloadException("No se encontró la compra con ID: " + purchaseId));

        // 2. Determine category
        String templateCategory = switch (purchase.getType()) {
            case constants.productTypes.Recipe -> "RECETARIOS";
            case constants.productTypes.SpecialControl -> "MEDICAMENTOS";
            case constants.productTypes.PublicHealth -> "MEDICAMENTOS_SP";
            default -> "RECETARIOS";
        };

        // 3. Fetch template
        DocumentTemplateEntity template = documentTemplateRepository.findByDocumentTypeAndCategoryAndIsDefault("COMPRA", templateCategory, true)
                .orElseThrow(() -> new DownloadException("No se encontró una plantilla predeterminada para la categoría: " + templateCategory + " y tipo COMPRA"));

        // 4. Fetch company & user info
        UserDTO userDTO = userService.getUserById(jwtUtils.getCurrentUserId());

        // 5. Replace variables in template HTML
        String html = template.getHtmlContent();

        // Company variables
        html = html.replace("{{ company.name }}", userDTO.getCompany().legalName() != null ? userDTO.getCompany().legalName() : "")
                .replace("{{ company.nit }}", userDTO.getCompany().nit() != null ? userDTO.getCompany().nit() : "")
                .replace("{{ company.address }}", userDTO.getCompany().address() != null ? userDTO.getCompany().address() : "")
                .replace("{{ company.phone }}", userDTO.getCompany().phone() != null ? userDTO.getCompany().phone() : "")
                .replace("{{ company.logoUrl }}", userDTO.getCompany().logoUrl() != null ? userDTO.getCompany().logoUrl() : "");

        // Purchase variables
        String code = purchase.getPurchasedCode() != null ? purchase.getPurchasedCode() : "";
        html = html.replace("{{ purchase.purchasedCode }}", code)
                .replace("{{ order.orderCode }}", code)
                .replace("{{ purchase.createdAt }}", purchase.getCreatedAt() != null ? purchase.getCreatedAt().toLocalDate().toString() : "")
                .replace("{{ order.createdAt }}", purchase.getCreatedAt() != null ? purchase.getCreatedAt().toLocalDate().toString() : "")
                .replace("{{ purchase.total }}", formatCurrency(purchase.getTotal()))
                .replace("{{ order.total }}", formatCurrency(purchase.getTotal()))
                .replace("{{ purchase.observations }}", purchase.getObservations() != null ? purchase.getObservations() : "")
                .replace("{{ order.observations }}", purchase.getObservations() != null ? purchase.getObservations() : "");

        // Provider/ThirdParty variables
        if (purchase.getThirdParty() != null) {
            html = html.replace("{{ thirdParty.fullName }}", purchase.getThirdParty().getFullName() != null ? purchase.getThirdParty().getFullName() : "")
                    .replace("{{ thirdParty.documentNumber }}", purchase.getThirdParty().getDocumentNumber() != null ? purchase.getThirdParty().getDocumentNumber() : "")
                    .replace("{{ thirdParty.email }}", purchase.getThirdParty().getEmail() != null ? purchase.getThirdParty().getEmail() : "")
                    .replace("{{ thirdParty.phone }}", purchase.getThirdParty().getPhoneNumber() != null ? purchase.getThirdParty().getPhoneNumber() : "");
        } else {
            html = html.replace("{{ thirdParty.fullName }}", "")
                    .replace("{{ thirdParty.documentNumber }}", "")
                    .replace("{{ thirdParty.email }}", "")
                    .replace("{{ thirdParty.phone }}", "");
        }

        // Table Items variables parsing
        int trIndex = html.indexOf("<tr");
        if (trIndex != -1) {
            StringBuilder tableRows = new StringBuilder();
            while (trIndex != -1) {
                int nextTrClose = html.indexOf("</tr>", trIndex);
                if (nextTrClose == -1) break;

                String trContent = html.substring(trIndex, nextTrClose + 5);
                if (trContent.contains("{{ item.")) {
                    if (purchase.getType().equals(constants.productTypes.Recipe)) {
                        // For Recipes, we have a single row representing the recipe purchase
                        if (purchase.getPurchasingRecipe() != null) {
                            String rowHtml = trContent;
                            String productCode = "REC-001";
                            String productName = "Recetario de Control Especial";
                            
                            rowHtml = rowHtml.replace("{{ item.product.code }}", productCode)
                                    .replace("{{ item.inventory.product.code }}", productCode)
                                    .replace("{{ item.product.name }}", productName)
                                    .replace("{{ item.inventory.product.name }}", productName)
                                    .replace("{{ item.units }}", String.valueOf(purchase.getPurchasingRecipe().getUnits()))
                                    .replace("{{ item.priceUnit }}", formatCurrency(purchase.getPurchasingRecipe().getPriceUnit()))
                                    .replace("{{ item.priceTotal }}", formatCurrency(purchase.getPurchasingRecipe().getPriceTotal()));
                            
                            tableRows.append(rowHtml).append("\n");
                        }
                    } else {
                        // For individual product items
                        if (purchase.getItems() != null) {
                            for (PurchasingItemEntity item : purchase.getItems()) {
                                String rowHtml = trContent;

                                String productCode = "";
                                String productName = "";

                                if (item.getProduct() != null) {
                                    productCode = item.getProduct().getCode();
                                    productName = item.getProduct().getName();
                                }

                                rowHtml = rowHtml.replace("{{ item.product.code }}", productCode != null ? productCode : "")
                                        .replace("{{ item.inventory.product.code }}", productCode != null ? productCode : "")
                                        .replace("{{ item.product.name }}", productName != null ? productName : "")
                                        .replace("{{ item.inventory.product.name }}", productName != null ? productName : "")
                                        .replace("{{ item.units }}", String.valueOf(item.getUnits()))
                                        .replace("{{ item.priceUnit }}", formatCurrency(item.getPriceUnit()))
                                        .replace("{{ item.priceTotal }}", formatCurrency(item.getPriceTotal()));

                                tableRows.append(rowHtml).append("\n");
                            }
                        }
                    }

                    html = html.substring(0, trIndex) + tableRows.toString() + html.substring(nextTrClose + 5);
                    break;
                }
                trIndex = html.indexOf("<tr", nextTrClose);
            }
        }

        // Generate PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            java.util.HashMap<String, Object> providers = new java.util.HashMap<>();
            providers.put("img_provider", new ImageProvider() {
                @Override
                public Image getImage(String src, java.util.HashMap attrs, ChainedProperties chain, DocListener doc) {
                    try {
                        String base64Data = src;
                        if (src.startsWith("data:image")) {
                            int comma = src.indexOf("base64,");
                            if (comma != -1) {
                                base64Data = src.substring(comma + 7);
                            }
                        }
                        byte[] decoded = java.util.Base64.getDecoder().decode(base64Data.trim());
                        return Image.getInstance(decoded);
                    } catch (Exception e) {
                        return null;
                    }
                }
            });

            java.util.List<Element> elements = HTMLWorker.parseToList(new java.io.StringReader(html), null, providers);
            for (Element element : elements) {
                document.add(element);
            }
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DownloadException("Error al generar el PDF de la compra: " + e.getMessage(), e);
        }

        byte[] pdfBytes = out.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=compra-" + code + ".pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    //REPORTES//

    private String escapeCsv(Object value) {
        if (value == null) return "";
        String str = value.toString();
        if (str.contains(";") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
            str = str.replace("\"", "\"\"");
            return "\"" + str + "\"";
        }
        return str;
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadReport(String type, String category, String startDate, String endDate, String documentNumber, String product, String batch, String status, String units) {
        // 1. Parse Dates safely
        LocalDateTime start = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                start = LocalDate.parse(startDate).atStartOfDay();
            } catch (Exception e) {
                // Ignore
            }
        }
        LocalDateTime end = null;
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            } catch (Exception e) {
                // Ignore
            }
        }

        Page<PrescriptionInventoryTableDTO> pageResult;

        // 2. Delegate to appropriate service based on logic
        if ("order".equalsIgnoreCase(type)) {
            // Quotations
            pageResult = orderService.getOrdersReport(
                    0, Integer.MAX_VALUE, false, category, start, end, documentNumber, product, batch);
        } else if ("sold".equalsIgnoreCase(type)) {
            // Sales
            pageResult = orderService.getOrdersReport(
                    0, Integer.MAX_VALUE, true, category, start, end, documentNumber, product, batch);
        } else if ("purchasing".equalsIgnoreCase(type)) {
            // Purchases
            if ("recipe".equalsIgnoreCase(category)) {
                pageResult = purchasingRecipeService.getPurchasingRecipeReport(
                        0, Integer.MAX_VALUE, start, end, documentNumber, product);
            } else {
                pageResult = purchasingService.getPurchasingReport(
                        0, Integer.MAX_VALUE, category, start, end, documentNumber, product, batch);
            }
        } else if ("inventory".equalsIgnoreCase(type)) {
            // Inventory
            pageResult = prescriptionInventoryService.getInventoryReport(
                    0, Integer.MAX_VALUE, status, units, product, batch, documentNumber);
        } else {
            // "Todos" type -> Fallback query using PrescriptionInventoryService
            if ("recipe".equalsIgnoreCase(category)) {
                RecipeInventoryEntity recipe = recipeInventoryService.findByIdEntity();
                List<PrescriptionInventoryTableDTO> list = new ArrayList<>();
                if (recipe != null) {
                    list.add(PrescriptionInventoryTableDTO.builder()
                            .id(recipe.getId())
                            .product("Recetarios")
                            .presentation("N/A")
                            .pharmaceuticalForm("N/A")
                            .batch("N/A")
                            .purchasePrice(BigDecimal.ZERO)
                            .salePrice(recipe.getPrice())
                            .totalUnits((long) recipe.getTotalUnits())
                            .availableUnits((long) recipe.getAvaliableUnits())
                            .expirationDate(null)
                            .isActive(true)
                            .build());
                }
                pageResult = new org.springframework.data.domain.PageImpl<>(list, PageRequest.of(0, Integer.MAX_VALUE), list.size());
            } else {
                String fallbackSearch = (product != null && !product.isEmpty()) ? product : ((batch != null && !batch.isEmpty()) ? batch : "");
                pageResult = prescriptionInventoryService.findAllToTable(PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").descending()), fallbackSearch, category);
            }
        }

        // 3. Generate CSV content
        StringBuilder csv = new StringBuilder();
        csv.append("\uFEFF"); // UTF-8 BOM so Excel opens it with correct encoding

        // Título del reporte
        String typeLabel = "Reporte de ";
        if ("order".equalsIgnoreCase(type)) {
            typeLabel += "Cotizaciones";
        } else if ("purchasing".equalsIgnoreCase(type)) {
            typeLabel += "Ingresos";
        } else if ("sold".equalsIgnoreCase(type)) {
            typeLabel += "Salidas";
        } else if ("inventory".equalsIgnoreCase(type)) {
            typeLabel += "Inventario";
        } else {
            typeLabel += "General";
        }
        csv.append("\"").append(typeLabel.toUpperCase()).append("\"\n\n");

        // Sección de filtros
        csv.append("\"Filtros Seleccionados:\"\n");
        if (category != null && !category.trim().isEmpty()) {
            String catLabel = category;
            if ("recipe".equalsIgnoreCase(category)) {
                catLabel = "Recetarios";
            } else if ("special".equalsIgnoreCase(category)) {
                catLabel = "Medicamentos";
            } else if ("public".equalsIgnoreCase(category)) {
                catLabel = "Medicamentos de Salud Pública";
            }
            csv.append("\"Categoría:\";\"").append(catLabel).append("\"\n");
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            csv.append("\"Fecha Inicio:\";\"").append(startDate).append("\"\n");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            csv.append("\"Fecha Fin:\";\"").append(endDate).append("\"\n");
        }
        if (documentNumber != null && !documentNumber.trim().isEmpty()) {
            csv.append("\"Tercero (Documento):\";\"").append(documentNumber).append("\"\n");
        }
        if (product != null && !product.trim().isEmpty()) {
            csv.append("\"Medicamento / Producto:\";\"").append(product).append("\"\n");
        }
        if (batch != null && !batch.trim().isEmpty()) {
            csv.append("\"Lote:\";\"").append(batch).append("\"\n");
        }
        if (status != null && !status.trim().isEmpty()) {
            String statLabel = status;
            if ("vigente".equalsIgnoreCase(status)) {
                statLabel = "Vigente";
            } else if ("vencido".equalsIgnoreCase(status)) {
                statLabel = "Vencido";
            } else if ("retirado".equalsIgnoreCase(status)) {
                statLabel = "Retirado";
            }
            csv.append("\"Estado:\";\"").append(statLabel).append("\"\n");
        }
        if (units != null && !units.trim().isEmpty()) {
            String unitLabel = units;
            if ("available".equalsIgnoreCase(units)) {
                unitLabel = "Con unidades disponibles";
            } else if ("unavailable".equalsIgnoreCase(units)) {
                unitLabel = "Sin unidades disponibles";
            } else if ("some_but_not_available".equalsIgnoreCase(units)) {
                unitLabel = "Con unidades pero sin disponibles";
            } else if ("all".equalsIgnoreCase(units)) {
                unitLabel = "Todo";
            }
            csv.append("\"Unidades:\";\"").append(unitLabel).append("\"\n");
        }
        csv.append("\n"); // Línea en blanco antes de la tabla

        // CSV Header
        csv.append("ID;Producto;Presentación;Forma Farmacéutica;Lote;P. Compra;P. Venta;Unid. Totales;Unid. Disp.;Fecha Venc.;Estado\n");

        if (pageResult != null && pageResult.getContent() != null) {
            for (PrescriptionInventoryTableDTO item : pageResult.getContent()) {
                csv.append(escapeCsv(item.id())).append(";")
                   .append(escapeCsv(item.product())).append(";")
                   .append(escapeCsv(item.presentation())).append(";")
                   .append(escapeCsv(item.pharmaceuticalForm())).append(";")
                   .append(escapeCsv(item.batch())).append(";")
                   .append(escapeCsv(item.purchasePrice())).append(";")
                   .append(escapeCsv(item.salePrice())).append(";")
                   .append(escapeCsv(item.totalUnits())).append(";")
                   .append(escapeCsv(item.availableUnits())).append(";")
                   .append(escapeCsv(item.expirationDate())).append(";")
                   .append(escapeCsv(item.isActive() ? "Activo" : "Inactivo")).append("\n");
            }
        }

        byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        ByteArrayInputStream bis = new ByteArrayInputStream(csvBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_" + (type != null ? type : "inventario") + ".csv");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(new InputStreamResource(bis));
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadSaleExcel(Long saleId) {
        // 1. Fetch sale (stored in orders table)
        OrderEntity order = orderRepository.findById(saleId)
                .orElseThrow(() -> new DownloadException("No se encontró la orden de salida con ID: " + saleId));

        // 2. Fetch company & user info
        UserDTO userDTO = null;
        try {
            userDTO = userService.getUserById(jwtUtils.getCurrentUserId());
        } catch (Exception ignored) {
        }

        // 3. Load template from classpath
        try (InputStream is = getClass().getResourceAsStream("/templates/ordensalida_saludpublica.xlsx")) {
            if (is == null) {
                throw new DownloadException("No se encontró la plantilla de Excel en /templates/ordensalida_saludpublica.xlsx");
            }

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            // 4. Header & General Info
            String code = order.getSoldCode() != null ? order.getSoldCode() : (order.getOrderCode() != null ? order.getOrderCode() : "");
            
            // Header Title B1
            Row row1 = sheet.getRow(0);
            if (row1 != null) {
                Cell cellB1 = row1.getCell(1);
                if (cellB1 != null && !code.isEmpty()) {
                    cellB1.setCellValue("ACTA Nº " + code + " ENTREGA DE BIOLOGICOS, MEDICAMENTOS E INSUMOS");
                }
            }

            // Institution & Municipality & Date (Row 6 -> index 5)
            Row row6 = sheet.getRow(5);
            if (row6 != null) {
                // A6: Institucion
                Cell cellA6 = row6.getCell(0);
                if (cellA6 != null && userDTO != null && userDTO.getCompany() != null && userDTO.getCompany().legalName() != null) {
                    cellA6.setCellValue(userDTO.getCompany().legalName().toUpperCase());
                }

                // F6: Municipio
                Cell cellF6 = row6.getCell(5);
                if (cellF6 != null && userDTO != null && userDTO.getCompany() != null && userDTO.getCompany().address() != null) {
                    cellF6.setCellValue(userDTO.getCompany().address().toUpperCase());
                }

                // Date
                LocalDateTime date = order.getSoldAt() != null ? order.getSoldAt() : (order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now());
                String day = String.format("%02d", date.getDayOfMonth());
                String month = String.format("%02d", date.getMonthValue());
                String year = String.valueOf(date.getYear());

                Cell cellH6 = row6.getCell(7);
                if (cellH6 != null) {
                    cellH6.setCellFormula(null);
                    cellH6.setCellValue("DÍA: " + day);
                }

                Cell cellI6 = row6.getCell(8);
                if (cellI6 != null) {
                    cellI6.setCellFormula(null);
                    cellI6.setCellValue("MES: " + month);
                }

                Cell cellJ6 = row6.getCell(9);
                if (cellJ6 != null) {
                    cellJ6.setCellFormula(null);
                    cellJ6.setCellValue("AÑO: " + year);
                }
            }

            // 5. Items dynamic list
            List<OrderItemEntity> items = order.getItems() != null ? order.getItems() : new ArrayList<>();
            int extraRows = Math.max(0, items.size() - 1);

            if (items.size() > 1) {
                int lastRow = sheet.getLastRowNum();
                if (lastRow >= 10) {
                    sheet.shiftRows(10, lastRow, extraRows, true, false);
                }
            }

            Row templateRow = sheet.getRow(9);

            for (int i = 0; i < items.size(); i++) {
                OrderItemEntity item = items.get(i);
                int currentRowIdx = 9 + i;
                Row row = sheet.getRow(currentRowIdx);
                if (row == null) {
                    row = sheet.createRow(currentRowIdx);
                }
                if (templateRow != null) {
                    row.setHeight(templateRow.getHeight());
                }

                String productCode = "";
                String productName = "";
                String presentation = "";
                String batchCode = "";
                String expirationDate = "";

                if (item.getInventory() != null) {
                    if (item.getInventory().getProduct() != null) {
                        productCode = item.getInventory().getProduct().getCode() != null ? item.getInventory().getProduct().getCode() : "";
                        productName = item.getInventory().getProduct().getName() != null ? item.getInventory().getProduct().getName() : "";
                        presentation = item.getInventory().getProduct().getPresentation() != null ? item.getInventory().getProduct().getPresentation() : "";
                    }
                    if (item.getInventory().getBatch() != null) {
                        batchCode = item.getInventory().getBatch().getCode() != null ? item.getInventory().getBatch().getCode() : "";
                    }
                    if (item.getInventory().getExpirationDate() != null) {
                        expirationDate = item.getInventory().getExpirationDate().toString();
                    }
                }

                // Col 0: Product code / name
                Cell c0 = getOrCreateCell(row, 0, templateRow);
                c0.setCellValue(productName.isEmpty() ? productCode : productName);

                // Col 1: Dosis autorizada
                Cell c1 = getOrCreateCell(row, 1, templateRow);
                if (item.getUnits() != null) {
                    c1.setCellValue(item.getUnits());
                } else {
                    c1.setCellValue(0);
                }

                // Col 2: Dosis entregada
                Cell c2 = getOrCreateCell(row, 2, templateRow);
                if (item.getUnits() != null) {
                    c2.setCellValue(item.getUnits());
                } else {
                    c2.setCellValue(0);
                }

                // Col 3: Presentación
                Cell c3 = getOrCreateCell(row, 3, templateRow);
                c3.setCellValue(presentation);

                // Col 4: Valor unitario
                Cell c4 = getOrCreateCell(row, 4, templateRow);
                if (item.getPriceUnit() != null) {
                    c4.setCellValue(item.getPriceUnit().doubleValue());
                } else {
                    c4.setCellValue(0.0);
                }

                // Col 5: Valor total
                Cell c5 = getOrCreateCell(row, 5, templateRow);
                if (item.getPriceTotal() != null) {
                    c5.setCellValue(item.getPriceTotal().doubleValue());
                } else {
                    c5.setCellValue(0.0);
                }

                // Col 6: Lote
                Cell c6 = getOrCreateCell(row, 6, templateRow);
                c6.setCellValue(batchCode);

                // Col 7: Fecha de vencimiento
                Cell c7 = getOrCreateCell(row, 7, templateRow);
                c7.setCellValue(expirationDate);
            }

            // 6. Update Footer (Signature & ThirdParty)
            int footerRow12Idx = 11 + extraRows;
            Row footerRow12 = sheet.getRow(footerRow12Idx);
            if (footerRow12 != null) {
                Cell b12 = footerRow12.getCell(1);
                if (b12 != null) {
                    String name = order.getThirdParty() != null && order.getThirdParty().getFullName() != null ? order.getThirdParty().getFullName() : "";
                    b12.setCellValue("NOMBRE: " + name);
                }
            }

            int footerRow13Idx = 12 + extraRows;
            Row footerRow13 = sheet.getRow(footerRow13Idx);
            if (footerRow13 != null) {
                Cell b13 = footerRow13.getCell(1);
                if (b13 != null && order.getThirdParty() != null) {
                    String docType = order.getThirdParty().getDocumentType() != null ? order.getThirdParty().getDocumentType() : "CC";
                    String docNum = order.getThirdParty().getDocumentNumber() != null ? order.getThirdParty().getDocumentNumber() : "";
                    b13.setCellValue(docType + ": " + docNum);
                }
            }

            int footerRow14Idx = 13 + extraRows;
            Row footerRow14 = sheet.getRow(footerRow14Idx);
            if (footerRow14 != null) {
                Cell b14 = footerRow14.getCell(1);
                if (b14 != null) {
                    String roleName = "";
                    if (order.getThirdParty() != null && order.getThirdParty().getRoles() != null && !order.getThirdParty().getRoles().isEmpty()) {
                        roleName = order.getThirdParty().getRoles().iterator().next().getRoleName();
                    }
                    b14.setCellValue("CARGO: " + roleName);
                }
            }

            // 7. Write to ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            byte[] excelBytes = out.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(excelBytes);

            HttpHeaders headers = new HttpHeaders();
            String filename = "orden_salida_salud_publica_" + saleId + ".xlsx";
            headers.add("Content-Disposition", "attachment; filename=" + filename);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(bis));

        } catch (Exception e) {
            e.printStackTrace();
            throw new DownloadException("Error al generar el Excel de la orden de salida: " + e.getMessage(), e);
        }
    }

    private Cell getOrCreateCell(Row targetRow, int colIndex, Row templateRow) {
        Cell cell = targetRow.getCell(colIndex);
        if (cell == null) {
            cell = targetRow.createCell(colIndex);
        }
        if (templateRow != null) {
            Cell templateCell = templateRow.getCell(colIndex);
            if (templateCell != null && templateCell.getCellStyle() != null) {
                cell.setCellStyle(templateCell.getCellStyle());
            }
        }
        return cell;
    }

}
