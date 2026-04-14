package com.gatepass.backend.Service;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openpdf.text.Document;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.gatepass.backend.PDF.DateLetter;
import com.gatepass.backend.PDF.HeaderFooterEvent;
import com.gatepass.backend.PDF.Signature;
import com.gatepass.backend.PDF.TableLogs;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Gatepass;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Util.QrCodeUtil;

@Service
public class TransmittalService {
    public byte[] exportLogsPdf(List<Equipments> equipments) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36f, 36f, 72f, 54f);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterEvent());

            document.open();
            document.add(new Paragraph(" "));
            Requestors requestor = getRequestor(equipments);
            LocalDate today = LocalDate.now();
            Gatepass gatepass = getGatepass(equipments);
            String controlNumber = buildControlNumber(today, gatepass);
            new DateLetter().buildHeaderLetter(today, document, requestor, null, controlNumber);
            new TableLogs().getTableLogs(document, equipments);
            BufferedImage qrImage = buildGatepassQr(equipments);
            new Signature().buildSignature(document, qrImage, requestor);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to generate transmittal PDF", exception);
        }
    }

    private BufferedImage buildGatepassQr(List<Equipments> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            return null;
        }

        Gatepass firstGatepass = getGatepass(equipments);
        if (firstGatepass == null || firstGatepass.getQrToken() == null) {
            return null;
        }

        for (Equipments eq : equipments) {
            Gatepass gatepass = eq.getGatepass();
            if (gatepass == null || gatepass.getQrToken() == null
                    || !firstGatepass.getQrToken().equals(gatepass.getQrToken())) {
                throw new IllegalArgumentException("All selected rows must belong to the same gatepass to build a single QR.");
            }
        }

        String qrUrl = "http://localhost:4200/verify/" + firstGatepass.getQrToken();
        return QrCodeUtil.generateQr(qrUrl, 300);
    }

    private Gatepass getGatepass(List<Equipments> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            return null;
        }
        Gatepass first = equipments.get(0).getGatepass();
        if (first == null) {
            return null;
        }
        for (Equipments eq : equipments) {
            Gatepass gatepass = eq.getGatepass();
            if (gatepass == null || gatepass.getId() == null || !first.getId().equals(gatepass.getId())) {
                throw new IllegalArgumentException("All selected rows must belong to the same gatepass to build a control number.");
            }
        }
        return first;
    }

    private String buildControlNumber(LocalDate date, Gatepass gatepass) {
        if (date == null || gatepass == null || gatepass.getId() == null) {
            return "";
        }
        String yyMm = date.format(DateTimeFormatter.ofPattern("yy-MM"));
        String idPart = String.format("%04d", gatepass.getId());
        return yyMm + "-" + idPart;
    }

    private Requestors getRequestor(List<Equipments> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            return null;
        }
        Requestors first = equipments.get(0).getRequestor();
        if (first == null) {
            return null;
        }
        for (Equipments eq : equipments) {
            if (eq.getRequestor() == null || !first.getId().equals(eq.getRequestor().getId())) {
                throw new IllegalArgumentException("All selected rows must belong to the same requestor to show a single name.");
            }
        }
        return first;
    }
}
