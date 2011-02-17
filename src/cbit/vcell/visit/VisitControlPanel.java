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


import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.visit.VisitSession;
import cbit.vcell.visit.VisitSession.VisitSessionException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JButton;

import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import llnl.visit.ViewerMethods;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.SwingConstants;

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
				//VisitProcess.visitCommand(VisitPythonCommand.DeleteAllPlots());	
				visitSession.deleteActivePlots();
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
		        visitSession.showVisitGUI();
				//visitSession.InterpretPython("OpenGUI()");
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
				//VisitProcess.visitCommand(VisitPythonCommand.GetGlobalAttributes());
			}
		});
		GridBagConstraints gbc_btnTestGetglobalattributes = new GridBagConstraints();
		gbc_btnTestGetglobalattributes.gridx = 0;
		gbc_btnTestGetglobalattributes.gridy = 7;
		panel.add(btnTestGetglobalattributes, gbc_btnTestGetglobalattributes);
		
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
		
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 2;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel_2.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{152, 0};
		gbl_panel_1.rowHeights = new int[]{41, 25, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JButton btnViewSliceIn = new JButton("View Slice in Plane:");
		GridBagConstraints gbc_btnViewSliceIn = new GridBagConstraints();
		gbc_btnViewSliceIn.insets = new Insets(0, 0, 5, 0);
		gbc_btnViewSliceIn.gridx = 0;
		gbc_btnViewSliceIn.gridy = 0;
		panel_1.add(btnViewSliceIn, gbc_btnViewSliceIn);
		btnViewSliceIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visitSession.addCartesianSliceOperator(sliceAxisSelected);
				visitSession.drawPlots();
				tempSliceSelected = true;
			}
		});
		
		final JRadioButton rdbtnProjectSlice2D = new JRadioButton("Project to 2D");
		rdbtnProjectSlice2D.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnProjectSlice2D.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				visitSession.changeSliceProject2D(rdbtnProjectSlice2D.isSelected());
			}
		});
		GridBagConstraints gbc_rdbtnProjectTod = new GridBagConstraints();
		gbc_rdbtnProjectTod.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnProjectTod.gridx = 0;
		gbc_rdbtnProjectTod.gridy = 1;
		panel_1.add(rdbtnProjectSlice2D, gbc_rdbtnProjectTod);
		
		final JRadioButton rdbtnShowPlaneTool = new JRadioButton("Show plane tool");
		rdbtnShowPlaneTool.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				try {
					visitSession.enableViewerTool(2,rdbtnShowPlaneTool.isSelected());
					System.out.println("Toggling Viewer Tool enabled to:"+rdbtnShowPlaneTool.isSelected());
				} catch (VisitSessionException e) {
					
					e.printStackTrace();
				}
			}
		});
		rdbtnShowPlaneTool.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_rdbtnShowPlaneTool = new GridBagConstraints();
		gbc_rdbtnShowPlaneTool.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnShowPlaneTool.gridx = 0;
		gbc_rdbtnShowPlaneTool.gridy = 2;
		panel_1.add(rdbtnShowPlaneTool, gbc_rdbtnShowPlaneTool);
		
		JRadioButton rdbtnToggleSurfaceMesh = new JRadioButton("Toggle surface mesh");
		rdbtnToggleSurfaceMesh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visitSession.addAndDrawSurfaceMesh();
				System.out.println("Triggered Action Listener");
			}
		});
		
		final JRadioButton rdbtnShowPointTool = new JRadioButton("Show point tool");
		rdbtnShowPointTool.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					visitSession.enableViewerTool(0,rdbtnShowPointTool.isSelected());
				} catch (VisitSessionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_rdbtnShowPointTool = new GridBagConstraints();
		gbc_rdbtnShowPointTool.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnShowPointTool.gridx = 0;
		gbc_rdbtnShowPointTool.gridy = 3;
		panel_1.add(rdbtnShowPointTool, gbc_rdbtnShowPointTool);
		rdbtnToggleSurfaceMesh.setSelected(true);
		
		GridBagConstraints gbc_rdbtnToggleSurfaceMesh = new GridBagConstraints();
		gbc_rdbtnToggleSurfaceMesh.gridx = 0;
		gbc_rdbtnToggleSurfaceMesh.gridy = 4;
		panel_1.add(rdbtnToggleSurfaceMesh, gbc_rdbtnToggleSurfaceMesh);
		
		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 2;
		gbc_panel_3.gridy = 0;
		panel_2.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{148, 0};
		gbl_panel_3.rowHeights = new int[]{53, 25, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		JButton button_1 = new JButton("Reset view");
		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.insets = new Insets(0, 0, 5, 0);
		gbc_button_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_button_1.gridx = 0;
		gbc_button_1.gridy = 0;
		panel_3.add(button_1, gbc_button_1);
		
		JButton btnAddClipPlane = new JButton("Add Clip Plane");
		btnAddClipPlane.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visitSession.addClipPlaneOperator();
			}
		});
		GridBagConstraints gbc_btnAddClipPlane = new GridBagConstraints();
		gbc_btnAddClipPlane.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddClipPlane.gridx = 0;
		gbc_btnAddClipPlane.gridy = 1;
		panel_3.add(btnAddClipPlane, gbc_btnAddClipPlane);
		
		JButton btnAddThreeSlice = new JButton("Add Three Slice");
		btnAddThreeSlice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visitSession.addThreeSliceOperator();
			}
		});
		GridBagConstraints gbc_btnAddThreeSlice = new GridBagConstraints();
		gbc_btnAddThreeSlice.gridx = 0;
		gbc_btnAddThreeSlice.gridy = 2;
		panel_3.add(btnAddThreeSlice, gbc_btnAddThreeSlice);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visitSession.resetView();
			}
		});
		
		JPanel sliceAxisSelectorPanel = new JPanel();
		GridBagConstraints gbc_panel_1_1 = new GridBagConstraints();
		gbc_panel_1_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1_1.weightx = 1.0;
		gbc_panel_1_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1_1.gridx = 1;
		gbc_panel_1_1.gridy = 0;
		panel_2.add(sliceAxisSelectorPanel, gbc_panel_1_1);
		GridBagLayout gbl_sliceAxisSelectorPanel = new GridBagLayout();
		gbl_sliceAxisSelectorPanel.columnWidths = new int[]{90, 0};
		gbl_sliceAxisSelectorPanel.rowHeights = new int[]{23, 0, 0, 0};
		gbl_sliceAxisSelectorPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_sliceAxisSelectorPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		sliceAxisSelectorPanel.setLayout(gbl_sliceAxisSelectorPanel);
		
		JRadioButton rdbtnYzxAcis = new JRadioButton("YZ (X axis)");
		rdbtnYzxAcis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sliceAxisSelected=0;
				visitSession.changeSliceAxis(sliceAxisSelected);
				
			}
		});
		buttonGroup.add(rdbtnYzxAcis);
		GridBagConstraints gbc_rdbtnYzxAcis = new GridBagConstraints();
		gbc_rdbtnYzxAcis.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnYzxAcis.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnYzxAcis.gridx = 0;
		gbc_rdbtnYzxAcis.gridy = 0;
		sliceAxisSelectorPanel.add(rdbtnYzxAcis, gbc_rdbtnYzxAcis);
		
		JRadioButton rdbtnXzyAxis = new JRadioButton("XZ (Y axis)");
		rdbtnXzyAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sliceAxisSelected=1;
				visitSession.changeSliceAxis(sliceAxisSelected);
			}
		});
		buttonGroup.add(rdbtnXzyAxis);
		GridBagConstraints gbc_rdbtnXzyAxis = new GridBagConstraints();
		gbc_rdbtnXzyAxis.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnXzyAxis.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnXzyAxis.gridx = 0;
		gbc_rdbtnXzyAxis.gridy = 1;
		sliceAxisSelectorPanel.add(rdbtnXzyAxis, gbc_rdbtnXzyAxis);
		
		JRadioButton rdbtnXyzAxis = new JRadioButton("XY (Z axis)");
		rdbtnXyzAxis.setSelected(true);  // Default is to slice along the Z axis
		rdbtnXyzAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sliceAxisSelected=2;
				visitSession.changeSliceAxis(sliceAxisSelected);
			}
		});
		buttonGroup.add(rdbtnXyzAxis);
		GridBagConstraints gbc_rdbtnXyzAxis = new GridBagConstraints();
		gbc_rdbtnXyzAxis.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnXyzAxis.gridx = 0;
		gbc_rdbtnXyzAxis.gridy = 2;
		sliceAxisSelectorPanel.add(rdbtnXyzAxis, gbc_rdbtnXyzAxis);
		
		
		
		
		
		JLabel lblTimeStepSlider = new JLabel("Time step slider");
		GridBagConstraints gbc_lblTimeStepSlider = new GridBagConstraints();
		gbc_lblTimeStepSlider.insets = new Insets(0, 0, 5, 0);
		gbc_lblTimeStepSlider.gridx = 0;
		gbc_lblTimeStepSlider.gridy = 3;
		add(lblTimeStepSlider, gbc_lblTimeStepSlider);
		timeSliceSlider.setMajorTickSpacing(10);
		timeSliceSlider.setPaintLabels(true);
		timeSliceSlider.setPaintTicks(true);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.weighty = 1.0;
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.insets = new Insets(0, 0, 5, 0);
		gbc_slider.gridx = 0;
		gbc_slider.gridy = 4;
		add(timeSliceSlider, gbc_slider);
		
		JLabel lblXyPlaneSlice = new JLabel("Plane slice slider");
		GridBagConstraints gbc_lblXyPlaneSlice = new GridBagConstraints();
		gbc_lblXyPlaneSlice.insets = new Insets(0, 0, 5, 0);
		gbc_lblXyPlaneSlice.gridx = 0;
		gbc_lblXyPlaneSlice.gridy = 5;
		add(lblXyPlaneSlice, gbc_lblXyPlaneSlice);
		
		
		GridBagConstraints gbc_xySliceSlider = new GridBagConstraints();
		gbc_xySliceSlider.weighty = 1.0;
		gbc_xySliceSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_xySliceSlider.gridx = 0;
		gbc_xySliceSlider.gridy = 6;
		sliceSlider.setPaintLabels(true);
		sliceSlider.setToolTipText("Slide me to select a slice in the selected plane");
		sliceSlider.setPaintTicks(true);
		add(sliceSlider, gbc_xySliceSlider);
		
