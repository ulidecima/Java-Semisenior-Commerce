
# Java-Semisenior-Commerce

## Descripcion
Este proyecto es una API REST para gestionar productos. Permite realizar operaciones CRUD sobre productos, gestionar usuarios y realizar autenticación segura.  
La clase UsuarioModel representa un Usuario con nombre, emial y contraseña.  
La clase ProductoModel representa un producto con nombre, descripcion, precio y cantidad (stock) disponible.  
La clase PedidoModel representa la solicitud que puede hacer un usuario para adquirir productos, puede ser un solo tipo de producto o varios.  
La clase DetalleModel representa el conjunto de productos de un mismo tipo, que están contenidas en un PedidoModel.

## Prerrequisitos para ejecutar el proyecto

- **Java 17** o superior instalado.
- **Maven** instalado.
- Base de datos **PostgreSQL**.
- Git
- Clonar el proyecto:
```bash
  git clone https://github.com/ulidecima/Java-Semisenior-Commerce
```

### Configuracion
Antes de ejecutar el proyecto, configurar el archivo `application.properties` ubicado en `src/main/resources`. El archivo `application.properties.example` sirve de ejemplo, esta incluido en el repositorio.  
En el proyecto, puede encontrarse el script de creacion de la base de datos `init.sql` ubicado en `src/main/resources`. Tambien contiene inserciones de datos de ejemplo para empezar a interactuar con la aplicacion.

#### Pasos para configurar:
1. **Base de Datos:** Reemplazar `tu_base_datos`, `tu_usuario` y `tu_contraseña` con los valores correspondientes a la base de datos PostgreSQL.
2. **Clave JWT:** Sustituir `clave` por una clave segura para firmar los tokens JWT.

#### Ejemplo de configuracion del archivo application.properties
```properties
    # Nombre de la aplicación
    spring.application name=Java-Semisenior-Commerce
    
    # Configuración de la base de datos
    spring.datasource.url=jdbc:postgresql://localhost:5432/tu_base_datos
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    spring.datasource.driver-class-name=org.postgresql.Driver
    
    # Configuración de JPA
    spring.jpa.hibernate.ddl-auto=create-drop
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

    # Clave para JWT
    jwt.secret.key=clave
    
    # Swagger/OpenAPI
    springdoc.swagger-ui.enabled=true
    springdoc.swagger-ui.path=/swagger-ui
```

## Ejecutar la aplicacion

Moverse a la carpeta del proyecto
```bash
  cd Java-Semisenior-Commerce
```

Construir el proyecto
```bash
  mvn clean install
```

Iniciar la aplicacion
```bash
  mvn spring-boot:run
```

## Ejecutar los tests

El proyecto cuenta con una serie de tests que prueban el correcto funcionamiento de la capa de servicio (logica de negocio).

```bash
  mvn test
```
## Documentacion

- La interfaz de documentación de la API se encuentra aqui: `localhost:8080/swagger-ui`
- La documentación en formato JSON se encuentra aqui: `localhost:8080/api-docs`
## API Reference
Todos los endpoints se encuentran en la siguiente url: `http://localhost:8080/`

Todos los endpoints (salvo los de Auth Endpoint) necesitan el sigueinte header:

| Header       | Type     | Description                 |
| :----------- | :------- | :-------------------------- |
| `Authorization` | `string` | **Required**. Bearer Token |

### Auth Endpoint
#### Logear un usuario

```http
  POST /auth
```

| Body         | Type     | Description                 |
| :----------- | :------- | :-------------------------- |
| `email`   | `String` | **Required**. Correo electronico.    |
| `password`  | `String` | **Required**. Contraseña.  |

Request de ejemplo:
```json
  {
    "email": "email@mail.com",
    "password": "password"
  }
```

#### Registrar un usuario

```http
  POST /register
```
| Body         | Type     | Description                 |
| :----------- | :------- | :-------------------------- |
| `nombre`     | `String` | **Required**. Nombre de usuario.     |
| `email`   | `String` | **Required**. Correo electronico.    |
| `password`  | `String` | **Required**. Contraseña.  |

Request de ejemplo:
```json
  {
    "nombre": "Nombre del usuario",
    "email": "email@mail.com",
    "password": "password"
  }
```

### Pedido Endpoint
#### Obtener un pedido

