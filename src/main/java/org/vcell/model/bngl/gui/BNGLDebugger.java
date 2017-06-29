package org.vcell.model.bngl.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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

import org.vcell.model.bngl.ParseException;
import org.vcell.model.bngl.Token;
import org.vcell.model.rbm.RbmUtils;

import cbit.vcell.xml.ExternalDocInfo;

@Deprecated
public class BNGLDebugger extends JFrame implements KeyListener, ActionListener {
	
	private JFrame frame = new JFrame("Bngl Debugger");
	private static BNGLDebugger instance = null;
	
	private int lines = 0;
	private JTextArea	lineNumberArea;
	private JTextArea	bnglTextArea;
	private JTextArea	exceptionTextArea;
	
	private ExternalDocInfo docInfo = null;
    
	// create actions for menu items, buttons
//	private Action openAction = new OpenAction();
//	private Action saveAction = new SaveAction();
	private Action exitAction = new ExitAction();
	
	private Button buttonParse = new Button("Parse");
	private Button buttonSave = new Button("Save");
	private Button buttonExit = new Button("Exit");
    
	private BNGLDebugger() {
		initialize();
	}
@Deprecated
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
			String bnglText = "";		// the text of the bngl file
			while((s = br.readLine()) != null) {
				bnglText += s + "\n";
			}
			bnglTextArea.setText(bnglText);

			fr = docInfo.getReader();
			br = new BufferedReader(fr);
			parse1(br);
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			frame.setVisible(true);
		}
	}
@Deprecated	
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
//		fileMenu.setMnemonic('F');
//		fileMenu.add(openAction);       // Note use of actions, not text.
//		fileMenu.add(saveAction);
//		fileMenu.addSeparator(); 
		fileMenu.add(exitAction);

		JPanel buttonPane = new JPanel();
		buttonParse.addActionListener(this);
		buttonSave.addActionListener(this);
		buttonExit.addActionListener(this);
		buttonPane.add(buttonParse);
		buttonPane.add(buttonSave);
		buttonPane.add(buttonExit);
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        framePanel.add(splitPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        framePanel.add(buttonPane, gbc); 
		
		frame.add(framePanel);
		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.setName("BnglDebugger");
		frame.setSize(900,650);
		frame.setVisible(true);
	}

    
//    class OpenAction extends AbstractAction {
//        public OpenAction() {
//            super("Open...");
//            putValue(MNEMONIC_KEY, new Integer('O'));
//        }
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            int retval = fileChooser.showOpenDialog(BNGLDebugger.this);
//            if (retval == JFileChooser.APPROVE_OPTION) {
//                File f = fileChooser.getSelectedFile();
//                try {
//                    FileReader reader = new FileReader(f);
//                    bnglTextArea.read(reader, "");  // Use TextComponent read
//                } catch (IOException ioex) {
//                    System.out.println(e);
//                    System.exit(1);
//                }
//            }
//        }
//    }
//    
//    class SaveAction extends AbstractAction {
//        SaveAction() {
//            super("Save...");
//            putValue(MNEMONIC_KEY, new Integer('S'));
//        }
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            int retval = fileChooser.showSaveDialog(BNGLDebugger.this);
//            if (retval == JFileChooser.APPROVE_OPTION) {
//                File f = fileChooser.getSelectedFile();
//                try {
//                    FileWriter writer = new FileWriter(f);
//                    bnglTextArea.write(writer);  // Use TextComponent write
//                } catch (IOException ioex) {
//                    JOptionPane.showMessageDialog(BNGLDebugger.this, ioex);
//                    System.exit(1);
//                }
//            }
//        }
//    }
    
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

    // ----------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getSource() + ", " + e.getActionCommand());
		if(e.getSource() == buttonParse) {
			parse();
		} else if(e.getSource() == buttonSave) {
			save();
		} else if(e.getSource() == buttonExit) {
			frame.dispose();
		}

	}
	private void parse() {
		String str = bnglTextArea.getText();
		InputStream is = new ByteArrayInputStream(str.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		parse1(br);
	}
	private void parse1(BufferedReader br) {
		String exceptionText = "No errors detected. Please Save this file, Exit the debugger and Import again.";
		try {
		RbmUtils.importBnglFile(br);
		} catch (ParseException e) {
			Token exceptionToken = e.currentToken;
			exceptionText = e.getMessage();
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
		} finally {
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
	}
	private static boolean isNumeric(String str)
	{
	    return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
	private void save() {
		try {
			File file = docInfo.getFile();
            String str = bnglTextArea.getText();

            FileWriter fw = new FileWriter(file);
            fw.write(str);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	
// ========================================================================
//    public static void main(String[] args) {
//        new BNGLDebugger();
//    }
//
}
