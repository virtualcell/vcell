package org.vcell.client.logicalwindow;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.util.Objects;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Replacement for {@link JOptionPane#createDialog(java.awt.Component, String)}
 */
@SuppressWarnings("serial")
public abstract class LWOptionPaneDialog extends LWDialog {

	/**
	 * @param parent preferably not null but works ...
	 * @param title null is okay
	 * @param optionPane not null
	 */
	public LWOptionPaneDialog(LWContainerHandle parent, String title, JOptionPane optionPane) {
		super(parent, title);
		Objects.requireNonNull(optionPane);
		JPanel contentPane = new JPanel(new BorderLayout());
		JMenuBar mb = new JMenuBar();
		mb.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		mb.add( LWTopFrame.createWindowMenu(false) );
		contentPane.add(mb, BorderLayout.NORTH);
		contentPane.add(optionPane,BorderLayout.CENTER);
		setContentPane(contentPane);
		pack();
		new OptionPaneHandler(this,optionPane);
	}

}
