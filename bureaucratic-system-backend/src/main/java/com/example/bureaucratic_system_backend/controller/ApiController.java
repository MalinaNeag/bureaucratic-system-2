package com.example.bureaucratic_system_backend.controller;

import com.example.bureaucratic_system_backend.model.Citizen;
import com.example.bureaucratic_system_backend.model.Fees;
import com.example.bureaucratic_system_backend.model.LoanRequest;
import com.example.bureaucratic_system_backend.service.BookLoaningService;
import com.example.bureaucratic_system_backend.service.CitizenService;
import com.example.bureaucratic_system_backend.service.EnrollmentDepartmentService;
import com.example.bureaucratic_system_backend.service.FeeService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ApiController handles all citizen-related operations such as creating citizens,
 * enrolling them, processing loan requests, retrieving fees, and marking fees as paid.
 */
@RestController
@RequestMapping("/api/citizens")
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

    /**
     * Extracts the role from the provided JWT token.
     *
     * @param token the JWT token from the Authorization header
     * @return the role of the user
     * @throws Exception if token verification fails or role is not present
     */
    private String extractRoleFromToken(String token) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token.replace("Bearer ", ""));
        return (String) decodedToken.getClaims().get("role");
    }

    /**
     * Creates a new citizen in the system.
     *
     * @param citizen the Citizen object to be created
     * @return ResponseEntity with success or error message
     */
    @Operation(
            summary = "Create a New Citizen",
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

    /**
     * Enrolls a citizen into the enrollment department.
     *
     * @param token   the JWT token from the Authorization header
     * @param citizen the Citizen object to enroll
     * @return ResponseEntity with success or error message
     */
    @Operation(
            summary = "Enroll a Citizen",
            description = "Enrolls a citizen into the enrollment department.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Citizen enrolled successfully."),
                    @ApiResponse(responseCode = "400", description = "Enrollment failed."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "403", description = "Access denied: citizen only.")
            }
    )
    @PostMapping(value = "/enroll", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> enrollCitizen(
            @Parameter(description = "Bearer JWT token", required = true, in = ParameterIn.HEADER)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "Citizen object to enroll", required = true)
            @RequestBody Citizen citizen) {
        try {
            String role = extractRoleFromToken(token);
            if (!"citizen".equals(role)) {
                logger.warn("Access denied: role {} is not authorized to enroll citizens.", role);
                return ResponseEntity.status(403).body("Access denied: citizen only.");
            }

            boolean success = enrollmentDepartmentService.enrollCitizen(citizen);
            if (success) {
                return ResponseEntity.ok("Citizen enrolled successfully.");
            } else {
                logger.warn("Enrollment failed for citizen: {}", citizen.getId());
                return ResponseEntity.badRequest().body("Enrollment failed.");
            }
        } catch (Exception e) {
            logger.error("Unauthorized access attempt: {}", e.getMessage());
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    /**
     * Processes a loan request from a citizen.
     *
     * @param token       the JWT token from the Authorization header
     * @param loanRequest the LoanRequest object containing loan details
     * @return ResponseEntity with success or error message
     */
    @Operation(
            summary = "Process a Loan Request",
            description = "Processes a loan request from a citizen.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Loan request processed successfully."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized."),
                    @ApiResponse(responseCode = "403", description = "Access denied: citizen only.")
            }
    )
    @PostMapping(value = "/loan-request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> processLoanRequest(
            @Parameter(description = "Bearer JWT token", required = true, in = ParameterIn.HEADER)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "LoanRequest object containing loan details", required = true)
            @RequestBody LoanRequest loanRequest) {
        try {
            String role = extractRoleFromToken(token);
            if (!"citizen".equals(role)) {
                logger.warn("Access denied: role {} is not authorized to process loan requests.", role);
                return ResponseEntity.status(403).body("Access denied: citizen only.");
            }

            Citizen citizen = new Citizen();
            citizen.setId(loanRequest.getCitizenId());
            bookLoaningService.addCitizenToQueue(citizen, loanRequest.getBookTitle(), loanRequest.getBookAuthor());
            return ResponseEntity.ok("Loan request processed successfully.");
        } catch (Exception e) {
            logger.error("Unauthorized access attempt: {}", e.getMessage());
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    /**
     * Retrieves the fee associated with a specific borrow ID.
     *
     * @param token    the JWT token from the Authorization header
     * @param borrowId the borrow ID to retrieve the fee for
     * @return ResponseEntity containing the Fees object or an error message
     */
    @Operation(
            summary = "Get Fee by Borrow ID",
            description = "Retrieves the fee associated with a specific borrow ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fee retrieved successfully.",
                            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Fees.class))),
                    @ApiResponse(responseCode = "404", description = "Fee not found for the provided borrow ID."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @GetMapping(value = "/fees/{borrowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFeeByBorrowId(
            @Parameter(description = "Bearer JWT token", required = true, in = ParameterIn.HEADER)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "ID of the borrow to retrieve fee for", required = true)
            @PathVariable String borrowId) {
        try {
            Fees fee = feeService.getFeeByBorrowId(borrowId);
            if (fee == null) {
                logger.warn("Fee not found for borrow ID: {}", borrowId);
                return ResponseEntity.status(404).body("Fee not found for borrow ID: " + borrowId);
            }

            return ResponseEntity.ok(fee);
        } catch (Exception e) {
            logger.error("Error retrieving fee for borrow ID {}: {}", borrowId, e.getMessage());
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

    /**
     * Marks a fee as paid based on the provided fee ID.
     *
     * @param token the JWT token from the Authorization header
     * @param feeId the ID of the fee to mark as paid
     * @return ResponseEntity with success or error message
     */
    @Operation(
            summary = "Mark Fee as Paid",
            description = "Marks a fee as paid based on the provided fee ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fee marked as paid successfully."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @PostMapping(value = "/mark-as-paid", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> markFeeAsPaid(
            @Parameter(description = "Bearer JWT token", required = true, in = ParameterIn.HEADER)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "ID of the fee to mark as paid", required = true)
            @RequestBody String feeId) {
        try {
            feeService.markFeeAsPaid(feeId);
            return ResponseEntity.ok("Fee marked as paid successfully.");
        } catch (Exception e) {
            logger.error("Error marking fee as paid for ID {}: {}", feeId, e.getMessage());
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }
}
