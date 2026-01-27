# ksqlDB Stream Enrichment Demo

## Overview

This repository demonstrates how to use **ksqlDB for ETL** (Extract-Transform-Load) tasks in enterprise-level development.

The example consists of simple microservices that send data to Kafka. To help you understand **why ksqlDB is valuable**, the repository presents **three different approaches** to solving the same problem:

1. **Approach 1**: [Traditional method - rekeying based on strings manipulation; the ugly approach but it allows to help the methodology of concept]
2. **Approach 2**: [Alternative method - using SerDes for serialization/deserialization of string messages which come from Kafka for ETL-processing]
3. **Approach 3**: Using ksqlDB (the recommended solution)

By comparing these approaches, you'll experience the challenges firsthand and see how ksqlDB addresses them.

## Prerequisites

Before diving into this example, you should be familiar with:

- **Kafka concepts**: KStream, KTable, and topology
- **Basic Java development**

Understanding these concepts will help you fully appreciate the differences between approaches.

## Technology Stack

- **Quarkus** for microservices (the patterns shown are framework-agnostic and can be adapted to Spring or other Java frameworks)
- **Node.js** for the demo UI (optional - you can use any REST client instead)

## Important Notes

⚠️ **This is a demonstration project** - it requires additional work before being production-ready.

## Getting Started

The repository includes a simple UI application for sending requests to the microservices. To use it, ensure you have Node.js installed. Alternatively, use any REST client you prefer.

---

## Architecture

The repository contains four Java microservices that simulate a typical e-commerce workflow:

- **Product Service** - Handles product data and inventory
- **User Service** - Manages user information and operations
- **Order Service** - Manages order creation and processing
- **Enricher Service** - Listens to messages from other services and combines them into enriched objects

These services simulate a typical user journey: registration → product selection → order placement.

```java
    1                                +-----------+
    2                                | React UI  |
    3                                +-----------+
    4                                      |
    5                                      v
    6 +-----------------+      +-----------------+      +-----------------+
    7 |   User Service  |      | Product Service |      |  Order Service  |
    8 +-----------------+      +-----------------+      +-----------------+
    9         |                        |                        |
   10         | (users topic)          | (products topic)       | (orders topic)
   11         v                        v                        v
   12 +-----------------------------------------------------------------+
   13 |                                                                 |
   14 |                             Kafka                               |
   15 |                                                                 |
   16 +-----------------------------------------------------------------+
   17         ^                        ^                        ^
   18         | (users topic)          | (products topic)       | (orders topic)
   19         |                        |                        |
   20         |                        v                        |
   21         |              +--------------------+             |
   22         +--------------> Enricher Service <-------------+
   23                        +--------------------+
   24                                  |
   25                                  | (enriched-orders topic)
   26                                  v
   27                        +--------------------+
   28                        |  Consumer Service  |
   29                        +--------------------+

```

Demo UI (Optional)

A simple React UI is included for testing. To run it:

```java
   1 cd react-ui
   2 npm install
   3 npm run dev
```

