package com.mx.Producto.Exceptions;


// Excepción lanzada cuando se intenta crear o editar un producto con datos duplicados
public class ProductoDuplicadoException extends RuntimeException {
	
	public ProductoDuplicadoException(String campo, String valor) {
		super("El " + campo + ": '" + valor + "' ya existe en la base de datos");
	}

}
