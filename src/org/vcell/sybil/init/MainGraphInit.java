/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.init;

/*   InitMain  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2009
 *   Provides the universe in which the application runs, meaning this class
 *   will be instantiated exactly once under any circumstances.
 */

import org.vcell.sybil.gui.graph.GUIGraphInit;

public class MainGraphInit extends GUIGraphInit implements MainInit.SubInitGraph {

}
