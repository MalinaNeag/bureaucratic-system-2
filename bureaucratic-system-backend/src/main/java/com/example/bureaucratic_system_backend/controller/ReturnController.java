package com.example.bureaucratic_system_backend.controller;

import com.example.bureaucratic_system_backend.model.ReturnRequest;
import com.example.bureaucratic_system_backend.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
@Tag(name = "Return Controller", description = "Operations for processing book returns")
public class ReturnController {

    private static final Logger logger = LoggerFactory.getLogger(ReturnController.class);

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @Operation(
            summary = "Process a book return",
            description = "Processes the return of a book, verifying membership and book details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book return processed successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request, validation error"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/return-book")
    public ResponseEntity<String> processReturn(
            @Parameter(description = "The return request details", required = true)
            @RequestBody ReturnRequest returnRequest) {
        try {
            returnService.processReturn(returnRequest.getMembershipId(),
                    returnRequest.getBookTitle(),
                    returnRequest.getBookAuthor());
            return ResponseEntity.ok("Book return processed successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing return: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }
}