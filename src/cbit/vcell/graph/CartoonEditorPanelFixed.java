package cbit.vcell.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.vcell.util.gui.ButtonGroupCivilized;
import org.vcell.util.gui.JToolBarToggleButton;

import cbit.gui.graph.CartoonTool.Mode;
import cbit.gui.graph.GraphPane;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;

/**
 * Insert the type's description here.
 * Creation date: (6/19/2005 9:34:21 AM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class CartoonEditorPanelFixed extends JPanel {
	private static final Dimension TOOLBAR_BUTTON_SIZE = new Dimension(28, 28);
	private static final Dimension TOOL_BAR_SEPARATOR_SIZE = new Dimension(15,0);
	private JToolBarToggleButton ivjFeatureButton = null;
	private javax.swing.JToolBar ivjJToolBar = null;
	private JToolBarToggleButton ivjSelectButton = null;
	private JToolBarToggleButton ivjSpeciesButton = null;

	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private StructureCartoon ivjStructureCartoon1 = null;
	private StructureCartoonTool ivjStructureCartoonTool1 = null;
	private ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.ButtonModel ivjselection1 = null;
	private GraphPane ivjGraphPane1 = null;
	private BioModel fieldBioModel = null;
	private DocumentManager fieldDocumentManager = null;
	private BioModel ivjbioModel1 = null;
	private boolean ivjConnPtoP3Aligning = false;

	private class IvjEventHandler implements java.beans.PropertyChangeListener, ActionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == CartoonEditorPanelFixed.this.getButtonGroupCivilized1() && (evt.getPropertyName().equals("selection"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == CartoonEditorPanelFixed.this && (evt.getPropertyName().equals("documentManager"))) 
				connEtoM1(evt);
			if (evt.getSource() == CartoonEditorPanelFixed.this && (evt.getPropertyName().equals("bioModel"))) 
				connPtoP3SetTarget();
		}

		public void actionPerformed(ActionEvent e) {
		}
	};

	/**
	 * CartoonEditorPanelFixed constructor comment.
	 */
	public CartoonEditorPanelFixed() {
		super();
		initialize();
	}

	/**
	 * CartoonEditorPanelFixed constructor comment.
	 * @param layout java.awt.LayoutManager
	 */
	public CartoonEditorPanelFixed(java.awt.LayoutManager layout) {
		super(layout);
	}


	/**
	 * CartoonEditorPanelFixed constructor comment.
	 * @param layout java.awt.LayoutManager
	 * @param isDoubleBuffered boolean
	 */
	public CartoonEditorPanelFixed(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}


	/**
	 * CartoonEditorPanelFixed constructor comment.
	 * @param isDoubleBuffered boolean
	 */
	public CartoonEditorPanelFixed(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}


	/**
	 * Comment
	 */
	private void cartoonEditorPanelFixed_Initialize() {
		getButtonGroupCivilized1().add(getSelectButton());
		getButtonGroupCivilized1().add(getFeatureButton());
		getButtonGroupCivilized1().add(getSpeciesButton());
		getSelectButton().setSelected(true);

		getStructureCartoonTool1().setGraphModel(getStructureCartoon1());
		getStructureCartoonTool1().setButtonGroup(getButtonGroupCivilized1());
		getStructureCartoonTool1().setGraphPane(getGraphPane1());
		getGraphPane1().setGraphModel(getStructureCartoon1());

	}


	/**
	 * connEtoC1:  (CartoonEditorPanelFixed.initialize() --> CartoonEditorPanelFixed.cartoonEditorPanelFixed_Initialize()V)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1() {
		try {
			// user code begin {1}
			// user code end
			this.cartoonEditorPanelFixed_Initialize();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM1:  (CartoonEditorPanelFixed.documentManager --> StructureCartoonTool1.documentManager)
	 * @param arg1 java.beans.PropertyChangeEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getStructureCartoonTool1().setDocumentManager(this.getDocumentManager());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM2:  (bioModel1.this --> StructureCartoon1.model)
	 * @param value cbit.vcell.biomodel.BioModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM2(cbit.vcell.biomodel.BioModel value) {
		try {
			// user code begin {1}
			// user code end
			if ((getbioModel1() != null)) {
				getStructureCartoon1().setModel(getbioModel1().getModel());
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
	 * connPtoP1SetSource:  (ButtonGroupCivilized1.selection <--> selection1.this)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				if ((getselection1() != null)) {
					getButtonGroupCivilized1().setSelection(getselection1());
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
	 * connPtoP1SetTarget:  (ButtonGroupCivilized1.selection <--> selection1.this)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				setselection1(getButtonGroupCivilized1().getSelection());
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
	 * connPtoP2SetTarget:  (selection1.actionCommand <--> StructureCartoonTool1.modeString)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			if ((getselection1() != null)) {
				getStructureCartoonTool1().setModeString(getselection1().getActionCommand());
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
	 * connPtoP3SetSource:  (CartoonEditorPanelFixed.bioModel <--> bioModel1.this)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP3Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP3Aligning = true;
				if ((getbioModel1() != null)) {
					this.setBioModel(getbioModel1());
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
	 * connPtoP3SetTarget:  (CartoonEditorPanelFixed.bioModel <--> bioModel1.this)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP3SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP3Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP3Aligning = true;
				setbioModel1(this.getBioModel());
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
	 * Gets the bioModel property (cbit.vcell.biomodel.BioModel) value.
	 * @return The bioModel property value.
	 * @see #setBioModel
	 */
	public cbit.vcell.biomodel.BioModel getBioModel() {
		return fieldBioModel;
	}


	/**
	 * Return the bioModel1 property value.
	 * @return cbit.vcell.biomodel.BioModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private cbit.vcell.biomodel.BioModel getbioModel1() {
		// user code begin {1}
		// user code end
		return ivjbioModel1;
	}


	/**
	 * Return the ButtonGroupCivilized1 property value.
	 * @return cbit.gui.ButtonGroupCivilized
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private ButtonGroupCivilized getButtonGroupCivilized1() {
		if (ivjButtonGroupCivilized1 == null) {
			try {
				ivjButtonGroupCivilized1 = new ButtonGroupCivilized();
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
	 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
	 * @return The documentManager property value.
	 * @see #setDocumentManager
	 */
	public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
		return fieldDocumentManager;
	}


	/**
	 * Return the FeatureButton property value.
	 * @return cbit.gui.JToolBarToggleButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JToolBarToggleButton getFeatureButton() {
		if (ivjFeatureButton == null) {
			try {
				ivjFeatureButton = new JToolBarToggleButton();
				ivjFeatureButton.setName("FeatureButton");
				ivjFeatureButton.setToolTipText("Compartment Tool");
				ivjFeatureButton.setText("");
				ivjFeatureButton.setMaximumSize(TOOLBAR_BUTTON_SIZE);
				ivjFeatureButton.setActionCommand(Mode.FEATURE.getActionCommand());
				ivjFeatureButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/feature.gif")));
				ivjFeatureButton.setPreferredSize(TOOLBAR_BUTTON_SIZE);
				ivjFeatureButton.setMinimumSize(TOOLBAR_BUTTON_SIZE);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjFeatureButton;
	}


	/**
	 * Return the GraphPane1 property value.
	 * @return cbit.gui.graph.GraphPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private GraphPane getGraphPane1() {
		if (ivjGraphPane1 == null) {
			try {
				ivjGraphPane1 = new GraphPane();
				ivjGraphPane1.setName("GraphPane1");
				ivjGraphPane1.setBounds(0, 0, 150, 150);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjGraphPane1;
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
//				ivjJScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//				ivjJScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				getJScrollPane1().setViewportView(getGraphPane1());
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
	 * Return the JToolBar property value.
	 * @return javax.swing.JToolBar
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JToolBar getJToolBar() {
		if (ivjJToolBar == null) {
			try {
				ivjJToolBar = new javax.swing.JToolBar();
				ivjJToolBar.setName("JToolBar");
				ivjJToolBar.setFloatable(false);
				ivjJToolBar.setBorder(new javax.swing.border.EtchedBorder());
				ivjJToolBar.setOrientation(javax.swing.SwingConstants.HORIZONTAL);
				getJToolBar().addSeparator(TOOL_BAR_SEPARATOR_SIZE);
				getJToolBar().add(getSelectButton(), getSelectButton().getName());
				getJToolBar().add(getFeatureButton(), getFeatureButton().getName());
				getJToolBar().add(getSpeciesButton(), getSpeciesButton().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJToolBar;
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
				ivjSelectButton.setToolTipText("Select Tool");
				ivjSelectButton.setText("");
				ivjSelectButton.setMaximumSize(TOOLBAR_BUTTON_SIZE);
				ivjSelectButton.setActionCommand(Mode.SELECT.getActionCommand());
				ivjSelectButton.setSelected(true);
				ivjSelectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select.gif")));
				ivjSelectButton.setPreferredSize(TOOLBAR_BUTTON_SIZE);
				ivjSelectButton.setMinimumSize(TOOLBAR_BUTTON_SIZE);
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
	 * Return the selection1 property value.
	 * @return javax.swing.ButtonModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.ButtonModel getselection1() {
		// user code begin {1}
		// user code end
		return ivjselection1;
	}


	/**
	 * Return the SpeciesButton property value.
	 * @return cbit.gui.JToolBarToggleButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private org.vcell.util.gui.JToolBarToggleButton getSpeciesButton() {
		if (ivjSpeciesButton == null) {
			try {
				ivjSpeciesButton = new org.vcell.util.gui.JToolBarToggleButton();
				ivjSpeciesButton.setName("SpeciesButton");
				ivjSpeciesButton.setToolTipText("Species Tool");
				ivjSpeciesButton.setText("");
				ivjSpeciesButton.setMaximumSize(TOOLBAR_BUTTON_SIZE);
				ivjSpeciesButton.setActionCommand(Mode.SPECIES.getActionCommand());
				ivjSpeciesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/species.gif")));
				ivjSpeciesButton.setPreferredSize(TOOLBAR_BUTTON_SIZE);
				ivjSpeciesButton.setMinimumSize(TOOLBAR_BUTTON_SIZE);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjSpeciesButton;
	}

	/**
	 * Return the StructureCartoon1 property value.
	 * @return cbit.vcell.graph.StructureCartoon
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private StructureCartoon getStructureCartoon1() {
		if (ivjStructureCartoon1 == null) {
			try {
				ivjStructureCartoon1 = new StructureCartoon();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStructureCartoon1;
	}


	/**
	 * Return the StructureCartoonTool1 property value.
	 * @return cbit.vcell.graph.StructureCartoonTool
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private StructureCartoonTool getStructureCartoonTool1() {
		if (ivjStructureCartoonTool1 == null) {
			try {
				ivjStructureCartoonTool1 = new StructureCartoonTool();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStructureCartoonTool1;
	}


	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}


	/**
	 * Initializes connections
	 * @exception java.lang.Exception The exception description.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initConnections() throws java.lang.Exception {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().addPropertyChangeListener(ivjEventHandler);
		this.addPropertyChangeListener(ivjEventHandler);
		connPtoP1SetTarget();
		connPtoP2SetTarget();
		connPtoP3SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("CartoonEditorPanelFixed");
			setLayout(new BorderLayout());

			add(getJToolBar(), BorderLayout.NORTH);
			add(getJScrollPane1(), BorderLayout.CENTER);
			initConnections();
			connEtoC1();
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
			javax.swing.JFrame frame = new javax.swing.JFrame();
			CartoonEditorPanelFixed aCartoonEditorPanelFixed;
			aCartoonEditorPanelFixed = new CartoonEditorPanelFixed();
			frame.setContentPane(aCartoonEditorPanelFixed);
			frame.setSize(aCartoonEditorPanelFixed.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}


	/**
	 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
	 * @param bioModel The new value for the property.
	 * @see #getBioModel
	 */
	public void setBioModel(cbit.vcell.biomodel.BioModel bioModel) {
		cbit.vcell.biomodel.BioModel oldValue = fieldBioModel;
		fieldBioModel = bioModel;
		firePropertyChange("bioModel", oldValue, bioModel);
	}


	/**
	 * Set the bioModel1 to a new value.
	 * @param newValue cbit.vcell.biomodel.BioModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setbioModel1(cbit.vcell.biomodel.BioModel newValue) {
		if (ivjbioModel1 != newValue) {
			try {
				cbit.vcell.biomodel.BioModel oldValue = getbioModel1();
				ivjbioModel1 = newValue;
				connPtoP3SetSource();
				connEtoM2(ivjbioModel1);
				firePropertyChange("bioModel", oldValue, newValue);
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
	 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
	 * @param documentManager The new value for the property.
	 * @see #getDocumentManager
	 */
	public void setDocumentManager(DocumentManager documentManager) {
		DocumentManager oldValue = fieldDocumentManager;
		fieldDocumentManager = documentManager;
		firePropertyChange("documentManager", oldValue, documentManager);
	}


	/**
	 * Set the selection1 to a new value.
	 * @param newValue javax.swing.ButtonModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setselection1(javax.swing.ButtonModel newValue) {
		if (ivjselection1 != newValue) {
			try {
				ivjselection1 = newValue;
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
}