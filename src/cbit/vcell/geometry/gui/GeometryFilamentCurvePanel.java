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
	D0CB838494G88G88GC9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DD4945739264144C4131216761954B6541804A4B6252F2631FDA6A5E9CEEB935ADA8EC9C9232FA6210DF935ADC9FCBD2671B44332482EC145C89293D1C9C2A288628FA0ABA0A8A002002E0202C8A28BBB404242AE33337C9854775D39773BBB3BBB4B6A3E661CF24E676EFC7F5FF73FFB675EBBF7D6A1F3C7E4EA32AED39012D38576D71AAC887AFEC1B8DEB43784F1261DF1C6C17577A140A66127
	A77570D98FB46B2DF1468C613AA283DCF742F56C4CB863FB70FD33F0C311558ADF825829G5A4BF3DF6E185ACE4CCE341326DC3FB4128E1F9BG3AGC7BE676FA4765F9C594270E18C0F102C930470DC4273011445F0C940E3GDC93C0D7AE659F07CF6A4074B293636B3CADCAD8752424DBCF7428F4A2A4B059B56CB3DB78C7723CBCE22BC6CAE7B245F0ED87A04127886B5B096E8A73E0DFE5CF37CDAE9C6D916D7642BE51B1A44A52C4E11F5BD6D6F60CDD55596C16A1F1D82EF1CB232259B2AC5A33F7915DB3
	A50322CB90D6005E798A2E2D0F58334277C90008DD08970788BE893E9B72630C59799863211D95AC0676AF9E9662778F69B786085114CFE36B4A774418BD34CC773E529EC29E497B9C2045G44832C8658D290E7B481ECD56AA0ADF797FC9E6916EB9CC3C30E61A3CEE7355DAAB5BB2C229D3E1715810561CE0BBD8E896A0236F503D420614FD4C0655A2ED90E66A42A885BFD7E69870525BF7C26AECDA3A7CB233E50198BE8AE7A8BBCB9213643835A1ED160DD175E365B84356D703FBD1D122AE1FB7E5227D366
	B0BD51AA5B7A5DCA9FD0EAE08960095CBBG947FF6BF41576F7660E90DFE2670136BC9206596E2ED34EFC0DF1A74F3050CFD7F211907590F4F524597D29FD6967A7AF250C1F4EF59C3ED9E633E088ABEDA05273E8CAAF5CA2E6B77469943764799E9FD9973C91F0DA90EB3AE85D88510DE92E7ACG288768AC419AB3676E394A9AB3DBA4395AED33DBC5C92032463CF761D39C17A5CB4330CB368C7708E7AD124D52ED97DD8263F9BB0F64205BCB6EBD2576FB4046B1F1D814AC324DB18C5A214F4A226422BC59F9
	1B880FA4AA54D65B108886061C826A4D4D5B8C1FF60BCBBE6334DAE45143C169FB73C81B0CDA4E02C6D0G5FE9DE8E9410FCCDC27C91G73F89E9ED074B50A920C96E5E5CD8E47205B59CAA291D61510BC5F2B0C9D8B617B12924C717E5D84E7BA90E7B49FC0BDAF643D2D52D3EB6BA16EDB24896CE3297D3970E9857EC900F000A8009800F80095GC2E91CB18B40D40AF57557F62C2B5A755F938A1E7E06E6DDE5AFFBBA45DC4A7AD729EF7F5A2258A53E0783AD9AE089C092C006A94E3897408C608488B761BD
	2327AF729A4682AB0B872F23D8CCD349E02CEA5C8AA6B34972E2AA1DDA87757556EB72B5054929471275C1E4B6A96D6B0D5B48BE49187316C0470429F4503A09ACD47AB264B996E02589EBE75B2EEC5A972BADAE91FB5FC989BA0EE875D4920D374AB10B79AD32F8CE81D48138C93F81B8845082F081E08C608A4083GA3G67G6E83B883D083308F208800CCDE9AC833G9C84780C54B2C0B3C0B9G143150C7FC944848AB88C3G03GC7F9BB8CAAFE698F42D883508AB089B08FA096E095C0D6D91C3196408E
	90813088A091A03DBC4ED88C50899086B09FA09EE08DC0CE854CB9GE400B9GCFG2C8448BC94E72CG30824C82D8G3094C0DF897681DAAB3116C706766ABCED2FEE77EB395F8583F59EB107652CAD2AD85EEAD83EB630DCE3FEB730B6ECE16D52465AE983EB078B6CFB8FEB73BEF6BD485AE183EB5B8D2C8646D8FB21EFD77E4531EEE0B574401720775F85E86D8843D8BB6AB1A365DFECEBA7C09EC0BEG39A713DABAC86DABE38B291FCF84FA2FA77C3B85CF1DE1AD137169E30156598E06A775086392992F
	48D8F508DD13BA2CE15F3731CFB28E12F5C8394A37773F045C12F54A56FF03B67E32000E691B73C94E45D1D80E540ACEF1588ADFDC0C76C13EB2FE5379D32F034BE4ABF8A74C37EAE002636278CF73C9ADD8E4D932F53BE531C514E55BF01F07DE2858F2393B1D1E299A5AAA49A76D6E9803799667AF4BA7F3159E477030285CDC388823D7A8F3A551AEAA0BA907FBD826786A8232369C94A758DC2D2EE02DC066DAF6B719775175DA3405542CD3328D41AD0BCC9D0558CAB20FFAD45A224A51A460483AB392BE97
	D462FCE76CE31C6F64DC3CCF08396054E94D69E75F7DD5DDE6951D9B64D6F966BB5427525D5A3E9FD266EC8C6F223E1E2EA2F62BC54BF0AB4CA10B845A37E19D5EC562D2D6117B9057D6C7F8F125390F7336FEC470D6C70F1BA87660CD5DDC47FE44BDB041F1C508CB2BD56B5D4F65F3CE127ED8E3F738C4EB35DB169D439E5AC09B0939172DF60FC98E37137BF932B9DD252F45A97650394733FB14B58D642B93C02E4239470FF793BA4EDB59242346E1375352G5A12AA52FE567ECC167FD9F0BD2F0A365F86CE
	7FBD27EFB98C734A433EF4338D69DDC09B77233F4669B3009669C77F66G52230136580FDEBC08748420A57951FF47694291D8EF9F7125CF58111E8B3443FE743FF13A8C34D9AABA4D63BD0599248F0AA3EE9116C3E48151487B49778A5F76211DE5FD06529FAAA4FD462EDAFB2CB9227499876E3F44B0FB89C7689CFEA5FCEE87BC653B4509FC7555142F2B1A4E6D65EAB5DF5B887205B13EB955F4DD007ADAEB885FC7FC5FA79A680B81D6G64G2D3606461C28703D4179A2EB68FCEFE18D650F054FC2EE77A1
	97726D2D25FA6AEB69BA847DDBDACB783E10112F936935544F70DA355D83EE644BBDCA67160761538C60BCCA6DD2BE7DA872CDE3FC330FD27BD1C7557E45F13E05C7293DA5C7699A49BBFF334710AFD14517214AB34D5F676348370671E55651F5D6EE1DBA0E77A6102F360E4E07BB6B28DDFB9D750F6665CF1CEFDA9D55B71BFD4EE77A285DCF843EDFD7C7675506E3F1466CE3B4AF9BF89C4FF23E43C0EB8798BFC67DA31F1E7638B39979E6B03E48E3546E52E36AB83AF5BC0EFABA076F2C27F190FD1EB88AD2
	104FDECF7504314F5975EAFDFF64FCC975F4ADE0E820F53A25C15D3EBF5473BAE820FC079BE87FC0BEEA77D2AA72199B283D7E86AD3B8756A25FE483DDF7C4E85A955210EF9E63DB64E3173647131CAF967009G7A63F42DDDFBDCED77D6834F73F13A4609BCCE6BB47A385AEE27110F870CAF61B82D3F954755716EDA07FC2B99DFC6A30DF7EFA335CB79DE5C0AFC56C64AE7B826151769D9BCAF0DF44D955168ED17721D5C4673427896B552F18879E8DE5E5F4E7352C8779D929BE9DE929B55F6DF64FC2B9A69
	7A0E0CAF04BF3DC92D6FBBBBF89DB4D1FB09AA3D9EFD56ECDE77CDF41DC874917EF6260F4E8BDED3762914BD67814B28E53C50B586732D3232632ABD65DC170171BB1BF06F38F5145CE32360BA86A0218967B53FDD0FFB479103D102BDA9BEA5507EF8D29375B73359B32FD99CB516C26F619F6FA3B6DB01E6ED467B775EBD6A7D2BA3AE17234726EC1D31FB4B9F762867BD941738E71DC0668F12CBEE000969B85E077EE78F71176C2DB1B45BEB1C590FFB7363E0B73A997766271F5CA1845E1BAFABD3E6BAEC3F
	362788F390FE71EE216B9BF7685E8A100395ACF64389CF8EE6FFDA12C2FDC93621AF0701BEFE82FD39DF7F51143E600C88E39AD37410B9D3B468D88110FD126097581467A750BFA85F878348E7857A1C134857B6C0784878CF799E61FC71C0CFB7A35FBFFC784E8FA2DFAD70CCF23E5903040F5C37A95FBFB95F8260D949794A6D040F0C2B14AF520EFCB927E05CBD0579385B3EF34AFC5429E70FACA76F8EE1EEA3C0CFAC57753512CF2FAA37E4360979FD4901FE69CF4799ABG687C74041354A34AD25C181344
	A4775BDC34D6DEBDF298470787678A8B53E669341E35745FBBDBA71F26B5B2673427D604971769B281289FBFF2229FAB01A727857DF8F0505F0FA3A36887151DB602321DA09741E5DF9AA1F969D12A9B63ED1410B79678742DD01B2D4A18D31529723922156EA3DBDBD157FFCAE44DEF134521C272CF0BEC11451AFE4BF01FE84DA503CF21E5CCAED41611058DC0BFCA3E6589D4F7C239DAF78C533D026B7E232444838BCD1A576FEDD76755E0192E1957D9AF7769563452FC661C7164F5B2FE19AEBC9E731A6342
	F86D40B36F8C56CAE6A579D45604822EC93E2D703AC43226902DA48C6A4F64B974E7DA6C82E14B66DBB47DD16ED74C8F7DD90FBF75AFAC53F53E007E7CB7772795F8E61D459CBCE9A36D0C6BB34ABBCFA63C444FA700EF8D67FD4BCD46E5773055C1A661C72DD06A825E931E4BB92452336F429995D7EE1352D2F6E8354296D8248F77157BE3724E8A786CEA6EE6F5BBD4DC79DCB36E999B7BF545EDB45E4EB6CF5C195086393C9DAE38B16EA860D95E06317C5CCD5A5EA2756D2335F2A6C5DDABDB5AE92DD434A3
	7FDF5DD8ABD46F6ED154EB861E991CAFE214695DCF2F970DFA4B3D4165E2C0E6B5179B9A706FB30F0DA16F5EF3B0A7BB07E35A0D6358BE1100CBB847FD9CD76B206DF2E92B3277A14AAD2C3CF2BBA053D44F6189541F5181EB0E8E54F36F846606DE7FC6D91777A8EBED74ED63C574E19A48AD6432EB2E787BF06483329FC776D30E4A0E66628B4A6ACEDD94A73B87403772BC7F2A6039B8A1202FC9E0AB77BC76274AD384BF6C1006AC7653224BED17E9FBFEB220551EDD67B1073AE454A783EE7EF99C0F1EEBD0
	4BA51E27F2691D985FA93B779838B89973108DBC6D1CEF0FAE14F10B46E45B5509B9249B9CB57DE24FE035E37CC2032F2A26C03A4A1B340E83A8B38E68EAEC5E22439C04035F0B396F2F6A3006A95F19940CD178AC4EE87A8C799625783775BC9B593B17C428389A47D037833C4E1D4295CD925EAF6918C0FD5820C7DF1DE0BF0A7B6056FB576A37755E6D33144BAD8719AD1F23DCCD2AF70C7724E20CE660194179562FD55F875918321677E160DB4DF9FF3E96FD21FCF9E93C7FDE007EFB817942533CED3F4679
	A201A76182566226FCF5ADE6F451DAB4F5211E8E83597332A89BD1057443ACCA3D50977C0B6C08C2E6E5465A6A72BA321FE831DA1536509A250357504386743D897C8A633EEDB7FA37CB1D51FB9C1CEEC439C5A0330A4B2DB4FA0F1F196B3C5B33E59D36D301856689969C876EFA0B6C810B63BD221384FA32874AD2A243950B55FA144C77DC7D0E3123126410EAED963B23CF9DF2EB3F6498A3F72CF2BEBE26E7212DB9E0A75E02E34703D968FBFAB72CD53B5177F736F9377F78B6641B841E851C6F8F5B3C5B3F
	FABB1F0381CFCE8F725D375DBB76749DFC3E85BC911CEF7A8EF2DF765A6F647D6697DCA6967875D60C6116EC44D7GCE36222E359F262B6A1D4CD869331E5DAE5FF3A8732D1E674FB12A6F8874FC469A65FE97EF75BDC332D1393799C45FB3A49D23F8062482E8ABC4BCD370EA91D9F3C674427A8AE0B140F2G218F667DFD3856137612BD6AA94FECB48C4B30943458DB3C9E8660598925D6874589F6DD30CFBDD723F9382DBF34B3C4F7DE339C0D3B2D040C819590E3BB0093E086C0940F79877B8A02450CE7CF
	D830541FB4DBE8F13C93225CA5C5EEF15F54E7967C65E6AAF24B2FC10E4EA173BA54E76E741B0E6BC85BEA4D1DB37A595C315FB30724363387C27339F060DAFD26F2BF8B515EEFC334E7998C4D1EE3B0B4FBFAFBE8765E3607E66F1E906DAD8851DE56D0E8FDE3D708F2FFF184136B6A770E014A3D91D4AE4CC70E0EC52F9FA07B03F3003690E08940CA008CDB1C31G40EC43B1694DFD55C23033F378303655413F5678BEF0C5FF97B9C30B734920F27DB6AD399BC6C2339791D4EE1A263DD3C14F17EA5B3B90D4
	EE360FBDBA976A3313F652583F45993BC077F03FD505FB3851B65C2B3DD4275E2BDD3471D260FDCAE65BB06099D74D31FFCF21BEFC121B256DC3ABCC916BEC225DCAFD58D801BE548F60DC60A7A6A2BB8D2E67GACGD882308AA0E3B04ED8BC08F5F8EAFFF930BAD4A644D003CA597997A06B1FFEF31F2BEB9FD3D03956C12DFED6D6C6623383ED86C0A440A20065GEBG326D985FDD7B0376B3FEFC74EA7AD9E208F13E9AD44EE457123BDF7615B37B70E95BDB68A7D768F3ED37EB65757B65A42FE1C00B84D8
	88908F309AA0F38856D5C318573F6FBFF415F5D38338FA07E3D0A3254CD7519DDACEDDC1655A07347A76EB3D811E4DB4D93A71594C2C948333639C42F300C5232179F9A428DC541016DC72D8E876B686150B5134770B906DBD9DD4AEC1535EAD63215576377C64FCEBBBC953DE5DC4E871358615B38CEB7AF9A534786E73135B6EF31D2DE92FD588AD3E6E20F2A69FFB340FF48E60FD47BC0CFD44109C1A8F6F86151BB12C5517AB4F863ACF337DC0666BF8A37A9A49FD7D351F4D2BEB1F25FEF23E6D132059BE37
	69C24B4D1DC16512B46D1DCF89ADBE1B1FDC214F354121A517298FAD3E0F024AE5EB5AFBA1C4FBC9C165CC1A76662706164F1F78497966532CE9CFDC9B62FDB128DC3F263DBDE92159BB94D4EE1A263D57C33417E688BA27F7E80D959BDB489CC7E32C2007A3859C576A73715E9F6D4031621120B635FDFDAA285C8A4D5C4CB406E66F7620F202D3CB6E5C3A50EA7B7256E0E36196CDFBEFE705E6AF5BCF2E5067FA2F263D85FEF2D7174F47034A55EB5A3B3CADB4FB33024BE95A2B5F9E1A3D33C16546355BEFC7
	E87632034A05EB5A7BD508761E8BAA9725E96F6B5921597BEED039989FFB749D41DA65ACD88250D682D05C07EE024B9809B3660E60B8249BA36F62B541359DA09CE0BEC0AC40CAG0394E74C81288730CBA8730EA2B38D2EA381E2A4FA1E131C45DC4C4E6412F305646C6BF2AE53B59A2961BB31293ED65B7665A53EF0B9491FCEA0076143B5646B8348ECE071FBF2BED35357D41FEB32B7C376E1345E555B287A9E68BD3CCD2AF67625793E5B2786FC57072E1B5FA88A7CCE072ED87B1D8EC33176BB9D9B0AB7D1
	BC68D03FE10B74F70B27FE67E3FBF160F7BEAE1484763337C45BCFFB09361FD24954FEDEAC896467B6A5FF4FA84F60A57730EC9B92955574FEAE0E1377000EF44B275D43F49B02E8F779BE5391F0EF63EF9564D9216042BD0CCD87768B7EFB9864B40EC3C2A52D22C5BA595B4BB7066879B46A57CBB229953FF7E0CED806BC6716F21CC646E3169D667DBC6614524079DDD72A1D5F0CD2D23BAE07DB6A91FDF378DE6996CE736C3410238E449D76EE10A2AFDB24BE512F8DF6AA721496C8FEEFA969BF5DB62BCDE2
	EFF65098DF730A71A00F7D1191B55E73DE4B8CE91D60B96B322F887BF1E455D4E3E796345BDE17561ED5EEA5F97ED78B34AE00F100F000E85E4EF125C11FF99E9D57FC664956B595DAFEADE13EA43A7C47A9EA7789D360B6FE4A245D463FB585EE6384D370B6FE5E94380DD718023771AB26C0FD11361D45759636E92F6498B21B186E5A324031B616E947FA2AACF02C6DE541E33514850E353FACF82CA3E55A753CD5D657ED919F07FE796EC6E59CF339C4173239D9EB11ADCC66F9AE83B5DDD3427D78F1F9603C
	4414EB676567650173722B7260F9F926BCF0DE1615874F4B1F4A272E01B94AF9C1CF8D6C5B4F76B77C768D882CE67DA3DD06F9076C5DD7583BE823BE7A0A587BEFE36AF6B840797F6905B7F17C93632AB6E8760CB717A63CEBF69F4347DDD1371907FF262056C35A1FBD8B9E500A2F1845D4AF7B0F83D4DFD5325A5FE22E6FD223163ECE264799D0DF2ACE6DF7B147AF56F947F900613F162266773C83582877665F4F706F27FA630B987E45D4359ECFFB4CD96B4DDF4A703DE95E78038C3F57204E2FA79F4D79DA
	7988E3F9188D1F73B56BE715415BCE8943BFEE74292B3D6C6C66D6F59C1EF7B69B325478D20E7FF3160F9EF6DE6B21EDBE78E20A3F32DD9DDF09C77F8E9F7E920A57E72B79BD6D73E436F7DC458CFF5EC54E1A5AAD938E37AC8885EE32D610DC78EEEC8C64A881C0GFC261B1E072373259F0C91FD3D12E5883A33B211B10C92D9139BE5E5601F8190853098E0059B4FBC3DFF140C89F8621F0C954A43926E6B21F4B297727D95A5EF1E39054406F2B62EF542098AA86D7B2D1B7DE84A99D8C6AF97B85D7B3CA723
	3FD21921728F9F52D25A2FBF4870711DBCC02534E7EA5F71213510C1136D8731F3FAA64203070335E3DBFB1A54B0FD02340F1D2DBFB926F53E56BC0AE7977F1226C537F2FA1BC10BBE4E69B776E951E70D623C67358AF29F6063B81B1752356543D538360C9C457566E4B941A54135FE0C3F9BD499787ED5DA29FD7FBAD4293D8E28291CFA9D50D899683EC37D4BD71EF3EF815FF2475067708A0C2318632214E7F7E1F09D41F16B75BCDE0EFBDCD46205EB43B8624615E79AD9F0DD40F14F8E202C0963A29A11EF
	86477DEFA372C5F01CD559CBCCA4579C774BFC6493A690773A0E604CF0ED1D407307F7A86FA3A86FCC81AEFA827B713EAE2FF3816484EBD959B149E62D3674B5A923825D47FCC159D3A072ABA6707D328C653D056C0B908B0079A21ED9987F944FAC4C3FE72E5035D27B7D32652D17F47D976998BC6B22674CC27150B29D3579399BEDAE863A700572AC552D2ED352DFF6A653FDBBCAFF540BCEEA602097BD490BCE7C7D0DCECD7FE8D4CDA7B94AE372B43627BE415856BC95AD589F8A8B785BF275DF50186C6CB3
	BC717664305597D3E8ECB70C61B324F9C027389C836216F25C2D9C17B90938C77B9057843858C9027BF5D5AD339BB629FD2FCF91F06F9A6EFF40B3FF127E4E9F4145CC52F32149FC8C7A3A52FFC5D83F12E5EF6EA5E2637EF2EC0F4CCBB816D7D760B9E37BA5727BFF0F121F729028CF77D620CF511734FDCAD5797644A56A53478C1FF04957271F26F85B7AFB1D47D60E9EEDA58630152C32379AF852AF47995FE1786C4B3E361E9515F7929D1258A7B95C43D6C120B9F8C569678540EF3A0CB978711837DF1575
	9E3F1286502F764B5AFE2529F2E0871EE917E98E14775ED4FED15D16860F6E74A6549D2921BB55C777B426FBAB533740AF66AF723D63386D38475672FC34959340163AAD6301A7DEE5EB051F2D0C94621BB8CC2A4B4AF23BB3856BEB55E55CBB0C8CFA564597285C03A14A3DF44DF274FC758B4AD8E838620913627F2C60B3D5F876EE230C63F1B650422E60D9AB39114C1FD64035FEDA1CB18B2098209540B98D6F234FD76E95026CC134BA1CCDF023352BEE0D6C3754021EF37185BA6EBD1606635E021F5CAFAC
	3C21B660B93430E934BE66314F5C97EFCFA61FB43E34E692DFBC5012GB22F03FBA340E100766BB03E3CAAF6461AEC1F9D11E5CBCF3F7236B05F0C3309E39E34674C7CCC4D33A3766B289F532E732FDBAA172779DCAD0271C785147B3A265CA2463FA4205C4F7A346492997FEA8DB9BA6E3F494F8726FFA54E38C320FB4586817F122A7CF1B8EE7C7104405B7279F4EC4B2C876E92EAFFD72B43714657EE6DD728AF91DF715C1FF3C632D3E89FFE2C1C7852EB313BC42EBBF2CCFD8E5C346EAB1A3F19C3A01669DC
	4EBE573CF3C7B255711B73EA9D99F758F54981F4EC391E4A66DC6F710F66AA011FE3EC3FDEBBD7B4AF4F19851E5FB3FA14895714213FE5251D5F4D27BD5FB3DB49F7761E21B22F66AF8755B86C8E29305BEE699924B6BF9CC41B0B2F59E669990F6F071AD00F9096E724746F1C7550B795A05D9426ED074ABC5666713DB2C55D3E34F6D748827F7E0A6AFB165C2E51BE7FAA20B6CA3ED49BF46C395498F80D9256283DC63939D1FB0D925EB875B313590D011F195C59287E1DACBAF65E56CEEA49896DB78BA09A60
	890004B09CB7F7B7115F9C20273DFD474C2356BE3247C7FE9DD5664FG1A83477ADEF3705FD44DEEA6F324EE87ACF00708FE7E7BE04D243DE407D3C1D2DCFEB34911E46B6B17A9AB436FEBA6B5E897FB95B4DD3F4C9D4375502AB0DC53BCB24E57B95391F75800FCE60E7B2E8179BAB96EFE4EB71F635EB2A25FC20E7BFD9F2E758CB7A0AEF99079B2EF40B9097E30FA7C3682FE7C06295FDDE04F0C4E11B954AC600D820885C884D88D10F1E31CB18740845084E0851884888718873090E0B140D200249B313DF7
	1DA43F53F756B2607F348910EC9127B43AE43111E6E7FE1CB9CFE2524F0075448C54B5F2B2FF8ADDE6496194A5F902E269DE8A55E5A62F4E3236FC7864A701F4C0859E07B1103EF85CEA6963FDFA195773104360CF6B0C29F3C965BA3C4EDD75034C64D549FDBA28168B8719F9D7A5D75D2416DB88B231D7A577EE01DAEEB9482C8AA2C7F3F9FBFB603E99599E3CEF5E57CE57EFFD4A969845BD58CE767A9C3D3DAE110F81BFEC4F6038861759613211BDFCFAAF08A6BF7749667F7163B87FB75C646F3FFAEDC5A0
	6BA66F35465E1BFC57B18997BC3A979AD0F7C5G5DA9AAB07B68367A69FE234B237BA3A36AF68650BD559AA94CCF7776EE0F6EAD0328BBBC00EE356FE43D29DEF34ED36926E34D5643244D22991E6232955CE206E34FFBBB26F85E5B913CB6B6F7E80F5B6FF7780F5B5BBB34476D1C8E75384D666D9D24867AE05CE967FC059CF70E630A941D8457E6334A7D9CFFD0710B606BC5E29071D50ADFE38A2E9D1F912AF067982E210354B7045C6F10D076C487D953765A6CF6FAFDDA61318DC33973FC34A9BE5A1C9615
	5F1D9C67713B63BCE9D7FEBF2743A023D94EE7AAFA217BD86C360B4ACF9E51373AE9BB2E2A70EF5B57AB54EDCBF1A3FAFF3ED13DBF5F21C1FF3E6AC1FF3EF3CD7EFC1FB779736DAE70675BDF206623734854891CC7A65EB455BC72C80F40E5DE66FBD8D9B7CFA5237CEF93CC264508B22DB763FC76C651C3EF607366D91C7ECC2FA060B8E479B45D539ECA19D1FC5678FA8F9E4B0C0D556B3C4E66BF81FA93EE3E1AB17E2FDE6B4015A0A34C3C9A39F62F731519A013FBD5F2A73C6EE195A0E38EA2474E00CD7837
	7127937E3510EA7467DBE77467F3E870499AFC22C62D8EF855AA7D7FC61EEB1E29930479FF91D2E561DDFCC4120D4FC83249B387D948628465810EAFB92D45B264340BD6D9D8CB1F6D48C28ACC3CDD123011AF429A145E3FFC3428AC65453BD2F4023EC1D8CBC74386C1CF063486A1D599719A0494988D47495B7DFAB538C6044BCA1DDD8DD692B6F8AF7F126FFE6F7A0C2F9639F50221CED88B4BABE73F85ACAA6F3DB789EFF5AB26C589F8E5A1C3794504BDA03BC711ADEF92F4BDAED9586A417790BC2536B8AD
	BDA230982D96E9D051C0DCDAF7DEB076CAFD5D0A81D9501D2FBB8FE615ABC2CEEF9256497DE4F1C0F67EE461BD0FEAB319B6B2C239128661CDG2772F7CDC950444A4226936218EF8BEE2A95FBED432A0607F0575136A6D399E070AB0D7F3FAB5AD8FC377D0AFABFE275741E00FB9AD13752FE3868D64FFE841DA77F48CC6AFDB560B33F0AF36E35E6725B5901664BADC3963BDD3658870F8CDB495C39FAC296BDEB477FB2937DAE64E1C4CAFB5CCC7C9ED0497C9FD0CB878868CB93874B9FGGACE8GGD0CB81
	8294G94G88G88GC9FBB0B668CB93874B9FGGACE8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG85A0GGGG
**end of data**/
}
}