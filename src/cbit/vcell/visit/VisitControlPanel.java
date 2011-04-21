package cbit.vcell.visit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import llnl.visit.Plot;

import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.visit.VisitSession.VisitSessionException;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class VisitControlPanel extends JPanel {
	private JSlider timeSliceSlider = new JSlider();
	private JSlider sliceSlider = new JSlider();

	private Origin origin;
	private Extent extent;
	private JComboBox comboBox = new JComboBox();
	private PDEDataContext pdeDataContext;
	private JTextField textField;
	private DataIdentifier dataIdentifier;
	//private VisitProcess visitProcess;
	private VisitSession visitSession;
    private JLabel lblDataset = new JLabel("Database name:");
    private JTextArea jTextArea = new JTextArea();
    private String[] commandHistory = new String[2000]; //TODO: replace this with a more intelligent container
    private int commandIndex, maxCommandIndex= 0;
    private int numberOfOpenPlots=0;
    private String selectedVariable= null;
    private boolean tempSliceSelected = false;
    private boolean sliceProjectTo2D = false;
    private int sliceAxisSelected = 2;
    
    private final ButtonGroup buttonGroup = new ButtonGroup();
    
    ClipPlaneOperatorControlPanel clipPlaneOperatorControlPanel = new ClipPlaneOperatorControlPanel();
    
	/**
	 * Create the panel.
	 */
	public VisitControlPanel() {
		commandHistory[0]="";
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{0, 0, 92, 0, 0, 0, 119};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0};
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
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblSelectVariable = new JLabel("Select variable to work with");
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
		
		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 3;
		panel.add(panel_4, gbc_panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel_4.rowHeights = new int[]{0, 0, 0};
		gbl_panel_4.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_4.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_4.setLayout(gbl_panel_4);
		
		JButton btnClearPlots = new JButton("Clear Plots");
		GridBagConstraints gbc_btnClearPlots = new GridBagConstraints();
		gbc_btnClearPlots.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnClearPlots.insets = new Insets(0, 0, 5, 5);
		gbc_btnClearPlots.gridx = 0;
		gbc_btnClearPlots.gridy = 0;
		panel_4.add(btnClearPlots, gbc_btnClearPlots);
		
		JButton btnAddPlot = new JButton("Add Pseudocolor Plot");
		GridBagConstraints gbc_btnAddPlot = new GridBagConstraints();
		gbc_btnAddPlot.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddPlot.gridx = 1;
		gbc_btnAddPlot.gridy = 0;
		panel_4.add(btnAddPlot, gbc_btnAddPlot);
		
		JButton btnDrawPlot = new JButton("Draw Plots");
		GridBagConstraints gbc_btnDrawPlot = new GridBagConstraints();
		gbc_btnDrawPlot.insets = new Insets(0, 0, 5, 5);
		gbc_btnDrawPlot.gridx = 2;
		gbc_btnDrawPlot.gridy = 0;
		panel_4.add(btnDrawPlot, gbc_btnDrawPlot);
		
		JButton btnShowVisitGui = new JButton("Show VisIt GUI");
		GridBagConstraints gbc_btnShowVisitGui = new GridBagConstraints();
		gbc_btnShowVisitGui.insets = new Insets(0, 0, 5, 5);
		gbc_btnShowVisitGui.gridx = 3;
		gbc_btnShowVisitGui.gridy = 0;
		panel_4.add(btnShowVisitGui, gbc_btnShowVisitGui);
		
		JButton button_1 = new JButton("Reset view");
		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.insets = new Insets(0, 0, 5, 0);
		gbc_button_1.gridx = 4;
		gbc_button_1.gridy = 0;
		panel_4.add(button_1, gbc_button_1);
		
		JButton btnSaveVisitSession = new JButton("Save VisIt Session");
		btnSaveVisitSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Please choose a location to save Session");
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				String selectedFile=null;
				try {
					chooser.setSelectedFile(File.createTempFile(visitSession.getCurrentLogFile().substring(0,visitSession.getCurrentLogFile().length() - 4), ".session"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				chooser.showSaveDialog(VisitControlPanel.this);
				if (chooser.getSelectedFile()==null) {
					DialogUtils.showErrorDialog(VisitControlPanel.this, "No filename was choosen");
					return;
				}
				try {
					selectedFile=chooser.getSelectedFile().getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				visitSession.saveSession(selectedFile);
				
			}
		});
		GridBagConstraints gbc_btnSaveVisitSession = new GridBagConstraints();
		gbc_btnSaveVisitSession.anchor = GridBagConstraints.WEST;
		gbc_btnSaveVisitSession.insets = new Insets(0, 0, 0, 5);
		gbc_btnSaveVisitSession.gridx = 0;
		gbc_btnSaveVisitSession.gridy = 1;
		panel_4.add(btnSaveVisitSession, gbc_btnSaveVisitSession);
		
		JButton btnRestoreVisitSession = new JButton("Restore VisIt Session");
		btnRestoreVisitSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Please choose saved Session file");
				String selectedFile=null;
				chooser.showOpenDialog(VisitControlPanel.this);
				if (chooser.getSelectedFile()==null) {
					DialogUtils.showErrorDialog(VisitControlPanel.this, "No filename was choosen");
					return;
				}
				try {
					selectedFile=chooser.getSelectedFile().getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				visitSession.restoreSession(selectedFile);
			}
		});
		GridBagConstraints gbc_btnRestoreVisitSession = new GridBagConstraints();
		gbc_btnRestoreVisitSession.anchor = GridBagConstraints.WEST;
		gbc_btnRestoreVisitSession.insets = new Insets(0, 0, 0, 5);
		gbc_btnRestoreVisitSession.gridx = 1;
		gbc_btnRestoreVisitSession.gridy = 1;
		panel_4.add(btnRestoreVisitSession, gbc_btnRestoreVisitSession);
		
		
		JButton btnMakeMovieButton = new JButton("Make Movie");
		btnMakeMovieButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visitSession.makeMovie();
				System.out.println("Returned from VisitSession.makeMovie");
			}
		});
		GridBagConstraints gbc_MakeMovieButton = new GridBagConstraints();
		gbc_MakeMovieButton.insets = new Insets(0, 0, 0, 5);
		gbc_MakeMovieButton.anchor = GridBagConstraints.WEST;
		gbc_MakeMovieButton.gridx = 2;
		gbc_MakeMovieButton.gridy = 1;
		panel_4.add(btnMakeMovieButton, gbc_MakeMovieButton);
		
		
		
		JLabel lblTimeStepSlider = new JLabel("Time step slider");
		GridBagConstraints gbc_lblTimeStepSlider = new GridBagConstraints();
		gbc_lblTimeStepSlider.insets = new Insets(0, 0, 5, 0);
		gbc_lblTimeStepSlider.gridx = 0;
		gbc_lblTimeStepSlider.gridy = 4;
		panel.add(lblTimeStepSlider, gbc_lblTimeStepSlider);
		GridBagConstraints gbc_timeSliceSlider = new GridBagConstraints();
		gbc_timeSliceSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeSliceSlider.insets = new Insets(0, 0, 5, 0);
		gbc_timeSliceSlider.gridx = 0;
		gbc_timeSliceSlider.gridy = 5;
		panel.add(timeSliceSlider, gbc_timeSliceSlider);
		

		
		timeSliceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (visitSession != null && !timeSliceSlider.getValueIsAdjusting()) {
					System.out.println(arg0);
					//VisitProcess.visitCommand(VisitPythonCommand.SetTimeSliderState(slider.getValue()));
					try {
						visitSession.setSliderState(timeSliceSlider.getValue());
					} catch (VisitSessionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		});
		timeSliceSlider.setMajorTickSpacing(10);
		timeSliceSlider.setPaintLabels(true);
		timeSliceSlider.setPaintTicks(true);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visitSession.resetView();
			}
		});
		btnShowVisitGui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Trying to open VisIt GUI");
		        visitSession.showVisitGUI();
				//visitSession.InterpretPython("OpenGUI()");
			}
		});
		btnAddPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (selectedVariable != null) {
					numberOfOpenPlots++;
					
					visitSession.deleteActivePlots();
					visitSession.addAndDrawPseudocolorPlot(selectedVariable);
//					viewerMethods(visitSession).SetActivePlot(numberOfOpenPlots-1);
//					viewerMethods(visitSession).AddOperator("Transform");
					//visitSession.getVisitClient().getViewerProxy().GetOperatorAttributes("Tranform").
					
//					VisitProcess.visitCommand(VisitPythonCommand.AddPlot("Pseudocolor", selectedVariable));
//					VisitProcess.visitCommand(VisitPythonCommand.SetActivePlots(String.valueOf(numberOfOpenPlots-1)));
//					VisitProcess.visitCommand(VisitPythonCommand.AddOperator("Transform"));
//					VisitProcess.visitCommand(VisitPythonCommand.makeTransformAttributes("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1)));
//					VisitProcess.visitCommand("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1)+".doTranslate=1");
					
					System.out.println("I have"+String.valueOf(numberOfOpenPlots)+" open");
					/*
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
					*/
					//VisitProcess.visitCommand(VisitPythonCommand.SetOperatorOptions("transformAttrsPlot"+String.valueOf(numberOfOpenPlots-1)));
					
//					viewerMethods(visitSession).DrawPlots();
					//VisitProcess.visitCommand(VisitPythonCommand.DrawPlots());
					
				}
			}
		});
		btnClearPlots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Trying to delete all plots");
				//VisitProcess.visitCommand(VisitPythonCommand.DeleteAllPlots());	
				visitSession.deleteActivePlots();
				numberOfOpenPlots=0;
				}
		});
		
		
		sliceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (visitSession != null && !sliceSlider.getValueIsAdjusting() && tempSliceSelected) {
					System.out.println(arg0);
				
					try {
						visitSession.changeSliceAlongAxis(sliceSlider.getValue());
					} catch (VisitSessionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		});
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.gridheight = 2;
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 5;
		add(tabbedPane, gbc_tabbedPane);
		
		tabbedPane.addTab("Clip plane", new JLabel("Clip Plane Operator Control Panel will go here"));
		//tabbedPane.addTab("Clip plane", clipPlaneOperatorControlPanel); 
		tabbedPane.addTab("Slice", new JLabel("Slice Control Panel will go here"));
		tabbedPane.addTab("3 Plane Slice", new JLabel("3 Plane Slice Control Panel will go here"));
		tabbedPane.addTab("Threshold", new JLabel("Threshold Selection Control Panel will go here"));
		tabbedPane.addTab("Transform", new JLabel("Geometry Transform Control Panel will go here"));
		tabbedPane.addTab("Smoothing", new JLabel("Smoothing Operator Control Panel will go here"));
		


	}

	
	public void init(DataIdentifier dataIdentifier, VisitSession visitSession){
		this.dataIdentifier = dataIdentifier;
		this.visitSession = visitSession;
		lblDataset.setText(dataIdentifier.getName());
	    
		clipPlaneOperatorControlPanel.initializeVisitSessionInfo(visitSession.getViewerState().GetPlotList().GetPlots(0), visitSession);
		
	}


	public void setPdeDataContext(PDEDataContext pdeDataContext, Origin origin, Extent extent) {
		this.origin = origin;
		this.extent = extent;
		
		this.pdeDataContext = pdeDataContext;
		comboBox.removeAllItems();
		for (int i = 0; i < pdeDataContext.getDataIdentifiers().length; i++) {
			comboBox.addItem(pdeDataContext.getDataIdentifiers()[i].getName());
		}
		timeSliceSlider.setValue(1);
		timeSliceSlider.setMinimum(0);
		timeSliceSlider.setMaximum(pdeDataContext.getTimePoints().length-1);
		
		timeSliceSlider.setMinorTickSpacing(1);
		timeSliceSlider.setMajorTickSpacing(10);
		
		sliceSlider.setMinimum(0);
		sliceSlider.setMaximum(100);
		sliceSlider.setValue(0);
		sliceSlider.setMinorTickSpacing(1);
		sliceSlider.setMajorTickSpacing(10);
		
	}
	
	public void initSliceOperatorWithSliceAttributes(){
		
	}
}
