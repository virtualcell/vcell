package org.vcell.restq.services;

import cbit.vcell.modeldb.DatabaseServerImpl;
import jakarta.enterprise.context.ApplicationScoped;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCInfoContainer;

@ApplicationScoped
public class VCInfoContainerService {

    private final DatabaseServerImpl databaseServerImpl;

    public VCInfoContainerService(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
        databaseServerImpl = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
    }

    /**
     * Single bulk collection of all document metadata (BioModels, MathModels, Geometries, Images)
     * visible to the requester, scoped by the resolved identity (anonymous -> public only). This is
     * the REST replacement for the legacy RPC {@code UserMetaDbServer.getVCInfoContainer}.
     */
    public VCInfoContainer getVCInfoContainer(User user) throws DataAccessException {
        return databaseServerImpl.getVCInfoContainer(user);
    }
}
