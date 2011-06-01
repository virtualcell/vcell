/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml.vcell;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.mapping.StructureMapping;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2006 10:21:14 AM)
 * @author: Anuradha Lakshminarayana
 */
public interface StructureSizeEvaluator {
/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 10:27:55 AM)
 * @return double[]
 * @param structName java.lang.String
 * @param structSize double
 */
void updateAbsoluteStructureSizes(SimulationContext simContext, cbit.vcell.model.Structure struct, double structSize, VCUnitDefinition structSizeUnit) throws Exception;


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 10:27:55 AM)
 * @return double[]
 * @param structName java.lang.String
 * @param structSize double
 */
void updateRelativeStructureSizes(SimulationContext simContext) throws Exception;
}
