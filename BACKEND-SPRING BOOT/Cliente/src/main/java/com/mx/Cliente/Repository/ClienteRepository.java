package com.mx.Cliente.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.Cliente.Entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
	
	// Aqui podemos agregar metodos personalizados si es necesario
	
	//METODOS PERSONALIZADOS
	
	//Metodo para validar si existe el nombre ya registrado
	boolean existsByNombreIgnoreCase(String nombre);
	
	//Metodo para validar si existe el email ya registrado
	boolean existsByEmailIgnoreCase(String email);
	
	//Metodo para validar si existe el telefono ya registrado
	boolean existsByTelefonoIgnoreCase(String telefono);
	
	

}
