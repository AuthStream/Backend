package authstream.presentation.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.services.ApplicationService;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("")
    public String createApplication(@RequestBody String entity) {
        applicationService.createApplication();        
        return entity;
    }
    
}
