#!/bin/bash
helm uninstall  --namespace kpops-word-count wc-data-producer
helm uninstall  --namespace kpops-word-count  wc-word-counter
