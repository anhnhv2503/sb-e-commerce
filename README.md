# VA E-commerce — Spring Boot Backend

REST API for a clothing e-commerce shop. It handles products, carts, orders and users. Login uses JWT and Google OAuth, and access is split into user and admin roles. Payments go through VNPAY or PayOS, images are stored on Cloudinary, email goes out over Gmail SMTP, and cart updates are pushed live over WebSocket.

Frontend (React/Vite) runs at `http://localhost:5173` in development; demo deployment: https://demo-va-shop.vercel.app/

## Tech stack

| Area | Technology |
|------|------------|
| Framework | Spring Boot 3.4.0 (Web, Validation, Data JPA, Security, Mail, WebSocket) |
| Language | Java 17 (the Docker image runs on Java 21) |
| Database | MySQL 8 with Hibernate (`ddl-auto=update`) |
| Auth | Spring Security, stateless JWT (jjwt 0.11.5), Google OAuth through OpenFeign |
| Payments | VNPAY, PayOS (`payos-java` 1.0.1), cash on delivery |
| Media | Cloudinary (`cloudinary-http44` 1.36.0) |
| Real-time | STOMP over WebSocket (simple broker) |
| API docs | springdoc-openapi 2.8.4 (Swagger UI) |
| Other | Lombok, Spring Cloud 2024.0.0, in-memory per-IP rate limiting |

## Project structure

```
src/main/java/com/anhnhvcoder/ecommerce
├── client/        # OpenFeign clients for Google OAuth (token exchange, user info)
├── config/        # Cloudinary, VNPAY, PayOS, OpenAPI, WebSocket, rate limit, data seeding
├── controller/    # REST controllers (/api/**)
├── dto/           # Response DTOs
├── enums/         # OrderStatus, PaymentStatus, PaymentType, CollectionStatus
├── exception/     # Custom exceptions + GlobalExceptionHandler
├── mapper/        # Entity ↔ DTO mappers
├── model/         # JPA entities (User, Role, Product, Size, Brand, Category, Cart, Order, …)
├── repository/    # Spring Data JPA repositories
├── request/       # Request payloads
├── response/      # ApiResponse wrapper and auth responses
├── security/      # Security filter chain, JWT filter/utils, UserDetails
├── service/       # Service interfaces + Impl/ (business logic, email, Cloudinary, VNPAY)
└── utils/         # VNPAY signing helpers
```

## Features

- **Authentication**: email/password login and Google login, both returning a JWT. Also covers registration, email verification, forgot/reset password and change password.
- **Roles**: `ROLE_USER` and `ROLE_ADMIN`, checked with `@PreAuthorize`.
- **Catalog**: products with images, sizes and per-size stock, plus brands and categories. Supports search and paging, filtered by category, brand and keyword.
- **Cart**: add, update and remove items. The cart item count is broadcast on `/topic/cart`.
- **Orders**: place, cancel and confirm delivery. Status moves through `PENDING → IN_PROGRESS → SHIPPING → DELIVERED` (or `CANCELLED`).
- **Payments**: VNPAY (payment URL plus callback), PayOS (payment link plus execute), or cash on delivery.
- **Admin user management**: list, search and filter users; create accounts; edit profiles; lock and unlock accounts; activate accounts by hand; change roles; delete users who have no orders. A locked user can't log in (password or Google), and their existing tokens are rejected with `403`.
- **Dashboard** (admin only): summary totals, order counts by status, monthly revenue for a year, best-selling products and low-stock sizes.
- **Rate limiting**: a token bucket per client IP for `/api/**`. Over the limit, the API returns `429` with a `Retry-After` header.

## Getting started

### Prerequisites

- JDK 17+
- Maven 3.9+ (or the bundled `mvnw`)
- MySQL 8
- Accounts or keys for Cloudinary, Gmail (app password), VNPAY sandbox, PayOS and Google OAuth

### Environment variables

`src/main/resources/application.properties` reads the following:

| Variable | Purpose |
|----------|---------|
| `LOCAL_DB` | JDBC URL, e.g. `jdbc:mysql://localhost:3306/shop` |
| `LOCAL_DB_USERNAME`, `LOCAL_DB_PASSWORD` | Database credentials |
| `JWT_SECRET` | Key used to sign JWTs (tokens expire after 24h) |
| `CLOUD_NAME`, `API_KEY`, `API_SECRET` | Cloudinary |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | Gmail SMTP account and app password |
| `VNPAY_TMN_CODE`, `VNPAY_SECRET_KEY` | VNPAY merchant config |
| `PAYOS_CLIENT_ID`, `PAYOS_API_KEY`, `PAYOS_CHECKSUM_KEY` | PayOS credentials |
| `GG_CLIENT_ID`, `GG_CLIENT_SECRET` | Google OAuth client |

