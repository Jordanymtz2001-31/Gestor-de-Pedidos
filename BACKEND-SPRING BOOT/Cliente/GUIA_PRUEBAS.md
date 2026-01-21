# 🧪 Guía de Pruebas - Excepciones Cliente

## Pruebas con cURL o Postman

### 1️⃣ GUARDAR CLIENTE - Éxito

**Request:**
```bash
curl -X POST http://localhost:8080/cliente/guardar \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "telefono": "5551234567"
  }'
```

**Response (200 OK):**
```json
{
    "mensaje": "Cliente guardado con éxito"
}
```

---

### 2️⃣ GUARDAR CLIENTE - Error: Nombre Duplicado

**Request:**
```bash
curl -X POST http://localhost:8080/cliente/guardar \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "otro@example.com",
    "telefono": "5559876543"
  }'
```

**Response (409 CONFLICT):**
```json
{
    "codigo": "CLI-409",
    "mensaje": "El nombre: 'Juan Pérez' ya existe en la base de datos",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

### 3️⃣ GUARDAR CLIENTE - Error: Email Duplicado

**Request:**
```bash
curl -X POST http://localhost:8080/cliente/guardar \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos López",
    "email": "juan@example.com",
    "telefono": "5559876543"
  }'
```

**Response (409 CONFLICT):**
```json
{
    "codigo": "CLI-409",
    "mensaje": "El email: 'juan@example.com' ya existe en la base de datos",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

### 4️⃣ GUARDAR CLIENTE - Error: Teléfono Duplicado

**Request:**
```bash
curl -X POST http://localhost:8080/cliente/guardar \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María González",
    "email": "maria@example.com",
    "telefono": "5551234567"
  }'
```

**Response (409 CONFLICT):**
```json
{
    "codigo": "CLI-409",
    "mensaje": "El teléfono: '5551234567' ya existe en la base de datos",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

### 5️⃣ BUSCAR CLIENTE - Éxito

**Request:**
```bash
curl http://localhost:8080/cliente/buscar/1
```

**Response (200 OK):**
```json
{
    "idCliente": 1,
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "telefono": "5551234567"
}
```

---

### 6️⃣ BUSCAR CLIENTE - Error: No Existe

**Request:**
```bash
curl http://localhost:8080/cliente/buscar/999
```

**Response (404 NOT FOUND):**
```json
{
    "codigo": "CLI-404",
    "mensaje": "Cliente con ID 999 no encontrado",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

### 7️⃣ EDITAR CLIENTE - Éxito

**Request:**
```bash
curl -X PUT http://localhost:8080/cliente/editar \
  -H "Content-Type: application/json" \
  -d '{
    "idCliente": 1,
    "nombre": "Juan Pérez Actualizado",
    "email": "juan_nuevo@example.com",
    "telefono": "5551234567"
  }'
```

**Response (200 OK):**
```json
{
    "mensaje": "Cliente editado con éxito"
}
```

---

### 8️⃣ EDITAR CLIENTE - Error: No Existe

**Request:**
```bash
curl -X PUT http://localhost:8080/cliente/editar \
  -H "Content-Type: application/json" \
  -d '{
    "idCliente": 999,
    "nombre": "No Existe",
    "email": "noexiste@example.com",
    "telefono": "5551111111"
  }'
```

**Response (404 NOT FOUND):**
```json
{
    "codigo": "CLI-404",
    "mensaje": "Cliente con ID 999 no encontrado",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

### 9️⃣ EDITAR CLIENTE - Error: Email Duplicado

**Request (intentando cambiar email de Juan a email de Carlos):**
```bash
curl -X PUT http://localhost:8080/cliente/editar \
  -H "Content-Type: application/json" \
  -d '{
    "idCliente": 1,
    "nombre": "Juan Pérez",
    "email": "carlos@example.com",
    "telefono": "5551234567"
  }'
```

**Response (409 CONFLICT):**
```json
{
    "codigo": "CLI-409",
    "mensaje": "El email: 'carlos@example.com' ya existe en la base de datos",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

### 🔟 LISTAR CLIENTES - Éxito

**Request:**
```bash
curl http://localhost:8080/cliente/listar
```

**Response (200 OK):**
```json
[
    {
        "idCliente": 1,
        "nombre": "Juan Pérez",
        "email": "juan@example.com",
        "telefono": "5551234567"
    },
    {
        "idCliente": 2,
        "nombre": "Carlos López",
        "email": "carlos@example.com",
        "telefono": "5559876543"
    }
]
```

---

### 1️⃣1️⃣ LISTAR CLIENTES - Error: Sin registros

**Response (204 NO CONTENT):**
```
(Body vacío)
```

---

### 1️⃣2️⃣ ELIMINAR CLIENTE - Éxito

**Request:**
```bash
curl -X DELETE http://localhost:8080/cliente/eliminar/1
```

**Response (200 OK):**
```json
{
    "mensaje": "Cliente eliminado con éxito"
}
```

---

### 1️⃣3️⃣ ELIMINAR CLIENTE - Error: No Existe

**Request:**
```bash
curl -X DELETE http://localhost:8080/cliente/eliminar/999
```

**Response (404 NOT FOUND):**
```json
{
    "codigo": "CLI-404",
    "mensaje": "Cliente con ID 999 no encontrado",
    "timestamp": "2026-01-18T14:35:22.123456"
}
```

---

## 📊 Tabla Resumen de Respuestas

| Operación | Éxito | Error | Status |
|-----------|-------|-------|--------|
| Guardar | ✅ | Duplicado | 200 / 409 |
| Editar | ✅ | No existe / Duplicado | 200 / 404 / 409 |
| Buscar | ✅ | No existe | 200 / 404 |
| Eliminar | ✅ | No existe | 200 / 404 |
| Listar | ✅ | Sin registros | 200 / 204 |

---

## 🔍 Importar en Postman

Puedes usar esta colección en Postman:

1. Copia el JSON de abajo
2. En Postman: File → Import → Raw text → Pega aquí

```json
{
  "info": {
    "name": "Cliente Microservice - Excepciones",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Guardar Cliente",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/cliente/guardar",
        "header": [
          {"key": "Content-Type", "value": "application/json"}
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"nombre\": \"Juan Pérez\", \"email\": \"juan@example.com\", \"telefono\": \"5551234567\"}"
        }
      }
    },
    {
      "name": "Buscar Cliente",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/cliente/buscar/1"
      }
    },
    {
      "name": "Listar Clientes",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/cliente/listar"
      }
    },
    {
      "name": "Editar Cliente",
      "request": {
        "method": "PUT",
        "url": "http://localhost:8080/cliente/editar",
        "header": [
          {"key": "Content-Type", "value": "application/json"}
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"idCliente\": 1, \"nombre\": \"Juan P\", \"email\": \"juan@example.com\", \"telefono\": \"5551234567\"}"
        }
      }
    },
    {
      "name": "Eliminar Cliente",
      "request": {
        "method": "DELETE",
        "url": "http://localhost:8080/cliente/eliminar/1"
      }
    }
  ]
}
```

---

**Fecha:** 18/01/2026  
**Versión:** 1.0
