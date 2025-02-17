package authstream.presentation.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.services.ApplicationService;
import authstream.domain.entities.Application;
import authstream.utils.ApplicationUtil;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("")
    public ResponseEntity<?> createApplication(@RequestBody Application application) {
        try{
            applicationService.createApplication(application);
            return ResponseEntity.ok("Application created successfully");
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating application");
        }        
    }

    @GetMapping("")
    public ResponseEntity<?> getApplication() {
        List<Application> applications;
        try{
            applications = applicationService.getApplications();
            // System.out.println(applications.get(0));
            return ResponseEntity.ok(applications);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting applications");
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable String param) {
        Application application;
        try{
            application = applicationService.getApplicationById(param);
            return ResponseEntity.ok(application);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting application by id");
        }

    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> putMethodName(@PathVariable String id, @RequestBody Application application) {
        //TODO: process PUT request

        Application existedApplication = applicationService.getApplicationById(id);
        if(existedApplication == null) {
            return ResponseEntity.status(404).body("message: Application not found");
        }

        existedApplication = ApplicationUtil.updateNonNullFields(existedApplication, application);
        
        try {
            applicationService.updateApplication(existedApplication);
            return ResponseEntity.ok(existedApplication);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("message: Error updating application");
        }

        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMethodName(@PathVariable String id) {
        
        //TODO: process DELETE request
        try {
            applicationService.deleteApplication(id);
            return ResponseEntity.ok().body("message: Application deleted!, application id: " + id);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("message: Error deleting application");
        }
    }
    
}
