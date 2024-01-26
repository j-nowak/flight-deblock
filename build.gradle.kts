import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
	id("org.springframework.boot")

	id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

val springBootVersion: String by project
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")

	testImplementation("org.springframework.boot:spring-boot-starter-test:${springBootVersion}")
}

group = "org.deblock"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
		freeCompilerArgs += "-Xjsr305=strict"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}