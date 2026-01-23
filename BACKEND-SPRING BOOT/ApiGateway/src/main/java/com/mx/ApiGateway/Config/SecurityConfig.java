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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mx.ApiGateway.Entity.Users;
import com.mx.ApiGateway.Service.UserService;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {
            Users user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no Encontrado"));
            return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRol().name())
                .build();
        };
    }
    
    // ⭐ CORS CONFIGURATION - ANTES DE authorizeHttpRequests
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:4200"));  // Angular
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));  // Authorization, Content-Type, etc
        config.setAllowCredentials(true);        // Basic Auth cookies
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ⭐ CORS PRIMERO - ANTES DE authorizeHttpRequests
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(auth -> auth
                // ⭐ OPTIONS LIBRES para preflight (Angular)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Reglas originales
                //IMPORTANTE: Si importa el ORDEN
                .requestMatchers(HttpMethod.POST, "/cliente/**", "/pedido/**", "/producto/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/cliente/**", "/pedido/**", "/producto/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/cliente/**", "/pedido/**", "/producto/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/cliente/**", "/pedido/**", "/producto/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
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
