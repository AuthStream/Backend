package authstream.presentation.controllers;

import authstream.application.dtos.ForwardDto;

import authstream.application.dtos.ProviderDto;
import authstream.application.services.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<ProviderDto> createProvider(@RequestBody ProviderDto dto) {
        ProviderDto createdDto = providerService.createProvider(dto);
        return ResponseEntity.ok(createdDto);
    }

    @PutMapping
    public ResponseEntity<ProviderDto> updateProvider(@RequestBody ProviderDto dto) {
        ProviderDto updatedDto = providerService.updateProvider(dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable UUID id) {
        providerService.deleteProvider(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> GetProviders(@PathVariable UUID id) {
        ProviderDto provider = providerService.getProviderById(id);
        return provider != null ? ResponseEntity.ok(provider) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<?> getProviderById(@RequestParam(required = false) UUID id) {
        if (id != null) {
            ProviderDto provider = providerService.getProviderById(id);
            return provider != null ? ResponseEntity.ok(provider) : ResponseEntity.notFound().build();
        } else {
            List<ProviderDto> providers = providerService.getProviders();
            return ResponseEntity.ok(providers);
        }
    }

}