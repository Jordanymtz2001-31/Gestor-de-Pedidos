package com.mx.Pedido.Services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.mx.Pedido.Dtos.ClienteDto;
import com.mx.Pedido.Dtos.DetalleConProductoDto;
import com.mx.Pedido.Dtos.DetallePedidoGuardarDto;
import com.mx.Pedido.Dtos.PedidoCompletoDto;
import com.mx.Pedido.Dtos.PedidoGuardarDto;
import com.mx.Pedido.Dtos.ProductoDto;
import com.mx.Pedido.Entity.Detalle_Pedido;
import com.mx.Pedido.Entity.EEstado;
import com.mx.Pedido.Entity.Pedido;
import com.mx.Pedido.PedidoExceptions.PedidoNoEncontradoException;
import com.mx.Pedido.PedidoExceptions.PedidoServiceException;
import com.mx.Pedido.ProductoFeingCliente.ProductoFeingCliente;
import com.mx.Pedido.Repository.DetallePedidoRespository;
import com.mx.Pedido.Repository.PedidoRepository;

@Service // Indicamos que esta clase es un servidor
public class PedidoService {
	
	@Autowired //Inyectamos las dependencias de RestTemplate
    private RestTemplate restTemplate;
	
	@Autowired // Inyeccion de dependencias de PedidoRepository
	private PedidoRepository repoPedido;
	
	@Autowired
	private ProductoFeingCliente productoFeing;
	
	@Autowired
	private DetallePedidoRespository repoDetalle;

	
	//Metodo para listar todos los pedidos
	public List<Pedido> listarPedidos(){
		try {
			return repoPedido.findAll();
		} catch (Exception e) {
			throw new PedidoServiceException("Error al listar los pedidos: " + e.getMessage());
		}
	}
	
	//Metodo para guardar un pedido Completo con sus detalles
	@Transactional
	public PedidoCompletoDto  guardarPedido(PedidoGuardarDto pedidoDto) {
		try {
			// Validar que el cliente existe
			ClienteDto cliente = obtenerClienteId(pedidoDto.getClienteId());
			if(cliente == null) {
				throw new PedidoServiceException("El cliente con ID " + pedidoDto.getClienteId() + " no existe");
			}
			
			BigDecimal total = BigDecimal.ZERO;
			List<Detalle_Pedido> detallesEntidades = new ArrayList<>(); //Creamos una lista de detalles para ir almacenandolos
			
			//Crear Pedido pero solo los datoa que no tienen por default
			Pedido pedido = new Pedido();
			pedido.setClienteId(pedidoDto.getClienteId()); //Le pasamos el ClienteId que se almaceno en PedidoGuardarDto
			// de momento el total esta en 0, calculamos despues
			repoPedido.save(pedido); // LO guardamos ya colo los vamores predefinidos
			
			
			//Creamos detalle del pedido
			for (DetallePedidoGuardarDto d : pedidoDto.getDetalles()) {
				// Traemos el ID del producto
				ProductoDto producto = productoFeing.ListarProductosXDetalleId(d.getProductoId()); //Traemos el prducto 
				
				BigDecimal precioInitario = d.getPrecioUnitario() != null ? d.getPrecioUnitario() : producto.getPrecio(); //si el frontend mandó precioUnitario en el detalle, uso ese; si no lo mandó, uso el precio actual del producto
				
				BigDecimal subtotal = precioInitario.multiply(BigDecimal.valueOf(d.getCantidad())); //Subtotal = precioUnitario * cantidad
				
				total = total.add(subtotal);
				
				Detalle_Pedido det = new Detalle_Pedido(); 
				det.setIdPedido(pedido); //Agreamos el pedido a la entidad de Detalle_Pedido
				det.setProductoId(d.getProductoId()); // ProductoId FK
				det.setCantidad(d.getCantidad());
				det.setPrecioUnitario(precioInitario);
				
				det = repoDetalle.save(det); //Guardamos el detalle
				detallesEntidades.add(det); //Lo agregamos a la lista de detalles
				
			}
			
			//Actualizamos ahora si el total de pedido
			pedido.setTotal(total);
			repoPedido.save(pedido);
			
			//Mapeamos el PedidoGuardarDto para respuestas
			return mapToPedidoCompletoDto(pedido, detallesEntidades);
			
		} catch (PedidoServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new PedidoServiceException("Error al guardar el pedido: " + e.getMessage());
		}
	}
	//Creamo una clase para 
	private PedidoCompletoDto mapToPedidoCompletoDto(Pedido pedido, List<Detalle_Pedido> detallesEntidades) {
		try {
				
			PedidoCompletoDto dto = new PedidoCompletoDto();
			dto.setPedido(pedido);
			
			List<DetalleConProductoDto> detalles = new ArrayList<>();
			
			for (Detalle_Pedido det : detallesEntidades) {
				DetalleConProductoDto dcp = new DetalleConProductoDto();
				dcp.setDetallePedido(det);
				
				//Cargar producto para armar dto de respuesta
				ProductoDto prod = productoFeing.ListarProductosXDetalleId(det.getProductoId());
				dcp.setProducto(prod);
				
				detalles.add(dcp);
			}
			
			dto.setDetalles(detalles);
			return dto;
		}catch (PedidoServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new PedidoServiceException("Error al guardar el pedido: " + e.getMessage());
		}
	}
	
		
	
	
	//Metodo para editar un pedido
	public void editarPedido(Pedido pedido) {
		try {
			// Validar que el cliente existe
			ClienteDto cliente = obtenerClienteId(pedido.getClienteId());
			
			//Validar que el pedido existe
			if(!repoPedido.existsById(pedido.getIdPedido())) {
				throw new PedidoNoEncontradoException(pedido.getIdPedido());
				
			}else if(cliente == null) { // Si el cliente no existe
				throw new PedidoServiceException("El cliente con ID " + pedido.getClienteId() + " no existe");
			}
			
			repoPedido.save(pedido);
		} catch (PedidoServiceException | PedidoNoEncontradoException e) {
			throw e;
		} catch (Exception e) {
			throw new PedidoServiceException("Error al editar el pedido: " + e.getMessage());
		}
	}
	
