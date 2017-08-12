package org.vcell.client.logicalwindow;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class TDialog extends LWDialog implements ProofOfConceptHandle{
	private final LWContainerHandle parent;

	public TDialog(LWContainerHandle lwh, String title) {
		super(lwh, title); 
		parent = lwh;
		
		JLabel lblIdent = new JLabel(title);
		lblIdent.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblIdent, BorderLayout.NORTH);
		
		getContentPane().add(new JLabel(menuDescription( )), BorderLayout.CENTER);
		
		JButton btnClose = new JButton("Close");
		getContentPane().add(btnClose, BorderLayout.SOUTH);
		btnClose.addActionListener( a -> { setVisible(false); });
		pack( );
	}
	
	private ProofOfConceptHandle pocHandle( ) {
		return (ProofOfConceptHandle) parent;
	}
	@Override
	public String menuDescription() {
		return getTitle( ) + " modal "; 
	}

	@Override
	public int level() {
		return pocHandle( ).level() + 1; 
	}
}
