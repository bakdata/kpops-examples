package com.bakdata.kpops.examples;

import com.bakdata.kafka.KafkaProducerApplication;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

@Setter
@Slf4j
public class SentenceProducer extends KafkaProducerApplication {
    static final String FILE_NAME = "kpops.txt";

    public static void main(final String[] args) {
        startApplication(new SentenceProducer(), args);
    }

    @Override
    protected Properties createKafkaProperties() {
        final Properties kafkaProperties = super.createKafkaProperties();
        kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return kafkaProperties;
    }

    @Override
    protected void runApplication() {
        try (final KafkaProducer<String, String> producer = this.createProducer()) {
            final URL url = Resources.getResource(FILE_NAME);
            final List<String> textLines = Resources.readLines(url, StandardCharsets.UTF_8);

            for (int i = 0; i < textLines.size(); i++) {
                log.info("Producing message:  {}---->to <<{}>>", textLines.get(i), this.getOutputTopic());
                this.publish(producer, String.valueOf(i + 1), textLines.get(i));
                log.info("Data produced.");
            }
            producer.flush();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void publish(final Producer<? super String, ? super String> producer, final String id, final String line) {
        log.info("Producer.send(): {}: {}", id, line);
        producer.send(new ProducerRecord<>(this.getOutputTopic(), id, line));
        log.info("Sent!");
    }
}
