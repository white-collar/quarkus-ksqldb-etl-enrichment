package org.demo.enricherservice;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.Produced;
import org.demo.enricherservice.dto.*;

@ApplicationScoped
public class StreamsApp {

    @Produces
    public Topology topology() {

        System.out.println("[enricher-service] building topology");

        // SerDes
        ObjectMapperSerde<Order> orderSerde = new ObjectMapperSerde<>(Order.class);
        ObjectMapperSerde<User> userSerde = new ObjectMapperSerde<>(User.class);
        ObjectMapperSerde<Product> productSerde = new ObjectMapperSerde<>(Product.class);
        ObjectMapperSerde<EnrichedOrder> enrichedOrderSerde = new ObjectMapperSerde<>(EnrichedOrder.class);
        ObjectMapperSerde<FullyEnrichedOrder> finalSerde = new ObjectMapperSerde<>(FullyEnrichedOrder.class);

        StreamsBuilder builder = new StreamsBuilder();

        var orders = builder.stream(
                "orders",
                Consumed.with(Serdes.String(), orderSerde)
        );

        var users = builder.table(
                "users",
                Consumed.with(Serdes.String(), userSerde)
        );

        var products = builder.table(
                "products",
                Consumed.with(Serdes.String(), productSerde)
        );

        // FIRST JOIN: orders + users
        var withUser = orders.leftJoin(
                users,
                EnrichedOrder::new,
                Joined.with(Serdes.String(), orderSerde, userSerde)
        );

        // REKEY by productId
        var byProduct = withUser.selectKey(
                (oldKey, enriched) -> enriched.order()
        );

        // SECOND JOIN: add product info
        var finalEnriched = byProduct.leftJoin(
                products,
                FullyEnrichedOrder::new,
                Joined.with(Serdes.String(), enrichedOrderSerde, productSerde)
        );

        // Debug log
        finalEnriched.peek((key, value) ->
                System.out.println("ENRICHED key=" + key + " value=" + value)
        );

        // Sink
        finalEnriched.to(
                "orders-enriched",
                Produced.with(Serdes.String(), finalSerde)
        );

        Topology topology = builder.build();
        System.out.println(topology.describe());

        return topology;
    }
}
