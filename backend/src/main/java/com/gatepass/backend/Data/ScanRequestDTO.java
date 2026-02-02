package com.gatepass.backend.Data;

import lombok.Data;

@Data
public class ScanRequestDTO {
    private String qrToken;
    private String pin;
}
