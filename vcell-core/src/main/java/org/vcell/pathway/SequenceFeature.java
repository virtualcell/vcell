/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

/**
 * BioPAX Level 3 SequenceFeature.
 * See: http://www.biopax.org/owldoc/Level3/SequenceFeature.html
 */
public class SequenceFeature extends EntityFeatureImpl {

    private ArrayList<SequenceInterval> featureLocation = new ArrayList<>();
    private ArrayList<SequenceModificationVocabulary> modificationType = new ArrayList<>();

    public ArrayList<SequenceModificationVocabulary> getModificationType() {
        return modificationType;
    }

    public void setModificationType(ArrayList<SequenceModificationVocabulary> modificationType) {
        this.modificationType = modificationType;
    }

    @Override
    public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject) {
        super.replace(objectProxy, concreteObject);

        for (int i = 0; i < featureLocation.size(); i++) {
            if (featureLocation.get(i) == objectProxy) {
                featureLocation.set(i, (SequenceInterval) concreteObject);
            }
        }
        for (int i = 0; i < modificationType.size(); i++) {
            if (modificationType.get(i) == objectProxy) {
                modificationType.set(i, (SequenceModificationVocabulary) concreteObject);
            }
        }
    }

    @Override
    public void replace(
            HashMap<String, BioPaxObject> resourceMap,
            HashSet<BioPaxObject> replacedBPObjects
    ) {
        super.replace(resourceMap, replacedBPObjects);

        for (int i = 0; i < featureLocation.size(); i++) {
            Object item = featureLocation.get(i);
            if (item instanceof RdfObjectProxy) {
                RdfObjectProxy proxy = (RdfObjectProxy) item;
                BioPaxObject obj = resourceMap.get(proxy.getID());
                if (obj != null) {
                    featureLocation.set(i, (SequenceInterval) obj);
                }
            }
        }

        for (int i = 0; i < modificationType.size(); i++) {
            Object item = modificationType.get(i);
            if (item instanceof RdfObjectProxy) {
                RdfObjectProxy proxy = (RdfObjectProxy) item;
                BioPaxObject obj = resourceMap.get(proxy.getID());
                if (obj != null) {
                    modificationType.set(i, (SequenceModificationVocabulary) obj);
                }
            }
        }
    }

    @Override
    public void showChildren(StringBuffer sb, int level) {
        super.showChildren(sb, level);
        printObjects(sb, "featureLocation", featureLocation, level);
        printObjects(sb, "modificationType", modificationType, level);
    }

    public static final String typeName = "SequenceFeature";

    @Override
    public String getDisplayType() {
        return typeName;
    }
}
