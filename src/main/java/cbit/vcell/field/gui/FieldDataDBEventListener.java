/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field.gui;

import java.util.EventListener;

public interface FieldDataDBEventListener extends EventListener {

	void fieldDataDBEvent(FieldDataDBEvent fieldDataDBEvent);
}
