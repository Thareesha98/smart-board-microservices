
package com.sbms.sbms_backend.dto.appointment;

import com.sbms.sbms_backend.model.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentOwnerDecisionDTO {

    private AppointmentStatus status;  // ACCEPTED or DECLINED

    // If ACCEPTED → owner chooses slot inside student's range
    private LocalDateTime ownerStartTime;
    private LocalDateTime ownerEndTime;

    private String ownerNote;
}
