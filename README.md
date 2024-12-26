# Proyecto: Java-Semisenior-Commerce

## Descripción

> Este proyecto es una API REST para gestionar productos. Permite realizar operaciones CRUD sobre productos, gestionar usuarios y realizar autenticación segura.
> La clase UsuarioModel representa un Usuario con nombre, emial y contraseña
> La clase ProductoModel representa un producto con nombre, descripcion, precio y cantidad (stock) disponible
> La clase PedidoModel representa la solicitud que puede hacer un usuario para adquirir productos, puede ser un solo tipo de producto o varios
> La clase DetalleModel representa el conjunto de productos de un mismo tipo, que están contenidas en un PedidoModel
---

## Instrucciones para ejecutar el proyecto

### Configuración

El proyecto requiere un archivo `application.properties` para configurar el acceso a la base de datos y otras propiedades del entorno. Es necesario crear el archivo en la carpeta `src/main/resources` antes de ejecutar el proyecto.

Usar el archivo de ejemplo `application.properties.example` incluido en el repositorio.

#### Ejemplo de configuración:
```properties
# Nombre de la aplicación
spring.application.name=Java-Semisenior-Commerce

# Configuración de la base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/tu_base_datos
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuración de JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Swagger/OpenAPI
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui
```

## Configuración de la Clave JWT

La clave secreta utilizada para firmar los tokens JWT tiene que ser configurada en el archivo `application.properties`:

1. Abrir el archivo `src/main/resources/application.properties`.
2. Agregar la siguiente línea, reemplazando `clave` con tu propia clave:

   ```properties
   # Clave para JWT
   jwt.secret.key=clave
    ```

### Prerrequisitos
- **Java 17** o superior instalado.
- **Maven** (o Gradle) instalado.
- Base de datos PostgreSQL.

## Inicializacion de la Base de Datos
> Se incluye un SQL para configurar la base de datos y cargar datos iniciales, disponible en src/main/resources/sql/init.sql. Contiene el script de creacion de las tablas para usuarios, productos, pedidos y detalles, y la insercion de datos iniciales de productos para realizar pruebas.

### Pasos
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/ulidecima/Java-Semisenior-Commerce.git
   cd Java-Semisenior-Commerce
   ```
2. Compilar el proyecto:
    ```bash
   mvn clean install
   ```
3. Configurar el archivo application.properties
   + Crear la base de datos.
   + Configurar las credenciales y el acceso a la base de datos.
4. Ejecutar la aplicacion
    ```bash
   mvn spring-boot:run
   ```
5. Acceder a la API en
    + http://localhost:8080
    + La documentación de Swagger se encuentra en: http://localhost:8080/swagger-ui

## Endpoints

A continuación, se detallan los principales endpoints de la API, junto con ejemplos de uso en **Postman**:

- **Descripción:** Autentica un usuario y devuelve un token JWT.
- **Configuración en Postman:**
    1. Seleccionar método **POST**.
    2. Ingresar la URL: `http://localhost:8080/api/auth/login`.
    3. En la pestaña **Headers**, agregar:
        - **Key:** `Content-Type`
        - **Value:** `application/json`
    4. En la pestaña **Body**, seleccionar **raw** y colocar el siguiente JSON:
       ```json
       {
         "email": "juan@email.com",
         "password": "psswrd"
       }
       ```
    5. Hacer click en **Send**.

- **Respuesta esperada (200 OK):**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "message": "Usuario Autenticado"
  }
  ```
- **Respuesta esperada (401 Unauthorized):**
  ```json
  {
    "message": "Credenciales invalidas.",
    "success": false
  }
    ```

### **Productos**
#### **GET /api/productos**
- **Descripción:** Obtiene una lista con los productos disponibles.
- **Configuración en Postman:**
    1. Seleccionar el método **GET**.
    2. Ingresar la URL: `http://localhost:8080/api/productos`.
    3. Hacer click en **Send**
- **Respuesta esperada (401 Unauthorized):**
  ```json
    [
      {
        "id": 1,
        "nombre": "Producto A",
        "descripcion": "Descripción del producto A",
        "precio": 100.0,
        "cantidad": 50
      },
      {
        "id": 2,
        "nombre": "Producto B",
        "descripcion": "Descripción del producto B",
        "precio": 150.0,
        "cantidad": 30
      }
    ]
    ```

#### **POST /api/productos**
- **Descripción:** Crea un nuevo producto.
- **Configuración en Postman:**
    1. Seleccionar el método **POST**.
    2. Ingresar la URL: `http://localhost:8080/api/productos`.
    3. En la pestaña **Headers**, agregar:
        - **Key:** `Content-Type`
        - **Value:** `application/json`
    4. En la pestaña **Body**, seleccionar **raw** y colocar el siguiente JSON:
       ```json 
       {
       "nombre": "Producto C",
       "descripcion": "Descripción del producto C",
       "precio": 200.0,
       "cantidad": 10
       }
       ```
    5. Hacer click en **Send**.

- **Respuesta esperada (201 Createsd):**
  ```json
  {
  "id": 3,
  "nombre": "Producto C",
  "descripcion": "Descripción del producto C",
  "precio": 200.0,
  "cantidad": 10
  }
  ```

#### **POST /api/pedidos**
- **Descripción:** Crea un nuevo pedido con una lista de productos.
- **Configuración en Postman:**
    1. Seleccionar el método **POST**.
    2. Ingresar la URL: `http://localhost:8080/api/pedidos`.
    3. En la pestaña **Headers**, agregar:
        - **Key:** `Content-Type`
        - **Value:** `application/json`
    4. En la pestaña **Body**, seleccionar **raw** y colocar el siguiente JSON:
       ```json
       {
       "usuarioId": 1,
       "productos": [
          { "productoId": 1, "cantidad": 2 },
          { "productoId": 2, "cantidad": 1 }
         ]
       }
       ```
    5. Hacer click en **Send**.

- **Respuesta esperada (201 Createsd):**
  ```json
  {
  "message": "Pedido creado exitosamente",
  "pedidoId": 123
  }

  ```