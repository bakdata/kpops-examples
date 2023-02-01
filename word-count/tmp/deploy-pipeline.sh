#!/bin/bash

helm repo add bakdata-common https://bakdata.github.io/streams-bootstrap/
helm repo update
helm upgrade --debug --install  --values values-dataproducer.yaml  --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count wc-data-producer bakdata-common/producer-app
helm upgrade --debug --install  --values values-wordcounter.yaml --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count  wc-word-counter bakdata-common/streams-app
