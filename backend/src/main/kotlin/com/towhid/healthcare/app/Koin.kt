package com.towhid.healthcare.app

import com.towhid.healthcare.di.appModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

import com.towhid.healthcare.di.databaseModule

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule(this@configureKoin), databaseModule)
    }
}
