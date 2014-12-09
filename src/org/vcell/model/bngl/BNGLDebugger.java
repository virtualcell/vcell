package org.vcell.model.bngl;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import org.vcell.model.rbm.RbmUtils;

import cbit.vcell.xml.ExternalDocInfo;

import java.io.*;

public class BNGLDebugger extends JFrame {
	
	private JFrame frame;
	private static BNGLDebugger instance = null;
	
	private JTextArea	lineNumberArea;
	private JTextArea	bnglTextArea;
	private JTextArea	exceptionTextArea;
	
	private ExternalDocInfo docInfo = null;
	private String bnglText = "";
	private String exceptionText = "";

	private JFileChooser fileChooser = new JFileChooser();
    
	// create actions for menu items, buttons
	private Action openAction = new OpenAction();
	private Action saveAction = new SaveAction();
	private Action exitAction = new ExitAction();
    
	private BNGLDebugger() {
		initialize();
	}

	public static final BNGLDebugger getInstance() {
		if(instance == null) {
			instance = new BNGLDebugger();
		}
		return instance;
	}
	public void setInfo(ExternalDocInfo info) {
		this.docInfo = info;
		Reader fr = null;
		try {
			fr = docInfo.getReader();
			BufferedReader br = new BufferedReader(fr); 
			String s;
			bnglText = "";		// the text of the bngl file
			while((s = br.readLine()) != null) {
				bnglText += s + "\n";
			}
			fr.close();
			bnglTextArea.setText(bnglText);

			RbmUtils.reactionRuleLabelIndex = 0;
			RbmUtils.reactionRuleNames.clear();
			Reader reader = docInfo.getReader();
			RbmUtils.importBnglFile(reader);
		} catch (ParseException e) {
			Token exceptionToken = e.currentToken;
			exceptionText = e.getMessage();
			exceptionTextArea.setText(exceptionText);
			
			int lineNumber = 1;
			int startIndex;
			try {
				if(exceptionToken != null) {
					lineNumber = exceptionToken.beginLine;
				}
				startIndex = lineNumberArea.getLineStartOffset(lineNumber-1);
				int endIndex = lineNumberArea.getLineEndOffset(lineNumber-1);

				Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
				lineNumberArea.getHighlighter().addHighlight(startIndex, endIndex, painter);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			frame.setVisible(true);
		}
	}
	
	public void initialize(){
		// -------------------------------------------------- bngl panel
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
				int caretPosition = bnglTextArea.getDocument().getLength();
				Element root = bnglTextArea.getDocument().getDefaultRootElement();
				String nr = "1" + " " + System.getProperty("line.separator");
				for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
					nr += i + " " + System.getProperty("line.separator");
				}
				return nr;
			}
			@Override
			public void changedUpdate(DocumentEvent de) {
				lineNumberArea.setText(getNr());
			}
			@Override
			public void insertUpdate(DocumentEvent de) {
				lineNumberArea.setText(getNr());
			}
 			@Override
			public void removeUpdate(DocumentEvent de) {
				lineNumberArea.setText(getNr());
			}
		});
		bnglPanel.getViewport().add(bnglTextArea);
		bnglPanel.setRowHeaderView(lineNumberArea);
		bnglPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.add(bnglPanel, BorderLayout.CENTER);
		// ---------------------------------------------------- exception panel
		JScrollPane exceptionPanel = new JScrollPane();
		exceptionTextArea = new JTextArea();
		exceptionTextArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		exceptionTextArea.setFont(new Font("monospaced", Font.PLAIN, 14));

		exceptionPanel.getViewport().add(exceptionTextArea);
		exceptionPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		lowerPanel.add(exceptionPanel, BorderLayout.CENTER);
		// ---------------------------------------------------- all together now  :)
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(350);
		splitPane.setResizeWeight(0.9);
		splitPane.setTopComponent(upperPanel);
		splitPane.setBottomComponent(lowerPanel);

		//... Create menubar
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		fileMenu.setMnemonic('F');
		fileMenu.add(openAction);       // Note use of actions, not text.
		fileMenu.add(saveAction);
		fileMenu.addSeparator(); 
		fileMenu.add(exitAction);

		frame = new JFrame();

		frame.add(splitPane);
		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.setName("BnglDebugger");
		frame.setSize(500,500);
		
		bnglTextArea.setText(bnglText);
		frame.setVisible(true);
	}

    
    class OpenAction extends AbstractAction {
        public OpenAction() {
            super("Open...");
            putValue(MNEMONIC_KEY, new Integer('O'));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            int retval = fileChooser.showOpenDialog(BNGLDebugger.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    FileReader reader = new FileReader(f);
                    bnglTextArea.read(reader, "");  // Use TextComponent read
                } catch (IOException ioex) {
                    System.out.println(e);
                    System.exit(1);
                }
            }
        }
    }
    
    class SaveAction extends AbstractAction {
        SaveAction() {
            super("Save...");
            putValue(MNEMONIC_KEY, new Integer('S'));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            int retval = fileChooser.showSaveDialog(BNGLDebugger.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    FileWriter writer = new FileWriter(f);
                    bnglTextArea.write(writer);  // Use TextComponent write
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(BNGLDebugger.this, ioex);
                    System.exit(1);
                }
            }
        }
    }
    
    class ExitAction extends AbstractAction {
        public ExitAction() {
            super("Exit");
            putValue(MNEMONIC_KEY, new Integer('X'));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        new BNGLDebugger();
    }

}
