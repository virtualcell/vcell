package cbit.image.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import org.vcell.util.Coordinate;
/**
 * Insert the type's description here.
 * Creation date: (10/11/00 3:05:12 PM)
 * @author: 
 */
public class ImagePlanePanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JLabel ivjLabel1 = null;
	private javax.swing.JLabel ivjSliceLabel = null;
	private javax.swing.JRadioButton ivjXAxisCheckbox = null;
	private javax.swing.JRadioButton ivjYAxisCheckbox = null;
	private javax.swing.JRadioButton ivjZAxisCheckbox = null;
	private javax.swing.JButton ivjSliceMinus10Button = null;
	private javax.swing.JButton ivjSliceMinus1Button = null;
	private javax.swing.JButton ivjSlicePlus10Button = null;
	private javax.swing.JButton ivjSlicePlus1Button = null;
	private ImagePlaneManager fieldImagePlaneMananager = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ImagePlaneManager ivjimagePlaneMananager1 = null;
	private javax.swing.ButtonGroup ivjButtonGroup = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjJPanel3 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ImagePlanePanel.this.getSlicePlus10Button()) 
				connEtoC2(e);
			if (e.getSource() == ImagePlanePanel.this.getSlicePlus1Button()) 
				connEtoC3(e);
			if (e.getSource() == ImagePlanePanel.this.getSliceMinus10Button()) 
				connEtoC4(e);
			if (e.getSource() == ImagePlanePanel.this.getSliceMinus1Button()) 
				connEtoC5(e);
			if (e.getSource() == ImagePlanePanel.this.getZAxisCheckbox()) 
				connEtoC6(e);
			if (e.getSource() == ImagePlanePanel.this.getYAxisCheckbox()) 
				connEtoC7(e);
			if (e.getSource() == ImagePlanePanel.this.getXAxisCheckbox()) 
				connEtoC8(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ImagePlanePanel.this && (evt.getPropertyName().equals("imagePlaneMananager"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == ImagePlanePanel.this.getimagePlaneMananager1() && (evt.getPropertyName().equals("normalAxis"))) 
				connEtoC1(evt);
			if (evt.getSource() == ImagePlanePanel.this.getimagePlaneMananager1() && (evt.getPropertyName().equals("slice"))) 
				connEtoC9(evt);
			if (evt.getSource() == ImagePlanePanel.this.getimagePlaneMananager1() && (evt.getPropertyName().equals("sourceDataInfo"))) 
				connEtoC12(evt);
		};
	};
/**
 * ImagePlanePanel constructor comment.
 */
public ImagePlanePanel() {
	super();
	initialize();
}
/**
 * ImagePlanePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ImagePlanePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ImagePlanePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ImagePlanePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ImagePlanePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ImagePlanePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  (imagePlaneMananager1.normalAxis --> ImagePlanePanel.normalAxisChanged()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.normalAxisChanged();
		connEtoC13();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (ImagePlanePanel.initialize() --> ImagePlanePanel.imagePlanePanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10() {
	try {
		// user code begin {1}
		// user code end
		this.imagePlanePanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (imagePlaneMananager1.this --> ImagePlanePanel.updateView()V)
 * @param value cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(ImagePlaneManager value) {
	try {
		// user code begin {1}
		// user code end
		this.initView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (imagePlaneMananager1.sourceDataInfo --> ImagePlanePanel.updateSliceLabel()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSliceLabel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  ( (imagePlaneMananager1,normalAxis --> ImagePlanePanel,normalAxisChanged()V).normalResult --> ImagePlanePanel.updateSliceLabel()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13() {
	try {
		// user code begin {1}
		// user code end
		this.updateSliceLabel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (SlicePlus10Button.action.actionPerformed(java.awt.event.ActionEvent) --> ImagePlanePanel.slicePlus10Button_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.slicePlus10Button_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (SlicePlus1Button.action.actionPerformed(java.awt.event.ActionEvent) --> ImagePlanePanel.slicePlus1Button_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.slicePlus1Button_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (SliceMinus10Button.action.actionPerformed(java.awt.event.ActionEvent) --> ImagePlanePanel.sliceMinus10Button_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sliceMinus10Button_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (SliceMinus1Button.action.actionPerformed(java.awt.event.ActionEvent) --> ImagePlanePanel.sliceMinus1Button_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sliceMinus1Button_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (ZAxisCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> ImagePlanePanel.zAxisCheckbox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.zAxisCheckbox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (YAxisCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> ImagePlanePanel.yAxisCheckbox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.yAxisCheckbox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (XAxisCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> ImagePlanePanel.xAxisCheckbox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.xAxisCheckbox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (imagePlaneMananager1.slice --> ImagePlanePanel.updateSliceLabel()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSliceLabel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (ImagePlanePanel.imagePlaneMananager <--> imagePlaneMananager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getimagePlaneMananager1() != null)) {
				this.setImagePlaneMananager(getimagePlaneMananager1());
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
 * connPtoP1SetTarget:  (ImagePlanePanel.imagePlaneMananager <--> imagePlaneMananager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setimagePlaneMananager1(this.getImagePlaneMananager());
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
 * Insert the method's description here.
 * Creation date: (10/11/00 3:45:42 PM)
 * @param delta int
 */
private void deltaSlice(int delta) {
	if (getImagePlaneMananager() != null) {
		getImagePlaneMananager().setSlice(getImagePlaneMananager().getSlice() + delta);
	}
	//updateSliceLabel();
}
/**
 * Return the ButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup() {
	if (ivjButtonGroup == null) {
		try {
			ivjButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup;
}
/**
 * Gets the imagePlaneMananager property (cbit.image.ImagePlaneManager) value.
 * @return The imagePlaneMananager property value.
 * @see #setImagePlaneMananager
 */
