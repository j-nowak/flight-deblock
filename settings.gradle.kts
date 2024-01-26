rootProject.name = "exercise"

pluginManagement {
    val kotlinVersion = "1.9.10"
    val springBootVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion

        id("org.springframework.boot") version springBootVersion
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}