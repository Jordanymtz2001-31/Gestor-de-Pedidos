package com.mx.Cliente.Exceptions;


//Creamo una exception para cuando no se encuentre un cliente
public class ClienteNoEncontradoException extends RuntimeException{
	public ClienteNoEncontradoException(Integer id) {
        super("Cliente con ID " + id + " no encontrado");
    }

}
