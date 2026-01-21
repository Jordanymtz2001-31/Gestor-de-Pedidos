package com.mx.Pedido.PedidoExceptions;

//Creamos una clase para manejar la excepcion de que este servidor no tiene un error
public class DetalleServiceException extends RuntimeException {
	
	public DetalleServiceException(String mensaje) {
		super("Error en servicio de detalle de pedido: " + mensaje );
		}

}
