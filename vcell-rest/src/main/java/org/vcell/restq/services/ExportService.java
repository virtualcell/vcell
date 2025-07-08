package org.vcell.restq.services;

import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ExportHistoryDBDriver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

@ApplicationScoped
public class ExportService {
    private final DatabaseServerImpl databaseServer;

    @Inject
    public ExportService(AgroalConnectionFactory connectionFactory) throws DataAccessException {
        this.databaseServer = new DatabaseServerImpl(connectionFactory, connectionFactory.getKeyFactory());
    }


    public ExportHistoryDBDriver.ExportHistory getExportHistory(User user) throws DataAccessException {
        // TODO: Pipe down to DB layer
        return null;
    }

    public void addExportHistory(User user, ExportHistoryDBDriver.ExportHistory history) throws DataAccessException {
        databaseServer.addExportHistory(user, history);
    }

}
