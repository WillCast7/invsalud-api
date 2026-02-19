package com.aurealab.service.impl.pdf;

import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.service.CashRegister.CashMovementService;
import com.aurealab.service.CashRegister.CashSessionService;
import com.aurealab.service.CashRegister.PdfReportService;
import com.aurealab.service.UserService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;

@Service
public class PdfReportServiceImpl implements PdfReportService {

    @Autowired
    CashSessionService cashSessionService;

    @Autowired
    CashMovementService cashMovementService;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    Color lightGray = new Color(220, 220, 220); // Fondo de etiquetas
    Color darkGray = new Color(100, 100, 100);  // Texto de etiquetas
    Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
    Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
    Font bigTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);

    public ResponseEntity<InputStreamResource> downloadReport(Long sessionId){
        // 1. Obtener todos los datos necesarios
        CashSessionDTO session = cashSessionService.findById(sessionId);
        Set<CashMovementResponseDTO> movements = cashMovementService.findAllByCashSessionId(sessionId);
        CashSessionSummaryDTO summary = cashMovementService.getSummaries(sessionId);
// Este es un ejemplo de un PNG minimalista en Base64
        String logoBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";

// Convertirlo a byte[]
        byte[] logoQuemado = java.util.Base64.getDecoder().decode(logoBase64);
        // 2. Generar el PDF
        ByteArrayInputStream bis = generateCashSessionReport(session, movements, summary, logoQuemado);

        // 3. Configurar headers de respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte-caja-" + sessionId + ".pdf");

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
            UserDTO userDTO = userService.getSimplyUserById(session.openedBySystemUserId());
            System.out.println("servicio pdf");
            System.out.println(userDTO.getPerson().getNames());
            System.out.println(userDTO.getPerson().getSurNames());
            // --- 2. INFORMACIÓN GENERAL (Estilo Card) ---
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            addStyledCell(infoTable, "Sesión numero:", session.id().toString(), materialAccent);
            addStyledCell(infoTable, "Fecha:", session.businessDate().toString(), materialAccent);
            addStyledCell(infoTable, "Estado:", session.status().equals(constants.configParam.statusOpen) ? "Abierto":"Cerrado", materialAccent);
            addStyledCell(infoTable, "Responsable:", userDTO.getPerson().getNames() + " " + userDTO.getPerson().getSurNames(), materialAccent);
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

                table.addCell(createDataCell(move.product(), bodyFont));
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
        CashMovementResponseDTO move = cashMovementService.findById(movementId);
        UserDTO userDTO = userService.getUserById(jwtUtils.getCurrentUserId());
        // 2. Logo quemado o de DB
        byte[] logo = java.util.Base64.getDecoder().decode(userDTO.getCompany().logoUrl());

        // 3. Llamamos al método de generación que escribimos arriba
        ByteArrayInputStream bis = generateInvoicePdf(move, logo, userDTO);

        // 4. Headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=factura-" + move.referenceNumber() + ".pdf");

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
            PdfPCell labelBox = new PdfPCell(new Phrase("RECIBO DE CAJA", labelFont));
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

            // Contenido Principal
            PdfPCell contentCell = new PdfPCell(new Phrase(move.product() + " - " + move.concept(), valueFont));
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
            PdfPCell obsCell = new PdfPCell(new Phrase(formatCurrency(move.observations()),
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
        table.addCell(new PdfPCell(new Phrase(move.paymentMethod().name(), FontFactory.getFont(FontFactory.HELVETICA, 8))));
    }
}
