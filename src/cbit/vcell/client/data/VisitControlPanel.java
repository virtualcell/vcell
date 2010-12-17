package cbit.vcell.client.data;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.vcell.util.VisitProcess;

import cbit.vcell.simdata.DataIdentifier;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;

public class VisitControlPanel extends JPanel {
	private JTextField textField;
	private DataIdentifier dataIdentifier;
	private VisitProcess visitProcess;
    private JLabel lblDataset = new JLabel("Database name:");
    private JTextArea jTextArea = new JTextArea();
    private String[] commandHistory = new String[2000]; //TODO: replace this with a more intelligent container
    private int commandIndex, maxCommandIndex= 0;
	/**
	 * Create the panel.
	 */
	public VisitControlPanel() {
		commandHistory[0]="";
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0};
		gridBagLayout.columnWeights = new double[]{1.0};
		setLayout(gridBagLayout);
		
		
		GridBagConstraints gbc_lblDataset = new GridBagConstraints();
		gbc_lblDataset.insets = new Insets(0, 0, 5, 0);
		gbc_lblDataset.weightx = 1.0;
		gbc_lblDataset.gridx = 0;
		gbc_lblDataset.gridy = 0;
		add(lblDataset, gbc_lblDataset);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.weighty = 1.0;
		gbc_panel_1.weightx = 1.0;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		panel_1.setLayout(gbl_panel_1);
		
		JScrollPane scrollPane = new JScrollPane(jTextArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.insets = new Insets(4, 4, 4, 4);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panel_1.add(scrollPane, gbc_scrollPane);
				
		textField = new JTextField();
		
		//listen for up-arrow key
		textField.addKeyListener(new KeyListener() {	
			public void keyPressed(KeyEvent k) {
				//if (k.getKeyCode()==KeyEvent.VK_UP) System.out.println("Observed key up pressed. Command Index was: "+ String.valueOf(commandIndex));
				if ((k.getKeyCode() == KeyEvent.VK_UP) & (commandIndex>0)){
					textField.setText(commandHistory[commandIndex-1]);
					commandIndex--;
				}
				if ((k.getKeyCode() == KeyEvent.VK_DOWN) & (commandIndex<maxCommandIndex)){
					textField.setText(commandHistory[commandIndex+1]);
					commandIndex++;
				}
			}
			public void keyReleased(KeyEvent k){}
			public void keyTyped(KeyEvent k){
				
			}
		});
		
		
		//listen for entered command
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println("Detected a command entered");
				
				if (!SwingUtilities.isEventDispatchThread()) {
			    	try {
						SwingUtilities.invokeAndWait(
								new Runnable() {
									public void run() {
										visitProcess.sendCommandAndNoteResponse(textField.getText()+"\n");
									}
								}
						);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }else{
			    	visitProcess.sendCommandAndNoteResponse(textField.getText()+"\n");
			    }
				
				
				commandHistory[commandIndex]=textField.getText();
				commandIndex++;
				if (commandIndex>maxCommandIndex) maxCommandIndex++;
				//System.out.println("commandIndex is now: "+ String.valueOf(commandIndex));
				textField.setText("");
			    
			}
		});
		
		
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(4, 4, 4, 4);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		panel_1.add(textField, gbc_textField);
		textField.setColumns(10);

	}

	
	public void init(DataIdentifier dataIdentifier, VisitProcess visitProcess){
		this.dataIdentifier = dataIdentifier;
		this.visitProcess = visitProcess;
		lblDataset.setText(dataIdentifier.getName());
		VisitProcess.setJTextArea(this.jTextArea);
	}
}
