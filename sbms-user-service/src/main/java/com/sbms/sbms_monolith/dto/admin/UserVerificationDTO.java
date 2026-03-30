package com.sbms.sbms_monolith.dto.admin;


import lombok.Data;

@Data
public class UserVerificationDTO {

	public boolean approved;
    private String reason; // optional admin note
}
