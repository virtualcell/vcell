package org.vcell.imagej.common.gui;

import com.google.common.util.concurrent.FutureCallback;

interface FutureProgressCallback<V> extends FutureCallback< V> {
	
	public void progressOccurred(int current, int max);
	
}
