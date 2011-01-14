package org.vcell.pathway;

import java.util.EventListener;

public interface PathwayListener extends EventListener {

	void pathwayChanged(PathwayEvent event);
	
}
