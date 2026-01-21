package com.mx.Cliente.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.Cliente.Entity.Cliente;
import com.mx.Cliente.Exceptions.ClienteNoEncontradoException;
import com.mx.Cliente.Exceptions.ClienteDuplicadoException;
import com.mx.Cliente.Exceptions.ClienteServiceException;
import com.mx.Cliente.Repository.ClienteRepository;

//IMPORTANTE: LAS EXCEPCIONES SE MANEJAN EN SERVICE SIEMPRE, Y DESDE AHI SE PROPAGAN AL CONTROLADOR PARA SER MANEJADAS POR EL GLOBAL EXCEPTION HANDLER

@Service //Indicamos que esta clase es un servicio
public class ClienteService {
	
	@Autowired // Hacemos la inyeccion de Repositorio
	private ClienteRepository repoCliente;
	
	
	//Metodo para guardar un cliente
	public void guardarCliente(Cliente cliente) {
		try {
			// Validar datos duplicados
			if(existeCliente(cliente.getNombre())) {
				throw new ClienteDuplicadoException("nombre", cliente.getNombre()); // Lanzamos la excepcion personalizada
			}
			if(existeEmail(cliente.getEmail())) {
				throw new ClienteDuplicadoException("email", cliente.getEmail()); // Lanzamos la excepcion personalizada
			}
			if(existeTelefono(cliente.getTelefono())) {
				throw new ClienteDuplicadoException("teléfono", cliente.getTelefono()); // Lanzamos la excepcion personalizada
			}
			repoCliente.save(cliente); // Guardamos el cliente en dado caso no existan datos duplicados
			
		} catch (ClienteDuplicadoException e) { // Capturamos la excepcion personalizada de datos duplicados
			throw e; // Re-lanzar la excepción de datos duplicados
			
		} catch (Exception e) {
			throw new ClienteServiceException("Error al guardar el cliente: " + e.getMessage()); // Lanzamos una excepcion generica de servicio en caso de otro error
		}
	}
	
	
	//Metodo para listar todos los clientes
	public List<Cliente> listarClientes(){
		try {
			return repoCliente.findAll(); // Retornamos la lista de clientes
		} catch (Exception e) {
			throw new ClienteServiceException("Error al listar los clientes: " + e.getMessage()); // Lanzamos una excepcion generica de servicio en caso de error
		}
	}
	
	
	//Metodo para editar un cliente
	public void editarCliente(Cliente cliente) {
		try {
			// Validar que el cliente existe
			Cliente clienteExistente = repoCliente.findById(cliente.getIdCliente()) // Para editar necesitamos si Id para verificar si existe,
				.orElseThrow(() -> new ClienteNoEncontradoException(cliente.getIdCliente())); // Si no existe, lanzamos la excepcion personalizada
			
			// Validar datos duplicados (permitiendo los mismos valores del cliente actual)
			if(existeCliente(cliente.getNombre()) && !clienteExistente.getNombre().equalsIgnoreCase(cliente.getNombre())) {
				throw new ClienteDuplicadoException("nombre", cliente.getNombre());
			}
			if(existeEmail(cliente.getEmail()) && !clienteExistente.getEmail().equalsIgnoreCase(cliente.getEmail())) {
				throw new ClienteDuplicadoException("email", cliente.getEmail());
			}
			if(existeTelefono(cliente.getTelefono()) && !clienteExistente.getTelefono().equalsIgnoreCase(cliente.getTelefono())) {
				throw new ClienteDuplicadoException("teléfono", cliente.getTelefono());
			}
			
			repoCliente.save(cliente);
		} catch (ClienteNoEncontradoException | ClienteDuplicadoException e) { // Capturamos las excepciones personalizadas
			throw e; // Re-lanzar excepciones personalizadas
		} catch (Exception e) {
			throw new ClienteServiceException("Error al editar el cliente: " + e.getMessage()); // Lanzamos una excepcion generica de servicio en caso de otro error
		}
	}
	
	
	//Metodo para eliminar un cliente
	public void eliminarCliente(int idCliente) {
		try {
			// Validar que el cliente existe
			if(!repoCliente.existsById(idCliente)) { // Si cliente no existe
				throw new ClienteNoEncontradoException(idCliente); //Entonces lanzamos la excepcion Personalizada
			}
			repoCliente.deleteById(idCliente); // Y si existe, procedemos a eliminarlo
		} catch (ClienteNoEncontradoException e) { // Capturamos la excepcion personalizada
			throw e; // Re-lanzar la excepción de cliente no encontrado
			
		} catch (Exception e) {
			throw new ClienteServiceException("Error al eliminar el cliente: " + e.getMessage()); // Lanzamos una excepcion generica de servicio en caso de otro error
		}
	}
	
	
	//Metodo para buscar cliente por id
	public Cliente buscarClienteId(Integer idCliente) {
		try {
			return repoCliente.findById(idCliente) // Buscamos el ID del cliente 
				.orElseThrow(() -> new ClienteNoEncontradoException(idCliente)); //Sino se encuentra entonces lanzamos la excepcion personalizada
		} catch (ClienteNoEncontradoException e) { // Capturamos la excepcion personalizada
			throw e; // Re-lanzar la excepción de cliente no encontrado
		} catch (Exception e) {
			throw new ClienteServiceException("Error al buscar el cliente: " + e.getMessage()); // Lanzamos una excepcion generica de servicio en caso de otro error
		}
	}
	
	
	
	//Metodo para validar si existe el nombre del cliente
	public boolean existeCliente(String nombre) {
		try {
			return repoCliente.existsByNombreIgnoreCase(nombre);
		} catch (Exception e) {
			throw new ClienteServiceException("Error al validar nombre: " + e.getMessage()); // Lanzamos una excepcion generica de servicio en caso de otro error
		}
	}
	
	//Metodo para validar si existe el email del cliente
	public boolean existeEmail(String email) {
		try {
			return repoCliente.existsByEmailIgnoreCase(email);
		} catch (Exception e) {
			throw new ClienteServiceException("Error al validar email: " + e.getMessage()); // Lanzamos una excepcion generica de servicio en caso de otro error
		}
	}
	
	//Metodo para validar si existe el telefono del cliente
	public boolean existeTelefono(String telefono) {
		try {
			return repoCliente.existsByTelefonoIgnoreCase(telefono);
		} catch (Exception e) {
			throw new ClienteServiceException("Error al validar teléfono: " + e.getMessage()); // Lanzamos una excepcion generica de servicio en caso de otro error
		}
	}

}
