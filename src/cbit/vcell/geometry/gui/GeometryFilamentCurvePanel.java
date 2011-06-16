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
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.*;
/**
 * Insert the type's description here.
 * Creation date: (8/11/00 6:18:51 PM)
 * @author: 
 */
public class GeometryFilamentCurvePanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.UndoableEditListener {
	private Geometry fieldGeometry = null;
	private Curve fieldCurve = null;
	private Curve ivjCurve1 = null;
	private Geometry ivjGeometry1 = null;
	private javax.swing.JCheckBox ivjCurveClosedCheckBox = null;
	private javax.swing.JLabel ivjCurveTypeLabel = null;
	private javax.swing.JLabel ivjCurveTypeLabelValue = null;
	private javax.swing.JLabel ivjFilamentNameLabel = null;
	private javax.swing.JButton ivjFilamentNameSetButton = null;
	private javax.swing.JTextField ivjFilamentNameText = null;
	private javax.swing.JLabel ivjGeometryFilamentLabel = null;
	private javax.swing.JComboBox ivjFilamentComboBox = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.JDialog fieldParentDialog = new javax.swing.JDialog();
	private javax.swing.JLabel ivjJLabel1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.text.Document ivjdocument1 = null;
	private javax.swing.JLabel ivjSelectedFilamentLabel = null;
	private boolean ivjConnPtoP6Aligning = false;
	private boolean ivjConnPtoP8Aligning = false;
	private boolean ivjConnPtoP9Aligning = false;
	private Curve ivjCurve2 = null;
	private Curve ivjCurve3 = null;
	private Geometry ivjGeometry2 = null;
	private VCellDrawable fieldVcellDrawable = null;
	private javax.swing.JCheckBox ivjClosedButton2 = null;
	private FilamentGroup ivjfilamentGroup1 = null;
	private GeometrySpec ivjGeometrySpec = null;

/**
 * BeanTest3 constructor comment.
 */
public GeometryFilamentCurvePanel() {
	super();
	initialize();
}

/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getFilamentNameSetButton()) 
		connEtoM4(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (GeometryFilamentCurvePanel.curve --> GeometryFilamentCurvePanel.connEtoM4_Value(Ljava.lang.Object;)Ljava.lang.String;)
 * @return java.lang.String
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.String connEtoC1(java.beans.PropertyChangeEvent arg1) {
	String connEtoC1Result = null;
	try {
		// user code begin {1}
		// user code end
		connEtoC1Result = this.connEtoM4_Value(this.getCurve());
		connEtoM3(connEtoC1Result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoC1Result;
}

/**
 * connEtoC2:  (GeometryFilamentCurvePanel.curve --> GeometryFilamentCurvePanel.resetComboBox(Lcbit.vcell.geometry.Curve;Lcbit.vcell.geometry.Geometry;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getCurve1() != null) || (getGeometry1() != null)) {
			this.resetComboBox(getCurve1(), getGeometry1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void connEtoC2_NormalResult() {
	getFilamentNameText().setText("hello");
}


/**
 * Comment
 */
public String connEtoC2_NormalResult(Filament fil) {
	return fil.getName();
}


/**
 * connEtoC3:  ( (FilamentNameSetButton,action.actionPerformed(java.awt.event.ActionEvent) --> filamentGroup1,addCurve(Ljava.lang.String;Lcbit.vcell.geometry.Curve;)V).exceptionOccurred --> GeometryFilamentCurvePanel.showErrorDialog(Ljava.lang.Throwable;)V)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		this.showErrorDialog(exception);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (CurveClosedCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> GeometryFilamentCurvePanel.curveClosedCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;Lcbit.vcell.geometry.Curve;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getCurve2() != null)) {
			this.curveClosedCheckBox_ItemStateChanged(arg1, getCurve2());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (document1.undoableEdit. --> GeometryFilamentCurvePanel.document1_UndoableEdit(Ljava.lang.String;Ljava.lang.String;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.document1_UndoableEdit(getSelectedFilamentLabel().getText(), getFilamentNameText().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (GeometryFilamentCurvePanel.geometry --> GeometryFilamentCurvePanel.resetComboBox(Lcbit.vcell.geometry.Curve;Lcbit.vcell.geometry.Geometry;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getCurve1() != null) || (getGeometry1() != null)) {
			this.resetComboBox(getCurve1(), getGeometry1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  ( (FilamentNameSetButton,action.actionPerformed(java.awt.event.ActionEvent) --> filamentGroup1,addCurve(Ljava.lang.String;Lcbit.vcell.geometry.Curve;)V).normalResult --> GeometryFilamentCurvePanel.resetComboBox(Lcbit.vcell.geometry.Curve;Lcbit.vcell.geometry.Geometry;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		if ((getCurve3() != null) || (getGeometry2() != null)) {
			this.resetComboBox(getCurve3(), getGeometry2());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (GeometryFilamentCurvePanel.curve --> CurveClosedButton2.enabled)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getCurve2() != null)) {
			getClosedButton2().setSelected(getCurve2().isClosed());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM13:  (FilamentComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> FilamentNameText.text)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getFilamentNameText().setText((String)getFilamentComboBox().getSelectedItem());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (Geometry2.this --> GeometrySpec.this)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(Geometry value) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometry2() != null)) {
			setGeometrySpec(getGeometry2().getGeometrySpec());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  ( (Curve1,this --> GeometryFilamentCurvePanel,connEtoM4_Value(Ljava.lang.Object;)Ljava.lang.String;).normalResult --> CurveTypeLabelValue.text)
 * @param result java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.lang.String result) {
	try {
		// user code begin {1}
		// user code end
		getCurveTypeLabelValue().setText(result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (FilamentNameSetButton.action.actionPerformed(java.awt.event.ActionEvent) --> filamentGroup1.addCurve(Ljava.lang.String;Lcbit.vcell.geometry.Curve;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getCurve3() != null)) {
			getfilamentGroup1().addCurve(getFilamentNameText().getText(), getCurve3());
		}
		connEtoC7();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		connEtoC3(ivjExc);
	}
}


/**
 * Comment
 */
public java.lang.String connEtoM4_Value(Object obj) {
	if(obj == null){
		return null;
	}
	String className = obj.getClass().getName();
	int pl = className.lastIndexOf('.');
	return className.substring(pl+1);
}


/**
 * connEtoM5:  (GeometrySpec.this --> filamentGroup1.this)
 * @param value cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(GeometrySpec value) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometrySpec() != null)) {
			setfilamentGroup1(getGeometrySpec().getFilamentGroup());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void connEtoM6_NormalResult() {
	if(getVcellDrawable() != null){
		getVcellDrawable().repaint();
	}
}


/**
 * connPtoP1SetSource:  (GeometryFilamentDialog.curve <--> Curve1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getCurve1() != null)) {
				this.setCurve(getCurve1());
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
 * connPtoP1SetTarget:  (GeometryFilamentDialog.curve <--> Curve1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setCurve1(this.getCurve());
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
 * connPtoP2SetSource:  (GeometryFilamentDialog.geometry <--> Geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getGeometry1() != null)) {
				this.setGeometry(getGeometry1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (GeometryFilamentDialog.geometry <--> Geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setGeometry1(this.getGeometry());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (FilamentNameText.document <--> document1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getdocument1() != null)) {
				getFilamentNameText().setDocument(getdocument1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP3SetTarget:  (FilamentNameText.document <--> document1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setdocument1(getFilamentNameText().getDocument());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP4SetTarget:  (GeometryFilamentCurvePanel.vcellDrawable <--> vcellDrawable1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		setClosedButton2(getCurveClosedCheckBox());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP6SetSource:  (GeometryFilamentCurvePanel.curve <--> Curve2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getCurve2() != null)) {
				this.setCurve(getCurve2());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP6SetTarget:  (GeometryFilamentCurvePanel.curve <--> Curve2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			setCurve2(this.getCurve());
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP8SetSource:  (GeometryFilamentCurvePanel.curve <--> Curve3.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			if ((getCurve3() != null)) {
				this.setCurve(getCurve3());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP8SetTarget:  (GeometryFilamentCurvePanel.curve <--> Curve3.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			setCurve3(this.getCurve());
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP9SetSource:  (GeometryFilamentCurvePanel.geometry <--> Geometry2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP9SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP9Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP9Aligning = true;
			if ((getGeometry2() != null)) {
				this.setGeometry(getGeometry2());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP9Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP9Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP9SetTarget:  (GeometryFilamentCurvePanel.geometry <--> Geometry2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP9SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP9Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP9Aligning = true;
			setGeometry2(this.getGeometry());
			// user code begin {2}
			// user code end
			ivjConnPtoP9Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP9Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void curveClosedCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent, Curve curve) {
	boolean curveState = curve.isClosed();
	boolean newState = (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED);
	if (curveState != newState) {
		curve.setClosed(newState);
		if (getVcellDrawable() != null) {
			getVcellDrawable().repaint();
		}
	}
}


/**
 * Comment
 */
public void document1_UndoableEdit(String selectedFilamentName,String newFilamentName) {
	if(selectedFilamentName == null){
		getFilamentNameSetButton().setEnabled(false);
		return;
	}
	if(newFilamentName == null || newFilamentName.length() == 0){
		getFilamentNameSetButton().setEnabled(false);
		return;
	}
	if(!selectedFilamentName.equals(newFilamentName)){
		getFilamentNameSetButton().setEnabled(true);
	}else{
		getFilamentNameSetButton().setEnabled(false);
	}
}


/**
 * Comment
 */
public String filament_This(Filament filament) {
	if(filament != null){
		return filament.getName();
	}else{
		return null;
	}
}


/**
 * Comment
 */
public void geometry1_This(Geometry arg1) {
	//Reset Combo box
	Filament[] filaments = arg1.getGeometrySpec().getFilamentGroup().getFilaments();
	java.util.Vector<String> v = new java.util.Vector<String>();
	if(getFilamentComboBox().getItemCount() > 0){//Do this because it doesn't work otherwise
		getFilamentComboBox().removeAllItems();
	}
	for(int c = 0; c< filaments.length;c+= 1){
		String fName = filaments[c].getName();
		if(!v.contains(fName)){
			v.add(fName);
			getFilamentComboBox().addItem(fName);
		}
	}
	
}


/**
 * Return the ClosedButton2 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getClosedButton2() {
	// user code begin {1}
	// user code end
	return ivjClosedButton2;
}


/**
 * Gets the curve property (cbit.vcell.geometry.Curve) value.
 * @return The curve property value.
 * @see #setCurve
 */
public cbit.vcell.geometry.Curve getCurve() {
	return fieldCurve;
}


/**
 * Return the Curve1 property value.
 * @return cbit.vcell.geometry.Curve
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Curve getCurve1() {
	// user code begin {1}
	// user code end
	return ivjCurve1;
}

/**
 * Return the Curve2 property value.
 * @return cbit.vcell.geometry.Curve
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Curve getCurve2() {
	// user code begin {1}
	// user code end
	return ivjCurve2;
}

/**
 * Return the Curve3 property value.
 * @return cbit.vcell.geometry.Curve
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Curve getCurve3() {
	// user code begin {1}
	// user code end
	return ivjCurve3;
}

/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getCurveClosedCheckBox() {
	if (ivjCurveClosedCheckBox == null) {
		try {
			ivjCurveClosedCheckBox = new javax.swing.JCheckBox();
			ivjCurveClosedCheckBox.setName("CurveClosedCheckBox");
			ivjCurveClosedCheckBox.setText("Closed");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurveClosedCheckBox;
}

/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCurveTypeLabel() {
	if (ivjCurveTypeLabel == null) {
		try {
			ivjCurveTypeLabel = new javax.swing.JLabel();
			ivjCurveTypeLabel.setName("CurveTypeLabel");
			ivjCurveTypeLabel.setText("Curve Type");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurveTypeLabel;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCurveTypeLabelValue() {
	if (ivjCurveTypeLabelValue == null) {
		try {
			ivjCurveTypeLabelValue = new javax.swing.JLabel();
			ivjCurveTypeLabelValue.setName("CurveTypeLabelValue");
			ivjCurveTypeLabelValue.setText("Sampled");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurveTypeLabelValue;
}

/**
 * Return the document1 property value.
 * @return javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.text.Document getdocument1() {
	// user code begin {1}
	// user code end
	return ivjdocument1;
}


/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFilamentComboBox() {
	if (ivjFilamentComboBox == null) {
		try {
			ivjFilamentComboBox = new javax.swing.JComboBox();
			ivjFilamentComboBox.setName("FilamentComboBox");
			ivjFilamentComboBox.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilamentComboBox;
}

/**
 * Return the filamentGroup1 property value.
 * @return cbit.vcell.geometry.FilamentGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.FilamentGroup getfilamentGroup1() {
	// user code begin {1}
	// user code end
	return ivjfilamentGroup1;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFilamentNameLabel() {
	if (ivjFilamentNameLabel == null) {
		try {
			ivjFilamentNameLabel = new javax.swing.JLabel();
			ivjFilamentNameLabel.setName("FilamentNameLabel");
			ivjFilamentNameLabel.setText("New Filament Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilamentNameLabel;
}

/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getFilamentNameSetButton() {
	if (ivjFilamentNameSetButton == null) {
		try {
			ivjFilamentNameSetButton = new javax.swing.JButton();
			ivjFilamentNameSetButton.setName("FilamentNameSetButton");
			ivjFilamentNameSetButton.setText("Set");
			ivjFilamentNameSetButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilamentNameSetButton;
}

/**
 * Return the JLabel5 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFilamentNameText() {
	if (ivjFilamentNameText == null) {
		try {
			ivjFilamentNameText = new javax.swing.JTextField();
			ivjFilamentNameText.setName("FilamentNameText");
			ivjFilamentNameText.setText("Selected Filament Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilamentNameText;
}

/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
}


/**
 * Return the Geometry1 property value.
 * @return cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Geometry getGeometry1() {
	// user code begin {1}
	// user code end
	return ivjGeometry1;
}

/**
 * Return the Geometry2 property value.
 * @return cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Geometry getGeometry2() {
	// user code begin {1}
	// user code end
	return ivjGeometry2;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGeometryFilamentLabel() {
	if (ivjGeometryFilamentLabel == null) {
		try {
			ivjGeometryFilamentLabel = new javax.swing.JLabel();
			ivjGeometryFilamentLabel.setName("GeometryFilamentLabel");
			ivjGeometryFilamentLabel.setText("Defined Filaments");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryFilamentLabel;
}

/**
 * Return the GeometrySpec property value.
 * @return cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.GeometrySpec getGeometrySpec() {
	// user code begin {1}
	// user code end
	return ivjGeometrySpec;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Selected Filament");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Gets the parentDialog property (javax.swing.JDialog) value.
 * @return The parentDialog property value.
 * @see #setParentDialog
 */
public javax.swing.JDialog getParentDialog() {
	return fieldParentDialog;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectedFilamentLabel() {
	if (ivjSelectedFilamentLabel == null) {
		try {
			ivjSelectedFilamentLabel = new javax.swing.JLabel();
			ivjSelectedFilamentLabel.setName("SelectedFilamentLabel");
			ivjSelectedFilamentLabel.setText("Selected Curve Filament Name");
			ivjSelectedFilamentLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectedFilamentLabel;
}

/**
 * Gets the vcellDrawable property (cbit.vcell.geometry.VCellDrawable) value.
 * @return The vcellDrawable property value.
 * @see #setVcellDrawable
 */
public VCellDrawable getVcellDrawable() {
	return fieldVcellDrawable;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
	 System.exit(1);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(this);
	getFilamentComboBox().addItemListener(this);
	getFilamentNameText().addPropertyChangeListener(this);
	getCurveClosedCheckBox().addItemListener(this);
	getFilamentNameSetButton().addActionListener(this);
	connPtoP2SetTarget();
	connPtoP9SetTarget();
	connPtoP1SetTarget();
	connPtoP3SetTarget();
	connPtoP6SetTarget();
	connPtoP8SetTarget();
	connPtoP4SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BeanTest3");
		setLayout(new java.awt.GridBagLayout());
		setSize(359, 147);

		java.awt.GridBagConstraints constraintsFilamentNameLabel = new java.awt.GridBagConstraints();
		constraintsFilamentNameLabel.gridx = 0; constraintsFilamentNameLabel.gridy = 2;
		constraintsFilamentNameLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsFilamentNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getFilamentNameLabel(), constraintsFilamentNameLabel);

		java.awt.GridBagConstraints constraintsGeometryFilamentLabel = new java.awt.GridBagConstraints();
		constraintsGeometryFilamentLabel.gridx = 0; constraintsGeometryFilamentLabel.gridy = 1;
		constraintsGeometryFilamentLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsGeometryFilamentLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getGeometryFilamentLabel(), constraintsGeometryFilamentLabel);

		java.awt.GridBagConstraints constraintsCurveTypeLabel = new java.awt.GridBagConstraints();
		constraintsCurveTypeLabel.gridx = 0; constraintsCurveTypeLabel.gridy = 3;
		constraintsCurveTypeLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsCurveTypeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getCurveTypeLabel(), constraintsCurveTypeLabel);

		java.awt.GridBagConstraints constraintsCurveTypeLabelValue = new java.awt.GridBagConstraints();
		constraintsCurveTypeLabelValue.gridx = 1; constraintsCurveTypeLabelValue.gridy = 3;
		constraintsCurveTypeLabelValue.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCurveTypeLabelValue.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getCurveTypeLabelValue(), constraintsCurveTypeLabelValue);

		java.awt.GridBagConstraints constraintsCurveClosedCheckBox = new java.awt.GridBagConstraints();
		constraintsCurveClosedCheckBox.gridx = 2; constraintsCurveClosedCheckBox.gridy = 3;
		constraintsCurveClosedCheckBox.anchor = java.awt.GridBagConstraints.EAST;
		constraintsCurveClosedCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getCurveClosedCheckBox(), constraintsCurveClosedCheckBox);

		java.awt.GridBagConstraints constraintsFilamentComboBox = new java.awt.GridBagConstraints();
		constraintsFilamentComboBox.gridx = 1; constraintsFilamentComboBox.gridy = 1;
		constraintsFilamentComboBox.gridwidth = 2;
		constraintsFilamentComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsFilamentComboBox.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsFilamentComboBox.weightx = 1.0;
		constraintsFilamentComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getFilamentComboBox(), constraintsFilamentComboBox);

		java.awt.GridBagConstraints constraintsFilamentNameText = new java.awt.GridBagConstraints();
		constraintsFilamentNameText.gridx = 1; constraintsFilamentNameText.gridy = 2;
		constraintsFilamentNameText.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsFilamentNameText.anchor = java.awt.GridBagConstraints.WEST;
		constraintsFilamentNameText.weightx = 1.0;
		constraintsFilamentNameText.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getFilamentNameText(), constraintsFilamentNameText);

		java.awt.GridBagConstraints constraintsFilamentNameSetButton = new java.awt.GridBagConstraints();
		constraintsFilamentNameSetButton.gridx = 2; constraintsFilamentNameSetButton.gridy = 2;
		constraintsFilamentNameSetButton.anchor = java.awt.GridBagConstraints.EAST;
		constraintsFilamentNameSetButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getFilamentNameSetButton(), constraintsFilamentNameSetButton);

		java.awt.GridBagConstraints constraintsSelectedFilamentLabel = new java.awt.GridBagConstraints();
		constraintsSelectedFilamentLabel.gridx = 1; constraintsSelectedFilamentLabel.gridy = 0;
		constraintsSelectedFilamentLabel.gridwidth = 2;
		constraintsSelectedFilamentLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSelectedFilamentLabel(), constraintsSelectedFilamentLabel);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getFilamentComboBox()) 
		connEtoM13(e);
	if (e.getSource() == getCurveClosedCheckBox()) 
		connEtoC4(e);
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
		GeometryFilamentCurvePanel aGeometryFilamentCurvePanel;
		aGeometryFilamentCurvePanel = new GeometryFilamentCurvePanel();
		frame.setContentPane(aGeometryFilamentCurvePanel);
		frame.setSize(aGeometryFilamentCurvePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("geometry"))) 
		connPtoP2SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("geometry"))) 
		connPtoP9SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("curve"))) 
		connPtoP1SetTarget();
	if (evt.getSource() == getFilamentNameText() && (evt.getPropertyName().equals("document"))) 
		connPtoP3SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("curve"))) 
		connPtoP6SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("curve"))) 
		connPtoP8SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("curve"))) 
		connEtoM1(evt);
	if (evt.getSource() == this && (evt.getPropertyName().equals("curve"))) 
		connEtoC1(evt);
	if (evt.getSource() == this && (evt.getPropertyName().equals("curve"))) 
		connEtoC2(evt);
	if (evt.getSource() == this && (evt.getPropertyName().equals("geometry"))) 
		connEtoC6(evt);
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
public void resetComboBox(cbit.vcell.geometry.Curve curve, cbit.vcell.geometry.Geometry geom) {
	if(geom == null){
		return;
	}
	if (getFilamentComboBox().getItemCount() > 0) { //Swing Bug
		getFilamentComboBox().removeAllItems();
	}
	String[] filamentNames = (geom != null ? geom.getGeometrySpec().getFilamentGroup().getFilamentNames() : new String[0]);
	if (filamentNames.length > 0) {
		for (int c = 0; c < filamentNames.length; c += 1) {
			getFilamentComboBox().addItem(filamentNames[c]);
		}
		getFilamentComboBox().setSelectedIndex(0);
	}
	String selectedCurveName = ((geom != null && curve != null) ? geom.getGeometrySpec().getFilamentGroup().getFilamentName(curve) : null);
	if (selectedCurveName != null) {
		getSelectedFilamentLabel().setText(selectedCurveName);
		getFilamentNameText().setEnabled(true);
		getFilamentComboBox().setSelectedItem(selectedCurveName);
		getCurveClosedCheckBox().setEnabled(true);
	} else {
		getSelectedFilamentLabel().setText(null);
		getFilamentNameText().setText(null);
		getFilamentNameText().setEnabled(false);
		getCurveClosedCheckBox().setSelected(false);
		getCurveClosedCheckBox().setEnabled(false);
	}
	getGeometryFilamentLabel().setText("Defined Filaments("+filamentNames.length+")");
	getCurveTypeLabel().setText("Curves("+geom.getGeometrySpec().getFilamentGroup().getCurveCount()+") - Type");
}


