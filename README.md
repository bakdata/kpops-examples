# KPOps Examples

This repository offers various sample pipelines deployed using `KPOps`. Setting up `KPOps` actually involves:

- optionally creating a local Kubernetes cluster
- deploying Apache Kafka and Confluent's Schema Registry
- installing KPOps

## Prerequisites

- [k3d (Version 5.4.7+)](https://k3d.io/v5.4.7/) and [Docker (Version >= v20.10.5)](https://www.docker.com/get-started/)
  or an existing Kubernetes cluster (>= 1.21.0)
- [kubectl (compatible with server version 1.21.0)](https://kubernetes.io/docs/tasks/tools/)
- [Helm (version 3.8.0+)](https://helm.sh)

## Setup Kubernetes with k3d

If you don't have access to an existing Kubernetes cluster,
this section will guide you through creating a local cluster.
We recommend the lightweight Kubernetes distribution [k3s](https://k3s.io/) for this.
[k3d](https://k3d.io/) is a wrapper around k3s in Docker that lets you get started fast.

1. You can install k3d with its installation script:

   ```shell
   wget -q -O - https://raw.githubusercontent.com/k3d-io/k3d/v5.4.7/install.sh | bash
   ```

   For other ways of installing k3d, you can have a look at their
   [installation guide](https://k3d.io/v5.4.7/#installation).

2. The [Kafka deployment](#deploy-kafka) needs a modified Docker image.
   In that case, the image is built and pushed to a Docker registry that holds it.
   If you do not have access to an existing Docker registry, you can use k3d's Docker registry:

   ```shell
   k3d registry create kpops-registry.localhost --port 12345
   ```

3. Now you can create a new cluster called `kpops-examples` that uses the previously created Docker registry:

   ```shell
   k3d cluster create kpops-examples --k3s-arg "--no-deploy=traefik@server:*" --registry-use k3d-kpops-registry.localhost:12345
   ```

   **Note**
   Creating a new k3d cluster automatically configures `kubectl` to connect
   to the local cluster by modifying your `~/.kube/config`.
   In case you manually set the `KUBECONFIG` variable or don't want k3d to modify your config,
   k3d offers [many other options](https://k3d.io/v5.4.6/usage/kubeconfig/#handling-kubeconfigs).

You can check the cluster status with `kubectl get pods -n kube-system`.
If all returned elements have a `STATUS` of `Running` or `Completed`, then the cluster is up and running.

## Deploy Kafka and install `KPOps`

Because the configurations for installing the Kafka ecosystem differ from project to project, the following phases of the setup procedure will be done in each project:

- [Word Count Pipeline](https://github.com/bakdata/kpops-examples/tree/main/word-count)
