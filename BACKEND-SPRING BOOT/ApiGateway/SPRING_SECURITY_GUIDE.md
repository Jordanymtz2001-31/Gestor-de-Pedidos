# 📋 GUÍA DE SPRING SECURITY - Autenticación por Roles

## 🔐 Resumen
Se ha implementado **Spring Security con autenticación Basic** en todos los microservicios. Cada usuario tiene un rol que determina qué endpoints puede acceder.

---

## 👥 USUARIOS PREDEFINIDOS

### Usuario 1: ADMIN
- **Usuario**: `admin`
- **Contraseña**: `admin123`
- **Rol**: `ADMIN`
- **Permisos**: 
  - ✅ Consultar (GET)
  - ✅ Crear (POST)
  - ✅ Modificar (PUT)
  - ✅ Eliminar (DELETE)

### Usuario 2: USER
- **Usuario**: `user`
- **Contraseña**: `user123`
- **Rol**: `USER`
- **Permisos**: 
  - ✅ Solo Consultar (GET)
  - ❌ No puede Crear, Modificar ni Eliminar

---

## 🛡️ REGLAS DE SEGURIDAD POR MICROSERVICIO

### API Gateway (Puerto 9000)
```
GET    /cliente/**        → USER, ADMIN
GET    /producto/**       → USER, ADMIN
GET    /pedido/**         → USER, ADMIN
GET    /detalle/**        → USER, ADMIN

POST   /cliente/**        → Solo ADMIN
PUT    /cliente/**        → Solo ADMIN
DELETE /cliente/**        → Solo ADMIN

POST   /producto/**       → Solo ADMIN
PUT    /producto/**       → Solo ADMIN
DELETE /producto/**       → Solo ADMIN

POST   /pedido/**         → Solo ADMIN
PUT    /pedido/**         → Solo ADMIN
DELETE /pedido/**         → Solo ADMIN

POST   /detalle/**        → Solo ADMIN
PUT    /detalle/**        → Solo ADMIN
DELETE /detalle/**        → Solo ADMIN
```

### Microservicio Pedido (Puerto 8001)
```
GET    /pedido/**         → USER, ADMIN
GET    /detalle/**        → USER, ADMIN

POST   /pedido/**         → Solo ADMIN
PUT    /pedido/**         → Solo ADMIN
DELETE /pedido/**         → Solo ADMIN

POST   /detalle/**        → Solo ADMIN
PUT    /detalle/**        → Solo ADMIN
DELETE /detalle/**        → Solo ADMIN
```

### Microservicio Cliente (Puerto 8002)
```
GET    /cliente/**        → USER, ADMIN

POST   /cliente/**        → Solo ADMIN
PUT    /cliente/**        → Solo ADMIN
DELETE /cliente/**        → Solo ADMIN
```

### Microservicio Producto (Puerto 8003)
```
GET    /producto/**       → USER, ADMIN

POST   /producto/**       → Solo ADMIN
PUT    /producto/**       → Solo ADMIN
DELETE /producto/**       → Solo ADMIN
```

---

## 🧪 PRUEBAS CON POSTMAN O CURL

### 1️⃣ GET (Lectura) - Funciona para ambos roles

```bash
# Con ADMIN
curl -X GET "http://localhost:9000/cliente/1" \
  -H "Authorization: Basic $(echo -n 'admin:admin123' | base64)"

# Con USER
curl -X GET "http://localhost:9000/cliente/1" \
  -H "Authorization: Basic $(echo -n 'user:user123' | base64)"
```

**Resultado**: ✅ Ambos funcionan

---

### 2️⃣ POST (Crear) - Solo funciona para ADMIN

```bash
# Con ADMIN
curl -X POST "http://localhost:9000/cliente" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'admin:admin123' | base64)" \
  -d '{"nombre":"Nuevo Cliente","email":"cliente@example.com"}'

# Con USER
curl -X POST "http://localhost:9000/cliente" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'user:user123' | base64)" \
  -d '{"nombre":"Nuevo Cliente","email":"cliente@example.com"}'
```

**Resultado**: ✅ ADMIN = OK | ❌ USER = 403 Forbidden

---

### 3️⃣ PUT (Modificar) - Solo funciona para ADMIN

```bash
curl -X PUT "http://localhost:9000/cliente/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'admin:admin123' | base64)" \
  -d '{"nombre":"Cliente Actualizado"}'
```

---

