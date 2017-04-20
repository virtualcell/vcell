/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;

/**
 * Insert the type's description here.
 * Creation date: (10/7/00 1:07:55 PM)
 * @author: 
 */
public interface PanListener extends java.util.EventListener {
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:17:30 PM)
 * @param panEvent cbit.image.PanEvent
 */
void panning(PanEvent panEvent);
}
