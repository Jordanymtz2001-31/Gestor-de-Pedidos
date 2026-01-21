package com.mx.Pedido.PedidoExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice // Indica que esta clase maneja excepciones a nivel global
public class GlobalExceptionHandler {
	
	// Captura la Excepcion de que no se encontro el cliente
		@ExceptionHandler(PedidoNoEncontradoException.class)
	    public ResponseEntity<ErrorResponse> clienteNoEncontrado(PedidoNoEncontradoException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(new ErrorResponse("PED-404", ex.getMessage()));
	    }
		
		
		// Captura la Excepcion de que ocurrio un error en este servidor 
		@ExceptionHandler(PedidoServiceException.class)
	    public ResponseEntity<ErrorResponse> clienteServiceError(PedidoServiceException ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
	            .body(new ErrorResponse("PED-500", ex.getMessage()));
	    }
		
		// Captura excepciones de validación en @RequestBody
		@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
			String mensaje = "Error de validación: ";
			if (ex.getBindingResult().hasErrors()) {
				mensaje += ex.getBindingResult().getFieldError().getDefaultMessage();
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse("PED-400", mensaje));
		}
		
		// Captura excepciones cuando el servicio de Producto no responde
		@ExceptionHandler(ProductoServiceException.class)
		public ResponseEntity<ErrorResponse> productoServiceError(ProductoServiceException ex) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE) // 503
				.body(new ErrorResponse("PED-503", "Servicio de Producto no disponible: " + ex.getMessage()));
		}
		
		// Captura cualquier otra excepción
	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) { 
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
	            .body(new ErrorResponse("PED-500", "Error interno del servicio"));
	    }
//----------------------------------------------------------------------------------------------------------------------------------------------------
	    
	    // Captura la Excepcion de que no se encontro eL detalle de pedido
	    @ExceptionHandler(DetalleNoEncontradoException.class)
	    public ResponseEntity<ErrorResponse> pediodNoEncontrado(DetalleNoEncontradoException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(new ErrorResponse("DET-404", ex.getMessage()));
	    }
	    
	    // Captura la Excepcion de que ocurrio un error en este servidor 
	 	@ExceptionHandler(DetalleServiceException.class)
	    public ResponseEntity<ErrorResponse> detalleServiceError(DetalleServiceException ex) {
	 		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
	 			.body(new ErrorResponse("DET-500", ex.getMessage()));
	 	    }

}
