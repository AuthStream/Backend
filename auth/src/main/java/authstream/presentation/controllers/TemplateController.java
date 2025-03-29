// package authstream.presentation.controllers;

// import java.util.List;
// import java.util.UUID;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import authstream.application.dtos.AdminDto;
// import authstream.application.dtos.ApiResponse;
// import authstream.application.services.AdminService;
// import authstream.application.services.RouteService;

// @RestController
// @RequestMapping("/admins/config")
// public class TemplateController {
//     private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

//     private final AdminService adminService;

//     public TemplateController(AdminService adminService) {
//         this.adminService = adminService;
//     }

//     @PostMapping
//     public ResponseEntity<ApiResponse> createAdmin(@RequestBody AdminDto adminDto) {

//         try {
//             AdminDto createdAdmin = adminService.createAdmin(adminDto);
//             return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(createdAdmin,"create Admin config successfully"));
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                     .body(new ApiResponse(e.getMessage(), null));
//         }
//     }

//     @GetMapping
//     public ResponseEntity<ApiResponse> getAllAdmins() {
//         List<AdminDto> admins = adminService.getAllAdmins();
//         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(admins,"get all Admin config successfully"));
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ApiResponse> getAdminById(@PathVariable UUID id) {
//         AdminDto admin = adminService.getAdminById(id);
//         return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(admin,"get Admin successfully"));
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<ApiResponse> updateAdmin(@PathVariable UUID id, @RequestBody AdminDto adminDto) {

//            try {
//         AdminDto updatedAdmin = adminService.updateAdmin(id, adminDto);
//             return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(updatedAdmin,"create Admin config successfully"));
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                     .body(new ApiResponse(e.getMessage(), null));
//         }
//     }

// }