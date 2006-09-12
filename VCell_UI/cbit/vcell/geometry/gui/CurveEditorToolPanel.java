package cbit.vcell.geometry.gui;

import cbit.vcell.geometry.CurveRenderer;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/15/00 4:40:24 PM)
 * @author: 
 */
public class CurveEditorToolPanel extends javax.swing.JPanel {
	private CurveEditorTool fieldCurveEditorTool = null;
	private boolean ivjConnPtoP1Aligning = false;
	private CurveEditorTool ivjcurveEditorTool1 = null;
	private CurveRenderer ivjcurveRenderer1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JToggleButton ivjAddCPJButton = null;
	private javax.swing.ButtonGroup ivjButtonGroup1 = null;
	private javax.swing.JToggleButton ivjLineJButton = null;
	private javax.swing.JToggleButton ivjPanJButton = null;
	private javax.swing.JToggleButton ivjPointJButton = null;
	private javax.swing.JToggleButton ivjSelectJButton = null;
	private javax.swing.JToggleButton ivjSplineJButton = null;
	private javax.swing.JToggleButton ivjZoomJButton = null;
	private javax.swing.JPanel ivjToolJPanel = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == CurveEditorToolPanel.this.getSelectJButton()) 
				connEtoC1(e);
			if (e.getSource() == CurveEditorToolPanel.this.getZoomJButton()) 
				connEtoC2(e);
			if (e.getSource() == CurveEditorToolPanel.this.getPanJButton()) 
				connEtoC4(e);
			if (e.getSource() == CurveEditorToolPanel.this.getPointJButton()) 
				connEtoC5(e);
			if (e.getSource() == CurveEditorToolPanel.this.getAddCPJButton()) 
				connEtoC12(e);
			if (e.getSource() == CurveEditorToolPanel.this.getLineJButton()) 
				connEtoC13(e);
			if (e.getSource() == CurveEditorToolPanel.this.getSplineJButton()) 
				connEtoC14(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == CurveEditorToolPanel.this && (evt.getPropertyName().equals("curveEditorTool"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == CurveEditorToolPanel.this.getcurveEditorTool1() && (evt.getPropertyName().equals("tool"))) 
				connEtoC6(evt);
			if (evt.getSource() == CurveEditorToolPanel.this.getcurveEditorTool1() && (evt.getPropertyName().equals("properlyConfigured"))) 
				connEtoC7(evt);
			if (evt.getSource() == CurveEditorToolPanel.this.getcurveEditorTool1() && (evt.getPropertyName().equals("selectionOnly"))) 
				connEtoC8(evt);
			if (evt.getSource() == CurveEditorToolPanel.this.getcurveRenderer1() && (evt.getPropertyName().equals("selection"))) 
				connEtoC10(evt);
			if (evt.getSource() == CurveEditorToolPanel.this.getcurveEditorTool1() && (evt.getPropertyName().equals("curveRenderer"))) 
				connEtoM1(evt);
			if (evt.getSource() == CurveEditorToolPanel.this.getcurveEditorTool1() && (evt.getPropertyName().equals("enableDrawingTools"))) 
				connEtoC15(evt);
		};
	};
/**
 * CurveEditorToolPanel constructor comment.
 */
public CurveEditorToolPanel() {
	super();
	initialize();
}
/**
 * CurveEditorToolPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public CurveEditorToolPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * CurveEditorToolPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public CurveEditorToolPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * CurveEditorToolPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public CurveEditorToolPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/00 5:14:39 PM)
 */
