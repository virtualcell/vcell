package org.vcell.restq.models;

import cbit.vcell.modeldb.MathModelReferenceRep;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public record MathmodelRef(
        Long mmKey,
        String name,
        String ownerName,
        Long ownerKey,
        Integer versionFlag) {

    public static MathmodelRef fromMathModelReferenceRep(MathModelReferenceRep mathModelReferenceRep) {
        return new MathmodelRef(
                Long.parseLong(mathModelReferenceRep.getMmKey().toString()),
                mathModelReferenceRep.getName(),
                mathModelReferenceRep.getOwner().getName(),
                Long.parseLong(mathModelReferenceRep.getOwner().getID().toString()),
                mathModelReferenceRep.getVersionFlag().intValue()
        );
    }

    public MathModelReferenceRep toMathModelReferenceRep() {
        return new MathModelReferenceRep(
                mmKey!=null ? new KeyValue(Long.toString(mmKey)) : null,
                name,
                new User(ownerName, new KeyValue(Long.toString(ownerKey))),
                versionFlag!=null ? versionFlag.longValue() : null
        );
    }
}