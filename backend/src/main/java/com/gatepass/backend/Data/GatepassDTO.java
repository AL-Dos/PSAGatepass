package com.gatepass.backend.Data;

import java.time.OffsetDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GatepassDTO {
    private Long id;
    private String qrToken;
    private boolean released;
    private boolean returned;
    private OffsetDateTime releasedAt;
    private OffsetDateTime returnedAt;
}
