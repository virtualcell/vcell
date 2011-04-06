package org.vcell.sybil.gui.dialog;

/*   SimpleLargeTextDialog  --- by Oliver Ruebenacker, UCHC --- February 2010
 *   A dialog for displaying a large piece of text.
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.util.gui.ToolBar;

@SuppressWarnings("serial")
public class SimpleLargeTextDialog extends InfoDialog {

	public static class Factory implements InfoDialog.Factory<SimpleLargeTextDialog> {

		protected String text, title;
		
		public Factory(String title, String text) {
			this.title = title;
			this.text = text;
		}
		
		public SimpleLargeTextDialog create(Component component) {
			SimpleLargeTextDialog dialog = null;
			if(component instanceof JFrame) { 
				dialog = new SimpleLargeTextDialog(title, text, (JFrame) component); 
			} else if(component instanceof JDialog) { 
				dialog = new SimpleLargeTextDialog(title, text, (JDialog) component); 
			} else { 
				dialog = new SimpleLargeTextDialog(title, text); 
			}
			return dialog;
		}
		
	}
	
	public SimpleLargeTextDialog(String title, String text) { 
		super(); 
		setTitle(title);
		init(text); 
	}
	
	public SimpleLargeTextDialog(String title, String text, JFrame frameNew) { 
		super(frameNew, title); 
		init(text); 
	}
	
	public SimpleLargeTextDialog(String title, String text, JDialog dialogNew) { 
		super(dialogNew, title); 
		init(text); 
	}

	protected void init(String text) {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { finished(); }
		});
		add(new JScrollPane(new JTextArea(text)));
		add(new OKButtonBar(this), BorderLayout.SOUTH);
		pack();
	}

	@Override
	public void showDialog() {
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	@Override
	public void finished() { dispose(); }
	
	static protected class OKButtonBar extends ToolBar {
		private static final long serialVersionUID = -1766287628531669896L;
		
		static protected class OKAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected SimpleLargeTextDialog dialog;
			
			public OKAction(SimpleLargeTextDialog dialogNew) {
				super(new ActionSpecs("OK", "OK", "Close Info Dialog", 
						"files/forward.gif"));
				dialog = dialogNew;
			}
			
			public void actionPerformed(ActionEvent event) { dialog.finished(); }
			
		}

		public OKButtonBar(SimpleLargeTextDialog panelParent) {
			add(new OKAction(panelParent));
		}
		
	}

}
