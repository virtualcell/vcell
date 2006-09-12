package cbit.vcell.model.gui;
import cbit.vcell.dictionary.ReactionDescription;
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
		private cbit.vcell.dictionary.SpeciesDescription dbsd = null;
		private int stoich;
		private boolean bResolved = false;
		private boolean bSelectable = true;
		private Boolean bFluxResolvedInside;
		//
		public RCJLabel(cbit.vcell.dictionary.SpeciesDescription argdbsd,int argStoich,char argType){
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
		public cbit.vcell.dictionary.SpeciesDescription getSpeciesDescription(){
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
public void markResolved(cbit.vcell.dictionary.SpeciesDescription dbfs, boolean bResolved) {
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
public void markSelectable(cbit.vcell.dictionary.SpeciesDescription dbfs, boolean bSelectable) {
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
public void reactionAddCatalyst(cbit.vcell.dictionary.SpeciesDescription dbfs) {

	addCatalystComponent(new RCJLabel(dbfs,0,ReactionDescription.RX_ELEMENT_CATALYST));
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2003 4:21:01 PM)
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public void reactionAddFlux(cbit.vcell.dictionary.SpeciesDescription dbfs,boolean bInside,int stoich) {

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
public void reactionAddProduct(cbit.vcell.dictionary.SpeciesDescription dbfs,int stoich) {

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
public void reactionAddReactant(cbit.vcell.dictionary.SpeciesDescription dbfs,int stoich) {

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
	D0CB838494G88G88G6A0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D46715A44108968D7EC508C489EA57D61A1216B65465F4B531A9E9ECE33BE9E24F21276E165DB52D1BA59B4E9EF75733FD8C98D1B012E89A5B92C111C4AD2122B49A22097C89489827B00C030070E09EB07218194EBCE0785FFB3F1FB70F618D2827F24E754DF76F7D3E7B735D6FFE777B5ED3B81C9735A74DF0D8905276886C6FFC1AA0E41C9704EDEF64FE4EF1E18ECB162079BB867036F038
	AF831ED1C0CBD8E3494A911637E4C2BB8E5AA7217D9E7CBEA4C436747E99FE8410F399E899430EF9B34BB18EF1B9FBC97B53235970FC8BA09EF0784CF0227C4623C798BE0961B9249984A11970B80E66B2DCAD34BBG22G621C14FF8BBC779149F34AAEB03E56C56B04549F388D87F09C4D189C0ADC342F09BD73057FC95B65A27EA0FAE21F90E8EF84C06353050E4133703CE86A6B792C2B532E948FF6C932DC5C6F34C9F2F14F003D2C2C2A22C1923B943B53D1ADCB7D12C37118C407A48B0272D79C7797C7B3
	49B8B0863485B762DCEDA8AB9E5ADB81F62878733790DF853FDB8194B7376F520797197E56512705641E6F6DBB98403ED0B735AB4E6D33AF7FF4E77A6F48DC04FA5167A9C0BB6C01F987B08248G21GFFA0B130E468B4BC4BBB15CAE7FF3F53D16EF2D548B677B53497FE17158105619A24AE275BA688F41E9FBFDA24234FCCC07B2D3F6BFE54A76B4E71B90FE9F8D2C87A6623063DBABEC9DAB7EAD863213EC87078FCC2E5A787141D64199A13D3E55F90343205D74F6E5D23A3BB776533FBD358B8990ACFF629
	02F31DCCE260847CCEE371C1712F913CD7032771B9C870588E815ACE05470675AD2E0BB1A3DA48A9D92E6B0770E7C28D26812AC36F003FAEFFCF62EE69G15F9D055658502CF506029AE17889E5B49C06B9834E45178DAB6026BB5895A3BGF206A046GEA81ECG5EA19EE3C9C3FF3A4398B309EE25E240AE5BA437C07BAE737E961E12D7F10BF58E0FA2BA3AA40B68360B1D326491984FCAAF7A20F3CA3F55A4763BC0C60D64105CA2AED898DD6C1794496D21BCB15E371147AD912A595EAF0100FE1740470D73
	9E0227ACFA14A617CDD4A49F8725A7F8F1CE86C58B0C888340EF6A17E7C651DF61E0FFAC40C6558F6707F13CFA498D79222C4C68F4768D384CE8091063C5BF7FAF499DAF817F4EA16E636F0EA14E8CF4594B47F18C3F2399274ACE3211689E66EB6CD7ED1813033CBCE6DE33721829BA70B52168274BF4E3A68C62F70D172D9D2F7F5AC9A5E322DE34BD56E63D4BF5FCA7904464D2994B6E0B0CF476247EAD1A4039CA86394230A5ABFF187B78AB4379B4E6ABC40F4423743A9B82CC3279BCCE755CF48B57417BG
	A5G38C966811C832881B8205A73E342F781E4F4G04G4482EC8390C6E06D0CF05FD65EF28550BD0B0DD7B303FD6FB3793379210469B8B5BFF786140D7403CC7ECC3E5DF7873277B0DBF81B7AFC55880EAF8D4226DAA539A4078DF7D7C67B72880E4B72C037D36D93C370AE489B1530D0BDAA7E9BA33836C5C5F15BBB879429D1D2943B2347C75FC0E4F986BADD3E1443E5ED9AC17D1CC310B7D47E67C6F04DF5B99D8E09CC3E5A055193491A17E8C9D0699CF0A8947F23D1DCB7FD52B04BB9BF99FDD3401CA18F
	E07E22B5C72C083EF0396D7D90D218820528914C876F649D51784884B82C1D94F8060C7035BDD440577609112F889B5BDD863D3DA9FC6DA3069DA3F4ED6D9E712DED2A5333E37A3ABFCFF28F43FB282E8FB7E26D98288822EBCA1A4471F2C7ADD9E7GEAC7791A2A9EC7BA4FDBECB1D5BAE5193AD4757559099C1C9B692F839224FBCC10752A7FBE19F8470FE6E13E2474728974252C492DB123684B4CF6DEDBF60CD2BFFA59B3EC147A0372BD23726DE2741CB17ABCB926659B6B60FCD500EF8588E2FC89D378BE
	97B9DFAA634B9E27FCD563DA3E34CE4E97B4CE79427D78CC934877BD15EF9B63CB99277BC56904164F544D79A2A7A8DF5C44D4BEBA1FEF1C4298A3756EEDF1D07496FB06E01D1415DDA593CAEB5A313CCC463FFB02572E6B0E639C17CEC2CD89E01B64717863833CF6DD5A37CEC8FA35E4EF205AFCEC126A91B5690B47387FA8FF134650578514399DE8A913BCFE92A6347BE73947636C32132D1B4DFD5F24B6DEA94EB5399F1E5DF637C72903046265F1B2B8097A625E4E502C56A9E94FE636E684D932AE822493
	7E7076FDA46EFC31CA7DF74AF5C045B7D4DEB51EBCE591F85D98FD68B233F54B1351424549DB863DBABE64E02FC18922BE088C727922F6F1E7FAABG556B572ADEBB00A77F812ED7C3874A6A3577744AG0A2AD77CF80E964FD423E32D6D60E3D9E11CB0G1AEF5EB37B671BC4206D84909E647232450F60596F9C70C84564DF13DB72F8A45BC90C1EE2F1C8A9A6793B789A522A7167A9D5273C6C532A2F5A896D24B1E037CF5111D45FDE116BFB8474B0293A3CA896CD55255AEBD7E6D4056A3175630F343E39B3
	D518AE27BB39AEA12047C6D517C51DA5DC17EBCE0C10E276FBF0B65F50F163A4DE6F6F02B14FBC44477DC3B79C77F15FD1C4BB6CC745DD4EFE175381DD4BED3669A3D7F2BEB6BA357703FD9823EE89F69DBB56315A987D5AD1ED0CE60AF3F4E3B474559EC36FC3B4B6439E7245E8AC4467E6B5C6073B398D5B00A7A7185B303D7B93FD9B9A24FEBD7F78DB41EB1387727C4FEF35413EF3DBEB7074B39CBD57649620DE2EE07F335787849F946CFF76FA72B837A38CE829413C96DFE84075E11D83FBB0C0A8409A00
	4DG49F3F80E7A3FC92C29E6BC6B54B9608862906546A94587BFF39039D0AF30F6BC19C3D2AF28FEB85F6E6F8761612936677A35E93F6AE97D0A7D5A177D7AD13B7F3B9A7792AB509400D000F5GDBGF6GE44F6536D724DD1A4DF6B223E6E7A560EAE10F2E742F27D4DDDF3FC79BFFB32B0DC5F3757A2D6F38B7F95F19566F28DF3B4ACF9E4D35ED855ADC5BBA176E4DA1700C84207B6EF5B35FF7636672FD77D98B4666CEE8E704D832F2C3781941A15E1B7EE3537A796BDF9A22576FCC673D497BECDABFFFF9
	C6DDF99B24596E0D7465A54E5A4F962297732E9A0C79B12005834482EC81C881481E8779F91EFAFF167629F0E7B1DFCE62FCFA44D3BDA73B6F4D3E853376B34D1B5A0F5EB504131A2E97E8E3G94B7F08CF1D1500E1F47636AF8811EDF1254F691E9E3DDC05BF1477F96E77424F953F3374F469F7ECDE424874053F9DCEE88FC76DCE950BFFB2EB1681FBD3FEAF81B62E18C6D0D16FAB6B54CF2B6B5681DCD5FA7B6BC7F5B03E4EC2839BCA4302AC4C5E434FFD35E95F44E28DF087D92337F1060A0F5AC72D4AB4E
	4AF32A4C74AB016D5FFFC55F7E1CAB2476F58E383BA4FF9B5F3BF2C42579E23ECE117AD19D4A13CF7AAB223BC71A66238FC9FFCA8B547F4C15C0FE24361E6C505ADA2A627FF98A9EDF0150BA29279A7D28F396EA6CF243A995CFC4823F67DFD6104962B3F59E4F4D1D0D181B0B6643198D20F5BEDFA3E6337F9AD5667B62BDE4FE203A657C8D9CAFF2BE2DCF32D4FCB541A7E87034EEF91E6C8D58CE825A65AF717AEB2D2835772C6A071ECEAD3EC4453FAEE97057CA54B99929899CA38F546847485C1A40B13230
	E67698D9DE93B8C6D6564C9EA3FFD7B3F30CFC3BDBEB2B4FB7DF4F43FB9AD99CF68E4021AAA7947D7B1CFA9F3386FC1BG108C306BCBF46F26B17454907A3F5BAD760318C61754A56CA4FDC30F713ED6E84BG21GB1GDBC279BCAD9C477953C4DB890D1F478AF87C14AE30E455AE189AAF2D8B82454F4BA49EDD8B7C6B5E6D849F34403F6E6DEDD46BDE2025AE607BC29F29C32C8BC16F059C772BA63ED704AD64F1FE6946E1F846C1FBABC0AA72BEE249AAFD04536B895D84ED97C098C0AC40D6157E22E526BC
	0F63705FB8363A1FE970DB976A6FA3F12A0EBBE614213D135C2B792D3D535C93202F4F0F2F6571F70BA9205B49B09E9BA5644EC84DAFF862A9AB2BF15BED95E20F11C49A4DAD9F67E7327E9DE1BC37587249BB85E84782A406717B966F877C3EA5664B514256FF513FEF59EE9EB7EC8FE36F2696E95E871D6ECC27B23FF10C4B3C8CF46FA292A7478E7B59B7AE50FA1A525FB92E2563BA3B1E2625D7CD21233EBFB3E86903D36868239758785436978A39ED3BDF089564EF8587FC4F3B1159144CED7BD1C4DACE38
	69CD9A2BCF11DC1D3B98F262E29E3F7FF0023CD70176180A7BDD219A530BF93CA4B5E23D33DDED7FABE963FD9A2B3B9B6F26B641F16E26E621B2665E150CC0F54ECC7C0704874D5373DD04FA9F1431844E9AGD5G56A574BE116635B48B67F1AD21368533671AA5BC2FE512B639B1969D4AD52358096F4BA96D008D6F13AB249E3B235AB1B25CAFDD935DFD121BB1D11EFF1C407E15D06848439E3F7E2F25E14DD36D30697466BC914398B7A63753B65025F697F6AFC17D03EEE8E3F0B333ED87BCA9FDEB339E3D
	EAA927EF3829C7372A742896BD3A2C5267376A5103167278CA1DF667F1E7B1E3483F37FE1F5EF5BF3A27FCFC0C3CD3DECA7714B4957FB941C7E970B4C75E4E67B9B286E8B94B789D4BD09D5953219D86308EE0A340F6005D4B78196BEA6D99E116B3175969B2C221A2EB4AEFEA5F53476E6D7B03CDFBF9AE0A5B70D5A17E61AAC316201F0BF29E2571FF06BD6337C42461135A373F9E6D6BG5A98C094C0BC40960064C739FD0BEB0B28FD55B63BD22EA8E2D7AF39516646D96C52108F0DC5860D734482DCCB0312
	DB31F709B2ADA3AAC43749A95B3B060552656877DF12DC78C71C07C7797AFC79846A566BF45BC77066EDDACF01757DCF12B34B813C50964291F726300A599D3FFCFAEE63F31E4EE08B70EC87F81735D3177B62026A7217D224793DDD3A4ABE31E13133494CE32796F4888A677B6B3933E8075F7EFAC5F68E99D9DAE3357B3EA63E5745072B5F62B46118A9504E0C30E465C770BB188FAD7C8E66C2845F975BAD7E7B9E0E655B5796D97C77B5CEE76FDC3A833CF3A129094AFD7A86176B0A60FB7D78956D3B150403
	6381E3F0DD841D0B44885FDEFF225F3C170E3D77A69FBB73B1EE676BAA6E320AFB2505633CAAEED3AB4745BE46F354C57C940C5FB1EAFE3B726F477B7B2979EA5EFD15C157E3D0DD60F34F5CBA7DF34F02BA7D3B01A5F5B31F7DD75485FEAF9DDD27FDAFCD73C92491E3ED874CC5468A28B581AE8218D670DCF2666AF10164921DBCD2ED6B41F397FED523F079076A835B7A7B7A593F45A9284777ED1DCEC5F17663789CFF2A9E67CBF13A8812620A6B51C7EEFC87C3D9993E349EE3CC163A891A660558931873B5
	4B25D8F3CEAA36D4B7186BAA4B0D6D0D158D570D460A720676AF2A81C957CD541076FB418B7B9F54FDC70EA02F206BA508BDC3FF9311C6656FABBCB7037C5A6B8DF5B72FFFE11E22C13971C6F9F3A34BB9E2A1DFB771B0F69E99D3A06FB869DF4A67FA381495784D653BE6ECD1DB7EA91B67005C752B052AD72A8C8581ECA90DE475D824AF878437166E236B667B536EA46EEC3D7D749E7B3D55F4EFF545FA121793A27D6B0AB802CF0C742FABAEB5713D61A520DDF8DC3D3734E865C782BE767119EBF1F67F592C
	2777E656F71B4E98F4DD1A1AF0DD6E84FA66CA58CFGAEGD8D772F519D8BF6BFB22EA2F6EFBA22AD776CDBD3D3CABD95C2E1C2E976D77DF3A7D2298FFDC40FEC9ADFA7D9299FFD240FE4F356A75CBE17C992B2677E3F72B4640F9E83DF176BC94E7A45F82C07EB08F3B58DD50F70CF846F1F6F7FBA4B55FFD571823626ABCA627470EF5871DC3A7597B0F000E252B783C7D659A56CE9671B61BB1AC511CEEB2E50004B3068B4A2FE10AA5DF26B0BFF497EA7DE00671FAF5EC5782DD6FAF36E8F3D74EE33201473B
	BF7F989BAFE415EF3DD3BBD2EEE00D16G78EDG3B818EC741DE81F099408C508BB0863094A086E0A340CBGBBGF681E4BF81B17C847741CD732999FC40AEB4484662966D3835B09FBC55A4283FB7E8FED7B65D7FBA21753E4A20778715A4DECA41CF2F24F1DCAD41998177EB156F13E6726E85F05EA7785D434AE6FE77902C7A595A94F88D34B769579DD213FE5D5157B473BB89CFD3203A046AD7C06A43F520DB3C2AF348CD2E73E695F720055B36C345B9DAB8DF2A0AFB03600EC4C39CC6F31C351573152A38
	1F3772712CAA6E33B64EE70B663ED2ACEFE97527CB0F59B6CA6AF13B831636A85BC748A7C30D40686821630D9772F9CBD36DFD30104F9B6268BA32B48B2A3F52EE9486DA83103A2FC21DF03D7336542518459E35CF06B570FCE6D9E74FE9072D7A35556F2D53EB2BBC2BFEEDD5E85556D694F75A0AF5C98F2CDB2B4A77A795572C62CA4918083BE137A93DAA3E0C680578DA09FE04C4711709DEC384E7E538EA8D2E19612EDAB1AF0349F08465FD2FDB711E2D5BAE4B345DC8F86C8EC8532ABF2CC4C73BCB54685D
	2662D0EFFA5E92BF48764DBFD11D62F33D87FCF82EBABB071A1AD13E8293C2EB30EF5F44398F4198943E552CFDF76F8F34FF594D297DFF56626B3F6A66CC7DA97F3EB69F7F1C1659656DEF1DAA2F68162F3F33F5F6F9CB5BFD7CCDEDB37153FDA931CDFBB71B7F476B3AF7333B5AC68CDB2369FE149A6D5B17DCD00752B5754D8E14F5F235A52B8A2097A088E0A9C04CEA3E3637351F8C3456GC98F773E6D06FDAB5891F82DBD5BB17BDA5B52213F56FE52B1FD2D3D52213F56FE5E21DDEB747FDCBDE5FF981EB1
	7FAE6C713805036A07CFF5A430378F1EABCB7F656AF40310D1A73CC905578999B8C21D300788289352C13897BF524E5002A2E417BB067379A52BA298AEDED2047D0823B72300397419A2E4A106DD057239BCB5292DB1F64D698AF8BE925652B9BC3C563F46481251D693606535FC5EDA7ABE8AB4EF10A39B7BC5D9D6C4392F5CE143FCD9B12CC83E7DAAC74671BD1C0791593BE599753E2D69737FD0CB8788E59D31B0B193GGACB7GGD0CB818294G94G88G88G6A0171B4E59D31B0B193GGACB7GG8C
	GGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEB93GGGG
**end of data**/
}
}