package com.gatepass.backend.PDF;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;

public class TableLogs {
    public void getTableLogs(Document docs) throws DocumentException{
        Font logsFont = FontCollection.getLogsFont();

        // Table header
        PdfPTable headerCell = new PdfPTable(4);
        headerCell.setWidthPercentage(100);
        headerCell.setSpacingBefore(10f);
        headerCell.setSpacingAfter(0f);
        headerCell.setWidths(new float[] {1f, 1f, 1f, 1f});
        headerCell.getDefaultCell().setBorder(PdfPCell.BOX);

        PdfPCell description = new PdfPCell();
        description.setPhrase(new Phrase("Description of Property/Equipment", logsFont));
        description.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        description.setBorder(PdfPCell.BOX);
        description.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        headerCell.addCell(description);

        PdfPCell number = new PdfPCell(new Paragraph("Property Number", logsFont));
        number.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        number.setBorder(PdfPCell.BOX);
        number.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        headerCell.addCell(number);

        PdfPCell destination = new PdfPCell(new Paragraph("Destination", logsFont));
        destination.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        destination.setBorder(PdfPCell.BOX);
        destination.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        headerCell.addCell(destination);

        PdfPCell period = new PdfPCell(new Paragraph("Period Covered", logsFont));
        period.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        period.setBorder(PdfPCell.BOX);
        period.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        headerCell.addCell(period);

        docs.add(headerCell);
    }   
}
