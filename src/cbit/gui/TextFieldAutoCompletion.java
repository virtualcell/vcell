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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.vcell.util.BeanUtils;

import cbit.vcell.math.ReservedMathSymbolEntries;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.resource.OperatingSystemInfo;

@SuppressWarnings("serial")
public class TextFieldAutoCompletion extends JTextField {
	private JPopupMenu autoCompJPopupMenu = null;
	private JList<String> autoCompJList = null;
	private DefaultListModel<String> listModel = null;
	private Set<String> autoCompWordList = null;
	private static final String COMMIT_ACTION = "commit";
	private static final String GOUPLIST_ACTION = "gouplist";
	private static final String GODOWNLIST_ACTION = "godownlist";
	private static final String GOPAGEUPLIST_ACTION = "gopageuplist";
	private static final String GOPAGEDOWNLIST_ACTION = "gopagedownlist";
	private static final String GOHOMELIST_ACTION = "gohomelist";
	private static final String GOENDLIST_ACTION = "goendlist";
	private static final String SHOWLIST_ACTION = "showlist";
	private static final String DISMISSLIST_ACTION = "dimisslist";
	private SymbolTable symbolTable = null;
	
	private boolean bInCompleteTask = false;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private ArrayList<String> functList = new ArrayList<String>();
	private Rectangle highlightRectangle = null;
	private boolean bSetText = false;
	
	OperatingSystemInfo osi = null;
		
	private class InternalEventHandler implements DocumentListener, MouseListener, KeyListener, FocusListener {
		public void changedUpdate(DocumentEvent e) {
			showPopupChoices(e);		
		}

		public void insertUpdate(DocumentEvent e) {
			showPopupChoices(e);		
		}

