package com.gatepass.backend.PDF;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;

public class Signature {
    public void buildSignature(Document docs) throws DocumentException {
        Font signatureFont = FontCollection.getSignatureFont();
        Font signatureeFont = FontCollection.getSignaturee();
        Font guardFont = FontCollection.getGuard();

        // Requested by
        PdfPTable requestedTable = new PdfPTable(3);
        requestedTable.setWidthPercentage(100);
        requestedTable.setSpacingBefore(20f);
        requestedTable.setSpacingAfter(0);
        requestedTable.setWidths(new float[] {1f, 2f, 2.5f});

        PdfPCell labelCell1 = new PdfPCell(new Paragraph("Requested by:  ", signatureFont));
        labelCell1.setBorder(Rectangle.NO_BORDER);
        requestedTable.addCell(labelCell1);

        PdfPCell employeeCell = new PdfPCell(); // Employee or Requestor goes here
        employeeCell.setBorder(Rectangle.BOTTOM);
        requestedTable.addCell(employeeCell);

        PdfPCell blankCell1 = new PdfPCell();
        blankCell1.setBorder(Rectangle.NO_BORDER);
        requestedTable.addCell(blankCell1);

        PdfPTable Designation1 = new PdfPTable(3);
        Designation1.setWidthPercentage(100);
        Designation1.setSpacingBefore(0f);
        Designation1.setSpacingAfter(20f);
        Designation1.setWidths(new float[] {1f, 2f, 2.5f});

        PdfPCell blankCell2 = new PdfPCell();
        blankCell2.setBorder(Rectangle.NO_BORDER);
        Designation1.addCell(blankCell2);

        PdfPCell designationCell1 = new PdfPCell(new Paragraph("PSA Employee", signatureFont));
        designationCell1.setBorder(Rectangle.NO_BORDER);
        Designation1.addCell(designationCell1);

        PdfPCell blankCell3 = new PdfPCell();
        blankCell3.setBorder(Rectangle.NO_BORDER);
        Designation1.addCell(blankCell3);

        docs.add(requestedTable);
        docs.add(Designation1);

        // Approved by
        PdfPTable approvedTable = new PdfPTable(3);
        approvedTable.setWidthPercentage(100);
        approvedTable.setSpacingBefore(10f);
        approvedTable.setSpacingAfter(0);
        approvedTable.setWidths(new float[] {1f, 2f, 2.5f});

        PdfPCell labelCell2 = new PdfPCell(new Paragraph("Approved by:  ", signatureFont));
        labelCell2.setBorder(Rectangle.NO_BORDER);
        approvedTable.addCell(labelCell2);

        PdfPCell bossCell = new PdfPCell(new Paragraph("Jessie A. Madulin", signatureeFont));
        bossCell.setBorder(Rectangle.BOTTOM);
        bossCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        bossCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        approvedTable.addCell(bossCell);

        PdfPCell blankCell4 = new PdfPCell();
        blankCell4.setBorder(Rectangle.NO_BORDER);
        approvedTable.addCell(blankCell4);

        PdfPTable Designation2 = new PdfPTable(3);
        Designation2.setWidthPercentage(100);
        Designation2.setSpacingBefore(0f);
        Designation2.setSpacingAfter(20f);
        Designation2.setWidths(new float[] {1f, 2f, 2.5f});

        PdfPCell blankCell5 = new PdfPCell();
        blankCell5.setBorder(Rectangle.NO_BORDER);
        Designation2.addCell(blankCell5);

        PdfPCell designationCell2 = new PdfPCell(new Paragraph("Chief Statistical Specialist", signatureFont));
        designationCell2.setBorder(Rectangle.NO_BORDER);
        Designation2.addCell(designationCell2);

        PdfPCell blankCell6 = new PdfPCell();
        blankCell6.setBorder(Rectangle.NO_BORDER);
        Designation2.addCell(blankCell6);

        docs.add(approvedTable);
        docs.add(Designation2);

        // Guard on Duty
        Paragraph guardSign = new Paragraph("______________________", signatureeFont);
        guardSign.setSpacingBefore(20f);
        guardSign.setSpacingAfter(0);
        guardSign.setAlignment(Paragraph.ALIGN_RIGHT);
        docs.add(guardSign);

        Paragraph guardLabel = new Paragraph("Guard on Duty", guardFont);
        guardLabel.setSpacingBefore(0);
        guardLabel.setAlignment(Paragraph.ALIGN_RIGHT);
        docs.add(guardLabel);
    }
}
