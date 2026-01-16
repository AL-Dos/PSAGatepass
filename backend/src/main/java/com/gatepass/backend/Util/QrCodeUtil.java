package com.gatepass.backend.Util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;

public final class QrCodeUtil {
    private QrCodeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static BufferedImage generateQr(String content, int size) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } 
        catch (WriterException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}   
