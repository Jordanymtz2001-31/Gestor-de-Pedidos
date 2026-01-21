package com.mx.Pedido.Controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.Pedido.Dtos.GuardarDetalleEditDto;
import com.mx.Pedido.Entity.Detalle_Pedido;
import com.mx.Pedido.Services.DetallePedidoService;


@RestController // Indicamos que esta clase es un controlador REST Y indica que los metodos retornan JSON
@RequestMapping("/detalle") // Ruta base para el controlador
public class DetallePedidoController {
	
	@Autowired //Inyectamos las dependencias
	private DetallePedidoService service;
	
	// Endpoint para listar Detalles
	@GetMapping("/listar")
	public ResponseEntity<?> ListarDetalle(){
		if(service.listarDetallePedidos().isEmpty()) { // Validamos si la lista es vacia
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.ok(service.listarDetallePedidos());
		}
	}
	
	//Endpoint para guardar un detalle de pedido
	@PostMapping("/guardar")
	public ResponseEntity<Map<String, String>> GuardarDetallePedido(@RequestBody GuardarDetalleEditDto detalleDto){
		service.guardarDetallePedido(detalleDto);
		return ResponseEntity.status(201).body(Map.of("mensaje", "Detalle de pedido guardado con exito"));
	}
	
	//Endpoint para editar un detalle de pedido
	@PutMapping("/editar")
	public ResponseEntity<Map<String, String>> EditarDetalle(@RequestBody GuardarDetalleEditDto detalle){
		service.editarDetallePedido(detalle);
		return ResponseEntity.ok(Map.of("mensaje", "Detalle de pedido editado con exito"));
		
	}
	
	//Endpoint para buscar 
	@GetMapping("/buscar/{idDetalle}")
	public ResponseEntity<?> Buscar(@PathVariable Integer idDetalle){
		Detalle_Pedido detalle = service.buscarID(idDetalle);
		return ResponseEntity.ok(detalle);
	}
	
	//Endpoint para eliminar por ID
	@DeleteMapping("/eliminar/{idDetalle}")
	public ResponseEntity<Map<String, String>> Eliminar(@PathVariable Integer idDetalle){
		service.eliminarDetallePedido(idDetalle);
		return ResponseEntity.ok(Map.of("mensaje", "Detalle de pedido eliminado con exito"));
	}
	

}
