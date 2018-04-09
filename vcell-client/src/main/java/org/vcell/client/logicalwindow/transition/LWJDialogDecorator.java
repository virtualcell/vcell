package org.vcell.client.logicalwindow.transition;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWDialog;
import org.vcell.client.logicalwindow.LWHandle;
import org.vcell.client.logicalwindow.LWMenuItemFactory;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.client.logicalwindow.LWTraits;
import org.vcell.client.logicalwindow.LWTraits.InitialPosition;

/**
 * Decorator which adds Logical Window functionality to existing {@link JDialog}; using {@link LWDialog} preferred
 */
public class LWJDialogDecorator implements LWHandle {
	private static final Map<JDialog, LWJDialogDecorator>  decorators = new HashMap<>( );
	static final Logger LG = LogManager.getLogger(LWJDialogDecorator.class);
	
	private final JDialog jdialog;
	private static final LWTraits traits = new LWTraits(InitialPosition.CENTERED_ON_PARENT);

	private LWJDialogDecorator(JDialog jdialog) {
		super();
		this.jdialog = jdialog;
		LWContainerHandle lwch = LWNamespace.findLWOwner( jdialog.getOwner() );
		if (lwch != null) {
			lwch.manage(this);
		}
	}
	
	/**
	 * @param jdialog not null
	 * @return new or existing decorator
	 */
	public static LWJDialogDecorator decoratorFor(JDialog jdialog) {
		LWJDialogDecorator deco = decorators.get(jdialog);
		if (deco != null) {
			return deco.modalityNormalized();
		}
		deco = new LWJDialogDecorator(jdialog);
		jdialog.addWindowListener(closeListener);
		decorators.put(jdialog, deco);
		return deco.modalityNormalized();
	}

	@Override
	public LWTraits getTraits() {
		return traits;
	}

	@Override
	public LWModality getLWModality() {
		return LWNamespace.getEquivalentModality(jdialog);
	}

	@Override
	public Window getWindow() {
		return jdialog; 
	}

	@Override
	public Iterator<LWHandle> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public LWContainerHandle getlwParent() {
		return LWNamespace.findLWOwner(jdialog);
	}

	/**
	 * no-op, dialogs don't iconify, I think
	 */
	@Override
	public void unIconify() {
		//no-op, 

	}

	@Override
	public JMenuItem menuItem(int level) {
		return LWMenuItemFactory.menuFor(level, this);
	}

	@Override
	public void closeRecursively() {
		jdialog.dispose();
	}

	@Override
	public String menuDescription() {
		return jdialog.getTitle(); 
	}
	
	/**
	 * remove application / toolkit modality
	 * @return this (for chaining)
	 */
	private LWJDialogDecorator modalityNormalized( ) {
		LWDialog.normalizeModality(jdialog);
		return this;
	}
	
	/**
	 * remove closed {@link JDialog}s from hash
	 */
	private static final WindowListener closeListener = new WindowAdapter() {

		@Override
		public void windowClosed(WindowEvent e) {
			decorators.remove(e.getSource());
		}
		
	};
}
