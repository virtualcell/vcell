/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.vcell.model.rbm.MolecularType;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.document.Identifiable;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.MiriamManager.DataType;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.desktop.Annotation;
import cbit.vcell.desktop.BioModelCellRenderer;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.xml.gui.MIRIAMAnnotationEditor;
import cbit.vcell.xml.gui.MiriamTreeModel;
import cbit.vcell.xml.gui.MiriamTreeModel.IdentifiableNode;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
/**
 * Superclass for all the Annotation panels
 * Creation date: (11/6/2018 3:44:00 PM)
 * @author: Dan Vasilescu
 */
@SuppressWarnings("serial")
public class AnnotationsPanel extends DocumentEditorSubPanel {
	private BioModel bioModel = null;
	private VCMetaData vcMetaData = null;
	MiriamTreeModel miriamTreeModel = null;
	
	private Identifiable selectedObject = null;
	
	public static final String ACTION_ADD ="Add Annotation ...";
	private EventHandler eventHandler = new EventHandler();
	
	private JTree jTreeMIRIAM = null;

	private JPanel jPanelNewIdentifier = null;		// upper panel with the new identifier editor
	private JPanel jBottomPanel = null;
	private JSplitPane splitPane = null;
	private JScrollPane jScrollPane = null;			// will show the MIRIAM JTree
	
	private JButton jButtonEdit = null;
	private JButton jButtonAdd = null;
	private JButton jButtonDelete = null;
	
	private JComboBox jComboBoxURI = null;			// identity provider combo
	private JTextField jTextFieldFormalID = null;	// immortal ID text
	
	private JTextPane annotationTextArea = null;

	private class EventHandler extends MouseAdapter implements ActionListener, FocusListener, PropertyChangeListener {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeAnnotation();
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseExited(e);
			if(e.getSource() == annotationTextArea){
				changeAnnotation();
			}
		}
		@Override
		// TODO: What else?
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() instanceof SpeciesContext
					|| evt.getSource() instanceof MolecularType
					|| evt.getSource() instanceof ReactionRule
					|| evt.getSource() instanceof ReactionStep
					|| evt.getSource() instanceof RbmObservable
					|| evt.getSource() instanceof BioModel
					|| evt.getSource() instanceof Structure
//					|| evt.getSource() instanceof MolecularType
					) {
				updateInterface();
			}
		}
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getSource() == jButtonAdd) {
				addIdentifier();
			}
		}

	}


public AnnotationsPanel() {
	super();
	initialize();
}

private void handleException(java.lang.Throwable exception) {
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

private JPanel getJPanelNewIdentifier() {
	if (jPanelNewIdentifier == null) {
		jPanelNewIdentifier = new JPanel();
		jPanelNewIdentifier.setLayout(new GridBagLayout());
		jPanelNewIdentifier.setPreferredSize(new Dimension(725, 37));
		jPanelNewIdentifier.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 2));
		
		int gridx = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 5, 3, 0);		// top left bottom right
		gbc.gridx = gridx;
		gbc.gridy = 0;
		JLabel jLabel = new JLabel("Identitiy Provider");
		jPanelNewIdentifier.add(jLabel, gbc);
		
		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 5, 3, 4);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		jPanelNewIdentifier.add(getJComboBoxURI(), gbc);
		
		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 10, 3, 0);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		jLabel = new JLabel("Immortal ID");
		jPanelNewIdentifier.add(jLabel, gbc);
		
		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 5, 3, 4);
