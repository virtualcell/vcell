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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmElementAbstract;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.SpeciesPattern.Bond;

import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.RbmObservable.ObservableType;
import cbit.vcell.model.Structure;
import cbit.vcell.model.RbmObservable.Sequence;
import cbit.vcell.model.common.VCellErrorMessages;

import org.vcell.util.Compare;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.SpeciesPatternLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.HighlightableShapeInterface;
import cbit.vcell.graph.LargeShapePanel;
import cbit.vcell.graph.MolecularComponentLargeShape;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.MolecularTypeLargeShape;
import cbit.vcell.graph.MolecularComponentLargeShape.ComponentStateLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape.DisplayRequirements;
import cbit.vcell.graph.SpeciesPatternSmallShape;


@SuppressWarnings("serial")
public class ObservablePropertiesPanel extends DocumentEditorSubPanel {
	
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener, TreeSelectionListener,
		TreeWillExpandListener, FocusListener, KeyListener
	{
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == observable) {
				if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
					updateInterface();
				} else if (evt.getPropertyName().equals("entityChange")) {
					updateInterface();
				}
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (e.getSource() == getAddSpeciesButton()) {
				addSpeciesPattern();
			} else if (e.getSource() == getAddSpeciesPatternFromShapeMenuItem()) {
				addSpeciesPattern();
			} else if (source == getDeleteFromShapeMenuItem()) {
				deleteFromShape();
			} else if (source == getAddFromShapeMenu()) {
				addNewFromShape();
			} else if (source == getSequenceMultimolecularButton()) {
				getLengthEqualTextField().setEnabled(false);
				getLengthGreaterTextField().setEnabled(false);
				observable.setSequence(Sequence.Multimolecular);
				try {
				observable.setType(ObservableType.Molecules);
				} catch(Exception ex) {
					System.out.println(ex);
				}
			} else if (source == getSequencePolimerEqualButton()) {
				getLengthEqualTextField().setEnabled(true);
				getLengthGreaterTextField().setEnabled(false);
				observable.setSequence(Sequence.PolymerLengthEqual);
				try {
				observable.setType(ObservableType.Species);
				} catch(Exception ex) {
					System.out.println(ex);
				}
			} else if (source == getSequencePolimerGreaterButton()) {
				getLengthEqualTextField().setEnabled(false);
				getLengthGreaterTextField().setEnabled(true);
				observable.setSequence(Sequence.PolymerLengthGreater);
				try {
				observable.setType(ObservableType.Species);
				} catch(Exception ex) {
					System.out.println(ex);
				}
			} else if (source == getLengthEqualTextField()) {
			} else if (source == getLengthGreaterTextField()) {
			}
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void keyPressed(KeyEvent e) {
		}
		@Override
		public void keyReleased(KeyEvent e) {
			Object source = e.getSource();
			if (source == getLengthEqualTextField()) {
				String str = getLengthEqualTextField().getText();
				if(str.length() == 0) {
					return;		// just deleted the number; do nothing and expect the user to type something
				} else if(str.length() > 2) {
					getLengthEqualTextField().setText(observable.getSequenceLength()+"");
					return;		// don't allow anything larger than 2 digits
				}
				int value;
				try {
					value = Integer.parseInt(str);
				} catch(NumberFormatException ex) {
					// if error just put back whatever the observable has
					getLengthEqualTextField().setText(observable.getSequenceLength()+"");
					return;
				}
				observable.setSequence(Sequence.PolymerLengthEqual, value);
			} else if (source == getLengthGreaterTextField()) {
				String str = getLengthGreaterTextField().getText();
				if(str.length() == 0) {
					return;
				} else if(str.length() > 2) {
					getLengthGreaterTextField().setText(observable.getSequenceLength()+"");
					return;
				}
				int value;
				try {
					value = Integer.parseInt(str);
				} catch(NumberFormatException ex) {
					getLengthGreaterTextField().setText(observable.getSequenceLength()+"");
					return;
				}
				observable.setSequence(Sequence.PolymerLengthGreater, value);
			}		
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
//			System.out.println("click! " + e.getSource());
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
//			super.mouseExited(e);
			if(e.getSource() == annotationTextArea){
				changeFreeTextAnnotation();
			}
		}
		@Override
		public void valueChanged(TreeSelectionEvent e) {
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
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeFreeTextAnnotation();
			}
		}
	}
	
	private JTree observableTree = null;
	private ObservableTreeModel observableTreeModel = null;
	private RbmObservable observable;
	JScrollPane scrollPane;
	private JTextArea annotationTextArea;
	private JButton addSpeciesButton = null;
	
	private JRadioButton sequenceMultimolecularButton;
	private JRadioButton sequencePolimerEqualButton;
	private JRadioButton sequencePolimerGreaterButton;
	private JTextField lengthEqualTextField;
	private JTextField lengthGreaterTextField;

	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	LargeShapePanel shapePanel;
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);	// between shape and annotation

	List<SpeciesPatternLargeShape> spsList = new ArrayList<SpeciesPatternLargeShape>();

	private JPopupMenu popupFromShapeMenu;
	private JMenu addFromShapeMenu;
	private JMenuItem deleteFromShapeMenuItem;	
	private JMenuItem renameFromShapeMenuItem;
	private JMenuItem editFromShapeMenuItem;
	private JMenuItem addSpeciesPatternFromShapeMenuItem;