public ImagePlaneManager getImagePlaneMananager() {
	return fieldImagePlaneMananager;
}
/**
 * Return the imagePlaneMananager1 property value.
 * @return cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImagePlaneManager getimagePlaneMananager1() {
	// user code begin {1}
	// user code end
	return ivjimagePlaneMananager1;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSliceMinus1Button = new java.awt.GridBagConstraints();
			constraintsSliceMinus1Button.gridx = 1; constraintsSliceMinus1Button.gridy = 0;
			constraintsSliceMinus1Button.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel1().add(getSliceMinus1Button(), constraintsSliceMinus1Button);

			java.awt.GridBagConstraints constraintsSliceMinus10Button = new java.awt.GridBagConstraints();
			constraintsSliceMinus10Button.gridx = 0; constraintsSliceMinus10Button.gridy = 0;
			constraintsSliceMinus10Button.insets = new java.awt.Insets(0, 4, 4, 0);
			getJPanel1().add(getSliceMinus10Button(), constraintsSliceMinus10Button);

			java.awt.GridBagConstraints constraintsSlicePlus1Button = new java.awt.GridBagConstraints();
			constraintsSlicePlus1Button.gridx = 3; constraintsSlicePlus1Button.gridy = 0;
			constraintsSlicePlus1Button.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel1().add(getSlicePlus1Button(), constraintsSlicePlus1Button);

			java.awt.GridBagConstraints constraintsSlicePlus10Button = new java.awt.GridBagConstraints();
			constraintsSlicePlus10Button.gridx = 4; constraintsSlicePlus10Button.gridy = 0;
			constraintsSlicePlus10Button.insets = new java.awt.Insets(0, 0, 4, 4);
			getJPanel1().add(getSlicePlus10Button(), constraintsSlicePlus10Button);

			java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
			constraintsJPanel3.gridx = 2; constraintsJPanel3.gridy = 0;
			constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel3.weightx = 1.0;
			constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJPanel3(), constraintsJPanel3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLabel1 = new java.awt.GridBagConstraints();
			constraintsLabel1.gridx = 1; constraintsLabel1.gridy = 0;
			constraintsLabel1.insets = new java.awt.Insets(4, 4, 0, 4);
			getJPanel2().add(getLabel1(), constraintsLabel1);

			java.awt.GridBagConstraints constraintsXAxisCheckbox = new java.awt.GridBagConstraints();
			constraintsXAxisCheckbox.gridx = 1; constraintsXAxisCheckbox.gridy = 1;
			constraintsXAxisCheckbox.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel2().add(getXAxisCheckbox(), constraintsXAxisCheckbox);

			java.awt.GridBagConstraints constraintsZAxisCheckbox = new java.awt.GridBagConstraints();
			constraintsZAxisCheckbox.gridx = 0; constraintsZAxisCheckbox.gridy = 1;
			constraintsZAxisCheckbox.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel2().add(getZAxisCheckbox(), constraintsZAxisCheckbox);

			java.awt.GridBagConstraints constraintsYAxisCheckbox = new java.awt.GridBagConstraints();
			constraintsYAxisCheckbox.gridx = 2; constraintsYAxisCheckbox.gridy = 1;
			constraintsYAxisCheckbox.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel2().add(getYAxisCheckbox(), constraintsYAxisCheckbox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(null);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}
/**
 * Return the Label1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabel1() {
	if (ivjLabel1 == null) {
		try {
			ivjLabel1 = new javax.swing.JLabel();
			ivjLabel1.setName("Label1");
			ivjLabel1.setText("Axis:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabel1;
}
/**
 * Return the SliceLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSliceLabel() {
	if (ivjSliceLabel == null) {
		try {
			ivjSliceLabel = new javax.swing.JLabel();
			ivjSliceLabel.setName("SliceLabel");
			ivjSliceLabel.setText("0");
			ivjSliceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSliceLabel;
}
/**
 * Return the Button4 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSliceMinus10Button() {
	if (ivjSliceMinus10Button == null) {
		try {
			ivjSliceMinus10Button = new javax.swing.JButton();
			ivjSliceMinus10Button.setName("SliceMinus10Button");
			ivjSliceMinus10Button.setText("-10");
			ivjSliceMinus10Button.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSliceMinus10Button;
}
/**
 * Return the Button2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSliceMinus1Button() {
	if (ivjSliceMinus1Button == null) {
		try {
			ivjSliceMinus1Button = new javax.swing.JButton();
			ivjSliceMinus1Button.setName("SliceMinus1Button");
			ivjSliceMinus1Button.setText("-1");
			ivjSliceMinus1Button.setMargin(new java.awt.Insets(2, 5, 2, 5));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSliceMinus1Button;
}
/**
 * Return the Button3 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSlicePlus10Button() {
	if (ivjSlicePlus10Button == null) {
		try {
			ivjSlicePlus10Button = new javax.swing.JButton();
			ivjSlicePlus10Button.setName("SlicePlus10Button");
			ivjSlicePlus10Button.setText("+10");
			ivjSlicePlus10Button.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSlicePlus10Button;
}
/**
 * Return the Button1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSlicePlus1Button() {
	if (ivjSlicePlus1Button == null) {
		try {
			ivjSlicePlus1Button = new javax.swing.JButton();
			ivjSlicePlus1Button.setName("SlicePlus1Button");
			ivjSlicePlus1Button.setText("+1");
			ivjSlicePlus1Button.setMargin(new java.awt.Insets(2, 5, 2, 5));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSlicePlus1Button;
}
/**
 * Return the XAxisCheckbox property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getXAxisCheckbox() {
	if (ivjXAxisCheckbox == null) {
		try {
			ivjXAxisCheckbox = new javax.swing.JRadioButton();
			ivjXAxisCheckbox.setName("XAxisCheckbox");
			ivjXAxisCheckbox.setText("YZ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXAxisCheckbox;
}
/**
 * Return the YAxisCheckbox property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getYAxisCheckbox() {
	if (ivjYAxisCheckbox == null) {
		try {
			ivjYAxisCheckbox = new javax.swing.JRadioButton();
			ivjYAxisCheckbox.setName("YAxisCheckbox");
			ivjYAxisCheckbox.setText("XZ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYAxisCheckbox;
}
/**
 * Return the ZAxisCheckbox property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getZAxisCheckbox() {
	if (ivjZAxisCheckbox == null) {
		try {
			ivjZAxisCheckbox = new javax.swing.JRadioButton();
			ivjZAxisCheckbox.setName("ZAxisCheckbox");
			ivjZAxisCheckbox.setSelected(true);
			ivjZAxisCheckbox.setText("XY");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZAxisCheckbox;
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
private void imagePlaneMananager1_This() {
	return;
}
/**
 * Comment
 */
