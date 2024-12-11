package com.bakdata.kpops.examples;

import com.bakdata.kafka.KafkaApplication;
import com.bakdata.kafka.SerdeConfig;
import com.bakdata.kafka.SimpleKafkaStreamsApplication;
import com.bakdata.kafka.StreamsApp;
import com.bakdata.kafka.StreamsTopicConfig;
import com.bakdata.kafka.TopologyBuilder;
import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Arrays;
import java.util.regex.Pattern;

public class WordCountApplication implements StreamsApp {
    private static final Pattern COMPILE = Pattern.compile("\\W+");

    public static void main(final String[] args) {
        KafkaApplication.startApplication(
                new SimpleKafkaStreamsApplication<>(WordCountApplication::new),
                args
        );
    }

    @Override
    public void buildTopology(final TopologyBuilder builder) {
        final KStream<String, String> textLines = builder.streamInput();
        final KTable<String, String> wordCounts = textLines
                .flatMapValues(value -> Arrays.asList(COMPILE.split(value.toLowerCase())))
                .groupBy((key, value) -> value)
                .count()
//                 The redis sink connection lacks a Long converter and instead relies on a string converter.
                .mapValues(Object::toString);

        wordCounts.toStream()
                .to(builder.getTopics().getOutputTopic());
    }

    @Override
    public String getUniqueAppId(final StreamsTopicConfig topics) {
        return String.format("word-count-app-%s", topics.getOutputTopic());
    }

    @Override
    public SerdeConfig defaultSerializationConfig() {
        return new SerdeConfig(StringSerde.class, StringSerde.class);
    }
}
