package org.vcell.restq.activemq;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.simdata.OutputContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Identifier;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.vcell.restq.QuarkusStartUpTasks;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.Exports.ExportService;

import javax.jms.*;
import java.util.concurrent.ExecutorService;

@ApplicationScoped
public class ExportRequestListenerMQ {
    private static final Logger logger = LogManager.getLogger(ExportRequestListenerMQ.class);

    @Inject
    @Identifier(QuarkusStartUpTasks.internalMQBeanIdentifier)
    QueueConnection queueConnection;

    @Inject
    ExportService exportService;

    @Inject
    ObjectMapper jsonMapper;

    @PostConstruct
    void init() {
        try {
            QueueSession session = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("dataReq");
            session.createTextMessage();
            session.createProducer(destination).send(new ActiveMQMessage());

            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage tm) {
                    try {
                        ExportResource.ExportJob exportJob = jsonMapper.readValue(tm.getText(), ExportResource.ExportJob.class);
                        exportService.startExportJob(exportJob.user(), new OutputContext(exportJob.outputContext()), exportJob.exportSpecs());
                    } catch (JsonProcessingException | JMSException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    logger.error("Message is not a TextMessage");
                }
            });

        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException("Failed to setup JMS manually", e);
        }
    }
}
