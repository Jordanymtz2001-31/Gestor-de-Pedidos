package com.mx.ApiGateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.mx.ApiGateway.Entity.Users;
import com.mx.ApiGateway.Service.UserService;

@Configuration  // ← Registra esta clase como bean de configuración de Spring
@EnableWebSecurity  // ← Habilita Spring Security WebFlux para Gateway (NO @EnableWebFluxSecurity)
public class SecurityConfig {

    // ⭐ ENCRIPTADOR DE PASSWORD - BCrypt (estándar industria)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Hashea passwords (123 → $2a$10$...)
    }

    // ⭐ PUENTE Spring Security ↔ Base de Datos
    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {  // Spring llama esto AUTOMÁTICAMENTE en cada Basic Auth
            Users user = userService.findByUsername(username)  // SELECT * FROM users WHERE username=?
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no Encontrado"));
            return User.builder()  // Convierte tu entidad Users → Spring Security User
                .username(user.getUsername())
                .password(user.getPassword())  // Password HASHEADO
                .roles(user.getRol().name())   // "ADMIN" → ROLE_ADMIN automáticamente
                .build();
        };
    }
    
    // ⭐ CADENA PRINCIPAL DE FILTROS - CORAZÓN DE LA SEGURIDAD
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/cliente/**", "/pedido/**", "/producto/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/cliente/**", "/pedido/**", "/producto/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/cliente/**", "/pedido/**", "/producto/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/cliente/**", "/pedido/**", "/producto/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())  // ⭐ Basic Auth (reemplaza JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                        {
                            "error": "Acceso denegado",
                            "mensaje": "Estás autenticado pero no tienes permisos para esta acción",
                            "ruta": "%s",
                            "metodo": "%s"
                        }
                        """.formatted(request.getRequestURI(), request.getMethod())
                    );
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                        {
                            "error": "No autenticado",
                            "mensaje": "Necesitas credenciales Validas Basic Auth en header Authorization",
                            "ruta": "%s"
                        }
                        """.formatted(request.getRequestURI())
                    );
                })
            );
        return http.build();
    }
}

