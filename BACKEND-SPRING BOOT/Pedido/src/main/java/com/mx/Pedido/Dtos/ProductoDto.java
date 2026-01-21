package com.mx.Pedido.Dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoDto {
	
	private int idProducto;
	private String nombre;
	private BigDecimal precio;
	private int stock;
	private String estatus;
}
