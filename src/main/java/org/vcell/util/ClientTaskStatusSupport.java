/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;


public interface ClientTaskStatusSupport {
	void setMessage(String message);
	void setProgress(int progress);
	int getProgress();
	boolean isInterrupted();
	
	void addProgressDialogListener(ProgressDialogListener progressDialogListener);
}
