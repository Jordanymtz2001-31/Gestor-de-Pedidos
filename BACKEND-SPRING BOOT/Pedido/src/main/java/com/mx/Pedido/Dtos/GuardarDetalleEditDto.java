package com.mx.Pedido.Dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardarDetalleEditDto {
	
	private Integer idDetallePedido;
	private Integer cantidad;
	private BigDecimal precioUnitario;
	private Integer productoId;
	private Integer idPedido; // ← Solo el ID, no el objeto
}
