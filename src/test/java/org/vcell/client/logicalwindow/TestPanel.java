package org.vcell.client.logicalwindow;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;

@SuppressWarnings("serial")
public class TestPanel extends JPanel {
	private ProofOfConceptContainer hwindow;
	public TestPanel( ) {
	
		JPanel panel = this;
		
		JButton btnYesNo = new JButton("Y/N");
		btnYesNo.setToolTipText("JOptionPane dialogs");
		panel.add(btnYesNo);
		btnYesNo.addActionListener( a ->  optionPaneDialogs()  );
		btnYesNo.setForeground(Color.RED);
		
		JButton btnYesNo2 = new JButton("Y/N");
		btnYesNo2.setToolTipText("LW dialogs");
		panel.add(btnYesNo2);
		btnYesNo2.addActionListener( a ->  lwDialog()  );
		
		JButton btnModalD = new JButton("ModalDialog");
		panel.add(btnModalD);
		btnModalD.addActionListener( a ->  modalD()  );
		
//		JButton btnModeless = new JButton("Modeless Dialog");
//		panel.add(btnModeless);
//		btnModeless.addActionListener( (a) -> { modelessD(); } );
//		
		JButton btnTL = new JButton("TL");
		btnTL.setToolTipText("Another top level window");
		panel.add(btnTL);
		btnTL.addActionListener( a ->  topLevel()  );
		
		JButton btnSub = new JButton("Child");
		panel.add(btnSub);
		btnSub.addActionListener( a -> { childW( ); } ); 
		
		JButton btnChoose = new JButton("Choose");
		add(btnChoose);
		btnChoose.addActionListener( a -> { chooseFile( ); } ); 
		
		JButton btnModelessWarning = new JButton("Warning");
		add(btnModelessWarning);
		btnModelessWarning.addActionListener( a -> { warning( ); } ); 
		
		JButton btnExit = new JButton("Exit App");
		panel.add(btnExit);
		btnExit.addActionListener( a ->  System.exit(0) );
	}
	

	private void warning() {
		LWModelessWarning lwm = new LWModelessWarning(hwindow,"Danger");
		lwm.setVisible(true);
	}


	public void setHwindow(ProofOfConceptContainer hwindow) {
		this.hwindow = hwindow;
	}	

	private static void topLevel( ) {
		new TopLevel().setVisible(true);
	}

	private void modalD( ) {
		new TDialog(hwindow,hwindow.menuDescription( )).setVisible(true);
	}
	
//	private void modelessD( ) {
//		new TDialog(hwindow.getWindow(),hwindow.childName( ),ModalityType.MODELESS).setVisible(true);
//	}
//	
	private void childW( ) {
		TestChildWindow cw = new TestChildWindow(hwindow);
		cw.setVisible(true);
	}
	
	private void optionPaneDialogs( ) {
		JOptionPane jop = new JOptionPane("What say ye?", JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_CANCEL_OPTION);
		TestJOptionDialog top = new TestJOptionDialog(hwindow, "A question",jop);
		top.setVisible(true);
		top.dispose();

		//		int r = JOptionPane.showConfirmDialog(this, "What say ye?","A question", JOptionPane.YES_NO_OPTION);
		Object v = jop.getValue(); 
		System.out.println(v);
		Integer rval = BeanUtils.downcast(Integer.class, v);
		if (rval != null) {
			int r = rval;
			switch (r) {
			case JOptionPane.YES_OPTION:
				JOptionPane.showMessageDialog(this, "Yes!!", "Yes",JOptionPane.INFORMATION_MESSAGE);
				break;
			case JOptionPane.NO_OPTION:
				JOptionPane.showMessageDialog(this, "So negative...", "No?",JOptionPane.WARNING_MESSAGE);
				break;
			default:
				JOptionPane.showMessageDialog(this, "Got return value " + r, "Unknown",JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
	}
	
	private void lwDialog( ) {
		boolean rval = SomeHeadlessClass.doSomething( new LWInteractionContext(hwindow) );
		if (rval) {
			JOptionPane.showMessageDialog(this, "Yes!!", "Yes",JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(this, "So negative...", "No?",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void chooseFile() {
		JFileChooser jfc = new LWFileChooser();
		int r = jfc.showOpenDialog(hwindow.getWindow());
		if (r == JFileChooser.APPROVE_OPTION) {
			DialogUtils.showInfoDialog(hwindow.getWindow(), "User choose " + jfc.getSelectedFile().getAbsolutePath());
		}
		
	}

}
