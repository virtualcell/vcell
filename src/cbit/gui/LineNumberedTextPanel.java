package cbit.gui;
/**
 * Insert the type's description here.
 * Creation date: (10/9/2006 1:17:05 PM)
 * @author: Fei Gao
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.Highlighter.Highlight;
import cbit.vcell.math.VCML;

public class LineNumberedTextPanel extends JPanel implements DocumentListener, ActionListener {
    private JTextArea textArea = null;
    private JScrollPane scrollPane = null;
    private LineNumberPanel numberPanel = null;
    
    private JButton searchPrevButton = null;
    private JButton searchNextButton = null;
    
    private int searchStartOffset = -1;
    private int searchEndOffset = -1;
    
    private static final String COMMIT_ACTION = "commit";
    private static enum Mode { INSERT, COMPLETION };
    private List<String> words = null;
    private Mode mode = Mode.INSERT;
    
    private final static String CANCEL_ACTION = "cancel-search";
	private Color searchTextFieldBackground = null;
	private Highlighter hilit =  null;
    private Highlighter.HighlightPainter painter = null;
	private JTextField searchTextField = null;   

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
        
        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }
        
        public void run() {
            getTextArea().insert(completion, position);
            getTextArea().setCaretPosition(position + completion.length());
            getTextArea().moveCaretPosition(position);
            mode = Mode.COMPLETION;
        }
    }
    
    private class CommitAction extends AbstractAction {
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                int pos = textArea.getSelectionEnd();
                getTextArea().insert(" ", pos);
                getTextArea().setCaretPosition(pos + 1);
                mode = Mode.INSERT;
            } else {
            	getTextArea().replaceSelection("\n");
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
    int start = getTextArea().viewToModel(scrollPane.getViewport().getViewPosition()); // starting pos in document 
    int end = getTextArea().viewToModel(new Point(
                scrollPane.getViewport().getViewPosition().x + getTextArea().getWidth(),
                scrollPane.getViewport().getViewPosition().y + getTextArea().getHeight()));
    // end pos in doc 

    // translate offsets to lines 
    Document doc = getTextArea().getDocument();
    int startline = doc.getDefaultRootElement().getElementIndex(start) + 1;
    int endline = doc.getDefaultRootElement().getElementIndex(end) + 1;

    java.awt.FontMetrics fm = g.getFontMetrics(getTextArea().getFont());
    int fontHeight = fm.getHeight();
    int fontDesc = fm.getDescent();
    int starting_y = -1;

    try {
        starting_y =  getTextArea().modelToView(start).y - scrollPane.getViewport().getViewPosition().y + fontHeight - fontDesc;
    } catch (javax.swing.text.BadLocationException ex) {
        ex.printStackTrace();
    }

    g.setFont(getTextArea().getFont());
    for (int line = startline, y = starting_y;  line <= endline;  y += fontHeight, line++) {
	    String linestr = Integer.toString(line);
	    int width = 0;
	    for (int i = 0; i < linestr.length(); i ++) {
		    width += fm.charWidth(linestr.charAt(i));
	    }
        g.drawString(linestr, preferred_size - width - 2, y);
    }

}
}
    

public LineNumberedTextPanel() {
    super(); 

    numberPanel = new LineNumberPanel();
    scrollPane = new JScrollPane(getTextArea());
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
	
	fillKeywords();	
}

public LineNumberedTextPanel(boolean editable) {
	this();
	getTextArea().setEditable(editable);
}

private JTextField getSearchTextField() {
	if (searchTextField  == null) {
		try {
			searchTextField = new JTextField();
			searchTextField.setColumns(15);

	        searchTextFieldBackground = searchTextField.getBackground();
	        searchTextField.getDocument().addDocumentListener(this);
	        
	        InputMap im = searchTextField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	        ActionMap am = searchTextField.getActionMap();
	        im.put(KeyStroke.getKeyStroke("ESCAPE"), CANCEL_ACTION);
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
			searchPrevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow_up.jpg")));
			searchPrevButton.setBorder(BorderFactory.createRaisedBevelBorder());
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
			searchNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow_down.jpg")));
			searchNextButton.setBorder(BorderFactory.createRaisedBevelBorder());
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
 * Insert the method's description here.
 * Creation date: (10/10/2006 2:19:21 PM)
 * @param line int
 */
