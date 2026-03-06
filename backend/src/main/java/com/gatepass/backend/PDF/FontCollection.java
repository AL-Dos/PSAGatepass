package com.gatepass.backend.PDF;

import org.openpdf.text.FontFactory;
import org.openpdf.text.Font;

public class FontCollection {
    private FontCollection() {}

    public static Font getGatepassFont() {
        return FontFactory.getFont("Arial", 14, Font.BOLD);
    }

    public static Font getHeaderFont() {
        return FontFactory.getFont("Arial", 12, Font.NORMAL);
    }

    public static Font getSignatureFont() {
        return FontFactory.getFont("Arial", 12, Font.ITALIC);
    }

    public static Font getSignaturee() {
        return FontFactory.getFont("Arial", 12, Font.BOLD);
    }

    public static Font getGuard() {
        return FontFactory.getFont("Arial", 12, Font.ITALIC);
    }
}
