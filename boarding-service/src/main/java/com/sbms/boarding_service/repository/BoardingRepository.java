package com.sbms.boarding_service.repository;

import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardingRepository extends JpaRepository<Boarding, Long> {

    
}
