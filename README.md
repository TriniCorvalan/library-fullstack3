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

## Configurar Oracle

1. Coloca el wallet en `Wallet_TG243BR3UDTANBR0/` (el contenido no se versiona).
2. Copia `.env.example` a `.env` y completa `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.
   - En Docker, `TNS_ADMIN` debe ser `/app/wallet`.
   - En local con `mvnw`, usa la ruta real al wallet (por ejemplo el symlink `~/oracle_wallets/library`).

`.env` y el wallet **no** van en la imagen Docker ni al repositorio.

## Ejecutar en local

```bash
export DB_URL='jdbc:oracle:thin:@tg243br3udtanbr0_tp?TNS_ADMIN='$HOME'/oracle_wallets/library'
export DB_USERNAME='...'
export DB_PASSWORD='...'
./mvnw spring-boot:run
```

Las escrituras con JWT requieren `ms-usuarios` en local (`:8081`).

## Docker

```bash
cp .env.example .env   # completar credenciales
docker compose up --build
```

O desde [`library-ng`](../library-ng) (`docker compose up --build`), que monta el mismo `.env` y wallet de este proyecto.

La imagen no incluye el wallet: en local se monta como volumen; en ECS se inyectará `ORACLE_WALLET_BASE64` (paso posterior de CI/CD).