//	private JCheckBox showDetailsCheckBox;
	
	private BioModel bioModel;
	public ObservablePropertiesPanel() {
		super();
		initialize();
	}

	
	public void addSpeciesPattern() {
		SpeciesPattern sp = new SpeciesPattern();
		observable.addSpeciesPattern(sp);
		final TreePath path = observableTreeModel.findObjectPath(null, sp);
		observableTree.setSelectionPath(path);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {				
				observableTree.scrollPathToVisible(path);
				int h = SpeciesPatternLargeShape.defaultHeight;
				Rectangle r = new Rectangle(0, observable.getSpeciesPatternList().size()*h, 20, h);
				scrollPane.getViewport().scrollRectToVisible(r);	// scroll up to the newly added SP
			}
		});
	}
	
	public void addNewFromShape() {
		
	}
	public void deleteFromShape() {

	}

	private JRadioButton getSequenceMultimolecularButton() {
		if (sequenceMultimolecularButton == null) {
			sequenceMultimolecularButton = new JRadioButton("Multimolecular");
			sequenceMultimolecularButton.addActionListener(eventHandler);
		}
		return sequenceMultimolecularButton;
	}
	private JRadioButton getSequencePolimerEqualButton() {
		if (sequencePolimerEqualButton == null) {
			sequencePolimerEqualButton = new JRadioButton("length = ");
			sequencePolimerEqualButton.addActionListener(eventHandler);
		}
		return sequencePolimerEqualButton;
	}
	private JRadioButton getSequencePolimerGreaterButton() {
		if (sequencePolimerGreaterButton == null) {
			sequencePolimerGreaterButton = new JRadioButton("length > ");
			sequencePolimerGreaterButton.addActionListener(eventHandler);
		}
		return sequencePolimerGreaterButton;
	}
	private JTextField getLengthEqualTextField() {
		if (lengthEqualTextField == null) {
			lengthEqualTextField = new JTextField();
			if(observable == null) {
				lengthEqualTextField.setText(""+RbmObservable.DefaultLengthEqual);
			} else {
				lengthEqualTextField.setText(observable.getSequenceLength(RbmObservable.Sequence.PolymerLengthEqual)+"");
			}
			lengthEqualTextField.addActionListener(eventHandler);
			lengthEqualTextField.addKeyListener(eventHandler);
		}
		return lengthEqualTextField;
	}
	private JTextField getLengthGreaterTextField() {
		if (lengthGreaterTextField == null) {
			lengthGreaterTextField = new JTextField();
			if(observable == null) {
				lengthGreaterTextField.setText(""+RbmObservable.DefaultLengthGreater);
			} else {
				lengthGreaterTextField.setText(observable.getSequenceLength(RbmObservable.Sequence.PolymerLengthGreater)+"");
			}
			lengthGreaterTextField.addActionListener(eventHandler);
			lengthGreaterTextField.addKeyListener(eventHandler);
		}
		return lengthGreaterTextField;
	}
	
	void updateSequence() {
		if(observable == null) {
			getSequenceMultimolecularButton().setSelected(true);
			getLengthEqualTextField().setText(""+RbmObservable.DefaultLengthEqual);
			getLengthGreaterTextField().setText(""+RbmObservable.DefaultLengthGreater);
			getLengthEqualTextField().setEnabled(false);
			getLengthGreaterTextField().setEnabled(false);
		} else {
			Sequence sequence = observable.getSequence();
			switch(sequence) {
			case Multimolecular:
				getSequenceMultimolecularButton().setSelected(true);
				getLengthEqualTextField().setEnabled(false);
				getLengthGreaterTextField().setEnabled(false);
				break;
			case PolymerLengthEqual:
				getSequencePolimerEqualButton().setSelected(true);
				getLengthEqualTextField().setEnabled(true);
				getLengthGreaterTextField().setEnabled(false);
				break;
			case PolymerLengthGreater:
				getSequencePolimerGreaterButton().setSelected(true);
				getLengthEqualTextField().setEnabled(false);
				getLengthGreaterTextField().setEnabled(true);
				break;
			}
			getLengthEqualTextField().setText(""+observable.getSequenceLength(Sequence.PolymerLengthEqual));
			getLengthGreaterTextField().setText(""+observable.getSequenceLength(Sequence.PolymerLengthGreater));
		}
	}

	private void initialize() {
		
		observableTree = new BioModelNodeEditableTree();
		observableTreeModel = new ObservableTreeModel(observableTree);
		observableTree.setModel(observableTreeModel);
		
		setLayout(new GridBagLayout());
		
// --------------------------------------------------------------------------------------------------------	
		
		splitPaneHorizontal.setOneTouchExpandable(true);
		splitPaneHorizontal.setDividerLocation(120);
		splitPaneHorizontal.setResizeWeight(0.1);
		
		Border border = BorderFactory.createLineBorder(Color.gray);
		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder annotationBorder = BorderFactory.createTitledBorder(loweredEtchedBorder, " Annotation ");
		annotationBorder.setTitleJustification(TitledBorder.LEFT);
		annotationBorder.setTitlePosition(TitledBorder.TOP);
		annotationBorder.setTitleFont(getFont().deriveFont(Font.BOLD));
		
		shapePanel = new LargeShapePanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(SpeciesPatternLargeShape sps : spsList) {
					if(sps == null) {
						continue;
					}
					sps.paintSelf(g);
				}
			}
		};
		shapePanel.setBorder(border);
		shapePanel.setBackground(Color.white);
		shapePanel.setLayout(null);
		
		shapePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(e.getButton() == 1) {					// left click selects the object (we highlight it)
					Point whereClicked = e.getPoint();
					PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
					manageMouseActivity(locationContext);
				} else if(e.getButton() == 3) {				// right click invokes popup menu (only if the object is highlighted)
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
				for (SpeciesPatternLargeShape sps : spsList) {
					sps.turnHighlightOffRecursive(g);
				}
				for (SpeciesPatternLargeShape sps : spsList) {
					if (sps.contains(locationContext)) {		//check if mouse is inside shape
						break;
					}
				}
				locationContext.highlightDeepestShape();
				locationContext.paintDeepestShape(g);
			}
		});
