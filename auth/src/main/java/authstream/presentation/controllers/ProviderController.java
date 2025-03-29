package authstream.presentation.controllers;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
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

import authstream.application.dtos.ApiResponse;
import authstream.application.dtos.ProviderDto;
import authstream.application.services.ProviderService;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createProvider(@RequestBody ProviderDto dto) {
        try {
            Pair<ProviderDto, Object> createdDto = providerService.createProvider(dto);
            if (createdDto.getRight() == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(createdDto.getLeft(), "Create Provider successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, createdDto.getRight().toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, e.getMessage()));
        }

    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateProvider(@RequestBody ProviderDto dto) {

        try {
            Pair<ProviderDto, Object> updatedDto = providerService.updateProvider(dto);
            if (updatedDto.getRight() == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(updatedDto.getLeft(), "Update Provider successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, updatedDto.getRight().toString()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse(null, "Something wrong with Server: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProvider(@PathVariable UUID id) {
        try {
            Pair<Boolean, Object> deleteProvider = providerService.deleteProvider(id);
            if (deleteProvider.getRight() == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(deleteProvider.getLeft(), "Delete Provider successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, deleteProvider.getRight().toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse(null, "Something wrong with Server: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")

    public ResponseEntity<ApiResponse> GetProviders(@PathVariable UUID id) {

        try {
            Pair<ProviderDto, Object> provider = providerService.getProviderById(id);
            if (provider.getRight() == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(provider.getLeft(), "Get Provider successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, provider.getRight().toString()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse(null, "Something wrong with Server: " + e.getMessage()));
        }

    }

    @GetMapping
    public ResponseEntity<?> getProviderById(@RequestParam(required = false) UUID id) {

        try {

            if (id != null) {
                Pair<ProviderDto, Object> provider = providerService.getProviderById(id);
                if (provider.getRight() == null) {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ApiResponse(provider.getLeft(), "Get Provider successfully"));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse(null, provider.getRight().toString()));
                }

            } else {
                Pair<List<ProviderDto>, Object> providers = providerService.getProviders();

                if (providers.getRight() == null) {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ApiResponse(providers.getLeft(), "Get Providers successfully"));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse(null, providers.getRight().toString()));
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse(null, "Something wrong with Server: " + e.getMessage()));
        }

    }

}