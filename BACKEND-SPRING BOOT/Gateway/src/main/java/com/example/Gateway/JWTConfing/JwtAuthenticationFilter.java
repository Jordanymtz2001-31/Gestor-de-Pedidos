package com.example.Gateway.JWTConfing;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Gateway.Security.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//La funcion de esta clase sera validar la informacion del token y si este es exitiso, establecer la autenticacion de un usuario en la solicitudad que aga
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	//Inyectamos las depencencias
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	//Metodo para obtener el token de la peticion que el cliente nos da
	private String obtenerTokenFromRequest(HttpServletRequest request) { //reuest es kla peticion que obtendremos del cliente
		String bearerToken = request.getHeader("Authorization"); //Obtenemos el token del encabezado de la peticion
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) { //Validamos que el token no sea nulo y que empiece con Bearer
			return bearerToken.substring(7, bearerToken.length()); //Regresamos el token sin la palabra Bearer
		}
		return null;
	}
	
	

	@Override						//Solicitud entrante 		//Solicitud saliente		//Mecanismo para invocar el siguiente filtro
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = obtenerTokenFromRequest(request); //Obtenemos el token de la peticion
		
		if(StringUtils.hasText(token) && jwtProvider.validateToken(token)) { //Validamos que el token no sea nulo y que sea valido
			String username = jwtProvider.obtenerUserFromToken  (token); //Obtenemos el nombre de usuario del token
			
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(username); //Cargamos el objeto userDetails el cual contendra todos los detalles de nuestro username (nombre, pasword, roles)
			

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities()); //Creamos el objeto de autenticacion el cual contendra los detalles de autenticacion del usuario
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //Aca establecimos informacion adicional de la autenticacion, como por ejemplo ip del usuario
			SecurityContextHolder.getContext().setAuthentication(authentication); //Establecemos el objeto anterior (autenticacion del usuario) en el contexto de seguridad
			
			
		} //Permite que la solicitud continue hacia el siguiente filtro en la cadena de filtro
		filterChain.doFilter(request,response);
		
	}

}
