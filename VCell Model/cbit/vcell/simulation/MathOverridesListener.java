package cbit.vcell.simulation;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The event set listener interface for the mathOverrides feature.
 */
public interface MathOverridesListener extends java.util.EventListener {
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
void constantAdded(cbit.vcell.simulation.MathOverridesEvent event);
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
void constantChanged(cbit.vcell.simulation.MathOverridesEvent event);
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
void constantRemoved(cbit.vcell.simulation.MathOverridesEvent event);
}
