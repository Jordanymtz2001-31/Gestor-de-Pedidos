package com.example.Gateway.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Gateway.Entity.Roles;


public interface RolRepositroy extends JpaRepository<Roles, Long>{
	
	//Metodo para buscar un role por su nombre en nuestra DB
	Optional<Roles> findByName(String name);
	
}
