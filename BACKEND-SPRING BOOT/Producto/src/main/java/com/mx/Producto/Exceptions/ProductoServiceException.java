package com.mx.Producto.Exceptions;

//Creamos una clase para manejar la excepcion de que este servidor no tiene un error
public class ProductoServiceException extends RuntimeException {
	
	public ProductoServiceException(String mensaje) {
		super("Error en servicio de producto: " + mensaje );
		}

}