//		shapePanel.addMouseListener(eventHandler);		// alternately use this
		shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point overWhat = e.getPoint();
				PointLocationInShapeContext locationContext = new PointLocationInShapeContext(overWhat);
				for (SpeciesPatternLargeShape sps : spsList) {
					if (sps.contains(locationContext)) {
						break;
					}
				}
				HighlightableShapeInterface hsi = locationContext.getDeepestShape();
				if(hsi == null) {
					shapePanel.setToolTipText(null);
				} else {
					shapePanel.setToolTipText("Right click for " + hsi.getDisplayType() + " menus");
				}
			} 
		});
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(112, 200));		// gray options panel
		optionsPanel.setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.insets = new Insets(4,4,2,4);			// top, left bottom, right
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		optionsPanel.add(getAddSpeciesButton(), gbc);

		
		ButtonGroup bg = new ButtonGroup();
		bg.add(getSequenceMultimolecularButton());
		bg.add(getSequencePolimerEqualButton());
		bg.add(getSequencePolimerGreaterButton());

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(8,4,2,4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.add(getSequenceMultimolecularButton(), gbc);

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,10,2,4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.add(new JLabel("Polymer of"), gbc);

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2,4,1,7);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.add(getSequencePolimerEqualButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2,4,1,7);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.add(getLengthEqualTextField(), gbc);

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(1,4,4,7);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.add(getSequencePolimerGreaterButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(1,4,4,7);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.add(getLengthGreaterTextField(), gbc);

		// --------------------------------------------------------------------------------
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		optionsPanel.add(new JLabel(""), gbc);

// -----------------------------------------------------------------------------------------		
		JPanel generalPanel = new JPanel();		// right bottom panel, contains just the annotation
		generalPanel.setBorder(annotationBorder);
		generalPanel.setLayout(new GridBagLayout());

		gridy = 0;
		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setFont(new Font("monospaced", Font.PLAIN, 11));
		annotationTextArea.setEditable(false);
		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.1;
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		generalPanel.add(jsp, gbc);

		scrollPane = new JScrollPane(shapePanel);	// where we display the shapes
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel containerOfScrollPanel = new JPanel();
		containerOfScrollPanel.setLayout(new BorderLayout());
		containerOfScrollPanel.add(optionsPanel, BorderLayout.WEST);
		containerOfScrollPanel.add(scrollPane, BorderLayout.CENTER);

		splitPaneHorizontal.setTopComponent(containerOfScrollPanel);
		splitPaneHorizontal.setBottomComponent(generalPanel);
		splitPaneHorizontal.setResizeWeight(0.9d);
		splitPaneHorizontal.setDividerLocation(0.8d);
		
		setName("ObservablePropertiesPanel");
		setLayout(new BorderLayout());
		add(splitPaneHorizontal, BorderLayout.CENTER);
		setBackground(Color.white);
		
		annotationTextArea.addFocusListener(eventHandler);
		annotationTextArea.addMouseListener(eventHandler);
	}
	
	private JButton getAddSpeciesButton() {
		if (addSpeciesButton == null) {
			addSpeciesButton = new JButton("Add Pattern");
			addSpeciesButton.addActionListener(eventHandler);
		}
		return addSpeciesButton;
	}
	
	private JMenu getAddFromShapeMenu() {
		if (addFromShapeMenu == null) {
			addFromShapeMenu = new JMenu("Add");
			addFromShapeMenu.addActionListener(eventHandler);
		}
		return addFromShapeMenu;
	}
	
	private JMenuItem getRenameFromShapeMenuItem() {
		if (renameFromShapeMenuItem == null) {
			renameFromShapeMenuItem = new JMenuItem("Rename");
			renameFromShapeMenuItem.addActionListener(eventHandler);
		}
		return renameFromShapeMenuItem;
	}
	
	private JMenuItem getDeleteFromShapeMenuItem() {
		if (deleteFromShapeMenuItem == null) {
			deleteFromShapeMenuItem = new JMenuItem("Delete");
			deleteFromShapeMenuItem.addActionListener(eventHandler);
		}
		return deleteFromShapeMenuItem;
	}
	
	private JMenuItem getEditFromShapeMenuItem() {
		if (editFromShapeMenuItem == null) {
			editFromShapeMenuItem = new JMenuItem("Edit");
			editFromShapeMenuItem.addActionListener(eventHandler);
		}
		return editFromShapeMenuItem;
	}
	
	private JMenuItem getAddSpeciesPatternFromShapeMenuItem() {
		if (addSpeciesPatternFromShapeMenuItem == null) {
			addSpeciesPatternFromShapeMenuItem = new JMenuItem("Add Species Pattern");
			addSpeciesPatternFromShapeMenuItem.addActionListener(eventHandler);
		}
		return addSpeciesPatternFromShapeMenuItem;
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		RbmObservable observable = null;
		if (selectedObjects.length == 1 && selectedObjects[0] instanceof RbmObservable) {
			observable = (RbmObservable) selectedObjects[0];
		}
		setObservable(observable);
		updateSequence();
	}
	
	private void setObservable(RbmObservable newValue) {
		if (observable == newValue) {
			return;
		}
		RbmObservable oldValue = observable;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
		}
		observable = newValue;
		observableTreeModel.setObservable(observable);
		if(observable != null) {
			shapePanel.setHighlightedRecursively(observable, LargeShapePanel.Highlight.off);
		}
		updateInterface();
		scrollPane.getViewport().setViewPosition(new Point(0,0));	// scroll to top to show the first species pattern of the observable
	}
	
	private void updateInterface() {
		boolean bNonNullObservable = observable != null && bioModel != null;
		annotationTextArea.setEditable(bNonNullObservable);
		if (bNonNullObservable) {
			VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
			annotationTextArea.setText(vcMetaData.getFreeTextAnnotation(observable));
		} else {
			annotationTextArea.setText(null);
		}
		updateShape();
	}

