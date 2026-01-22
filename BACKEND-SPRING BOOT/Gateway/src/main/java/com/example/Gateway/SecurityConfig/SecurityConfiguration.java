package com.example.Gateway.SecurityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Gateway.JWTConfing.JwtAuthenticationFilter;

//Configuracion de este archivo es para indicar las reglas de seguridad de la aplicacion

@Configuration //Indica que es una clase de configuración
@EnableWebSecurity //Indica junto a @Configuration que es una clase de configuración de seguridad
@EnableMethodSecurity //Indica que se debe activar los metodos de seguridad
public class SecurityConfiguration {
	@Lazy
	@Autowired
	private JwtAuthenticationFilter  jwtAuthenticationFilter;
	
	//Esta bean va a encargar de verificar la informacion de los usuarios que se loguearan 
	//Este bean nos permite obtener el AuthenticationManager que es el encargado de gestionar la autenticacion
	@Bean 
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}

	//Este bean nos permite encriptar las contraseñas usando BCryptPasswordEncoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
	//Vamos a crear un bean el caul va a establecer una cadena de filtros de seguridad en nuestra aplicacion
	//Y es aqui donde determinamos los permisos segun los roles de usuarios para acceder a nuestros servicios
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
		.csrf(csrf -> csrf.disable()) //Por defecot se activa con el WebSecurity/ pero usaaremos confing
		.authorizeHttpRequests(auth -> auth //Nos ayudara a indicar los filtros para las urls
				
				.requestMatchers("/auth/**").permitAll() //Rutas publicas que permitAll indica que entra sin autorizacion
				.requestMatchers(HttpMethod.POST, "/cliente/**", "/pedido/**", "/detalle/**", "/producto/**").hasAuthority("ADMIN")//Rutas privadas que solo puede entrar el rol de ADMIN Para metodos POST
				.requestMatchers(HttpMethod.DELETE, "/cliente/**", "/pedido/**", "/detalle/**", "/producto/**").hasAuthority("ADMIN")//Rutas privadas que solo puede entrar el rol de ADMIN Para metodos DELET
				.requestMatchers(HttpMethod.PUT, "/cliente/**", "/pedido/**", "/detalle/**", "/producto/**").hasAuthority("ADMIN")//Rutas privadas que solo puede entrar el rol de ADMIN Para metodos PUT
				
				.requestMatchers(HttpMethod.GET, "/cliente/**", "/pedido/**", "/detalle/**", "/producto/**").hasAnyAuthority("USER", "ADMIN")//Rutas privadas que solo puede entrar el rol de USER Para metodos GET
				
				//Rutas por defecto, cuando aplicamos seguridad, pero seguimos modificando la aplicacion
				//debemos asegurar que todo tenemos seguridad y este filtro asegura que todas las rutas que no
				//haya especificado aun, llevaran authentificacion por lo menos 
				.anyRequest().authenticated()) //Esta aqui todas las rutas que maneje apiGateway// todas las aplicaciones deben de entrar por lo menos el login para entrar que es por lo defecto
		
		.sessionManagement(sesion -> sesion //Es un componente que indica como se crea, manejan y destruyen las sessiones http
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Usamos STATELESS para evitar que una sesion se cierre en automatica, ya que nosotros lo manejaremos
				.addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class) //Indicamos que nuestro filtro se ejecute antes que el filtro de autenticacion por defecto de spring security
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
			                        "mensaje": "Necesitas un token JWT válido en header Authorization: Bearer",
			                        "ruta": "%s"
			                    }
			                    """.formatted(request.getRequestURI())
			                );
			            })
			        );
		return http.build();
	}
}
