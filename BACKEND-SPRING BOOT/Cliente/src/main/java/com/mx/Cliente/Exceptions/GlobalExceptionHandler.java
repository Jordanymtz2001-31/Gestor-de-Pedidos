package com.mx.Cliente.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice // Indica que esta clase maneja excepciones a nivel global
public class GlobalExceptionHandler {
	
	// Captura la Excepcion de que no se encontro el cliente
	@ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> clienteNoEncontrado(ClienteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("CLI-404", ex.getMessage()));
    }
	
	// Captura la Excepcion de datos duplicados
	@ExceptionHandler(ClienteDuplicadoException.class)
    public ResponseEntity<ErrorResponse> clienteDuplicado(ClienteDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT) // 409
            .body(new ErrorResponse("CLI-409", ex.getMessage()));
    }
	
	// Captura la Excepcion de que ocurrio un error en este servidor 
	@ExceptionHandler(ClienteServiceException.class)
    public ResponseEntity<ErrorResponse> clienteServiceError(ClienteServiceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
            .body(new ErrorResponse("CLI-500", ex.getMessage()));
    }
	
	// Captura cualquier otra excepción
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) { 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
            .body(new ErrorResponse("CLI-500", "Error interno del servicio"));
    }

}