public int getCaretPosition() {
	return getTextArea().getCaretPosition();
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2006 12:04:52 PM)
 * @return javax.swing.text.Document
 */
public Document getDocument() {
	return getTextArea().getDocument();
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2006 2:17:29 PM)
 * @return int
 */
public int getLineEndOffset(int line) throws javax.swing.text.BadLocationException {
	return getTextArea().getLineStartOffset(line);
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2006 2:17:29 PM)
 * @return int
 */
public int getLineStartOffset(int line) throws javax.swing.text.BadLocationException {
	return getTextArea().getLineStartOffset(line);
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2006 1:52:17 PM)
 * @return java.lang.String
 */
public String getText() {
	return getTextArea().getText();
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2006 12:04:52 PM)
 * @return javax.swing.text.Document
 */
public JTextArea getTextArea() {
	if (textArea == null) {
		textArea = new JTextArea() 
		// we need to override paint so that the linenumbers stay in sync 
	    {
	        public void paint(Graphics g) {
	            super.paint(g);
	            numberPanel.repaint();
	        }
	    };
	    textArea.setHighlighter(getHighlighter());
	    textArea.getDocument().addDocumentListener(this);
	    
	    InputMap im = textArea.getInputMap();
	    ActionMap am = textArea.getActionMap();
	    im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
	    am.put(COMMIT_ACTION, new CommitAction());
	}
	return textArea;
}


    // test main 
    public static void main(String[] args) {
        JFrame frame = new JFrame();        
        final LineNumberedTextPanel nr = new LineNumberedTextPanel();
        frame.getContentPane().add(nr);
        frame.pack();
        frame.setSize(new Dimension(400, 400));
        frame.setVisible(true);
    }


/**
 * Insert the method's description here.
 * Creation date: (10/10/2006 2:19:21 PM)
 * @param line int
 */
public void select(int startline, int endline) {
	textArea.select(startline, endline);
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2006 2:19:21 PM)
 * @param line int
 */
public void setCaretPosition(int position) {
	textArea.setCaretPosition(position);
}

public void scrollToShow(int position) {
	try {
		setCaretPosition(position);
		Rectangle r = textArea.modelToView(position);
		if (r != null) {
			Rectangle vr = scrollPane.getViewport().getViewRect();
			if (!vr.contains(r)) {
				scrollPane.getViewport().scrollRectToVisible(r);
			}
		}
	} catch (BadLocationException e) {
		e.printStackTrace();
	}
}

public void moveCaretPosition(int position) {
	textArea.moveCaretPosition(position);
}

/**
 * Insert the method's description here.
 * Creation date: (10/11/2006 12:04:52 PM)
 * @return javax.swing.text.Document
 */