private void configureView() {
	if (isEnabled()) {
		boolean selectButtonEnabled = true;
		boolean pointButtonEnabled = true;
		boolean lineButtonEnabled = true;
		boolean splineButtonEnabled = true;
		boolean addCPButtonEnabled = false;
		boolean zoomButtonEnabled = true;
		boolean panButtonEnabled = true;
		//
		if (getCurveEditorTool() != null && getCurveEditorTool().getProperlyConfigured()) {
			//
			int tool =
				(getSelectJButton().isSelected()?CurveEditorTool.TOOL_SELECT:0) +
				(getPanJButton().isSelected()?CurveEditorTool.TOOL_PAN:0) +
				(getZoomJButton().isSelected()?CurveEditorTool.TOOL_ZOOM:0) +
				(getPointJButton().isSelected()?CurveEditorTool.TOOL_POINT:0) +
				(getLineJButton().isSelected()?CurveEditorTool.TOOL_LINE:0) +
				(getSplineJButton().isSelected()?CurveEditorTool.TOOL_SPLINE:0) +
				(getAddCPJButton().isSelected()?CurveEditorTool.TOOL_ADDCP:0);
			getCurveEditorTool().setTool(tool);
			//if(tool == CurveEditorTool.TOOL_SELECT){
				//pointButtonEnabled = true;
				//lineButtonEnabled = true;
				//splineButtonEnabled = true;
				//addCPButtonEnabled = false;
				//zoomButtonEnabled = true;
				//panButtonEnabled = true;
			//}
			//
			if (/*tool == CurveEditorTool.TOOL_SELECT && */!getCurveEditorTool().getSelectionOnly()) {
				cbit.vcell.geometry.CurveSelectionInfo csi = getCurveEditorTool().getCurveRenderer().getSelection();
				if (csi != null && getCurveEditorTool().getCurveRenderer().isControlPointAddable()) {
					addCPButtonEnabled = true;
					//pointButtonEnabled = false;
					//lineButtonEnabled = false;
					//splineButtonEnabled = false;
				}
			}
		}
		//getZoomJButton().setEnabled(zoomButtonEnabled);
		//getPanJButton().setEnabled(panButtonEnabled);
		//getPointJButton().setEnabled(pointButtonEnabled);
		//getLineJButton().setEnabled(lineButtonEnabled);
		//getSplineJButton().setEnabled(splineButtonEnabled);
		getAddCPJButton().setEnabled(addCPButtonEnabled);
	}
}
/**
 * connEtoC1:  (SelectJButton.action.actionPerformed(java.awt.event.ActionEvent) --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (curveRenderer1.selection --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (CurveEditorToolPanel.initialize() --> CurveEditorToolPanel.initButtons()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11() {
	try {
		// user code begin {1}
		// user code end
		this.initButtons();
		connEtoC3();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (AddCPJButton.action.actionPerformed(java.awt.event.ActionEvent) --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (LineJButton.action.actionPerformed(java.awt.event.ActionEvent) --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (SplineJButton.action.actionPerformed(java.awt.event.ActionEvent) --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC15:  (curveEditorTool1.enableDrawingTools --> CurveEditorToolPanel.enableDrawingTools()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableDrawingTools();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ZoomJButton.action.actionPerformed(java.awt.event.ActionEvent) --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  ( (CurveEditorToolPanel,initialize() --> CurveEditorToolPanel,initButtons()V).normalResult --> CurveEditorToolPanel.configureView()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (PanJButton.action.actionPerformed(java.awt.event.ActionEvent) --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (PointJButton.action.actionPerformed(java.awt.event.ActionEvent) --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (curveEditorTool1.tool --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (curveEditorTool1.properlyConfigured --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (curveEditorTool1.selectionOnly --> CurveEditorToolPanel.configureView()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (curveEditorTool1.this --> CurveEditorToolPanel.configureView()V)
 * @param value cbit.vcell.geometry.gui.CurveEditorTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(CurveEditorTool value) {
	try {
		// user code begin {1}
		// user code end
		this.configureView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (curveEditorTool1.curveRenderer --> curveRenderer1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurveRenderer1(getcurveEditorTool1().getCurveRenderer());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (CurveEditorToolPanel.curveEditorTool <--> curveEditorTool1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getcurveEditorTool1() != null)) {
				this.setCurveEditorTool(getcurveEditorTool1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (CurveEditorToolPanel.curveEditorTool <--> curveEditorTool1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setcurveEditorTool1(this.getCurveEditorTool());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void enableDrawingTools() {
	if(getCurveEditorTool() != null){
		if(!getCurveEditorTool().getEnableDrawingTools()){
			getSelectJButton().setSelected(true);
		}
		boolean drawingToolsEnabled = getCurveEditorTool().getEnableDrawingTools();
		getPointJButton().setVisible(drawingToolsEnabled);
		getLineJButton().setVisible(drawingToolsEnabled);
		getSplineJButton().setVisible(drawingToolsEnabled);
		getAddCPJButton().setVisible(drawingToolsEnabled);
		configureView();
	}
}
/**
 * Return the AddCPJButton property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getAddCPJButton() {
	if (ivjAddCPJButton == null) {
		try {
			ivjAddCPJButton = new javax.swing.JToggleButton();
			ivjAddCPJButton.setName("AddCPJButton");
			ivjAddCPJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/control point.gif")));
			ivjAddCPJButton.setText("");
			ivjAddCPJButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddCPJButton;
}
/**
 * Return the ButtonGroup1 property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup1() {
	if (ivjButtonGroup1 == null) {
		try {
			ivjButtonGroup1 = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup1;
}
/**
 * Gets the curveEditorTool property (cbit.vcell.geometry.gui.CurveEditorTool) value.
 * @return The curveEditorTool property value.
 * @see #setCurveEditorTool
 */
