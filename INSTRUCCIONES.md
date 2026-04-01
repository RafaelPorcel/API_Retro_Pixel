# 🎮 Proyecto: API REST "RetroPixel"

## 👨‍💼 1. El requerimiento del Cliente (Marcos, dueño)

*"¡Hola! Mira, acabo de abrir **RetroPixel**, una tienda friki en el centro donde vendemos consolas retro,
videojuegos y accesorios. Hasta ahora lo llevo todo apuntado en una libreta y un Excel, pero esto es un caos.
Necesito un programa, una API o como lo llaméis, que haga lo siguiente:*

* *Quiero poder registrar mis **categorías** (ej: Consolas, Juegos, Accesorios) y mis **artículos** con su nombre, precio, 
  * la cantidad que tengo en la trastienda (stock) y saber a qué categoría pertenecen.*
* *Necesito poder registrar los **pedidos** que me hacen los clientes. Si alguien me pide 2 juegos y 1 mando, el
  sistema tiene que restar eso de mi stock automáticamente. ¡Y no me puede dejar vender algo que no tengo!*
* *Para atraer clientes, tengo dos **promociones** en mente que el sistema debe calcular solo:*
  1. *Si un cliente compra **más de 3 videojuegos** en el mismo pedido, le hacemos un **15% de descuento** en toda su compra.*
  2. *Si el cliente se lleva **al menos 1 consola**, le descontamos **20€ fijos** del total del ticket (es como si le 
     regaláramos los gastos de envío y gestión).*
* *Ah, y por favor, cuando el sistema me muestre el ticket de venta o la lista de artículos al público,
  no quiero que se vea cuánto stock me queda, ¡es información confidencial de mi negocio!"*

---

## 👩‍💻 2. Consigna Técnica (Ticket de Jira)

**Título:** Implementar API REST para gestión de inventario y ventas con JPA/MySQL (RetroPixel)  
**Asignado a:** Desarrollador Backend  
**Prioridad:** Alta  
**Stack:** Spring Boot, Java, Lombok, Spring Data JPA, MySQL

### Especificaciones por capas:

#### 🔌 Dependencias y Configuración (POM y Properties)
* **pom.xml:** Debes agregar estas dos dependencias dentro de `<dependencies>` para conectar con la base de datos:
  1. `spring-boot-starter-data-jpa` (Para Hibernate y repositorios).
  2. `mysql-connector-j` (El driver para conectar con XAMPP).
* **application.properties:** Configura la conexión a tu MySQL local de XAMPP (puerto 3306 por defecto) añadiendo:
  * `spring.datasource.url=jdbc:mysql://localhost:3306/retropixel_db?createDatabaseIfNotExist=true`
  * `spring.datasource.username=root`
  * `spring.datasource.password=` (déjalo vacío si XAMPP no tiene contraseña).
  * `spring.jpa.hibernate.ddl-auto=update` (para que Spring cree las tablas automáticamente).

#### 📦 Capa Model (Entidades de Base de Datos y Relaciones)
Crearemos exactamente 3 entidades.

**1. Clase `Categoria`**
* Anótala con `@Entity` y `@Table(name = "categorias")`.
* **Primary Key:** `Long id` anotado con `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
* Atributo: `String nombre`.
* **Relación:** `List<Articulo> articulos` anotado con `@OneToMany(mappedBy = "categoria")`. *(Una categoría tiene muchos artículos).*

**2. Clase `Articulo`**
* Anótala con `@Entity` y `@Table(name = "articulos")`.
* **Primary Key:** `Long id` anotado con `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
* Atributos normales: `String titulo`, `Double precio`, `Integer stock`.
* **Relación con Categoría:** `Categoria categoria` anotado con `@ManyToOne` y `@JoinColumn(name = "categoria_id")`. 
  * *(Muchos artículos pertenecen a una categoría).*

