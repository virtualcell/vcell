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
	/**
	 * message to user about why we want information
	 */
	public final static String SHARE_MODEL_TEXT =
			//per Ann Cowan email 8/5/2015
		"Sending your model information and log file allows VCell Support to reproduce an issue and repair potential software bugs "
		+ "that may have escaped release testing; the information will not be used for any other purpose. ";

	public static abstract class DialogMessagePanel extends JPanel {
		/**
		 * @return supplemental information or null if none
		 */
		public abstract String getSupplemental( );

		/**
		 * @return message type for {@link JOptionPane#JOptionPane(Object, int,int)} constructor
		 */
		public abstract int optionType() ;
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
		return new JPanelWithSendOption(message, errorContext);
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
		
		@Override
		public int optionType() {
			return JOptionPane.DEFAULT_OPTION; 
		}
	}

	private static class JPanelWithSendOption extends DialogMessagePanel {
		private final ErrorContext errorContext;
		private String logContent;
		private int generatedOptionType = JOptionPane.YES_NO_OPTION;
		JPanelWithSendOption(String message, ErrorContext errorContext) {
			commonConstructionCode(this,message);
			this.errorContext = errorContext;
			logContent = null;

			final boolean haveContext = isHaveContext();
			CurrentContent currentCapture = ConsoleCapture.getInstance().getLastLines(1000);
			if (currentCapture.isAvailable) {
				logContent = currentCapture.content;
			}
			if (currentCapture.isAvailable || haveContext) {

				FlowLayout fl = new FlowLayout(FlowLayout.TRAILING);
				JPanel buttonPanel = new JPanel(fl);
				add(buttonPanel, BorderLayout.SOUTH);
				JLabel label = new JLabel("Send error report to VCell development team to assist debugging?"); 
				buttonPanel.add(label);

				JButton helpBtn = new JButton(DialogUtils.swingIcon(JOptionPane.QUESTION_MESSAGE));
				helpBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Component c = (Component) e.getSource();
						DialogUtils.showInfoDialog(c,MessagePanelFactory.SHARE_MODEL_TEXT);
					}
				});
				buttonPanel.add(helpBtn);
			}else{
				generatedOptionType = JOptionPane.DEFAULT_OPTION;
			}
		}
		
		@Override
		public int optionType() {
			return generatedOptionType; 
		}
		
		private boolean isHaveContext( ) {
			return errorContext != null && errorContext.canUse();
		}

		@Override
		public String getSupplemental() {
			final boolean haveContext = isHaveContext();
			if (haveContext || logContent != null) {
				StringBuilder sb = new StringBuilder();
				if (haveContext) {
						Objects.requireNonNull(errorContext);
						sb.append(errorContext.modelInfo);
						sb.append('\n');
				}
				if (logContent != null) {
					sb.append("Log file content:\n");
					sb.append(logContent);
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