//		gbc.gridwidth = 100;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = gridx;
		gbc.gridy = 0;
		jPanelNewIdentifier.add(getJTextFieldFormalID(), gbc);
		
		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 0, 3, 0);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		jPanelNewIdentifier.add(new JLabel(""), gbc);

		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 5, 3, 5);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		jPanelNewIdentifier.add(getJButtonAdd(), gbc);

		getJButtonAdd().addActionListener(eventHandler);
	}
	return jPanelNewIdentifier;
}
private JTextField getJTextFieldFormalID() {
	if (jTextFieldFormalID == null) {
		jTextFieldFormalID = new JTextField();
		jTextFieldFormalID.setText("NewID");
	}
	return jTextFieldFormalID;
}
private JComboBox getJComboBoxURI() {
	if (jComboBoxURI == null) {
		jComboBoxURI = new JComboBox();
//		DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
//		for (DataType dataType : vcMetaData.getMiriamManager().getAllDataTypes().values()){
//			defaultComboBoxModel.addElement(dataType);
//		}
//		jComboBoxURI.setModel(defaultComboBoxModel);
//		jComboBoxURI.setRenderer(new DefaultListCellRenderer() {
//			public Component getListCellRendererComponent(JList list, Object value,
//					int index, boolean isSelected, boolean cellHasFocus) {
//				return super.getListCellRendererComponent(list,((DataType)value).getDataTypeName(),index,isSelected,cellHasFocus);
//			}
//		});
	}
	return jComboBoxURI;
}

private JPanel getBottomPanel() {
	if (jBottomPanel == null) {
		jBottomPanel = new JPanel();
		jBottomPanel.setLayout(new BorderLayout());
		jBottomPanel.add(getSplitPane(), BorderLayout.CENTER);
	}
	return jBottomPanel;

}
private JSplitPane getSplitPane() {
	if (splitPane == null) {
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(getJScrollPane(), BorderLayout.CENTER);
		
		annotationTextArea = new JTextPane();
		annotationTextArea.setContentType("text/html");
		annotationTextArea.setEditable(false);
		annotationTextArea.addFocusListener(eventHandler);
		annotationTextArea.addMouseListener(eventHandler);
		JScrollPane rightPanel = new JScrollPane(annotationTextArea);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(rightPanel);
		splitPane.setResizeWeight(0.4);
		splitPane.setDividerLocation(0.6);

	}
	return splitPane;
	
}
	
private JScrollPane getJScrollPane() {
	if (jScrollPane == null) {
		jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(getJTreeMIRIAM());
	}
	return jScrollPane;
}
private JTree getJTreeMIRIAM() {
	if (jTreeMIRIAM == null) {
		try {
			DefaultTreeSelectionModel ivjLocalSelectionModel;
			ivjLocalSelectionModel = new DefaultTreeSelectionModel();
			ivjLocalSelectionModel.setSelectionMode(1);
			jTreeMIRIAM = new JTree();
			jTreeMIRIAM.setName("JTree1");
			jTreeMIRIAM.setToolTipText("");
			jTreeMIRIAM.setBounds(0, 0, 357, 405);
			jTreeMIRIAM.setMinimumSize(new java.awt.Dimension(100, 72));
			jTreeMIRIAM.setSelectionModel(ivjLocalSelectionModel);
			jTreeMIRIAM.setRowHeight(0);
			jTreeMIRIAM.setRootVisible(false);
			
			// Add cellRenderer
			DefaultTreeCellRenderer dtcr = new BioModelCellRenderer(null) {
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
						boolean leaf, int row, boolean hasFocus) {
					// default for LinkNode is in BioModelCellRenderer.java
					JLabel component = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					component.setIcon(null);
					return component;
				}
			};
			jTreeMIRIAM.setCellRenderer(dtcr);
			
			MouseListener mouseListener = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					super.mouseClicked(e);
					TreePath closestMousePath =jTreeMIRIAM.getClosestPathForLocation(e.getPoint().x, e.getPoint().y);
//					showPopup(e, closestMousePath);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					super.mouseReleased(e);
					TreePath closestMousePath =jTreeMIRIAM.getClosestPathForLocation(e.getPoint().x, e.getPoint().y);
//					showPopup(e,closestMousePath);
				}

				public void mousePressed(MouseEvent e) {
					TreePath closestMousePath =jTreeMIRIAM.getClosestPathForLocation(e.getPoint().x, e.getPoint().y);
					jTreeMIRIAM.setSelectionPath(closestMousePath);
//					showPopup(e,closestMousePath);
//					if(e.getClickCount() == 2) {
//						DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTreeMIRIAM.getLastSelectedPathComponent();
//						if (node instanceof LinkNode) {
//							showBrowseToLink((LinkNode)node);
//						}else if(isNodeFreeHandTextEditable(node)){
//							showEditFreehandText(node);
//							
//						}
//					}
				} 
			};
			jTreeMIRIAM.addMouseListener(mouseListener);

		} catch (java.lang.Throwable ivjExc) {
			ivjExc.printStackTrace(System.out);
		}
	}
	return jTreeMIRIAM;
}

