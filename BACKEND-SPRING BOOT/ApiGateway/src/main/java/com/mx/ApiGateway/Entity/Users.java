package com.mx.ApiGateway.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
public class Users {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Autuincrementale
	@Column(name = "id_user")
	private Long idUser;
	
	@Column(name = "USERNAME", unique = true)
	private String username;
	
	@Column(name = "PASSWORD")
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ROL")
	private ERol rol;

}
