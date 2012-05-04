package cbit.vcell.client;

import cbit.vcell.client.ChildWindowManager.ChildWindow;

public interface ChildWindowListener {

	void closed(ChildWindow childWindow);
	void closing(ChildWindow childWindow);

}
