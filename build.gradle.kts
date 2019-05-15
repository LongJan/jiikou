import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
}

group = "pub.ronin"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.named<Jar>("jar") {
    manifest {
        attributes("Main-Class" to "pub.ronin.JiiKoUCliKt")
    }
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    dependsOn("test")
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks.register<Copy>("copyJar") {
    group = "jiikou"
    dependsOn("jar")
    doFirst {
        file("$projectDir/dist").apply {
            if(exists()){
                deleteRecursively()
            }
        }
    }
    from(file("$buildDir/libs/${project.name}-$version.jar"))
    into(file("$projectDir/dist"))
}

tasks.register<Exec>("makeJarExec") {
    group = "jiikou"
    dependsOn("copyJar")
    commandLine = listOf(
        "java",
        "-jar",
        "$buildDir/libs/${project.name}-$version.jar",
        "x",
        "$projectDir/dist/${project.name}-$version.jar",
        "-name",
        "jiikou"
    )
}

tasks.register("release") {
    group = "jiikou"
    dependsOn("makeJarExec")
}