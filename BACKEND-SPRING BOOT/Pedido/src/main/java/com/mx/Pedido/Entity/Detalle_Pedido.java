package com.mx.Pedido.Entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table
public class Detalle_Pedido {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_DETALL_PEDIDO")
	private Integer idDetallePedido;
	
	@Column(name = "CANTIDAD")
	private int  cantidad;
	
	@Column(name = "PRECIO_UNITARIO", precision = 10, scale = 2)
	private BigDecimal precioUnitario;

	//DEFINIMOS LA RELACIÓN DE DETALLE PEDIDO CON PEDIDO
	@JsonIgnore  //Lo colocamos para No serializar el Pedido en JSON sino se hace un bucle ya que pedido tiene detalles y esos detalle esta ligada a Pedido
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PEDIDO_ID", nullable = false)
	private Pedido idPedido;
	
	@Column(name = "PRODUCTO_ID")
	private int productoId;
	
}