public CurveEditorTool getCurveEditorTool() {
	return fieldCurveEditorTool;
}
/**
 * Return the curveEditorTool1 property value.
 * @return cbit.vcell.geometry.gui.CurveEditorTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CurveEditorTool getcurveEditorTool1() {
	// user code begin {1}
	// user code end
	return ivjcurveEditorTool1;
}
/**
 * Return the curveRenderer1 property value.
 * @return cbit.vcell.geometry.gui.CurveRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CurveRenderer getcurveRenderer1() {
	// user code begin {1}
	// user code end
	return ivjcurveRenderer1;
}
/**
 * Return the LineJButton property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getLineJButton() {
	if (ivjLineJButton == null) {
		try {
			ivjLineJButton = new javax.swing.JToggleButton();
			ivjLineJButton.setName("LineJButton");
			ivjLineJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/line2.gif")));
			ivjLineJButton.setText("");
			ivjLineJButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineJButton;
}
/**
 * Return the PanJButton property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getPanJButton() {
	if (ivjPanJButton == null) {
		try {
			ivjPanJButton = new javax.swing.JToggleButton();
			ivjPanJButton.setName("PanJButton");
			ivjPanJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pan.gif")));
			ivjPanJButton.setText("");
			ivjPanJButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPanJButton;
}
/**
 * Return the PointJButton property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getPointJButton() {
	if (ivjPointJButton == null) {
		try {
			ivjPointJButton = new javax.swing.JToggleButton();
			ivjPointJButton.setName("PointJButton");
			ivjPointJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/point2.gif")));
			ivjPointJButton.setText("");
			ivjPointJButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointJButton;
}
/**
 * Return the SelectJButton property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getSelectJButton() {
	if (ivjSelectJButton == null) {
		try {
			ivjSelectJButton = new javax.swing.JToggleButton();
			ivjSelectJButton.setName("SelectJButton");
			ivjSelectJButton.setText("");
			ivjSelectJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select2.gif")));
			ivjSelectJButton.setSelected(true);
			ivjSelectJButton.setContentAreaFilled(true);
			ivjSelectJButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectJButton;
}
/**
 * Return the SplineJButton property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getSplineJButton() {
	if (ivjSplineJButton == null) {
		try {
			ivjSplineJButton = new javax.swing.JToggleButton();
			ivjSplineJButton.setName("SplineJButton");
			ivjSplineJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spline4.gif")));
			ivjSplineJButton.setText("");
			ivjSplineJButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSplineJButton;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getToolJPanel() {
	if (ivjToolJPanel == null) {
		try {
			ivjToolJPanel = new javax.swing.JPanel();
			ivjToolJPanel.setName("ToolJPanel");
			ivjToolJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSelectJButton = new java.awt.GridBagConstraints();
			constraintsSelectJButton.gridx = 0; constraintsSelectJButton.gridy = 0;
			constraintsSelectJButton.ipadx = 4;
			constraintsSelectJButton.ipady = 4;
			getToolJPanel().add(getSelectJButton(), constraintsSelectJButton);

			java.awt.GridBagConstraints constraintsPointJButton = new java.awt.GridBagConstraints();
			constraintsPointJButton.gridx = 0; constraintsPointJButton.gridy = 1;
			constraintsPointJButton.ipadx = 4;
			constraintsPointJButton.ipady = 4;
			getToolJPanel().add(getPointJButton(), constraintsPointJButton);

			java.awt.GridBagConstraints constraintsLineJButton = new java.awt.GridBagConstraints();
			constraintsLineJButton.gridx = 1; constraintsLineJButton.gridy = 1;
			constraintsLineJButton.ipadx = 4;
			constraintsLineJButton.ipady = 4;
			getToolJPanel().add(getLineJButton(), constraintsLineJButton);

			java.awt.GridBagConstraints constraintsSplineJButton = new java.awt.GridBagConstraints();
			constraintsSplineJButton.gridx = 2; constraintsSplineJButton.gridy = 1;
			constraintsSplineJButton.ipadx = 4;
			constraintsSplineJButton.ipady = 4;
			getToolJPanel().add(getSplineJButton(), constraintsSplineJButton);

			java.awt.GridBagConstraints constraintsAddCPJButton = new java.awt.GridBagConstraints();
			constraintsAddCPJButton.gridx = 3; constraintsAddCPJButton.gridy = 1;
			constraintsAddCPJButton.ipadx = 4;
			constraintsAddCPJButton.ipady = 4;
			getToolJPanel().add(getAddCPJButton(), constraintsAddCPJButton);

			java.awt.GridBagConstraints constraintsZoomJButton = new java.awt.GridBagConstraints();
			constraintsZoomJButton.gridx = 1; constraintsZoomJButton.gridy = 0;
			constraintsZoomJButton.ipady = 2;
			getToolJPanel().add(getZoomJButton(), constraintsZoomJButton);

			java.awt.GridBagConstraints constraintsPanJButton = new java.awt.GridBagConstraints();
			constraintsPanJButton.gridx = 2; constraintsPanJButton.gridy = 0;
			getToolJPanel().add(getPanJButton(), constraintsPanJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjToolJPanel;
}
/**
 * Return the ZoomJButton property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getZoomJButton() {
	if (ivjZoomJButton == null) {
		try {
			ivjZoomJButton = new javax.swing.JToggleButton();
			ivjZoomJButton.setName("ZoomJButton");
			ivjZoomJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zoom.gif")));
			ivjZoomJButton.setText("");
			ivjZoomJButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZoomJButton;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Comment
 */
