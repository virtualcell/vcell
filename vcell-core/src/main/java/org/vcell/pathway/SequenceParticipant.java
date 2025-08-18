package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.relationship.PathwayMapping;
import org.vcell.util.Displayable;

/*
 * A minimal BioPAX L3 SequenceParticipant modeled after your PhysicalEntityParticipant.
 * See ontology: http://www.biopax.org/owldoc/Level3/SequenceParticipant.html
 */
public class SequenceParticipant extends PhysicalEntityParticipant implements Displayable {
    private ArrayList<SequenceFeature> sequenceFeature = new ArrayList<>();

    // getter
    public ArrayList<SequenceFeature> getSequenceFeature() {
        return sequenceFeature;
    }

    // setter (or you may prefer an addSequenceFeature helper)
    public void setSequenceFeature(ArrayList<SequenceFeature> seqFeat) {
        this.sequenceFeature = seqFeat;
    }

    @Override
    public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject) {
        super.replace(objectProxy, concreteObject);

        for (int i = 0; i < sequenceFeature.size(); i++) {
            if (sequenceFeature.get(i) == objectProxy) {
                sequenceFeature.set(i, (SequenceFeature) concreteObject);
            }
        }
    }

    @Override
    public void replace(
            HashMap<String, BioPaxObject> resourceMap,
            HashSet<BioPaxObject> replacedBPObjects
    ) {
        super.replace(resourceMap, replacedBPObjects);

        for (int i = 0; i < sequenceFeature.size(); i++) {
            Object item = sequenceFeature.get(i);
            if (item instanceof RdfObjectProxy) {
                RdfObjectProxy proxy = (RdfObjectProxy) item;
                BioPaxObject obj = resourceMap.get(proxy.getID());
                if (obj != null) {
                    sequenceFeature.set(i, (SequenceFeature) obj);
                }
            }
        }
    }

    @Override
    public void showChildren(StringBuffer sb, int level) {
        super.showChildren(sb, level);
        printObjects(sb, "sequenceFeature", sequenceFeature, level);
    }

    public static final String typeName = "SequenceParticipant";

    @Override
    public String getDisplayName() {
        // inherits getDisplayName logic from PhysicalEntityParticipant
        return super.getDisplayName();
    }

    @Override
    public String getDisplayType() {
        return typeName;
    }
}