Other settings in the same file:

- `client.domain` and `gg.identity.redirectUri` point at the frontend (`http://localhost:5173`).
- Uploads are limited to 5 MB.
- Rate limiting is configured with `rate-limit.enabled`, `rate-limit.capacity`, `rate-limit.refill-tokens` and `rate-limit.refill-period-seconds`. The default is 100 requests per 60 s per IP.

### Run locally

```bash
# build
./mvnw clean package

# run
./mvnw spring-boot:run
# or
java -jar target/spring-shopping-cart-0.0.1-SNAPSHOT.jar
```

The API listens on `http://localhost:8080`.

### Run with Docker

```bash
./mvnw clean package -DskipTests
docker build -t va-shop-api .
docker run -p 8080:8080 --env-file .env va-shop-api
```

### Seed data

On first start, `ApplicationInitConfig` creates:

- the roles `ROLE_USER` and `ROLE_ADMIN`
- default categories (Áo thun, Áo sơ mi, Áo khoác, Quần jean, Quần kaki, Quần âu, Blazer)
- an admin account `prjonlineshop@gmail.com` with the password set in that class

**Change the admin password before deploying anywhere public.**

## API overview

Every endpoint is under `/api`. Responses are wrapped in `ApiResponse` (`statusCode`, `data`). Protected endpoints require the header `Authorization: Bearer <token>`.

| Resource | Base path | Main endpoints | Access |
|----------|-----------|----------------|--------|
| Auth | `/api/auth` | `POST /login`, `POST /google-login` | Public |
| Users | `/api/user` | `POST /register`, `POST /forgot-password`, `POST /reset-password`, `GET /{userId}/detail` | Public |
| | | `PUT /update`, `PUT /change-password` | User |
| | | `POST /admin/register` | Admin |
| Email | `/api/email` | `POST /verify` | Public |
| Products | `/api/products` | `GET ?page&size&categoryName&brandName&search`, `GET /all`, `GET /{id}/get`, `GET /search`, `GET /new-arrival`, `GET /category`, `GET /count`, `GET /brand/all`, `GET /size/{sizeId}`, `GET /size/inventory/{sizeId}` | Public |
| | | `POST /add`, `PUT /{id}/update`, `DELETE /{id}/delete`, `POST /size/add/{productId}`, `PUT /size/edit/{sizeId}` | Admin |
| Categories | `/api/categories` | `GET /all`, `GET /{id}/get` | Public |
| | | `POST /add`, `DELETE /{id}/delete` | Admin |
| Brands | `/api/brands` | `GET /`, `GET /{id}` | Public |
| | | `POST /`, `PUT /{id}`, `DELETE /{id}` (refused if products use the brand) | Admin |
| Cart | `/api/cart` | `GET /my-cart`, `GET /count` | User |
| Cart items | `/api/cart-item` | `POST /item/add`, `PUT /update/{itemId}/{quantity}`, `DELETE /remove/{itemId}` | User |
| Orders | `/api/order` | `POST /place-order`, `GET /my-orders` | Authenticated |
| | | `PUT /cancel-order/{orderId}` (PENDING/IN_PROGRESS only), `PUT /confirm-delivered/{orderId}` | Order owner or admin |
| | | `GET /all?page&status`, `GET /newest`, `PUT /update-order-status/{orderId}` | Admin |
| | | `POST /vnpay`, `POST /vnpay-callback`, `POST /payos/create-link`, `POST /payos/execute` | Authenticated |
| Admin users | `/api/admin/users` | `GET ?search&role&active&page&size`, `GET /{id}`, `POST /`, `PUT /{id}`, `PUT /{id}/lock`, `PUT /{id}/unlock`, `PUT /{id}/activate`, `PUT /{id}/role?roleName`, `DELETE /{id}` | Admin |
| Dashboard | `/api/dashboard` | `GET /data`, `GET /order-status`, `GET /revenue?year`, `GET /top-products?limit`, `GET /low-stock?threshold` | Admin |

Admins can't lock, delete or change the role of their own account. A caller without the required role gets `403`.

### Interactive docs

- OpenAPI JSON: http://localhost:8080/shop/api-docs
- Swagger UI: http://localhost:8080/swagger-ui/index.html

In Swagger UI, use **Authorize** ("Bearer Authentication") to paste a JWT (the `accessToken` returned by `POST /api/auth/login`). The definition dropdown has three groups: **all**, **shop** (storefront APIs) and **admin** (`/api/admin/**` and `/api/dashboard/**`).

### WebSocket

- STOMP endpoint: `ws://localhost:8080/ws`
- Application prefix: `/app`
- Topic `/topic/cart` sends the current number of items in the cart whenever the cart changes

## License

Personal project, built for learning and demonstration.
