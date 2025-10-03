package ar.edu.utn.dds.k3003.worker;

import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkerStarter {

    private Connection connection;

    @PostConstruct
    public void startWorker() throws Exception {
        System.out.println("Starting worker inside Spring Boot...");

        Map<String, String> env = System.getenv();
        ConnectionFactory factory = new ConnectionFactory();

        // Safer: use CLOUDAMQP_URL if provided
        if (env.containsKey("CLOUDAMQP_URL")) {
            factory.setUri(env.get("CLOUDAMQP_URL"));
        } else {
            factory.setHost(env.get("QUEUE_HOST"));
            factory.setUsername(env.get("QUEUE_USERNAME"));
            factory.setPassword(env.get("QUEUE_PASSWORD"));
            factory.setVirtualHost(env.getOrDefault("QUEUE_USERNAME", "/"));
        }

        this.connection = factory.newConnection();
        Channel channel = connection.createChannel();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("yourPU");
        hechoWorker worker = new hechoWorker(channel, env.get("QUEUE_NAME"), emf);
        worker.init();

        System.out.println("Worker initialized and consuming messages!");
    }

    @PreDestroy
    public void stop() throws Exception {
        if (connection != null && connection.isOpen()) connection.close();
    }
}