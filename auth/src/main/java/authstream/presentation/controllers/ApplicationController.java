package authstream.presentation.controllers;

import authstream.application.dtos.ApplicationDto;
import authstream.application.services.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationDto> createApplication(@RequestBody ApplicationDto dto) {
        ApplicationDto createdDto = applicationService.createApplication(dto);
        return ResponseEntity.ok(createdDto);
    }

    @PutMapping
    public ResponseEntity<Void> updateApplication(@RequestBody ApplicationDto dto) {
        applicationService.updateApplication(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ApplicationDto>> getApplications() {
        List<ApplicationDto> applications = applicationService.getApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> getApplicationById(@PathVariable UUID id) {
        ApplicationDto application = applicationService.getApplicationById(id);
        return application != null ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
    }
}