FROM confluentinc/cp-kafka-connect:7.3.1
ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"
RUN confluent-hub install --no-prompt jcustenborder/kafka-connect-redis:0.0.4

#CMD exec /bin/sh -c "trap : TERM INT; sleep infinity & wait"
