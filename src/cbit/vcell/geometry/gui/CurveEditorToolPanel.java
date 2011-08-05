/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

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
	D0CB838494G88G88GD9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D4671936312D27B5DBF3EADB13E3F7EDC2DA36A12D5D1A56EDEDEB37EECBD33735142426CBFA584626583A8DE9B8BD1EDDFBFA99A002A22200CF9AC9B4898862830C84D1C1B0029985E100C19038B3F3E766421D07B31727A4596FFF5EBBB3F798C2CE7714F33EF367FE7F777E9F5F7FFF77DE0462A3ABF2B2CD45021019A3503FF319021051A1880DFB7BFE47F0F52FA765893A3F178176889F
	9B4B06EBAB34D9A612728A056F0F67427DG5C9F8BA6656D075FC5C262781A3761C794BDCB212DEB595D07E657736F57191E6D78FEF0EFBEDC8BG0681072E892FA37D63FB8FD07CC20AE710E99204456508663E3D2594178EB43900EB8598A8A774AADC09819F292F23F48377458B990F87CC3B109C1DCC864914F7AB3D16897F13392282697AA63693711C02FBB7GF2BECB98EABD8357060ED157C53BCDD62B476D1222D43BA41FC7D283D3552EB1393E3EF9ACB0AEDDF1482AAFE07179148E51ABA902D0FA9C
	49B59565E2B9023089E446E3DC3393699A065F13GCB0EB3FC2F9B6137406F6C4A243C52CA665F05579B287D563B5F90124FF832F7C7712F361278B5D0297937C5F95AF49077C5CEBB0A79B2E8DB833081A0832038AAA92F9660B09E8379B70E43354926B67BBC9E1F37496F3F2CB88257FD8EC9015F757550C2F11D125D97F08882696735FB2B8C6C198D885FCF5EB79F09C9FC8D6B73044EAF8BA9DF7B0CE93BC1CCD2626F1A4CD5A4960EAAADA6C4777622E83A97D6050E49D05D3D02DEF755D3CFE4659868EE
	7D4193D94B281C841D6E34AA54575F40E3E0BD7C4E24F289FE9D46D71D5070E4FC36E1BC3AEF05360493ECECD88B18AD666C870542531FB50C4332FFDBECAA2CA6B614D7075B52A5A35916EA2273A837450371F1BABC3165B69E2768BE835A8ACFA5651171D5F0934D572A53B0CF810681A68116812C82C8BC4D46D8770D13F39CE39DE2C03DBCA6AB8EA9A0905E0276FDF015A6550058668D2A2257AE750881D934A9D2D020B47B5BD18CECA1FC25F86C5BC147D549AB85C4D576F9C13A8853D88A84894D2B6DFB
	90CDC0422D9659A301820FDFE0F2CF349741D59103EA375FA12A12C6C15A4F3623BE9997FBC0A288005FA4AE67BBD13C5240FF618C446B8C0B43CAAC6FEDA9G6BC5FD3D59679B9D73DB10A742B02293DE42EB47B1785DF0064578DC974245417D9AAE67376DFBF5F2DAE4BBB2DF8CCC31B976EDE7A1DCB7GFDBAC0FECDD2DEF98D9BBFFF30327153326BD1216A09CF9B0E1FB5B0169BEB48383156044F234731FCE46382E8DB813036062D7F6DCEAB9AF3B511738959930ECBB74069A1BFCC8E516146BA728C68
	338CF0B93348E7B27577A46E8FDBD09F26027EEDG2535AC762FF4141131FCD98CCAEC743687E060C98E2DA7081DFDF28B0DAD4ACF3BB18E49908414FFAB292ED781DE87A8813884F0020E3F3F829C91C81EA57202D8DEAD58B2G3090A081A099E09B403133C9F9E6G95E0B940BA00ADG45EF40F882F0GAC86D875866BAB3B3BB053B82F949858DCC26DBEE8E0DF014E0F826A5B0998F1AF207E3E1FDC707F81A4B6DF4231B19ACFECCCED27E3F0A70D4BAE9AABFDDC0ED2FA963F829A5343F33063605FC1AC
	42014C0183B7118F523864D5DBA43F64F5400FA0EDBBF29345042E57CE9F67F995637D303EB743029A64784A1BA8E6222A86E45B18AAF5C92AAAFBDDDAFB8D56959C3379355440F4555FC47D601B00751D53DF3A0956C63B4F6B1570E464AC343D892F4D12A2F94068E65F18D7A5782B1DE89FBBAACD515C70F6670B82DA5B15B114E7485EF025087A5A9F10BDB045D12A922A4E21F57BEC5181DD0CD2E0FE21BD2EGED4567583ABBF1142D3B47267FD9D8BB64B7996DA116BD7CC913FA0E2CF90B4EE96BAE3129
	214B58764BB8C7D0FC10587A668D58508BC6BB55AC4C734EAE34065BC35BCF13366FE0DBF0DBA76A5D0094B8CD66C21130E79F326172182A7A3CD7833EB17FE932DE7E2A9B61634166B5G496758FA791DAE544EF295DDA81BFD0AC23A875A0A6BD08C8BDD45B486DB00B73B0E44300037BF465B8760DE5535EF446D156EE2DD9C93F46D440EC7AC485F20F4E7CC02AD84CA3AEF733E7E974B5E30369E5A6F247D1B96546F0AAEDFBB5EC4BAD7496C3CD276A651D777A6490996383E84788CCC776E8823DBC95B93
	A97D46B7090D046E0DD1C657D8CF682C75C6F4FF64F41314EE09A15D238A23CB28576C0A2493BD0CEEDDBD4967A9E1726A4EA33A43DEC617C165651FA7746567757236F03A0567091C65743AAA046E8BBEC6378170E9G398DC6760D7899DDAB348FB7C423FB698E23DB50C0F261B2C33A351CEEA534A7C615278698DD9A348B972251158779B8G1A3A28F4A91C4E8AB47E28F4CBD4C63784E8622352350CB13A448B24BFD2AF503567A221A3EB418B95E8FD42E75A91F1DC1C2C8ECE409ADBDFFF8D2F87645C7A
	CFF86F09680FDDE467539F6339BEG77AA40620BECAD7B59AEF6BEDDBA9AAF647EB2B9AB5A79BB0E5A11F8D1DB4BD6FDE6A20B2C99653DC8E76E25243C831758FAF136DB3FC7EE8A86FDF699EF4F69DC5C542DDF6B88EEF3778E38BA65C0D0ED0365EA124D5B673A113DE87FCE5174BCF34E15AF10752C8A74BAG16DF427D6DC2B9G4DE162273D067939E47AE1E178538F18F6C671B3759271CFE8547CDCD8FCB60B68CB617A6A205D5F48EA8D4F6F2C9466D4EBE02BB06D4FBFD77F5530BFF52B32404E2E6F3C
	256F2BB53B5F096AC3BC35BDD1674316477EC4FDF8DAE6BE64B6A565B58210FC70F1974A4FE1F904668A77880A276A16034416BFB6B5D2DD6B3F7C2030FA7BE213D1DD437D45E52649A6E2430AA64D16753FFE5A148AC06C7962C8BE9D2F69C0D3FE1959D3B00AF50291A4D6BF7262BC6773BAE557D8C06A112509720A9EE24F03C5FAFB9A5E9BB434E751EE37E978B24D4917B5FBD2FF7E34A963674C1E21D1E6CFB25064B7B3FBFEB00AF20BBF6073CB81E52A195961E0E371304278DA00E70673FDD8C1F3AAA8
	51BC596ED5267878FD12732C827AADG847FF18F439FEB01B3C28B13753CA7CFAF0B4BE971B27A25C03B1E53BF625DA50465FF01657FA73D21FD7A68CB7A98660A9FB10C61624DAED37695923B2AABDA8CDB131FB68DA43398167818BDAA50A4DCE1764CE03C6C15D5D9D464E9293C02746D415B8594CFC6FE90FA167976D3AEEB9348A9FD8B56CE3CCFF87530FE1FE0817C82003837182EB77C287EA762C0D513CB0794F07A829E49D10EE6DB35B821D6633DEFF593EE3E02FED7B03D1F72B33D2BC1E6BA57FB44
	E124377CAA6B3B2BF798DF8360542BEC0D38F893613D20DFD4BA25601822927A4D1CBE81E8B7DDE5FA2C12111E52D62667F38146F78AF06ED646D76FB562DB4179EE867918835C964E17EBB362BB564678F2D446D787B8FF9B637B72B9A33EF84E772F1CEF8D60B2B85F7306F12C3A4678BCE30C2F91F01357D83F5E3A0B662164C55B3716000896CC34D2713D7D24DAE2580E0794BD9346C5548FD7DE536A936BF43F99103A5A257D48260D57C8DDAD0B635BB0BECD07A7BE7CEC9FDBEB37C25B405B2C9674DC9F
	4A63EB61BE85A083A05F8C3E83184DACFFCE745488B1EAEDEDDE95522B28F4059C2AD84D2B976905F3903DBF5D0B62034FC1BC8E97DCA88EEE736C359BBD903E37B15F027741C756180ADB7AFAFF761EEB26AC03B5867787153D562C2DB1A4F6AB7A5138912E43DCG2885B083A857D95C3E5EDB95ABEE2C6EC883C6FC7A75BCE3117E3EE3C17CF871B672E3B1589D8F3096E0A340D66E07E00D597FD7A68D7B1F58750C9C2BBEFF6006D1CD6E7918FC8DE1FC441F01C1340735C01B9FE091C09CC0A240A6005CF6
	66D70775F2AC3F1AD891416263BF1B438B814C5EC7C7426DADEB8F354F584FEFC570D5077B59EE64674F06301F506687D884908710883089A0370379791075C24C7EC3894762EB86DCAB1CC98DDC2436CA237373B19841976EE3F907915F4B4A7C741D0C608B5757E828AFE51E7AFE97D31F55D05F924F7C747DE384DF38BE3F213EEB5E796933C6703D94F63F2843E80C360AE80C2E0036B5G49G69G45B71372CE81D8EE32B11AE1BDBF47B1CAF6C811A31458F958B7BF7FAAA2784263295EB462FBE21E7A1E
	0929EFF118BE32277842A02A839B1C3BADBE178B659134E925B544857B43EB086879CA9C48DD8DC04E6883075159976D4DB75CE4B57C7B7D7373690B91FC61BE55F60672919BCADD488614174D1D4C066BF766E7C3FF84DF388D8BBB0D781E1B273EBF4754375C5067CFCA4C67D55C6707827333612B91FC61B6ECB134610E1759105D45EC688D4E4F86A902AF5C0606AEA39BAE59188D96EE43CEF5FEB69C0C600B18D306B67C669C33A10E5B705DF95A709FB1EDC8B134211C0F078CEEC3F0ECFEB6FCA802AF5C
	06AACBA89FF9D67CADDC2FEB01B6ABG41ADABC1389938DFEAE1F6DD590F4E6B2B79FDBB3EC7E7A6F27F0BFD56B0FDC630BA4C0639825D6B4EC9C734E7ED310064242DFD511FC97D254F7819D44EAD63E7D2BBEF6DA1F81021FFA30135975D1A7D195561DBC64F2C7E0AFDFC52044EF601B12FAAFBA42C16648AE992BDAF044C59B966A5E995E98E061FBD84162B9759D0BFEE30301C7C55DBE704481CCCCAF2CC08C592836DCEA75F9CF779A5BB33B93EBFFA7C3E52EF9C3F477A0D63772D7E5963777D7E5963D7
	F0BB3AADA5370DEDB9FC5B581617EF4FEECB456DE836107E7AAD2E8BC4BCCF3CA1FAA40155448E0D64D11A8E5557F11A4BF68EC67743B3E86CC7F090577DFCE3813B94EE6B7460815E266D295AD449034CA1B477E0FED58C382488DFBF0E79C9DBB47ECF8D227A30CDF64881DAADA4F178513E5DB80E412094441B3B96D195297F1B387FD7D4DF739BDC5FFF8EC5777F57C3467E278DC5777F3921587EEF9D0A6E7FDF06E27B1FBB9CEDBC90DF170F6AFDBD4771D6C50F2F6378A20F9E7FA6477F5033C307BFFD0A
	61BF645551DF5746D22798BD16FD22F1AC07446831F40A31E369932347F2DC0C9D4B774459E359698B0981FF461C6F57478C4500645D557B59B32AAD96760C4AF2882D27473AE13F8CE069E6F9CDBCEC14F3146E68F9EC8134AD553547535FEB603AA10C0F5839A144CE347E52F3B02E6DEC8A6359CA6F73FB12724AFA0C64AD3C2317D743712F857478DA2EE7290474540649EA257783F0759B6A79E5083C33DC5EB8BEB7AE8C63D9C66F93603A56D05EBF84C37A718C1357E2C3721243F852687DB63896769A49
	3B2C06482B616776F3C85E31B01EBAFAEF066B30213CBF054A2BE572CE607829E1BC0B687DF2382EB41477253190F9E799BE29883DB32208D33EB1D590CED9F11D1E3F9B328EE425GE4837E00151C6FC86E7F6A84DAF31D8151835383A7657CBE447B159246BB89740B8192G5683EC31323A7663EEE48BAAD5DF236FA11339375836CBBF77E83E226F876E8F5FF3DA7AB4BF5DFD11F304542BB76239B653C76A553BB9BE8D6363F4F8D2473E057168FEBD34355EE27B54FB70B9F69D5CA7F3DC9A462542FDBA47
	D92690AE3B9F62554FF01F4338AA38B7F35C66A9366F9D6638268904DB89776BB82E4E45F4EC60387B5C4C16CD9C77C7B713174EF15B1CCCDE63G43BDA2B1F9E60E5BA6B3F9439CF7FB0449D3B82ED5E27256F05C3DDEA6AF116386FA183C940E7BF68F131746F1CD17183C3A5B8C57EFE372DAB8AE4E4564D9B9AE077B6B6038F89B13370A63B26B183C359CF777B86FCB0ECBB94E6425F2DC050949BBB548F0ADBCFE8D9C57A8B1F99D9C77F909499B6038FB6DCCDE42A0FB763327183D533F9AF06903ECFEBD
	DB0D66842F4320A7F8757517FD68DDA4B3192CA43F749567D27E72A116DFBE06E58E437DA44012A176EE4064EB6C39FA42979E9436BDE17C8EC42A659DD37C9019CF6B0734E7920267E9935179C4895319BB8CE3EB98613276691FF121F9FE0FC04E05247DECC8BB4A31BF4D54373BC25A113DA5A16D7707342398BDC565935FFE7CAA73ED5B0FD78A4AE38B23FEDF31E8186686FADD32697ECC7F3353D9240F12F13E5E84EDDB07D93F659DE2FDD9AA32DC7D2BC3286E588A778E00990025GAB819281D2B95D9F
	B09DFACFC8206F4F445377ED0669FBBC04EE7810DFE7A31249FE4F084679BFB18C5FCAEDC80DC22F0715140668FEAFC4377E5D6370770C996C8A3B4F227D30BB06DE84BBF5BCC54283077133F95888EE955137BA9BE401377D8537491034165B703376D2548F8D703B5546F249A8DEC749FB82120350FDF80AFD4F124A69D6CD617766FDDE95B621CD81C9FCCBD69474CA03C0F8FE42F952613E560EF042F48B35D530C746B413C29605E220C1D0C27165F6B26F7238BF568914EBBD307F143D3037427E5E9C9538
	8DA2DE67E600EF91GE9D79554CE6C491319BD498676E44530A74DCEF29D6ADFA2BB4E2369CE466BD58650888E267B07DECD37BA42F41B9D113AF3E2689EF6905D19DC77993F267B6BB807A8C0B349F5F75F51F4A777B05D6B8DF46B759A69CEF5E87DC0E45F9B50F4AF44B9E38B524BF52F8CEA3ACB5DCC37C50A543DBD06EE37942EFB332A69BE0EF3138AB48BA426BBFFDC537D42F15E5F863A33E268CE0B50FDF2C2536DA17D8DB40213696E1C54F42F16385FCE637E1E55EFE7386EB174AA90DDEF76974F6F
	BB2CD76749B7783E79489E312F8465D155290DDD0237E17C829D1E6472FBF9AEDF8CED69CE768E04F59A6D09ADAED87F8196812CGD88B1062E23530E2BB9C27E7FFBEE571794DF018D6F4A5A1625FAFCB6677FD5A7736331C396A1BDF92D6FF34A56AFBF669AEB2868A5D64DA7E4C7D1968CA7C7B7DDD64DFA3348DGACGD88610G304E4D7C9B3053F795D0F92FC9D5C53B9B3F8D490B0532B421215101022C193F5F9FF9BEB19DE0E70C0DA0BF9D60001C14D7A51377EA2CB222A9BBD4B60B7FADB2710368B8
	0A5F9D6B42B9649AFD42455ADE48C4FD5A61133D2C09601FEDC131B24BDE09F1907CF7F7E2D9FEC55742E45D1B05E4B5B99C4D9DA132DE9BC232EE79FC1ED0D9E6BFBAA74191098BA27D7653FE7D3BA5058FA8A6B68E437DDCC17DCC1035FD9C190F2F3FA7F03BAECD354255ABCD14699E79D537F9C417548699339C15CBD001323C229AFEF6CA2482D37E4AC9991184CF126CFB265AA5BBDF2928A0B62E18505BD8376343065F8EA0C82536E5506B361287B209DD77CF613DC1E0CCA2773F6177E49FFAFA1A55D6
	2AC612720E88641B15DCC17BA33E5E07FF933B7EAB1F0D7952C79E92DAFE51E2BA9A45AE478831E75508963BED3E32ACA2F3A0CB0B5F3B6E0B730B5FB4F4B90D9A2B97554B3CDEB4425665C732B5DF0E3F28695DACDF181FDE3F6865EA295E07C7783B5423CC6F756D1A5EEB5D1A5E2BBDD7BF50F871235944068B551F534B748FF07D1E9ECDFFEA2F267FDE570D8F249F6D8283BE258C5B91967F6BEEE647D2EEC7D61FE64757EFE9F6B80F3F75016CC0EB4419B07D47AA197E75DC7FD37D1AFE73EDCDFF257481
	67ADDE25EA2985C47FE609694FD618FE5FA052C9F24DD009765B2D7B7D296239BCAF1BAF909D89FFD39DA4CF095351EB566EE9631A35F75A78B90EBABD7BB3231969685F095DB32D7FCE0C6459CBC3688C5CG7DE2851881D8829027309C3B676EAB824E318679750A4305EA6668EBE415694F389B5D577F3D9B7B9B666C99141FECBE48CD9EA41F61F74C207ED2FDFE0CA4385DB3A8C6815965D689A9FBB6B603777E12932349BEAA75G5BC72DC5E3D0E1B511534368AC9DD693399A109D17C597A90A50E75D87
	D9FD62140755C4FA8EA219EE38DFG304C434E05978E218535D9C26F17329CB1A169F3C46911C6539EA14A3B1B9E129BD2BCDA0E5076C51F9F96381F8F4F20EFCDFB449143271C57401DF65B88E4D10B68627C1F90119F05DE585BF8D99FAB6FD5C61383488E7C4E3EBAC530780BB0BAF7165F556F914DA0EF58BBFB1D005870C39BBA8728C03B84E09540FA00D4002DG793E243CB200DAGB30083E092E0B1408A0015G6BGB67A187DCF8A953358CFBB938F5400A82321CA6D3F94722E669610A778E7371FFE
	939B726EE3B170144F09EFC72F1E2F81F8BA664457695673B900E7F2CEFC9515FA3E454033E2CEFCFF1074FC2B00A7B1869F7DF6F416671CB706E24F7D5EA17CCD934CD94B141FBEBB9B98C2F34967F486A53E4608C3059C57966C7085E5744C0D587E6C01791D67B26649F762607C4E010F6335A3459FFE8E7CB9462779434F018387597A1581ED56BB6C9CA8B94A603A866EB782EC83A88E40F884E88430GA881B6CFDE98AA97224F93D28856268961D9A2A21BF93EA1531F7643E7C514BB7877A8787334AEA0
	743AC80D73EBF7D1FFAE8A90DF88EEB5462D20B87AFC5CB64B73F1DB6CF173FB1BF14EF85E9619B37EE4B34E995BEC7A1CC1F019F614A3DC30FED8B95D0B9C5747F1BB6DF99457ABBBD4B7476F316F2078D689A9647F27430E6C1A40B8AB7BBF9DBADC9FFBE7430E46BE386C7685986FEBF634F1F74A0AC26E0FE39A598BD3014763A436D1760BBA3BEBB92E0F77C50F9C59BF7D323EFF886EFBA311F4BF980924FB39B7126E355EC8BA599DC967F5C752B5D6C652DD290C244BF1C6526DF06A6948DC5D00268C7D
	7D146E376D605FFE1FF977EF2A0374315F91FD9ECDB8E24F23858E63F974D1C764BC7A0443F89E5D67081CC71F21F2837C05A102DF6EC0F58DD20739AEFA617C007625E4DFA63E267B568DCFAB02AF1D5C2561593492487F713A90BC8613A3613F059C40858342EE7E1DDA9B1E9F2963B575D94FBC14E592325B0497091FEDF0A681E35B049C6CCB1B1085FECE62C2327E8F893CA33C0B97603F1F867071289D0E7B324DD3BDB66E2996EDEA35840BFAB50A32039F5AEEFDFFF8357BC7A52F18041CB6E127D31694
	C797BE361905C2FC57A2856D81590F56EFD558EBC6E1BB2908DE57C9321A1FB78BFBB09DBB6EA1714820C233D0249377162CC8DE2D29C057545DE9D60582A259AB29A7617EBC0EF226AA3CD2DDBD5053ACA9CAD933A243109DA0C74981DD89C8950A8C8AC3C8CF11BFCAE890F6B7D1300FAB68D6E5E500D5C8D4A10FD7CB90757EB908AA8995C50B9E2A104B6A9F0876709C843D9C45E92D0E81BD91D95CC0920E4CC1FCF9947191E58AD5B898350401649D1D033232A84AF8ADC29576C454A7907F6BF3909EF5F4
	E805867D685030C8C6E5948574FB3A777475442D9F31C72DA7AE1B20B54F09706F695E457FCFB08D705913EC1F7513B174BFFF221D4B3ABC22222822B25A64F520B35A65A9D5524E6F770CA379C1C6C39B69FB0B63486E919D4F7F81D0CB87887FA93B667A99GG9CD1GGD0CB818294G94G88G88GD9FBB0B67FA93B667A99GG9CD1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB49AGGGG
**end of data**/
}
}