//	public static final int ReservedSpaceForNameOnYAxis = 20;	// enough to write some text above the shape
	public static final int xOffsetInitial = 25;
	public static final int ReservedSpaceForNameOnYAxis = 2;	// just a little empty spacing above the shape
	private void updateShape() {
		spsList.clear();
		int maxXOffset = xOffsetInitial;
		int maxYOffset = 88 + SpeciesPatternLargeShape.defaultHeight;
		if(observable != null && observable.getSpeciesPatternList() != null && observable.getSpeciesPatternList().size() > 0) {
			for(int i = 0; i<observable.getSpeciesPatternList().size(); i++) {
				SpeciesPattern sp = observable.getSpeciesPatternList().get(i);
				SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffsetInitial, 8+(SpeciesPatternLargeShape.defaultHeight+ReservedSpaceForNameOnYAxis)*i, 80, sp, shapePanel, observable);
				spsList.add(sps);
				int xOffset = sps.getRightEnd();
				maxXOffset = Math.max(maxXOffset, xOffset);
			}
			maxYOffset = Math.max(maxYOffset,8+(80+ReservedSpaceForNameOnYAxis)*observable.getSpeciesPatternList().size() + SpeciesPatternLargeShape.defaultHeight);
		}
		Dimension preferredSize = new Dimension(maxXOffset+200, maxYOffset);
		shapePanel.setPreferredSize(preferredSize);

		splitPaneHorizontal.repaint();
	}
	private void changeFreeTextAnnotation() {
		try{
			if (observable == null) {
				return;
			}
			// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
			if(bioModel.getModel() != null && bioModel.getModel().getVcMetaData() != null){
				VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
				String textAreaStr = (annotationTextArea.getText() == null || annotationTextArea.getText().length()==0?null:annotationTextArea.getText());
				if(!Compare.isEqualOrNull(vcMetaData.getFreeTextAnnotation(observable),textAreaStr)){
					vcMetaData.setFreeTextAnnotation(observable, textAreaStr);	
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			PopupGenerator.showErrorDialog(this,"Edit Observable Error\n"+e.getMessage(), e);
		}
	}
	
	private void showPopupMenu(MouseEvent e, PointLocationInShapeContext locationContext) {
		if (popupFromShapeMenu == null) {
			popupFromShapeMenu = new JPopupMenu();			
		}		
		if (popupFromShapeMenu.isShowing()) {
			return;
		}
		boolean bDelete = false;
		boolean bAdd = false;
		boolean bEdit = false;
		boolean bRename = false;
		popupFromShapeMenu.removeAll();
		Point mousePoint = e.getPoint();

		final Object deepestShape = locationContext.getDeepestShape();
		final RbmElementAbstract selectedObject;
		
		if(deepestShape == null) {
			selectedObject = null;
			System.out.println("outside");		// when cursor is outside any species pattern we offer to add a new one
			popupFromShapeMenu.add(getAddSpeciesPatternFromShapeMenuItem());
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
				return;
			}
		} else {
			selectedObject = null;
			System.out.println("inside something else?");
			return;
		}

		if(selectedObject instanceof SpeciesPattern) {
			getAddFromShapeMenu().setText(VCellErrorMessages.AddMolecularTypes);
			getAddFromShapeMenu().removeAll();
			for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
				JMenuItem menuItem = new JMenuItem(mt.getName());
				Graphics gc = splitPaneHorizontal.getGraphics();
				Icon icon = new MolecularTypeSmallShape(4, 4, mt, null, gc, mt, null);
				menuItem.setIcon(icon);
				getAddFromShapeMenu().add(menuItem);
				menuItem.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
						((SpeciesPattern)selectedObject).addMolecularTypePattern(molecularTypePattern);
						
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {				

							}
						});
					}
				});
			}
			JMenu compartmentMenuItem = new JMenu("Specify structure (for all)");
			compartmentMenuItem.removeAll();
			for (final Structure struct : bioModel.getModel().getStructures()) {
				JMenuItem menuItem = new JMenuItem(struct.getName());
				compartmentMenuItem.add(menuItem);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String nameStruct = e.getActionCommand();
						Structure struct = bioModel.getModel().getStructure(nameStruct);
						observable.setStructure(struct);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								observableTreeModel.populateTree();			// repaint tree
								//observableTree.scrollPathToVisible(path);	// scroll back up to show the observable
							}
						});
					}
				});
			}
			JMenuItem deleteMenuItem = new JMenuItem("Delete Species Pattern");
			deleteMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					observable.getSpeciesPatternList().remove((SpeciesPattern)selectedObject);
					observable.removeSpeciesPattern((SpeciesPattern)selectedObject);
					
					final TreePath path = observableTreeModel.findObjectPath(null, observable);
					observableTree.setSelectionPath(path);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							observableTreeModel.populateTree();			// repaint tree
							observableTree.scrollPathToVisible(path);	// scroll back up to show the observable
						}
					});
				}
			});
			popupFromShapeMenu.add(deleteMenuItem);
			popupFromShapeMenu.add(new JSeparator());
			popupFromShapeMenu.add(getAddFromShapeMenu());
			popupFromShapeMenu.add(compartmentMenuItem);
			
		} else if (selectedObject instanceof MolecularTypePattern) {
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
					observableTreeModel.populateTree();
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
					observableTreeModel.populateTree();
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
				}
			});
			popupFromShapeMenu.add(deleteMenuItem);
			
		} else if (selectedObject instanceof MolecularComponentPattern) {
			manageComponentPatternFromShape(selectedObject, locationContext, ShowWhat.ShowBond);
			bDelete = false;
			
		} else if (selectedObject instanceof ComponentStatePattern) {
			MolecularComponentPattern mcp = ((ComponentStateLargeShape)deepestShape).getMolecularComponentPattern();
			manageComponentPatternFromShape(mcp, locationContext, ShowWhat.ShowState);
		}
		if (bRename) {
			popupFromShapeMenu.add(getRenameFromShapeMenuItem());
		}
		if (bDelete) {
			popupFromShapeMenu.add(getDeleteFromShapeMenuItem());
		}
		if (bEdit) {
			popupFromShapeMenu.add(getEditFromShapeMenuItem());
		}
		if (bAdd) {
			popupFromShapeMenu.add(new JSeparator());
			popupFromShapeMenu.add(getAddFromShapeMenu());
		}
		popupFromShapeMenu.show(e.getComponent(), mousePoint.x, mousePoint.y);
	}
	
	private enum ShowWhat {
		ShowState,
		ShowBond
	}
	public void manageComponentPatternFromShape(final RbmElementAbstract selectedObject, PointLocationInShapeContext locationContext, ShowWhat showWhat) {
		final MolecularComponentPattern mcp = (MolecularComponentPattern)selectedObject;
		final MolecularComponent mc = mcp.getMolecularComponent();
		popupFromShapeMenu.removeAll();
		// ------------------------------------------------------------------- State
		if(showWhat == ShowWhat.ShowState && mc.getComponentStateDefinitions().size() != 0) {
			String prefix = "State:  ";
			String csdCurrentName = "";
			final Map<String, String> itemMap = new LinkedHashMap<String, String>();
			if(mcp.getComponentStatePattern() == null || mcp.getComponentStatePattern().isAny()) {
				csdCurrentName = "<html>" + prefix + "<b>" + ComponentStatePattern.strAny + "</b></html>";
			} else {
				csdCurrentName = "<html>" + prefix + ComponentStatePattern.strAny + "</html>";
			}
			itemMap.put(csdCurrentName, ComponentStatePattern.strAny);
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
		// ------------------------------------------------------------------------------------------- Bonds
		final MolecularTypePattern mtp = locationContext.getMolecularTypePattern();
		final SpeciesPattern sp = locationContext.getSpeciesPattern();
		
		JMenu editBondMenu = new JMenu();
		final String specifiedString = mcp.getBondType() == BondType.Specified ? "<html><b>" + "Site bond specified" + "</b></html>" : "<html>" + "Site bond specified" + "</html>";
		editBondMenu.setText(specifiedString);
		editBondMenu.setToolTipText("Specified");
		editBondMenu.removeAll();
		final Map<String, Bond> itemMap = new LinkedHashMap<String, Bond>();
		
//		String noneString = "<html>Bond:&nbsp;&nbsp;<b>" + BondType.None.symbol + "</b> " + BondType.None.name() + "</html>";
//		String existsString = "<html>Bond:&nbsp;&nbsp;<b>" + BondType.Exists.symbol + "</b> " + BondType.Exists.name() + "</html>";
//		String possibleString = "<html>Bond:&nbsp;&nbsp;<b>" + BondType.Possible.symbol + "</b> " + BondType.Possible.name() + "</html>";
		String noneString = mcp.getBondType() == BondType.None ? "<html><b>" + "Site is unbound" + "</b></html>" : "<html>" + "Site is unbound" + "</html>";
		String existsString = mcp.getBondType() == BondType.Exists ? "<html><b>" + "Site has external bond" + "</b></html>" : "<html>" + "Site has external bond" + "</html>";	// Site is bound
		String possibleString = mcp.getBondType() == BondType.Possible ? "<html><b>" + "Site may be bound" + "</b></html>" : "<html>" + "Site may be bound" + "</html>";
		itemMap.put(noneString, null);
		itemMap.put(existsString, null);
		itemMap.put(possibleString, null);
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
//				itemMap.put(b.toHtmlStringLong(mtp, mc, sp, index), b);
				itemMap.put(b.toHtmlStringLong(sp, mtp, mc, index), b);
//				itemMap.put(b.toHtmlStringLong(sp, index), b);
			}
		}
		int index = 0;
		Graphics gc = splitPaneHorizontal.getGraphics();
		for(String name : itemMap.keySet()) {
			JMenuItem menuItem = new JMenuItem(name);
			if(index == 0) {
				menuItem.setIcon(VCellIcons.rbmBondNoneIcon);
				menuItem.setToolTipText("None");
				popupFromShapeMenu.add(menuItem);
			} else if(index == 1) {
				menuItem.setIcon(VCellIcons.rbmBondExistsIcon);
				menuItem.setToolTipText("Exists");
				popupFromShapeMenu.add(menuItem);
			} else if(index == 2) {
				menuItem.setIcon(VCellIcons.rbmBondPossibleIcon);
				menuItem.setToolTipText("Possible");
				popupFromShapeMenu.add(menuItem);
			} else if(index > 2) {
				Bond b = itemMap.get(name);
				SpeciesPattern spBond = new SpeciesPattern(sp);		// clone of the sp, with only the bond of interest
				spBond.resetBonds();
				spBond.resetStates();
				MolecularTypePattern mtpFrom = spBond.getMolecularTypePattern(mtp.getMolecularType().getName(), mtp.getIndex());
				MolecularComponentPattern mcpFrom = mtpFrom.getMolecularComponentPattern(mc);
				MolecularTypePattern mtpTo = spBond.getMolecularTypePattern(b.molecularTypePattern.getMolecularType().getName(), b.molecularTypePattern.getIndex());
				MolecularComponentPattern mcpTo = mtpTo.getMolecularComponentPattern(b.molecularComponentPattern.getMolecularComponent());
				spBond.setBond(mtpTo, mcpTo, mtpFrom, mcpFrom);
				Icon icon = new SpeciesPatternSmallShape(3,4, spBond, gc, observable, false);
				((SpeciesPatternSmallShape)icon).setDisplayRequirements(DisplayRequirements.highlightBonds);
				menuItem.setIcon(icon);
				editBondMenu.add(menuItem);
//			} else {
//				if(index == 0) {
//					menuItem.setForeground(Color.blue);
//				}
//				popupFromShapeMenu.add(menuItem);
			}
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = e.getActionCommand();
					BondType btBefore = mcp.getBondType();
					if(name.equals(noneString)) {
						if(btBefore == BondType.Specified) {	// specified -> not specified
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.None);
						mcp.setBond(null);
						SwingUtilities.invokeLater(new Runnable() { public void run() { observableTreeModel.populateTree(); } });
					} else if(name.equals(existsString)) {
						if(btBefore == BondType.Specified) {	// specified -> exists
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Exists);
						mcp.setBond(null);
						SwingUtilities.invokeLater(new Runnable() { public void run() { observableTreeModel.populateTree(); } });
					} else if(name.equals(possibleString)) {
						if(btBefore == BondType.Specified) {	// specified -> possible
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Possible);
						mcp.setBond(null);
						SwingUtilities.invokeLater(new Runnable() { public void run() { observableTreeModel.populateTree(); } });
					} else {
						if (btBefore != BondType.Specified) {
							// if we go from a non-specified to a specified we need to find the next available
							// bond id, so that we can choose the color for displaying the bond
							// a bad bond id, like -1, will crash badly when trying to choose the color
							int bondId = sp.nextBondId();
							mcp.setBondId(bondId);
						} else {
							// specified -> specified
							// change the old partner to possible, continue using the bond id
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Specified);
						Bond b = itemMap.get(name);
						mcp.setBond(b);
						mcp.getBond().molecularComponentPattern.setBondId(mcp.getBondId());
						sp.resolveBonds();
						SwingUtilities.invokeLater(new Runnable() { public void run() { observableTreeModel.populateTree(); } });
					}
				}
			});
			index ++;
		}
		popupFromShapeMenu.add(editBondMenu);
	}
	
	public void setBioModel(BioModel newValue) {
		bioModel = newValue;
		observableTreeModel.setBioModel(bioModel);
	}
}
