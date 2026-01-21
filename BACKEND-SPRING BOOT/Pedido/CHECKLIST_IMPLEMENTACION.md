# ✅ CHECKLIST INTERACTIVO DE IMPLEMENTACIÓN

## 🚀 FASE 1: VALIDAR QUE TODO FUNCIONA (Hoy)

### Paso 1: Compilar el Código
- [ ] Abre terminal en `D:\JORDANY_GM\Proyectos\Gestor de Pedidos\BACKEND-SPRING BOOT\Pedido`
- [ ] Ejecuta: `mvn clean install`
- [ ] ¿Compiló sin errores? → Sí ✅

### Paso 2: Levantar la Aplicación
- [ ] Ejecuta: `mvn spring-boot:run`
- [ ] Espera a que levante (busca: "Tomcat started on port")
- [ ] ¿La aplicación levantó? → Sí ✅

### Paso 3: Probar Endpoint Básico
```bash
curl http://localhost:8003/listar
```
- [ ] ¿Retornó resultados? → Sí ✅
- [ ] ¿O retornó 204 (sin contenido)? → También ✅

### Paso 4: Probar Excepción
```bash
curl http://localhost:8003/buscar/999
```
- [ ] ¿Retornó 404?
- [ ] ¿Tiene código "PED-404"?
- [ ] ¿Tiene timestamp?
→ Todo bien ✅

---

## 📚 FASE 2: ENTENDER LOS CAMBIOS (Esta Semana)

### Documentos a Leer
- [ ] `GUIA_RAPIDA.md` (5 min)
- [ ] `RESUMEN_FINAL.md` (10 min)
- [ ] `ERROR_JPA_SOLUCION.md` (15 min)
- [ ] `CORRECCIONES_EXCEPCIONES.md` (15 min)
- [ ] `COMPARACION_ANTES_DESPUES.md` (10 min)

**Tiempo total:** ~55 minutos

### Verificaciones de Comprensión
- [ ] ¿Entiendes qué era el error de JPA?
- [ ] ¿Sabes por qué faltaba @ManyToOne?
- [ ] ¿Comprendes cómo funciona GlobalExceptionHandler?
- [ ] ¿Sabes por qué cambié los códigos a PED-xxx?
- [ ] ¿Entiendes dónde van las validaciones?

Si contestaste SÍ a todo → Continúa ✅

---

## 🔧 FASE 3: CREAR DTTO CON VALIDACIONES (Próxima Semana)

### Crear archivo: `src/main/java/com/mx/Pedido/Dtos/PedidoRequestDto.java`

- [ ] Archivo creado
- [ ] Anotaciones `@NotNull` agregadas
- [ ] Anotaciones `@Min` agregadas
- [ ] Anotaciones de validación funcionando

```java
@Data
public class PedidoRequestDto {
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "El total es obligatorio")
    @Min(value = 1, message = "El total debe ser mayor a 0")
    private BigDecimal total;
    
    @NotNull(message = "El estado es obligatorio")
    private String estatus;
    
    @NotNull(message = "El cliente es obligatorio")
    @Min(value = 1, message = "ID de cliente inválido")
    private Integer clienteId;
}
```

### Actualizar Controller para usar DTO

- [ ] Importar PedidoRequestDto
- [ ] Cambiar `@PostMapping("/guardar")` para usar DTO
- [ ] Cambiar `@PutMapping("/editar")` para usar DTO
- [ ] Agregar `@Valid` en `@RequestBody`

```java
@PostMapping("/guardar")
public ResponseEntity<Map<String, String>> GuardarPedido(
    @Valid @RequestBody PedidoRequestDto pedidoDto
) {
    service.guardarPedido(pedidoDto);
    return ResponseEntity.ok(Map.of("mensaje", "Pedido guardado con éxito"));
}
```

### Actualizar Service para recibir DTO

- [ ] Service ahora recibe DTO en lugar de Entity
- [ ] Mapear DTO a Entity antes de guardar
- [ ] Compilar sin errores

---

## 📝 FASE 4: AGREGAR LOGGING (Próximas 2 Semanas)

### Agregar SLF4J a GlobalExceptionHandler

