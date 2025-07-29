package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import com.pragma.plazoleta.infrastructure.exception.InfraestructureException;
import com.pragma.plazoleta.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityContextAdapter implements ISecurityContextPort {
    
    private final JwtService jwtService;

    @Override
    public String getRoleOfUserAutenticated() {
        String token = extractTokenFromRequest();
        return jwtService.extractRole(token);
    }

    @Override
    public UUID getUserIdOfUserAutenticated() {
        String token = extractTokenFromRequest();
        String userId = jwtService.extractUserId(token);
        return UUID.fromString(userId);
    }
    
    private String extractTokenFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                throw new InfraestructureException("No request context found");
            }
            
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new InfraestructureException("No valid Authorization header found");
            }
            
            return authHeader.substring(7);
        } catch (Exception e) {
            throw new InfraestructureException("Error extracting token from request: " + e.getMessage());
        }
    }
}
