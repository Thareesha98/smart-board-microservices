package com.sbms.boarding_service.service;

import com.sbms.boarding_service.client.UserClient;
import com.sbms.boarding_service.dto.boarding.*;
import com.sbms.boarding_service.dto.common.UserMinimalDTO;
import com.sbms.boarding_service.mapper.BoardingMapper;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.enums.Status;
import com.sbms.boarding_service.repository.BoardingRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardingService {

    private final BoardingRepository boardingRepository;
    private final BoardingCacheService boardingCacheService;
    private final UserClient userClient;

    public BoardingService(
            BoardingRepository boardingRepository,
            BoardingCacheService boardingCacheService,
            UserClient userClient) {

        this.boardingRepository = boardingRepository;
        this.boardingCacheService = boardingCacheService;
        this.userClient = userClient;
    }

    // -------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------
    public Page<BoardingSummaryDTO> getAll(BoardingSearchRequest request) {

        List<BoardingSummaryDTO> list =
                boardingCacheService.getAllCached();

        return buildPage(request, list);
    }

    // -------------------------------------------------------
    // FILTER
    // -------------------------------------------------------
    public Page<BoardingSummaryDTO> getAllFiltered(BoardingSearchRequest request) {

        List<BoardingSummaryDTO> list =
                boardingCacheService.getFilteredCached(request);

        return buildPage(request, list);
    }

    // -------------------------------------------------------
    // SEARCH
    // -------------------------------------------------------
    public Page<BoardingSummaryDTO> searchBoardings(BoardingSearchRequest request) {

        List<BoardingSummaryDTO> list =
                boardingCacheService.searchCached(request);

        return buildPage(request, list);
    }

    // -------------------------------------------------------
    // GET ONE
    // -------------------------------------------------------
    @Cacheable(value = "boarding_detail", key = "#id")
    public BoardingDetailDTO getById(Long id) {

        Boarding b = boardingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Boarding not found with id: " + id));

        if (b.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Boarding is not approved yet");
        }
        
        UserSnapshotDTO user  = userClient.getUserSnapshot(b.getOwnerId());

        OwnerDto owner = new OwnerDto();
        owner.setName(user.getFullName());
        owner.setContact(owner.getContact());
        owner.setImage(user.getProfileImageUrl());
        
        return BoardingMapper.toDetail(b , owner);
    }

    // -------------------------------------------------------
    // PAGE BUILDER
    // -------------------------------------------------------
    private Page<BoardingSummaryDTO> buildPage(
            BoardingSearchRequest request,
            List<BoardingSummaryDTO> list) {

        int page = request.getPage();
        int size = request.getSize();

        int from = page * size;
        int to = Math.min(from + size, list.size());

        if (from > list.size()) {
            from = list.size();
        }

        List<BoardingSummaryDTO> content = list.subList(from, to);

        Pageable pageable = PageRequest.of(page, size);

        return new PageImpl<>(content, pageable, list.size());
    }

    // -------------------------------------------------------
    // INTERNAL SERVICE API
    // -------------------------------------------------------
    @Cacheable(value = "approved_boarding", key = "#boardingId")
    public Boarding getApprovedBoardingById(Long boardingId) {

        Boarding boarding = boardingRepository.findById(boardingId)
                .orElseThrow(() ->
                        new RuntimeException("Boarding not found with id: " + boardingId));

        if (boarding.getStatus() != Status.APPROVED) {
            throw new RuntimeException(
                    "Boarding is not approved. boardingId=" + boardingId);
        }

        return boarding;
    }
}