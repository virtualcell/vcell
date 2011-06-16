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

/*   UserInterface  --- by Oliver Ruebenacker, UCHC --- August 2007 to January 2009
 *   An interface for GUIBase or equvalent
 */

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public interface UserInterfaceGraph<S extends UIShape<S>, G extends UIGraph<S, G>> {

	public UIModelGraphSpace<S, G> createGraphSpace();
}
