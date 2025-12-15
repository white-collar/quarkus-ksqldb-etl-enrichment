#!/usr/bin/env bash
set -e
echo "Consuming orders_enriched topic (from beginning). Use Ctrl+C to stop."
docker run --rm --network host bitnami/kafka:3.6 kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic orders_enriched --from-beginning
