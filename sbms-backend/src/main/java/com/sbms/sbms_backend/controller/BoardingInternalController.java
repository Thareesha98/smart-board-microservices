package com.sbms.sbms_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbms.sbms_backend.client.BoardingClient;
import com.sbms.sbms_backend.dto.boarding.BoardingOwnerInfo;

@RestController
@RequestMapping("/internal/boardings")
public class BoardingInternalController {

   private final BoardingClient boardingClient;
   
   public BoardingInternalController(BoardingClient boardingClient) {
       this.boardingClient = boardingClient;
   }
   
	
    @GetMapping("/{id}/owner")
    public BoardingOwnerInfo getOwner(@PathVariable Long id) {
        return boardingClient.getOwnerInfo(id);
    }
}

