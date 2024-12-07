package com.example.bureaucratic_system_backend.controller;

import com.example.bureaucratic_system_backend.model.Borrows;
import com.example.bureaucratic_system_backend.model.Fees;
import com.example.bureaucratic_system_backend.model.Membership;
import com.example.bureaucratic_system_backend.service.FirebaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/firebase")
@Tag(name = "Firebase Controller", description = "Operations for managing Firebase records like memberships, borrows, books, and fees")
public class FirebaseController {

    private final FirebaseService firebaseService;

    public FirebaseController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    // ----------------------- Memberships -----------------------

    @Operation(
            summary = "Get Membership ID by Citizen ID",
            description = "Fetches the membership ID of a citizen using their citizen ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the membership ID"),
                    @ApiResponse(responseCode = "404", description = "Citizen not found")
            }
    )
    @GetMapping("/memberships/{citizenId}")
    public String getMembershipIdById(@PathVariable String citizenId) {
        return FirebaseService.getMembershipIdById(citizenId);
    }

    // ----------------------- Books -----------------------

    @Operation(
            summary = "Get All Books",
            description = "Fetches all books stored in the database, grouped by author and name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the books list"),
                    @ApiResponse(responseCode = "500", description = "Error fetching books")
            }
    )
    @GetMapping("/books")
    public ResponseEntity<List<Map<String, Object>>> getAllBooks() {
        List<Map<String, Object>> groupedBooks = firebaseService.getAllBooksGroupedByAuthorAndName();
        return ResponseEntity.ok(groupedBooks);
    }

    // ----------------------- Membership -----------------------

    @Operation(
            summary = "Add a New Membership",
            description = "Adds a new membership record to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully added the membership"),
                    @ApiResponse(responseCode = "400", description = "Invalid membership data")
            }
    )
    @PostMapping("/memberships")
    public void addMembership(@RequestBody Membership membership) {
        firebaseService.addMembership(membership);
    }

    // ----------------------- Borrows -----------------------

    @Operation(
            summary = "Get Borrows by Membership ID",
            description = "Fetches the list of borrows associated with a membership ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the borrows list"),
                    @ApiResponse(responseCode = "500", description = "Error fetching borrows")
            }
    )
    @GetMapping("/borrows/{membershipId}")
    public ResponseEntity<List<Borrows>> getBorrowsByMembershipId(@PathVariable String membershipId) {
        try {
            List<Borrows> borrowsList = firebaseService.getBorrowsByMembershipId(membershipId);
            return ResponseEntity.ok(borrowsList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // ----------------------- Users -----------------------

    @Operation(
            summary = "Get User by Email",
            description = "Fetches user data from Firebase using the user's email address",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the user data"),
                    @ApiResponse(responseCode = "404", description = "User not found for the provided email"),
                    @ApiResponse(responseCode = "500", description = "Error fetching user data")
            }
    )
    @GetMapping("/users/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            Map<String, Object> user = firebaseService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found for email: " + email);
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user: " + e.getMessage());
        }
    }

    // ----------------------- Fees -----------------------

    @Operation(
            summary = "Get Fees by Membership ID",
            description = "Fetches the list of fees associated with a given membership ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the fees list"),
                    @ApiResponse(responseCode = "500", description = "Error fetching fees")
            }
    )
    @GetMapping("/fees/{membershipId}")
    public ResponseEntity<List<Fees>> getFeesByMembershipId(@PathVariable String membershipId) {
        try {
            List<Fees> feesList = firebaseService.getFeesByMembershipId(membershipId);
            return ResponseEntity.ok(feesList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}