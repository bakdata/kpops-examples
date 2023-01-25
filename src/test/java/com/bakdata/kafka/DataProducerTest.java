package com.bakdata.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

public class DataProducerTest {
    @Test
    void shouldLoadTxt() {
        final String filename = "test_text.txt";
        final List<String> textLines = DataProducer.loadTextData(filename);

        assertThat(textLines).hasSize(2);
        assertThat(textLines.get(0)).isEqualTo("Hello");
        assertThat(textLines.get(1)).isEqualTo("World");
    }
}
