package com.aurealab.service.impl.pdf;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.dto.response.UserTableResponseDTO;
import com.aurealab.service.Inventory.PdfReportService;
import com.aurealab.service.UserService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.NumberToText;
import com.aurealab.util.constants;
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
import java.io.StringReader;
import java.util.Objects;
import java.util.Set;

@Service
public class PdfReportServiceImpl implements PdfReportService {

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

    public ResponseEntity<InputStreamResource> downloadOrder(Long orderId) {
        // 1. Fetch order
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("No se encontró la cotización con ID: " + orderId));

        // 2. Determine category
        String templateCategory = switch (order.getType()) {
            case constants.productTypes.Recipe -> "RECETARIOS";
            case constants.productTypes.SpecialControl -> "MEDICAMENTOS";
            case constants.productTypes.PublicHealth -> "MEDICAMENTOS_SP";
            default -> "RECETARIOS";
        };

        // 3. Fetch template
        DocumentTemplateEntity template = documentTemplateRepository.findByCategoryAndIsDefault(templateCategory, true)
                .orElseThrow(() -> new RuntimeException("No se encontró una plantilla predeterminada para la categoría: " + templateCategory));

        // 4. Fetch company & user info (needed for company headers)
        UserDTO userDTO = userService.getUserById(jwtUtils.getCurrentUserId());

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
            throw new RuntimeException("Error al generar el PDF de la cotización: " + e.getMessage());
        }

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
                .orElseThrow(() -> new RuntimeException("No se encontró la venta con ID: " + saleId));

        // 2. Determine category
        String templateCategory = switch (order.getType()) {
            case constants.productTypes.Recipe -> "RECETARIOS";
            case constants.productTypes.SpecialControl -> "MEDICAMENTOS";
            case constants.productTypes.PublicHealth -> "MEDICAMENTOS_SP";
            default -> "RECETARIOS";
        };

        // 3. Fetch template
        DocumentTemplateEntity template = documentTemplateRepository.findByDocumentTypeAndCategoryAndIsDefault("VENTA", templateCategory, true)
                .orElseThrow(() -> new RuntimeException("No se encontró una plantilla predeterminada para la categoría: " + templateCategory + " y tipo VENTA"));

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
            throw new RuntimeException("Error al generar el PDF de la venta: " + e.getMessage());
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
                .orElseThrow(() -> new RuntimeException("No se encontró la compra con ID: " + purchaseId));

        // 2. Determine category
        String templateCategory = switch (purchase.getType()) {
            case constants.productTypes.Recipe -> "RECETARIOS";
            case constants.productTypes.SpecialControl -> "MEDICAMENTOS";
            case constants.productTypes.PublicHealth -> "MEDICAMENTOS_SP";
            default -> "RECETARIOS";
        };

        // 3. Fetch template
        DocumentTemplateEntity template = documentTemplateRepository.findByDocumentTypeAndCategoryAndIsDefault("COMPRA", templateCategory, true)
                .orElseThrow(() -> new RuntimeException("No se encontró una plantilla predeterminada para la categoría: " + templateCategory + " y tipo COMPRA"));

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
            throw new RuntimeException("Error al generar el PDF de la compra: " + e.getMessage());
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
}
