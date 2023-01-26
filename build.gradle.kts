description = "Word count pipeline with Kafka Streams"
plugins {
    java
    idea
    id("io.freefair.lombok") version "6.6.1"
    id("com.google.cloud.tools.jib") version "3.3.1"
}

group = "com.bakdata.kpops.examples"

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
}


configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
    test {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(group = "com.bakdata.kafka", name = "streams-bootstrap", version = "2.8.0")
    implementation(group = "org.apache.logging.log4j", name = "log4j-slf4j-impl", version = "2.19.0")
    implementation("com.google.guava:guava:31.1-jre")

    val junitVersion: String by project
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = junitVersion)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.24.2")
    val fluentKafkaVersion = "2.8.1"
    testImplementation(
        group = "com.bakdata.fluent-kafka-streams-tests",
        name = "fluent-kafka-streams-tests-junit5",
        version = fluentKafkaVersion
    )
    testImplementation(
        group = "com.bakdata.fluent-kafka-streams-tests",
        name = "schema-registry-mock-junit5",
        version = fluentKafkaVersion
    )
    val kafkaVersion: String by project
    testImplementation(group = "net.mguenther.kafka", name = "kafka-junit", version = kafkaVersion) {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }
    implementation(group = "com.opencsv", name = "opencsv", version = "5.2")
}

jib {
    from {
        image = "eclipse-temurin:17.0.6_10-jre"
    }
}
