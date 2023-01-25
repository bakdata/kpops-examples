package com.bakdata.kafka.integration;

import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;
import static net.mguenther.kafka.junit.Wait.delay;
import static org.assertj.core.api.Assertions.assertThat;

import com.bakdata.kafka.DataProducer;
import com.bakdata.schemaregistrymock.junit5.SchemaRegistryMockExtension;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
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

public class DataProducerIntegrationTest {
    private static final int TIMEOUT_SECONDS = 10;
    @RegisterExtension
    final SchemaRegistryMockExtension schemaRegistryMockExtension = new SchemaRegistryMockExtension();
    private final EmbeddedKafkaCluster kafkaCluster = provisionWith(defaultClusterConfig());

    private static final String OUTPUT_TOPIC = "word-count-raw-data";

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
        DataProducer dataProducer = new DataProducer();
        dataProducer = this.setupApp(dataProducer);
        dataProducer.run();
        delay(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertThat(this.kafkaCluster.read(ReadKeyValues.from(OUTPUT_TOPIC, String.class, String.class)
                .with(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .with(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .with(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                        this.schemaRegistryMockExtension.getUrl())
                .build()))
                .hasSize(4)
                .allSatisfy(keyValue -> {
                    final String recordKey = keyValue.getKey();
                    assertThat(recordKey.length()).isEqualTo(1);
                    assertThat(recordKey).isIn("1", "2", "3", "4");
                });
    }

    DataProducer setupApp(final DataProducer producerApp) {
        producerApp.setBrokers(this.kafkaCluster.getBrokerList());
        producerApp.setSchemaRegistryUrl(this.schemaRegistryMockExtension.getUrl());
        producerApp.setOutputTopic(OUTPUT_TOPIC);
        producerApp.setStreamsConfig(Map.of(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000"));
        return producerApp;

    }
}
