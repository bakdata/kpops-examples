# Word count Pipeline

> This is a use case of the example Kafka Streams application for [word count](https://docs.confluent.io/5.5.1/streams/quickstart.html)
> using [streams-bootstrap](https://github.com/bakdata/streams-bootstrap).
> Our word count pipeline consists of a producer app, a streaming app, a SinkConnector, and a Redis database.
> The producer reads a text and produces it into a topic, which is read by the streams app, which counts the number of times each word appears. The streams-app then writes these entries to a different topic, which our Redis connector consumes in order to save the data(words and occurances) in our database.

## Build containers using jib

```shell
gradle jib -Djib.to.image=url-to-container-registry.com/kpops-word-count-data-producer -Djib.container.mainClass=com.bakdata.kafka.DataProducer
gradle jib -Djib.to.image=url-to-container-registry.com/kpops-word-count-streams-app -Djib.container.mainClass=com.bakdata.kafka.WordCounter
```

## Publish Connect Image

```
docker build . -t my-registry/my-wc-redis-connector
docker push my-registry/my-wc-redis-connector
```
