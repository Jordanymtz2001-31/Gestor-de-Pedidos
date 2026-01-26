package com.mx.Pedido.Controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.Pedido.Dtos.PedidoCompletoDto;
import com.mx.Pedido.Dtos.PedidoGuardarDto;
import com.mx.Pedido.Entity.Pedido;
import com.mx.Pedido.Services.PedidoService;

@RestController //Indicamos que esta clase es un controlador REST Y indica que los metodos retornan JSON
@RequestMapping("/pedido/pedido") // Ruta base para el controlador
public class PedidoController {
	
	@Autowired // Inyeccion de dependencias
	private PedidoService service;
	
	//Endpoint para listar todos los pedidos
	@GetMapping("/listar")
	public ResponseEntity<?> ListarPedidos(){
		if(service.listarPedidos().isEmpty()) { //Verificamos si la lista es vacia
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.ok(service.listarPedidos());
		}
	}
		
	//Endpoint para guardar un pedido
	@PostMapping("/guardar")		//Aqui decimos que los datos se van a guardar en PedidoCompletoDto que es donde esta tanto Detalles como Pedido (Todo relacionado)
	public ResponseEntity<?> GuardarPedido(@RequestBody PedidoGuardarDto  pedidoDto){
		service.guardarPedido(pedidoDto);
		return ResponseEntity.ok(Map.of("mensaje", "Pedido editado con exito")); 
	}
	
	//Endpoint para editar un pedido
	@PutMapping("/editar")
	public ResponseEntity<Map<String, String>> EditarPedido(@RequestBody Pedido pedido){
		service.editarPedido(pedido);
		return ResponseEntity.ok(Map.of("mensaje", "Pedido editado con exito")); 
		
	}
	
	//Endpoint para buscar un pedido por su id
	@GetMapping("/buscar/{idPedido}")
	public ResponseEntity<?> BuscarPorID(@PathVariable Integer idPedido){
		Pedido pedido = service.buscarID(idPedido); //Lo buscamos
		return ResponseEntity.ok(pedido);
		
	}
	
	//Enpoint para eliminar un pedido
	@DeleteMapping("/eliminar/{idPedido}")
	public ResponseEntity<Map<String, String>> EliminarPedido(@PathVariable Integer idPedido){
		service.eliminarPedido(idPedido);
		return ResponseEntity.ok(Map.of("mensaje", "Pedido eliminado con exito"));
	}

//----------ENPOINT PRSONALIZADO -------------------------------------------------------------------------------------------------------------
	
	//Endpoint personalizado para listar pedidos por clientes
	@GetMapping("/listarXCliente/{clienteId}")
	public ResponseEntity<?> ListaPedidoPorCliente(@PathVariable int clienteId){
		if(clienteId <= 0) {
			return ResponseEntity.badRequest().body(Map.of("error", "El ID del cliente debe ser mayor a 0"));
		}
		if(service.listarPedidoPorCliente(clienteId).isEmpty()) { //Verificar que la lista de pedido esta vacia
			return ResponseEntity.noContent().build(); //Retornamos que no se encontro 
		}else {
			return ResponseEntity.ok(service.listarPedidoPorCliente(clienteId)); 
		}
	}
	
	//Endpoint para listar detalles de un Pedido
	@GetMapping("/listarDetalleXPediod/{idPedido}")
	public ResponseEntity<Pedido> ObtenerDetallesDePedido(@PathVariable Integer idPedido){
		Pedido pedidoConDetalles = service.obtenerPedidoConDetalles(idPedido); // Obtenemos el pedido con detalles
		return ResponseEntity.ok(pedidoConDetalles);
	}
	
	//Endpoint para listar estatus Cancelados de un pedido 
	@GetMapping("/listar/cancelados")
	public ResponseEntity<?> ObtenerPedidoCancelados(){
		if(service.pedidosCancelados().isEmpty()) {//Verificamos si la lista es vacia
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.ok(service.pedidosCancelados());
		}
	}
}
