package com.mx.Producto.Controller;


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

import com.mx.Producto.Entity.Producto;
import com.mx.Producto.Servive.ProductoService;

@RequestMapping("/producto")
@RestController // Indicamos que esta clase sera un controlador REST y indica que puede manejar solicitudes HTTP y enviar respuestas en formato JSON o XML.
public class ProductoController {
	
	@Autowired // Inyectamos las dependencias
	private ProductoService serviceProducto;
	
	//Metodo para listar todos los productos
	@GetMapping("/listar")
	public ResponseEntity<?> listarProductos() {
		if(serviceProducto.listarProductos().isEmpty()) { //Validamos si esta vacia
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.ok(serviceProducto.listarProductos());
		}
	}
	
	//Metodo para guardar producto
	@PostMapping("/guardar")
	public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
		serviceProducto.guardarProducto(producto);
		return ResponseEntity.status(201).body(Map.of("Mensaje","Producto guardado correctamente"));
			
	}
	
	//Metodo para editar un producto
	@PutMapping("/editar")
	public ResponseEntity<Map<String, String>> editarCliente(@RequestBody Producto producto) {
		serviceProducto.editarProducto(producto);
		return ResponseEntity.status(201).body(Map.of("Mensaje","Producto editado correctamente"));
				
			
	}
	
	//Metodo para buscar un producto
	@GetMapping("/buscar/{idProducto}")
	public ResponseEntity<?> buscarProducto(@PathVariable Integer idProducto){
		Producto producto = serviceProducto.buscarID(idProducto);
		return ResponseEntity.ok(producto);
		
	}
	
	//Metodo para eliminar producto
	@DeleteMapping("/eliminar/{idProducto}")
	public ResponseEntity<?> eliminarProducto(@PathVariable Integer idProducto){
		serviceProducto.eliminarProducto(idProducto);
		return ResponseEntity.ok(Map.of("Mensaje", "Producto Eliminado Correctamente"));
		
	}
}
