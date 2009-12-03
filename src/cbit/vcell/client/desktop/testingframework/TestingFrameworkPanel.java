package cbit.vcell.client.desktop.testingframework;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.TFRefresh;
import cbit.vcell.client.task.TFUpdateRunningStatus;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelNode;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreeNode;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.gui.NumericsTestCellRenderer;

import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;

/**
 * Insert the type's description here.
 * Creation date: (7/22/2004 6:19:16 PM)
 * @author: Anuradha Lakshminarayana
 */
public class TestingFrameworkPanel extends javax.swing.JPanel {
	public static final String REFRESH_TESTSUITE = "Refresh TestSuite";
	public static final String TOGGLE_STEADYSTATE = "Toggle SteadyState...";
	public static final String EDIT_ANNOT_TESTCASE = "Edit TestCase Annotation...";
	public static final String EDIT_ANNOT_TESTSUITE = "Edit TestSuite Annotation...";
	public static final String QUERY_TCRIT_CROSSREF = "Query Test Criteria Cross Ref";
	public static final String QUERY_TCRITVAR_CROSSREF = "Query TCrit Var Cross Ref";
	public static final String REMOVE_TESTCASE = "Remove TestCase...";
	public static final String ADD_TESTCASE = "Add TestCase...";
	public static final String ADD_TESTSUITE = "Add TestSuite...";
	public static final String DUPLICATE_TESTSUITE = "Duplicate TestSuite...";
	public static final String REMOVE_TESTSUITE = "Remove TestSuite...";
	public static final String EDIT_TESTCRITERIA = "Edit Test Criteria...";
	public static final String GENTCRITREPORT_USERDEFREF_TESTCRITERIA = "Show TCrit Report (Choose RefSim)";
	public static final String GENTCRITREPORT_INTERNALREF_TESTCRITERIA = "Generate TestCriteria Report";
	
	public static final String COMPARERREGR_USERDEFREF_TESTCRITERIA = "Compare With Regression (Choose RefSim)";
	public static final String COMPARERREGR_INTERNALREF_TESTCRITERIA = "Compare With Regression";

	private class TSRefreshListener implements PropertyChangeListener {
			private BioModelNode selectedNode = null;
			private Vector<BioModelNode> expandedSubNodeV = null;
			private boolean isExpanded = false;
			public void propertyChange(final PropertyChangeEvent evt) {
				if(evt.getSource() == gettestingFrmwkTreeModel1() && evt.getPropertyName().equals(TestingFrmwkTreeModel.TS_NODE_REFRESH)){
					if(selectedNode == evt.getOldValue()){
						BioModelNode newNode = (BioModelNode)evt.getNewValue();
						if(isExpanded){
							getJTree1().expandPath(new TreePath(newNode.getPath()));
						}
						for (int i = 0;  expandedSubNodeV != null && i < expandedSubNodeV.size(); i++) {
							TestCaseNew tcn = (TestCaseNew)((BioModelNode)expandedSubNodeV.elementAt(i)).getUserObject();
							for (int j = 0; j < newNode.getChildCount(); j++) {
								TestCaseNew nodeTCN = (TestCaseNew)((BioModelNode)newNode.getChildAt(j)).getUserObject();
								if(tcn.getTCKey().equals(nodeTCN.getTCKey())){
									TreeNode[] tna = ((DefaultTreeModel)getJTree1().getModel()).getPathToRoot(newNode.getChildAt(j));
									final TreePath tp = new TreePath(tna);
									getJTree1().expandPath(tp);
									break;
								}
							}
						}
						selectedNode = null;
						expandedSubNodeV = null;
						isExpanded = false;
					}
				}
			}
			public void rememberSelectedNode(){
				selectedNode = (BioModelNode)getJTree1().getSelectionPath().getLastPathComponent();
				if(selectedNode.getUserObject() instanceof TestSuiteInfoNew){
					isExpanded = getJTree1().isExpanded(getJTree1().getSelectionPath());
				}else if(selectedNode.getUserObject() instanceof TestCaseNew){
					selectedNode = (BioModelNode)getJTree1().getSelectionPath().getParentPath().getLastPathComponent();
					TreePath tsPath = getJTree1().getSelectionPath().getParentPath();
					isExpanded = getJTree1().isExpanded(tsPath);
				}else if(selectedNode.getUserObject() instanceof TestCriteriaNew){
					selectedNode = (BioModelNode)getJTree1().getSelectionPath().getParentPath().getParentPath().getLastPathComponent();
					TreePath tsPath = getJTree1().getSelectionPath().getParentPath().getParentPath();
					isExpanded = getJTree1().isExpanded(tsPath);
				}else{
					selectedNode = null;
					expandedSubNodeV = null;
				}
				if(selectedNode != null){
					expandedSubNodeV = null;
					for (int j = 0; j < selectedNode.getChildCount(); j++) {
						TreeNode[] tna = ((DefaultTreeModel)getJTree1().getModel()).getPathToRoot(selectedNode.getChildAt(j));
						TreePath tp = new TreePath(tna);
						if(getJTree1().isExpanded(tp)){
							if(expandedSubNodeV == null){
								expandedSubNodeV = new Vector<BioModelNode>();
							}
							expandedSubNodeV.add((BioModelNode)selectedNode.getChildAt(j));
						}
					}

				}
			}
	};
	private TSRefreshListener tsRefreshListener = new TSRefreshListener();

