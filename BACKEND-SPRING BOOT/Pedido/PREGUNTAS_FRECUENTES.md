# ❓ PREGUNTAS FRECUENTES (FAQ)

## 🆘 Problemas y Soluciones

### P1: ¿Por qué no levantaba la aplicación?

**R:** Porque en `Detalle_Pedido.java` faltaba la anotación `@ManyToOne`. 

Cuando `Pedido.java` decía:
```java
@OneToMany(mappedBy = "idPedido")
```

JPA buscaba una propiedad llamada `idPedido` con `@ManyToOne` en `Detalle_Pedido`, pero no la encontraba.

**Solución:** Agregar:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "PEDIDO_ID", nullable = false)
private Pedido idPedido;
```

---

### P2: ¿Qué es `mappedBy`?

**R:** Es un atributo de `@OneToMany` que le dice a JPA:

"La relación está mapeada por la propiedad `idPedido` en la entidad `Detalle_Pedido`"

En otras palabras, le dice dónde está la propiedad que maneja la relación en el otro lado.

```
Pedido (lado UNO)
@OneToMany(mappedBy = "idPedido")  ← Busca aquí
         ↓
Detalle_Pedido (lado MUCHOS)
@ManyToOne
private Pedido idPedido;  ← La encuentra aquí
```

---

### P3: ¿Dónde va `@JoinColumn`?

**R:** Siempre en el lado **MUCHOS** (el que tiene la FK).

```java
// ✓ CORRECTO: En Detalle_Pedido (lado muchos)
@ManyToOne
@JoinColumn(name = "PEDIDO_ID")  // ← Aquí
private Pedido idPedido;

// ❌ INCORRECTO: En Pedido (lado uno)
@OneToMany
@JoinColumn(name = "PEDIDO_ID")  // ← NO aquí
private List<Detalle_Pedido> detalles;
```

---

### P4: ¿Qué es `nullable = false`?

**R:** Significa que la FK (PEDIDO_ID) no puede ser NULL en la BD.

```java
@JoinColumn(name = "PEDIDO_ID", nullable = false)
```

En la BD se vería:
```sql
ALTER TABLE DETALLE_PEDIDO 
ADD CONSTRAINT fk_pedido_id 
FOREIGN KEY (PEDIDO_ID) REFERENCES PEDIDO(ID_PEDIDO) 
ON DELETE CASCADE;
```

---

### P5: ¿Qué es `FetchType.LAZY`?

**R:** Le dice a JPA que NO cargue automáticamente los detalles cuando traes un pedido.

```java
@ManyToOne(fetch = FetchType.LAZY)
```

**Ventajas:**
- ✓ Mejor rendimiento
- ✓ Menos datos transferidos
- ✓ Menor carga en BD

**Desventajas:**
- ✗ Si accedes a `pedido.getDetalles()` después, hace otra query

```java
// LAZY: 2 queries
Pedido p = repo.findById(1);          // Query 1: SELECT Pedido
List<Detalle> d = p.getDetalles();    // Query 2: SELECT Detalle

// EAGER: 1 query
Pedido p = repo.findByIdWithDetails(1);  // Query 1: JOIN SELECT
```

---

### P6: ¿Qué es `CascadeType.ALL`?

**R:** Significa que las operaciones en el pedido se "propagan" a los detalles.

```java
@OneToMany(mappedBy = "idPedido", cascade = CascadeType.ALL)
```

**Qué significa:**
- Si guardes un pedido → se guardan sus detalles
- Si eliminas un pedido → se eliminan sus detalles
- Si actualizas un pedido → se actualizan sus detalles

```java
// Guarda pedido Y detalles automáticamente
pedido.getDetalles().add(detalle1);
repo.save(pedido);  // Guarda ambos

