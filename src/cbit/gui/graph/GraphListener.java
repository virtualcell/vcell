package cbit.gui.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
/**
 * The event set listener interface for the graph feature.
 */
public interface GraphListener extends java.util.EventListener {
	/**
	 * 
	 * @param event cbit.vcell.graph.GraphEvent
	 */
	void graphChanged(cbit.gui.graph.GraphEvent event);
}