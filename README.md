#  RetroPixel REST API

RetroPixel es una API REST robusta desarrollada en **Java y Spring Boot** para la gestión integral de una tienda de 
videojuegos y cultura pop. Proporciona una interfaz segura y eficiente para la administración del inventario, 
categorización de productos y el procesamiento de ventas en tiempo real.

## Características Principales

* **Gestión de Inventario:** CRUD completo para artículos y control de stock en tiempo real.
* **Categorización:** Organización dinámica del catálogo mediante categorías relacionales.
* **Procesamiento de Pedidos:** Registro de ventas con cálculo automático de totales.
* **Motor de Promociones:** Aplicación automática de descuentos basados en el volumen y tipo de productos adquiridos.
* **Arquitectura Escalable:** Diseño basado en capas (Controller, Service, Repository) utilizando Spring Data JPA.

---

## Tecnologías y Stack

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.x (Spring Web)
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** MySQL
* **Herramientas:** Lombok, Maven

---

##  Requisitos e Instalación

### Prerrequisitos
Asegúrate de tener instalado en tu entorno local:
* [Java JDK 17](https://adoptium.net/) o superior.
* [Maven](https://maven.apache.org/).
* Servidor MySQL (por ejemplo, [XAMPP](https://www.apachefriends.org/es/index.html)).

### Configuración del Entorno

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/RafaelPorcel/API_Retro_Pixel





erDiagram
%% Relaciones
CATEGORIA ||--o{ ARTICULO : "1 a Muchos (mappedBy)"
PEDIDO }|--|{ ARTICULO : "Muchos a Muchos (@ManyToMany)"

    %% Entidades y Atributos
    CATEGORIA {
        Long id PK "🔑 Clave Primaria"
        String nombre "🏷️"
    }

    ARTICULO {
        Long id PK "🔑 Clave Primaria"
        String titulo "📝"
        Double precio "💰"
        Integer stock "📦"
        Long categoria_id FK "🔗 Clave Foránea (Dueño)"
    }

    PEDIDO {
        Long id PK "🔑 Clave Primaria"
        LocalDateTime fecha "📅"
        Double total "💶"
    }

    PEDIDO_ARTICULO {
        Long pedido_id FK "🔗 Puente Pedido"
        Long articulo_id FK "🔗 Puente Articulo"
    }