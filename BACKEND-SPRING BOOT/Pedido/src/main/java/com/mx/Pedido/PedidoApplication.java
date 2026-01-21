package com.mx.Pedido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient  //Indica que este servicio se puede registrar en un servidor de descubrimiento como Eureka
@EnableFeignClients // Esta es una anotacion y otra forma de comunicacion entre microservicios
public class PedidoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PedidoApplication.class, args);
	}
 
}
