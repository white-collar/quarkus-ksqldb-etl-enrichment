# Orders Enrichment Demo (Pain Zone)

This scaffold contains minimal Quarkus-based services and a Kafka Streams enricher to demonstrate the "Order + User + Product" enrichment scenario.

Important: This is a minimal educational scaffold. Adjust versions and settings before using in production.

## Quick start

1. Start Kafka & UI:
   docker compose up -d

2. Build services (requires Java 17+, Maven):
   mvn -T1C -pl user-service,product-service,order-service,enricher-service -am package -DskipTests

3. Run services locally (or build Docker images). Each service listens on:
   - user-service: 8081
   - product-service: 8082
   - order-service: 8083
   - enricher-service: (runs Kafka Streams app on startup)

   Example: run user-service with:
     java -jar user-service/target/*-runner.jar

4. Produce sample events:
   ./scripts/produce-sample-users.sh
   ./scripts/produce-sample-products.sh
   ./scripts/produce-sample-orders.sh

5. Consume enriched topic:
   ./scripts/consume-topics.sh

## What you'll feel

- The enricher uses KTable for users & products and KStream for orders.
- If you restart enricher, state will be restored from changelogs (observe time).
- Repartitioning & keying mismatches are common pain points.