//		JPanel panel_1 = new JPanel();
//		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
//		gbc_panel_1.weighty = 1.0;
//		gbc_panel_1.weightx = 1.0;
//		gbc_panel_1.fill = GridBagConstraints.BOTH;
//		gbc_panel_1.gridx = 0;
//		gbc_panel_1.gridy = 4;
//		add(panel_1, gbc_panel_1);
//		GridBagLayout gbl_panel_1 = new GridBagLayout();
//		panel_1.setLayout(gbl_panel_1);
//		
//		JScrollPane scrollPane = new JScrollPane(jTextArea);
//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
//		gbc_scrollPane.weighty = 1.0;
//		gbc_scrollPane.weightx = 1.0;
//		gbc_scrollPane.insets = new Insets(4, 4, 4, 4);
//		gbc_scrollPane.fill = GridBagConstraints.BOTH;
//		gbc_scrollPane.gridx = 0;
//		gbc_scrollPane.gridy = 0;
//		panel_1.add(scrollPane, gbc_scrollPane);
		
//		textField = new JTextField();
//		
//		//listen for up-arrow key
//		textField.addKeyListener(new KeyListener() {	
//			public void keyPressed(KeyEvent k) {
//				//if (k.getKeyCode()==KeyEvent.VK_UP) System.out.println("Observed key up pressed. Command Index was: "+ String.valueOf(commandIndex));
//				if ((k.getKeyCode() == KeyEvent.VK_UP) & (commandIndex>0)){
//					textField.setText(commandHistory[commandIndex-1]);
//					commandIndex--;
//				}
//				if ((k.getKeyCode() == KeyEvent.VK_DOWN) & (commandIndex<maxCommandIndex)){
//					textField.setText(commandHistory[commandIndex+1]);
//					commandIndex++;
//				}
//			}
//			public void keyReleased(KeyEvent k){}
//			public void keyTyped(KeyEvent k){
//				
//			}
//		});
//		
		
		//listen for entered command
