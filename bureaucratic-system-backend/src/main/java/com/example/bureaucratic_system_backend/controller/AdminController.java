package com.example.bureaucratic_system_backend.controller;

import com.example.bureaucratic_system_backend.model.*;
import com.example.bureaucratic_system_backend.service.AdminService;
import com.example.bureaucratic_system_backend.service.BookLoaningService;
import com.example.bureaucratic_system_backend.service.FeeService;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Controller", description = "Endpoints for managing books, citizens, fees, and configurations")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private BookLoaningService bookLoaningService;

    @Autowired
    private FeeService feeService;

    // ----------------------- Configuration -----------------------

    @Operation(
            summary = "Configure offices",
            description = "Configures offices based on the provided JSON structure. Includes office names, counters, and required documents with dependencies.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuration loaded successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PostMapping("/config")
    public ResponseEntity<String> configureOffices(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody JsonObject configJson) {
        // Implementation here
        return ResponseEntity.ok("Configuration received and loaded successfully.");
    }

    // ----------------------- Counter Management -----------------------

    @Operation(
            summary = "Pause a counter",
            description = "Pauses a specific counter in a department for a break.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Counter paused successfully"),
                    @ApiResponse(responseCode = "404", description = "Department not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PostMapping("/pause-counter")
    public ResponseEntity<String> pauseCounter(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody BreakTime breakTime) {
        return ResponseEntity.ok("Counter paused successfully.");
    }

    @Operation(
            summary = "Resume a counter",
            description = "Resumes a specific counter in a department.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Counter resumed successfully"),
                    @ApiResponse(responseCode = "404", description = "Department not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PostMapping("/resume-counter")
    public ResponseEntity<String> resumeCounter(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody BreakTime breakTime) {
        return ResponseEntity.ok("Counter resumed successfully.");
    }

    // ----------------------- Book Management -----------------------

    @Operation(
            summary = "Add a new book",
            description = "Adds a new book to the system with details like ID, title, author, and availability status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book added successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PostMapping("/add-book")
    public ResponseEntity<String> addBook(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody Book book) {
        return ResponseEntity.ok("Book added successfully.");
    }

    @Operation(
            summary = "Update a book",
            description = "Updates specific details of an existing book. Accepts book ID, the field to update, and the new value.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book updated successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PutMapping("/update-book")
    public ResponseEntity<String> updateBook(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> updateRequest) {
        return ResponseEntity.ok("Book updated successfully.");
    }

    @Operation(
            summary = "Delete a book",
            description = "Deletes a book from the system by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @DeleteMapping("/delete-book/{bookId}")
    public ResponseEntity<String> deleteBook(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Book ID to delete", required = true)
            @PathVariable String bookId) {
        return ResponseEntity.ok("Book deleted successfully.");
    }

    // ----------------------- Citizen Management -----------------------

    @Operation(
            summary = "Add a new citizen",
            description = "Adds a new citizen to the system with details like ID, name, and contact information.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Citizen added successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PostMapping("/add-citizen")
    public ResponseEntity<String> addCitizen(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody Citizen citizen) {
        return ResponseEntity.ok("Citizen added successfully.");
    }

    @Operation(
            summary = "Update a citizen",
            description = "Updates specific details of an existing citizen. Accepts citizen ID, the field to update, and the new value.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Citizen updated successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PutMapping("/update-citizen")
    public ResponseEntity<String> updateCitizen(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> updateRequest) {
        return ResponseEntity.ok("Citizen updated successfully.");
    }

    @Operation(
            summary = "Delete a citizen",
            description = "Deletes a citizen from the system by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Citizen deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @DeleteMapping("/delete-citizen/{citizenId}")
    public ResponseEntity<String> deleteCitizen(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Citizen ID to delete", required = true)
            @PathVariable String citizenId) {
        return ResponseEntity.ok("Citizen deleted successfully.");
    }

    // ----------------------- Fee Management -----------------------

    @Operation(
            summary = "Add a new fee",
            description = "Adds a fee for a specific borrow record.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fee added successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PostMapping("/add-fee")
    public ResponseEntity<String> addFee(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody Fees fee) {
        return ResponseEntity.ok("Fee added successfully.");
    }

    @Operation(
            summary = "Update a fee",
            description = "Updates specific details of an existing fee record. Accepts fee ID, the field to update, and the new value.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fee updated successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @PutMapping("/update-fee")
    public ResponseEntity<String> updateFee(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> updateRequest) {
        return ResponseEntity.ok("Fee updated successfully.");
    }

    @Operation(
            summary = "Delete a fee",
            description = "Deletes a fee record from the system by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fee deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied: Admins only")
            }
    )
    @DeleteMapping("/delete-fee/{feeId}")
    public ResponseEntity<String> deleteFee(
            @Parameter(description = "Authorization token", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Fee ID to delete", required = true)
            @PathVariable String feeId) {
        return ResponseEntity.ok("Fee deleted successfully.");
    }
}
