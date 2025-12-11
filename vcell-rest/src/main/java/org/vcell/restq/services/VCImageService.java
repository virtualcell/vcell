package org.vcell.restq.services;

import cbit.image.VCImageInfo;
import cbit.vcell.modeldb.DatabaseServerImpl;
import jakarta.enterprise.context.ApplicationScoped;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

@ApplicationScoped
public class VCImageService {

    private final DatabaseServerImpl databaseServerImpl;

    public VCImageService(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
        databaseServerImpl = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
    }

    public String getVCImageXML(User user, KeyValue keyValue) throws DataAccessException {
        return databaseServerImpl.getVCImageXML(user, keyValue).toString();
    }

    public void deleteVCImage(User user, KeyValue keyValue) throws DataAccessException {
        databaseServerImpl.deleteVCImage(user, keyValue);
    }

    public String saveVCImage(User user, String vcImageXML, String name) throws DataAccessException {
        if (name == null || name.isEmpty()) {
            return databaseServerImpl.saveVCImage(user, new BigString(vcImageXML)).toString();
        }
        return databaseServerImpl.saveVCImageAs(user, new BigString(vcImageXML), name).toString();
    }

    public VCImageInfo getVCImageInfo(User user, KeyValue keyValue) throws DataAccessException {
        return databaseServerImpl.getVCImageInfo(user, keyValue);
    }

    public VCImageInfo[] getVCImageInfos(User user, boolean includePublicAndShared) throws DataAccessException {
        return databaseServerImpl.getVCImageInfos(user, includePublicAndShared);
    }

}
