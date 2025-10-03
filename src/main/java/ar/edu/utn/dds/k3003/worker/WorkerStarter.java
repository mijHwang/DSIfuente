package ar.edu.utn.dds.k3003.worker;

import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@Component
public class WorkerStarter {

    private static final Logger log = Logger.getLogger(String.valueOf(WorkerStarter.class));

    private Connection connection;

    @Autowired
    private EntityManagerFactory emf;

    @PostConstruct
    public void startWorker() throws Exception {
        log.info("Starting worker");

        try {


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

            factory.setAutomaticRecoveryEnabled(true);


            this.connection = factory.newConnection();
            Channel channel = connection.createChannel();

            hechoWorker worker = new hechoWorker(channel, env.get("QUEUE_NAME"), emf);
            worker.init();

            log.info("Worker initialized and consuming messages!");
        }catch (Exception e) {

            log.info("Failed to start worker");
        }

    }

    @PreDestroy
    public void stop() throws Exception {
        if (connection != null && connection.isOpen()) connection.close();
    }
}