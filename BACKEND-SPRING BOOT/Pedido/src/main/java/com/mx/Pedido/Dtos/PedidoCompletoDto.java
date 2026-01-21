package com.mx.Pedido.Dtos;

import java.util.List;

import com.mx.Pedido.Entity.Pedido;

import lombok.Data;

//Creamos este DTO para enviar el pedido junto con sus detalles

@Data
public class PedidoCompletoDto {
	
	private Pedido pedido; // Aqui al,acenamos todos los datos del pedido
	private List<DetalleConProductoDto> detalles; // Aqui almacenamos los detalles del pedido
}