// Elimina pedido Y detalles automáticamente
repo.deleteById(1);  // Elimina pedido e hijos
```

---

### P7: ¿Por qué cambié "CLI-404" a "PED-404"?

**R:** Para mantener consistencia y claridad.

- Este es el **microservicio de Pedido**, no de Cliente
- Los códigos deben reflejar el servicio real
- Es más fácil debuggear viendo "PED-" cuando hay un problema

```
microservicio Cliente:  CLI-404, CLI-500
microservicio Pedido:   PED-404, PED-500
microservicio Producto: PRD-404, PRD-500
```

---

### P8: ¿Qué significa `@ControllerAdvice`?

**R:** Indica que esta clase maneja excepciones a nivel **global** para toda la aplicación.

Sin `@ControllerAdvice`:
```java
@PostMapping("/guardar")
public ResponseEntity<?> guardar(Pedido p) {
    try {
        service.guardar(p);
    } catch (PedidoServiceException e) {  // ❌ Repetir en cada endpoint
        return ResponseEntity.status(500).body(new ErrorResponse("...", "..."));
    }
}
```

Con `@ControllerAdvice`:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PedidoServiceException.class)
    public ResponseEntity<ErrorResponse> handle(PedidoServiceException e) {
        return ResponseEntity.status(500).body(new ErrorResponse("...", "..."));
    }
}

// Ahora es automático en TODOS los endpoints
@PostMapping("/guardar")
public ResponseEntity<?> guardar(Pedido p) {
    service.guardar(p);  // ✓ Si lanza excepción, el handler la captura
}
```

---

### P9: ¿Qué es `@Valid`?

**R:** Le dice a Spring que valide los datos del `@RequestBody` usando anotaciones.

```java
@PostMapping("/guardar")
public ResponseEntity<?> guardar(@Valid @RequestBody PedidoDto pedido) {
    // Si pedido tiene errores de validación, 
    // Spring lanza MethodArgumentNotValidException
    service.guardar(pedido);
}

// DTO con validaciones:
@Data
public class PedidoDto {
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @Min(value = 1, message = "El total debe ser > 0")
    private BigDecimal total;
}
```

Si los datos no son válidos, el `GlobalExceptionHandler` captura la excepción y retorna:
```json
{
    "codigo": "PED-400",
    "mensaje": "Error de validación: La fecha es obligatoria",
    "timestamp": "2026-01-19T..."
}
```

---

### P10: ¿Por qué agregué `@NoArgsConstructor`?

**R:** Porque Jackson (la librería que serializa a JSON) lo necesita.

Cuando Spring intenta convertir JSON a Java:

```json
{
    "codigo": "PED-404",
    "mensaje": "No encontrado",
    "timestamp": "..."
}
```

Jackson necesita un constructor sin argumentos para crear la instancia:

```java
// Sin @NoArgsConstructor:
ErrorResponse er = new ErrorResponse();  // ❌ Error, no existe

// Con @NoArgsConstructor:
ErrorResponse er = new ErrorResponse();  // ✓ Funciona
er.setCodigo("PED-404");
er.setMensaje("No encontrado");
```

---

### P11: ¿Cuál es la diferencia entre `RuntimeException` y `Exception`?

**R:**

| Tipo | Checked | Debo capturar | Caso de uso |
|------|---------|---------------|-----------|
| `Exception` | Sí | Obligatorio | Errores recuperables |
| `RuntimeException` | No | Opcional | Errores de lógica |

Para excepciones personalizadas en Spring Boot, se recomienda extender de `RuntimeException`:

```java
// ✓ RECOMENDADO en Spring Boot
public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(Integer id) {
        super("Pedido " + id + " no encontrado");
    }
}

// ❌ NO RECOMENDADO (obliga try-catch en muchos lados)
public class PedidoNoEncontradoException extends Exception {
    // ...
}
```

---

### P12: ¿Debo cambiar la BD manualmente después de estos cambios?

**R:** Depende:

**Si usas Hibernate/JPA con `spring.jpa.hibernate.ddl-auto`:**

