import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


val profile = "local"

fun Project.setResource(profile: String) {
    val component = "../BuildProperties/component"
    val properties = "../BuildProperties/$profile"
    val resource = "../BuildResource"
    extensions.configure(SourceSetContainer::class.java) {
        named("main") {
            resources {
                srcDirs(component,properties,resource)
            }
        }
    }
}

buildscript {
    extra["common"]   = "../BuildGradle/build-common.gradle"
    extra["spring_3"] = "../BuildGradle/build-springboot3.gradle"
    extra["jwt"] = "../BuildGradle/build-jwt.gradle"
    extra["datasource"] = "../BuildGradle/build-datasource.gradle"
    extra["test"]      = "../BuildGradle/build-test.gradle"
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
apply(from = extra["jwt"] as String)
apply(from = extra["datasource"] as String)
apply(from = extra["test"] as String)

setResource(profile)

group = "com.example.choquality.proxy"
/*version = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"))*/
version = "ver0001"

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

val externalLibDirs = listOf(
    "../BuildLibs/common",
    "../BuildLibs/todo"
)
dependencies {
    implementation(project(":common"))
    implementation(project(":todo"))
    externalLibDirs.forEach { dir ->
        runtimeOnly(files(fileTree(dir) { include("*.jar") }))
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileKotlin {
    dependsOn(":todo:jar")
}

tasks.bootJar {
    dependsOn(tasks.compileKotlin)
}

tasks.jar {
    enabled = false
}

tasks.register<Delete>("delBuildLibs") {
    delete(externalLibDirs)
}

tasks.clean {
    dependsOn(tasks.named("delBuildLibs"))
}
