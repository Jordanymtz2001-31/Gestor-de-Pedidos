package com.mx.Pedido.ProductoFeingCliente;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mx.Pedido.Dtos.ProductoDto;

//@Anotacion que indica que esta interfaz es un cliente Feign para comunicarse con otro servicio
//Una de las dos formas en que el DetallePpedido puede comunicarse con el servicio 
@FeignClient(name = "Producto", url = "http://localhost:8003/producto")
public interface ProductoFeingCliente {
	
	//Colocamos los metodos que necesitamos para comunicarnos con el servicio de Empleado
	@GetMapping("/buscar/{idProducto}")
	public ProductoDto ListarProductosXDetalleId(@PathVariable int idProducto);

}
