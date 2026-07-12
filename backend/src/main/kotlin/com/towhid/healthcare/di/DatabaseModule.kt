package com.towhid.healthcare.di

import com.towhid.healthcare.infrastructure.database.DatabaseFactory
import io.github.cdimascio.dotenv.Dotenv
import org.koin.dsl.module

val databaseModule = module {
    single {
        val dotenv = get<Dotenv>()
        DatabaseFactory.init(dotenv)
        DatabaseFactory
    }
}
