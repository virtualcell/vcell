/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.NameScope;
/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 12:40:03 PM)
 * @author: Jim Schaff
 */
public abstract class BioNameScope extends AbstractNameScope {
	
	public enum NamescopeType {
		modelType,
		structureType,
		reactionStepType,
		reactionRuleType,

		simulationContextType,
		speciesContextSpecType,
		reactionSpecType,
		bioeventType,
		electricalDeviceType,
		electricalStimulusType,

		mathmappingType,
		structureMappingType, 
		spatialProcessType,
	}
/**
 * BioNameScope constructor comment.
 */
public BioNameScope() {
	super();
}

/**
 * the default for BioNameScope subclasses is to treat the ReservedSymbolNameScope as a peer
 * see ReservedSymbolNameScope.isPeer() which accepts any subclass of BioNameScope as a peer).
 */
public boolean isPeer(NameScope nameScope){
		return false;
}

public abstract NamescopeType getNamescopeType();
}
