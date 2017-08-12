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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmElementAbstract;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;
import org.vcell.relationship.RelationshipObject;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.http.uniprot.UniProtConstants;
import org.vcell.sybil.util.miriam.XRefToURN;
import org.vcell.util.Compare;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.HighlightableShapeInterface;
import cbit.vcell.graph.MolecularComponentLargeShape;
import cbit.vcell.graph.MolecularComponentLargeShape.ComponentStateLargeShape;
import cbit.vcell.graph.MolecularTypeLargeShape;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.ReactionCartoon.RuleAnalysisChanged;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.graph.SpeciesPatternSmallShape.DisplayRequirements;
import cbit.vcell.graph.gui.LargeShapePanel;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.common.VCellErrorMessages;
import uk.ac.ebi.www.miriamws.main.MiriamWebServices.MiriamProvider;
import uk.ac.ebi.www.miriamws.main.MiriamWebServices.MiriamProviderServiceLocator;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class SpeciesPropertiesPanel extends DocumentEditorSubPanel {
	private SpeciesContext fieldSpeciesContext = null;
	private BioModel bioModel = null;
	
	private JTextArea annotationTextArea;
	private JScrollPane linkedPOScrollPane;
	private JEditorPane PCLinkValueEditorPane = null;
	private JTextField nameTextField = null;
	
	private JTree speciesPropertiesTree = null;
	private SpeciesPropertiesTreeModel speciesPropertiesTreeModel = null;
	private JScrollPane scrollPane;		// shapePanel lives inside this
	private JPanel leftPanel;			// outermost container
	
	private EventHandler eventHandler = new EventHandler();

//	private JPopupMenu popupFromTreeMenu;
//	private JMenu addMenu;
//	private JMenuItem deleteMenuItem;	
//	private JMenuItem renameMenuItem;
//	private JMenuItem editMenuItem;
	private JCheckBox showDetailsCheckBox;

	private JPopupMenu popupFromShapeMenu;

	private SpeciesPatternLargeShape spls;
	private LargeShapePanel shapePanel = null;
	
	public class BioModelNodeEditableTree extends JTree {
		@Override
		public boolean isPathEditable(TreePath path) {
			Object object = path.getLastPathComponent();
			return object instanceof BioModelNode;
		}
	}
	
	public void saveSelectedXRef(final XRef selectedXRef, final MIRIAMQualifier miriamQualifier) {
		AsynchClientTask task1 = new AsynchClientTask("retrieving metadata", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String urn = XRefToURN.createURN(selectedXRef.db(), selectedXRef.id());
				try {
					MiriamManager miriamManager = bioModel.getModel().getVcMetaData().getMiriamManager();
					MiriamResource resource = miriamManager.createMiriamResource(urn);
					String urnstr = resource.getMiriamURN(); 
					if (urnstr != null && urnstr.toLowerCase().contains("uniprot")) {
						String prettyName = UniProtConstants.getNameFromID(urnstr);	
						if (prettyName != null) {
							miriamManager.setPrettyName(resource, prettyName);
						}
					}

					Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
					miriamResources.add(resource);
					miriamManager.addMiriamRefGroup(getSpeciesContext().getSpecies(), miriamQualifier, miriamResources);
					
					MiriamProviderServiceLocator providerLocator = new MiriamProviderServiceLocator();
					MiriamProvider provider = providerLocator.getMiriamWebServices();
					
					String pcLink = resource.getMiriamURN();
					if (pcLink != null && pcLink.length() > 0) {
						String[] locations = provider.getLocations(pcLink);
						if (locations != null){
							for(String url : locations) {
								try {
									miriamManager.addStoredCrossReferencedLink(resource, new URL(url));
								} catch (MalformedURLException e) {
									e.printStackTrace(System.out);
								}
							}
						}
					}
				} catch (URNParseFailureException e) {
					e.printStackTrace();
					DialogUtils.showErrorDialog(SpeciesPropertiesPanel.this, e.getMessage());
				}
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("displaying metadata", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				updatePCLink();
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });
	}

	private void updatePCLink() {
		try {
			StringBuffer buffer = new StringBuffer("<html>");
			MiriamManager miriamManager = bioModel.getModel().getVcMetaData().getMiriamManager();
			Map<MiriamRefGroup,MIRIAMQualifier> refGroups = miriamManager.getAllMiriamRefGroups(getSpeciesContext().getSpecies());
			if (refGroups != null && refGroups.size()>0) {
				for (MiriamRefGroup refGroup : refGroups.keySet()){
					Set<MiriamResource> miriamResources = refGroup.getMiriamRefs();
					for (MiriamResource resource : miriamResources){
						String urn = resource.getMiriamURN();
						String preferredName = ""; 
						if (urn != null && urn.length() > 0) {
							String prettyName = miriamManager.getPrettyName(resource);
							if (prettyName != null) {
								preferredName = "[" + prettyName + "]";	
							}
							String prettyResourceName = urn.replaceFirst("urn:miriam:", "");
							buffer.append("&#x95;&nbsp;" + prettyResourceName + "&nbsp;<b>" + preferredName + "</b><br>");
							List<URL> linkURLs = miriamManager.getStoredCrossReferencedLinks(resource);
							for (URL url : linkURLs) {
								buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;-&nbsp;<a href=\"" + url.toString() + "\">" + url.toString() + "</a><br>");
							}
						}
					}
				}
			}
			buffer.append("</html>");
			getPCLinkValueEditorPane().setText(buffer.toString());
			getPCLinkValueEditorPane().setCaretPosition(0);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}

	}
	
	private class EventHandler extends MouseAdapter implements java.awt.event.ActionListener, HyperlinkListener, FocusListener, PropertyChangeListener, TreeSelectionListener,
	TreeWillExpandListener
	{
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			Object source = e.getSource();
			if (source == nameTextField) {
				changeName();
			} else if (source == showDetailsCheckBox) {
				speciesPropertiesTreeModel.setShowDetails(showDetailsCheckBox.isSelected());
			}
		};
		@Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == EventType.ACTIVATED) {
				URL link = e.getURL();
				if (link != null) {
					DialogUtils.browserLauncher(SpeciesPropertiesPanel.this, link.toExternalForm(), "failed to launch");
				}
			}
		}
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeFreeTextAnnotation();
			} else if (e.getSource() == nameTextField) {
				changeName();
			} 
		}
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// we update everything matter what property change event was fired by the speciesContext
			if (evt.getSource() == fieldSpeciesContext) {
				updateInterface();
			}
		}
		@Override
		public void valueChanged(TreeSelectionEvent e) {
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseExited(e);
			if(e.getSource() == annotationTextArea){
				changeFreeTextAnnotation();
			}
		}
		@Override
		public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
			boolean veto = false;
			if (veto) {
				throw new ExpandVetoException(e);
			}
		}
		@Override
		public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
			JTree tree = (JTree) e.getSource();
			TreePath path = e.getPath();
			boolean veto = false;
			if(path.getParentPath() == null) {
				veto = true;
			}
			if (veto) {
				throw new ExpandVetoException(e);
			}
		}
	}


