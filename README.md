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

### **Autenticacion**
#### **POST /auth**
- **Descripción:** Autentica un usuario y devuelve un token JWT.
- **Configuración en Postman:**
1. Seleccionar método **POST**.
2. Ingresar la URL: `http://localhost:8080/auth/login`.
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

#### **POST /register**  
- **Descripcion:** Registra un usuario y devuelve un token JWT.
- **Configuracion en Postman:**
1. Seleccionar método **POST**.
2. Ingresar la URL: `http://localhost:8080/auth/register`.
3. En la pestaña **Headers**, agregar:
- **Key:** `Contenet-Type`
- **Value:** `application/json`
4. En la pestaña **Body**, seleccionar **raw** y colocar el siguiente JSON:
    ```json
        {
            "nombre": "Juan",
            "email": "juan@mail.com",
            "password": "123passwrd"
        }
    ```

### **Usuarios**
## ** GET /usuario/{email}**
- **Descripción:** Obtiene la informacion del perfil de un usuario mediante su email.
- **Configuración en Postman:**
1. Seleccionar el método **GET**.
2. Ingresar la URL: `http://localhost:8080/usuario/info/email`.
3. Reemplazar `email` por el email del usuario.
4. Hacer click en **Send**
- **Respuesta esperada**
    ```json
        {
            "id": 1,
            "nombre": "Juan",
            "email": "juan@mail.com",
            "password": "$2a$10$de66NB4lFMu.RRRe6IQroOynU2L7H7hmAd6o36Km46/udiSL/gELa",
            "habilitado": true
        }
    ```

### **Productos**
#### **GET /productos**
- **Descripción:** Obtiene una lista con los productos disponibles.
- **Configuración en Postman:**
1. Seleccionar el método **GET**.
2. Ingresar la URL: `http://localhost:8080/productos`.
3. Hacer click en **Send**
- **Respuesta esperada**
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

#### **GET /productos/search**
- **Descripción:** Obtiene una lista con los productos disponibles en base a los filtros de palabras clave y precios.
- **Configuración en Postman:**
1. Seleccionar el método **GET**.
2. Ingresar la URL: `http://localhost:8080/search`.
3. En la pestaña **Params** agregar:
3. En la pestaña **Params** agregar:
    - `palabraClave` que es la palabra clave por la cual vamos a buscar algun producto
    - `precioMin` precio minimo del producto
    - `precioMax` precio maximo del producto
4. Hacer click en **Send**

#### **POST /productos**
- **Descripción:** Crea un nuevo producto.
- **Configuración en Postman:**
1. Seleccionar el método **POST**.
2. Ingresar la URL: `http://localhost:8080/productos`.
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

### **Pedidos**
#### **POST /pedidos**
- **Descripción:** Crea un nuevo pedido con una lista de productos.
- **Configuración en Postman:**
1. Seleccionar el método **POST**.
2. Ingresar la URL: `http://localhost:8080/pedidos`.
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

#### **GET /pedidos/{id}/detalle**
- **Descripción:** Obtiene el detalle de un pedido.
- **Configuración en Postman:**
1. Seleccionar el método **GET**.
2. Ingresar la URL: `http://localhost:8080/id/pedidos`.
3. Reemplazamos `id` de la url por el id del pedido del cual queremos conocer su detalle.
4. Hacer click en **Send**.

- **Respuesta esperada (200 OK):**
    ```json
        {
            "username": "juan@mail.com",
            "productos": [
                {
                    "nombreProducto": "Producto A",
                    "cantidad": 1
                }
            ],
            "precioTotal": 100.0,
            "fechaDeCreacion": "2024-12-30T04:28:34.8578463"
        }
    ```