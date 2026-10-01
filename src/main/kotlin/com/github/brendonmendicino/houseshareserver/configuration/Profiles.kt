package com.github.brendonmendicino.houseshareserver.configuration

import com.github.brendonmendicino.houseshareserver.configuration.Profiles.DEV
import com.github.brendonmendicino.houseshareserver.configuration.Profiles.NO_SECURITY


/**
 * All the Spring profiles used by the application.
 *
 * Use these constants in `@Profile`/`@ActiveProfiles` instead of plain strings.
 */
object Profiles {
    /**
     * Development behaviour: seeds an empty database with sample users/groups/items/expenses
     * (`DbInitializer`) and logs every request (`LoggingConfig`).
     */
    const val DEV = "dev"

    /**
     * Connection settings for the local infrastructure started by `compose.yaml`
     * (Postgres, Keycloak, Grafana LGTM), 100% trace sampling and Swagger UI (`application-local.yaml`).
     * Usually activated together with [DEV].
     */
    const val LOCAL = "local"

    /**
     * Production: every setting is read from environment variables (`application-prod.yaml`).
     */
    const val PROD = "prod"

    /**
     * Automated tests: activated by `src/test/resources/application.yaml`, allows `flyway.clean()`
     * (`application-test.yaml`).
     */
    const val TEST = "test"

    /**
     * Disables authentication and authorization: every request is permitted and CSRF/CORS are off
     * (`NoSecurityConfig`). Only meant for tests that don't need an identity provider, like `FlywayMigrationTest`.
     */
    const val NO_SECURITY = "no-security"

    /**
     * Negation of [NO_SECURITY]: beans that must exist only when security is enabled
     * (`SecurityConfig`, `PublicController`).
     */
    const val SECURITY = "!$NO_SECURITY"
}
