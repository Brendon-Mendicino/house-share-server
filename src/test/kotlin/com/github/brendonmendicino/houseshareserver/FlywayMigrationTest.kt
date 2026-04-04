package com.github.brendonmendicino.houseshareserver

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
abstract class FlywayMigrationTest {
    companion object {
        @JvmStatic
        @Container
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:17")
    }
}