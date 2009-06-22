package cbit.gui;

import java.awt.Color;
import java.awt.Event;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.vcell.util.BeanUtils;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableEntryFilter;

public class TextFieldAutoCompletion extends JTextField {
	private JPopupMenu autoCompJPopupMenu = null;
	private JList autoCompJList = null;
	private DefaultListModel listModel = null;
	private ArrayList<String> autoCompWordList = null;
	private static final String COMMIT_ACTION = "commit";
	private static final String GOUPLIST_ACTION = "gouplist";
	private static final String GODOWNLIST_ACTION = "godownlist";
	private static final String SHOWLIST_ACTION = "showlist";
	private static final String DISMISSLIST_ACTION = "dimisslist";
	private SymbolTable symbolTable = null;
	
	private boolean bInCompleteTask = false;
	private SymbolTableEntryFilter symbolTableEntryFilter = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
		
	private class InternalEventHandler implements DocumentListener, MouseListener, KeyListener {
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
				showPopupChoices(null);
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
				if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {		
					showPopupChoices(null);
				} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
					if (autoCompJPopupMenu.isVisible()) {
						autoCompJPopupMenu.requestFocus();
						autoCompJList.setSelectionBackground(getSelectionColor());
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					stopEditing();
				}
			}
		}

		public void keyTyped(KeyEvent e) {
		}
	}
	public TextFieldAutoCompletion() {
		super();
		getDocument().addDocumentListener(eventHandler);
		addKeyListener(eventHandler);
		addMouseListener(eventHandler);
		
		autoCompWordList = new ArrayList<String>();		
		listModel = new DefaultListModel();
		autoCompJList = new JList(listModel);
		autoCompJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		autoCompJList.addMouseListener(eventHandler);

		autoCompJPopupMenu = new JPopupMenu();
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(autoCompJList);
		autoCompJPopupMenu.add(sp);
		
		setupActions();
	}
		
	public void setAutoCompletionWords(Set<String> words) {
		autoCompWordList.clear();
		autoCompWordList.addAll(words);
	}
		
	private class GoToList extends AbstractAction {
		int increment = 0;
		public GoToList(int incr) {
			increment = incr;
		}
		public void actionPerformed(ActionEvent ev) {
			if (increment != 0) {				
				int totalLen = listModel.getSize();
				if (totalLen == 0) {
					return;
				}
				int si = (autoCompJList.getSelectedIndex() + increment + totalLen) % totalLen; 
				autoCompJList.setSelectedIndex(Math.max(0, si));
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
			autoCompJPopupMenu.setVisible(false);
			requestFocus();
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
			match = match.trim();
			CurrentWord currentWord = findCurrentWord(null);			
				
			Document doc = getDocument();				
			doc.remove(currentWord.startPos, currentWord.endPos - currentWord.startPos + 1);
			
			doc.insertString(currentWord.startPos, match, null);				
			setCaretPosition(currentWord.startPos + match.length());	
			autoCompJPopupMenu.setVisible(false);
			requestFocus();
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
		
		im = getInputMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), GOUPLIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), GODOWNLIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, Event.CTRL_MASK), SHOWLIST_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), DISMISSLIST_ACTION);	
		
		am = getActionMap();
		am.put(GOUPLIST_ACTION, new GoToList(-1));
		am.put(GODOWNLIST_ACTION, new GoToList(1));
		am.put(SHOWLIST_ACTION, new ShowList());
		am.put(DISMISSLIST_ACTION, new DismissList());
	}
	
	private static class CurrentWord {
		int startPos = 0;
		String prefix = null;
		int endPos = 0;
	}
	
	private CurrentWord findCurrentWord(DocumentEvent docEvt) {
		CurrentWord currentWord = null;
		String text = getText();		
		int txtLen = text.length();
		if (txtLen == 0) {
			return currentWord;
		}
		
		int pos = getCaretPosition();
		if (docEvt != null) {
			if (docEvt.getType() == EventType.INSERT) {
				pos += 1;
			} else if (docEvt.getType() == EventType.REMOVE){
				pos -= 1;
			}
		}
		if (pos <= 0) {
			return currentWord;
		}
		pos = Math.min(pos, text.length());
		
		currentWord = new CurrentWord();

		// Find where the word starts
		int w;
		for (w = pos-1; w >= 0; w--) {
			char c = text.charAt(w);
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
		try {
			if (bInCompleteTask) {
				return;
			}
			if (symbolTable == null && (autoCompWordList == null || autoCompWordList.size() == 0)) {
				autoCompJPopupMenu.setVisible(false);
				return;
			}
			 
	        CurrentWord currentWord = findCurrentWord(docEvt);
	        
	        ArrayList<String> tempList = new ArrayList<String>();
			if (symbolTable != null) {
				tempList.clear();
				
				Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
				symbolTable.getEntries(entryMap);
				Set<Entry<String, SymbolTableEntry>> entrySet = entryMap.entrySet();
				Iterator<Entry<String, SymbolTableEntry>> iter = entrySet.iterator();
				while (iter.hasNext()) {
					Entry<String, SymbolTableEntry> entry = iter.next();
					if (symbolTableEntryFilter == null || symbolTableEntryFilter.accept(entry.getValue())) {
						if (currentWord == null 
								|| currentWord.prefix.length() == 0 
								|| entry.getKey().toLowerCase().startsWith(currentWord.prefix.toLowerCase())) {
							tempList.add(entry.getKey());
						}
					}
				}

			} else {
				for (String w : autoCompWordList) {
					if (currentWord == null 
							|| currentWord.prefix.length() == 0 
							|| w.toLowerCase().startsWith(currentWord.prefix.toLowerCase())) {
						tempList.add(w);
					}
				}
			}
			
			Collections.sort(tempList, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return o1.compareToIgnoreCase(o2);
				}
			});
			
			listModel.removeAllElements();
			for (String w : tempList) {
				listModel.addElement(w + "          ");
			}
	       	   
	       	if (listModel.getSize() > 0) {
	       		autoCompJList.setVisibleRowCount(Math.min(8, Math.max(3, listModel.getSize())));
	       		autoCompJList.setSelectedIndex(0);
	       		autoCompJList.setSelectionBackground(Color.lightGray);				
				
	       		try {
	       			Rectangle loc = modelToView(getCaretPosition());
	       			autoCompJPopupMenu.show(this, loc.x, loc.y + getHeight() - getInsets().bottom);
	       		} catch (BadLocationException ex) {	       			
	       		}
	       	} else {
	       		autoCompJPopupMenu.setVisible(false);
	       	}		   
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		} finally {
			 requestFocus();
		}
	}
	
	// test main
	public static void main(String[] args) {
		final TextFieldAutoCompletion tf = new TextFieldAutoCompletion();
		tf.setColumns(100);
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
		JPanel panel = new JPanel();
		panel.add(tf);
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		BeanUtils.centerOnScreen(frame);
		frame.setVisible(true);
	}
	
	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public void stopEditing() {
		if (autoCompJPopupMenu.isVisible()) {
			onComplete();
		}		
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public final void setSymbolTableEntryFilter(SymbolTableEntryFilter symbolTableEntryFilter) {
		this.symbolTableEntryFilter = symbolTableEntryFilter;
	}
	
	public boolean isPopupVisible() {
		return autoCompJPopupMenu.isVisible();
	}
}