		public void removeUpdate(DocumentEvent e) {
			showPopupChoices(e);		
		}
		
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == autoCompJList) {
				autoCompJList.setSelectionBackground(getSelectionColor());
				if (e.getClickCount() == 2) {
					SwingUtilities.invokeLater(new CompletionTask());
				}
			} else if (e.getSource() == TextFieldAutoCompletion.this) {
				repaint();
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
			if (e.getSource() == TextFieldAutoCompletion.this) {
				repaint();
				if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (e.getModifiersEx() != 0) {
						autoCompJPopupMenu.setVisible(false);
					} else {
						showPopupChoices(null);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
					if (autoCompJPopupMenu.isVisible()) {
						autoCompJList.setSelectionBackground(getSelectionColor());
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					stopEditing();
				}
			}
		}

		public void keyTyped(KeyEvent e) {
		}

		public void focusGained(FocusEvent e) {			
		}
		public void focusLost(FocusEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					int cp = getCaretPosition();
					setCaretPosition(cp);
					moveCaretPosition(cp);				
				}
			});
			repaint();
		}
		
	}
	
	public TextFieldAutoCompletion() {
		super();
		initialize();
	}
		
	private void initialize() {
		osi = getOperatingSystemInfo();
		getDocument().addDocumentListener(eventHandler);
		addKeyListener(eventHandler);
		addMouseListener(eventHandler);
		
		autoCompWordList = new HashSet<String>();		
		listModel = new DefaultListModel<String>();
		autoCompJList = new JList<String>(listModel);
		autoCompJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		autoCompJList.addMouseListener(eventHandler);

		autoCompJPopupMenu = new JPopupMenu();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(autoCompJList);
		autoCompJPopupMenu.add(scrollPane);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel panel1 = new JPanel();
		JLabel infoLabel1 = new JLabel("\u2191\u2193 to move; 'Esc' to cancel; 'Enter' to accept;");
		Font font = getFont();
		infoLabel1.setFont(font.deriveFont(AffineTransform.getScaleInstance(0.9, 0.9)));
		panel1.add(infoLabel1);
		panel.add(panel1);
		autoCompJPopupMenu.add(panel);
		
		setupActions();
		
		// functions
		for (FunctionType ft : FunctionType.values()) {
			functList.add(ft.getName());
		}
		Collections.sort(functList);
	}
	
	public void setAutoCompletionWords(Set<String> words) {
		autoCompWordList.clear();
		if (words != null) {
			autoCompWordList.addAll(words);
		}
	}
		
	private class GoToList extends AbstractAction {
		int increment = 0;
		boolean bRequireListHasFocus = false;
		
		public GoToList(int incr) {
			this(incr, false);
		}
		
		public GoToList(int incr, boolean bRequireFocus) {
			increment = incr;
			bRequireListHasFocus = bRequireFocus;
		}
		public void actionPerformed(ActionEvent ev) {
			if (autoCompJPopupMenu.isVisible() && (!bRequireListHasFocus || autoCompJList.getSelectedIndex() >= 0)) {
				if (increment != 0) {
					int totalLen = listModel.getSize();
					if (totalLen == 0) {
						return;
					}
					int si = 0;
					if (increment == Integer.MIN_VALUE) {
						si = 0;
					} else if (increment == Integer.MAX_VALUE) {
						si = totalLen - 1;
					} else {
						si = Math.min(totalLen - 1, Math.max(0, autoCompJList.getSelectedIndex() + increment));
					}
					autoCompJList.setSelectedIndex(si);
					autoCompJList.ensureIndexIsVisible(si);
				}
			} else {
				if ((increment == Integer.MIN_VALUE) || (increment == Integer.MAX_VALUE)) {
					setCaretPosition(increment == Integer.MIN_VALUE ? 0 : getText().length());
					showPopupChoices(null);
				}
			}
		}
	}
	
	private class ShowList extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			showPopupChoices(null, true);
		}
	}
	
	private class DismissList extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			if (autoCompJPopupMenu.isVisible()) {
				autoCompJPopupMenu.setVisible(false);
			} else {
				Container parent = TextFieldAutoCompletion.this.getParent();
				if (parent instanceof JTable) {
					((JTable)parent).getCellEditor().cancelCellEditing();
				}
			}
		}
	}

	private class CommitAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			if (ev.getSource() == this) {
				autoCompJPopupMenu.setVisible(false);
			} else {
				SwingUtilities.invokeLater(new CompletionTask());
			}
		}
	}
	
	private class CompletionTask implements Runnable {		
		public void run() {
			onComplete();
		}
	}
	
	private void onComplete() {
		bInCompleteTask = true;
		try {			
			String match = (String)autoCompJList.getSelectedValue();
			if (match == null) {
				return;
			}
			boolean bFunction = match.endsWith(")");
			
			CurrentWord currentWord = findCurrentWord(null);
			Document doc = getDocument();
			if (currentWord == null) {
				doc.insertString(0, match, null);
				setCaretPosition(match.length() + (bFunction ? -1 : 0));				
			} else {
				doc.remove(currentWord.startPos, currentWord.endPos - currentWord.startPos + 1);
				doc.insertString(currentWord.startPos, match, null);
				setCaretPosition(currentWord.startPos + match.length() + (bFunction ? -1 : 0));
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					autoCompJPopupMenu.setVisible(false);
				}
			});	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			bInCompleteTask = false;
		}
	}
	
	private void setupActions() {
		InputMap im = autoCompJList.getInputMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), DISMISSLIST_ACTION);
		
		ActionMap am = autoCompJList.getActionMap();			
		am.put(COMMIT_ACTION, new CommitAction());
		am.put(DISMISSLIST_ACTION, new DismissList());
		
		addFocusListener(eventHandler);
		im = getInputMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), GOUPLIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), GODOWNLIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), GOPAGEUPLIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), GOPAGEDOWNLIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, Event.CTRL_MASK), SHOWLIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), DISMISSLIST_ACTION);	
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), GOHOMELIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), GOENDLIST_ACTION);
		
		am = getActionMap();
		am.put(GOUPLIST_ACTION, new GoToList(-1));
		am.put(GODOWNLIST_ACTION, new GoToList(1));
		am.put(GOPAGEUPLIST_ACTION, new GoToList(-5));
		am.put(GOPAGEDOWNLIST_ACTION, new GoToList(5));
		am.put(GOHOMELIST_ACTION, new GoToList(Integer.MIN_VALUE, true));
		am.put(GOENDLIST_ACTION, new GoToList(Integer.MAX_VALUE, true));
		am.put(SHOWLIST_ACTION, new ShowList());
		am.put(DISMISSLIST_ACTION, new DismissList());
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
		
		int pos = getCaretPosition();
		if (docEvt != null) {
			if (docEvt.getType() == EventType.INSERT) {
				pos += 1;
			} else if (docEvt.getType() == EventType.REMOVE){
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
	
	public void showPopupChoices(DocumentEvent docEvt) {
		showPopupChoices(docEvt, false);
	}
	
	public void showPopupChoices(DocumentEvent docEvt, boolean bForce) {		
		if (bSetText) {
			return;
		}
		try {
			if (bInCompleteTask) {
				return;
			}
			autoCompJPopupMenu.setVisible(false);
			if (!isShowing()) {
				return;
			}
			if (symbolTable == null && (autoCompWordList == null || autoCompWordList.size() == 0)) {				
				return;
			}
			
	        CurrentWord currentWord = findCurrentWord(docEvt);
			if (currentWord == null && !bForce) {
				return;
			} // if the cursor is the at the beginning, don't show list
		
			if (currentWord != null) {
				int len = currentWord.prefix.length();
				if (len > 1) {
					char lastCh = currentWord.prefix.charAt(len - 1);
					if (lastCh == ')' || lastCh == '.') {
						return;
					}
				}
			}
			
	        ArrayList<String> tempList = createAutoCompletionList(currentWord);
			
			listModel.removeAllElements();
			for (String w : tempList) {
				listModel.addElement(w);
			}
	       	   
	       	if (listModel.getSize() > 0) {
	       		autoCompJList.setVisibleRowCount(Math.min(8, Math.max(3, listModel.getSize())));
//	       		if (currentWord.prefix.length() > 0) {
//	       			autoCompJList.setSelectedIndex(0);
//	       		}
	       		autoCompJList.ensureIndexIsVisible(0);
	       		autoCompJList.setSelectionBackground(Color.lightGray);				
				
	       		try {
	       			Rectangle loc = modelToView(getCaretPosition());
	       			autoCompJPopupMenu.setFocusable(false);
	       			autoCompJPopupMenu.show(this, loc.x, loc.y + loc.height);
	       		} catch (BadLocationException ex) {
	       		}
	       	}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		} finally {
			// for mac, for some reason, when popup menu shows up
			// the whole text was selected. So we need to de-select 
			// everything.
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					int cp = getCaretPosition();
					setCaretPosition(cp);
					moveCaretPosition(cp);				
				}
			});
		}
	}

	private ArrayList<String> createAutoCompletionList(CurrentWord currentWord) {
		ArrayList<String> tempList = new ArrayList<String>();
		if (symbolTable != null) {
			tempList.clear();
			
			Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
			symbolTable.getEntries(entryMap);
			Set<Entry<String, SymbolTableEntry>> entrySet = entryMap.entrySet();
			Iterator<Entry<String, SymbolTableEntry>> iter = entrySet.iterator();
			while (iter.hasNext()) {
				Entry<String, SymbolTableEntry> entry = iter.next();
				if (autoCompleteSymbolFilter == null || autoCompleteSymbolFilter.accept(entry.getValue())) {
					if (currentWord == null || currentWord.prefix.length() == 0 
							|| entry.getKey().toLowerCase().startsWith(currentWord.prefix.toLowerCase())) {
						tempList.add(entry.getKey());
					}
				}
			}

		} else {
			for (String w : autoCompWordList) {
				if (currentWord == null || currentWord.prefix.length() == 0 
						|| w.toLowerCase().startsWith(currentWord.prefix.toLowerCase())) {
					tempList.add(w);
				}
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
		
		if (symbolTable != null) {
			// add functions
			ArrayList<String> tempFuncList = new ArrayList<String>();
			for (String w : functList) {
				if (autoCompleteSymbolFilter == null || autoCompleteSymbolFilter.acceptFunction(w)) {
					if (currentWord == null || currentWord.prefix.length() == 0 
							|| w.toLowerCase().startsWith(currentWord.prefix.toLowerCase())) {
						tempFuncList.add(w + "()");
					}
				}
			}		
			tempList.addAll(tempFuncList);
		}
		return tempList;
	}
	
	private OperatingSystemInfo getOperatingSystemInfo() {
		if(osi == null) {
			osi = OperatingSystemInfo.getInstance();
		}
		return osi;
	}
	
	// test main
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			final TextFieldAutoCompletion tf = new TextFieldAutoCompletion();
			tf.setText("((x >  - 2.25) && (((((( - 1.5 + sqrt((2.25 + x))) ^ 2.0) + (0.25 * (y ^ 2.0)) + (z ^ 2.0)) ^ 2.0) - pow(((( - 1.5 + sqrt((2.25 + x))) ^ 2.0) + (0.25 * (y ^ 2.0)) + (z ^ 2.0)),1.5) - (0.8 * z * ((( - 1.5 + sqrt((2.25 + x))) ^ 2.0) + (0.25 * (y ^ 2.0)) + (z ^ 2.0))) + (0.7 * pow(z,3.0))) <= 0.0))");
//			tf.setColumns(100);
			Set<String> al = new HashSet<String>();
	//		for (String s : RegistrationPanel.COUNTRY_LIST) {
	//			al.add(s);
	//		}
			al.add("t");
			al.add("ab");
			al.add("aaaaaaa");
			tf.setAutoCompletionWords(al);
			
			JFrame frame = new JFrame();
			frame.setTitle("AutoCompleteTextField");
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(tf, BorderLayout.CENTER);
			frame.getContentPane().add(panel);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			BeanUtils.centerOnScreen(frame);
			frame.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public void stopEditing() {
		if (getSelectedIndex() >= 0) {
			onComplete();
		} else {
			autoCompJPopupMenu.setVisible(false);
		}
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public final void setAutoCompleteSymbolFilter(AutoCompleteSymbolFilter filter) {
		this.autoCompleteSymbolFilter = filter;
	}
	
	public int getSelectedIndex() {
		if (autoCompJPopupMenu.isVisible()) {
			return autoCompJList.getSelectedIndex();
		}
		return -1;
	}
	
	public boolean isPopupVisible() {
		return autoCompJPopupMenu.isVisible();
	}
	
	private void highlightParenthesis(final Graphics g){
		FontMetrics fm = g.getFontMetrics();
		final int charWidth = fm.getWidths()['('];
		
		String text = getText();		
		int txtLen = text.length();
		if (txtLen == 0) {
			return;
		}
			
		boolean bRight = false;
		int pos1 = -1;
		int caretPos = getCaretPosition();
		if (caretPos <= 0) {
			return;
		}
		pos1 = caretPos -1;
		pos1 = Math.max(0, Math.min(pos1, txtLen - 1));
		char currChar = text.charAt(pos1);
		if (currChar == ')') {
			bRight = true;			
		} else if (currChar == '(') {
			bRight = false;			
		} else {
			return;
		}
		int pos2 = -1;
		int count = 1;
		if (bRight) {
			for (pos2 = pos1 -1; pos2 >= 0; pos2 --) {
				if (text.charAt(pos2) == ')') {
					count ++;
				}
				if (text.charAt(pos2) == '(') {
					count --;
					if (count == 0) {
						break;
					}
				}
			}
		} else {
			for (pos2 = pos1 + 1; pos2 < txtLen; pos2 ++) {
				if (text.charAt(pos2) == '(') {
					count ++;
				}
				if (text.charAt(pos2) == ')') {
					count --;
					if (count == 0) {
						break;
					}
				}
			}
		}
		if (pos2 == -1 || pos2 >= txtLen || pos1 == pos2) {
			return;
		}
		
		try {
			highlightRectangle = modelToView(pos2);
			Color c = g.getColor();
			g.setColor(new Color(0xff, 0x99, 0x33));
			g.drawRect(highlightRectangle.x, highlightRectangle.y, charWidth, highlightRectangle.height);
			g.setColor(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (highlightRectangle != null) {
			FontMetrics fm = g.getFontMetrics();
			int charWidth = fm.getWidths()['('];
			Color c = g.getColor();
			g.setColor(getBackground());
			g.drawRect(highlightRectangle.x, highlightRectangle.y, charWidth, highlightRectangle.height);
			g.setColor(c);
		}
		super.paintComponent(g);
		if (hasFocus()) {			
			highlightParenthesis(g);
		}
	}

	@Override
	public void setText(String t) {
		bSetText = true;
		try {
			super.setText(t);
		} finally {
			bSetText = false;
		}
	}

	@Override
	protected void fireActionPerformed() {
		if (isPopupVisible()) {
			if (autoCompJList.getSelectedIndex() >= 0) {
				return;
			}

			autoCompJPopupMenu.setVisible(false);
		}
		super.fireActionPerformed();
	}	
}
