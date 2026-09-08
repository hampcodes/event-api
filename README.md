# Event API

API REST para la gestión de eventos (creación, consulta, actualización y eliminación), construida con Spring Boot y persistencia en PostgreSQL.

## Stack Tecnológico

- **Java 21**
- **Spring Boot 4.1.1** (`spring-boot-starter-parent`)
- **Spring Web MVC** — exposición de endpoints REST (`spring-boot-starter-webmvc`)
- **Spring Data JPA** — persistencia y acceso a datos (`spring-boot-starter-data-jpa`)
- **PostgreSQL** — base de datos relacional (driver `org.postgresql:postgresql`)
- **Hibernate** — implementación de JPA (dialecto `PostgreSQLDialect`)
- **Lombok** — reducción de código boilerplate (getters/setters/constructores)
- **Maven** (con Maven Wrapper `mvnw` / `mvnw.cmd`) — build y gestión de dependencias

## Estructura del Proyecto

```
event-api/
├── src/
│   ├── main/
│   │   ├── java/com/eventapi/
│   │   │   ├── EventApiApplication.java     # Clase principal (entry point) de Spring Boot
│   │   │   ├── controller/
│   │   │   │   └── EventController.java     # Endpoints REST (/api/events)
│   │   │   ├── service/
│   │   │   │   └── EventService.java        # Lógica de negocio
│   │   │   ├── repository/
│   │   │   │   └── EventRepository.java     # Acceso a datos (JpaRepository)
│   │   │   └── entiy/
│   │   │       ├── Event.java               # Entidad JPA "events"
│   │   │       └── EventStatus.java         # Enum de estados del evento
│   │   └── resources/
│   │       ├── application.yaml             # Configuración de la app (BD, servidor, JPA)
│   │       ├── static/                      # Recursos estáticos
│   │       └── templates/                   # Plantillas del lado del servidor
│   └── test/
│       └── java/com/eventapi/
│           └── EventApiApplicationTests.java
├── pom.xml                                  # Definición de dependencias y build (Maven)
├── mvnw / mvnw.cmd                          # Maven Wrapper
└── .gitignore
```

> Nota: el paquete `entiy` conserva ese nombre tal como está en el código fuente (sería `entity`).

## Endpoints

Base path: `/api/events` (context-path `/api` + `/events` del controlador)

| Método | Endpoint             | Descripción                    |
|--------|-----------------------|---------------------------------|
| GET    | `/api/events`         | Lista todos los eventos         |
| GET    | `/api/events/{id}`    | Obtiene un evento por su ID     |
| POST   | `/api/events`         | Crea un nuevo evento            |
| PUT    | `/api/events/{id}`    | Actualiza un evento existente   |
| DELETE | `/api/events/{id}`    | Elimina un evento                |

## Requisitos

- JDK 21
- PostgreSQL en ejecución (por defecto en `localhost:5432`, base de datos `event_db`)
- Maven (o usar el wrapper incluido `./mvnw`)

## Configuración

La configuración de la base de datos se encuentra en `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/event_db
    username: postgres
    password: adminadmin
```

> ⚠️ Estas credenciales están hardcodeadas solo para desarrollo local. Se recomienda no versionar credenciales reales y mover valores sensibles a variables de entorno o a un archivo `application-local.yaml` (ya excluido en `.gitignore`).

## Cómo ejecutar el proyecto

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd event-api

# Crear la base de datos en PostgreSQL
# createdb event_db

# Ejecutar la aplicación
./mvnw spring-boot:run
```

La API quedará disponible en `http://localhost:8080/api`.

## Tests

```bash
./mvnw test
```
