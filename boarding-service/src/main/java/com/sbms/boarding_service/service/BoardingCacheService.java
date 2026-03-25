package com.sbms.boarding_service.service;

import com.sbms.boarding_service.dto.boarding.*;
import com.sbms.boarding_service.mapper.BoardingMapper;
import com.sbms.boarding_service.model.enums.Status;
import com.sbms.boarding_service.repository.BoardingRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class BoardingCacheService {

    private final BoardingRepository boardingRepository;
    private final RedisLockService lockService;

    public BoardingCacheService(
            BoardingRepository boardingRepository,
            RedisLockService lockService) {

        this.boardingRepository = boardingRepository;
        this.lockService = lockService;
    }

    // -------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------
    @Cacheable("boardings_all")
    public List<BoardingSummaryDTO> getAllCached() {

        final List<BoardingSummaryDTO>[] holder = new List[1];

        lockService.executeWithLock("lock:boardings_all", () -> {

            holder[0] = boardingRepository.findAll().stream()
                    .filter(b -> b.getStatus() == Status.APPROVED)
                    .map(BoardingMapper::toSummary)
                    .collect(Collectors.toList());
        });

        if (holder[0] == null) {
            holder[0] = boardingRepository.findAll().stream()
                    .filter(b -> b.getStatus() == Status.APPROVED)
                    .map(BoardingMapper::toSummary)
                    .collect(Collectors.toList());
        }

        return holder[0];
    }

    // -------------------------------------------------------
    // FILTER
    // -------------------------------------------------------
    @Cacheable(value = "boardings_filtered",
            key = "#request.genderType + '_' + #request.boardingType + '_' + #request.minPrice + '_' + #request.maxPrice + '_' + #request.minKeyMoney + '_' + #request.maxKeyMoney")
    public List<BoardingSummaryDTO> getFilteredCached(BoardingSearchRequest request) {

        return boardingRepository.findAll().stream()
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
                .map(BoardingMapper::toSummary)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // SEARCH
    // -------------------------------------------------------
    @Cacheable(value = "boardings_search",
            key = "#request.addressKeyword + '_' + #request.genderType + '_' + #request.boardingType")
    public List<BoardingSummaryDTO> searchCached(BoardingSearchRequest request) {

        return boardingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Status.APPROVED)
                .filter(b -> request.getGenderType() == null ||
                        b.getGenderType() == request.getGenderType())
                .filter(b -> request.getBoardingType() == null ||
                        b.getBoardingType() == request.getBoardingType())
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
                .map(BoardingMapper::toSummary)
                .collect(Collectors.toList());
    }
}