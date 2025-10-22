package com.gatepass.back.Security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gatepass.back.Model.Auditors;

public class AuditorDetails implements UserDetails{
    private final Auditors auditor;

    public AuditorDetails(Auditors auditor) {
        this.auditor = auditor;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() {
        return auditor.getPassword();
    }

    @Override
    public String getUsername() { 
        return auditor.getName(); 
    }
}
