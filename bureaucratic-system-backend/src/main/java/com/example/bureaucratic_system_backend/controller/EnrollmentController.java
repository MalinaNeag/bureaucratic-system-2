package com.example.bureaucratic_system_backend.controller;

import com.example.bureaucratic_system_backend.model.Citizen;
import com.example.bureaucratic_system_backend.service.EnrollmentDepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * EnrollmentController handles operations related to citizen enrollment in the system.
 */
@RestController
@RequestMapping("/api/enrollment")
@Tag(name = "Enrollment Controller", description = "Endpoints for managing citizen enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentDepartmentService enrollmentService;

    @Operation(
            summary = "Enroll a citizen",
            description = "Enrolls a citizen in the system. Requires the citizen object containing necessary details for enrollment.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Citizen enrolled successfully."),
                    @ApiResponse(responseCode = "400", description = "Citizen enrollment failed or citizen already enrolled.")
            }
    )
    @PostMapping("/enroll")
    public String enrollCitizen(
            @Parameter(description = "Citizen object with details for enrollment", required = true)
            @RequestBody Citizen citizen) {
        boolean enrolled = enrollmentService.enrollCitizen(citizen);
        return enrolled ? "Citizen enrolled successfully" : "Citizen enrollment failed or already enrolled";
    }
}
