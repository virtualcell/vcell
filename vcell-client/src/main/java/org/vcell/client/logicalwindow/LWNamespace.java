package org.vcell.client.logicalwindow;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Window;
import java.util.Objects;

import javax.swing.JMenuBar;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.client.logicalwindow.LWHandle.LWModality;
import org.vcell.util.BeanUtils;

import edu.uchc.connjur.wb.ExecutionTrace;

/**
 * utility methods for package
 */
public interface LWNamespace {
	/**
	 * holder for {@link Log4JLogger}
	 */
	public static class LGHolder {
		private final static Logger LG = Logger.getLogger(LWNamespace.class);
	}

	/**
	 * restore Frame to normal if it's currently iconified
	 * @param frame not null
	 */
	public static void unIconify(Frame frame) {
		int st = frame.getState();
		if (st == Frame.ICONIFIED) {
			frame.setState(Frame.NORMAL);
		}
	}

	/**
	 * arrange children
	 * @param w handle to arrange for, not null
	 * @return last Window positioned
	 */
	public static Window positionChildren(LWHandle w) {
		w.unIconify();
		Window lastW = w.getWindow();
		lastW.toFront();
		for (LWHandle childHw : w ) {
			Window child = childHw.getWindow();
			stagger(lastW,child);
			lastW = child;
			lastW = positionChildren(childHw);
		}
		return lastW;
	}

	/**
	 * arrange children from logically topmost to specified 
	 * @param to child to stop at (not null)
	 */
	public static void positionTopDownTo(LWHandle to) {
		LWHandle starting = to;
		LWHandle p = starting.getlwParent();
		while (p != null) {
			starting = p;
			p = starting.getlwParent();
		}
		positionChildrenTo(starting, to);
	}

	/**
	 * arrange children
	 * @param from handle to arrange for, not null
	 * @param to handle to stop at  
	 * @return last Window positioned
	 */
	public static Window positionChildrenTo(LWHandle from, LWHandle to) {
		from.unIconify();
		Window lastW = from.getWindow();
		lastW.toFront();
		if (from != to) {
			for (LWHandle childHw : from ) {
				Window child = childHw.getWindow();
				stagger(lastW,child);
				lastW = positionChildrenTo(childHw,to);
			}
		}
		return lastW;
	}

	/**
	 * position window relative another window
	 * @param reference window to position relative to not null
	 * @param positioned window to position not null
	 */
	public static void stagger(Container reference, Window positioned) {
		Insets insets = reference.getInsets();
		int x = reference.getX() + insets.top;
		int y = reference.getY() + insets.top;
		positioned.setLocation(x,y);
	}
	/**
	 * find specific type window owner of component, if any
	 * @param swingParent could be null
	 * @return or null
	 */
	public static <T> T findOwnerOfType(Class<? extends T> clzz,Component swingParent) {
		final Logger lg = LGHolder.LG; 
		T t = BeanUtils.downcast(clzz, swingParent);
		if (t != null) {
			return t;
		}
		if (lg.isDebugEnabled( )) {
			lg.debug(ExecutionTrace.justClassName(swingParent) + " does not implement " + ExecutionTrace.justClassName(clzz));

		}
		Container up = swingParent.getParent();
		if (up == null) {
			lg.error("top level object " + ExecutionTrace.justClassName(swingParent) + " does not implement "  + ExecutionTrace.justClassName(clzz));
			return null;
		}
		return findOwnerOfType(clzz,up);
	}

	/**
	 * find logical window owner of component, if any
	 * @param swingParent could be null
	 * @return logical owner or null
	 */
	public static LWContainerHandle findLWOwner(Component swingParent) {
		return findOwnerOfType(LWContainerHandle.class, swingParent);
	}

	/**
	 * @param awtModality not null
	 * @return {@link LWModality} that best matches awt modality 
	 */
	public static LWModality bestModalityMatch(ModalityType awtModality) {
		Objects.requireNonNull(awtModality);
		switch (awtModality) {
		case MODELESS:
			return LWModality.MODELESS;
		case DOCUMENT_MODAL:
		case APPLICATION_MODAL:
		case TOOLKIT_MODAL: 
			return LWModality.PARENT_ONLY;
		default:
			return LWModality.PARENT_ONLY;
		}
	}
	/**
	 * @param dialog not null
	 * @return {@link LWModality} that best matches current dialog swing modality
	 */
	public static LWModality getEquivalentModality(Dialog dialog) {
		Objects.requireNonNull(dialog);
		final Logger lg = LGHolder.LG; 
		ModalityType awtModality = dialog.getModalityType();
		switch (awtModality) {
		case MODELESS:
			return LWModality.MODELESS;
		case DOCUMENT_MODAL:
			return LWModality.PARENT_ONLY;
		default:
			if (lg.isEnabledFor(Level.WARN)) {
				lg.warn(ExecutionTrace.justClassName(dialog) + " titled " + dialog.getTitle() + 
						" using unsupported modality "  + awtModality);
			}
			return LWModality.PARENT_ONLY;
		}
	}

	/**
	 * create menu bar for windows that don't have one
	 * 	right justified, iconic
	 * @return new menu bar
	 */
	public static JMenuBar createRightSideIconMenuBar() {
		JMenuBar mb = new JMenuBar();
		mb.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		mb.add( LWTopFrame.createWindowMenu(false));
		return mb;
	}

	/**
	 * get screensize including multi monitor environment 
	 * @return
	 */
	public static Dimension getScreenSize( ) {
		//http://stackoverflow.com/questions/3680221/how-can-i-get-the-monitor-size-in-java
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();	
		return new Dimension(width, height);
	}
}
