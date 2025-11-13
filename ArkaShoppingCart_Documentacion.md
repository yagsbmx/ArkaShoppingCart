
# 🛒 Arquitectura y Diseño del Microservicio de Carrito (ArkaShoppingCart)

**Proyecto:** ArkaShoppingCart  
**Tipo:** Microservicio Spring Boot  
**Arquitectura:** Hexagonal / Clean Architecture  
**Lenguaje:** Java  
**Build System:** Gradle  
**Autor:** Equipo ArkaShoppingCart  

---

## 🎯 Objetivo del Microservicio

El microservicio **ArkaShoppingCart** es el encargado de gestionar toda la **lógica del carrito de compras** dentro del ecosistema Arka.  
Su propósito principal es **mantener, sincronizar y procesar** los productos seleccionados por los usuarios antes de generar una orden.

Funciones principales:
- Crear y administrar carritos por usuario.  
- Agregar, eliminar o actualizar productos dentro del carrito.  
- Calcular subtotales y totales dinámicos.  
- Sincronizar información con el microservicio **ArkaProduct** (para stock y precios).  
- Enviar la información consolidada al microservicio **ArkaOrder** al momento del checkout.  

---

## 🧩 Arquitectura General

El diseño de **ArkaShoppingCart** sigue los principios de **Clean Architecture**, garantizando independencia entre capas y alta mantenibilidad.

```
┌──────────────────────────────────────────────┐
│                  Presentation                │
│          (Controllers / Web Layer)           │
├──────────────────────────────────────────────┤
│              Application Layer               │
│        (Use Cases / Business Logic)          │
├──────────────────────────────────────────────┤
│                Domain Layer                  │
│     (Entities / Models / Ports / Rules)      │
├──────────────────────────────────────────────┤
│             Infrastructure Layer             │
│ (Adapters / Persistence / External Clients)  │
└──────────────────────────────────────────────┘
```

Cada capa es independiente y se comunica solo con las internas, favoreciendo el **principio de inversión de dependencias**.

---

## 🧱 Estructura de Carpetas Principal

```
arkashoppingcart/
 ├── src/main/java/com/example/cart/
 │   ├── application/
 │   │   ├── service/
 │   │   │   ├── CartService.java
 │   │   │   └── CheckoutService.java
 │   │   └── usecase/
 │   │       ├── AddItemUseCase.java
 │   │       ├── RemoveItemUseCase.java
 │   │       ├── UpdateQuantityUseCase.java
 │   │       └── CheckoutUseCase.java
 │   ├── domain/
 │   │   ├── model/
 │   │   │   ├── Cart.java
 │   │   │   └── CartItem.java
 │   │   ├── enums/
 │   │   │   └── CartStatus.java
 │   │   └── ports/
 │   │       ├── in/
 │   │       │   ├── ManageCartPort.java
 │   │       │   └── CheckoutPort.java
 │   │       └── out/
 │   │           ├── ProductPort.java
 │   │           ├── UserPort.java
 │   │           ├── OrderPort.java
 │   │           └── CartRepositoryPort.java
 │   ├── infraestructure/
 │   │   ├── adapter/
 │   │   │   ├── web/controller/
 │   │   │   │   └── CartController.java
 │   │   │   ├── persistence/
 │   │   │   │   ├── entity/CartEntity.java
 │   │   │   │   └── repository/CartJpaRepository.java
 │   │   │   └── feign/
 │   │   │       ├── ProductFeignClient.java
 │   │   │       ├── OrderFeignClient.java
 │   │   │       └── UserFeignClient.java
 │   │   ├── dto/
 │   │   │   ├── CartRequestDto.java
 │   │   │   ├── CartResponseDto.java
 │   │   │   └── CartItemDto.java
 │   │   ├── mapper/
 │   │   │   └── CartMapper.java
 │   │   ├── config/
 │   │   │   ├── FeignConfig.java
 │   │   │   └── OpenApiConfig.java
 │   │   └── exception/
 │   │       └── CartExceptionHandler.java
 │   ├── config/
 │   │   └── SwaggerConfig.java
 │   └── CartApplication.java
 └── resources/
     ├── application.yml
     └── messages.properties
```

---

## 🧠 Capa Domain

📍 **Ubicación:** `com.example.cart.domain`

Encapsula la **lógica central del negocio** del carrito.

**Entidades principales:**
- `Cart` → representa el carrito del usuario.  
- `CartItem` → representa un producto dentro del carrito con cantidad y subtotal.  

**Enums:**
- `CartStatus`: `ACTIVE`, `CHECKED_OUT`, `CANCELLED`.  

**Puertos:**
- `in`: define casos de uso principales (agregar, eliminar, actualizar).  
- `out`: define interfaces externas para comunicación con Product, User y Order.  

Esta capa es completamente agnóstica a frameworks.

---

