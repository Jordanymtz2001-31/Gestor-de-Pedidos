package com.mx.Producto.Servive;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.Producto.Entity.Producto;
import com.mx.Producto.Exceptions.ProductoDuplicadoException;
import com.mx.Producto.Exceptions.ProductoNoEncontradoException;
import com.mx.Producto.Exceptions.ProductoServiceException;
import com.mx.Producto.Repository.ProductoRepository;


//IMPORTANTE: LAS EXCEPCIONES SE MANEJAN EN SERVICE SIEMPRE, Y DESDE AHI SE PROPAGAN AL CONTROLADOR PARA SER MANEJADAS POR EL GLOBAL EXCEPTION HANDLER


@Service //Indicamos que esta clase sera un servicio
public class ProductoService {
	
	@Autowired //Inyectamos las dependencias
	private ProductoRepository repoProducto;
	
	//Metodo para listar todos los productos
	public List<Producto> listarProductos(){
		try {
			return repoProducto.findAll(); //Retornamos la lista de productos
		} catch (Exception e) {
			throw new ProductoServiceException("Error el lista productod: " + e.getMessage() ); //En caso de error, lanzamos la excepcion
		}
	}
	
	
	
	//Metodo para guardar producto
	public void guardarProducto(Producto producto) {
		try {
			//Validar datos duplicados
			if(existePorNombre(producto.getNombre())) {
				throw new ProductoDuplicadoException(" Producto ", producto.getNombre() ); //Lanzamos la excepcion personalizada
			}
			
			repoProducto.save(producto); //Guardamos el producto en dado caso no existan datos duplicados
			
		}catch (ProductoDuplicadoException e) { //Capturamos la excepcion personalizada de datos duplicados
			throw e; //Re-lanzar la excepción de datos duplicados
			
		} catch (Exception e) {
			throw new ProductoServiceException("Error al guardar el producto: " + e.getMessage() ); //En caso de otro error, lanzamos una excepcion generica de servicio
		}
	}
	
	
	
	//Metodos para editar producto
	public void editarProducto(Producto producto) {
		try {
			//Validar si existe el producto
			Producto existe = repoProducto.findById(producto.getIdProducto()).orElseThrow(() -> new ProductoNoEncontradoException(producto.getIdProducto())); //Lanzamos la excepcion personalizada
				
			if(existePorNombre(producto.getNombre()) && !existe.getNombre().equals(producto.getNombre())) {
				throw new ProductoDuplicadoException("El nombre del producto ya existe: ", producto.getNombre() ); //Lanzamos la excepcion personalizada
			}
			
			repoProducto.save(producto); //Guardamos el producto en dado caso no existan datos duplicados
			
		}catch (ProductoNoEncontradoException | ProductoDuplicadoException e) { //Capturamos la excepcion personalizada de datos duplicados
			throw e; //Re-lanzar la excepción de datos duplicados
			
		} catch (Exception e) {
			throw new ProductoServiceException("Error al editar el producto: " + e.getMessage() ); //En caso de otro error, lanzamos una excepcion generica de servicio
		}
	}
	//Metodos para eliminar producto
	public void eliminarProducto(Integer idProducto) {
		try {
			//Validar si existe el producto
			Producto existe = repoProducto.findById(idProducto).orElseThrow(() -> new ProductoNoEncontradoException(idProducto)); //Lanzamos la excepcion personalizada
			
			repoProducto.delete(existe); //Eliminamos el producto
			
		}catch (ProductoNoEncontradoException e) { //Capturamos la excepcion personalizada de datos duplicados
			throw e; //Re-lanzar la excepción de datos duplicados
			
		} catch (Exception e) {
			throw new ProductoServiceException("Error al eliminar el producto: " + e.getMessage() ); //En caso de otro error, lanzamos una excepcion generica de servicio
		}
	}
	
	//Metodo para buscar producto por id
	public Producto buscarID(Integer idProducto) {
		try {
			return repoProducto.findById(idProducto).orElseThrow(() -> new ProductoNoEncontradoException(idProducto)); //Retornamos el producto o lanzamos la excepcion personalizada
		} catch (ProductoNoEncontradoException e) {
			throw e; //Re-lanzar la excepción personalizada
		} catch (Exception e) {
			throw new ProductoServiceException("Error al buscar el producto por ID: " + e.getMessage() ); //En caso de otro error, lanzamos una excepcion generica de servicio
		}
	}
	
	//Metodo para validar que no se repita el nombre del producto
	public boolean existePorNombre(String nombre) {
		try {
			return repoProducto.existsByNombre(nombre);
		} catch (Exception e) {
			throw new ProductoServiceException("Error al verificar nombre de producto: " + e.getMessage() ); //En caso de error, lanzamos la excepcion
		}
		
	}
	

}
