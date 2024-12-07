package com.example.bureaucratic_system_backend.controller;

import com.example.bureaucratic_system_backend.model.Citizen;
import com.example.bureaucratic_system_backend.model.Fees;
import com.example.bureaucratic_system_backend.model.LoanRequest;
import com.example.bureaucratic_system_backend.service.BookLoaningService;
import com.example.bureaucratic_system_backend.service.CitizenService;
import com.example.bureaucratic_system_backend.service.EnrollmentDepartmentService;
import com.example.bureaucratic_system_backend.service.FeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ApiController handles all citizen-related operations such as creating citizens,
 * enrolling them, processing loan requests, retrieving fees, and marking fees as paid.
 */
@RestController
@RequestMapping("/api/citizens")
@Tag(name = "Citizen Controller", description = "Endpoints for managing citizens, loans, and fees")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private BookLoaningService bookLoaningService;

    @Autowired
    private CitizenService citizenService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private EnrollmentDepartmentService enrollmentDepartmentService;

    @Operation(
            summary = "Create a new citizen",
            description = "Adds a new citizen to the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Citizen created successfully."),
                    @ApiResponse(responseCode = "500", description = "Error creating citizen.")
            }
    )
    @PostMapping(value = "/create-citizen", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createCitizen(
            @Parameter(description = "Citizen object to create", required = true)
            @RequestBody Citizen citizen) {
        try {
            citizenService.addCitizen(citizen);
            return ResponseEntity.ok("Citizen created successfully.");
        } catch (Exception e) {
            logger.error("Error creating citizen: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error creating citizen: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Enroll a citizen",
            description = "Enrolls a citizen into the enrollment department.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Citizen enrolled successfully."),
                    @ApiResponse(responseCode = "400", description = "Enrollment failed."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @PostMapping(value = "/enroll", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> enrollCitizen(
            @Parameter(description = "Citizen object to enroll", required = true)
            @RequestBody Citizen citizen) {
        try {
            boolean success = enrollmentDepartmentService.enrollCitizen(citizen);
            if (success) {
                return ResponseEntity.ok("Citizen enrolled successfully.");
            } else {
                logger.warn("Enrollment failed for citizen: {}", citizen.getId());
                return ResponseEntity.badRequest().body("Enrollment failed.");
            }
        } catch (Exception e) {
            logger.error("Error enrolling citizen: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

    @Operation(
            summary = "Process a loan request",
            description = "Processes a loan request from a citizen.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Loan request processed successfully."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @PostMapping(value = "/loan-request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> processLoanRequest(
            @Parameter(description = "LoanRequest object containing loan details", required = true)
            @RequestBody LoanRequest loanRequest) {
        try {
            Citizen citizen = new Citizen();
            citizen.setId(loanRequest.getCitizenId());
            bookLoaningService.addCitizenToQueue(citizen, loanRequest.getBookTitle(), loanRequest.getBookAuthor());
            return ResponseEntity.ok("Loan request processed successfully.");
        } catch (Exception e) {
            logger.error("Error processing loan request: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

    @Operation(
            summary = "Get fee by borrow ID",
            description = "Retrieves the fee associated with a specific borrow ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fee retrieved successfully."),
                    @ApiResponse(responseCode = "404", description = "Fee not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @GetMapping(value = "/fees/{borrowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFeeByBorrowId(
            @Parameter(description = "Borrow ID to retrieve fee for", required = true)
            @PathVariable String borrowId) {
        try {
            Fees fee = feeService.getFeeByBorrowId(borrowId);
            if (fee == null) {
                logger.warn("Fee not found for borrow ID: {}", borrowId);
                return ResponseEntity.status(404).body("Fee not found for borrow ID: " + borrowId);
            }
            return ResponseEntity.ok(fee);
        } catch (Exception e) {
            logger.error("Error retrieving fee: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

    @Operation(
            summary = "Mark fee as paid",
            description = "Marks a fee as paid based on the provided fee ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fee marked as paid successfully."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @PostMapping(value = "/mark-as-paid", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> markFeeAsPaid(
            @Parameter(description = "Fee ID to mark as paid", required = true)
            @RequestBody String feeId) {
        try {
            feeService.markFeeAsPaid(feeId);
            return ResponseEntity.ok("Fee marked as paid successfully.");
        } catch (Exception e) {
            logger.error("Error marking fee as paid: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }
}
