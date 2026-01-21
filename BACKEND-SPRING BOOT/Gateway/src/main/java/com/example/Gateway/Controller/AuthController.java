package com.example.Gateway.Controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Gateway.Dtos.AuthResponseDtos;
import com.example.Gateway.Dtos.LoginDtos;
import com.example.Gateway.Dtos.RegistroDtos;
import com.example.Gateway.Entity.Roles;
import com.example.Gateway.Entity.Usuario;
import com.example.Gateway.JWTConfing.JwtProvider;
import com.example.Gateway.Repository.RolRepositroy;
import com.example.Gateway.Repository.UsuarioRepository;


@RestController
@RequestMapping("/auth")
public class AuthController {
	
	//Intectamos todas las dependencias necesarias
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private PasswordEncoder passworEncoder;
	
	@Autowired
	private RolRepositroy rolesRepo;
	 
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	//Metodo para crear un Usuario con el rol de User normal
	@PostMapping("/registro/user")
	public ResponseEntity<String> registroUser (@RequestBody RegistroDtos dtoRegistro){
		if(usuarioRepo.existsByUsername(dtoRegistro.getUsername())) { //Si existe este usuario
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario ya existe, intente con otro");
		}
		Usuario usuario = new Usuario();
		usuario.setUsername(dtoRegistro.getUsername());
		usuario.setPassword(dtoRegistro.getPassword());
		
		//Encriptamos la contraseña
		usuario.setPassword(passworEncoder.encode(dtoRegistro.getPassword()));
		Roles roles = rolesRepo.findByName("USER").get();
		usuario.setRoles(Collections.singletonList(roles));
		usuarioRepo.save(usuario);
		return ResponseEntity.status(201).body("Usuario guardado con éxito"); 
	}
	
	//Metodo para crear un Usuario con el rol de Admin
	@PostMapping("/registro/admin")
	public ResponseEntity<String> registroAdmin (@RequestBody RegistroDtos dtoRegistro){
		if(usuarioRepo.existsByUsername(dtoRegistro.getUsername())) { //Si existe este usuario
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario ya existe, intente con otro");
		}
		Usuario usuario = new Usuario();
		usuario.setUsername(dtoRegistro.getUsername());
		usuario.setPassword(dtoRegistro.getPassword());
		
		//Encriptamos la contraseña
		usuario.setPassword(passworEncoder.encode(dtoRegistro.getPassword()));
		Roles roles = rolesRepo.findByName("ADMIN").get();
		usuario.setRoles(Collections.singletonList(roles));
		usuarioRepo.save(usuario);
		return ResponseEntity.status(201).body("Usuario guardado con éxito"); 
	}
	
	//Metodo para poder logearse y obtener el token
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDtos> login (@RequestBody LoginDtos loginDtos){
		
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				loginDtos.getUsername(), loginDtos.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = jwtProvider.generateToken(authentication);
		
		return ResponseEntity.ok(new AuthResponseDtos(token));
	}
}
