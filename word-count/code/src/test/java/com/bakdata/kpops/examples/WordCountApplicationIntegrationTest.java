package com.bakdata.kpops.examples;

import com.bakdata.fluent_kafka_streams_tests.junit5.TestTopologyExtension;
import com.bakdata.kafka.AppConfiguration;
import com.bakdata.kafka.ConfiguredStreamsApp;
import com.bakdata.kafka.StreamsTopicConfig;
import com.bakdata.kafka.TestTopologyFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.net.URL;
import java.util.List;

class WordCountApplicationIntegrationTest {
    public static final String INPUT_TOPIC = "word-count-raw-data";
    private static final String OUTPUT_TOPIC = "word-count-topic";

    private final ConfiguredStreamsApp<WordCountApplication> app = configureStreamsApp();

    @RegisterExtension
    final TestTopologyExtension<String, String> testTopology =
            TestTopologyFactory.createTopologyExtensionWithSchemaRegistry(this.app);

    private static ConfiguredStreamsApp<WordCountApplication> configureStreamsApp() {

        final StreamsTopicConfig topicConfig = StreamsTopicConfig.builder()
                .inputTopics(List.of(INPUT_TOPIC))
                .outputTopic(OUTPUT_TOPIC)
                .build();

        final AppConfiguration<StreamsTopicConfig> appConfig = new AppConfiguration<>(topicConfig);

        final WordCountApplication app = new WordCountApplication();

        return new ConfiguredStreamsApp<>(app, appConfig);
    }

    @AfterEach
    void tearDown() {
        this.app.close();
    }

    @Test
    void shouldCountWordsCorrectly() throws IOException {
        final URL resource = Resources.getResource("test_text.txt");
        final List<String> fixture = Resources.readLines(resource, Charsets.UTF_8);

        for (final String line : fixture) {
            this.testTopology.input().add(null, line);
        }

        this.testTopology.streamOutput(this.app.getTopics().getOutputTopic())
                .expectNextRecord()
                .hasKey("kpops")
                .hasValue("1")
                .expectNextRecord()
                .hasKey("test")
                .hasValue("1")
                .expectNextRecord()
                .hasKey("kpops")
                .hasValue("2")
                .expectNextRecord()
                .hasKey("test2")
                .hasValue("1")
                .expectNextRecord()
                .hasKey("kpops")
                .hasValue("3")
                .expectNoMoreRecord();
    }
}
