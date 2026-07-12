package com.towhid.healthcare.core.config

import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.config.ApplicationConfig

class AppConfig(private val applicationConfig: ApplicationConfig) {
    private val dotenv = Dotenv.configure().ignoreIfMissing().load()

    fun getString(key: String): String {
        val envKey = key.replace(".", "_").uppercase()
        return dotenv[envKey] 
            ?: System.getenv(envKey) 
            ?: applicationConfig.propertyOrNull(key)?.getString() 
            ?: throw IllegalArgumentException("Configuration property $key not found")
    }

    fun getStringOrNull(key: String): String? {
        val envKey = key.replace(".", "_").uppercase()
        return dotenv[envKey] 
            ?: System.getenv(envKey) 
            ?: applicationConfig.propertyOrNull(key)?.getString()
    }

    fun getInt(key: String): Int = getString(key).toInt()
    fun getIntOrNull(key: String): Int? = getStringOrNull(key)?.toIntOrNull()
}
