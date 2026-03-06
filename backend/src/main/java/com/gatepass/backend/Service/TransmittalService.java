package com.gatepass.backend.Service;

import java.io.ByteArrayOutputStream;

import org.openpdf.text.Document;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.gatepass.backend.PDF.HeaderFooterEvent;

@Service
public class TransmittalService {
    public byte[] exportLogsPdf() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36f, 36f, 72f, 54f);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterEvent());

            document.open();
            document.add(new Paragraph(" "));
            document.close();

            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to generate transmittal PDF", exception);
        }
    }
}
