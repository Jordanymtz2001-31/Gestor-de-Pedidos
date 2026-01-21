# 🔧 SOLUCIÓN: Error de JPA EntityManagerFactory - Relación OneToMany

## ❌ El Problema

Recibiste este error al levantar el microservicio:

```
Collection 'com.mx.Pedido.Entity.Pedido.detalles' is 'mappedBy' a property 
named 'idPedido' which does not exist in the target entity 
'com.mx.Pedido.Entity.Detalle_Pedido'
```

---

## 🔍 ¿Por Qué Ocurría?

### La configuración ANTES (incorrecta):

**En Pedido.java:**
```java
@OneToMany(mappedBy = "idPedido", cascade = CascadeType.ALL)
List<Detalle_Pedido> detalles = new ArrayList<>();
```

**En Detalle_Pedido.java:**
```java
// ❌ FALTABA ESTO:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "PEDIDO_ID", nullable = false)
private Pedido idPedido;  // ← Anotación @ManyToOne no existía
```

### El problema es:

1. `Pedido.java` dice: "Espero encontrar una propiedad llamada **`idPedido`** en `Detalle_Pedido`"
2. `Detalle_Pedido.java` NO tenía una propiedad `idPedido` con la anotación `@ManyToOne`
3. JPA no puede crear la relación sin encontrar el lado esclavo (ManyToOne)

---

## ✅ La Solución (Ya Implementada)

Se agregó la anotación `@ManyToOne` faltante en `Detalle_Pedido.java`:

```java
//DEFINIMOS LA RELACIÓN DE DETALLE PEDIDO CON PEDIDO
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "PEDIDO_ID", nullable = false)
private Pedido idPedido;  // ✓ Ahora JPA lo encuentra
```

### Ahora la relación funciona correctamente:

```
Pedido (lado uno)
    ↓
@OneToMany(mappedBy = "idPedido")  ← Busca...
    ↓
Detalle_Pedido (lado muchos)
    ↓
@ManyToOne
@JoinColumn(name = "PEDIDO_ID")  ← ...y lo encuentra aquí
private Pedido idPedido;
```

---

## 📚 Entendiendo OneToMany / ManyToOne

### Relación Uno a Muchos (1:N):

```
┌──────────────────────────────────────────┐
│         PEDIDO (1)                       │
│  ┌──────────────────────────────────┐   │
│  │ id_pedido: 1                     │   │
│  │ fecha: 2026-01-19                │   │
│  │ total: $500                      │   │
│  │ detalles: [↓, ↓, ↓]              │   │
│  └──────────────────────────────────┘   │
└──────────────────────────────────────────┘
         ↓        ↓        ↓
    ┌────────┬────────┬────────┐
    │        │        │        │
    ↓        ↓        ↓        │
┌─────────────────────────┐  ┌─────────────────────────┐
│  DETALLE_PEDIDO (N)     │  │  DETALLE_PEDIDO (N)     │
│  id: 1                  │  │  id: 2                  │
│  pedido_id: 1  ←────────┼──┼──pedido_id: 1           │
│  producto_id: 10        │  │  producto_id: 20        │
│  cantidad: 2            │  │  cantidad: 3            │
│  precio: $200           │  │  precio: $100           │
└─────────────────────────┘  └─────────────────────────┘
```

### En código:

```java
// LADO UNO (Pedido): Define la colección
@OneToMany(
    mappedBy = "idPedido",        // ← Busca esta propiedad en Detalle_Pedido
    cascade = CascadeType.ALL,    // ← Operaciones en cascada
    fetch = FetchType.LAZY         // ← Carga perezosa
)
private List<Detalle_Pedido> detalles = new ArrayList<>();

// LADO MUCHOS (Detalle_Pedido): Define la referencia
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(
    name = "PEDIDO_ID",            // ← Nombre de la columna FK en BD
    nullable = false               // ← No puede ser null
)
private Pedido idPedido;           // ← Propiedad que Pedido busca con mappedBy
```

---

## 🎯 Reglas Importantes

| Aspecto | OneToMany | ManyToOne |
|---------|-----------|-----------|
| **Ubicación** | Lado uno (Pedido) | Lado muchos (Detalle_Pedido) |
| **mappedBy** | SI, aquí se define | NO, aquí NO va |
| **@JoinColumn** | NO | SÍ, aquí se define |
| **Responsable de FK** | ManyToOne | ManyToOne |
| **Responsable de lista** | OneToMany | Ninguno |

---

## 📊 Errores Comunes en Relaciones

### ❌ Error 1: Olvidar @ManyToOne

