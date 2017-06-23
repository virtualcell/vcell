/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2006 1:17:05 PM)
 * @author: Fei Gao
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.vcell.util.BeanUtils;
import org.vcell.util.Commented;
import org.vcell.util.CountingLineReader;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.math.ReservedMathSymbolEntries;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.ASTFuncNode.FunctionType;

@SuppressWarnings("serial")
public class MultiPurposeTextPanel extends JPanel {
	private JTextPane textPane = null;
	private JScrollPane scrollPane = null;
	private LineNumberPanel numberPanel = null;

	private JButton searchPrevButton = null;
	private JButton searchNextButton = null;

	private int searchStartOffset = -1;
	private int searchEndOffset = -1;

	private static final String COMMIT_ACTION = "commit";
	private static final String GOUPLIST_ACTION = "gouplist";
	private static final String GODOWNLIST_ACTION = "godownlist";
	private static final String SHOWLIST_ACTION = "showlist";
	private static final String DISMISSLIST_ACTION = "dimisslist";
	
	private final static String CANCEL_ACTION = "cancel-search";
	private final static String UNDO_ACTION = "Undo    Crtl+Z";
	private final static String REDO_ACTION = "Redo    Crtl+Y";	

	private Set<String> autoCompletionWords = new HashSet<String>();
	private Set<String> fullAutoCompletionWords = new HashSet<String>();
	private Set<String> keywords = new HashSet<String>();
	private boolean bInCompleteTask = false;
	
	private Color searchTextFieldBackground = null;
	private Highlighter hilit = null;
	private Highlighter.HighlightPainter painter = null;
	private JTextField searchTextField = null;

	private UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();
	
	private MutableAttributeSet keywordStyle = null;
	private MutableAttributeSet defaultStyle = null;
	private MutableAttributeSet commentStyle = null;
	
	private MyUndoableEditListener undoListener = new MyUndoableEditListener();
	private int searchPointer = 0;
	
	private String textContent = null;
	
	private JPopupMenu autoCompJPopupMenu = null;
	private JList autoCompJList = null;
	private DefaultListModel listModel = null;
	private ArrayList<String> functList = new ArrayList<String>();

	class MyPasteAction extends DefaultEditorKit.PasteAction {
	
		public void actionPerformed(ActionEvent e) {			
            JTextComponent target = getTextComponent(e);            
            if (target != null) {
            	int pos = target.getCaretPosition();
            	String text = null;
           	 	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
           	 	try {
					Transferable content = clipboard.getContents(null);
					if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						text = (String)content.getTransferData(DataFlavor.stringFlavor);						
					}
				} catch (UnsupportedFlavorException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				textPane.setCharacterAttributes(getDefaultStyle(), true);
                target.paste();
            	if (text.length() > 0) {
            		highlightKeywords(text, pos);
            	}
            }
        }
	}
	
	class UndoAction extends AbstractAction {
		public UndoAction() {
			super(UNDO_ACTION);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
//				String pname = undoManager.getUndoPresentationName();
//				System.out.println("undo presentationName = " + pname);
				if (undoManager.canUndo()) {
					undoManager.undo();
				}
			} catch (CannotUndoException ex) {
				System.out.println("Unable to undo: " + ex);
				ex.printStackTrace();
			}
			updateUndoState();
			redoAction.updateRedoState();
			repaint();
		}

		protected void updateUndoState() {
			if (undoManager.canUndo()) {
				setEnabled(true);
			} else {
				setEnabled(false);
			}
		}
	}

	class RedoAction extends AbstractAction {
		public RedoAction() {
			super(REDO_ACTION);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				if (undoManager.canRedo()) {
					undoManager.redo();
				}
			} catch (CannotRedoException ex) {
				System.out.println("Unable to redo: " + ex);
				ex.printStackTrace();
			}
			updateRedoState();
			undoAction.updateUndoState();
			repaint();
		}

		protected void updateRedoState() {
			if (undoManager.canRedo()) {
				setEnabled(true);
			} else {
				setEnabled(false);
			}
		}
	}

	protected class MyUndoableEditListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			// Remember the edit and update the menus
			String presentationName = e.getEdit().getPresentationName();
			if (presentationName.indexOf("style") >= 0) {
				return;
			}
//			System.out.println("presentationName = " + presentationName);
			undoManager.addEdit(e.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();			
		}
	}

	class CancelSearchAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			hilit.removeAllHighlights();
			searchTextField.setText("");
			searchTextField.setBackground(searchTextFieldBackground);
		}
	}

	private class CompletionTask implements Runnable {		
		public void run() {		
			bInCompleteTask = true;
			try {			
				String match = (String)autoCompJList.getSelectedValue();
				if (match == null) {
					return;
				}
				boolean bFunction = match.endsWith(")");
				
				CurrentWord currentWord = findCurrentWord(null);
				int startPos = 0;
				Document doc = textPane.getDocument();
				if (currentWord == null) {
					doc.insertString(0, match, null);
					setCaretPosition(match.length() + (bFunction ? -1 : 0));				
				} else {
					startPos = currentWord.startPos;
					doc.remove(currentWord.startPos, currentWord.endPos - currentWord.startPos + 1);
					doc.insertString(currentWord.startPos, match, null);
					setCaretPosition(currentWord.startPos + match.length() + (bFunction ? -1 : 0));
				}
				highlightKeywords(match, startPos);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
//						textPane.requestFocusInWindow();
						autoCompJPopupMenu.setVisible(false);
					}
				});	
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {			
				bInCompleteTask = false;
			}
		}
	}
	
