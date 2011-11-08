/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.normalform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.uome.core.UOMEBinaryExpression;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMESingleUnitExpression;
import org.vcell.uome.core.UOMEUnit;
import org.vcell.uome.normalform.UOMENormalFormFactory.CanNotCreateNormalFormException;

public class UOMENormalFormPool {

	protected final List<UOMEUnit> baseUnitList = new ArrayList<UOMEUnit>();
	protected final Set<UOMEUnit> baseUnits = new HashSet<UOMEUnit>();
	protected final Set<UOMEUnit> unitsEqualOne = new HashSet<UOMEUnit>();
	protected final UOMENormalFormFactory factory;
	protected Map<UOMEUnit, Set<UOMENormalForm>> unit2forms = new HashMap<UOMEUnit, Set<UOMENormalForm>>();
	
	public UOMENormalFormPool(List<UOMEUnit> baseUnits, Set<UOMEUnit> unitsEqualOne, UOMENormalFormFactory factory) {
		this.baseUnitList.addAll(baseUnits);
		this.baseUnits.addAll(baseUnits);
		this.unitsEqualOne.addAll(unitsEqualOne);
		this.factory = factory;
	}
	
	public Set<UOMENormalForm> getNormalForms(UOMEUnit unit) {
		Set<UOMENormalForm> forms = unit2forms.get(unit);
		if(forms == null) { 
			forms = findNormalForms(unit); 
			unit2forms.put(unit, forms);
		}
		return forms;
	}
	
	public Set<UOMENormalForm> findNormalForms(UOMEUnit unit) {
		Set<UOMENormalForm> forms = new HashSet<UOMENormalForm>();
		if(baseUnits.contains(unit)) {
			forms.add(factory.createNormalUnit(unit));
		}
		for(UOMEExpression expression : unit.getExpressions()) {
			if(expression instanceof UOMESingleUnitExpression) {
				UOMESingleUnitExpression usuExpression = (UOMESingleUnitExpression) expression;
				Set<UOMENormalForm> forms1 = getNormalForms(usuExpression.getUnit());
				for(UOMENormalForm form1 : forms1) {
					try {
						forms.add(factory.createNormalSubstitution(usuExpression, form1));
					} catch (CanNotCreateNormalFormException e) { }
				}
			} else if(expression instanceof UOMEBinaryExpression) {
				UOMEBinaryExpression ubuExpression = (UOMEBinaryExpression) expression;
				Set<UOMENormalForm> forms1 = getNormalForms(ubuExpression.getUnit1());
				Set<UOMENormalForm> forms2 = getNormalForms(ubuExpression.getUnit2());
				for(UOMENormalForm form1 : forms1) {
					for(UOMENormalForm form2 : forms2) {
						try {
							forms.add(factory.createNormalSubstitution(ubuExpression, form1, form2));
						} catch (CanNotCreateNormalFormException e) { }
					}					
				}
			}
		}
		return forms;
	}
	
}
