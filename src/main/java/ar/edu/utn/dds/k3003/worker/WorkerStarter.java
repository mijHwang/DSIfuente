package ar.edu.utn.dds.k3003.worker;

import ar.edu.utn.dds.k3003.app.FachadaFuente;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@Component
public class WorkerStarter {

    private static final Logger log = Logger.getLogger(String.valueOf(WorkerStarter.class));

    private Connection connection;

    @Autowired
    private FachadaFuente ff;



    @PostConstruct
    public void startWorker() throws Exception {
        log.info("Starting worker");

        try {


            Map<String, String> env = System.getenv();
            ConnectionFactory factory = new ConnectionFactory();

            // Safer: use CLOUDAMQP_URL if provided??? i don't think I will ever get one at this point.
            if (env.containsKey("CLOUDAMQP_URL")) {
                factory.setUri(env.get("CLOUDAMQP_URL"));
            } else {
                factory.setHost(env.get("QUEUE_HOST"));
                log.info("Host set");
                factory.setUsername(env.get("QUEUE_USERNAME"));
                log.info("Username set");
                factory.setPassword(env.get("QUEUE_PASSWORD"));
                log.info("Password set");
                factory.setVirtualHost(env.getOrDefault("QUEUE_USERNAME", "/"));
                log.info("VHost set");
            }

            factory.setAutomaticRecoveryEnabled(true);
            log.info("factory automatic recovery");

            this.connection = factory.newConnection();
            Channel channel = connection.createChannel();
            log.info("channel created");

            String queueName = System.getenv().getOrDefault("QUEUE_NAME", "hechos");

            log.info("worker created");
            hechoWorker worker = new hechoWorker(channel, queueName, ff);
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