private void imagePlanePanel_Initialize() {
	getZAxisCheckbox().setText(Coordinate.getNormalAxisPlaneName(Coordinate.Z_AXIS));
	getYAxisCheckbox().setText(Coordinate.getNormalAxisPlaneName(Coordinate.Y_AXIS));
	getXAxisCheckbox().setText(Coordinate.getNormalAxisPlaneName(Coordinate.X_AXIS));
	getButtonGroup().add(getXAxisCheckbox());
	getButtonGroup().add(getYAxisCheckbox());
	getButtonGroup().add(getZAxisCheckbox());
	initView();
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
	getSlicePlus10Button().addActionListener(ivjEventHandler);
	getSlicePlus1Button().addActionListener(ivjEventHandler);
	getSliceMinus10Button().addActionListener(ivjEventHandler);
	getSliceMinus1Button().addActionListener(ivjEventHandler);
	getZAxisCheckbox().addActionListener(ivjEventHandler);
	getYAxisCheckbox().addActionListener(ivjEventHandler);
	getXAxisCheckbox().addActionListener(ivjEventHandler);
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
		org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
		ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
		ivjLocalBorder.setTitle("Slice");
		setName("ImagePlanePanel");
		setBorder(ivjLocalBorder);
		setLayout(new java.awt.GridBagLayout());
		setSize(169, 110);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(0, 4, 0, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 2;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel2.insets = new java.awt.Insets(0, 4, 0, 4);
		add(getJPanel2(), constraintsJPanel2);

		java.awt.GridBagConstraints constraintsSliceLabel = new java.awt.GridBagConstraints();
		constraintsSliceLabel.gridx = 0; constraintsSliceLabel.gridy = 0;
		constraintsSliceLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSliceLabel.weightx = 1.0;
		constraintsSliceLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSliceLabel(), constraintsSliceLabel);
		initConnections();
		connEtoC10();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 4:02:12 PM)
 */
private void initView() {
	org.vcell.util.BeanUtils.enableComponents(this, getImagePlaneMananager() != null);
	normalAxisChanged();
	updateSliceLabel();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ImagePlanePanel aImagePlanePanel;
		aImagePlanePanel = new ImagePlanePanel();
		frame.setContentPane(aImagePlanePanel);
		frame.setSize(aImagePlanePanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (10/11/00 3:47:40 PM)
 * @param axis int
 */
private void normalAxis(int axis) {
    if (getImagePlaneMananager() != null) {
        getImagePlaneMananager().setNormalAxis(axis);
    }
}
/**
 * Comment
 */
private void normalAxisChanged() {
    if (getImagePlaneMananager() != null) {
        switch (getImagePlaneMananager().getNormalAxis()) {
            case Coordinate.X_AXIS :
                if (!getXAxisCheckbox().isSelected()) {
                    getXAxisCheckbox().setSelected(true);
                }
                break;
            case Coordinate.Y_AXIS :
                if (!getYAxisCheckbox().isSelected()) {
                    getYAxisCheckbox().setSelected(true);
                }
                break;
            case Coordinate.Z_AXIS :
                if (!getZAxisCheckbox().isSelected()) {
                    getZAxisCheckbox().setSelected(true);
                }
                break;
        }
    }
}
/**
 * Sets the imagePlaneMananager property (cbit.image.ImagePlaneManager) value.
 * @param imagePlaneMananager The new value for the property.
 * @see #getImagePlaneMananager
 */
public void setImagePlaneMananager(ImagePlaneManager imagePlaneMananager) {
	ImagePlaneManager oldValue = fieldImagePlaneMananager;
	fieldImagePlaneMananager = imagePlaneMananager;
	firePropertyChange("imagePlaneMananager", oldValue, imagePlaneMananager);
}
/**
 * Set the imagePlaneMananager1 to a new value.
 * @param newValue cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setimagePlaneMananager1(ImagePlaneManager newValue) {
	if (ivjimagePlaneMananager1 != newValue) {
		try {
			cbit.image.gui.ImagePlaneManager oldValue = getimagePlaneMananager1();
			/* Stop listening for events from the current object */
			if (ivjimagePlaneMananager1 != null) {
				ivjimagePlaneMananager1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjimagePlaneMananager1 = newValue;

			/* Listen for events from the new object */
			if (ivjimagePlaneMananager1 != null) {
				ivjimagePlaneMananager1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoC11(ivjimagePlaneMananager1);
			firePropertyChange("imagePlaneMananager", oldValue, newValue);
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
 * Comment
 */
private void sliceMinus10Button_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	deltaSlice(-10);
}
/**
 * Comment
 */
private void sliceMinus1Button_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	deltaSlice(-1);
}
/**
 * Comment
 */
private void slicePlus10Button_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	deltaSlice(10);
}
/**
 * Comment
 */
private void slicePlus1Button_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	deltaSlice(1);
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 3:47:15 PM)
 */
