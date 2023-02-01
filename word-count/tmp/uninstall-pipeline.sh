#!/bin/bash
helm uninstall --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count wc-data-producer
helm uninstall --kube-context gke_gcp-bakdata-cluster_us-east1_gcp-bakdata-dev-cluster --namespace kpops-word-count  wc-word-counter