private void initButtons() {
	getButtonGroup1().add(getSelectJButton());
	getButtonGroup1().add(getZoomJButton());
	getButtonGroup1().add(getPanJButton());
	getButtonGroup1().add(getPointJButton());
	getButtonGroup1().add(getLineJButton());
	getButtonGroup1().add(getSplineJButton());
	getButtonGroup1().add(getAddCPJButton());
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getSelectJButton().addActionListener(ivjEventHandler);
	getZoomJButton().addActionListener(ivjEventHandler);
	getPanJButton().addActionListener(ivjEventHandler);
	getPointJButton().addActionListener(ivjEventHandler);
	getAddCPJButton().addActionListener(ivjEventHandler);
	getLineJButton().addActionListener(ivjEventHandler);
	getSplineJButton().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CurveEditorToolPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(138, 65);
		add(getToolJPanel(), "Center");
		initConnections();
		connEtoC11();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CurveEditorToolPanel aCurveEditorToolPanel;
		aCurveEditorToolPanel = new CurveEditorToolPanel();
		frame.setContentPane(aCurveEditorToolPanel);
		frame.setSize(aCurveEditorToolPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Sets the curveEditorTool property (cbit.vcell.geometry.gui.CurveEditorTool) value.
 * @param curveEditorTool The new value for the property.
 * @see #getCurveEditorTool
 */
public void setCurveEditorTool(CurveEditorTool curveEditorTool) {
	CurveEditorTool oldValue = fieldCurveEditorTool;
	fieldCurveEditorTool = curveEditorTool;
	firePropertyChange("curveEditorTool", oldValue, curveEditorTool);
}
/**
 * Set the curveEditorTool1 to a new value.
 * @param newValue cbit.vcell.geometry.gui.CurveEditorTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setcurveEditorTool1(CurveEditorTool newValue) {
	if (ivjcurveEditorTool1 != newValue) {
		try {
			cbit.vcell.geometry.gui.CurveEditorTool oldValue = getcurveEditorTool1();
			/* Stop listening for events from the current object */
			if (ivjcurveEditorTool1 != null) {
				ivjcurveEditorTool1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjcurveEditorTool1 = newValue;

			/* Listen for events from the new object */
			if (ivjcurveEditorTool1 != null) {
				ivjcurveEditorTool1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoC9(ivjcurveEditorTool1);
			firePropertyChange("curveEditorTool", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Set the curveRenderer1 to a new value.
 * @param newValue cbit.vcell.geometry.gui.CurveRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setcurveRenderer1(CurveRenderer newValue) {
	if (ivjcurveRenderer1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjcurveRenderer1 != null) {
				ivjcurveRenderer1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjcurveRenderer1 = newValue;

			/* Listen for events from the new object */
			if (ivjcurveRenderer1 != null) {
				ivjcurveRenderer1.addPropertyChangeListener(ivjEventHandler);
			}
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF4946539910322269AB5C2D02AA984850CB655D8E31BAA7552DE509C2527314D3D651E52963DD839962F74B647626D64A5418D8164A156A8C1D0034448CB0D889AC8448496D8C3D81238011D5D1DA493E69F191DCD8284385F7F1C595D59EC0C2777B467FCE7E63E7FFB7F0F6F7F3F190D30F6E3DAE9E151DAC1A8AC95685F4705023044A688894AC74FB1DC324FEE918CFF1BGD68B5D01923826
	C1DB4603F64BAA615D3EB2F84E0267219F59AD2F427D9A2126AF67B05C44501382ED63DC17DF98DE4F54A626E799FE3E65CCB9DCABG3281072EA5FE247F5EB39BA8FE9E45B3A8AC920485186631B3159457844FCE00B400ACBF214F056BF22CF96C1EBACA6738FE1A30649135E88512E3104920A6C0F8ED74DAA53CD0D890C03A7E026DC4BC6360F9A6GF23ED8B85D34932EFBEDE73ABFF33BE42D365FADA9CAED376477CA1AFA3636BBA46F595390D27B25AFBC3266D7DB7DFE45A67AA4C59034BEA477F3258C
	4B819970AC2888176EC43A3261F9AE40C20E4FF6A1FCA35CBBGB41579376F5D3D547E3673778B8B3AFF36FCE58C7F12D462D7162A7BD7751E2B78B55C97CFB62318AF063635C13B259A408A20GA4813C0E4740222677603A5F25B5783DDE3FEFFFA0F0C07128C77C9EC9017BBDFB200562DAA437DF7588826967974F541858B39C90BE0B72CD79C8CC266DE2FD1E51F237B06F5E71C54BCCE2B2EF5A792274A009C5CED00F89515D98D3773CE078188C57FDD2B06A961E5E392C54C4F7652FF6AEDBCC6514E83A
	6EFA8D7575869175759638AF24F2897EED0CCFB46049781C01478CFACE033612909B9BED954C96EB49E4E1550E8926F1C8FDB82928A7C4ECC888C55A1265C632B3C2C467E6EE4BE3983F5400A736BC0847A9FA36765BADBD83F68B99DF4F9EC673B5F19066A9C0B640DC0045G65E76D162A33EC0CFD587C41884718CDD43583A1D971C82AC0F81FEDDE8FD7E9D0D345E6DFD093FDEE49AE2A3268D22420C0E97E520CE2608A63DB0A473E9BF4FCA979A4D554E43F8F240BB00DA5B5C8E8CA8F2DC6B42A04DBDBE5
	2F848A3C810149DDF1E88DDC95B1281D88F8C4CD52A9C87B3AC328CF7AC5BBC88481F0CF62727A91942FFA703F8D200747A1994BBBAC2930DE6C59E3757B4F04822D4893A17BAC0A7357F86D98027B1433AC462F9EC53825703C769C13732F07DEB148E9145D48FCD1BD4B66581B62AA38D683FD930087A0619C9BBF4F363171533862AE2166091BCC47CFB20C6554F3E45CE41E0B1CC75B30FCE4E3BE3495GECB84F567FCCE79B9AF3672267D335892E4984E784CFC4BB51B1876BD8EEC2DFEC02AB9DC6BE13E9
	FCA6F19FFB8C75E19D78558A201DE731AFBADCC5467281B1A83151FBCC0501A7F9741EA0F67E485DC8E30B7253CA0CC3B28481655FEDD4575BG6F822C83789C60FDBA7E5EG58A810BCCB6465E1F9C9E0CB9640BC0012A13B25862095E088A09DA097E089C065853B2591C081C8814881D8GD0FE117555AC572AC2733CD2E1E273BAEA77EBA676D5987C28203E3D9FA76E95545FEF128B7EBF0066A9B70A0D59F8E2E3EA999D03969A1795B4D64638EC2674ACFE95B4262F0F400E577E89E29189E48E949CC6BE
	C87D12CFEB1482124F83B7C15A763743A8A6F43D6E72F31ED2AB4287E0FDEF0085B5487196AB0A1928E92A6C8AE952F1C953E4DF375E3E460AF485C32E001E9A182E3FDBD1BF7887E0FD67749B2DE8EDF47BFDBE89CFC64EC25BB76135D9D2A4AF985D608F79B402FF77885A471E114E525C306D48CB82DA5B15904AB3E4EF18A9223E8E28329726B8CAD5C262C534EE2B4A86C30CF6838E6DF15B602ADCE46B6E40E6366EEEB9B7DD18F9BAD0E43607C81DFADDD16EC532664D3F282F3B44264A2366363F05F384
	4587092D1F9C058D3DE036D3AD46BC97D620B55C9D5E3E0334BD08ED41EDAD28F7D5C95DC166429AE175FAE44301102679FDDF2A7ED0E087D9AF9F34A13CF0897294C04DA536DE5EFA943533DCC5974A863F22106E01B665920AE11F38164620911E9D17C88CABF87BFA97EB4F0267DCC37B7684541E445B9114A4686D440EEB0EA1FF03D2DFC802AD84CA3A07F9DF5FF06C1508B6BB6DF752FE73B1546F0AA1DF67E01D6BBC6C3CB2C875A5A7101C188157B781EF3D8C517D165315DDC65A2BAEA3745BAFA3B692
	3A899223CB25F41926F4473B985DDCCA3750142E221B51150C516D0A26FB1853D50EA179FC7718F03A64B108EE280751D929BC8F25CF98E314F7C4E6F473281CA574DAFE3911EED9AF232B86FCBD0073F2B37B9E65F4E9501E9D136E92BFBF66DFCEF261E2D33ACD8A6F0FC4680B44D8721E60F475C05396136EAAAF9F87C0139C13EE1F0F51E5824D6C18F44FF13A05C0A30C0DC5B7554F682A46127E289BCB571E31040E2C854FEFC56B93BE53760A7D62E0EDF0G5658BDFB8E6175001CDB67613DA7229F9A4B
	4E27E581B4273360B997E041D836167DE285BB1F261C19A64CFBFA4732D8676F25540E2AAB74352CE6717E17481AD15602F4BA21ADF085DBAF56590CFB647D41205FAD636DB91D0B33EC46350E606658DE06EB172C8635E6D82E86593CFD5C066CC57BF70A266719D4FC66C26BD9A2684D81D8F2856EEF9165GB407091F6EDD4C4F64F3D38567CDB797D9E278D9F7A5712F6DCA031F978BCA093EFDAE26AF995AE7DF49EA8D4FD93689A32AB530D51876670B35EF187627E1D59658597542C1E3DF652C3C905387
	E19C35FD1C6EC33E37F5997161B38F7341896D29GA49FBCA3227C9C11C7E82E1863C17154FA64A031653F7757D3DD39F7CF963217A5951955B5FA6EC8AD1ACBEDA8B058327BFB2E62A6GE2CF19D4CE47EB53D5307EDC456C71CBD8A798C1E2B54707731C5F57A5F707D449AECB8355DB6D441E49EB0C766C3DF44A541EF1ABFB0A322F22B979AA5D1E269BDC450E9B18BDB339BDB5D7433AF8B533675DAE14DB822ABFA0294A5986E607070D4521AE46B789F872B85F9F3A501C8ACAB4CF9E73A9E779787D381B
	7114DF83FBFCG02FF066307G17F58D135552335CA80B4B195A436897816D16A4C6DFAE2F90A2723F40727F47F2F81F5E7526B106E562D85398A6BD5DDD64C8A231CBCC52E31889711B45E358AFB3FBF2012664BB4C1E7F40F859A7EB32284867246A2D24EFDF6B2C20F8B2720350334C376D3DCC56DE10238110FD02CBB36E93B2G1F8F30146B1A500B6AFFA28ED4AD3958A4354B2FFAA5CFB51AED356200D60B773E357BF1739768FEAB533B046BDDF5AD1C3F2FE5FADF68B4531BF0AD6B3BE9E798DF8A60F2
	2FE5EB44E7DFA13C8F740BCA0B948CA99A21BF24B07A126B6C163D57B1BD8F1CB65323DD47747CAF679B8738191C2F56EB46D740796E7172B1178CE3AE19717D51E146B7144C783AFC0CAF99F033B95FD4590CCF381E717D5D4F7856824EFABD632BB70DE3A267FB0C732582EE6E752CDFFF9CC273D072216DDB23AA2285932DD4FCEFBF282DB3ED47C30A1E89FF97D5BFAC3BC12FCFD49A6E99103A5A904E235BEFA0F535E20E3F82636B8DF862C3C5002D35B6E84B3A0155029EEFC5F9FC430D30BF833082F8G
	928152EFE4797364D73B04B83536E61F8669D5D40E079D2AD84D2B856905F390FDDE5D0262034FC1BC8E370AA88EB3EF9C3EF6E38442170179723F819FD9E336F6986B7DA52B8F95951B2CB138BFD2086C8DA97A9AC3E217E0C763268D709AC092C0BA402C94963771ADB57162466A0EB4E04427865768E271156B1B4602787154C964478230DB3849EE59G309D40F69373E3FECB5C7E7FE250347FE90D5C932FBE9F38492CA6579217AFA5020F7833379D6DE1B320EDB640FC0025GD563E1CD83F00EE7FEEDBF
	FEA01EDF7BD99121554FEF9BA28B814C5E8AA9525E41716176197BD99945D79B6967F8B3BFFF50017D043659G73811682D4CDGBF811C93181F179F5F97377FD042E975B7G2E894E24A6AED2DBFFBECA9F73627A18B0410CEFCC576874A5C771C56ACBB555372FFBF47A8EC771C56A4BB455775CA8757DAD2E3E59267A6E6C991D3E8722785E0CF81EBF41EC0CD6BB5098AD00363529F6CB8DC0930082B08EA0A3150D513C631F0EF00C129DD27488A5F68E4923736F6AA83E48F866261A71FD54BBBAFD0DD1FC
	117A96C468A3FB0A5BCF21BA3049393B555F5D0D72885A34529A62CF8211B5C474FEE5A948DDB591F296BE23CF545059976D4D2BA7329A7E8B237429AC6A7DED24CFC9934379088DFFF1A29BD0DECE67B65C9DA5EBE4B6644435E15EC4B3BEDB54BEECE47ACEC571C56ADBE26A7362534C67721B194FA55E515970CA94DF248D0DB71B59A0F9198D8EEEC376A8ED78F9DC9BD2CCED585449EC4860B6A83E5159F0AE0AAFEACE195AB0CFE6B6AC65B6D479C7E7C3ED94DF248D3BEFB133E1004741FA8B33A1EF14B6
	7CBE2E8D0991B610F7458538DEB7895AB281886E899F42654173A2EE57987C9D502AC96CB989BF23B393F9DE9BE80B50E78608BF9E0D99909D6F0FC8C72CF7ED710064247C56586F24FE5BEA7ECE6A49D673F7D24F342EA6F810E17CA20135BF5FBA7CBB2B3F361A3D33FA837B782BA2F436D3C3BECD76CAD8AD499552A0FADF881933A564A3E995E98EC61EBD84162BFBBAD1BFD6CFE2B9797A93BB05681CCCCAF2CCC82BA42A473A3A786678F8C0F2B31B4759E3476FBA3BF97CEE321B476F967B70711BE29FBE
	FE4F1E0CED4B1FCF1A5B725713663694368DEF4B7236D836107E7ACFDC9708FA1FF8D474CA822B091D77AC27B4B64DEF5B41E59FEC0F6D4711F6F3BF0E37633A1FBF243A25C8DBBB5AB770B6FDCF552CC9DEE48E21F1E3FECDD43B25A8DFFBB1BFE90B459FECC775E1176C11D5DAADA4F11833FEA50EE3B0A88571662ED154C46A7F9792737F8B4D5F70915777630E587EBF5CE16E7F6C0E587EBF5E915F7F7C0E587E7F36A33E7F8BBBE20D87626B0BE13EFE4C718FF49971F59C2FF59B711FF0FCF54F4B867C0E
	6D8C7F7B9E837D91FDAC6DF4440E65A78E73D8FE6E089D4B030E7831BC66089DCB3BA3FEACCFBB060F653DF2D88C78BBE6EF2FB1E6A886A46F7EB9405ED1B5CEE26F282E8F2275F4881ED3GB2F81E3BD5B34BB9330649E37950364850AEFC175C2F05EB75F74379081DBB4E986DC46BAF513D5409F27A5E889E9BFD7640F550D45E13E172F6F17C150A913F1B6B7973E924A7A9C2D69AFD4E026BECD3BD1FFA0D72BE6472FA713EF0DE844FE27ADCF22B5D3261D6B3F9FF72067563CEA66F1DCEA42FA602271EBE
	37423547D45E94DF183CDD3C7E0A4FFDC391BC4974B99D2E592672CE7A43646DE6720630FD3991BC7369739238165DE6A64F92881377A1432F7422EFC694712CBF248942385B5078FC474B4EF515703C9B408190380D1C6FC86E7F41GDAF33BD4518B5383A7E5CF9A62DD64E33CF301FE81C0896037GB426313A76A3F89DC1256AC374BBE4B277144E954639C77395BDB386A2771C99E93A1FB35322678829D72F5350DC4BCBA3756A159C7FB646AFB560C99DFB821E1B68F94B776C16346F31FD6ACCFC0E2D1C
	EC3754CCE63837C388D7874FCD9C2761EF569D709C60381FE0DCA2BC27F35CF2156DFB33B96EA93C97AE1BE237D4CEE138B5A253D14DF1C1B733E5AF476539193CA60EBBA0B2F9299CD76EE47252B94E69E17232B96E279EA6EF9647EDF4B2F9EB53996EB2AF13D745F16B9DCC5EEE0E9BF0B0F9759C37ED0049CB6638FD1DCC5EA40EFBC4E472B2F5DE97131743F197BA193C72298CD7A0B3F99BB84E6762FD49F10FF3F9F59C57F00A499B47F1B7F27BD2B82E464964CD63386FB8193CAC0EFB1E5BD7F2BBFB77
	F3544B3E69DF8538265B597CFA2A964D89DE07C1EF70766CB960C75FA2D949E4A579E5BC0EBF62CF381D6517D9D8E6B6BC4F85D8F8BB7BB6E0709D76DEBD637649425CA74D3F01185FFA21C838034C27ADF7985EE53F67AAA6BADF71B31DCEE8CF3D833F63729B5FF121F9FE39C04E0524DD8DEBC7B97671C2E3FBFAD8BB32F7DDD87B2F425AD10C7E0D4AA73EBD7AB673ED69231902F2DFE24C5FD74C27BE15CC233EBDB631F0DC2A6DA552C72B71DC5D8BED36E92C5F8E86D9DFEA53D82EFEDDC3F547B4F84E81
	4883D884D0B69D6681C05DF4C6378D53216F045A6877B3685B98743D8D7AA688FD47C3683E9B8C98EC4C19262FE7F9534C73BF529316C3298DF553631FFB4BA88D2DE90569B6FEFB9C791DB10395914F45349FD646510B40E260D9A37CDA436F66E1A3E81B0EFE2B136991F85B5E90EA13A1E9AD190E7A21852F37A9F01FB61D6512EBF0FF116F84A48F211B5C47FE4FD2B703516575616F667DBE8DB6217BD5C9BCA8AB8A7A24C1A0BC4FF21EA660C91A01F0D5EAA33535EDC6F4CC8BA994D3D0008641BA0ACF18
	C1665DF26E4F0BBE14EB3D307F14FD3037427E6E6892388DFB71BA1787FC7381C8FBEBB7EAA776BC63E676545C99EDCFF19CFB6A6FA4398E75AF111D58236B7E902FD7D620E93B13693EC356F567FB186EF4935D25F1F4E7D35D05DC77ABE7F45DB3709AB68BE866F25D9FAA3A6EEB9DCC77168C73FEA89EC6F7DD065E8FC436572B6B6E42EBF2A35058B2186EE41F2E7B000B694EB0513DAC0E6E19D13A6778F55D1BFA116EDC2049673A9785F45DCFF15DB5F7197777F03A6B6F0A543DD255F567E03F2DC053F6
	97533DB3286BCEF7F23FCDF4174673BBCA7789CD9058FA539AD54B0D87044FB5CA3EDB3EB19F59A30E475F01645E250FDD023F8D63738DF8124B9F6139FC8134B55D4D3E01288B21BDF186BC4F86188FD0G30617B30BF7ABE2B05AD69046374706F27DA7D81AB9C2695C3C9087877EAD4BDB69E101C7953E5ACE7E67DE80610FDC5E34C6F6C1A3ECF46C08F3DE667CEACC4D7625F237D483FD4E84B824887D88CD012897B58CC665F5ED374DB85D45E5B2FE9223B87FF8D490B0532B42023510102569C7A224FA7
	1F7731B346F6105F8490GC84CA45F55E4E6A21A4EE055B07ECF4AA4FE909D1BFDA81F9D47B96490FD43455A1EAFC4FDEA734BBE56C470CFB522D8D9E51F44B8887EA18B1695D08CADCC56F70A112C7D9ECF03ADCC56BB27112C8E3F5F9BAE4B0A4FECF0C46202C83FBD7E35715B12D5B7ABC5EC9CC67AD9C07DAC79013E0FA373F159B9015BF53A2F892EBEE9204A704A2F3659ABF6CB4D10B12BD1398495A82B375642ED0BC4AAB055EFFDA0A3126087A47B6E2C6D163B5E5A3A155818B6E034316E65B1263F9D
	C0D0C7ED3352EB525FA696527AD49F5E9B28A109BC172A6C196CC33FD2D9EDA591F8B78A64B7ABE5027EC7FC6DBBAF70B17F3BF2B666AB6F1CA2B47E3A31E8F38C3BF228BD657768311B695C3D1C485CFBD10F1F537D596862F78E3A1CC60D550BAABD3CDEF48FDB172F39247B72F2212E7708E75F687486C49FD7CB75AE17783754DC6F7BC53A5E3B0F69FA8B9DDFFE2B7192C033098D972A7FA187531F45757F42266BEFEC51755FAB9E7DD67A51AED075ABD5580E087887DD4C0EC55C0E6E633A9DFFBF215B51
	6EBA782D6CC0EB444E887D2F3A197EAD77B27D7B6C3A7E4C133A7ED71D5FF25E62D5EAB7350068FF424974BB387EDF2232A44D2513FC7A7DD4437DDC6FC85E170D96080E277F21BAC81E5A9F0ADD33EE8A19572C0F064C5F63340606FFE76488457E1D18BBE47C1D984933D35A5199B8857AA593A08FE0A14052FBD90EFDAE741600F32CC9FE7D42530DEA666857489A531F579F5B575F74477F8D7302FE141FDCFE48CDDEA41F617F580F7ACB7387B012607E540FE2244A5DBD9AA1E56F467A51F8D324AE0CA67B
	28B7D4360F5A1085E3B00B55C4F6B851D9BA22A67225AAFB8E085D24A8C23FBBD0D9FDE2DC962B095C08E54E0467FC0045D96CDCF8AE0876978D927A3E146508814918A3AAB756972D96E2FC3BF99F498D3B6F53F3043EAFAA7DDA60FE0E9DC03FB5350B3D26EFB98F01BB47DC3D10C5DB45EE4E7F272F119FBDA0BB71BE564756F35BE24981248DFF332F1DA5D87C0BB0FE46B76E9153C1DE76FD435789088D5BBA51B9A097E89782147F50EE5982D087E08370G8C82A481248364G4C85D8GD0GD0F6BF6C81
	81365F4F6C7F61792D4358CFBB938FD4D51451D02576EF8A7BD6339164355DBF3C7DE65FCCAA4013B0A23ED98EA3DF8A70CC9B915F204B481783BCF3C744D769B672ADG1E0291717D3B5348D71EED37D4E58F4FC77A614EE15E735E539EFF6E7F389D7F268966EC6B59GFDF7B6339D4DA5FFD7D7D062EB4C4F5AD7F1DCF350668F4A681D9B31FD33BA3A735C0ED17275FEE3BEF25EDB065F896C4E0EBC872E42787A6C48F360C43EFED921ADB31B1D83671C2A026B5A87E0BEG3482A8G89G29G99G339E60
	5F44F7D48B316789A9846B53041EA13FC6B673FCC326BF7B5660EB14BB786FD1704F530E83212F1B54B8FF09EB1C739FA03E90DCBE4695D09CFDBF5B99FB5C9C6C0CBFEE0EF51A670C931D51B9232353BCE7B8BB0DB9036024CE14A33AE17DE863F43D9C574EF1FEAC9361CE4A9E2D0763C358AE04EF1210C206BF0F6D9A4038B60ACBB0605A597A5E0946BE385C63D7996F5578FB17AED9D148733518C67641D4607138915BA887C4035D299C574E7B224E935DBF7BBC467E216F2424E83A7724E83A5FB8226966
	BB22692EF3C55325382269F63923696A5C51F44FB923697E47E924A3F3F5852EC5117B75067B5B54FF7CBEB37B9F2A036E3DCE8D3377BAB5023D572998FB2FD3A6FB2FD3B176DE272267518BD42E4ABF98A2F861B42AEB10BA4C915187678734AFA57BB2719D43EF5D7034A2784A4195BA1ECDAB017C9F2FB7FCDBE0F2E47C17D08A3820AA2C643FD3EB4673E3FE7F2EBD45FF18D2DCA414B48BAF91BF1B0592E4EC33D00AFDE9960A414FC1DCB0B57EA101FD42C53CG7F73B4000F1B5DF05C17DD5E5AD03F37D6
	F4E93592AC6A35A84A9EFEE86B7897E7F64F1CF5EF9589254D0225CB16944FF1FCEC338A2B70D32394F42BF2G2D5F1A700A95056D83C574F5FFC0D673CF2D42EACC470EFBC8BCB2E815D5D8E390F7D0D6A41F5ED4E1E8BA51E255048AA259A7E99F407327B84A051A70D6ED2D435EA0A9CAD503A24310F510232443D0825204B5A605A124E7CDA0C6E890F6A5D1301EABB821490A03D5C8B4E1B92F16A06AD7C7A0EADD38A8DA745004B2D67FC0342F0FC0502698CE6BF58C68096862861230F184622BE3080FAA
	D3E842EBB1CB98C85E6691A82B0A210C57A2B4E1F5D4FD82713FBB8261B1C707DEE8B00E8E9D0BE4EC0B21007E1E6E12311ED8B0569D331E3878C1DA73FCA87277F42770BB3EFA40BB9EE27B2C3B8368FF7E44BA179D770A0A22094A197DBE8FBA239DB82BC97A795D5D076487998DED24FB0FBEE4F72F01677FGD0CB8788922AE01B999AGG9CD1GGD0CB818294G94G88G88G5C0171B4922AE01B999AGG9CD1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB85
	86GGGG81G81GBAGGGD39AGGGG
**end of data**/
}
}
