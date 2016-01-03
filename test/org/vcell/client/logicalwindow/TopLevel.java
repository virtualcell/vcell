package org.vcell.client.logicalwindow;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class TopLevel extends LWTopFrame implements ProofOfConceptContainer {
	private static int TLCounter = 1;
	private final int id;
	public TopLevel() throws HeadlessException {
		this.id = TLCounter;
		setTitle(titleMe( ));
		init( );
	}

	public String toString( ) {
		return "Top " + id;
	}

	public String menuDescription( ) {
		return getTitle( );
	}

	private static String titleMe( ) {
		return "Top Level " + TLCounter++;
	}

	/**
	 * @return last window to receive focus, or null if none
	 */
//	private static TopLevel lastFocused( ) {
//		Optional<WeakReference<TopLevel>> first = liveWindows( ).findFirst();
//		if (first.isPresent()) {
//			return first.get( ).get( );
//		}
//		return null;
//	}

	private void init() {
//		TopLevel lastWindow = lastFocused();
//		tops.addFirst(new WeakReference<TopLevel>(this));
//		addWindowFocusListener(new FocusWatch());
//
		TestPanel tp = new TestPanel();
		tp.setHwindow(this);
		getContentPane().add(tp,BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);

		menuBar.add( super.createWindowMenu(true) );
		pack( );
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		System.out.println(SwingUtilities.isDescendingFrom(this, tp));
		System.out.println(SwingUtilities.isDescendingFrom(tp, this));
	}

	public static void main(String[] args) {
		topLevel( );
	}

	private static void topLevel( ) {
		new TopLevel().setVisible(true);
	}

	@Override
	public int level() {
		return 0;
	}



}
