package com.gatepass.backend.Controller;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gatepass.backend.Data.GuardLoginDTO;
import com.gatepass.backend.Model.Guard;
import com.gatepass.backend.Repository.GuardRepository;
import com.gatepass.backend.Security.JwtUtil;

import com.gatepass.backend.Repository.GatepassRepository;

@RestController
@RequestMapping("/api/guard")
public class GuardController {
    private final GuardRepository guardRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final GatepassRepository gatepassRepo;

    public GuardController(
            GuardRepository guardRepo,
            PasswordEncoder encoder,
            JwtUtil jwtUtil,
            GatepassRepository gatepassRepo) {
        this.guardRepo = guardRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.gatepassRepo = gatepassRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody GuardLoginDTO dto) {

        Guard guard = guardRepo.findByName(dto.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!guard.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Inactive guard");
        }

        if (!encoder.matches(dto.getPin(), guard.getPinHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid PIN");
        }

        UserDetails guardUser = User.builder()
                .username(guard.getName())
                .password("")
                .roles("GUARD")
                .build();

        String token = jwtUtil.generateToken(guardUser);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", "GUARD"));
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scan(@RequestBody com.gatepass.backend.Data.ScanRequestDTO dto, Authentication authentication) {
        if (dto.getQrToken() == null) {
            return ResponseEntity.badRequest().body("Missing QR Token");
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Guard verifiedGuard = guardRepo.findByName(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!verifiedGuard.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Inactive guard");
        }

        com.gatepass.backend.Model.Gatepass gatepass = gatepassRepo.findByQrToken(dto.getQrToken());

        if (gatepass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR Code");
        }

        String action = "";

        if (!gatepass.isReleased()) {
            gatepass.setReleased(true);
            gatepass.setReleasedAt(OffsetDateTime.now(ZoneId.of("Asia/Manila")));
            action = "Released";
        } else if (!gatepass.isReturned()) {
            gatepass.setReturned(true);
            gatepass.setReturnedAt(OffsetDateTime.now(ZoneId.of("Asia/Manila")));
            action = "Returned";
        } else {
            return ResponseEntity.badRequest().body("Gatepass already completed (Returned)");
        }

        gatepassRepo.save(gatepass);

        return ResponseEntity.ok(Map.of(
                "message", "Success: Items " + action,
                "guard", verifiedGuard.getName(),
                "action", action,
                "gatepass", gatepass));
    }
}