/**
 * Set the ClosedButton2 to a new value.
 * @param newValue javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setClosedButton2(javax.swing.JCheckBox newValue) {
	if (ivjClosedButton2 != newValue) {
		try {
			ivjClosedButton2 = newValue;
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
 * Sets the curve property (cbit.vcell.geometry.Curve) value.
 * @param curve The new value for the property.
 * @see #getCurve
 */
public void setCurve(cbit.vcell.geometry.Curve curve) {
	cbit.vcell.geometry.Curve oldValue = fieldCurve;
	fieldCurve = curve;
	firePropertyChange("curve", oldValue, curve);
}


/**
 * Set the Curve1 to a new value.
 * @param newValue cbit.vcell.geometry.Curve
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCurve1(cbit.vcell.geometry.Curve newValue) {
	if (ivjCurve1 != newValue) {
		try {
			cbit.vcell.geometry.Curve oldValue = getCurve1();
			ivjCurve1 = newValue;
			connPtoP1SetSource();
			firePropertyChange("curve", oldValue, newValue);
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
 * Set the Curve2 to a new value.
 * @param newValue cbit.vcell.geometry.Curve
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCurve2(cbit.vcell.geometry.Curve newValue) {
	if (ivjCurve2 != newValue) {
		try {
			cbit.vcell.geometry.Curve oldValue = getCurve2();
			ivjCurve2 = newValue;
			connPtoP6SetSource();
			firePropertyChange("curve", oldValue, newValue);
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
 * Set the Curve3 to a new value.
 * @param newValue cbit.vcell.geometry.Curve
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCurve3(cbit.vcell.geometry.Curve newValue) {
	if (ivjCurve3 != newValue) {
		try {
			cbit.vcell.geometry.Curve oldValue = getCurve3();
			ivjCurve3 = newValue;
			connPtoP8SetSource();
			firePropertyChange("curve", oldValue, newValue);
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
 * Set the document1 to a new value.
 * @param newValue javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocument1(javax.swing.text.Document newValue) {
	if (ivjdocument1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdocument1 != null) {
				ivjdocument1.removeUndoableEditListener(this);
			}
			ivjdocument1 = newValue;

			/* Listen for events from the new object */
			if (ivjdocument1 != null) {
				ivjdocument1.addUndoableEditListener(this);
			}
			connPtoP3SetSource();
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
 * Set the filamentGroup1 to a new value.
 * @param newValue cbit.vcell.geometry.FilamentGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setfilamentGroup1(cbit.vcell.geometry.FilamentGroup newValue) {
	if (ivjfilamentGroup1 != newValue) {
		try {
			ivjfilamentGroup1 = newValue;
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
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	cbit.vcell.geometry.Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}


/**
 * Set the Geometry1 to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometry1(cbit.vcell.geometry.Geometry newValue) {
	if (ivjGeometry1 != newValue) {
		try {
			cbit.vcell.geometry.Geometry oldValue = getGeometry1();
			ivjGeometry1 = newValue;
			connPtoP2SetSource();
			firePropertyChange("geometry", oldValue, newValue);
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
 * Set the Geometry2 to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometry2(cbit.vcell.geometry.Geometry newValue) {
	if (ivjGeometry2 != newValue) {
		try {
			cbit.vcell.geometry.Geometry oldValue = getGeometry2();
			ivjGeometry2 = newValue;
			connPtoP9SetSource();
			connEtoM2(ivjGeometry2);
			firePropertyChange("geometry", oldValue, newValue);
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
 * Set the GeometrySpec to a new value.
 * @param newValue cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometrySpec(cbit.vcell.geometry.GeometrySpec newValue) {
	if (ivjGeometrySpec != newValue) {
		try {
			ivjGeometrySpec = newValue;
			connEtoM5(ivjGeometrySpec);
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
 * Sets the parentDialog property (javax.swing.JDialog) value.
 * @param parentDialog The new value for the property.
 * @see #getParentDialog
 */
public void setParentDialog(javax.swing.JDialog parentDialog) {
	fieldParentDialog = parentDialog;
}


/**
 * Sets the vcellDrawable property (cbit.vcell.geometry.VCellDrawable) value.
 * @param vcellDrawable The new value for the property.
 * @see #getVcellDrawable
 */
public void setVcellDrawable(VCellDrawable vcellDrawable) {
	VCellDrawable oldValue = fieldVcellDrawable;
	fieldVcellDrawable = vcellDrawable;
	firePropertyChange("vcellDrawable", oldValue, vcellDrawable);
}


/**
 * Comment
 */
public void showErrorDialog(java.lang.Throwable arg1) {
	PopupGenerator.showErrorDialog(this,"Set Filament Error:\n"+arg1.getMessage(), arg1);
}


/**
 * Method to handle events for the UndoableEditListener interface.
 * @param e javax.swing.event.UndoableEditEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void undoableEditHappened(javax.swing.event.UndoableEditEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getdocument1()) 
		connEtoC5();
	// user code begin {2}
	// user code end
}

}
