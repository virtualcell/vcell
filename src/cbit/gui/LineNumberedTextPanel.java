package cbit.gui;


/**
 * Insert the type's description here.
 * Creation date: (10/9/2006 1:17:05 PM)
 * @author: Fei Gao
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

public class LineNumberedTextPanel extends JPanel {
    //JTextPane textPane = null;
    JTextArea textArea = null;
    JScrollPane scrollPane = null;
    LineNumberPanel numberPanel = null;

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
    int start = textArea.viewToModel(scrollPane.getViewport().getViewPosition()); // starting pos in document 
    int end = textArea.viewToModel(new Point(
                scrollPane.getViewport().getViewPosition().x + textArea.getWidth(),
                scrollPane.getViewport().getViewPosition().y + textArea.getHeight()));
    // end pos in doc 

    // translate offsets to lines 
    Document doc = textArea.getDocument();
    int startline = doc.getDefaultRootElement().getElementIndex(start) + 1;
    int endline = doc.getDefaultRootElement().getElementIndex(end) + 1;

    java.awt.FontMetrics fm = g.getFontMetrics(textArea.getFont());
    int fontHeight = fm.getHeight();
    int fontDesc = fm.getDescent();
    int starting_y = -1;

    try {
        starting_y =  textArea.modelToView(start).y - scrollPane.getViewport().getViewPosition().y + fontHeight - fontDesc;
    } catch (javax.swing.text.BadLocationException ex) {
        ex.printStackTrace();
    }

    g.setFont(textArea.getFont());
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
	textArea = new JTextArea() 
	// we need to override paint so that the linenumbers stay in sync 
    {
        public void paint(Graphics g) {
            super.paint(g);
            numberPanel.repaint();
        }
    };
    scrollPane = new JScrollPane(textArea);
    setLayout(new BorderLayout());
    add(numberPanel, BorderLayout.WEST);
 	add(scrollPane, BorderLayout.CENTER);      
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2006 2:19:21 PM)
 * @param line int
 */
public int getCaretPosition() {
	return textArea.getCaretPosition();
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2006 2:17:29 PM)
 * @return int
 */
public int getLineEndOffset(int line) throws javax.swing.text.BadLocationException {
	return textArea.getLineStartOffset(line);
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2006 2:17:29 PM)
 * @return int
 */
public int getLineStartOffset(int line) throws javax.swing.text.BadLocationException {
	return textArea.getLineStartOffset(line);
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2006 1:52:17 PM)
 * @return java.lang.String
 */
public String getText() {
	return textArea.getText();
}


    // test main 
    public static void main(String[] args) {
        JFrame frame = new JFrame();        
        //frame.getContentPane().setLayout(new BorderLayout());
        final LineNumberedTextPanel nr = new LineNumberedTextPanel();
        //frame.getContentPane().add(nr, BorderLayout.WEST);
        //frame.getContentPane().add(nr.scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(nr);
        frame.pack();
        frame.setSize(new Dimension(400, 400));
        frame.show();
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
}