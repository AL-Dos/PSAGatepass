package com.gatepass.backend.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gatepass.backend.Model.Auditors;
import com.gatepass.backend.Repository.AuditorRepository;
import com.gatepass.backend.Security.AuditorDetails;

@Service
public class CustomUserService implements UserDetailsService{
    private final AuditorRepository repo;

    public CustomUserService(AuditorRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Auditors aud = repo.findByName(name).orElseThrow(() -> new UsernameNotFoundException("User not found: " + name));
        return new AuditorDetails(aud);
    }
}