**3. Clase `Pedido`**
* Anótala con `@Entity` y `@Table(name = "pedidos")`.
* **Primary Key:** `Long id` anotado con `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
* Atributos normales: `LocalDateTime fecha`, `Double total`.
  * **Relación con Artículo:** `List<Articulo> articulosComprados` anotado con `@ManyToMany`. Puedes usar 
    `@JoinTable(name = "pedido_articulo")` para nombrar la tabla intermedia que Spring creará automáticamente en MySQL. 
    *(Un pedido tiene muchos artículos).*

#### 📨 Capa DTO (Transferencia de Datos)
* **`CategoriaDto`**: Debe exponer `id` y `nombre`.
* **`ArticuloDto`**: Solo debe exponer `id`, `titulo`, `precio` y un objeto `CategoriaDto categoria`. (Ocultamos el stock).
* **`PedidoDto`**: Debe exponer `id`, `fecha`, `List<ArticuloDto> articulosComprados` y el `total`.
* **`CrearPedidoDto`**: Debe contener un `Map<Long, Integer> lineasPedido`. La clave (`Long`) será el ID del artículo, 
  y el valor (`Integer`) será la cantidad que el cliente quiere comprar.

#### 🗄️ Capa Repository (Spring Data JPA)
Crea 3 interfaces que extiendan de `JpaRepository` (Spring se encarga de la magia):
* `CategoriaRepository extends JpaRepository<Categoria, Long>`
* `ArticuloRepository extends JpaRepository<Articulo, Long>`
* `PedidoRepository extends JpaRepository<Pedido, Long>`

#### ⚠️ Capa Exception (Manejo de Errores)
* Crea dos excepciones personalizadas (heredando de `RuntimeException`): `ArticuloNoEncontradoException` y `StockInsuficienteException`.
* Crea un `GlobalExceptionHandler` con `@ControllerAdvice` para capturar estas excepciones y devolver un JSON limpio 
  con el mensaje de error y un código HTTP 404 (Not Found) o 400 (Bad Request).

#### ⚙️ Capa Service (Lógica de Negocio y Mapeo)
* Crea `RetroPixelService` e inyecta (`@Autowired`) los 3 repositorios (`CategoriaRepository`, `ArticuloRepository` y `PedidoRepository`).
* **Responsabilidad de Mapeo:** Esta capa transformará las entidades traídas de la base de datos en sus respectivos 
  DTOs antes de devolverlos al Controller.
* **Métodos CRUD:** Implementa listar y crear categorías y artículos usando `.findAll()` y `.save()` de los repositorios.
* **Método `registrarPedido(CrearPedidoDto dto)`:**
  1. Crea una lista vacía `List<Articulo> articulosParaPedido = new ArrayList<>()`.
  2. Recorre el `Map` que llega en el DTO (ID Artículo -> Cantidad).
  3. Busca cada artículo por ID usando `articuloRepository.findById(id)`. Si no existe -> Lanza `ArticuloNoEncontradoException`.
  4. Verifica el stock. Si la cantidad solicitada es mayor al stock -> Lanza `StockInsuficienteException`.
  5. Resta el stock y actualiza el artículo en la base de datos con `articuloRepository.save(articulo)`.
  6. Añade el artículo a la lista `articulosParaPedido` tantas veces como cantidad se haya solicitado (para simular la 
     cantidad en la relación `@ManyToMany`).
  7. Ve sumando el subtotal y cuenta cuántos artículos pertenecen a la categoría "JUEGO" y cuántos a "CONSOLA" 
     (usando el nombre de la categoría del artículo).
  8. **Aplica las reglas de negocio:**
  * Si `cantidadJuegos > 3` -> `total = total * 0.85` (15% descuento).
  * Si `cantidadConsolas >= 1` -> `total = total - 20.0`. *(Validación extra: asegúrate de que el total no sea menor a 0€).*
  9. Guarda el `Pedido` en la BD con la lista de artículos, mapéalo a `PedidoDto` y devuélvelo.

#### 🌐 Capa Controller (Endpoints REST)
* Crea `RetroPixelController` con `@RestController` y mapeado a `/api`.
* El Controller **solo** debe interactuar con el Service, recibiendo y devolviendo DTOs.
* Endpoints requeridos:
  * `GET /api/categorias` -> Devuelve `List<CategoriaDto>`.
  * `POST /api/categorias` -> Recibe un `Categoria` y devuelve `CategoriaDto`.
  * `GET /api/articulos` -> Devuelve `List<ArticuloDto>`.
  * `POST /api/articulos` -> Recibe un `Articulo` y devuelve `ArticuloDto`.
  * `POST /api/pedidos` -> Recibe `CrearPedidoDto` y devuelve `PedidoDto` (con HTTP 201 Created).
  * `GET /api/pedidos/{id}` -> Devuelve `PedidoDto`.