package com.mx.ApiGateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * CLASE: SecurityConfig
 * 
 * ╔════════════════════════════════════════════════════════════════╗
 * ║  PUNTO ÚNICO DE CONTROL DE SEGURIDAD DEL SISTEMA              ║
 * ║  Esta es la ÚNICA clase SecurityConfig del proyecto            ║
 * ║  (Cliente, Producto, Pedido NO tienen SecurityConfig)         ║
 * ╚════════════════════════════════════════════════════════════════╝
 * 
 * Esta clase configura toda la seguridad del API Gateway usando Spring Security.
 * El ApiGateway es el **punto de entrada único** para todas las solicitudes.
 * 
 * ARQUITECTURA DE SEGURIDAD:
 * 
 *     Cliente HTTP
 *          ↓
 *     ┌─────────────────┐
 *     │  API GATEWAY    │  ← AQUÍ se validan credenciales y permisos
 *     │ (SecurityConfig)│  ← ÚNICO lugar donde se decide quién accede
 *     └────────┬────────┘
 *              ↓
 *     ┌────────────────────┐
 *     │ Microservicios:    │  ← Reciben solicitudes PRE-VALIDADAS
 *     │ - Cliente          │  ← No necesitan SecurityConfig
 *     │ - Producto         │  ← No necesitan validar permisos
 *     │ - Pedido           │  ← Solo procesan el negocio
 *     └────────────────────┘
 * 
 * AUTENTICACIÓN:
 * - Método: Basic Authentication (usuario y contraseña)
 * - Almacenamiento: En memoria (InMemoryUserDetailsManager)
 * - NO utiliza tokens JWT
 * 
 * AUTORIZACIÓN (Roles y Permisos):
 * - ROL ADMIN: Acceso completo (lectura + escritura en todos los endpoints)
 * - ROL USER: Solo lectura (GET requests)
 * 
 * FLUJO DE SEGURIDAD:
 * 1. Cliente envía solicitud HTTP con credenciales (Basic Auth)
 * 2. SecurityFilterChain valida las credenciales
 * 3. Se verifica el rol del usuario
 * 4. Se validan los permisos según el endpoint y método HTTP
 * 5. Si el usuario tiene los permisos, se procesa la solicitud
 * 6. Si no, se devuelve un error 403 Forbidden
 * 
 * VENTAJAS DE ESTA ARQUITECTURA:
 * ✅ Punto único de control: Una sola configuración para todo el sistema
 * ✅ Mantenimiento fácil: Cambios de permisos en un solo lugar
 * ✅ Microservicios simples: No necesitan lógica de seguridad
 * ✅ Patrón API Gateway: Cumple con arquitectura de microservicios
 * ✅ Escalable: Agregar nuevos microservicios es más fácil
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * MÉTODO: userDetailsService()
     * 
     * Propósito: Crear y configurar los usuarios en memoria del sistema.
     * 
     * USUARIOS DISPONIBLES:
     * 1. admin / admin123
     *    - Rol: ADMIN
     *    - Permisos: Acceso completo (GET, POST, PUT, DELETE)
     * 
     * 2. user / user123
     *    - Rol: USER
     *    - Permisos: Solo lectura (GET)
     * 
     * Nota: Las contraseñas se codifican con BCrypt antes de almacenarse
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    	return new InMemoryUserDetailsManager(
                User.builder().username("user").password(passwordEncoder.encode("user123")).roles("USER").build(),
                User.builder().username("admin").password(passwordEncoder.encode("admin123")).roles("ADMIN").build()
            );
    }

    /**
     * MÉTODO: passwordEncoder()
     * 
     * Propósito: Crear el codificador de contraseñas BCrypt.
     * 
     * BCrypt es un algoritmo de hash seguro que:
     * - Codifica las contraseñas de forma irreversible
     * - Añade un "salt" aleatorio para mayor seguridad
     * - Hace que sea prácticamente imposible recuperar la contraseña original
     * 
     * Se utiliza para:
     * - Codificar contraseñas al crear usuarios
     * - Validar contraseñas en el login
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * MÉTODO: filterChain()
     * 
     * Propósito: Configurar la cadena de filtros de seguridad (SecurityFilterChain).
     * 
     * Define:
     * - Qué endpoints requieren autenticación
     * - Qué roles pueden acceder a cada endpoint
     * - Qué métodos HTTP están permitidos por rol
     * 
     * ESTRUCTURA DE PERMISOS:
     * 
     * ENDPOINTS PÚBLICOS (Sin autenticación):
     * - GET /health: Verificar estado del servidor
     * - GET /auth/**:  Rutas de autenticación pública
     * 
     * ENDPOINTS CON AUTENTICACIÓN - LECTURA (GET):
     * - /cliente/** : Listar clientes (USER + ADMIN)
     * - /producto/***: Listar productos (USER + ADMIN)
     * - /pedido/**  : Listar pedidos (USER + ADMIN)
     * - /detalle/** : Listar detalles (USER + ADMIN)
     * 
     * ENDPOINTS CON AUTENTICACIÓN - ESCRITURA (POST, PUT, DELETE):
     * - /cliente/** : Crear, actualizar, eliminar clientes (Solo ADMIN)
     * - /producto/***: Crear, actualizar, eliminar productos (Solo ADMIN)
     * - /pedido/**  : Crear, actualizar, eliminar pedidos (Solo ADMIN)
     * - /detalle/** : Crear, actualizar, eliminar detalles (Solo ADMIN)
     * 
     * MÉTODO: hasAnyRole("USER", "ADMIN")
     * - Permite acceso a usuarios con rol USER o ADMIN
     * 
     * MÉTODO: hasRole("ADMIN")
     * - Permite acceso solo a usuarios con rol ADMIN
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 🔓 PÚBLICO
                .requestMatchers("/auth", "/login/**").permitAll()
                
                // 👤 USER (lectura)
                .requestMatchers("/cliente/**", "/producto/**").hasAuthority("ROLE_USER")
                
                // 👑 ADMIN (escritura + todo)
                .requestMatchers("/pedido/**", "/detalle/**").hasAuthority("ROLE_ADMIN")
                
                // Todo lo demás requiere auth
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }

}
