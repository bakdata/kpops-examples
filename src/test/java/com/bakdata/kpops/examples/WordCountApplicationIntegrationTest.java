package com.bakdata.kpops.examples;

import com.bakdata.fluent_kafka_streams_tests.junit5.TestTopologyExtension;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class WordCountApplicationIntegrationTest {
    public static final String INPUT_TOPIC = "word-count-raw-data";
    private static final String OUTPUT_TOPIC = "word-count-topic";
    private final WordCountApplication app = createApp();

    @RegisterExtension
    final TestTopologyExtension<String, String> testTopology =
        new TestTopologyExtension<>(this.app::createTopology, this.app.getKafkaProperties());

    private static WordCountApplication createApp() {
        final WordCountApplication app = new WordCountApplication();
        app.setInputTopics(List.of(INPUT_TOPIC));
        app.setOutputTopic(OUTPUT_TOPIC);
        return app;
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

        this.testTopology.streamOutput(this.app.getOutputTopic())
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
