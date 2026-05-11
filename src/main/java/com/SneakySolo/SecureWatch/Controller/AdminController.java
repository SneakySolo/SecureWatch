package com.SneakySolo.SecureWatch.Controller;

import com.SneakySolo.SecureWatch.Dto.*;
import com.SneakySolo.SecureWatch.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/api/admin/suspicious-events")
    public ResponseEntity<List<SuspiciousEventDTO>> getAdminSuspiciousEvents() {
        List<SuspiciousEventDTO> dtoList = adminService.getAllSuspiciousEvents();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/api/admin/blocked-entities")
    public ResponseEntity<List<BlockedEntityDTO>> getAdminBlockedEntities() {
        List<BlockedEntityDTO> dtoList = adminService.getAllBlockedEntities();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/api/admin/decoy-logs")
    public ResponseEntity<List<DecoyAccessLogDTO>> getDecoyLogs() {
        List<DecoyAccessLogDTO> dtoList = adminService.getAllDecoyLogs();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/api/admin/summary")
    public ResponseEntity<AdminSummaryDTO> getAdminSummary() {
        AdminSummaryDTO dto = adminService.getSummary();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/admin/block")
    public ResponseEntity<Void> block(@RequestBody BlockRequestDTO dto,
                      Principal principal){

        adminService.blockEntity(dto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/admin/unblock/{id}")
    public ResponseEntity<Void> unblock(@PathVariable Integer id){
        adminService.unblockEntity(id);
        return ResponseEntity.noContent().build();
    }
}
