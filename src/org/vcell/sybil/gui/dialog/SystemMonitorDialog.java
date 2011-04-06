package org.vcell.sybil.gui.dialog;

/*   SystemMonitorDialog  --- by Oliver Ruebenacker, UCHC --- September 2008 to February 2010
 *   A dialog for displaying various system information (OS, Java, JRE, libSBML, etc.)
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.models.probe.Feedback;
import org.vcell.sybil.models.probe.ProbeUtil;
import org.vcell.sybil.models.probe.ProbesDefault;
import org.vcell.sybil.util.gui.ToolBar;

@SuppressWarnings("serial")
public class SystemMonitorDialog extends InfoDialog {

	static protected final String prequel = "[Click 'report' to send this to the developers " + 
	"(we love feedback). " + 
	"Optionally edit this and/or to add a message]\n\n";
	
	public static class Factory implements InfoDialog.Factory<SystemMonitorDialog> {

		public SystemMonitorDialog create(Component component) {
			SystemMonitorDialog dialog = null;
			if(component instanceof JFrame) { dialog = new SystemMonitorDialog((JFrame) component); } 
			else if(component instanceof JDialog) { dialog = new SystemMonitorDialog((JDialog) component); } 
			else { dialog = new SystemMonitorDialog(); }
			return dialog;
		}
		
	}
	
	protected JTextArea textArea = new JTextArea();
	
	public SystemMonitorDialog() { super(); init(); }
	public SystemMonitorDialog(JFrame frameNew) { super(frameNew, "SystemMonitor"); init(); }
	public SystemMonitorDialog(JDialog dialogNew) { super(dialogNew, "SystemMonitor"); init(); }

	protected void init() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { finished(); }
		});
		setTitle("System Monitor");
		textArea.setEditable(true);
		//textArea.setLineWrap(true);
		textReset();
		add(new JScrollPane(textArea));
		add(new InfoToolBar(this), BorderLayout.SOUTH);
		pack();
	}

	@Override
	public void showDialog() {
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	public String text() { return textArea.getText(); }
	
	public void textReset() {
		textArea.setText(prequel + ProbeUtil.prettyPrint(ProbesDefault.probesAll));
		System.out.println(textArea.getText());
	}
	
	@Override
	public void finished() { dispose(); }
	
	public void indicateMesageSent() {
		JOptionPane.showMessageDialog(this, "Thank you for your feedback! Your message has been sent " +
				"to the developers.", 
				"Message sent", JOptionPane.INFORMATION_MESSAGE);
		System.out.println("Message sent");
	}
	
	public void indicateProblem(Throwable t) {
		t.printStackTrace();
		String title = t.getClass().getSimpleName() + ": " + t.getMessage();
		String message = "An exception or error has occured.\n" + title;
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	static protected class InfoToolBar extends ToolBar {
		private static final long serialVersionUID = -1766287628531669896L;
		
		static protected class ReportAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected SystemMonitorDialog dialog;
			
			public ReportAction(SystemMonitorDialog dialogNew) {
				super(new ActionSpecs("Report", "Report", "Send report to developers", 
						"files/forward.gif"));
				dialog = dialogNew;
			}
			
			public void actionPerformed(ActionEvent event) { 
				Feedback feedback = new Feedback();
				feedback.add("message", dialog.text());
				feedback.add("GUI Used", "Systems Monitor Dialog");
				try { 
					feedback.send();
					dialog.indicateMesageSent();
				} catch (MalformedURLException e) { dialog.indicateProblem(e); } 
				catch (UnsupportedEncodingException e) { dialog.indicateProblem(e); } 
				catch (IOException e) { dialog.indicateProblem(e); }
				
			}
			
		}

		static protected class ResetAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected SystemMonitorDialog dialog;
			
			public ResetAction(SystemMonitorDialog dialogNew) {
				super(new ActionSpecs("Reset", "Reset", "Reset text", 
						"files/forward.gif"));
				dialog = dialogNew;
			}
			
			public void actionPerformed(ActionEvent event) { dialog.textReset(); }
			
		}

		static protected class CloseAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected SystemMonitorDialog dialog;
			
			public CloseAction(SystemMonitorDialog dialogNew) {
				super(new ActionSpecs("Close", "Close", "Close System Monitor Dialog", 
						"files/forward.gif"));
				dialog = dialogNew;
			}
			
			public void actionPerformed(ActionEvent event) { dialog.finished(); }
			
		}

		public InfoToolBar(SystemMonitorDialog panelParent) {
			add(new ReportAction(panelParent));
			add(new ResetAction(panelParent));
			add(new CloseAction(panelParent));
		}
		
	}

}