//	private class CompletionTask implements Runnable {
//		String completion;
//		int position;
//		String prefix;
//
//		CompletionTask(String completion, String prefix, int position) {
//			this.completion = completion;
//			this.prefix = prefix;
//			this.position = position;
//		}
//
//		public void run() {
//			try {
//				getTextPane().getDocument().insertString(position, completion, null);
//				highlightKeywords(prefix + completion, position - prefix.length());
//				getTextPane().setCaretPosition(position + completion.length());
//				getTextPane().moveCaretPosition(position);
//				mode = Mode.COMPLETION;
//			} catch (BadLocationException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	private class CommitAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			if (ev.getSource() == autoCompJList || ev.getSource() == textPane && autoCompJPopupMenu.isVisible() && autoCompJList.getSelectedIndex() >= 0) {
				SwingUtilities.invokeLater(new CompletionTask());
			} else if (ev.getSource() == textPane) {
				defaultKeyEnterAction.actionPerformed(ev);
			}
		}
	}

	// up and down key
	private class GoToList extends AbstractAction {
		int increment = 0;
		
		public GoToList(int incr) {
			increment = incr;
			if (increment != -1 && increment != 1) {
				throw new RuntimeException();
			}
		}
		public void actionPerformed(ActionEvent ev) {
			if (autoCompJPopupMenu.isVisible()) {
				int totalLen = listModel.getSize();
				if (totalLen == 0) {
					return;
				}
				int si = Math.min(totalLen - 1, Math.max(0, autoCompJList.getSelectedIndex() + increment));
				autoCompJList.setSelectedIndex(si);
				autoCompJList.ensureIndexIsVisible(si);				
			} else {
				if (increment == -1) {
					defaultKeyUpAction.actionPerformed(ev);
				} else {
					defaultKeyDownAction.actionPerformed(ev);
				}
			}
		} 
	}
	
	private class ShowList extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			autoComplete(null, true);
		}
	}
	
	private class DismissList extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			if (autoCompJPopupMenu.isVisible()) {
				autoCompJPopupMenu.setVisible(false);
//				textPane.requestFocusInWindow();
			}
		}
	}
	
	public class LineNumberPanel extends JPanel {
		private int preferred_size = 50;
		private int errorLine = -1;			// 1-indexed; once the user presses any key the errorLine is reset

		public LineNumberPanel() {
			super();
			setForeground(java.awt.Color.white);
			setBackground(java.awt.Color.gray);
			setMinimumSize(new Dimension(preferred_size, preferred_size));
			setPreferredSize(new Dimension(preferred_size, preferred_size));
			setMinimumSize(new Dimension(preferred_size, preferred_size));
			
			setBorder(new MatteBorder(0,0,1,0, Color.gray));
		}
		
		public void setErrorLine(int line) {
			if(line >= 0) {
				this.errorLine = line + 1;	// line is 0-indexed while errorLine is 1-indexed
			} else {
				this.errorLine = -1;
			}
		}

		public void paint(Graphics g) {
			super.paint(g);

			// We need to properly convert the points to match the viewport
			// Read docs for viewport
			int start = getTextPane().viewToModel(					// starting position in document
					scrollPane.getViewport().getViewPosition());
			
			int end = getTextPane().viewToModel(					// end position in doc
					new Point(scrollPane.getViewport().getViewPosition().x
							+ getTextPane().getWidth(), scrollPane
							.getViewport().getViewPosition().y
							+ getTextPane().getHeight()));
			
			// translate offsets to lines			
			Element defaultRootElement = getTextPane().getDocument().getDefaultRootElement();
			int startline = defaultRootElement.getElementIndex(start) + 1;
			int endline = defaultRootElement.getElementIndex(end) + 1;

			java.awt.FontMetrics fm = g.getFontMetrics(getTextPane().getFont());
			int fontHeight = fm.getHeight();
			int fontDesc = fm.getDescent();
			int starting_y = -1;

			try {
				starting_y = getTextPane().modelToView(start).y - scrollPane.getViewport().getViewPosition().y	+ fontHeight - fontDesc;
			} catch (javax.swing.text.BadLocationException ex) {
				ex.printStackTrace();
				starting_y = 0;
			}

			g.setFont(getTextPane().getFont());
			for (int line = startline, y = starting_y; line <= endline; y += fontHeight, line++) {
				String linestr = Integer.toString(line);
				int width = 0;
				for (int i = 0; i < linestr.length(); i++) {
					width += fm.charWidth(linestr.charAt(i));
				}
				
				if(line == errorLine) {
//					Icon icon = VCellIcons.issueGoodIcon;
					Color oldColor = g.getColor();
					Font oldFont = g.getFont();
					Font newFont = oldFont.deriveFont(Font.BOLD);
					g.setColor(Color.red);
					g.setFont(newFont);
					g.drawString(linestr, preferred_size - width - 2, y + 2);
					g.setColor(oldColor);
					g.setFont(oldFont);
				} else {
					g.drawString(linestr, preferred_size - width - 2, y + 2);
				}
			}
		}
	}
	
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private Action defaultKeyUpAction;
	private Action defaultKeyDownAction;
	private Action defaultKeyEnterAction;
	private static final int NOT_THERE = -1;
	
	private class InternalEventHandler implements MouseListener, KeyListener, DocumentListener, ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getSearchPrevButton()) {
				searchPrevNext(false);
			} else if (e.getSource() == getSearchNextButton()) {
				searchPrevNext(true);
			}
		}
		public void changedUpdate(DocumentEvent e) {
			onDocumentEvent(e);	
		}
		
		public void insertUpdate(DocumentEvent ev) {
			onDocumentEvent(ev);
		}
		
		public void removeUpdate(DocumentEvent e) {
			onDocumentEvent(e);
		}
		
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == autoCompJList) {
				autoCompJList.setSelectionBackground(textPane.getSelectionColor());
				if (e.getClickCount() == 2) {
					SwingUtilities.invokeLater(new CompletionTask());
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
			
		}

		public void mouseExited(MouseEvent e) {
			
		}

		public void mousePressed(MouseEvent e) {
			
		}

		public void mouseReleased(MouseEvent e) {
			
		}
		
		public void keyPressed(KeyEvent e) {
			
		}

		public void keyReleased(KeyEvent e) {
			if (e.getSource() == textPane) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (e.getModifiersEx() != 0) {
						autoCompJPopupMenu.setVisible(false);
					} else {
						autoComplete(null);
					}
				}
			} else if (e.getSource() == autoCompJList) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (autoCompJPopupMenu.isVisible()) {
						SwingUtilities.invokeLater(new CompletionTask());
					}
				}
			}
		}

		public void keyTyped(KeyEvent e) {
			numberPanel.setErrorLine(-1);		// reset error line once we start typing
		}
	}
	
	public MultiPurposeTextPanel() {
		this(true);
	}

	public MultiPurposeTextPanel(boolean editable) {
		super();

		numberPanel = getLineNumberPanel();
		scrollPane = new JScrollPane(getTextPane());
		setLayout(new BorderLayout());
		add(numberPanel, BorderLayout.WEST);
		add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel label = new JLabel("Search");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		panel.add(label);
		panel.add(getSearchTextField());
		panel.add(getSearchPrevButton());
		panel.add(getSearchNextButton());

		add(panel, BorderLayout.SOUTH);
		
		getTextPane().setEditable(editable);
		
		if (editable) {
			listModel = new DefaultListModel();
			autoCompJList = new JList(listModel);
			autoCompJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			autoCompJList.addMouseListener(eventHandler);

			autoCompJPopupMenu = new JPopupMenu();
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(autoCompJList);
			autoCompJPopupMenu.add(scrollPane);
			panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			
			JPanel panel1 = new JPanel();
			JLabel infoLabel1 = new JLabel("\u2191\u2193 to move; 'Esc' to cancel; 'Enter' to accept;");
			Font font = getFont();
			infoLabel1.setFont(font.deriveFont(AffineTransform.getScaleInstance(0.9, 0.9)));
			panel1.add(infoLabel1);
			panel.add(panel1);
			autoCompJPopupMenu.add(panel);
			
			// functions
			for (FunctionType ft : FunctionType.values()) {
				functList.add(ft.getName());
			}
			Collections.sort(functList);
			
			// set up actions
			InputMap im = autoCompJList.getInputMap();
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), COMMIT_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), DISMISSLIST_ACTION);
			
			ActionMap am = autoCompJList.getActionMap();			
			am.put(COMMIT_ACTION, new CommitAction());
			am.put(DISMISSLIST_ACTION, new DismissList());
			
			im = textPane.getInputMap();
			am = textPane.getActionMap();
			Object obj1 = im.get(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
			Object obj2 = im.get(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
			Object obj3 = im.get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
			defaultKeyUpAction = am.get(obj1);
			defaultKeyDownAction = am.get(obj2);
			defaultKeyEnterAction = am.get(obj3);

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), COMMIT_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), GOUPLIST_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), GODOWNLIST_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, Event.CTRL_MASK), SHOWLIST_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), DISMISSLIST_ACTION);			
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK), UNDO_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK), REDO_ACTION);
			
			am.put(COMMIT_ACTION, new CommitAction());
			am.put(GOUPLIST_ACTION, new GoToList(-1));
			am.put(GODOWNLIST_ACTION, new GoToList(1));
			am.put(SHOWLIST_ACTION, new ShowList());
			am.put(DISMISSLIST_ACTION, new DismissList());
			am.put(UNDO_ACTION, undoAction);
			am.put(REDO_ACTION, redoAction);
			am.put(DefaultEditorKit.pasteAction, new MyPasteAction());			
		}
	}

	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			try {
				searchTextField = new JTextField();
				searchTextField.setColumns(15);

				searchTextFieldBackground = searchTextField.getBackground();
				searchTextField.getDocument().addDocumentListener(eventHandler);

				InputMap im = searchTextField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				im.put(KeyStroke.getKeyStroke("ESCAPE"), CANCEL_ACTION);
				
				ActionMap am = searchTextField.getActionMap();
				am.put(CANCEL_ACTION, new CancelSearchAction());

			} catch (java.lang.Throwable ivjExc) {
				ivjExc.printStackTrace();
			}
		}
		return searchTextField;
	}

	private JButton getSearchPrevButton() {
		if (searchPrevButton == null) {
			try {
				searchPrevButton = new JButton();
				searchPrevButton.setIcon(new javax.swing.ImageIcon(getClass()
						.getResource("/icons/arrow_up.jpg")));
				searchPrevButton.setBorder(BorderFactory
						.createRaisedBevelBorder());
				searchPrevButton.setToolTipText("Previous");
				searchPrevButton.setEnabled(false);
				searchPrevButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable ivjExc) {
				ivjExc.printStackTrace();
			}
		}
		return searchPrevButton;
	}

	private JButton getSearchNextButton() {
		if (searchNextButton == null) {
			try {
				searchNextButton = new JButton();
				searchNextButton.setIcon(new javax.swing.ImageIcon(getClass()
						.getResource("/icons/arrow_down.jpg")));
				searchNextButton.setBorder(BorderFactory
						.createRaisedBevelBorder());
				searchNextButton.setToolTipText("Next");
				searchNextButton.setEnabled(false);
				searchNextButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable ivjExc) {
				ivjExc.printStackTrace();
			}
		}
		return searchNextButton;
	}

	/**
	 * Insert the method's description here. Creation date: (10/10/2006 2:17:29
	 * PM)
	 * 
	 * @return int
	 */
	public int getLineStartOffset(int line)	throws javax.swing.text.BadLocationException {
		Element map = getTextPane().getDocument().getDefaultRootElement();
        Element lineElem = map.getElement(line);
        return lineElem.getStartOffset();
	}

	/**
	 * Insert the method's description here. Creation date: (10/9/2006 1:52:17
	 * PM)
	 * 
	 * @return java.lang.String
	 */
	public String getText() {
		return getTextPane().getText();
	}

	/**
	 * Insert the method's description here. Creation date: (10/11/2006 12:04:52
	 * PM)
	 * 
	 * @return javax.swing.text.Document
	 */
	public JTextPane getTextPane() {
		if (textPane == null) {
			textPane = new JTextPane()
			// we need to override paint so that the linenumbers stay in sync
			{
				public void paint(Graphics g) {
					super.paint(g);
					numberPanel.repaint();
				}
			};
			textPane.setHighlighter(getHighlighter());
			textPane.getDocument().addDocumentListener(eventHandler);
			textPane.getDocument().addUndoableEditListener(undoListener);
			textPane.addKeyListener(eventHandler);
		}
		return textPane;
	}
	public LineNumberPanel getLineNumberPanel() {
		if(numberPanel == null) {
			numberPanel = new LineNumberPanel();
		}
		return numberPanel;
	}

	// test main
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		final MultiPurposeTextPanel nr = new MultiPurposeTextPanel();
		frame.getContentPane().add(nr);
		frame.pack();
		frame.setSize(new Dimension(400, 400));
		frame.setVisible(true);
	}

	/**
	 * Insert the method's description here. Creation date: (10/10/2006 2:19:21
	 * PM)
	 * 
	 * @param line
	 *            int
	 */
	public void setCaretPosition(int position) {
		textPane.setCaretPosition(position);
	}

	public void setCursor(int line) {
		setCursor(line, 0);
	}
	public void setCursor(int line, int columnOffset) {
		showTextContent();
	    int currentLine = 0;
	    int currentSelection = 0;
	    String tc = getTextPane().getText();
	    int seperatorLength = 1;
	    String separator = "\n";
	    while (currentLine < line) {
	        int next = tc.indexOf(separator,currentSelection);
	        if (next > -1) {
	            currentSelection = next + seperatorLength;
	            currentLine++;
	        } else {
	            // set to the end of doc
	            currentSelection = tc.length();
	            currentLine= line; // exits loop
	        }
	    }
		textPane.setCaretPosition(currentSelection + columnOffset);
	}

	private void scrollToShow(int position) {
		try {
			setCaretPosition(position);
			Rectangle r = textPane.modelToView(position);
			r.width += 1;
			if (r != null) {
				Rectangle vr = scrollPane.getViewport().getViewRect();
				if (!vr.contains(r)) {
					scrollPane.getViewport().scrollRectToVisible(r);
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private String filterCarriageReturn(String text) {
		String newText = text;
		if (newText.indexOf("\r") >= 0) {
			// somehow "\r" causes weird problem, 
			// which looks like "\r" doesn't take space.
			// so get rid of "\r" from the text
			StringBuffer sb = new StringBuffer();
			StringTokenizer st = new StringTokenizer(newText, "\r\n", true);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (!token.equals("\r")) {
					sb.append(token);
				}
			}
			newText = sb.toString();			
		}
		return newText;
	}

	public void setText(String text) {		
		textContent = filterCarriageReturn(text);
		textPane.setText(null);
		fullAutoCompletionWords.clear();
		fullAutoCompletionWords.addAll(autoCompletionWords);
		textPane.setCharacterAttributes(getDefaultStyle(), true);
		
		if (textPane.isShowing()) {
			updateTextPane();
		}		
	}
	
	/**
	 * Insert the method's description here. Creation date: (10/9/2006 1:53:03
	 * PM)
	 * 
	 * @param text
	 *            java.lang.String
	 */
	private void showTextContent() {
		if (textPane.getText() != null && textPane.getText().length()>0) {
			return;
		}
		textPane.getDocument().removeDocumentListener(eventHandler);
		textPane.getDocument().removeUndoableEditListener(undoListener);
				
		textPane.setText(textContent);
		highlightKeywords(textContent, 0);
		highlightComments();
		
		textPane.getDocument().addDocumentListener(eventHandler);
		textPane.getDocument().addUndoableEditListener(undoListener);
		
		setCaretPosition(0);
	}

	/**
	 * Insert the method's description here. Creation date: (10/10/2006 10:16:15
	 * AM)
	 * 
	 * @param f
	 *            java.awt.Font
	 */
	public void setTextFont(java.awt.Font f) {
		textPane.setFont(f);
	}
	
	private ArrayList<String> createAutoCompletionList(CurrentWord currentWord) {
		ArrayList<String> tempList = new ArrayList<String>(); 
		for (String w : fullAutoCompletionWords) {
			if (currentWord.prefix.length() == 0 
					|| w.toLowerCase().startsWith(currentWord.prefix.toLowerCase())) {
				tempList.add(w);
			}
		}
		
		Collections.sort(tempList, new Comparator<String>() {
			public int compare(String o1, String o2) {
				ReservedVariable v1 = ReservedMathSymbolEntries.getReservedVariableEntry(o1);
				ReservedVariable v2 = ReservedMathSymbolEntries.getReservedVariableEntry(o2);
				if (v1 == null && v2 != null) {
					return 1;
				} else if (v1 != null && v2 == null) {
					return -1;
				}
				return o1.compareToIgnoreCase(o2);
			}
		});	
		
		// add functions
		ArrayList<String> tempFuncList = new ArrayList<String>();
		for (String w : functList) {
			if (currentWord.prefix.length() == 0 
					|| w.toLowerCase().startsWith(currentWord.prefix.toLowerCase())) {
				tempFuncList.add(w + "()");				
			}
		}		
		tempList.addAll(tempFuncList);
		
		return tempList;
	}
	
	private void autoComplete(DocumentEvent ev) { 
		autoComplete(ev, false);
	}
	private void autoComplete(DocumentEvent docEvent, boolean bForce) {	
		try {
			if (bInCompleteTask) {
				return;
			}
			autoCompJPopupMenu.setVisible(false);
			if (!isShowing()) {
				return;
			}
			
			if (docEvent != null && docEvent.getDocument() == getSearchTextField().getDocument()) {
				return;
			}
			
			if (fullAutoCompletionWords.size() == 0 || docEvent != null && docEvent.getLength() != 1) {
				return;
			}
	
			CurrentWord currentWord = findCurrentWord(docEvent);
			if (currentWord == null) {
				return;
			} 
			
			if (currentWord.prefix.length() == 0 && !bForce) {
				return;
			}
			ArrayList<String> tempList = createAutoCompletionList(currentWord);
			listModel.removeAllElements();
			for (String s : tempList) {
				listModel.addElement(s);
			}
	       	   
	       	if (listModel.getSize() > 0) {
	       		autoCompJList.setVisibleRowCount(Math.min(8, Math.max(3, listModel.getSize())));
	       		autoCompJList.ensureIndexIsVisible(0);
	       		autoCompJList.setSelectionBackground(Color.lightGray);				
				
	       		Rectangle loc = null;
	       		int caretPosition = textPane.getCaretPosition();
	       		if (docEvent != null) {
	    			EventType type = docEvent.getType();
	    			if (type == EventType.INSERT) {
	    				caretPosition += 1;
	    			} else if (type == EventType.REMOVE){
	    				caretPosition -= 1;
	    			}
	    		}
       			loc = textPane.modelToView(caretPosition);
       			autoCompJPopupMenu.setFocusable(false);
       			autoCompJPopupMenu.show(textPane, loc.x, loc.y + loc.height);
	       	}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		} finally {
//			textPane.requestFocusInWindow();
			// for mac, for some reason, when popup menu shows up
			// the whole text was selected. So we need to de-select 
			// everything.
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					int caretPosition = textPane.getCaretPosition();
					textPane.setCaretPosition(caretPosition);
					textPane.moveCaretPosition(caretPosition);				
				}
			});
		}
	}

	private static class CurrentWord {
		int startPos = 0;
		String prefix = "";
		int endPos = 0;
	} 

	private CurrentWord findCurrentWord(DocumentEvent docEvt) {
		if (docEvt != null && docEvt.getLength() != 1) {
			return null;
		}
		
		String text = getText();		
		int txtLen = text.length();
		if (txtLen == 0) {
			return null;
		}
		
		int pos = textPane.getCaretPosition();
		if (docEvt != null) {
			EventType type = docEvt.getType();
			if (type == EventType.INSERT) {
				pos += 1;
			} else if (type == EventType.REMOVE){
				pos -= 1;
			}
		}
		if (pos < 0) {
			return null;
		}
		pos = Math.min(pos, text.length());
		
		CurrentWord currentWord = new CurrentWord();

		// Find where the word starts
		int w;
		for (w = pos-1; w >= 0; w--) {
			char c = text.charAt(w);
			if ((c == '.' || c == ')') && w == pos - 1) {
				continue; // if . or ) is at the end of the word, include it
			}
			if (!Character.isJavaIdentifierPart(c)) {
				break;
			}
		}
		
		currentWord.prefix = text.substring(w + 1, pos);
		currentWord.startPos = w + 1; 
		
		for (w = pos; w < text.length(); w++) {
			char c = text.charAt(w);
			if (!Character.isJavaIdentifierPart(c)) {
				break;
			}
		}	
		currentWord.endPos = w - 1;
		
		return currentWord;
	}

	private Highlighter getHighlighter() {
		if (hilit == null) {
			try {
				hilit = new DefaultHighlighter();
			} catch (java.lang.Throwable ivjExc) {
				ivjExc.printStackTrace();
			}
		}
		return hilit;
	}

	private Highlighter.HighlightPainter getHighlighterPainter() {
		if (painter == null) {
			try {
				painter = new MathSearchPainter(Color.YELLOW);
			} catch (java.lang.Throwable ivjExc) {
				ivjExc.printStackTrace();
			}
		}
		return painter;
	}

	private void search() {
		searchPointer  = 0;
		searchStartOffset = -1;
		searchEndOffset = -1;
		getHighlighter().removeAllHighlights();
		searchTextField.setBackground(searchTextFieldBackground);

		String s = searchTextField.getText().toLowerCase();
		if (s.length() <= 0) {
			getSearchPrevButton().setEnabled(false);
			getSearchNextButton().setEnabled(false);
			return;
		}

		String content = getText().toLowerCase();
		int index = content.indexOf(s, 0);
		if (index >= 0) { // match found
			getSearchPrevButton().setEnabled(true);
			getSearchNextButton().setEnabled(true);
			while (index >= 0) {
				try {
					int end = index + s.length();
					getHighlighter().addHighlight(index, end, getHighlighterPainter());
					index = content.indexOf(s, end);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			searchPrevNext(true);
		} else {
			searchTextField.setBackground(Color.PINK);
			getSearchPrevButton().setEnabled(false);
			getSearchNextButton().setEnabled(false);
		}
	}

	private void searchPrevNext(boolean bNext) {
		Highlight[] highlights = getHighlighter().getHighlights();
		if (highlights.length == 0) {
			return;
		}
		int currentPosition = textPane.getCaretPosition();
		if (bNext) {
			int oldSearchStartOffset = searchStartOffset;
			while (true) {
				searchStartOffset = highlights[searchPointer].getStartOffset();
				if (searchStartOffset > currentPosition
						|| currentPosition == searchStartOffset
						&& oldSearchStartOffset == -1) {
					break;
				}
				if (searchPointer >= highlights.length - 1) {
					currentPosition = 0;
					searchPointer = 0;
				} else {
					searchPointer++;
				}
			}
		} else {
			while (true) {
				searchStartOffset = highlights[searchPointer].getStartOffset();
				if (searchStartOffset < currentPosition) {
					break;
				}
				if (searchPointer <= 0) {
					currentPosition = getText().length();
					searchPointer = highlights.length - 1;
				} else {
					searchPointer--;
				}
			}
		}
		if (searchPointer < highlights.length && searchPointer >= 0) {
			searchEndOffset = highlights[searchPointer].getEndOffset();
			scrollToShow(searchStartOffset);
		} else {
			searchStartOffset = -1;
			searchEndOffset = -1;
		}
		repaint();
	}

	class MathSearchPainter extends DefaultHighlighter.DefaultHighlightPainter {
		private Color currentColor = null;
		private Color selectColor = new Color(0x6495ED);

		public MathSearchPainter(Color c) {
			super(c);
			currentColor = c;
		}

		public Color getColor() {
			return currentColor;
		}

		public void paint(Graphics g, int offs0, int offs1, Shape bounds,
				JTextComponent c) {
			currentColor = super.getColor();
			if (searchStartOffset >= offs0 && searchEndOffset <= offs1) {
				currentColor = selectColor;
			}
			super.paint(g, offs0, offs1, bounds, c);
		}

		public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
				JTextComponent c, View view) {
			currentColor = super.getColor();
			if (searchStartOffset <= offs0 && searchEndOffset >= offs1) {
				currentColor = selectColor;
			}
			Shape shape = super.paintLayer(g, offs0, offs1, bounds, c, view);
			return shape;
		}
	}
	
	private void onDocumentEvent(DocumentEvent e) {
		if (e.getDocument() == getSearchTextField().getDocument()) {
			search();
			return;
		}
		clearSearchText();
		
		if (e instanceof AbstractDocument.DefaultDocumentEvent) {
			if (((AbstractDocument.DefaultDocumentEvent)e).getPresentationName().indexOf("style") >= 0) {
				return;
			}
		} else {
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				highlightUpdate();
			}
		});
	
		if (textPane.isEditable()) {
			autoComplete(e);
		}
	}
	
	private void highlightUpdate() {	
		if (keywords.size() < 1) {
			return;
		}		
		if (textPane.getSelectedText() != null) {
			return;
		}
		String content = textPane.getText();
		if (content.trim().length() < 1) {
			return;
		}
		
		int pos = textPane.getCaretPosition();
		
		int textLength = textPane.getText().length();
		if (pos == 0 || pos >= textLength) {
			return;
		}
		
		char ch = content.charAt(pos - 1);	
		// Find where the word starts
		int w1;
		for (w1 = Math.min(pos-1, content.length() - 1); w1 >= 0; w1--) {
			char c = content.charAt(w1);
			if (!isIdentifierPart(c)) {
				break;
			}
		}
		w1 = Math.max(0, w1+1);
		int w2;
		for (w2 = pos; w2 < content.length(); w2 ++) {
			char c = content.charAt(w2);
			if (!isIdentifierPart(c)) {
				break;
			}
		}
		if (w2 > w1) {
			try {
				String substr = content.substring(w1, w2);
				((StyledDocument)getTextPane().getDocument()).setCharacterAttributes(w1, w2 - w1, 
								keywords.contains(substr) ? getKeywordStyle() : getDefaultStyle(), true);
			} catch (IllegalStateException ex) {
				ex.printStackTrace();
			}
		}
		if (!isIdentifierPart(ch)) {
			w2 = pos - 1;
			for (w1 = pos - 2 ; w1 >= 0; w1 --) {
				char c = content.charAt(w1);
				if (!isIdentifierPart(c)) {
					break;
				}
			}
			w1 = Math.max(0, w1+1);
			if (w2 > w1) {
				try {
					String substr = content.substring(w1, w2);
					((StyledDocument)getTextPane().getDocument()).setCharacterAttributes(w1, w2 - w1, 
									keywords.contains(substr) ? getKeywordStyle() : getDefaultStyle(), true);
				} catch (IllegalStateException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void clearSearchText() {
		getSearchTextField().setText("");
	}

	public void setAutoCompletionWords(Set<String> acw) {
		autoCompletionWords.addAll(acw);
	}
	
	public void setKeywords(Set<String> kw) {
		keywords.addAll(kw);
	}
	
	public JMenu createEditMenu() {
	    JMenu menu = new JMenu("Edit");
	    menu.add(undoAction);
	    menu.add(redoAction);
	    
	    menu.addSeparator();
	    
	    JMenuItem mi = new JMenuItem("Cut       Ctrl+X");
	    mi.addActionListener(getTextPane().getActionMap().get(DefaultEditorKit.cutAction));
	    menu.add(mi);

	    mi = new JMenuItem("Copy    Ctrl+C");
	    mi.addActionListener(getTextPane().getActionMap().get(DefaultEditorKit.copyAction));
	    menu.add(mi);
	    
	    mi = new JMenuItem("Paste   Ctrl+V");
	    mi.addActionListener(getTextPane().getActionMap().get(DefaultEditorKit.pasteAction));
	    menu.add(mi);
	    
	    return menu;
	}
	
	private MutableAttributeSet getKeywordStyle() {
		if (keywordStyle == null) {
			keywordStyle = new SimpleAttributeSet();
			StyleConstants.setForeground(keywordStyle, Color.blue);
			StyleConstants.setBold(keywordStyle, true);
		}
		
		return keywordStyle;		
	}
	
	private MutableAttributeSet getDefaultStyle() {
		if (defaultStyle == null) {
			defaultStyle = new SimpleAttributeSet();
			StyleConstants.setForeground(defaultStyle, Color.black);
			StyleConstants.setBold(defaultStyle, false);
		}
		
		return defaultStyle;		
	}
	
	private MutableAttributeSet getCommentStyle() {
		if (commentStyle == null) {
			commentStyle = new SimpleAttributeSet();
			StyleConstants.setForeground(commentStyle, Color.magenta);
			StyleConstants.setItalic(commentStyle, true);
			
		}
		return commentStyle;
		
	}
	
	private class KeywordToken {
		  private String contents;
		  private int charBegin;
		  public KeywordToken(String contents, int charBegin) {
			super();
			this.contents = contents;
			this.charBegin = charBegin;
		}
	}
	
	private void highlightKeywords(String text, int position) {
		if (text == null || keywords.size() < 1) {
			return;
		}
		ArrayList<KeywordToken> tokenList = new ArrayList<KeywordToken>();
		int p0 = 0;
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreTokens()) {
			while (p0 < text.length()) {
				char ch = text.charAt(p0);
				if (ch == ' ' || ch == '\n' || ch == '\f' || ch == '\t' || ch == '\r') {	
					p0 ++;
				} else { 
					break;
				}
			}			
			String token = st.nextToken();
			if (keywords.contains(token)) {
				tokenList.add(new KeywordToken(token, p0 + position));
			}
			if (Character.isJavaIdentifierStart(token.charAt(0)) && Character.isJavaIdentifierPart(token.charAt(token.length() - 1))) {			
				fullAutoCompletionWords.add(token);
			}
			p0 = p0 + token.length();
		}
		for (KeywordToken kt : tokenList) {
			((StyledDocument)getTextPane().getDocument()).setCharacterAttributes(kt.charBegin, kt.contents.length(),  getKeywordStyle(), true);
		}
	}
	
	private void highlightComments( ) {
		String text = getTextPane( ).getText();
		StyledDocument doc = (StyledDocument) getTextPane( ).getDocument();
		MutableAttributeSet cstyle = getCommentStyle();
		boolean inComment = false;
		try (CountingLineReader reader = new CountingLineReader(new StringReader(text))) {
			String line = reader.readLine();
			while (line != null) {
				if (!inComment) {
					int cstart = line.indexOf(Commented.BEFORE_COMMENT);
					if (cstart != NOT_THERE) {
						inComment = parseAndMarkEndComment(doc, line, reader.lastStringPosition(),cstart); 
					}

					int start = line.indexOf(Commented.AFTER_COMMENT);
					if (start != NOT_THERE) {
						int length = line.length( ) - start;
						doc.setCharacterAttributes(reader.lastStringPosition() + start, length, cstyle,true); 
					}
				}
				else { //currently inside a /* */. comment
					inComment = parseAndMarkEndComment(doc, line, reader.lastStringPosition(),0); 
				}

				line = reader.readLine();
			}
		} catch (IOException e) {

		}
	}
	
	/**
	 * highlight inline comment and see 
	 * see if /* comment ends on same line
	 * @param doc
	 * @param line
	 * @param lineStart position of beginning of line in doc
	 * @param cstart starting position of comment
	 * @return true if comment continues 
	 */
	private boolean parseAndMarkEndComment(StyledDocument doc, String line, int lineStart, int cstart) {
		int cend = line.indexOf(Commented.END_BEFORE_COMMENT);
		boolean endFound = cend != NOT_THERE;
		int length;
		if (endFound) {
			length = (cend + Commented.END_BEFORE_COMMENT_LENGTH) - cstart; 
		}
		else {
			length = line.length( ) - cstart;
		}
		doc.setCharacterAttributes(lineStart + cstart, length, getCommentStyle(),true); 
		return !endFound;
	}

	public static boolean isIdentifierPart(char ch) {
		return Character.isJavaIdentifierStart(ch) || Character.isDigit(ch);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateTextPane();
			}
		});
	}

	private void updateTextPane() {
		final Frame parentFrame = JOptionPane.getFrameForComponent(MultiPurposeTextPanel.this);
		BeanUtils.setCursorThroughout(parentFrame, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {			
			String text = textPane.getText();
			boolean bNewText = text == null || text.length() == 0;			
			showTextContent();
			if (bNewText) {
				undoManager.discardAllEdits();
			}
		} finally {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {			
					BeanUtils.setCursorThroughout(parentFrame, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
	}
}
