package com.mx.Pedido.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.Pedido.Dtos.ProductoDto;
import com.mx.Pedido.Dtos.GuardarDetalleEditDto;
import com.mx.Pedido.Entity.Detalle_Pedido;
import com.mx.Pedido.Entity.Pedido;
import com.mx.Pedido.PedidoExceptions.DetalleNoEncontradoException;
import com.mx.Pedido.PedidoExceptions.DetalleServiceException;
import com.mx.Pedido.PedidoExceptions.ProductoServiceException;
import com.mx.Pedido.PedidoExceptions.PedidoNoEncontradoException;
import com.mx.Pedido.ProductoFeingCliente.ProductoFeingCliente;
import com.mx.Pedido.Repository.DetallePedidoRespository;
import com.mx.Pedido.Repository.PedidoRepository;

@Service
public class DetallePedidoService {
	
	@Autowired // Inyeccion de dependencias de DetallePedidoRespository
	private DetallePedidoRespository repodetalle;
	
	@Autowired // Inyeccion de dependencias de PedidoRepository
	private PedidoRepository repoPedido;
	
	@Autowired // Inyeccion de dependencias de Feing Client
	private ProductoFeingCliente productoFC;
	
	//Metodo para listar todos los detalles del pedido
	public List<Detalle_Pedido> listarDetallePedidos(){
		try {
			return repodetalle.findAll();
		} catch (Exception e) {
			throw new DetalleServiceException("Error al listar los detalles de pedidos: " + e.getMessage());
		}
	}
	
	//Metodo para guardar un detalle de pedido
	public void guardarDetallePedido(GuardarDetalleEditDto detalleDto) {
		try {
			// 1. Validar que el pedido existe
			Pedido pedido = repoPedido.findById(detalleDto.getIdPedido())
				.orElseThrow(() -> new PedidoNoEncontradoException(detalleDto.getIdPedido()));
			
			// 2. Validar que el producto existe mediante Feign Client
			ProductoDto productos = obtenerProductoId(detalleDto.getProductoId());
			if(productos == null) {
				throw new DetalleServiceException("El producto con ID " + detalleDto.getProductoId() + " no existe");
			}
			
			// 3. Mapear DTO a Entity
			Detalle_Pedido detallePedido = new Detalle_Pedido();
			detallePedido.setCantidad(detalleDto.getCantidad());
			detallePedido.setPrecioUnitario(detalleDto.getPrecioUnitario());
			detallePedido.setProductoId(detalleDto.getProductoId());
			detallePedido.setIdPedido(pedido);; //asignamos el Pedido
			
			// 4. Guardar el detalle
			repodetalle.save(detallePedido);
		} catch (PedidoNoEncontradoException |DetalleServiceException | ProductoServiceException  e) {
			throw e;
		}catch (Exception e) {
			throw new DetalleServiceException("Error al guardar el detalle del pedido: " + e.getMessage());
		}
	}
	
	//Metodo para editar un detalle de pedido
	public void editarDetallePedido(GuardarDetalleEditDto detalleDto) {
		try {
			// 1. Validar que el detalle de pedido existe
			Detalle_Pedido detallePedidoExistente = repodetalle.findById(detalleDto.getIdDetallePedido())
				.orElseThrow(() -> new DetalleNoEncontradoException(detalleDto.getIdDetallePedido()));
			
			// 2. Validar que el pedido existe
			Pedido pedido = repoPedido.findById(detalleDto.getIdPedido())
				.orElseThrow(() -> new PedidoNoEncontradoException(detalleDto.getIdPedido()));
			
			// 3. Validar que el producto existe mediante Feign Client
			ProductoDto producto = obtenerProductoId(detalleDto.getProductoId());
			if(producto == null) {
				throw new DetalleServiceException("El producto con ID " + detalleDto.getProductoId() + " no existe");
			}
			
			// 4. Actualizar los datos
			detallePedidoExistente.setCantidad(detalleDto.getCantidad());
			detallePedidoExistente.setPrecioUnitario(detalleDto.getPrecioUnitario());
			detallePedidoExistente.setProductoId(detalleDto.getProductoId());
			detallePedidoExistente.setIdPedido(pedido);
			
			// 5. Guardar los cambios
			repodetalle.save(detallePedidoExistente);
		} catch (DetalleNoEncontradoException | PedidoNoEncontradoException | DetalleServiceException | ProductoServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new DetalleServiceException("Error al editar el detalle del pedido: " + e.getMessage());
		}
	}
	
	//Meotodo para eliminar un detalle de pedido
	public void eliminarDetallePedido(Integer idDetallePedido) {
		try {
			//Validar que el detalle de pedido existe
			if(!repodetalle.existsById(idDetallePedido)) { // Si detalle de pedido no existe
				throw new DetalleNoEncontradoException(idDetallePedido);  //Entonces lanzamos la excepcion Personalizada
			}
			repodetalle.deleteById(idDetallePedido); // Eliminamos el detalle del pedido
		} catch (DetalleNoEncontradoException e) {
			throw e;
		} catch (Exception e) {
			throw new DetalleServiceException("Error al eliminar el detalle del pedido: " + e.getMessage());
		}
	}
	
	//Metodo para obtener un detalle de pedido por su id
	public Detalle_Pedido buscarID(Integer idDetallePedido) {
		try {
			return repodetalle.findById(idDetallePedido) //Buscamos el detalle del pedido por su id
					.orElseThrow(() -> new DetalleNoEncontradoException(idDetallePedido)); // Si no existe, lanzamos la excepcion personalizada
		} catch (DetalleNoEncontradoException e) {
			throw e;
		} catch (Exception e) {
			throw new DetalleServiceException("Error al buscar el detalle del pedido: " + e.getMessage());
		}
	}
	
	// Configuracion para el consumo de otro microservicio 
	//listar los productos de un detalle de pedido
	public ProductoDto obtenerProductoId(int idProducto){
		try {
			return productoFC.ListarProductosXDetalleId(idProducto);
		} catch (Exception e) {
			throw new ProductoServiceException("No se pudo obtener el producto con ID " + idProducto + ": " + e.getMessage(), e);
		}
	}

}
