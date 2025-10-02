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
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {

        this.getChannel().basicAck(envelope.getDeliveryTag(), false);
        String analisisId = new String(body,"UTF-8");
        System.out.println("Siguiente payload recibido" + analisisId);
    }

    public static void main(String[] args) throws Exception {

        Map<String, String> env = System.getenv();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.get("jackal.rmq.cloudamqp.com"));
        factory.setUsername(env.get("rxqcxovb"));
        factory.setPassword(env.get("fzwDuHfOd0QPo2eKYyvma4JiwNb4qVGh"));

        factory.setVirtualHost(env.get("rxqcxovb"));
        String queueName = env.get("Fuente_QUEUE");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        hechoWorker worker = new hechoWorker(channel,queueName);
        worker.init();
    }

}
