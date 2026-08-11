package com.gatepass.backend.Data;

import java.util.List;

import lombok.Data;

@Data
public class AdminEquipmentActionRequest {
    private List<Long> equipmentIds;
}
