package com.aurealab.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.ImageProvider;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.*;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PdfGenerationTest {

    @Test
    void testCotizacionRecetariosSinglePageWithAnchoredFooter() throws Exception {
        String base64Png = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAFUlEQVR42mP8z8BQz0AEYBxVSF+FABJADveWkH6oAAAAAElFTkSuQmCC";

        String htmlBody = "<div>" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-bottom: 2px solid #000; padding-bottom: 4px; margin-bottom: 4px;\">" +
                "  <tr>" +
                "    <td width=\"65%\" valign=\"middle\">" +
                "      <img src=\"" + base64Png + "\" width=\"200\" height=\"50\" alt=\"Logo Institucional\" />" +
                "    </td>" +
                "    <td width=\"35%\" valign=\"top\" align=\"right\">" +
                "      <div><b>FO-M9-P3-02- V04</b></div>" +
                "      <div>1.220.30 - 27.39</div>" +
                "      <div><b>CR-01</b></div>" +
                "      <div>Santiago de Cali, 2026-09-02</div>" +
                "    </td>" +
                "  </tr>" +
                "</table>" +
                "<div>" +
                "  <div><b>Señor, (A):</b></div>" +
                "  <div><b>Hospital San Juan de Dios</b></div>" +
                "  <div><b>Ref: COTIZACION RECETARIOS OFICIALES PARA LA PRESCRIPCION DE MCE</b></div>" +
                "</div>" +
                "<table width=\"100%\" border=\"1\" cellpadding=\"2\" cellspacing=\"0\" style=\"margin-top: 4px; border-collapse: collapse;\">" +
                "  <tr bgcolor=\"#f2f2f2\">" +
                "    <th width=\"15%\" align=\"center\">Cantidad</th>" +
                "    <th width=\"20%\" align=\"right\">Valor Unitario</th>" +
                "    <th width=\"20%\" align=\"right\">Subtotal</th>" +
                "    <th width=\"20%\" align=\"right\">Iva / 19%</th>" +
                "    <th width=\"25%\" align=\"right\">Valor Total</th>" +
                "  </tr>" +
                "  <tr>" +
                "    <td align=\"center\">150</td>" +
                "    <td align=\"right\">$ 2.000</td>" +
                "    <td align=\"right\">$ 300.000</td>" +
                "    <td align=\"right\">$ 57.000</td>" +
                "    <td align=\"right\"><b>$ 357.000</b></td>" +
                "  </tr>" +
                "  <tr bgcolor=\"#fafafa\" style=\"font-weight: bold;\">" +
                "    <td colspan=\"4\" align=\"right\">TOTAL</td>" +
                "    <td align=\"right\">$ 357.000</td>" +
                "  </tr>" +
                "</table>" +
                "<div class=\"notes\">" +
                "  <div><b>Requisitos para reclamar o reposición de recetarios:</b></div>" +
                "  <div><b>Nota: 1. Para reclamar los recetarios por primera vez, favor:</b></div>" +
                "  <div>A. Original y Copia del Recibo de Consignación con Firma y sello del Cajero; Consignación del Banco DAVIVIENDA cuenta de ahorros No 379400001804, a nombre del Departamento del Valle del Cauca - Fondo Rotatorio de Estupefacientes NIT 890399029-5</div>" +
                "  <div>B. Listado de Médicos u Odontólogos con La fotocopia del registro o tarjeta profesional respectiva.</div>" +
                "  <div>C. Autoevaluación vigente de Habilitación según Resolución 3100 del 2019 como prestadores de Servicios de Salud.</div>" +
                "  <div>D. Dirección de la Institución.</div>" +
                "  <div>E. Teléfono, Fax y Correo Electrónico de la Institución.</div>" +
                "  <div>F. Resolución de inscripción ante el fondo de estupefacientes si realizan la dispensación, Y utilización del medicamento en sus procedimientos.</div>" +
                "  <div>G. Autorización firmada por el representante legal donde delegue al personal que realizara El proceso de reclamación de los talonarios y copia de La cedula.</div>" +
                "  <div>H. Entrega de recetarios CITA PREVIA SOLICITADA POR CORREO ELECTRONICO.</div>" +
                "  <div>I. Cotización válida por 08 días. Después de esta fecha no se responde por cantidades ni por precios. Pasado este lapso de tiempo antes de consignar solicitar reconfirmación de esta cotización.</div>" +
                "  <div><b>Nota: 2. Para reposición de los recetarios, favor:</b></div>" +
                "  <div>A. Entrega de recetarios CITA PREVIA SOLICITADA POR CORREO ELECTRONICO.</div>" +
                "  <div>B. Original y Copia del Recibo de Consignación con Firma y sello del Cajero; Consignación del Banco DAVIVIENDA Cuenta de Ahorros # 379400001804, a nombre del Departamento del Valle del Cauca - Fondo Rotatorio de Estupefacientes NIT de la Institución.</div>" +
                "  <div>C. Oficio membretado con los datos del prestador, persona autorizada para reclamar los recetarios.</div>" +
                "  <div>D. Cédula de la persona autorizada.</div>" +
                "  <div>E. Estar al día con el envió de los anexos.</div>" +
                "  <div>F. Formulas anuladas.</div>" +
                "  <div>G. Formatos blancos que se encuentran en la última parte de los recetarios debidamente diligenciados</div>" +
                "  <div>H. Entrega de recetarios CITA PREVIA SOLICITADA POR CORREO ELECTRONICO.</div>" +
                "  <div>I. Cotización válida por 08 días. Después de esta fecha no se responde por cantidades ni por precios. Pasado este lapso de tiempo antes de consignar solicitar reconfirmación de esta cotización.</div>" +
                "  <div>J. Rut actualizado</div>" +
                "  <div>Atentamente,</div>" +
                "  <div><b>Fondo Rotatorio de Estupefacientes del Valle del Cauca</b></div>" +
                "  <div><b>Secretaría Departamental de Salud del Valle</b></div>" +
                "</div>" +
                "</div>";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 15, 35);
        PdfWriter writer = PdfWriter.getInstance(document, out);

        PdfPTable footerTable = new PdfPTable(4);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[]{28f, 42f, 18f, 12f});

        Color corpBlue = new Color(0, 51, 153);
        Color borderGray = new Color(176, 190, 197);

        PdfPCell c1 = new PdfPCell(new Phrase("Gobernación Departamento\ndel Valle del Cauca", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, corpBlue)));
        c1.setBorder(Rectangle.TOP);
        c1.setBorderColorTop(corpBlue);
        c1.setBorderWidthTop(1.5f);
        c1.setPadding(2);
        footerTable.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("Carrera 76 # 4 - 30 edificio complejo integral de servicios de salud pública \"Aníbal Patiño Rodríguez\"", FontFactory.getFont(FontFactory.HELVETICA, 6.5f, Color.DARK_GRAY)));
        c2.setBorder(Rectangle.TOP | Rectangle.LEFT);
        c2.setBorderColorTop(corpBlue);
        c2.setBorderColorLeft(borderGray);
        c2.setBorderWidthTop(1.5f);
        c2.setPadding(2);
        footerTable.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase("fre@valledelcauca.gov.co", FontFactory.getFont(FontFactory.HELVETICA, 7f, Color.DARK_GRAY)));
        c3.setBorder(Rectangle.TOP | Rectangle.LEFT);
        c3.setBorderColorTop(corpBlue);
        c3.setBorderColorLeft(borderGray);
        c3.setBorderWidthTop(1.5f);
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
        c3.setPadding(2);
        footerTable.addCell(c3);

        PdfPCell c4 = new PdfPCell(new Phrase("3104683988", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, Color.DARK_GRAY)));
        c4.setBorder(Rectangle.TOP | Rectangle.LEFT);
        c4.setBorderColorTop(corpBlue);
        c4.setBorderColorLeft(borderGray);
        c4.setBorderWidthTop(1.5f);
        c4.setHorizontalAlignment(Element.ALIGN_CENTER);
        c4.setPadding(2);
        footerTable.addCell(c4);

        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                footerTable.setTotalWidth(document.right() - document.left());
                footerTable.writeSelectedRows(0, -1, document.left(), document.bottom() - 5, writer.getDirectContent());
            }
        });

        document.open();

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.loadTagStyle("body", "font-family", "Helvetica");
        styleSheet.loadTagStyle("body", "size", "7pt");
        styleSheet.loadTagStyle("div", "size", "7pt");
        styleSheet.loadTagStyle("div", "leading", "8.5");
        styleSheet.loadTagStyle("p", "size", "7pt");
        styleSheet.loadTagStyle("p", "leading", "8.5");
        styleSheet.loadTagStyle("td", "size", "7.5pt");
        styleSheet.loadTagStyle("td", "leading", "9");
        styleSheet.loadTagStyle("th", "size", "7.5pt");
        styleSheet.loadTagStyle("th", "leading", "9");
        styleSheet.loadStyle("notes", "size", "6.5pt");
        styleSheet.loadStyle("notes", "leading", "7.8");

        Map<String, Object> providers = new HashMap<>();
        providers.put("img_provider", new ImageProvider() {
            @Override
            public Image getImage(String src, HashMap attrs, ChainedProperties chain, DocListener doc) {
                try {
                    String base64Data = src;
                    if (src.startsWith("data:image")) {
                        int comma = src.indexOf("base64,");
                        if (comma != -1) {
                            base64Data = src.substring(comma + 7);
                        }
                    }
                    base64Data = base64Data.replaceAll("\\s+", "");
                    byte[] decoded = Base64.getMimeDecoder().decode(base64Data);
                    Image img = Image.getInstance(decoded);
                    img.scaleToFit(200f, 50f);
                    return img;
                } catch (Exception e) {
                    return null;
                }
            }
        });

        List<Element> elements = HTMLWorker.parseToList(new StringReader(htmlBody), styleSheet, providers);
        for (Element e : elements) {
            document.add(e);
            System.out.println("Added: " + e.getClass().getSimpleName() + " at y=" + writer.getVerticalPosition(false));
        }
        document.close();

        PdfReader reader = new PdfReader(out.toByteArray());
        int pageCount = reader.getNumberOfPages();
        System.out.println("Cotización Recetarios generated page count with StyleSheet: " + pageCount);
        assertEquals(1, pageCount, "The cotización recetarios MUST fit in exactly 1 page!");
    }
}
