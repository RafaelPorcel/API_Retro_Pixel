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
Para una arquitectura profesional, se divide la lógica en tres servicios independientes:

**1. `CategoriaService`**
* Inyecta (`@Autowired`) el `CategoriaRepository`.
* Implementa métodos para listar (`findAll`) y crear (`save`) categorías mapeando a `CategoriaDto`.

**2. `ArticuloService`**
* Inyecta (`@Autowired`) el `ArticuloRepository`.
* Implementa métodos para listar y crear artículos.
* **Método `actualizarStock(Long id, Integer cantidad)`:** * Busca el artículo por ID. Si no existe -> lanza `ArticuloNoEncontradoException`.
  * Verifica si el stock es suficiente; si no -> lanza `StockInsuficienteException`.
  * Resta la cantidad al stock, guarda el cambio en la BD y devuelve la entidad actualizada.

**3. `PedidoService`**
* Inyecta (`@Autowired`) el `PedidoRepository` y el `ArticuloService`.
* **Método `registrarPedido(CrearPedidoDto dto)`:**
  1. Crea una lista vacía `List<Articulo> articulosParaPedido = new ArrayList<>()`.
  2. Recorre el `Map` del DTO (ID Artículo -> Cantidad).
  3. Por cada ID, llama a `articuloService.actualizarStock(id, cantidad)`.
  4. Añade el artículo devuelto a la lista `articulosParaPedido` tantas veces como la cantidad solicitada.
  5. Suma los precios para el subtotal y cuenta los artículos cuya categoría sea "JUEGO" o "CONSOLA".
  6. **Aplica las reglas de negocio:**
    * Si `cantidadJuegos > 3` -> `total = total * 0.85` (15% descuento).
    * Si `cantidadConsolas >= 1` -> `total = total - 20.0`. *(Validación extra: el total no puede ser menor a 0€).*
  7. Crea la entidad `Pedido` con la fecha actual, la lista de artículos y el total calculado.
  8. Guarda el `Pedido` en la BD, mapéalo a `PedidoDto` y devuélvelo.

#### 🌐 Capa Controller (Endpoints REST)
Para mantener la coherencia con la capa de servicio, dividiremos la entrada de la API en tres controladores especializados. 
Cada controlador debe interactuar **únicamente** con su servicio correspondiente y manejar exclusivamente DTOs.

**1. `CategoriaController`**
* Anótalo con `@RestController` y `@RequestMapping("/api/categorias")`.
* Inyecta (`@Autowired`) el `CategoriaService`.
* **Endpoints:**
  * `GET` -> Llama a `categoriaService.listarTodas()` y devuelve `List<CategoriaDto>`.
  * `POST` -> Recibe una entidad `Categoria`, llama a `categoriaService.crear()` y devuelve `CategoriaDto`.

**2. `ArticuloController`**
* Anótala con `@RestController` y `@RequestMapping("/api/articulos")`.
* Inyecta (`@Autowired`) el `ArticuloService`.
* **Endpoints:**
  * `GET` -> Llama a `articuloService.listarTodos()` y devuelve `List<ArticuloDto>`.
  * `POST` -> Recibe un `Articulo`, llama a `articuloService.crear()` y devuelve `ArticuloDto`.

**3. `PedidoController`**
* Anótala con `@RestController` y `@RequestMapping("/api/pedidos")`.
* Inyecta (`@Autowired`) el `PedidoService`.
* **Endpoints:**
  * `POST` -> Recibe un `CrearPedidoDto`. Debe devolver el `PedidoDto` generado con un código de estado **HTTP 201 Created** 
    (usando `@ResponseStatus(HttpStatus.CREATED)` o `ResponseEntity`).
  * `GET /{id}` -> Recibe el ID por path variable, llama a `pedidoService.buscarPorId()` y devuelve el `PedidoDto`.

> **Nota de Arquitectura:** Los controladores son la "cara" de tu aplicación. Su única función es recibir la petición, 
> pasarle la pelota al servicio y entregar la respuesta. No deben contener lógica de descuentos ni de validación de stock.