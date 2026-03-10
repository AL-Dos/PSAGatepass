package com.gatepass.backend.PDF;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;

public class DateLetter {
    public void buildHeaderLetter(LocalDate date, Document docs) throws DocumentException {
        Font gatepass = FontCollection.getGatepassFont();
        Font headerFont = FontCollection.getHeaderFont();
        
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        // Gatepass
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] {3f, 1f});
        headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell blankCell = new PdfPCell();
        blankCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(blankCell);

        PdfPCell gatepassCell = new PdfPCell(new Paragraph("GATEPASS", gatepass));
        gatepassCell.setBorder(Rectangle.BOX);
        gatepassCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        gatepassCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        headerTable.addCell(gatepassCell);

        docs.add(headerTable);

        // Date and Control No.
        PdfPTable controlTable = new PdfPTable(3);
        controlTable.setWidthPercentage(100);
        controlTable.setSpacingAfter(5f);
        controlTable.setWidths(new float[] {1f, 1.3f, 3.5f});

        PdfPCell controlLabelCell = new PdfPCell(new Paragraph("Control No.:", headerFont));
        controlLabelCell.setBorder(Rectangle.NO_BORDER);
        controlTable.addCell(controlLabelCell);

        PdfPCell controlValueCell = new PdfPCell();
        controlValueCell.setBorder(Rectangle.BOTTOM);
        controlTable.addCell(controlValueCell);

        PdfPCell blankControlCell = new PdfPCell();
        blankControlCell.setBorder(Rectangle.NO_BORDER);
        controlTable.addCell(blankControlCell);

        PdfPTable dateTable = new PdfPTable(3);
        dateTable.setWidthPercentage(100);
        dateTable.setSpacingBefore(5f);
        dateTable.setWidths(new float[] {1f, 1.3f, 3.5f});

        PdfPCell dateLabelCell = new PdfPCell(new Paragraph("Date:", headerFont));
        dateLabelCell.setBorder(Rectangle.NO_BORDER);
        dateTable.addCell(dateLabelCell);

        PdfPCell dateValueCell = new PdfPCell(new Paragraph(formattedDate, headerFont));
        dateValueCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        dateValueCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        dateValueCell.setBorder(Rectangle.BOTTOM);
        dateTable.addCell(dateValueCell);

        PdfPCell blankDateCell = new PdfPCell();
        blankDateCell.setBorder(Rectangle.NO_BORDER);
        dateTable.addCell(blankDateCell);

        docs.add(controlTable);
        docs.add(dateTable);

        // Letter
        Paragraph letterEntry = new Paragraph("TO THE GUARD ON DUTY:", headerFont);
        letterEntry.setSpacingBefore(15f);
        docs.add(letterEntry);

        Paragraph letter = new Paragraph("Please allow ____________________________________ to bring out the property/equipment listed below from PSA Davao del Norte to their assigned locations for the purposes of _________________________________________ on ____________________________.", headerFont);
        letter.setSpacingBefore(10f);
        letter.setSpacingAfter(10f);
        letter.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        docs.add(letter);
    }
}
