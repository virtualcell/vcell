package org.vcell.restq.services;

import cbit.vcell.modeldb.DatabaseServerImpl;
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


    public ExportResource.ExportHistory getExportHistory(User user) throws DataAccessException {
        return new ExportResource.ExportHistory("Hello");
    }

    public void addExportHistory(User user, ExportResource.ExportHistory history) throws DataAccessException {
        databaseServer.addExportHistory(user, history.exportHistory());
    }

}
