package org.vcell.restq;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.resource.PropertyLoader;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.sql.SQLException;

@ApplicationScoped
public class StartUpTasks {
    private final static Logger logger = LogManager.getLogger(StartUpTasks.class);

    @Inject
    AgroalConnectionFactory connectionFactory;

    public void onStartUp(@Observes StartupEvent ev) throws SQLException, DataAccessException {
        logger.info("Executing startup tasks");

        AdminDBTopLevel adminDBTopLevel = new AdminDBTopLevel(connectionFactory);
        User vcellSupport = adminDBTopLevel.getVCellSupportUser(true);
        PropertyLoader.setProperty(PropertyLoader.vcellSupportId, vcellSupport.getID().toString());
    }

}
