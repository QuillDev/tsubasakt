plugins {
    kotlin("jvm") version "1.5.31"
}

group "moe.quill"
version "0.0.1-SNAPSHOT"


repositories {
    mavenCentral()

    //JDA
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io/")
}

dependencies {
    val jdaVersion = "4.3.0_339"

    //Discord Stuff
    implementation("net.dv8tion:JDA:$jdaVersion") { exclude("module", "opus-java") }
    implementation("com.github.minndevelopment:jda-ktx:1223d5cbb8a8caac6d28799a36001f1844d7aa7d")

    //Logging
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    //JSON
    implementation("com.google.code.gson:gson:2.8.8")
    //Reflection
    implementation("org.reflections:reflections:0.10.2")
    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    //Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    //DI
    implementation("org.kodein.di:kodein-di:7.9.0")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "moe.quill.tsubasa.Tsubasa"
    }

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
