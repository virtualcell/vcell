package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.SwingConstants;

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
			cbit.image.ImagePlaneManager oldValue = getimagePlaneMananager1();
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
	D0CB838494G88G88GB2FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D455157E840A3B69965B3822DFEC29224D36299F555825B65BCDAB5BAF55FC95D3203860B656ECEBD76C07CAD73438FD791384D122C4259AED24A8C1E28CA4C20C61AFA418C08801CC428C89A1134C1B191779E5666587B201BD775EF76EFB7366CDA6C9B7FE8B5F49FB771CF34FB9775CF37F6F9B2164AF738BF2F2CB84A127C0D07E6D4B91041EFEC1F8E7652CB144556E48AC92B47F5E83F8
	D578EADEBEBC1B0116BAA73328D8E848AD04F48F24D74C4EACFA935E3788EB1C7E737092C5CFB3506A1E3FBAB8311E3BCC28E783CD7BCC1B60798AC09F6048B3F987517FA553D6859F276091F2F285E1341C705CEAAAD5F0EB002790F0B5G4D650C3F8F1E4C00D9F5358ADF4F75C9425A7B8339AF93B99A1988E9CA5EEC65D9A6BC1F933F1368FA005AC97214C25A84C08A1FA7BC75FA99BC6B5BDD0E83837D12DCA9F92C8E312E2E05BC5A5DD62F588E60AE7C10485852D9C873884292481FC0F189EEA237955E
	EDG218F913F5BCB70AB60FD8DC061AEAC4B7E9D750A2D6631FF96166E7164EF0ED21632DD2C8C4D3B5432B4FA9F497DB3757B531D443FF1C0CB82C885488290AAB20B4AG5E26757D1C69C3F89E6E170F78BC9E1F77305F5F603685CE78EC229B5E6B6A002260CE0B833E00CD90D81DBEE42AB030E7A2E079FEB565FC4CA7C955D83F49276F94D65D7D0F399B8CFC32AAE9AC37360279223DC27589533D35B31AEEFFC5F87C056BBEA3E8F50FFA1767959868CE68DE1E9727481927513D240254F521075475A2F8
	4FD1FC4170A594DF72110AE731F8B30D99122EG5A5C0FB0B64C2F20AD2D79378A453BEFB474433C1F44672E7D08593029D2EFCB110F482E2DE4BA5F613614D1FCC803E7366C23F1CA52E9C0CB7EB8330845579F4F1036B92FAA33A885A083A09BA0FFB74497C04DEE0C31064E0FA799E36D56005CB0A439EDE2C0E0F97F587986BC45D1B9E0ED7186E52BF7C06C3286A4EB3FDB04DE087164F7919F740765AB6AA231BFGBABE973DE240AACBBEAFC837FAC4D98C84994FEBDD2F921E00C829A649A302820FDFC0
	39EFF5ED01275B9A14BB7DB62BAC2A9C0C3E2D0B544930358BA402GF8E7FEA93790FF19207CEE00907743ADD45EF1B1G3DC5DDDD2B4F679A721BC8C9040C5D444F9F533EE3AE3CA76DC69FEFBFCBF045FBB20B4A77201C25DD2FE964B4CA8344FCEB60A23631D7E8CC55833F99E0F48F464E6F4D98BB0DAFFFD728D8F603E16C9476AC4F1B3B0745CC529EFD9BFA0B4AA676AD815A1ABD58474FF71B2750266B7704373751BD116D4F2C41B19DBF18120EBCDDFA0381CF01661D797B2B5D24FC455519C595G2D
	5568734DE74AD88CB7D803A2C66D49G841CE8D3EB00593959D72B7835C051CB7C7F92G9913B6D39EC1F89F60F5001DGEFAA7A5F66E57D004A30017EF8009400D5G79B5E0970089A08430GA083E0ADC069A719C50DG6ECF30CE76FB33B475BB11AD2FA836E86D507B6AFD6539F38AF570C5GAB6BA8ADAB5679E44A0A65FC47C066966579667F0372E90145E2011914C59C963DF22368973DB6F889AA34CDE6D2E6253F347BF81E57A85E8F7D6B916850029C3F55CCFCE215650054BFA40B9D22ACCBDE07CA
	AF233A02C37DFE35EBC6DDBB4C444F3E9168DFB9FF051974CD83BE2FD7240D02E7D1681534EF945D22870CBE629B724A8CDFE3A1F3C617F8D1691BEBADAF8A24EFF58F11FE1E4D4396DAC9DD7A83B07F8ADCA4C305107D8969B70FF6EE5578A881F0E4BE39881E991FE05FB772D76C7B36DF7AA7A16D3CBF57E88C1FF77BD7F2AB7632FE6750DE356FE3B66DBCEBEC7B47340FD670C1E66B579B095FF4D3C49A1FDB04BF344344DA10B86D04558B7F9DE2E0B723FF131633E1C816FD5E4F833EA13F52B7BD50C374
	7701DDB2C07CDE6C1B3EF31650F1BCD0BA25A3BE371BD58150B27692BF1D7714A865DCG69543D4CCF342C7B883D40DB2271C549BE154E744C6BAEA631A0DE9892E198A68357F1DEDF5F68FECDC76BD26A0E513F55CD4A64560CF9CB28CE1B8F6777F394FD73773131A58D1E6F82BE3B1670D57A112F9452B5G66DA46BF2FD6194FD33E27B9DF552730CE78D465F3FF2A653B638272AD827C928D5F5AB03E338164EB2C4BAC6A29D3792E2A53723D92C43E8C40E7EB788ABF53727D047335835E79194A979F46B7
	ACA35FD2402F56709557EB79BE9CC2BEF3BD731B3F1E714D89634B66FCC98ABDCDF92E88635B9EC23E7C7DCCDE69FE46D735DF4B778867EB5F2F569761F30671DDBF0EFCD7A9FC738C6535DEC63E147D4A382663E36D6A191D242D53355820F558BADA999C017E2A2E6E18E68D36104E23C87F1F3D9F57DAA3F4DCAFBDG318350FAG7B0507DE4635D602ABC9B0791665C5DBB75A8EB0BB669CD07B057C0C11BC56B6DF6FA5BA53013662G364BEDBD5A795E61E050B7A051292652A69E6A51769B8C373CE7A3BC
	6DD2A0A837C047BE0A6DE7D58F31174CC59534B2B71FCD67E3245FD88DFA379F0498BDC87077F9C8FFCA5A92AB67C0B516F36E255B053E9BEE4EFDA9CAB9679FE465CBBB2816F3E94BA7F94C7FAFD43EE368FFEDEF056B267123DA5F26EE9E0F2AF31D22EB6BA1D5E76B71E773D8993F6C43B21A01BE970075138FFB36987723CABF3B55CFFCA0BB25A03367694307300D5EF92B30E8C3FC2E513A5A792DF939690A9DEBB476EC4FFEA437960059F4463FC90949C307E10E8D4046225F6D500EC58B813F82E0BDC0
	F1835A7DA37F9EF8DAE967DC499E6DE2406E8BF8C4DBB971EA25F5C42E2463FB65E1CAEEA26FBB318D7CF08BA9C750AD8DC03907023B2B18734F9F5609EA0DAE6B331DDD4CA737EE517A247ECA2F21CF66ECF66616B7A86B6A8655A7354B9E49EDDD06BEF97B827AA491F816B620CF8E9DB4724926A330CEBC02FBAC755B8E0615E9DAC5D25A44035C0EDA106FBF02F6DCF69A59118474D4EE47759F9CC6BBCEC85EE9BB17374FEB83E84BD250312991EDE99BB33225916866C634253269D038AD53B2C5D9438751
	8EBF48CF62F6A4C9DABB32G3F29897ADC0046A63463F7E3249DDFBABCAA850FB84581D73FEFF4DAB6BC41ED68815989CDE80355E5E4C386C09637A1095AF071EF36619BB25A300E1473A85AB0BCE8E40393682123E843F6EA43685FECC347905A10G32B30EE23FF06F90697B3DA0486AA6CA303DBFDCD69206BF6234FA9D208B653DBC0C7272BF4FAC2A7F9C654994AFF9A5D9323A25CBE279CEA66FB7836F04751BF468BC5F2261D36377C488E54BA0B7194BCE8995E0EC8AE827A8137EF8086E15F490CA2B35
	DFF41773F67962B84A4A86B9254D68570C1A1794BB3BA4F1C4653F17731B00B73E99F53FF71954D150B79498909B2D323250E1F906AEE31E45403F1E6BB8F0EA937761E9B1B86416593A711E30BD411A9618B336287B8EBDAD117B92EC2F6C89BA1F70376877731E2578B9AD7A7D3C71EE5C4F1B8F347CE3383F935FC7624E8969B800C400940025GEB0F61BC42F62EDA0831FF566215E11AE1F5F704AD54F09F2B17788D56D6CA7A5DDE62933A3692F07D9C6FA1FEA8BEEE3C8FEA84AC5F4DB4DF79947231F1E7
	67D96DFEFD7E2B47F2090DC6F38333A25BF9DC9DF7186F767713F611847845G2BG56821434225FEE6B2D0865B75CCBD49C46645EB6C06456001CF6GA740D5G73395CA7FBE356C75328E1FDB01FFD4D97EB8FBC2DB55C1FAC5FB7E3664B5665E365F94EC666566B0136758474F9G2DGFE00F80005A730DC7B4FB744AA57E15CA8B079786B917DE29F6DBD67575B1BFEA25CBE63F23AA2723D2BAF27CE8EEB631BBBC8F98D5686EC5CA479E8DFDF55235FAFA07B196BC1E6F19B2C5768FA613DF2DCC3143761
	9A620C0D2E3FA1BD8AB0378D5B3057CE7C1B846945G2BG5681141E4CAC2A8568B9097E1DFBFEFF4C38A1830549F784F04D30D6B6F02D622377AECC4F379FC764537BB6F4B2BC9F734768C174C742C974475E9D449F6BA0DD5C1ED9D45E0E7B0E4B26E95BE3B1EDBB54EEE45B771DE81B399DED7B7281E25BDCC8A78124F15B2E8BCC4F369BA37269EDDBE1E85B6CB134ED8D376D8187312D7CD4E651A1G73A93EDF9B1C1EED47A37269ED731FB2EAAFEFB43CEB5CDECEDBED128F9B8DEBB3858E23B6B38764A6
	1E42B613A5E1D9D3CEE1D96F9CA4E5DD8D697C5350A6CEE3D91F1AE6D97F94332CB5270D6A61A6D721D26E5653E85B9A0958968769C40094EE5B6D7274EC3BBBA21F5E368CC35B6E9CC45B3239EDFFF0527D0A0E4C229A00568E344DB2B4BD5B9C9179AAF5E9DB07D13EAD43535377DEC4BE3D3E10213E0E507474D9A3726975A5986A5BB4BEBDFDEFC564536BCBB65417B6CDFDBF0F29EF31213E314B5353F7DDC4BE3D3ED5BAFD6C8CB3036E3D2D85DA218956B6943759CCF09510BE44F17FE0A1B89B24C3A65E
	A750B972FCC827G24F37C8B94CF778C481A10632F6D1E68BC1048417794D3382DC610EEB272C36204BA34E70C85B16423CCED1A4D8BEE680BFE1E1358E7FC1EF3EB1F71F9CED25F2B8C8FB2342769C8DF58B771F94F22BE23731EF7E8D9176712FA888CF9E549A3D2354A3AEF145C7B01594B69A1AF1B5A904DC17D9CDB4079681D8E32AF175F097322B66B9EA1F2DE54E413E4DFG0518C4EB60245D4EA71D9DFEF1GEDEE6A0F6E3F56FEE37F1D6EB7761F39FFE27F7575CF6C3F6F8CC437652E81E3DB968D98
	5B72230109ED7969C0B4DBD8FD7DD6A6671C91E7F1EDD60F28446E9661FEDF114253AE7B5AF7F359F9E274F2EC940D4BD1AC123DF336AE575B7A2638155354D9ED0BACFA08B94AD9A34DAFDB838EB1222CBBE8FEC60B163FCAA47B887D12CD8AA8A7DF4C8FBFFBE3B375E3B0A88669841BEC9AA865CF70E3791BE45F919207ECCCF6766018DC5609E3727517C87C1BA16D871853097D508765C66B32791D2CBFC901E7FAE7F85F42768A9E206B09D51DEC2FA0076317D27C639ABC5BC3B8E7437BE9EB01E66E4276
	B3EA0FDED7D76C46F5B54B9E3D2E626D316B6A86FB743ACA34472E2B5B6D51E217550B790236DE2A856C0F478F9A754729DD135FFF60F5GF9323B0C6A655BECDD53252F173BA9BE7F0C3EDE6AF7E03D9483CDBE037BD41B825AB25470B27C50E994AFAB4EB0DBD643D3B09B5915F3105E95B36B6D5AC871E5E63DDDDFBB00F6D5GED2E996DCA8B336B93EE575F0F995935D63125508273F70B11DD8769DC354A22372B19626BADFA3B9EF220DD4DC0CB36707D4A20562E3D3CAD6EF7E0DBACBE0BED7189AA3B99
	52FDG23E731AD7E27C44AB077EC381D8B1474223311F142EC5D66A272B24E6A4B300B62D71C5517A1E5904B108D3466EEACC3392CAD43BE6E5B2F3B0CFC1B5C2D1C35F5C7334BC675AC6D565B6521782CEE3DDD7FAD21DD2B01565E03F62D8A332B165BF5772011DDA9BD4C1E741EE8F6C9D44F2A9E3DDD810AFF3CC7EF579F1D3C6F821A799C5AB5F7C8EB9799FF987E5870C6AD3E8A71FF975272FF4A790F0C07716FC67C336178BD083F6BF2983E9A710FF512FBA77D3EG6D50AAFA096D4F737BA529E077D2
	G8170A53DEC8D4C72FDE0A6795C560B3EA199736DB7E3BE995271GA9G69CABEB6F77A5E8899336C812B873ABFBA29114F133C37DBB0EF96702FA57AGDF83E0BA0FBA5F277777584977EE457E174BD45C9E8577EEAFA92B3AA30FEB7A5B424EBCD24FB33B32603976BC0EADF7B9C8BF4F7BE9E56E40E24753230F1DCDFDEA3C1475C50B1D02B1928BB5FDACC6DE6278AD94EF5260D96C5CC847DD12160136228F57907B699EDD3D955647D644FD3D9C5795E60E4B3AC2F0C910CE35E2BF7110CD606578F74D7B
	00EDAAF78C278ACCC7E8C6F530B2FF63A07AA61D7BC1B804FED84AF1972B51DFD95CB78BF7287E7801663DE1474C7B26FBC6F530B2E7BA51B7D57D6807EA89FDD34FF1C5ED681B66FE744D06032ABF0AB56FB79D18F95F5CB523BAD819EF9DC35F4C63FE5890C25FAC603813A3681B055CB7CDCE559FED1A77DFBAE65EB7EB3EG9D8D8EFDFFA78C28FDDC49C0347E6EBC9D934B87D83FD64471EE0AEF5460D9FF77949DABC93A8FE80B8750676BE90C165BA0C6ED08EB22FB076D106E6338BB8631EE5CB62C1BF2
	17DA9F3BB56F778E4E3C5FFEB123BAD8196FF1219FD2399F5EF0239F52399F8687553287B56F6B251977436BDF009F6E677130D5C4BFFC6CC2BFEC9779B7D912DA76D14DFB4E97600737E6D487EBC35B3A704E188CE5CE96F13E55BB7A97C17BAD17E3C82A2BB3C932DB34B550C9DF03E875AA6B0E41AE7C16ABDB44F5C74F993AAFEF073998C02B9D6F570DFE00F75D126F38D5E8F6995FA34CB20D675A6C4ABDC23BFA1EED76629DAA510C736445C0DFEFC73BF777158A1A339AF243242E6E7300E4EB30BADA69
	2C13597CDD3A57CA7257B8506615D4269B52F1G098EBC2FF940023CA900DB6DA03867AC5ABB40246C0F88EC6F1751F71D5552499A7E059CADFDCB3716CEFC5391C6EF8A2313326D4CE5F46647875EC7BF2EFFE021603EA7AE6AF7FCB5CE66BF33721C1BFDD3CE5A5BE34ABD45E76CD16E095299B0AB7F4AC1BC2F1A6BC4DFBDCA57FB69104E82D86744B6945703FB584592627E679C362B9A895B55CBE324FCF110DEG3098A08BC098C47A1FA91D5CA3DE225CCFA6F719CBB87D16CB7A36C1E4CD946FC476C4F4
	2D9DCC4777A3F4683F155143CBB1685A6FAD581A6387E3746E25B8AA578E12EFC41F7792D3978E523E3A3CD14997B2303DC8296FBC8566828F0157957C02C1F5ED292CEFC2E46D6531869C92E8E17A4649B5D465FD7E8575FDC9C035E3EEB55A314E400E8DB16C5864D26DE0321F892AFA2CC32A1E32B65433559529A7A7061E0A88BDF1432A1E5491D50FFD8475549A68490B2127D5230745374501B1EFF6E14C7F42494FEDDC98CF22C43E8B4800F49640FA002DEE2485A82D464D6E3013FB71644EBB23BD6B0C
	B68EE49958BF912CD778095ED794DB14B9FA5431E6B267B57AF84078C4FF32785BC27B1120C83E3790EDCEB771530703B8F6185D38DEFFF4B03CCD7CEAD42D3BFF99443ACBF69B47C81E903D6E525C7A9859FAC9157D119BE5E79848AE08A1BBABC276E7E32A6C23AE143D5AC0F6DE8C5902C7152D4CD33BE25DF15A64B1BA275B9BB31F7EACDE1987454C2705F2504D666ECB68BC205E235F7FFA18621BBD7A7D2F7EB3B82E1B00364803F7075E8D11F12A541BD9D48B508E6084188330400BFBB0F54EAAA1463D
	A1134F5FAA8E0BEE4DB111F2174ABC3D6F34AFCEB35FDDD34E477CF612FE9736482B7727196297FB757EFC144F1316G2D46076DFD09E52A7A593C63479BF05E11F26F3705C5339B23FE2BB04F276CA5AB4F7247EE4AA1CFD61F5F9BA77579B850727D19C565G07GCCGEEBF5667B5CE65DEA2B962BCAC4B5681A77D82049F18CA62080AA61B022CECD906FB35D779199D894AB3492F368F6643CE7AADF92ADF6F5BDE0ACF776BFD7BDB977AF6A950EAAFE496317D4C6F72FBF30997147DCCF826DFE0F757D9FF
	70FAAF7687D997E277E3EB0127B0407A83528F158674E3FBB9BDA76A505F01677415C7DF8B27D7A183230F5696A3DD73DD81234DEEFDB50CD625139D6AD277D22B15BD530D227E9E94A9FBB958DD9F403540035D5A3B2D45B73BF331CCFAFF18824CC7FD81F5AD4066263FA947B30F84207DC5E05F24968A6ABF5697FF650A40E37851CD9843255F39CDE87CE5E36EDF23684DD0741586D53D594132BC267B4755285B84F42667B92A0151CF35A1BD1E53CBF3D57A0DA3C8DF4C698709CB15FD71EB78DD3275C196
	C7A5B278D0E677BBD89C7D1793636810EC9CC75AF14B84BCCED90D23102C0F23D947489C11CE7E795940BD47485A422C3D4F4F7993A96D2CA16D6714E65151D8FC5CA1697754C9196600BD09B24E838E33F9C0E0C8E475B6FFC49BAF359B2FB17CB61576CD0AAF5215677A57EFD66265777C8E19B004BE3FDC2056C92F9B69351C7E4D97D57A883F9F67677445A47C15B37434AD244C1257ADF945A36CD795841CA74E266B64A44817BA047D7072B3A4AFFB7F15665DF9E66677972E1ED19D4AFD04D074B3774A10
	7119FBCD48781EC7DDE862BBA58D21685FE03704345FE03372F7DAD4FF77EA5EFFEB1EF95F8B16195441465804C23206CE07D84B86A89C4EACAA85289A4671751721ED829DDF8D4656A61B035CE9A03FA4A223FF6F9D0FDE17BF9E0F7D7BA37753B346FE9F8C9E9EA29F71CB4609CFE41F1FA299EE59B809010064F04A0CD5412F9CA7ED48AD5AA91A352BE7E91BEE05F2758C637A6BBA3E17676738B10ACB03F456B0363FEE37DA6FB64D7B132E190F01972774BB9DD385B6C7D956434F13214C95A33827754EB9
	5C53320D601E4E71F304B79152A9G991C77E8AF3FF7BBE2B4462EB998FD5C5DC12ADC69BB9BC414D3BA0ABACF5B69D9A6245DGF123B85F5FC6DC2354396BB279CD0DAE6B20610D37E356E16B497EC1D8D31A2C8E6E5BA7426E9AA5025C0523932F15195E9396B21EAE865EAC007C0B50E6G2AG1A81FAG7CGF1G09GA9G69G2BG56G8817E00C86A83F44FF03664A4EE83683D25918248DADE015C8D3D36C7FE935567EFA1057FEE9E27BD93E2EB6EDBE9B64991DD43E4491ED3EF848B3BFC6BE96DF5F
	66F7D4D200FF75A50C19279D98DF5B47302E4B68F9CD8F24E50078B164FDDB4238D8B0E6BC070B9EDFBB3B55787A8FAEE79557F99FBDBBAA89C16C83B406B03E16F7535FF40374E8886BAAAEEF5784F5559E70794500FC11E1692FB6A83EFBA6EC3DB18F64A507A663738137B6DF9A64D9BA29FC2130BBB20FC31E75B172317954ADDD747BC432094F7D7779B9351DA596C66F5FEFAB0C5D3FA797526F7421DFB6DD74AB77573ED7C8A6CFBE3BBDA872F1647B05459C5792EC7785A5F26F0D59780A8B77FE587E50
	F65AE7970FC3BB9C47B96FFC3E17D2B19EFB1F709070184655B92FF3DCBF677D2A0B4416539710AE793C3255FD58ADB93CE4B494188D49AE0CE5F99C470D1591F7BAA657677FEE1A79361D1B5EFE413F52BEFD4138FECD1BCE718B4775EB5AD36730BD27822D6CB21677F82FD6FF9C6067DD1ECC2CFEAE6A6DCE5664CBB310416C7BA99D9716DE565B7DA045E7DD565BED36737BDCC0EB3F02E3FE6ECED2F7A9D7983FF2CECFF1998A0E45245592BD761D165831FF41E2BC379931C44EEDC29663394DB5966D5C06
	613EE4A1B16B00F1424C7962B94E42F157D31984F7C63249CE0E3F115AC5704DA2D10878DB28DDA394E7463B729A1C85FF0F067E3E9094991A87EF6774B7086C125B2D5C1527BC12971AB37747BD54C649EF5558FDAF47D9F8DDBCD29DD9BF3F2E56568F43F95AA27982ED11FCF70FC472AD9A5172A9F7F7A26EA8CC2E9D7EE81A791E1DF2BE966F89F44CFC7C0A3E9D5CC271EB2F685B41877C7E20F0D5E6116DAAEC3FE512D6FFBA60D7DDB519767B18E4E4779AA5EF7E55515A6FBEFA6FF26B55FA3B8FD17C76
	2B75F67F0C5F472C82DA4255B8A62F6CA6B2791A0F8D3BCABC7D1A1E11726F7669CF46F4G2357416AFD23ABB296B63BA2E36612BB126F0ABB126FEB8672660549E3E36AE0AFF13F321F62A24FEDD55B14B94AC3FC7FE05D55D31DCBFF793CAA77BE57FB70549C59D6F6103F38E48BD69EE9EAB3B51D667578EFF41CD73E11523C1FB9B7736B9C7F0C6AE075505F9BD9B70E5E48BA6C25EB102AEBE0AEF88D0E918B6C38D6F4DE036DA5432E7AE819663D57BE737E9A19D19DECBF507A81E9EFCA5B21C38643170E
	3E2C62F148405FB0505CD53BCD73DE9175FC727F4EA7CDB32A034541BEA9B2066A254898FA1E6296C12C247398BA4F6F12AD65B134DFF3EF6A28667D41AF606E5063DF000E65E16B106D57E69655DEBB19F5481098F987BC1E1F9734028C1BA2E75E356C4E871B777F500E737E0586FAF0AF98677D0B01E74535EC5ECFFF776BDA7D3C7FD55DB97342BED2C7CC57B0BFE7DEE720AB569D01CDF17ABB82CF9A0C9DCF070D9D4A7792A20E03DBC1C6FB9C5EEBF2ECA3FBA06CBE164FD3A975FBAA0706BD1556FE39D2
	0485E9DDDD5B105B5D2A38D4E0EB5F27282CB9A0E3BE4022B8DCEF2F36639834A4EE2AE3501F2C641D4979965FCBAF1C05FB615B86849E9F77C64C7DA797D769D34E476696A2DD839C1A251FF3F8A93EF516FE4E71AC773599E82933787735F62D7E325919C5B533A7B3D7CA8E5B87E806BCBD134A67520CAF32667D85F1665B709BB3BA5630B8AFF4C4467E4B0E48FE753E41C83E74C1AD9F7B6D7D15B94BA0117C24D0A08BF93BF6D7858342E67EF3D2ADF49912B5DCDD177758EDF939C2FE0B70A2DB16358879
	E4ED55A2945025D70B10874B32D1F27FA9D78B32103BEBF77002F019C6C62E9261850C1477912853523459BE0A1AAD57ECA1FF4E429F4BD94EC4FA9F811F91D8A1821BEF08B5667D5B30E8D5FEF42CAC945093E1D948290A301282F010233FBAE32A21D4824A6F9EDD511E752D1BB590752CCF380E3531126B743FFBF4DDBE3D638BF877F5381EF8B86FA3A17ADEF207476AF64BD6376B3057C676159BAE4A22FA3E748F79C4FE90F99422721DE7BE31FBD01367FF81D0CB8788FB0125A4F49DGG04E2GGD0CB
	818294G94G88G88GB2FBB0B6FB0125A4F49DGG04E2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2E9DGGGG
**end of data**/
}
}
