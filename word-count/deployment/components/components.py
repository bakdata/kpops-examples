from typing_extensions import override
from kpops.components.streams_bootstrap import StreamsApp


class LocalStreamsApp(StreamsApp):
    repo_config: None = None

    @property
    @override
    def helm_chart(self) -> str:
        return "../argo/streams-app"
