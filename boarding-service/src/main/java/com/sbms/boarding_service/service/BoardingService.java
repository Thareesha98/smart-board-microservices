package com.sbms.boarding_service.service;

import com.sbms.boarding_service.dto.boarding.*;
import com.sbms.boarding_service.mapper.BoardingMapper;
import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.enums.Status;
import com.sbms.boarding_service.repository.BoardingRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class BoardingService {

    private final BoardingRepository boardingRepository;

    public BoardingService(BoardingRepository boardingRepository) {
        this.boardingRepository = boardingRepository;
    }

    // -------------------------------------------------------
    // 1) SEARCH (FILTER + KEYWORD)
    // -------------------------------------------------------
    public Page<BoardingSummaryDTO> searchBoardings(BoardingSearchRequest request) {

        List<Boarding> filtered = boardingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Status.APPROVED)
                .filter(b -> request.getGenderType() == null ||
                             b.getGenderType() == request.getGenderType())
                .filter(b -> request.getBoardingType() == null ||
                             b.getBoardingType() == request.getBoardingType())
                .filter(b -> request.getMinPrice() == null ||
                             b.getPricePerMonth().compareTo(request.getMinPrice()) >= 0)
                .filter(b -> request.getMaxPrice() == null ||
                             b.getPricePerMonth().compareTo(request.getMaxPrice()) <= 0)
                .filter(b -> request.getMinKeyMoney() == null ||
                             b.getKeyMoney().compareTo(request.getMinKeyMoney()) >= 0)
                .filter(b -> request.getMaxKeyMoney() == null ||
                             b.getKeyMoney().compareTo(request.getMaxKeyMoney()) <= 0)
                .filter(b -> {
                    if (request.getAddressKeyword() == null ||
                        request.getAddressKeyword().isBlank()) {
                        return true;
                    }
                    String keyword = request.getAddressKeyword()
                            .toLowerCase(Locale.ROOT);

                    return (b.getAddress() != null &&
                            b.getAddress().toLowerCase(Locale.ROOT).contains(keyword))
                        || (b.getTitle() != null &&
                            b.getTitle().toLowerCase(Locale.ROOT).contains(keyword));
                })
                .collect(Collectors.toList());

        return toPagedResult(request, filtered);
    }

    // -------------------------------------------------------
    // 2) FILTER ONLY (NO SEARCH KEYWORD)
    // -------------------------------------------------------
    public Page<BoardingSummaryDTO> getAllFiltered(BoardingSearchRequest request) {

        List<Boarding> filtered = boardingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Status.APPROVED)
                .filter(b -> request.getGenderType() == null ||
                             b.getGenderType() == request.getGenderType())
                .filter(b -> request.getBoardingType() == null ||
                             b.getBoardingType() == request.getBoardingType())
                .filter(b -> request.getMinPrice() == null ||
                             b.getPricePerMonth().compareTo(request.getMinPrice()) >= 0)
                .filter(b -> request.getMaxPrice() == null ||
                             b.getPricePerMonth().compareTo(request.getMaxPrice()) <= 0)
                .filter(b -> request.getMinKeyMoney() == null ||
                             b.getKeyMoney().compareTo(request.getMinKeyMoney()) >= 0)
                .filter(b -> request.getMaxKeyMoney() == null ||
                             b.getKeyMoney().compareTo(request.getMaxKeyMoney()) <= 0)
                .collect(Collectors.toList());

        return toPagedResult(request, filtered);
    }

    // -------------------------------------------------------
    // 3) GET ALL (NO FILTERS)
    // -------------------------------------------------------
    public Page<BoardingSummaryDTO> getAll(BoardingSearchRequest request) {

        List<Boarding> approved = boardingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Status.APPROVED)
                .collect(Collectors.toList());

        return toPagedResult(request, approved);
    }

    // -------------------------------------------------------
    // GET ONE (DETAIL)
    // -------------------------------------------------------
    public BoardingDetailDTO getById(Long id) {
        Boarding b = boardingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Boarding not found with id: " + id));

        if (b.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Boarding is not approved yet");
        }

        return BoardingMapper.toDetail(b);
    }

    // -------------------------------------------------------
    // COMMON PAGINATION HELPER
    // -------------------------------------------------------
    private Page<BoardingSummaryDTO> toPagedResult(
            BoardingSearchRequest request,
            List<Boarding> filtered
    ) {
        int page = request.getPage();
        int size = request.getSize();

        int from = page * size;
        int to = Math.min(from + size, filtered.size());

        if (from > filtered.size()) {
            from = filtered.size();
        }

        List<BoardingSummaryDTO> content = filtered.subList(from, to)
                .stream()
                .map(BoardingMapper::toSummary)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(content, pageable, filtered.size());
    }
    
    public Boarding getApprovedBoardingById(Long boardingId) {

        Boarding boarding = boardingRepository.findById(boardingId)
                .orElseThrow(() ->
                        new RuntimeException("Boarding not found with id: " + boardingId)
                );

        if (boarding.getStatus() != Status.APPROVED) {
            throw new RuntimeException(
                    "Boarding is not approved. boardingId=" + boardingId
            );
        }

        return boarding;
    }
}
