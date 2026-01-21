package com.mx.Cliente.Exceptions;

/**
 * Excepción lanzada cuando se intenta crear o editar un cliente con datos duplicados
 * (nombre, email o teléfono que ya existen en la base de datos)
 */
public class ClienteDuplicadoException extends RuntimeException {
	
	public ClienteDuplicadoException(String campo, String valor) {
		super("El " + campo + ": '" + valor + "' ya existe en la base de datos");
	}

}
