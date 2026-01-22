package com.mx.ApiGateway.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.ApiGateway.AuthLogin.DTOs.RegisterRequest;
import com.mx.ApiGateway.Entity.Users;
import com.mx.ApiGateway.Service.UserService;

@RestController //Indicamos que esta clase ocupara Jackson y que son APIRest
@RequestMapping("/auth")
public class AuthController {

	@Autowired //Inyectamos la dependencia de servicio de User
	private UserService serviceUser;
	
	@Autowired //Inyecatmos la dependencia para encreiptar contraeñas
	private PasswordEncoder passwordEncoder;
	
	//Endpoint para registrarse
	@PostMapping("/registro")
	public ResponseEntity<?> registro (@RequestBody RegisterRequest request){
		try { //Lo guardamos si no hay ningun error
			serviceUser.registro(request);
			return ResponseEntity.ok("Usuario registrado Correctamente");
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	/*CONCLUSIÓN: En Basic Auth NO existe "login". Las credenciales van directo en cada request. 
	 * Es como si estuvieras "loguiéndote 1000 veces por segundo". Por eso /auth/login es inútil y confuso. */
	
	//Este metodo solo es Test login (opcional)
	@PostMapping("/login") 
    public ResponseEntity<String> login(@RequestBody RegisterRequest request) {
        Optional<Users> user = serviceUser.findByUsername(request.getUsername());
        if (user.isPresent() && passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.ok("Login success: " + user.get().getRol());
        }
        return ResponseEntity.status(401).body("Credenciales Invalidas");
    }
}
