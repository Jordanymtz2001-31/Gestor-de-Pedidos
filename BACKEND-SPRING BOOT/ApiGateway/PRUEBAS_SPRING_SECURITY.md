# 🧪 EJEMPLOS DE PRUEBAS - SPRING SECURITY

## 📝 Tabla de Pruebas

| # | Endpoint | Método | Usuario | Resultado | Código |
|---|----------|--------|---------|-----------|--------|
| 1 | /cliente | GET | admin | ✅ OK | 200 |
| 2 | /cliente | GET | user | ✅ OK | 200 |
| 3 | /cliente | POST | admin | ✅ OK | 201 |
| 4 | /cliente | POST | user | ❌ NO | 403 |
| 5 | /cliente | PUT | admin | ✅ OK | 200 |
| 6 | /cliente | PUT | user | ❌ NO | 403 |
| 7 | /cliente | DELETE | admin | ✅ OK | 200 |
| 8 | /cliente | DELETE | user | ❌ NO | 403 |
| 9 | /producto | GET | admin | ✅ OK | 200 |
| 10 | /producto | GET | user | ✅ OK | 200 |
| 11 | /producto | POST | admin | ✅ OK | 201 |
| 12 | /producto | POST | user | ❌ NO | 403 |
| 13 | /pedido | GET | admin | ✅ OK | 200 |
| 14 | /pedido | GET | user | ✅ OK | 200 |
| 15 | /pedido | POST | admin | ✅ OK | 201 |
| 16 | /pedido | POST | user | ❌ NO | 403 |
| 17 | /detalle | GET | admin | ✅ OK | 200 |
| 18 | /detalle | GET | user | ✅ OK | 200 |
| 19 | /detalle | POST | admin | ✅ OK | 201 |
| 20 | /detalle | POST | user | ❌ NO | 403 |

---

## 🔑 CREDENCIALES

### ADMIN (Acceso Completo)
```
Usuario: admin
Contraseña: admin123
Base64: YWRtaW46YWRtaW4xMjM=
```

### USER (Solo Lectura)
```
Usuario: user
Contraseña: user123
Base64: dXNlcjp1c2VyMTIz
```

---

## 📋 PRUEBAS POR SECCIÓN

### 🔹 CLIENTE - GET (Lectura)

**✅ FUNCIONA CON ADMIN**
```
URL: http://localhost:9000/cliente
Método: GET
Auth: Basic Auth
Username: admin
Password: admin123
Headers:
  Content-Type: application/json
```

**Respuesta esperada (200 OK)**:
```json
{
  "id": 1,
  "nombre": "Cliente 1",
  "email": "cliente1@example.com"
}
```

---

**✅ FUNCIONA CON USER**
```
URL: http://localhost:9000/cliente
Método: GET
Auth: Basic Auth
Username: user
Password: user123
```

**Respuesta esperada (200 OK)**:
```json
{
  "id": 1,
  "nombre": "Cliente 1",
  "email": "cliente1@example.com"
}
```

---

### 🔹 CLIENTE - POST (Crear)

**✅ FUNCIONA CON ADMIN**
```
URL: http://localhost:9000/cliente
Método: POST
Auth: Basic Auth
Username: admin
Password: admin123
Headers:
  Content-Type: application/json

Body (JSON):
{
  "nombre": "Nuevo Cliente",
  "email": "nuevo@example.com",
  "telefono": "123456789"
}
```

**Respuesta esperada (201 Created)**:
```json
{
  "id": 10,
  "nombre": "Nuevo Cliente",
  "email": "nuevo@example.com",
  "telefono": "123456789"
}
```

---

**❌ NO FUNCIONA CON USER**
```
URL: http://localhost:9000/cliente
Método: POST
Auth: Basic Auth
Username: user
Password: user123
```

**Respuesta esperada (403 Forbidden)**:
```json
{
  "timestamp": "2025-01-19T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/cliente"
}
```

---

### 🔹 CLIENTE - PUT (Actualizar)

**✅ FUNCIONA CON ADMIN**
```
URL: http://localhost:9000/cliente/1
Método: PUT
Auth: Basic Auth
Username: admin
Password: admin123
Headers:
  Content-Type: application/json

Body (JSON):
{
  "nombre": "Cliente Actualizado",
  "email": "actualizado@example.com",
  "telefono": "987654321"
}
```

**Respuesta esperada (200 OK)**:
```json
{
  "id": 1,
  "nombre": "Cliente Actualizado",
  "email": "actualizado@example.com",
  "telefono": "987654321"
}
```

---

**❌ NO FUNCIONA CON USER**
```
URL: http://localhost:9000/cliente/1
Método: PUT
Auth: Basic Auth
Username: user
Password: user123
```

**Respuesta esperada (403 Forbidden)**

---

### 🔹 CLIENTE - DELETE (Eliminar)

**✅ FUNCIONA CON ADMIN**
```
URL: http://localhost:9000/cliente/1
Método: DELETE
Auth: Basic Auth
Username: admin
Password: admin123
```

**Respuesta esperada (204 No Content)**

---

