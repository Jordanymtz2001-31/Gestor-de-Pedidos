# 🚀 RECOMENDACIONES AVANZADAS - Excepciones (Futuro)

## 1. Crear Clases DTO con Validaciones

Actualmente tus endpoints aceptan la entidad `Pedido` directamente. Es mejor crear un DTO:

```java
// Crear: Dtos/PedidoRequestDto.java
package com.mx.Pedido.Dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PedidoRequestDto {
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "El total es obligatorio")
    @Min(value = 0, message = "El total debe ser mayor a 0")
    private BigDecimal total;
    
    @NotNull(message = "El estado es obligatorio")
    private String estatus;
    
    @NotNull(message = "El ID del cliente es obligatorio")
    @Min(value = 1, message = "El ID del cliente debe ser mayor a 0")
    private Integer clienteId;
}
```

Luego en el controller:

```java
@PostMapping("/guardar")
public ResponseEntity<Map<String, String>> GuardarPedido(@Valid @RequestBody PedidoRequestDto pedidoDto){
    // El @Valid dispara MethodArgumentNotValidException si hay errores
    // El GlobalExceptionHandler la captura automáticamente
    service.guardarPedido(pedidoDto);
    return ResponseEntity.ok(Map.of("mensaje", "Pedido guardado con exito"));
}
```

---

## 2. Excepciones Personalizadas Adicionales

Crear más excepciones específicas para diferentes casos:

```java
// PedidoExceptions/InvalidClienteException.java
public class InvalidClienteException extends RuntimeException {
    public InvalidClienteException(Integer clienteId) {
        super("Cliente con ID " + clienteId + " es inválido o no existe");
    }
}

// PedidoExceptions/PedidoDataIntegrityException.java
public class PedidoDataIntegrityException extends RuntimeException {
    public PedidoDataIntegrityException(String mensaje) {
        super("Error de integridad de datos: " + mensaje);
    }
}

// PedidoExceptions/InvalidPedidoDataException.java
public class InvalidPedidoDataException extends RuntimeException {
    public InvalidPedidoDataException(String campo, String motivo) {
        super("Campo '" + campo + "' inválido: " + motivo);
    }
}
```

Y los manejadores correspondientes en GlobalExceptionHandler:

```java
@ExceptionHandler(InvalidClienteException.class)
public ResponseEntity<ErrorResponse> handleInvalidCliente(InvalidClienteException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("PED-400", ex.getMessage()));
}

@ExceptionHandler(PedidoDataIntegrityException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrity(PedidoDataIntegrityException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)  // 409
        .body(new ErrorResponse("PED-409", ex.getMessage()));
}
```

---

## 3. Agregar Logging a las Excepciones

Importar SLF4J:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> clienteNoEncontrado(PedidoNoEncontradoException ex) {
        logger.warn("Pedido no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("PED-404", ex.getMessage()));
    }
    
    @ExceptionHandler(PedidoServiceException.class)
    public ResponseEntity<ErrorResponse> clienteServiceError(PedidoServiceException ex) {
        logger.error("Error en servicio de pedido: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("PED-500", "Error interno del servicio"));
    }
}
```

---

## 4. Manejo de Excepciones de BD Específicas

```java
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    // Capturar violaciones de constraints (unique, foreign key, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        logger.error("Violación de integridad de datos: {}", ex.getMessage());
        String mensaje = "Los datos proporcionados violan restricciones de la BD";
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("PED-409", mensaje));
    }
    
    // Capturar cuando no hay resultados
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleEmptyResult(EmptyResultDataAccessException ex) {
        logger.warn("No se encontraron resultados: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("PED-404", "Recurso no encontrado"));
    }
}
```

---

## 5. Mejorar ErrorResponse con más información

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private String codigo;
    private String mensaje;
    private String detalle;  // Detalles adicionales (campo que falló, etc.)
    private Integer statusCode;  // Código HTTP
    private LocalDateTime timestamp;
    
    // Constructor simple (para compatibilidad)
    public ErrorResponse(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }
}
```

---

## 6. Crear Interceptor para logging de todas las excepciones

```java
// Crear: PedidoExceptions/LoggingInterceptor.java
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                 Object handler, Exception ex) throws Exception {
        if (ex != null) {
            logger.error("Error en {} {}: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                ex.getMessage(), 
                ex);
        }
    }
}
```

---

## 7. Documentar en Swagger/OpenAPI

Si usas Swagger, documenta tus excepciones:

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
    
    @GetMapping("/buscar/{idPedido}")
    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado - código: PED-404"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor - código: PED-500")
    })
    public ResponseEntity<?> BuscarPorID(@PathVariable Integer idPedido) {
        return ResponseEntity.ok(service.buscarID(idPedido));
    }
}
```

---

## 8. Tests para validar el manejo de excepciones

```java
// Tests/PedidoControllerTest.java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PedidoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testBuscarPedidoNoEncontrado() throws Exception {
        mockMvc.perform(get("/pedido/buscar/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.codigo").value("PED-404"))
            .andExpect(jsonPath("$.mensaje").stringContains("no encontrado"));
    }
    
    @Test
    public void testListarPedidosConClienteInvalido() throws Exception {
        mockMvc.perform(get("/pedido/listarXCliente/-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("PED-400"));
    }
}
```

---

## 9. Estructura Final Recomendada

```
PedidoExceptions/
├── ErrorResponse.java                    // Respuesta de error
├── GlobalExceptionHandler.java           // Manejador global
├── LoggingInterceptor.java              // Interceptor de logging
├── Excepciones personalizadas/
│   ├── PedidoNoEncontradoException.java
│   ├── PedidoServiceException.java
│   ├── InvalidClienteException.java
│   ├── PedidoDataIntegrityException.java
│   └── InvalidPedidoDataException.java
└── Config/
    └── ExceptionHandlerConfig.java      // Configuración adicional

Dtos/
├── PedidoRequestDto.java               // DTO con validaciones
├── PedidoResponseDto.java              // DTO de respuesta
└── ErrorResponseDto.java
```

---

## 10. Checklist de Buenas Prácticas

- [ ] Todas las excepciones extienden de `RuntimeException`
- [ ] GlobalExceptionHandler maneja todas las excepciones
- [ ] Los códigos de error son consistentes (PED-XXX)
- [ ] Los DTOs tienen validaciones `@Valid`
- [ ] El logging registra todos los errores importantes
- [ ] Las excepciones son específicas (no solo `Exception`)
- [ ] Las respuestas de error tienen formato JSON consistente
- [ ] Los códigos HTTP son apropiados (404, 400, 500, etc.)
- [ ] Los tests validan el manejo de excepciones
- [ ] La documentación está actualizada

---

