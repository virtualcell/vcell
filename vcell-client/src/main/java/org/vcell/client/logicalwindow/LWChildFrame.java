package org.vcell.client.logicalwindow;

import java.awt.HeadlessException;
import java.awt.Window;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import org.vcell.client.logicalwindow.LWTraits.InitialPosition;

/**
 * base class for logical child windows of LWTopFrames
 *
 * implements all {@link LWContainerHandle} methods except {@link LWHandle#menuDescription()}  
 */
@SuppressWarnings("serial")
public abstract class LWChildFrame extends JFrame implements LWFrameOrDialog, LWContainerHandle {

	private final LWManager lwManager;
	protected LWTraits traits;

	/**
	 * see {@link JFrame#JFrame()}
	 * @param parent logical owner, not null
	 * @throws HeadlessException
	 */
	public LWChildFrame(LWContainerHandle parent) throws HeadlessException {
		super();
		lwManager = new LWManager(parent,this);
		lwInit(parent);
	}

	/**
	 * see {@link JFrame#JFrame(String title)}
	 * @param parent logical owner, not null
	 * @param title
	 * @throws HeadlessException
	 */
	public LWChildFrame(LWContainerHandle parent, String title) throws HeadlessException {
		super(title);
		lwManager = new LWManager(parent,this);
		lwInit(parent);
	}
	
	/**
	 * common constructor support
	 * @param parent
	 */
	private void lwInit(LWContainerHandle parent) {
		traits = new LWTraits(InitialPosition.STAGGERED_ON_PARENT);
		if (parent != null) {
			parent.manage(this);
			LWContainerHandle.stagger(parent.getWindow(),this);
		}
	}

	@Override
	public Window getWindow() {
		return this;
	}

	@Override
	public Iterator<LWHandle> iterator() {
		return lwManager.visible(); 
	}

	@Override
	public void manage(LWHandle child) {
		lwManager.manage(this,child);
	}
	
	@Override
	public LWModality getLWModality() {
		return LWModality.MODELESS; 
	}

	@Override
	public LWContainerHandle getlwParent() {
		return lwManager.getLwParent();
	}

	@Override
	public void closeRecursively() {
		lwManager.closeRecursively();
	}

	@Override
	public void unIconify() {
		LWHandle.unIconify(this);
	}

	@Override
	public JMenuItem menuItem(int level) {
		return LWMenuItemFactory.menuFor(level, this);
	}

	@Override
	public LWTraits getTraits() {
		return traits; 
	}
	
	@Override
	public Window self() {
		return this;
	}
}
