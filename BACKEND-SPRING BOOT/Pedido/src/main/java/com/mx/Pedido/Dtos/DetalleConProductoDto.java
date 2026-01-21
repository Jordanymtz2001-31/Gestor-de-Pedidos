package com.mx.Pedido.Dtos;

import com.mx.Pedido.Entity.Detalle_Pedido;

import lombok.Data;

//DTO para enviar el detalle del pedido junto con la informacion del producto

@Data
public class DetalleConProductoDto {
	
	private Detalle_Pedido detallePedido; // Aqui almacenamos los datos del detalle del pedido
	private ProductoDto producto;	 // Aqui almacenamos los datos del producto
}
