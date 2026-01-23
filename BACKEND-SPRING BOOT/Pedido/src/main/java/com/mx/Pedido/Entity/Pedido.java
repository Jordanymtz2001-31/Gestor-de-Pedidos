package com.mx.Pedido.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table
public class Pedido {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PEDIDO")
	private Integer idPedido;
	
	@Column(name = "FECHA")
	private LocalDate  fecha = LocalDate.now(); // Mapeo nativo de JPA
	
	@Column(name = "TOTAL", precision = 10, scale = 2)
	private BigDecimal total; // Precicion y escala para valores decimales
	
	@Enumerated(EnumType.STRING) // Indicamos que se guarde como String en la BD
	@Column(name = "ESTATUS")
	private EEstado estatus = EEstado.CREADO; // Estado inicial del pedido
	
	@Column(name = "CLIENTE_ID")
	private int clienteId;
	
	//DEFINIMOS LA RELACION DE PEDIDO CON DETALLE PEDIDO
	
	@OneToMany(mappedBy = "idPedido", cascade = CascadeType.ALL) //Definimos la relacion uno a muchos con Detlle_Pedido
	List<Detalle_Pedido> detalles = new ArrayList<>(); // lista de Detalles asociados al pedido
	
}
	
