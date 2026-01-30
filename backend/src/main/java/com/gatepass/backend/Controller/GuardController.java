package com.gatepass.backend.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gatepass.backend.Data.GuardLoginDTO;
import com.gatepass.backend.Model.Guard;
import com.gatepass.backend.Repository.GuardRepository;
import com.gatepass.backend.Security.JwtUtil;

@RestController
@RequestMapping("/api/guard")
public class GuardController {
    private final GuardRepository guardRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public GuardController(
        GuardRepository guardRepo,
        PasswordEncoder encoder,
        JwtUtil jwtUtil
    ) {
        this.guardRepo = guardRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody GuardLoginDTO dto) {

        Guard guard = guardRepo.findByName(dto.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!guard.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Inactive guard");
        }

        if (!encoder.matches(dto.getName(), guard.getPinHash())) {
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
            "role", "GUARD"
        ));
    }
}