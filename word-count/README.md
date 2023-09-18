# Word-count Pipeline

Read how to deploy and destroy this pipeline in the official [KPOps Documentation](https://bakdata.github.io/kpops/latest/user/getting-started/quick-start/).

## Modify applications

Create a Pull Request, then after merging, checkout the main branch and run the following commands.

```shell
# deploy producer app
gradle jib -Djib.to.image=us.gcr.io/gcp-bakdata-cluster/kpops-word-count-data-producer -Djib.container.mainClass=com.bakdata.kpops.examples.SentenceProducer


# deploy streams-app
gradle jib -Djib.to.image=us.gcr.io/gcp-bakdata-cluster/kpops-word-count-streams-app -Djib.container.mainClass=com.bakdata.kpops.examples.WordCountApplication
```
