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

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;


public class PropertyChangeListenerProxyVCell implements PropertyChangeListener {

	private PropertyChangeListener listener = null;
	private boolean bRunOnSwing = false;
	public PropertyChangeListenerProxyVCell(PropertyChangeListener listener) {
		this.listener = listener;
		Class<?> enclosingClass = listener.getClass().getEnclosingClass();
		if (listener instanceof Component 
				|| listener instanceof TableModel
				|| listener instanceof ListModel
				|| listener instanceof TreeModel
				|| enclosingClass != null && Component.class.isAssignableFrom(enclosingClass)
			) {
			bRunOnSwing = true;
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		final PropertyChangeEvent finalEvent = evt;
		if (bRunOnSwing && !SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run(){
						listener.propertyChange(finalEvent);
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			listener.propertyChange(evt);
		}
	}

	public static void removeProxyListener(PropertyChangeSupport pcs, PropertyChangeListener listener) {	
		PropertyChangeListener[] listeners = pcs.getPropertyChangeListeners();
		for (PropertyChangeListener pcl : listeners) {
			if (pcl instanceof PropertyChangeListenerProxyVCell && ((PropertyChangeListenerProxyVCell)pcl).listener == listener) {
				pcs.removePropertyChangeListener(pcl);
				return;
			}
		}
	}
	public static void addProxyListener(PropertyChangeSupport pcs, PropertyChangeListener listener) {
		if(listener instanceof PropertyChangeListenerProxyVCell){
			throw new IllegalArgumentException("listener already proxy");
		}
		PropertyChangeListener[] listeners = pcs.getPropertyChangeListeners();
		for (PropertyChangeListener pcl : listeners) {
			if (pcl == listener ||
				(pcl instanceof PropertyChangeListenerProxyVCell && ((PropertyChangeListenerProxyVCell)pcl).listener == listener)) {
				throw new IllegalArgumentException("duplicate listener "+listener);
			}
		}
		pcs.addPropertyChangeListener(new PropertyChangeListenerProxyVCell(listener));
	}

}
