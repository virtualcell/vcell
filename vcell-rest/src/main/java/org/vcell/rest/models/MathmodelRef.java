package org.vcell.rest.models;

import cbit.vcell.modeldb.MathModelReferenceRep;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public record MathmodelRef(
        String mmKey,
        String name,
        String ownerName,
        String ownerKey,
        String versionFlag) {

    public static MathmodelRef fromMathModelReferenceRep(MathModelReferenceRep mathModelReferenceRep) {
        return new MathmodelRef(
                mathModelReferenceRep.getMmKey().toString(),
                mathModelReferenceRep.getName(),
                mathModelReferenceRep.getOwner().getName(),
                mathModelReferenceRep.getOwner().getID().toString(),
                mathModelReferenceRep.getVersionFlag().toString()
        );
    }

    public MathModelReferenceRep toMathModelReferenceRep() {
        return new MathModelReferenceRep(
                new KeyValue(mmKey),
                name,
                new User(ownerName, new KeyValue(ownerKey)),
                Long.parseLong(versionFlag)
        );
    }
}