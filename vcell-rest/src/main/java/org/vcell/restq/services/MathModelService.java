package org.vcell.restq.services;

import cbit.vcell.modeldb.DatabaseServerImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;

@ApplicationScoped
public class MathModelService {
    private final DatabaseServerImpl databaseServer;

    @Inject
    public MathModelService(AgroalConnectionFactory connectionFactory) throws DataAccessException {
        databaseServer = new DatabaseServerImpl(connectionFactory, connectionFactory.getKeyFactory());
    }

    public MathModelInfo getMathModelInfo(User user, KeyValue keyValue) throws ObjectNotFoundException, DataAccessException {
        return databaseServer.getMathModelInfo(user, keyValue);
    }

    public MathModelInfo[] getMathModelInfos(User user, boolean includePublishedAndShared) throws ObjectNotFoundException, DataAccessException {
        return databaseServer.getMathModelInfos(user, includePublishedAndShared);
    }


    public BigString getMathModelVCML(User user, KeyValue id) throws ObjectNotFoundException, DataAccessException {
        return databaseServer.getBioModelXML(user, id);
    }

    public BigString saveModel(User user, BigString xml, String newName, String[] simNames) throws DataAccessException {
        if (newName != null){
            return databaseServer.saveMathModelAs(user, xml, newName, simNames);
        }
        return databaseServer.saveMathModel(user, xml, simNames);
    }


    public void deleteMathModelVCML(User user, KeyValue id) throws ObjectNotFoundException, DataAccessException {
        databaseServer.deleteMathModel(user, id);
    }

}
