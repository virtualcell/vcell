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

import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

/**
 * BioPAX Level 3 ParticipantDirectionVocabulary.
 * Used to indicate directionality of a PhysicalEntityParticipant.
 * See: http://www.biopax.org/owldoc/Level3/ParticipantDirectionVocabulary.html
 */
public class ParticipantDirectionVocabulary extends ControlledVocabulary {

    @Override
    public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject) {
        super.replace(objectProxy, concreteObject);
    }

    @Override
    public void replace(
            HashMap<String, BioPaxObject> resourceMap,
            HashSet<BioPaxObject> replacedBPObjects
    ) {
        super.replace(resourceMap, replacedBPObjects);
    }
}
