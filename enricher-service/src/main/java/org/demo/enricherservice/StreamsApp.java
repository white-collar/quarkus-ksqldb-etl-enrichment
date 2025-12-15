package org.demo.enricherservice;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

@ApplicationScoped
public class StreamsApp {

    void onStart(@Observes StartupEvent ev) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "enricher-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("kafka.bootstrap.servers", "localhost:9092"));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> orders = builder.stream("orders");
        KTable<String, String> users = builder.table("users");
        KTable<String, String> products = builder.table("products");

        KStream<String, String> ordersByUser = orders.selectKey((key, value) -> {
            try {
                int idx = value.indexOf("\"userId\"");
                if (idx == -1) return key;
                String sub = value.substring(idx+9);
                int start = sub.indexOf('\"')+1;
                int end = sub.indexOf('\"', start);
                return sub.substring(start, end);
            } catch (Exception e) {
                return key;
            }
        });

        KStream<String, String> withUser = ordersByUser.leftJoin(users, (order, user) -> "{\"order\": " + order + ", \"user\": " + (user==null?"null":user) + "}");

        KStream<String, String> byProduct = withUser.selectKey((k, v) -> {
            try {
                int idx = v.indexOf("\"productId\"");
                if (idx == -1) return k;
                String sub = v.substring(idx+12);
                int start = sub.indexOf('"')+1;
                int end = sub.indexOf('"', start);
                return sub.substring(start, end);
            } catch (Exception e) {
                return k;
            }
        });

        KStream<String, String> finalEnriched = byProduct.leftJoin(products, (enr, product) -> "{\"enriched\": " + enr + ", \"product\": " + (product==null?"null":product) + "}");

        finalEnriched.to("orders_enriched");

        Topology t = builder.build();
        KafkaStreams streams = new KafkaStreams(t, props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
