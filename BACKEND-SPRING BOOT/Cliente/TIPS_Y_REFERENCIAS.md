# 💡 Tips y Referencias - Sistema de Excepciones

## 🎯 Puntos Clave a Recordar

### 1. **Dónde Lanzar Excepciones**
✅ **Correcto:** En el `Service`
```java
// Service.java
if(existeCliente(nombre)) {
    throw new ClienteDuplicadoException("nombre", nombre);
}
```

❌ **Incorrecto:** En el `Controller`
```java
// Controller.java - NO HAGAS ESTO
if(service.existeCliente(nombre)) {
    return ResponseEntity.status(409)...
}
```

### 2. **Flujo de Excepciones**
```
Service lanza excepción
        ↓
GlobalExceptionHandler captura
        ↓
Crea ErrorResponse
        ↓
Retorna HTTP con status
```

### 3. **Controller Limpio**
```java
@PostMapping("/guardar")
public ResponseEntity<Map<String, String>> guardar(@RequestBody Cliente cliente) {
    // Una sola línea de lógica
    service.guardarCliente(cliente);
    // Una sola línea de respuesta
    return ResponseEntity.ok(Map.of("mensaje", "éxito"));
}
```

El Controller **NO debe validar**, eso lo hace el Service.

---

## 📚 Analogía: Hotel

Imagina un hotel:

```
🎯 ANTES (Con validaciones en Controller)
├─ Recepción valida:
│  ├─ "¿Tiene reserva?"
│  ├─ "¿Tienen habitación disponible?"
│  ├─ "¿Fue pagado?"
│  └─ Etc...
│
└─ Problema: Recepción muy ocupada

✅ DESPUÉS (Con excepciones en Service)
├─ Recepción solo registra check-in
│  └─ "OK, bienvenido"
│
├─ Gerencia valida:
│  ├─ "¿Tiene reserva?" 
│  ├─ "¿Disponibilidad?"
│  ├─ "¿Fue pagado?"
│  └─ Si hay problema → lanza excepción
│
└─ Handler global resuelve la excepción
   └─ "Lo siento, no hay habitaciones"
```

**El Controller es la recepción (solo recibe y responde)**  
**El Service es la gerencia (valida la lógica)**

---

## 🔧 Cómo Extender el Sistema

### Agregar una Nueva Excepción

**Paso 1:** Crear la excepción
```java
// archivo: MiNuevaException.java
public class MiNuevaException extends RuntimeException {
    public MiNuevaException(String mensaje) {
        super(mensaje);
    }
}
```

**Paso 2:** Agregar handler
```java
// GlobalExceptionHandler.java
@ExceptionHandler(MiNuevaException.class)
public ResponseEntity<ErrorResponse> miNuevaExcepcion(MiNuevaException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("CLI-400", ex.getMessage()));
}
```

**Paso 3:** Lanzar en Service
```java
// ClienteService.java
public void miMetodo() {
    if(condicionErronea) {
        throw new MiNuevaException("Descripción del error");
    }
}
```

---

## 🧪 Testing - Ejemplos Rápidos

### Test 1: Verificar Excepción en Service
```java
@Test
public void testGuardarClienteEmailDuplicado() {
    Cliente cliente1 = new Cliente("Juan", "juan@test.com", "555-1234");
    Cliente cliente2 = new Cliente("Carlos", "juan@test.com", "555-9999");
    
    service.guardarCliente(cliente1); // OK
    
    assertThrows(ClienteDuplicadoException.class, 
        () -> service.guardarCliente(cliente2));
}
```

### Test 2: Verificar Respuesta HTTP
```java
@Test
public void testGuardarClienteDuplicadoEndpoint() {
    // Guardar primero
    mvc.perform(post("/cliente/guardar")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{...}"))
        .andExpect(status().isOk());
    
    // Intentar guardar con email duplicado
    mvc.perform(post("/cliente/guardar")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{...}"))
        .andExpect(status().isConflict())  // 409
        .andExpect(jsonPath("$.codigo").value("CLI-409"));
}
```

---

## 📊 Tabla de HTTP Status Codes

| Status | Nombre | Uso | Excepción |
|--------|--------|-----|-----------|
| **200** | OK | Éxito general | N/A |
| **201** | Created | Recurso creado | N/A |
| **204** | No Content | Sin contenido | N/A |
| **400** | Bad Request | Dato inválido | CustomException |
| **401** | Unauthorized | No autenticado | N/A |
| **403** | Forbidden | No autorizado | N/A |
| **404** | Not Found | Recurso no existe | ClienteNoEncontradoException |
| **409** | Conflict | Datos duplicados | ClienteDuplicadoException |
| **500** | Internal Server Error | Error del servidor | ClienteServiceException |
| **502** | Bad Gateway | Servicio externo caído | N/A |
| **503** | Service Unavailable | Servicio no disponible | N/A |

---

## 💻 Comandos Útiles

### Maven
```bash
# Compilar
mvn clean compile

# Empaquetar
mvn clean package

# Ejecutar
mvn spring-boot:run

# Tests
mvn test
```

