FROM confluentinc/cp-kafka-connect:7.1.3
ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"
RUN confluent-hub install --no-prompt jcustenborder/kafka-connect-redis:0.0.4
