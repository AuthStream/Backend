package authstream.presentation.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.dtos.ApiResponse;
import authstream.application.dtos.ApplicationDto;
import authstream.application.services.ApplicationService;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createApplication(@RequestBody ApplicationDto dto) {
        // ApplicationDto createdDto = applicationService.createApplication(dto);
        // return ResponseEntity.ok(createdDto);
        try {

            ApplicationDto createdDto = applicationService.createApplication(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(createdDto, "Get application Successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, e.getMessage()));
        }

    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateApplication(@RequestBody ApplicationDto dto) {
        // applicationService.updateApplication(dto);
        // return ResponseEntity.ok().build();

        try {
            applicationService.updateApplication(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(1, "Update application successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteApplication(@PathVariable UUID id) {

        try {
            applicationService.deleteApplication(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(1, "delete application successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, e.getMessage()));
        }

    }

    @GetMapping
    public ResponseEntity<ApiResponse> getApplications() {

        try {
            List<ApplicationDto> applications = applicationService.getApplications();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(applications, "get applications successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getApplicationById(@PathVariable UUID id) {

        try {
            ApplicationDto application = applicationService.getApplicationById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(application, "get application successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, e.getMessage()));
        }

    }
}