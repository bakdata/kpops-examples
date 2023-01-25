package com.bakdata.kafka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;

@Setter
@Slf4j
public class DataProducer extends KafkaProducerApplication {
    static final String FILE_NAME = "kpops.txt";

    public static void main(final String[] args) {
        startApplication(new DataProducer(), args);
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
        final List<String> textLines = loadTextData(FILE_NAME);
        try (final KafkaProducer<String, String> producer = this.createProducer()) {
            for (int i = 0; i < textLines.size(); i++) {

                log.info("Producing message:  {}---->to <<{}>>", textLines.get(i), this.getOutputTopic());
                this.publish(producer, String.valueOf(i + 1), textLines.get(i));
                log.info("Data produced.");
            }
            producer.flush();
        }
    }

    public static List<String> loadTextData(final String fileName) {
        final ClassLoader classLoader = DataProducer.class.getClassLoader();
        final List<String> textLines = new ArrayList<>();
        final InputStream inputStream = classLoader.getResourceAsStream(fileName);
        try {
            assert inputStream != null;
            try (final BufferedReader br
                    = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    textLines.add(line);
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException("Error occurred while loading .txt file", e);
        }
        return textLines;
    }

    private void publish(final Producer<? super String, ? super String> producer, final String id, final String line) {
        log.info("Producer.send(): {}: {}", id, line);
        producer.send(new ProducerRecord<>(this.getOutputTopic(), id, line));
        log.info("Sent!");
    }
}
