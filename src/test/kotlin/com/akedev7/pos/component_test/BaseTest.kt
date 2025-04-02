package com.akedev7.pos.component_test

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseTest {
    companion object {
        private val postgresContainer = PostgreSQLContainer("postgres:15.2").apply {
            withDatabaseName("appdb")
            withUsername("postgres")
            withPassword("postgres")
            start()
        }
        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
        }
    }

    @AfterAll
    fun cleanup() {
        postgresContainer.stop()
    }
}
