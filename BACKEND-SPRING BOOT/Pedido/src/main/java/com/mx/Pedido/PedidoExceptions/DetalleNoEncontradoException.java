package com.mx.Pedido.PedidoExceptions;

//Creamo una exception para cuando no se encuentre un detalle de pedido
public class DetalleNoEncontradoException extends RuntimeException{
	
	public DetalleNoEncontradoException(Integer id) {
		super("Detalle con ID " + id + " no encontrado");
	}

}
