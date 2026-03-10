package com.gatepass.backend.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
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

@Service
public class TransmittalService {
    public byte[] exportLogsPdf(List<Equipments> equipments) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36f, 36f, 72f, 54f);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterEvent());

            document.open();
            document.add(new Paragraph(" "));
            new DateLetter().buildHeaderLetter(LocalDate.now(), document);
            new TableLogs().getTableLogs(document, equipments);
            new Signature().buildSignature(document);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to generate transmittal PDF", exception);
        }
    }
}
