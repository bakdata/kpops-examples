# Word-count Pipeline

Read how to deploy and destroy this pipeline in the official [KPOps Documentation](https://bakdata.github.io/kpops/latest/user/getting-started/quick-start/).

## Modify applications

Create a Pull Request, then after merging, checkout the main branch and run the following commands.

```shell
# deploy producer app
gradle jib -Djib.to.image=bakdata/kpops-demo-sentence-producer -Djib.container.mainClass=com.bakdata.kpops.examples.SentenceProducer


# deploy streams-app
gradle jib -Djib.to.image=bakdata/kpops-demo-word-count-app -Djib.container.mainClass=com.bakdata.kpops.examples.WordCountApplication
```
