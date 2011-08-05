/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;
/**
 * Insert the type's description here.
 * Creation date: (6/19/2006 11:07:01 AM)
 * @author: Fei Gao
 */
public interface VCellMessageEventListener extends java.util.EventListener {
void onVCellMessageEvent(VCellMessageEvent event);
}