private void updateSliceLabel() {
    if (getImagePlaneMananager() != null) {
	    double sliceWorld = getImagePlaneMananager().getSliceWorldCoordinate();
	    String currSliceText = null;
	    if(getImagePlaneMananager().getNormalAxis() == Coordinate.Z_AXIS){
        	currSliceText = "Z";
	    }else if(getImagePlaneMananager().getNormalAxis() == Coordinate.Y_AXIS){
        	currSliceText = "Y";
	    }else if(getImagePlaneMananager().getNormalAxis() == Coordinate.X_AXIS){
        	currSliceText = "X";
	    }
	    getSliceLabel().setText(currSliceText+" ["+getImagePlaneMananager().getSlice()+"] = "+org.vcell.util.NumberUtils.formatNumber(sliceWorld));
        javax.swing.border.Border border = getBorder();
        if(border != null && border instanceof javax.swing.border.TitledBorder){
	        ((javax.swing.border.TitledBorder)border).setTitle("Slice [0-"+(getImagePlaneMananager().sliceBoundary()-1)+"]");
	        //((javax.swing.border.TitledBorder)border).setTitle("Slice ["+getImagePlaneMananager().sliceBoundary()+"]");
	        repaint();
        }
    }
}
/**
 * Comment
 */
private void xAxisCheckbox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	normalAxis(Coordinate.X_AXIS);
}
/**
 * Comment
 */
private void yAxisCheckbox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	normalAxis(Coordinate.Y_AXIS);
}
/**
 * Comment
 */
