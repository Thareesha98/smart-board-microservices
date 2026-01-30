package com.sbms.sbms_maintenance_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sbms.sbms_maintenance_service.client.BoardingClient;
import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceCreateDTO;
import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceDecisionDTO;
import com.sbms.sbms_maintenance_service.dto.maintenance.MaintenanceResponseDTO;
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
        publisher.created(m);

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
        Maintenance m = repo.findById(id).orElseThrow();

        Long realOwner =
                boardingClient.getBoardingOwner(m.getBoardingId());

        if (!realOwner.equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }

        m.setStatus(dto.getStatus());
        m.setOwnerNote(dto.getOwnerNote());

        repo.save(m);
        publisher.updated(m);

        return MaintenanceMapper.toDTO(m);
    }
}
