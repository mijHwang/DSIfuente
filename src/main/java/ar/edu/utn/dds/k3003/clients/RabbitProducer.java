package ar.edu.utn.dds.k3003.clients;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class RabbitProducer {

    private static final Logger log = Logger.getLogger(RabbitProducer.class.getName());
    private final ObjectMapper objectMapper;
    private final Connection connection;

    public RabbitProducer(ObjectMapper objectMapper) throws Exception {
        this.objectMapper = objectMapper;
        this.connection = createConnection();
    }

    private Connection createConnection() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        Map<String, String> env = System.getenv();

        if (env.containsKey("CLOUDAMQP_URL")) {
            factory.setUri(env.get("CLOUDAMQP_URL"));
        } else {
            factory.setHost(env.get("QUEUE_HOST"));
            factory.setUsername(env.get("QUEUE_USERNAME"));
            factory.setPassword(env.get("QUEUE_PASSWORD"));
            factory.setVirtualHost(System.getenv().getOrDefault("QUEUE_USERNAME", "/"));
        }

        factory.setAutomaticRecoveryEnabled(true);
        return factory.newConnection();
    }

    public void sendMessage(String queueName, Object payload) {
        try (Channel channel = connection.createChannel()) {
            // idempotent declaration
            channel.queueDeclare(queueName, true, false, false, null);

            String message = objectMapper.writeValueAsString(payload);

            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            log.info("Sent message to queue '" + queueName + "': " + message);
        } catch (Exception e) {
            log.severe("Error sending message to queue: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}


// problems regarding queue.
/*

Queue name: Make sure QUEUE_NAME matches what the PDI consumer expects.

Optional fields: null values (imagenUrl, ocrText, etiquetasIA) must be tolerated by the consumer.

Date format: "yyyy-MM-dd'T'HH:mm:ss" must match the consumer parser.

Exception handling: RabbitMQ failures throw RuntimeException — may need graceful handling.

Connection lifecycle: RabbitProducer keeps a persistent connection; consider proper shutdown on app exit.*/
