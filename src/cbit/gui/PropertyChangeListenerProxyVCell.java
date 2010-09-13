package cbit.gui;

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
		Class enclosingClass = listener.getClass().getEnclosingClass();
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
