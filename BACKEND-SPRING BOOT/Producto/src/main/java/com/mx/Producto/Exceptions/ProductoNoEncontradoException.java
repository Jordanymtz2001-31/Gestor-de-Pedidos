package com.mx.Producto.Exceptions;

//Creamo una exception para cuando no se encuentre un producto
public class ProductoNoEncontradoException extends RuntimeException{
	
	public ProductoNoEncontradoException(Integer id) {
        super("Cliente con ID " + id + " no encontrado");
    }

}
