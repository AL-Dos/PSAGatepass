package com.gatepass.backend.PDF;

import org.openpdf.text.Document;
import java.util.List;

import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;

import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Requestors;

public class TableLogs {
    public void getTableLogs(Document docs, List<Equipments> equipments) throws DocumentException{
        Font logsFont = FontCollection.getLogsFont();

        // Table header
        PdfPTable headerCell = new PdfPTable(5);
        headerCell.setWidthPercentage(100);
        headerCell.setSpacingBefore(10f);
        headerCell.setSpacingAfter(0f);
        headerCell.setWidths(new float[] {1f, 3f, 3f, 3f, 3f});
        headerCell.getDefaultCell().setBorder(PdfPCell.BOX);

        PdfPCell numberCell = new PdfPCell();
        numberCell.setPhrase(new Phrase("No.", logsFont));
        numberCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        numberCell.setBorder(PdfPCell.BOX);
        numberCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        headerCell.addCell(numberCell);

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

        // Table body
        PdfPTable bodyTable = new PdfPTable(5);
        bodyTable.setWidthPercentage(100);
        bodyTable.setSpacingBefore(0f);
        bodyTable.setSpacingAfter(0f);
        bodyTable.setWidths(new float[] {1f, 3f, 3f, 3f, 3f});
        bodyTable.getDefaultCell().setBorder(PdfPCell.BOX);

        int index = 1;
        for (Equipments equipment : equipments) {
            Requestors requestor = equipment.getRequestor();
            String destinationValue = requestor == null ? "" : requestor.getDestination();
            String periodValue = requestor == null ? "" : requestor.getPeriod();

            String descriptionText = equipment.getQuantity() + " " + equipment.getEquipmentName();
            bodyTable.addCell(buildCell(String.valueOf(index), logsFont, PdfPCell.ALIGN_CENTER));
            bodyTable.addCell(buildCell(descriptionText, logsFont, PdfPCell.ALIGN_CENTER));
            bodyTable.addCell(buildCell(equipment.getEquipmentCode(), logsFont, PdfPCell.ALIGN_CENTER));
            bodyTable.addCell(buildCell(destinationValue, logsFont, PdfPCell.ALIGN_CENTER));
            bodyTable.addCell(buildCell(periodValue, logsFont, PdfPCell.ALIGN_CENTER));
            index++;
        }

        docs.add(bodyTable);
    }   

    private PdfPCell buildCell(String text, Font font, int horizontalAlignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setBorder(PdfPCell.BOX);
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        return cell;
    }
}
