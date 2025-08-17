import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.Delete
import org.gradle.jvm.toolchain.JavaLanguageVersion

buildscript {
    extra["common"]   = "../../BuildGradle/build-common.gradle"
    extra["spring_3"] = "../../BuildGradle/build-springboot3.gradle"
    extra["datasource"] = "../../BuildGradle/build-datasource.gradle"
    extra["test"]      = "../../BuildGradle/build-test.gradle"
}

plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.jpa") version "1.9.24"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

apply(from = extra["common"] as String)
apply(from = extra["spring_3"] as String)
apply(from = extra["datasource"] as String)
apply(from = extra["test"] as String)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations {
    named("compileOnly") {
        extendsFrom(configurations.named("annotationProcessor").get())
    }
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

val libs = file("../../BuildLibs/todo")

tasks.bootJar {
    enabled = false
}

tasks.register<Delete>("delBuildLibs") {
    delete(libs)
}

tasks.clean {
    dependsOn(tasks.named("delBuildLibs"))
}

tasks.compileKotlin {
    dependsOn(tasks.clean)
}

tasks.jar {
    dependsOn(tasks.compileKotlin)
    isEnabled = true
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val yyMMddHH = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHH"))
    val major = yyMMddHH.substring(0, 2)
    val miner = yyMMddHH.substring(2, 6)
    val fix = yyMMddHH.substring(6, 8)

    archiveFileName.set("com.example.choquality.todo.$major.$miner.$fix.jar")

    exclude("*.yml")
    exclude("static/**")
    exclude("templates/**")

    destinationDirectory.set(libs)
}