## ⚙️ Capa Application

📍 **Ubicación:** `com.example.cart.application`

Contiene la **lógica de aplicación** y los **casos de uso**.

**Casos de uso principales:**
- `AddItemUseCase`: Agrega productos al carrito verificando disponibilidad con `ProductPort`.  
- `RemoveItemUseCase`: Elimina un producto del carrito.  
- `UpdateQuantityUseCase`: Modifica la cantidad de un ítem.  
- `CheckoutUseCase`: Cierra el carrito, genera una orden vía `OrderPort` y descuenta el stock.  

**Servicios principales:**
- `CartService`: Gestiona las operaciones CRUD del carrito.  
- `CheckoutService`: Coordina la creación de órdenes y validación de stock con Product.  

**Flujo típico:**
1. Usuario agrega un producto → `ProductPort` valida precio y stock.  
2. Sistema actualiza subtotal → recalcula total del carrito.  
3. En checkout → se envía a `OrderPort` la información consolidada.  

---

## 🌐 Capa Infrastructure

📍 **Ubicación:** `com.example.cart.infraestructure`

Implementa las dependencias externas y expone la API REST.

**Endpoints principales:**
- `POST /cart/{userId}/add` → Agregar producto al carrito.  
- `DELETE /cart/{userId}/remove/{productId}` → Eliminar producto.  
- `PUT /cart/{userId}/update` → Actualizar cantidad.  
- `POST /cart/{userId}/checkout` → Confirmar compra.  
- `GET /cart/{userId}` → Obtener el carrito actual.  

**Persistencia:**
- `CartEntity` y `CartJpaRepository` gestionan la base de datos.  

**Feign Clients:**
- `ProductFeignClient` → obtiene info y stock actualizados.  
- `OrderFeignClient` → envía datos del carrito al crear una orden.  
- `UserFeignClient` → valida existencia y estado del usuario.  

**Configuración:**
- `FeignConfig` para autenticación y logging.  
- `OpenApiConfig` y `SwaggerConfig` para documentación API.  

---

## 🔄 Sincronización con Product y Order

El microservicio **ArkaShoppingCart** depende directamente de **ArkaProduct** y **ArkaOrder** para mantener coherencia.

### 🔹 Con Product
Cada vez que un usuario agrega o modifica productos:
1. `CartService` llama a `ProductFeignClient`.
2. Se valida si el producto existe y su stock es suficiente.  
3. Se obtiene el **precio actualizado**.  
4. El subtotal del ítem y total del carrito se recalculan automáticamente.

### 🔹 Con Order
Durante el **checkout**:
1. El carrito se bloquea (estado `CHECKED_OUT`).  
2. `CheckoutService` invoca `OrderFeignClient` para generar la orden.  
3. El microservicio **Order** descuenta el stock final vía **Product**.  
4. Se marca el carrito como **cerrado** (`CLOSED`).  

---

## 🧩 Diagrama Simplificado de Interacción

```
[Client / API Gateway]
          |
          v
    [CartController]
          |
          v
     [CartService] -----> [ProductPort]
          |                     |
          v                     v
 [CartRepositoryPort]     [ProductFeignClient]
          |
          v
   [CartJpaRepository]

        |
        v
 [CheckoutService] -----> [OrderPort]
                               |
                               v
                       [OrderFeignClient]
```

---

## 🧮 Lógica Analítica

El microservicio incluye procesos analíticos ligeros para mejorar la experiencia del usuario:

- Calcular totales dinámicos por sesión.  
- Detectar carritos abandonados.  
- Medir frecuencia de productos agregados.  
- Generar métricas de conversión enviadas a **ArkaAnalytics**.

Estas operaciones pueden ejecutarse de forma asíncrona usando **mensajería** (Kafka/RabbitMQ).

---

## ⚙️ Configuración Global

📍 **Ubicación:** `com.example.cart.config`

Incluye archivos y parámetros globales:
- `application.yml` → define puertos, conexiones DB, y URLs de Product/Order/User.  
- `FeignConfig` → configuración de autenticación y timeouts.  
- `OpenApiConfig` → documentación Swagger.  

---

## 🚀 Beneficios de la Arquitectura

- **Alta cohesión y bajo acoplamiento.**  
- **Sincronización en tiempo real con Product y Order.**  
- **Escalabilidad horizontal garantizada.**  
- **Manejo centralizado de errores y validaciones.**  
- **Arquitectura limpia y mantenible.**  

---

## 🧾 Conclusión

El microservicio **ArkaShoppingCart** es el núcleo operativo entre el usuario, los productos y las órdenes.  
Su arquitectura hexagonal y su diseño desacoplado garantizan una experiencia fluida, coherente y escalable dentro del ecosistema Arka.  
Además, su integración directa con **Product** y **Order** permite mantener la coherencia de inventario y flujo de compra.

---