```properties
# application.properties
spring.jpa.hibernate.ddl-auto=update  # Auto-actualiza la BD

# Opciones:
# validate    - Solo valida, no cambia nada
# update      - Agrega columnas/tablas nuevas (RECOMENDADO)
# create      - Elimina y crea TODO (⚠️ Pierde datos)
# create-drop - Crea al iniciar, elimina al parar
# none        - No hace nada
```

Con `update`, JPA automáticamente:
- ✓ Crea la FK si no existe
- ✓ Agrega constraints
- ✓ No pierde datos

**Si manejas la BD manualmente:**

Ejecuta:
```sql
ALTER TABLE DETALLE_PEDIDO 
ADD CONSTRAINT fk_pedido_id 
FOREIGN KEY (PEDIDO_ID) REFERENCES PEDIDO(ID_PEDIDO) 
ON DELETE CASCADE;
```

---

### P13: ¿Por qué sacé la validación del Controller?

**R:** Porque es mejor que esté en el Service.

**Arquitectura correcta:**

```
Controller (HTTP layer)
  ├─ Parsea JSON → Objeto Java
  ├─ Valida con @Valid (sintaxis básica)
  └─ Delega al Service

Service (Business logic)
  ├─ Valida reglas de negocio
  ├─ Valida datos contra BD
  └─ Lanza excepciones específicas

GlobalExceptionHandler (Error layer)
  ├─ Captura excepciones
  ├─ Crea respuesta JSON
  └─ Retorna HTTP status
```

**Ejemplo:**

```java
// ✓ CORRECTO: Controller simple
@PostMapping("/guardar")
public ResponseEntity<?> guardar(@Valid @RequestBody PedidoDto dto) {
    service.guardar(dto);  // Delega todo al service
}

// ✓ CORRECTO: Service valida todo
public void guardar(PedidoDto dto) {
    if (dto.getTotal().signum() <= 0) {  // Validación de negocio
        throw new PedidoServiceException("Total debe ser > 0");
    }
    if (!clienteExiste(dto.getClienteId())) {  // Validación contra BD
        throw new InvalidClienteException(dto.getClienteId());
    }
    repo.save(dto);
}
```

---

### P14: ¿Cómo probar si las excepciones funcionan?

**R:** Usando curl o Postman:

```bash
# Pedido no encontrado (404)
curl http://localhost:8003/buscar/999

# Cliente inválido (400)
curl http://localhost:8003/listarXCliente/-1

# Datos vacíos (400 - validación)
curl -X POST http://localhost:8003/guardar \
  -H "Content-Type: application/json" \
  -d '{}'

# Éxito (200)
curl http://localhost:8003/buscar/1
```

Deberías recibir respuestas como:
```json
{
    "codigo": "PED-404",
    "mensaje": "Pedido con ID 999 no encontrado",
    "timestamp": "2026-01-19T10:25:00"
}
```

---

### P15: ¿Qué debería hacer ahora?

**R:** Prioridades:

1. **Ahora (0 minutos):** Levanta la app y verifica que funciona
   ```bash
   mvn spring-boot:run
   ```

2. **Esta semana (4 horas):** Implementa DTOs con validaciones
   - Ver `RECOMENDACIONES_AVANZADAS.md`

3. **Próximas 2 semanas (8 horas):** 
   - Excepciones más específicas
   - Logging integrado
   - Tests

4. **Próximo mes (4 horas):**
   - Documentación Swagger
   - Caché
   - Circuit breaker

---

## 📚 Más información

- **JPA Relationships:** `ERROR_JPA_SOLUCION.md`
- **Spring Exceptions:** `RECOMENDACIONES_AVANZADAS.md`
- **Validación:** `RECOMENDACIONES_AVANZADAS.md`
- **Testing:** `RECOMENDACIONES_AVANZADAS.md`

---

**Última actualización:** 19/01/2026  
**Versión:** 1.0 ✅
