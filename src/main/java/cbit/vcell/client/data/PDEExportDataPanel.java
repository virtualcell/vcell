/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.LocalVCDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DefaultListModelCivilized;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.LineBorderBean;
import org.vcell.util.gui.NoEventRadioButton;
import org.vcell.util.gui.PropertyChangeButtonGroup;
import org.vcell.util.gui.TitledBorderBean;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DataViewerManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.ExportSpecs.SimNameSimDataID;
import cbit.vcell.export.server.FormatSpecificSpecs;
import cbit.vcell.export.server.GeometrySpecs;
import cbit.vcell.export.server.ImageSpecs;
import cbit.vcell.export.server.MovieSpecs;
import cbit.vcell.export.server.PLYSpecs;
import cbit.vcell.export.server.RasterSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.VariableType;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.ClientPDEDataContext;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataInfoProvider;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solvers.CartesianMesh;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class PDEExportDataPanel extends JPanel implements ExportConstants {
	private JRadioButton bothVarRadioButton;
	private JLabel defineExportDataLabel;
	private JLabel selectVariablesLabel;
	private JRadioButton membVarRadioButton;
	private JRadioButton volVarRadioButton;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private JButton ivjJButtonExport = null;
	private JComboBox<ExportFormat> ivjJComboBox1 = null;
	private JLabel ivjJLabelFormat = null;
	private JPanel ivjJPanelExport = null;
	private ExportSettings ivjExportSettings1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JScrollPane ivjJScrollPane1 = null;
	private JSlider ivjJSlider1 = null;
	private JSlider ivjJSlider2 = null;
	private JTextField ivjJTextField1 = null;
	private JTextField ivjJTextField2 = null;
	private PDEDataContext fieldPdeDataContext = null;
	private JList<Object> ivjJListSelections = null;
	private JList<?> ivjJListVariables = null;
	private JRadioButton ivjJRadioButtonSelection = null;
	private JRadioButton ivjJRadioButtonSlice = null;
	private JScrollPane ivjJScrollPane2 = null;
	private DefaultListModelCivilized ivjDefaultListModelCivilizedSelections = null;
	private DefaultListModelCivilized ivjDefaultListModelCivilizedVariables = null;
//	private cbit.vcell.simdata.gui.SpatialSelection fieldSelectedRegion = null;
	private int fieldSlice = -1;
	private int fieldNormalAxis = -1;
	private PropertyChangeButtonGroup ivjButtonGroupCivilized1 = null;
	private JPanel ivjJPanelRegion = null;
	private JPanel ivjJPanelTime = null;
	private JPanel ivjJPanelVariables = null;
	private JLabel ivjJLabelRegion = null;
	private JLabel ivjJLabelTime = null;
	private JPanel ivjJPanelSelections = null;
	private JPanel ivjJPanelSlice = null;
	private PDEDataContext ivjpdeDataContext1 = null;
	private DisplayAdapterService fieldDisplayAdapterService = null;
	private JRadioButton ivjJRadioButtonFull = null;
	private JLabel ivjJLabelFull = null;
//	private JLabel ivjJLabelCurrentSelection = null;
	private JLabel ivjJLabelSlice = null;
	private boolean ivjConnPtoP3Aligning = false;
	private DataViewerManager fieldDataViewerManager = null;
	private ButtonGroup exportVarButtonGroup = new ButtonGroup();
	private static final String PROP_SELECTEDREGION = "selectedRegion";
	private SpatialSelection[] spatialSelectionsVolume = null;
	private SpatialSelection[] spatialSelectionsMembrane = null;
	private int viewZoom;
	
	private boolean isSmoldyn = false;
	
	/*
	private static final String EXPORT_QT_MOVIE = "QuickTime movie files (*.mov)";
	private static final String EXPORT_GIF_IMAGES = "GIF89a image files (*.gif)";
	private static final String EXPORT_JPEG_IMAGES = "JPEG image files (*.jpg)";
	private static final String EXPORT_GIF_ANIM = "Animated GIF files (*.gif)";
	private static final String EXPORT_NRRD = "Nearly raw raster data (*.nrrd)";
	private static final String EXPORT_UCD = "UCD (*.ucd)";
	private static final String EXPORT_VTK_IMAGE = "VTK Image (*.vtk)";
	private static final String EXPORT_VTK_UNSTRUCT = "VTK Unstructured (*.vtu)";
	*/
	/**
	 * base text of Export Button ... dynamically updated depending on selection
	 */
	private static final String BTN_EXPORT_TEXT = "Start Export";



class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.ListDataListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PDEExportDataPanel.this.getJTextField1()) 
				connEtoC5(e);
			if (e.getSource() == PDEExportDataPanel.this.getJTextField2()) 
				connEtoC6(e);
			if (e.getSource() == PDEExportDataPanel.this.getJButtonExport()) 
				connEtoM1(e);
			if (e.getSource() == PDEExportDataPanel.this.getJButtonExport()) 
				connEtoC9(e);
//			if (e.getSource() == NewPDEExportPanel.this.getJButtonAdd()) 
//				connEtoM4(e);
//			if (e.getSource() == NewPDEExportPanel.this.getJButtonRemove()) 
//				connEtoM5(e);
			if (e.getSource() == PDEExportDataPanel.this.getVolVarRadioButton()){
				vol_memb_both_change();
			}
			if (e.getSource() == PDEExportDataPanel.this.getMembVarRadioButton()){
				vol_memb_both_change();
			}
			if (e.getSource() == PDEExportDataPanel.this.getBothVarRadioButton()){
				vol_memb_both_change();
			}

		};
		public void contentsChanged(javax.swing.event.ListDataEvent e) {
			if (e.getSource() == PDEExportDataPanel.this.getDefaultListModelCivilizedSelections()) 
				connEtoC17();
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == PDEExportDataPanel.this.getJTextField1()) 
				connEtoC7(e);
			if (e.getSource() == PDEExportDataPanel.this.getJTextField2()) 
				connEtoC8(e);
		};
		public void intervalAdded(javax.swing.event.ListDataEvent e) {
			if (e.getSource() == PDEExportDataPanel.this.getDefaultListModelCivilizedSelections()) 
				connEtoC17();
		};
		public void intervalRemoved(javax.swing.event.ListDataEvent e) {
			if (e.getSource() == PDEExportDataPanel.this.getDefaultListModelCivilizedSelections()) 
				connEtoC17();
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == PDEExportDataPanel.this.getFormatComboBox()){
				connEtoM6(e);
				updateChoiceVariableType(getPdeDataContext());
				updateChoiceROI();
				updateInterface();
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
//			if (evt.getSource() == NewPDEExportPanel.this && (evt.getPropertyName().equals(PROP_SELECTEDREGION))) 
//				connEtoC10(evt);
			if (evt.getSource() == PDEExportDataPanel.this && (evt.getPropertyName().equals("slice"))) 
				connEtoC11(evt);
			if (evt.getSource() == PDEExportDataPanel.this && (evt.getPropertyName().equals("normalAxis"))) 
				connEtoC12(evt);
			if (evt.getSource() == PDEExportDataPanel.this.getExportSettings1() && (evt.getPropertyName().equals("selectedFormat"))) 
				connEtoC13(evt);
			if (evt.getSource() == PDEExportDataPanel.this && (evt.getPropertyName().equals(PDEDataContext.PROP_PDE_DATA_CONTEXT))) 
				connPtoP3SetTarget();
			if (evt.getSource() == PDEExportDataPanel.this.getpdeDataContext1() && (evt.getPropertyName().equals("timePoints"))) 
				connEtoC14(evt);
			if (evt.getSource() == PDEExportDataPanel.this.getButtonGroupCivilized1() && (evt.getPropertyName().equals("selection"))) 
				connEtoC16(evt);
			if (evt.getSource() == PDEExportDataPanel.this && (evt.getPropertyName().equals(PROP_SELECTEDREGION))) 
				connEtoC18(evt);
			if (evt.getSource() == PDEExportDataPanel.this.getpdeDataContext1() && (evt.getPropertyName().equals(SimDataConstants.PROPERTY_NAME_DATAIDENTIFIERS))) 
				connEtoC20(evt);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == PDEExportDataPanel.this.getJSlider1()) 
				connEtoC3(e);
			if (e.getSource() == PDEExportDataPanel.this.getJSlider2()) 
				connEtoC4(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == PDEExportDataPanel.this.getJListVariables()) 
				connEtoC15(e);
			if (e.getSource() == PDEExportDataPanel.this.getROISelections()) 
				connEtoC19(e);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PDEExportDataPanel() {
	super();
	initialize();
}