	//Meotodo para eliminar un pedido
	public void eliminarPedido(Integer idPedido) {
		try {
			//Validar que el pedido existe
			if(!repoPedido.existsById(idPedido)) { // Si pedido no existe
				throw new PedidoNoEncontradoException(idPedido);  //Entonces lanzamos la excepcion Personalizada
			}
			
			repoPedido.deleteById(idPedido); // Y si existe, procedemos a eliminarlo
		} catch (PedidoNoEncontradoException e) {
			throw e; // Re-lanzar la excepción de pedido no encontrado
			
		} catch (Exception e) {
			throw new PedidoServiceException("Error al eliminar el pedido: " + e.getMessage());
		}
	}
	
	//Metodo para obtener un pedido por su id
	public Pedido buscarID(Integer idPedido) {
		try {
			return repoPedido.findById(idPedido) // Buscamos el pedido por su id
					.orElseThrow(() -> new PedidoNoEncontradoException(idPedido)); // Si no existe, lanzamos la excepcion personalizada
		} catch (PedidoNoEncontradoException e) { // Capturamos la excepcion personalizada
			throw e; // Re-lanzar excepcion personalizada
		} catch (Exception e) {
			throw new PedidoServiceException("Error al buscar el pedido: " + e.getMessage());
		}
	}
	
	//Metodo para listar pedidos por Cliente
	public List<Pedido> listarPedidoPorCliente(int clienteId){
		try {
			// Validar que clienteId es válido
			if(clienteId <= 0) {
				throw new PedidoServiceException("ID del cliente debe ser mayor a 0");
			}
			return repoPedido.findByClienteId(clienteId);
			
		} catch (PedidoServiceException e) {
			throw e; // Re-lanzar las excepciones de servicio personalizadas
		} catch (Exception e) {
			throw new PedidoServiceException("Error al listar los pedidos por cliente: " + e.getMessage());
		}
	}
	
	//METODOS PERSONALIZADOS-------------------------------------------------------------------------------------------------------------
	
	//Metodo para obtener los detalles de un pedido
	public Pedido obtenerPedidoConDetalles(Integer idPedido) {
		try {
			Pedido pedido = repoPedido.findByIdConDetalles(idPedido);
			// Si el pedido no existe, lanzamos la excepcion personalizada
			if(pedido == null) {
				throw new PedidoNoEncontradoException(idPedido);
			}
			return pedido;
		} catch (PedidoNoEncontradoException e) {
			throw e; // Re-lanzar la excepción de pedido no encontrado
		} catch (Exception e) {
			throw new PedidoServiceException("Error al obtener los detalles del pedido: " + e.getMessage());
		}
		 
	}
	
	//Metodo para obtener el Id del cliente metiantes restTemplate
	public ClienteDto obtenerClienteId(int clienteId) {
		return restTemplate.getForObject("http://localhost:8002/cliente/buscar/{idCliente}", ClienteDto.class, clienteId);
	}
	
	//Metodo para obstener Estatus Cancelados de pedidos
	public List<Pedido> pedidosCancelados(){
		try {
			return repoPedido.findByEstatus(EEstado.CANCELADO);
		}catch (Exception e) {
			throw new PedidoServiceException("Error al listar los pedidos Cancelados" + e.getMessage());
		}
	}
	

}
