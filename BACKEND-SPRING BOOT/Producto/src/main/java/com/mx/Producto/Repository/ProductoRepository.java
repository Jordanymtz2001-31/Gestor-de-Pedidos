package com.mx.Producto.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.Producto.Entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
	
	//METODOS PERSONALIZADOS
	
	//Metodos para validar que no se repita el nombre del producto
	boolean existsByNombre(String nombre);
	
	

}
