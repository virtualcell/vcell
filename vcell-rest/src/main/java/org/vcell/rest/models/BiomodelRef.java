package org.vcell.rest.models;

import cbit.vcell.modeldb.BioModelReferenceRep;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public record BiomodelRef(
        String bmKey,
        String name,
        String ownerName,
        String ownerKey,
        Long versionFlag) {

    public static BiomodelRef fromBioModelReferenceRep(BioModelReferenceRep bioModelReferenceRep) {
        return new BiomodelRef(
                bioModelReferenceRep.getBmKey().toString(),
                bioModelReferenceRep.getName(),
                bioModelReferenceRep.getOwner().getName(),
                bioModelReferenceRep.getOwner().getID().toString(),
                bioModelReferenceRep.getVersionFlag()
        );
    }

    public BioModelReferenceRep toBioModelReferenceRep() {
        return new BioModelReferenceRep(
                new KeyValue(bmKey),
                name,
                new User(ownerName, new KeyValue(ownerKey)),
                versionFlag
        );
    }
}