private void initialize() {
	try {
		setLayout(new GridBagLayout());
		setBackground(Color.white);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(getJPanelNewIdentifier(), gbc);

		gridy++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(getBottomPanel(), gbc);

        getJTreeMIRIAM().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
//				TreePath tp = ((JTree)e.getSource()).getSelectionPath();
//				Object lastPathComponent = tp.getLastPathComponent();
//				getJButtonAdd().setEnabled(tp != null && lastPathComponent instanceof IdentifiableNode);
			}
		});

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void changeAnnotation() {
	try{
		if (bioModel == null) {
			return;
		}
		String textAreaStr = (annotationTextArea.getText() == null || annotationTextArea.getText().length()==0?null:annotationTextArea.getText());
		String oldText = bioModel.getVCMetaData().getFreeTextAnnotation(selectedObject);
		if(selectedObject != null && !Compare.isEqualOrNull(oldText,textAreaStr)){
			bioModel.getVCMetaData().setFreeTextAnnotation(selectedObject, textAreaStr);	
		}
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Annotation Error\n"+e.getMessage(), e);
	}
}

public void setBioModel(BioModel newValue) {
	if (newValue == bioModel) {
		return;
	}
	bioModel = newValue;
	vcMetaData = bioModel.getVCMetaData();

	// set tree model on jTableMIRIAM here, since we have access to miriamDescrHeir here
	miriamTreeModel = new MiriamTreeModel(new DefaultMutableTreeNode("Object Annotations",true), vcMetaData, false);
	jTreeMIRIAM.setModel(miriamTreeModel);

	updateInterface();
}

private void updateInterface() {
	if (bioModel == null) {
		return;
	}
	Identifiable entity = getIdentifiable(selectedObject);
	if(selectedObject != null && entity != null) {
		miriamTreeModel.createTree(entity);

		initializeComboBoxURI();
		getJButtonAdd().setEnabled(true);
		
		annotationTextArea.setEditable(true);
		String freeText = bioModel.getVCMetaData().getFreeTextAnnotation(selectedObject);
		annotationTextArea.setText(freeText);
		annotationTextArea.setCaretPosition(0);
	} else {
		getJButtonAdd().setEnabled(false);

		miriamTreeModel.createTree(null);
		annotationTextArea.setEditable(false);
		annotationTextArea.setText(null);
	}
}
private static Identifiable getIdentifiable(Identifiable selectedObject) {
	//
	// for SpeciesContext objects:
	//		MIRIAM tree needs the Species				!!!
	//		text Annotation needs the SpeciesContext
	//
	// !!!	use this only for the tree !!!
	//
	if(selectedObject == null) {
		return null;
	}
	if(selectedObject instanceof SpeciesContext) {
		return (Species)((SpeciesContext)selectedObject).getSpecies();
	} else if(selectedObject instanceof MolecularType) {
		return (MolecularType)selectedObject;
	} else if(selectedObject instanceof ReactionRule) {
		return (ReactionRule)selectedObject;
	} else if(selectedObject instanceof ReactionStep) {
		return (ReactionStep)selectedObject;
	} else if(selectedObject instanceof RbmObservable) {
		return (RbmObservable)selectedObject;
	} else if(selectedObject instanceof BioModel) {
		return (BioModel)selectedObject;
	} else if(selectedObject instanceof Structure) {
		return (Structure)selectedObject;
	} else {
		return null;
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects != null && selectedObjects.length == 1) {
		if(selectedObjects[0] instanceof Identifiable && selectedObjects[0] instanceof Displayable) {
			selectedObject = (Identifiable)selectedObjects[0];
			System.out.println("class: " + selectedObject.getClass().getSimpleName() + ", selected object: " + ((Displayable)selectedObject).getDisplayName());
		} else {
			selectedObject = null;
			System.out.println("Unsupported or null entity");
		}
		updateInterface();
	}
}
	
