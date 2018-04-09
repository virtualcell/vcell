package org.vcell.client.logicalwindow;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.client.logicalwindow.LWTraits.InitialPosition;

/**
 * base class for logical top level frames
 *
 * keeps track of all top level objects
 *
 * implements all {@link LWContainerHandle} methods except {@link LWHandle#menuDescription()}
 */
@SuppressWarnings("serial")
public abstract class LWTopFrame extends JFrame implements LWContainerHandle {
	//
	/**
	 * {@link LWContainerHandle} support
	 */
	private final LWManager lwManager;
//	private TakeItBack takeItBack;

	/**
	 * static method data structure
	 */
	private static LinkedList<WeakReference<LWTopFrame>> tops = new LinkedList<>();

	private static Map<String,MutableInt> titleSequenceNumbers = new HashMap<>();

	private static final LWTraits traits = new LWTraits(InitialPosition.NOT_LW_MANAGED);
	private static final Logger LG = LogManager.getLogger(LWTopFrame.class);

//	private final static Set<Window> transientStatusDialogs;
	static {
		//newSetFromMap requires Boolean value type
		WeakHashMap<Window, Boolean> whm = new WeakHashMap<>( );
//		transientStatusDialogs = Collections.newSetFromMap(whm);
	}

	protected LWTopFrame( ) {
		lwManager = new LWManager(null,this);
//		takeItBack = null;
		tops.addFirst(new WeakReference<LWTopFrame>(this));
		addWindowFocusListener(new FocusWatch());
	}

	@Override
	public LWTraits getTraits() {
		return traits;
	}

	/**
	 * @param bTextMenu if true, make top menu item text; if false, use icon
	 * @return Window menu for adding a JMenuBar
	 */
	public static JMenu createWindowMenu(boolean bTextMenu ) {
		JMenu mnWindow = new JMenu( );
		if (bTextMenu) {
			mnWindow.setText("Window");
			mnWindow.setMnemonic('W');
		}
		else {
			mnWindow.setIcon(LWButton.SHOW_WINDOW_MENU_ICON);
			mnWindow.setToolTipText("Show open windows");
		}
		mnWindow.addMenuListener(new Activator());
		return mnWindow;
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
	public void unIconify() {
		LWHandle.unIconify(this);
	}

	@Override
	public JMenuItem menuItem(int level) {
		return LWMenuItemFactory.menuFor(level, this);
	}

	@Override
	public void closeRecursively() {
		lwManager.closeRecursively();
	}

	@Override
	public void manage(LWHandle child) {
		lwManager.manage(this,child);
	}

	@Override
	public LWContainerHandle getlwParent() {
		return lwManager.getLwParent();
	}

	@Override
	public LWModality getLWModality() {
		return LWModality.MODELESS;
	}

	/**
	 * return next sequential menu description
	 * @param windowName not null
	 * @return next sequential menu description, or just window name if first call with this name
	 */
	protected static String nextSequentialDescription(String windowName) {
		Objects.requireNonNull(windowName);
		if (titleSequenceNumbers.containsKey(windowName)) {
			MutableInt mi = titleSequenceNumbers.get(windowName);
			int seq = mi.intValue();
			mi.increment();
			return windowName + ' ' + seq;
		}
		titleSequenceNumbers.put(windowName, new MutableInt(1) );
		return windowName;
	}

	/**
	 * @return ordered list of menu items
	 */
	static List<JMenuItem> activeMenuItems() {
		ArrayList<JMenuItem> rval = new ArrayList<>( );
		Iterator<WeakReference<LWTopFrame>> iter = liveWindows().iterator();
		while (iter.hasNext()) {
			LWTopFrame ltf = iter.next().get();
			buildMenuItemList(ltf,0,rval);
		}
		return rval;
	}
	/**
	 * last focused window will be first in list
	 * @return non-garbage collected windows which are visible
	 */
	public static Stream<WeakReference<LWTopFrame>> liveWindows() {
		tops.removeIf( wr -> wr.get( ) == null);
		return tops.stream().filter( wr -> wr.get( ) != null).filter( wr -> wr.get( ).isVisible() );
	}

	/**
	 * recursive method for building / indenting list of menu items
	 * @param lwh current logical window
	 * @param level current indentation level
	 * @param destination (output)
	 */
	private static void buildMenuItemList(LWHandle lwh, int level, List<JMenuItem> destination) {
		destination.add( lwh.menuItem(level) );
		for (LWHandle logicalChild : lwh) {
			buildMenuItemList(logicalChild, level + 1, destination);
		}
	}

	/**
	 * class to ensure last focused top window is at beginning of collection
	 */
	private class FocusWatch implements WindowFocusListener {
		/**
		 * remove our reference from {@link TopLevel#tops} and reinsert at end
		 */
		@Override
		public void windowGainedFocus(WindowEvent e) {
			Predicate<WeakReference<LWTopFrame>> findOurself = wr -> wr.get( ) == LWTopFrame.this;

			WeakReference<LWTopFrame> ourWr = tops.stream().filter(findOurself).findFirst().get();
			Objects.requireNonNull(ourWr);

			tops.removeIf( findOurself );
			tops.addFirst(ourWr);
			if (LG.isDebugEnabled()) {
				LG.debug(LWTopFrame.this.menuDescription() + " gained focus");
			}
		}

		@Override
		public void windowLostFocus(WindowEvent e) {
//			lostFocusHandler(e);
		}
	}

	/**
	 * listener for {@link LWTopFrame#createWindowMenu(boolean)}
	 * rebuilds menu with actively visible windows
	 */
	private static class Activator implements MenuListener {
		@Override
		public void menuSelected(MenuEvent e) {
			JMenu menu = (JMenu) e.getSource();
			menu.removeAll();
			for (JMenuItem mi : activeMenuItems()) {
				menu.add(mi);
			}
		}

		@Override
		public void menuDeselected(MenuEvent e) { }

		@Override
		public void menuCanceled(MenuEvent e) { }
	}

}
