/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.ui;

/*   UIPortSpace  --- by Oliver Ruebenacker, UCHC --- June 2008 to October 2009
 *   An interface for a UI component for editing options for a port
 */

import org.vcell.sybil.models.views.SBWorkView;

public interface UIPortSpace extends UIComponent {
	
	public void update(SBWorkView view);
	
}