### Git (para commits descriptivos)
```bash
git add ClienteService.java
git commit -m "feat: agregar excepciones personalizadas al servicio"

git add ClienteController.java
git commit -m "refactor: simplificar controller delegando validaciones"

git add EXCEPCIONES_IMPLEMENTACION.md
git commit -m "docs: agregar documentación de excepciones"
```

---

## 🔗 Referencias de Spring Boot

### Anotaciones Usadas
```java
@ControllerAdvice           // Maneja excepciones globales
@ExceptionHandler(...)      // Define qué excepción maneja
@RestController             // API REST, retorna JSON
@Service                    // Capa de lógica de negocio
@Autowired                  // Inyección de dependencias
@PostMapping                // HTTP POST
@GetMapping                 // HTTP GET
@PutMapping                 // HTTP PUT
@DeleteMapping              // HTTP DELETE
@PathVariable               // Parámetro en la URL
@RequestBody                // Body del request
```

### Interfaces/Clases Clave
```java
ResponseEntity<T>           // Respuesta HTTP completa
HttpStatus                  // Códigos HTTP (200, 404, 500, etc)
RuntimeException            // Base para excepciones
Map.of()                    // Crear Map inmutable
```

---

## ❌ Errores Comunes y Soluciones

### Error 1: Excepción no se captura
```javascript
❌ INCORRECTO:
@ExceptionHandler(ClienteDuplicadoException.class)
public ResponseEntity<...> handle(Exception ex) { // Parámetro genérico
    // ...
}

✅ CORRECTO:
@ExceptionHandler(ClienteDuplicadoException.class)
public ResponseEntity<...> handle(ClienteDuplicadoException ex) {
    // ...
}
```

### Error 2: Status code incorrecto
```java
❌ INCORRECTO:
return ResponseEntity.status(409).body(error);  // Número mágico

✅ CORRECTO:
return ResponseEntity.status(HttpStatus.CONFLICT).body(error);  // Claro
```

### Error 3: Validar en Controller
```java
❌ INCORRECTO:
@PostMapping("/guardar")
public ResponseEntity<...> guardar(Cliente cliente) {
    if(service.existeCliente(cliente.getNombre())) {
        return ResponseEntity.status(409)...;
    }
    service.guardarCliente(cliente);
    return ResponseEntity.ok(...);
}

✅ CORRECTO:
@PostMapping("/guardar")
public ResponseEntity<...> guardar(Cliente cliente) {
    service.guardarCliente(cliente);  // Delega todo al Service
    return ResponseEntity.ok(...);
}

// El Service lanza excepciones si hay validación
```

### Error 4: Excepción sin mensaje
```java
❌ INCORRECTO:
throw new ClienteDuplicadoException();

✅ CORRECTO:
throw new ClienteDuplicadoException("nombre", cliente.getNombre());
```

---

## 🎓 Conceptos de Ingeniería

### Separación de Responsabilidades
```
Controller: ¿Qué HTTP recibí? → Delego al Service
Service:   Valido datos → Lanzo excepción si hay error
Handler:   Excepciones → Formatea respuesta HTTP
```

### DRY (Don't Repeat Yourself)
```
ANTES: Validaciones en 5 endpoints
DESPUÉS: 1 Service, 1 Handler reutilizado en 5 endpoints
```

### SOLID - Single Responsibility
```
1 Clase = 1 Responsabilidad

Service → Lógica de negocio
Controller → Mapear HTTP
Handler → Procesar excepciones
ErrorResponse → Formatear respuesta
```

---

## 📖 Lectura Recomendada

1. **Spring Boot Documentation:**
   https://spring.io/projects/spring-boot

2. **Exception Handling:**
   https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc

3. **REST Best Practices:**
   https://restfulapi.net/http-status-codes/

4. **Clean Code by Robert C. Martin:**
   - Clean Architecture
   - Single Responsibility Principle

---

## 🚀 Optimizaciones Futuras

1. **Validación con @Valid:**
```java
@PostMapping("/guardar")
public ResponseEntity<?> guardar(@Valid @RequestBody Cliente cliente) {
    // @Valid valida automáticamente campos con @NotNull, @Size, etc
}
```

2. **Logging:**
```java
private static final Logger logger = LoggerFactory.getLogger(...);

@ExceptionHandler(ClienteDuplicadoException.class)
public ResponseEntity<?> handle(ClienteDuplicadoException ex) {
    logger.warn("Duplicado detectado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(...);
}
```

3. **Internacionalización (i18n):**
```java
// Mensajes en múltiples idiomas
messages_es.properties: "El email ya existe"
messages_en.properties: "Email already exists"
```

4. **Actuator (Métricas):**
```java
// Agregar a pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

// Acceder a métricas
GET /actuator/metrics
```

---

## 🎯 Resumen Rápido

| Aspecto | Qué Hacer |
|--------|-----------|
| **Excepciones** | Lanzar en Service |
| **Validaciones** | Hacer en Service |
| **Controller** | Solo recibir y responder |
| **Handler** | Capturar excepciones |
| **ErrorResponse** | Formato JSON consistente |
| **HTTP Status** | Codes semánticamente correctos |

---

**Última actualización:** 18/01/2026  
**Versión:** 1.0
