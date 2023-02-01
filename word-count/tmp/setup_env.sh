#!/bin/bash

helm repo add confluentinc https://confluentinc.github.io/cp-helm-charts/ &&
helm repo add redpanda-console 'https://dl.redpanda.com/public/console/helm/charts/'  &&
helm repo update

helm upgrade \
    --install \
    --version 0.6.1 \
    --values ./kafka.yaml \
    --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster \
    --namespace kpops-word-count \
    k8kafka confluentinc/cp-helm-charts


helm upgrade \
    --install \
    --values ./values-redpanda.yaml \
    --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster \
    --namespace kpops-word-count \
    redpanda redpanda-console/console
