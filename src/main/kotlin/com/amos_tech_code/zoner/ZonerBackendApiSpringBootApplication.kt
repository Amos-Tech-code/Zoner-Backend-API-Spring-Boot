package com.amos_tech_code.zoner

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ZonerBackendApiSpringBootApplication

fun main(args: Array<String>) {
    val dotenv = Dotenv.load() // Load .env file
    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }
    runApplication<ZonerBackendApiSpringBootApplication>(*args)
}