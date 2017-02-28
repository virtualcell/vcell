/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;
import java.awt.GridBagConstraints;

import javax.swing.ButtonModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.vcell.util.gui.ButtonGroupCivilized;
import org.vcell.util.gui.JToolBarToggleButton;

import cbit.gui.graph.CartoonTool.Mode;
import cbit.gui.graph.GraphPane;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class StructureMappingCartoonPanel extends DocumentEditorSubPanel implements java.beans.PropertyChangeListener {
	private GraphPane ivjStructureGraphPane = null;
	private StructureMappingPanel ivjStructureMappingPanel = null;
	private org.vcell.util.gui.ButtonGroupCivilized ivjButtonGroupCivilized = null;
	private JToolBar ivjJToolBar1 = null;
	private JToolBarToggleButton ivjLineButton = null;
	private JToolBarToggleButton ivjSelectButton = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ButtonModel ivjSelection = null;
	private JScrollPane ivjJScrollPane1 = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP3Aligning = false;
	private SimulationContext ivjsimulationContext1 = null;
	private JPanel ivjJPanel2 = null;
	private StructureMappingCartoonTool ivjStructureMappingCartoonTool1 = null;
	private StructureMappingCartoon ivjStructureMappingCartoon1 = null;
	private GeometryContext ivjGeometryContext1 = null;
	private JLabel ivjMessageLabel = null;
	private JLabel boundaryMessageLabel = null;

	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public StructureMappingCartoonPanel() {
		super();
		initialize();
	}

	/**
	 * connEtoM1:  (StructureMappingCartoonPanel.initialize() --> ButtonGroupCivilized.add(Ljavax.swing.AbstractButton;)V)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM1() {
		try {
			// user code begin {1}
			// user code end
			getButtonGroupCivilized().add(getSelectButton());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM10:  (simulationContext1.this --> GeometryContext1.this)
	 * @param value cbit.vcell.mapping.SimulationContext
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM10(SimulationContext value) {
		try {
			// user code begin {1}
			// user code end
			if ((getsimulationContext1() != null)) {
				setGeometryContext1(getsimulationContext1().getGeometryContext());
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
	 * connEtoM11:  (simulationContext1.geometryContext --> GeometryContext1.this)
	 * @param arg1 java.beans.PropertyChangeEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM11(java.beans.PropertyChangeEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			setGeometryContext1(getsimulationContext1().getGeometryContext());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM12:  (GeometryContext1.geometry --> LineButton.enabled)
	 * @param arg1 java.beans.PropertyChangeEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM12(java.beans.PropertyChangeEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getLineButton().setEnabled(this.hasMappableGeometry());
			getMessageLabel().setVisible(this.hasMappableGeometry());
			getBoundaryMessageLabel().setVisible(this.hasMappableGeometry());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM15:  (simulationContext1.this --> StructureMappingPanel.geometryContext)
	 * @param value cbit.vcell.mapping.SimulationContext
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM15(cbit.vcell.mapping.SimulationContext value) {
		try {
			// user code begin {1}
			// user code end
			getStructureMappingPanel().setGeometryContext(this.getGeoContext(getsimulationContext1()));
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM2:  (StructureMappingCartoonPanel.initialize() --> ButtonGroupCivilized.add(Ljavax.swing.AbstractButton;)V)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM2() {
		try {
			// user code begin {1}
			// user code end
			getButtonGroupCivilized().add(getLineButton());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM3:  (simulationContext1.this --> StructureMappingCartoon1.simulationContext)
	 * @param value cbit.vcell.mapping.SimulationContext
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM3(SimulationContext value) {
		try {
			// user code begin {1}
			// user code end
			getStructureMappingCartoon1().setSimulationContext(getsimulationContext1());
			connEtoM5();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM4:  (StructureMappingCartoonPanel.initialize() --> StructureMappingCartoonTool1.structureMappingCartoon)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM4() {
		try {
			// user code begin {1}
			// user code end
			getStructureMappingCartoonTool1().setStructureMappingCartoon(getStructureMappingCartoon1());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM5:  ( (simulationContext1,this --> StructureMappingCartoon1,simulationContext).normalResult --> LineButton.enabled)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM5() {
		try {
			// user code begin {1}
			// user code end
			getLineButton().setEnabled(this.hasMappableGeometry());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM6:  (StructureMappingCartoonPanel.initialize() --> StructureGraphPane.graphModel)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM6() {
		try {
			// user code begin {1}
			// user code end
			getStructureGraphPane().setGraphModel(getStructureMappingCartoon1());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM7:  (simulationContext1.geometryContext --> StructureMappingPanel.geometryContext)
	 * @param arg1 java.beans.PropertyChangeEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getStructureMappingPanel().setGeometryContext(getsimulationContext1().getGeometryContext());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM8:  (StructureMappingCartoonPanel.initialize() --> StructureMappingCartoonTool1.buttonGroup)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM8() {
		try {
			// user code begin {1}
			// user code end
			getStructureMappingCartoonTool1().setButtonGroup(getButtonGroupCivilized());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM9:  (StructureMappingCartoonPanel.initialize() --> StructureMappingCartoonTool1.graphPane)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM9() {
		try {
			// user code begin {1}
			// user code end
			getStructureMappingCartoonTool1().setGraphPane(getStructureGraphPane());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connPtoP1SetSource:  (ButtonGroupCivilized.selection <--> Selection.this)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				if ((getSelection() != null)) {
					getButtonGroupCivilized().setSelection(getSelection());
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
	 * connPtoP1SetTarget:  (ButtonGroupCivilized.selection <--> Selection.this)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				setSelection(getButtonGroupCivilized().getSelection());
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
	 * connPtoP2SetTarget:  (Selection.actionCommand <--> StructureMappingCartoonTool1.modeString)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			if ((getSelection() != null)) {
				getStructureMappingCartoonTool1().setModeString(getSelection().getActionCommand());
			}
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connPtoP3SetSource:  (StructureMappingCartoonPanel.simulationContext <--> simulationContext1.this)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP3Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP3Aligning = true;
				if ((getsimulationContext1() != null)) {
					this.setSimulationContext(getsimulationContext1());
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
	 * connPtoP3SetTarget:  (StructureMappingCartoonPanel.simulationContext <--> simulationContext1.this)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP3Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP3Aligning = true;
				setsimulationContext1(this.getSimulationContext());
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
	 * Return the ButtonGroupCivilized property value.
	 * @return cbit.gui.ButtonGroupCivilized
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private ButtonGroupCivilized getButtonGroupCivilized() {
		if (ivjButtonGroupCivilized == null) {
			try {
				ivjButtonGroupCivilized = new ButtonGroupCivilized();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjButtonGroupCivilized;
	}


	/**
	 * Comment
	 */
	public GeometryContext getGeoContext(SimulationContext argSimulationContext) {
		if (argSimulationContext!=null){
			return argSimulationContext.getGeometryContext();
		}else{
			return null;
		}
	}


	/**
	 * Return the GeometryContext1 property value.
	 * @return cbit.vcell.mapping.GeometryContext
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private GeometryContext getGeometryContext1() {
		// user code begin {1}
		// user code end
		return ivjGeometryContext1;
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

				java.awt.GridBagConstraints constraintsMessageLabel = new java.awt.GridBagConstraints();
				constraintsMessageLabel.gridx = 0; constraintsMessageLabel.gridy = 0;
				constraintsMessageLabel.fill = java.awt.GridBagConstraints.BOTH;
				constraintsMessageLabel.anchor = GridBagConstraints.CENTER;
				constraintsMessageLabel.weightx = 1.0;
				constraintsMessageLabel.weighty = 0;
				constraintsMessageLabel.insets = new java.awt.Insets(3, 5, 0, 4);
				ivjJPanel2.add(getMessageLabel(), constraintsMessageLabel);

				java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
				constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
				constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
				constraintsJScrollPane1.weightx = 1.0;
				constraintsJScrollPane1.weighty = 5.0;
				constraintsJScrollPane1.insets = new java.awt.Insets(2, 4, 4, 4);
				ivjJPanel2.add(getJScrollPane1(), constraintsJScrollPane1);

				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; gbc.gridy = 2;
				gbc.fill = java.awt.GridBagConstraints.BOTH;
				gbc.anchor = GridBagConstraints.CENTER;
				gbc.weightx = 1.0;
				gbc.weighty = 0;
				gbc.insets = new java.awt.Insets(1, 5, 2, 4);
				ivjJPanel2.add(getBoundaryMessageLabel(), gbc);
				
				java.awt.GridBagConstraints constraintsStructureMappingPanel = new java.awt.GridBagConstraints();
				constraintsStructureMappingPanel.gridx = 0; constraintsStructureMappingPanel.gridy = 3;
				constraintsStructureMappingPanel.fill = java.awt.GridBagConstraints.BOTH;
				constraintsStructureMappingPanel.weightx = 1.0;
				constraintsStructureMappingPanel.weighty = 1.5;
				ivjJPanel2.add(getStructureMappingPanel(), constraintsStructureMappingPanel);

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
	 * Return the JScrollPane1 property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getJScrollPane1() {
		if (ivjJScrollPane1 == null) {
			try {
				ivjJScrollPane1 = new javax.swing.JScrollPane();
				ivjJScrollPane1.setName("JScrollPane1");
				ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				getJScrollPane1().setViewportView(getStructureGraphPane());
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
	 * Return the JScrollPane1 property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getMessageLabel() {
		if (ivjMessageLabel == null) {
			try {
				ivjMessageLabel  = new javax.swing.JLabel();
				ivjMessageLabel.setName("MessageLabel");
				ivjMessageLabel.setText("<html>All structures and subdomains must be mapped to run a simulation. Use line tool or drop down menu in the 'subdomain' column.</html>");
				ivjMessageLabel.setHorizontalAlignment(SwingConstants.LEFT);
				ivjMessageLabel.setHorizontalTextPosition(SwingConstants.LEFT);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMessageLabel;
	}
	private javax.swing.JLabel getBoundaryMessageLabel() {
		if (boundaryMessageLabel == null) {
			try {
				boundaryMessageLabel  = new javax.swing.JLabel();
				boundaryMessageLabel.setName("BoundaryMessageLabel");
				boundaryMessageLabel.setText("<html><font color = \"#8B0000\">Membrane boundary conditions are chosen alphabetically among the adjacent subdomains.</font></html>");
				boundaryMessageLabel.setHorizontalAlignment(SwingConstants.LEFT);
				boundaryMessageLabel.setHorizontalTextPosition(SwingConstants.LEFT);
				boundaryMessageLabel.setToolTipText("Rename a subdomain in the Geometry Definition Panel if you need to change which one is chosen.");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return boundaryMessageLabel;
	}

	/**
	 * Return the JToolBar1 property value.
	 * @return javax.swing.JToolBar
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JToolBar getJToolBar1() {
		if (ivjJToolBar1 == null) {
			try {
				ivjJToolBar1 = new javax.swing.JToolBar();
				ivjJToolBar1.setName("JToolBar1");
				ivjJToolBar1.setFloatable(false);
				ivjJToolBar1.setBorder(new javax.swing.border.EtchedBorder());
				ivjJToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
				ivjJToolBar1.add(getSelectButton(), getSelectButton().getName());
				ivjJToolBar1.add(getLineButton(), getLineButton().getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJToolBar1;
	}

	/**
	 * Return the LineButton property value.
	 * @return cbit.gui.JToolBarToggleButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private org.vcell.util.gui.JToolBarToggleButton getLineButton() {
		if (ivjLineButton == null) {
			try {
				ivjLineButton = new org.vcell.util.gui.JToolBarToggleButton();
				ivjLineButton.setName("LineButton");
				ivjLineButton.setText("");
				ivjLineButton.setMaximumSize(new java.awt.Dimension(28, 28));
				ivjLineButton.setActionCommand(Mode.LINE.getActionCommand());
				ivjLineButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/line.gif")));
				ivjLineButton.setPreferredSize(new java.awt.Dimension(28, 28));
				ivjLineButton.setMinimumSize(new java.awt.Dimension(28, 28));
				ivjLineButton.setEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLineButton;
	}

	/**
	 * Return the SelectButton property value.
	 * @return cbit.gui.JToolBarToggleButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JToolBarToggleButton getSelectButton() {
		if (ivjSelectButton == null) {
			try {
				ivjSelectButton = new JToolBarToggleButton();
				ivjSelectButton.setName("SelectButton");
				ivjSelectButton.setText("");
				ivjSelectButton.setMaximumSize(new java.awt.Dimension(28, 28));
				ivjSelectButton.setActionCommand(Mode.SELECT.getActionCommand());
				ivjSelectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select.gif")));
				ivjSelectButton.setSelected(true);
				ivjSelectButton.setPreferredSize(new java.awt.Dimension(28, 28));
				ivjSelectButton.setMinimumSize(new java.awt.Dimension(28, 28));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjSelectButton;
	}

	/**
	 * Return the Selection property value.
	 * @return javax.swing.ButtonModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.ButtonModel getSelection() {
		// user code begin {1}
		// user code end
		return ivjSelection;
	}


	/**
	 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
	 * @return The simulationContext property value.
	 * @see #setSimulationContext
	 */
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}


	/**
	 * Return the simulationContext1 property value.
	 * @return cbit.vcell.mapping.SimulationContext
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private SimulationContext getsimulationContext1() {
		// user code begin {1}
		// user code end
		return ivjsimulationContext1;
	}

	/**
	 * Return the StructureGraphPane property value.
	 * @return cbit.vcell.graph.GraphPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private GraphPane getStructureGraphPane() {
		if (ivjStructureGraphPane == null) {
			try {
				ivjStructureGraphPane = new GraphPane();
				ivjStructureGraphPane.setName("StructureGraphPane");
				ivjStructureGraphPane.setBounds(0, 0, 290, 237);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStructureGraphPane;
	}

	/**
	 * Return the StructureMappingCartoon1 property value.
	 * @return cbit.vcell.mapping.gui.StructureMappingCartoon
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private StructureMappingCartoon getStructureMappingCartoon1() {
		if (ivjStructureMappingCartoon1 == null) {
			try {
				ivjStructureMappingCartoon1 = new StructureMappingCartoon();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStructureMappingCartoon1;
	}


	/**
	 * Return the StructureMappingCartoonTool1 property value.
	 * @return cbit.vcell.mapping.gui.StructureMappingCartoonTool
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private StructureMappingCartoonTool getStructureMappingCartoonTool1() {
		if (ivjStructureMappingCartoonTool1 == null) {
			try {
				ivjStructureMappingCartoonTool1 = new StructureMappingCartoonTool();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStructureMappingCartoonTool1;
	}


	/**
	 * Return the StructureMappingPanel property value.
	 * @return cbit.vcell.mapping.StructureMappingPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private StructureMappingPanel getStructureMappingPanel() {
		if (ivjStructureMappingPanel == null) {
			try {
				ivjStructureMappingPanel = new StructureMappingPanel();
				ivjStructureMappingPanel.setName("StructureMappingPanel");
				ivjStructureMappingPanel.setPreferredSize(new java.awt.Dimension(100, 200));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStructureMappingPanel;
	}

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in CartoonPanel");
		exception.printStackTrace(System.out);
	}

	/**
	 * Comment
	 */
	public boolean hasMappableGeometry() {
		if (getSimulationContext() != null
				&& getSimulationContext().getGeometryContext() != null
				&& getSimulationContext().getGeometryContext().getGeometry() != null
				&& getSimulationContext().getGeometryContext().getGeometry().getDimension() > 0) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Initializes connections
	 * @exception java.lang.Exception The exception description.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initConnections() throws java.lang.Exception {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized().addPropertyChangeListener(this);
		this.addPropertyChangeListener(this);
		connPtoP1SetTarget();
		connPtoP3SetTarget();
		connPtoP2SetTarget();
	}

	/**
	 * Initialize class
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("StaticCartoonPanel");
			setLayout(new java.awt.BorderLayout());
			//setSize(522, 463);
			add(getJToolBar1(), "West");
			add(getJPanel2(), "Center");
			initConnections();
			connEtoM1();
			connEtoM2();
			connEtoM4();
			connEtoM6();
			connEtoM8();
			connEtoM9();
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
			StructureMappingCartoonPanel aStructureMappingCartoonPanel;
			aStructureMappingCartoonPanel = new StructureMappingCartoonPanel();
			frame.setContentPane(aStructureMappingCartoonPanel);
			frame.setSize(aStructureMappingCartoonPanel.getSize());
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
		if (evt.getSource() == getButtonGroupCivilized() && (evt.getPropertyName().equals("selection"))) 
			connPtoP1SetTarget();
		if (evt.getSource() == this && (evt.getPropertyName().equals("simulationContext"))) 
			connPtoP3SetTarget();
		if (evt.getSource() == getsimulationContext1() && (evt.getPropertyName().equals("geometryContext"))) 
			connEtoM7(evt);
		if (evt.getSource() == getsimulationContext1() && (evt.getPropertyName().equals("geometryContext"))) 
			connEtoM11(evt);
		if (evt.getSource() == getGeometryContext1() && (evt.getPropertyName().equals("geometry"))) 
			connEtoM12(evt);
		// user code begin {2}
		// user code end
	}

	/**
	 * Set the GeometryContext1 to a new value.
	 * @param newValue cbit.vcell.mapping.GeometryContext
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setGeometryContext1(GeometryContext newValue) {
		if (ivjGeometryContext1 != newValue) {
			try {
				/* Stop listening for events from the current object */
				if (ivjGeometryContext1 != null) {
					ivjGeometryContext1.removePropertyChangeListener(this);
				}
				ivjGeometryContext1 = newValue;

				/* Listen for events from the new object */
				if (ivjGeometryContext1 != null) {
					ivjGeometryContext1.addPropertyChangeListener(this);
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
	 * Set the Selection to a new value.
	 * @param newValue javax.swing.ButtonModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setSelection(javax.swing.ButtonModel newValue) {
		if (ivjSelection != newValue) {
			try {
				ivjSelection = newValue;
				connPtoP1SetSource();
				connPtoP2SetTarget();
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
	 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
	 * @param simulationContext The new value for the property.
	 * @see #getSimulationContext
	 */
	public void setSimulationContext(SimulationContext simulationContext) {
		SimulationContext oldValue = fieldSimulationContext;
		fieldSimulationContext = simulationContext;
		getMessageLabel().setVisible(this.hasMappableGeometry());
		getBoundaryMessageLabel().setVisible(this.hasMappableGeometry());
		firePropertyChange("simulationContext", oldValue, simulationContext);
	}


	/**
	 * Set the simulationContext1 to a new value.
	 * @param newValue cbit.vcell.mapping.SimulationContext
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setsimulationContext1(SimulationContext newValue) {
		if (ivjsimulationContext1 != newValue) {
			try {
				SimulationContext oldValue = getsimulationContext1();
				/* Stop listening for events from the current object */
				if (ivjsimulationContext1 != null) {
					ivjsimulationContext1.removePropertyChangeListener(this);
				}
				ivjsimulationContext1 = newValue;

				/* Listen for events from the new object */
				if (ivjsimulationContext1 != null) {
					ivjsimulationContext1.addPropertyChangeListener(this);
				}
				connPtoP3SetSource();
				connEtoM15(ivjsimulationContext1);
				connEtoM3(ivjsimulationContext1);
				connEtoM10(ivjsimulationContext1);
				firePropertyChange("simulationContext", oldValue, newValue);
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

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		getStructureMappingPanel().setSelectionManager(selectionManager);
	}
	
	@Override
	public void setIssueManager(IssueManager newValue) {
		super.setIssueManager(newValue);
		getStructureMappingPanel().setIssueManager(newValue);
	}
}
