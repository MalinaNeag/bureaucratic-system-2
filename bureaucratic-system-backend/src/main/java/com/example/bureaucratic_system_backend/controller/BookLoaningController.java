package com.example.bureaucratic_system_backend.controller;

import com.example.bureaucratic_system_backend.model.Citizen;
import com.example.bureaucratic_system_backend.service.BookLoaningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book-loaning")
@Tag(name = "Book Loaning Controller", description = "Endpoints for managing book loaning processes")
public class BookLoaningController {

    @Autowired
    private BookLoaningService bookLoaningService;

    @Operation(
            summary = "Add a citizen to the book loaning queue",
            description = "Adds a citizen to the queue for loaning a specific book.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Citizen added to the queue successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PostMapping("/add-to-queue")
    public String addCitizenToQueue(
            @RequestBody Citizen citizen,
            @Parameter(description = "The title of the book to be loaned", required = true) @RequestParam String bookTitle,
            @Parameter(description = "The author of the book to be loaned", required = true) @RequestParam String bookAuthor) {
        bookLoaningService.addCitizenToQueue(citizen, bookTitle, bookAuthor);
        return "Citizen added to the queue for book loaning.";
    }

    @Operation(
            summary = "Pause a counter",
            description = "Pauses a specific counter in a department to take a break.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Counter paused successfully"),
                    @ApiResponse(responseCode = "404", description = "Counter not found")
            }
    )
    @PostMapping("/pause-counter/{counterId}")
    public String pauseCounter(
            @Parameter(description = "The ID of the counter to pause", required = true) @PathVariable int counterId) {
        bookLoaningService.pauseCounter(counterId);
        return "Counter " + counterId + " paused.";
    }

    @Operation(
            summary = "Resume a counter",
            description = "Resumes a specific counter in a department after a break.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Counter resumed successfully"),
                    @ApiResponse(responseCode = "404", description = "Counter not found")
            }
    )
    @PostMapping("/resume-counter/{counterId}")
    public String resumeCounter(
            @Parameter(description = "The ID of the counter to resume", required = true) @PathVariable int counterId) {
        bookLoaningService.resumeCounter(counterId);
        return "Counter " + counterId + " resumed.";
    }
}
