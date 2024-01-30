import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
	id("org.springframework.boot")

	id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

val springBootVersion: String by project
val coroutinesVersion = "1.7.3"
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}")
	implementation("org.springframework.boot:spring-boot-starter-validation:${springBootVersion}")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${coroutinesVersion}")

	testImplementation("org.springframework.boot:spring-boot-starter-test:${springBootVersion}")
	testImplementation("com.github.tomakehurst:wiremock:2.27.2")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${coroutinesVersion}")
}

group = "org.deblock"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
repositories {
	mavenCentral()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
		freeCompilerArgs += "-Xjsr305=strict"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("spring.test.constructor.autowire.mode", "all")
}