	private DocumentManager fieldDocumentManager = null;
	private TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTree ivjJTree1 = null;
	private NumericsTestCellRenderer ivjnumericsTestCellRenderer = null;
	private javax.swing.JMenuItem ivjChangeTypeToSteadyMenuItem = null;
	private javax.swing.JMenuItem ivjEditAnnotationTestCaseMenuItem = null;
	private javax.swing.JMenuItem ivjEditAnnotationTestSuiteMenuItem = null;
	private javax.swing.JMenuItem ivjAddTestSuiteMenuItem = null;
	private javax.swing.JPopupMenu ivjMainPopupMenu = null;
	private javax.swing.JMenuItem ivjAddTestCaseMenuItem = null;
	private javax.swing.JMenuItem ivjDuplicateTSMenuItem = null;
	private javax.swing.JMenuItem ivjGenTSReportMenuItem = null;
	private javax.swing.JMenuItem ivjRunAllMenuItem = null;
	private javax.swing.JPopupMenu ivjTestSuitePopupMenu = null;
	private javax.swing.JMenuItem ivjEditTCrMenuItem = null;
	private javax.swing.JMenuItem ivjGenerateTCReportMenuItem = null;
	private javax.swing.JMenuItem ivjLoadMenuItem = null;
	private javax.swing.JPopupMenu ivjTestCasePopupMenu = null;
	private javax.swing.JMenuItem ivjRemoveMenuItem = null;
	private javax.swing.JMenuItem ivjRunSimMenuItem = null;
	private javax.swing.JMenuItem ivjRunSimsMenuItem = null;
	private javax.swing.JPopupMenu ivjSimulationPopupMenu = null;
	private TestingFrmwkTreeModel ivjtestingFrmwkTreeModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	protected transient java.awt.event.ActionListener aActionListener = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private DocumentManager ivjdocumentManager1 = null;
	private TestingFrameworkWindowManager ivjtestingFrameworkWindowManager1 = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.tree.TreeSelectionModel ivjselectionModel = null;
	private javax.swing.JMenuItem ivjRemoveTSMenuItem = null;
	private javax.swing.JMenuItem ivjRefreshTestSuiteJMenuItem = null;
	private javax.swing.JMenuItem ivjRefreshTestCaseJMenuItem = null;
	private javax.swing.JMenuItem ivjRefreshTestCriteriaJMenuItem = null;
	private javax.swing.JMenuItem ivjGenerateTCRiteportMenuItem1 = null;
	private javax.swing.JMenuItem ivjQueryTCritCrossRefMenuItem1 = null;
	private javax.swing.JMenuItem  ivjQueryTCritVarCrossRefMenuItem1 = null;
	private javax.swing.JMenuItem ivjCompareMenuItem = null;
	private javax.swing.JMenuItem ivjViewMenuItem = null;
	private javax.swing.JPopupMenu  ivjTCritVarPopupMenu = null;
	private javax.swing.JMenuItem  ivjGenerateTCRiteportUserDefinedReferenceMenuItem1 = null;
	private javax.swing.JMenuItem  ivjCompareUserDefinedMenuItem = null;

class IvjEventHandler implements TreeExpansionListener,java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TestingFrameworkPanel.this.getAddTestSuiteMenuItem()) 
				connEtoC1(e);
			if (e.getSource() == TestingFrameworkPanel.this.getAddTestCaseMenuItem()) 
				connEtoC3(e);
			if (e.getSource() == TestingFrameworkPanel.this.getRunAllMenuItem()) 
				connEtoC4(e);
			if (e.getSource() == TestingFrameworkPanel.this.getDuplicateTSMenuItem()) 
				connEtoC5(e);
			if (e.getSource() == TestingFrameworkPanel.this.getGenTSReportMenuItem()) 
				connEtoC6(e);
			if (e.getSource() == TestingFrameworkPanel.this.getLoadMenuItem()) 
				connEtoC7(e);
			if (e.getSource() == TestingFrameworkPanel.this.getRemoveMenuItem()) 
				connEtoC8(e);
			if (e.getSource() == TestingFrameworkPanel.this.getRunSimsMenuItem()) 
				connEtoC9(e);
			if (e.getSource() == TestingFrameworkPanel.this.getGenerateTCReportMenuItem()) 
				connEtoC10(e);
			if (e.getSource() == TestingFrameworkPanel.this.getRunSimMenuItem()) 
				connEtoC11(e);
			if (e.getSource() == TestingFrameworkPanel.this.getEditTCrMenuItem()) 
				connEtoC12(e);
			if (e.getSource() == TestingFrameworkPanel.this.getRemoveTSMenuItem()) 
				connEtoC15(e);
			if (e.getSource() == TestingFrameworkPanel.this.getRefreshTestSuiteJMenuItem()) 
				connEtoC2(e);
			if (e.getSource() == TestingFrameworkPanel.this.getRefreshTestCaseJMenuItem()) 
				connEtoC2(e);
			if (e.getSource() == TestingFrameworkPanel.this.getRefreshTestCriteriaJMenuItem()) 
				connEtoC2(e);
			if (e.getSource() == TestingFrameworkPanel.this.getGenerateTCRitReportMenuItem1()) 
				connEtoC17(e);
			if (e.getSource() == TestingFrameworkPanel.this.getGenerateTCRitReportUserDefinedReferenceMenuItem1()) 
				genTCritReportUserDefinedReference(e);
			if (e.getSource() == TestingFrameworkPanel.this.getCompareMenuItem()) 
				connEtoC16(e);
			if (e.getSource() == TestingFrameworkPanel.this.getCompareUserDefinedMenuItem()) 
				refireActionPerformed(e);
			if (e.getSource() == TestingFrameworkPanel.this.getViewMenuItem()) 
				connEtoC18(e);
			if (e.getSource() == TestingFrameworkPanel.this.getChangeTypeToSteadyMenuItem()){
				TestingFrameworkPanel.this.tsRefreshListener.rememberSelectedNode();
				TestingFrameworkPanel.this.refireActionPerformed(e);
			}
			if (e.getSource() == TestingFrameworkPanel.this.getEditAnnotationTestCaseMenuItem()){
				TestingFrameworkPanel.this.tsRefreshListener.rememberSelectedNode();
				TestingFrameworkPanel.this.refireActionPerformed(e);
			}
			if (e.getSource() == TestingFrameworkPanel.this.getEditAnnotationTestSuiteMenuItem()){
				TestingFrameworkPanel.this.tsRefreshListener.rememberSelectedNode();
				TestingFrameworkPanel.this.refireActionPerformed(e);
			}
			if (e.getSource() == TestingFrameworkPanel.this.getQueryTCritCrossRefMenuItem1()){
				TestingFrameworkPanel.this.tsRefreshListener.rememberSelectedNode();
				TestingFrameworkPanel.this.refireActionPerformed(e);
			}
			if (e.getSource() == TestingFrameworkPanel.this.getQueryTCritVarCrossRefMenuItem1()){
				TestingFrameworkPanel.this.tsRefreshListener.rememberSelectedNode();
				TestingFrameworkPanel.this.refireActionPerformed(e);
			}
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
//			if (e.getSource() == TestingFrameworkPanel.this.getJTree1()) 
//				connEtoC13(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == TestingFrameworkPanel.this.getJTree1()) 
				connEtoC13(e);

		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == TestingFrameworkPanel.this.getJTree1()) 
				connEtoC13(e);

		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == TestingFrameworkPanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == TestingFrameworkPanel.this && (evt.getPropertyName().equals("testingFrameworkWindowManager"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == TestingFrameworkPanel.this.getJTree1() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP5SetTarget();
		};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == TestingFrameworkPanel.this.getselectionModel()) 
				connEtoC14();
		}
		public void treeCollapsed(TreeExpansionEvent event) {
		}
		public void treeExpanded(TreeExpansionEvent event) {
			BioModelNode expandedNode = (BioModelNode)(event.getPath().getLastPathComponent());
			if(expandedNode != null && expandedNode.getUserObject() != null && expandedNode.getUserObject() instanceof TestSuiteInfoNew){
				if(TestingFrameworkPanel.hasNullChild(expandedNode)){
					getJTree1().setSelectionPath(event.getPath());
					tsRefreshListener.rememberSelectedNode();
					ActionEvent refresh = new ActionEvent(event.getSource(),ActionEvent.ACTION_PERFORMED,TestingFrameworkPanel.REFRESH_TESTSUITE);
					TestingFrameworkPanel.this.refireActionPerformed(refresh);
				}
			}
		};
	};
