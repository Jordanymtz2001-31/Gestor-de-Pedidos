package com.mx.Pedido.RestController;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // Indica que esta clase contiene configuraciones de Spring y puede definir beans
public class RestControllerConfing {
	
	//Define un bean de RestTemplate para que pueda ser inyectado en otras partes de la aplicación
	
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	//RestTemplate es una clase de Spring que facilita la comunicación con servicios RESTful
	//Con los metodos HTTP como GET, POST, PUT, DELETE, etc.
	//Al tener esta configuracion, convierte mi microservicio en un cliente REST

}