### 4️⃣ DELETE (Eliminar) - Solo funciona para ADMIN

```bash
curl -X DELETE "http://localhost:9000/cliente/1" \
  -H "Authorization: Basic $(echo -n 'admin:admin123' | base64)"
```

---

## 🔄 CÓMO FUNCIONA LA AUTENTICACIÓN BASIC

### Formato
```
Authorization: Basic <base64(usuario:contraseña)>
```

### Ejemplo con Admin
```
usuario: admin
contraseña: admin123
concatenado: admin:admin123
base64: YWRtaW46YWRtaW4xMjM=

Header Final:
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### En Postman
1. Ir a la pestaña **Authorization**
2. Seleccionar **Basic Auth**
3. Ingresar:
   - Username: `admin` (o `user`)
   - Password: `admin123` (o `user123`)
4. ✅ Postman genera automáticamente el header

---

## 📝 RESPUESTAS DE ERROR

### 🔴 401 Unauthorized (Sin autenticación)
```json
{
  "error": "Unauthorized",
  "message": "No se proporcióron credenciales"
}
```
**Causa**: No enviaste el header `Authorization`

### 🔴 403 Forbidden (Sin permisos)
```json
{
  "error": "Forbidden",
  "message": "Acceso denegado - Se requiere rol ADMIN"
}
```
**Causa**: El usuario no tiene el rol requerido para esta acción

### 🔴 400 Bad Request (Credenciales inválidas)
```json
{
  "error": "Bad Credentials"
}
```
**Causa**: Usuario o contraseña incorrectos

---

## 🔍 VERIFICAR QUIÉN ERES (Solo autenticado)

```bash
curl -X GET "http://localhost:9000/auth/me" \
  -H "Authorization: Basic $(echo -n 'admin:admin123' | base64)"
```

**Respuesta**:
```json
{
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

---

## 📁 ARCHIVOS MODIFICADOS

### ApiGateway
- ✅ `SecurityConfig.java` - Configuración de seguridad
- ✅ `AuthenticationConfig.java` - Gestión de autenticación
- ✅ `AuthController.java` - Endpoints de autenticación
- ✅ `application.properties` - Propiedades del gateway
- ✅ `pom.xml` - Dependencias

### Pedido
- ✅ `SecurityConfig.java` - Configuración de seguridad
- ✅ `pom.xml` - Dependencias

### Cliente
- ✅ `SecurityConfig.java` - Configuración de seguridad
- ✅ `pom.xml` - Dependencias

### Producto
- ✅ `SecurityConfig.java` - Configuración de seguridad
- ✅ `pom.xml` - Dependencias

---

## ⚙️ FUTUROS MEJORAMIENTOS

1. **JWT Tokens**: Cambiar de Basic Auth a JWT para mayor seguridad
2. **Base de Datos**: Almacenar usuarios en BD en lugar de en memoria
3. **OAuth2**: Implementar OAuth2 para autenticación con terceros
4. **LDAP**: Integrar LDAP para autenticación empresarial
5. **Auditoría**: Registrar todas las acciones de usuarios
6. **2FA**: Implementar autenticación de dos factores

---

## 💡 EJEMPLO COMPLETO DE FLUJO

```
1. Usuario intenta acceder a GET /cliente/1 con USER
   ↓
2. Spring Security intercepta la solicitud
   ↓
3. Valida las credenciales (user:user123)
   ↓
4. Verifica el rol (USER tiene permiso para GET)
   ↓
5. ✅ Solicitud autorizada → Se ejecuta el endpoint
   ↓
6. Devuelve los datos del cliente

---

1. Usuario intenta acceder a POST /cliente con USER
   ↓
2. Spring Security intercepta la solicitud
   ↓
3. Valida las credenciales (user:user123)
   ↓
4. Verifica el rol (USER NO tiene permiso para POST)
   ↓
5. ❌ Solicitud rechazada → Error 403 Forbidden
   ↓
6. Devuelve mensaje de acceso denegado
```

---

## 🚀 PRÓXIMOS PASOS

1. **Probar** con Postman/Insomnia todos los endpoints
2. **Documentar** nuevos usuarios si los agregas
3. **Monitorear** logs de Spring Security
4. **Implementar** auditoría de accesos
5. **Migrar** a JWT cuando estés listo

---

**Creado**: Enero 2025
**Versión**: 1.0 - Basic Auth
**Estado**: ✅ Implementado y listo para pruebas
