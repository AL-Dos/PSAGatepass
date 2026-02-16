package com.gatepass.backend.Data;

import java.time.LocalDateTime;

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
    private LocalDateTime releasedAt;
    private LocalDateTime returnedAt;
}
