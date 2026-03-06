package com.gatepass.backend.Util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Image;
import org.openpdf.text.PageSize;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import com.gatepass.backend.Model.Equipments;

public class GatepassUtil {
    private GatepassUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static byte[] createMultiItemPdf(BufferedImage qrImg, List<Equipments> equipments) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // A7 is small, so we need smaller margins (left, right, top, bottom)
            Document document = new Document(PageSize.A7, 10, 10, 10, 10);
            PdfWriter.getInstance(document, out);

            document.open();

            Image qr = Image.getInstance(qrImg, null);
            qr.scaleToFit(100, 100);
            qr.setAlignment(Image.ALIGN_CENTER);

            document.add(qr);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 5f, 4f, 5f });

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            PdfPCell headerCell;

            headerCell = new PdfPCell(new Phrase("Equipment", headerFont));
            headerCell.setPadding(5f);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Quantity", headerFont));
            headerCell.setPadding(5f);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Code", headerFont));
            headerCell.setPadding(5f);
            table.addCell(headerCell);

            for (Equipments eq : equipments) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(eq.getEquipmentName(), bodyFont));
                cell.setPadding(4f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(eq.getQuantity()), bodyFont));
                cell.setPadding(4f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(eq.getEquipmentCode(), bodyFont));
                cell.setPadding(4f);
                table.addCell(cell);
            }
            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create gatepass PDF", e);
        }
    }
}
