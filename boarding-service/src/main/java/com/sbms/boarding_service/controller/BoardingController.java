package com.sbms.boarding_service.controller;

import com.sbms.boarding_service.dto.boarding.*;
import com.sbms.boarding_service.dto.common.PageResponse;
import com.sbms.boarding_service.model.enums.BoardingType;
import com.sbms.boarding_service.model.enums.Gender;
import com.sbms.boarding_service.service.BoardingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/boardings")
@CrossOrigin
@Tag(
		name="boardings",
		description = "Public APIs for searching and viewing boarding advertisements"
)
public class BoardingController {

    private final BoardingService boardingService;

    public BoardingController(BoardingService boardingService) {
        this.boardingService = boardingService;
    }

    @Operation(
    		summary = "Get all approved boardings",
    		description = "Returns a paginated list of all approved boarding advertisements. "
                    + "This API is used by students to browse available boardings."
    )
    @ApiResponses({
    	@ApiResponse(responseCode = "200" ,  description = "Boardings retrieved successfully"),
    	@ApiResponse(responseCode="400" , description="Invalid pagination parameters") 
    })
    @GetMapping
    public Page<BoardingSummaryDTO> getAll(
    		@Parameter(description = "Page number (starts from 0" , example="0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of records per page" , example="10")
            @RequestParam(defaultValue = "10") int size
    ) {
        BoardingSearchRequest req = new BoardingSearchRequest();
        req.setPage(page);
        req.setSize(size);

        return boardingService.getAll(req);
    }

    
    
    
   
    @Operation(
    		summary= "Filter boardings",
    		description="Filters boardings using optional criteria such as gender, boarding type, "
                    + "price range, and key money range. Search keyword is NOT applied here."
    )
    @ApiResponses({
    	@ApiResponse(responseCode="200" , description = "Filters boardings using optional criteria such as gender, boarding type, "
                + "price range, and key money range. Search keyword is NOT applied here.")
    })
    @GetMapping("/filter")
    public Page<BoardingSummaryDTO> getAllFiltered(
    		
    		@Parameter(
    	            description = "Allowed gender type",
    	            example = "MALE",
    	            schema = @Schema(implementation = Gender.class)
    	        )
            @RequestParam(required = false) String genderType,
            
            @Parameter(
            		description="Type of Boarding",
            		example="ANNEX",
            		schema= @Schema(implementation = BoardingType.class)
            )
            @RequestParam(required = false) String boardingType,
            
            @Parameter(description = "Minimum monthly rent", 
            	example = "10000")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum monthly rent", 
            	example = "30000")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Minimum key money",
            	example = "50000")
            @RequestParam(required = false) BigDecimal minKeyMoney,
            
            @Parameter(description = "Maximum key money", 
            	example = "150000")
            @RequestParam(required = false) BigDecimal maxKeyMoney,
            
            @Parameter(description = "Page numbe", 
            	example = "2")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", 
            	example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        BoardingSearchRequest req = new BoardingSearchRequest();

        if (genderType != null && !genderType.isBlank()) {
            req.setGenderType(Gender.valueOf(genderType.toUpperCase()));
        }
        if (boardingType != null && !boardingType.isBlank()) {
            req.setBoardingType(BoardingType.valueOf(boardingType.toUpperCase()));
        }

        req.setMinPrice(minPrice);
        req.setMaxPrice(maxPrice);
        req.setMinKeyMoney(minKeyMoney);
        req.setMaxKeyMoney(maxKeyMoney);
        req.setPage(page);
        req.setSize(size);

        return boardingService.getAllFiltered(req);
    }

    
    
    @Operation(
    		summary = "Search boardings with filters",
    		description= "Searches boardings using address keyword combined with optional filters "
                    + "such as gender, boarding type, and price ranges."
    		)
    @ApiResponses({
    	@ApiResponse(responseCode = "200", description = "Search results returned"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")

    })
    @GetMapping("/search")
    public Page<BoardingSummaryDTO> search(
    		
    		@Parameter(description= "Allowed Gender" , example = "MALE")
            @RequestParam(required = false) String genderType,
            
            @Parameter(description = "Boarding type", example = "ROOM")
            @RequestParam(required = false) String boardingType,
            
            @Parameter(description = "Minimum rent", example = "12000")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum rent", example = "25000")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Minimum key money", example = "30000")
            @RequestParam(required = false) BigDecimal minKeyMoney,
            
            @Parameter(description = "Maximum key money", example = "100000")
            @RequestParam(required = false) BigDecimal maxKeyMoney,
            
            @Parameter(
                    description = "Address keyword for search",
                    example = "Colombo 07"
                )
            @RequestParam(required = false) String addressKeyword,
            
            
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BoardingSearchRequest req = new BoardingSearchRequest();

        if (genderType != null && !genderType.isBlank()) {
            req.setGenderType(Gender.valueOf(genderType.toUpperCase()));
        }
        if (boardingType != null && !boardingType.isBlank()) {
            req.setBoardingType(BoardingType.valueOf(boardingType.toUpperCase()));
        }

        req.setMinPrice(minPrice);
        req.setMaxPrice(maxPrice);
        req.setMinKeyMoney(minKeyMoney);
        req.setMaxKeyMoney(maxKeyMoney);
        req.setAddressKeyword(addressKeyword);
        req.setPage(page);
        req.setSize(size);

        return boardingService.searchBoardings(req);
    }
    
    
    
    
    
    
    

  @Operation(
		  summary = "Get boarding details",
		  description = "Returns full details of a single boarding advertisement by its ID"

	)
  @ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Boarding found"),
	    @ApiResponse(responseCode = "404", description = "Boarding not found")
	})
    @GetMapping("/{id}")
    public BoardingDetailDTO getOne(@PathVariable Long id) {
        return boardingService.getById(id);
    }
}
