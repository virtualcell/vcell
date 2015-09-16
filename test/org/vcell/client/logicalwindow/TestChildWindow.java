package org.vcell.client.logicalwindow;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.vcell.client.logicalwindow.LWButton;
import org.vcell.client.logicalwindow.LWChildFrame;
import org.vcell.client.logicalwindow.LWContainerHandle;
import java.awt.ComponentOrientation;

@SuppressWarnings("serial")
public class TestChildWindow extends LWChildFrame implements ProofOfConceptContainer {
	private LWContainerHandle parent;
	private JButton btnWindows; 
	/**
	 * given unique ids for tracking which is which
	 */
	private static int serialId = 1;
	
	public TestChildWindow(LWContainerHandle hwindow) {
		super(hwindow,((ProofOfConceptHandle)hwindow).menuDescription() + " child:" + serialId++);
		this.parent = hwindow;
		TestPanel tp = new TestPanel();
		tp.setHwindow(this);
		JMenuBar menuBar = new JMenuBar();
		menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		menuBar.add( LWTopFrame.createWindowMenu(false) );
		
		getContentPane().add(tp,BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		btnWindows = new LWButton(this);
		panel.add(btnWindows, BorderLayout.WEST);
		
		pack( );
	}
	private ProofOfConceptHandle pocHandle( ) {
		return (ProofOfConceptHandle) parent;
	}
	
	@Override
	public String menuDescription() {
		return getTitle( );
	}
	public String toString() {
		return menuDescription( );
	}

	@Override
	public int level() {
		return pocHandle( ).level() + 1;
	}
}
