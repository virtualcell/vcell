package cbit.vcell.client.data;
import swingthreads.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.net.*;
import cbit.vcell.simdata.gui.*;
import javax.swing.event.*;
import cbit.vcell.server.*;
import cbit.plot.*;
import java.awt.*;
import java.util.*;
import cbit.vcell.math.*;
import cbit.vcell.export.ExportConstants;
import cbit.vcell.export.ExportSpecs;
import cbit.vcell.export.GeometrySpecs;
import cbit.vcell.export.TimeSpecs;
import cbit.vcell.export.VariableSpecs;
import cbit.vcell.export.gui.ExportSettings;
import cbit.vcell.export.server.*;
import cbit.image.*;
import cbit.vcell.simdata.*;

import javax.swing.*;
import cbit.vcell.solver.ode.*;
import cbit.util.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.client.*;
import cbit.vcell.client.server.*;
/**
 * This type was created in VisualAge.
 */
public class NewPDEExportPanel extends JPanel implements ExportConstants {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private JButton ivjJButtonExport = null;
	private JComboBox ivjJComboBox1 = null;
	private JLabel ivjJLabelFormat = null;
	private JPanel ivjJPanelExport = null;
	private ExportSettings ivjExportSettings1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JScrollPane ivjJScrollPane1 = null;
	private JSlider ivjJSlider1 = null;
	private JSlider ivjJSlider2 = null;
	private JTextField ivjJTextField1 = null;
	private JTextField ivjJTextField2 = null;
	private cbit.vcell.simdata.PDEDataContext fieldPdeDataContext = null;
	private JButton ivjJButtonAdd = null;
	private JButton ivjJButtonRemove = null;
	private JList ivjJListSelections = null;
	private JList ivjJListVariables = null;
	private JRadioButton ivjJRadioButtonSelection = null;
	private JRadioButton ivjJRadioButtonSlice = null;
	private JScrollPane ivjJScrollPane2 = null;
	private cbit.gui.DefaultListModelCivilized ivjDefaultListModelCivilizedSelections = null;
	private cbit.gui.DefaultListModelCivilized ivjDefaultListModelCivilizedVariables = null;
	private cbit.vcell.simdata.SpatialSelection fieldSelectedRegion = null;
	private int fieldSlice = -1;
	private int fieldNormalAxis = -1;
	private cbit.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private JPanel ivjJPanelRegion = null;
	private JPanel ivjJPanelTime = null;
	private JPanel ivjJPanelVariables = null;
	private JLabel ivjJLabelRegion = null;
	private JLabel ivjJLabelTime = null;
	private JLabel ivjJLabelVariables = null;
	private JPanel ivjJPanelSelections = null;
	private JPanel ivjJPanelSlice = null;
	private PDEDataContext ivjpdeDataContext1 = null;
	private cbit.vcell.simdata.DisplayAdapterService fieldDisplayAdapterService = null;
	private JRadioButton ivjJRadioButtonFull = null;
	private JLabel ivjJLabelFull = null;
	private JLabel ivjJLabelCurrentSelection = null;
	private JLabel ivjJLabelSlice = null;
	private boolean ivjConnPtoP3Aligning = false;
	private cbit.vcell.client.DataViewerManager fieldDataViewerManager = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.ListDataListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == NewPDEExportPanel.this.getJTextField1()) 
				connEtoC5(e);
			if (e.getSource() == NewPDEExportPanel.this.getJTextField2()) 
				connEtoC6(e);
			if (e.getSource() == NewPDEExportPanel.this.getJButtonExport()) 
				connEtoM1(e);
			if (e.getSource() == NewPDEExportPanel.this.getJButtonExport()) 
				connEtoC9(e);
			if (e.getSource() == NewPDEExportPanel.this.getJButtonAdd()) 
				connEtoM4(e);
			if (e.getSource() == NewPDEExportPanel.this.getJButtonRemove()) 
				connEtoM5(e);
		};
		public void contentsChanged(javax.swing.event.ListDataEvent e) {
			if (e.getSource() == NewPDEExportPanel.this.getDefaultListModelCivilizedSelections()) 
				connEtoC17();
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == NewPDEExportPanel.this.getJTextField1()) 
				connEtoC7(e);
			if (e.getSource() == NewPDEExportPanel.this.getJTextField2()) 
				connEtoC8(e);
		};
		public void intervalAdded(javax.swing.event.ListDataEvent e) {
			if (e.getSource() == NewPDEExportPanel.this.getDefaultListModelCivilizedSelections()) 
				connEtoC17();
		};
		public void intervalRemoved(javax.swing.event.ListDataEvent e) {
			if (e.getSource() == NewPDEExportPanel.this.getDefaultListModelCivilizedSelections()) 
				connEtoC17();
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == NewPDEExportPanel.this.getJComboBox1()) 
				connEtoM6(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == NewPDEExportPanel.this && (evt.getPropertyName().equals("selectedRegion"))) 
				connEtoC10(evt);
			if (evt.getSource() == NewPDEExportPanel.this && (evt.getPropertyName().equals("slice"))) 
				connEtoC11(evt);
			if (evt.getSource() == NewPDEExportPanel.this && (evt.getPropertyName().equals("normalAxis"))) 
				connEtoC12(evt);
			if (evt.getSource() == NewPDEExportPanel.this.getExportSettings1() && (evt.getPropertyName().equals("selectedFormat"))) 
				connEtoC13(evt);
			if (evt.getSource() == NewPDEExportPanel.this && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == NewPDEExportPanel.this.getpdeDataContext1() && (evt.getPropertyName().equals("timePoints"))) 
				connEtoC14(evt);
			if (evt.getSource() == NewPDEExportPanel.this.getButtonGroupCivilized1() && (evt.getPropertyName().equals("selection"))) 
				connEtoC16(evt);
			if (evt.getSource() == NewPDEExportPanel.this && (evt.getPropertyName().equals("selectedRegion"))) 
				connEtoC18(evt);
			if (evt.getSource() == NewPDEExportPanel.this.getpdeDataContext1() && (evt.getPropertyName().equals("dataIdentifiers"))) 
				connEtoC20(evt);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == NewPDEExportPanel.this.getJSlider1()) 
				connEtoC3(e);
			if (e.getSource() == NewPDEExportPanel.this.getJSlider2()) 
				connEtoC4(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == NewPDEExportPanel.this.getJListVariables()) 
				connEtoC15(e);
			if (e.getSource() == NewPDEExportPanel.this.getJListSelections()) 
				connEtoC19(e);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public NewPDEExportPanel() {
	super();
	initialize();
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 * @param listener java.beans.PropertyChangeListener
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * connEtoC1:  (pdeDataContext1.this --> PDEExportPanel.updateChoices(Lcbit.vcell.simdata.PDEDataContext;)V)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(cbit.vcell.simdata.PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		this.updateAllChoices(getpdeDataContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC10:  (NewPDEExportPanel.selectedRegion --> NewPDEExportPanel.updateCurrentSelection(Lcbit.vcell.simdata.gui.SpatialSelection;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateCurrentSelection(this.getSelectedRegion());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (NewPDEExportPanel.slice --> NewPDEExportPanel.updateSlice(II)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSlice(this.getSlice(), this.getNormalAxis());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (NewPDEExportPanel.normalAxis --> NewPDEExportPanel.updateSlice(II)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSlice(this.getSlice(), this.getNormalAxis());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (ExportSettings1.selectedFormat --> PDEExportPanel.updateExportFormat(I)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateExportFormat(getExportSettings1().getSelectedFormat());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC14:  (pdeDataContext1.timePoints --> NewPDEExportPanel.updateTimes([D)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateTimes(getpdeDataContext1().getTimePoints());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (JListVariables.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> PDEExportPanel.updateInterface()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC16:  (ButtonGroupCivilized1.selection --> PDEExportPanel.updateInterface()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC17:  (DefaultListModelCivilizedSelections.listData. --> PDEExportPanel.updateInterface()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17() {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC18:  (NewPDEExportPanel.selectedRegion --> NewPDEExportPanel.updateInterface()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC19:  (JListSelections.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> PDEExportPanel.updateInterface()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ODEExportPanel.initialize() --> ODEExportPanel.initFormatChoices()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.initFormatChoices();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC20:  (pdeDataContext1.dataIdentifiers --> NewPDEExportPanel.updateAllChoices(Lcbit.vcell.simdata.PDEDataContext;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getpdeDataContext1() != null)) {
			this.updateAllChoices(getpdeDataContext1());
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
 * connEtoC3:  (JSlider1.change.stateChanged(javax.swing.event.ChangeEvent) --> PDEExportPanel.setTimeFromSlider(ILjavax.swing.JTextField;)V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromSlider(getJSlider1().getValue(), getJTextField1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JSlider2.change.stateChanged(javax.swing.event.ChangeEvent) --> PDEExportPanel.setTimeFromSlider(ILjavax.swing.JTextField;)V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromSlider(getJSlider2().getValue(), getJTextField2());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> ODEExportPanel.setTimeFromTextField(Ljavax.swing.JTextField;Ljavax.swing.JSlider;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField1(), getJSlider1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JTextField2.action.actionPerformed(java.awt.event.ActionEvent) --> ODEExportPanel.setTimeFromTextField(Ljavax.swing.JTextField;Ljavax.swing.JSlider;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField2(), getJSlider2());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (JTextField1.focus.focusLost(java.awt.event.FocusEvent) --> ODEExportPanel.setTimeFromTextField(Ljavax.swing.JTextField;Ljavax.swing.JSlider;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField1(), getJSlider1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (JTextField2.focus.focusLost(java.awt.event.FocusEvent) --> ODEExportPanel.setTimeFromTextField(Ljavax.swing.JTextField;Ljavax.swing.JSlider;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField2(), getJSlider2());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (JButtonExport.action.actionPerformed(java.awt.event.ActionEvent) --> ODEExportPanel.startExport()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.startExport();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (JButtonExport.action.actionPerformed(java.awt.event.ActionEvent) --> ExportSettings1.simDataType)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getExportSettings1().setSimDataType(this.dataType());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (PDEExportPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonSlice());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (PDEExportPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonSelection());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM4:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> DefaultListModelCivilizedSelections.addNewElement(Ljava.lang.Object;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getDefaultListModelCivilizedSelections().addNewElement(this.getSelectedRegion());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> DefaultListModelCivilizedSelections.removeElementAt(I)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getDefaultListModelCivilizedSelections().removeElementAt(getJListSelections().getSelectedIndex());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (JComboBox1.item.itemStateChanged(java.awt.event.ItemEvent) --> ExportSettings1.selectedFormat)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getExportSettings1().setSelectedFormat(getJComboBox1().getSelectedIndex());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (PDEExportPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonFull());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (DefaultListModel1.this <--> JList1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getJListVariables().setModel(getDefaultListModelCivilizedVariables());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetTarget:  (DefaultListModelCivilized2.this <--> JListSelections.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getJListSelections().setModel(getDefaultListModelCivilizedSelections());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP3SetSource:  (NewPDEExportPanel.pdeDataContext <--> pdeDataContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getpdeDataContext1() != null)) {
				this.setPdeDataContext(getpdeDataContext1());
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
 * connPtoP3SetTarget:  (NewPDEExportPanel.pdeDataContext <--> pdeDataContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setpdeDataContext1(this.getPdeDataContext());
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
 * Comment
 */
public int dataType() {
	if (getPdeDataContext().hasParticleData()) {
		return PDE_SIMULATION_WITH_PARTICLES;
	} else {
		return PDE_SIMULATION_NO_PARTICLES;
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 * @param propertyName java.lang.String
 * @param oldValue java.lang.Object
 * @param newValue java.lang.Object
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Return the ButtonGroupCivilized1 property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new cbit.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupCivilized1;
}


/**
 * Gets the dataViewerManager property (cbit.vcell.client.DataViewerManager) value.
 * @return The dataViewerManager property value.
 * @see #setDataViewerManager
 */
public cbit.vcell.client.DataViewerManager getDataViewerManager() {
	return fieldDataViewerManager;
}


/**
 * Return the DefaultListModelCivilized2 property value.
 * @return cbit.gui.DefaultListModelCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.DefaultListModelCivilized getDefaultListModelCivilizedSelections() {
	if (ivjDefaultListModelCivilizedSelections == null) {
		try {
			ivjDefaultListModelCivilizedSelections = new cbit.gui.DefaultListModelCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultListModelCivilizedSelections;
}

/**
 * Return the DefaultListModelCivilized1 property value.
 * @return cbit.gui.DefaultListModelCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.DefaultListModelCivilized getDefaultListModelCivilizedVariables() {
	if (ivjDefaultListModelCivilizedVariables == null) {
		try {
			ivjDefaultListModelCivilizedVariables = new cbit.gui.DefaultListModelCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultListModelCivilizedVariables;
}

/**
 * Gets the displayAdapterService property (cbit.image.DisplayAdapterService) value.
 * @return The displayAdapterService property value.
 * @see #setDisplayAdapterService
 */
public cbit.vcell.simdata.DisplayAdapterService getDisplayAdapterService() {
	return fieldDisplayAdapterService;
}


/**
 * Return the ExportSettings1 property value.
 * @return cbit.vcell.export.ExportSettings
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ExportSettings getExportSettings1() {
	if (ivjExportSettings1 == null) {
		try {
			ivjExportSettings1 = new ExportSettings();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExportSettings1;
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.server.ExportSpecs
 */
private ExportSpecs getExportSpecs() {
	Object[] variableSelections = getJListVariables().getSelectedValues();
	String[] variableNames = new String[variableSelections.length];
	for (int i = 0; i < variableSelections.length; i++){
		variableNames[i] = (String)variableSelections[i];
	}
	VariableSpecs variableSpecs = new VariableSpecs(variableNames, ExportConstants.VARIABLE_MULTI);
	TimeSpecs timeSpecs = new TimeSpecs(getJSlider1().getValue(), getJSlider2().getValue(), getPdeDataContext().getTimePoints(), ExportConstants.TIME_RANGE);
	int geoMode = ExportConstants.GEOMETRY_SELECTIONS;
	if (getJRadioButtonSlice().isSelected()) {
		geoMode = ExportConstants.GEOMETRY_SLICE;
	} else if (getJRadioButtonFull().isSelected()) {
		geoMode = ExportConstants.GEOMETRY_FULL;
	}
	SpatialSelection[] selections = (SpatialSelection[])BeanUtils.getArray(getDefaultListModelCivilizedSelections().elements(), SpatialSelection.class);
	GeometrySpecs geometrySpecs = new GeometrySpecs(selections, getNormalAxis(), getSlice(), geoMode);
	return new ExportSpecs(
		getPdeDataContext().getVCDataIdentifier(),
		getExportSettings1().getSelectedFormat(),
		variableSpecs,
		timeSpecs,
		geometrySpecs,
		getExportSettings1().getFormatSpecificSpecs()
	);
}


/**
 * Return the JButtonAdd property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdd() {
	if (ivjJButtonAdd == null) {
		try {
			ivjJButtonAdd = new javax.swing.JButton();
			ivjJButtonAdd.setName("JButtonAdd");
			ivjJButtonAdd.setText("Add");
			ivjJButtonAdd.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdd;
}

/**
 * Return the JButtonExport property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonExport() {
	if (ivjJButtonExport == null) {
		try {
			ivjJButtonExport = new javax.swing.JButton();
			ivjJButtonExport.setName("JButtonExport");
			ivjJButtonExport.setText("Start Export");
			ivjJButtonExport.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonExport;
}

/**
 * Return the JButtonRemove property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setText("Remove");
			ivjJButtonRemove.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBox1() {
	if (ivjJComboBox1 == null) {
		try {
			ivjJComboBox1 = new javax.swing.JComboBox();
			ivjJComboBox1.setName("JComboBox1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBox1;
}


/**
 * Return the JLabelSelection property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCurrentSelection() {
	if (ivjJLabelCurrentSelection == null) {
		try {
			ivjJLabelCurrentSelection = new javax.swing.JLabel();
			ivjJLabelCurrentSelection.setName("JLabelCurrentSelection");
			ivjJLabelCurrentSelection.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCurrentSelection;
}

/**
 * Return the JLabelFormat property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFormat() {
	if (ivjJLabelFormat == null) {
		try {
			ivjJLabelFormat = new javax.swing.JLabel();
			ivjJLabelFormat.setName("JLabelFormat");
			ivjJLabelFormat.setPreferredSize(new java.awt.Dimension(100, 15));
			ivjJLabelFormat.setText("Export Format:");
			ivjJLabelFormat.setMaximumSize(new java.awt.Dimension(100, 15));
			ivjJLabelFormat.setHorizontalAlignment(SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFormat;
}

/**
 * Return the JLabelFull property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFull() {
	if (ivjJLabelFull == null) {
		try {
			ivjJLabelFull = new javax.swing.JLabel();
			ivjJLabelFull.setName("JLabelFull");
			ivjJLabelFull.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFull;
}


/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRegion() {
	if (ivjJLabelRegion == null) {
		try {
			ivjJLabelRegion = new javax.swing.JLabel();
			ivjJLabelRegion.setName("JLabelRegion");
			ivjJLabelRegion.setText("Region:");
			ivjJLabelRegion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelRegion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRegion;
}

/**
 * Return the JLabelSLice property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSlice() {
	if (ivjJLabelSlice == null) {
		try {
			ivjJLabelSlice = new javax.swing.JLabel();
			ivjJLabelSlice.setName("JLabelSlice");
			ivjJLabelSlice.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSlice;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTime() {
	if (ivjJLabelTime == null) {
		try {
			ivjJLabelTime = new javax.swing.JLabel();
			ivjJLabelTime.setName("JLabelTime");
			ivjJLabelTime.setText("Time interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTime;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelVariables() {
	if (ivjJLabelVariables == null) {
		try {
			ivjJLabelVariables = new javax.swing.JLabel();
			ivjJLabelVariables.setName("JLabelVariables");
			ivjJLabelVariables.setText("Variables:");
			ivjJLabelVariables.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelVariables;
}

/**
 * Return the JListSelections property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListSelections() {
	if (ivjJListSelections == null) {
		try {
			ivjJListSelections = new javax.swing.JList();
			ivjJListSelections.setName("JListSelections");
			ivjJListSelections.setBounds(0, 0, 160, 120);
			ivjJListSelections.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListSelections;
}

/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListVariables() {
	if (ivjJListVariables == null) {
		try {
			ivjJListVariables = new javax.swing.JList();
			ivjJListVariables.setName("JListVariables");
			ivjJListVariables.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListVariables;
}

/**
 * Return the JPanelExport property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelExport() {
	if (ivjJPanelExport == null) {
		try {
			ivjJPanelExport = new javax.swing.JPanel();
			ivjJPanelExport.setName("JPanelExport");
			ivjJPanelExport.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonExport = new java.awt.GridBagConstraints();
			constraintsJButtonExport.gridx = 2; constraintsJButtonExport.gridy = 0;
			constraintsJButtonExport.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanelExport().add(getJButtonExport(), constraintsJButtonExport);

			java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
			constraintsJComboBox1.gridx = 1; constraintsJComboBox1.gridy = 0;
			constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBox1.weightx = 1.0;
			constraintsJComboBox1.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanelExport().add(getJComboBox1(), constraintsJComboBox1);

			java.awt.GridBagConstraints constraintsJLabelFormat = new java.awt.GridBagConstraints();
			constraintsJLabelFormat.gridx = 0; constraintsJLabelFormat.gridy = 0;
			constraintsJLabelFormat.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelFormat.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanelExport().add(getJLabelFormat(), constraintsJLabelFormat);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelExport;
}

/**
 * Return the JPanelRegion property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelRegion() {
	if (ivjJPanelRegion == null) {
		try {
			ivjJPanelRegion = new javax.swing.JPanel();
			ivjJPanelRegion.setName("JPanelRegion");
			ivjJPanelRegion.setPreferredSize(new java.awt.Dimension(200, 200));
			ivjJPanelRegion.setLayout(new java.awt.BorderLayout());
			ivjJPanelRegion.setMinimumSize(new java.awt.Dimension(50, 50));
			getJPanelRegion().add(getJPanelSelections(), "Center");
			getJPanelRegion().add(getJLabelRegion(), "North");
			getJPanelRegion().add(getJPanelSlice(), "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelRegion;
}


/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSelections() {
	if (ivjJPanelSelections == null) {
		try {
			ivjJPanelSelections = new javax.swing.JPanel();
			ivjJPanelSelections.setName("JPanelSelections");
			ivjJPanelSelections.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJRadioButtonSelection = new java.awt.GridBagConstraints();
			constraintsJRadioButtonSelection.gridx = 0; constraintsJRadioButtonSelection.gridy = 0;
			constraintsJRadioButtonSelection.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonSelection.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSelections().add(getJRadioButtonSelection(), constraintsJRadioButtonSelection);

			java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
			constraintsJButtonAdd.gridx = 2; constraintsJButtonAdd.gridy = 1;
			constraintsJButtonAdd.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJButtonAdd.weighty = 1.0;
			constraintsJButtonAdd.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSelections().add(getJButtonAdd(), constraintsJButtonAdd);

			java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
			constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 2;
			constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsJButtonRemove.weighty = 1.0;
			constraintsJButtonRemove.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSelections().add(getJButtonRemove(), constraintsJButtonRemove);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.gridwidth = 2;
constraintsJScrollPane2.gridheight = 2;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(4, 15, 4, 4);
			getJPanelSelections().add(getJScrollPane2(), constraintsJScrollPane2);

			java.awt.GridBagConstraints constraintsJLabelCurrentSelection = new java.awt.GridBagConstraints();
			constraintsJLabelCurrentSelection.gridx = 1; constraintsJLabelCurrentSelection.gridy = 0;
			constraintsJLabelCurrentSelection.gridwidth = 2;
			constraintsJLabelCurrentSelection.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelCurrentSelection.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSelections().add(getJLabelCurrentSelection(), constraintsJLabelCurrentSelection);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelSelections;
}

/**
 * Return the JPanelSlice property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSlice() {
	if (ivjJPanelSlice == null) {
		try {
			ivjJPanelSlice = new javax.swing.JPanel();
			ivjJPanelSlice.setName("JPanelSlice");
			ivjJPanelSlice.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJRadioButtonSlice = new java.awt.GridBagConstraints();
			constraintsJRadioButtonSlice.gridx = 0; constraintsJRadioButtonSlice.gridy = 0;
			constraintsJRadioButtonSlice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonSlice.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSlice().add(getJRadioButtonSlice(), constraintsJRadioButtonSlice);

			java.awt.GridBagConstraints constraintsJRadioButtonFull = new java.awt.GridBagConstraints();
			constraintsJRadioButtonFull.gridx = 0; constraintsJRadioButtonFull.gridy = 1;
			constraintsJRadioButtonFull.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonFull.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSlice().add(getJRadioButtonFull(), constraintsJRadioButtonFull);

			java.awt.GridBagConstraints constraintsJLabelSlice = new java.awt.GridBagConstraints();
			constraintsJLabelSlice.gridx = 1; constraintsJLabelSlice.gridy = 0;
			constraintsJLabelSlice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelSlice.weightx = 1.0;
			constraintsJLabelSlice.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSlice().add(getJLabelSlice(), constraintsJLabelSlice);

			java.awt.GridBagConstraints constraintsJLabelFull = new java.awt.GridBagConstraints();
			constraintsJLabelFull.gridx = 1; constraintsJLabelFull.gridy = 1;
			constraintsJLabelFull.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelFull.weightx = 1.0;
			constraintsJLabelFull.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSlice().add(getJLabelFull(), constraintsJLabelFull);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelSlice;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTime() {
	if (ivjJPanelTime == null) {
		try {
			ivjJPanelTime = new javax.swing.JPanel();
			ivjJPanelTime.setName("JPanelTime");
			ivjJPanelTime.setPreferredSize(new java.awt.Dimension(200, 200));
			ivjJPanelTime.setLayout(new java.awt.GridBagLayout());
			ivjJPanelTime.setMinimumSize(new java.awt.Dimension(50, 50));

			java.awt.GridBagConstraints constraintsJLabelTime = new java.awt.GridBagConstraints();
			constraintsJLabelTime.gridx = 0; constraintsJLabelTime.gridy = 0;
			constraintsJLabelTime.gridwidth = 2;
			constraintsJLabelTime.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelTime().add(getJLabelTime(), constraintsJLabelTime);

			java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
			constraintsJTextField1.gridx = 0; constraintsJTextField1.gridy = 1;
			constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField1.weightx = 1.0;
			constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelTime().add(getJTextField1(), constraintsJTextField1);

			java.awt.GridBagConstraints constraintsJTextField2 = new java.awt.GridBagConstraints();
			constraintsJTextField2.gridx = 0; constraintsJTextField2.gridy = 2;
			constraintsJTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField2.weightx = 1.0;
			constraintsJTextField2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelTime().add(getJTextField2(), constraintsJTextField2);

			java.awt.GridBagConstraints constraintsJSlider1 = new java.awt.GridBagConstraints();
			constraintsJSlider1.gridx = 1; constraintsJSlider1.gridy = 1;
			constraintsJSlider1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJSlider1.weightx = 1.0;
			constraintsJSlider1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelTime().add(getJSlider1(), constraintsJSlider1);

			java.awt.GridBagConstraints constraintsJSlider2 = new java.awt.GridBagConstraints();
			constraintsJSlider2.gridx = 1; constraintsJSlider2.gridy = 2;
			constraintsJSlider2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJSlider2.weightx = 1.0;
			constraintsJSlider2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelTime().add(getJSlider2(), constraintsJSlider2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTime;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelVariables() {
	if (ivjJPanelVariables == null) {
		try {
			ivjJPanelVariables = new javax.swing.JPanel();
			ivjJPanelVariables.setName("JPanelVariables");
			ivjJPanelVariables.setPreferredSize(new java.awt.Dimension(200, 200));
			ivjJPanelVariables.setLayout(new java.awt.BorderLayout());
			ivjJPanelVariables.setMinimumSize(new java.awt.Dimension(50, 50));
			getJPanelVariables().add(getJLabelVariables(), "North");
			getJPanelVariables().add(getJScrollPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelVariables;
}

/**
 * Return the JRadioButtonFull property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonFull() {
	if (ivjJRadioButtonFull == null) {
		try {
			ivjJRadioButtonFull = new javax.swing.JRadioButton();
			ivjJRadioButtonFull.setName("JRadioButtonFull");
			ivjJRadioButtonFull.setText("Full");
			ivjJRadioButtonFull.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonFull;
}

/**
 * Return the JRadioButtonSelection property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonSelection() {
	if (ivjJRadioButtonSelection == null) {
		try {
			ivjJRadioButtonSelection = new javax.swing.JRadioButton();
			ivjJRadioButtonSelection.setName("JRadioButtonSelection");
			ivjJRadioButtonSelection.setSelected(true);
			ivjJRadioButtonSelection.setText("Selection(s)");
			ivjJRadioButtonSelection.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonSelection;
}

/**
 * Return the JRadioButtonSlice property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonSlice() {
	if (ivjJRadioButtonSlice == null) {
		try {
			ivjJRadioButtonSlice = new javax.swing.JRadioButton();
			ivjJRadioButtonSlice.setName("JRadioButtonSlice");
			ivjJRadioButtonSlice.setText("Slice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonSlice;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getJListVariables());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			getJScrollPane2().setViewportView(getJListSelections());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}


/**
 * Return the JSlider1 property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getJSlider1() {
	if (ivjJSlider1 == null) {
		try {
			ivjJSlider1 = new javax.swing.JSlider();
			ivjJSlider1.setName("JSlider1");
			ivjJSlider1.setPaintTicks(true);
			ivjJSlider1.setValue(0);
			ivjJSlider1.setMajorTickSpacing(10);
			ivjJSlider1.setSnapToTicks(true);
			ivjJSlider1.setMinorTickSpacing(1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSlider1;
}

/**
 * Return the JSlider2 property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getJSlider2() {
	if (ivjJSlider2 == null) {
		try {
			ivjJSlider2 = new javax.swing.JSlider();
			ivjJSlider2.setName("JSlider2");
			ivjJSlider2.setPaintTicks(true);
			ivjJSlider2.setValue(100);
			ivjJSlider2.setMajorTickSpacing(10);
			ivjJSlider2.setSnapToTicks(true);
			ivjJSlider2.setMinorTickSpacing(1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSlider2;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			ivjJTextField1.setPreferredSize(new java.awt.Dimension(40, 20));
			ivjJTextField1.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField2() {
	if (ivjJTextField2 == null) {
		try {
			ivjJTextField2 = new javax.swing.JTextField();
			ivjJTextField2.setName("JTextField2");
			ivjJTextField2.setPreferredSize(new java.awt.Dimension(40, 20));
			ivjJTextField2.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField2;
}

/**
 * Gets the normalAxis property (int) value.
 * @return The normalAxis property value.
 * @see #setNormalAxis
 */
public int getNormalAxis() {
	return fieldNormalAxis;
}


/**
 * Gets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @return The pdeDataContext property value.
 * @see #setPdeDataContext
 */
public cbit.vcell.simdata.PDEDataContext getPdeDataContext() {
	return fieldPdeDataContext;
}


/**
 * Return the pdeDataContext1 property value.
 * @return cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simdata.PDEDataContext getpdeDataContext1() {
	// user code begin {1}
	// user code end
	return ivjpdeDataContext1;
}

/**
 * Accessor for the propertyChange field.
 * @return java.beans.PropertyChangeSupport
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the selectedRegion property (cbit.vcell.simdata.gui.SpatialSelection) value.
 * @return The selectedRegion property value.
 * @see #setSelectedRegion
 */
public cbit.vcell.simdata.SpatialSelection getSelectedRegion() {
	return fieldSelectedRegion;
}


/**
 * Gets the slice property (int) value.
 * @return The slice property value.
 * @see #setSlice
 */
public int getSlice() {
	return fieldSlice;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBox1().addItemListener(ivjEventHandler);
	getJSlider1().addChangeListener(ivjEventHandler);
	getJSlider2().addChangeListener(ivjEventHandler);
	getJTextField1().addActionListener(ivjEventHandler);
	getJTextField2().addActionListener(ivjEventHandler);
	getJTextField1().addFocusListener(ivjEventHandler);
	getJTextField2().addFocusListener(ivjEventHandler);
	getJButtonExport().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getExportSettings1().addPropertyChangeListener(ivjEventHandler);
	getJListVariables().addListSelectionListener(ivjEventHandler);
	getButtonGroupCivilized1().addPropertyChangeListener(ivjEventHandler);
	getDefaultListModelCivilizedSelections().addListDataListener(ivjEventHandler);
	getJListSelections().addListSelectionListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP1SetTarget();
	connPtoP3SetTarget();
}

/**
 * Comment
 */
private void initFormatChoices() {
	if (getJComboBox1().getItemCount() > 0) getJComboBox1().removeAllItems();
	getJComboBox1().addItem("Comma delimited ASCII files (*.csv)");
	getJComboBox1().addItem("QuickTime movie files (*.mov)");
	getJComboBox1().addItem("GIF89a image files (*.gif)");
	getJComboBox1().addItem("Animated GIF files (*.gif)");
	getJComboBox1().addItem("Nearly raw raster data (*.nrrd)");
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		cbit.gui.LineBorderBean ivjLocalBorder1;
		ivjLocalBorder1 = new cbit.gui.LineBorderBean();
		ivjLocalBorder1.setLineColor(java.awt.Color.blue);
		cbit.gui.TitledBorderBean ivjLocalBorder;
		ivjLocalBorder = new cbit.gui.TitledBorderBean();
		ivjLocalBorder.setBorder(ivjLocalBorder1);
		ivjLocalBorder.setTitle("Specify data to be exported");
		setName("PDEExportPanel");
		setBorder(ivjLocalBorder);
		setLayout(new java.awt.GridBagLayout());
		setPreferredSize(new java.awt.Dimension(400, 250));
		setSize(672, 255);
		setMinimumSize(new java.awt.Dimension(200, 100));

		java.awt.GridBagConstraints constraintsJPanelExport = new java.awt.GridBagConstraints();
		constraintsJPanelExport.gridx = 0; constraintsJPanelExport.gridy = 1;
		constraintsJPanelExport.gridwidth = 3;
		constraintsJPanelExport.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelExport.weightx = 1.0;
		constraintsJPanelExport.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getJPanelExport(), constraintsJPanelExport);

		java.awt.GridBagConstraints constraintsJPanelTime = new java.awt.GridBagConstraints();
		constraintsJPanelTime.gridx = 0; constraintsJPanelTime.gridy = 0;
		constraintsJPanelTime.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTime.weightx = 1.0;
		constraintsJPanelTime.weighty = 1.0;
		constraintsJPanelTime.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelTime(), constraintsJPanelTime);

		java.awt.GridBagConstraints constraintsJPanelVariables = new java.awt.GridBagConstraints();
		constraintsJPanelVariables.gridx = 1; constraintsJPanelVariables.gridy = 0;
		constraintsJPanelVariables.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelVariables.weightx = 1.0;
		constraintsJPanelVariables.weighty = 1.0;
		constraintsJPanelVariables.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelVariables(), constraintsJPanelVariables);

		java.awt.GridBagConstraints constraintsJPanelRegion = new java.awt.GridBagConstraints();
		constraintsJPanelRegion.gridx = 2; constraintsJPanelRegion.gridy = 0;
		constraintsJPanelRegion.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelRegion.weightx = 1.5;
		constraintsJPanelRegion.weighty = 1.0;
		constraintsJPanelRegion.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelRegion(), constraintsJPanelRegion);
		initConnections();
		connEtoC2();
		connEtoM2();
		connEtoM7();
		connEtoM3();
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
		JFrame frame = new javax.swing.JFrame();
		NewPDEExportPanel aNewPDEExportPanel;
		aNewPDEExportPanel = new NewPDEExportPanel();
		frame.setContentPane(aNewPDEExportPanel);
		frame.setSize(aNewPDEExportPanel.getSize());
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
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 * @param listener java.beans.PropertyChangeListener
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Sets the dataViewerManager property (cbit.vcell.client.DataViewerManager) value.
 * @param dataViewerManager The new value for the property.
 * @see #getDataViewerManager
 */
public void setDataViewerManager(cbit.vcell.client.DataViewerManager dataViewerManager) {
	DataViewerManager oldValue = fieldDataViewerManager;
	fieldDataViewerManager = dataViewerManager;
	firePropertyChange("dataViewerManager", oldValue, dataViewerManager);
}


/**
 * Sets the displayAdapterService property (cbit.image.DisplayAdapterService) value.
 * @param displayAdapterService The new value for the property.
 * @see #getDisplayAdapterService
 */
public void setDisplayAdapterService(cbit.vcell.simdata.DisplayAdapterService displayAdapterService) {
	DisplayAdapterService oldValue = fieldDisplayAdapterService;
	fieldDisplayAdapterService = displayAdapterService;
	firePropertyChange("displayAdapterService", oldValue, displayAdapterService);
}


/**
 * Sets the normalAxis property (int) value.
 * @param normalAxis The new value for the property.
 * @see #getNormalAxis
 */
public void setNormalAxis(int normalAxis) {
	int oldValue = fieldNormalAxis;
	fieldNormalAxis = normalAxis;
	firePropertyChange("normalAxis", new Integer(oldValue), new Integer(normalAxis));
}


/**
 * Sets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @param pdeDataContext The new value for the property.
 * @see #getPdeDataContext
 */
public void setPdeDataContext(cbit.vcell.simdata.PDEDataContext pdeDataContext) {
	PDEDataContext oldValue = fieldPdeDataContext;
	fieldPdeDataContext = pdeDataContext;
	firePropertyChange("pdeDataContext", oldValue, pdeDataContext);
}


/**
 * Set the pdeDataContext1 to a new value.
 * @param newValue cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setpdeDataContext1(cbit.vcell.simdata.PDEDataContext newValue) {
	if (ivjpdeDataContext1 != newValue) {
		try {
			cbit.vcell.simdata.PDEDataContext oldValue = getpdeDataContext1();
			/* Stop listening for events from the current object */
			if (ivjpdeDataContext1 != null) {
				ivjpdeDataContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjpdeDataContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjpdeDataContext1 != null) {
				ivjpdeDataContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP3SetSource();
			connEtoC1(ivjpdeDataContext1);
			firePropertyChange("pdeDataContext", oldValue, newValue);
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
 * Sets the selectedRegion property (cbit.vcell.simdata.gui.SpatialSelection) value.
 * @param selectedRegion The new value for the property.
 * @see #getSelectedRegion
 */
public void setSelectedRegion(cbit.vcell.simdata.SpatialSelection selectedRegion) {
	SpatialSelection oldValue = fieldSelectedRegion;
	fieldSelectedRegion = selectedRegion;
	firePropertyChange("selectedRegion", oldValue, selectedRegion);
}


/**
 * Sets the slice property (int) value.
 * @param slice The new value for the property.
 * @see #getSlice
 */
public void setSlice(int slice) {
	int oldValue = fieldSlice;
	fieldSlice = slice;
	firePropertyChange("slice", new Integer(oldValue), new Integer(slice));
}


/**
 * Comment
 */
private void setTimeFromSlider(int sliderPosition, JTextField textField) {
	if (getPdeDataContext() != null && getPdeDataContext().getTimePoints() != null) {
		double timepoint = getPdeDataContext().getTimePoints()[sliderPosition];
		textField.setText(Double.toString(timepoint));
		textField.setCaretPosition(0);
	}else{
		textField.setText("");
	}
}
	
/**
 * Comment
 */
private void setTimeFromTextField(JTextField textField, JSlider slider) {
	int oldVal = slider.getValue();
	double time = 0;
	double[] times = getPdeDataContext().getTimePoints();
	try {
		time = Double.parseDouble(textField.getText());
	} catch (NumberFormatException e) {
		// if typedTime is crap, put back old value
		textField.setText(Double.toString(times[oldVal]));
		return;
	}
	// we find neighboring time value; if out of bounds, it is set to corresponding extreme
	// we correct text, then adjust slider; change in slider will fire other updates
	int val = 0;
	if (time > times[0]) {
		if (time >= times[times.length - 1]) {
			val = times.length - 1;
		} else {
			for (int i=0;i<times.length;i++) {
				val = i;
				if ((time >= times[i]) && (time < times[i+1]))
					break;
			}
		}
	}
	textField.setText(Double.toString(times[val]));
	slider.setValue(val);
}


/**
 * Comment
 */
private void startExport() {
	DisplayPreferences[] displayPreferences = null;
	switch (getExportSettings1().getSelectedFormat()) {
		case ExportConstants.FORMAT_QUICKTIME:
		case ExportConstants.FORMAT_GIF:
		case ExportConstants.FORMAT_ANIMATED_GIF: {
			Object[] variableSelections = getJListVariables().getSelectedValues();
			displayPreferences = new DisplayPreferences[variableSelections.length];
			for (int i = 0; i < displayPreferences.length; i++){
				displayPreferences[i] = getDisplayAdapterService().getDisplayPreferences((String)variableSelections[i]);
				Vector v = new Vector();
				if (displayPreferences[i] == null) {
					PopupGenerator.showErrorDialog(this, "No scale range or colormap available for variable '" + (String)variableSelections[i] + "'\nplease view each variable to be exported");
					return;
				} else {
					if (displayPreferences[i].scaleIsDefault()) {
						v.add(variableSelections[i]);
					}
				}
				if (! v.isEmpty()) {
					StringBuffer names = new StringBuffer();
					Enumeration en = v.elements();
					while (en.hasMoreElements()) {
						names.append("\n" + en.nextElement());
					}
					String choice = PopupGenerator.showWarningDialog((TopLevelWindowManager)getDataViewerManager(), getDataViewerManager().getUserPreferences(), UserMessage.warn_noScaleSettings,null);
					if (choice.equals(UserMessage.OPTION_CANCEL)){
						// user canceled
						return;
					}
				}
			}
			break;
		}
		case ExportConstants.FORMAT_CSV: {
			// check for membrane variables... warn for 3D geometry...
			// one gets the whole nine yards by index, not generally useful... except for a few people like Boris :)
			boolean mbVars = false;
			Object[] variableSelections = getJListVariables().getSelectedValues();
			DataIdentifier[] dataIDs = getPdeDataContext().getDataIdentifiers();
			for (int i = 0; i < variableSelections.length; i++){
				String varName = (String)variableSelections[i];
				for (int j = 0; j < dataIDs.length; j++){
					if (dataIDs[j].getName().equals(varName) && dataIDs[j].getVariableType().equals(VariableType.MEMBRANE)) {
						mbVars = true;
						break;
					}
				}
			}
			if (mbVars && getPdeDataContext().getCartesianMesh().getGeometryDimension() == 3) {
				String choice = PopupGenerator.showWarningDialog(this, getDataViewerManager().getUserPreferences(), UserMessage.warn_exportMembraneData3D, null);
				if (choice.equals(UserMessage.OPTION_CANCEL)) {
					// user canceled
					return;
				}
			}
		}
	};
	if (getJRadioButtonSelection().isSelected() && getDefaultListModelCivilizedSelections().isEmpty()) {
		PopupGenerator.showErrorDialog(this, "To export selections, you must add at least one item to the selection list");
	}
	getExportSettings1().setDisplayPreferences(displayPreferences);
	boolean okToExport = getExportSettings1().showFormatSpecificDialog(this);
			
	if (!okToExport) {
		return;
	}
	// pass the request down the line; non-blocking call
	getDataViewerManager().startExport(getExportSpecs());
}


/**
 * Comment
 */
private void updateAllChoices(cbit.vcell.simdata.PDEDataContext pdeDataContext) {
	if (pdeDataContext != null) {
		getDefaultListModelCivilizedVariables().setContents(pdeDataContext.getVariableNames());
		updateTimes(pdeDataContext.getTimePoints());
	}
}


/**
 * Comment
 */
private void updateCurrentSelection(cbit.vcell.simdata.SpatialSelection currentSelection) {
	if (currentSelection == null) {
		getJLabelCurrentSelection().setText("");
		getJLabelCurrentSelection().setToolTipText("");
	} else {
		getJLabelCurrentSelection().setText(currentSelection.isPoint() ? "Point" : "Curve");
		getJLabelCurrentSelection().setToolTipText(currentSelection.toString());
	}
}


/**
 * Comment
 */
private void updateExportFormat(int exportFormat) {
	switch (exportFormat) {
		case ExportConstants.FORMAT_CSV: {
			BeanUtils.enableComponents(getJPanelSelections(), true);
			getJRadioButtonFull().setEnabled(false);
			getJRadioButtonSelection().setSelected(true);
			break;
		}
		case ExportConstants.FORMAT_QUICKTIME:
		case ExportConstants.FORMAT_GIF:
		case ExportConstants.FORMAT_ANIMATED_GIF: {
			BeanUtils.enableComponents(getJPanelSelections(), false);
			getJRadioButtonSlice().setSelected(true);
			getJRadioButtonFull().setEnabled(false);
			break;
		}
		case ExportConstants.FORMAT_NRRD: {
			BeanUtils.enableComponents(getJPanelSelections(), false);
			getJRadioButtonFull().setSelected(true);
			getJRadioButtonFull().setEnabled(true);			
			break;
		}
	};
}


/**
 * Comment
 */
private void updateInterface() {
	//
	if(!getJRadioButtonSelection().isSelected()){
		getJListSelections().clearSelection();
	}
	//
	boolean startExportEnabled =
		(getJListVariables().getSelectedIndex() != -1)
		&&
		(
		(getJRadioButtonSelection().isSelected() && getDefaultListModelCivilizedSelections().getSize() > 0)
		||
		(getJRadioButtonSlice().isSelected())
		||
		(getJRadioButtonFull().isSelected())
		);

	getJButtonExport().setEnabled(startExportEnabled);
	//
	boolean selectionAddEnabled = true;
	for(int i=0;i < getJListSelections().getModel().getSize();i+= 1){
		SpatialSelection sl = (SpatialSelection)getJListSelections().getModel().getElementAt(i);
		if(sl.compareEqual(getSelectedRegion())){
			selectionAddEnabled = false;
			break;
		}
	}
	selectionAddEnabled = selectionAddEnabled &&
		getJRadioButtonSelection().isSelected()
		&&
		(getSelectedRegion() != null);
		
	getJButtonAdd().setEnabled(selectionAddEnabled);
	//
	boolean selectionRemoveEnabled =
		getJRadioButtonSelection().isSelected()
		&&
		(getJListSelections().getSelectedIndex() != -1);
		
	getJButtonRemove().setEnabled(selectionRemoveEnabled);
	//
	getJListSelections().setEnabled(getJRadioButtonSelection().isSelected());
}


/**
 * Comment
 */
private void updateSlice(int slice, int normalAxis) {
	String axis = "";
	switch (normalAxis) {
		case 0: {
			axis = "YZ";
			break;
		}
		case 1: {
			axis = "ZX";
			break;
		}
		case 2: {
			axis = "XY";
			break;
		}
	}
	getJLabelSlice().setText("Axis: " + axis + ", slice: " + slice);
}


/**
 * Comment
 */
private void updateTimes(double[] times) {
	
	if (times == null) times = new double[1];
	
	// finally, update widgets
	getJSlider1().setMaximum(times.length - 1);
	getJSlider2().setMaximum(times.length - 1);
	getJSlider1().setValue(0);
	getJSlider2().setValue(times.length - 1);
	getJTextField1().setText(Double.toString(times[0]));
	getJTextField2().setText(Double.toString(times[times.length - 1]));
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G550171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFFDC14D556B83554DAD1E1D90BC6C5E5C5C5ADED34D1CB9B15DBD8D4D4D434CB6FD2D131EFD61436D1315B438F958315924B8A95959594F1D07C19757A83950A0A1290B74A991887E6E0E026E190B4546F39BF4EFDFE4CB38C4EEEFD5FBFDEBF9E1EF94EBD671EF34FBD775EF36FF31F7BC8A50BA326676416C8D24EF4097F5B14A3C91937CB52DBCBC2B6A0AE342F25D0D27DDB82700E74788779F0
	0D003448357B8B0B257DDF9740FD8C5CD72C5EDF78BE7C1EA735FDED0B039FBE64EC07341995FF6899D94EF39F211C19747E0E4EA2384E8108859C3966BBDBC07E1FBB97F0FCAA47A36464CAD29A25F9353314636A615E8C908E906DE47489F01DC5A51F32F5B327EB9B9BA9CD3B471DBB1B64234A93214245F89B7935CCFAA3E71A0B48DA54D544F90261BE8E0094BECF7214D643F5DBD3CF57279D6DF6CF75218E5961286EF05865BECF35556231EC5D7A25BC58D437FB77104B6976B4D97AE407A439FEA4F9F6
	F69550BCA4E9B35CE7D35C0B9DC4CE8C7CCE84C897788554CE5B5DAD052DGAEB7166D13155B386E2DBF5DA425F77DE956DC9FE59B63E6E50AF6ABE5ABDB551E77812D07158B093D27C0DAF1FFCBE1B9C0BD008DE08C40C7347E2BCBD741F5C73BE717333757593743655A69303A3FF0DAE5877C5E3A95D2B86E9B3943693602BF503C4B3B2A8C749989985F3A9366E3B609DC0F759D754DF5D26A8D67674EB430C9EA64CF39917D4C96317D0ACD186C9E1F32D37A357E28153DDFD24B167E36EE66F48359250F2E
	1BB90567A3F99459F59E6A97D6D257E5701B7091DFE578810A9F2342B35F1CC6FD065CC7C15A0281740D56B928CBE37E25D2F14DEF8C6D90F6C7C8EE70G53A1FCC02FCB120D641DB040E4768ADD665A89BE6A1002676D047AA9398F9D845FBD52D2487C4B341834D55B311642E000F0009800E400CC007C6368E30325EBC769E3CD9637E767005DE1155D9263B5ADFC8F2E721047EDE9686B77D87ABA64960B5BEEE9F7487D92275932105820DD4377A9757D8E1031D76E135D960F5D598739DBFAE50F6C6EE7B4
	FB973EC3E85CB2CDED36774AA02057A5E13EDFAD1C87D707255F732D8B1A3D2CD030747D8BC91D9C3234C00E10817CE6F6793E0C582B9C4A3F8D20D15861A11A5F67329B7A0B2DDB9B1D4E1E81D7B3A90994FE1C5879E45AF718617750F1347101A584178677D9A21FD90B5ED565D3E76FA06ADB5C07310DB92C246CC5C1504F835481F8GA682A484219F3D580AFED4B77BDA296A11738C7DA894FCBABD0879CFF6103EBD7DC46590DD4BCF82B9GD600E00009A761D870C6079965F0791EA06F36B611630A8C52
	0C20C2C747E44D371AD57733F97B55739672EB5EA86490182E3BE775F277D2D24713201CE9G5249D8B7F50B4A182F6F34744B685DDF3941B1E52BD2D3CC5FC7EDA9F0DD86301C6BC57C63FDAEE3A140E2GB248FF8E308560E300CA0055GEBGC83B2A81D88F50G709D40DE006F811CGE45C7891E0934097GDF81FC8270BFC4C700DDG60B6528100DF819C843010B681308F60DB00D3G2029C983G3F16C8EF487A0253G8681E4008B814EG6882308374A8F5E2A7E5DCG76598EE08388850885C883A8
	78D5CB21892095A088A092A089A08B20AC9846ABG97C098C0BCC086C071A9AD051B814CGE3G2281D2G24D3415F811AG06GA2G92GB281967C9A648358GC281E28152G8A4600FC00D600A000C800A400AC003253C0BE008BA08CA09EA0832078F4108FE0BE9DFD3C3C531CE76CBB68EBF379F59E3FE29D4FD155739C15AF4C99211EB7707AC4FF5057679C6E838D5CEF305E67286AF4BF2F736F382F286BDD5655E3AF77259FD5FEB0487DC6DF5EDFF1FF79B5770FB38CE874F061A8E87E8F7E8FD8DBFB
	4BE7DB1B492FB57C0AFDBFE90333796F39B48F761B747F5837133E9B5BA11953E0BBA46D1534B7EC2F7A76C6F8C99BDD89B0DFD25AA54113B67735445A58EE0935E75216556D1234B7F5BBA3ED1134F5524E311D12F6096DDA8D240D1376CC5AB1E93FDD2330217C3F209E7F8F7E77830BE536AEA66DC5BE84535EBA59A577D961C7BFCF5B311834AF9E17F7BA854FDE0AF7C19C3F8B8267FE017FE2B1E9F7960F47EDEF9F70487BE40F475E572524FFCBE5758F343B14A9G4AEADBCC5A29F39062F8C1EFD9CCE2
	608EE7DF1FCC03AA414253BBE88CAEBB64DED0FA17F3204F437076B232D651A39F66F3005E32999209619D83E4BE41667F519652E7385C76DE88554814C4CAB91D4467633B9628ECB486F0E49DA38A2E7127E3DCBD388C636A72A3D7C97187DD39C6F345304B4F4EADBB03453035E7A8F1B5536907A5463ADB69DC00637B73A86E0D3504D73F964152DE1FC1EC4A925060B5BC2DFE55D23856491D16810727515E6F215331DD76C3F6077D08ECD54D31987D9907170DC43F8F342275504F691BE6107EF067004763
	6C5B6BF68E3884E90D04F9DE56C674F3D965BA0B47324B596781F72A617135D4CEEC508C363181841D0171F55A92120EF39F9ED86FF2BA38F8C8CBBC0354551D5692353D4FE0F5B5C7243F24C9AF895127FFA40F1C3ED713DE2FCA0F88A1697DA2FD886EC3D5692C9C77ADADA67EAE7FB8A0431416187AF3611349CB5F552535F07FE46929CB09FFBAD4734761B3094C1F6C38F6168172A440151F4966E666B3196C6233885D7783C817GF8921B271D49682781D483BE0A52ADB804F4EDE7B17DBD7C9AF2964B0F
	51BDA46832487D5990D31F4D68DCE72B651EBF08F4D1E733F940A438A683CCBBDBAD57BA04F47921AD0525213E72DBF0986936053279C4B3DC2D217A7C1E92F44121CCDEF8A84BAFB5D41D5F95C7102EEAAC1B17540D556A4768DCBFA1DDB327F30CE565564A5DBA0CF443E319DE46F2DF94F4634632B9D054D8EDB998DD6CD1240B63F449E319FD34F2CFBE06F469DC7F6C31C6F26B0EA3DD6DB9EC3E55F80ED1F967C8428FB81D679C237226883A60F3187E46F2C3F210AE7C9CB6370BB947283CAD39C8B70953
	251E23ADAF737B32BC241B8269796742BC70DC667736F355FE1AAD68024EE5F4E167B27DD2B8DD79B8C2971F0FF4534EE573CD52BE88DD51B85E87D03AB90B114EB50E2DFD0C01EB98C0AC40A2C1770420DBF09EDBA7A17A1B60F753F90C0E6997D906F45673183CE1FE8DBBCFDD0E0EA5221D73F469FCF6ADB9DFCDD73A9469CCBC3DDEC7476C7C2820B31F4F6651C31CEE4C796A7A38FC1928B7CE97234AD721F3160BFABB1F4D49D38D6936AC9775466972FF23154B6AB7EF85521570742A5F30F5263A5F30F2
	B03ADB851D07530598528D2EC43A09BE72E37A2D2E906D6DB7EC9DA1992E69G79E1EA7DDE97F4A5002F82E88CE37E9092264E6F56CA11DF98DB07C88EE37253B5791D358A69247150BF0FD7725BBCDEDD3FBBD7A3DD6BF8666F2E713C5C6355FE7A8FC1974D53934633B53A74714C7F18FEF7D7893F9F4F56CB0AA6C0FB1B406814FA9B3BC6743B93D8BF5EBC0175875689EABAF3B5528DCDE07DF3688456FE35F42B56A2DD448436AE93BB01352304896A7E65CDC117BE01691FBD01374B8B54F6394104F4CD97
	B0BA5B850CAEC8C357DBA37C7982569EE3B81D942E262BDD07F4E5614CCFB6079B513DA8681A42D9FD598C69A2570BFE0827079952F554A2DDCCB833C7220E0E5BEF0368DFAEE4EBDDA638EE87B0DF2836DF1620F3DD486CB266C22D7DD8FEB7EC946D63C2365695476993F9FEACCE1EDAC962E17A1C2C5BF248B2D45DBF88A166562D1F51A814BD8F4B3311181544AB299762F32FF87AECE37BC5AD052DG2E0BB0F6FEF0B6BE779A5793A9257E2DE6262FE7FAE3AE62F63AC8091D2BB2F74CE03163E74B094CA9
	1016FD91460D7B1729575EF77477BBBB6CF4591F47D4B34A5531B54395163F8D57CE3B3B5F53G131FA10C3F5EA9A77A1267829C4D6D9134F805647D5CD0267177562D5AB89C1FAD9D5D23AEF35C5C233905BE4ADCFCB1AB2B69E2254CA93D4DB3196C8B4BAAB4323B866CDB377A8C57994F954B16F97398056C7CB9508439C8E2313B8BF40884C83B18609F13491C0A74A92CCC9D6B31CC21C7AE174C67CD70D9267288D6163A88D5BD9E4B1A4E64CD34233CE1C80F81E07330AD7D6AF958944017DC82FE8A5088
	406696055DE49D456E11FB2B491FFD9E0BC75EE53374F549568A622C5516C1CFB51D5AD6B7C07AEE722B124FCBBE6DA6797653A90DEC5D63F477DABCD8DF57AE5A4E65A6DEF729D4E0B9A5576859EC485F3AF22D1730B205DC2214AD7A1C763CC9G2CFC278EE07912012668D2FC7EDC72A93952B92904F3253786E62BF4C23FE3BA8D9FC01D0292E32485734EB454095A79D2260B63D2C5276108763C508854E94721A26E4FF1C0B36DD234E7A4455B7B6C9E3B0578C4C5A5537571C5A59C4F6C344B6634F7487D
	9515ADCC37CB67296D356D788F063A0D19EB4B2D3A0C6954FC1922DBBD582ACD586BCD21DB845024G3039D812D9BD97AB1E883EG50B691F5EF9FDCC73C1256FEF53F5E93F837451D0125F0F7606D7A3206B554769E72GF00F5B593B4FE107F9E7C5C3231A7F33E6183E6E314B8E6B4715AD92BEBB3DEDD0349950A7D26834E20834990B3BEB9DABC3EE3B3A8CE93C8C8517A37D17145EE3337733FC4F9CC2DB1482CD7365E80B5F9DB432C58824737ABD0C7AC4GAEC5649FF9D82BCF2921BE45D7E0BE05C7D0
	FEB9605A2E40FC5CC7082DADF4B2DE4DAECD323B93FC433B616D2049DADB97FDD527353530EA05AFEBEB712CEE208A181E8F9C41720E839D93051E9BFF62652DE1F463FFC23A7CABDB8A37DD09F452B05A0551F58CE339BDC093F5A55A637D23481F8E3852C864BFF6D4AB67C9C1578CB4A1022E7E18D64EB547D04EA4204996F46F9EA77BB5BA1D9D837D5574EF23335F23B76C9E1200F6E579759EC73935D741FC75AA4CAF7938D63FCD9252CD841AD4C1F7112455AFA78775AB399A7A5D2B518EB766A0FFB060
	622EC67E52DC2D1C6339C8B78DE82A2EC13AE7F335F29A72104E8AB46157609870252C9E83122FE1E3C0B6DC0B23B02F683CC0FD1171276711F12B5F5ECB56271A8F3BE4EE4B6579D8768A10E59672C2727FBD5F3FAD070EBB9E0B5B4302058A5211B01967ADC219E1A0AFA98A470A7327939D2DDC410A4A86BEA63C39907B5D723A095266E9BDBE470402EBF9DCF12D526FC64DFEFDFABCG770945D887C1C093FB2D70094558A731BE63F21B3A4F48843A126B205F388E696F226B383DA49211F06CFAE5876AC9
	46075819A106FA5A2E8C4BED3C0E696739CE51337612763C7ACBF0FC18D106FACE821A6C6B30CF3C3DCB2FD76DEFA1767CAD6AB536CC2D974B6B41B234378368A68A5A4DCB543141B7F297DDF4E4F5F16317BB61BAC0770A6C9AF03B21EE453AE8C523AAA48417A2B5C6822E6AFDAE8B99D2856147CA1D07ACC19DD2C1FEC9B46AB0F5894175BBE02015D076493D45C276BE12D2515000FD606E2518CFA36491AC7209DACA625BBEB2EEBBF68C597BD16EDBCB510EF1C0BBCD50F7AE43B64C6E4FDA265603715E3F
	8CF92B2E07B95075483B21DC5B2E434A11AE9CE812855DDBBA3A7665C8D774BB181F7F8E69A6AF5776A7D5ABC42C88B4B1026E5C9546F10B290F2CFFDB2C3AE1E5C7FB3F478DCD17C5412A3AF8D664BF8572AE3F81737FE225BEFFC69F3192695B00F65C8D58BFB6D7A0BE91F079B1184F9FAA30BC4CE77B7B54BE3B8D68ECGA1027E698AEDBC3034D20C6BC01392033E5FE7D767C37A49629B417FEF44FCCE2B7C777A2C34EF399D3F1487F733599333E8358382296A2F5A3B412D55F694FA56038EC1C20FAFD6
	E9FDEBE015D1BBE3F95C348A7DB99678B3EF44327E51E6D4560A5FE3DF795BDCB287F44B3D4EC3B257F50727821A894F37E7B5663B8DF8BC3FC75D9EDAEDB49FE0F95EDB721EE8F72C53E669B4DB1E6F0AAAACF3946419AE72FDC126F3F14D738F9CFB2E2F5256EF26482374A60877EE42BC762F517A4DEC3B5EEF088DC2EEC27F3BE50D7093402508FC16D4EB6D5FD02D1E03B05E532A1137B8362530BE96F9B32B35EDF15FDAB1DEGCD2C203BE52DD646D3EB0D66B4AC0FF29356C5A670D75C0CF91CE1C23BB0
	7B9F2D5BA86C3F4361406907D1EF5BD437DBE5E815CF268AD9E61093A6E46D2AA17D23878251A6271DBC7A64F5DEDC257435F4CFE045F7F558571EDF03F9A5C1BE8537E0DE7F28598ED7874C44C5CFDFCD2D6BFBAA5228A6E62D0E49BFB07BDDA13F818A636E34C0FF2F14677BF52843F610BFA4F41834EE1630B7FBD646E35775C89F8D3499027E587AD9DCE7E237EAC17F2020AF7B830C23FF90E3E32D6FB69BD20BBC21C01FA0F826567EA76C427DEE837A1D9487F1C71C703B8D3A3E55EA54373A847DD39B34
	EDEF49C654BD98E862845D180DA4D6A13E55C09E0D5BBB6D6481B477290DA23E857A2ADB05CFED5236011ECDA80B6DEFA930352AF433825F502D7E77C4069850C4GAEC607E77BFCEB691E6304DB59BE5FD9823F036253D5F8D6163B68FA9D0D577E88E37A9FF1EF6A4D95A486CF007BF400EC00125BE0EC81E83E8D575D4617133DACA36E7D25AE5C87A1106661BF6E41DDCE6C54A39F66777B162B7BA53E4FFD9529CF97488C811888908710AAF47873F23FBA6C9EB2540155C904CC760DE75FB63A3D2984985F
	F5142FA4FE74FCACA62E7C177A5D067CF7BE4BA5B6B0DC4F61F9B747ABB1B133C952EAE213F1000F86C8GC88310EEC71B142F2872E7935C83400D414AF4355D5F7E7D325B0D7666467A652B5371313E2E4BC174B5D8E3863D5B1DBB1DC3FCEF865B0BAC5613CEE7FB285BA0CF97813A6E7CE3BF3E1BB166F6DC0B7E42C9FCBB9E6ED3GB2EFC75FDEC11FF1945C81319E40B600D600E100B00038BB500E2E959F7B75AD52C5B5BBD97434CB3FE3055BA7E4A0B03BCE704B17FA07160F156F8A7AECE69A24955F89
	63ABC09D008DA098A072CEAC5F9DABF67AAB1F58D68FE5447DBD5E45647A6EBD2457B77ECE2DFE46657C560B2FDADF4EBB0D4A7991FD96B48D520AA7C1B9816AGECG41G1113301C73D7FEB24AFA5C8538FA2733472008DC571C41404AB8570BCFDF4604C9C6ED24F44B8746ED042D05C19AEBA3859623B61286F9CE1B04EDE4260534919217957D8977B77F49CB2F51796643FE79DCFFB2AA4FF27ABC5520BCE24D0F693A253D004B9AA3F4BDF7A8B05BDF62452737FD3CCED726435F8E223DD2048EFB8F87E6
	2FEF7D72ED3B4BC807FD428E4DF7218E6F9C894C8E0B3D7874F688394B086F91AF3E511579E93FFC938D65DD7CD3E0722E754B97E7A84FBE9C18BD873D787476CCB6143770E8E065DB6D17EF0A213C678214772ADF3E7C3B0D782EBF9618BDEF7362535B3354D0DE7F71404AF712DF3EDAC3F91FC801497B5CAFDF23213C19B90159F33E971F5E1ED6C3F9FF70629BDD79A67B659BB614F7CAEEE0724E754BB74ED05ED7F901497B5EAFDF14CE9E9B73666E73B1063315C8897B7AD3E56C6BA75D0DB16C29EE9243
	E643FDC9824CB593F08CD81D9F18CFEC7162537BC4DD02915F850B8213F715971FDEDE1BCE9E0B6DAE207BF7BC10968A9089B089A083A0FFB244FA13B136332DA46FEA0CB606759F5FFD32B840366D172FFE3211EF5CDB793E31EF1015909E5B9D362BE3BB73E43E6E87570849985B25523DB8F45DF4B24EF35E25FB11B2613E689E186BGEC8730828481CC3C87ED9837526FBCC0847F231987941585E6438F7D72455D2365E376696D545BA7996832G0A6EC57BE409F7774B6F45B67418173C517974B3DEFC0B
	74BEFD2F915FB9CB821397614527EFC39EC3F91FAF8DCC5E81AFBE3D3C90C3F9531785D66F737C72CDB414179C203C277C7245994A3B28BCB0F9D1FE79128D65591687A66F10DF3EA90672562D88CC5EF63FFC7909C6FCD382147706DF3ED2C3F9D72C8CCCDE0CDF3EDAC3F97F2A88CC1E4DAFDFE32251D8725ECE528F990CA55FD82CF6A7869B2C4F3C38C73FDEE005BC47G04A7E21FF9F59F0EA9B10958E7D6D486D6260DFE7926A49A756D6BBA0D744C3F2F25F041FD286765F60C1BCC77211ED38254738D3F
	FC367B0C78EEDD95D8DFFB2F97DF356EBEC8A70F1579D59B16B9CC147947550169F05C0BCFBF1EA59916392AAAB09BEF754B17E1A86F6F814A4BDF630FCF3A5F4846F5542F483A7D027B514671FE73B25661FE3FFCCD779B711DDD9D183C8B7D72598C659DD89B183CAE3FFCC10672D6198273594DDEFCFA1F8DB31477D40072B27D4A0BB61497DE93183C2B3D782AF577890672BA5685A66FC7AFBE3D3CB4C3F98B5787A6EF35971FDEDE16213C478214770CDFF9458F98466C3581466CDEFCFAF99506728EEC88
	307D79655B7E00D19FF7359547115687300FDB39B130B2EF70625317B954304CCFF9710D2E4C19FE79A2F57258D9CC2B6858998FE949G8C77DCA741E541FDD19262BAA91D896E9B852E31031E638477C38257CBED988E77B1C9E8432DD6320FA4816E53G32GCA9E944F90E89AFD2FE1A21B2BDAC5FC1558C17688905A114AC55BC712B7CD8247259924295A8215E1F4FE12913D9E07E7AB694FCEC2604F96ABFC1FAFB0394278FC0187AA0C4F97F824629D0607BC542708E17A9395A31FBF70CC056F7387562E
	722D6746D546FAEEDBE52C674ED5A36B7959AADFFAAE24F6FD146E1BF68F7411EDC9B4EB96574BC3645C129D6D1EEF867A58928D493DDF7FACDF42353326DA326F246AC1DC5FF9F255BA49FBFDE737556EF13AB113E659627E2A33D3AC6E6CF3499D28F74AEA5FB67A6BEAE39BBD335A58C64F2F9E59C6D3D70FEC2367BC646C812F33A13E3474AA6F53BCE91F45E9F6FB1CDF10BDE42C2F3B410A73197A87F1FD75BC3AAFA6886E43G22C59BBD54BFD25F12E050BE59BE1B5B691E03548759BE1B9C013F0762B3
	D4F83E2F096269BD2435BE04F5E6DA635B661B56985B7C13B5F44F1DF3405DA16B6DDA37E601C8D3966E486BD144F47C9C8F4A6F31383BE42FFAE9267CAC4D977F77EBFC559D2B17BB878AF93DB4F53A1D3DCD9BF95E51553E4BFAD335F1D9EF2D76DD56C9557E4BFADF356F32A6D77BAF6B935524AC6DF62B5D4DF75CB17F1C7C9EBD7782DCDEE6EFE9106DF53C7C478E297CF257BAA1EF3949F7792BCC4665DFE772DD7EADA67F655FE972DD7E86137F72FFE39A392EB78F2A4B3ADE34416D961CD345BD04ED70
	A05D03B68D6E0B9EEEA9ACFF985BE011C53DDF4E883681ED5343CADB342AFE8F29FE07406F080735ED166919BD287549F5FC0C7C349D5BFF4C438663AC60D28D733BE7C8DD6EDA89475C978F7AABCBA664171F2C6439C0753BCA75FBBB7CEECEB612FD6C3036AC35DCF62BA80BAD593BAC43008BB54C2F7E083AAC9B843E68A72D1C8D9C7F288E3F0963A3065579EC9479B49F5552EF6678C5473478AD9C1FF1CC1D4FA6114F55747DA7053E4644709EC907677BB2BF4DD16733D96473CA0E0EFE9D4347656A706B
	997E54BC75B8D3A36A7A8C59E86C1848ED9C8B57C41D3D59987186FD5617162C9FBB727A89FECA32FE6C88F163581185E9ED0F08F7A27355655BA24A7750A20DDDF7557076FEF90D6F7EA72A46387F79DD0D6F7E67669A7F7D4F9DB53E7B1F49B57E7B1F07EAC66EFFBC0B5186CD9EE79369EB791E435D241FB65ACF5561F6BA9C646DF6897B2A6B6D784CA87E916C2BB2693952D9F0DF7CE8CBE1452358D7BDE99BA9DE587E28363E1B9F45F13EF43DEF5BAFDBEFEC7B4A753EEDEFDA6F5F76DB567B367D76757E
	ED5F30DEBF76717D4FE5EA1B13764056F007FEB05A1BE6839B8CBFAA56F0BB5156639EC5DB97D1DBA742FDBA4189DB7FE344F326CB9E736E6318FEF3163C2DEA97B5A644CF5662EB90FF72D28DFE9D6237AD5376CFF86ED6E139963F1E639FA957759F358CFF71F29DFE83435BD7287B15F5C27FE5AB55F8257FFBC203DFAF70E3ABF479EFE4753157EED49F95E037ED0FE1FDBC560331F273E3D89F313DA43F90381F889077985647EF7B0C6AA31957C3064F7AB8D829563BD6343B6ECE141DADE4B751F32DEBD3
	E0AE8EE0CDC15946FB1607D3184CD038CECCB19A5B162D5259E6B343CFDB2556090CF97C3900E17B0E65F9A742B54DD0CE64EA0D8F2DC7FCC7153A2DA8F536E60DCE2FCDCCFE83AD67B41D0C024759FDA93F1A9ED752D9FE2F6972DB4747343B2AF5F8BE26052E55495F42709613FACCD14EF97B20C6D33EDA44BF2C45EFC07C04F59A7CC6918B2C5760B709B6DB2B41EF96EDD60B5F0278508D3AF219380FDA0D6A2F0E5B2B952E0E470D6A6F070DEAB96BC41F7141A61D9C9EDB6C3011B3875B1DEE5A29E6BECE
	FC3755066DAD886E43GA68124BC4E5A9D637BE7A761F3D88EBB87BC48B7DF1CC938794F9067828C81047CD94DF754C67AF217DB6E145DEE593A4FFE040CDF29FF266FAB51752B0970BB7A4F781E5C75B45E6DF4DBFAE15823137C183F50F75FBBD0D6995C5782B483F8G467D85E585DB89AFB05AFB87FA29A4A6A71C4E2B33012EA095E9A368FB266C3498766E9C439F1F7F364013772B501E3574FD9B06D78EA8647DED111297FB73084FC9EBC959C58C4057GD8FDAF6A5457F7ED2AD23F75293E666F3368B9
	A8ED29AC561AA97073A95E234233986C823AFEC06EC3A0ADB39557844F6C47F54266A7302F3A1B8C61DC3FA4556F83CEFFF303FF87188C77AFA023D0343109CF70D87789454E0970BB6D89233877E1B67FFB025936D0601F2478127FD2704C66B245137BDAC88B7BAF34F9B51DDBE643FD49DF91B7C4F195F03FCD60EE2738A638F788DC915DE79D8477E18257520EF598A5F05F52BD6159F03FA0CD60A8DD955C578B5CBD0237CD607EC265C6417DA401ABB6234EA98257ECC61D2788DC1899F5161EC4DC2A99F5
	AE9138B28B4A2D903825BF204EC3826794BA07885C35E6640DD0F85BD167CC013B146ED34CFF8A64BE0538191D48DBA6F02B5C8467027B6027308D50E346393F4CD27DBE477D737B67B53F000C5F0F78AED458D33E7A9DAB0DEF221EE23E1EA770FD949F274233B670F79A9B53F74DA14D74B45ABC4B0675B0CE60BE68A638D838CFFE9A633B689FF09DA663E90C31BA58F7CC52DB8AB783B4250B984B2BAFD503AD5DD71902280E43697AB21DCE71A1697AB23DAB4A948EE952B328FF09A8137599547F38F8B6BF
	6699547FB06D236361BE85A0739954BF35C72FFF41330A4E254F7A523F2A0F64E7FAD67FAE65960AEFFCD67FAE659FE9FCCC6EED101678AC6AFF975DC7E3FA2E25F07BF308DB6944B2B5BE07FABA3D6C9C7E1C22DB74F33E741CC4FDE452F37A7E73A10ACFFFCE5FFF3658307F4C4F00F9D80668976D28E72240AD6DC6BDD3842E4B066D3A62EF08FBC770EE93B8379DF96B85EE299DF943844ECCF1D1F09FAFF0D7097C1285EE0E50A5CD608A985DFF033F7E3730B70DC72CF06F913855B27286895C5594178477
	697F0D6F508759508733GE7FA9E63269F064879713A73541A6D9E07EC5DC9033D1D32250F4FDF9244DEDC5B73681B4728EFC640FDACC05273F81E5A508ABCCFAD6A0ACB2524E70C4F45CBEBBE1A1B71BC2B7F22A96CCA4FC8D9056723FCA23EF7D58F69C1D3D06F9A3366FDABF26243562DFB5DF66BCECBD7A30DB6194E27507D3104BFF68A6AFC834DB3936E8BDE00FE9DE0738B649BE1A71157A2F9EC3623238C2EFD72E019B8D0228E423ABE72511C0ADA13292AEC5D86D3A5ABEB44207A5D574DEF1FECF8FE
	B6016197787C0DDFC35E9A1F4374BC2D8375CCA03A3CC8F0B7F5285F79A6FA3D15431EBD33743FE952493A514D12BA7DA34DBB63C4579D535569DFE852094EFBB9BFAB573D4B318E336F0D169CB7867B7CDED97D0B2CBC8EFEAD3BFFFC4E183026994C2E7BBFD16CFA59C8F6DDC76CDAE3B2D5B2BF5EC0475E0910E76C0B685BB1DD686F29AFE27FB2177AE055CB509E814CAFA17EA30AA7E76612F32207847E926BC863A54903DC4DAF0D7C2C3A4AC7BA13F14B08B2262BE8C7121167831F74923145447EBABF3C
	B88E513187F8228166F37913DED256DDF99FC67B178519B0CF4AC4FBDFCF6324FA38EF93B813D574B98277A325CB027B0CCC34736354BF33613E64651642AA00BA005617B17DB91A6EF8191D5FCC4ED2A667311233D6D9FACF075E06A49F7CCD729563454B5EF5D1E500C3D0F488761221FE7E2F363F513E02116A2FD0787CEE8B199B3AAC76BECF333D23279F67315FDBF0EDB064E51CDF1EEDD11F9540682E30E05F90AE68D2ACE42FC52F255B69A6F96EF3D9BAE0624B68DF2674241E23847D32F6FAAED81F45
	556CE4CAB05A0F5BD107B820E5FE555B0EFE15E8E043BC9D24814D3417953FAAF8C571ABA65BF1106AEA6FB350751403A83F64955455516EFB6EFF8A55379CE8CD2F203ECF8AFD5BDE7156B7C7272F6B952D3E41DE7A3EE2B1DE8FF81B4A8E857AF0G065BE2C67D93846EA845CD86C891385B4DD8A79982B7DD54FF16A84BA68B1625E02A316D272B4AD2BAD5DB9653D425ACCC4667BF200CEDD3D1EE98ADC7A35C37CEC539134CA8B758C0EE0ECEEE18CEEE1437DCAAA396701326E23D5ECC4FDCA5875930F161
	4827F04B7B32C51DA375E5B81E227CE8D5DFC67019D375F538031E7DA077117596AB5729550D6DA8FBAA76DB7709D82EF89A76895DB62227D95CFB68BDB94B185DE7F71D68FC0B64B5DADAA6E33E6DC4E5789AAB0C002D0D4D956B654320DFA8C0B4C092C066B42CB3D337F25E918C516465A9D1478D5DE84F62D751B6B77E0031FE632BFC9CFDD5A93B87FE0FF955E82D64B1BAC78BFF155563F401FF0A6223D5F896975AA91E0EE310D671F7916B0B79FA485FD127D33BC87D450A7BD0FACF4E4EE6778BCE38BE
	C783C41ED21FB5BF0B8C2CF3D61F2B45FAECB2484EGA8F88DEC83D07F1A986F44BC5E769A53EF78B525BEC2617744570C6A67859ACB453E26DFBFFC1D6213DE532F9F9E17F17DB083529A3330FE265267AF51F0BFC960FAFB314EB232D04FF5FDF4AF616B900BGB48158DE47347F21E964CCFBF2DEF89060A3C45A245ED6951F3A9CCD2F0F3E8D9A01A27F89AA837BE2FD5B7DF7E44C12307F0A37EB7B4A2B49673BF8DF7929937B4AF803B2297BC18249C0C3G7B6A0C5775F15F29344DCC83FC419BD8A75345
	5AC4559BD86EFB68D9F52DF0BF8CB08EA09AA0C1242750F4F276BE791EG7986GB9CBF30AC8EF5374E904F7A4DB8C7374F1A35091592DEF285B5900C6063F3D20230943B10EA33FD9DD5C37DD290BAD5DD897C5EFFA6B299EB3891481CDAD40E21ED67F26FE5C8C35E11F5A7CA64ECBB3F7A872CE3323BC1B8FF953F57206815E67F906FA497B5CAEE24BB7314F9F24B8BC930D511DA5768CC48B3DDAF7A9FAAD97F608B750CB6F13C4AF82681349EF6A476F45F6F26E214D69369FF176F9AC0E9D8EFBD79FB97D
	106B3DC5681DA674FE422AEDB321F50AFEBBE5542FB85BFF0CD88EB484D03F4D59FA7DEE173D630B9499F52A4B96E34F087BA788346A74E1FC637C72695F53E0FC77E978BC86E5D54398C8E77DF8A47DDEF2F836FE0F4E75949F192D5F2393AC633ACC8C24AD78873E1B5D3F1B0CC143F09F86908D10G1081107FCF5C9B12DAEB127C3C175D6CF4B54A07E407EAABADAB5F2EG3F617DF500FC271FB09F335BCA3A5E36601FFAFB1AA83E7C1FFAFB5EAC5639CC10B66E1F380E7640F76A352562890EDC64534B4D
	7AA72B6B7C3714F5B4D6A729FB08DFEE86FC73DBD887F7FDCC3E8159E269765EB58D48A63753A53BBD079916FE490B5B632C0E933587AB476DB3F1ADA966162B2558D36BFCFEDF4143759F472F3109636999532CACFB6A09FFCD0234B400FCE9FFE1A90089209E0015ED7636AA66DFC47D9D9E0F2543C63F10A1B6165B65C185CD9E9633B65DE8702C3AC33C2759867913E7559E380E81A06FB9303E655EAE6CDBA2G6F2BEF19492FB1C013G00FDCBAA7C5676AD0B1AC94C4DCF51E08F19C5DAF99FFD4E0CA749
	09BDBDA19F53E7490D16F65941CEF3E5FBE2A417FACFCC86573BA8E7FFE19940E24A8BF3CD897FE57E0FB78E473DD749E7C0F9FDBED90475D9FA4DE5D25DE3F5394BFC5467669CE613B17CCA7CB25EDCBB0B693D456AFB2E7F8DDDE70A873EC4GC61F506EDDC7FFED47B2E66530BAAA4E5DDFD88EC05EC3E1F534570AF534AD57380E54E3C09350D8F3D99D117B21DCFD9D0DE9B7DEA30820735EE0208F4DC51DD3D63EAD6AC63DB760CCFAD63BF2C2986E37EAB82A452F6378CFB7113C7989E9FCDF466BDF2AF0
	789D844D3AAE29F3E207B8210F47C247C977004C6EEF3550FEC7142D20BD972E4ABE32EF5A111E2DD5DF540E4F8112F2CF54078E124F4872FC438F9239B5A26D64C6A56D36F6B23E79DCFF2FAADBE72A44FDD56FF620BE85F9467A307CD7FD29687145D70A2C37AC231145E81B4CDA5B0DB367D1DB19F8FA0AD9EB3BD20B56F6B68B6A3AFDC4DDDDCD0AFE6DBFE86D745857CA5A2566515B69D5B34A9E9AD1769B5FA8F6DA582C483AE1D432985F76EF95BE271B486C34B87AC95BE66DB1DD443151F93E7BCC0CE7
	A781CDCA1E529E2764695BE358A63A4FC6F34EEEBFAF778691ABE6819FFB7651E157BFDBF9D9D2BF9BB94F2EFF361223C93F5B6E75ECC573EC66F53B7E598A72B3FD4D74DBBF6A0302B52A53E028FADF4317FB9BF79F5C373BF1772E66062F3E647335449FF01D5E143F1F4F5758F3EF32CEDF27604433C11B405D51016B7431006373D95A4FA642FD0640E5D01CB4FDBF7D6EGFF0EBB62B38DB5103CFC25E578C8E3B2460CDA46C86BDD3E5E612B0572C01C5ED9B682EF59F4269F6F9B4A6620DF293EAF4B7C6A
	EBB1D79B1E0EFEF55CB632DFDDEF9B592F1E360D6CD773FD7A952B5F8B282F4700BEC953314E6F33621ACB3A40B50AF532029908FB0962DA615EB5837DE0AA0D730361BE82A0D6605F22F8F2DED3926048D9548CFF0A6C2B7E882F9E97EC00D3C304AE1D493848270C115E6B9C2D2F30FACF37FA4799258E0CB3B2E730B823E0A6441FB3D97919EF85770E64DB35B3553EF5E39129FF75A9A67B78F7A9D87A787EE2DD7A1E810703255DE426B1820DA9989743FF3DD109B588B5FE7BF7C627BEA6A0BA5703BEAC7D
	0B8E2374B6C8E73E6EF46258611AE9B4F6E03F5FC1BEA345F53950A01E0E9171F464CCE643F838A68350EFBCD09B165AC63261940D8DBFB8B0DFEFC355D9710CE6C9A53589CB5EE135A2FE62D6D51C7D8D7D440260F9FB860D271537A7787E62E7B63FA76C2B7FDE8A5A7A24EE3DADC959248234E5F7B75A32244058163E4766D377AB763D52269ED7D95E3758B16FC6433CB11F2BDA15FC5A6D6AFC986EF23BB6EEE991BA87C33EAC0F4483CA9ECDF672AE2FFABC5BC77E6E02D158E30139D7752E5DDFB66F7EC6
	6859AFE3FEF1BEEC40E43C74AFC546CB32DACF76DEE27CBCD20776BE073DCFA627606D15C93D337EAC1D764F53A07F02D958474D20F22B60FE7BAC6C4B2E6BA4B2586F1BD43FCF77594F7CA70049386CE715416643433BFD3F0B732B5D466F621C365B78BD6733F70F7CCE7AEFF67BFEA77D225D247E1C03FD2AF389575894FBEFD47D3E6DE7F9DE2235FD4A4FAA032DD704FFA7895BD46FD9AE79D8F359516E798C5A8E7B74CC3325CB70BC39E70E5A5EECA90627E56C416F98526F7850CF036E83423EAEA67B2A
	B6125E88FEEE83888108G0819056BA40E3D64BBB86C94C47D9A49EEEB170C0D9765FDD56F5B0FDA6A5FD37C08F0F5482AF809519C2CA77DF03B933A5BDE12BF6265FAD29F9E270BA2992E3B1E781F5B5EE573B0D20EF75513766B10BBA91A2D1D1D5109EBE7C9D0360237311FFD389363336D00E3FB3FF268BA5B905C0782C48A5A6CAE6C0F625FBE51B9ED6FC1C9925F176D7471FDD9BAE2B0D91BDDA48FB2FEE7893D3EB553BD19057B8B6B814CG2C8E3F234F650760FE9CC0ACC092C0BAC0B6C0F1517E42F2
	004DGCDGB600E100D00009GB1G89G29C5D867DBBF2B74650300649B68E8453B49FE95D146CD6D12047D70956DE55248FB38608F7F3E494C8E2C1BCC5021E2B65A7BAB315F4DBC9BEDF014520641FDB4C002209D92679B244D9EED5D60B8772F8E251ECE33AAE55859B142DCF68394E103B2EF5EA764AF1803E346E56D6AF550A640DB670C9C4BB23E77B56B27C34093B2AA3EDE0B1AAF82F8E2C6457706D94D17GBC29FE78D819670B71BEF34E095ABC0AB4D7BC934D6FB7B474405651DE2573590A39B07F
	84E031636362FDCBB36006GA2GE2G48D9B3ECDD61EE711CA7F12EF7B975FB625200E65ADCE5DD2120F83F6E79587354CFCBGDFD60C6BE73F7F0E2DB510962DFF27E7FE11AEEE645F6F79D0564704C467C611672BB4D65EA5136F4A609A6B202CDEE3ADDD3CBD97670B7AF2190BD9D9C30A15B5D64587DA7CBECBB23253BC69F7F65FEB337752B8B39E64A596E37D3CA93EE712D54C6A27789D681FG48B9C02CFEBA44BE1FED6FF8570FFE1FCF9361FDC7358E7B0EFE5DE7C61771BA6CA25A1F8783FD68BBE8
	6306C5F4BEC55F37D29609D85AC5834A7AB9FB970B6157FA8A959EBA4FE275791ADDDF1F240CF1A00B75FBF58EB1A6812E60DD6C4BC29CE2CCFA977B3DDBA86D905C0782C43E0B7ADE52CB6C318FE6CFA4B0E27C1F08F7C863C51E41E27DB36BDD6C1FBCCEF51D1B666DAF2C1BB7F23B675F0092E3E9AB50074EC39965E29FF542BC547B9BBA16CAA57B8B97G18CAD06FADB635AFB37ED8511F5417200E4EEE350EC1009FD7B21ABE6DB6CD1F9289BCF123627BCEA3AF89F852C745B724B977A08BF80A660F4C47
	6C702641BB13AE31672A8C72588CE085981A2F1E53AE24739C8F5904AF6158D5C2C2C29E83DD5F66BB8E0BED739F075D5EC66AC2067809FCFF15BF8FECA33ABABBBB7BE5916F5D5BD6ACF08D7DCD4EFEBBE9B04CA6E3C7FCF74637ADAF8E10AF650479587356816A232173776B1E439E2378F0951E47AB62B9ECA424953D073E1A2369C3DD008FFEEFB43E73098B7942D474D17CF7BCDC1381B2D4E9CC3F49B49E4CFECF2F77C394DF703EDE6F6F4C28F789248D3D0F711E536C5BD78E197D7B4AF133F14CFE0A59
	BBE6BF5DEC9C33071A55B1BB431DE7A6BE5E8571E62B201BA0F087846EE21AA7416D37DBE9BF484F7620FA91FC3DCC84A27E9A2A57A0453562B9CCAA5C810E3B51CC7C9D0AECF33A1177967AEC27536EF0307BBFD29AFB9F383F30471DD4C73B4B2252BBC1608E08FA1FFD424F4979BB8B8172B51FB09F732717E94C1D7A3E5E4F5E20788CAFBF7B15086727C1DA6B82EC9FC7B56F214781BEE941E85AC72B617B6B691CB74BA08F360EF38BFD86D97481530F61EEE3FB43B5386A8EEF3A759D5EF44196EF3A53AC
	5EF46F98503D2721E3657AD0B356EC0734668FC636871FE751B5903E8677C319B45AF9562F2DD8AFB61093814066234FD6EB3EC5475E13E2EFF631572478787C00954777148FF0FC7F874D3378C3183781EC7F90E301AC31F6577A61094E8DAE36CB222CF7F4ABE55DE39D7DFC6CEB71BD11508FF11EFCE58F31D5825C278394FC8471BAC0ADC0A30099E068A36C93B3658A49779C1899C71982B3DD3F6AD5F43D43BA7AFAD9A676C90D8379099F218DFB44DE317CD23477F8CA3B8D6E1B819C25C8BBCE44FC4125
	C6764E9A415E6EBE455E67B915B234492337F7B50D5D6859E625585EBFF38456CF358648B7760479D8BFD6CD5FDD1DD62A6F5FB6D13C34D05F3F450B383D8852DC8B313CBB8D630F64052CCDE7AC74EE5B0C6FB443FEB91B5397AF72553F1D61746EE7C21D5E7D4CD38146D3198172D586389F6EAE9AD715AF5257438394EFDA242F07D69107ED033408C5D88F37F69B494FDF4C6C38E031AFFB1E5A6DED4FB33A3D7B77C76D5EF4FF31FB53398D729BB4486FEC591BEE1C2C26635F1637637B39D5D0866BE26C3F
	4F7FC61C6B253C1F5BE86F13D5EF67E2BBCD93FBF44397E31FA253BEBC996E27G6417219DFF2273897ECE006A776CDFE05DFD454FAA0359F8014D5B6E8BED5E7553CC57E2CB41AE1B4B7099D1919D4F9B613ECD60FE9467CF5884AE043D638477C982778AFD1FAD9D6E7317202D9FB0AB76CDD17D3618FFFEDB9F7985E43C562114C932AA3FCF73FA3772BFAF7B521FD586EF2FD6EFDF9AEF7576A5A7FBFF966A3DE2897AC332F8D736F68936E94B8A52A679F39BBA2D615FC49D1A2D60F1DA43732EB168EFB6F6
	FB0F87E79A46537E6DF8C100FC49A74C477A7D9F593E12A57A7160884507AE510F87F9A20E8A07B4E9A9362DFFE856E62C009FDABA1AF9C70B61D916A11CB702DFE3D4F97173A1FBFC4F27EF6E71BF1F3E2347F8BE1D5063BD1F3E2F47F8BE7DF00F77FCBA05676B9607C6B37CFF75901FE2FBB03E3074D93AD44FDEAB8DED605FF61B8264F31DB09F730959F4CDB2E1295ED75E23786425FADF39D878CA9A2455ADC3DF992F09F9A2819F37ECB43E325EB066C8623C697C1A855722727D2AF711EA691C012F8329
	FE5F6578797B453F7E2CB2D87FF3004ECB4A204C26F26C6FEE917B4A3689DCC487520588DC0E6897434A31FE92D5F148A32A5FBF7C82B14960AF9013A4F7239DB2049D3674209D2689BBDC6477191971BBC3D1FE790C5F99FAC643D737FC64B6C1E9E87BFB1C46162D4B756D123D33EBDD2EEF17BDE20D4285E9494B71F9C10F4B77F326E09A1FEDDE816DF88552AF33E85F730AF591DFE14F1746D8C6FA0FDC3F4FBFDAF7A606EB057E7952E5EC1F3FE5C877FC698FB4769B867AB1C22FD966115FD9DBE646B9FF
	0C60B9D5774E5AC7EE25AC4FDBC7D396FC5FEF1A6A9C863A0E23AA8B77F155F359A0556FD93F40BE2725BF2B8CFE1ED627F70C74C127F71C56ABE31C163E825B617DE22FF4264031335FCAD76EAF5C36925B66BC557E30D2556F4BFF819B7E719730E134018DEFB43061E734AFA367D8B52DC4FBDDA34E31EA9338F40A9B8377D1AB35FDDEBC5C272D44F670FC37369DFCCA4F50E36D60C1714CF85ACA7F4F748BAA769716D6A86FD719AA14F6407236F8143C97A86F85D598672D06A6DD5ED62F3C0387143C1BFA
	B0EF170F3C556D37D417F730AAEFE637ECBA3F8F85FCE4857A63DFD5732D8C556F9FC73D0FBEF0FFBC7DE715416CF965A1C9423EB847C256B9FD2CD956503323B6D4F23EFB86A511478BC3CA9E4F9D9CCD9E0CF62B1954052FFD2CAC6F62A30A1C75BF115FEC8F73B3F25988E50B2BD0BF6FDD54B9723831D96CE3CB2A403D37AD430A2CBB3AC712A5D52AE53D597D1EC696F9498C1F6C62187BDE8F4AAB2944765954B3320E5D02272E92572DF3451EBACF65096E29BBFC147CE679B4087DD7D1BE72E1BC614794
	1E1344B95F6915E8335B0EABFEB02F1B4C2D582EE187F984484B311A76D7252BE08E3F8A4B1E5BC972795269264F9F996E2B4E516EF7E674FF6D764EE3CD4F0965F1522869D97B1C29D92B2D0772342D9A4DFC67CB4D1C5E85BC412362BBD5F39EE59870C4796163FDFD1ED2AF1FDB6771B25236C9AB273A1DECFAE5FDA05B57239D1F16F5F90FCF32411ACD37411A4D05BD5EF4177628695E27FAD5657E8883D4548B5274FE37742EF75F31632C479F0BBAD8D712AB65B6C8B3ECF47126C11ABEC8D6F706C8FC7A
	86340E507E9FA56A23F415AC41A3956D73D85C1EB2760E2FC71ACEF798F9243C9DD62BC71A41DEC9A0D438D929223F52A3651377BAC00B39C20B867A08BA6D507A2DF9CFDF1617AB65038AEC0921C14AA76B8420091BE91467F13A28BE12243CDEA7D13DC80348D7035016ECF52A787FC2B3DAADFFCE6CF4B588BD938878A98B794441BA8D4DDC2471C89F11974F6D1D074B2C960F254C63ACEB174BE46A8832B5C0A9A744611166B03FABE36F0BEF8584F9BB394CCE76ED9D32B8GA1317F021128EF55D48ED610
	AF9A0D829E69ED31A086394FE49B6D367E876A4AAD4D517699B9173F7F2B6233578F64CA85FB24991D96074BE601D6C08F56EB148A5BE9F3A00FF99D9E293811702E855E35147763C6A937239FEAC841F790BCCBA5475749C0B24BEAF177509C08CA355FC933BA5DDD6DD400C74A7DEE4FF7A0165E11640D0D525B9E9B59D1CFC2DC0F743E12F593F9484C93BEE6CD73AD786F227FA45C8BDAF9DCFD8EC456A99DBE4F981D3216753912C9598B4A1E11A7FDC87A35ED00EFB5613370328F57C83E5FC95857EBF1B8
	E0346F5951E7A56FA76CBC6C11D55FAF7C10645F0FB4BC111F7171A1513BDB45737FGD0CB8788A6171DE8B9B8GG9842GGD0CB818294G94G88G88G550171B4A6171DE8B9B8GG9842GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF3B8GGGG
**end of data**/
}
}