private void vol_memb_both_change(){
	updateChoiceVariableType(getPdeDataContext());
	updateChoiceROI();
	updateInterface();

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
private void connEtoC1(PDEDataContext value) {
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

///**
// * connEtoC10:  (NewPDEExportPanel.selectedRegion --> NewPDEExportPanel.updateCurrentSelection(Lcbit.vcell.simdata.gui.SpatialSelection;)V)
// * @param arg1 java.beans.PropertyChangeEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoC10(java.beans.PropertyChangeEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		this.updateCurrentSelection(this.getSelectedRegion());
//		// user code begin {2}
//		// user code end
//	} catch (java.lang.Throwable ivjExc) {
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}


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
		this.setFormatChoices_0();
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
		this.setTimeFromSlider(getJSlider1(), getJTextField1());
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
		this.setTimeFromSlider(getJSlider2(), getJTextField2());
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
		getButtonGroupCivilized1().add(getJRadioButtonROI());
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
private void updateChoiceROI() {
	try {
		// user code begin {1}
		// user code end
		getDefaultListModelCivilizedSelections().removeAllElements();
		if(getVolVarRadioButton().isSelected() && (getSpatialSelectionsVolume() != null && getSpatialSelectionsVolume().length != 0)){
			for (int i = 0; i < getSpatialSelectionsVolume().length; i++) {
				getDefaultListModelCivilizedSelections().addElement(getSpatialSelectionsVolume()[i]);
			}
		}
		if(getMembVarRadioButton().isSelected() && (getSpatialSelectionsMembrane() != null && getSpatialSelectionsMembrane().length != 0)){
			for (int i = 0; i < getSpatialSelectionsMembrane().length; i++) {
				getDefaultListModelCivilizedSelections().addElement(getSpatialSelectionsMembrane()[i]);
			}
		}
		
//		if(getJRadioButtonSelection().isSelected() && getDefaultListModelCivilizedSelections().getSize() == 0){
//			if(getJRadioButtonSlice().isEnabled()){
//				getJRadioButtonSlice().setSelected(true);
//			}else if(getJRadioButtonFull().isEnabled()){
//				getJRadioButtonFull().setSelected(true);
//			}
//		}
//		getJRadioButtonSelection().setEnabled(getDefaultListModelCivilizedSelections().getSize() != 0);


		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

///**
// * connEtoM5:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> DefaultListModelCivilizedSelections.removeElementAt(I)V)
// * @param arg1 java.awt.event.ActionEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoM5(java.awt.event.ActionEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		getDefaultListModelCivilizedSelections().removeElementAt(getJListSelections().getSelectedIndex());
//		// user code begin {2}
//		// user code end
//	} catch (java.lang.Throwable ivjExc) {
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}

/**
 * connEtoM6:  (JComboBox1.item.itemStateChanged(java.awt.event.ItemEvent) --> ExportSettings1.selectedFormat)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getExportSettings1().setSelectedFormat(getSelectedFormat());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * @return format selected in combobox
 */
private ExportFormat getSelectedFormat( ) {
	JComboBox<ExportFormat> cb = getFormatComboBox();
	//because getSelectedItem( ) returns object; see http://stackoverflow.com/questions/7026230/why-isnt-getselecteditem-on-jcombobox-generic
	return cb.getItemAt(cb.getSelectedIndex());
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
@SuppressWarnings("unchecked")
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
@SuppressWarnings("unchecked")
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getROISelections().setModel(getDefaultListModelCivilizedSelections());
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
//			if ((getpdeDataContext1() != null)) {
//				this.setPdeDataContext(getpdeDataContext1());
//			}
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
private PropertyChangeButtonGroup getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new PropertyChangeButtonGroup(); 
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
public DataViewerManager getDataViewerManager() {
	return fieldDataViewerManager;
}


/**
 * Return the DefaultListModelCivilized2 property value.
 * @return cbit.gui.DefaultListModelCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DefaultListModelCivilized getDefaultListModelCivilizedSelections() {
	if (ivjDefaultListModelCivilizedSelections == null) {
		try {
			ivjDefaultListModelCivilizedSelections = new DefaultListModelCivilized();
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
private DefaultListModelCivilized getDefaultListModelCivilizedVariables() {
	if (ivjDefaultListModelCivilizedVariables == null) {
		try {
			ivjDefaultListModelCivilizedVariables = new DefaultListModelCivilized();
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
public DisplayAdapterService getDisplayAdapterService() {
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
	@SuppressWarnings("deprecation")
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
	Object[] selectionsArr = getROISelections().getSelectedValuesList().toArray();
	SpatialSelection[] selections = new SpatialSelection[selectionsArr.length];
	for (int i = 0; i < selections.length; i++) {
		selections[i] = (SpatialSelection)selectionsArr[i];
	}
	GeometrySpecs geometrySpecs = new GeometrySpecs(selections, getNormalAxis(), getSlice(), geoMode);
	return new ExportSpecs(
		getPdeDataContext().getVCDataIdentifier(),
		getExportSettings1().getSelectedFormat(),
		variableSpecs,
		timeSpecs,
		geometrySpecs,
		getExportSettings1().getFormatSpecificSpecs(),
		dataInfoProvider.getSimulationModelInfo().getSimulationName(),
		dataInfoProvider.getSimulationModelInfo().getContextName()
	);
}

private ExportSpecs.SimulationSelector createSimulationSelector(){
	
	ExportSpecs.SimulationSelector simulationSelector =
		new ExportSpecs.SimulationSelector(){
			private ExportSpecs.SimNameSimDataID[] multiSimNameSimDataIDs;
//			private ExportSpecs.ExportParamScanInfo exportParamScanInfo;
			private int[] selectedParamScanIndexes;
			private Simulation[] simulations;
			public SimNameSimDataID[] getSelectedSimDataInfo() {
//				if(currentSimNameSimDataID != null){
//					boolean bFoundCurrentSimNameAndSimID = false;
//					for (int i = 0; i < multiSimNameSimDataIDs.length; i++) {
//						if(multiSimNameSimDataIDs[i].getSimulationName().equals(dataInfoProvider.getSimulationModelInfo().getSimulationName())){
//							bFoundCurrentSimNameAndSimID = true;
//							break;
//						}
//					}
//					if(!bFoundCurrentSimNameAndSimID){
//						ExportSpecs.SimNameSimDataID[] temp = new ExportSpecs.SimNameSimDataID[multiSimNameSimDataIDs.length+1];
//						System.arraycopy(multiSimNameSimDataIDs, 0, temp, 0, multiSimNameSimDataIDs.length);
//						temp[multiSimNameSimDataIDs.length] = currentSimNameSimDataID;
//						multiSimNameSimDataIDs = temp;
//					}
//				}
				if(multiSimNameSimDataIDs == null){
					return new ExportSpecs.SimNameSimDataID[] {currentSimNameSimDataID};
				}
				return multiSimNameSimDataIDs;
			}
			public void selectSimulations() {
				getNumAvailableSimulations();
				String[][] rowData = new String[simulations.length][5];
				for (int i = 0; i < rowData.length; i++) {
					rowData[i][0] = simulations[i].getName();
					rowData[i][1] = simulations[i].getMeshSpecification().getSamplingSize().toString();
					rowData[i][2] = simulations[i].getSolverTaskDescription().getExpectedNumTimePoints()+"";
					rowData[i][3] = simulations[i].getSolverTaskDescription().getTimeBounds().getEndingTime()+"";
					rowData[i][4] = simulations[i].getSolverTaskDescription().getOutputTimeSpec().getShortDescription();
				}
		
				try{
					int[] choices = DialogUtils.showComponentOKCancelTableList(
							PDEExportDataPanel.this, "Choose Sims to export together",
							new String[] {"Simulation","Mesh x,y,z","NumTimePoints","EndTime","Output Descr."},
							rowData, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					if (choices != null) {
						multiSimNameSimDataIDs = new ExportSpecs.SimNameSimDataID[choices.length];
						for (int i = 0; i < choices.length; i++) {
							multiSimNameSimDataIDs[i] =
								new ExportSpecs.SimNameSimDataID(
									simulations[choices[i]].getName(),
									simulations[choices[i]].getSimulationInfo().getAuthoritativeVCSimulationIdentifier(),
									SimResultsViewer.getParamScanInfo(simulations[choices[i]], (currentSimNameSimDataID==null?0:currentSimNameSimDataID.getDefaultJobIndex()))
								);
						}
					}
				}catch (UserCancelException uce){
					//ignore
				}
			}
			public void selectParamScanInfo(){
				String[][] rowData = new String[currentSimNameSimDataID.getExportParamScanInfo().getParamScanJobIndexes().length][currentSimNameSimDataID.getExportParamScanInfo().getParamScanConstantNames().length];
				for (int i = 0; i < rowData.length; i++) {
					for (int j = 0; j < rowData[i].length; j++) {
						rowData[i][j] = currentSimNameSimDataID.getExportParamScanInfo().getParamScanConstantValues()[i][j];
					}
				}
				try{
					int[] choices = DialogUtils.showComponentOKCancelTableList(
							PDEExportDataPanel.this, "Choose ParameterScans to export together",
							currentSimNameSimDataID.getExportParamScanInfo().getParamScanConstantNames(),
							rowData, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					if(choices != null && choices.length > 0){
						selectedParamScanIndexes = new int[choices.length];
//						String[][] selectedParamScanValues = new String[choices.length][currentSimNameSimDataID.getExportParamScanInfo().getParamScanConstantNames().length];
						for (int i = 0; i < choices.length; i++) {
							selectedParamScanIndexes[i] = choices[i];
//							for (int j = 0; j < currentSimNameSimDataID.getExportParamScanInfo().getParamScanConstantNames().length; j++) {
//								selectedParamScanValues[i][j] = currentSimNameSimDataID.getExportParamScanInfo().getParamScanConstantValues()[choices[i]][j];
//							}
						}
//						exportParamScanInfo =
//								new ExportSpecs.ExportParamScanInfo(selectedParamScanIndexes, selectedParamScanIndexes[0], currentSimNameSimDataID.getExportParamScanInfo().getParamScanConstantNames(), selectedParamScanValues);
					}else{
						selectedParamScanIndexes = null;
					}
				}catch(UserCancelException uce){
					//ignore
				}
			}
			public int[] getselectedParamScanIndexes(){
				return selectedParamScanIndexes;
			}
			public int getNumAvailableParamScans(){
				if(currentSimNameSimDataID == null || currentSimNameSimDataID.getExportParamScanInfo() == null){
					return 0;
				}
				return currentSimNameSimDataID.getExportParamScanInfo().getParamScanJobIndexes().length;
			}
			public int getNumAvailableSimulations() {
				if(simulations==null){
					VCDocument thisDocument = (getDataViewerManager() instanceof DocumentWindowManager?((DocumentWindowManager)getDataViewerManager()).getVCDocument():null);
					if(thisDocument instanceof BioModel){
						String thisSimContextName = dataInfoProvider.getSimulationModelInfo().getContextName();
						SimulationContext[] simContexts = ((BioModel)thisDocument).getSimulationContexts();
						SimulationContext thisSimulationContext = null;
						for (int i = 0; i < simContexts.length; i++) {
							if(thisSimContextName.equals(thisDocument.getName()+"::"+simContexts[i].getName())){
								thisSimulationContext = simContexts[i];
								break;
							}
						}
						simulations = thisSimulationContext.getSimulations();
					}else if(thisDocument instanceof MathModel){
						simulations = ((MathModel)thisDocument).getSimulations();
					}else{
						simulations = new Simulation[0];
					}
				}
				return simulations.length;
			}
		
	};
	return simulationSelector;
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
			ivjJButtonExport.setText(BTN_EXPORT_TEXT);
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

///**
// * Return the JButtonRemove property value.
// * @return javax.swing.JButton
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JButton getJButtonRemove() {
//	if (ivjJButtonRemove == null) {
//		try {
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjJButtonRemove;
//}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox<ExportFormat> getFormatComboBox() {
	if (ivjJComboBox1 == null) {
		try {
			ivjJComboBox1 = new javax.swing.JComboBox<>();
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


///**
// * Return the JLabelSelection property value.
// * @return javax.swing.JLabel
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JLabel getJLabelCurrentSelection() {
//	if (ivjJLabelCurrentSelection == null) {
//		try {
//			ivjJLabelCurrentSelection = new javax.swing.JLabel();
//			ivjJLabelCurrentSelection.setName("JLabelCurrentSelection");
//			ivjJLabelCurrentSelection.setText("");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjJLabelCurrentSelection;
//}

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
			ivjJLabelRegion.setText("Define Export Data Range:");
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
			ivjJLabelTime.setText("Define Export Time interval:");
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
 * Return the JListSelections property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList<Object> getROISelections() {
	if (ivjJListSelections == null) {
		try {
			ivjJListSelections = new javax.swing.JList<Object>();
			ivjJListSelections.setName("JListSelections");
			ivjJListSelections.setBounds(0, 0, 160, 120);
			ivjJListSelections.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ivjJListSelections.setCellRenderer(new DefaultListCellRenderer(){
				@Override
				public Component getListCellRendererComponent(JList<?> list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					// TODO Auto-generated method stub
//					System.out.println(value.getClass().getName()+"   "+value);
					String curveDescr = "";
					if(value instanceof SpatialSelectionMembrane){
						curveDescr = ((SpatialSelectionMembrane)value).getSelectionSource().getDescription()+" ("+((SpatialSelectionMembrane)value).getSelectionSource().getBeginningCoordinate()+")";
					}else if(value instanceof SpatialSelectionVolume){
						curveDescr = ((SpatialSelectionVolume)value).getCurveSelectionInfo().getCurve().getDescription()+" ("+((SpatialSelectionVolume)value).getCurveSelectionInfo().getCurve().getBeginningCoordinate()+")";
					}else{
						curveDescr = value.toString();
					}

					return super.getListCellRendererComponent(list, curveDescr, index, isSelected,
							cellHasFocus);
				}
			});
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
private javax.swing.JList<?> getJListVariables() {
	if (ivjJListVariables == null) {
		try {
			ivjJListVariables = new javax.swing.JList<Object>();
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
			ivjJPanelExport.setBorder(new LineBorder(Color.black, 2, false));
			ivjJPanelExport.setName("JPanelExport");
			ivjJPanelExport.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonExport = new java.awt.GridBagConstraints();
			constraintsJButtonExport.insets = new Insets(4, 4, 4, 4);
			constraintsJButtonExport.gridx = 2; constraintsJButtonExport.gridy = 0;
			getJPanelExport().add(getJButtonExport(), constraintsJButtonExport);

			java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
			constraintsJComboBox1.insets = new Insets(4, 4, 4, 4);
			constraintsJComboBox1.gridx = 1; constraintsJComboBox1.gridy = 0;
			constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBox1.weightx = 1.0;
			getJPanelExport().add(getFormatComboBox(), constraintsJComboBox1);

			java.awt.GridBagConstraints constraintsJLabelFormat = new java.awt.GridBagConstraints();
			constraintsJLabelFormat.insets = new Insets(4, 4, 4, 4);
			constraintsJLabelFormat.gridx = 0; constraintsJLabelFormat.gridy = 0;
			constraintsJLabelFormat.fill = java.awt.GridBagConstraints.HORIZONTAL;
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
			ivjJPanelRegion.setLayout(new GridBagLayout());
			ivjJPanelRegion.setBorder(new LineBorder(Color.black, 1, false));
			ivjJPanelRegion.setName("JPanelRegion");
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.fill = GridBagConstraints.BOTH;
			gridBagConstraints_1.weightx = 1;
			gridBagConstraints_1.weighty = 1;
			gridBagConstraints_1.gridx = 0;
			gridBagConstraints_1.gridy = 1;
			gridBagConstraints_1.insets = new Insets(0, 1, 0, 0);
			getJPanelRegion().add(getJPanelSelections(), gridBagConstraints_1);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
//			gridBagConstraints.ipadx = 252;
			gridBagConstraints.insets = new Insets(1, 1, 0, 0);
			getJPanelRegion().add(getJLabelRegion(), gridBagConstraints);
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints_2.weightx = 1;
			gridBagConstraints_2.gridx = 0;
			gridBagConstraints_2.gridy = 2;
//			gridBagConstraints_2.ipadx = 179;
			gridBagConstraints_2.insets = new Insets(0, 1, 0, 0);
			getJPanelRegion().add(getJPanelSlice(), gridBagConstraints_2);
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
			constraintsJRadioButtonSelection.weightx = 1;
			constraintsJRadioButtonSelection.anchor = GridBagConstraints.WEST;
			constraintsJRadioButtonSelection.gridx = 0; constraintsJRadioButtonSelection.gridy = 0;
			constraintsJRadioButtonSelection.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSelections().add(getJRadioButtonROI(), constraintsJRadioButtonSelection);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.fill = GridBagConstraints.BOTH;
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.weightx = 1;
			constraintsJScrollPane2.weighty = 1;
			constraintsJScrollPane2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSelections().add(getJScrollPane2(), constraintsJScrollPane2);

//			java.awt.GridBagConstraints constraintsJLabelCurrentSelection = new java.awt.GridBagConstraints();
//			constraintsJLabelCurrentSelection.gridx = 1; constraintsJLabelCurrentSelection.gridy = 0;
//			constraintsJLabelCurrentSelection.gridwidth = 2;
//			constraintsJLabelCurrentSelection.fill = java.awt.GridBagConstraints.HORIZONTAL;
//			constraintsJLabelCurrentSelection.insets = new java.awt.Insets(4, 4, 4, 4);
//			getJPanelSelections().add(getJLabelCurrentSelection(), constraintsJLabelCurrentSelection);
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
			constraintsJRadioButtonSlice.anchor = GridBagConstraints.WEST;
			constraintsJRadioButtonSlice.gridx = 0; constraintsJRadioButtonSlice.gridy = 0;
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
			ivjJPanelTime.setBorder(new LineBorder(Color.black, 1, false));
			ivjJPanelTime.setName("JPanelTime");
			ivjJPanelTime.setLayout(new java.awt.GridBagLayout());

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
			ivjJPanelVariables.setBorder(new LineBorder(Color.black, 1, false));
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] {0,7,7};
			gridBagLayout.rowHeights = new int[] {7,7};
			ivjJPanelVariables.setLayout(gridBagLayout);
			ivjJPanelVariables.setName("JPanelVariables");
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints_1.weighty = 1;
			gridBagConstraints_1.fill = GridBagConstraints.BOTH;
			gridBagConstraints_1.gridwidth = 3;
			gridBagConstraints_1.gridx = 0;
			gridBagConstraints_1.gridy = 3;
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.weightx = 1;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.gridwidth = 3;
			gridBagConstraints_3.gridx = 0;
			gridBagConstraints_3.gridy = 0;
			ivjJPanelVariables.add(getDefineExportDataLabel(), gridBagConstraints_3);
			ivjJPanelVariables.add(getVolVarRadioButton(), gridBagConstraints);
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.weightx = 1;
			gridBagConstraints_2.gridy = 1;
			gridBagConstraints_2.gridx = 1;
			ivjJPanelVariables.add(getMembVarRadioButton(), gridBagConstraints_2);
			getJPanelVariables().add(getJScrollPane1(), gridBagConstraints_1);
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
private javax.swing.JRadioButton getJRadioButtonROI() {
	if (ivjJRadioButtonSelection == null) {
		try {
			ivjJRadioButtonSelection = new NoEventRadioButton(); 
			ivjJRadioButtonSelection.setName("JRadioButtonSelection");
			ivjJRadioButtonSelection.setSelected(true);
			ivjJRadioButtonSelection.setText("User ROI from \"View Data\" (select 1 or more)");
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
			ivjJRadioButtonSlice = new NoEventRadioButton(); 
			ivjJRadioButtonSlice.setName("JRadioButtonSlice");
			ivjJRadioButtonSlice.setText("Current Slice from \"View Data\"");
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
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.gridwidth = 3;
			gridBagConstraints_3.gridx = 0;
			gridBagConstraints_3.gridy = 2;
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.weightx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridx = 2;
			getJPanelVariables().add(getBothVarRadioButton(), gridBagConstraints);
			ivjJPanelVariables.add(getSelectVariablesLabel(), gridBagConstraints_3);
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
			getJScrollPane2().setViewportView(getROISelections());
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
public PDEDataContext getPdeDataContext() {
	return fieldPdeDataContext;
}


/**
 * Return the pdeDataContext1 property value.
 * @return cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PDEDataContext getpdeDataContext1() {
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


///**
// * Gets the selectedRegion property (cbit.vcell.simdata.gui.SpatialSelection) value.
// * @return The selectedRegion property value.
// * @see #setSelectedRegion
// */
//public cbit.vcell.simdata.gui.SpatialSelection getSelectedRegion() {
//	return fieldSelectedRegion;
//}


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
	exportVarButtonGroup.add(getVolVarRadioButton());
	exportVarButtonGroup.add(getMembVarRadioButton());
	exportVarButtonGroup.add(getBothVarRadioButton());
	getVolVarRadioButton().addActionListener(ivjEventHandler);
	getMembVarRadioButton().addActionListener(ivjEventHandler);
	getBothVarRadioButton().addActionListener(ivjEventHandler);
	
	getFormatComboBox().addItemListener(ivjEventHandler);
	getJSlider1().addChangeListener(ivjEventHandler);
	getJSlider2().addChangeListener(ivjEventHandler);
	getJTextField1().addActionListener(ivjEventHandler);
	getJTextField2().addActionListener(ivjEventHandler);
	getJTextField1().addFocusListener(ivjEventHandler);
	getJTextField2().addFocusListener(ivjEventHandler);
	getJButtonExport().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getExportSettings1().addPropertyChangeListener(ivjEventHandler);
	getJListVariables().addListSelectionListener(ivjEventHandler);
	getButtonGroupCivilized1().addPropertyChangeListener(ivjEventHandler);
	getDefaultListModelCivilizedSelections().addListDataListener(ivjEventHandler);
	getROISelections().addListSelectionListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP1SetTarget();
	connPtoP3SetTarget();
}


private void setFormatChoices_0(/*boolean bMembrane*/){
	JComboBox<ExportFormat> cb = getFormatComboBox();
	try{
		cb.removeItemListener(ivjEventHandler);
		Object currentSelection = cb.getSelectedItem();
		cb.removeAllItems();
		cb.addItem(ExportFormat.CSV);
		cb.addItem(ExportFormat.QUICKTIME);
		cb.addItem(ExportFormat.GIF);
		cb.addItem(ExportFormat.ANIMATED_GIF);
		cb.addItem(ExportFormat.FORMAT_JPEG);
		cb.addItem(ExportFormat.NRRD);
		cb.addItem(ExportFormat.IMAGEJ);
		cb.addItem(ExportFormat.UCD);
		cb.addItem(ExportFormat.PLY);
		cb.addItem(ExportFormat.VTK_UNSTRUCT);
		if(getVolVarRadioButton().isSelected()){
			cb.addItem(ExportFormat.VTK_IMAGE);
		}
		if(currentSelection != null){
			cb.setSelectedItem(currentSelection);
		}else{
			cb.setSelectedIndex(0);
		}
	}finally{
		cb.addItemListener(ivjEventHandler);
	}

}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		LineBorderBean ivjLocalBorder1;
		ivjLocalBorder1 = new LineBorderBean();
		ivjLocalBorder1.setLineColor(java.awt.Color.blue);
		TitledBorderBean ivjLocalBorder;
		ivjLocalBorder = new TitledBorderBean();
		ivjLocalBorder.setBorder(ivjLocalBorder1);
		ivjLocalBorder.setTitle("Specify data to be exported");
		setName("PDEExportPanel");
		setBorder(ivjLocalBorder);
		setLayout(new java.awt.GridBagLayout());
		setSize(828, 352);

		java.awt.GridBagConstraints constraintsJPanelExport = new java.awt.GridBagConstraints();
		constraintsJPanelExport.fill = GridBagConstraints.HORIZONTAL;
		constraintsJPanelExport.gridx = 0; constraintsJPanelExport.gridy = 0;
		constraintsJPanelExport.gridwidth = 2;
		constraintsJPanelExport.weightx = 1.0;
		constraintsJPanelExport.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getJPanelExport(), constraintsJPanelExport);

		java.awt.GridBagConstraints constraintsJPanelTime = new java.awt.GridBagConstraints();
		constraintsJPanelTime.fill = GridBagConstraints.BOTH;
		constraintsJPanelTime.gridx = 1; constraintsJPanelTime.gridy = 2;
		constraintsJPanelTime.weightx = 1;
		constraintsJPanelTime.weighty = 0;
		constraintsJPanelTime.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelTime(), constraintsJPanelTime);

		java.awt.GridBagConstraints constraintsJPanelVariables = new java.awt.GridBagConstraints();
		constraintsJPanelVariables.fill = GridBagConstraints.BOTH;
		constraintsJPanelVariables.gridheight = 2;
		constraintsJPanelVariables.gridx = 0; constraintsJPanelVariables.gridy = 1;
		constraintsJPanelVariables.weightx = 1.0;
		constraintsJPanelVariables.weighty = 1;
		constraintsJPanelVariables.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelVariables(), constraintsJPanelVariables);

		java.awt.GridBagConstraints constraintsJPanelRegion = new java.awt.GridBagConstraints();
		constraintsJPanelRegion.gridx = 1; constraintsJPanelRegion.gridy = 1;
		constraintsJPanelRegion.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelRegion.weightx = 0;
		constraintsJPanelRegion.weighty = 1.0;
		constraintsJPanelRegion.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelRegion(), constraintsJPanelRegion);
		initConnections();
		connEtoC2();
		connEtoM2();
		connEtoM7();
		connEtoM3();
		getExportSettings1().setSelectedFormat(getSelectedFormat());
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
		PDEExportDataPanel aNewPDEExportPanel;
		aNewPDEExportPanel = new PDEExportDataPanel();
		frame.setContentPane(aNewPDEExportPanel);
		frame.setSize(aNewPDEExportPanel.getSize());
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
public void setDataViewerManager(DataViewerManager dataViewerManager) {
	DataViewerManager oldValue = fieldDataViewerManager;
	fieldDataViewerManager = dataViewerManager;
	firePropertyChange("dataViewerManager", oldValue, dataViewerManager);
}


