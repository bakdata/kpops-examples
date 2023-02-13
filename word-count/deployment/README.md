# Set up the infrastructure

## Prerequisites

### Deploy redpanda

```shell
helm repo add redpanda-console 'https://dl.redpanda.com/public/console/helm/charts/'
helm repo update
helm upgrade \
    --install \
    --values ./values-redpanda.yaml \
    --namespace kpops-word-count \
    redpanda redpanda-console/console
```

### Create topics

- use redpanda and create those topics:

  - word-count-countedwords-topic
  - word-count-raw-data-producer-topic

- port forward kafka connect to localhost:8083

```shell
kubectl port-forward deployment/k8kafka-cp-kafka-connect 8083:8083
```

## RedisSinkConnector

### Add connector

```shell
curl -s -X POST -H 'Content-Type: application/json' --data @kafka-connect-redis.json http://localhost:8083/connectors
```

### Remove connector

```shell
curl -X DELETE http://localhost:8083/connectors/RedisSinkConnector
```

## Word count pipeline

### Deploy pipeline

```shell
sh deploy-pipeline.sh
```

### Uninstall pipeline

```shell
sh uninstall-pipeline.sh
```

## Verify that data is stored in redis DB

```shell
# enter redis db master node's shell   and open the redis cli:
redis-cli -h redis-master
config get databases # list all databases: should return 1 data base
keys * # this should return all keys in the database.(every word found in the text)
GET <key> # replace by a word you would like to find out the occurence in the text.
```
