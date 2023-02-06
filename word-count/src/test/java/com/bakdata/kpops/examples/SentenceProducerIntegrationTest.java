package com.bakdata.kpops.examples;

import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;
import static net.mguenther.kafka.junit.Wait.delay;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.KeyValue;
import net.mguenther.kafka.junit.ReadKeyValues;
import net.mguenther.kafka.junit.TopicConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SentenceProducerIntegrationTest {
    private static final int TIMEOUT_SECONDS = 10;
    private static final String OUTPUT_TOPIC = "word-count-raw-data";
    private final EmbeddedKafkaCluster kafkaCluster = provisionWith(defaultClusterConfig());
    private SentenceProducer sentenceProducer;

    @BeforeEach
    void setup() {
        this.kafkaCluster.start();
        this.sentenceProducer = this.setupApp();
    }

    @AfterEach
    void teardown() {
        this.kafkaCluster.stop();
    }

    @Test
    void shouldRunApp() throws InterruptedException {
        this.kafkaCluster.createTopic(TopicConfig.withName(OUTPUT_TOPIC).useDefaults());

        this.sentenceProducer.run();
        delay(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertThat(
                this.kafkaCluster.read(
                        ReadKeyValues
                                .from(OUTPUT_TOPIC, String.class, String.class)
                                .with(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                                .with(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                                .build()
                )
        )
                .hasSize(4)
                .satisfies(keyValue -> {
                    final KeyValue<String, String> firstRecord = keyValue.get(0);
                    assertThat(firstRecord).isNotNull();
                    assertThat(firstRecord.getKey()).isNull();
                    assertThat(firstRecord.getValue()).isEqualTo("What is KPOps?");
                });
    }

    private SentenceProducer setupApp() {
        final SentenceProducer producerApp = new SentenceProducer();
        producerApp.setBrokers(this.kafkaCluster.getBrokerList());
        producerApp.setOutputTopic(OUTPUT_TOPIC);
        producerApp.setStreamsConfig(Map.of(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000"));
        return producerApp;
    }
}
