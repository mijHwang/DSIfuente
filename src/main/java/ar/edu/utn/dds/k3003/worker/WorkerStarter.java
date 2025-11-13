package ar.edu.utn.dds.k3003.worker;
import io.github.cdimascio.dotenv.Dotenv;
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
    Dotenv dotenv = Dotenv.load();

    @Autowired
    private FachadaFuente ff;



    @PostConstruct
    public void startWorker() throws Exception {
        log.info("Starting worker");

        try {


            ConnectionFactory factory = new ConnectionFactory();

            // Safer: use CLOUDAMQP_URL if provided??? i don't think I will ever get one at this point.
            if (dotenv.get("CLOUDAMQP_URL")!=null) {
                factory.setUri(dotenv.get("CLOUDAMQP_URL"));
            } else {
                factory.setHost(dotenv.get("QUEUE_HOST"));
                log.info("Host set");
                factory.setUsername(dotenv.get("QUEUE_USERNAME"));
                log.info("Username set");
                factory.setPassword(dotenv.get("QUEUE_PASSWORD"));
                log.info("Password set");
                factory.setVirtualHost(dotenv.get("QUEUE_USERNAME", "/"));
                log.info("VHost set");
            }

            factory.setAutomaticRecoveryEnabled(true);
            log.info("factory automatic recovery");

            this.connection = factory.newConnection();
            Channel channel = connection.createChannel();
            log.info("channel created");

            String queueName = dotenv.get("QUEUE_NAME", "hechos");

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