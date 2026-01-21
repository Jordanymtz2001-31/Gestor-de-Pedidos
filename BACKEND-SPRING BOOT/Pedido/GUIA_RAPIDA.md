# ⚡ GUÍA RÁPIDA DE REFERENCIA

## 🚀 Para levantar el microservicio sin errores

```bash
# 1. Navegar a la carpeta del Pedido
cd D:\JORDANY_GM\Proyectos\Gestor de Pedidos\BACKEND-SPRING BOOT\Pedido

# 2. Limpiar y compilar
mvn clean install

# 3. Ejecutar
mvn spring-boot:run
```

---

## 📌 Lo que se corrigió

### 1. Relación OneToMany (CRÍTICO)
```java
// ❌ ANTES: Faltaba @ManyToOne en Detalle_Pedido
// ✅ AHORA: Agregado correctamente
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "PEDIDO_ID", nullable = false)
private Pedido idPedido;
```

### 2. Constructor sin argumentos
```java
// ❌ ANTES: Faltaba @NoArgsConstructor
@Data
public class ErrorResponse { }

// ✅ AHORA:
@Data
@NoArgsConstructor
public class ErrorResponse { }
```

### 3. Códigos de error
```java
// ❌ ANTES: "CLI-404", "CLI-500"
// ✅ AHORA: "PED-404", "PED-500"
new ErrorResponse("PED-404", ex.getMessage());
```

### 4. Ruta dinámica
```java
// ❌ ANTES: @GetMapping("/listarXCliente")
// ✅ AHORA: @GetMapping("/listarXCliente/{clienteId}")
```

### 5. Typo
```java
// ❌ ANTES: private LocalDate feha;
// ✅ AHORA: private LocalDate fecha;
```

---

## 📋 Archivos cambiados

1. **ErrorResponse.java** - @NoArgsConstructor agregado
2. **GlobalExceptionHandler.java** - Códigos y manejadores mejorados
3. **PedidoController.java** - Rutas y validaciones corregidas
4. **PedidoService.java** - Validaciones mejoradas
5. **Pedido.java** - Typo corregido
6. **Detalle_Pedido.java** - @ManyToOne agregado ⭐ CRÍTICO

---

## 🧪 Pruebas rápidas

```bash
# 1. Listar todos los pedidos
curl http://localhost:8003/listar

# 2. Buscar pedido por ID
curl http://localhost:8003/buscar/1

# 3. Buscar pedido que no existe (debe retornar 404)
curl http://localhost:8003/buscar/999

# 4. Listar por cliente
curl http://localhost:8003/listarXCliente/1

# 5. Listar por cliente inválido (debe retornar 400)
curl http://localhost:8003/listarXCliente/-1

# 6. Obtener detalles
curl http://localhost:8003/detalle/1
```

---

## 🔍 Respuestas esperadas

### Pedido no encontrado (404)
```json
{
  "codigo": "PED-404",
  "mensaje": "Pedido con ID 999 no encontrado",
  "timestamp": "2026-01-19T10:25:00"
}
```

### Cliente inválido (400)
```json
{
  "codigo": "PED-400",
  "mensaje": "El ID del cliente debe ser mayor a 0",
  "timestamp": "2026-01-19T10:25:00"
}
```

### Error interno (500)
```json
{
  "codigo": "PED-500",
  "mensaje": "Error interno del servicio",
  "timestamp": "2026-01-19T10:25:00"
}
```

---

## 📚 Documentación importante

- `ANALISIS_EXCEPCIONES.md` - Análisis técnico
- `CORRECCIONES_EXCEPCIONES.md` - Detalles de cambios
- `ERROR_JPA_SOLUCION.md` - Explicación del error de JPA
- `RECOMENDACIONES_AVANZADAS.md` - Mejoras futuras

---

## ✅ Checklist antes de producción

```
☐ Microservicio levanta sin errores
☐ GlobalExceptionHandler captura excepciones
☐ Códigos de error son PED-XXX
☐ Rutas dinámicas funcionan correctamente
☐ Validaciones se ejecutan en Service
☐ Relaciones JPA funcionan
```

---

## 🆘 Si hay problemas

### Error: "Collection 'Pedido.detalles' is 'mappedBy'..."
→ Revisa que `Detalle_Pedido.idPedido` tenga `@ManyToOne`

### Error: "No default constructor for entity"
→ Verifica que `ErrorResponse` tiene `@NoArgsConstructor`

### Endpoint retorna 404 inesperado
→ Revisa que la ruta tiene `{variable}` en la URL

### Validación no funciona
→ Asegúrate que estés usando `@Valid` en `@RequestBody`

---

## 💡 Próximas mejoras

1. **DTOs con validaciones** (`@NotNull`, `@Min`, etc.)
2. **Excepciones específicas** (InvalidClienteException, etc.)
3. **Logging en handlers** (SLF4J)
4. **Tests para excepciones** (JUnit 5)
5. **Documentación Swagger** (OpenAPI)

---

**Última actualización:** 19/01/2026  
**Versión:** 1.0 ✅
