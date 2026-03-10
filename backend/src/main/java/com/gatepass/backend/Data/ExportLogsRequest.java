package com.gatepass.backend.Data;

import java.util.List;

import lombok.Data;

@Data
public class ExportLogsRequest {
    private List<Long> equipmentIds;
}
