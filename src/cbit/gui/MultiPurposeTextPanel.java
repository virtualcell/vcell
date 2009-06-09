package cbit.gui;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2006 1:17:05 PM)
 * @author: Fei Gao
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class MultiPurposeTextPanel extends JPanel implements DocumentListener, ActionListener, KeyListener {
	private JTextPane textPane = null;
	private JScrollPane scrollPane = null;
	private LineNumberPanel numberPanel = null;

	private JButton searchPrevButton = null;
	private JButton searchNextButton = null;

	private int searchStartOffset = -1;
	private int searchEndOffset = -1;

	private static final String COMMIT_ACTION = "commit";
	private final static String CANCEL_ACTION = "cancel-search";
	private final static String UNDO_ACTION = "Undo    Crtl+Z";
	private final static String REDO_ACTION = "Redo    Crtl+Y";	
	
	private static enum Mode {
		INSERT, COMPLETION
	};

	private List<String> autoCompletionWords = new ArrayList<String>();
	private List<String> keywords = new ArrayList<String>();
	private Mode mode = Mode.INSERT;
	
	private Color searchTextFieldBackground = null;
	private Highlighter hilit = null;
	private Highlighter.HighlightPainter painter = null;
	private JTextField searchTextField = null;

	private UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();
	
	private MutableAttributeSet keywordStyle = null;
	private MutableAttributeSet defaultStyle = null;
	
	private MyUndoableEditListener undoListener = new MyUndoableEditListener();
	private int searchPointer = 0;

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
                target.paste();
                try {
                	if (text.length() > 0) {
                		highlightKeywords(text, pos);
                	}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
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
				undoManager.undo();
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
				undoManager.redo();
			} catch (CannotRedoException ex) {
				System.out.println("Unable to redo: " + ex);
				ex.printStackTrace();
			}
			updateRedoState();
			undoAction.updateUndoState();
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
		String completion;
		int position;
		String prefix;

		CompletionTask(String completion, String prefix, int position) {
			this.completion = completion;
			this.prefix = prefix;
			this.position = position;
		}

		public void run() {
			try {
				getTextPane().getDocument().insertString(position, completion, null);
				highlightKeywords(prefix + completion, position - prefix.length());
				getTextPane().setCaretPosition(position + completion.length());
				getTextPane().moveCaretPosition(position);
				mode = Mode.COMPLETION;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	private class CommitAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			try {
				if (mode == Mode.COMPLETION) {
					int pos = textPane.getSelectionEnd();
					getTextPane().getDocument().insertString(pos, " ", null);
					getTextPane().setCaretPosition(pos + 1);
					mode = Mode.INSERT;
				} else {
					getTextPane().replaceSelection("\n");
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}				
		}
	}

	public class LineNumberPanel extends JPanel {
		private int preferred_size = 50;

		public LineNumberPanel() {
			super();
			setForeground(java.awt.Color.white);
			setBackground(java.awt.Color.gray);
			setMinimumSize(new Dimension(preferred_size, preferred_size));
			setPreferredSize(new Dimension(preferred_size, preferred_size));
			setMinimumSize(new Dimension(preferred_size, preferred_size));
		}

		public void paint(Graphics g) {
			super.paint(g);

			// We need to properly convert the points to match the viewport
			// Read docs for viewport
			int start = getTextPane().viewToModel(
					scrollPane.getViewport().getViewPosition()); // starting pos
																	// in
																	// document
			int end = getTextPane().viewToModel(
					new Point(scrollPane.getViewport().getViewPosition().x
							+ getTextPane().getWidth(), scrollPane
							.getViewport().getViewPosition().y
							+ getTextPane().getHeight()));
			// end pos in doc

			// translate offsets to lines			
			int startline = getTextPane().getDocument().getDefaultRootElement().getElementIndex(start) + 1;
			int endline = getTextPane().getDocument().getDefaultRootElement().getElementIndex(end) + 1;

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
				g.drawString(linestr, preferred_size - width - 2, y);
			}

		}
	}

	public MultiPurposeTextPanel() {
		super();

		numberPanel = new LineNumberPanel();
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
	}

	public MultiPurposeTextPanel(boolean editable) {
		this();
		getTextPane().setEditable(editable);
	}

	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			try {
				searchTextField = new JTextField();
				searchTextField.setColumns(15);

				searchTextFieldBackground = searchTextField.getBackground();
				searchTextField.getDocument().addDocumentListener(this);

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
				searchPrevButton.addActionListener(this);
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
				searchNextButton.addActionListener(this);
			} catch (java.lang.Throwable ivjExc) {
				ivjExc.printStackTrace();
			}
		}
		return searchNextButton;
	}

	/**
	 * Insert the method's description here. Creation date: (10/10/2006 2:19:21
	 * PM)
	 * 
	 * @param line
	 *            int
	 */
	public int getCaretPosition() {
		return getTextPane().getCaretPosition();
	}

	/**
	 * Insert the method's description here. Creation date: (10/10/2006 2:17:29
	 * PM)
	 * 
	 * @return int
	 */
	public int getLineEndOffset(int line) throws javax.swing.text.BadLocationException {
		Element map = getTextPane().getDocument().getDefaultRootElement();
        Element lineElem = map.getElement(line);
        return lineElem.getEndOffset();
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
			textPane.getDocument().addDocumentListener(this);
			textPane.getDocument().addUndoableEditListener(undoListener);
			textPane.addKeyListener(this);

			InputMap im = textPane.getInputMap();
			im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK), UNDO_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK), REDO_ACTION);
			
			ActionMap am = textPane.getActionMap();			
			am.put(COMMIT_ACTION, new CommitAction());
			am.put(UNDO_ACTION, undoAction);
			am.put(REDO_ACTION, redoAction);
			am.put(DefaultEditorKit.pasteAction, new MyPasteAction());
		}
		return textPane;
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

	public void scrollToShow(int position) {
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

	public void moveCaretPosition(int position) {
		textPane.moveCaretPosition(position);
	}

	/**
	 * Insert the method's description here. Creation date: (10/9/2006 1:53:03
	 * PM)
	 * 
	 * @param text
	 *            java.lang.String
	 */
	public void setText(String text) {
		textPane.getDocument().removeDocumentListener(this);
		textPane.getDocument().removeUndoableEditListener(undoListener);
		textPane.setText(text);	
		try {
			highlightKeywords(text, 0);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		textPane.getDocument().addDocumentListener(this);
		textPane.getDocument().addUndoableEditListener(undoListener);
		getTextPane().setCharacterAttributes(getDefaultStyle(), true);
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

	public void insertUpdate(DocumentEvent ev) {		
		
		if (ev.getDocument() == getSearchTextField().getDocument()) {
			search();
			return;
		}

		clearSearchText();
		
		if (autoCompletionWords == null || autoCompletionWords.size() < 1 || ev.getLength() != 1) {
			return;
		}

		int pos = ev.getOffset();
		String content = null;
		try {
			content = textPane.getText(0, pos + 1);
			char nextChar = textPane.getText(pos + 1, 1).charAt(0);
			if (isIdentifierPart(nextChar)) {
				return;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			char c = content.charAt(w);
			if (!Character.isJavaIdentifierPart(c)) {
				break;
			}
		}

		if (pos - w < 2) {
			// Too few chars
			return;
		}		
		 
		String prefix = content.substring(w + 1);		
		int n = Collections.binarySearch(autoCompletionWords, prefix);
		if (n < 0 && -n <= autoCompletionWords.size()) {
			String match = autoCompletionWords.get(-n - 1);
			if (match.startsWith(prefix)) {
				// A completion is found
				String completion = match.substring(pos - w);
				// We cannot modify Document from within notification,
				// so we submit a task that does the change later
				SwingUtilities.invokeLater(new CompletionTask(completion,
						prefix, pos + 1));
			}
		} else {
			// Nothing found
			mode = Mode.INSERT;			
		}
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

	public void search() {
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
		int currentPosition = getCaretPosition();
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

	public void changedUpdate(DocumentEvent e) {
		if (e.getDocument() == getSearchTextField().getDocument()) {
			search();
			return;
		}
		clearSearchText();
	}

	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument() == getSearchTextField().getDocument()) {
			search();
			return;
		}		
		clearSearchText();
	}
	
	public void highlightUpdate() {	
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
				getTextPane().setCharacterAttributes(getDefaultStyle(), true);
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
					getTextPane().setCharacterAttributes(getDefaultStyle(), true);
				} catch (IllegalStateException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getSearchPrevButton()) {
			searchPrevNext(false);
		} else if (e.getSource() == getSearchNextButton()) {
			searchPrevNext(true);
		}

	}

	public void clearSearchText() {
		getSearchTextField().setText("");
	}

	public void setAutoCompletionWords(List<String> acw) {
		this.autoCompletionWords = acw;
	}
	
	public void setKeywords(List<String> kw) {
		this.keywords = kw;
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
	
	private void highlightKeywords(String text, int position) throws BadLocationException {
		if (keywords.size() < 1) {
			return;
		}
		int p0 = 0;
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreTokens()) {
			String delimiter = "";
			while (p0 < text.length()) {
				char ch = text.charAt(p0);
				if (ch == ' ' || ch == '\n' || ch == '\f' || ch == '\t' || ch == '\r') {	
					delimiter += ch;
					p0 ++;
				} else { 
					((StyledDocument)getTextPane().getDocument()).setCharacterAttributes(p0 + position, delimiter.length(), getDefaultStyle(), true);
					break;
				}
			}			
			String token = st.nextToken();
			((StyledDocument)getTextPane().getDocument()).setCharacterAttributes(p0 + position, token.length(), 
					keywords.contains(token) ? getKeywordStyle() : getDefaultStyle(), true);		
			p0 = p0 + token.length();
		}
		getTextPane().setCharacterAttributes(getDefaultStyle(), true);
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (keywords.size() < 1) {
			return;
		}
		int modifier = e.getModifiersEx();
		if (modifier != 0) {
			return;
		}
		int keyCode = e.getKeyCode();
		if (!e.isActionKey() && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_SHIFT) {
			highlightUpdate();
		}
	}

	public void keyTyped(KeyEvent e) {
	}
	
	public static boolean isIdentifierPart(char ch) {
		return Character.isJavaIdentifierStart(ch) || Character.isDigit(ch);
	}
}