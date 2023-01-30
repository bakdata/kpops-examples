#!/bin/bash

# helm repo add confluentinc https://confluentinc.github.io/cp-helm-charts/ &&
# helm repo add redpanda-console 'https://dl.redpanda.com/public/console/helm/charts/'  &&
helm repo update

helm upgrade \
    --install \
    --version 0.6.1 \
    --values ./kafka.yaml \
    --namespace kpops-word-count \
    k8kafka confluentinc/cp-helm-charts
    # --create-namespace \
    # --wait \

helm upgrade \
    --install \
    --values ./values-redpanda.yaml \
    --namespace kpops-word-count \
    redpanda redpanda-console/console
    # --create-namespace \
    # --wait \