<!-- Copilot instructions for nekonihongo-pj backend -->

# Overview

This repository is a Spring Boot backend (Java 21, Spring Boot 4) for the Neko Nihongo app. Key packages:

- `com.nekonihongo.backend` — application entrypoint (`BackendApplication.java`).
- `config` — security and JWT filter (`SecurityConfig.java`, `JwtAuthenticationFilter.java`).
- `controller` — REST controllers (`/api/auth/**`, `/api/admin/**`).
- `service` — business logic, including `AuthService`, `IUserService`, and `impl/UserService`.
- `repository` — JPA repositories (e.g., `UserRepository`).
- `entity` / `dto` — JPA entities and DTOs.

# Big-picture architecture & data flow

- Stateless JWT-based auth: clients call `/api/auth/login` → `AuthService` validates credentials with `IUserService` and `PasswordEncoder` → `JwtService` issues JWT and refresh token. The token is expected in `Authorization: Bearer <token>` header.
- `JwtAuthenticationFilter` runs before Spring's username/password filter. It:
  - Skips paths: `/api/auth/**`, `/error`, and swagger paths.
  - Extracts JWT, validates via `JwtService`, and sets `SecurityContext` using `UserDetailsService`.
- Security rules: defined in `SecurityConfig`.
  - `/api/auth/**` are permitted to all.
  - `/api/admin/**` requires role `ADMIN`.
  - Swagger and `/error` are explicitly allowed.
- DB access: JPA repositories use MySQL (see `application.properties`), `spring.jpa.hibernate.ddl-auto=update` in dev.

# Important project-specific conventions (do not assume defaults)

- Auth wrapper: All non-auth controllers use `ApiResponse<T>` (see `dto/ApiResponse.java`). Return `ApiResponse.success(data, msg)` or `ApiResponse.error(msg)`.
- DTO mapping is manual in controllers (example: `UserController#toResponse(User)`). Prefer following existing manual mapping instead of introducing automappers without agreement.
- Roles: use the `User.Role` enum (`USER`, `ADMIN`). Claims set in JWT use `user.getRole().name()`.
- CORS: `SecurityConfig.corsConfigurationSource()` currently allows `http://localhost:3000`. `application.properties` also lists Vite origins (`5173`). Be aware: the runtime CORS in `SecurityConfig` controls behavior.
- Password encoding: `SecurityConfig#passwordEncoder()` returns `NoOpPasswordEncoder` (plain-text) in this code. Services call `passwordEncoder.encode(rawPassword)` (see `UserService#createUser`). Verify production settings before changing passwords or running in prod.

# Build, run & test (developer shortcuts)

- On Windows PowerShell (repo root `d:/nekonihongo-pj/BE/backend`):

  - Build: `./gradlew build` or `./gradlew.bat build`.
  - Run (dev): `./gradlew bootRun` or `./gradlew.bat bootRun`.
  - Run tests: `./gradlew test`.

- The app expects a local MySQL by default (see `src/main/resources/application.properties`):
  - JDBC: `jdbc:mysql://localhost:3306/neko_db` (creates DB if missing).
  - Default `spring.datasource.username=root` and `spring.datasource.password` are set in properties for local dev.

# JWT and security specifics

- JWT secret is base64 in `application.properties` (`jwt.secret`). `JwtService` decodes it using JJWT (`io.jsonwebtoken`).
- Token lifetime: `jwt.expiration-ms` (default 86400000 ms = 1 day). Refresh tokens are generated separately.
- `JwtAuthenticationFilter` uses `userDetailsService.loadUserByUsername(email)` to load authorities. The `UserDetailsServiceImpl` builds a Spring `User` with `.roles(user.getRole().name())`.

# Key files to inspect for future changes (examples)

- Security: `src/main/java/com/nekonihongo/backend/config/SecurityConfig.java`
- JWT filter: `src/main/java/com/nekonihongo/backend/config/JwtAuthenticationFilter.java`
- Auth/login flow: `src/main/java/com/nekonihongo/backend/controller/AuthController.java` and `src/main/java/com/nekonihongo/backend/service/AuthService.java`
- User APIs (admin): `src/main/java/com/nekonihongo/backend/controller/UserController.java` and `service/impl/UserService.java`
- Entities & DTOs: `src/main/java/com/nekonihongo/backend/entity/User.java`, `src/main/java/com/nekonihongo/backend/dto`
- Build: `build.gradle.kts`, Spring Boot plugin version is `4.0.0` and Java 21.

# Quick examples for AI edits

- To add a protected endpoint for admins, add controller under `/api/admin/**` and annotate methods with `@PreAuthorize("hasRole('ADMIN')")` (see `UserController`).
- When adding new JWT claims, update `AuthService` (claims map), and ensure `JwtService.extractEmail` and `JwtAuthenticationFilter` read whatever claim is needed.

# Notes & cautions

- The repository uses `NoOpPasswordEncoder` — passwords are not hashed by default. This is likely intended for local/dev but must be changed before production (swap to `BCryptPasswordEncoder`).
- CORS origins appear in two places; the runtime `SecurityConfig` takes precedence. Update both to keep consistency.
- Exception handling: `SecurityConfig` sets a custom authenticationEntryPoint returning 401 JSON — follow this format if you add global exception handlers.

# When in doubt / next steps

- If adding features that touch auth/security, run integration flows manually:
  1. Start local MySQL.
  2. Run `./gradlew bootRun` and test `/api/auth/login` and an admin `/api/admin/users` call with a token.
- Ask for frontend expectations (ports/origins) before changing CORS.

---

If you'd like, I can refine any section (more examples, recommended local dev scripts, or a safety checklist for production). What should I expand first?
