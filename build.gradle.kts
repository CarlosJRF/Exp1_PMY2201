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
    // Bouncy Castle: implementación de Argon2id para el hash de contraseñas
    implementation("org.bouncycastle:bcprov-jdk18on:1.86")
    // Reflection de Kotlin: leer anotaciones y listar propiedades/métodos en tiempo de ejecución
    implementation(kotlin("reflect"))
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
