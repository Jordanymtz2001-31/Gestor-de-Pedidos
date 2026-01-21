# 📋 ANÁLISIS DE IMPLEMENTACIÓN DE EXCEPCIONES - Microservicio Pedido

## ✅ LO QUE ESTÁ BIEN

1. **Clases de Excepciones Personalizadas Bien Definidas**
   - ✓ `PedidoNoEncontradoException` - Heredan de `RuntimeException` correctamente
   - ✓ `PedidoServiceException` - Mensajes descriptivos con contexto
   - ✓ Ambas extienden de `RuntimeException` (ideal para excepciones no verificadas)

2. **GlobalExceptionHandler Implementado Correctamente**
   - ✓ Anotación `@ControllerAdvice` bien aplicada
   - ✓ Métodos con `@ExceptionHandler` capturan cada tipo de excepción
   - ✓ Códigos HTTP apropiados (404 para no encontrado, 500 para error del servicio)
   - ✓ Clase `ErrorResponse` bien estructurada con timestamp

3. **Uso en el Service**
   - ✓ El service lanza excepciones personalizadas correctamente
   - ✓ Try-catch bien implementado en `buscarID()` y `eliminarPedido()`
   - ✓ Re-lanzamiento correcto de excepciones con `throw e`

---

## ❌ PROBLEMAS ENCONTRADOS Y POR QUÉ

### **1. CRÍTICO: ErrorResponse - Falta Constructor sin argumentos (Deserialización)**

**🔴 Problema:**
```java
@Data
public class ErrorResponse {	
	private String codigo;
	private String mensaje;
	private String timestamp = LocalDateTime.now().toString();
	
	public ErrorResponse(String codigo, String mensaje) {
      this.codigo = codigo;
      this.mensaje = mensaje;
  }
}
```

**¿Por qué es un problema?**
- Cuando Jackson intenta serializar a JSON, podría haber problemas
- Falta un **constructor sin argumentos** que Lombok necesita para la deserialización
- Si el cliente intenta deserializar esta respuesta, fallará

**Solución:**
Agregar `@NoArgsConstructor` de Lombok o un constructor vacío manualmente

---

### **2. CRÍTICO: Validación incompleta en Controller - No se capturan excepciones**

**🔴 Problema:**
```java
@PostMapping("/guardar")
public ResponseEntity<Map<String, String>> GuardarPedido(@RequestBody Pedido pedido){
    service.guardarPedido(pedido);  // ❌ Si lanza excepción, el GlobalExceptionHandler la captura
    return ResponseEntity.ok(Map.of("mensaje", "Pedido guardado con exito"));
}

@PutMapping("/editar")
public ResponseEntity<Map<String, String>> EditarPedido(@RequestBody Pedido pedido){
    service.editarPedido(pedido);   // ❌ Igual aquí
    return ResponseEntity.ok(Map.of("mensaje", "Pedido editado con exito")); 
}
```

**¿Por qué es un problema?**
- El GlobalExceptionHandler SÍ captura las excepciones del service
- **PERO** el error podría causar que el cliente reciba la excepción genérica si hay un error inesperado
- No hay validación de null en `@RequestBody` (podría recibir un pedido null)
- No hay validación de datos del pedido antes de guardar

**Solución:**
- Agregar validación de datos con `@Valid` y `@NotNull`
- Validar que el pedido tenga datos obligatorios (cliente ID, etc.)

---

### **3. IMPORTANTE: GlobalExceptionHandler tiene códigos de error inconsistentes**

**🔴 Problema:**
```java
@ExceptionHandler(PedidoNoEncontradoException.class)
public ResponseEntity<ErrorResponse> clienteNoEncontrado(PedidoNoEncontradoException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("CLI-404", ex.getMessage())); // ❌ Código "CLI-" (Cliente?)
}

@ExceptionHandler(PedidoServiceException.class)
public ResponseEntity<ErrorResponse> clienteServiceError(PedidoServiceException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("CLI-500", ex.getMessage())); // ❌ Código "CLI-" (debería ser "PED-")
}
```

**¿Por qué es un problema?**
- Los códigos comienzan con "CLI-" que sugiere "Cliente"
- Esto es confuso porque es el microservicio de **Pedido**
- Debería ser "PED-404", "PED-500", etc. para mantener consistencia

**Solución:**
Cambiar los códigos de error a "PED-404" y "PED-500"

---

### **4. IMPORTANTE: Falta manejo de validación de datos**

**🔴 Problema:**
```java
@PostMapping("/guardar")
public ResponseEntity<Map<String, String>> GuardarPedido(@RequestBody Pedido pedido){
    // ❌ ¿Qué pasa si pedido es null?
    // ❌ ¿Qué pasa si clienteId es 0 o negativo?
    // ❌ ¿Qué pasa si total es negativo?
    service.guardarPedido(pedido);
    return ResponseEntity.ok(Map.of("mensaje", "Pedido guardado con exito"));
}
```

**¿Por qué es un problema?**
- No hay validación de integridad de datos
- Podría guardarse un pedido inválido en la BD
- El servicio debería validar datos antes de guardar

**Solución:**
- Usar `@Valid` en `@RequestBody`
- Crear una clase DTO con validaciones (`@NotNull`, `@Min`, `@Max`, etc.)
- Crear un manejador para `MethodArgumentNotValidException` en el GlobalExceptionHandler

---

### **5. MODERADO: Endpoint `/listarXCliente` tiene un problema**

