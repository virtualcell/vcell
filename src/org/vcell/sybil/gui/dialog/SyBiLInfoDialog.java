package org.vcell.sybil.gui.dialog;

/*   SyBiLInfoDialog  --- by Oliver Ruebenacker, UCHC --- September 2008 to February 2010
 *   A dialog for displaying information about Sybil.
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.models.specs.SybilSpecs;
import org.vcell.sybil.util.gui.Splash;
import org.vcell.sybil.util.gui.ToolBar;

public class SyBiLInfoDialog extends InfoDialog {

	public static class Factory implements InfoDialog.Factory<SyBiLInfoDialog> {

		public SyBiLInfoDialog create(Component component) {
			SyBiLInfoDialog dialog = null;
			if(component instanceof JFrame) { dialog = new SyBiLInfoDialog((JFrame) component); } 
			else if(component instanceof JDialog) { dialog = new SyBiLInfoDialog((JDialog) component); } 
			else { dialog = new SyBiLInfoDialog(); }
			return dialog;
		}
		
	}
	
	private static final long serialVersionUID = 3370193373388109286L;	
	protected JPanel panel = new JPanel();
	
	public SyBiLInfoDialog() { super(); init(); }
	public SyBiLInfoDialog(JFrame frameNew) { super(frameNew, "SyBiL Info"); init(); }
	public SyBiLInfoDialog(JDialog dialogNew) { super(dialogNew, "SyBiL Info"); init(); }

	protected void init() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { finished(); }
		});
		setTitle(SybilSpecs.shortText);
		panel.add(new JLabel(new ImageIcon(new Splash())));
		add(panel);
		add(new InfoToolBar(this), BorderLayout.SOUTH);
		pack();
	}

	@Override
	public void showDialog() {
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	@Override
	public void finished() { dispose(); }
	
	static protected class InfoToolBar extends ToolBar {
		private static final long serialVersionUID = -1766287628531669896L;
		
		static protected class OKAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected SyBiLInfoDialog dialog;
			
			public OKAction(SyBiLInfoDialog dialogNew) {
				super(new ActionSpecs("OK", "OK", "Close Info Dialog", 
						"files/forward.gif"));
				dialog = dialogNew;
			}
			
			public void actionPerformed(ActionEvent event) { dialog.finished(); }
			
		}

		public InfoToolBar(SyBiLInfoDialog panelParent) {
			add(new OKAction(panelParent));
		}
		
	}

}