```http
  GET /pedido/{pedido_id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `pedido_id` | `Long` | **Required**. ID del pedido |

#### Obtener los productos de un pedido

```http
  GET /pedido/{pedido_id}/productos
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `pedido_id` | `Long` | **Required**. ID del pedido |

#### Obtener el detalle de un pedido

```http
  GET /pedido/{detalle_id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `detalle_id` | `Long` | **Required**. ID del detalle |

#### Crear un pedido

```http
  POST /pedido
```

| Body         | Type     | Description                 |
| :----------- | :------- | :-------------------------- |
| `username`   | `string` | **Required**. User email    |
| `detalles`   | `array`  | **Required**. Pedido details |

Request de ejemplo:
```json
  {
    "username": "example@mail.com",
    "detalles": [
      { "productoId": 1, "cantidad": 2 },
      { "productoId": 2, "cantidad": 3 }
    ]
  }
```

#### Eliminar un pedido

```http
  DELETE /pedido/{detalle_id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `detalle_id` | `Long` | **Required**. ID del pedido. |

#### Obtener todos los pedidos de un usuario

```http
  GET /pedido
```

| Query        | Type     | Description                 |
| :----------- | :------- | :-------------------------- |
| `usuario_email` | `string` | **Required**. Correo electronico del usuario. |

### Producto Endpoint
#### Obtener un producto

```http
  GET /producto/{producto_id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `producto_id` | `Long` | **Required**. ID del producto. |

#### Obtener todos los productos

```http
  GET /producto
```

| Body | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `page` | `Integer` | **Optional**. Numero de pagina. |
| `size` | `Integer` | **Optional**. Tamaño de muestra. |

#### Crear un producto

```http
  POST /producto/{producto_request}
```

| Body         | Type     | Description                 |
| :----------- | :------- | :-------------------------- |
| `nombre`     | `String` | **Required**. Product name  |
| `precio`     | `Double`  | **Required**. Product price |
| `stockDisponible` | `Integer` | **Required**. Stock count |

Request de ejemplo:
```json
  {
    "nombre": "Producto",
    "precio": 150.0,
    "stockDisponible": 30
  }
```

#### Actualizar un producto

```http
  PUT /producto/{producto_id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `producto_id` | `Long` | **Required**. ID del producto. |
| `producto_request` | `json` | **Required**. Datos del producto que se quiere crear. |

| Body         | Type     | Description                 |
| :----------- | :------- | :-------------------------- |
| `nombre`     | `String` | **Required**. Product name  |
| `precio`     | `Double`  | **Required**. Product price |
| `stockDisponible` | `Integer` | **Required**. Stock count |

Request de ejemplo:
```json
  {
    "nombre": "Producto actualizado",
    "precio": 1500.0,
    "stockDisponible": 50
  }
```

#### Buscar productos

```http
  GET /producto/search
```

| Query | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `palabra_clave` | `String` | **Optional**. Palabra clave para realizar la busqueda. |
| `precio_min` | `Double` | **Optional**. Precio minimo de productos. |
| `precio_max` | `Double` | **Optional**. Precio maximo de productos. |
| `page` | `Integer` | **Optional**. Numero de pagina. |
| `size` | `Integer` | **Optional**. Tamaño de muestra. |

#### Eliminar un producto

```http
  DELETE /producto/{producto_id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `producto_id` | `Long` | **Required**. ID del producto que se quiere eliminar. |

### Usuario Endpoint
#### Ver la informacion de un usuario

```http
  GET /usuario/info/{usuario_email}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `usuario_email` | `String` | **Required**. Correo electronico del usuario. |

#### Actualizar los datos de un usuario

```http
  PUT /usuario/{usuario_email}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `usuario_email` | `String` | **Required**. Correo electronico del usuario. |

| Body         | Type     | Description                 |
| :----------- | :------- | :-------------------------- |
| `nombre`     | `String` | **Required**. Nombre de usuario.     |
| `email`   | `String` | **Required**. Correo electronico.    |
| `password`  | `String` | **Required**. Contraseña.  |

Request de ejemplo:
```json
  {
    "nombre": "Nuevo Nombre",
    "email": "emailnuevo@mail.com",
    "password": "NuevoPassword"
  }
```

#### Eliminar un usuario

```http
  DELETE /usuario/{usuario_email}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `usuario_email` | `String` | **Required**. Correo electronico del usuario. |

