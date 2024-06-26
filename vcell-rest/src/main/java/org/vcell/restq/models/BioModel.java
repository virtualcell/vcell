package org.vcell.restq.models;

import cbit.vcell.parser.ExpressionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.util.ArrayList;

public record BioModel(
        String bmKey,
        String name,
        int privacy,
        String[] groupUsers,
        Long savedDate,
        String annot,
        String branchID,
        String physModelKey,  // pydantic complains about 'model_' being a reserved prefix
        String ownerName,
        String ownerKey,
        KeyValue[] simulationKeyList,
        Application[] applications
) {

        public static BioModel fromBioModelRep(cbit.vcell.modeldb.BioModelRep bioModelRep) throws ExpressionException {
            ArrayList<String> groupList = new ArrayList<>();
            for (User user: bioModelRep.getGroupUsers()){
                groupList.add(user.getName());
            }
            return new BioModel(bioModelRep.getBmKey().toString(), bioModelRep.getName(), bioModelRep.getPrivacy(),
                    groupList.toArray(new String[groupList.size()]), bioModelRep.getDate().getTime(), bioModelRep.getAnnot(), bioModelRep.getBranchID().toString(),
                    bioModelRep.getModelRef().toString(), bioModelRep.getOwner().getName(), bioModelRep.getOwner().getID().toString(), bioModelRep.getSimKeyList(), null);
        }

}
