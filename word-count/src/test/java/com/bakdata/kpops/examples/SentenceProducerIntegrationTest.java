package com.bakdata.kpops.examples;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;
import static net.mguenther.kafka.junit.Wait.delay;
import static org.assertj.core.api.Assertions.assertThat;

import com.bakdata.schemaregistrymock.junit5.SchemaRegistryMockExtension;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.ReadKeyValues;
import net.mguenther.kafka.junit.TopicConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class SentenceProducerIntegrationTest {
    private static final int TIMEOUT_SECONDS = 10;
    private static final String OUTPUT_TOPIC = "word-count-raw-data";

    @RegisterExtension
    final SchemaRegistryMockExtension schemaRegistryMockExtension = new SchemaRegistryMockExtension();
    private final EmbeddedKafkaCluster kafkaCluster = provisionWith(defaultClusterConfig());


    @BeforeEach
    void setup() {
        this.kafkaCluster.start();
    }

    @AfterEach
    void teardown() {
        this.kafkaCluster.stop();
    }

    @Test
    void shouldRunApp() throws InterruptedException {
        this.kafkaCluster.createTopic(TopicConfig.withName(OUTPUT_TOPIC).useDefaults());

        final SentenceProducer sentenceProducer = this.setupApp();
        sentenceProducer.run();
        delay(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertThat(
            this.kafkaCluster.read(
                ReadKeyValues.from(OUTPUT_TOPIC, String.class, String.class)
                    .with(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                    .with(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                    .with(SCHEMA_REGISTRY_URL_CONFIG, this.schemaRegistryMockExtension.getUrl())
                    .build()
            )
        )
            .hasSize(4)
            .allSatisfy(keyValue -> {
                final String recordKey = keyValue.getKey();
                assertThat(recordKey.length()).isEqualTo(1);
                assertThat(recordKey).isIn("1", "2", "3", "4");
            });
    }

    SentenceProducer setupApp() {
        final SentenceProducer producerApp = new SentenceProducer();
        producerApp.setBrokers(this.kafkaCluster.getBrokerList());
        producerApp.setSchemaRegistryUrl(this.schemaRegistryMockExtension.getUrl());
        producerApp.setOutputTopic(OUTPUT_TOPIC);
        producerApp.setStreamsConfig(Map.of(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000"));
        return producerApp;
    }
}