- [ ] Importar Logger y LoggerFactory
- [ ] Agregar `Logger logger = LoggerFactory.getLogger(...)`
- [ ] Logging en cada @ExceptionHandler

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handle(PedidoNoEncontradoException ex) {
        logger.warn("Pedido no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("PED-404", ex.getMessage()));
    }
    
    @ExceptionHandler(PedidoServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceError(PedidoServiceException ex) {
        logger.error("Error en servicio de pedido: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("PED-500", "Error interno del servicio"));
    }
}
```

### Crear excepciones más específicas

- [ ] `InvalidClienteException.java`
- [ ] `PedidoDataIntegrityException.java`
- [ ] `InvalidPedidoDataException.java`
- [ ] Manejadores en GlobalExceptionHandler para cada una

---

## 🧪 FASE 5: ESCRIBIR TESTS (Próximas 3 Semanas)

### Test 1: Pedido No Encontrado

- [ ] Crear `PedidoControllerTest.java`
- [ ] Test para `/buscar/999` → debe retornar 404
- [ ] Verificar código de error es "PED-404"

```java
@Test
public void testBuscarPedidoNoEncontrado() throws Exception {
    mockMvc.perform(get("/buscar/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.codigo").value("PED-404"));
}
```

### Test 2: Validación de Cliente

- [ ] Test para `/listarXCliente/-1` → debe retornar 400
- [ ] Verificar código de error es "PED-400"

```java
@Test
public void testListarConClienteInvalido() throws Exception {
    mockMvc.perform(get("/listarXCliente/-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("PED-400"));
}
```

### Test 3: Guardar Pedido Válido

- [ ] Test para POST `/guardar` con datos válidos
- [ ] Debe retornar 200
- [ ] Pedido debe estar en BD

### Test 4: Guardar Pedido Inválido

- [ ] Test para POST `/guardar` sin fecha
- [ ] Debe retornar 400
- [ ] Mensaje debe indicar error de validación

---

## 📖 FASE 6: DOCUMENTAR EN SWAGGER (Próximas 4 Semanas)

### Agregar dependencia Springdoc-OpenAPI

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>
```

### Documentar endpoints con @Operation

- [ ] Importar `io.swagger.v3.oas.annotations.*`
- [ ] Agregar `@Operation` a cada endpoint
- [ ] Documentar códigos de error posibles
- [ ] Acceder a http://localhost:8003/swagger-ui.html

```java
@GetMapping("/buscar/{idPedido}")
@Operation(summary = "Buscar pedido por ID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado - PED-404"),
    @ApiResponse(responseCode = "500", description = "Error del servidor - PED-500")
})
public ResponseEntity<?> BuscarPorID(@PathVariable Integer idPedido) {
    // ...
}
```

---

## 🎯 VERIFICACIONES FINALES

### Código Limpio
- [ ] Sin excepciones genéricas (solo RuntimeException)
- [ ] Sin validación manual en Controllers
- [ ] Sin código duplicado
- [ ] Nombres claros y consistentes

### Funcionalidad
- [ ] Todos los endpoints funcionan
- [ ] Validaciones se ejecutan
- [ ] Excepciones son capturadas
- [ ] Respuestas JSON son correctas

### Documentación
- [ ] Código comentado (si es necesario)
- [ ] README actualizado
- [ ] Swagger documentado
- [ ] Equipo capacitado

### Performance
- [ ] Queries optimizadas (lazy loading)
- [ ] No hay N+1 queries
- [ ] Respuestas son rápidas
- [ ] Uso de memoria es normal

---

## 📊 TABLA DE PROGRESO

| Fase | Tarea | Estado | Fecha |
|------|-------|--------|-------|
| 1 | Compilar | ✅ Hecho | Hoy |
| 1 | Levantar App | ✅ Hecho | Hoy |
| 1 | Probar Endpoints | ⏳ Por hacer | Hoy |
| 2 | Leer Documentos | ⏳ Por hacer | Esta semana |
| 3 | Crear DTOs | ⏳ Por hacer | Próxima semana |
| 4 | Agregar Logging | ⏳ Por hacer | 2 semanas |
| 4 | Excepciones Específicas | ⏳ Por hacer | 2 semanas |
| 5 | Escribir Tests | ⏳ Por hacer | 3 semanas |
| 6 | Documentar Swagger | ⏳ Por hacer | 4 semanas |

---

## 🎓 RECURSOS DE APRENDIZAJE

### Documentación en el Proyecto
- [x] GUIA_RAPIDA.md
- [x] RESUMEN_FINAL.md
- [x] ERROR_JPA_SOLUCION.md
- [x] CORRECCIONES_EXCEPCIONES.md
- [x] PREGUNTAS_FRECUENTES.md
- [x] RECOMENDACIONES_AVANZADAS.md

### Recursos Externos (Recomendados)
- [ ] [Spring Boot Docs - Exception Handling](https://spring.io/guides/tutorials/rest/)
- [ ] [JPA/Hibernate Relationships](https://www.baeldung.com/hibernate-one-to-many)
- [ ] [Validation in Spring Boot](https://www.baeldung.com/spring-boot-bean-validation)
- [ ] [OpenAPI/Swagger](https://springdoc.org/)

---

## 🚨 PUNTOS CRÍTICOS A RECORDAR

```
⚠️ NO olvides:
  ├─ @ManyToOne en Detalle_Pedido (YA HECHO)
  ├─ @NoArgsConstructor en ErrorResponse (YA HECHO)
  ├─ @Valid en @RequestBody (Para hacer)
  ├─ Validación en Service, no en Controller (YA HECHO)
  └─ Manejo centralizado en GlobalExceptionHandler (YA HECHO)

✅ HECHO HOY:
  ├─ Relaciones JPA funcionando
  ├─ Excepciones centralizadas
  ├─ Códigos de error consistentes
  ├─ Validaciones mejoradas
  └─ 10 documentos detallados
```

---

## 💡 TIPS PARA NO OLVIDAR

1. **Siempre** usa `@Valid` para DTOs
2. **Nunca** valides en el Controller
3. **Siempre** crea excepciones específicas
4. **Nunca** dejes Exception genérica
5. **Siempre** centraliza excepciones en @ControllerAdvice
6. **Nunca** duplices código de error
7. **Siempre** usa @JoinColumn en el lado muchos
8. **Nunca** olvides @ManyToOne en relaciones

---

## 📝 NOTAS PERSONALES

Espacio para anotar tus notas:

```
_________________________________________________
_________________________________________________
_________________________________________________
_________________________________________________
_________________________________________________
```

---

**Última actualización:** 19/01/2026  
**Progreso General:** Fase 1 ✅, Fases 2-6 ⏳  
**Tiempo Estimado Total:** 3-4 semanas para completar todo
