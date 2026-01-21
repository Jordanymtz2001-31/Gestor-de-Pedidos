package com.mx.Cliente.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table
@Data
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Indicar que el id se generara automaticamnete
	@Column(name = "ID_CLIENTE")
	private Integer idCliente;
	
	@Column(name = "NOMBRE")
	private String nombre;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "TELEFONO")
	private String telefono;
	
	@Column(name = "ESTATUS")
	@Enumerated(EnumType.STRING)
	private EEstatus estatus;

}