//		textField.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				//System.out.println("Detected a command entered");
//				
//				if (!SwingUtilities.isEventDispatchThread()) {
//			    	try {
//						SwingUtilities.invokeAndWait(
//								new Runnable() {
//									public void run() {
//										visitProcess.sendCommandAndNoteResponse(textField.getText()+"\n");
//									}
//								}
//						);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InvocationTargetException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//			    }else{
//			    	visitProcess.sendCommandAndNoteResponse(textField.getText()+"\n");
//			    }
//				
//				
//				commandHistory[commandIndex]=textField.getText();
//				commandIndex++;
//				if (commandIndex>maxCommandIndex) maxCommandIndex++;
//				//System.out.println("commandIndex is now: "+ String.valueOf(commandIndex));
//				textField.setText("");
//			    
//			}
//		});
//		
		
//		GridBagConstraints gbc_textField = new GridBagConstraints();
//		gbc_textField.insets = new Insets(4, 4, 4, 4);
//		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
//		gbc_textField.gridx = 0;
//		gbc_textField.gridy = 1;
//		panel_1.add(textField, gbc_textField);
//		textField.setColumns(10);

	}

	
	public void init(DataIdentifier dataIdentifier, VisitSession visitSession){
		this.dataIdentifier = dataIdentifier;
		this.visitSession = visitSession;
		lblDataset.setText(dataIdentifier.getName());
		//VisitProcess.setJTextArea(this.jTextArea);
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
