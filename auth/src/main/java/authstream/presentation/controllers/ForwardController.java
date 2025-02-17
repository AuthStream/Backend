package authstream.presentation.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.services.ForwardService;
import authstream.domain.entities.Forward;
import authstream.utils.Util;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/forward")
public class ForwardController {
    private final ForwardService forwardService;

    public ForwardController(ForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @PostMapping("")
    public ResponseEntity<?> createForward(@RequestBody Forward forward) {
        try{
            forwardService.createForward(forward);
            return ResponseEntity.ok("Forward created successfully");
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating Forward");
        }        
    }

    @GetMapping("")
    public ResponseEntity<?> getForward() {
        List<Forward> forwards;
        try{
            forwards = forwardService.getForwards();
            // System.out.println(providers.get(0));
            return ResponseEntity.ok(forwards);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting forwards");
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getForwardById(@PathVariable String param) {
        Forward forward;
        try{
            forward = forwardService.getForwardById(param);
            return ResponseEntity.ok(forward);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Forward by id");
        }

    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> putMethodName(@PathVariable String id, @RequestBody Forward forward) {
        //TODO: process PUT request

        Forward existedForward = forwardService.getForwardById(id);
        if(existedForward == null) {
            return ResponseEntity.status(404).body("message: Forward not found");
        }

        existedForward = Util.updateNonNullFields(existedForward, forward);
        
        try {
            forwardService.updateForward(existedForward);
            return ResponseEntity.ok(existedForward);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("message: Error updating Forward");
        }

        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMethodName(@PathVariable String id) {
        
        //TODO: process DELETE request
        try {
            forwardService.deleteForward(id);
            return ResponseEntity.ok().body("message: Forward deleted!, Forward id: " + id);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("message: Error deleting Forward");
        }
    }
    
}
