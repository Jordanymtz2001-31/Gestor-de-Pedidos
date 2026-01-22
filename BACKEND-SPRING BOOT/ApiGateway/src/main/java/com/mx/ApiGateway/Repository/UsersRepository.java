package com.mx.ApiGateway.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.ApiGateway.Entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long>{
	
	//Metodo para poder buscar un usuario por nombre
	Optional<Users> findByUsername(String username);
	
	//Meotod para verificar si ya existe el nombre del usuario
	boolean existsByUsername(String username);
	

}
