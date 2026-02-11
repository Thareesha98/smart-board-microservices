package com.sbms.sbms_backend.mapper;

import java.math.BigDecimal;


import org.springframework.beans.factory.annotation.Autowired;

import com.sbms.sbms_backend.client.UserClient;
import com.sbms.sbms_backend.dto.registration.RegistrationResponseDTO;
import com.sbms.sbms_backend.dto.user.UserMinimalDTO;
import com.sbms.sbms_backend.model.PaymentIntent;
import com.sbms.sbms_backend.model.Registration;
import com.sbms.sbms_backend.repository.PaymentIntentRepository;



public class RegistrationMapper {
	
	@Autowired UserClient userClient;
	
    public static RegistrationResponseDTO toDTO(
    		Registration r, 
    		BigDecimal keyMoney,
    	    BigDecimal monthlyPrice,
    	    UserMinimalDTO user,
    	    String boardingTitle
    	  
    	    
    		
    		) {
        RegistrationResponseDTO dto = new RegistrationResponseDTO();

        dto.setId(r.getId());

        dto.setBoardingId(r.getBoardingId());
        dto.setBoardingTitle(boardingTitle);

        dto.setStudentId(r.getStudentId());
        dto.setStudentName(user.getFullName());
        dto.setStudentEmail(user.getEmail());

        dto.setNumberOfStudents(r.getNumberOfStudents());
        dto.setStatus(r.getStatus());
        dto.setStudentNote(r.getStudentNote());
        dto.setOwnerNote(r.getOwnerNote());

        dto.setKeyMoney(keyMoney);
        dto.setMonthlyPrice(monthlyPrice);
        dto.setKeyMoneyPaid(r.isKeyMoneyPaid());
        dto.setPaymentSlipUrl(r.getPaymentTransactionRef());
        dto.setAgreementPdfPath(r.getAgreementPdfPath());

        
        dto.setPaymentMethod(r.getPaymentMethod());


        return dto;
    }
    
    
    
    
    public static RegistrationResponseDTO toDTO(
    		Registration r, 
    		BigDecimal keyMoney,
    	    BigDecimal monthlyPrice,
    	    UserMinimalDTO user
    	  
    	    
    		
    		) {
        RegistrationResponseDTO dto = new RegistrationResponseDTO();

        dto.setId(r.getId());

        dto.setBoardingId(r.getBoardingId());
        //dto.setBoardingTitle(r.getBoardingTitle());

        dto.setStudentId(r.getStudentId());
        dto.setStudentName(user.getFullName());
        dto.setStudentEmail(user.getEmail());

        dto.setNumberOfStudents(r.getNumberOfStudents());
        dto.setStatus(r.getStatus());
        dto.setStudentNote(r.getStudentNote());
        dto.setOwnerNote(r.getOwnerNote());

        dto.setKeyMoney(keyMoney);
        dto.setMonthlyPrice(monthlyPrice);
        dto.setKeyMoneyPaid(r.isKeyMoneyPaid());
        dto.setPaymentSlipUrl(r.getPaymentTransactionRef());
        dto.setAgreementPdfPath(r.getAgreementPdfPath());

        
        dto.setPaymentMethod(r.getPaymentMethod());


        return dto;
    }
    
    
    
    
}