Once running, navigate to [http://localhost:5173/](http://localhost:5173/) in your browser.

**Note:** The UI is optional. You can use any REST client (Postman, cURL, etc.) to interact with the services directly.

![Screenshot 2026-01-14 at 14.48.17.png](readme/ksqlDB%20Stream%20Enrichment%20Demo/Screenshot_2026-01-14_at_14.48.17.png)

## Running the Services

### Project Structure

The services are organized as Maven modules:

```xml
<modules>
    <module>user-service</module>
    <module>product-service</module>
    <module>order-service</module>
    <module>enricher-service</module>
  </modules>
```

### Build and Run

**1. Build all services:**

```bash
mvn -T1C -pl user-service,product-service,order-service,enricher-service -am package -DskipTests
```

**2. Run three services** (in separate terminals to view logs):

```bash
# User Service
java -jar user-service/target/*-runner.jar

# Product Service
java -jar product-service/target/*-runner.jar

# Order Service
java -jar order-service/target/*-runner.jar

```

## Exploring Different Approaches

The **Enricher Service** has two branches demonstrating different approaches to the same problem:

```bash
- rekeying-based-on-strings
- with-serde-without-ugly-strings
```

**Can I skip these?** If you want to jump directly to ksqlDB, you can skip to the "Introducing ksqlDB" section. However, **we strongly recommend** exploring these branches first—they demonstrate fundamental concepts of joining streams and tables in Kafka. While the syntax may look similar to SQL, the underlying mechanics are quite different and can be surprising if you're new to Kafka and ksqlDB.

## Getting Started with the First Approach

**1. Check out the first branch:**

```bash
git checkout rekeying-based-on-strings
```

**2. Run the Enricher Service:**

```bash
java -jar enricher-service/target/*-runner.jar
```

**3. Review the infrastructure** defined in `docker-compose.yml`.

## Infrastructure Overview

The `docker-compose.yml` file defines the following services:

- **zookeeper** - Apache ZooKeeper instance for managing Kafka brokers and topic configurations
- **kafka** - Apache Kafka broker (the core messaging system for all event streams: orders, users, products, enriched orders)
- **kafka-ui** - Web-based UI for monitoring Kafka clusters, viewing topics, messages, and consumer groups.

### Important Notes

**About ZooKeeper:**

Newer Kafka versions use KRaft (an internal consensus protocol) instead of ZooKeeper. This demo uses the traditional ZooKeeper setup, which is still common in existing projects and doesn't affect the concepts being demonstrated—the approaches discussed are infrastructure-agnostic.

**About Kafka UI:**

The UI is included for convenience when observing topics, streams, and Kafka topology. If you prefer command-line tools for monitoring Kafka, feel free to use those instead—it won't affect the demo.

![Screenshot 2026-01-14 at 14.48.17.png](readme/ksqlDB%20Stream%20Enrichment%20Demo/Screenshot_2026-01-14_at_14.48.17.png)

## Simulating the User Journey

If you're using the UI client, you can simulate a typical e-commerce flow:

### Step 1: User Registration

Click the registration button in the **User Service** block. This sends an HTTP request with user data to the `user-service`. You'll see corresponding logs in the User Service terminal confirming the registration event.

### Step 2: Product Selection

Click **Send** in the **Product Service** block to simulate a user viewing a product (e.g., "opening a product page in a mobile app"). The `product-service` will log this event.

### Step 3: Order Placement

Click the checkout button to simulate the user purchasing the product. The `order-service` will receive this request and log the order creation.

## What's Happening Behind the Scenes

Each service receives HTTP requests and publishes events to Kafka topics:

- **User Service** → `users` topic
- **Product Service** → `products` topic
- **Order Service** → `orders` topic

The implementations of these three services are intentionally simple (and could be optimized), because **the real focus is on the Enricher Service**—where all the interesting work happens.

## The Enricher Service: Combining Event Streams

The **Enricher Service** combines events from three Kafka sources:

- **KStream** for orders
- **KTable** for users
- **KTable** for products

### Why KStream vs KTable?

This design choice is driven by **business logic, not technical limitations**:

- **Orders** change frequently (users place orders constantly) → best modeled as a **KStream** (event stream)
- **Users** and **products** change less frequently (registrations and catalog updates are relatively rare) → best modeled as **KTables** (changelog tables)

If you're unfamiliar with KStreams and KTables, this is a great opportunity to understand these fundamental Kafka concepts in action.

![Screenshot 2026-01-23 at 14.22.57.png](readme/ksqlDB%20Stream%20Enrichment%20Demo/Screenshot_2026-01-23_at_14.22.57.png)

## Approach 1: String-Based Rekeying (Inefficient but Educational)

**Branch:** `rekeying-based-on-strings`

This approach uses **inefficient and cumbersome string manipulation** to process Kafka messages. While not recommended for production, it's valuable for understanding the "low-level" mechanics and experiencing the pain points developers face without proper tooling like SerDes or ksqlDB.

**Key takeaway:** See what happens when you don't have the right tools.

---

## Approach 2: SerDes-Based Processing (More Elegant)

**Branch:** `with-serde-without-ugly-strings`

This solution uses **SerDes** (Serialization/Deserialization) libraries to convert Kafka messages into Java DTOs—similar to how ObjectMapper works, but optimized for Kafka.

**What's different:**

- Messages are serialized/deserialized into type-safe Java objects
- Code is cleaner and more maintainable
- Several DTOs handle the data after serialization

**What stays the same:**

- Still requires manual rekeying to join KStreams and KTables
- The core logic follows the same pattern as the string-based approach

---

## Approach 3: ksqlDB (Eliminating the Enricher Service Entirely)

The final approach uses **ksqlDB**—a native Kafka solution that handles stream processing declaratively.

**The game-changer:**
Instead of writing and maintaining the Enricher Service, we delegate all the enrichment logic to ksqlDB. Our application simply consumes the enriched stream that ksqlDB creates.

**Result:** Less code, less maintenance, more focus on business logic.

## Setting Up ksqlDB

**Branch:** `with-serde-without-ugly-strings`

The updated `docker-compose.yml` in this branch includes two new services:

- **ksqldb-server** - The ksqlDB engine that handles stream processing for us
- **ksqldb-cli** - A command-line interface for interacting with ksqlDB

### Getting Started

**1. Start the infrastructure:**

```bash
docker-compose up -d

```

**2. Stop the Enricher Service** (we no longer need it):

```bash
# Stop the enricher-service if it's running

```

**3. Connect to ksqlDB CLI:**

```bash
docker exec -it ksqldb-cli ksql http://ksqldb-server:8088

```

**4. Run the following ksqlDB commands:**

### Step 1: Create the Users Table

```sql
CREATE TABLE users (
  userId STRING PRIMARY KEY,
  name STRING
) WITH (
  KAFKA_TOPIC = 'users',
  VALUE_FORMAT = 'JSON',
  PARTITIONS = 1,
  REPLICAS = 1
);

```

This creates a ksqlDB table that mirrors what we previously defined in Java code:

```java
var users = builder.table("users", Consumed.with(Serdes.String(), userSerde));

```

We're essentially **replacing Java code with SQL**—ksqlDB handles the implementation details for us.

---

### Step 2: Create the Products Table

```sql
CREATE TABLE products (
  productId STRING PRIMARY KEY,
  name STRING
) WITH (
  KAFKA_TOPIC = 'products',
  VALUE_FORMAT = 'JSON',
  PARTITIONS = 1,
  REPLICAS = 1
);

```

This follows the same pattern as the users table, mapping to the `products` Kafka topic.

---

### Step 3: Create the Orders Stream

```sql
CREATE STREAM orders (
  orderId STRING,
  userId STRING,
  productId STRING,
  quantity INT
) WITH (
  KAFKA_TOPIC = 'orders',
  VALUE_FORMAT = 'JSON',
  PARTITIONS = 1
);

```

This creates a **KStream** in ksqlDB for the `orders` topic—representing the continuous flow of order events

---

### Step 4: Join Orders with Users

Now we can perform the first join—**replacing the awkward rekeying and joining logic** from the Enricher Service. The syntax is similar to standard SQL:

```sql
CREATE STREAM orders_with_user AS
SELECT
  o.orderId     AS orderId,
  o.userId      AS userId,
  o.productId   AS productId,
  o.quantity    AS quantity,
  u.name        AS userName
FROM orders o
LEFT JOIN users u
  ON o.userId = u.userId
EMIT CHANGES;

```

This creates a new stream `orders_with_user` that enriches each order with the corresponding user's name.

---

### Step 5: Create the Final Enriched Stream

Finally, let's enrich the `orders_with_user` stream with product data:

```sql
CREATE STREAM orders_enriched
WITH (
  KAFKA_TOPIC = 'orders_enriched',
  VALUE_FORMAT = 'JSON'
) AS
SELECT
  o.orderId     AS "orderId",              
  AS_VALUE(o.orderId) AS "orderIdValue",   
  o.userId      AS "userId",
  o.userName    AS "userName",
  o.productId   AS "productId",
  p.name        AS "productName",
  o.quantity    AS "quantity"
FROM orders_with_user o
LEFT JOIN products p
  ON o.productId = p.productId
PARTITION BY o.orderId
EMIT CHANGES;

```

This final stream contains fully enriched order data: order details, user information, and product information—all combined through declarative SQL statements instead of complex Java code.

---

Here's a rewritten version:

---

## Testing the Setup

You can test the enriched stream in two ways:

### Option 1: Using ksqlDB CLI

**1. Open a second ksqlDB CLI session:**

```bash
docker exec -it ksqldb-cli ksql http://ksqldb-server:8088

```

**2. Insert test data:**

```sql
INSERT INTO users (userId, name)
VALUES ('u-1', 'Alice');

INSERT INTO products (productId, name)
VALUES ('p-1', 'Laptop');

INSERT INTO orders (orderId, userId, productId, quantity)
VALUES ('o-1', 'u-1', 'p-1', 1);

```

**3. In your first CLI session, query the enriched stream:**

```sql
SELECT * FROM enriched_orders EMIT CHANGES;

```

You should see the enriched order appear in real-time, combining data from all three sources: order details, user name, and product name.

---

### Option 2: Using the UI Application

If you prefer to use the UI application (or REST client), simply send requests as you did earlier:

1. **Register a user** (User Service)
2. **Select a product** (Product Service)
3. **Place an order** (Order Service)

The messages will flow through Kafka and be automatically processed by ksqlDB. You'll see the enriched orders appear in real-time in the CLI session where you ran:

```sql
SELECT * FROM enriched_orders EMIT CHANGES;

```

**What's happening:**

- Your microservices publish events to Kafka topics (`users`, `products`, `orders`)
- ksqlDB automatically joins and enriches the data
- The enriched stream emits the combined results to your console

No Enricher Service needed—ksqlDB handles everything!
