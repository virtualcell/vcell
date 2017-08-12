package org.vcell.client.logicalwindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.vcell.client.logicalwindow.LWTraits.InitialPosition;

/**
 * OK / Cancel dialog -- uses question as basis for menu title
 */
@SuppressWarnings("serial")
public class LWOkCancelDialog extends LWDialog {
	private static final int QUESTION_TRUNCATE_LIMIT = 40;
	final private String question;
	private boolean saidYes = false;
	private static final LWTraits trait = new LWTraits(InitialPosition.CENTERED_ON_PARENT);

	/**
	 * @wbp.parser.constructor
	 */
	public LWOkCancelDialog(LWContainerHandle parent, String question) {
		this(parent,"Confirm",question);
	}

	public LWOkCancelDialog(LWContainerHandle parent, String title, String question) {
		super(parent, title);
		this.question = question;
		getContentPane().add(createMessagePanel(question));
		pack( );
	}

	@Override
	public String menuDescription() {
		String questionStub; 
		if (question.length() <= QUESTION_TRUNCATE_LIMIT) {
			questionStub = question ;
		}
		else {
			questionStub = question.substring(0,QUESTION_TRUNCATE_LIMIT)  + " ...";

		}
		return getTitle( ) + ' ' + questionStub;
	}
	
	@Override
	public LWTraits getTraits() {
		return trait;
	}

	public boolean isSaidYes() {
		return saidYes;
	}

	private JPanel createMessagePanel(final String message) {
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
		Dimension screenSize = LWNamespace.getScreenSize( );
		final int horizBorderSize = 90;
		final int vertBorderSize = 40;
		int w = Math.min(textAreaPreferredSize.width + horizBorderSize, screenSize.width - horizBorderSize);
		int h = Math.min(textAreaPreferredSize.height + vertBorderSize, screenSize.height - vertBorderSize);
		Dimension preferredSize = new Dimension(w,h);
		
		JScrollPane scroller = new JScrollPane();
		JPanel panel = new JPanel();
		scroller.setViewportView(textArea);
		scroller.getViewport().setPreferredSize(preferredSize);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(scroller, BorderLayout.CENTER);
		
		JPanel btnPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(btnPanel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnPanel.add(btnOk);
		btnOk.addActionListener( a -> { saidYes = true; dispose( ); }); 
		
		JButton btnCancel = new JButton("Cancel");
		btnPanel.add(btnCancel);
		btnCancel.addActionListener( a -> { dispose( ); }); 
		return panel;
	}
}