public void setDocument(Document doc) {
	if (getTextArea().getDocument() != null) {
		getTextArea().getDocument().removeDocumentListener(this);
	}
	getTextArea().setDocument(doc);
	if (getTextArea().getDocument() != null) {
		getTextArea().getDocument().addDocumentListener(this);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2006 1:53:03 PM)
 * @param text java.lang.String
 */
public void setText(String text) {
	textArea.setText(text);
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2006 10:16:15 AM)
 * @param f java.awt.Font
 */
public void setTextFont(java.awt.Font f) {
	textArea.setFont(f);
}

public void insertUpdate(DocumentEvent ev) {
	if (ev.getDocument() == getSearchTextField().getDocument()) {
		search();
		return;
	}
	
	clearSearchText();
	
    if (ev.getLength() != 1) {
        return;
    }
    
    int pos = ev.getOffset();
    String content = null;
    try {
        content = textArea.getText(0, pos + 1);
    } catch (BadLocationException e) {
        e.printStackTrace();
    }
    
    // Find where the word starts
    int w;
    for (w = pos; w >= 0; w--) {
        if (! Character.isLetter(content.charAt(w))) {
            break;
        }
    }
    if (pos - w < 2) {
        // Too few chars
        return;
    }
    
    String prefix = content.substring(w + 1);
    int n = Collections.binarySearch(words, prefix);
    if (n < 0 && -n <= words.size()) {
        String match = words.get(-n - 1);
        if (match.startsWith(prefix)) {
            // A completion is found
            String completion = match.substring(pos - w);
            // We cannot modify Document from within notification,
            // so we submit a task that does the change later
            SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
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
    if (index >= 0) {   // match found
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
	int searchPointer = 0;
	if (bNext) {
		searchPointer = 0;
		int oldSearchStartOffset = searchStartOffset;
		while (true) {
			searchStartOffset = highlights[searchPointer].getStartOffset(); 
			if (searchStartOffset > currentPosition || currentPosition == searchStartOffset && oldSearchStartOffset == -1) {
				break;
			}			
			if (searchPointer >= highlights.length - 1) {
				currentPosition = 0;
				searchPointer = 0;
			} else {
				searchPointer ++;
			}
		}
	} else {
		searchPointer = highlights.length - 1;
		while (true) {			
			searchStartOffset = highlights[searchPointer].getStartOffset();
			if (searchStartOffset < currentPosition) {
				break;
			}		
			if (searchPointer <= 0) {
				currentPosition = getText().length();
				searchPointer = highlights.length - 1;
			} else {
				searchPointer --;
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
	
    public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
    	currentColor = super.getColor();
    	if (searchStartOffset >= offs0 && searchEndOffset <= offs1) {
    		currentColor = selectColor;
    	}
    	super.paint(g, offs0, offs1, bounds, c); 		
    }
    
	public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
		currentColor = super.getColor();
		if (searchStartOffset >= offs0 && searchEndOffset <= offs1) {
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

public void fillKeywords() {
	   // must be ordered
    words = new ArrayList<String>();
    words.add(VCML.BoundaryXm);
    words.add(VCML.CompartmentSubDomain);
    words.add(VCML.Constant);
    words.add(VCML.Diffusion);
    words.add(VCML.Function);
    words.add(VCML.InFlux);
    words.add(VCML.Initial);
    words.add(getTemplate_JumpCondition());
    words.add(VCML.JumpProcess);
    words.add(VCML.MathDescription);
    words.add(VCML.MembraneSubDomain);
    words.add(VCML.MembraneVariable); 
    words.add(getTemplate_OdeEquation());
    words.add(VCML.OutFlux);
    words.add(getTemplate_PdeEquation()); 
    words.add(VCML.Priority);
    words.add(VCML.Rate);
    words.add(VCML.Value);
    words.add(VCML.VelocityX);
    words.add(VCML.VolumeVariable);
    
    String functions[] = {
    		"abs()",
    		"acos()",
    		"acosh()",
    		"acot()",
    		"acoth()",
    		"acsc()",
    		"acsch()",
    		"asec()",
    		"asech()",
    		"asin()",
    		"asinh()",
    		"atan()",
    		"atan2(,)",
    		"atanh()",
    		"ceil()",
    		"cos()",
    		"cosh()",
    		"cot()",
    		"coth()",
    		"csc()",
    		"csch()",
    		"exp()",
    		"factorial()",
    		"field(,,,,)",
    		"floor()",
    		"grad(,)",
    		"log()",
    		"log10()",
    		"logbase(,)",
    		"max(,)",
    		"min(,)",
    		"pow(,)",
    		"sec()",
    		"sech()",
    		"sin()",
    		"sinh()",
    		"sqrt()",
    		"tan()",
    		"tanh()",
    };
    for (String f : functions) {
    	words.add(f);
    }
}
public String getTemplate_OdeEquation() {	
	return VCML.OdeEquation + " varName " + VCML.BeginBlock + "\n" 
		+ "\t\t" + VCML.Rate + " 0.0;\n" 
		+ "\t\t" + VCML.Initial + "0.0;\n"
		+ "\t}\n";
}

public String getTemplate_PdeEquation() {	
	return VCML.PdeEquation + " varName " + VCML.BeginBlock + "\n"
		+ "\t\t" + VCML.BoundaryXm + " 0.0;\n"
		+ "\t\t" + VCML.BoundaryXp + " 0.0;\n"
		+ "\t\t" + VCML.Rate + " 0.0;\n" 
		+ "\t\t" + VCML.Diffusion + " 0.0;\n" 
		+ "\t\t" + VCML.Initial + " 0.0;\n"
		+ "\t}\n";
}
public String getTemplate_JumpCondition() {	
	return VCML.JumpCondition + " varName " + VCML.BeginBlock + "\n" 
		+ "\t\t" + VCML.InFlux + " 0.0;\n" 
		+ "\t\t" + VCML.OutFlux + " 0.0;\n" 
		+ "\t}\n";
}
}