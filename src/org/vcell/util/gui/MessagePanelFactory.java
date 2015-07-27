package org.vcell.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.vcell.util.gui.DialogUtils.ErrorContext;
import org.vcell.util.logging.ConsoleCapture;
import org.vcell.util.logging.ConsoleCapture.CurrentContent;

/**
 * creates JPanels for message dialogs with same look and fell for
 * message part with variations to support using allow application to
 * send debug information back to VCell Support 
 * @author GWeatherby
 */
@SuppressWarnings("serial") 
public class MessagePanelFactory {
	public final static String SHARE_MODEL_TEXT =
			"Sending your model information and/or log file will improve the ability of the Virtual Cell Support team to diagnose " +
					"and repair transient software bugs which have escaped release testing. We may use the information you send to " +
					"for the sole purpose of reproducing the bugs you've encountered.";

	public static abstract class DialogMessagePanel extends JPanel {
		/**
		 * @return supplemental information or null if none
		 */
		public abstract String getSupplemental( );
	}

	/**
	 * simple JPanel
	 * @param message not null
	 * @return new JPanel
	 */
	public static JPanel createSimple(String message) {
		JPanel jp = new JPanel();
		commonConstructionCode(jp, message);
		return jp; 
	}

	/**
	 * @param message not null
	 * @param errorContext; may be null
	 * @return new {@link DialogMessagePanel}
	 */
	public static DialogMessagePanel createExtended(String message, ErrorContext errorContext) {
		return new JPanelWithSendOptions(message, errorContext);
	}

	/**
	 * 
	 * @param message not null
	 * @return {@link DialogMessagePanel} whose {@link DialogMessagePanel#getSupplemental()} returns null
	 */
	public static DialogMessagePanel createNonSending(String message) {
		return new NonSending(message);
	}



	private MessagePanelFactory() {};


	private static void commonConstructionCode(JPanel panel, String message) {
		panel.setLayout(new BorderLayout( ));

		JTextPane textArea = new JTextPane();
		if (message != null && message.contains("<html>")) {
			textArea.setContentType("text/html");
		}
		textArea.setText(message);
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
		textArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		//
		// determine "natural" TextArea prefered size (what it would like if it didn't wrap lines)
		// and try to set size accordingly (within limits ... e.g. 400<=X<=500 and 100<=Y<=400).
		//
		Dimension textAreaPreferredSize = textArea.getPreferredSize();
		Dimension preferredSize = new Dimension((int)Math.min(500,Math.max(400,textAreaPreferredSize.getWidth()+20)),
				(int)Math.min(400,Math.max(100,textAreaPreferredSize.getHeight()+20)));

		JScrollPane scroller = new JScrollPane();
		scroller.setViewportView(textArea);
		scroller.getViewport().setPreferredSize(preferredSize);
		panel.add(scroller, BorderLayout.CENTER);
	}

	private static class NonSending extends DialogMessagePanel {
		NonSending(String message) {
			commonConstructionCode(this, message);
		}

		@Override
		public String getSupplemental() {
			return null;
		}

	}

	private static class JPanelWithSendOptions extends DialogMessagePanel {
		private final ErrorContext errorContext;
		private String logContent;
		private boolean initialModelCheckboxState;
		private final JCheckBox allowSendModel;
		private final JCheckBox allowSendLog; 

		JPanelWithSendOptions(String message, ErrorContext errorContext) {
			commonConstructionCode(this,message);
			this.errorContext = errorContext;
			logContent = null;
			initialModelCheckboxState = false;

			JCheckBox cbModel = null;
			JCheckBox cbLog = null;

			final boolean haveContext = isHaveContext();
			if (haveContext) {
				//don't pay cost of getting this unless it's going to be used
				initialModelCheckboxState = errorContext.userPreferences.getSendModelInfoInErrorReportPreference();
			}
			CurrentContent currentCapture = ConsoleCapture.getInstance().getLogContent();
			if (currentCapture.isAvailable || haveContext) {

				FlowLayout fl = new FlowLayout(FlowLayout.TRAILING);
				JPanel buttonPanel = new JPanel(fl);
				add(buttonPanel, BorderLayout.SOUTH);
				JLabel label = new JLabel("Send to VCell Support team:  ");
				buttonPanel.add(label);

				if (haveContext) {
					cbModel = new JCheckBox("Model info");
					buttonPanel.add(cbModel);
					cbModel.setSelected(initialModelCheckboxState);
				}

				if (currentCapture.isAvailable) {
					cbLog = new JCheckBox("Local log file",true);
					buttonPanel.add(cbLog);
					logContent = currentCapture.content;
				}

				JButton helpBtn = new JButton(DialogUtils.swingIcon(JOptionPane.QUESTION_MESSAGE));
				helpBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Component c = (Component) e.getSource();
						DialogUtils.showInfoDialog(c,MessagePanelFactory.SHARE_MODEL_TEXT);
					}
				});
				buttonPanel.add(helpBtn);
			}	
			allowSendModel = cbModel;
			allowSendLog = cbLog;
		}

		private boolean isHaveContext( ) {
			return errorContext != null && errorContext.canUse();
		}

		@Override
		public String getSupplemental() {
			String logToSend = logContent; 
			if (allowSendLog != null && !allowSendLog.isSelected()) {
				logToSend = null; 
			}
			final boolean haveContext = isHaveContext();
			if (haveContext || logToSend != null) {
				StringBuilder sb = new StringBuilder();
				if (haveContext) {
					final boolean userSelectedSendModel = allowSendModel.isSelected();
					if (userSelectedSendModel != initialModelCheckboxState) {
						errorContext.userPreferences.setSendModelInfoInErrorReportPreference(userSelectedSendModel);
					}
					if (userSelectedSendModel) {
						sb.append(errorContext.modelInfo);
						sb.append('\n');
						Objects.requireNonNull(errorContext);
					}
					else {
						sb.append("User declined to share model\n");
					}
				}
				if (logToSend != null) {
					sb.append("Log file content:\n");
					sb.append(logToSend);
					sb.append('\n');
				}
				if (sb.length() > 0) {
					return sb.toString();
				}
			}
			return null;
		}	
	}
}