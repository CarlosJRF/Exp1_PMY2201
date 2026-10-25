plugins {
    kotlin("jvm") version "2.4.21"
    application
}

group = "cl.duoc.pizza"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "cl.duoc.pizza.MainKt"
}

tasks.test {
    useJUnitPlatform()
}

// Permite leer la entrada de la consola al ejecutar con ./gradlew run
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
