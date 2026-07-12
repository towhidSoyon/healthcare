package com.towhid.healthcare.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun init(dotenv: Dotenv) {
        val url = dotenv["DB_URL"]
        val user = dotenv["DB_USER"]
        val password = dotenv["DB_PASSWORD"]

        logger.info("Initializing database connection pool...")
        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = url
            username = user
            this.password = password
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)

        logger.info("Running database migrations...")
        Flyway.configure()
            .dataSource(dataSource)
            .load()
            .migrate()

        logger.info("Connecting Exposed to database...")
        Database.connect(dataSource)
        logger.info("Database initialized successfully.")
    }
}
