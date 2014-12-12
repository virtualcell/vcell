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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import org.vcell.model.rbm.RbmUtils;

public class BNGLDebuggerPanel extends JPanel /*implements KeyListener, ActionListener*/ {
	
	private int lines = 0;
	private JTextArea	lineNumberArea;
	private JTextArea	bnglTextArea;
	private JTextArea	exceptionTextArea;
	
	private ParseException parseException = null;
    	
	public BNGLDebuggerPanel(String initialDocText, final ParseException parseException) {
		initialize();
		bnglTextArea.setText(initialDocText);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {		
				setParseException(parseException);
			}
		});
	}
	
	private void initialize(){

		JScrollPane bnglPanel = new JScrollPane();
		bnglTextArea = new JTextArea();
        bnglTextArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//        bnglTextArea.setFont(new Font("monospaced", Font.PLAIN, 14));

        lineNumberArea = new JTextArea("1");
		lineNumberArea.setBackground(Color.LIGHT_GRAY);
		lineNumberArea.setEditable(false);
		lineNumberArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		bnglTextArea.getDocument().addDocumentListener(new DocumentListener(){
			public String getNr(){
				lines = 1;
				int caretPosition = bnglTextArea.getDocument().getLength();
				Element root = bnglTextArea.getDocument().getDefaultRootElement();
				String nr = "1" + System.getProperty("line.separator");
				for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
					nr += i + System.getProperty("line.separator");
					lines++;
				}
				return nr;
			}
			@Override
			public void changedUpdate(DocumentEvent de) {
				int oldLines = lines;
				String numbers = getNr();
				if(oldLines != lines) {
					lineNumberArea.setText(numbers);
				}
				lineNumberArea.getHighlighter().removeAllHighlights();

			}
			@Override
			public void insertUpdate(DocumentEvent de) {
				int oldLines = lines;
				String numbers = getNr();
				if(oldLines != lines) {
					lineNumberArea.setText(numbers);
				}
				lineNumberArea.getHighlighter().removeAllHighlights();
			}
 			@Override
			public void removeUpdate(DocumentEvent de) {
				int oldLines = lines;
				String numbers = getNr();
				if(oldLines != lines) {
					lineNumberArea.setText(numbers);
				}
				lineNumberArea.getHighlighter().removeAllHighlights();
			}
		});
		bnglPanel.getViewport().add(bnglTextArea);
		bnglPanel.setRowHeaderView(lineNumberArea);
		bnglPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.add(bnglPanel, BorderLayout.CENTER);

		JScrollPane exceptionPanel = new JScrollPane();
		exceptionTextArea = new JTextArea();
		exceptionTextArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		exceptionTextArea.setFont(new Font("monospaced", Font.PLAIN, 14));

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
		return bnglTextArea.getText();
	}

//	private void parse() {
//		String str = bnglTextArea.getText();
//		InputStream is = new ByteArrayInputStream(str.getBytes());
//		BufferedReader br = new BufferedReader(new InputStreamReader(is));
//		try {
//			RbmUtils.importBnglFile(br);
//			setParseException(null);
//		}catch (ParseException e){
//			setParseException(e);
//		}
//	}
	public void setParseException(ParseException e){
		this.parseException = e;
		updateException();
	}
	private void updateException(){
		String exceptionText = "No errors detected. Please Save this file, Exit the debugger and Import again.";
		if (parseException!=null){
			exceptionText = parseException.getMessage();
			int lineNumber = 0;
			int startIndex;
			try {
				String key = " at line ";
				String sn = exceptionText.substring(exceptionText.indexOf(key) + key.length());
				sn = sn.substring(0, sn.indexOf(','));
				if(sn != null && isNumeric(sn)) {
					lineNumber = Integer.parseInt(sn) - 1;	// sn is 1 based, we make lineNumber 0 based
				}
				startIndex = lineNumberArea.getLineStartOffset(lineNumber);
				int endIndex = lineNumberArea.getLineEndOffset(lineNumber);
				
				Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
				lineNumberArea.getHighlighter().addHighlight(startIndex, endIndex, painter);
				
				Rectangle rect = bnglTextArea.modelToView(bnglTextArea.getLineStartOffset(lineNumber));
				bnglTextArea.scrollRectToVisible(rect);
				lineNumberArea.scrollRectToVisible(rect);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
				exceptionText = e1.getMessage();
			}
		}
		exceptionTextArea.setText(exceptionText);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Rectangle rect = exceptionTextArea.modelToView(exceptionTextArea.getLineStartOffset(0));
					exceptionTextArea.scrollRectToVisible(rect);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static boolean isNumeric(String str)
	{
	    return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
}
