#!/bin/sh

# Generate a random-uuid
#/kafka/bin/kafka-storage.sh random-uuid

/kafka/bin/kafka-storage.sh format --config /kafka/config/kraft/server.properties --cluster-id 'EP6hyiddQNW5FPrAvR9kWw'
--ignore-formatted

/kafka/bin/kafka-server-start.sh /kafka/config/kraft/server.properties