/**
 * EditSpeciesDialog constructor comment.
 */
public SpeciesPropertiesPanel() {
	super();
	initialize();
}

/**
 * Gets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @return The speciesContext property value.
 * @see #setSpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return fieldSpeciesContext;
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
private void initConnections() throws java.lang.Exception {
	annotationTextArea.addFocusListener(eventHandler);
	annotationTextArea.addMouseListener(eventHandler);
	nameTextField.addFocusListener(eventHandler);
	getPCLinkValueEditorPane().addHyperlinkListener(eventHandler);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		Border border = BorderFactory.createLineBorder(Color.gray);
		shapePanel = new LargeShapePanel() {		// glyph (shape) panel
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(spls != null) {
					spls.paintSelf(g);
				}
			}

			@Override
			public DisplayMode getDisplayMode() {
				return DisplayMode.other;
			}

			@Override
			public RuleAnalysisChanged hasStateChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasStateChanged(MolecularComponentPattern molecularComponentPattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasBondChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasBondChanged(MolecularComponentPattern molecularComponentPattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasNoMatch(String reactionRuleName, MolecularTypePattern mtp) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasNoMatch(MolecularTypePattern molecularTypePattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleParticipantSignature getSignature() {
				return null;
			}

			@Override
			public GroupingCriteria getCriteria() {
				return null;
			}

			@Override
			public boolean isViewSingleRow() {
				return true;
			}
		};
		shapePanel.setBorder(border);
		shapePanel.setBackground(Color.white);		
		shapePanel.setEditable(true);
		shapePanel.setShowMoleculeColor(true);
		shapePanel.setShowNonTrivialOnly(true);
//		Dimension ms = new Dimension(350, 80);
//		shapePanel.setMinimumSize(ms);
		shapePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(e.getButton() == 1) {		// left click selects the object (we highlight it)
					Point whereClicked = e.getPoint();
					PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
					manageMouseActivity(locationContext);
				} else if(e.getButton() == 3) {						// right click invokes popup menu (only if the object is highlighted)
					Point whereClicked = e.getPoint();
					PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
					manageMouseActivity(locationContext);
					if(locationContext.getDeepestShape() != null && !locationContext.getDeepestShape().isHighlighted()) {
						// TODO: (maybe) add code here to highlight the shape if it's not highlighted already but don't show the menu
						// return;
					}					
					showPopupMenu(e, locationContext);
				}
			}
			private void manageMouseActivity(PointLocationInShapeContext locationContext) {
				Graphics g = shapePanel.getGraphics();
				spls.turnHighlightOffRecursive(g);
				if (spls.contains(locationContext)) {		//check if mouse is inside shape
					System.out.println("left click inside shape " + locationContext.getDeepestShape().toString());
				}
				locationContext.highlightDeepestShape();
				locationContext.paintDeepestShape(g);
			}
		});
		shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point overWhat = e.getPoint();
				PointLocationInShapeContext locationContext = new PointLocationInShapeContext(overWhat);
				spls.contains(locationContext);
				HighlightableShapeInterface hsi = locationContext.getDeepestShape();
				if(hsi == null) {
					shapePanel.setToolTipText(null);
				} else {
					shapePanel.setToolTipText("Right click for " + hsi.getDisplayType() + " menus");
				}
			} 
		});
		// ----------------------------------------------------------------------------------
		leftPanel = new JPanel();
		GridBagLayout mgr = new GridBagLayout();
		mgr.rowHeights = new int[] { 100,100 };
		leftPanel.setLayout(mgr);
		leftPanel.setBackground(Color.white);
		
		speciesPropertiesTree = new BioModelNodeEditableTree();
		speciesPropertiesTreeModel = new SpeciesPropertiesTreeModel(speciesPropertiesTree);
		speciesPropertiesTree.setModel(speciesPropertiesTreeModel);
		
		JPanel generalPanel = new JPanel();
		generalPanel.setLayout(new GridBagLayout());
		Dimension size = new Dimension(100, 150);
		generalPanel.setMinimumSize(size);

		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		nameTextField.addActionListener(eventHandler);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		JLabel label = new JLabel("Species Name");
		generalPanel.add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		generalPanel.add(nameTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		generalPanel.add(new JLabel("Linked Pathway Object(s)"), gbc);

		linkedPOScrollPane = new JScrollPane();
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.1;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		generalPanel.add(linkedPOScrollPane, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(9, 8, 4, 6);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		generalPanel.add(new JLabel("Annotation "), gbc);

		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setFont(new Font("monospaced", Font.PLAIN, 11));
		annotationTextArea.setEditable(false);
		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.1;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		generalPanel.add(jsp, gbc);
	
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.gridwidth = 1;
        gbc1.weightx = 1;
        gbc1.weighty = 1;
        gbc1.fill = GridBagConstraints.BOTH;
        leftPanel.add(generalPanel, gbc1);

        scrollPane = new JScrollPane(shapePanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 1;
        gbc1.weightx = 1;
        gbc1.weighty = 0.1;
        gbc1.fill = GridBagConstraints.BOTH;
        leftPanel.add(scrollPane, gbc1); 
		
		setName("SpeciesEditorPanel");
		setLayout(new BorderLayout());
		setBackground(Color.white);
		add(leftPanel, BorderLayout.CENTER);
		
		initConnections();
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void changeFreeTextAnnotation() {
	try{
		if (getSpeciesContext() == null) {
			return;
		}
		// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
		if(bioModel.getModel() != null && bioModel.getModel().getVcMetaData() != null){
			VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
			String textAreaStr = (annotationTextArea.getText() == null || annotationTextArea.getText().length()==0?null:annotationTextArea.getText());
			if(!Compare.isEqualOrNull(vcMetaData.getFreeTextAnnotation(getSpeciesContext().getSpecies()),textAreaStr)){
				vcMetaData.setFreeTextAnnotation(getSpeciesContext().getSpecies(), textAreaStr);	
			}
		}
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage(), e);
	}
}

// wei's code
private String listLinkedPathwayObjects(){
	if (getSpeciesContext() == null) {
		return "no selected species";
	}
	if(bioModel == null || bioModel.getModel() == null){
		return "no biomodel";
	}
	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	String linkedPOlist = "";
	for(RelationshipObject relObject : bioModel.getRelationshipModel().getRelationshipObjects(getSpeciesContext())){
		final BioPaxObject bpObject = relObject.getBioPaxObject();
		if(bpObject instanceof Entity){
			JLabel label = new JLabel("<html><u>" + ((Entity)bpObject).getName().get(0) + "</u></html>");
			label.setForeground(Color.blue);
			label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram),new Object[]{bpObject});
					}
				}
			});
			panel.add(label);
		}
	}
	linkedPOScrollPane.setViewportView(panel);
	return linkedPOlist;
}

public synchronized void addActionListener(ActionListener l) {
    listenerList.add(ActionListener.class, l);
}

public void setBioModel(BioModel newValue) {
	if (bioModel == newValue) {
		return;
	}
	bioModel = newValue;
	speciesPropertiesTreeModel.setBioModel(bioModel);
}


/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param newValue The new value for the property.
 * @see #getSpeciesContext
 */
