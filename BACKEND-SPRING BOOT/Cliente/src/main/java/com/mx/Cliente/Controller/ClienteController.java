package com.mx.Cliente.Controller;

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

import com.mx.Cliente.Entity.Cliente;
import com.mx.Cliente.Service.ClienteService;

@RestController //Indicamos que esta clase es un controlador y que los metodos retornan JSON
@RequestMapping("/cliente") // Ruta base para el controlador
public class ClienteController {
	
	@Autowired //Inyectamos el servicio
	private ClienteService service;

	//Endpoint para listar todos los clientes
	@GetMapping("/listar")
	public ResponseEntity<?> ListarCliente(){
		if(service.listarClientes().isEmpty()) { //Verificamos si la lista es vacia
			return ResponseEntity.noContent().build(); 
		}
		return ResponseEntity.ok(service.listarClientes());
	}
	
	//Endpoint para guardar un cliente
	@PostMapping("/guardar")
	public ResponseEntity<Map<String, String>> GuardarCliente(@RequestBody Cliente cliente){
		service.guardarCliente(cliente);
		return ResponseEntity.ok(Map.of("mensaje", "Cliente guardado con éxito"));
	}
	
	//Endpoint para editar un cliente
	@PutMapping("/editar")
	public ResponseEntity<Map<String, String>> EditarCliente(@RequestBody Cliente cliente){
		service.editarCliente(cliente);
		return ResponseEntity.ok(Map.of("mensaje", "Cliente editado con éxito"));
	}
	
	//Endpoint para buscar un cliente por su id
	@GetMapping("/buscar/{idCliente}")
	public ResponseEntity<?> BuscarPorID(@PathVariable Integer idCliente){
		Cliente cliente = service.buscarClienteId(idCliente);
		return ResponseEntity.ok(cliente);
	}
	
	//Endpoint para eliminar un cliente
	@DeleteMapping("/eliminar/{idCliente}")
	public ResponseEntity<Map<String, String>> EliminarCliente(@PathVariable Integer idCliente){
		service.eliminarCliente(idCliente);
		return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado con éxito"));
	}
	
}