/**
 * Sets the displayAdapterService property (cbit.image.DisplayAdapterService) value.
 * @param displayAdapterService The new value for the property.
 * @see #getDisplayAdapterService
 */
public void setDisplayAdapterService(DisplayAdapterService displayAdapterService) {
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
private ExportSpecs.SimNameSimDataID currentSimNameSimDataID;
public void setPdeDataContext(PDEDataContext pdeDataContext,ExportSpecs.SimNameSimDataID currentSimNameSimDataID) {
	//currentSimNameSimDataID 2 states
	//1.  with ExportSpecs.ExportParamScanInfo
	//2.  without ExportSpecs.ExportParamScanInfo
	this.currentSimNameSimDataID = currentSimNameSimDataID;
	PDEDataContext oldValue = fieldPdeDataContext;
	fieldPdeDataContext = pdeDataContext;
	firePropertyChange(PDEDataContext.PROP_PDE_DATA_CONTEXT, oldValue, pdeDataContext);
}


/**
 * Set the pdeDataContext1 to a new value.
 * @param newValue cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setpdeDataContext1(PDEDataContext newValue) {
	if (ivjpdeDataContext1 != newValue) {
		try {
			PDEDataContext oldValue = getpdeDataContext1();
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
			firePropertyChange(PDEDataContext.PROP_PDE_DATA_CONTEXT, oldValue, newValue);
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

///**
// * Sets the selectedRegion property (cbit.vcell.simdata.gui.SpatialSelection) value.
// * @param selectedRegion The new value for the property.
// * @see #getSelectedRegion
// */
//public void setSelectedRegion(cbit.vcell.simdata.gui.SpatialSelection selectedRegion) {
//	SpatialSelection oldValue = fieldSelectedRegion;
//	fieldSelectedRegion = selectedRegion;
//	firePropertyChange(PROP_SELECTEDREGION, oldValue, selectedRegion);
//}


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
private void setTimeFromSlider(JSlider jSlider, JTextField textField) {
	if (getPdeDataContext() != null && getPdeDataContext().getTimePoints() != null) {
		double timepoint = getPdeDataContext().getTimePoints()[jSlider.getValue()];
		textField.setText(Double.toString(timepoint));
		textField.setCaretPosition(0);
	}else{
		textField.setText("");
	}
	if(jSlider == getJSlider1() && getJSlider1().getValue() > getJSlider2().getValue()){
		getJSlider2().setValue(getJSlider1().getValue());
	}else if(jSlider == getJSlider2() && getJSlider2().getValue() < getJSlider1().getValue()){
		getJSlider1().setValue(getJSlider2().getValue());
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


private DataInfoProvider dataInfoProvider;
public void setDataInfoProvider(DataInfoProvider dataInfoProvider){
	this.dataInfoProvider = dataInfoProvider;
}
/**
 * Comment
 */
private void startExport() {
	if(getExportSettings1().getSelectedFormat() == ExportFormat.QUICKTIME &&
		getJSlider1().getValue() == getJSlider2().getValue()){
		DialogUtils.showWarningDialog(this, "User selected 'begin' and 'end' export times are the same.  'Movie' export format 'begin' and 'end' times must be different");
		return;
	}
	DisplayPreferences[] displayPreferences = null;
	@SuppressWarnings("deprecation")
	Object[] variableSelections = getJListVariables().getSelectedValues();
	boolean selectionHasVolumeVariables = false;
	boolean selectionHasMembraneVariables = false;
	switch (getExportSettings1().getSelectedFormat()) {
		case PLY:
		case QUICKTIME:
		case GIF:
		case FORMAT_JPEG:
		case ANIMATED_GIF: {
			
			displayPreferences = new DisplayPreferences[variableSelections.length];
			
			StringBuffer noScaleInfoNames = new StringBuffer();
			for (int i = 0; i < displayPreferences.length; i++) {
				BitSet domainValid = null;
				try {
					if(dataInfoProvider != null){
						DataIdentifier varSelectionDataIdnetDataIdentifier = null;
						for (int j = 0; j < dataInfoProvider.getPDEDataContext().getDataIdentifiers().length; j++) {
							if(dataInfoProvider.getPDEDataContext().getDataIdentifiers()[j].getName().equals(variableSelections[i])){
								varSelectionDataIdnetDataIdentifier = dataInfoProvider.getPDEDataContext().getDataIdentifiers()[j];
							}
						}
						if(varSelectionDataIdnetDataIdentifier != null){
							selectionHasVolumeVariables = selectionHasVolumeVariables ||
								varSelectionDataIdnetDataIdentifier.getVariableType().equals(VariableType.VOLUME) ||
								varSelectionDataIdnetDataIdentifier.getVariableType().equals(VariableType.POSTPROCESSING) ||
								varSelectionDataIdnetDataIdentifier.getVariableType().equals(VariableType.VOLUME_REGION);
							selectionHasMembraneVariables = selectionHasMembraneVariables ||
								varSelectionDataIdnetDataIdentifier.getVariableType().equals(VariableType.MEMBRANE) ||
								varSelectionDataIdnetDataIdentifier.getVariableType().equals(VariableType.MEMBRANE_REGION);
							CartesianMesh cartesianMesh = dataInfoProvider.getPDEDataContext().getCartesianMesh();
							int dataLength = cartesianMesh.getDataLength(varSelectionDataIdnetDataIdentifier.getVariableType());
							domainValid = new BitSet(dataLength);
							domainValid.clear();
							for (int j = 0; j < dataLength; j++) {
								if(dataInfoProvider.isDefined(varSelectionDataIdnetDataIdentifier,j)){
									domainValid.set(j);
								}
							}
						}else{
							throw new Exception("No DataIdentifer found for variable name '"+variableSelections[i]+"'");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					DialogUtils.showErrorDialog(this, "Error during domain evaluation:\n"+e.getMessage());
					return;
				}
				displayPreferences[i] = new DisplayPreferences(getDisplayAdapterService().getDisplayPreferences((String)variableSelections[i]), domainValid);
				if(!getDisplayAdapterService().hasStateID((String)variableSelections[i])){
					noScaleInfoNames.append("--- "+(String)variableSelections[i]+"\n");
				}
			}
			break;
		}
		
		
		case CSV: {
			// check for membrane variables... warn for 3D geometry...
			// one gets the whole nine yards by index, not generally useful... except for a few people like Boris :)
			boolean mbVars = false;
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
			if (mbVars && getPdeDataContext().getCartesianMesh().getGeometryDimension() == 3 && getJRadioButtonSlice().isSelected()) {
				String choice = PopupGenerator.showWarningDialog(this, getDataViewerManager().getUserPreferences(), UserMessage.warn_exportMembraneData3D, null);
				if (choice.equals(UserMessage.OPTION_CANCEL)) {
					// user canceled
					return;
				}
			}
			getExportSettings1().setSimulationSelector(createSimulationSelector());
			getExportSettings1().setIsCSVExport(true);
			break;
		}
	case NRRD:
	case IMAGEJ:
	case UCD:
	case VTK_IMAGE:
	case VTK_UNSTRUCT:
		break;
	default:
		break;
	};
	if (getJRadioButtonROI().isSelected() && getROISelections().getSelectedIndex() == -1) {
		PopupGenerator.showErrorDialog(this, "To export selections, you must select at least one item from the ROI selection list");
	}

	getExportSettings1().setTimeSpecs(new TimeSpecs(getJSlider1().getValue(), getJSlider2().getValue(), getPdeDataContext().getTimePoints(), ExportConstants.TIME_RANGE));
	getExportSettings1().setDisplayPreferences(displayPreferences,Arrays.asList(variableSelections).toArray(new String[0]),viewZoom);
	getExportSettings1().setSliceCount(FormatSpecificSpecs.getSliceCount(getJRadioButtonFull().isSelected(), getNormalAxis(), getPdeDataContext().getCartesianMesh()));
	getExportSettings1().setImageSizeCalculationInfo(getPdeDataContext().getCartesianMesh(),getNormalAxis());
	getExportSettings1().setIsSmoldyn(isSmoldyn);
	
	ExportFormat format = getSelectedFormat();
	if(format.equals(ExportFormat.PLY)){
		getExportSettings1().setFormatSpecificSpecs(new PLYSpecs(true,displayPreferences));
	}
	if (format.requiresFollowOn()){
		Frame theFrame = JOptionPane.getFrameForComponent(this);
		boolean okToExport = getExportSettings1().showFormatSpecificDialog(theFrame,selectionHasVolumeVariables,selectionHasMembraneVariables);
				
		if (!okToExport) {
			return;
		}
	}
	if(format.equals(ExportFormat.IMAGEJ)){
		//export nrrd for imagej direct, the we'll send to imagej from vcell client
		getExportSettings1().setFormatSpecificSpecs(new RasterSpecs(ExportConstants.NRRD_SINGLE, false));
	}
	
	// determine of sim result is from local (quick) run or on server.
	final OutputContext outputContext = ((ClientPDEDataContext)getPdeDataContext()).getDataManager().getOutputContext();
	final ExportSpecs exportSpecs = getExportSpecs();
	boolean isLocalSimResult = false;
	VCDataIdentifier vcId = exportSpecs.getVCDataIdentifier();  
	if (vcId instanceof LocalVCDataIdentifier) {
		isLocalSimResult = true;
	}

	// find out if smoldyn export choice is 'particle' - not available at this time
	boolean isParticle = false;
	if (getExportSettings1().getFormatSpecificSpecs() instanceof ImageSpecs) {
		isParticle = ((ImageSpecs)getExportSettings1().getFormatSpecificSpecs()).getParticleMode() == FormatSpecificSpecs.PARTICLE_SELECT;
	} else if (getExportSettings1().getFormatSpecificSpecs() instanceof MovieSpecs) {
		isParticle = ((MovieSpecs)getExportSettings1().getFormatSpecificSpecs()).getParticleMode() == FormatSpecificSpecs.PARTICLE_SELECT;
	} 
		
	if (isLocalSimResult && isParticle) {
		DialogUtils.showErrorDialog(this, "Particle export for Smoldyn particles unavailable in local data at this time.");
		return;
	}
	
	// pass the export request down the line; non-blocking call
	if (!isLocalSimResult) {
		// for sims that ran on server, do as before.
		getDataViewerManager().startExport(this,outputContext,exportSpecs);
	} else {
		final String SOURCE_FILE_KEY = "SOURCE_FILE_KEY";
		final String DESTINATION_FILE_KEY = "DEESTINATION_FILE_KEY";
		AsynchClientTask localExportTast = new AsynchClientTask("Start Local Export", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING)  {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				try {
					StdoutSessionLog sessionLog = new StdoutSessionLog("Local");
					File primaryDir = ResourceUtil.getLocalRootDir();
					User usr = User.tempUser;
					File usrDir = new File(primaryDir.getAbsolutePath(),usr.getName());
					System.setProperty(PropertyLoader.exportBaseDirProperty, usrDir.getAbsolutePath()+File.separator);
					System.setProperty(PropertyLoader.exportBaseURLProperty, usrDir.toURI().toURL().toString());
					DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(sessionLog,null,primaryDir,null);
					ExportServiceImpl localExportServiceImpl = new ExportServiceImpl(sessionLog);
					DataServerImpl dataServerImpl = new DataServerImpl(sessionLog, dataSetControllerImpl, localExportServiceImpl);
					ExportEvent localExportEvent = dataServerImpl.makeRemoteFile(outputContext,usr, exportSpecs);
					File sourceFile = new File(usrDir,new File((new URL(localExportEvent.getLocation()).getPath())).getName());
					hashTable.put(SOURCE_FILE_KEY, sourceFile);
				} catch (Exception e) {
					throw new Exception("Unable to export local sim results data : " + e.getMessage(),e);
				}
			}
		};
		
		AsynchClientTask localSaveTask = new AsynchClientTask("Start Local Export", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				File sourceFile = (File)hashTable.get(SOURCE_FILE_KEY);
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setSelectedFile(new File(sourceFile.getName()));
				if(jFileChooser.showSaveDialog(PDEExportDataPanel.this) == JFileChooser.APPROVE_OPTION){
					File destinationFile = jFileChooser.getSelectedFile();
					if(destinationFile.exists()){
						final String OVERWRITE = "Overwrite";
						final String CANCEL = "Cancel";
						String response = DialogUtils.showWarningDialog(PDEExportDataPanel.this, "OK to Overwrite "+destinationFile.getAbsolutePath()+"?", new String[] {OVERWRITE,CANCEL},OVERWRITE);
						if(response == null || !response.equals(OVERWRITE)){
							return;
						}
					}
					hashTable.put(DESTINATION_FILE_KEY,destinationFile);
				}
			}
		};
		
		AsynchClientTask localDeleteTempTask = new AsynchClientTask("Start Local Export", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,false,false) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				File sourceFile = (File)hashTable.get(SOURCE_FILE_KEY);
				File destinationFile = (File)hashTable.get(DESTINATION_FILE_KEY);
				if(sourceFile != null && sourceFile.exists()){
					try{
						if(destinationFile != null){
							copyFile(sourceFile, destinationFile);
						}
					}finally{
						sourceFile.delete();
					}
				}
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {localExportTast,localSaveTask,localDeleteTempTask}, false, true, null);
	}
}

private static void copyFile(File sourceFile, File destFile) throws IOException {
    if(!destFile.exists()) {
        destFile.createNewFile();
    }
	try (FileInputStream fis = new FileInputStream(sourceFile);
        FileOutputStream fos = new FileOutputStream(destFile) ) {
        FileChannel source = fis.getChannel();
		FileChannel destination = fos.getChannel();
        // to avoid infinite loops, should be:
        long count = 0;
        long size = source.size();              
        while((count += destination.transferFrom(source, count, size-count))<size);
    }
}
/**
 * Comment
 */
private void updateAllChoices(PDEDataContext pdeDataContext) {
	if (pdeDataContext != null) {
		updateChoiceVariableType(pdeDataContext);
		updateTimes(pdeDataContext.getTimePoints());
	}
}

private void updateChoiceVariableType(PDEDataContext pdeDataContext){
	if (pdeDataContext != null && pdeDataContext.getDataIdentifiers() != null &&pdeDataContext.getDataIdentifiers().length > 0) {
		TreeSet<String> dataIdentifierTreeSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

		DataIdentifier[] dataIDArr = pdeDataContext.getDataIdentifiers();
		for (int i = 0; i < dataIDArr.length; i++) {
//			String vmPrefix = (dataIDArr[i].getVariableType().equals(VariableType.VOLUME)
//								?"V"
//								:(dataIDArr[i].getVariableType().equals(VariableType.MEMBRANE)?"M":"?"));
			String varListName = dataIDArr[i].getName();//"("+vmPrefix+")  "+dataIDArr[i].getName();
			if(getBothVarRadioButton().isSelected()){
				dataIdentifierTreeSet.add(varListName);
			}else if(getVolVarRadioButton().isSelected() && (dataIDArr[i].getVariableType().equals(VariableType.VOLUME) || dataIDArr[i].getVariableType().equals(VariableType.POSTPROCESSING))){
				dataIdentifierTreeSet.add(varListName);
			}else if(getMembVarRadioButton().isSelected() && dataIDArr[i].getVariableType().equals(VariableType.MEMBRANE)){
				dataIdentifierTreeSet.add(varListName);
			}
		}
		if(dataIdentifierTreeSet.size() != 0){
			getDefaultListModelCivilizedVariables().setContents(dataIdentifierTreeSet.toArray(new String[0])/*pdeDataContext.getVariableNames()*/);			
		}else{
			getDefaultListModelCivilizedVariables().setContents(null);
		}
	}
}

///**
// * Comment
// */
//private void updateCurrentSelection(cbit.vcell.simdata.gui.SpatialSelection currentSelection) {
//	if (currentSelection == null) {
//		getJLabelCurrentSelection().setText("");
//		getJLabelCurrentSelection().setToolTipText("");
//	} else {
//		getJLabelCurrentSelection().setText(currentSelection.isPoint() ? "Point" : "Curve");
//		getJLabelCurrentSelection().setToolTipText(currentSelection.toString());
//	}
//}


/**
 * Comment
 */
private void updateExportFormat(ExportFormat exportFormat) {
	getJRadioButtonSlice().setEnabled(true);
	switch (exportFormat) {
		case CSV: {
			BeanUtils.enableComponents(getJPanelSelections(), true);
			getJRadioButtonFull().setEnabled(false);
			getJRadioButtonROI().setSelected(true);
			break;
		}
		case QUICKTIME:
		case GIF:
		case FORMAT_JPEG:
		case ANIMATED_GIF: {
			BeanUtils.enableComponents(getJPanelSelections(), false);
			getJRadioButtonSlice().setSelected(true);
			getJRadioButtonFull().setEnabled(true);
			break;
		}
		case NRRD: 
		case IMAGEJ:
		{
			BeanUtils.enableComponents(getJPanelSelections(), false);
			getJRadioButtonFull().setSelected(true);
			getJRadioButtonFull().setEnabled(true);			
			break;
		}
		case UCD:
		case PLY:
		case VTK_UNSTRUCT:
		case VTK_IMAGE: {
			BeanUtils.enableComponents(getJPanelSelections(), false);
			getJRadioButtonSlice().setSelected(false);
			getJRadioButtonSlice().setEnabled(false);
			getJRadioButtonFull().setSelected(true);
			getJRadioButtonFull().setEnabled(true);			
			break;
		}
	default:
		break;
	};
	String bText = BTN_EXPORT_TEXT;
	if (exportFormat.requiresFollowOn()) {
		bText += " ...";
	}
	getJButtonExport().setText(bText);
	
}


/**
 * Comment
 */
private void updateInterface() {
	setFormatChoices_0();
	//
	if(!getJRadioButtonROI().isSelected()){
		getROISelections().clearSelection();
	}
	//
	boolean startExportEnabled =
		(getJListVariables().getSelectedIndex() != -1)
		&&
		(
		(getJRadioButtonROI().isSelected() && getROISelections().getSelectedIndex() != -1)
		||
		(getJRadioButtonSlice().isSelected())
		||
		(getJRadioButtonFull().isSelected())
		);

	getJButtonExport().setEnabled(startExportEnabled);
	//
//	getJButtonAdd().setEnabled(false);
//	if(getSpatialSelections() != null && getSpatialSelections().length > 0){
//		getJButtonAdd().setEnabled(true);
//	}
	
	
	
//	boolean selectionAddEnabled = true;
//	for(int i=0;i < getJListSelections().getModel().getSize();i+= 1){
//		SpatialSelection sl = (SpatialSelection)getJListSelections().getModel().getElementAt(i);
//		if(sl.compareEqual(getSelectedRegion())){
//			selectionAddEnabled = false;
//			break;
//		}
//	}
//	selectionAddEnabled = selectionAddEnabled &&
//		getJRadioButtonSelection().isSelected()
//		&&
//		(getSelectedRegion() != null);
//		
//	getJButtonAdd().setEnabled(selectionAddEnabled);
//	//
//	boolean selectionRemoveEnabled =
//		getJRadioButtonSelection().isSelected()
//		&&
//		(getJListSelections().getSelectedIndex() != -1);
//	//
	
	getROISelections().setEnabled(getJRadioButtonROI().isSelected());

	
	getBothVarRadioButton().setEnabled(true);
	getVolVarRadioButton().setEnabled(true);
	getMembVarRadioButton().setEnabled(true);
	switch (getSelectedFormat()) {
	case QUICKTIME:
	case GIF:
	case ANIMATED_GIF:
	case FORMAT_JPEG:
		if(!getBothVarRadioButton().isSelected()){
			getBothVarRadioButton().doClick();
		}
		getVolVarRadioButton().setEnabled(false);
		break;
	case NRRD:
	case IMAGEJ:
		if(getBothVarRadioButton().isSelected()){
			getVolVarRadioButton().doClick();
		}
		getBothVarRadioButton().setEnabled(false);
		break;
	case VTK_IMAGE:
		if(!getVolVarRadioButton().isSelected()){
			getVolVarRadioButton().doClick();
		}
		getBothVarRadioButton().setEnabled(false);
		break;
	case CSV:
		if (getJRadioButtonROI().isSelected() && getROISelections().getModel( ).getSize() == 0) {
			getJRadioButtonSlice().setSelected(true);
		}
		break;
	case UCD:
	case PLY:
	case VTK_UNSTRUCT:
		//no operation?
		break;
	default:
		break;
	}
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
	 * @return
	 */
	protected JRadioButton getVolVarRadioButton() {
		if (volVarRadioButton == null) {
			volVarRadioButton = new JRadioButton();
			volVarRadioButton.setSelected(true);
			volVarRadioButton.setText("Volume Data");
		}
		return volVarRadioButton;
	}
	/**
	 * @return
	 */
	protected JRadioButton getMembVarRadioButton() {
		if (membVarRadioButton == null) {
			membVarRadioButton = new JRadioButton();
			membVarRadioButton.setText("Membrane Data");
		}
		return membVarRadioButton;
	}
	/**
	 * @return
	 */
	protected JLabel getSelectVariablesLabel() {
		if (selectVariablesLabel == null) {
			selectVariablesLabel = new JLabel();
			selectVariablesLabel.setText("Variable Names (Select 1 or more)");
		}
		return selectVariablesLabel;
	}


	public SpatialSelection[] getSpatialSelectionsVolume() {
		return spatialSelectionsVolume;
	}
	public SpatialSelection[] getSpatialSelectionsMembrane() {
		return spatialSelectionsMembrane;
	}


	public void setSpatialSelections(SpatialSelection[] spatialSelectionsVolume,SpatialSelection[] spatialSelectionsMembrane,int viewZoom) {
		this.spatialSelectionsVolume = spatialSelectionsVolume;
		this.spatialSelectionsMembrane = spatialSelectionsMembrane;
		this.viewZoom = viewZoom;
		updateChoiceROI();
		updateInterface();
	}
	/**
	 * @return
	 */
	protected JLabel getDefineExportDataLabel() {
		if (defineExportDataLabel == null) {
			defineExportDataLabel = new JLabel();
			defineExportDataLabel.setText("Define Export Data Variables:");
		}
		return defineExportDataLabel;
	}
	/**
	 * @return
	 */
	protected JRadioButton getBothVarRadioButton() {
		if (bothVarRadioButton == null) {
			bothVarRadioButton = new JRadioButton();
			bothVarRadioButton.setText("Vol/Membr Data");
		}
		return bothVarRadioButton;
	}

	public void setSolverTaskDescription(SolverTaskDescription solverDescription) {
		isSmoldyn = solverDescription.getSolverDescription().equals(SolverDescription.Smoldyn);
	}
}
