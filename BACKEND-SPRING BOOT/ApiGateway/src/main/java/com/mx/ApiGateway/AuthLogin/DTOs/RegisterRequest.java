package com.mx.ApiGateway.AuthLogin.DTOs;

import com.mx.ApiGateway.Entity.ERol;

import lombok.Data;

//Clase que recibira la peticion del registro (es decir los dato entraran aqui)
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private ERol rol;
    
}
