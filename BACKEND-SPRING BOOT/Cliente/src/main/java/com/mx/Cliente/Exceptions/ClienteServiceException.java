package com.mx.Cliente.Exceptions;


//Creamos una clase para manejar la excepcion de que este servidor no tiene un error
public class ClienteServiceException extends RuntimeException {
	
	public ClienteServiceException(String mensaje) {
		super("Error en servicio de cliente: " + mensaje );
		}

}
 