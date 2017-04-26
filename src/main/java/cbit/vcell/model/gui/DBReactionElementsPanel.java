/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;
import cbit.vcell.model.ReactionDescription;
/**
 * Insert the type's description here.
 * Creation date: (4/22/2003 12:54:11 PM)
 * @author: Frank Morgan
 */
public class DBReactionElementsPanel extends javax.swing.JPanel {

	//
	static class RCJLabel extends javax.swing.JLabel{
		//
		private char type;
		private cbit.vcell.model.SpeciesDescription dbsd = null;
		private int stoich;
		private boolean bResolved = false;
		private boolean bSelectable = true;
		private Boolean bFluxResolvedInside;
		//
		public RCJLabel(cbit.vcell.model.SpeciesDescription argdbsd,int argStoich,char argType){
			super();
			if(	argType != ReactionDescription.RX_ELEMENT_REACTANT &&
				argType != ReactionDescription.RX_ELEMENT_PRODUCT && 
				argType != ReactionDescription.RX_ELEMENT_CATALYST &&
				argType != ReactionDescription.RX_ELEMENT_FLUX){
				throw new IllegalArgumentException("Unknown type="+argType);
			}
			String text = argdbsd.getPreferredName();
			if(text.length() > 21){
				text = text.substring(0,9)+"..."+text.substring(text.length()-9);
			}
			//if(argType == ReactionDescription.RX_ELEMENT_CATALYST){
				//text = "<= "+text+" =>";
			//}
			if(argStoich > 1){
				text = argStoich+" "+text;
			}
			setText(text);
			//(argType == ReactionDescription.RX_ELEMENT_CATALYST?"<="+argdbfs.getFormalSpeciesInfo().getPreferredName()+"=>":(argStoich>1?argStoich+" ":"")+argdbfs.getFormalSpeciesInfo().getPreferredName())
			this.type = argType;
			this.dbsd = argdbsd;
			this.stoich = argStoich;
			this.setOpaque(true);//So highlight background works
			this.setBorder(unhighlightOutline);
			this.setForeground(UNHIGHLITED_FOREGROUND_COLOR);
		}
		public char getType(){
			return type;
		}
		public int getStoich(){
			return stoich;
		}
		public cbit.vcell.model.SpeciesDescription getSpeciesDescription(){
			return dbsd;
		}
		public void setResolved(boolean argResolved){
			bResolved = argResolved;
			if(bResolved){
				this.setForeground(RESOLVED_FOREGROUND_COLOR);
			}else{
				this.setForeground(UNHIGHLITED_FOREGROUND_COLOR);
			}
		}
		public boolean getResolved(){
			return bResolved;
		}
		public void setSelectable(boolean argSelectable){
			bSelectable = argSelectable;
		}
		public boolean isSelectable(){
			return bSelectable;
		}
		public void setFluxResolvedInside(Boolean argFRI){
			bFluxResolvedInside = argFRI;
		}
		public Boolean isFluxResolvedInside(){
			return bFluxResolvedInside;
		}
	}

class IvjEventHandler implements java.awt.event.ContainerListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener {
		public void componentAdded(java.awt.event.ContainerEvent e) {
			if (e.getSource() == DBReactionElementsPanel.this.getReactionElementJPanel()) 
				connEtoM1(e);
		};
		public void componentRemoved(java.awt.event.ContainerEvent e) {
			if (e.getSource() == DBReactionElementsPanel.this.getReactionElementJPanel()) 
				connEtoM2(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseDragged(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {
			if (e.getSource() == DBReactionElementsPanel.this.getReactionElementJPanel()) 
				connEtoC2(e);
		};
		public void mouseMoved(java.awt.event.MouseEvent e) {
			if (e.getSource() == DBReactionElementsPanel.this.getReactionElementJPanel()) 
				connEtoC1(e);
		};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == DBReactionElementsPanel.this.getReactionElementJPanel()) 
				connEtoC5(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DBReactionElementsPanel.this && (evt.getPropertyName().equals("selection"))) 
				connEtoC3(evt);
			if (evt.getSource() == DBReactionElementsPanel.this && (evt.getPropertyName().equals("highlight"))) 
				connEtoC4(evt);
		};
	}
	//
	public static final java.awt.Color UNHIGHLITED_FOREGROUND_COLOR = java.awt.Color.black;
	public static final java.awt.Color RESOLVED_FOREGROUND_COLOR = java.awt.Color.red;
	//
	private static final javax.swing.border.LineBorder highlightOutline = new javax.swing.border.LineBorder(java.awt.Color.black);
	private static final javax.swing.border.EmptyBorder unhighlightOutline = new javax.swing.border.EmptyBorder(1,1,1,1);
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjProductJLabel = null;
	private javax.swing.JLabel ivjReactantJLabel = null;
	private RCJLabel fieldSelection = null;
	private RCJLabel fieldHighlight = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JPanel ivjReactionElementJPanel = null;
	private javax.swing.JLabel ivjCatalystJLabel = null;
	private javax.swing.JLabel ivjBeginEnzymeMarkerJLabel = null;
	private javax.swing.JLabel ivjEndEnzymeMarkerJLabel = null;

/**
 * DBReactionElementsPanel constructor comment.
 */
public DBReactionElementsPanel() {
	super();
	initialize();
}


/**
 * DBReactionElementsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public DBReactionElementsPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * DBReactionElementsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public DBReactionElementsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * DBReactionElementsPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public DBReactionElementsPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2003 2:45:20 PM)
 * @param enzymeRCJL java.lang.String
 */
private void addCatalystComponent(javax.swing.JComponent catalystComponent) {

	if(!(catalystComponent == getCatalystJLabel()) && !(catalystComponent instanceof RCJLabel && ((RCJLabel)catalystComponent).getType() == ReactionDescription.RX_ELEMENT_CATALYST)){
		throw new IllegalArgumentException("Must be enzyme reaction element component");
	}
	getReactionElementJPanel().remove(getCatalystJLabel());
	int transition = 0;
	for(int i = 0 ; i < getReactionElementJPanel().getComponentCount();i+= 1){
		java.awt.Component comp = getReactionElementJPanel().getComponent(i);
		if(comp == getEndEnzymeMarkerJLabel()){
			transition = i;
			break;
		}else if(comp instanceof RCJLabel){
			RCJLabel rcjlComp = (RCJLabel)comp;
			if(rcjlComp.getType() == ReactionDescription.RX_ELEMENT_CATALYST){
				getReactionElementJPanel().add(createCommaJLabel(),i);
				transition = i;
				break;
			}
		}
	}
	getReactionElementJPanel().add(catalystComponent,transition);
		
}


/**
 * Insert the method's description here.
 * Creation date: (7/13/2003 12:48:53 PM)
 */
public void changeCatalystDefaultText(String newText) {

	getCatalystJLabel().setText(newText);
}


/**
 * Insert the method's description here.
 * Creation date: (7/13/2003 12:48:53 PM)
 */
public void changeProductDefaultText(String newText) {

	getProductJLabel().setText(newText);
}


/**
 * Insert the method's description here.
 * Creation date: (7/13/2003 12:48:53 PM)
 */
public void changeReactantDefaultText(String newText) {

	getReactantJLabel().setText(newText);
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 3:47:45 PM)
 */
public void clearReaction() {

	boolean bDeleted;
	do{
		bDeleted = false;
		for(int i = 0 ; i < getReactionElementJPanel().getComponentCount();i+= 1){
			java.awt.Component comp = getReactionElementJPanel().getComponent(i);
			if(comp instanceof RCJLabel){
				reactionElementDelete((RCJLabel)comp);
				bDeleted = true;
				break;
			}
		}
	}while(bDeleted);
}


/**
 * connEtoC1:  (DBReactionElementsPanel.mouse.mouseExited(java.awt.event.MouseEvent) --> DBReactionElementsPanel.highlightRCJL(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.highlight_Event(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (DBReactionElementsPanel.mouse.mousePressed(java.awt.event.MouseEvent) --> DBReactionElementsPanel.select_Event(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.highlight_Event(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (DBReactionElementsPanel.selection --> DBReactionElementsPanel.selectRCJL(Lcbit.vcell.model.gui.DBReactionElementsPanel$RCJLabel;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectRCJL();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (DBReactionElementsPanel.highlight --> DBReactionElementsPanel.highlightRCJL()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.highlightRCJL();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (DBReactionElementsPanel.mouse.mousePressed(java.awt.event.MouseEvent) --> DBReactionElementsPanel.select_Event(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.select_Event(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (DBReactionElementsPanel.mouseMotion.mouseMoved(java.awt.event.MouseEvent) --> DBReactionElementsPanel.highlightRCJL(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.highlight_Event(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (ReactionElementJPanel.container.componentAdded(java.awt.event.ContainerEvent) --> DBReactionElementsPanel.revalidate()V)
 * @param arg1 java.awt.event.ContainerEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ContainerEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.revalidate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (ReactionElementJPanel.container.componentRemoved(java.awt.event.ContainerEvent) --> DBReactionElementsPanel.revalidate()V)
 * @param arg1 java.awt.event.ContainerEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.awt.event.ContainerEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.revalidate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2003 3:39:22 PM)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel createCommaJLabel() {
	return new javax.swing.JLabel(",");
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2003 3:39:22 PM)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel createPlusJLabel() {
	return new javax.swing.JLabel("+");
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBeginEnzymeMarkerJLabel() {
	if (ivjBeginEnzymeMarkerJLabel == null) {
		try {
			ivjBeginEnzymeMarkerJLabel = new javax.swing.JLabel();
			ivjBeginEnzymeMarkerJLabel.setName("BeginEnzymeMarkerJLabel");
			ivjBeginEnzymeMarkerJLabel.setText("<=");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBeginEnzymeMarkerJLabel;
}


/**
 * Return the MiddleJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCatalystJLabel() {
	if (ivjCatalystJLabel == null) {
		try {
			ivjCatalystJLabel = new javax.swing.JLabel();
			ivjCatalystJLabel.setName("CatalystJLabel");
			ivjCatalystJLabel.setText("Any Enzyme");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCatalystJLabel;
}


/**
 * Return the JLabel11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEndEnzymeMarkerJLabel() {
	if (ivjEndEnzymeMarkerJLabel == null) {
		try {
			ivjEndEnzymeMarkerJLabel = new javax.swing.JLabel();
			ivjEndEnzymeMarkerJLabel.setName("EndEnzymeMarkerJLabel");
			ivjEndEnzymeMarkerJLabel.setText("=>");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndEnzymeMarkerJLabel;
}


/**
 * Gets the highlight property (java.lang.String) value.
 * @return The highlight property value.
 * @see #setHighlight
 */
public RCJLabel getHighlight() {
	return fieldHighlight;
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
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getReactionElementJPanel());
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
 * Return the ProductJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getProductJLabel() {
	if (ivjProductJLabel == null) {
		try {
			ivjProductJLabel = new javax.swing.JLabel();
			ivjProductJLabel.setName("ProductJLabel");
			ivjProductJLabel.setText("Any Product");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProductJLabel;
}


/**
 * Return the ReactantJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getReactantJLabel() {
	if (ivjReactantJLabel == null) {
		try {
			ivjReactantJLabel = new javax.swing.JLabel();
			ivjReactantJLabel.setName("ReactantJLabel");
			ivjReactantJLabel.setText("Any Reactant");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactantJLabel;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getReactionElementJPanel() {
	if (ivjReactionElementJPanel == null) {
		try {
			ivjReactionElementJPanel = new javax.swing.JPanel();
			ivjReactionElementJPanel.setName("ReactionElementJPanel");
			ivjReactionElementJPanel.setLayout(new java.awt.FlowLayout());
			ivjReactionElementJPanel.setLocation(0, 0);
			getReactionElementJPanel().add(getReactantJLabel(), getReactantJLabel().getName());
			getReactionElementJPanel().add(getBeginEnzymeMarkerJLabel(), getBeginEnzymeMarkerJLabel().getName());
			getReactionElementJPanel().add(getCatalystJLabel(), getCatalystJLabel().getName());
			getReactionElementJPanel().add(getEndEnzymeMarkerJLabel(), getEndEnzymeMarkerJLabel().getName());
			getReactionElementJPanel().add(getProductJLabel(), getProductJLabel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionElementJPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 12:08:09 PM)
 * @param i int
 */
public java.util.Hashtable getReactionElements() {

	
	java.util.Hashtable rElements = new java.util.Hashtable();
		
	for(int i = 0; i < getReactionElementJPanel().getComponentCount();i+= 1){
		if(getReactionElementJPanel().getComponent(i) instanceof RCJLabel){
			RCJLabel comp = (RCJLabel)getReactionElementJPanel().getComponent(i);
			rElements.put(comp.getSpeciesDescription(),new Character(comp.getType()));
		}
	}
	
	return rElements;
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2003 5:14:32 PM)
 * @return int
 */
public int getReactionElementsCount() {
	int count = 0;
	for (int i = 0;i < getReactionElementJPanel().getComponentCount();i+= 1){
		if(getReactionElementJPanel().getComponent(i) instanceof RCJLabel){
			count+= 1;
		}
	}
	return count;
}


/**
 * Gets the selection property (javax.swing.JLabel) value.
 * @return The selection property value.
 * @see #setSelection
 */
public RCJLabel getSelection() {
	return fieldSelection;
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
private void highlight_Event(java.awt.event.MouseEvent mouseEvent) {
	//
	RCJLabel highlighted = null;
	if(mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_MOVED){
		java.awt.Component comp = javax.swing.SwingUtilities.getDeepestComponentAt(this,mouseEvent.getX(), mouseEvent.getY());
		if(comp instanceof RCJLabel){
			highlighted = (RCJLabel)comp;
			if(getSelection() != null && getSelection() != comp){
				setSelection(null);
			}
		}
	}
	setHighlight(highlighted);
}


/**
 * Comment
 */
private void highlightRCJL() {
	//
	for(int i = 0 ; i < getReactionElementJPanel().getComponentCount();i+= 1){
		((javax.swing.JComponent)getReactionElementJPanel().getComponent(i)).setBorder(unhighlightOutline);
	}
	if(getHighlight() != null){
		getHighlight().setBorder(highlightOutline);
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
	this.addPropertyChangeListener(ivjEventHandler);
	getReactionElementJPanel().addMouseListener(ivjEventHandler);
	getReactionElementJPanel().addMouseMotionListener(ivjEventHandler);
	getReactionElementJPanel().addContainerListener(ivjEventHandler);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DBReactionElementsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(364, 62);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);
		initConnections();
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
		DBReactionElementsPanel aDBReactionElementsPanel;
		aDBReactionElementsPanel = new DBReactionElementsPanel();
		frame.setContentPane(aDBReactionElementsPanel);
		frame.setSize(aDBReactionElementsPanel.getSize());
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
 * Creation date: (7/13/2003 12:27:43 PM)
 * @param rcjl java.lang.Object
 * @param bResolved boolean
 */
public void markResolved(cbit.vcell.model.SpeciesDescription dbfs, boolean bResolved) {
	//cbit.vcell.dictionary.SpeciesDescription dbfs = rcjl.getSpeciesDescription();
	//do this because fluxes are special
	for(int i = 0; i < getReactionElementJPanel().getComponentCount();i+= 1){
		if(getReactionElementJPanel().getComponent(i) instanceof RCJLabel){
			RCJLabel comp = (RCJLabel)getReactionElementJPanel().getComponent(i);
			if(comp.getSpeciesDescription().equals(dbfs)){
				comp.setResolved(bResolved);
			}
		}
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (7/13/2003 12:27:43 PM)
 * @param rcjl java.lang.Object
 * @param bResolved boolean
 */
public void markSelectable(cbit.vcell.model.SpeciesDescription dbfs, boolean bSelectable) {
	//cbit.vcell.dictionary.SpeciesDescription dbfs = rcjl.getSpeciesDescription();
	//do this because fluxes are special
	for(int i = 0; i < getReactionElementJPanel().getComponentCount();i+= 1){
		if(getReactionElementJPanel().getComponent(i) instanceof RCJLabel){
			RCJLabel comp = (RCJLabel)getReactionElementJPanel().getComponent(i);
			if(comp.getSpeciesDescription() == dbfs){
				comp.setSelectable(bSelectable);
			}
		}
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2003 4:21:01 PM)
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public void reactionAddCatalyst(cbit.vcell.model.SpeciesDescription dbfs) {

	addCatalystComponent(new RCJLabel(dbfs,0,ReactionDescription.RX_ELEMENT_CATALYST));
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2003 4:21:01 PM)
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public void reactionAddFlux(cbit.vcell.model.SpeciesDescription dbfs,boolean bInside,int stoich) {

	for(int i = 0 ; i < getReactionElementJPanel().getComponentCount();i+= 1){
		java.awt.Component comp = getReactionElementJPanel().getComponent(i);
		if(comp instanceof RCJLabel){
			RCJLabel rcjlComp = (RCJLabel)comp;
			if(rcjlComp.getType() == ReactionDescription.RX_ELEMENT_PRODUCT || rcjlComp.getType() == ReactionDescription.RX_ELEMENT_REACTANT){
				throw new IllegalArgumentException("Can't mix flux and reactant-product types");
			}
		}
	}
	//Remove default Reactant-Product JLabels if any
	if(bInside){
		getReactionElementJPanel().remove(getProductJLabel());
	}
	else{
		getReactionElementJPanel().remove(getReactantJLabel());
	}
	//Remove Flux Jlabels if any exist
	if(bInside && getReactionElementJPanel().getComponent(getReactionElementJPanel().getComponentCount()-1) instanceof RCJLabel && ((RCJLabel)getReactionElementJPanel().getComponent(getReactionElementJPanel().getComponentCount()-1)).getType() == ReactionDescription.RX_ELEMENT_FLUX){
		getReactionElementJPanel().remove(getReactionElementJPanel().getComponent(getReactionElementJPanel().getComponentCount()-1));
	}
	if(!bInside && getReactionElementJPanel().getComponent(0) instanceof RCJLabel && ((RCJLabel)getReactionElementJPanel().getComponent(0)).getType() == ReactionDescription.RX_ELEMENT_FLUX){
		getReactionElementJPanel().remove(getReactionElementJPanel().getComponent(0));
	}
	//
	if(!bInside){
		RCJLabel rcjl = new RCJLabel(dbfs,stoich,ReactionDescription.RX_ELEMENT_FLUX);
		rcjl.setFluxResolvedInside(new Boolean(false));
		getReactionElementJPanel().add(rcjl,0);
	}else{
		RCJLabel rcjl = new RCJLabel(dbfs,stoich,ReactionDescription.RX_ELEMENT_FLUX);
		rcjl.setFluxResolvedInside(new Boolean(true));
		getReactionElementJPanel().add(rcjl,-1);
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2003 4:21:01 PM)
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public void reactionAddProduct(cbit.vcell.model.SpeciesDescription dbfs,int stoich) {

	getReactionElementJPanel().remove(getProductJLabel());
	if(getReactionElementJPanel().getComponent(getReactionElementJPanel().getComponentCount()-1) instanceof RCJLabel && ((RCJLabel)getReactionElementJPanel().getComponent(getReactionElementJPanel().getComponentCount()-1)).getType() == ReactionDescription.RX_ELEMENT_PRODUCT){
		getReactionElementJPanel().add(createPlusJLabel(),-1);
	}
	getReactionElementJPanel().add(new RCJLabel(dbfs,stoich,ReactionDescription.RX_ELEMENT_PRODUCT),-1);
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2003 4:21:01 PM)
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public void reactionAddReactant(cbit.vcell.model.SpeciesDescription dbfs,int stoich) {

	getReactionElementJPanel().remove(getReactantJLabel());
	if(getReactionElementJPanel().getComponent(0) instanceof RCJLabel && ((RCJLabel)getReactionElementJPanel().getComponent(0)).getType() == ReactionDescription.RX_ELEMENT_REACTANT){
		getReactionElementJPanel().add(createPlusJLabel(),0);
	}
	getReactionElementJPanel().add(new RCJLabel(dbfs,stoich,ReactionDescription.RX_ELEMENT_REACTANT),0);
	
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2003 4:21:01 PM)
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public void reactionElementDelete(RCJLabel rcjl) {

	if(getHighlight() == rcjl){
		setHighlight(null);
	}
	if(getSelection() == rcjl){
		setSelection(null);
	}
	//
	if(rcjl.getType() == ReactionDescription.RX_ELEMENT_FLUX){
		getReactionElementJPanel().remove(getReactionElementJPanel().getComponent(getReactionElementJPanel().getComponentCount()-1));
		getReactionElementJPanel().remove(getReactionElementJPanel().getComponent(0));
		getReactionElementJPanel().add(getReactantJLabel(),0);
		getReactionElementJPanel().add(getProductJLabel(),-1);
		return;
	}
	//
	getReactionElementJPanel().remove(rcjl);
	//
	int rCount = 0;
	int eCount = 0;
	int pCount = 0;
	for(int i = 0; i < getReactionElementJPanel().getComponentCount();i+= 1){
		if(getReactionElementJPanel().getComponent(i) instanceof RCJLabel){
			RCJLabel comp = (RCJLabel)getReactionElementJPanel().getComponent(i);
			if(comp.getType() == ReactionDescription.RX_ELEMENT_REACTANT){
				rCount+= 1;
			}else if(comp.getType() == ReactionDescription.RX_ELEMENT_CATALYST){
				eCount+= 1;
			}else if(comp.getType() == ReactionDescription.RX_ELEMENT_PRODUCT){
				pCount+= 1;
			}
		}
	}
	//
	if(rCount == 0){
		getReactionElementJPanel().add(getReactantJLabel(),0);
	}
	if(pCount == 0){
		getReactionElementJPanel().add(getProductJLabel(),-1);
	}
	if(eCount == 0){
		addCatalystComponent(getCatalystJLabel());
	}
	//
	boolean bDeleted;
	do{
		bDeleted = false;
		boolean prevRorP = false;
		for(int i = 0; i < getReactionElementJPanel().getComponentCount();i+= 1){
			javax.swing.JComponent currComp = (javax.swing.JComponent)getReactionElementJPanel().getComponent(i);
			if(getReactionElementJPanel().getComponent(i) instanceof javax.swing.JLabel && ((javax.swing.JLabel)getReactionElementJPanel().getComponent(i)).getText().equals("+")){
				boolean bPlusOK = false;
				if(i < (getReactionElementJPanel().getComponentCount()-1) && prevRorP){
					javax.swing.JComponent nextComp = (javax.swing.JComponent)getReactionElementJPanel().getComponent(i+1);
					bPlusOK = nextComp instanceof RCJLabel && (((RCJLabel)nextComp).getType() == ReactionDescription.RX_ELEMENT_REACTANT || ((RCJLabel)nextComp).getType() == ReactionDescription.RX_ELEMENT_PRODUCT);
				}
				if(!bPlusOK){
					getReactionElementJPanel().remove(i);
				}
			}
			prevRorP = currComp instanceof RCJLabel && ( ((RCJLabel)currComp).getType() == ReactionDescription.RX_ELEMENT_REACTANT || ((RCJLabel)currComp).getType() == ReactionDescription.RX_ELEMENT_PRODUCT);
		}
	}while(bDeleted);
	//
	do{
		bDeleted = false;
		boolean prevCatalyst = false;
		for(int i = 0; i < getReactionElementJPanel().getComponentCount();i+= 1){
			javax.swing.JComponent currComp = (javax.swing.JComponent)getReactionElementJPanel().getComponent(i);
			if(getReactionElementJPanel().getComponent(i) instanceof javax.swing.JLabel && ((javax.swing.JLabel)getReactionElementJPanel().getComponent(i)).getText().equals(",")){
				boolean bCommaOK = false;
				if(i < (getReactionElementJPanel().getComponentCount()-1) && prevCatalyst){
					javax.swing.JComponent nextComp = (javax.swing.JComponent)getReactionElementJPanel().getComponent(i+1);
					bCommaOK = nextComp instanceof RCJLabel && (((RCJLabel)nextComp).getType() == ReactionDescription.RX_ELEMENT_CATALYST);
				}
				if(!bCommaOK){
					getReactionElementJPanel().remove(i);
				}
			}
			prevCatalyst = currComp instanceof RCJLabel && ( ((RCJLabel)currComp).getType() == ReactionDescription.RX_ELEMENT_CATALYST);
		}
	}while(bDeleted);
}


/**
 * Comment
 */
private void select_Event(java.awt.event.MouseEvent mouseEvent) {

	if(mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED){
		if((mouseEvent.getModifiers()&java.awt.event.InputEvent.BUTTON1_MASK) != 0){
			RCJLabel selectedRCJLabel = null;
			java.awt.Component comp = javax.swing.SwingUtilities.getDeepestComponentAt(this,mouseEvent.getX(), mouseEvent.getY());
			if(comp instanceof RCJLabel && ((RCJLabel)comp).isSelectable()){
				selectedRCJLabel = (RCJLabel)comp;
			}
			setSelection(selectedRCJLabel);
		}
	}
}


/**
 * Comment
 */
private void selectRCJL() {

	for(int i = 0 ; i < getReactionElementJPanel().getComponentCount();i+= 1){
		getReactionElementJPanel().getComponent(i).setBackground(getBackground());
	}
	if(getSelection() != null){
		getSelection().setBackground(getBackground().brighter());
	}
}


/**
 * Sets the highlight property (java.lang.String) value.
 * @param highlight The new value for the property.
 * @see #getHighlight
 */
private void setHighlight(RCJLabel highlight) {
	RCJLabel oldValue = fieldHighlight;
	fieldHighlight = highlight;
	if(oldValue == null && fieldHighlight == null){return;}
	firePropertyChange("highlight", oldValue, highlight);
}


/**
 * Sets the selection property (javax.swing.JLabel) value.
 * @param selection The new value for the property.
 * @see #getSelection
 */
private void setSelection(RCJLabel selection) {
	RCJLabel oldValue = fieldSelection;
	fieldSelection = selection;
	firePropertyChange("selection", oldValue, selection);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEDFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BD0D4D716EE56E45D8D15B03BEE9693F7E3B9B8CBA6CCE2EACC8D35F1A64E4459B109DBA1C4A7199419F5B7CE4559900B2D31F6580DDBF9B410C04408A3E65C9D92C9E28CAA9290D4A420C00382B650F4B750C873F9F4BF7AE37F6CFE7C0CEE761C7BF97DE8DE03D2A3D547776EB9675E7339671EFB6EFD2DD0FCF8D5DE362ED89032738476F7B6DB90F24F8A42A3093FFE1C63EA3FC8AB90D4FF1F
	G6C97AE656442338DE80E213402A26139F0BE34ED50CE3525957C9E5E8F88AF04DD2761A5061CC620F5BE54754C7CF256DC66F25EA36DB6E3A1BCBFG98859CBE133FC0792346C38C3F1461B9E46B84A12E8CF93EB616B0DCA67064832E8D40D6C679E5F8D2856E2E2BE5FC36EFA5893B1E8B697662B82AB1B9ECE1FD335833D4782F6CD547D0D6A251937BD4C25B850046678823132760D9EF3C6ABCEF9FF34B9513F6496B2D70859C123742B9612E2BEBE9B6C922DDF6877C2DDE49A7796530D174CBDEC1A8B9
	0E63E6987349B802309D46CCA2B80F0432C661FD9AA07EB847D70DA3BE835EF3CF2495141C6076B5FCD14F7437FE7D83E14BA9DF6E3E9876551C20F659CEC46C5B6CF9CD77BFE4AEFEE5C01FA7GEDBDC0AA40AE00627234029A00BF10984838FC9C1ECDE332BE6073857CCD41E03357916AC4FB613D2E8EA88CE7126C0110C39068BCFF57D82E214FFCC07B255CF6BF6A1324EABE6749264704746FBF20FBCF43A769C9DF6B8C6554970E7208CF286CFF0FA9FBE9796C181CAD3BDFD04B5EF4EDEBCE1E066C6D43
	DBF39258B849AA596965B8573FA4B130995E33D9FCD07C8E02AFBD994153788C92BC36AB01367CA40F8D6B87DC97C36EEA21286AEFB47D107013B8DD56C92AC3D1C534AECF12382B2F20B277A93A7C14609DAABC5565AC41E3BB8EE879DF269550785A59056BF5A934D78124G2482E48294D6269514D67298BBED787296E34CA8066466893757A105845AF72761C3F8CA53F2C8EC7707E551EF177A4410DB9C73CAE101717C5600BE981B55EFB709FDBB4838A4792510088B96C697FD12AC054214A72FF3BF7204
	A4C23538FD9288708585BE6E5E4E8370740AE139B76890E5A942C16987BBF1CEA645BE98918600F76A17BF105C528876DB81DC0A9F6249F8172590640B3ABAC3A0F0F5A2E8C1CB0414CA74F3FE8F660E9B703E3C127B787784379B5A45D5FC1C1FF57ECEB5CE0B1BE4A3B1B4435758F689F3F2D9950F191DD69EB3ADFB3FA714EF7DEB4D18310EEC4DB1D43135D3953DF6F612B1D1AF5A1E142C37390EEF854A18DCAA637EBBA2A307BD29FF6FB661DC45015CA400CD0A0FF5DD25B4E61B4530442334BB8481A6B9
	A29E27FABEB50E6B60A300AA00BDG07812A81AE816CD56CF91E70ED83997927528AAA812CGCBG12GD2CFF15FB60C87E3683E070DF7E99E7BBEE2729772C39553F1F6FE3645140D74FDCC7EFC3EAD388559F94C965E26BE4F6A427125C958D4DB242064F7606E4AE86FF46138AC8F0C8714BEF95D088FC25E5043C28DAB78BD5D3836C5D98E3947A6E449AC49325B6F0C508FF4232C7044D8B012F2382C7F6DC67D82D310B7947E435D38666C813FDFA2132FF4E174CF481A17E8C9208FCC78E50A7F42086B66
	2AB443F24E89633B82668C6F8466AFDAF32C95519741105B87A105A9D0E82B46FCF03867104AC7D9909FD8BB15G2D321A2F6D294F785ABEF27D91E143C8D02735B7A52C39DF97DFCD57D6E2F5E4EDD31DCAFA34F57F14649E068FD3DD6FB1E36D982B8822EB6AC7E69CEFA348F985A0331A2F293F6BC1BA4FDBECB16983DEAFF529626BFBCDC5B8B75235898952BDA6484B0A7F7E52743BA8DA9F73A525BFE8C2DFFAD53955D2033E54B9F9ED19DFC37DD84A1E35B5549F14EF3D42370A51D358B3F5965F240B73
	E5C0BB77F4DAC159E94A67B82D66BB6766FCABCFD33EF50CAFE3965F5B8ADF59994A57F8E6B6DF569964FB5A437916B03E04B3F43FC8BF23666B70F33E1633144FF6F6B69F1D4F5F9C4398A3752EC71C9427AB42D330CE6A6ABA4804521A764FC99CA0FF62D9DE3BBEE941B9CE0776AE0042DA9E0FAF6E65356B722BC9C2E2E8CBCE2C5A7CC8AD55232DB6920F6523D3B9B40672FBD166B220A5547278B9E8D66F1FCD61F0406EA6DBB71B7B0DE6F53CD25C33667761B96E8E0565F6C8A853BCCEDEB0233E3837B3
	B42BF5CEB88B192D49A0F7BBC08E0DBBE781093BC82CD27F9D8B6ED570A6FD07216CD81F406B4655879A192D1B9FDBAD54FFB32C532A63176DF369CA4ED19F341C0B78E2430B2F6936GD03DFE2568958FBC1B4EF13D8CAE1465F2BBDDDEGD951AB65E6119A4FD423E3BD6C62E389F5907FGB45F6C37C5671BA0501683A455F1F96F3ACF4253971888CB9564DFE3C88A07A5C799C6CF05B8A5D7107CDD510934D6FCBD2668F4387038622B91C2ABB3446C7695BA126ABB66667AEEGBD3294DD1EF5174F5625F5
	5AAD4F2B8A55A3754CC935EFEECD95264B918F572572AB286D3E623A5C6B29623AF486B0C2AA587B64C23E2163BE6663757EAA987395E55C03FEB86E633EA30BEE580FAA6C81DFB060072ECD8E475C11751C0F0DCE6DBDDA00B19A12E057F1E39D2B0E516FFD2C0E51FC71EE4D980D7B35D3D7D4CFE33336BE9223E5E92F696A53F80C067D5C06A54013D24FEDF855FFCE5B8613645372CF34953CB6392FA77A7C16D99FB93765B64CBD4351F34D31DE546BD0C374592B1A604B9A224FDECFD9389D35C0DB5940EB
	71B77AF0FD8867E18F862881B0GF881624E739C55EF411AEA5E33CE3B9F0EA0FE51EB1ED5FC70B3C7AF4A05FA01357777629C12FAC171C315B35A8FC967E75B3EB12ACD7BDD18532FA22A3DA32A9F353B550AFB09F0816C862881B08248G71G6BAEF05B9F6AEDD848F6B22316009EF0ED30C76B236BA9C557B797E963FFAEE863168BDA7D563896A76F7BF37AFD9C554E0812C7F32D27D71DEBF39B695EDC894F96G3A6F365B783EEBEB647B6E8F07B1B617C3BB99E0E3A3BFB3385C0B53FFF2CE3FE87D539B
	357A9D77ACCEDE6D1CFE5172F6E94ADB67DB685EC8DB5ECF966CD7582495736B87B0660FG2D91408AA083A4G2C83F825C939BF6B7D6A96E33E0944795C0827FA4E7897E75F1FAD58AFAB4ABEFA57300654F4C54DE985C7GA86EAED2BF35C1FB34194755173DF8FED92A344F11B65685347DE34B9F630C0E63EB61290CD77FA8B2F2E26069BCE67645BEFB7E36CF7B6C795FFD5AE74F1C7E7D948FE328EF341433E97F82E753FE2D3369C74406E7BF5CC746061AABCC822BC514C5C6FBC3BEA8E81CD13BC41F44
	6CBFA038C89D0BBC2DF2C0DF2D486C3646363F4F2AED3F4DCAEA5F40C448AEC55BE8379ED2E81118EF17A59F2AC3F93C243FAC061C529C9F05C9FFCA0B557F06B5169F292D1F3A543656A87874D9F87C84C26BA4E7AB7AD163ACE4360760140AA7A2015F732FA667930C4F154DBCB75B86B1B7EF01F686C02E1E2F11AEDB749AAD51C7623DD29F2BEE299A4171DA7434BE592360AF903CC30527F54BB3E4EF4076D2206D50737A6BE1375A5ED30A9FC40F9ADF25605F7429701DD54A1C7C63C06C987927816D98F9
	E9A0F60C3CBA30F00C3CBE90BBC67EEDE06198794D407CB132562F36B5621B75BDF8CF6395E78293F028CAE9C17F3E245C4798206DG0883D855C277EE9AC3CFCC217F47C3228F44180312DDD85E0AFDD718F9DF815A45GB5G96G39154F53FDB7F17ED451D6C563676FFBF97C2483EFE66B6CF8493D98ABFED2C9BC9E3A98DD773ECA70E597236B5E31C1256E855A4A0BFCDF10C89DA2DC823DAFF15C76A13ED754DE62F1FEF624981EB6E8CF832CC4DE00F4055ECA68D950BE54867DG2CG53ED1C7E6C70FC
	F99E47616FABD56FA92A7769CB5A7B08CD5161DF6715213E13FCCF752E3E534C0B51B7624737FB7837458430AD350D47C655A85694CAFE4193CFDD5D25105B51ACBA8DA452E8EE511B72D97F7CF61EDB2E11FB6AC6E8DB81026D7C3EE57AA83FEFC97E4EEAA1412FFD5F324DF2D3372C1DFD1BEA0F1C5F36F9DE53D119CF1B394C9DC0AF6DC05CDB6662A87BEE8A341E26744396B59D57D9F7361A5EB30B0E7A7EC227267F69ACBA7A68F9B6BE356D794F39ED3B1FDFABF81FDC9A73BB2F2B035A94471E715BD7E4
	6FF87CFA8E0D559F11DC3D91E8BBBAF87CEE6EA35FD520FD64B2479D6DD7E27AB20F171F8FE23D33CCE9EFA3ED3CCFE3F577606D54A6B84E6D54ACD4461F5C160CD8F54EFC7C30175A66663B3FD26E0312C16F0DG99G02015EA7523C76769067B9E42036153327414073DA7E9EEC139BE351AFF79844B17CDECEE9FB9DF81F5CACB95D7ED67F75991F54A9062ECAA146C4F9DE7ABF6C2F07C247BB930E6A3FB39BEB1ED63FC323B767D9B103F1E38C859C93F6253B10E8C07D2F0F28E3504BEC0B07A725BFA7EA
	51B3947A3AB1AD3A5049698F5A3568458AFD29C30BDE564963AB5B34386F6C7B96592F6D367B51BDC5CF6E7C6BBB691E122D608D845F2642539CB9E562B952823414CEFE47724CA046D3C9972CC1GA3008BE099C0E297BFF3BDF025D2D8604CE589848DD028F8D565B735EF23F9F13FBF7871FBBC972D7B076F8AA977346876885A39A8250B467FAB6CD9767A0AECFCD27B7A48FA496F063C835086B08AA08344F5F37BFEF9251C5A576AF04BCD32AC5ADD64C61B9B57671626A2E8ACB2E81CEF6D4535B4A905E4
	37DD7452B222D98C9983DE37FDC6C86FC63FE712DCF88267211B2F4F577BD0B7D7A0643E0EB7EFF3FA8A2C6FBBA4E7B681149AE1DFB2A22E213F0559BDEA1C1B5B781C67B01001E79940C156DEE90C448555252786E9D1DF17BA58CFEC787D1C0D47CEA674AF6B617BEB75A934A3EAFF3D688DCC99D8DAE335FB4E905F6BC6FB145F628C61188950DE8B3029075F41FCB24C6FE0367570FDF1E0B8FA5F4331A27B5A3D43517B9A2733EFAE63B13E391054C465BEB142659EB2713D7E66C5753715757BEE460CC123
	094EC55094596B0B3A7EA3070E1DBD4647DEEB62F63E2960F6A838976D9CD7EA6638279C9CE7B5739C55EEC66D683BC975FE53F4A73E5F4F4ED78F5CD199F4BDEE9E0CFD6EF9E9D07B5C730BC16D3B01FF9E1C7F6C7F46E06C6F52BB87555F25E9BE59C1E2AD9E66A299A095E0BBC0161967129B36CF8512CBB472C82B430967AE7CD50D4C650F8C4536F5FCE8615F62840670FB5BD8C01683BE9C1F63A706F03E64C010A0A96E7A907AA8045FE0A8AB43AFA16B53AB0D93B44D8BBF6C431C2FDAAE952AF3D2C5DF
	2B49522EEFB20C187526EE03213949B452558AC83AEED6CD29FFAFD87B7EDD1A5F48910AADACE733676E038FE6D3799F76D74FA33F2D5B54FE253B4BB2CB03A6C3FF530019659CFFBFDFB723B076E1B2A66C83827F4B3C2005CB3060EFAE45F1ECD1DB7E2510670012C73FAD343C5C227BAC06AD694C0672DE55EF83FD25B96C5EEE4E1D442D2D37979659AFFFE8F1F5459A12179D3D51F545E3849F6C0D2EAB4E8E713D6186503675729CF5F4D8AD3F258F4ED1FD73576234DF092825371775DD22B186DD17AB1D
	38AE1783FDAD40A600EDGC2BFDF1745438BFEA7EA1D567CCEC475FAF7CCCB2F52FE2ACFCD7FDC3DE83FCC4DFEED0C5F96335F4F6DDA7D020CFFE94CFE9B9CDA7D9298FF32C6BFF69FBBBC4FFD6C7042F9C8A07B27847943B293E4F7C1F9A3F846890C0F07A5A55F3DBFD224605A4346C0580DF5871D4327DCB8468650B1DD19373F9D415A29CF7430994392AD90A2D386C8B8E384217C1A21D8724B946607F03F5A8F3BE13CA26B7C71C7577B8B036A5CD57420D74763BD1A7F889B2F529AD96F540EFA8F56E88E
	40AF81C884D88F108A3083E0B7C051G5423G75G96G97408D00F800D5G6BGB68DF09FFCE77C58BCBEE0979AE4E38909EE5CDA188F9E9D921477F52A770621BBDFA7745DD199747E2001540A6960271733B92E1960F6816ECD052FDAA45FDE2EC0BDF9055FBDA4087C6E217C8A77F322B3769AF85429DDF7BC61542EBBFE601C7F1B44534ED8F589556FA3D29F9AC137D1C5673B46384EDE85771E1D5B962F60DCF64E37D2413DC5F09B201D2A60CC8E4E172E60DEF6707104C10E2B15B8DF61A07755865787
	EA7D6952E3367D4C0539586D0725ADFA5D5749CF064C4068F752717E221F4FDB36E26F7D7DFC5E90C757516A2B02622FCE77673156G246E8E28933A47BC12DD3608CE250F5193FBBE7BBD8B6734D10FF6EDB56E19DBDBDD75E857D657BC6A5A0A62A6BDD817B8E15DDA953E2F955C0002DBC246C4DC3F5BA13B947CBDC4AF4437C974C7C8949FC7741AA2B8AB43FDCB059BE03887BC181741E4B802723E8FF9701EED5C6D7552764304476D07B42D78A31168688E0AAA3D9FD1F028B7BDEF09C78BA373CFD42778
	12693D91BCD71D1DC3BBC414AF4304509ACC6743392F9C4458F8DCD4FF3B0F865A3FE6ECF67F4F7D117EAB46666BCF79FB83917EEF6C8B4B4BF54C16B79D0C74F7BB9616376DDA043FDB1A0F1F6ECB1B0654F73325A73AB56FE633062E6B268769FE34F2B032AF25C09DCA5754BBA114158A348C14ED0373BCC0BD004546577611D0D92C3586C8FA380FECB734CFFEB876DA5B97DEF82D9D8AEB2F350F43F3575AE7E16D35F6AC2CDEEB747FDCED4F3E871E49EF89F9611030CF7961D3BB896CED1355F5B92FFFBB
	C7A76436C305C704378B39B8C23B10C784348BB9A01C4CEE2E9AE4213049BFD34AAFD9E5C1D75FA08B6FA30E5E0C8226613CAC6CC18C3B8A65F3F96C9BF50D11F537BD6679A8E9084E617A21689A63FCEEBE196F340262E1BEEF77651C0CB5EF10A34DBE516B15C56F55A63F8373E5730CACC576ABFDAE0E9F66BC0CC8E99D392837C755677F81D0CB8788864D3F6A9D93GGACB7GGD0CB818294G94G88G88GEDFBB0B6864D3F6A9D93GGACB7GG8CGGGGGGGGGGGGGGGGGE2F5E9
	ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD793GGGG
**end of data**/
}
}
