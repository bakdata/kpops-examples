description = "Word count pipeline with Kafka Streams"
plugins {
    java
    idea
    id("io.freefair.lombok") version "8.11"
    id("com.google.cloud.tools.jib") version "3.4.4"
}

group = "com.bakdata.kpops.examples"

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
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
    val streamsBootstrapVersion = "3.1.0"
    implementation(group = "com.bakdata.kafka", name = "streams-bootstrap-cli", version = streamsBootstrapVersion)
    val log4jVersion = "2.24.2"
    implementation(group = "org.apache.logging.log4j", name = "log4j-slf4j2-impl", version = log4jVersion)
    val guavaVersion = "33.3.1-jre"
    implementation(group = "com.google.guava", name = "guava", version = guavaVersion )

    val junitVersion = "5.11.3"
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
    val assertJVersion = "3.26.3"
    testImplementation(group = "org.assertj", name = "assertj-core", version = assertJVersion)

    testImplementation(group = "com.bakdata.kafka", name = "streams-bootstrap-test", version = streamsBootstrapVersion)
    val fluentKafkaVersion = "2.14.0"
    testImplementation(
        group = "com.bakdata.fluent-kafka-streams-tests",
        name = "fluent-kafka-streams-tests-junit5",
        version = fluentKafkaVersion
    )
    val kafkaJunitVersion = "3.6.0"
    testImplementation(group = "net.mguenther.kafka", name = "kafka-junit", version = kafkaJunitVersion) {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }
}

jib {
    from {
        image = "eclipse-temurin:21.0.5_11-jre"
    }
}
