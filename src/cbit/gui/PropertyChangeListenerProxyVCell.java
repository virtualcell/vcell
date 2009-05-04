package cbit.gui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;


public class PropertyChangeListenerProxyVCell implements PropertyChangeListener {

	private PropertyChangeListener listener = null;
	public PropertyChangeListenerProxyVCell(PropertyChangeListener listener) {
		this.listener = listener;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		final PropertyChangeEvent finalEvent = evt;
		Class enclosingClass = listener.getClass().getEnclosingClass();
		if ((listener instanceof Component || enclosingClass != null && Component.class.isAssignableFrom(enclosingClass))  && !SwingUtilities.isEventDispatchThread()) {
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

}
