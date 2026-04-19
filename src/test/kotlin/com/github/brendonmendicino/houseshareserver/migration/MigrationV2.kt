package com.github.brendonmendicino.houseshareserver.migration

import com.github.brendonmendicino.houseshareserver.FlywayMigrationTest
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@TestPropertySource(
    properties = [
        "spring.flyway.target=next"
    ]
)
class MigrationV2 : FlywayMigrationTest() {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var flyway: Flyway

    @BeforeEach
    fun setupDb() {
        flyway.clean()

        Flyway.configure()
            .configuration(flyway.configuration)
            .target(MigrationVersion.fromVersion("1"))
            .load()
            .migrate()
    }

    @Test
    fun `migrate column 'picture' from byte to text`() {
        jdbcTemplate.update(
            "insert into app_user (username, email, first_name, last_name, sub, picture, created_at) " +
                    "values ('test1', 'test1', 'test1', 'test1', 'test1', 'https://example.com/test1?test1=10&boh=90', now()), " +
                    "('test2', 'test2', 'test2', 'test2', 'test2', null, now())"
        )

        flyway.migrate()

        val (first, second) = jdbcTemplate.queryForList("select * from app_user")

        assertEquals("https://example.com/test1?test1=10&boh=90", first["picture"])
        assertEquals(null, second["picture"])
    }

    @Test
    fun `adding 'image_url' column causes already present rows to have it as null`() {
        jdbcTemplate.update(
            "insert into app_group (name, description, created_at, updated_at, created_by, last_modified_by) " +
                    "values ('test', 'test', now(), now(), null, null)"
        )

        flyway.migrate()

        val group = jdbcTemplate.queryForList("select * from app_group").first()

        assertEquals(null, group["image_url"])
    }
}