// -------------------------------------------------------------------------------------------------------


private void addIdentifier() {
	MIRIAMQualifier qualifier = MIRIAMQualifier.BIO_isDescribedBy;
	MiriamManager.DataType objectNamespace = (MiriamManager.DataType)getJComboBoxURI().getSelectedItem();
	String objectID = getJTextFieldFormalID().getText();
	MiriamManager miriamManager = vcMetaData.getMiriamManager();
	HashSet<MiriamResource> miriamResources = new HashSet<MiriamResource>();
	try {
		Identifiable entity = getIdentifiable(selectedObject);
		MiriamResource mr = miriamManager.createMiriamResource(objectNamespace.getBaseURN()+":"+objectID);
		miriamResources.add(mr);
		miriamManager.addMiriamRefGroup(entity, qualifier, miriamResources);
		updateInterface();
//		miriamTreeModel.annotationChanged();
	} catch (Exception e) {
		e.printStackTrace();
		DialogUtils.showErrorDialog(this,"Add Identifier failed:\n"+e.getMessage(), e);
	}
}

private void initializeComboBoxURI() {
	Identifiable entity = getIdentifiable(selectedObject);

	DefaultComboBoxModel<DataType> defaultComboBoxModel = new DefaultComboBoxModel<DataType> ();
	for (DataType dataType : vcMetaData.getMiriamManager().getAllDataTypes().values()){
		defaultComboBoxModel.addElement(dataType);
	}
	getJComboBoxURI().setModel(defaultComboBoxModel);
	getJComboBoxURI().setRenderer(new DefaultListCellRenderer() {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			return super.getListCellRendererComponent(list,((DataType)value).getDataTypeName(),index,isSelected,cellHasFocus);
		}
	});
}

private JButton getJButtonAdd() {
	if (jButtonAdd == null) {
		jButtonAdd = new JButton();
		jButtonAdd.setText(ACTION_ADD);
	}
	return jButtonAdd;
}



	/*

Universal (goes in all types of annotations):
PubMed ID https://www.ncbi.nlm.nih.gov/pubmed
DOI: https://www.doi.org/

Model
BioModels Database https://www.ebi.ac.uk/biomodels/
Reactome: https://reactome.org/
Grey: NeuronDB: https://senselab.med.yale.edu/neurondb/
Grey: https://senselab.med.yale.edu/modeldb/
Grey: CellML Model Repository: https://models.cellml.org/cellml

Species/Molecules/Observables:
CheBI: https://www.ebi.ac.uk/chebi/
Kegg Compound: https://www.genome.jp/kegg/compound/
Uniprot: https://www.uniprot.org/
Enzyme Nomenclature: http://www.sbcs.qmul.ac.uk/iubmb/enzyme/
Gene Ontology: http://www.geneontology.org/
Reactome: https://reactome.org/

Reactions
Kegg Reaction: https://www.genome.jp/kegg/reaction/
Kegg Pathway: https://www.genome.jp/kegg/pathway.html
Reactome: https://reactome.org/
Grey: BRENDA: https://www.brenda-enzymes.org/
Grey: BIND  https://www.bindingdb.org/bind/index.jsp

Parameters (all grey for now)
Grey:BIND https://www.bindingdb.org/bind/index.jsp

Structures
Grey: BRENDA: https://www.brenda-enzymes.org/
Grey: Gene Ontology  http://geneontology.org/

*/

}
