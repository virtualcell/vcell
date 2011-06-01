/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;

/**
 * The event set listener interface for the mathOverrides feature.
 */
public interface MathOverridesListener extends java.util.EventListener {
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
void constantAdded(cbit.vcell.solver.MathOverridesEvent event);
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
void constantChanged(cbit.vcell.solver.MathOverridesEvent event);
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
void constantRemoved(cbit.vcell.solver.MathOverridesEvent event);
}
