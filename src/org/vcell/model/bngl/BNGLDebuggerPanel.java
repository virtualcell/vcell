package org.vcell.model.bngl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import org.vcell.model.rbm.RbmUtils;

import cbit.gui.MultiPurposeTextPanel;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.VCML;
import cbit.vcell.math.gui.MathDescEditor;

public class BNGLDebuggerPanel extends JPanel {
	
	private final static Set<String> autoCompletionWords = new HashSet<String>();
	private final static Set<String> keywords = null;

	private MultiPurposeTextPanel bnglTextArea = null;
	private JTextPane exceptionTextArea;
	
	private ParseException parseException = null;
	private Exception exception = null;
    	
	public BNGLDebuggerPanel(String initialDocText, final Exception e) {
		
		
		initialize();
		getBnglPanel().setText(initialDocText);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {		
				setParseException(e);
			}
		});
	}
	
	private void initialize(){

		getBnglPanel().getTextPane().setFont(new Font("monospaced", Font.PLAIN, 12));

		getBnglPanel().getLineNumberPanel().setForeground(java.awt.Color.gray);
		getBnglPanel().getLineNumberPanel().setBackground(java.awt.Color.white);
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.add(getBnglPanel(), BorderLayout.CENTER);

		JScrollPane exceptionPanel = new JScrollPane();
		exceptionTextArea = new JTextPane();
		exceptionTextArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		exceptionTextArea.setContentType("text/html");
//		Font f = new Font("monospaced", Font.PLAIN, 10);
//		exceptionTextArea.setFont(f);

		exceptionPanel.getViewport().add(exceptionTextArea);
		exceptionPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		lowerPanel.add(exceptionPanel, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(350);
		splitPane.setResizeWeight(0.9);
		splitPane.setTopComponent(upperPanel);
		splitPane.setBottomComponent(lowerPanel);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(splitPane, gbc);
		
		setPreferredSize(new Dimension(900,650));
	}


	public String getText(){
		return getBnglPanel().getText();
	}

	public void setParseException(Exception e){
		if(e instanceof ParseException) {
			this.parseException = (ParseException)e;
			this.exception = null;
		} else {
			this.parseException = null;
			this.exception = e;
		}
		updateException();
	}
	private void updateException(){
		String exceptionText = "No errors detected. Please Save this file, Exit the debugger and Import again.";
		if (parseException != null){
			exceptionText = parseException.getMessage();
//			int bl = parseException.currentToken.beginLine;
//			int bc = parseException.currentToken.beginColumn;
//			int el = parseException.currentToken.endLine;
//			int ec = parseException.currentToken.endColumn;
//			System.out.println(bl + ":" + bc + ", " + el + ":" + ec);
			int lineNumber = parseLineNumber(exceptionText);
			int columnNumber = parseColumnNumber(exceptionText);
			getBnglPanel().setCursor(lineNumber, columnNumber);
			getBnglPanel().getLineNumberPanel().setErrorLine(lineNumber);
		} else if(exception != null) {
			exceptionText = exception.getMessage();
			int lineNumber = parseLineNumber(exceptionText);
			int columnNumber = parseColumnNumber(exceptionText);
			getBnglPanel().setCursor(lineNumber, columnNumber);
			getBnglPanel().getLineNumberPanel().setErrorLine(lineNumber);
		}
		Font font = new Font("monospaced", Font.PLAIN, 3);
		String pre = "<html><font face=\"" + font.getFamily() + "\" size=\"" + font.getSize() + "\"> " + exceptionText + "</font></html>";
		exceptionTextArea.setText(pre);
		
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Rectangle rect = exceptionTextArea.modelToView(exceptionTextArea.getLineStartOffset(0));
//					exceptionTextArea.scrollRectToVisible(rect);
//				} catch (BadLocationException e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}

	//   isAsciiPrintable('a')  = true
	//   isAsciiPrintable('A')  = true
	//   isAsciiPrintable('3')  = true
	//   isAsciiPrintable('-')  = true
	//   isAsciiPrintable('\n') = false
	//   isAsciiPrintable('&copy;') = false
		public static boolean isAsciiPrintable(char ch) {
			return ch >= 32 && ch < 127;
		}
		private static boolean isNumeric(String str)
		{
		    return str.matches("[+-]?\\d*(\\.\\d+)?");
		}

	// typical message:   Encountered "x" at line 15, column 11.
	private static int parseLineNumber(String exceptionText) {
		int lineNumber = 0;
		try {
		final String key = " at line ";
		String sn = exceptionText.substring(exceptionText.indexOf(key) + key.length());
		sn = sn.substring(0, sn.indexOf(','));
		if(sn != null && isNumeric(sn)) {
			lineNumber = Integer.parseInt(sn) - 1;	// sn is 1 based, we make lineNumber 0 based
		}
		} catch (Exception e) {
		e.printStackTrace();
		}
		return (lineNumber < 0 ? 0 : lineNumber);
	}
	private static int parseColumnNumber(String exceptionText) {
		int columnNumber = 0;
		try {
		final String key = ", column ";
		String sn = exceptionText.substring(exceptionText.indexOf(key) + key.length());
		sn = sn.substring(0, sn.indexOf('.'));
		if(sn != null && isNumeric(sn)) {
			columnNumber = Integer.parseInt(sn) - 1;	// sn is 1 based, we make lineNumber 0 based
		}
		} catch (Exception e) {
		e.printStackTrace();
		}
		return (columnNumber < 0 ? 0 : columnNumber);
	}
	
	private MultiPurposeTextPanel getBnglPanel() {
		if (bnglTextArea == null) {
			bnglTextArea = new MultiPurposeTextPanel(true);
			bnglTextArea.setName("BnglPanel");
			bnglTextArea.setKeywords(new HashSet<String>(Arrays.asList(kw)));
		}
		return bnglTextArea;
	}

	public static final String[] kw = new String[] {
		"begin", "end",
		"model",
		"parameters",
		"molecule", "types",
		"seed", "species",
		"observables",
		"reaction", "rules",
		"Molecules", 
		"generate_network"
		};

}
