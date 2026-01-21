package com.example.Gateway.Security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Gateway.Entity.Roles;
import com.example.Gateway.Entity.Usuario;
import com.example.Gateway.Repository.UsuarioRepository;


// Este es un servicio que es el encargado de buscar al usuario
@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	//Metodo para traernos una lista de autoridades por medio de una lista de roles
	public Collection<GrantedAuthority> maToAutorities (List<Roles> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	//Metodo para traernos un usuario con sus datos por medio de su username
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		return new User(usuario.getUsername(), usuario.getPassword(), maToAutorities(usuario.getRoles()));
	}
	
	

}
