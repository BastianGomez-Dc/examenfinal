# TecnoFix — Arquitectura de Microservicios

Proyecto del Examen Final Transversal (EFT) de **DSY1103 – Desarrollo FullStack 1** (Duoc UC).

## Integrantes

- Bastián Gómez

## Contexto del proyecto

**TecnoFix** es una tienda de mantención de computadores. El sistema digitaliza su operación completa: registro de clientes y sus equipos, técnicos y especialidades, catálogo de servicios de mantención, inventario de repuestos y proveedores, órdenes de trabajo (el núcleo del negocio), facturación, agendamiento de citas y autenticación de usuarios.

La solución está construida como una arquitectura de **microservicios independientes**, cada uno con su propia base de datos, comunicándose entre sí mediante REST (`RestClient` de Spring), y expuestos al exterior a través de un **API Gateway** único.

## Arquitectura

| Servicio | Puerto | BD | Responsabilidad | Consume a |
|---|---|---|---|---|
| **api-gateway** | 8080 | — | Punto de entrada único (Spring Cloud Gateway) | todos |
| **bff** | 8081 | — | Fachada de autenticación (login/registro) hacia ms-auth | ms-auth |
| **ms-cliente** | 8082 | Postgres `clientedb` | Clientes | — |
| **ms-equipo** | 8083 | MySQL `equipodb` | Equipos ingresados a mantención + tipos de equipo | ms-cliente |
| **ms-tecnico** | 8084 | Postgres `tecnicodb` | Técnicos + especialidades | — |
| **ms-servicio** | 8085 | MySQL `serviciodb` | Catálogo de servicios + categorías | — |
| **ms-repuesto** | 8086 | MySQL `repuestodb` | Inventario de repuestos y control de stock | ms-proveedor |
| **ms-proveedor** | 8087 | Postgres `proveedordb` | Proveedores de repuestos | — |
| **ms-orden** | 8088 | Postgres `ordendb` | Órdenes de trabajo (núcleo del negocio) | ms-cliente, ms-equipo, ms-tecnico, ms-servicio |
| **ms-factura** | 8089 | MySQL `facturadb` | Facturación de órdenes | ms-orden |
| **ms-auth** | 8090 | Postgres `authdb` | Login / registro, emisión de JWT (24h) | — |
| **ms-agenda** | 8091 | Postgres `agendadb` | Agendamiento de citas cliente ↔ técnico | ms-cliente, ms-tecnico |

Un solo contenedor **Postgres 17** y uno **MySQL 8.4** alojan todas las bases de datos (una por microservicio), inicializadas por `init/postgres-init.sql` e `init/mysql-init.sql`. Cada microservicio administra su propio esquema con **Flyway**.

## Rutas principales del API Gateway

Todo el tráfico externo entra por `http://localhost:8080`. El gateway antepone `/api` y enruta al recurso correspondiente (`StripPrefix`):

| Ruta del Gateway | Destino |
|---|---|
| `/api/clientes/**` | ms-cliente → `/clientes` |
| `/api/equipos/**` | ms-equipo → `/equipos` |
| `/api/tipos-equipo/**` | ms-equipo → `/tipos-equipo` |
| `/api/tecnicos/**` | ms-tecnico → `/tecnicos` |
| `/api/especialidades/**` | ms-tecnico → `/especialidades` |
| `/api/servicios/**` | ms-servicio → `/servicios` |
| `/api/categorias/**` | ms-servicio → `/categorias` |
| `/api/repuestos/**` | ms-repuesto → `/repuestos` |
| `/api/proveedores/**` | ms-proveedor → `/proveedores` |
| `/api/ordenes/**` | ms-orden → `/ordenes` |
| `/api/facturas/**` | ms-factura → `/facturas` |
| `/api/citas/**` | ms-agenda → `/citas` |
| `/api/auth/**` | **bff** → `/login`, `/register` (que a su vez consulta a ms-auth) |

El listado de rutas activas también se puede consultar en vivo vía Actuator: `GET http://localhost:8080/actuator/gateway/routes`.

## Documentación Swagger (entorno local)

Cada microservicio expone su propia documentación OpenAPI/Swagger UI:

| Servicio | Swagger UI |
|---|---|
| bff | http://localhost:8081/swagger-ui.html |
| ms-cliente | http://localhost:8082/swagger-ui.html |
| ms-equipo | http://localhost:8083/swagger-ui.html |
| ms-tecnico | http://localhost:8084/swagger-ui.html |
| ms-servicio | http://localhost:8085/swagger-ui.html |
| ms-repuesto | http://localhost:8086/swagger-ui.html |
| ms-proveedor | http://localhost:8087/swagger-ui.html |
| ms-orden | http://localhost:8088/swagger-ui.html |
| ms-factura | http://localhost:8089/swagger-ui.html |
| ms-auth | http://localhost:8090/swagger-ui.html |
| ms-agenda | http://localhost:8091/swagger-ui.html |