void setSpeciesContext(SpeciesContext newValue) {
	if (fieldSpeciesContext == newValue) {
		return;
	}
	SpeciesContext oldValue = fieldSpeciesContext;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(eventHandler);
	}
	// commit the changes before switch to another species
	changeName();
	changeFreeTextAnnotation();
	
	fieldSpeciesContext = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(eventHandler);
	}
	speciesPropertiesTreeModel.setSpeciesContext(fieldSpeciesContext);
	updateInterface();
}

private void updateInterface() {
	boolean bNonNullSpeciesContext = fieldSpeciesContext != null && bioModel != null;
	annotationTextArea.setEditable(bNonNullSpeciesContext);
	nameTextField.setEditable(bNonNullSpeciesContext);
	if (bNonNullSpeciesContext) {
		nameTextField.setText(getSpeciesContext().getName());
		annotationTextArea.setText(bioModel.getModel().getVcMetaData().getFreeTextAnnotation(getSpeciesContext().getSpecies()));
		updatePCLink();		
	} else {
		annotationTextArea.setText(null);
		getPCLinkValueEditorPane().setText(null);
		nameTextField.setText(null);
	}
	listLinkedPathwayObjects();
	updateShape();
}

public static final int xOffsetInitial = 20;
public static final int yOffsetInitial = 10;
private void updateShape() {
	if(fieldSpeciesContext!= null) {
		SpeciesPattern sp = fieldSpeciesContext.getSpeciesPattern();
		spls = new SpeciesPatternLargeShape(xOffsetInitial, yOffsetInitial, -1, sp, shapePanel, fieldSpeciesContext, issueManager);
		
		int maxXOffset = xOffsetInitial + spls.getWidth();
		int maxYOffset = yOffsetInitial + 80;
		Dimension preferredSize = new Dimension(maxXOffset+120, maxYOffset+20);
		shapePanel.setPreferredSize(preferredSize);
		shapePanel.repaint();
//		scrollPane.repaint();
	}
}

	private JEditorPane getPCLinkValueEditorPane() {
		if (PCLinkValueEditorPane == null) {
			PCLinkValueEditorPane = new JEditorPane();
			PCLinkValueEditorPane.setContentType("text/html");
			PCLinkValueEditorPane.setEditable(false);
			PCLinkValueEditorPane.setBackground(UIManager.getColor("TextField.inactiveBackground"));
			PCLinkValueEditorPane.setText(null);
		}
		return PCLinkValueEditorPane;
	}

	private void changeName() {
		if (fieldSpeciesContext == null) {
			return;
		}
		String newName = nameTextField.getText();
		if (newName == null || newName.length() == 0) {
			nameTextField.setText(getSpeciesContext().getName());
			return;
		}
		if (newName.equals(fieldSpeciesContext.getName())) {
			return;
		}
		try {
			getSpeciesContext().setName(newName);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
			DialogUtils.showErrorDialog(SpeciesPropertiesPanel.this, e1.getMessage());
		}
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) {
			return;
		}
		if (selectedObjects[0] instanceof SpeciesContext) {
			setSpeciesContext((SpeciesContext) selectedObjects[0]);
		} else {
			setSpeciesContext(null);
		}		
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
	}
	
	private void showPopupMenu(MouseEvent e, PointLocationInShapeContext locationContext) {
		if (popupFromShapeMenu == null) {
			popupFromShapeMenu = new JPopupMenu();			
		}		
		if (popupFromShapeMenu.isShowing()) {
			return;
		}
		final Object deepestShape = locationContext.getDeepestShape();
		final RbmElementAbstract selectedObject;
		
		if(deepestShape == null) {
			selectedObject = null;
			System.out.println("outside");		// when cursor is outside any species pattern we offer to add a new one
//			popupFromShapeMenu.add(getAddSpeciesPatternFromShapeMenuItem());
		} else if(deepestShape instanceof ComponentStateLargeShape) {
			System.out.println("inside state");
			if(((ComponentStateLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((ComponentStateLargeShape)deepestShape).getComponentStatePattern();
			} else {
				return;
			}
		} else if(deepestShape instanceof MolecularComponentLargeShape) {
			System.out.println("inside component");
			if(((MolecularComponentLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((MolecularComponentLargeShape)deepestShape).getMolecularComponentPattern();
			} else {
				return;
			}
		} else if(deepestShape instanceof MolecularTypeLargeShape) {
			System.out.println("inside molecule");
			if(((MolecularTypeLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((MolecularTypeLargeShape)deepestShape).getMolecularTypePattern();
			} else {
				return;
			}
		} else if(deepestShape instanceof SpeciesPatternLargeShape) {
			System.out.println("inside species pattern");
			if(((SpeciesPatternLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((SpeciesPatternLargeShape)deepestShape).getSpeciesPattern();
			} else {
				if(!fieldSpeciesContext.hasSpeciesPattern()) {
					selectedObject = new SpeciesPattern();
				} else {
					return;
				}
			}
		} else {
			selectedObject = null;
			System.out.println("inside something else?");
			return;
		}
		System.out.println(selectedObject);	
		popupFromShapeMenu.removeAll();
		Point mousePoint = e.getPoint();
		
		if(selectedObject instanceof SpeciesPattern) {
			final SpeciesPattern sp = (SpeciesPattern)selectedObject;
			JMenu addMenuItem = new JMenu(VCellErrorMessages.SpecifyMolecularTypes);
			popupFromShapeMenu.add(addMenuItem);
			addMenuItem.removeAll();
			for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
				JMenuItem menuItem = new JMenuItem(mt.getName());
				Graphics gc = shapePanel.getGraphics();
				Icon icon = new MolecularTypeSmallShape(1, 4, mt, null, gc, mt, null, issueManager);
				menuItem.setIcon(icon);
				addMenuItem.add(menuItem);
				menuItem.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
						for(MolecularComponentPattern mcp : molecularTypePattern.getComponentPatternList()) {
							mcp.setBondType(BondType.None);
						}
						if(!fieldSpeciesContext.hasSpeciesPattern()) {
							fieldSpeciesContext.setSpeciesPattern(sp);
						}
						fieldSpeciesContext.getSpeciesPattern().addMolecularTypePattern(molecularTypePattern);
					}
				});
			}
//			JMenu compartmentMenuItem = new JMenu("Specify structure");
//			popupFromShapeMenu.add(compartmentMenuItem);
//			compartmentMenuItem.removeAll();
//			for (final Structure struct : bioModel.getModel().getStructures()) {
//				JMenuItem menuItem = new JMenuItem(struct.getName());
//				compartmentMenuItem.add(menuItem);
//				menuItem.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						String nameStruct = e.getActionCommand();
//						Structure struct = bioModel.getModel().getStructure(nameStruct);
//						fieldSpeciesContext.setStructure(struct);
//					}
//				});
//			}
		} else if(selectedObject instanceof MolecularTypePattern) {
			MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
			
			String moveRightMenuText = "Move <b>" + "right" + "</b>";
			moveRightMenuText = "<html>" + moveRightMenuText + "</html>";
			JMenuItem moveRightMenuItem = new JMenuItem(moveRightMenuText);
			Icon icon = VCellIcons.moveRightIcon;
			moveRightMenuItem.setIcon(icon);
			moveRightMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MolecularTypePattern from = (MolecularTypePattern)selectedObject;
					SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
					sp.shiftRight(from);
					speciesPropertiesTreeModel.populateTree();
				}
			});
			popupFromShapeMenu.add(moveRightMenuItem);
			
			String moveLeftMenuText = "Move <b>" + "left" + "</b>";
			moveLeftMenuText = "<html>" + moveLeftMenuText + "</html>";
			JMenuItem moveLeftMenuItem = new JMenuItem(moveLeftMenuText);
			icon = VCellIcons.moveLeftIcon;
			moveLeftMenuItem.setIcon(icon);
			moveLeftMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MolecularTypePattern from = (MolecularTypePattern)selectedObject;
					SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
					sp.shiftLeft(from);
					speciesPropertiesTreeModel.populateTree();
				}
			});
			popupFromShapeMenu.add(moveLeftMenuItem);
			popupFromShapeMenu.add(new JSeparator());

			String deleteMenuText = "Delete <b>" + mtp.getMolecularType().getName() + "</b>";
			deleteMenuText = "<html>" + deleteMenuText + "</html>";
			JMenuItem deleteMenuItem = new JMenuItem(deleteMenuText);
			deleteMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
					SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
					sp.removeMolecularTypePattern(mtp);
					if(sp.getMolecularTypePatterns().isEmpty()) {
						fieldSpeciesContext.setSpeciesPattern(null);
					}
				}
			});
			popupFromShapeMenu.add(deleteMenuItem);
			
		} else if(selectedObject instanceof MolecularComponentPattern) {
			manageComponentPatternFromShape(selectedObject, locationContext, ShowWhat.ShowBond);		
			
		} else if(selectedObject instanceof ComponentStatePattern) {
			MolecularComponentPattern mcp = ((ComponentStateLargeShape)deepestShape).getMolecularComponentPattern();
			manageComponentPatternFromShape(mcp, locationContext, ShowWhat.ShowState);
			
		} else {
			System.out.println("Where am I ???");
		}
		popupFromShapeMenu.show(e.getComponent(), mousePoint.x, mousePoint.y);
	}
	
	private enum ShowWhat {
		ShowState,
		ShowBond
	}
	public void manageComponentPatternFromShape(final RbmElementAbstract selectedObject, PointLocationInShapeContext locationContext, ShowWhat showWhat) {
		popupFromShapeMenu.removeAll();
		final MolecularComponentPattern mcp = (MolecularComponentPattern)selectedObject;
		final MolecularComponent mc = mcp.getMolecularComponent();
		
		// ------------------------------------------------------------------- State
		if(showWhat == ShowWhat.ShowState && mc.getComponentStateDefinitions().size() != 0) {
			String prefix = "State:  ";
			final Map<String, String> itemMap = new LinkedHashMap<String, String>();
//			itemList.add(ComponentStatePattern.strAny);			// any is not an option for state
			String csdCurrentName;
			for (final ComponentStateDefinition csd : mc.getComponentStateDefinitions()) {
				csdCurrentName = "";
				if(mcp.getComponentStatePattern() != null && !mcp.getComponentStatePattern().isAny()) {
					ComponentStateDefinition csdCurrent = mcp.getComponentStatePattern().getComponentStateDefinition();
					csdCurrentName = csdCurrent.getName();
				}
				String name = csd.getName();
				if(name.equals(csdCurrentName)) {	// currently selected menu item is shown in bold
					name = "<html>" + prefix + "<b>" + name + "</b></html>";
				} else {
					name = "<html>" + prefix + name + "</html>";
				}
				itemMap.put(name, csd.getName());
			}
			for(String name : itemMap.keySet()) {
				JMenuItem menuItem = new JMenuItem(name);
				popupFromShapeMenu.add(menuItem);
				menuItem.setIcon(VCellIcons.rbmComponentStateIcon);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String key = e.getActionCommand();
						String name = itemMap.get(key);
						if(name.equals(ComponentStatePattern.strAny)) {
							ComponentStatePattern csp = new ComponentStatePattern();
							mcp.setComponentStatePattern(csp);
						} else {
							ComponentStateDefinition csd = mcp.getMolecularComponent().getComponentStateDefinition(name);
							if(csd == null) {
								throw new RuntimeException("Missing ComponentStateDefinition " + name + " for Component " + mcp.getMolecularComponent().getName());
							}
							ComponentStatePattern csp = new ComponentStatePattern(csd);
							mcp.setComponentStatePattern(csp);
						}
					}
				});
			}
		}
		if(showWhat == ShowWhat.ShowState) {
			return;
		}
		
		// ---------------------------------------------------------------------------- Bonds
		final MolecularTypePattern mtp = locationContext.getMolecularTypePattern();
		final SpeciesPattern sp = locationContext.getSpeciesPattern();
		
		JMenu editBondMenu = new JMenu();
		final String specifiedString = mcp.getBondType() == BondType.Specified ? "<html><b>" + "Site bond specified" + "</b></html>" : "<html>" + "Site bond specified" + "</html>";
		editBondMenu.setText(specifiedString);
		editBondMenu.setToolTipText("Specified");
		editBondMenu.removeAll();
		final Map<String, Bond> itemMap = new LinkedHashMap<String, Bond>();
		
		String noneString = mcp.getBondType() == BondType.None ? "<html><b>" + "Site is unbound" + "</b></html>" : "<html>" + "Site is unbound" + "</html>";
		itemMap.put(noneString, null);
