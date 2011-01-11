package cbit.vcell.visit;

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


import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComboBox;
import javax.swing.JButton;

import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class VisitControlPanel extends JPanel {
	private JSlider slider = new JSlider();

	private Origin origin;
	private Extent extent;
	private JComboBox comboBox = new JComboBox();
	private PDEDataContext pdeDataContext;
	private JTextField textField;
	private DataIdentifier dataIdentifier;
	private VisitProcess visitProcess;
    private JLabel lblDataset = new JLabel("Database name:");
    private JTextArea jTextArea = new JTextArea();
    private String[] commandHistory = new String[2000]; //TODO: replace this with a more intelligent container
    private int commandIndex, maxCommandIndex= 0;
    private int numberOfOpenPlots=0;
    private String selectedVariable= null;

	/**
	 * Create the panel.
	 */
	public VisitControlPanel() {
		commandHistory[0]="";
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0};
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
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblSelectVariable = new JLabel("Select variable");
		GridBagConstraints gbc_lblSelectVariable = new GridBagConstraints();
		gbc_lblSelectVariable.insets = new Insets(0, 0, 5, 0);
		gbc_lblSelectVariable.gridx = 0;
		gbc_lblSelectVariable.gridy = 1;
		panel.add(lblSelectVariable, gbc_lblSelectVariable);
		
		
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 2;
		/*comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataIdentifier dataIdentifier = null;
				String varName = (String) comboBox.getSelectedItem();
				for (int i = 0; i < pdeDataContext.getDataIdentifiers().length; i++) {
					if (pdeDataContext.getDataIdentifiers()[i].getName().equals(varName)) {
						dataIdentifier=pdeDataContext.getDataIdentifiers()[i];
						break;
					}
				}
				init(dataIdentifier, visitProcess);
			}
		});*/
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedVariable=(String)comboBox.getSelectedItem();
			}
		});
		
		panel.add(comboBox, gbc_comboBox);
		
		JButton btnClearPlots = new JButton("Clear Plots");
		btnClearPlots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Trying to delete all plots");
				VisitProcess.visitCommand(VisitPythonCommand.DeleteAllPlots());	
				numberOfOpenPlots=0;
				}
		});
		GridBagConstraints gbc_btnClearPlots = new GridBagConstraints();
		gbc_btnClearPlots.insets = new Insets(0, 0, 5, 0);
		gbc_btnClearPlots.gridx = 0;
		gbc_btnClearPlots.gridy = 3;
		panel.add(btnClearPlots, gbc_btnClearPlots);
		
		JButton btnAddPlot = new JButton("Add Pseudocolor Plot");
		btnAddPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (selectedVariable != null) {
					numberOfOpenPlots++;
					VisitProcess.visitCommand(VisitPythonCommand.AddPlot("Pseudocolor", selectedVariable));
					VisitProcess.visitCommand(VisitPythonCommand.SetActivePlots(String.valueOf(numberOfOpenPlots-1)));
					VisitProcess.visitCommand(VisitPythonCommand.AddOperator("Transform"));
					VisitProcess.visitCommand(VisitPythonCommand.makeTransformAttributes("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1)));
					VisitProcess.visitCommand("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1)+".doTranslate=1");
					System.out.println("I have"+String.valueOf(numberOfOpenPlots)+" open");
					switch (numberOfOpenPlots) {
						case 1: {break;
						}
						case 2: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(extent.getX()));
							break;
						}
						case 3: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(2*extent.getX()));
							break;
						}	
						case 4: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(3*extent.getX()));
							break;
						}	
						case 5: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateY="+String.valueOf(extent.getY()));
							break;
						}
						case 6: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(extent.getX()));
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateY="+String.valueOf(extent.getY()));
							break;
						}
						case 7: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(2*extent.getX()));
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateY="+String.valueOf(extent.getY()));
							break;
						}	
						case 8: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(3*extent.getX()));
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateY="+String.valueOf(extent.getY()));
							break;
						}	
						case 9: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateY="+String.valueOf(2*extent.getY()));
							break;
						}
						case 10: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(extent.getX()));
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateY="+String.valueOf(2*extent.getY()));
							break;
						}
						case 11: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(2*extent.getX()));
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateY="+String.valueOf(2*extent.getY()));
							break;
						}	
						case 12: {
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateX="+String.valueOf(3*extent.getX()));
							VisitProcess.visitCommand(("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1))+".translateY="+String.valueOf(2*extent.getY()));
							break;
						}	
						default: {}
					}
					VisitProcess.visitCommand(VisitPythonCommand.SetOperatorOptions("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1)));
					VisitProcess.visitCommand(VisitPythonCommand.DrawPlots());
					
				}
			}
		});
		GridBagConstraints gbc_btnAddPlot = new GridBagConstraints();
		gbc_btnAddPlot.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddPlot.gridx = 0;
		gbc_btnAddPlot.gridy = 4;
		panel.add(btnAddPlot, gbc_btnAddPlot);
		
		JButton btnDrawPlot = new JButton("Draw Plots");
		GridBagConstraints gbc_btnDrawPlot = new GridBagConstraints();
		gbc_btnDrawPlot.insets = new Insets(0, 0, 5, 0);
		gbc_btnDrawPlot.gridx = 0;
		gbc_btnDrawPlot.gridy = 5;
		panel.add(btnDrawPlot, gbc_btnDrawPlot);
		
		JButton btnShowVisitGui = new JButton("Show VisIt GUI");
		btnShowVisitGui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Trying to open VisIt GUI");
				VisitProcess.visitCommand(VisitPythonCommand.OpenGUI());  
			}
		});
		GridBagConstraints gbc_btnShowVisitGui = new GridBagConstraints();
		gbc_btnShowVisitGui.insets = new Insets(0, 0, 5, 0);
		gbc_btnShowVisitGui.gridx = 0;
		gbc_btnShowVisitGui.gridy = 6;
		panel.add(btnShowVisitGui, gbc_btnShowVisitGui);
		
		JButton btnTestGetglobalattributes = new JButton("Test - GetGlobalAttributes");
		btnTestGetglobalattributes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				VisitProcess.visitCommand(VisitPythonCommand.GetGlobalAttributes());
			}
		});
		GridBagConstraints gbc_btnTestGetglobalattributes = new GridBagConstraints();
		gbc_btnTestGetglobalattributes.gridx = 0;
		gbc_btnTestGetglobalattributes.gridy = 7;
		panel.add(btnTestGetglobalattributes, gbc_btnTestGetglobalattributes);
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (!slider.getValueIsAdjusting()) {
					System.out.println(arg0);
					VisitProcess.visitCommand(VisitPythonCommand.SetTimeSliderState(slider.getValue()));
					
				}
			}
		});
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 2;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JButton button = new JButton("New button");
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 0, 5);
		gbc_button.gridx = 0;
		gbc_button.gridy = 0;
		panel_2.add(button, gbc_button);
		
		JButton button_1 = new JButton("New button");
		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.gridx = 1;
		gbc_button_1.gridy = 0;
		panel_2.add(button_1, gbc_button_1);
		slider.setMajorTickSpacing(10);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.weighty = 1.0;
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.insets = new Insets(0, 0, 5, 0);
		gbc_slider.gridx = 0;
		gbc_slider.gridy = 3;
		add(slider, gbc_slider);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.weighty = 1.0;
		gbc_panel_1.weightx = 1.0;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 4;
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


	public void setPdeDataContext(PDEDataContext pdeDataContext, Origin origin, Extent extent) {
		this.origin = origin;
		this.extent = extent;
		
		this.pdeDataContext = pdeDataContext;
		comboBox.removeAllItems();
		for (int i = 0; i < pdeDataContext.getDataIdentifiers().length; i++) {
			comboBox.addItem(pdeDataContext.getDataIdentifiers()[i].getName());
		}
		
		slider.setMinimum(0);
		slider.setMaximum(pdeDataContext.getTimePoints().length-1);
		
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(10);
	}
}
