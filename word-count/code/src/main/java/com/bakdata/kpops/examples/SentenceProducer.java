package com.bakdata.kpops.examples;

import com.bakdata.kafka.ProducerApp;
import com.bakdata.kafka.ProducerBuilder;
import com.bakdata.kafka.ProducerRunnable;
import com.bakdata.kafka.SerializerConfig;
import com.bakdata.kafka.SimpleKafkaProducerApplication;
import com.google.common.io.Resources;
import lombok.Setter;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.bakdata.kafka.KafkaApplication.startApplication;

@Setter
public class SentenceProducer implements ProducerApp {
    static final String FILE_NAME = "kpops.txt";

    public static void main(final String[] args) {
        startApplication(
                new SimpleKafkaProducerApplication<>(SentenceProducer::new),
                args
        );
    }

    @Override
    public ProducerRunnable buildRunnable(final ProducerBuilder producerBuilder) {
        return () -> {
            try (final Producer<String, String> producer = producerBuilder.createProducer()) {
                final URL url = Resources.getResource(FILE_NAME);
                final List<String> textLines = Resources.readLines(url, StandardCharsets.UTF_8);
                final String outputTopic = producerBuilder.getTopics().getOutputTopic();
                for (final String textLine : textLines) {
                    producer.send(new ProducerRecord<>(outputTopic, null, textLine));
                }
                producer.flush();
            } catch (final IOException e) {
                throw new RuntimeException("Error occurred while reading the .txt file.", e);
            }
        };
    }

    @Override
    public SerializerConfig defaultSerializationConfig() {
        return new SerializerConfig(StringSerializer.class, StringSerializer.class);
    }
}
