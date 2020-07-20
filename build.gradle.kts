plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    application
}

repositories {
    jcenter()
}

dependencies {
    val debeziumVersion = "1.1.0.Final"
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.debezium:debezium-embedded:$debeziumVersion")
    implementation("io.debezium:debezium-connector-postgres:$debeziumVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "debezium.wal.streaming.bug.repro.AppKt"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}