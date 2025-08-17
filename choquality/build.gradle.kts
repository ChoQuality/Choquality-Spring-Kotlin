plugins {
    base
}

repositories {
    mavenCentral()
}

tasks.build {
    dependsOn(":ProxyMain:bootJar")
}

tasks.matching { it.name == "jar" || it.name == "bootJar" }.configureEach {
    enabled = false
}
