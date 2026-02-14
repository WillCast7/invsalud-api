package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.service.CashRegister.CashMovementService;
import com.aurealab.service.CashRegister.CashSessionService;
import com.aurealab.service.CashRegister.PdfReportService;
import com.aurealab.service.UserService;
import com.aurealab.util.constants;
import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.Phrase;
import com.lowagie.text.Element;

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
}
