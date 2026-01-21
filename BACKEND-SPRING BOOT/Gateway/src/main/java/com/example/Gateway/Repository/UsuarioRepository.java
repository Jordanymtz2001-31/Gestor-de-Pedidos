package com.example.Gateway.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Gateway.Entity.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	//Metodo para poder buscar un usuario por nombre
	Optional<Usuario> findByUsername(String username);
	
	//Metodo para poder verificar si un usuario existe en DB
	Boolean existsByUsername(String username);
}