**❌ NO FUNCIONA CON USER**
```
URL: http://localhost:9000/cliente/1
Método: DELETE
Auth: Basic Auth
Username: user
Password: user123
```

**Respuesta esperada (403 Forbidden)**

---

## 🧬 COMANDOS CURL COMPLETOS

### Listar Clientes (GET) con ADMIN
```bash
curl -X GET "http://localhost:9000/cliente" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -H "Content-Type: application/json"
```

### Listar Clientes (GET) con USER
```bash
curl -X GET "http://localhost:9000/cliente" \
  -H "Authorization: Basic dXNlcjp1c2VyMTIz" \
  -H "Content-Type: application/json"
```

### Crear Cliente (POST) con ADMIN
```bash
curl -X POST "http://localhost:9000/cliente" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Cliente",
    "email": "nuevo@example.com",
    "telefono": "123456789"
  }'
```

### Crear Cliente (POST) con USER (Debe fallar)
```bash
curl -X POST "http://localhost:9000/cliente" \
  -H "Authorization: Basic dXNlcjp1c2VyMTIz" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Cliente",
    "email": "nuevo@example.com"
  }'
```

**Respuesta**: `403 Forbidden`

### Actualizar Cliente (PUT) con ADMIN
```bash
curl -X PUT "http://localhost:9000/cliente/1" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Cliente Actualizado",
    "email": "actualizado@example.com"
  }'
```

### Eliminar Cliente (DELETE) con ADMIN
```bash
curl -X DELETE "http://localhost:9000/cliente/1" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

---

## 🛠️ CONFIGURAR EN POSTMAN

### Paso 1: Crear Colección
1. Click en **"+"** para nueva colección
2. Nombre: `Gestor de Pedidos - Security Tests`
3. Click en **"Create"**

### Paso 2: Crear Carpeta
1. Dentro de la colección, click en **"New Folder"**
2. Nombre: `Cliente`
3. Repetir para `Producto`, `Pedido`, `Detalle`

### Paso 3: Crear Requests

**Request 1: GET Cliente (ADMIN)**
- Nombre: `GET Cliente - ADMIN`
- URL: `http://localhost:9000/cliente`
- Método: GET
- Pestaña Auth: Basic Auth
  - Username: `admin`
  - Password: `admin123`
- Click **Send** → Resultado: 200 OK ✅

**Request 2: GET Cliente (USER)**
- Nombre: `GET Cliente - USER`
- URL: `http://localhost:9000/cliente`
- Método: GET
- Pestaña Auth: Basic Auth
  - Username: `user`
  - Password: `user123`
- Click **Send** → Resultado: 200 OK ✅

**Request 3: POST Cliente (ADMIN)**
- Nombre: `POST Cliente - ADMIN`
- URL: `http://localhost:9000/cliente`
- Método: POST
- Headers:
  - `Content-Type: application/json`
- Pestaña Auth: Basic Auth
  - Username: `admin`
  - Password: `admin123`
- Body (raw JSON):
  ```json
  {
    "nombre": "Nuevo Cliente",
    "email": "nuevo@example.com"
  }
  ```
- Click **Send** → Resultado: 201 Created ✅

**Request 4: POST Cliente (USER) - DEBE FALLAR**
- Nombre: `POST Cliente - USER (Debe fallar)`
- URL: `http://localhost:9000/cliente`
- Método: POST
- Headers:
  - `Content-Type: application/json`
- Pestaña Auth: Basic Auth
  - Username: `user`
  - Password: `user123`
- Body (raw JSON):
  ```json
  {
    "nombre": "Nuevo Cliente",
    "email": "nuevo@example.com"
  }
  ```
- Click **Send** → Resultado: 403 Forbidden ✅ (Esperado)

---

## ⚠️ ERRORES COMUNES

### Error 1: 401 Unauthorized
```json
{
  "timestamp": "2025-01-19T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized"
}
```
**Causa**: No incluiste el header `Authorization` o está mal formado
**Solución**: Asegúrate de enviar `Authorization: Basic <base64>`

### Error 2: 403 Forbidden
```json
{
  "timestamp": "2025-01-19T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden"
}
```
**Causa**: El usuario no tiene permisos para esa acción
**Solución**: Usa `admin` para POST, PUT, DELETE o usa `user` solo para GET

### Error 3: 400 Bad Credentials
```json
{
  "timestamp": "2025-01-19T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Credentials"
}
```
**Causa**: Usuario o contraseña incorrectos
**Solución**: Verifica que uses `admin:admin123` o `user:user123`

---

## 📊 MATRIZ DE PERMISOS

```
┌─────────────┬────────┬────────┐
│   Acción    │ ADMIN  │  USER  │
├─────────────┼────────┼────────┤
│ GET         │   ✅   │   ✅   │
│ POST        │   ✅   │   ❌   │
│ PUT         │   ✅   │   ❌   │
│ DELETE      │   ✅   │   ❌   │
│ PATCH       │   ✅   │   ❌   │
└─────────────┴────────┴────────┘
```

---

**Última actualización**: Enero 19, 2025
**Versión**: 1.0
**Estado**: ✅ Listo para pruebas
