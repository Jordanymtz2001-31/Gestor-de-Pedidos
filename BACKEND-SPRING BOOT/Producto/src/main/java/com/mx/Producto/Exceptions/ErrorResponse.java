package com.mx.Producto.Exceptions;

import java.time.LocalDateTime;

import lombok.Data;

//Creamos esta clase para manejar la respuesta de error

@Data
public class ErrorResponse {	
	
	private String codigo;
	private String mensaje;
	private String timestamp = LocalDateTime.now().toString();
	
	
	//Creamos un construcctor
	public ErrorResponse(String codigo, String mensaje) {
      this.codigo = codigo;
      this.mensaje = mensaje;
  }

}