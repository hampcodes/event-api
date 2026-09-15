**Laboratorio Spring Data JPA .**

`event-api · entrega 1 · cada paso incluye el archivo completo`

<a id="indice"></a>
## Índice

**Mapeo (entidades)**

1. [Crea tu rama de trabajo](#rama)
2. [Punto de partida — así está el proyecto hoy](#partida)
3. [Category — la entidad nueva más simple](#category)
4. [Role — catálogo de roles](#role)
5. [User — organizador, comprador y favoritos](#user)
6. [OrganizerDetails — datos del organizador (1:1 con User)](#organizerdetails)
7. [Profile — datos personales (1:1 con User)](#profile)
8. [Event — FK al organizador y relación con categorías](#event)
9. [EventCategoryId — la clave compuesta](#eventcategoryid)
10. [EventCategory — la entidad intermedia](#eventcategory)
11. [Purchase — la compra](#purchase)
12. [Payment — pagos de una compra](#paymentEntity)

**Repositorios y consultas**

13. [CategoryRepository, RoleRepository, OrganizerDetailsRepository, ProfileRepository, EventCategoryRepository y UserRepository](#categoryrepo)
14. [EventRepository — Query Method, JPQL, @Modifying](#eventrepo)
15. [PurchaseRepository — SQL nativo](#purchaserepo)
16. [PaymentRepository](#paymentrepo)
17. [Procedimiento almacenado — reporte de ventas](#procedimiento)

**Cierre**

18. [Tests de repositorio — cómo probar las queries](#pruebas)
19. [Flujo Git — commit, push y Pull Request](#gitflow)

---

<a id="rama"></a>
### Paso 0 · Crea tu rama de trabajo

```bash
git checkout main
git pull origin main
git checkout -b feature/spring-data-jpa-relaciones
```

[↑ Volver al índice](#indice)

---

<a id="partida"></a>
### Paso 1 · Punto de partida: así está el proyecto hoy

Todavía no existe ninguna de las 10 tablas del ER diagram (`docs/er_diagram.png`): `roles`, `users`, `organizer_details`, `profiles`, `events`, `categories`, `event_categories`, `purchases`, `favorites`, `payments`. La guía va en dos bloques: primero todo el mapeo (pasos 3 a 12), después repositorios y consultas (pasos 13 a 17).

[↑ Volver al índice](#indice)

---

## Mapeo (entidades)

<a id="category"></a>
### Paso 2 · Category — la entidad nueva más simple

Entidad simple, sin relación directa con `Event` — la resuelve `EventCategory`.

**`src/main/java/com/eventapi/entiy/Category.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategory;

    @Column(nullable = false, length = 50)
    private String name;
}
```

[↑ Volver al índice](#indice)

---

<a id="role"></a>
### Paso 3 · Role — catálogo de roles

Tabla de referencia; se crea antes que `User` porque `User` declara `private Role role;`.

**`src/main/java/com/eventapi/entiy/Role.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRole;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 150)
    private String description;
}
```

[↑ Volver al índice](#indice)

---

<a id="user"></a>
### Paso 4 · User — organizador, comprador y favoritos

FK a `Role` y lado dueño del M:N de favoritos; se crea antes que `Event`, `OrganizerDetails` y `Profile`.

**`src/main/java/com/eventapi/entiy/User.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(nullable = false, length = 36, name = "supabase_user_id")
    private String supabaseUserId;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany
    @JoinTable(name = "favorites",
        joinColumns = @JoinColumn(name = "buyer_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> favoriteEvents;
}
```

[↑ Volver al índice](#indice)

---

<a id="organizerdetails"></a>
### Paso 5 · OrganizerDetails — datos del organizador (1:1 con User)

1:1 con clave compartida (`@MapsId`), unidireccional: `User` no referencia a `OrganizerDetails`.

**`src/main/java/com/eventapi/entiy/OrganizerDetails.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organizer_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerDetails {

    @Id
    private Long idUser;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "business_name", length = 100)
    private String businessName;

    @Column(name = "tax_id", length = 20)
    private String taxId;

    @Column(name = "bank_account", length = 30)
    private String bankAccount;
}
```

[↑ Volver al índice](#indice)

---

<a id="profile"></a>
### Paso 6 · Profile — datos personales (1:1 con User)

Mismo patrón que `OrganizerDetails`, pero para cualquier usuario.

**`src/main/java/com/eventapi/entiy/Profile.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    private Long idUser;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 150)
    private String address;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

[↑ Volver al índice](#indice)

---

<a id="event"></a>
### Paso 7 · Event — FK al organizador y relación con categorías

FK al organizador (`User`) y lado inverso hacia `EventCategory` y favoritos.

**`src/main/java/com/eventapi/entiy/Event.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    private String name;
    private String description;
    private LocalDateTime date;
    private String location;
    private String imageUrl;
    private Integer capacity;

    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @OneToMany(mappedBy = "event")
    private List<EventCategory> eventCategories;

    @ManyToMany(mappedBy = "favoriteEvents")
    private List<User> favoritedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

[↑ Volver al índice](#indice)

---

<a id="eventcategoryid"></a>
### Paso 8 · EventCategoryId — la clave compuesta

Las dos FK que forman la llave primaria de `event_categories`.

**`src/main/java/com/eventapi/entiy/EventCategoryId.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCategoryId implements Serializable {
    private Long eventId;
    private Long categoryId;
}
```

[↑ Volver al índice](#indice)

---

<a id="eventcategory"></a>
### Paso 9 · EventCategory — la entidad intermedia (M:N con dato extra)

M:N entre `Event` y `Category`, con `isPrimary` como dato extra.

**`src/main/java/com/eventapi/entiy/EventCategory.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCategory {

    @EmbeddedId
    private EventCategoryId id = new EventCategoryId();

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "is_primary")
    private Boolean isPrimary;
}
```

[↑ Volver al índice](#indice)

---

<a id="purchase"></a>
### Paso 10 · Purchase — la compra

Una compra de un `Event` por un `User` comprador.

**`src/main/java/com/eventapi/entiy/PurchaseStatus.java`**
```java
package com.eventapi.entiy;

public enum PurchaseStatus {
    PENDING, PAID, CANCELED
}
```

**`src/main/java/com/eventapi/entiy/Purchase.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    private Integer quantity;
    private BigDecimal totalAmount;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

[↑ Volver al índice](#indice)

---

<a id="paymentEntity"></a>
### Paso 11 · Payment — pagos de una compra

N:1 unidireccional hacia `Purchase` — una compra puede tener varios pagos.

**`src/main/java/com/eventapi/entiy/Payment.java`**
```java
package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @Column(length = 30)
    private String method;

    private BigDecimal amount;

    @Column(length = 20)
    private String status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
```

[↑ Volver al índice](#indice)

---

## Repositorios y consultas

<a id="categoryrepo"></a>
### Paso 12 · CategoryRepository, RoleRepository, OrganizerDetailsRepository, ProfileRepository, EventCategoryRepository y UserRepository

Repositorios simples; `EventCategoryRepository` usa la clave compuesta y trae el primer Query Method. `UserRepository` no tiene consultas propias — se agregó para poder construir `User` (compradores) directamente con `save()` en los tests, igual que con el resto de entidades.

**`src/main/java/com/eventapi/repository/CategoryRepository.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
```

**`src/main/java/com/eventapi/repository/RoleRepository.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
```

**`src/main/java/com/eventapi/repository/OrganizerDetailsRepository.java`**

```java
package com.eventapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerDetailsRepository extends JpaRepository<OrganizerDetails, Long> {
}
```

**`src/main/java/com/eventapi/repository/ProfileRepository.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
```

**`src/main/java/com/eventapi/repository/EventCategoryRepository.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.EventCategory;
import com.eventapi.entiy.EventCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository
        extends JpaRepository<EventCategory, EventCategoryId> {

    boolean existsByCategoryIdCategory(Long categoryId);
}
```

**`src/main/java/com/eventapi/repository/UserRepository.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
```

[↑ Volver al índice](#indice)

---

<a id="eventrepo"></a>
### Paso 13 · EventRepository — Query Method, JPQL y @Modifying

Los tres tipos de consulta sobre `Event`, más la llamada al procedimiento almacenado (paso 17).

**`src/main/java/com/eventapi/repository/EventRepository.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatusAndAvailableTicketsGreaterThan(
            EventStatus status, Integer tickets);

    long countByOrganizerIdUser(Long organizerId);

    List<Event> findByOrganizerIdUser(Long organizerId);

    @Query("""
            SELECT DISTINCT ec.event FROM EventCategory ec
            WHERE ec.category.idCategory = :categoryId AND ec.event.status = :status
            """)
    List<Event> findByCategoryAndStatus(@Param("categoryId") Long categoryId,
                                         @Param("status") EventStatus status);

    @Query("""
            SELECT e FROM Event e
            WHERE e.id = :eventId AND e.availableTickets >= :quantity
            """)
    Optional<Event> findAvailableEvent(@Param("eventId") Long eventId,
                                        @Param("quantity") Integer quantity);

    // clearAutomatically limpia el contexto de persistencia después del UPDATE
    // (si no, una lectura posterior con findById puede devolver el valor cacheado, no el que quedó en la BD).
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Event e SET e.availableTickets = e.availableTickets - :quantity
            WHERE e.id = :eventId
            """)
    int decreaseAvailableTickets(@Param("eventId") Long eventId,
                                  @Param("quantity") Integer quantity);

    @Query(value = "SELECT * FROM fn_reporte_ventas_evento(:eventId)",
           nativeQuery = true)
    List<Object[]> reporteVentasPorEvento(@Param("eventId") Long eventId);
}
```

[↑ Volver al índice](#indice)

---

<a id="purchaserepo"></a>
### Paso 14 · PurchaseRepository — SQL nativo

Compras pendientes de pago, con SQL nativo.

**`src/main/java/com/eventapi/repository/PurchaseRepository.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query(value = """
            SELECT * FROM purchases
            WHERE status = 'PENDING' AND buyer_id = :buyerId
            """, nativeQuery = true)
    List<Purchase> findPendingPurchasesNative(@Param("buyerId") Long buyerId);
}
```

[↑ Volver al índice](#indice)

---

<a id="paymentrepo"></a>
### Paso 15 · PaymentRepository

Los pagos de una compra.

**`src/main/java/com/eventapi/repository/PaymentRepository.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPurchaseId(Long purchaseId);
}
```

[↑ Volver al índice](#indice)

---

<a id="procedimiento"></a>
### Paso 16 · Procedimiento almacenado — reporte de ventas

Hibernate no crea funciones — este SQL se corre una sola vez, a mano, contra `event_db`.

```sql
CREATE OR REPLACE FUNCTION
  fn_reporte_ventas_evento(p_event_id BIGINT)
RETURNS TABLE(
  evento VARCHAR, entradas BIGINT,
  recaudacion NUMERIC)
LANGUAGE plpgsql AS $$
BEGIN
  RETURN QUERY
  SELECT e.name, SUM(p.quantity),
         SUM(p.total_amount)
  FROM events e
  JOIN purchases p ON p.event_id = e.id
  WHERE e.id = p_event_id
  GROUP BY e.name;
END; $$;
```

[↑ Volver al índice](#indice)

---

## Cierre

<a id="pruebas"></a>
### Paso 17 · Tests de repositorio — cómo probar las queries

Cada `@DataJpaTest` es **independiente de cualquier dato precargado**: no depende de `docs/seed_data.sql` ni de que otra persona haya corrido la app antes. Cada test crea con `save()` los registros que necesita (evento, comprador, compra, etc.), llama a la query que se está probando y valida el resultado. Solo se necesita:

- Un Postgres accesible con la conexión de `application.yaml` (`event_db`).
- Que las tablas existan (`ddl-auto: update` las crea/actualiza solas al levantar el contexto de test).
- Para `reporteVentasPorEvento`: que la función `fn_reporte_ventas_evento` ya se haya creado a mano (Paso 16).

`@AutoConfigureTestDatabase(replace = Replace.NONE)` le dice a Spring que **no** reemplace el datasource por uno embebido — usa el Postgres real. Cada `@Test` corre dentro de una transacción que Spring hace `rollback` al terminar, así que no ensucia la base de datos entre corridas.

Las clases inyectan sus repositorios por **constructor** (`@RequiredArgsConstructor(onConstructor_ = @Autowired)` + campos `final`), no por `@Autowired` en el campo — mismo resultado, pero sin mutabilidad innecesaria.

**`src/test/java/com/eventapi/repository/CategoryRepositoryTest.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Category;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryRepositoryTest {

    private final CategoryRepository categoryRepository;

    @Test
    void save_shouldPersistCategory() {
        System.out.println(">>> Probando: guardar una categoría nueva.");

        Category category = new Category();
        category.setName("Concierto");

        Category saved = categoryRepository.save(category);

        System.out.println(">>> Resultado: " + saved);

        assertThat(saved.getIdCategory()).isNotNull();
    }

    @Test
    void findByName_shouldReturnCategory() {
        System.out.println(">>> Probando: Query Method que busca una categoría por su nombre.");

        categoryRepository.save(new Category(null, "Deporte"));

        Optional<Category> found = categoryRepository.findByName("Deporte");

        System.out.println(">>> Resultado: " + found.orElse(null));

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Deporte");
    }
}
```

**`src/test/java/com/eventapi/repository/EventCategoryRepositoryTest.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Category;
import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventCategory;
import com.eventapi.entiy.EventStatus;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventCategoryRepositoryTest {

    private final EventCategoryRepository eventCategoryRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    @Test
    void existsByCategoryIdCategory_shouldReturnTrue() {
        System.out.println(">>> Probando: Query Method que verifica si una categoría está usada en al menos un evento.");

        Event event = new Event();
        event.setName("Concierto de Rock");
        event.setDescription("Festival al aire libre");
        event.setDate(LocalDateTime.now().plusDays(10));
        event.setLocation("Parque Central");
        event.setImageUrl("https://example.com/images/concierto-rock.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(100);
        event.setPrice(new BigDecimal("45.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Category category = new Category();
        category.setName("Música");
        categoryRepository.save(category);

        EventCategory eventCategory = new EventCategory();
        eventCategory.setEvent(event);
        eventCategory.setCategory(category);
        eventCategory.setIsPrimary(true);
        eventCategoryRepository.save(eventCategory);

        boolean exists = eventCategoryRepository.existsByCategoryIdCategory(category.getIdCategory());

        System.out.println(">>> Resultado: " + exists);

        assertThat(exists).isTrue();
    }
}
```

**`src/test/java/com/eventapi/repository/EventRepositoryTest.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Category;
import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventCategory;
import com.eventapi.entiy.EventStatus;
import com.eventapi.entiy.Purchase;
import com.eventapi.entiy.PurchaseStatus;
import com.eventapi.entiy.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventRepositoryTest {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    @Test
    void findByCategoryAndStatus_shouldReturnEventMatchingCategoryAndStatus() {
        System.out.println(">>> Probando: listar eventos de una categoría que además tengan un status específico.");

        Event activeEvent = new Event();
        activeEvent.setName("Concierto Activo");
        activeEvent.setDescription("Descripción de prueba");
        activeEvent.setDate(LocalDateTime.now().plusDays(5));
        activeEvent.setLocation("Lugar de prueba");
        activeEvent.setImageUrl("https://example.com/image.jpg");
        activeEvent.setCapacity(100);
        activeEvent.setAvailableTickets(50);
        activeEvent.setPrice(new BigDecimal("30.00"));
        activeEvent.setStatus(EventStatus.ACTIVE);
        activeEvent.setCreatedAt(LocalDateTime.now());
        activeEvent.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(activeEvent);

        Event canceledEvent = new Event();
        canceledEvent.setName("Concierto Cancelado");
        canceledEvent.setDescription("Descripción de prueba");
        canceledEvent.setDate(LocalDateTime.now().plusDays(5));
        canceledEvent.setLocation("Lugar de prueba");
        canceledEvent.setImageUrl("https://example.com/image.jpg");
        canceledEvent.setCapacity(100);
        canceledEvent.setAvailableTickets(50);
        canceledEvent.setPrice(new BigDecimal("30.00"));
        canceledEvent.setStatus(EventStatus.CANCELED);
        canceledEvent.setCreatedAt(LocalDateTime.now());
        canceledEvent.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(canceledEvent);

        Category category = categoryRepository.save(new Category(null, "Rock"));

        EventCategory activeLink = new EventCategory();
        activeLink.setEvent(activeEvent);
        activeLink.setCategory(category);
        activeLink.setIsPrimary(true);
        eventCategoryRepository.save(activeLink);

        EventCategory canceledLink = new EventCategory();
        canceledLink.setEvent(canceledEvent);
        canceledLink.setCategory(category);
        canceledLink.setIsPrimary(true);
        eventCategoryRepository.save(canceledLink);

        List<Event> found = eventRepository.findByCategoryAndStatus(
                category.getIdCategory(), EventStatus.ACTIVE);

        System.out.println(">>> Resultado: " + found);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getId()).isEqualTo(activeEvent.getId());
    }

    @Test
    void findAvailableEvent_shouldReturnEvent_whenEnoughTickets() {
        System.out.println(">>> Probando: buscar un evento por id solo si tiene boletos suficientes (hay cupo).");

        Event event = new Event();
        event.setName("Evento con cupo");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(10);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Optional<Event> found = eventRepository.findAvailableEvent(event.getId(), 5);

        System.out.println(">>> Resultado: " + found);

        assertThat(found).isPresent();
    }

    @Test
    void findAvailableEvent_shouldReturnEmpty_whenNotEnoughTickets() {
        System.out.println(">>> Probando: la misma búsqueda de cupo, pero pidiendo más boletos de los disponibles (no debe devolver nada).");

        Event event = new Event();
        event.setName("Evento sin cupo");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(2);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Optional<Event> found = eventRepository.findAvailableEvent(event.getId(), 5);

        System.out.println(">>> Resultado: " + found);

        assertThat(found).isEmpty();
    }

    @Test
    void decreaseAvailableTickets_shouldSubtractQuantity() {
        System.out.println(">>> Probando: el UPDATE que descuenta boletos disponibles de un evento.");

        Event event = new Event();
        event.setName("Evento para descontar");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(10);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        int updatedRows = eventRepository.decreaseAvailableTickets(event.getId(), 3);
        Event reloaded = eventRepository.findById(event.getId()).orElseThrow();

        System.out.println(">>> Resultado: filas actualizadas=" + updatedRows
                + ", boletos restantes=" + reloaded.getAvailableTickets());

        assertThat(updatedRows).isEqualTo(1);
        assertThat(reloaded.getAvailableTickets()).isEqualTo(7);
    }

    // Requiere que la función fn_reporte_ventas_evento ya exista en la BD (ver Paso 16).
    @Test
    void reporteVentasPorEvento_shouldReturnSalesSummary() {
        System.out.println(">>> Probando: el reporte de ventas de un evento (función almacenada nativa fn_reporte_ventas_evento).");

        Event event = new Event();
        event.setName("Evento con ventas");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(50);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        User buyer = new User();
        buyer.setUsername("buyer_reporte_" + System.nanoTime());
        buyer.setSupabaseUserId("sb-uuid-test");
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Purchase purchase = new Purchase();
        purchase.setEvent(event);
        purchase.setBuyer(buyer);
        purchase.setQuantity(2);
        purchase.setTotalAmount(new BigDecimal("60.00"));
        purchase.setCode("RPT-" + System.nanoTime());
        purchase.setStatus(PurchaseStatus.PAID);
        purchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);

        List<Object[]> reporte = eventRepository.reporteVentasPorEvento(event.getId());

        reporte.forEach(fila -> System.out.println(">>> Resultado: " + java.util.Arrays.toString(fila)));

        assertThat(reporte).isNotEmpty();
    }
}
```

**`src/test/java/com/eventapi/repository/PurchaseRepositoryTest.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventStatus;
import com.eventapi.entiy.Purchase;
import com.eventapi.entiy.PurchaseStatus;
import com.eventapi.entiy.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PurchaseRepositoryTest {

    private final PurchaseRepository purchaseRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Test
    void findPendingPurchasesNative_shouldReturnOnlyPendingPurchasesForBuyer() {
        System.out.println(">>> Probando: SQL nativo que trae solo las compras PENDING de un comprador.");

        User buyer = new User();
        buyer.setUsername("buyer_pending_" + System.nanoTime());
        buyer.setSupabaseUserId(UUID.randomUUID().toString());
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Event event = new Event();
        event.setName("Evento con compras");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(50);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Purchase pendingPurchase = new Purchase();
        pendingPurchase.setEvent(event);
        pendingPurchase.setBuyer(buyer);
        pendingPurchase.setQuantity(1);
        pendingPurchase.setTotalAmount(new BigDecimal("30.00"));
        pendingPurchase.setCode("PEND-" + System.nanoTime());
        pendingPurchase.setStatus(PurchaseStatus.PENDING);
        pendingPurchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(pendingPurchase);

        Purchase paidPurchase = new Purchase();
        paidPurchase.setEvent(event);
        paidPurchase.setBuyer(buyer);
        paidPurchase.setQuantity(1);
        paidPurchase.setTotalAmount(new BigDecimal("30.00"));
        paidPurchase.setCode("PAID-" + System.nanoTime());
        paidPurchase.setStatus(PurchaseStatus.PAID);
        paidPurchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(paidPurchase);

        List<Purchase> pending = purchaseRepository.findPendingPurchasesNative(buyer.getIdUser());

        System.out.println(">>> Resultado: " + pending);

        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getStatus()).isEqualTo(PurchaseStatus.PENDING);
    }

    @Test
    void findPendingPurchasesNative_shouldReturnEmpty_whenBuyerHasNoPendingPurchases() {
        System.out.println(">>> Probando: el mismo SQL nativo, pero cuando el comprador no tiene ninguna compra PENDING.");

        User buyer = new User();
        buyer.setUsername("buyer_nopending_" + System.nanoTime());
        buyer.setSupabaseUserId(UUID.randomUUID().toString());
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Event event = new Event();
        event.setName("Evento sin pendientes");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(50);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Purchase paidPurchase = new Purchase();
        paidPurchase.setEvent(event);
        paidPurchase.setBuyer(buyer);
        paidPurchase.setQuantity(1);
        paidPurchase.setTotalAmount(new BigDecimal("30.00"));
        paidPurchase.setCode("PAID-" + System.nanoTime());
        paidPurchase.setStatus(PurchaseStatus.PAID);
        paidPurchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(paidPurchase);

        List<Purchase> pending = purchaseRepository.findPendingPurchasesNative(buyer.getIdUser());

        System.out.println(">>> Resultado: " + pending);

        assertThat(pending).isEmpty();
    }
}
```

**`src/test/java/com/eventapi/repository/PaymentRepositoryTest.java`**
```java
package com.eventapi.repository;

import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventStatus;
import com.eventapi.entiy.Payment;
import com.eventapi.entiy.Purchase;
import com.eventapi.entiy.PurchaseStatus;
import com.eventapi.entiy.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PaymentRepositoryTest {

    private final PaymentRepository paymentRepository;
    private final PurchaseRepository purchaseRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Test
    void findByPurchaseId_shouldReturnPaymentsOfThatPurchase() {
        System.out.println(">>> Probando: Query Method que trae los pagos asociados a una compra.");

        User buyer = new User();
        buyer.setUsername("buyer_payment_" + System.nanoTime());
        buyer.setSupabaseUserId(UUID.randomUUID().toString());
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Event event = new Event();
        event.setName("Evento con pago");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(50);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Purchase purchase = new Purchase();
        purchase.setEvent(event);
        purchase.setBuyer(buyer);
        purchase.setQuantity(1);
        purchase.setTotalAmount(new BigDecimal("30.00"));
        purchase.setCode("PAY-" + System.nanoTime());
        purchase.setStatus(PurchaseStatus.PAID);
        purchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);

        Payment payment = new Payment();
        payment.setPurchase(purchase);
        payment.setMethod("YAPE");
        payment.setAmount(new BigDecimal("30.00"));
        payment.setStatus("COMPLETED");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByPurchaseId(purchase.getId());

        System.out.println(">>> Resultado: " + payments);

        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getMethod()).isEqualTo("YAPE");
    }

    @Test
    void findByPurchaseId_shouldReturnEmpty_whenPurchaseHasNoPayments() {
        System.out.println(">>> Probando: la misma query cuando la compra todavía no tiene ningún pago registrado.");

        User buyer = new User();
        buyer.setUsername("buyer_nopayment_" + System.nanoTime());
        buyer.setSupabaseUserId(UUID.randomUUID().toString());
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Event event = new Event();
        event.setName("Evento sin pago");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(50);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Purchase purchase = new Purchase();
        purchase.setEvent(event);
        purchase.setBuyer(buyer);
        purchase.setQuantity(1);
        purchase.setTotalAmount(new BigDecimal("30.00"));
        purchase.setCode("NOPAY-" + System.nanoTime());
        purchase.setStatus(PurchaseStatus.PENDING);
        purchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);

        List<Payment> payments = paymentRepository.findByPurchaseId(purchase.getId());

        System.out.println(">>> Resultado: " + payments);

        assertThat(payments).isEmpty();
    }
}
```

**Correr todos los tests de repositorio:**
```bash
mvn test -Dtest=*RepositoryTest
```

[↑ Volver al índice](#indice)

---

<a id="gitflow"></a>
### Paso 18 · Flujo Git — commit, push y Pull Request

**1. Confirmar los cambios**
```bash
git add src/main/java/com/eventapi/repository/EventRepository.java
git add src/main/java/com/eventapi/repository/UserRepository.java
git add src/test/java/com/eventapi/repository/*.java
git add docs/Guia_SpringDataJPA_event-api.md docs/Guia_SpringDataJPA_event-api.docx
git commit -m "test: agrega pruebas de repositorio para las nuevas queries de Event, Purchase y Payment"
```

**2. Enviar la rama al remoto**
```bash
git push origin feature/spring-data-jpa-relaciones
```

**3. Abrir el Pull Request contra `develop`**

Como el entorno no tiene `gh` (GitHub CLI) instalado, se abre manualmente desde el link que imprime el `git push`, o desde:
```
https://github.com/hampcodes/event-api/compare/develop...feature/spring-data-jpa-relaciones
```

**Descripción sugerida para el PR:**

> **Título:** test: pruebas de repositorio para EventRepository, PurchaseRepository y PaymentRepository
>
> **Resumen**
> - Se agregan `EventRepositoryTest`, `PurchaseRepositoryTest` y `PaymentRepositoryTest` (`@DataJpaTest`, independientes de datos precargados).
> - Se agrega `UserRepository` (simple) para poder construir compradores de prueba con `save()`.
> - Se corrige `EventRepository.decreaseAvailableTickets` con `@Modifying(clearAutomatically = true, flushAutomatically = true)` para evitar lecturas con datos cacheados tras el `UPDATE`.
> - Se actualizan `CategoryRepositoryTest` y `EventCategoryRepositoryTest` a inyección por constructor (`@RequiredArgsConstructor(onConstructor_ = @Autowired)`).
> - Se documenta todo en `docs/Guia_SpringDataJPA_event-api.md` / `.docx`.
>
> **Cómo probarlo**
> ```bash
> mvn test -Dtest=*RepositoryTest
> ```

[↑ Volver al índice](#indice)

---

*SPRING DATA JPA · EVENT-API · GUÍA PASO A PASO*