**🔴 Problema:**
```java
@GetMapping("/listarXCliente")
public ResponseEntity<?> ListaPedidoPorCliente(@PathVariable int clienteId){
    // ❌ Usa @PathVariable pero la ruta no tiene {clienteId}
    if(service.listarPedidoPorCliente(clienteId).isEmpty()) {
        return ResponseEntity.noContent().build();
    }else {
        return ResponseEntity.ok(service.listarPedidoPorCliente(clienteId)); 
    }
}
```

**¿Por qué es un problema?**
- `@PathVariable` espera una variable en la URL como `/listarXCliente/{clienteId}`
- Pero la ruta es solo `/listarXCliente`
- Debería recibir un `@RequestParam` o la ruta debe tener `{clienteId}`

**Solución:**
Cambiar la ruta a `/listarXCliente/{clienteId}`

---

### **6. MODERADO: Error de tipografía en Entity.java**

**🔴 Problema:**
```java
@Column(name = "FECHA")
private LocalDate  feha;  // ❌ "feha" debería ser "fecha" (typo)
```

**¿Por qué es un problema?**
- Es confuso leer el código
- Puede causar errores si otros desarrolladores esperan `fecha`

**Solución:**
Renombrar a `fecha`

---

### **7. MODERADO: Excepción no verificada puede causar problemas en transacciones**

**🔴 Problema:**
```java
public void guardarPedido(Pedido pedido) {
    try {
        repoPedido.save(pedido);
    } catch (Exception e) {
        throw new PedidoServiceException("Error al guardar el pedido: " + e.getMessage());
    }
}
```

**¿Por qué es un problema?**
- Si hay un error de validación de BD (constraint unique, foreign key, etc.)
- Se captura como `PedidoServiceException` pero el cliente no sabe si fue error de validación o error real del servidor
- Debería distinguir entre tipos de errores

**Solución:**
- Crear excepciones más específicas: `PedidoDataIntegrityException`, `InvalidClienteException`, etc.
- Capturar `DataIntegrityViolationException` de Spring Data específicamente

---

### **8. MODERADO: Método `obtenerPedidoConDetalles` no lanza PedidoNoEncontradoException**

**🔴 Problema:**
```java
@GetMapping("/detalle/{idPedido}")
public ResponseEntity<Pedido> ObtenerDetallesDePedido(@PathVariable Integer idPedido){
    Pedido pedidoConDetalles = service.obtenerPedidoConDetalles(idPedido);
    if(pedidoConDetalles == null) {  // ❌ Validación manual en controller
        return ResponseEntity.notFound().build();
    }else {
        return ResponseEntity.ok(pedidoConDetalles);
    }
}
```

```java
public Pedido obtenerPedidoConDetalles(Integer idPedido) {
    try {
        return repoPedido.findByIdConDetalles(idPedido);
        // ❌ No lanza excepción, solo retorna null
    } catch (Exception e) {
        throw new PedidoServiceException("Error al obtener los detalles del pedido: " + e.getMessage());
    }
}
```

**¿Por qué es un problema?**
- Inconsistencia con `buscarID()` que SÍ lanza `PedidoNoEncontradoException`
- Validación en controller viola el principio de que el service maneja excepciones
- El GlobalExceptionHandler no se utiliza

**Solución:**
Que `obtenerPedidoConDetalles` lance `PedidoNoEncontradoException` si es null, similar a `buscarID()`

---

### **9. MENOR: Métodos que no validan valores nulos correctamente**

**🔴 Problema:**
```java
public List<Pedido> listarPedidoPorCliente(int clienteId){
    try {
        return repoPedido.findByClienteId(clienteId);
        // ❌ ¿Qué pasa si clienteId es 0 o negativo?
    } catch (Exception e) {
        throw new PedidoServiceException("Error al listar los pedidos por cliente: " + e.getMessage());
    }
}
```

**¿Por qué es un problema?**
- No valida que `clienteId` sea válido
- Podría devolver resultados inesperados si clienteId es inválido

**Solución:**
Agregar validación: `if (clienteId <= 0) throw new InvalidClienteException(clienteId);`

---

## 📊 RESUMEN DE SEVERIDAD

| Severidad | Cantidad | Problemas |
|-----------|----------|-----------|
| 🔴 CRÍTICO | 2 | ErrorResponse sin constructor, validación incompleta en controller |
| 🟠 IMPORTANTE | 3 | Códigos de error inconsistentes, falta validación datos, listarXCliente URL incorrecta |
| 🟡 MODERADO | 3 | Typo en entity, excepciones no específicas, inconsistencia en obtenerPedidoConDetalles |
| 🟢 MENOR | 1 | Validación de valores nulos |

---

## 🔧 RECOMENDACIONES FINALES

### **Prioridad Alta (Hacer AHORA):**
1. Agregar `@NoArgsConstructor` a `ErrorResponse`
2. Cambiar códigos de error "CLI-" a "PED-"
3. Agregar validación con `@Valid` en endpoints de guardar/editar
4. Corregir URL de `/listarXCliente` a `/listarXCliente/{clienteId}`
5. Hacer que `obtenerPedidoConDetalles` lance `PedidoNoEncontradoException`

### **Prioridad Media (Hacer PRONTO):**
6. Crear excepciones más específicas para errores de datos
7. Agregar validación de datos en el service
8. Crear un manejador para `MethodArgumentNotValidException`

### **Prioridad Baja (Hacer LUEGO):**
9. Corregir typo "feha" → "fecha"
10. Agregar validación de valores en `listarPedidoPorCliente`

---

