package com.mx.Pedido.PedidoExceptions;

// Excepción cuando el servicio de Producto no responde o hay error
public class ProductoServiceException extends RuntimeException {
	
	public ProductoServiceException(String mensaje) {
		super("Error al comunicarse con servicio de Producto: " + mensaje);
	}
	
	public ProductoServiceException(String mensaje, Throwable cause) {
		super("Error al comunicarse con servicio de Producto: " + mensaje, cause);
	}
}
