package org.apache.activemq.recipes;
/* From:
 Instant Apache ActiveMQ Messaging Application Development How-to
Timothy Bish
Published 2013-05-23
 */

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;

public class AdvisoryConsumerApp implements MessageListener {

    private final String connectionUri = "tcp://code2.cam.uchc.edu:61616";
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer advisoryConsumer;
    private Destination monitored;

    public void before() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(connectionUri);
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        monitored = session.createQueue("dbReqTest4");
//        destination = session.createTopic(
//            AdvisorySupport.getConsumerAdvisoryTopic(monitored).getPhysicalName() + "," +
//            AdvisorySupport.getProducerAdvisoryTopic(monitored).getPhysicalName());
        destination = session.createTopic(
            AdvisorySupport.getConsumerAdvisoryTopic(monitored).getPhysicalName() );
        advisoryConsumer = session.createConsumer(destination);
        advisoryConsumer.setMessageListener(this);
        connection.start();
    }

    public void after() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public void onMessage(Message message) {
        try {
            Destination source = message.getJMSDestination();
            if (source.equals(AdvisorySupport.getConsumerAdvisoryTopic(monitored))) {
                int consumerCount = message.getIntProperty("consumerCount");
                System.out.println("New Consumer Advisory, Consumer Count: " + consumerCount);
            } else if (source.equals(AdvisorySupport.getProducerAdvisoryTopic(monitored))) {
                int producerCount = message.getIntProperty("producerCount");
                System.out.println("New Producer Advisory, Producer Count: " + producerCount);
            }
        } catch (JMSException e) {
        }
    }

    public void run() throws Exception {
        TimeUnit.MINUTES.sleep(10);
    }

    public static void main(String[] args) {
        AdvisoryConsumerApp example = new AdvisoryConsumerApp();
        System.out.print("\n\n\n");
        System.out.println("Starting Advisory Consumer example now...");
        try {
            example.before();
            example.run();
            example.after();
        } catch (Exception e) {
            System.out.println("Caught an exception during the example: " + e.getMessage());
        }
        System.out.println("Finished running the Advisory Consumer example.");
        System.out.print("\n\n\n");
    }
}