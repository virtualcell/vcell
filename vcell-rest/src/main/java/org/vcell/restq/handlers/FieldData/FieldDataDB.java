package org.vcell.restq.handlers.FieldData;

import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.modeldb.DatabaseServerImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.sql.SQLException;

@ApplicationScoped
public class FieldDataDB {

    private final DatabaseServerImpl databaseServer;

    @Inject
    public FieldDataDB(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
        databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
    }

    public FieldDataDBOperationResults copyNoConflict(User user, FieldDataDBOperationSpec spec) throws DataAccessException {
        return databaseServer.fieldDataDBOperation(user, spec);
    }

    public FieldDataDBOperationResults getAllFieldData(User user, FieldDataDBOperationSpec spec) throws SQLException, DataAccessException {
        return databaseServer.fieldDataDBOperation(user, spec);
    }

    public FieldDataDBOperationResults saveNewFieldData(User user, FieldDataDBOperationSpec spec) throws DataAccessException {
        return databaseServer.fieldDataDBOperation(user, spec);
    }

    public void deleteFieldData(User user, FieldDataDBOperationSpec spec) throws DataAccessException {
        databaseServer.fieldDataDBOperation(user, spec);
    }

}



















