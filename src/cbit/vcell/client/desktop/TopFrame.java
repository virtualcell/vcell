package cbit.vcell.client.desktop;

import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.JFrame;

import org.apache.log4j.Logger;


/**
 * Top level frames; used to find currently active DocumentWindow for Reconnect functionality.
 */
@SuppressWarnings("serial")
public class TopFrame extends JFrame {
	//This is a back port of LWTopFrame added in 6.0 and later
	private static final Logger LG = Logger.getLogger(TopFrame.class);

	/**
	 *  all the tops
	 */
	private static LinkedList<WeakReference<TopFrame>> tops = new LinkedList<>();
	/**
	 * ProgressDialogs, essentially
	 */
	private final static Set<Window> transientStatusDialogs;
	/**
	 * Helper to deal with ProgressDialog focus shifts
	 */
	private TakeItBack takeItBack;
	static {
		//newSetFromMap requires Boolean value type
		WeakHashMap<Window, Boolean> whm = new WeakHashMap<>( );
		transientStatusDialogs = Collections.newSetFromMap(whm);
	}

	public TopFrame() throws HeadlessException {
		takeItBack = null;
		tops.addFirst(new WeakReference<TopFrame>(this));
		addWindowFocusListener(new FocusWatch());
	}
	/**
	 * tell Top Frame about a transient (e.g. progress) dialog
	 * @param w
	 */
	public static void registerTransientDialog(Window w) {
		transientStatusDialogs.add(w);
		if (LG.isTraceEnabled()) {
			LG.trace(describe(w) + " added to transient dialogs, current count is " + transientStatusDialogs.size());
		}
	}
	/**
	 * last focused window will be first in list
	 * @return non-garbage collected windows which are visible
	 */
	public static Stream<WeakReference<TopFrame>> liveWindows() {
		tops.removeIf( wr -> wr.get( ) == null);
		return tops.stream().filter( wr -> wr.get( ) != null).filter( wr -> wr.get( ).isVisible() );
	}
	private void lostFocusHandler(WindowEvent e) {
		if (LG.isTraceEnabled()) {
			LG.trace(describe(this) + " lost focus to " + describe(e.getOppositeWindow()) );
		}
		Window taker = e.getOppositeWindow();
		if (transientStatusDialogs.contains(taker)) {
			if (takeItBack == null) {
				takeItBack = new TakeItBack();
			}
			taker.addWindowListener(takeItBack);
		}
	}

	/**
	 * attempt to describe window
	 * @param w could be null
	 * @return null or description String (e.g. title)
	 */
	public static String describe(Window w) {
//		if (w != null) {
//			String className = ExecutionTrace.justClassName(w) + " ";
//			Frame f = BeanUtils.downcast(Frame.class, w);
//			if (f != null) {
//				return className + f.getTitle();
//			}
//			Dialog d = BeanUtils.downcast(Dialog.class, w);
//			if (d != null) {
//				return className + d.getTitle();
//			}
//			return className;
//		}
//		return "null";
		return "";

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
			Predicate<WeakReference<TopFrame>> findOurself = wr -> wr.get( ) == TopFrame.this;

			WeakReference<TopFrame> ourWr = tops.stream().filter(findOurself).findFirst().get();
			Objects.requireNonNull(ourWr);

			tops.removeIf( findOurself );
			tops.addFirst(ourWr);
			if (LG.isTraceEnabled()) {
				LG.trace(describe(TopFrame.this) + " gained focus");
			}
		}

		@Override
		public void windowLostFocus(WindowEvent e) {
			lostFocusHandler(e);
		}
	}

	/**
	 * take focus / top placement back from transient dialog
	 */
	private class TakeItBack extends WindowAdapter {

		@Override
		public void windowClosed(WindowEvent e) {
			transientStatusDialogs.remove(e.getSource());
			TopFrame.this.toFront();
		}

	}
}
