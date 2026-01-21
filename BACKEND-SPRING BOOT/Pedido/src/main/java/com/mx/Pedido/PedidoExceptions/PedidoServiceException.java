package com.mx.Pedido.PedidoExceptions;

//Creamos una clase para manejar la excepcion de que este servidor no tiene un error
public class PedidoServiceException extends RuntimeException {
	
	public PedidoServiceException(String mensaje) {
		super("Error en servicio de pedido: " + mensaje );
		}

}
