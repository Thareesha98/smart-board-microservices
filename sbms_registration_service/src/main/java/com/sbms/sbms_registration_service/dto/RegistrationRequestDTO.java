package com.sbms.sbms_registration_service.dto;




import lombok.Data;

@Data
public class RegistrationRequestDTO {

    private Long boardingId;
    private int numberOfStudents;
    private String studentNote;

    // kept for backward compatibility (even if ignored)
    private boolean keyMoneyPaid;
}
