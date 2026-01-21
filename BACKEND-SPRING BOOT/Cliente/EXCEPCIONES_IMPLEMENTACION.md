# Implementación de Excepciones en Microservicio Cliente

## 📋 Resumen de Cambios

Se ha implementado un sistema completo de manejo de excepciones personalizado para el microservicio de Cliente siguiendo las mejores prácticas de Spring Boot.

---

## 🎯 Componentes Implementados

### 1. **Excepciones Personalizadas**

#### `ClienteNoEncontradoException.java`
- **Lanzada cuando:** Un cliente solicitado no existe en la base de datos
- **HTTP Status:** 404 Not Found
- **Código de error:** CLI-404
- **Ejemplo:** `new ClienteNoEncontradoException(idCliente)`

#### `ClienteDuplicadoException.java`
- **Lanzada cuando:** Se intenta crear/editar un cliente con nombre, email o teléfono duplicados
- **HTTP Status:** 409 Conflict
- **Código de error:** CLI-409
- **Ejemplo:** `new ClienteDuplicadoException("nombre", "Juan Pérez")`

#### `ClienteServiceException.java`
- **Lanzada cuando:** Ocurre un error genérico en el servicio
- **HTTP Status:** 500 Internal Server Error
- **Código de error:** CLI-500
- **Ejemplo:** `new ClienteServiceException("Error al guardar el cliente")`

---

## 🏗️ Arquitectura de Manejo de Excepciones

```
Cliente (Entity)
    ↓
ClienteController (REST API)
    ↓
ClienteService (Lógica de negocio + Excepciones)
    ↓
ClienteRepository (Base de datos)
    ↓
GlobalExceptionHandler (Captura y responde excepciones)
    ↓
ErrorResponse (Respuesta JSON estructurada)
```

---

## 📂 Archivos Modificados

### 1. **ClienteService.java** ✅
**Responsabilidades:**
- Validar datos duplicados → Lanza `ClienteDuplicadoException`
- Validar existencia de cliente → Lanza `ClienteNoEncontradoException`
- Manejar errores de BD → Lanza `ClienteServiceException`
- Envuelve todas las operaciones en try-catch

**Métodos principales:**
```java
public void guardarCliente(Cliente cliente)      // Valida duplicados
public void editarCliente(Cliente cliente)       // Valida existencia y duplicados
public void eliminarCliente(int idCliente)       // Valida existencia
public Cliente buscarClienteId(Integer idCliente) // Lanza excepción si no existe
```

### 2. **ClienteController.java** ✅
**Cambios:**
- Removidas todas las validaciones manuales
- Código limpio y conciso (SOLID principle)
- Las excepciones se propagan automáticamente
- GlobalExceptionHandler las captura y responde

**Antes (Con validaciones):**
```java
@PostMapping("/guardar")
public ResponseEntity<Map<String, String>> GuardarCliente(@RequestBody Cliente cliente){
    try {
        boolean existeNombre = service.existeCliente(cliente.getNombre());
        if(existeNombre) {
            return ResponseEntity.status(409).body(Map.of("error", "..."));
        }
        // más validaciones...
        service.guardarCliente(cliente);
        return ResponseEntity.ok(...);
    } catch (Exception e) {
        return ResponseEntity.status(500).body(...);
    }
}
```

**Después (Limpio):**
```java
@PostMapping("/guardar")
public ResponseEntity<Map<String, String>> GuardarCliente(@RequestBody Cliente cliente){
    service.guardarCliente(cliente);
    return ResponseEntity.ok(Map.of("mensaje", "Cliente guardado con éxito"));
}
```

### 3. **GlobalExceptionHandler.java** ✅
**Responsabilidades:**
- Captura `@ExceptionHandler` específicas para cada excepción
- Retorna respuestas JSON estructuradas (ErrorResponse)
- Maneja excepciones genéricas como fallback

**Métodos:**
```java
@ExceptionHandler(ClienteNoEncontradoException.class)  → HTTP 404
@ExceptionHandler(ClienteDuplicadoException.class)     → HTTP 409
@ExceptionHandler(ClienteServiceException.class)       → HTTP 500
@ExceptionHandler(Exception.class)                     → HTTP 500 (catch-all)
```

### 4. **ErrorResponse.java** (DTO)
**Estructura:**
```json
{
    "codigo": "CLI-404",
    "mensaje": "Cliente con ID 123 no encontrado",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

## 🔄 Flujo de Uso - Ejemplo: Guardar Cliente

### Escenario 1: Éxito
```
POST /cliente/guardar
{
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "telefono": "5551234567"
}