private void zAxisCheckbox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    normalAxis(Coordinate.Z_AXIS);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G410171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8FDCD4E536E4E8143411E92111211211E111D161CAE5C6C54A1A6DF3F75B5F72FE4B3E7535761E7BE20B6D517EEC7772CF54998515DCF7170A8CB7ADA3CCAB16E89341CDDBAA04E9C4A56042DC0651E1E61C394020E26FFC5FF74FB7F76E5CE1047D616F696FF06FF74E794EB95F794E777F3BA3143E91DB181BD7AA88390502726F63DCC1E86B82CC76C713919765B09B84553FB7813688AF8D9740
	B396E825AB4C0692E16770FAC8A7C13AEA395970C7F85FA6183ADD9D7092C44F9120ED08E9AC9BD94F762FD1CF91CD5F3B51884F4DG4900A34F82A7513FEC638E851F216091F27384E18565F9F6E31902BB8269CE00C800D8A763CF0667862AF9E2CD354257F6FD3C103D541D370948D149C45861E2F9EB15E739703B5CD5AE222B58E0D4724810CEGA0054F97FE73EAB961EFBAE37DFBF717CD2E347509D6292666A8F9B459C5075484E06FBCCBE49CB72C27F984E1B72457D05C459EA2B78E5ED3GD6F27CDF
	FA893E4AEDB69C8168F4E3D9BEFD3BD63135757C8342AA6BE39B0A0314456BE6E508F57B4A127CCED77E1F285F4DCD443F99C093BCD067G55G2DGDE003F507A7E766BF760D957A557BB7B7A1C0EBA176B105D627E52E9116C70DED383948557ACF5BB5D96C1E0F57A2AE12F0EBDA3814B77562873B11F446F477ACDE83ECB483877463CA29D1FE4441F4F0B72B0DF44FBFCBEE13A5B026ACE7378471F3F6E6302DA37705C87C505BA3A4BFE7CC1D106A2A7CB25FB37CC6A7A859A83A5701E2B780261FFCF71F6
	951E45E2B84513F4B850D64B989B2D1B5116460259C2493E1BF47D903DA4B22F311F59E069575A72A20D3B28FE2673B5EE4BC69B412FD46119ADEFD2FE125EB1E0B6948C1A8DAC3EBA1AC95B4C023451EBB6D4G54823481F881A23D98E3F33FFE7F92E32CC9F44B077AEDF60B6496D85E0E266D70143C32DBBC6A70482223DBB20BEE1B58E517BC0242535BC4FC5065172F0F46FEB76878DCF2C8EED136B99DA0DD6C13E4496DE1BC83CDDB880FDB22D4132DCF8285FDAE8165DEEC5AC66AC674485F38AC22AC79
	B898FDD293291381518C92C1G3CB33FC43690FFA5C079D381D6F2BF649EA3727EA93921372829E9F4BA4F743BCC24A4C24590713307769D2B01DF98C29FC718882E9752E1C3A8277A58D6151C86DBB7B1DFF48FE19B4B2131B395789381D68CE16C7CCFAB46CE432679425E9FCD530D1DA8086355C3ACE604F35AB6D4E0A53209FD3B01F6649C76715774340E22CDCF9D72EFEFAB06825BDF228A47F44C99150EFCCD3AC80727D0754E7C7D52B724FC3DD02EF00038F368F34F31F2964307C40F04D17B359B82CE
	3278EA005979F2EF356257C2C5AF71BF99A84918D4CCF984E197402BGFBG7E28687F8BAFEB090D48C8817D2BG0C674D0683GEDG61G89GAB815682D4DCB09B9A81E4009800548BD8A7EF77E62A6AF7A4DBB6AB36286D507AEA17725CB30ABA389C404A5AC54B0AF5FEA9E545F23E26A3F31B727C637F0372290145222D1914C59A109CF20364129C96F871A8B4D7B3A93352DF76B8F91E810AF7C17FDA8F9D1A0763AFB4931F08326C36F5754BD20BA44BB60755C78F273ABC7DDDAEDF570C3AA6B793BFBB
	8721FF657CD14D24EF6AF6BA9C92ED94BC0BC21FC27BC649AE750151754EFE074C7051ADE44EF8C69AD27A661816F58269DB6D7D241FE77330C41154254B8D73AF779099AA045AE152EFDEE3582172519A08FFB21FAC81DA45B076FD03EFE15FD7F16E8EE1F107ABCFEF8C0F3E6D3A3C70E1566FC48F7B7ABEE65335A6FD5BEF20FD340277B0DBE7B6903FE92608B4BE37893FED020935E06334AFC5877C37CA6EFD0CBE0B1673D03FACBB9D1F3B1D7DAE25EFFA7FB8511F8CF625832C9A463E29789BC24771C069
	146A1DF6BB2B82D2660B44CFC72D25CAB957C25E520B4CCF342C147EBC2793302B68CC4F5AEFCAC8ACC8E77BA59806494075CFDEDF79662D9A1AD929BBC6B7500502DDB5666D7E0E687C4A06737BCCC5DF4EC5B6366CG7A6B002F8DA3FCBBCEA31F8C78C800446F98FF56F74AFC1E72E5F03E88C847067978D24354FCD1E7102F648A50F7050F2F718AB5DF039D79E2G1F2462FB460FAF0773D5CC00F950849FDF6784B5DFC29F724583FE310AEF159FDF27837976DEE9B6B4DC69636B3DD24DD76AC43E44AB19
	5F522EE4FC19FEFC4BB91F904E68BB14E7D5381AEF73G723505B3F932429761477784670B8F77559761DB6C47F7FE90791ED17832F465FD64C5BE63C4E5DC1B684F475A550BFBC8DB27EB31536200682D748CC2FFD5D37305EA8D16CD67D1247F2F1D08EB2DC5DBC85B11A19D8990B7917B0527B761DAEB6A19F8A16339FDC541560DA993199D1993FD7DC2FAD65DBA56B607DA094E72C9604FC958AEA71955733DBA0F4759ED23D3CD25CDFCF8DC5DEFB0DC4D710D706C3139BD72D168583D58FE8E9EA7761239
	2802D66666AEBA9FA37DC683683DG10B4096067DBC9FFCA5A92ABE777FEACE754395B044EE9B3728CC14A19B30915EF47D53EF26E3D18D3487C7FCA65EB3A7ED777D638EE9A7E075A37A94543C1F5B6A93ADCAA1D49CED391AB63208D4B18887455G2C1FFC543ACD3F9FD57A593C534487F22F4D436CF9216E2022AF7D2E59C2F2D1E41E5E3A3A7776683C7288E6471188D5DD4F6A4A4F84E0B6551DB6AAB1998DBCABG58D8F430C3BD962D3F9A6281408450FBB55A9DFF7A83F80A34F32EE40FA6495D63F477
	C9165D442B156220DCC947774ABACABECC5E77E09BD8340D1443E337F5C3397BBD7B2A1873BB6AB4229A034B7AE40F1979E476B635CFEA3FEB5775C9C4F1EFDE58554A3A7AEA1FCFB227F465E7CFC11F941EC11FE4834F5EEB50A7EF687A4482F46FB53847D23B73B33FB20D29C8CA1BD8486D088279E95C0E43E7756C90A643D8B8996D387E2FF5E84717B647181D4B5BE71F9DED598BBAAC135116BFF874EC0981FAA237257270C1FFDB46E40A32066FC3BB52C03E9009F694D954F6D4835E829081909309F6BC
	F71E3463F3F5DE1B273ED76ABE5365740E49069FF19B12C0769AEE4353121E8D95571A8D5557228D71540621FF59062B9DE8C393480E3896EDD82E6B0745C0DF49ED2820B6F87FE59BBEF5228DEBC0F6457730DF08F3123E5F8102C4BBD1026D7D276525FE787ADE51E185DDA86FFF393CB610B5154BEBF7913C4DE113ED225DF6CE5A3D0749FB367BB53FFE138E1D9DC7D5FC3E787DF8GE527035C026BD0764C01C20CCD816D14E4529F77533D1296C2E9943BA47BEE5EAE33382CDA10A3DF07FECDBFE0D06CB4
	5B24C19FFF5CA072A7G6FAA2E5B60A5F564F1763B3B2586D1D696BAACCF07977314C5C13F9B05BA7EFE4C48FD58ACF97A6D3232D6775B930C847E58A85F3EC3D2D4603E845BABFBFFAB111796255D4F7B0462B323347BF90FEC417D3C9C2035DD0F7BBBD9A7C95CAD06F486C0B600F18A741DG0DD3F09E11F6FC3F90E27F6C28C306E906E8EF71DB2861BED6AB719B2C2D147404D662933A3692F07DBCE0A5FE681D223F8F2A87ACDFB84D97B60AFCEC5C59F3D23DDFDF30650BBCE2235E5CA0D1113DF80AEF5C
	E13E1B55C65A05F00359D086F0G209140FE837A2D68445ED0FE433DC445E1CCEED1BB119B89F262819683BC831043659EBB9132BE8EFBF56B0379EC5896EA8FFC47D4FFFF32FC13C3662B5564E3657936034C2DCDC0F381C4G448124812C82D8BF8D4BB56B4421D0652A430D8213133F56EB977BE86F6727357616CF7337CF3F1C5F8464FBDDDBCE0D9C56460BDBC8F9F55686EC5CA4B45A574BDB357B85E4BF5384B2FB81589A425B01EB0830E938063850CE575F10DE8130FA9A36619FF65283319BA18E818E
	GB48148GD1GC9B722FF33CFFC9AB2EE48C0E1F256836E882C15F5DC2B7848F0E6EC3E7DD3C0BE2DEFD75E681FCFB957627ED8F3A37A63DA6A0F261B40EFGE1B7613E6343E3346D87A1ED0B3EC94F366133E8DB62CDE81B1D5A369A52C2B42CCD2351B60FFDEC36DD9110CFEBDBD5341EED9F4BE85B11E834ED8A9D1743A09D8D1048EDFB3BEFEC36FD9810CFEBDBDA34DEFB59FE68F57D7652ACDAECCEECB42C4D740BFAEDA69364E6C7E31B794C02E5B5CE47323EAE1132B6C03A8DC01E0EE57D59984B7ADC
	4832C6CE572B078DBD6B15F247F15B4EDB08ED99104E86B04EC05B2EF30C4D36DB8272E9ED2B1821E75BBA89ED2B1D0136193A69FE8524A3816238ED754E3159F6ACA0DF25A61DB2C3AF5FABE354E7F0055237D2D75F2783E353774F00FCDAFDEBF4753DB4C6FD85032174955C2C17EFDEC03ECB53179CD2DF192EBE49BBB6FD1E00FCDAFD87B47A5899E6091144F2A350BA81982EE5B34105C3BA1A63FED8CCF0A910DEF9B3467607F46E1C83E9E30C59D09E0378BA0A27FB8630A62B6678A7360CF49E1823320F
	488B46477D922367074C91F5284F988BC348C7196AB41B973CFCB278F90EF0CA7FBCE75DA97D739C6329AD8C8FB2542769C8AFBDB572F9CF59A93D731E57E8D9FF1CC76A415D6F10EDFD92D52B2C7B3C645E8F4CDE1A7B9DECEAC3B4FB34F3EC816723DB3B483EDCDB8C4E0B961C7AC0881C979D3658E4279B0518A4517DF5CF8F1FF4363824EE34790E36607ECBEC53775F7DED7A7ECBE99B59FF29EDA37B2F38BD38AD5B5B75EDA9EB5737E5E77B48363C539E4C96D6DF3F1249B9E740D95CD7E21F24446EB6A1
	4936C161E9121DCD7B38ECEBC770F2B8BA744BA1F7103DF336AE575AFA1E6E81B21AEFD6FBD4167A08B94AD9A34DAF0BEE2B94D05649B4BF23854BBF35036CA3F459ACB637F272457C706476E26AC70FC7725089B659B4D04AFF71B4167F306C2CA7F1484664042DB8A6FBE3F0CCBE64A1711F8869B400CC5E8F8DF7682D4BF2147E44F88B74E5377877ADEC2FA00B4ED18F5C42768AF2B97EE50ABF284233BD04896DF8BF2D91E80937E07B49688CDED73F6C542F2B67BA0357D5D6E7683AFA39B3F8DD89E2683A
	5AA0860BDDD6AF074E286BE53F007DF1DB07DEFFDCBA7352779FF89DC01E5A19FA75125BC157B5B335753211625BE6EA6B6586DEAF3DC0CB1F097BD4AF791561GAF430433FA71D2F5AB3325811E2D376A5915C97558EF555A759C45FBEF555A6566F60583ED752DE857BC3B5A2E8F39DD35321EDD0D314C16CEF84A31FAF62DBBCB74C444EA6DAA26782931DA3B22DDE8D7AC508AE6717D4ABE35DD9F7136B8DF443658BB8B5B62F7F4FE9C8B69E400953330ADB6D8C899D64F7237F32D12AE199DECBFEF8ADD27
	D44C561661E60A2F1A2DADC30504E52885DA6CECAC43D6073A8C9FF35FEE6E5173ED419C652CEDCEB03B1E27EB143DF334F665D0FC759C2DDDD7D8502E8620454FC13B9E77332B1A5BE510746CB246A9FBEDF1416CCA237EBF9027356BE90ABF9827356BE4B76F3B00169807F61DF32A6DA2638F439FF0EDD4632B90AF8F28797F46795F9B74635F0778DF7863BFC0FC0C578F3F9F71EF9B493D13AE271BF6E86137915B7FEE4435EFA9247782348258EFE3EBE0166F74E6124FAE8EB97BE54CF745E64C178E69D5
	G4639B0964CE579585C691EC1B2E6753845BE687E6824A6FDAEED534518379A520DGF600C800043928F3973D3F47CE3E77A976EFAA7761BED0F02F3713327AF664F1CDDF69F76ED2FABB332B9A1E0D376358323D0B74733C1FD6668EACF66E592A0D9D4B6D3EF8715E9EACF66E2763E8646DACC68C9C1FCA7189AABC0B1DA394CF5269C02B0A47B544D43AA7B59552F19C775B8EDCD7A4F2DCDDA74189F7188D25F7E0BF71D23B404B718755FBC47BE86E980E96180ED9632A031579A277CD799D68071FF7209F
	76F25CE6AB7A2B1A7B262843670F92557B744B601B851741B7534F22EFA2389FFEF796FDB315631EBD063E09653EF9D695AB4F2B5E3D1741B757DF865F3448681BAC6E07E499FD3316633EF523EF564FC35F984F7A7C712A6AFD26EB7CFD7340E55031C05476F72D73FCFD1CFDDE307E6EC5BA76054DE37D5A860E9768D89D2342337EEEA265A769E420155D09BEBFC577D743A0BD156362A8AE9E52499CD7A8E15D245E09F593AD796AE3166A7D9DE97C7DD6BF2EBAD819F775209FCA93508F13FA508F65896807
	5FDAFCE57F3D6A7DAA4B787BA1F6DCF530B26F14508FAE6E072BF8BCDC60FED85E6DAB7BCFD46F1D5D636F0721F155415AD00F916F0C25C3198B6663FC2B5D7B06207E164B5AEF2B29B159E43BE4B9C4A7FD07A451212CBB6EB26237DC3573F15D913B096E4BC3BA92A0EEBE5E2F737E956F3AA54C1DAD2C78CF7DFB041926613C14794ABD42792A3BE56F609D2ABBB663BC396CAE3341F4975A3D0F1EB172339AF243242666F3374DF2C834B652D9A73379B7F42F15640F3C8BEDFE231848CC05F486C076DDF8DE
	D3DC023C463B4D06063B695D1492759DE0D276FF97585EAF23BB7D68E48D7FCA2E1AFEFC0B1ACEFC5362C73F5A0FCE4A36A70F51199F176DC2BF2EDD16A8586F8B8F7A9DDF645D4CFF094A33FC79745C5D531B56A977C4FB025C9325B3E0D67E9E894F2BD65F0D3EDAC657F56509B09F82E8CA44B61436957730FBB96ED0A9362B48C4ECD7A99ED23E8CC82F85A83B8764G345E037425141E245CB9A6771349DDE6BB27FF6551368DA2EB24F8A732C7222B6DE0BA2C81BA345F4AE84190022E7E5EC2D98F49746E
	256415236EA1D73F5BED24F657AFA0FEB3F6B6A879D66A583EC1296FFC85D6838F01D7957C5AFBFCEBCB266BA9192C3D7AC43755669084266F972709BE76DEE0773D9F687359314B0AF6B4AD883423A8049D1685BEBB186C638E1F1E471CBEBD2F9CC3BDAE9DBD39A174048768D96D7269A9F77B7448EE54932523A7BF041EB8159E965FCF0A9873098BB0666B3B79394D820C27173B49F7819577C20C8118GDC77A2ED1D0550A26FE5F758EB15BB6F4AFD162063G11B31ABE5F24703B14E785579F97D447251C
	57E8638163937D49624FCD67E09E09FCEFA1D9965FCB7BB98B0E9D097762FA7DEF12FF1B2877786A2ECC423AABC8520F11FCA1F85D6DC85246488559A77B169E14DD21A33BB0046C6AG5937767BE44F64F6B768484E8FA13BD5A51B55175D986A0E13C5230765BB97B21F76ACDE79B6BDE4BEB50401EEB6F7AF25637454A46D7E57EB949F1B245D7F1A3B8947758420155C07F707A68C503DF2C8C7814483AC864884D8FB9F6E414C6C2A92C25C9BB2B9DD0D5200E4D79D9331725D3DF9345FDB33FC8F0EB15FCB
	234E477CF6831D23145C2F75E78C45175D2F75672EE27467EE20C55E0F6D3D34E4347A593C6311A21CF7A4FDFF1E10BC29A168378AD977ABFB498F30E7F27A74DC72E475792A175467C12035810481C483A4G24BE007579EC17F2AF119CF1564932585DCB3FG618726B6E95007A61B022CECE7F477EA1FD16CD823BC05E4DF7BE0BE7C0CFE8FDC1A2C756D910AAFCF567A76E93E373D97E8D1GECBF73B77C5EDCF632321F399056D68B595DF5569F1CA8457E20FAE168FE2C91F8BA97327E0074C372C26D583E
	3B074CB1DB34F760B97DE77F586ACF2FC286C677D617A0DD75DD8123CDEA5C62C72B52483EE0766D256ED776CC5F777B16015C03A2E58F833B27AE443540D32754F7DBCBE6587330CCDAFFA4A8BECADE68DB8B303969271DF8663186E8EF886C1BD43A8F237CE3FD710BC3820F61DF98B1064B6E1CA3B47C24A16F2DA0FAAB3E4F74495FD77DB6006D4086265BECC55D89C0E7FA3EBDCFB4B0FAE5B352D7F1FA58859F7DB6B7524B96A1FD4EB02133B8FA015FA5B3ADE2F1E407E758A2F63F03455149EE0C2368C5
	7AF1249E379200E771A2DF9C2DDC240D2309DF10B9A21D7C7333017B3EA0EB0BD675FDFE4E9FC3E9A7F5E94BA96D040646622346225DD3A7E54A84FB329761BCA056C267816EFE0955DB6C20BADE2AB7CE507DB61500B1C529AF6599790769CA3CFC496F103526204F7F63BBDF1DAC67775F22B83DB057C7FF0A67CF63745DF9044E4E5097EFA3E536B96CB607D44FFED5C140F962D23ACE969EB49BCA9F44FE784F34ABE26FEF295E93B60D7F7E42E3632AC3390FB0907C4CFD4A007E19FB74007EBD0F1983A35F
	A919BB907C9B6C79836AEF3015FD7AE21F3FE72B5E5FAD9EFF5FB70C2B8EB64666109FA6924AA156EA81BA81E4000887F1FC7DE4F027C047D71D31753045CA6EB410DF9211513F2FFB035765AEEF685F9F2924E709DDCE98BC7A08FC449F70921F48CE97C5B25C5F3CA4865CB6EB2F4CD8957CE7DE52066CD28FC533F6B5013649B8A8D75203387EAA65FB1AE99CA7D13E9D8F417A6BA1ECFF07FBFC757E156A7DA717E13FB7EBDCF530B94A1F367173E4A8F378C33827A5EE43BD2D1407F0CF67BA4A1B8DE96343
	60330711771AD2FE6F76E13DB1B66B3360636EE45205ABFD27358365488F234EA51D74AC935229G998F63FCBF2FC560F57E14177C2606D9BC2DFB636D8BF1C07C3A6BB42CA9CD22157B76D027FA8E188DF257AF9EF92D4C743EE7A263E99970D683348148G91G31GC9GE9G99G59G46C7E0DE87F0G6088C0AB009DA06C917E9BB4677684339D104A46A4EDE8EE51C61A1AE27FA92B5A7E29A0AF7E11116DE77976B72B7325C01E951714EF1EDB1DEF95644989110F45571F3BB03E0CCB4C0606A598B3DD
	DD98DF9716E0DDC7D15EA4C827832C623C133BB1AE56AE511F43850F2F8DE6DFFC317D85A26740237C37436859119D5261GB10FE2FC5DEF263F698669950FE2DD2D9EFEF7043AEAF2BBDD12DB9EE2D87A2B8D0A6F1A7D569BD9A02FA075D2FC7EEFFEF7DDF6C01E3D1714EF251F3E03104794A21FF24743C85B9E5944677EFB3B55574E047C607DFBD1FE687E3DA41FFE278F7D32E948255CDFFBB51F4CA71DBDBD9E090FA3FF4EAF6138231EA62747C66E3DB19B27733D9F36BFF4B76D33FB21FCF2AA4EF95F65
	7BB461BA65566E93C683CFC22AEF4E3BB8D5BB673DA011586AF53AED671C8ED9345759EDD687998D85E6430D9246F2FAAA0E9BEFEC9B5B3A7F3DB1666B99F5BE36F64DAFA5713FB6D53B26B5D27C7A4734EB5AE9FC3CA8851A77B17E2DC729DAFF8660339E3B14D835875C79ACF85C17EF476301B218FD9F0974BE5763DA3BBF23786A4735F627F5727BDCC00BFF9C477C93F4FDE8FC0271B3DCBB45D5A8B89613F5266031FF4494BA760F197467B647CD01F31BB6137E5C264B241E5BA8770BCDA4E62DB0CE34F2
	BEBB471D603833D4A6419D37D964DE0E9F24F6917C9109A8C47CB035EB1062DA71FBD7956E04023B4AC45AAF94991A87660DB411B65AE3335B155FB222BCB687B4E76E0FE954C61BCBD459BD0363CE703AF0DB836BE7502A2E9F067B22B9106F6B66C03EC75C01FC0F3B55FCACAE4B3B46568EAB47184FBA6AFCAC5E8B58BD41A7346DE09345B7BE21ED87B772713895E8A9CFE07B0D6AD66BAFCF03B9CB5A25345FC10B1E5DC7143CEDBAB218FD5751F157152635BB1A62AF24E96DFE135F470CG5A1AB49C1397
	1909CC3E66E3432E92CFCB4C74772170FBFC7A13B1AD40683032FA37C90131601082E3663FFA82791E6F89647BEF9DF9D9FE7258187A8589EFE5ECA969A14F1DD5BB15B9CA9B5FDFE8FAF234F3693E13BE397325B76129BA322DECA1FFF14966292CBF7C1569F0B32F478D7493F0763ED575FE733671DF67A40D2B8ED68FF3CB836B664E5240BA3C122EC1A24077B1CF629871E4A72E9597BF096D25D6747968306AFDF967787B6BD7632A036D870AFFA56DCDE9BBF448E078B26FA69F9E078C452E0D2A7BAF5BD4
	6FB1DD636F137B46D5870B035B3B83E3A821BBB0063EC75799A5CBE16D3794E368C58B46505E2598C353D47726E62A5E5F3E8CF7288EDE869D225F7A658214BBEA69252CC3E6F4845E813F071F9744010C94C5CE56D2F667034D7BB379FD08754B744FE755737EB26029DA46667D74F73F16E9677D37D97C4F1923CE11BAE23AB672735AA69DDD216E88D8D43AD8FCF8F54618E13FB1060D03BF6D40F15085B26217613DA66BCE3287426EE3B97BAAEDDDFD157D83FD15E217DCA941023426662BFE3B3DD1F12940
	563E5B691CB893E464G142463FAFB8B7FCEE4F77AE847201F3711F7A667933E975E190EFB61F92A3B3EBF577D5EABF4DC7DFA5479585C629ADA5E68F46D1C63860A0FCB574EB94A781AA191E825BF40F63B5D6F7E2A977011BF38143952C13FF5F9AC64C93A24FCD11DBE1FCDD73D6F3C8CE34DA7632A034579FBE2E06CFFA086762B47F566D7EDFE73AB765B7B1FDD7891A492FEAD944AC27E3B7B2ABCEE21187F1C54D13A8C499C58DF137F4BB979F9C241D1E19DDB169D958A485A6A28D0C817DEC705FCD816
	F9499D21BCB548C25E3B7BBCE7050BB4B2882AD081A0653F876A54B4F5367702E64BEBBDC17E1C04BFA7CEF2A63AB50A4FG2C9000AD50456A737EEBD8342AA0B8D6968A690930AC64D685D8C981B84851DF0DBED5D7AA8165F70F3ED31F7565CC6C8EFA56573A0235B17B8A6D6F9E7D7AA23D638B785495381E787462FBC2703D6416BE51EE17C57B19BA07056CAB9F9A12A55F79528B9709FC8F72A8C465BB4F0B446E532ABC7F87D0CB878879BFDE00E79DGG04E2GGD0CB818294G94G88G88G410171
	B479BFDE00E79DGG04E2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG219DGGGG
**end of data**/
}
}
