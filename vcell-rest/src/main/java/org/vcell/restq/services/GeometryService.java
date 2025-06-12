package org.vcell.restq.services;

import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.modeldb.DatabaseServerImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

@ApplicationScoped
public class GeometryService {
    private final DatabaseServerImpl databaseServer;

    @Inject
    public GeometryService(AgroalConnectionFactory connectionFactory) throws DataAccessException {
        databaseServer = new DatabaseServerImpl(connectionFactory, connectionFactory.getKeyFactory());
    }

    public GeometryInfo getGeometryInfo(User user, KeyValue keyValue) throws ObjectNotFoundException, DataAccessException {
        return databaseServer.getGeometryInfo(user, keyValue);
    }

    public GeometryInfo[] getGeometryInfos(User user, boolean includePublishedAndShared) throws ObjectNotFoundException, DataAccessException {
        return databaseServer.getGeometryInfos(user, includePublishedAndShared);
    }


    public BigString getGeometryVCML(User user, KeyValue id) throws ObjectNotFoundException, DataAccessException {
        return databaseServer.getGeometryXML(user, id);
    }

    public BigString saveGeometry(User user, BigString xml, String newName) throws DataAccessException {
        if (newName != null){
            return databaseServer.saveGeometryAs(user, xml, newName);
        }
        return databaseServer.saveGeometry(user, xml);
    }


    public void deleteGeometryVCML(User user, KeyValue id) throws ObjectNotFoundException, DataAccessException {
        databaseServer.deleteGeometry(user, id);
    }
}
