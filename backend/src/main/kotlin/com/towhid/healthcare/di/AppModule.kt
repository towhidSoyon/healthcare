package com.towhid.healthcare.di

import com.towhid.healthcare.core.config.AppConfig
import io.ktor.server.application.Application
import org.koin.dsl.module

import io.github.cdimascio.dotenv.dotenv

fun appModule(application: Application) = module {
    single { AppConfig(application.environment.config) }
    single { dotenv { ignoreIfMissing = true } }
}
