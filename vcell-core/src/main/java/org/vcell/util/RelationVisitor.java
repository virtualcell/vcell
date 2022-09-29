/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import cbit.vcell.model.Diagram;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.units.VCUnitDefinition;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.document.KeyValue;

public interface RelationVisitor {
	boolean relate(int int1, int int2);
	boolean relate(boolean bool1, boolean bool2);
	boolean relate(String str1, String str2);
	boolean relate(Expression relatable1, Expression relatable2);
	boolean relate(Relatable relatable1, Relatable relatable2);
	public <T extends Relatable> boolean relateStrict(T[] v1, T[] v2);
	public <T extends Relatable> boolean relate(T[] v1, T[] v2);
	public <T extends Relatable> boolean relateOrNull(T relatable1, T relatable2);
	public <T extends Relatable> boolean relateOrNull(T[] relatable1, T[] relatable2);
	boolean relateOrNull(String str1, String str2);
	boolean relate(KeyValue key1, KeyValue key2);
	boolean relate(VCUnitDefinition unitDefinition, VCUnitDefinition unitDefinition1);
	boolean relate(SpeciesPattern speciesPattern, SpeciesPattern speciesPattern1);
	boolean relateStrict(Diagram[] fieldDiagrams, Diagram[] fieldDiagrams1);
	boolean relate(ModelUnitSystem unitSystem, ModelUnitSystem unitSystem1);
	boolean relate(Model.StructureTopology structureTopology, Model.StructureTopology structureTopology1);
	boolean relate(Model.ElectricalTopology electricalTopology, Model.ElectricalTopology electricalTopology1);
	boolean relate(Model.RbmModelContainer rbmModelContainer, Model.RbmModelContainer rbmModelContainer1);
}
