package com.sbms.sbms_maintenance_service.dto.maintenance;


import java.util.List;

import lombok.Data;

@Data
public class MaintenanceCreateDTO {

    private Long boardingId;
    private String title;
    private String description;
    private String studentNote;
    private List<String> imageUrls;

}
