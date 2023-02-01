# Set up the infrastructure

## Prerequisites

### Deploy kafka environment and redpanda

```shell
sh setup_env.sh
```

### Destroy kafka environment and redpanda

```shell
helm uninstall --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count redpanda
helm uninstall --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count k8kafka
```

### Create topics

- use redpanda and create those topics:

    - word-count-countedwords-topic
    - word-count-raw-data-producer-topic

- port forward kafka connect to localhost:8083

## Redis DB

### Install

```shell
helm repo add redis-repo https://charts.bitnami.com/bitnami
helm repo update
helm upgrade --debug --install  --values ../values-redis.yaml --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count wc-redis-db redis-repo/redis
```

### Uninstall

```shell
helm uninstall --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count wc-redis-db
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
redis-cli -h wc-redis-db-master
config get databases # list all databases: should return 1 data base
keys * # this should return all keys in the database.(every word found in the text)
GET key # replace by a word you would like to find out the occurence in the text.
```
