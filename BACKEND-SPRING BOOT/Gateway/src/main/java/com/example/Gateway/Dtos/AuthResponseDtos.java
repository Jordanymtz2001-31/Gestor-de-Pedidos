package com.example.Gateway.Dtos;

import lombok.Data;

//Esta clase es la que nos devolvera la informacion con el token y el tipo que tenga este
@Data
public class AuthResponseDtos {
	
	private String accessToken;
	private String tokenType = "Bearer " ;
	
	//Contrucctor para solo accessToken
	public AuthResponseDtos(String accessToken) {
		this.accessToken = accessToken;
	}

}
