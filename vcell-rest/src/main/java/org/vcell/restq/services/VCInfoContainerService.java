package org.vcell.restq.services;

import cbit.vcell.modeldb.DatabaseServerImpl;
import jakarta.enterprise.context.ApplicationScoped;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCInfoContainer;

import java.util.Map;

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
        VCInfoContainer vcInfoContainer = databaseServerImpl.getVCInfoContainer(user);

        // issue #1746 Phase 2: attach each BioModel's simulation database keys (bulk, one query) to its
        // child summary so a viewed simulation can be resolved to an owning BioModel across versions.
        BioModelInfo[] bioModelInfos = vcInfoContainer.getBioModelInfos();
        if (bioModelInfos != null && bioModelInfos.length > 0) {
            KeyValue[] bioModelKeys = new KeyValue[bioModelInfos.length];
            for (int i = 0; i < bioModelInfos.length; i++) {
                bioModelKeys[i] = bioModelInfos[i].getVersion().getVersionKey();
            }
            Map<KeyValue, KeyValue[]> simKeysByBioModel = databaseServerImpl.getSimulationKeysForBioModels(bioModelKeys);
            for (BioModelInfo info : bioModelInfos) {
                BioModelChildSummary childSummary = info.getBioModelChildSummary();
                if (childSummary == null) {
                    continue;
                }
                KeyValue[] simKeys = simKeysByBioModel.get(info.getVersion().getVersionKey());
                String[] simKeyStrings = new String[simKeys == null ? 0 : simKeys.length];
                for (int i = 0; i < simKeyStrings.length; i++) {
                    simKeyStrings[i] = simKeys[i].toString();
                }
                childSummary.setSimKeys(simKeyStrings);
            }
        }
        return vcInfoContainer;
    }
}
