package com.gatepass.backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieConfig {
    @Value("${app.cookie.secure:false}")
    private boolean secure;

    @Value("${app.cookie.same-site:Lax}")
    private String sameSite;

    @Value("${app.cookie.max-age:86400}")
    private int maxAge;

    public boolean isSecure() {
        return secure;
    }

    public String getSameSite() {
        return sameSite;
    }

    public int getMaxAge() {
        return maxAge;
    }
}