```java
// Pedido.java
@OneToMany(mappedBy = "idPedido")
private List<Detalle_Pedido> detalles;

// Detalle_Pedido.java - ❌ FALTA ESTO:
// @ManyToOne
// @JoinColumn(name = "PEDIDO_ID")
private Pedido idPedido;  // ← JPA no lo reconoce
```

### ❌ Error 2: mappedBy apunta a propiedad inexistente

```java
// Pedido.java
@OneToMany(mappedBy = "pedido")  // ❌ Busca "pedido"
private List<Detalle_Pedido> detalles;

// Detalle_Pedido.java
@ManyToOne
private Pedido idPedido;  // ← Pero aquí se llama "idPedido"
```

### ❌ Error 3: Usar @JoinColumn en ambos lados

```java
// Pedido.java
@OneToMany
@JoinColumn(name = "PEDIDO_ID")  // ❌ NO va aquí
private List<Detalle_Pedido> detalles;

// Detalle_Pedido.java
@ManyToOne
@JoinColumn(name = "PEDIDO_ID")  // ✓ Solo aquí
private Pedido idPedido;
```

### ❌ Error 4: Usar mappedBy en ManyToOne

```java
// Detalle_Pedido.java
@ManyToOne
@JoinColumn(mappedBy = "idPedido")  // ❌ mappedBy NO va aquí
private Pedido idPedido;
```

---

## ✅ Configuración Correcta (Tu caso)

### Pedido.java (Lado UNO):
```java
@OneToMany(
    mappedBy = "idPedido",              // ← Busca esta propiedad
    cascade = CascadeType.ALL,          // ← Elimina detalles si se elimina pedido
    fetch = FetchType.LAZY              // ← No carga automáticamente
)
private List<Detalle_Pedido> detalles = new ArrayList<>();
```

### Detalle_Pedido.java (Lado MUCHOS):
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(
    name = "PEDIDO_ID",                 // ← Columna en BD
    nullable = false                    // ← Siempre debe tener un pedido
)
private Pedido idPedido;                // ← JPA lo encuentra aquí
```

---

## 🧪 Cómo Probar

Después de esta corrección, deberías poder:

### 1. Levantar la aplicación sin errores
```bash
mvn spring-boot:run
```

### 2. Crear un pedido con detalles
```java
Pedido pedido = new Pedido();
pedido.setFecha(LocalDate.now());
pedido.setTotal(BigDecimal.valueOf(500));
pedido.setEstatus(EEstado.PENDIENTE);
pedido.setClienteId(1);

Detalle_Pedido detalle = new Detalle_Pedido();
detalle.setIdPedido(pedido);        // ← Relación bidireccional
detalle.setProductoId(10);
detalle.setCantuidad(2);
detalle.setPrecioUnitario(BigDecimal.valueOf(250));

pedido.getDetalles().add(detalle);  // ← Agregar a la colección

pedidoRepository.save(pedido);      // ← Guarda pedido y detalles
```

### 3. Recuperar pedido con detalles
```java
Pedido pedidoConDetalles = pedidoRepository.findByIdConDetalles(1);
List<Detalle_Pedido> detalles = pedidoConDetalles.getDetalles();
```

---

## 💡 Notas importantes

1. **Cascada:** Con `CascadeType.ALL`, al eliminar un pedido se eliminan automáticamente todos sus detalles
2. **Lazy Loading:** Los detalles NO se cargan automáticamente al traer un pedido (mejor rendimiento)
3. **Relación Bidireccional:** Puedes acceder desde ambos lados:
   - `pedido.getDetalles()` ✓
   - `detalle.getIdPedido()` ✓

4. **Nombrado:** En BD se verá:
   ```
   PEDIDO (tabla)
   - ID_PEDIDO (PK)
   - FECHA
   - TOTAL
   - ESTATUS
   - CLIENTE_ID
   
   DETALLE_PEDIDO (tabla)
   - ID_DETALL_PEDIDO (PK)
   - PEDIDO_ID (FK → PEDIDO.ID_PEDIDO)
   - PRODUCTO_ID
   - CANTIDAD
   - PRECIO_UNITARIO
   ```

---

## ✨ Estado Actual

✅ **El error está solucionado**
✅ **La relación OneToMany/ManyToOne está correctamente configurada**
✅ **Deberías poder levantar el microservicio sin problemas**

Si aún tienes errores, por favor comparte el stack trace y te ayudaré.

---

**Solucionado:** 19/01/2026
**Cambio aplicado:** Detalle_Pedido.java
