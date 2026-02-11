package com.sbms.sbms_backend.client;

import com.sbms.sbms_backend.dto.boarding.BoardingFullSnapshot;
import com.sbms.sbms_backend.dto.boarding.BoardingOwnerInfo;
import com.sbms.sbms_backend.record.BoardingSnapshot;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class BoardingClient {

    private static final String BOARDING_SERVICE_BASE =
            "http://boarding-service:8080/api/boardings";

    private final RestTemplate restTemplate;

    public BoardingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ----------------------------------------------------
    // INTERNAL – GET FULL BOARDING SNAPSHOT
    // ----------------------------------------------------
    public BoardingSnapshot getBoarding(Long boardingId) {
        return restTemplate.getForObject(
                BOARDING_SERVICE_BASE + "/internal/" + boardingId,
                BoardingSnapshot.class
        );
    }

    // ----------------------------------------------------
    // INTERNAL – GET OWNER INFO
    // ----------------------------------------------------
    public BoardingOwnerInfo getOwnerInfo(Long boardingId) {
        return restTemplate.getForObject(
                BOARDING_SERVICE_BASE + "/internal/" + boardingId + "/owner",
                BoardingOwnerInfo.class
        );
    }

    // ----------------------------------------------------
    // INTERNAL – GET OWNER ID ONLY
    // ----------------------------------------------------
    public Long getOwnerId(Long boardingId) {
        BoardingOwnerInfo info = getOwnerInfo(boardingId);
        return info.ownerId();
    }

    // ----------------------------------------------------
    // OWNER → GET BOARDING IDS BY OWNER
    // ----------------------------------------------------
    public List<Long> getBoardingIdsByOwner(Long ownerId) {
        Long[] ids = restTemplate.getForObject(
                BOARDING_SERVICE_BASE + "/internal/owner/" + ownerId + "/ids",
                Long[].class
        );
        return Arrays.asList(ids);
    }

    
    public List<BoardingSnapshot> getBoardingSnapshots(List<Long> boardingIds) {
        BoardingSnapshot[] snapshots = restTemplate.postForObject(
                BOARDING_SERVICE_BASE + "/internal/snapshots",
                boardingIds,
                BoardingSnapshot[].class
        );
        return Arrays.asList(snapshots);
    }
    
    // ----------------------------------------------------
    // RESERVE SLOTS (APPROVE REGISTRATION)
    // ----------------------------------------------------
    public void reserveSlots(Long boardingId, int count) {
        restTemplate.postForLocation(
                BOARDING_SERVICE_BASE + "/internal/" + boardingId + "/reserve-slots?count=" + count,
                null
        );
    }
    
    public BoardingFullSnapshot getBoardingFull(Long boardingId) {
        return restTemplate.getForObject(
                BOARDING_SERVICE_BASE + "/internal/" + boardingId + "/full",
                BoardingFullSnapshot.class
        );
    }
}
