package com.mx.ApiGateway.AuthLogin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Endpoint de bienvenida después de autenticarse
     * Devuelve la información del usuario autenticado
     */
    @GetMapping("/login")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok("No hay usuario autenticado");
        }
        
        var userInfo = new java.util.HashMap<String, Object>();
        userInfo.put("username", authentication.getName());
        userInfo.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(userInfo);
    }

}
