package com.example.Gateway.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Long idUsuario;
	private String username;
	private String password;
	
	//Usamos FetchType en EAGER para que cada ves que acceda o se extraiga un usuario de la DB, este se traiga todos sus roles
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // Relación muchos a muchos con la entidad Roles
	
	@JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id_usuario") //Aqui creamos una tabla donde se une la tabala de Usuarios
	, inverseJoinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id_role")) // Y la tabla de Role
	private List<Roles> roles = new ArrayList<>();

}
