package com.mx.Cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  //Indica que este servicio se puede registrar en un servidor de descubrimiento como Eureka
public class ClienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClienteApplication.class, args);
	}

}
