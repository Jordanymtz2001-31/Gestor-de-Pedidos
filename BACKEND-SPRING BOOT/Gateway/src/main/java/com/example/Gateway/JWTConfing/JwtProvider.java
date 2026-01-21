package com.example.Gateway.JWTConfing;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

//Configuracion para seguridad 
@Component
public class JwtProvider {
	
	//Aqui mandamos a traer a jwt.secret y jwt.expiration desde aplication.properities 
	@Value("${jwt.secret}")
	private String jwtSecret;
		
	@Value("${jwt.expiration}")
	private int jwtExpiration;
	
	//Aqui generamos la clave secreta para firmar el token
	private SecretKey getSigninKey() {
		try {
			byte[] keyBytes = jwtSecret.getBytes("UTF-8"); //Aqui convertimos la clave secreta en un arreglo de bytes
			return Keys.hmacShaKeyFor(keyBytes); //Regresamos la clave secreta generada
		}catch (Exception e) {
			throw new RuntimeException("Error al crear la clave" + e);
		}
	}
	
	//Metodo para generar el token con la informacion del usuario
	public String generateToken(Authentication authentication) {
		String username = authentication.getName(); //Almacenamos el nombre del usuario
		
		//Aqui obtenemos la fecha actual y la de expiracion
		Date now = new Date();
		Date expiryDate = new Date(now.getTime()+ jwtExpiration); //La expiracion se mide en milisegundos
		
		//Y por ultimo creamos y retornamos el token generado mediante los datos obtenidos y la expiracion
		return Jwts.builder().setSubject(username) //Uuasrio que se logea
				.setIssuedAt(now) // Establecer el inicio del token
				.setExpiration(expiryDate) // Establecer la expiracion del token
				.signWith(getSigninKey(), SignatureAlgorithm.HS512).compact(); //Firma que llevara 
	} 
	
	//Metodo para extraer un username apartir de un token y verificamos que sea correcto el usuario
	public String obtenerUserFromToken(String token) {
		//Con claims obtenemos el cuerpo del token y regresamos el usuario
		Claims claims = Jwts.parserBuilder() // El metodo paserBuilder para validar tokens
				.setSigningKey(getSigninKey()) //Establecer la firma, que se utiliza para verificar
				.build()
				.parseClaimsJws(token) //Se utiliza para verificar la firma del token
				.getBody(); // Obtenemos el cuerpo ya verificado del token el cual contiene la informacion de nombre, fecha de expiracion, y firma del token
		
		return claims.getSubject(); //Devolver el nombre del usuario
	}
		
	//Metodo para validar el token, en dando caso si ya expira el token
	public boolean validateToken(String token) {
		try {//Validamos que el token sea valido
			Jwts.parserBuilder().setSigningKey(getSigninKey()).build().parseClaimsJws(token);
			return true;
		}catch (JwtException | IllegalArgumentException e) { //Sino es valido arrogamos una excepcion
			System.out.println("Token invalido o expirado" + e.getMessage());
		}
		return false;
	}
}