/**
 * TestingFrameworkPanel constructor comment.
 */
public TestingFrameworkPanel() {
	super();
	initialize();
}

public static boolean hasNullChild(BioModelNode bmNode){
	return bmNode.getChildCount() == 1 && ((BioModelNode)bmNode.getChildAt(0)).getUserObject() == null;
}
/**
 * Comment
 */
private void actionsOnMouseClick(MouseEvent mouseEvent) {

	if (mouseEvent.isPopupTrigger()) {
		if(getJTree1().getSelectionCount() <= 1){
			getJTree1().setSelectionPath(getJTree1().getPathForLocation(mouseEvent.getPoint().x, mouseEvent.getPoint().y));
		}
		if (getTreeSelection() instanceof String) {
			if (((String)getTreeSelection()).equals("TestSuites") && getJTree1().getSelectionPath().getParentPath() == null) {
				getMainPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
			}
		}else if(getTreeSelection() instanceof TestingFrmwkTreeModel.TestCriteriaVarUserObj){
			getTCritVarPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);				
		}
		else if (getTreeSelection() instanceof TestSuiteInfoNew) {
			if(((TreeNode)getJTree1().getSelectionPath().getLastPathComponent()).getChildCount() == 0){
				getDuplicateTSMenuItem().setEnabled(false);
				getRunAllMenuItem().setEnabled(false);
				getGenTSReportMenuItem().setEnabled(false);
			}else{
				getDuplicateTSMenuItem().setEnabled(true);
				getRunAllMenuItem().setEnabled(true);
				getGenTSReportMenuItem().setEnabled(true);
			}
			getTestSuitePopupMenu().show(mouseEvent.getComponent(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
		} else if (getTreeSelection() instanceof TestCaseNew) {
			TestCaseNew tcNew = (TestCaseNew)getTreeSelection();
			if(tcNew.getType().equals(TestCaseNew.EXACT)|| tcNew.getType().equals(TestCaseNew.EXACT_STEADY)){
				getChangeTypeToSteadyMenuItem().setEnabled(true);
			}else{
				getChangeTypeToSteadyMenuItem().setEnabled(false);
			}
			getTestCasePopupMenu().show(mouseEvent.getComponent(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
		} else if (getTreeSelection() instanceof TestCriteriaNew) {
			TestCriteriaNew testCriteria = (TestCriteriaNew)getTreeSelection();
			if (testCriteria.getRegressionSimInfo() == null) {
				getCompareMenuItem().setEnabled(false);
			} else {
				getCompareMenuItem().setEnabled(true);
			}
			getSimulationPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
		}
	}else{
		getMainPopupMenu().setVisible(false);
		getTestSuitePopupMenu().setVisible(false);
		getTestCasePopupMenu().setVisible(false);
		getTCritVarPopupMenu().setVisible(false);
		getSimulationPopupMenu().setVisible(false);
	}
}
public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}

private void genTCritReportUserDefinedReference(ActionEvent e){
	tsRefreshListener.rememberSelectedNode();
	this.refireActionPerformed(e);
}
/**
 * connEtoC1:  (AddTestSuiteMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		this.refireActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (GenerateTCReportMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (RunSimMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
	try {
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (EditTCrMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (JTree1.mouse.mouseClicked(java.awt.event.MouseEvent) --> TestingFrameworkPanel.actionsOnMouseClick(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionsOnMouseClick(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (selectionModel.treeSelection. --> TestingFrameworkPanel.getTreeSelection()Ljava.lang.Object;)
 * @return java.lang.Object
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.Object connEtoC14() {
	Object connEtoC14Result = null;
	try {
		// user code begin {1}
		// user code end
		connEtoC14Result = this.getTreeSelection();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoC14Result;
}
/**
 * connEtoC15:  (RemoveTSMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC16:  (CompareMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.fireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC17:  (GenerateTCRiteportMenuItem1.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC18:  (ViewMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.fireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RefreshAllJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (AddTestCaseMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (RunAllMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (DuplicateTSMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (GenTSReportMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (LoadMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (RemoveMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (RunSimsMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		tsRefreshListener.rememberSelectedNode();
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (documentManager1.this --> testingFrmwkTreeModel1.documentManager)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		if ((getdocumentManager1() != null)) {
			gettestingFrmwkTreeModel1().setDocumentManager(getdocumentManager1());
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
 * connPtoP1SetTarget:  (numericsTestCellRenderer.this <--> JTree1.cellRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setCellRenderer(getnumericsTestCellRenderer());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetSource:  (TestingFrameworkPanel.documentManager <--> documentManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getdocumentManager1() != null)) {
				this.setDocumentManager(getdocumentManager1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (TestingFrameworkPanel.documentManager <--> documentManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setdocumentManager1(this.getDocumentManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetSource:  (TestingFrameworkPanel.testingFrameworkWindowManager <--> testingFrameworkWindowManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((gettestingFrameworkWindowManager1() != null)) {
				this.setTestingFrameworkWindowManager(gettestingFrameworkWindowManager1());
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
 * connPtoP3SetTarget:  (TestingFrameworkPanel.testingFrameworkWindowManager <--> testingFrameworkWindowManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			settestingFrameworkWindowManager1(this.getTestingFrameworkWindowManager());
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
 * connPtoP4SetTarget:  (testingFrmwkTreeModel1.this <--> JTree1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setModel(gettestingFrmwkTreeModel1());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetSource:  (JTree1.selectionModel <--> selectionModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getselectionModel() != null)) {
				getJTree1().setSelectionModel(getselectionModel());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetTarget:  (JTree1.selectionModel <--> selectionModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			setselectionModel(getJTree1().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Method to support listener events.
 */
protected void fireActionPerformed(java.awt.event.ActionEvent e) {
	if (aActionListener == null) {
		return;
	};
	aActionListener.actionPerformed(e);
}
/**
 * Return the AddTestCaseMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAddTestCaseMenuItem() {
	if (ivjAddTestCaseMenuItem == null) {
		try {
			ivjAddTestCaseMenuItem = new javax.swing.JMenuItem();
			ivjAddTestCaseMenuItem.setName("AddTestCaseMenuItem");
			ivjAddTestCaseMenuItem.setText(ADD_TESTCASE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddTestCaseMenuItem;
}
/**
 * Return the AddTestSuiteMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAddTestSuiteMenuItem() {
	if (ivjAddTestSuiteMenuItem == null) {
		try {
			ivjAddTestSuiteMenuItem = new javax.swing.JMenuItem();
			ivjAddTestSuiteMenuItem.setName("AddTestSuiteMenuItem");
			ivjAddTestSuiteMenuItem.setText(ADD_TESTSUITE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddTestSuiteMenuItem;
}

private javax.swing.JMenuItem getChangeTypeToSteadyMenuItem() {
	if (ivjChangeTypeToSteadyMenuItem == null) {
		try {
			ivjChangeTypeToSteadyMenuItem = new javax.swing.JMenuItem();
			ivjChangeTypeToSteadyMenuItem.setName("ChangeTypeToSteadyMenuItem");
			ivjChangeTypeToSteadyMenuItem.setText(TOGGLE_STEADYSTATE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChangeTypeToSteadyMenuItem;
}

private javax.swing.JMenuItem getEditAnnotationTestCaseMenuItem() {
	if (ivjEditAnnotationTestCaseMenuItem == null) {
		try {
			ivjEditAnnotationTestCaseMenuItem = new javax.swing.JMenuItem();
			ivjEditAnnotationTestCaseMenuItem.setName("EditAnnotationTestCase");
			ivjEditAnnotationTestCaseMenuItem.setText(EDIT_ANNOT_TESTCASE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditAnnotationTestCaseMenuItem;
}


private javax.swing.JMenuItem getEditAnnotationTestSuiteMenuItem() {
	if (ivjEditAnnotationTestSuiteMenuItem == null) {
		try {
			ivjEditAnnotationTestSuiteMenuItem = new javax.swing.JMenuItem();
			ivjEditAnnotationTestSuiteMenuItem.setName("EditAnnotationTestSuite");
			ivjEditAnnotationTestSuiteMenuItem.setText(EDIT_ANNOT_TESTSUITE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditAnnotationTestSuiteMenuItem;
}

/**
 * Return the CompareMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getCompareMenuItem() {
	if (ivjCompareMenuItem == null) {
		try {
			ivjCompareMenuItem = new javax.swing.JMenuItem();
			ivjCompareMenuItem.setName("CompareMenuItem");
			ivjCompareMenuItem.setText(COMPARERREGR_INTERNALREF_TESTCRITERIA);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCompareMenuItem;
}

private javax.swing.JMenuItem getCompareUserDefinedMenuItem() {
	if (ivjCompareUserDefinedMenuItem == null) {
		try {
			ivjCompareUserDefinedMenuItem = new javax.swing.JMenuItem();
			ivjCompareUserDefinedMenuItem.setName("CompareUserDefinedMenuItem");
			ivjCompareUserDefinedMenuItem.setText(COMPARERREGR_USERDEFREF_TESTCRITERIA);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCompareUserDefinedMenuItem;
}
/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}
/**
 * Return the documentManager1 property value.
 * @return cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DocumentManager getdocumentManager1() {
	// user code begin {1}
	// user code end
	return ivjdocumentManager1;
}
/**
 * Return the DuplicateTSMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getDuplicateTSMenuItem() {
	if (ivjDuplicateTSMenuItem == null) {
		try {
			ivjDuplicateTSMenuItem = new javax.swing.JMenuItem();
			ivjDuplicateTSMenuItem.setName("DuplicateTSMenuItem");
			ivjDuplicateTSMenuItem.setText(DUPLICATE_TESTSUITE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDuplicateTSMenuItem;
}
/**
 * Return the EditTCrMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getEditTCrMenuItem() {
	if (ivjEditTCrMenuItem == null) {
		try {
			ivjEditTCrMenuItem = new javax.swing.JMenuItem();
			ivjEditTCrMenuItem.setName("EditTCrMenuItem");
			ivjEditTCrMenuItem.setText(EDIT_TESTCRITERIA);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditTCrMenuItem;
}
/**
 * Return the GenerateTCReportMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getGenerateTCReportMenuItem() {
	if (ivjGenerateTCReportMenuItem == null) {
		try {
			ivjGenerateTCReportMenuItem = new javax.swing.JMenuItem();
			ivjGenerateTCReportMenuItem.setName("GenerateTCReportMenuItem");
			ivjGenerateTCReportMenuItem.setText("Generate TestCase Report");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenerateTCReportMenuItem;
}
/**
 * Return the GenerateTCRiteportMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getGenerateTCRitReportMenuItem1() {
	if (ivjGenerateTCRiteportMenuItem1 == null) {
		try {
			ivjGenerateTCRiteportMenuItem1 = new javax.swing.JMenuItem();
			ivjGenerateTCRiteportMenuItem1.setName("GenerateTCRiteportMenuItem1");
			ivjGenerateTCRiteportMenuItem1.setText(GENTCRITREPORT_INTERNALREF_TESTCRITERIA);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenerateTCRiteportMenuItem1;
}

private javax.swing.JMenuItem getGenerateTCRitReportUserDefinedReferenceMenuItem1() {
	if (ivjGenerateTCRiteportUserDefinedReferenceMenuItem1 == null) {
		try {
			ivjGenerateTCRiteportUserDefinedReferenceMenuItem1 = new javax.swing.JMenuItem();
			ivjGenerateTCRiteportUserDefinedReferenceMenuItem1.setName("GenerateTCRiteportUserDefinedReferenceMenuItem1");
			ivjGenerateTCRiteportUserDefinedReferenceMenuItem1.setText(GENTCRITREPORT_USERDEFREF_TESTCRITERIA);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenerateTCRiteportUserDefinedReferenceMenuItem1;
}

private javax.swing.JMenuItem getQueryTCritCrossRefMenuItem1() {
	if (ivjQueryTCritCrossRefMenuItem1 == null) {
		try {
			ivjQueryTCritCrossRefMenuItem1 = new javax.swing.JMenuItem();
			ivjQueryTCritCrossRefMenuItem1.setName("QueryTCritCrossRefMenuItem1");
			ivjQueryTCritCrossRefMenuItem1.setText(QUERY_TCRIT_CROSSREF);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryTCritCrossRefMenuItem1;
}

private javax.swing.JMenuItem getQueryTCritVarCrossRefMenuItem1() {
	if (ivjQueryTCritVarCrossRefMenuItem1 == null) {
		try {
			ivjQueryTCritVarCrossRefMenuItem1 = new javax.swing.JMenuItem();
			ivjQueryTCritVarCrossRefMenuItem1.setName("QueryTCritVarCrossRefMenuItem1");
			ivjQueryTCritVarCrossRefMenuItem1.setText(QUERY_TCRITVAR_CROSSREF);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryTCritVarCrossRefMenuItem1;
}


/**
 * Return the GenTSReportMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getGenTSReportMenuItem() {
	if (ivjGenTSReportMenuItem == null) {
		try {
			ivjGenTSReportMenuItem = new javax.swing.JMenuItem();
			ivjGenTSReportMenuItem.setName("GenTSReportMenuItem");
			ivjGenTSReportMenuItem.setText("Generate TestSuite Report");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenTSReportMenuItem;
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
			getJScrollPane1().setViewportView(getJTree1());
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
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new javax.swing.JTree();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setBounds(0, 0, 78, 72);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}
/**
 * Return the LoadMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getLoadMenuItem() {
	if (ivjLoadMenuItem == null) {
		try {
			ivjLoadMenuItem = new javax.swing.JMenuItem();
			ivjLoadMenuItem.setName("LoadMenuItem");
			ivjLoadMenuItem.setText("Load Model");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadMenuItem;
}
/**
 * Return the MainPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getMainPopupMenu() {
	if (ivjMainPopupMenu == null) {
		try {
			ivjMainPopupMenu = new javax.swing.JPopupMenu();
			ivjMainPopupMenu.setName("MainPopupMenu");
			ivjMainPopupMenu.add(getAddTestSuiteMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMainPopupMenu;
}


private javax.swing.JPopupMenu getTCritVarPopupMenu() {
	if (ivjTCritVarPopupMenu == null) {
		try {
			ivjTCritVarPopupMenu = new javax.swing.JPopupMenu();
			ivjTCritVarPopupMenu.setName("TCritVarPopupMenu");
			ivjTCritVarPopupMenu.add(getQueryTCritVarCrossRefMenuItem1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTCritVarPopupMenu;
}


/**
 * Return the MathModelPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getTestCasePopupMenu() {
	if (ivjTestCasePopupMenu == null) {
		try {
			ivjTestCasePopupMenu = new javax.swing.JPopupMenu();
			ivjTestCasePopupMenu.setName("TestCasePopupMenu");
			ivjTestCasePopupMenu.add(getRefreshTestCaseJMenuItem());
			ivjTestCasePopupMenu.add(getLoadMenuItem());
			ivjTestCasePopupMenu.add(getRemoveMenuItem());
			ivjTestCasePopupMenu.add(getRunSimsMenuItem());
			ivjTestCasePopupMenu.add(getGenerateTCReportMenuItem());
			ivjTestCasePopupMenu.add(getChangeTypeToSteadyMenuItem());
			ivjTestCasePopupMenu.add(getEditAnnotationTestCaseMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTestCasePopupMenu;
}
/**
 * Comment
 */
public TreePath[] getSelectedTreePaths() {
	TreeSelectionModel treeSelectionModel = getselectionModel();
	return treeSelectionModel.getSelectionPaths();
}
/**
 * Return the numericsTestCellRenderer property value.
 * @return cbit.vcell.numericstest.gui.NumericsTestCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private NumericsTestCellRenderer getnumericsTestCellRenderer() {
	if (ivjnumericsTestCellRenderer == null) {
		try {
			ivjnumericsTestCellRenderer = new NumericsTestCellRenderer();
			ivjnumericsTestCellRenderer.setName("numericsTestCellRenderer");
			ivjnumericsTestCellRenderer.setText("numericsTestCellRenderer");
			ivjnumericsTestCellRenderer.setBounds(571, 294, 153, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjnumericsTestCellRenderer;
}
/**
 * Return the RefreshAllJMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRefreshTestSuiteJMenuItem() {
	if (ivjRefreshTestSuiteJMenuItem == null) {
		try {
			ivjRefreshTestSuiteJMenuItem = new javax.swing.JMenuItem();
			ivjRefreshTestSuiteJMenuItem.setName("RefreshAllJMenuItem");
			ivjRefreshTestSuiteJMenuItem.setText(REFRESH_TESTSUITE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshTestSuiteJMenuItem;
}


private javax.swing.JMenuItem getRefreshTestCaseJMenuItem() {
	if (ivjRefreshTestCaseJMenuItem == null) {
		try {
			ivjRefreshTestCaseJMenuItem = new javax.swing.JMenuItem();
			ivjRefreshTestCaseJMenuItem.setName("RefreshAllJMenuItem");
			ivjRefreshTestCaseJMenuItem.setText(REFRESH_TESTSUITE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshTestCaseJMenuItem;
}


private javax.swing.JMenuItem getRefreshTestCriteriaJMenuItem() {
	if (ivjRefreshTestCriteriaJMenuItem == null) {
		try {
			ivjRefreshTestCriteriaJMenuItem = new javax.swing.JMenuItem();
			ivjRefreshTestCriteriaJMenuItem.setName("RefreshAllJMenuItem");
			ivjRefreshTestCriteriaJMenuItem.setText(REFRESH_TESTSUITE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshTestCriteriaJMenuItem;
}


/**
 * Return the RemoveMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRemoveMenuItem() {
	if (ivjRemoveMenuItem == null) {
		try {
			ivjRemoveMenuItem = new javax.swing.JMenuItem();
			ivjRemoveMenuItem.setName("RemoveMenuItem");
			ivjRemoveMenuItem.setText(REMOVE_TESTCASE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveMenuItem;
}
/**
 * Return the RemoveTSMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRemoveTSMenuItem() {
	if (ivjRemoveTSMenuItem == null) {
		try {
			ivjRemoveTSMenuItem = new javax.swing.JMenuItem();
			ivjRemoveTSMenuItem.setName("RemoveTSMenuItem");
			ivjRemoveTSMenuItem.setText(REMOVE_TESTSUITE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveTSMenuItem;
}
/**
 * Return the RunAllMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRunAllMenuItem() {
	if (ivjRunAllMenuItem == null) {
		try {
			ivjRunAllMenuItem = new javax.swing.JMenuItem();
			ivjRunAllMenuItem.setName("RunAllMenuItem");
			ivjRunAllMenuItem.setText("Run All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunAllMenuItem;
}
/**
 * Return the RunSimMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRunSimMenuItem() {
	if (ivjRunSimMenuItem == null) {
		try {
			ivjRunSimMenuItem = new javax.swing.JMenuItem();
			ivjRunSimMenuItem.setName("RunSimMenuItem");
			ivjRunSimMenuItem.setText("Run");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunSimMenuItem;
}
/**
 * Return the RunSimsMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRunSimsMenuItem() {
	if (ivjRunSimsMenuItem == null) {
		try {
			ivjRunSimsMenuItem = new javax.swing.JMenuItem();
			ivjRunSimsMenuItem.setName("RunSimsMenuItem");
			ivjRunSimsMenuItem.setText("Run Simulations");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunSimsMenuItem;
}
/**
 * Return the selectionModel property value.
 * @return javax.swing.tree.TreeSelectionModel
 */
private javax.swing.tree.TreeSelectionModel getselectionModel() {
	return ivjselectionModel;
}
/**
 * Return the SimulationPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getSimulationPopupMenu() {
	if (ivjSimulationPopupMenu == null) {
		try {
			ivjSimulationPopupMenu = new javax.swing.JPopupMenu();
			ivjSimulationPopupMenu.setName("SimulationPopupMenu");
			ivjSimulationPopupMenu.add(getRefreshTestCriteriaJMenuItem());
			ivjSimulationPopupMenu.add(getRunSimMenuItem());
			ivjSimulationPopupMenu.add(getViewMenuItem());
			ivjSimulationPopupMenu.add(getCompareMenuItem());
			ivjSimulationPopupMenu.add(getCompareUserDefinedMenuItem());
			ivjSimulationPopupMenu.add(getEditTCrMenuItem());
			ivjSimulationPopupMenu.add(getGenerateTCRitReportMenuItem1());
			ivjSimulationPopupMenu.add(getGenerateTCRitReportUserDefinedReferenceMenuItem1());
			ivjSimulationPopupMenu.add(getQueryTCritCrossRefMenuItem1());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSimulationPopupMenu;
}
/**
 * Comment
 */
public TestCaseNew getTestCaseOfSelectedCriteria() {
	TreeSelectionModel treeSelectionModel = getselectionModel();
	TreePath treePath = treeSelectionModel.getSelectionPath();
	if (treePath == null){
		return null;
	}

	BioModelNode selectedNode = (BioModelNode)treePath.getLastPathComponent();
	if (selectedNode.getUserObject() instanceof TestCriteriaNew) {
		TreePath parentPath = treePath.getParentPath();
		BioModelNode parentNode = (BioModelNode)parentPath.getLastPathComponent();	
		if (parentNode.getUserObject() instanceof TestCaseNew) {
			return (TestCaseNew)parentNode.getUserObject();
		}
	}
	return null;
}

public TestSuiteInfoNew getTestSuiteInfoOfSelectedTestCase(){
	TreeSelectionModel treeSelectionModel = getselectionModel();
	TreePath treePath = treeSelectionModel.getSelectionPath();
	return getTestSuiteInfoOfTreePath(treePath);
}


public TestSuiteInfoNew getTestSuiteInfoOfSelectedTestCriteria(){
	TreeSelectionModel treeSelectionModel = getselectionModel();
	TreePath treePath = treeSelectionModel.getSelectionPath();
	return getTestSuiteInfoOfTreePath(treePath);
}
/**
 * Gets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @return The testingFrameworkWindowManager property value.
 * @see #setTestingFrameworkWindowManager
 */
public TestingFrameworkWindowManager getTestingFrameworkWindowManager() {
	return fieldTestingFrameworkWindowManager;
}
/**
 * Return the testingFrameworkWindowManager1 property value.
 * @return cbit.vcell.client.TestingFrameworkWindowManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TestingFrameworkWindowManager gettestingFrameworkWindowManager1() {
	// user code begin {1}
	// user code end
	return ivjtestingFrameworkWindowManager1;
}
/**
 * Return the testingFrmwkTreeModel1 property value.
 * @return cbit.vcell.client.desktop.TestingFrmwkTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TestingFrmwkTreeModel gettestingFrmwkTreeModel1() {
	if (ivjtestingFrmwkTreeModel1 == null) {
		try {
			ivjtestingFrmwkTreeModel1 = new TestingFrmwkTreeModel(getJTree1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjtestingFrmwkTreeModel1;
}
/**
 * Comment
 */
public TestSuiteInfoNew getTestSuiteInfoOfTreePath(TreePath treePath) {
	if (treePath == null){
		return null;
	}

	TreePath parentPath = treePath;
	do{
		BioModelNode parentNode = (BioModelNode)parentPath.getLastPathComponent();	
		if (parentNode.getUserObject() instanceof TestSuiteInfoNew) {
			return (TestSuiteInfoNew)parentNode.getUserObject();
		}
	}while (((parentPath = parentPath.getParentPath()) != null));
	return null;
}
/**
 * Return the TestSuitePopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getTestSuitePopupMenu() {
	if (ivjTestSuitePopupMenu == null) {
		try {
			ivjTestSuitePopupMenu = new javax.swing.JPopupMenu();
			ivjTestSuitePopupMenu.setName("TestSuitePopupMenu");
			ivjTestSuitePopupMenu.add(getRefreshTestSuiteJMenuItem());
			ivjTestSuitePopupMenu.add(getAddTestCaseMenuItem());
			ivjTestSuitePopupMenu.add(getRunAllMenuItem());
			ivjTestSuitePopupMenu.add(getDuplicateTSMenuItem());
			ivjTestSuitePopupMenu.add(getRemoveTSMenuItem());
			ivjTestSuitePopupMenu.add(getGenTSReportMenuItem());
			ivjTestSuitePopupMenu.add(getEditAnnotationTestSuiteMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTestSuitePopupMenu;
}
/**
 * Comment
 */
public Object getTreeSelection() {
	TreeSelectionModel treeSelectionModel = getselectionModel();
	TreePath treePath = treeSelectionModel.getSelectionPath();
	if (treePath == null){
		return null;
	}
	BioModelNode bioModelNode = (BioModelNode)treePath.getLastPathComponent();
	Object object = bioModelNode.getUserObject();
	return object;
}
/**
 * Return the ViewMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getViewMenuItem() {
	if (ivjViewMenuItem == null) {
		try {
			ivjViewMenuItem = new javax.swing.JMenuItem();
			ivjViewMenuItem.setName("ViewMenuItem");
			ivjViewMenuItem.setText("View Results");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewMenuItem;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	getAddTestSuiteMenuItem().addActionListener(ivjEventHandler);
	getAddTestCaseMenuItem().addActionListener(ivjEventHandler);
	getRunAllMenuItem().addActionListener(ivjEventHandler);
	getDuplicateTSMenuItem().addActionListener(ivjEventHandler);
	getGenTSReportMenuItem().addActionListener(ivjEventHandler);
	getLoadMenuItem().addActionListener(ivjEventHandler);
	getRemoveMenuItem().addActionListener(ivjEventHandler);
	getRunSimsMenuItem().addActionListener(ivjEventHandler);
	getGenerateTCReportMenuItem().addActionListener(ivjEventHandler);
	getRunSimMenuItem().addActionListener(ivjEventHandler);
	getEditTCrMenuItem().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getJTree1().addMouseListener(ivjEventHandler);
	getJTree1().addPropertyChangeListener(ivjEventHandler);
	getRemoveTSMenuItem().addActionListener(ivjEventHandler);
	getRefreshTestSuiteJMenuItem().addActionListener(ivjEventHandler);
	getRefreshTestCaseJMenuItem().addActionListener(ivjEventHandler);
	getRefreshTestCriteriaJMenuItem().addActionListener(ivjEventHandler);
	getGenerateTCRitReportMenuItem1().addActionListener(ivjEventHandler);
	getGenerateTCRitReportUserDefinedReferenceMenuItem1().addActionListener(ivjEventHandler);
	getCompareMenuItem().addActionListener(ivjEventHandler);
	getCompareUserDefinedMenuItem().addActionListener(ivjEventHandler);
	getViewMenuItem().addActionListener(ivjEventHandler);
	getChangeTypeToSteadyMenuItem().addActionListener(ivjEventHandler);
	getEditAnnotationTestCaseMenuItem().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP5SetTarget();
	connPtoP4SetTarget();
	gettestingFrmwkTreeModel1().addPropertyChangeListener(tsRefreshListener);
	getJTree1().addTreeExpansionListener(ivjEventHandler);
	getEditAnnotationTestSuiteMenuItem().addActionListener(ivjEventHandler);
	getQueryTCritCrossRefMenuItem1().addActionListener(ivjEventHandler);
	getQueryTCritVarCrossRefMenuItem1().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("TestingFrameworkPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(477, 532);
		add(getJScrollPane1(), "Center");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TestingFrameworkPanel aTestingFrameworkPanel;
		aTestingFrameworkPanel = new TestingFrameworkPanel();
		frame.setContentPane(aTestingFrameworkPanel);
		frame.setSize(aTestingFrameworkPanel.getSize());
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
 * Comment
 */
public void refireActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireActionPerformed(new ActionEvent(this, actionEvent.getID(), actionEvent.getActionCommand(), actionEvent.getModifiers()));
}
/**
 * Comment
 */
public void refreshTFTree(TestSuiteInfoNew tsin) {
	gettestingFrmwkTreeModel1().refreshTree(tsin);
}
public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}

