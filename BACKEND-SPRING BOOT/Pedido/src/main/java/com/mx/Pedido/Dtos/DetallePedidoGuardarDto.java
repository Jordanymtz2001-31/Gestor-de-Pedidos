package com.mx.Pedido.Dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DetallePedidoGuardarDto {
	
	//Aqui declaramos solo las variables que se van a guardar
	
	//private Integer idDetallePedido;      NO ES NECESARIO GUARDAR, ES AUTOINCREMENTABLE

	//private Pedido idPedido;   			NO ES NECESARIO 

	
	//EN ESTE ORENDEN PARA QUE SE VEA BIEN
	private int productoId;					//NECESARIO PARA GUARDAR EL PRODUCTO QUE PUEDE ESTAR RELACIONADO
	private int  cantidad;
	private BigDecimal precioUnitario;
	
	/* 
	{
  	"clienteId": 1,
  	"detalles": [
    	{ "productoId": 5, "cantidad": 2, "precioUnitario": 100.00 },
    	{ "productoId": 8, "cantidad": 1, "precioUnitario": 50.00 }
  		]
	}

	*/

}