> El api-gateway no tiene Swagger propio (no expone lógica de negocio, solo enruta); la documentación de cada endpoint se revisa en el Swagger del microservicio dueño del recurso.

## Instrucciones de ejecución

### Requisitos

- Docker Desktop (o Docker Engine + Compose) para levantar todo el ecosistema.
- Opcionalmente JDK 25 y Maven si se quiere ejecutar un microservicio individual fuera de Docker.

### Ejecución local con Docker Compose (recomendado)

Desde la raíz del repositorio:

```bash
docker compose up -d --build
```

Esto construye las 12 imágenes (api-gateway, bff, ms-auth y los 9 microservicios de dominio) y levanta además los contenedores de Postgres y MySQL con sus bases de datos ya creadas. La primera vez puede tardar varios minutos mientras se descargan dependencias.

Para verificar que todo quedó arriba:

```bash
docker compose ps
```

Para ver logs de un servicio puntual:

```bash
docker compose logs -f ms-orden
```

Para detener todo:

```bash
docker compose down
```

(agregar `-v` solo si se quiere borrar también los datos persistidos en los volúmenes de Postgres/MySQL).

### Ejecución individual de un microservicio (desarrollo)

1. Levantar solo las bases de datos: `docker compose up -d postgres mysql`.
2. Pararse en la carpeta del microservicio (ej. `cd ms-cliente`).
3. Ejecutar `mvn spring-boot:run` (usa los valores por defecto de `application.yml`, que apuntan a `localhost`).
4. Repetir para cada microservicio que se quiera levantar fuera de Docker. Los microservicios que consumen a otros (ms-equipo, ms-repuesto, ms-orden, ms-factura, ms-agenda, bff, api-gateway) necesitan que sus dependencias estén arriba en el puerto esperado (ver tabla de arquitectura).

### Probar los endpoints

Cada microservicio incluye un archivo `client.http` con casos de prueba (casos felices y de error) listos para ejecutar desde IntelliJ/VS Code (extensión REST Client), o se puede usar Swagger UI / Postman apuntando a `http://localhost:8080/api/...` (vía gateway) o directo al puerto del microservicio.

### Ejecución remota

Pendiente. El plan es desplegar al menos el api-gateway y 2 microservicios en Railway o Render, reutilizando las mismas variables de entorno (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS`, `MS_*_URL`) que ya usa el `docker-compose.yml`, apuntando a bases de datos gestionadas en la nube.

## Pruebas unitarias

Los 12 servicios tienen pruebas con JUnit 5 + Mockito (capa de servicio, con mocks de repositorios y clients remotos), pruebas de controller (validando `ResponseEntity` y códigos HTTP) y pruebas de repositorio con H2 en memoria (usando las migraciones Flyway reales). Para correr las pruebas de un microservicio:

```bash
cd ms-cliente
mvn test
```

## Tecnologías utilizadas

- Java 25, Spring Boot 3.4.5, Maven
- Spring Web, Spring Data JPA, Spring Validation, Spring Security (ms-auth/bff)
- Spring Cloud Gateway (api-gateway)
- PostgreSQL 17 y MySQL 8.4, Flyway para migraciones
- JJWT para JSON Web Tokens
- springdoc-openapi (Swagger UI) en cada microservicio
- JUnit 5, Mockito, JaCoCo, H2 (pruebas de repositorio)
- Docker y Docker Compose

## Estructura del repositorio

```
tecnofix/
├── api-gateway/       # Spring Cloud Gateway - punto de entrada único
├── bff/                # Backend for Frontend - fachada de autenticación
├── ms-auth/            # Login, registro, JWT
├── ms-cliente/         # Clientes
├── ms-equipo/          # Equipos y tipos de equipo
├── ms-tecnico/         # Técnicos y especialidades
├── ms-servicio/        # Catálogo de servicios y categorías
├── ms-proveedor/       # Proveedores
├── ms-repuesto/        # Inventario de repuestos
├── ms-orden/           # Órdenes de trabajo (núcleo del negocio)
├── ms-factura/         # Facturación
├── ms-agenda/          # Agendamiento de citas
├── init/               # Scripts de inicialización de Postgres y MySQL
└── docker-compose.yml  # Orquestación de todo el ecosistema
```
