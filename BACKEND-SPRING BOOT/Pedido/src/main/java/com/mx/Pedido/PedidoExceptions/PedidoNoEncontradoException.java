package com.mx.Pedido.PedidoExceptions;

//Creamo una exception para cuando no se encuentre un pedido
public class PedidoNoEncontradoException extends RuntimeException{
	public PedidoNoEncontradoException(Integer id) {
		super("Pedido con ID " + id + " no encontrado");
	}

}
