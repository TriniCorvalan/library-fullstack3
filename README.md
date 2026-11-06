# library

Microservicio Spring Boot del CRUD de **libros** (Oracle Autonomous DB).

- Puerto: **8080**
- Base path: `/books`
- Auth: valida JWT emitidos por `ms-usuarios` (mismo `jwt.secret`). Las lecturas son públicas; las escrituras requieren Bearer token.

## Endpoints

| Método | Ruta | Acceso |
|--------|------|--------|
| GET | `/books` | Público |
| GET | `/books/{id}` | Público |
| POST | `/books` | JWT |
| PUT | `/books/{id}` | JWT |
| DELETE | `/books/{id}` | JWT |

Campos: `id`, `title`, `author`, `genre`, `year`.

## Ejecutar en local

```bash
./mvnw spring-boot:run
```

Requiere wallet Oracle (ver `application.properties` / symlink `~/oracle_wallets/library`).

## Docker

Incluido en el compose de [`library-ng`](../library-ng) (`docker compose up --build` desde esa carpeta), o solo este servicio:

```bash
docker compose up --build
```
