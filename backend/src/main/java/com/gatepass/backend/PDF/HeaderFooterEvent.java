package com.gatepass.backend.PDF;

import java.io.IOException;

import org.openpdf.text.BadElementException;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Image;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;

public class HeaderFooterEvent extends PdfPageEventHelper {
    private static final String HEADER_IMAGE_PATH = "templates/Header.png";
    private static final String FOOTER_IMAGE_PATH = "templates/Footer.png";

    private final Image headerImage;
    private final Image footerImage;

    public HeaderFooterEvent() {
        this.headerImage = loadImage(HEADER_IMAGE_PATH);
        this.footerImage = loadImage(FOOTER_IMAGE_PATH);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle pageSize = document.getPageSize();
        PdfContentByte canvas = writer.getDirectContentUnder();

        addHeader(pageSize, canvas);
        addFooter(pageSize, canvas);
    }

    private void addHeader(Rectangle pageSize, PdfContentByte canvas) throws BadElementException {
        if (headerImage == null) {
            return;
        }

        try {
            Image header = Image.getInstance(headerImage);
            header.scaleAbsolute(pageSize.getWidth(), 60f);
            header.setAbsolutePosition(pageSize.getLeft(), pageSize.getTop() - header.getScaledHeight());
            canvas.addImage(header);
        } catch (DocumentException exception) {
            throw new IllegalStateException("Failed to draw PDF header image", exception);
        }
    }

    private void addFooter(Rectangle pageSize, PdfContentByte canvas) throws BadElementException {
        if (footerImage == null) {
            return;
        }

        try {
            Image footer = Image.getInstance(footerImage);
            footer.scaleAbsolute(pageSize.getWidth(), 45f);
            footer.setAbsolutePosition(pageSize.getLeft(), pageSize.getBottom());
            canvas.addImage(footer);
        } catch (DocumentException exception) {
            throw new IllegalStateException("Failed to draw PDF footer image", exception);
        }
    }

    private Image loadImage(String classpathLocation) {
        try {
            ClassPathResource resource = new ClassPathResource(classpathLocation);
            return Image.getInstance(resource.getURL());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load PDF template image: " + classpathLocation, exception);
        }
    }
}
