# Digital Money House (DMH) - Backend

## 📖 Descripción
DMH es una aplicación financiera diseñada para permitir la gestión de cuentas bancarias, transferencias de dinero, y monitoreo de actividades, implementando una arquitectura de microservicios para garantizar escalabilidad, seguridad y eficiencia.

---

## 📋 Características Principales
### Usuarios (Users Service)
- **Registro de usuarios**: Creación de cuentas con generación automática de CVU y alias.
- **Inicio de sesión**: Autenticación con generación de tokens JWT.
- **Cierre de sesión**: Invalidación de tokens.
- **Gestión de perfiles**: Visualización y actualización de información personal.

### Tarjetas (Cards Service)
- **Gestión de tarjetas**: CRUD completo para tarjetas asociadas a cuentas.

### Transacciones (Transactions Service)
- **Transferencias**: Transferencia de dinero entre cuentas utilizando CVU.
- **Actividades**: Registro y consulta de todas las actividades de una cuenta.
- **Saldo**: Cálculo en tiempo real del saldo disponible.

### API Gateway
- Enrutamiento centralizado de solicitudes.
- Validación de tokens JWT.

### Config Server
- Centralización de configuraciones.

### Eureka Server
- Descubrimiento y registro de microservicios.

---

## 🛠 Arquitectura del Sistema

### Diagrama de Componentes
A continuación se muestra el flujo de comunicación entre los diferentes microservicios:

![Flujo de Certificación](https://github.com/Jfgazonb20/DigitalMoneyHouseCol/blob/main/Pruebas_y_Flujo/FlujoDMH-Certifiación.png?raw=true)

## 📁 Modelo de Base de Datos

### Diagrama Entidad-Relación (ERD)
El diseño de la base de datos asegura integridad y escalabilidad.

![Diagrama ERD](https://github.com/Jfgazonb20/DigitalMoneyHouseCol/blob/main/Pruebas_y_Flujo/FlujoDMH-Certifiación.png?raw=true)
**Tablas Principales:**
1. **Users**: Información de los usuarios.
2. **Accounts**: Gestión de cuentas bancarias.
3. **Cards**: Administración de tarjetas.
4. **Activities**: Registro de transferencias e ingresos.

---

## 🛠️ Microservicios Implementados

### Users Service
#### Endpoints Principales:
- `POST /api/register` - Registro de usuarios.
- `POST /api/login` - Inicio de sesión y generación de JWT.
- `POST /api/logout` - Invalidación de tokens.

#### Seguridad:
- Autenticación mediante JWT.
- Validación de roles.

---

### Cards Service
#### Endpoints Principales:
- `GET /api/accounts/{accountId}/cards` - Obtener todas las tarjetas.
- `POST /api/accounts/{accountId}/cards` - Registrar una nueva tarjeta.

#### Validaciones:
- Evitar duplicados de tarjetas.
- Manejo de errores (404, 409, 400).

---

### Transactions Service
#### Endpoints Principales:
- `POST /api/accounts/{id}/transferences` - Registrar una transferencia.
- `GET /api/accounts/{id}/activity` - Consultar actividades de una cuenta.
- `GET /api/accounts/{id}/balance` - Consultar saldo.

#### Validaciones:
- Verificación de saldo suficiente para transferencias.
- Validación de CVU destino.

---

### API Gateway
#### Funcionalidad:
- Enrutamiento de solicitudes hacia los servicios de **Users**, **Cards** y **Transactions**.
- Validación de autenticación mediante JWT.

#### Rutas Configuradas:
- `/api/users/**`
- `/api/accounts/**`

---

### Config Server
#### Funcionalidad:
- Centralización de configuraciones para todos los microservicios.
- Configuración en un repositorio remoto de GitHub.

---

### Eureka Server
#### Funcionalidad:
- Descubrimiento y registro de servicios:
  - Users Service
  - Cards Service
  - Transactions Service
  - API Gateway
  - Config Server

---

## 🚀 Pruebas Pendientes
### Unitarias
- Controladores y servicios para los microservicios.

### De Integración
- Flujo completo entre microservicios.

---

## 📊 Documentación Pendiente
- Diagrama de Secuencia para flujos como transferencias y registro de actividades.
- Ejemplos detallados de solicitudes y respuestas para todos los endpoints.

---

## 📝 Cómo Ejecutar el Proyecto
1. Clonar el repositorio.
   ```bash
   git clone https://github.com/tu-usuario/tu-repositorio.git
   ```
2. Configurar las propiedades del servidor Config.
3. Ejecutar los microservicios en el siguiente orden:
   - Config Server
   - Eureka Server
   - API Gateway
   - Users Service, Cards Service, Transactions Service.
4. Acceder a la aplicación a través del API Gateway.

---

## ✨ Contribuciones
Si deseas contribuir, crea un pull request con tus cambios. Todos los aportes son bienvenidos.

---

## 🛡️ Licencia
Este proyecto está bajo la Licencia MIT. Puedes consultar más detalles en el archivo LICENSE del repositorio.
