package org.vcell.restq.services;

import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ExportHistoryRep;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

@ApplicationScoped
public class ExportService {
    private final DatabaseServerImpl databaseServer;

    @Inject
    public ExportService(AgroalConnectionFactory connectionFactory) throws DataAccessException {
        this.databaseServer = new DatabaseServerImpl(connectionFactory, connectionFactory.getKeyFactory());
    }


    public ExportHistoryRep getExportHistory(User user) throws DataAccessException {
        // TODO: Pipe down to DB layer
        return null;
    }

    public void addExportHistory(User user, ExportHistoryRep history) throws DataAccessException {
        databaseServer.addExportHistory(user, history);
    }

}
