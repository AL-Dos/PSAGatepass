package com.gatepass.backend.Util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import com.gatepass.backend.Model.Equipments;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class GatepassUtil {
    private GatepassUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static byte[] createMultiItemPdf(BufferedImage qrImg, List<Equipments> equipments ) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A6);
            PdfWriter.getInstance(document, out);

            document.open();

            Image qr = Image.getInstance(qrImg, null);
            qr.scaleToFit(150, 150);
            qr.setAlignment(Image.ALIGN_CENTER);

            document.add(qr);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 4});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            table.addCell(new PdfPCell(new Phrase("Equipment", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Quantity", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Equipment Code", headerFont)));

            for (Equipments eq : equipments) {
                table.addCell(new PdfPCell(new Phrase(eq.getEquipmentName(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(eq.getQuantity()), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(eq.getEquipmentCode(), bodyFont)));
            }
            document.add(table);
            document.close();
            
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create gatepass PDF", e);
        }
    }
}
