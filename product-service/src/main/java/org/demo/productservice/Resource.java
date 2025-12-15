package org.demo.productservice;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

@Path("/products")
public class Resource {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final KafkaProducer<String, String> producer;

    public Resource() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("kafka.bootstrap.servers", "localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(props);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(String body) {
        try {
            String key = null;
            if (body.contains("userId")) key = mapper.readTree(body).get("userId").asText();
            if (body.contains("productId")) key = mapper.readTree(body).get("productId").asText();
            if (body.contains("orderId")) key = mapper.readTree(body).get("orderId").asText();
            producer.send(new ProducerRecord<>("products", key, body));
            return Response.accepted().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
