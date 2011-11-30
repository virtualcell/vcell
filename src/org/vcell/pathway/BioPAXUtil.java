/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BioPAXUtil {

	public static int MAX_INTERACTION_CONTROL_CHAIN_DEPTH = 10;
	
	public static Interaction getControlledNonControlInteraction(Control control) {
		for(int i = 0; i < MAX_INTERACTION_CONTROL_CHAIN_DEPTH; ++i) {
			Interaction interaction = control.getControlledInteraction();
			if(interaction instanceof Control) {
				control = (Control) interaction;
			} else {
				return interaction;
			}
		}
		return null;
	}
	
	public static Set<Control> findAllControls(Interaction interaction, PathwayModel pathwayModel) {
		return findAllControls(interaction, pathwayModel, 0);
	}

	public static Set<Control> findAllControls(Interaction interaction, PathwayModel pathwayModel, int depth) {
		if(depth >= MAX_INTERACTION_CONTROL_CHAIN_DEPTH) {
			return Collections.<Control>emptySet();
		}
		HashSet<Control> controls = new HashSet<Control>();
		ArrayList<BioPaxObject> parents = pathwayModel.getParents(interaction);
		if(parents != null) {
			for(BioPaxObject bpObject : parents) {
				if(bpObject instanceof Control) {
					Control control = (Control) bpObject;
					controls.add(control);
					controls.addAll(findAllControls(control, pathwayModel, depth + 1));
				}
			}			
		}
		return controls;
	}
	
}
