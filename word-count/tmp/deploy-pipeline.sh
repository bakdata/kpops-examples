#!/bin/bash

helm upgrade --debug --install  --values values-dataproducer.yaml  --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count wc-data-producer bakdata-common/producer-app
helm upgrade --debug --install  --values values-wordcounter.yaml --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count  wc-word-counter bakdata-common/streams-app

# curl -s -X POST -H 'Content-Type: application/json' --data @kafka-connect-redis.json http://localhost:8083/connectors
# curl -X DELETE http://localhost:8083/connectors/RedisSinkConnector
