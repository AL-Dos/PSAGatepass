package com.gatepass.backend.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gatepass.backend.Model.Auditors;
import com.gatepass.backend.Repository.AuditorRepository;
import com.gatepass.backend.Security.AuditorDetails;

@Service
public class AdminDetailsService implements UserDetailsService {
    private final AuditorRepository adminRepository;

    public AdminDetailsService(AuditorRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Auditors admin = adminRepository.findByName(name).orElseThrow(() -> new RuntimeException("Admin not found"));
        return new AuditorDetails(admin);
    }
}
