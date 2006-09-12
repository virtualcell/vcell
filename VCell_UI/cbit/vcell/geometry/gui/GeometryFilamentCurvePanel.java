package cbit.vcell.geometry.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
/**
 * Insert the type's description here.
 * Creation date: (8/11/00 6:18:51 PM)
 * @author: 
 */
public class GeometryFilamentCurvePanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.UndoableEditListener {
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private cbit.vcell.geometry.Curve fieldCurve = null;
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
public String connEtoC2_NormalResult(cbit.vcell.geometry.Filament fil) {
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
private void connEtoM2(cbit.vcell.geometry.Geometry value) {
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
private void connEtoM5(cbit.vcell.geometry.GeometrySpec value) {
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
public String filament_This(cbit.vcell.geometry.Filament filament) {
	if(filament != null){
		return filament.getName();
	}else{
		return null;
	}
}


/**
 * Comment
 */
public void geometry1_This(cbit.vcell.geometry.Geometry arg1) {
	//Reset Combo box
	cbit.vcell.geometry.Filament[] filaments = arg1.getGeometrySpec().getFilamentGroup().getFilaments();
	java.util.Vector v = new java.util.Vector();
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
	cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Set Filament Error:\n"+arg1.getMessage());
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G500171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DD4D5D5565610D92221D2E9D211DA11DADAD2C3C5B3D4D8D6D4B4E366DA43FCE3EB2C3115B35357B8EB18796C6B72C0A571874B94FFAAA6C15188C1109031D0C025A899F9A215A56F6772F8F25F0F77DD8495705B671E334F3B6F3D7BF8783EE92DF12D5D656E337F4FBE7B1EF36E39AFE17DC7314BB28C6B85A1E319407EFD16A188D91D02706AFD477F87F1D14E569CC1736F1FG6B0402AF3360
	9A8BED09AF3666648A2FFE1A8D7771F0DF706B561C0D7077FB426B1FDA2D70C790BDB550765B87FE77E688BD9F231E956AFD27FD95DC57GA4G0EDC33DCC47FB0FB9E4327B1BCC206C1909629B4936DDB996EA85C3783CC81C8F0D17AD4383E23EA3E2A3A0A511D9A93A7ACF9DAB62CA6F2B4B291CA5D1457482E7942BFB2163A092EE455CE4293897749G4479CCE1F59B11DD535CE57B5CE236ABA567AC22A41558C417C3D46473A536EEFBF57591F6F758AE199C22D3296B164F094DA627A8751EA532D71D49
	D6E589C2AD5CE7498477140968CB047B39G0BB97EA59B419F053F5B81FAE57471400E9A66C35B451F8B8BED335FD99B4447A8197A16A0FBFDEC5EE54E5C247607F59B097B92E84B7334669483B483388122G3628F9507571AE389EB4ABF5AE074365BC68F69F122C7257AE2BA8415F55555042F047C50BCB063C20FD7D343DD8471E4100724D3FECBE9A1338F26C7769476F9152E65DE0D821931334380B06B88F0DC512479B932AFB5D19E03A97F8FC7352D777C9C12BDBF8ED4F0AE5BA3A374E5B33E2891313
	2DF8F51FD2C8DF1FB5133EAE023F895F7BA4FFD5FC040A0F5560E90EBE2F668C391F8BED433AB1B75A5620ADCDD913045C329BF563B07E31C8C371B9EAC37DB9FFDB2629796FBEC7F59E61363C246233FB3CF8EA4BEF55BCA577315016F63EB507661723100C593C3E561C8A00A6GABC084C0ACC0FC9F66D87C473B0718E34DA6D9B954ED172C22ACD0DE47368DF095FB9559546874A8A627C5ECB549F613D992BD8223B9370D44406C43F7D14DFD8B68B8A2BAC5592458DDCE108EE3D691E58F250958360E5048
	225AEA34BBC4D060F08BA8B7F25BFBF015CC9E65045BEAD2C4AF85ED0F5EC67A641C2995A402G781B4665266DA4DEF3417F45GCBF99C569690F9DF09B2D40B6A6AA6172B2B5BEDA41E88CD7DA44EC5EA6D48053F0B7AB146B76CA038A8380F6BC7B9078B5E55482937DB0879A679BC0E319EF391DC9301FEAE40A2GE1GEAGC0B1C0ADC09B008290B500F97527B64C2B7A55F78B45AFDE2F1BD7D11063F183ECFC8D780F2FEBD53D4476C55016FD897CGB88A5089B082A08EE08E4022CB784CF85194FB9935
	A0117903778273E9B09E74D50B2BE577A4BEF9AC8E21EB17E03E9CDBB3991F36162C8E4133CE6DDFDF5CDAF6A5B507BC045F8FA10366CD56CEF5AC8F03E7BEC07DB04C1DAB8A7269D8BCE47208B87A0E49B0F0C42BB713283F775AA3195DA27367DFGDFGF413F183F0G208560B4C08F40A500E9GE7813E85388B6084C09DC099C0B940DE00C6004FG76817CC0F29960EB007DG1046020D58A8104AAB888E00AE00865E8F3FD56DBA85BEC5GCC87188B301478FA8554A1G85A086A099E0B1407AABDBF3EA
	G2CG11G71GE9GD9BF037C81B0827481CC81C881D88230B5826286A081C483A482AC84D8F59554BB00D3G91G532F42DC7E3D2D54606DFBED3FDF4E73AE94E86308B15C4F7A628B9637BA962FB5AC56985FB52C8FDBD83F1CE47D34067543E976370575390D5DF731FED8437AF68D4B019E56DFE85B25FF332FEBD88ECD7B89647E27G4D9D3993F3C7DBB3B27F4D3AF6G6C84788400BC13C9AE6D93E85DA1350564CF21C01F75047ED3411BE7184B24BEED93E81EFD4470A49F31AE11FAC5EA5581F6CF72
	300E7D7D813B12BAC856A17BB536ED7A89E2CB56A9DB7E837A782782DA53FB8AC94C45F3309C29975D22538AFFF8D85BC021DA3F697C294345F9A2D43C9B66DBF5B04171F07C3505A497CC0AA25B4D5D0A58A2AA0A5DE973360FD5F5F93A4DEE6FD48DF5CDA8A47D6E6A017996273F25104CD5ACAE27D3D49FAE1C0535472AF3A5D1925545142B5B29D07C9D5B495A32CBBC4F66EAD337AF9748DCCB6AA673BE3ADE1BE9A2B96B166D8EF8E4112923D0B01C4C233EB813270951DC284FE45D198E57D543F13E5333
	8D67BB85976E9212CE3B8DFAF37A7137DFE7100653394130615E798E35E9628EFD5BEFD367EC8C6F21369E7F0268BDA41A1CC618C3168BF4EC3FA78CEFA7FE292B48B2441DBCCCE8F125D946E90D9F933C55E569A6023D78AAB3173187F1534EF3DCB962D654EB656E617C854748F82C13DC9E51FA28DBD1DCCEEF5B191344678E365ABDA23B3A5D5C4EE3DF2F5248EBF10B96BA77B830CBDD53C03C6281D206635C6303A2520E73D6B66928F3C992CD8DE8AB3A1A745FBC73FA967F05403BF4B86D3FB53C3D4902
	6DEE389FF135FF7BE33CBD816ED3835AC5AB36AF007B740076EC915B33C73466640F70EF3F033717C25B510076468EECEF03B6F7C07B6B3CFD98340D8FE8B75930BD855A96EA5AE99CD76E4CA5E3D0BC5BAD42F208ACA03E6263E45D4EF77D5ADA5918216D1BF612B1A3E9569E46EB084E52B3387F12F78D55D7FA8D1D43574375C340D33A37B9DDAC23CB38064E6DD3FC681E6AC23AB4C637649A3AAEC0F9532FA5F4519252E543FDBEC0ADC09FC04C3554671A11044E61C03A74EB69FCAFF7A425AF00EB89573B
	4309F4A3C6D2B931A369BA846D2B08A4F4FF64F47158BE125A39E824D6EF1C8B6906C5523965F83846814C0924FAA95DB9B7524DE7F4EFC4D27D42A82DFD75E711AEF79455D7B40A2E11FC6337D2C63A9A8DDD27060E46AF155399991DB40A2E33060D527AF10D8769E2C65179F0BC531B4C6C23F1E9D3389FCC5E9B6C1AB51A4A23FAEF3C40776BC653F9F5BBDCFBC753382C61FEFCF5916946C35BCC00545154BEF27576473ABE24DB406852C7D33D9557E97D184B69E22E23F378786B289FC41E570FDB7A11AE
	79BAAAA70DDD5F7011F7F2007B91C557826DD1B4CF5DD15A7EAD3C447300510D0F22638369285EBFF33A38A82AAFA9CACFEF1C00F4F323683AE3312EDEB9836916B23A75E3FC75527E28B1A0DD813457G1C9AC3575AB1E334FA05CC9E67B1F40D13BE0666E976D82D5E67B8DD5ED8CAD7BA1666DF6DD82D3FB1D9C8574C68BA47D2FFC70C25FAA95D639B10AE1151350F560B0B381147E5ACDDF3AD7653CB69F6677138B03A7563E89DC2BA9A173FF33A02F1F45F21E69C0D8B39FA75BE3E89691A4651759D29AF
	045EBACEABEFE0B34F83262FC6A357AB2FFE8B4F7BF1F41DC864917A19CC9E1D977CE5A7192B28FB4EE7CC67CC3DA51E9E18EFD5D7FF2959D33ECB5D97A274F346615E710B8E720C9522DBF372GCA23F1DE7342EA5CBB0E6E0A9352DEABDB91ECFF7CE8B435D70A764EEBBA5FB8381CBE432F7E04681C8EED0951787C0E5E295D3FBA687138ACF6F56B0CBDDB6AF6E967BD94F7F457CA014C9FE40F52889353DEFC8EFD330B58CB7656981A6DB53EE5463D79D4501BFDBD6E4DDFF56CA3A1785EFCF535BA53E17B
	3516F20CC154055B05766BEFB2648409C16D755477766B3DB1A89EC8DFC6ED71D8501671501E4AED191A75712036600C88FDFACD15C366CC59B7C0FE82745EC0705B2D6A7B93488BCA97E9C53AC4E8DF42691ED269C87D27F43BC524AB3E917273C62413C4DF3AE7B9DD8C504C65F4ABBB889DF9EED3BAF787522D9A8F4F6771C8F737CAC76AAA25FB5706F4FDC093BF9E63F13B34E350F89C564E9ED9CCC658B0360BC1CE418414B5F66F76A145164CB6B13E5F1EC13BCE011CE8GBABFCDD2478D72D25CEBE708
	CFCA275DC3F3654D0335AC97D26E19A4A42C08B4683DEB693CF33CA1E5824D11A5933C39E23D4D1C598BC06DA86AC2BB6AEF021AFA935A115F91E8472CAE3403723E49F96301EFB1677DD6A2F13128590D7EBEA2A1ED41C42873G299389BEEF5FFA0D4D519369BEF262C414D560A0EBFE3BA2BACA48FFDA9413A256F51A1CB651DAC40ACF0929C7A9D11711A50D505EC07E5AA9D059A776EAE56745D05935B1A87B24C37587961AB42EF7FF280DEB3669AA5D38C63EE6B398E3E8BC7BE23CF1DDB0561C39E4AC46
	7586877A1B8CB4CBE3B0D756FFCE2E6A1AD040B549DAA7217508E4CDA1DA099B541E3EEF511EE1A971C25EFB23F46DD11FD7B7D3BBCE5D6C35E7FA2CB9B3A9966DE9F622BD5301E6614D98039AAB69E7DC1FD15A8DAEC2CB6C5CFACBEB0E7196246DF7113A5C6D343A48A43C418A29AE60B361B78587B4F24ACE1F50D095B529BDA5C12F153440A25DE95B9F085959AA603B2BC96FE97B2166520F3AFE0FD85BE9083C057A9BFF0B576FDE6803C85E8FAF3850EF6156561C2ADB5117AAB769FB13ECAB2339B23ADF
	1BAB6EDBE92EC4F37AF6B7668A15FB2B9B6546814D82CE377CAC133B075E171C7565F31CC53E3C58561C66D8647B1DB5F04C144BC8BB826812E231267DDD467EC987DC69ED5CC60FD6866D177EAD6A5E0728343074AA7A8EA2CD655C67C1791DA0A3164B5924E0EC687DC1052C4BAD6ADA9BED3BC2C19B6683DF6EA464F5DE88346160E6329FC776D39A945757952755551DB6A90E194F00ED7BF786E6850FC552853475A8689AB6894753677FA2F827CBF61824632227DBD2E8FF66DB747AB3E19246D0390872D2
	G17B5996B514203DA3E1A49144FBA997DFB3C433786D677E19CFA01E6A6273B2DAF1C3AC5FD326FBA05B1249B9CF51D22256B102B77F4233F2826E03276B7699D87D0E79CB054583C258FE330886C4E1F0236DB7BB107A95D137D68E39B50C4F13A1201403E1EE2A7FB77328899D767F218DDE0F551A02668B66173125684EA43308134F58E6897EEC79B7EB0601B2BEBAF79764FB717102F8AF85C1C6F7ECB3EBE668968E39C50AC60F417846DF3105594010F61BBE08C5F41EBCA865AC269A6E670718BB4C91C
	4EE07055AD9A10AE9DE8CA6F44DCECAD5466E2671DB497236EC4B9A9863267E5D2B722CA6825D914BBE0AC84A659C1351925996B2B0115E4BF51E4352AFD21D725C367504ECC34FD8A581546ED0B4A746D1704CC5FBA78B74E37BE2EB527A98E79FE4C722D1F4333FC7BF3F69676D38470A446E19D181CC3760045DE0B68A60E9E33C0DA4A24DCB1DF2D8DE43E6769F475B44832CB2E371BA417CD6B3231D3F67510A756FEDE9F97EFC0DDCBC0CF71DDD8BBC6EFC45B2D000B398BED7F6BC65F7EEF4BC33A39C033
	EAAA527DB24F377FBF5E4467E0C05347692E59646B7BE2CE978FB40BB91D3819BC17FD76BB793839F3B31FF7CD03F957B474413185715100CB190632DED571B64D0C153E6B39A7609CCA56F46F7B67BC4D5F8874FCC63FBA6FA81E6EFF06A4CA4537CF77BFC3729B871EA10906365A3B71CCC1EB89D9F3AE067B6CFB20D681D4813481745E03EB3DF93B489E7520E7B69A1D8AAC85CDD20B4F4BGBCBB718909C117F81E5DC7FD221D2B51B8E4DA42BBC3346E3279285F8F6FA6B5A09AFC1C89B087E08100B083FD
	FE3F38B8144FF87604B9CB6DB99F269FD7074977E715AFFF4660E79682795ED2792AAE030F4EA1F7FE27BDF317356ECB8369DB3D39F3A7135DBB437FEC48D8EBF8B647DAAF57E64A37D78CCFDF1D981E3EDF07296F65B0751D6B88CFDFC4C7F87A72EC61695BEE8BCF5FE8DBF8E3A3A6CC3E53C14F86A2A47879C0799CA179527C78E8AD7A210C6C8FAE01365C19B097812887688408G081B09B549D2FCC888F5F68EDF569ADD7C4FBA7F972EE8EFC9D7F8FE560464CB1A2947179E263E65D2A83E793A7A9E8F49
	272F6F0510FCEF786923F3A11BC47AC9E77F96E76C825D43BD368F77F0336F453D5A7E435A3D5A44357D4177A96FE57B1477FA6BEA2C5338025AD0D834D55F86A3CC918F5BC549CAED085C0BB6445E0BF3018F4B896FFC38DF82306ABE48C500A600CE00487BB08F6FAF5E9FAA8F5589B164201AF60189484627A33C7E199A12EF7AFDFA636C168A62DFB234ADGC887D8BF8B66BBGC6005ED9685F0ACFC30EB3FEFCF4A8632C55991E1F5610FCD1337478363A7C79627C6874756D8A60AB713BCF1E25975777AA
	C9DC5320AD9DA0B79E6674G4DG92C0D4BC46753BCF8F8CB1EF6AGF754656A5289A9337525GDB079653D7C2724D0C579B5BFF698872EE2649E446F7B3337B33191EB971F88E70F6F7F8F64E8A49A75C2F4767B99B1E3EABC27265696A5BA707276FC8C83ED2DDFDEF87708DAD37D7FBC26576D1DDFD8981FCC3736F0910FC6D3A7AD6DF884F3FAD81FC9F7A5D776A6AFBA200EFE87E4D8D499765270F0E111396FC6E445D0FE364424570EC989112EF417DFAE3797356E04FE9369F48EC3D228EEDCD67365676
	0557BF5F84707977CF6983FAFC197D6145E6DDC83E233A7A1E8D609B1AFF3F8960AB713BEF5755B7FCA0BC7F22C372756A6AB3DE8ACF1FB9A4DF142E3E8F057062D99C40679F4FB8DDFDAF85708D4D3FD7C272A5696A3BADA3BCFDB3C2724D575557E588CF5F7910FCEFBC20D7AB563610B90ECE2D2007A3852CEB238A71591F7DF32C953BB3433375C0C83E5A1F6B713D95263E1C2CD0FCED3A7A1AB704175BBF86707957C2372E3EB74354A7EC8CB516C6686A0B8E609BDABCA705640B555577E3DEF87A2450FC
	3A7A0AB605272FB2A4DF2AFE7F0529CF589C0AEF112E3E3BC372696BFBB8A41F1020BB775E92665CBBA4DF1E1FBE7A0D60EC75CCCBA9345583D05CA40960BA61FED88256211FB94937F8D360BE99E091C056032DB985G75G6DGFDG31G498FA24F8DAA4FFC38CF8748FB081E67A4E7B1735919DCF22E101CFD2DFA88F9DEF6446A581E6CE73BD637BFED3B86172C53CE200F6197A58436478661D9437C7746FC15D34F56E5FE77E4EF066C4368FD2B37D673F7306F7056E97A593F4D7F5BBEAD6037BEF45DFC
	3AA478B79D9DA57A5FF4C8A57A5FF448A56BA89EE4E83F3045768BA503FF73F1E5C970EFBE9E5F9D5C4E27F76B5B193ADB5F4EF93B8737733FF687337383B5FE7354F73FF237D331BBC4D5B4FD1E0B3D64BB2003E665F83713EEC390699E7FF7BA826EED34FCC65E9536BD04FB985D25FB0440BD8CF29A47A5239023E8120FF5F4700DA1FABE0D5A75870564CA40B7B05F189C5EF3CBB71CF90751B4AB2E66BD5CE7DBD97078BA4B74632B14115C75383AE50B689F430BE5F93C4D3B53C20EBA90F35837C1AA3FE2
	12EDE2C09F0CD479E9DBB07E68B2B2FE4CF62BDDE6DFF630F72BDDDA9F77F15FF7CBDA3C773B16FFC8AB856FD91732F2BE1F5FB7D86DD4205FC6BC2437E795D9C55E7F45C0DB82C0AA40A2006C07311FAB7704FC6759502B7B4E135AB5E62F1EDDC58F33F3418F8756A92A3728BCF89F6FAE576F633D65417B3826BCF49FB714876F63267250FDFC22BC58D824FD77EBC78E76E9076CF2B4D7B159F19541FD1DD1216F6B7D9541FDCD2C886D6B6C0A603E26D4047675458A7DFC9E655466EDB92FC3CFEDD82B56B1
	0FC774281B1B75A64544F8DADC4883B9DD37175B71C1E5703894D46A47E5D7E5703814D5060ECBF5E570389C2C8C9D17AFAB874F01B56A39BEEF8E146DE13F9D223BCF514C4607952E3DFEE3054ACB7317D7CE71D34FEA7B2112533BE5AD3E0A63ABBD1ABE785ADBEF7E6C714D59B2062F3F206DB3AF7DDBDA7C57247F29FF27F45F6BC7B21FE2F56AG15F76FC52D3D95DC5E70BABDF971CC4E1C2072FA7A34F6D7F0FCF53F2F1F158C1FB5202577FE83784800AF7D9E069FF94997DF4E70A7842D9CEFFF2C4970
	257F0C61677B6177B17CA403B63E5EF80CAD540BC79A0B439BF04DCA544B1F0ECCDFBDFB993EA24BAF2FCAA97E6A8DDABF3C5FEC6E5C2845FF46714FEC7413434EEBDD1F67072F2078531B347E6D754A5F6CC73F176297EE5652FB7BE76A96DF3FAAF03C1CA1E7CDA553F9D737A2889109E42D70977EEDEC9E5C17823481C80974BC9C1DAF5D5BC364F548A6878CE7F5A2537E8861FDD0C25E9420DFGA0GBE9F207691FE5E35015484BC71CFEA057A32045BFAE095198B797F0A12AF4D249222C3BD9BE7BC6FC6
	8134ED06713D00B6758CACEB5FAF70F65F731E2C7D3FBF4F555807AFE9E95BF31BF37DECA7AFD0E95B3C7A777D5ADAC851E47BC1146643AAC203070375FDDBFE1C64B0FD03D4464E5627BA754E5746BD02E797DF91745A93F97B618C3D76D45E5EE156EBDF78884EFBCEFEC61E833C0E33F9A9DDDBF66D4335E57AA3385E7CFB85419D7DC5EB4E29DF70EF032A02BF3FA6D669BF3FEE2B52DF875CD9B578BAE0C6D530678E35AFCEFD4F6D865B067D82EDDEDD09FEC4F25C4B6A3B3BB438DF4CF10787383F9CD752
	2E7A7BCBD8B77F92F19E751D068277919CB757023CD19CF7F39D52AD60387F2DC33A459CF7173A17D8138477C908DB339D695AB8EE0FEAF39C5CA7A6617943AD6A37A36AB7D3004BFE9447F1D93B4F39GF202353A7A08EC379EB2591A542AC077B1ADDD590C3F76D17C3EEC0CAA33976E23G629E45B38B3D5B714C4274BBA689292F68FFDF364058EFC8FA145660050FEA3E1759E54E24BAFFA7214E7C47403F4754BEF2E873140C179D99F45F0E36FF6153CEF2E01FCF7BD91FF6E26F7386ED7BE41FF692231D
	0C1F7A76CCA17A3674191902F4FFC4505F160BFD0C7A144C2E794FCD48C89E5F3C1C7A3642056F1016C2BB45ED4DC0DC4563086B67B80963EEB6A3EE8A608A12896E4AFD75CCEF5A637A4F7ACC81772E617987B4D94974F77E88AEAF191E8B4D60B5A8D79D9FA22CDF4932F7182AE347DE6C8FA9191F2D5D0B670C1313496F7FFDDCCB22C4EDAA28C41B32E76B5B34CCE35B56595426ED8CDFBA5B5F2674FEDFDD5F9D746A7AE1GF5558451152151578CB4D60077993EB7C0D7C33B7ACD22CB96ED322B5BE99584
	9A039F54F19E8174D133B18655AEDF3BA69D725A753A856D1A99442E951A98A4834D7C59B4866AF7EF9A3B286C59F5DE596F57236CF49D594B7CE44FE73237B0F92B1E7077B92950570F3F56FBF595EEC7DDF9CF686B5276E5B15090C0DD35813A9A7B09ED2213E4171545363D9F732B69895CBB7CE74833E17E405E31044957F959FC74FC35C57D5E2EDD63A745CBAADE5260E9BD3E115763DEE8CBFB824FDA4DEFA073275AA761398920GC482CC871873A4BEC79B76158AA176A00CAEF793BCE8A54D23117A37
	B06499B6FF20F56F5195D877629F1AAAA48C2F8FFA8EAD6DC91A9FCB59B5A1E5C28639D27F3A0E907F0A6734669C8510GA2G46834C1C037E0D5A474ED8136D33030AE232F42ADF8B734DB83B5863C5FB4F4C2F52BD4313BC075AB1FFCEE05E329C53FD673818518BCF85633BD217EFBD23AF8A4AB7492C47D743681BF578E85D1F454F87DA0166A3016E95E78B786F0DAF82F1D8B79A49E7C22CAFDFDE05FD39F55AE4217E25FA83569BFF3DB14C1645CFF91F4F6D160AF768987EFCAF312543A4F9C4AEBB36C7
	FB8E3CEA65153A3F19C32060E9AA330ADDA35F1E10C1E5BC7F3DD6C66ECD12A1A3088CB7636DFB5AEB9F0D5509CA0C554C2775E3C56372857959B3965F51834833C817077E1615FEFCFB1A3CFF77FEC37E26B1EAD267557C6320BA176412CB4C124952451E79D654197F4C656A1CF84CEB7B61FA145386F2E83B61DF5E76566D589E95C48F65A9BC6E353D27DF5B3FB4F76DE419023FC726797B662EB61D7E79F781557140CF2A03561E179B022FD196B5682FD17E54203FC6D952B078BB13258D415F19E49E567E
	CE962D1DFFBDCEF2E98E745FC2006C14561C2DG25A9D8B7479CA63FB9C0CFFB7B574C862B0D6C7111DFC7D578BB00A341FD3DF8A474EF2AC69CA1F3A4338B96388EA21F7FBE589152DF0A4B2DA2A96E3AA3A4C6325D5629D0D2063F7E8849C1C96CD051F47D324D056B212694DC53FC6E666B9C0E2B4AC03AB80E1B1001F471FEB8C21775AC627E4169F2B96EC1B32E755AB9EE0108F45233B8A7C970F93710887854E7877FF6013DB3B212B954C220951E03F9BBC08DC0B3C0A7C09FC094409400C40039G0BG
	168264BE5F1A138FD081F074F96C6F510D64F77ADACDE7825FB68112AD6254CE17CDF6526D4C0E87CF901FCE011CDEAE6B4546CF861155AC3B5C222C1C27D83A17C2E54513CFC6D9DFEE7932B0188C4840AF2186528F0F0DA69B9F535FD5EAE3B99E6C197E7C603124FC63FC4EF9A5814F5CA1717D532A65DB84BCCB074477D53D16AFB7957AAFF5A8FC5FEF5772D581CFD388BE9A4B370E879F1BD9AD2147666A963AFE3329DBE09477EE8B596BF3F5F4F8C4DE83B63664F2DC232759653113BDFC7AACD8CF4AAD
	1B7F1FF0637C3FDD47FE6D5A0A00126A3B5698116A3F0E29794EABFBE6864A0E8EA2BBD383F1FE329383E41F7E5EABFB1D81E54F89A2FB30B5D2DA006CABCFF9E57FCDC4590B0248565AEE75DBF3AE5548263566F9F5FE177D8245337DA295174FF06CFD2FF110773D46503951E3542F5B7D46403AFD25D13FEE0FB0EA6BB61B379BC98E58206E34F13A319C77AD475D284AA43813F62B524971B72BF6917CD111A8C47CE4552E9E9557066F88B538EF996EEEA349EFF03953A5A36FFDC632266D30CB923DFFC025
	31BBA15DF9BC9ED6ED343BCD9A3B13B84EEB77AFCE10FE654FF3DA86D95B6C936BD539B0FCCC127D027A13C7742BEE5A0FE7AB837B76DC2536EFA9EE6AC0A05D0C01C03AAAEBA05D81EBA01D39BE104ED69FC8A7EF8F246B592E2523734861EE1CC7563CB058BC72F672D3410C67E53E07258C4AB367C7AF4FA18372CCFF8167332F1F763697F5E07BC25EDEED92842CC3A675B7C3D9FF28E9C671DBFBD7FB7118E62CD60F747936E26B2FE0EE752B215478D7FD5601754053B6A43EA3BEE7CFA56099B6A43ECFBB
	34FC51409397020F1D81F38776711D6E40DCF8ABA3104EE088247B5190C8E751212B6F8824EB6C5052517F5FC86D511186686FBF8A4B94E1833EA24947F7A479641D03A2EC658D6A8B9C7F6695ADA607DB922D0A301C3E5BD104CC18F8FBE4E1ADDF04B52A23FF4139726A4C5FCF4EB488D90D42F2DA8D9B05ACD2529A05E5EA45EB94B2219A76122F7B333460B9AB8C28F9B6942CAC2C71DD7EE55C3E71E7395715F79B046C4342F2D8DE39BBCD20D17D6E3DC948B12B2AC599E895A1D77D45045D403BDB655D5F
	A498AC9EC5586245DB881E363638CD9691C85E311A64AED582B1E9657742BB9D324D2CAAD0844377073F87356A9DE9DE55A42CD4BA4962006C7CA942C62F68E6B2EDE48D7B55B088EF833855FF179584DD2CA22C7BC66C716F41F575E2075D2969F8F0F7A56DEBB21581020054787F1B220F45EF5BAFE977A3522F32845D53906651F138FE1EF7BF024E1337FCC5723D997052BC7E7FD579127CF6F63079F20B43A4C90AC96ABA683412397321730A68DDBB66FFC564FB1006B552369DDF913B4FE8F87E8FD0CB87
	88314A66135B9FGGACE8GGD0CB818294G94G88G88G500171B4314A66135B9FGGACE8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG95A0GGGG
**end of data**/
}
}