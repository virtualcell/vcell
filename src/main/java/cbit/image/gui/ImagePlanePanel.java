/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;

import java.awt.FlowLayout;

import javax.swing.JLabel;

import org.vcell.util.Coordinate;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.BorderLayout;
/**
 * Insert the type's description here.
 * Creation date: (10/11/00 3:05:12 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ImagePlanePanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JLabel ivjSliceLabel = null;
	private javax.swing.JRadioButton ivjXAxisCheckbox = null;
	private javax.swing.JRadioButton ivjYAxisCheckbox = null;
	private javax.swing.JRadioButton ivjZAxisCheckbox = null;
	private ImagePlaneManager fieldImagePlaneMananager = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ImagePlaneManager ivjimagePlaneMananager1 = null;
	private javax.swing.ButtonGroup ivjButtonGroup = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JSlider slider;

	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener,ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
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
		}
		@Override
		public void stateChanged(ChangeEvent e) {
			if(e.getSource() == getSlider()){
				if(/*!getSlider().getValueIsAdjusting() &&*/ getImagePlaneMananager() != null){
					getImagePlaneMananager().setSlice(getSlider().getValue());
				}else{
					getSliceLabel().setText("Slice["+getSlider().getValue()+"]");
				}
			}			
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
		initView();
		//this.updateSliceLabel();
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
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new BorderLayout(0, 0));
			ivjJPanel1.add(getSlider());
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJPanel2.add(new JLabel("Axis:"));
			ivjJPanel2.add(getZAxisCheckbox());
			ivjJPanel2.add(getYAxisCheckbox());
			ivjJPanel2.add(getXAxisCheckbox());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
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
	getZAxisCheckbox().addActionListener(ivjEventHandler);
	getYAxisCheckbox().addActionListener(ivjEventHandler);
	getXAxisCheckbox().addActionListener(ivjEventHandler);
	getSlider().addChangeListener(ivjEventHandler);
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
//		setSize(169, 110);

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
		constraintsSliceLabel.insets = new java.awt.Insets(0, 4, 0, 4);
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
    	int max = 0;
        switch (getImagePlaneMananager().getNormalAxis()) {
            case Coordinate.X_AXIS :
            	max = (getImagePlaneMananager().getSourceDataInfo()!=null?getImagePlaneMananager().getSourceDataInfo().getXSize()-1:0);
                if (!getXAxisCheckbox().isSelected()) {
                    getXAxisCheckbox().setSelected(true);
                }
                break;
            case Coordinate.Y_AXIS :
            	max = (getImagePlaneMananager().getSourceDataInfo()!=null?getImagePlaneMananager().getSourceDataInfo().getYSize()-1:0);
                if (!getYAxisCheckbox().isSelected()) {
                    getYAxisCheckbox().setSelected(true);
                }
                break;
            case Coordinate.Z_AXIS :
            	max = (getImagePlaneMananager().getSourceDataInfo()!=null?getImagePlaneMananager().getSourceDataInfo().getZSize()-1:0);
                if (!getZAxisCheckbox().isSelected()) {
                    getZAxisCheckbox().setSelected(true);
                }
                break;
        }
        
        if(getSlider().getValue() > max){
        	getSlider().setValue(0);
        }
        if(getSlider().getMaximum() != max){
        	getSlider().setMaximum(max);
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
	private JSlider getSlider() {
		if (slider == null) {
			slider = new JSlider();
			slider.setBorder(new LineBorder(new Color(0, 0, 0)));
			slider.setValue(0);
		}
		return slider;
	}
}
