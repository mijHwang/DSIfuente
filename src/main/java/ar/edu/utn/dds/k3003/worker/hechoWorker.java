package ar.edu.utn.dds.k3003.worker;

import com.rabbitmq.client.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;



public class hechoWorker extends DefaultConsumer {



    private String queueName;
    private EntityManagerFactory entityManagerFactory;

    protected hechoWorker(Channel channel, String queueName) {
        super(channel);
        this.queueName = queueName;
        this.entityManagerFactory = entityManagerFactory;
    }

    private void init() throws IOException {

        this.getChannel().queueDeclare(queueName, false, false, false, null);
        this.getChannel().queueBind(queueName, queueName, "");
        this.getChannel().basicConsume(queueName, false, this);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {

        this.getChannel().basicAck(envelope.getDeliveryTag(), false);
        String analisisId = new String(body,"UTF-8");
        System.out.println("Siguiente payload recibido" + analisisId);
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Worker started you mother fucker!");

        Map<String, String> env = System.getenv();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.get("QUEUE_HOST"));
        factory.setUsername(env.get("QUEUE_USERNAME"));
        factory.setPassword(env.get("QUEUE_PASSWORD"));

        factory.setVirtualHost(env.get("QUEUE_HOST"));
        String queueName = env.get("QUEUE_NAME");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        hechoWorker worker = new hechoWorker(channel,queueName);
        worker.init();

        System.out.println("Worker initialized and waiting for messages...");

        // Keep the program running
        while (true) {
            System.out.println("Worker still running...");
            Thread.sleep(5000); // print every 5 seconds
        }
    }

}