public void selectInTreeView(final BigDecimal testSuiteKey,final BigDecimal testCaseKey,final BigDecimal testCriteriaKey){
	
	new Thread(
		new Runnable() {
			public void run() {
				long WAIT_TIME_MILLISEC = 20000;
				long startTime = System.currentTimeMillis();
				boolean UPDATE_IN_PROGRESS = false;
				do {
					if((System.currentTimeMillis()-startTime) >= WAIT_TIME_MILLISEC){
						return;
					}
					final BioModelNode rootNode = (BioModelNode) getJTree1().getModel().getRoot();
					for (int i = 0; i < rootNode.getChildCount(); i++) {
						final int finalI = i;
						TestSuiteInfoNew tsInfo = (TestSuiteInfoNew) ((BioModelNode) rootNode.getChildAt(i)).getUserObject();
						if (tsInfo.getTSKey().equals(testSuiteKey)) {
							if (testCaseKey != null) {
								if (TestingFrameworkPanel.hasNullChild((BioModelNode) rootNode.getChildAt(i))) {
									if(UPDATE_IN_PROGRESS){
										break;
									}
									AsynchClientTask[] tasksArr = new AsynchClientTask[] {
										new TFUpdateRunningStatus(getTestingFrameworkWindowManager(),tsInfo),
										new TFRefresh(getTestingFrameworkWindowManager(),tsInfo)
									};
									ClientTaskDispatcher.dispatch(TestingFrameworkPanel.this,new Hashtable<String, Object>(), tasksArr, true);
									UPDATE_IN_PROGRESS = true;
									break;
								}
								UPDATE_IN_PROGRESS = false;
								for (int j = 0; j < rootNode.getChildAt(i).getChildCount(); j++) {
									final int finalJ = j;
									TestCaseNew tcase = (TestCaseNew) ((BioModelNode) rootNode.getChildAt(i).getChildAt(j)).getUserObject();
									if (tcase.getTCKey().equals(testCaseKey)) {
										if (testCriteriaKey != null) {
											for (int k = 0; k < rootNode.getChildAt(i).getChildAt(j).getChildCount(); k++) {
												final int finalK = k;
												TestCriteriaNew tcrit = (TestCriteriaNew) ((BioModelNode) rootNode
														.getChildAt(i)
														.getChildAt(j)
														.getChildAt(k))
														.getUserObject();
												if (tcrit.getTCritKey().equals(testCriteriaKey)) {
													SwingUtilities.invokeLater(new Runnable() {
														public void run() {TreePath treePath = new TreePath(
															((DefaultTreeModel) getJTree1().getModel())
																.getPathToRoot(rootNode.getChildAt(
																				finalI).getChildAt(
																				finalJ).getChildAt(finalK)));
															getJTree1().setSelectionPath(treePath);
															getJTree1().scrollPathToVisible(treePath);
																}
															});
													break;
												}
											}
											;
										} else {
											SwingUtilities.invokeLater(new Runnable() {
												public void run() {
													TreePath treePath = new TreePath(
															((DefaultTreeModel) getJTree1().getModel()).getPathToRoot(rootNode
																	.getChildAt(finalI).getChildAt(finalJ)));
													getJTree1().setSelectionPath(treePath);
													getJTree1().scrollPathToVisible(treePath);
														}
													});
											break;
										}
									}									
								}
							} else {
								SwingUtilities.invokeLater(
								new Runnable() {
									public void run() {
										TreePath treePath = new TreePath(
												((DefaultTreeModel) getJTree1().getModel()).getPathToRoot(rootNode.getChildAt(finalI)));
										getJTree1().setSelectionPath(treePath);
										getJTree1().scrollPathToVisible(treePath);
									}
								});						
								break;
							}
						}
					}
				} while (UPDATE_IN_PROGRESS);
			}
		}
	).start();
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
 * Set the documentManager1 to a new value.
 * @param newValue cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocumentManager1(DocumentManager newValue) {
	if (ivjdocumentManager1 != newValue) {
		try {
			DocumentManager oldValue = getdocumentManager1();
			ivjdocumentManager1 = newValue;
			connPtoP2SetSource();
			connEtoM1(ivjdocumentManager1);
			firePropertyChange("documentManager", oldValue, newValue);
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
 * Set the selectionModel to a new value.
 * @param newValue javax.swing.tree.TreeSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel(javax.swing.tree.TreeSelectionModel newValue) {
	if (ivjselectionModel != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel != null) {
				ivjselectionModel.removeTreeSelectionListener(ivjEventHandler);
			}
			ivjselectionModel = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel != null) {
				ivjselectionModel.addTreeSelectionListener(ivjEventHandler);
			}
			connPtoP5SetSource();
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
 * Sets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @param testingFrameworkWindowManager The new value for the property.
 * @see #getTestingFrameworkWindowManager
 */
public void setTestingFrameworkWindowManager(TestingFrameworkWindowManager testingFrameworkWindowManager) {
	TestingFrameworkWindowManager oldValue = fieldTestingFrameworkWindowManager;
	fieldTestingFrameworkWindowManager = testingFrameworkWindowManager;
	firePropertyChange("testingFrameworkWindowManager", oldValue, testingFrameworkWindowManager);
}
/**
 * Set the testingFrameworkWindowManager1 to a new value.
 * @param newValue cbit.vcell.client.TestingFrameworkWindowManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void settestingFrameworkWindowManager1(TestingFrameworkWindowManager newValue) {
	if (ivjtestingFrameworkWindowManager1 != newValue) {
		try {
			TestingFrameworkWindowManager oldValue = gettestingFrameworkWindowManager1();
			ivjtestingFrameworkWindowManager1 = newValue;
			connPtoP3SetSource();
			firePropertyChange("testingFrameworkWindowManager", oldValue, newValue);
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
