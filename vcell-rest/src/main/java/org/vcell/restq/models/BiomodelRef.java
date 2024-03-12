package org.vcell.restq.models;

import cbit.vcell.modeldb.BioModelReferenceRep;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public record BiomodelRef(
        Long bmKey,
        String name,
        String ownerName,
        Long ownerKey,
        Integer versionFlag) {

    public static BiomodelRef fromBioModelReferenceRep(BioModelReferenceRep bioModelReferenceRep) {
        return new BiomodelRef(
                Long.parseLong(bioModelReferenceRep.getBmKey().toString()),
                bioModelReferenceRep.getName(),
                bioModelReferenceRep.getOwner().getName(),
                Long.parseLong(bioModelReferenceRep.getOwner().getID().toString()),
                bioModelReferenceRep.getVersionFlag().intValue()
        );
    }

    public BioModelReferenceRep toBioModelReferenceRep() {
        return new BioModelReferenceRep(
                bmKey!=null ? new KeyValue(Long.toString(bmKey)) : null,
                name,
                new User(ownerName, new KeyValue(Long.toString(ownerKey))),
                versionFlag!=null ? versionFlag.longValue() : null
        );
    }
}

