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
 * Creation date: (2/10/2004 2:00:22 PM)
 * @author: Fei Gao
 */
public interface WorkerEventSender {
public abstract void addWorkerEventListener(WorkerEventListener listener);


public abstract void removeWorkerEventListener(WorkerEventListener listener);
}
