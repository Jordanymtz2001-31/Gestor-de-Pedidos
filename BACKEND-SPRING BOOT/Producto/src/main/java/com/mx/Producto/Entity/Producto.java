package com.mx.Producto.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "PRODUCTO")
@Data
public class Producto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PRODUCTO")
	private Integer idProducto;
	
	@Column(name = "NOMBRE")
	private String nombre;
	
	@Column(name = "PRECIO" , precision = 10, scale = 2)
	private BigDecimal precio;
	
	@Column(name = "STOCK")
	private int stock;
	
	@Column(name = "ESTATUS")
	@Enumerated(EnumType.STRING)
	private EEstatus estatus = EEstatus.DISPONIBLE;
	
	
	
}