//		itemMap.put(existsString, null);	// not a valid option for species
//		itemMap.put(possibleString, null);	// not a valid option for species
		if(mtp != null && sp != null) {
			List<Bond> bondPartnerChoices = sp.getAllBondPartnerChoices(mtp, mc);
			for(Bond b : bondPartnerChoices) {
//				if(b.equals(mcp.getBond())) {
//					continue;	// if the mcp has a bond already we don't offer it
//				}
				int index = 0;
				if(mcp.getBondType() == BondType.Specified) {
					index = mcp.getBondId();
				} else {
					index = sp.nextBondId();
				}
				itemMap.put(b.toHtmlStringLong(sp, mtp, mc, index), b);
//				itemMap.put(b.toHtmlStringLong(sp, index), b);
			}
		}
		int index = 0;
		Graphics gc = shapePanel.getGraphics();
		for(String name : itemMap.keySet()) {
			JMenuItem menuItem = new JMenuItem(name);
			if(index == 0) {
				menuItem.setIcon(VCellIcons.rbmBondNoneIcon);
				menuItem.setToolTipText("None");
				popupFromShapeMenu.add(menuItem);
			} else {
				Bond b = itemMap.get(name);
				SpeciesPattern spBond = new SpeciesPattern(bioModel.getModel(), sp);		// clone of the sp, with only the bond of interest
				spBond.resetBonds();
				spBond.resetStates();
				MolecularTypePattern mtpFrom = spBond.getMolecularTypePattern(mtp.getMolecularType().getName(), mtp.getIndex());
				MolecularComponentPattern mcpFrom = mtpFrom.getMolecularComponentPattern(mc);
				MolecularTypePattern mtpTo = spBond.getMolecularTypePattern(b.molecularTypePattern.getMolecularType().getName(), b.molecularTypePattern.getIndex());
				MolecularComponentPattern mcpTo = mtpTo.getMolecularComponentPattern(b.molecularComponentPattern.getMolecularComponent());
				spBond.setBond(mtpTo, mcpTo, mtpFrom, mcpFrom);
				Icon icon = new SpeciesPatternSmallShape(3,4, spBond, gc, fieldSpeciesContext, false, issueManager);
				((SpeciesPatternSmallShape)icon).setDisplayRequirements(DisplayRequirements.highlightBonds);
				menuItem.setIcon(icon);
				editBondMenu.add(menuItem);
			}
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = e.getActionCommand();
					BondType btBefore = mcp.getBondType();
					if(name.equals(noneString)) {
						if(btBefore == BondType.Specified) {	// specified -> not specified
							// change the partner to none since this is the only option
							mcp.getBond().molecularComponentPattern.setBondType(BondType.None);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.None);
						mcp.setBond(null);
						SwingUtilities.invokeLater(new Runnable() { public void run() { speciesPropertiesTreeModel.populateTree(); } });
					} else {
						if (btBefore != BondType.Specified) {
							// if we go from a non-specified to a specified we need to find the next available
							// bond id, so that we can choose the color for displaying the bond
							// a bad bond id, like -1, will crash badly when trying to choose the color
							int bondId = sp.nextBondId();
							mcp.setBondId(bondId);
						} else {
							// specified -> specified
							// change the old partner to none since it's the only available option, continue using the bond id
							mcp.getBond().molecularComponentPattern.setBondType(BondType.None);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Specified);
						Bond b = itemMap.get(name);
						mcp.setBond(b);
						mcp.getBond().molecularComponentPattern.setBondId(mcp.getBondId());
						sp.resolveBonds();
						SwingUtilities.invokeLater(new Runnable() { public void run() { speciesPropertiesTreeModel.populateTree(); } });
					}
				}
			});
			index++;
		}
		popupFromShapeMenu.add(editBondMenu);
	}	

}
