package com.gatepass.back.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gatepass.back.Model.Auditors;
import com.gatepass.back.Repository.AuditorRepository;
import com.gatepass.back.Security.AuditorDetails;

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
