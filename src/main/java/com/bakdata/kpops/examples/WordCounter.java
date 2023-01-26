package com.bakdata.kpops.examples;

import com.bakdata.kafka.KafkaStreamsApplication;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

public class WordCounter extends KafkaStreamsApplication {
    private static final Pattern COMPILE = Pattern.compile("\\W+");

    public static void main(final String[] args) {
        startApplication(new WordCounter(), args);
    }

    @Override
    public void buildTopology(final StreamsBuilder builder) {
        final KStream<String, String> textLines = builder.stream(this.getInputTopics());

        final KTable<String, Long> wordCounts = textLines
            .flatMapValues(value -> Arrays.asList(COMPILE.split(value.toLowerCase())))
            .groupBy((key, value) -> value)
            .count();

        wordCounts.toStream().to(this.getOutputTopic(), Produced.with(Serdes.String(), Serdes.Long()));
    }

    @Override
    public String getUniqueAppId() {
        return String.format("wordCounter-app-%s", this.getOutputTopic());
    }

    @Override
    protected Properties createKafkaProperties() {
        final Properties kafkaProperties = super.createKafkaProperties();
        kafkaProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerializer.class);
        kafkaProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringSerializer.class);
        kafkaProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return kafkaProperties;
    }
}
