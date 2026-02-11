package com.sbms.sbms_maintenance_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sbms.sbms_maintenance_service.client.BoardingClient;
import com.sbms.sbms_maintenance_service.dto.maintenance.*;
import com.sbms.sbms_maintenance_service.mapper.MaintenanceMapper;
import com.sbms.sbms_maintenance_service.model.Maintenance;
import com.sbms.sbms_maintenance_service.publisher.MaintenanceEventPublisher;
import com.sbms.sbms_maintenance_service.repository.MaintenanceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

    private static final Logger log =
            LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRepository repo;
    private final BoardingClient boardingClient;
    private final MaintenanceEventPublisher publisher;

    public MaintenanceResponseDTO create(
            Long studentId,
            MaintenanceCreateDTO dto
    ) {
        boardingClient.validateBoarding(dto.getBoardingId());

        Maintenance m = new Maintenance();
        m.setStudentId(studentId);
        m.setBoardingId(dto.getBoardingId());
        m.setTitle(dto.getTitle());
        m.setDescription(dto.getDescription());
        m.setStudentNote(dto.getStudentNote());
        m.setImageUrls(dto.getImageUrls());

        repo.save(m);

        //  SAFE EVENT PUBLISH
        try {
            publisher.created(m);
        } catch (Exception e) {
            log.warn("⚠️ Failed to publish maintenance.created", e);
        }

        return MaintenanceMapper.toDTO(m);
    }

    public List<MaintenanceResponseDTO> getForStudent(Long studentId) {
        return repo.findByStudentId(studentId)
                .stream()
                .map(MaintenanceMapper::toDTO)
                .toList();
    }

    public List<MaintenanceResponseDTO> getForOwner(Long ownerId) {
        List<Long> boardingIds =
                boardingClient.getBoardingIdsByOwner(ownerId);

        return repo.findByBoardingIdIn(boardingIds)
                .stream()
                .map(MaintenanceMapper::toDTO)
                .toList();
    }

    public MaintenanceResponseDTO decide(
            Long ownerId,
            Long id,
            MaintenanceDecisionDTO dto
    ) {
        Maintenance m = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        BoardingOwnerInfo ownerInfo =
                boardingClient.getBoardingOwner(m.getBoardingId());

        if (!ownerInfo.ownerId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }

        m.setStatus(dto.getStatus());
        m.setOwnerNote(dto.getOwnerNote());

        repo.save(m);

        // SAFE EVENT PUBLISH
        try {
            publisher.updated(m);
        } catch (Exception e) {
            log.warn("⚠️ Failed to publish maintenance.updated", e);
        }

        return MaintenanceMapper.toDTO(m);
    }
}
