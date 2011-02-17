package cbit.gui.graph;

/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */

import java.util.EventListener;

public interface GraphListener extends EventListener {
	void graphChanged(GraphEvent event);
}