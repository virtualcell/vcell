package cbit.vcell.simulation;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This is the event multicaster class to support the cbit.vcell.solver.MathOverridesListenerinterface.
 *
 * WARNING ... BUG IN GENERATED CODE FROM VisualAge for Java !!!!!!!!!
 *
 * This was a generated subclass of AWTEventMulticaster and the remove(EventListener) method was to be 
 * overridden.  Instead, VisualAge for Java generated the method: remove(MathOverridesListener) which doesn't
 * override anything ... and causes ClassCastExceptions upon removal of a listener.
 *
 * The fix for this bug is to change the argument type of the generated remove() method to that of java.util.EventListener.
 *
 */
public class MathOverridesEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.simulation.MathOverridesListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected MathOverridesEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.solver.MathOverridesListener
 * @param a cbit.vcell.solver.MathOverridesListener
 * @param b cbit.vcell.solver.MathOverridesListener
 */
public static cbit.vcell.simulation.MathOverridesListener add(cbit.vcell.simulation.MathOverridesListener a, cbit.vcell.simulation.MathOverridesListener b) {
	return (cbit.vcell.simulation.MathOverridesListener)addInternal(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static java.util.EventListener addInternal(java.util.EventListener a, java.util.EventListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new MathOverridesEventMulticaster(a, b);
}
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantAdded(cbit.vcell.simulation.MathOverridesEvent event) {
	((cbit.vcell.simulation.MathOverridesListener)a).constantAdded(event);
	((cbit.vcell.simulation.MathOverridesListener)b).constantAdded(event);
}
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantChanged(cbit.vcell.simulation.MathOverridesEvent event) {
	((cbit.vcell.simulation.MathOverridesListener)a).constantChanged(event);
	((cbit.vcell.simulation.MathOverridesListener)b).constantChanged(event);
}
/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantRemoved(cbit.vcell.simulation.MathOverridesEvent event) {
	((cbit.vcell.simulation.MathOverridesListener)a).constantRemoved(event);
	((cbit.vcell.simulation.MathOverridesListener)b).constantRemoved(event);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.solver.MathOverridesListener
 * @param l cbit.vcell.solver.MathOverridesListener
 * @param oldl cbit.vcell.solver.MathOverridesListener
 */
public static cbit.vcell.simulation.MathOverridesListener remove(cbit.vcell.simulation.MathOverridesListener l, cbit.vcell.simulation.MathOverridesListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof MathOverridesEventMulticaster)
		return (cbit.vcell.simulation.MathOverridesListener)((cbit.vcell.simulation.MathOverridesEventMulticaster) l).remove(oldl);
	return l;
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl cbit.vcell.solver.MathOverridesListener
 */
protected java.util.EventListener remove(java.util.EventListener oldl) {
	if (oldl == a)  return b;
	if (oldl == b)  return a;
	java.util.EventListener a2 = removeInternal(a, oldl);
	java.util.EventListener b2 = removeInternal(b, oldl);
	if (a2 == a && b2 == b)
		return this;
	return addInternal(a2, b2);
}
}
