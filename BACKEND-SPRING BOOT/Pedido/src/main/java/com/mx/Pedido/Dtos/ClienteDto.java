package com.mx.Pedido.Dtos;

import lombok.Data;

@Data
public class ClienteDto {
	
	private int idCliente;
	private String nombre;
	private String email;
	private String telefono;
	private String Estatus;
}
