package com.mx.Pedido.Dtos;


import java.util.List;
import lombok.Data;

@Data
public class PedidoGuardarDto {
	
	//private Integer idPedido;			NO ES NECESARIO, ES AUTOINCREMENTABLE

	//private LocalDate  fecha = LocalDate.now(); NO ES NECESARIO, SE LE COLOCA LA FECHA POR DEFAULT

	//private EEstado estatus = EEstado.CREADO; // NO ES NECESARIO, POR DEFAULT YA SE LE COLOCA EL ESTATUS CREADO
	
	//EN ESTE ORDEN PARA QUE SE VEA BIEN
	private int clienteId;
	//private BigDecimal total; // No se va a mandar el total, ya este lo vamos a calcular
	private List<DetallePedidoGuardarDto> detalles; // TRAEMOS LOS DEMAS DATOS DE DETALLE PARA GUARDAR
	
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
