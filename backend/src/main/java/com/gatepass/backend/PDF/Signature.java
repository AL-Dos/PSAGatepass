package com.gatepass.backend.PDF;

import org.openpdf.text.Document;
import java.awt.image.BufferedImage;

import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;

import com.gatepass.backend.Model.Requestors;

public class Signature {
    public void buildSignature(Document docs) throws DocumentException {
        buildSignature(docs, null, null);
    }

    public void buildSignature(Document docs, BufferedImage qrImg) throws DocumentException {
        buildSignature(docs, qrImg, null);
    }

    public void buildSignature(Document docs, BufferedImage qrImg, Requestors requestor) throws DocumentException {
        Font signatureFont = FontCollection.getSignatureFont();
        Font signatureeFont = FontCollection.getSignaturee();
        Font guardFont = FontCollection.getGuard();

        // Signatures with a merged 3rd column (QR space)
        PdfPTable sigTable = new PdfPTable(3);
        sigTable.setWidthPercentage(100);
        sigTable.setSpacingBefore(30f);
        sigTable.setSpacingAfter(20f);
        sigTable.setWidths(new float[] {1f, 2f, 2.5f});

        // Row 1: Requested by
        sigTable.addCell(noBorderCell("Requested by:  ", signatureFont));
        sigTable.addCell(bottomCell(requestorName(requestor).toUpperCase(), signatureeFont));

        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setRowspan(6);
        qrCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        qrCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        if (qrImg != null) {
            try {
                Image qr = Image.getInstance(qrImg, null);
                qr.scaleToFit(120f, 120f);
                qr.setAlignment(Image.ALIGN_CENTER);
                qrCell.addElement(qr);
            } catch (Exception e) {
                throw new DocumentException(e);
            }
        } else {
            qrCell.setMinimumHeight(120f);
        }
        sigTable.addCell(qrCell);

        // Row 2: Requested designation
        sigTable.addCell(noBorderCell("", signatureFont));
        sigTable.addCell(noBorderCell("PSA Borrower", signatureFont));

        // Row 3: Noted by
        sigTable.addCell(noBorderCell("Noted by:  ", signatureFont));
        sigTable.addCell(bottomCell("BEECHAM M. CONCON", signatureeFont));

        // Row 4: Noted designation
        sigTable.addCell(noBorderCell("", signatureFont));
        sigTable.addCell(noBorderCell("Property Custodian", signatureFont));

        // Row 5: Approved by
        sigTable.addCell(noBorderCell("Approved by:  ", signatureFont));
        sigTable.addCell(bottomCell("JESSIE A. MADULIN", signatureeFont));

        // Row 6: Approved designation
        sigTable.addCell(noBorderCell("", signatureFont));
        sigTable.addCell(noBorderCell("Chief Statistical Specialist", signatureFont));

        docs.add(sigTable);

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

    private PdfPCell noBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell bottomCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        return cell;
    }

    private String requestorName(Requestors requestor) {
        return requestor == null || requestor.getName() == null ? "" : requestor.getName();
    }
}
