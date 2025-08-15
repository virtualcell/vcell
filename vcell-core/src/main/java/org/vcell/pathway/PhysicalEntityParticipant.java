package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.*;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.relationship.PathwayMapping;
import org.vcell.util.Displayable;

/*
 * A minimal BioPAX L3 PhysicalEntityParticipant modeled after your PhysicalEntity.
 * See ontology: http://www.biopax.org/owldoc/Level3/PhysicalEntityParticipant.html
 */
public class PhysicalEntityParticipant extends EntityImpl implements Displayable {
    private PhysicalEntity physicalEntity;
    private Integer stoichiometry = 1;  // default per L3 spec
    private ParticipantDirectionVocabulary participantDirection;
    private CellularLocationVocabulary cellularLocation;
    private ArrayList<ExperimentalFormVocabulary> experimentalFormVocabulary = new ArrayList<>();
    private ArrayList<EntityFeature> feature = new ArrayList<>();

    // getters
    public PhysicalEntity getPhysicalEntity() {
        return physicalEntity;
    }
    public Integer getStoichiometry() {
        return stoichiometry;
    }
    public ParticipantDirectionVocabulary getParticipantDirection() {
        return participantDirection;
    }
    public CellularLocationVocabulary getCellularLocation() {
        return cellularLocation;
    }
    public ArrayList<ExperimentalFormVocabulary> getExperimentalFormVocabulary() {
        return experimentalFormVocabulary;
    }
    public ArrayList<EntityFeature> getFeature() {
        return feature;
    }

    // setters
    public void setPhysicalEntity(PhysicalEntity pe) {
        this.physicalEntity = pe;
    }
    public void setStoichiometry(Integer stoich) {
        this.stoichiometry = (stoich == null ? 1 : stoich);
    }
    public void setParticipantDirection(ParticipantDirectionVocabulary dir) {
        this.participantDirection = dir;
    }
    public void setCellularLocation(CellularLocationVocabulary loc) {
        this.cellularLocation = loc;
    }
    public void setExperimentalFormVocabulary(ArrayList<ExperimentalFormVocabulary> efv) {
        this.experimentalFormVocabulary = efv;
    }
    public void setFeature(ArrayList<EntityFeature> feature) {
        this.feature = feature;
    }

    @Override
    public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject) {
        super.replace(objectProxy, concreteObject);

        if (physicalEntity == objectProxy) {
            physicalEntity = (PhysicalEntity) concreteObject;
        }
        if (participantDirection == objectProxy) {
            participantDirection = (ParticipantDirectionVocabulary) concreteObject;
        }
        if (cellularLocation == objectProxy) {
            cellularLocation = (CellularLocationVocabulary) concreteObject;
        }
        for (int i = 0; i < experimentalFormVocabulary.size(); i++) {
            if (experimentalFormVocabulary.get(i) == objectProxy) {
                experimentalFormVocabulary.set(i, (ExperimentalFormVocabulary) concreteObject);
            }
        }
        for (int i = 0; i < feature.size(); i++) {
            if (feature.get(i) == objectProxy) {
                feature.set(i, (EntityFeature) concreteObject);
            }
        }
    }

    @Override
    public void replace(
            HashMap<String, BioPaxObject> resourceMap,
            HashSet<BioPaxObject> replacedBPObjects
    ) {
        super.replace(resourceMap, replacedBPObjects);

        // physicalEntity
        if (physicalEntity instanceof RdfObjectProxy) {
            RdfObjectProxy proxy = (RdfObjectProxy) physicalEntity;
            BioPaxObject obj = resourceMap.get(proxy.getID());
            if (obj != null) {
                physicalEntity = (PhysicalEntity) obj;
            }
        }
        // participantDirection
        if (participantDirection instanceof RdfObjectProxy) {
            RdfObjectProxy proxy = (RdfObjectProxy) participantDirection;
            BioPaxObject obj = resourceMap.get(proxy.getID());
            if (obj != null) {
                participantDirection = (ParticipantDirectionVocabulary) obj;
            }
        }
        // cellularLocation
        if (cellularLocation instanceof RdfObjectProxy) {
            RdfObjectProxy proxy = (RdfObjectProxy) cellularLocation;
            BioPaxObject obj = resourceMap.get(proxy.getID());
            if (obj != null) {
                cellularLocation = (CellularLocationVocabulary) obj;
            }
        }
        // experimentalFormVocabulary list
        for (int i = 0; i < experimentalFormVocabulary.size(); i++) {
            Object item = experimentalFormVocabulary.get(i);
            if (item instanceof RdfObjectProxy) {
                RdfObjectProxy proxy = (RdfObjectProxy) item;
                BioPaxObject obj = resourceMap.get(proxy.getID());
                if (obj != null) {
                    experimentalFormVocabulary.set(i, (ExperimentalFormVocabulary) obj);
                }
            }
        }
        // feature list
        for (int i = 0; i < feature.size(); i++) {
            Object item = feature.get(i);
            if (item instanceof RdfObjectProxy) {
                RdfObjectProxy proxy = (RdfObjectProxy) item;
                BioPaxObject obj = resourceMap.get(proxy.getID());
                if (obj != null) {
                    feature.set(i, (EntityFeature) obj);
                }
            }
        }
    }

    @Override
    public void showChildren(StringBuffer sb, int level) {
        super.showChildren(sb, level);
        printObject(sb, "physicalEntity", physicalEntity, level);
        printInteger(sb, "stoichiometry", stoichiometry, level);
        printObject(sb, "participantDirection", participantDirection, level);
        printObject(sb, "cellularLocation", cellularLocation, level);
        printObjects(sb, "experimentalFormVocabulary", experimentalFormVocabulary, level);
        printObjects(sb, "feature", feature, level);
    }

    public static final String typeName = "PhysicalEntityParticipant";

    @Override
    public String getDisplayName() {
        // TODO: customize display name if needed
        return (getName().isEmpty())
                ? PathwayMapping.getSafetyName(getID())
                : PathwayMapping.getSafetyName(getName().get(0));
    }

    @Override
    public String getDisplayType() {
        return typeName;
    }
}
