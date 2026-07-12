package com.towhid.healthcare.di

import com.towhid.healthcare.core.config.AppConfig
import io.ktor.server.application.Application
import org.koin.dsl.module

fun appModule(application: Application) = module {
    single { AppConfig(application.environment.config) }
}
