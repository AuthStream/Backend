package authstream.presentation.controllers;

import authstream.application.dtos.ForwardDto;

// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import authstream.application.services.ProviderService;
// import authstream.domain.entities.Provider;
// import authstream.utils.Util;

// import org.springframework.web.bind.annotation.RequestBody;

// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// // import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.PathVariable;

// @RestController
// @RequestMapping("/providers")
// public class ProviderController {
//     private final ProviderService providerService;

//     public ProviderController(ProviderService providerService) {
//         this.providerService = providerService;
//     }

//     @PostMapping("")
//     public ResponseEntity<?> createProvider(@RequestBody Provider provider) {
//         try{
//             providerService.createProvider(provider);
//             return ResponseEntity.ok("Provider created successfully");
//         }
//         catch(Exception e){
//             e.printStackTrace();
//             return ResponseEntity.badRequest().body("Error creating Provider");
//         }        
//     }

//     @GetMapping("")
//     public ResponseEntity<?> getProvider() {
//         List<Provider> providers;
//         try{
//             providers = providerService.getProviders();
//             // System.out.println(providers.get(0));
//             return ResponseEntity.ok(providers);
//         }
//         catch(Exception e){
//             e.printStackTrace();
//             return ResponseEntity.badRequest().body("Error getting providers");
//         }

//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<?> getProviderById(@PathVariable String param) {
//         Provider provider;
//         try{
//             provider = providerService.getProviderById(param);
//             return ResponseEntity.ok(provider);
//         }
//         catch(Exception e){
//             e.printStackTrace();
//             return ResponseEntity.badRequest().body("Error getting Provider by id");
//         }

//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<?> putMethodName(@PathVariable String id, @RequestBody Provider provider) {
//         //TODO: process PUT request

//         Provider existedProvider = providerService.getProviderById(id);
//         if(existedProvider == null) {
//             return ResponseEntity.status(404).body("message: Provider not found");
//         }

//         existedProvider = Util.updateNonNullFields(existedProvider, provider);

//         try {
//             providerService.updateProvider(existedProvider);
//             return ResponseEntity.ok(existedProvider);
//         } catch(Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(500).body("message: Error updating Provider");
//         }

//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteMethodName(@PathVariable String id) {

//         //TODO: process DELETE request
//         try {
//             providerService.deleteProvider(id);
//             return ResponseEntity.ok().body("message: Provider deleted!, Provider id: " + id);
//         } catch(Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(500).body("message: Error deleting Provider");
//         }
//     }

// }

import authstream.application.dtos.ProviderDto;
import authstream.application.services.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> deleteProvider(@PathVariable String id) {
        providerService.deleteProvider(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> GetProviders(@PathVariable String id) {
        ProviderDto provider = providerService.getProviderById(id);
        return provider != null ? ResponseEntity.ok(provider) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<?> getProviderById(@RequestParam(required = false) String id) {
        if (id != null) {
            ProviderDto provider = providerService.getProviderById(id);
            return provider != null ? ResponseEntity.ok(provider) : ResponseEntity.notFound().build();
        } else {
            List<ProviderDto> providers = providerService.getProviders();
            return ResponseEntity.ok(providers);
        }
    }

}