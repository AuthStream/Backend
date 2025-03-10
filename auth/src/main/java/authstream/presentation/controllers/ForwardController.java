package authstream.presentation.controllers;

import authstream.application.dtos.ForwardDto;
import authstream.application.dtos.ProviderDto;
import authstream.application.services.ForwardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
}