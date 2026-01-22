package com.mx.ApiGateway.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mx.ApiGateway.AuthLogin.DTOs.RegisterRequest;
import com.mx.ApiGateway.Entity.Users;
import com.mx.ApiGateway.Repository.UsersRepository;

@Service //Indicamos que es un servidor
public class UserService {
	
	@Autowired //Inyectamos la interfaz de repo de user
	private UsersRepository repoUser;
	
	@Autowired //Inyectamos la interfaz de PasswordEncoder para encriptar la contraseña
	private PasswordEncoder encoderPassword;
	
	
	//Metodo para Registrar y validar si no existe el nombre del usuario
	public Users registro(RegisterRequest request) {
		if (repoUser.findByUsername(request.getUsername()).isPresent()) {
			throw new RuntimeException("Este nombre" + request.getUsername() + "Y existe, intenta con otro");
		}
		//Si no existe entonces creamos un nuevo usuario
        Users user = new Users(); 
        
        //Y a este objeto le damos los davlores que se ingresaron en el registro
        user.setUsername(request.getUsername());
        user.setPassword(encoderPassword.encode(request.getPassword())); //Le pasamos la contraseña y la encriptamos
        user.setRol(request.getRol());
        
        //Por ultimo guardamos este usuario
        return repoUser.save(user);
	}
	
	
	//Metodo para verificar si existe el nombre
	public Optional<Users> findByUsername(String username) {
        return repoUser.findByUsername(username);
    }

}
