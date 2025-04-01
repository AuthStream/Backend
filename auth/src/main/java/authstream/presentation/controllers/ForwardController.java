package authstream.presentation.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.dtos.ForwardDto;
import authstream.application.services.ForwardService;

@RestController
@RequestMapping("/forwards")
public class ForwardController {

    private final ForwardService forwardService;

    public ForwardController(ForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @PostMapping
    public ResponseEntity<ForwardDto> createForward(@RequestBody ForwardDto dto) {
        ForwardDto createdDto = forwardService.createForward(dto);
        return ResponseEntity.ok(createdDto);
    }

    @PutMapping
    public ResponseEntity<ForwardDto> updateForward(@RequestBody ForwardDto dto) {
        ForwardDto updatedDto = forwardService.updateForward(dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForward(@PathVariable UUID id) {
        forwardService.deleteForward(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getForwards(@RequestParam(required = false) UUID id) {
        if (id != null) {
            ForwardDto forward = forwardService.getForwardById(id);
            return forward != null ? ResponseEntity.ok(forward) : ResponseEntity.notFound().build();
        } else {
            List<ForwardDto> forwards = forwardService.getForwards();
            return ResponseEntity.ok(forwards);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForwardDto> getForwardById(@PathVariable UUID id) {
        ForwardDto forward = forwardService.getForwardById(id);
        return forward != null ? ResponseEntity.ok(forward) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-application/{ApplicationId}")
    public ResponseEntity<ForwardDto> getForwardByApplicationId(@PathVariable UUID ApplicationId) {
        ForwardDto forward = forwardService.getForwardByApplicationId(ApplicationId);
        return forward != null ? ResponseEntity.ok(forward) : ResponseEntity.notFound().build();
    }
}
