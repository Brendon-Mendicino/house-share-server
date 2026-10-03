# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Spring Boot 4 / Kotlin 2.2 REST backend for the **HouseShare** app (shared-house groups with members, shopping lists and
split expenses). Java 24 toolchain, PostgreSQL, Flyway, Keycloak (OIDC) for auth, OpenTelemetry → Grafana LGTM for
observability.

## Commands

All via the Gradle wrapper (`./gradlew`):

- Build: `./gradlew build` — Run app: `./gradlew bootRun --args='--spring.profiles.active=dev,local'`
- Run all tests: `./gradlew test`
- Run one test class:
  `./gradlew test --tests 'com.github.brendonmendicino.houseshareserver.service.SignedUrlServiceTest'`
- Run one test method: `./gradlew test --tests '*MigrationV2.migrate column*'` (backtick-named Kotlin tests match by
  glob)
- Compile only: `./gradlew compileKotlin compileTestKotlin`
- Docker image: `docker build .` (multi-stage, runs `gradle bootJar` inside)

Tests use **Testcontainers** (Postgres + Grafana LGTM), so Docker must be running. There is no linter configured.

### Local infrastructure

`compose.yaml` provides Keycloak (:8080, admin/password, imports the realm from `docker/keycloak/realm`), Postgres (:
5432, `myuser`/`secret`/`mydatabase`) and Grafana LGTM (:3000, OTLP on :4317/:4318). `spring-boot-docker-compose` is a
`developmentOnly` dep, so `bootRun` starts these automatically. The app itself listens on **:9090** in `local`.
`scripts/export-keycloak-config.sh` exports the running Keycloak realm back into `docker/keycloak/realm`;
`doc/keycloak-setup.md` describes the realm-role → ID-token mapper needed for role-based auth.

`src/test/kotlin/.../TestHouseShareServerApplication.kt` is a `main` that boots the app against Testcontainers instead
of compose.

## Profiles

- `dev` — `DbInitializer` seeds users/groups/items/expenses on an empty DB (it runs with a fake `ROLE_admin` security
  context) and `LoggingInterceptor` logs every request. Usually combined with `local`.
- `local` — `application-local.yaml`: local compose services, port 9090, 100% trace sampling, Swagger UI at
  `/swagger-ui.html`, DEBUG logging for web/authorization/SQL.
- `prod` — everything from env vars (`DB_URL`, `ISSUER_URI`, `SERVER_CLIENT_ID`, …, `*_EXPORT_URL`, `SERVER_PORT`).
- `test` — activated by `src/test/resources/application.yaml`; enables `flyway.clean-disabled=false`.
- `no-security` — swaps `SecurityConfig` for `NoSecurityConfig` (permit-all, CSRF/CORS off) and disables
  `PublicController`. Used by `FlywayMigrationTest`.

## Architecture

Package root: `com.github.brendonmendicino.houseshareserver`. Layers are
`controller → service (interface + *Impl) → repository`, with `entity` (JPA) and `dto` kept separate and bridged by
extension functions in `mapper/` (`toDto()` / `toEntity()`).

**Generic CRUD.** `CrudService<T>` and the sealed `CrudController<T>` provide list/get/save/update/delete;
`GroupController` (`/api/v1/groups`) and `UserController` extend them and add sub-resource endpoints. Almost all domain
logic lives in `GroupServiceImpl`: shopping items, expenses/parts and members are all created *through the group* (
`/{groupId}/shopping-items`, `/{groupId}/expenses`, `/{groupId}/members`). `ExpenseServiceImpl`/
`ShoppingItemServiceImpl` and `ExpenseController` are empty stubs.

**Users vs. members.** `AppUser` is an authenticated account (created from the OIDC ID token in `OAuth2Service` on first
login, keyed by `sub`). `GroupMember` is a per-group participant that may or may not be linked to an `AppUser` —
shopping items and expense parts reference *members*, not users. Adding a user to a group (`AppGroup.addUser` +
`AppUser.toMember`) creates a linked member; removing the user unlinks the member (`member.user = null`) but keeps it so
its expenses survive. `AppGroup.members` cascades ALL; other collections mostly don't cascade, so persist via the
aggregate root and use the `addX()` helper methods on entities to keep both sides of relations in sync.

**Entities.** All extend `BaseEntity` (identity `id`, id-based `equals`/`hashCode`, `ref()` for log strings like
`Group@12`). `kotlin("plugin.jpa")` + `allOpen` handle the JPA open-class requirement. Hibernate runs with
`ddl-auto: validate`; **schema changes require a Flyway migration** in `src/main/resources/db/migration/` (currently
squashed to `V1__init.sql`; `db/migration_old/` is not on the Flyway path and only exists for reference).

**Security.** OAuth2 login against Keycloak with two client registrations (`house-share-server` for browser,
`house-share-app` for the mobile app with an `app://` redirect). `SecurityConfig` maps Keycloak `realm_access.roles` to
`ROLE_*` authorities; `/api/**` requires auth and returns 401 (not a redirect), `anyRequest` requires `ROLE_admin`.
Fine-grained authorization is done with `@PreAuthorize` on *service* methods using SpEL bean calls:
`@authorizationService.isMemberOf(#groupId)`, `@authorizationService.isSelf(#userId)`,
`@signedUrlService.validCurrentUri()`. Keep new service methods guarded the same way. CSRF uses a cookie token +
`SpaCsrfTokenRequestHandler` (SPA pattern); `/csrf` returns the token.

**Group invites.** `SignedUrlService` HMAC-signs `/api/v1/groups/{id}/invite/join?expires&nonce&signature` with a *
*per-process random secret** (invites don't survive restarts and won't work across instances). `joinFromInviteUrl`
validates the current request URI and calls `addUserNoMember` (the unguarded variant of `addUser`).

**Errors.** Domain exceptions are sealed classes in `exception/` (`GroupException.NotFound.from(id)` etc.) and are
translated to RFC 7807 `ProblemDetail` responses in `advice/GlobalExceptionHandler`. Add a handler there when adding a
new exception type.

**Observability.** `@Observed` on service methods, `TraceIdFilter`/`UserMdcFilter` put trace id and `user.sub` into MDC,
`logback-spring.xml` writes coloured console output (with trace id and `user.sub`) plus JSONL to `logs/` and ships logs
via the OTel appender (`InstallOpenTelemetryAppender`); under `prod` it uses a plain console and OTel only, no file.
`OpenTelemetryConfiguration` switches Micrometer JVM/HTTP metrics to OTel semantic
conventions.

**Web.** Paged endpoints accept Spring `Pageable`; `WebConfig` sets `PageSerializationMode.VIA_DTO`. Custom
bean-validation annotation `@NotBlankIfPresent` (null OK, blank not) is used on optional string fields.

## Tests

- `HouseShareServerApplicationTests` — context load with `@Import(TestcontainersConfiguration)`.
- `FlywayMigrationTest` — abstract base (`no-security` + `test` profiles, own Postgres container); subclasses in
  `migration/` set `spring.flyway.target`, `flyway.clean()`, migrate to a version, insert rows with `JdbcTemplate`, then
  `flyway.migrate()` and assert. Follow this pattern for data-migration tests.
- `SignedUrlServiceTest` — plain unit test.

## Misc

`dotsocr/` and `paddle/` are untracked, unrelated OCR experiments — ignore them. `build/`, `logs/`, `.idea/` are
generated.
