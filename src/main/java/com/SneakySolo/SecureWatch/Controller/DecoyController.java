package com.SneakySolo.SecureWatch.Controller;

import com.SneakySolo.SecureWatch.Service.DetectionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DecoyController {

    private final DetectionService detectionService;

    @GetMapping("api/decoy/admin-access")
    public ResponseEntity<Object> getDecoyAdminAccess(Principal principal, HttpServletRequest request) {

        String username = "Anonymous";
        if (principal != null) {
            username = principal.getName();
        }
        detectionService.registerDecoyAccess(username, request.getRemoteAddr(), "api/decoy/admin-access");
        Map<String, String> data = new HashMap<>();
        data.put("Username : ", null);
        data.put("Password : ", null);
        data.put("Access Time : ", LocalDateTime.now().toString());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping("/api/decoy/export-users")
    public ResponseEntity<Object> decoyExportUsers(Principal principal, HttpServletRequest request) {

        detectionService.registerDecoyAccess(principal.getName(), request.getRemoteAddr(), "/api/decoy/export-users");
        Map<String, String> data = new HashMap<>();
        data.put("Username : ", null);
        data.put("Password : ", null);

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/api/decoy/system-config")
    public ResponseEntity<Object> getDecoySystemConfig(Principal principal, HttpServletRequest request) {

        detectionService.registerDecoyAccess(principal.getName(), request.getRemoteAddr(), "api/decoy/system-config");
        Map<String, String> data = new HashMap<>();
        data.put("System : ", null);
        data.put("Access : ", null);

        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