↓ ClienteService.guardarCliente()
  - Valida no exista nombre   ✓
  - Valida no exista email    ✓
  - Valida no exista teléfono ✓
  - Guarda en BD              ✓

↓ Retorna:
HTTP 200 OK
{
    "mensaje": "Cliente guardado con éxito"
}
```

### Escenario 2: Cliente con Email Duplicado
```
POST /cliente/guardar
{
    "nombre": "Carlos",
    "email": "juan@example.com",  // ← Ya existe!
    "telefono": "5559876543"
}

↓ ClienteService.guardarCliente()
  - Valida no exista nombre   ✓
  - Valida no exista email    ✗ LANZA ClienteDuplicadoException

↓ GlobalExceptionHandler.clienteDuplicado()
  - Captura la excepción
  - Crea ErrorResponse
  - Retorna HTTP 409

↓ Retorna:
HTTP 409 CONFLICT
{
    "codigo": "CLI-409",
    "mensaje": "El email: 'juan@example.com' ya existe en la base de datos",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

### Escenario 3: Cliente No Existe al Buscar
```
GET /cliente/buscar/999

↓ ClienteService.buscarClienteId(999)
  - Busca en BD
  - No encuentra   ✗ LANZA ClienteNoEncontradoException

↓ GlobalExceptionHandler.clienteNoEncontrado()
  - Captura la excepción
  - Crea ErrorResponse
  - Retorna HTTP 404

↓ Retorna:
HTTP 404 NOT FOUND
{
    "codigo": "CLI-404",
    "mensaje": "Cliente con ID 999 no encontrado",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

## ✨ Ventajas de esta Implementación

| Ventaja | Descripción |
|---------|------------|
| **Separación de Responsabilidades** | Controller no valida, Service maneja lógica |
| **Código Limpio** | Menos if-else, más legible |
| **Mantenibilidad** | Cambios centralizados en Service y Handler |
| **Consistencia** | Todas las excepciones retornan mismo formato |
| **Escalabilidad** | Fácil agregar nuevas excepciones |
| **Debugging** | Códigos de error y timestamps para logs |
| **DRY (Don't Repeat Yourself)** | Sin duplicación de validaciones |

---

## 🚀 Endpoints Implementados

```
GET    /cliente/listar              → Lista todos los clientes
POST   /cliente/guardar             → Crea nuevo cliente
PUT    /cliente/editar              → Edita cliente existente
GET    /cliente/buscar/{idCliente}  → Busca cliente por ID
DELETE /cliente/eliminar/{idCliente} → Elimina cliente
```

---

## 📝 Códigos de Error HTTP

| Código | Significado | Excepción |
|--------|------------|-----------|
| 200 | OK - Operación exitosa | N/A |
| 204 | No Content - Sin registros | N/A |
| 404 | Not Found - Cliente no existe | ClienteNoEncontradoException |
| 409 | Conflict - Datos duplicados | ClienteDuplicadoException |
| 500 | Internal Server Error | ClienteServiceException / Exception |

---

## 🔐 Buenas Prácticas Aplicadas

✅ **Excepciones personalizadas** por tipo de error  
✅ **GlobalExceptionHandler** centralizado  
✅ **Validaciones en Service**, no en Controller  
✅ **Try-catch en Service** para capturar errores de BD  
✅ **Re-lanzar excepciones** personalizadas (no las genéricas)  
✅ **ErrorResponse** estructura JSON consistente  
✅ **HTTP Status codes** semánticamente correctos  
✅ **Logging ready** (fácil agregar logs)  
✅ **Annotations** (`@ControllerAdvice`, `@ExceptionHandler`)  
✅ **SOLID Principles** (Single Responsibility)  

---

## 🔧 Cómo Extender

### Agregar una nueva excepción:

1. **Crear clase exception:**
```java
public class MiNuevaException extends RuntimeException {
    public MiNuevaException(String mensaje) {
        super(mensaje);
    }
}
```

2. **Agregar handler en GlobalExceptionHandler:**
```java
@ExceptionHandler(MiNuevaException.class)
public ResponseEntity<ErrorResponse> miNuevaExcepcion(MiNuevaException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("CLI-400", ex.getMessage()));
}
```

3. **Lanzar en Service:**
```java
throw new MiNuevaException("Descripción del error");
```

---

**Creado:** 18/01/2026  
**Versión:** 1.0  
**Estado:** ✅ Implementado y Probado
