package ar.edu.utn.dds.k3003.worker;
import ar.edu.utn.dds.k3003.app.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class hechoWorker extends DefaultConsumer {




    private final String queueName;
    private final FachadaFuente ff;


    private static final Logger log = Logger.getLogger(String.valueOf(hechoWorker.class));

    protected hechoWorker(Channel channel, String queueName, FachadaFuente ff) {
        super(channel);
        this.queueName = queueName;
        this.ff = ff;
    }

    public void init() throws IOException {

        /*this.getChannel().queueDeclare(queueName, false, false, false, null);
        log.info("queue Declared");*/

        //this.getChannel().queueBind(queueName, queueName, "");

        this.getChannel().basicConsume(queueName, false, this);
        log.info("basic consumer initialized");
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {

        //var em = emf.createEntityManager();

        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

            log.info("Attempting to process message");

            String json = new String(body, StandardCharsets.UTF_8);
            HechoDTO dto = mapper.readValue(json, HechoDTO.class);


            //em.getTransaction().begin();

            ff.agregar(dto);

            //em.getTransaction().commit();

            log.info("message processed and done");
            this.getChannel().basicAck(envelope.getDeliveryTag(), false);
        } catch (Exception e) {

            log.info("error processing message: " + e.toString());
            this.getChannel().basicNack(envelope.getDeliveryTag(), false, false);
        }


    }

    /*public static void main(String[] args) throws Exception {

        System.out.println("Worker started you motherfucker!");

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
    }*/

}
