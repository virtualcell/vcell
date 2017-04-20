package org.vcell.util;

import cbit.rmi.event.DataJobListener;

public interface DataJobListenerHolder {
	void addDataJobListener(DataJobListener dataJobListener);
	void removeDataJobListener(DataJobListener dataJobListener);
}
