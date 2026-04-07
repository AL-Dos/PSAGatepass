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

import com.gatepass.backend.Model.Requestors;

public class DateLetter {
    public void buildHeaderLetter(LocalDate date, Document docs, Requestors requestor, Requestors purpose) throws DocumentException {
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
        // Adjust this height if you want the "GATEPASS" box taller/shorter.
        gatepassCell.setFixedHeight(24f);
        // Adjust padding to fine-tune vertical centering inside the box.
        gatepassCell.setPaddingTop(0f);
        gatepassCell.setPaddingBottom(4f);
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

        String requestorName = displayOrBlank(requestor == null ? null : requestor.getName(), 34);
        String requestPeriod = displayOrBlank(requestor == null ? null : requestor.getPeriod(), 26);
        String requestPurpose = displayOrBlank(requestor == null ? null : requestor.getPurpose(), 39);

        String letterText = "Please allow " + requestorName + " to bring out the "
                + "property/equipment listed below from PSA Davao del Norte to their "
                + "assigned locations for the purposes of " + requestPurpose + " on "
                + requestPeriod + ".";
        Paragraph letter = new Paragraph(letterText, headerFont);
        letter.setSpacingBefore(10f);
        letter.setSpacingAfter(10f);
        letter.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        docs.add(letter);
    }

    private String displayOrBlank(String value, int minUnderscores) {
        if (value == null || value.trim().isEmpty()) {
            return "_".repeat(minUnderscores);
        }
        return value.trim();
    }
}
