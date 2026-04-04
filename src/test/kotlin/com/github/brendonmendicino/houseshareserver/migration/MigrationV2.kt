package com.github.brendonmendicino.houseshareserver.migration

import com.github.brendonmendicino.houseshareserver.FlywayMigrationTest
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.sql.ResultSet

@ActiveProfiles("no-security", "dev")
@TestPropertySource(
    properties = [
        "spring.flyway.baseline-version=1",
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
        flyway.baseline()
    }

    @Test
    fun migrate() {
        jdbcTemplate.update(
            "insert into app_user (username, email, first_name, last_name, sub, picture, created_at) " +
                    "values ('test', 'test', 'test', 'test', 'test', 'https://example.com/test?test=10&boh=90', now())"
        )

        flyway.migrate()

        jdbcTemplate.query("select * from app_user", ResultSet::getRow)
    }
}