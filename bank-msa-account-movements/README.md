# Banco API - Backend

API REST para la gestión de clientes, cuentas y movimientos bancarios.

## Tecnologías
- Java 17
- Spring Boot 3
- PostgreSQL 15
- Docker 28+
- JPA/Hibernate
- JUnit 5 / Mockito
- Swagger OpenAPI 3

## Estructura del proyecto

```
bank-msa-account-movements/
├── Dockerfile
├── docker-compose.yml
├── BaseDatos.sql
├── pom.xml
├── src/
└── ...
```

## Requisitos previos
- Docker y Docker Compose instalados
- Java 17+ (solo para desarrollo local)

## Despliegue rápido con Docker

1. Clona el repositorio y entra al directorio del backend:
   ```bash
   git clone <repo-url>
   cd fullstack-bank-system/bank-msa-account-movements
   ```

2. Levanta los servicios (API + PostgreSQL + Frontend Angular):
   ```bash
   docker compose up --build -d
   ```

3. Accede a la API en: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

4. Accede al frontend Angular:
   - [http://localhost:4200](http://localhost:4200) (local)
   - [http://localhost:8081](http://localhost:8081) (útil para OrbStack o acceso externo)

   Si usas OrbStack, también puedes acceder desde la URL que te provee OrbStack para el puerto 8081.

## Endpoints principales

- CRUD de clientes: `/api/clientes`
- CRUD de cuentas: `/api/cuentas`
- Movimientos: `/api/movimientos`
- Reportes: `/api/reportes?clienteId={id}&inicio={yyyy-MM-dd}&fin={yyyy-MM-dd}`

## Base de datos
- El script [`BaseDatos.sql`](BaseDatos.sql) crea el esquema y datos de ejemplo automáticamente al levantar el contenedor.
- Usuario: `banco_user` / Password: `banco_pass`
- DB: `banco_db`

## Pruebas unitarias

```bash
mvn test
```

## Variables de entorno (docker-compose)

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`

## Apagar los servicios

```bash
docker compose down
```

## Documentación interactiva

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Colección Postman

- Ver carpeta `postman/collections/` para importar y probar todos los endpoints.

---

**Autor:** Tu Nombre
