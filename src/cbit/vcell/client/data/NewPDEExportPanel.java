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
	private cbit.vcell.export.ExportSettings ivjExportSettings1 = null;
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
	private cbit.vcell.simdata.gui.SpatialSelection fieldSelectedRegion = null;
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
	private cbit.image.DisplayAdapterService fieldDisplayAdapterService = null;
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
public cbit.image.DisplayAdapterService getDisplayAdapterService() {
	return fieldDisplayAdapterService;
}


/**
 * Return the ExportSettings1 property value.
 * @return cbit.vcell.export.ExportSettings
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.export.ExportSettings getExportSettings1() {
	if (ivjExportSettings1 == null) {
		try {
			ivjExportSettings1 = new cbit.vcell.export.ExportSettings();
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
public cbit.vcell.simdata.gui.SpatialSelection getSelectedRegion() {
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
public void setDisplayAdapterService(cbit.image.DisplayAdapterService displayAdapterService) {
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
public void setSelectedRegion(cbit.vcell.simdata.gui.SpatialSelection selectedRegion) {
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
private void updateCurrentSelection(cbit.vcell.simdata.gui.SpatialSelection currentSelection) {
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
	D0CB838494G88G88GCFFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFDDC14D5763815959595951595951B6CC6C5AD959995951535D42C31AC95ED38E1592E6D5AC69B7B7D3A3BFE3FBD3C8994A9EDD63431C5058509080A0A08050A85CA0A02CAC949408C4C403CB1B3A8A06A6F5C17F31F17F9E68659F5FF5FBF560F07E71EF34F39675CF34FFDFFF924259F0667E5E5AF15242CBC097FDB1FA5C99977C952C163190790D777D9F22124787791401B527E5D3970EC0630
	36F9490545D2798785705E856FC94FA5973E8B3F5F12AABFB047418F9FF29AA16C196FEFDE69DFCE36936564537757460A603998209BF064997599117F71D88947CF65F8042CECC98A2AA0B41B46CAB9AE83E88AG578450D941681D70E48A1CDEDF47693AAE0814B29FF1E53FC162D144091044F9B37933CC7ACBD6D8A51155C775A4BCD570EE84A0094F11BC255570ECE89B6C7F2237476229BE54EB32DA2BFB2D96135DD3EDB4F88C7575BBCD075B1A36ED9BF1BADC1EB6035DE41524124FC91CB70D9550B8A4
	A99D620B20383F7693B9DD705B83907CB962178D907CBC78ADADCFAEACD90EE95B74D983573D73486DD24AEADB6E929FE92BD94E525439DCCEDB19EDF676FBB49F368C91FB07C2D8ACC0A2C086C0F1D5F2E18D407BB47FDB1C1F43F3F30FE72B43E6F358B7BB1DDB2CC657B78E23498A3F6B6BA104637618FA9DAE23A431BC360ED56968638F985F588973B11BC42E417C0E5AF31314F66BA55979BAB6C90BBC125DDC45EC51DDA55B04493E55276CA9D5EAFFD44B5EAFA9E5073C74F8CE1E0E6C283447F3C2F9BC
	118A59732BC8DE3B4DA42F9360B761A33E4A7027D2FC4D8A994FFCB30B6249FB8704452FC05F68DC0C3A3466DEA3952F3AD4578E536E8F4E2E2AE6BAB4D5EBF519EDA5F1BB2B19CC1B502554C6709DABE5BC53E5B975D372DED7833E3BA6391079578EB7A92B456B138B2BG1AG3AG46812681C42DC79F3B5039F202BE56E6F0F9368CDB2CC613CBE23CBB065E0127E94463B234585D9E033D57344F603298FA2CA63744E9BA06088DFAD4FC5FD25F6F8599DF1B6CA61741E3F158A1F6034D64B1395C0C26E768
	CDC263B251508E0B4D8482ECCE89639D98FA8B1ED6035B335789455EA4D330F057904913C306FD90A3C4G3F19DDC6BC44DE09107E39G19428EAF5278F618DCD0DF545737BA9C03434E8E129229290E587991DAF794406F32BA3431E718600260BD3C8E63A9F77DCD91CF13251728EFF00DE2999BB5133447GFD9240FC00528D49052DG4E8D68C7FF68C4BFEAFA63C6296A710BF57D28EC7663B9419B187FC4EC501627A0AA07681A88E173810A6A4197GDA6B31AD48B7F77338089EC4FE69866F32564AF1C4
	67095489C99ABAA66BE3A1AB1F171DC29D5E5779B3E782F28864E95ED93EBCFB0864319552998490D90FF9F350D546FCFD0B41EDC26F5E658247B499651CE27ABEE7CD0567A7G4B38DE44BF5E65B27E8170A140DA1257G9BGB682D482AC87D881C04A55AA00B5GADG87G3E86789E4081C05A0DA10075G5FG6C82588470A55191E0ABG380D74AD40E9G87818C24CCG34836C85B89DG0A1AB48CF026C4EAC3D6971C85F0984084F0A540B9G7DG9600C1B9CFECA40D71E01FF9G459B138B6BG3A81
	02G2281D281240664428A00B6009100F00084008C0012CD49050DGE6009000D800F40002AF209D83688418829089108430G206C4B6442E6GA740B400F800F9G450DA09F209BA088A09AA095C05A8C72815AGC6G428192GB2GCA3600FCGB3C088C0ACC0BAC041D67471D5835DB97A3E033E3604BF5F62CF4C63450AFCDE2C700545FE72F9AD4FCF7487EDFEAE66BE50427D8673FD31A2CF7773BCBF40FDC5196FA6CDBE5A38AF8DA97C60B0779BEDFACF637EF2A6770FF3F4E834F06584E87E837F81D6
	560A86FD1535FC7EDC451FD87713B278867F3D044641FE137A9F6BF6D2F7E3B9A4E39AAC07243C12720665D5DB5E88AFA9231F813CAD49651260C9195BAD31B236CDE2651914E5E539A465CDD94EC8D9A4E51D14F3AC2724DCE239D682A96324BC13F2CC4AEF7F84ECE87ABF100F7F017F7B407AB2FB5D243C188E4130374964B4590D70434D433A5C24FC71FEF91FC37074D03C937A71DB21636C96F8331B14BB03476332748CFBCC6DA60F47E26F17431DD416FB3847A98F81D056E1B7A9270E43500F977463EE
	52876EF5586DA65A2992ACBC7C986D031B2CA69BA83D55B1EC77B07CD48F192B98B40D72B1C010E711C47A7056E1B21EE0637FE88329B31CAE0B8D3AEAE4C8A2CD59CA7A67F3447C85019A4011F90C8EF81A37E23F7A70A7582FAE9F3BCE0ABF684C569BABCE7B71795989DBD99FB6ED2B5C2FE6BA0D8E6B6BFE1C0E85B85E1DC3F1FFD9C9F835F391AC6C4F0B08CDD9G9AFC958FEB7E7CE3F8B6197A8C43D6CF2B456D2143312D16C3962BE54CE4D40C31987DB9231F7823EF872DE8BE38B9FD5BA2D29FEE9976
	F89C762FDD0EE127A0DDA5E11C533B08FECE23294960B1ECF558BD60CE2BF87F7A7D91E20305446ECD50B7EB427E75DF07C9B80EFDF847FA2B434A45C318270964D512F929425E0DCDAC2F960B70052A70B82F708A0B7F708ED5F806A23CF99B891FA2424B603DEE1B9C4E52B16FD0B171F75350308906344454BB04CF3EF468EF1A30FD5CBFD978FF9FA27EE9D50C9F4B3FA2B203ECB8F7968272A28192373131D941D7CCF6AC259B98C5BAA73C133EF950D70CBE8C1E55006F781A4ECF0939385C2F197E257CD9
	7BB50B0F51BDA3684261BD96A0155315B4AB65CEBF02F49D4DEC9CE00567B8C0D833D26E50B852C581FEE6332F78169DC53A394DECBC31901EC5AD5A78DE92F4D5ADCCDED38B0BEFEA0BB23EDB0FA1DDCA8B9B174CEFD16B47680E9EC73A051CEE69F616EE355C2FA524AB5F4E7452177BD7C157301D0D01BA362B5341689E4CC2BAA3279B5F4E6C2316FBFEB65285EFE77AC7EC57133BBB876952363371D666F63D743E1FAB7CE08723AB5D21175E6785DD558E263F3E5C4B72102EE9879B5BF56D50CBEF6FA224
	33F23A29BB5469E5FE3FA29F69C2A1BC8AA0E987737B62D6251F9696A0DDC5AB23EBECE57ACD61F4092D046E6702AE2C150DB7C979A0F4B11C4E738D213BE5995215FC4366BEEA605988508D700120BB65D3240B7F064D13907DD3617782CE4774EB7D8C690AF6B2F9657C5938D3190E7C8AD14EF9F8A4FF4629686285DDAA8F4F5050B1BB0FD5A2DD41AEB60EAE5B4568EAF6A9732365F311EF1C2EEB179C2FCC7756F211EF3B5818FC2AAE5DECC1974A6922B4F2D97EDEDB05F4F1BCBCE5971BE71A3F0B250351
	99D6A0DDE99B2F275A74685E2FC63A56B67D7818FE0F89BAE39B1BC79807E7B0C0D41BD23FABD78A7DG1F821059467C21F637B2BEC30D08EFB71B07985F4D64876FD64637FA9552C582BEFE379CDF7AEEE57E7EC650C9FB183F176C61695E2374539F2DC63ACE9E6E5C4366680277B07FE37A8D2E91FE3F074D174440F3669EC6A7675B5ADAD16F6EE1757842BD2CBEA8EAD752652EC53A32F6D6BF573533722B261BA5681A5B593CCCF7BBABC74EF6E57DF275BAD12F35B37DA35AF939ECD75AE57FFA24DB4069
	0ABB98DDC507126E4DBA614F9D2CBCF6F13AC895DD2A20CB68E0FE122ECBF76186A1370365D771DEBD3A56FAD18F7170C6DD3A7C0DC857351759432321637693F471FB59DCD7AABC6781946CD35AAF2CC154D77B18DDEA76296D47625B3FC9140FFDEC2E4B4869BDBCBE56CFFE2512740769BA59006110E1245AFD983A1875755BE9AF142D076DB8C27A2C243FB2F59F2EFBA5507530F970AE6D871F5D0FFD67DF3C016BDE21031112B5A3A547571ADE4DFEEE277DF25FB9A172F08E6BBB1AC6094CD0880B580F7D
	C657E1655C7BE6375B51EB21537E3CCF75510832EF4DF01F0E3C864FBE0B4B6DE901414F88763F2AC6083EE4DD002339BD26FC7829643DEEE8227D6F7AFAF5BF9C57160EFE25CCF35C1223592F7BC8F3ACCFEB2AA24DED097F1543E4DFD9D621125DBFEC292F7759DDE7BCBF796493EF9E3DAEBBDF873A56D2A4313EFBC9A774BFG02BA89FE2E050C29C81D4252543B865394B276E3297B624BFD26A9311325E5FE271C26E91F2E4DE172EE36213C72EF21EC8230F1D87C28F29C968A78B800F400CCGB636A831
	13F9940B47E42BA6FF5ABD860FE92B59E06FB799AB0833D69B8EFB2A6950363A85423711DF15FCDC3243CE62F553A10D49781543E5B3F8B03FEE7C2091F53F699A2940F0FA365E5AEC708B7D59C55F31B455FEA727EDBCE5F6F688GCB5FC523183EF12009798E571F17FEC11EF4CCAA61D829E86C8D05CE68F7CC2771EFD127A909B1D249DB676A6AC46D4CF5D9FAC0562979595959DD4F22CEBB691AB811EF841A3083E84F5BA85EE237F8AC8662939515CC5727BED84A714CCEDB4D8ECB2F49DDD1390F69F64D
	DBCAFBB59C7FC1D73720A5666C942E53C205EECB41D69542DE85C7D03766AE7085GB696B377AB47E23100CF87487D9EF5F79CD9CD3C1266FE35DB6B893C5A624E40C238BB70F2BD3DE585353D07ACGFE65F2585A2D9698F7D63434AA7937F7407075AB0B49EA5CD839CF42355344A3224C00BEEDC227B563244C98DC7D2BD99AAED50DC7033EE7E908967487A83D47ECF133F8AF9FC7DB4C841A055F23ADD2FB75ECD17B8364A54B5F2328CFB360267CA0727728DA1FD5FDFA7A440AF8CA0E217CC440659E44F8
	0E9FA336B65041F8B5FB3419DCFD609B5E85EFB38DD65B3AE8D7135A56422A953E2C2D46333C01ACE0FA269D4374B6000E9E21675663BC3D2B985DCF0EA3DD94504C95F4EFC8E897C66714B05D255DB0CE6BC6FB14E7A1FFB060E6F6A37FB9D9EAB9FF90F48B01265600F4FB3355F2E233D10E95E8A2845D07B9E43FC61F23F758DDCD7F36BA5C9E2DE13FA281E8D7965FD88E4ACD03381676E0FCF3F2547AB565A2DDAB50CC95F4B7642A75DB1C077A4581CDE68F5A617EBC642F6A05FEC3AF72D7AED24B89DE04
	F4E1C013A268DED9241633AF9F690A0CB0FEB6E29BE034A85B00F1A3EB83A26099EB44386E4E1F2CAFB27E1773C93B653658487CD447285344ED393AG531E84328ACCA86F3202FF4E776F4D226D0E476072304EC28529C8184C8D4BD0E6A3489BB1E1DBF1C99E515148952C28EC61ED42DF7F0175EEF9D304D417B96833CD08B671FE05C92EF7CBE62C4D291A016D70534BB08FAA7A20EF5E07695D72A956C92C4EE8F5A86B0CE9C0978710AE6887BEA5BA5BC8CFC442366B0F1BD1CF52BE4466876B6AE93EF6DA
	F6E69F532F34DF56D3FAEEF676526730FD387ED354538AB4917DD8A74E9A546A1586E1129975DA7B19D2AF965770E7E86F25C057AAE853BED3768D76187A6924A34B0B5BF6EE0167B05DAB32F55865023C957322952D0AAEA1389449B152612AEEF79AC813AA88B74AF9DED301BACC8579F1C207CBAB884EED0506D6C259275A0A056CF692D2515202F5608BA21ECC08234A027134D5127E2D1D345B564DA396B74A1DDE09F6B482ED1820FF6DF3AC436CFD7567CABD982F6BF364CD813E228164FD6AF3F539EED8
	0EF4CDC0B3AE6826AFD753AD2AC23A982019AF68ECD56A7AA4C950158FC2DFF19069562F506F375458497C374128E9D6B67738BDAEA83A2C972C480B33D7E07C2190F722087FE52F78F9BDDA0D743956644286AB560F8B845E8338A8AB46F3F0A52607796C10CB6933F3012E58866D058D694FDC296E8FBC34D2346BC0B345063EFF06CD998F29A7E3A1ACCD443322661F2B335277F2BB6EB49D5E46C6CF4C2255D668C8D56F6A99G37D65A3186754CG9DAA6C420EB5EA5FFAEF15DEB9E3F19CD8057E5C8D7C53
	6C18D613C3AF2DC9F62CABFF1ACD4600AE134DF14844F55D6C2900E24263DD329A631D8BBC258E546D50EA3D71G0B7351256F08F247AAED96CE236571BE329A535C81F1860BF87FE2A1712A57BF306D593BC61D3F21EBB00E19403FD0447157B5EA3F394621759BE203DAA77A5F8F3542CFGB7450971BCD82B367F0B354AB18863DD3196F9E301AFC370062ED517457F96F495C350DE8CA15D8F6B54B24ED827B726E1F1A42C433C18867CC9A20E156B51AE4C7EC71B56897BEF36DAF17821D75B36B5EDD398DA
	6113D356232C82D7F2E1238BE57DF6BD299FBD5091EDF3D84852A74F7362AA392E25FB82AB8EB4E1DDDBDF07F10DC0BC51A22E9F55B542538ACD2528692B29F5FD8FC5DA15442C54B1795F3E71B7A13F8592636AB3C0FDAF27A7EB836AB08F641739D1877306C2E1EF36D646686F92741DC09BA268BF2EAF64BA933BD58B7A61FA24CFGDA49A35A46FA5FE536E6A37254813DD370DC3A71DFE1976EF79B516FA2A17EF4A1630786F599A998542BDBCB06117E0C86F559FB3081F52F829A23202B5AC47AAA4437DA
	485238254FC2962039CFED927DDB20CF91BC3F5D24AE83CB3EC0D9ECFFCB5B11CE05EEC507A07F8E855E93D92BC3538C38AE8D1E6D73ED20FB0B1D0758BE5FC201EF2178E0851E25E5961D2FA36F1190D6F4987726260C13BE38935E0381A2G62G52G969E46F93718913217456F5EDF6A42F6688229967FF18F6EA8315320E9143F5BC71575925F67FE0C64E7498854F3G2DGC60029A328431FC6836A30EDC4D78716A7B7DA483E710811096DCDA540786E22FCF1A740477A44155FA94FB6643E39BD1B58C0
	F7BE07473DF0C46E93B31B7C7EB831C943E8F2E1A70093A088A0F294ED72A55D8B6F57A63887009B032569F6DB207D7B8923FAFBF39F8C48B7DF43476A3AFEAB51D7E70E99746EF1ECF10C703D99271935FBB3483E645CB1A85784683C7302D1BC1BD1B306F351CEB771EDB33CCF8118B6063E3D192E35C643FBA240DCG69C8F2E1B9C0A30071885A711C310D81FD0BD4D19D8E56FB5A2A5D31426DF35968646CFAEDC03E29C754FCACFD7751B562B0880B85C882188FD0BC8E63830036F1CC5F6C31AD0152A736
	55C39AF1FF0FF7B2393E9DDEE7B04C63EA7D7453596345D72DCD6738DEBA5751B569B0880B85C882188FD0FC94528950F69453D9F3E4538473F1AB601A9D0EC11DA4F2DD979F19DC9ACB3D7834E9F49E55ABA3259B5E53AFA3ECAE8C42D899392EDF2F0C84C11CE1C7310CDC5DCF4A886917459C457D4D49DEFACD4CB7538372159C53CB4FB23AEE2D139EB16747F41DE5AE6032EA0E212E616313337DCDDEFCDA5B1BB53AB29D169851DED3048E9DC7A7E72F1E00FCF3F5F5B077219D968A9DBEB8B6B9BBD4F971
	E96DD0FBDC0F6FD9AF3E0925B9A3A0DF2B2E3C28631313F7E7C0BE232E3CC352646CB9550BCFEB4FF1DDF91FE7CDAEFDF58179C2F565E5CED2DEF6F6A03EA8DDF977F871CD4C1E09DEFCDAFB4E5415B7A5E7F269BBAFA0DF1A2E3C6D391313579910AFD3D75E523C495973E3AFBE2DBD0B266871A5F871CDACFDCF8464AB5715F7612249498B8F485720AB2FABFFF272868272F5E86431B6EFC93B0FB61C4DC4CAD85707D9302E37CE41BE6C9BF49DB9825E63GD226E09BD0D7B0B91F784A0BCF6B9373F5ED7665
	3249497B460BCFABAF77943D3E5D8C3A0F3094426AG5AG2CGA1GD1GC927E05F6E34A3642C46C47B30017BF7F33C741D18EF3C9410AF6394BD5FF83472DDFD5FA0B3A13CEFF701CD5937ABB8154F7B413379D46C5B1D66A860F1F61F0A639C36F7F29A3C47GA4824C83A8BAAD3930822075B4346113C7820E83C467FFA2630068CFA7E743FB827299CFD373B17B0C8DE86DB38EF461GB12721FD722CB8BECCBC8D4B50114FA66753E7F971FD20756929FAFC6BAAA6A72F490BCFDB06CAF5653DB4C9F97F93
	D0DE2D2E3C6BAAA7176F378764EB5515B7727964641D91104F28ABEF4B724949EB8B48B72EAB2F28EAF2727E9E10AFD4D75E931314B7B7A0DF142E3C10951313F7D5C03E193A7236D7CF325F9610AFCDD75E7FCED2DE514A007D4429FAED49BBDBC8BD2453166CB198AD8E6CEC30BA73CA07F63E20687464429A002653314E0CF4E11B52F5BA56190981F553CF532F8272051E2ED7377F4C2A27E794C03C5073EE9B761BD2051EA1B513742D00FC45E7687199D6CD2E2EF5F871D5EB5EABB472F8DF5101E9EEBC83
	537C766A49697009971F36BD9B51CDF312975F44EC7C6B00FCA13A72AEDFB3B9F9118179A2F5ED7C9435B119370F97B6B656CECE87D7C03E853AE9DE33F6F272B68764ABBED3376D1F243C575785622B5015B74B0BEFE2BE1B6645277559C6DDF9E72C1F1C3CD0AFBE2D3CCEDDF90DF5131337530B2FDA736E5415173FE1F2725E7662534A8B5215F75FA465BD96D0DE382E3CA375136C33FB71E965456A4ADB37F112FDF6AFBE2D3CA47D72674527859F652FA1905F3CB3756A38AF87319D1102300EFB54AB2E09
	2579A9AFBEED1A6B02F44B64264925B9B4A0DF1BC69E3B0B49C54F2D18A1EC9C00617EB0C0F061709EA3F0B6BAE6CC0577CC015BCE57D08A4ECAAEACBB8BF103F42D22895E3B4EC29BEEB113FDA4CEF88F8288870893E13BE898BD575045462AC5225F1252CF768890DAFF692265C3074649F1C1BE78190C97298C3D7B13746835B83CDBC9FBF79282DFDB9C77FD3F405CF17D7B85FEB72EFF3FC046781B8C8FF1A8EF91437085637E6F9F100E7A3EFFE07BB15FFA6EBE262F677EE37AFAFEFF4C3F1E26E33E747C
	87356B93F4DF38EB58CE36A55128D93F5EB4C26EAD595C6359B3ECE7D3B4A4F637F6ADDF4239334182326FA465AC1C5FD9C8776DEB67F73699AD9E078BA369B099DC3B7A7A4464CE3B53540BFA3FF25C370D7EF2DC5FC64C3F3CED34C872EF234512FF9B7D56C36E9E703A9BE22741A61F2779032D1053EC73B83EA1FB48D8DDF7179947B399E7617C6A8DF4DFCC455949050DG1DE7E3B97C9F3FEB6F4E333D4B975BE7B3176EB918FAB65BE713A570AFD2FC08824F765F9C22F87A8EE152B918E7BB32FC5BFCCF
	163E4DBB33681EBB47302B5724356B412C9291A6CF5C1163D144F47C9E8F4A6FB1387ACDDE7962207CAC4C977FC8162F3CE37912B47ABA4F1736BE174356360E477DE8366F34A6E76B27B5B55BF7DA1F498E1C56F9593E537AD2F66034AE20675EFAACC60B0B6F38E37E7970BB745E8BF0F993BB25C1365761BD9BE38A3F5C3ADA48EB4C751D7E2FF375537FCD2E6F74F76486CE7F77393E53EF4C8D1CFEDB2E7F3C5EF6C41956B5228C4E6947B115719CAC0307699E34B0F80F81C8BC874BE0C43FF23F1C9E4C85
	5A856748E531A8D87EDD2678DD8B3F1B0355E51669D9B82E7649553C0D7C25994BFFD730CEBB8B38293A71BDB92ECCF72DA456700C0152B28D620BD244992F781D2278BD8FFEAF5415FD4ED1F5DAEA39ECDB9F262578DC6F3414832E6EDC3D78769ED326E52D403FF7DCADE7AD47BF27412F67789BA5E5BC6BC4BC06ACB5FD9D47AF4FD663B7F07C1F3215712C97715CCE4FBF49742BEA987E14DC8D1E6F4B5C11270C27CE4473EA1E06FEB543BF3CC803DF43709765AB5B19D5A22FFFE451EBBBDA390D3B616951
	581B35912750353E20F335ED4759949FFA2E366DF8EB945B0EF0884BBDCF1C09ACD026EF03C85F21E5AA3BEEDD454BFBFC1E6F7A67E7F97A754F4F73FC57BFA9F9016B1F59F93E6B1F39F9016B1F97737C57BFA51F228D5ABC0EB6D25772BD075BC8BD2D371F2A576530DA4969F6896B2A07ED38E6E4BE8F6B2ACB6D446661709E8B10F49E56D5C18EFF7D05F9672973FB61F9584E2F4F77ED7BAF7275ED5F146F5B76BB72835B3EA35F376D8F6487363DB1DF5B76717D4FAA1B13724066F0CBCDFAFB530A4FCFAE
	ACBF9F67F0EF99C0DBB71C0F36FEEF10585A836F4184F73E38035C6A4F56F167FB57F1CC3FDBBEFBCDD1AED655A05ED621422FC27C2715AA7CEA4427D72A6BA73CF76B271F2B71EBB8FEF439267E286567D42BB478357CDC6A8AE53D32DA687FB395DE2E7FCE29D66257887C5A151A7857317C68306965C79258ED2E480F4745FA72C2119FB306C8FC35A149052DG46904C0F9BDDFA79B19E4272A1A444D7FE6454A8752E95656ED5AB4A0E88C1594F53F217866F19GC597206C7BF5779E17DF40E45641337582
	3D366DE7B59A5B54B17C34D5CA1DC81B476BF45D725D4D637640B3C8D7CE6BEA158F2DC1FC7E9AE5D911736C97EBB4FA2D67F366D4FE18C6C6B4FF1F491F290AF0965F95356A78D673B6EDE02D864F5B345AF59A799B98BEEF3D32CD116FF93BFB3DAAFD3508BFDC27422FC57C979BD478F508DF20462F97E536DE052F93E5F6238A3F8171358D1AF455707938C13D7C1B4F6DA5DD18DC3874C23D7C4B5E241433DA5499F7EF524861FD0B330E10BB87FB9CAEDA29CE3B1078EE7891ACEF95705E88E08518F2A1AB
	F75C3E03044FEA98F58CFB102FF490795261FD81213FA8393076A2A55FD4AB294B1DAED31F4965B2995BADE3247D1AFA91DD6323652895FEF7DE046764EEBECC6A28BE1741864D9E9D64F70592DA0358BF1CG34E9G8B81CAA12CA194E51DEAA63C40E8318D5B28A4A667F2BA2E0EG3AE8C19B7EB1318F3BAD061D1DE37863EF3FA67064FC154837F5CB84DE3E201005B59449F13113C7FCCCDAC052AE7AGFC8E00657727835A7CCE8B1573B7A35457787DC3FA8FCA6E452C2F15AF704BA93ED4016777185079
	8372DE8BE153AE46B94125E3B8CF3870E22C2BFEC73AA7DC3FD7943F475D0146867F8CB099977F9BE4C4891B37DE427B3A1748F676406F20CB747A3D3FE7633FCB18EDDF9778BFD3FC1C824FECFE146249FB9A04B5DE0AB6FF080EADA360BDCE600A29EFA6417BDC017B166296407B52E9080B20388AF8EF9438BF18B18FBB846EC8AF15816F718277BBCA17826F998237308FF9F3AFC35CB8256B00F72B402DB3224ED384EE4C08BA078A5CBDC654B9D26032CD28F31C40FDE5C239C982771091F5AE3B9CF1D37B
	D067DA015BAF66361B85EEDD9F6ABCCD60E269BE45A8F80F9338FF8CA0EF02405DCC6FDDAA39A239306A8AAC83E70E4AFE9F22787D5E6849774F557F8699337C1E05EA3C42D73D733A0378F447954C57F3847EED0AB7AA702C8CDCC97BC3742CB90425DE01B63F5A017950900638F8A741F5437BF898766FBACCB88F9392A64ECB316F1840FBBA4002B04C335B3D6AD2A594DF69ABCD67527EFB79155AB4DDCA7135D7EA537461A026298942A22FC47DBF32E21A0A2EC27D03ED28FF4DD5287FE27A6D93B33CCF81
	18F6956A7FAB2F3EDB74D5324EB32F72257F67AE92DF6AD55A33146BA9BE73AA6DD94AB85ABFA66F45D743987FEA54FFA65DC7138A6F7384EE199B5314F9B56A196B65BBCD6132EE1D613E747C167A08B5DCDBFF76D3FCF038367E7C3D9D6B4FA888AB7E916A74148D757488DC3D9D751CA2F00B9DD82E1384AE56013CF3856EBDA772E64871591037719A447D0972F6403BD9603EF0E2FC6382B7DD4497A4F0E5CCE7F8DFA0F087681AE651F4683FCC97E5E090F9AB846EFEDA0F0D40FB70F4BCC31FA06EF48A87
	DC6AF46CB77DB0C26E0F575C2756E171D8CD46AD34333745E4307371CB71B80EDF0AA350B7CF277D07E6F86F869809407B54C6BE457B5422FEF20D54BDDF7FDE3C740E2359A191AC7FE378131C872D35617DA8CD03B89714816195BFC63DD7F52B4EDB119B9F6A6B3FF6D90CDB8C7D2D3437494B93358F616F7EB16AFCA70DF39A3CC783A4G247F18FCA3AC12DCF34F7BE6DF76174153EEBADCA6AE14E802EE1D1DFCB427223626262AEC755A1ACA165670434A332FF52F1D2AFBFFB601721F70719BFFAEFC7B72
	AC2667197D28279342A2FFC2F037296E88A3FA3D1A45561ED978EFD561E45E688EC9997E1E6AFEAE226B66BCE5788ED5B85179EB4E4F527568B24C43050FC6CB565B267A7CDED986CF4F52EB7958F86EE5D9D3970C65B03B9E5BA15B7586FFF6DDCD6C3A2A2626127971A65A7636C21C5D5722EF4789FF1FFAAD56AF4B288F2640FB86C0C1247846894513BBF349BD51E582FF3B59DFFB194153C0627157AE26DC6BEFADBA492F0CBC852DBF99B9BE70A311346FBE569400975BA15A76GCFA7405BDC3EB5D21EF7
	E536FD005ABB9E70E911E86F7BE8BFA9835EF32FC3DC2368BFD6885CC72C4E0177106B504EAFD37F0C0077B800940079G5275987EAA8DDFFABD3B3F195C25CC6EE3A5F72D3270533C56C9C8BC78BBC271FB7EF55EF9112203C310F588731221DC7FD75ADFEFDF013F7CFBDD787C6CFEB2B6F49AACF6CF0725F7500D6358BF75635CE06D75B83E7C4C2C3CAB0051FDE1463A21C950B969DE8B1BE14061A2F136BB8D3DB070E574E7D1FA124F9D027EAEBA46F55B8D4E8E87D302517E520CBA98011679D51E857D4A
	F33D7E9C17920200A66CFA592F222F177D0A493E30176AEA316B68FAC3AF4A0F933AE6D9FC0F7D7FCE7DAC91E8D3053EEF75233E39B7F86B1B2551376486353ED5B7E8753D4847FC408D54D6F5C05F8440F0870D283FD36026D35C43GD3846E37C64C139001DBEEC25E709BB0AD5F1BB0AD51BAE94911F03E1F5F2B23C9CB2AA2ADCC46CBC23F39C26E0D34CCE7423B9405F26D4206D5D17AB6D44AED0CD24B6D08524ABDC8E5F4835E9A05797A983DF315DCE4435A059F3512224864578E782B4B30BDC5791D0A
	3A0C6027C5E973B0E10414CD131D4C379839CE4F0CE0B90A0842FA6BF99B56E531D1D8A73C69A0FA965C086F6F53F7F217B1FB1FEEBF51719609EB2234CCC65C894B7055D668811B9B1BA1764A1703FEF5G1DGA3G53EE44BCFB52A95FF784CDB4B9BCA572B84309760C95367A5604FD7DCC1E66021B643417426F1A1B7466CA6652B1DA53CDAC9F73847EA50A6FD460797E920A276D9804A55D0479B9CB0C57EBFF0ABADDB4C8722FDB3CDFC95FD3C5FEAE9BBC9963E7A2CF4E4FAFCE0A8C4CF3161FF562CC41
	B8488E810886C8824890E93FC00C450A2319FE6551F2FE54416F56E83D7CB9150EC13B2335730767D17CC834F67EF02995678FC3A0ACB39A7367E5BAA66A3C996A051B45BE43A14C33101BD14F9A975DCB886FE9G8BG0AEF4130CDB40C5CE9CF6E8B2FGFC33883BFF28D34127CC47021BA7DE8675C0167FB41501F531366C7EB3B28AA531C741292EABFFC1BE3D456B4A86B7561566DB746BEAE5DDB98EB484302E8E39C55B6FFB1F5601E1000F3E857364F2B1BECE9169BEC86FE913EECDAEAC87E8G6884F0
	5E0A61B69ACE6E5EA75F93A05FGA0F7E9060A7017D5F59A6175E70BF29E5E60070E4816EED51633579CCA99017602CE249F0E7DB8721B65C566CEB9AFF65BB1AFE2F474D43619849200A68D60C39E16F12B365DDC6F40BAF5612DB8AEFD37CD16D763C4F945B17A7272B4724AE358F733C91CF5B1DAF93DB651370C41BA3F08566BF8A79A237B44097D084E98546B78EED92FBB05DEE69D3D34BEC974A200BEB99E23ED3F575B48DECB3343E599F358BD866BE62B255FCEEEBF64FA6F96FA87893DDD036AB2F37B
	9ED93FA787D13FD89F7AA973A991E88820FE69DE7ABDE5716EDFE4D8D02779B1687333825EA7A35D26FB8EA4A01F761C86BF6F203A932674B67FE52C8642D9BD7ED37A3D64265B34FBF4E2A93E6DB66D9E1DF3ACB8AF5385E1713761596C4B5BC99BD4BEA339309120934089908290B583770664EC2D11821C4B6EF0B8DBCD07CCD645D6DAFE56D1F73D539F70330E13643B600479185D2AE97B97BFC3EB4F75941FB8C3EB4FF8B14F158AE18D3763BC5AE38714F3CB4517DB3311CFABB77CF61657D1374B73E8AC
	CFF276923FCC877C425B45F71DDB48B7A07799863CF7CD83324D65F01ADC1ED10625DF726276886EBFD1FB30F45C170FF3C9B1F7DEAF451E5164737B8A25313C5E674F0A67AE23F7CC33345876917F32C2D890C09440CC00D4000CD8CCDB6D76AA66DFC47D4D9E0F2157CC3F10A1B616DBCC07E5B4D9ACE6E53AE340FB2DFAD01C414F3D032DD5174233668EF6F7B72BDBFEA96A1666BBFC57AD797C5985B44EBB643AE56A9D5A3A65038E52676637E830C5E691D6EE276B4CF8131C585393CC6F115C5EEA68B1D9
	59ED2EECCF4CA79E651E18902EF78CBC936EE06D9029E78BA47C177125B78E5B3DD25234727C1CD30479D9FA43F4296949266CCFFC64E7BA37C94D1D32DF168F1565B03DFFB560FB2C7F8A6D4B1B014FF3A7567129FD5EF974D29F26B17CCE16C73170CC3C131DC3E1F9345F0CF9B4774E406D538220A93A0B6591F9AF3BCB1BC7F5967DB902B65A86D481FD5DDDC2E73ABF0B650DF2EF4039B9A48E7906B05CEF55F2D40DDF0DFBCC561338798DE9FCDF461FF7AAF07CBB88352AF9DD1267448EC621CF11193613
	2EE1936FEB1AC93AEAC45A424D59701477117DCE5033396AF596DC8798396BC4FD688C7269D79E6F7D3DC46EAA91F65DF7F2584BFD24FD73B97FDED5363A269277D5B57523BE51BE74E1716F6E127570FCAF4BDABAA1D90C7610D1ED3BBB0DB954D6B5BC7CFF0DEA5BEDB129EDF71E89F51D67D757CBFF1075DBE4D25B69FF8F4AE12D7D9337535542CEE5F17EE4FFE4106D5454A34B5AB1A1D90C2F3FD7667B2F91A2334FE0F513324D4A63C29B16474EB85FF5A676E72DC0B3A5CEAE0F21F15A72B8EDBD5DE723
	3AE7574D531DA67A5461F13836721AD33B36723224DC9BE9F0EA57D632D461F627575A0AEAEDA65C29DDDBC1FE26EF7E9079560F7222E0156A34B3D45D5E32736B56ED875B3735EE5B5A5132EBA79F2FB9CDB8CF1F9A076335C7FBF01EFE3E0C3360982E78EE44758D60BCFD775DE27F8E1D577440FB08407D0962A26FE65F9D607BF97C2EE9A8014465ABAC44C7985F4FB3E1997E663BFC1D61CB3B1B4E798D147961CD607A31FCFA35ECB17A15627B324C2FFE3022DF155F03FE95EC756FD777D87D7BD506553F
	DFFDEC7365D7ACFFA36938288B74993987733CE79067DC0285EE0E58FB90ADF07914CE3AB7393064DE317F1B76D72B603D9920DB60C3A91E5C57B482B8F297B5437F5D6AAB7F882F96D72503D3C2733DFAE323B5BEE578BB57B9D1DFE179BE2A33EF716FCE6CE7CC3B1775B32261B913271F795629C37EFCAB6DDE25EF5DD6C472DFF90BC9BB7FAE858B3F4CDD2C897FEA58EAE5E137F753BE826DD3B0AE065F3DCE6EEB90EA7C766F87835ABE8151B963DE7C8E4173963D705CF85C17E2F3E35BD1922F57F6E03D
	FF36D1927A2F36F95B30C56C51EE0BE7B6B443F3BC1EFF63015A70E63BBF9B0646ABED785E37EFEBED2838AB1E51FCC4773B737E74E623917191750AFE769E7A098541739ABD9B23B8BD417797BFDF770E302F76FBA962BB4CC3DADB1234C58ADB1EEAC7DB467930256F3679C613EC5F1D8EE53B4A62B6B8B16ECC5D38B11E24BEB91EB11BB29E062BF72A7BAD3F9771D65D0773BF2F744BF118ED64AC2F32BDEBA7FF37C2AB6CB140582BFA6B361D9D5B7688BD8B44DEF863FD7E7C699D0BACA3FFC029A7BB1798
	7F9649C30B5DEA311B48ADF8DF1BC83E337A2C0A6ED7890378236F43BA6E8D5AA626407B3C7B304E3AED0048E03F6FD17C3E40F2727755DDFFD2E530717074F65FE7F12EEF57BF0B935D2EFF4EF9C63B7FB36977347BBE137EE0BB49BF47E13B62DE42DA2BEC6FCD0A5F8F1D147592356D1FB929B258FC458354FFD97A77F6AC13FC4C39EC6E71EC073243BEBD53E16897BC85FB97AB6D4D26E2F858123D789DC37A9D9F7AE950F6A03477B359C9344E4F84BFAF3EBF3930962099206BFE1CA73908FE87075D0228
	1DA359E66CB7E161C5F90776797623237B5E117D08F0751A947DA5C6F346BED28F77B8203A351178919F3C0F6407476124C8063B00FE0B51E569B7FB98A947DF3A0F14DF2B290F2279B7A987F06EEC845296FDBF5633CF8FE07FEC5E7D38772B104E3315BDG63F300368710B6D754C76687CEF4CC9BEA13A471FD59BE9F5F1725AD8613F540C362A06DF778832857FF7551BD19701E81D0B09367D3CF256B8BE5705EG508DB082908C9081908B1088108E30G2078416442F200BA00D600AEGA7405487B14F8D
	1D153EFC9010FC839D4DF897592FA2527803D312309E1E51D7A6795F4385FF7877CD5E6D473C1986BAA4BD0876BEA8763B95BC04B6B81752B642FBA70073A1248D92779B84BDB4513C41F66E14A1B91F22DDF29A3E73B716DD8BC9D8AB0F1B87057C7807304D5828BA8B3BG70C5BF735F17E5FCAD2A7953B260291D905FC5A6A5DFB370F4CD08AFC2A54F89BCD3837031B42F90FD14E9BFBBD11B7FD2F186E40E5F7323FA406651AE23E35CA4101D86407A0E19BD58F7ACC8C8AEAC83E88668CAE0F74D30F905E7
	653DCB897A7BE71473FCC1C09316A04FABC4A7E857474E27F5C89C609392F07EEC4681B657C0CA3476CC4F5BC51AFEA37FFE4FE7DE7DEB22F32608B31B0EB7361A48F7E5F00E7530C9B947DA7AE1E3B60E973569AAF818253576E1F90ED57601DE3F67CA750069F42F4D775CECAA9DE71AC11E67E14C1F90F12EAB7CE116BF3170CCFC185D834472E701D54C43BE2C3FFE291A07A53C0FA866E19F711AAB9F541F07DDC74732D5C0DF77885A386583BA1E22672D64C9A296F65530BCFF4E4EE2B17CCA4F6BB28F9D
	E7317C7C13CD1B1FA40D46C7F03F759627E813G977D8856E5E70BBDB3739E41FAAF165216BD8A3A8234BD0A7ADEBDC46C518E23A7330465F26F00E8279E45B8CB85AE7CD12C1FDCEEE51E2782FE7E237E4BBD53FB4806FACB0920CFA24A38C32C37BA93D16F5FD25AC8F80F87C8CDC43DE759153E4C6B7BA154B1A3D11C1931ABF52C78B934F1BF1FC81D5667D47235810FF1C2FC99AA3E9160891E905F9B2A72938EBCB18178189D360EFB1F190C9D976791A10EF40022D9D0D6E6A947346596E2A78F5904AFE1
	5B35F118E4893FA3E640F7BF6C1901407D30F954DFCC50FFA25FDF656B01F46D4B515767B6097E5E9F870A852E455D66F0DBC801E1B6394C6F59995F363CF612FC69A74C4756DB0F527EDE5DAC6DBA6C99945FB4CB3B8E3BC42C4336C1D84CAC7455C2158F14BC16DCD87558C4FC275B03FC0D8A7A8E7E5B8C4FF10090C59853EF007605A29E536A6D2678684734FAAF6CC33D63A02CAC897BFB1758FD7B4A1576403E72933BFE1FBD4A6E5DE73F592E5FE73F5D2E6C33B3DC1C1D78F8BF74B7BB855DFD82772D40
	BDC463A4387D96A32D8799BE116AC5704DA6A2907149D42F43945749F1CFAAF05FF2DC9A2D23A049E6078BF96758491CC71F45EAE56F4FD39A0B9D5CDF5863F7D4C70B5320507BA5017BD6647BBFCEF81D1C71AD1FA41F71047918BFE551FD14D31334FE769A4507A4E97D6CA2511F8F03B06997D8BE4EB5AB659B81BF720B09148F27D9CF6FE04E9B2E93871B4759C367F1E2FE41746367BC595EF0952E3B5F1B4E546FCDF73D491B6E27A6EF3A3BF4620BD745474FE42A5A36F99036B000BD78B86B907C646DC0
	FCCF19B451F156B5E64C1762E46897A763B9295FD42B3EC5474EC93113DD6C1894EF1FFFA74E98CCC946763D046E71090577A400F9495897F8CB74B724DF1E685860214312C86B8BA3F2DA5B4D939F0F7DA03EA7D277CB9CA74F9AA536F242FBB0C0B4C082C09AC0A6C0C18A5409A9D8A73E6B2910FC0F0119F164A1B053755091D9577D03934F17D8511EB400FCCF8A5AD092EBC7D1A9E8EF36E7E8AE3CAF84D87AB852D65A445C6B63FA76DE60475E170D4B760EBBAA27616541095B3B0A565F74EE3347313CB7
	BBA7D7CF6D1BA45F79A74C476A31AAFAF6B56CF1ED7D361662A39F5756EF7708FEFB8C317F93185E6FF47B9F63CF7073EECFF817ED467711AEDF84270F5561E3F54AD9EE6FFA66BC37F7BDF3B860B7AA7575BAFD12FC771CB09F337781FAB6B77189EDBE98A9BE7589EDBE3CA846A1F3A12C79C9F1CF79101E7C28A7199D631F74E54F2B6C5E761CEE772E5F0FBA3D69CEF5FA53658FF953958DF95315DB3D69AA2DCABA165E74F1BC1F1B82E9A8CA457A7B12BD62DEAF79FCEE2B45EED21C4E45F2DAF1947768B6
	25E21DE2256D42B83C0782C4250A33F503124853E0456F377F8D736E55A7D586BFBF2DE377E5D66F7CB9CC67E2E702DD52D3F10D689A5A1EE742FB6ED3E2EDDC1CF9AC9638996C0C933C0F885CC594978C6FD1CF212D1D7D32FDC7953F33835E83794F5B7A7DFF030CFC331C26E2456F4B7E8D32EFB929B214F72828FD69BA3337AF1DC9F171106FC9429F668B3FC9FB8A4B3461D3D2A6793A8D9D5670EF220E3CA163F1D84363EE51293FF65A3D5B03C85D7EF4E0BB4E18A45FCBA74C476A7DE05A372C7A1536BD
	3818626BFE25ED8FBE907B4D1BA0AC72D7D836CAD46DEB5153504FFBFAA2630EDE5D3BACEBB9EFB3FFF6A962E2FD4727C6FD0F2753C7830F27DF98559FCF3FB46ABD1E4E9C559FCF7FE554FBBC7DAA0F57A5AE0DE6783CB162D3EC8F46B7863B21DF3176FA4FA47744A7CE12AF6F04672119CFDCC37B454E27353EF283450FBF2D7515DAAB7ACA90044DFF9AFD25DA55F7E9CBCBAEB426CD44D7D65975749E613C417C998E4F18B465D92455F46D092F97A9FE4FF41E7CFAF176C915416A1FA1BA8ECC00B42726E1
	FD17AC4EDF4E9538BAC1D7BB9BF117893A465918BF34AB43759FD07CFE45F6726DD5F44267EDCF5CDEC706508EA1428E69626CE3183043EF3C3E6B2C857DB3C3198179744F8C2DD0FD43F97E6C4067A4D979CB27FDCB69575AF2798245977DDADBAEDDE20E22844246FF0D6B8567F9FC2FB3DDCB7BE769C0BBDF505F512FBE67754CB12286DBDF7A406C6F9C39F61F3F729CB95D5B760CF6FD6967FDF4FD49B022D9DFFA1E76734A013E6699546B7ABE7FE756E67661183FCB70DCE5D62705FC31925312E319C8DA
	703CDF18629E86BA0F23C88B4B678B69DE94767B8A456F72FF43BE27CDA7D586ABCBAB873C7BC835835E7D34B18B7653021F41F2783458ABBDCD6016D25E1970BE7799AC1B110ABDCAB7ABFE577B5C137A2F33E17BC91541523C4D66ED436DB6EF9BB6533EA73947EA013057F5624CF0EEBA621E25389AF86F90B8F646558C6FC169D88EAE9CD21783FA6401170397445D90E1690157742301E6E63A62CE0CF4399C30380332653823C55CF3FD442D04851A380B66E8633EA1C70E7B3F07B06E12B93E754672BBD3
	93F715A2EEE637A6BA3E2F83FC5B9C51D6A846DBAFA9FE0FCCF89F7D647D710213AA0359F396F18BDE97DF5ACF66B9FD4CD92E22F7C72D2D64FCAF5369E976FBE93E9CC7D05FC462E034CF77113C70350F1545CD0FE4715F5F51ED9BEC8F739FADE5FE52E61C23DC6FDDBE603F5D58A676310D4C413D37C7DF13E5457B5DAF9D291275E73A66A74BA2074CF0E5975B5C25623CCC5C9CAC1FBB9D7EF5B48B1E79F3F05E7A6F03B8EFDD7A6C096E298BA74DAEFEDBC32CB7F478080771BCD0A473D408EFFC86BF0BB6
	7B4D9B329F9CB8CAB20B6D9A3612A5C01E8EB7ADE3B301A769D9CC7B923A6FF82743C5579F992EEBE0227B1D997DEC3BF79C4B9DA7964771895333727921EA1C16816949FDEEA2631D5F284654A540D3B5A13E952A7B529B0127A3G9F2F6B5F1473E50F79AD1EC6DAB6E966D47710CD2F7C5E56C16F76E96320F77B74BFBAFD01EC9D3AA617B7DD0BCBC977AEABB7BBC9839575221467F6C9FF732EBBB61F775413D1871B16E6CB59AD52A2B31D3CE911728E13591D9132F772AFD0BAC25CC3926D2BCA6414C402
	C7AAEA7798DC1EB2F64657A365519DC69EA9E7335168119631A389048AB7ABD538ABBDD2AEB95781DAAC91DA3450A56A74C3EB6AF31E1B1E13AD65028AEC0A21C54AA5738420090BE91463F1B829BE12A49F2F1328DE24C064AAC1E8CB36BAD57CFF21192816A7931B278421E78201GE9A11FB8D82D22D902B49E69FDF2705C52B7DAE6B4F88CE59EC7D90F294CC49D41E41C2414936270C80B195F153173627500A027134BACE45F56A1039590927BAF98097AC6D566E0867922D1A96011DE9393E290FBBE5BE8
	D77FAF48AB1734D8DDE7E47D785D530A4FDFB31CAD95FCA5AD6AB3D81DE6831482FA31DE2B74FA8FAD8EE419576A110ADB896FCA60DDC9F9B736CA593DEE48A1995FCB70AC14DCDFE78212C22341B5C8E3A0AA559E108A7BDC7DBDD400C74ABE70558190CB5FC8703AD669B50F196C28A7DDDC0F742E9CF59BD9E466819BD951FC957EBB69BF897702D69ED75E83316074DE1FF70C064EE3F5EE64BC6DBD90BB7710FAEDAE602597F0AD7C2A5DABA45FE7925AED862B95DA7B414DF6A3B91F30E554E3124FFA346F
	A1713B11068732306F76903D8794BC7F8FD0CB878886A8D840A7B8GG9842GGD0CB818294G94G88G88GCFFBB0B686A8D840A7B8GG9842GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE1B8GGGG
**end of data**/
}
}