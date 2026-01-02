package com.sbms.sbms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbms.sbms_backend.model.Boarding;

public interface BoardingRepository extends JpaRepository<Boarding, Long> {
}
