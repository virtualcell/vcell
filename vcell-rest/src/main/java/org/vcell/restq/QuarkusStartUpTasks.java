package org.vcell.restq;

import cbit.sql.ServerStartUpTasks;
import cbit.vcell.modeldb.AdminDBTopLevel;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.QueueConnection;
import java.sql.SQLException;

@ApplicationScoped
public class QuarkusStartUpTasks {
    private final static Logger logger = LogManager.getLogger(QuarkusStartUpTasks.class);

    public static final String internalMQBeanIdentifier = "internal";

    @Inject
    AgroalConnectionFactory connectionFactory;

    public void onStartUp(@Observes StartupEvent ev) throws SQLException, DataAccessException {
        logger.info("Executing startup tasks");
        ServerStartUpTasks.executeStartUpTasks(connectionFactory);
        logger.info("Startup tasks executed successfully");
    }

    @Produces
    @ApplicationScoped
    @Identifier(internalMQBeanIdentifier)
    private QueueConnection getInternalQueueConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        QueueConnection connection = connectionFactory.createQueueConnection("admin", "admin");
        connection.start();
        return connection;
    }

}
