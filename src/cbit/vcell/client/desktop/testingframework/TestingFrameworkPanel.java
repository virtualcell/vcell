package cbit.vcell.client.desktop.testingframework;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.mathmodel.MathModelInfo;
import javax.swing.tree.TreeNode;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;

/**
 * Insert the type's description here.
 * Creation date: (7/22/2004 6:19:16 PM)
 * @author: Anuradha Lakshminarayana
 */
public class TestingFrameworkPanel extends javax.swing.JPanel {
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTree ivjJTree1 = null;
	private cbit.vcell.numericstest.gui.NumericsTestCellRenderer ivjnumericsTestCellRenderer = null;
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
	private javax.swing.JPopupMenu ivjMathModelPopupMenu = null;
	private javax.swing.JMenuItem ivjRemoveMenuItem = null;
	private javax.swing.JMenuItem ivjRunSimMenuItem = null;
	private javax.swing.JMenuItem ivjRunSimsMenuItem = null;
	private javax.swing.JPopupMenu ivjSimulationPopupMenu = null;
	private TestingFrmwkTreeModel ivjtestingFrmwkTreeModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	protected transient java.awt.event.ActionListener aActionListener = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private cbit.vcell.clientdb.DocumentManager ivjdocumentManager1 = null;
	private cbit.vcell.client.TestingFrameworkWindowManager ivjtestingFrameworkWindowManager1 = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.tree.TreeSelectionModel ivjselectionModel = null;
	private javax.swing.JMenuItem ivjRemoveTSMenuItem = null;
	private javax.swing.JMenuItem ivjRefreshAllJMenuItem = null;
	private javax.swing.JMenuItem ivjGenerateTCRiteportMenuItem1 = null;
	private javax.swing.JMenuItem ivjCompareMenuItem = null;
	private javax.swing.JMenuItem ivjViewMenuItem = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener {
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
			if (e.getSource() == TestingFrameworkPanel.this.getRefreshAllJMenuItem()) 
				connEtoC2(e);
			if (e.getSource() == TestingFrameworkPanel.this.getGenerateTCRiteportMenuItem1()) 
				connEtoC17(e);
			if (e.getSource() == TestingFrameworkPanel.this.getCompareMenuItem()) 
				connEtoC16(e);
			if (e.getSource() == TestingFrameworkPanel.this.getViewMenuItem()) 
				connEtoC18(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == TestingFrameworkPanel.this.getJTree1()) 
				connEtoC13(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
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
		};
	};
/**
 * TestingFrameworkPanel constructor comment.
 */
public TestingFrameworkPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
private void actionsOnMouseClick(MouseEvent mouseEvent) {

	if (javax.swing.SwingUtilities.isRightMouseButton(mouseEvent)) {
		if (getTreeSelection() instanceof String) {
			if (((String)getTreeSelection()).equals("TestSuites")) {
				getMainPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
			}
		} else if (getTreeSelection() instanceof TestSuiteInfoNew) {
			if(((TreeNode)getJTree1().getSelectionPath().getLastPathComponent()).getChildCount() == 0){
				getDuplicateTSMenuItem().setEnabled(false);
				getRunAllMenuItem().setEnabled(false);
				getGenTSReportMenuItem().setEnabled(false);
			}else{
				getDuplicateTSMenuItem().setEnabled(true);
				getRunAllMenuItem().setEnabled(true);
				getGenTSReportMenuItem().setEnabled(true);
			}
			getTestSuitePopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
		} else if (getTreeSelection() instanceof TestCaseNew) {
			getMathModelPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
		} else if (getTreeSelection() instanceof TestCriteriaNew) {
			TestCriteriaNew testCriteria = (TestCriteriaNew)getTreeSelection();
			if (testCriteria.getRegressionSimInfo() == null) {
				getCompareMenuItem().setEnabled(false);
			} else {
				getCompareMenuItem().setEnabled(true);
			}
			getSimulationPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
		}
	}
}
public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}
/**
 * connEtoC1:  (AddTestSuiteMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC10:  (GenerateTCReportMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
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
 * connEtoC11:  (RunSimMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
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
 * connEtoC12:  (EditTCrMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkPanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
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
private void connEtoM1(cbit.vcell.clientdb.DocumentManager value) {
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
			ivjAddTestCaseMenuItem.setText("Add TestCase");
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
			ivjAddTestSuiteMenuItem.setText("Add TestSuite");
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
			ivjCompareMenuItem.setText("Compare With Regression");
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
/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}
/**
 * Return the documentManager1 property value.
 * @return cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.clientdb.DocumentManager getdocumentManager1() {
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
			ivjDuplicateTSMenuItem.setText("Duplicate TestSuite");
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
			ivjEditTCrMenuItem.setText("Edit Test Criteria");
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
private javax.swing.JMenuItem getGenerateTCRiteportMenuItem1() {
	if (ivjGenerateTCRiteportMenuItem1 == null) {
		try {
			ivjGenerateTCRiteportMenuItem1 = new javax.swing.JMenuItem();
			ivjGenerateTCRiteportMenuItem1.setName("GenerateTCRiteportMenuItem1");
			ivjGenerateTCRiteportMenuItem1.setText("Generate TestCriteria Report");
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
/**
 * Return the MathModelPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getMathModelPopupMenu() {
	if (ivjMathModelPopupMenu == null) {
		try {
			ivjMathModelPopupMenu = new javax.swing.JPopupMenu();
			ivjMathModelPopupMenu.setName("MathModelPopupMenu");
			ivjMathModelPopupMenu.add(getLoadMenuItem());
			ivjMathModelPopupMenu.add(getRemoveMenuItem());
			ivjMathModelPopupMenu.add(getRunSimsMenuItem());
			ivjMathModelPopupMenu.add(getGenerateTCReportMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelPopupMenu;
}
/**
 * Comment
 */
public Object[][] getMultiTreeSelection() {
	TreeSelectionModel treeSelectionModel = getselectionModel();
	TreePath[] treePaths = treeSelectionModel.getSelectionPaths();
	if (treePaths == null){
		return null;
	}
	Object[][] objArr = new Object[treePaths.length][];
	for(int i=0;i<treePaths.length;i+= 1){
		objArr[i] = null;
		if(treePaths[i].getLastPathComponent() instanceof BioModelNode){
			if(((BioModelNode)treePaths[i].getLastPathComponent()).getUserObject() instanceof TestCriteriaNew &&
				((BioModelNode)treePaths[i].getParentPath().getLastPathComponent()).getUserObject() instanceof TestCaseNew){
				objArr[i] = new Object[2];
				objArr[i][0] = ((BioModelNode)treePaths[i].getParentPath().getLastPathComponent()).getUserObject();
				objArr[i][1] = ((BioModelNode)treePaths[i].getLastPathComponent()).getUserObject();
			}
		}
	}
	return objArr;
}
/**
 * Return the numericsTestCellRenderer property value.
 * @return cbit.vcell.numericstest.gui.NumericsTestCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.numericstest.gui.NumericsTestCellRenderer getnumericsTestCellRenderer() {
	if (ivjnumericsTestCellRenderer == null) {
		try {
			ivjnumericsTestCellRenderer = new cbit.vcell.numericstest.gui.NumericsTestCellRenderer();
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
private javax.swing.JMenuItem getRefreshAllJMenuItem() {
	if (ivjRefreshAllJMenuItem == null) {
		try {
			ivjRefreshAllJMenuItem = new javax.swing.JMenuItem();
			ivjRefreshAllJMenuItem.setName("RefreshAllJMenuItem");
			ivjRefreshAllJMenuItem.setText("Refresh All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshAllJMenuItem;
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
			ivjRemoveMenuItem.setText("Remove");
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
			ivjRemoveTSMenuItem.setText("Remove TestSuite");
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.tree.TreeSelectionModel getselectionModel() {
	// user code begin {1}
	// user code end
	return ivjselectionModel;
}
/**
 * Return the SimulationPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getSimulationPopupMenu() {
	if (ivjSimulationPopupMenu == null) {
		try {
			ivjSimulationPopupMenu = new javax.swing.JPopupMenu();
			ivjSimulationPopupMenu.setName("SimulationPopupMenu");
			ivjSimulationPopupMenu.add(getRunSimMenuItem());
			ivjSimulationPopupMenu.add(getViewMenuItem());
			ivjSimulationPopupMenu.add(getCompareMenuItem());
			ivjSimulationPopupMenu.add(getEditTCrMenuItem());
			ivjSimulationPopupMenu.add(getGenerateTCRiteportMenuItem1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
/**
 * Gets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @return The testingFrameworkWindowManager property value.
 * @see #setTestingFrameworkWindowManager
 */
public cbit.vcell.client.TestingFrameworkWindowManager getTestingFrameworkWindowManager() {
	return fieldTestingFrameworkWindowManager;
}
/**
 * Return the testingFrameworkWindowManager1 property value.
 * @return cbit.vcell.client.TestingFrameworkWindowManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.client.TestingFrameworkWindowManager gettestingFrameworkWindowManager1() {
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
			ivjtestingFrmwkTreeModel1 = new cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel();
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
public TestSuiteInfoNew getTestSuiteInfoOfSelectedNode() {
	TreeSelectionModel treeSelectionModel = getselectionModel();
	TreePath treePath = treeSelectionModel.getSelectionPath();
	if (treePath == null){
		return null;
	}

	TreePath parentPath = null;
	while (((parentPath = treePath.getParentPath()) != null)) {
		BioModelNode parentNode = (BioModelNode)parentPath.getLastPathComponent();	
		if (parentNode.getUserObject() instanceof TestSuiteInfoNew) {
			return (TestSuiteInfoNew)parentNode.getUserObject();
		}
	}
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
			ivjTestSuitePopupMenu.add(getRefreshAllJMenuItem());
			ivjTestSuitePopupMenu.add(getAddTestCaseMenuItem());
			ivjTestSuitePopupMenu.add(getRunAllMenuItem());
			ivjTestSuitePopupMenu.add(getDuplicateTSMenuItem());
			ivjTestSuitePopupMenu.add(getRemoveTSMenuItem());
			ivjTestSuitePopupMenu.add(getGenTSReportMenuItem());
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
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
	getRefreshAllJMenuItem().addActionListener(ivjEventHandler);
	getGenerateTCRiteportMenuItem1().addActionListener(ivjEventHandler);
	getCompareMenuItem().addActionListener(ivjEventHandler);
	getViewMenuItem().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP5SetTarget();
	connPtoP4SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TestingFrameworkPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(477, 532);
		add(getJScrollPane1(), "Center");
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
		TestingFrameworkPanel aTestingFrameworkPanel;
		aTestingFrameworkPanel = new TestingFrameworkPanel();
		frame.setContentPane(aTestingFrameworkPanel);
		frame.setSize(aTestingFrameworkPanel.getSize());
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
 * Comment
 */
public void refireActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireActionPerformed(new ActionEvent(this, actionEvent.getID(), actionEvent.getActionCommand(), actionEvent.getModifiers()));
}
/**
 * Comment
 */
public void refreshTFTree() {
	gettestingFrmwkTreeModel1().refreshTree();
}
public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}
/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}
/**
 * Set the documentManager1 to a new value.
 * @param newValue cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocumentManager1(cbit.vcell.clientdb.DocumentManager newValue) {
	if (ivjdocumentManager1 != newValue) {
		try {
			cbit.vcell.clientdb.DocumentManager oldValue = getdocumentManager1();
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
public void setTestingFrameworkWindowManager(cbit.vcell.client.TestingFrameworkWindowManager testingFrameworkWindowManager) {
	cbit.vcell.client.TestingFrameworkWindowManager oldValue = fieldTestingFrameworkWindowManager;
	fieldTestingFrameworkWindowManager = testingFrameworkWindowManager;
	firePropertyChange("testingFrameworkWindowManager", oldValue, testingFrameworkWindowManager);
}
/**
 * Set the testingFrameworkWindowManager1 to a new value.
 * @param newValue cbit.vcell.client.TestingFrameworkWindowManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void settestingFrameworkWindowManager1(cbit.vcell.client.TestingFrameworkWindowManager newValue) {
	if (ivjtestingFrameworkWindowManager1 != newValue) {
		try {
			cbit.vcell.client.TestingFrameworkWindowManager oldValue = gettestingFrameworkWindowManager1();
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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D45715EC17ECE9CA9B32B109493AA9CD4C563434B58D79CA2DE9ECCB37EEE293BBA1DDF7C33AEEEAD353B5DB5B359BF7CBF79F83AA93D1093211C45A10289184918195918C3F8A8ABA4008A8888E4C83C666AFB3EFG9513BD775EF76EFB7366BDC631793E72FD07775E39676F1EFB6E39BF6F3E91F25F09DF17EE4E950474F5027CF7B8DD90264E8B4202B7F66FC0DC559E13C5D07D3D8B7006
	701F0ECC3836C0D96A47A6CB0E7072C7D9705C874F319F19AC5B61FE1B70E85B2EF3F0E320A72DDB9006D7FB6E18DE4FCEAB6A59C01FFBF7E543F5B340A54011EB429E22BF30ABCF46C74BF804F433A04497921A4F6F4A17F12B01A68BF0FDG3EC2C69F83D7E640ED55D5B2DD5F1D7304B5CF794D1B089C15CC0415B2EFF6913B96887F1D1ED8C4F4FD135AC9F81A60F98A00D4BEC318E7B98E579A6B58483121C127D4BABEA43ADC25C3AE276811CA9DE2E0CC727ACAA5B1A0B9BDA343FE3BDB1C707A472A2BED
	8CF39CB1D63BC7F489C259DE226BFFF7E5D15902309674AC2438CD7B08FE896EE381666EC57C416384AF941BACF9GE545D8675ABDB5F21DFA2EFDD3C8A9F7E7EEB128F3CBB12B2B2FD82973FC770B6637E87B8CF510F6C8003225GABG32CACC16C20096009DB4AEAEDB774235FED0EA743A5DDECF3D4F5760F2785B3D8E5185775555D0A263BA45A12F5FA1882C6D1F58D522E34FF440781E3EE9BE6613F9959887891DDF93D29F3B473CC147A729732E19AF15B0DFCC16A8BEE13AB3F79B691EDD929A27213A
	4F89EA5D79A7FE1231CEC7F7D565CFB292E4B90BD43AF3771136FE1C4640F240933E7F83E07845945F33CF4133185DC67164B9964A2ACAB1B6FAB623ADED198F8AB9876E5575436C6F451AD714B29B52CA35365CF90248AE5C4FF4D6F0DB9E237859AABC3365A80DD3721CD58671FB40E4E171B551C57AF0DD8574DDG89A096A081E0B1C0EA854658E3567DB798E3D63BDFEA88BADD8E51AFB0DED7471BF095A7A53F3D5593106C1EA1315B6EF75A87DDE2C010E9029D448703A1FCD7E96C8F010E9322C7745BA5
	27578352214BCA22BF40E87E32638DC26397E9294D69060EEDF77B84147B590EEDF0F5598352D91F43AE098A85AB3F2B033449383D9BA402G38E7FE19EBA37ECA037A67D61AAC3BAA518F1BBB093CD3229F72C5F5F51B57BB967459C8CD84291278F9476EAC9AC7A60B3592FD9CFF16601260F9A917535C39D5A52749B9C44C377B2FE09F7B763EC2382EG7A2C03A6CB89C08B40A8C08C40BC00E40055G5907B02E7E3D87632AE953D70512676F560D2B84087132C3AC1EDA8EE97B57B3D4AF315D87E5F1G09
	G4BG52G8A8E1BACF5G17G228E639872563EF8D97628EC93394F122FA587C37BE50B7C1CFDA83C3FE23D70B9C63EA7FE3819FCE31C3B1A382D9BE9BB307BF5AA9AED6E58AC77E12D3CADD41E723CC93EE65C221D1BF98CEEBEC7E2B09E7C3C90A075B046CED4E7816B0B8D7600083D6F0C9FBA0E68D0A20955775E938EB986B2647AD9643AEFD255E11BDC476D32EE22678FGC5G45G7BGF6836CA1FA811A81E0DAC37389498B8738FF3FC5752D81DB732BCC16A6G9740AC0005GABG320F18AC15GFD
	G51G89GA9GE9G3B2ACD16B6G89E08EC0B240AA005C23A6CB8D0083A096A071A846DE7D7138F425FE1BE7D0C7355F310E6AFAE37DAA26E92FFA795AFC036DFB7ECF90A3FF4E405AE69EED9B52AE6A7EF52BED730E406632BBD56D32FF9ADB0A64EB718D5A3E6F4F40FF1FA4309C7297DD447762B8CC131BC41F68F140CDC0AE0B69A2EDA40F5743DE4E73F90A7741785E8883EA0063E7F511B634CB125FB9981444AED1A2F3EB25FC8E5595888E7A1429816AFA280B4406F78246F7CEBF2F0B0C0DC3DE0FC7
	24490C334865DF26E33368925DE0F423B7681198FE3E0D2CED4644AB725CA05136DEA0E33BABC86699EC3DB05FCEE24F67F73AA1C512290AD0D2C3466D725DF9AA9F25C2FEA16B9E814AF2EBF0FC1D5809636B2E2B0F880B86FCE63DB9646C076FB0CBB5729A28C699DF19CD8F1F55377DABF40EA063834C56AF3DCD7A0441D205762BED42F0B3315B93F40BFE67D00050B642822813B428DF74B31A4AC3242F355B1D9E2B579774350B1EA0673F27E12B2C23AB6814C49581AB7F682D2D14D79A25F33D3072775E
	A3FD374B698E3A587C0C9330724F36644055619D8A120A355BBD769151FF00156DEE7AA3DCA5CD7D4EB9BD8E6F84D2221E4FD591BF8640C1B4A428B1D8767159B755F25C93E3B63FA8D21283B8161E68A1FE1F876D3190A025864752BD5D249C67E172A05A68F549FA202C251644475B25392AB6DED5436223924AD87C2CABD347CFDCADABA777C91CE6DB05914DD2CE7375AAA31A158ACD0D910DF08C66E74788CDF3935A1B874FE547949AD67ECBDE9E834F71E165F7B5E379FCF8DE2CAAE77E309CD77BC378C0
	A9E7FE4DBD17C35B6B43208853FDB2C1BE457B650EF3DBB5E55DF29FE565BB4F11BE6AD24D2D93BFA0BA4DAD385FE07B004DCFDDF01D821883C0E2A9290E5019B81DG4FF9GD5F54C3E49BA39CDA85DBD2DC817D98F7315FAA33A13A7112E86E8EC06F47F4369A42009B5247B46A924CBG1AE4C33A61B624DB81B4E906F4EF37A3DDFE8344E403915DB31C4E86B4AEC33ACF1DC63AD82019EBC8F7688C52A583CD2AA15DAFB9DD9A5064B606533176FD400AF4150D2C3C2D112DB9DCF27BB2BAC787520D4AF4D1
	0DECDD628A09033D1D5C4F0DACCE12E5FA21C92D77D5CED7574468FA1A589AA7D45E02AE5E3EF2F9EC9313B7CF16175FCC6802B624DB56446CDF5D4456CBC2B3231BC56916DC6075E8E6735D28E6053E145B97550BF44B1B191C55321DEB1B55761D6AC33A6C63B01786E8BA2E579EDBAEA2DDB4144F81D828CB779C27CB03725C9390DBA774686E6D67FE0172D80084DD3A5E8124DB856519AD61F4EC5C7B0FA2125F681E5EE57B38FD32B4B081093D3A7AA49D7A589E5D9C3A4EA6743BDAF0AFEEF1A14971FD70
	AC81443660387D63CD3897B7EBEC1E3076447399C67B0FF3DB189D0BDB14F1FB6A49098CB6065CF91E684CEA051C520A63478367547B81750100F748C987C2B91735772847F5066B6CF99D2E43CEFFC0EA05F1FA9273DECF8F31176CD548E8599F4BBB2A0570BDCE9C734938D7BA92F4D6D71FB61885603EE6C7A97A2C2495FDF37D385AB78B37DCB7EFB4700D231579A43AD571CD6A4E57B2180D6B73728D5A8CA7842C0D4A4BC89BA5030CD400B52D58F6F595841FFD9262962066A462D32B885E864FAE00E885
	DFC370F360B991E0893952FDAED7D336406685ABG17FF8A7275A902FF24094C8DC9BEE1759E2A40FA47DDFDD838F4777D06751EFDCA0E61D3CA3D5BEA8FE6B0FDEFB4233E55D0DE58067ACEB793FDA4AFB13A854D38FFFD89E8E637617EF53027C847EF2592CCE02A2B49B426CBB36511E3227975DB5B77D6CF12F00F3038856B32946C4BEAB7D9587C6A2FDBC8DD24D1E7000D7D1FAED60F7DF5C067G0861748B281CA1D584B21F7F263ECE66D972358705248D31E63DBD7151AF4DB64FE9E73ECEEED7FC9E77
	33974DF381182D3F23BA08AFF2CF43B88140ECEBAFD75B96FD1A6DE1A51CC65B86DB4B61EA279EADE5972B689F767A5D22231034C029FDC2AA256B2252FADA5CCC6E0B506F9D9315F0750B50FB45FA8DFB1BB17F5122EE6603873729FDD073F13F2E8FE2360C1A971D1667FD27959F4CCDF9511C1402BEF83295FDD0F9867A479974410FAA35BEA0FBF8F34FE0AC86CEA2DF9260D6F33E3B8E6A7195DA11EF4FA9642B811CE4C53E7307747892B85F8B1CEF916056F23ED78E6B7115F5A05F9DED48578438298E64
	3B7708160F6CCFA6F23E46F664DB8CB8219379AED66B71D5F5A25FEB1C2F8DF0511CEF67D1BD3E051C6FAB2711EFA960323AF87DF4796A3A10EF608C7259G974B796E2B5572113D57E44E374D0AFC4BG17E3C33E479B7478DAEC4877C3AB766FBE404D32E11FF832094420E60DA667044FF5A04F92204FBC0BBC0EE632BFB36D5ACD403C525549638678A5AEE3C1A759EBF1FB038131147EEFF4B90746427BE1BBA943EEA87743349D87F8BF8E1C7134F37630DE28F0C3A764B63D590575CAGFBD6F01B2EF769
	2DB3994F4F3A309E795D10673B1167240D4EFD54493A946D4C1A5C855791D18A496505C54CCA179DF27D19414B00BD4AF4BCE0C33BE2C1FEB257316EAC66E0763C6F2CDE1B319C7355BF2AF3CC167DB65D9C937B4A08B92D1B6516DD67149C137D7C0B660267B1477C76825A63821A3967F05C4ABDC62EF4EFC540F7ECD367D1EF5412C4A1EF5B67F47592C81575E576A8FA173D7422F955CB287711DE54EB831A589E74C3E55FBA9AB3ECACE434D29F36CBB2502575E0FFF8C7B73FD41EC7D9AF744DF4ACE0FA2B
	AE225E961099F59E75E668665FA42E778B97EFCDEF9A573B84E4E6DEC03D579A757AFF4D8554BB54FFEBFA977523DEAB480C61FAFFD52F27F791573BE7605674C68F205E9410195D0BFAF76B6AED6A45F783DF8CFBD75C532B3CC7F2296E9158BB541AA222AF2A173DC34D607863949F2742B37B1EA944D80D07324CBEFC6F1750C756B123709C8DB087A091A085E0CD9F2E9F1677107D6BE95F2B36FAA4D8DE58DDDDA19B28721A611E73C46F18F8C5FE3E7F3C3ADFB2BFF4176A3DB34F39A8777D0B61FEE0763F
	5DCF7CDB8565B6G97C0B4405C0BE87B3B97A25A5EBC29EBBB73E7F6B9DD8BDC547A79CD0ACF392875F3D5991F5BC3D9CBBF2CC9A9FD92DDBBA474B37ACDDC4E8FA9FE298A4F649C27F8723C864AAE8D209C6B9102DFB24068ADDC4EC50ADF2942B3B9AF513589F94E3CE4320CDE62F2EA291CCB0CFEA31753CF712BD4F8D98EDD4B1067EC3B496233E37C7A1BC83B2558EF7C5DA7630B22FC2BEE020F656F228B6AF3BA19EF1CB493DF6B656F7CC1795D6E20123FD94CFCFF00440C8B70B1G71GC9G4B86B1E6
	46AE14C40A99BC9BA0878B2B5376661979E2F7734D7A0271ADEA1E595918E7E648F720A5925F5A4150BAB03E8FA272658F0572496FC1EC64DD1B4EFA1D4CD364F7A2570B547B4C15C36CCC43A538CAGECCF61BB45F8D6AAF688735EDBF63297108C4F4B81568264BAE01E8DE08318F4E0ACFC6BC2C324D8284717CAB6AF3FED543E98C2DF2437EAFD9167882D3B3E8F3704717DD1731C6050736162F5BB74FDC836BBDAA551AD6F3BC417230F129D68234F8F929F2541F31E883E84E883708144824C9751C7593D
	3511FCC487CB1B3791F0ADDE6F180EFB64FA7EE0067EF9AE22FF160A21FC2C5E1987315EABC54CE9D7CE4E4C06CF0771E9EDA898563361A9EEC355B05A306F544CECB89A4627354127EBC35CA134A1065B702FB33461D791EDD824EBC3C1955A300C5BF0F75B4CECF8A80CCFEBC34E081E8D4F9CC19B8AC7500656761959F0AE0CCFEB43A5DD9BE6D5238D135C065F4F50060D27A359B0DF57068BC75106455C06C743E45D188DCFC634C198550D079A34A1EF94ED904F4C4C06C0981F56862B2E8DCBEB5106D1EE
	43DB56195930A70CCFEBC33C2E8D2B9A5006A4EE430F42E4E9C15F0697A2722D9E55633B2BE3E67A3E9811AF4B2947F73EF3E6BE9E89638B4B413A7A723BE6D63F3D91792AF4753D9246F7E375FBAD0C2FD473EC5555B757B6337AFDA3A25F28C69F1BBB2CF31039C394144D81C884D88A3086A0673249D2F999678E97FB0F8AB7B6F7E02B6E7019833373F797E6D6BFCBEFA4BE5BE53D7A5DA6127A3920AC86A09EE0A140F200B40002B12C5F57FBA34E9F79F1619B19BFFEBD223D7A75FCA2A2DF55189E5F553E
	1945E9CC98DFD83E9D534BF55D07F93E9D43DCF76C624CECE88B638B4B373AB6D4D772FC4BEDB0774F4C061CB0BE2D8D2BF5ED78C6A35A1065C29B7EFE06B624C434214E25E7437BFC5C6961B65CBEB0B39BE60771E9ED085335212F9EED1887656C7CFA92FD2F3E9C1E57F05C5DFB88AE4F8D6B94B75A5AD0C47623ED7C390DBE13770032DF8AFBB4B64C8408FCBDBC53716A1FC40751F9ED16E39E6FB3BEA73930CF7F1C64777A744FC9AE6EFB0361C1067AAB89ACDF5AB77DB94AE5FDFA67A8D99B39693BFD97
	78EB4A0D6DE6AA45F60BF1E34EFC2F5F38BEC57D7A75595F2FDF1F037D535727365F28BE2C7D36161318C960B114CF1F49F9ADB9B715CD573E898631F123A02FA32655F12BE779190E6F50F3FFCB405ED5G599E1325902085406161F3CECAC34E62644BE74C4879B1F206071C8F137BD845A5D9AE61C3F9C63A098E233296CDD9366AD91E6BF1DD1B0450B35CFAE01926EC230ECFC2F5F59FA1E785234106B9GC9GA95CAFC3340C1CFD2B144F2C1173E82CECE595E93B24E96A2905B99A5AE8D917F15B259F69
	51589C8934FE4A1846BFF27D697B5E35E0C72E57E4A981E881F0F8311E8EDACE4E5711B3D364FC95B9C3C74EE349FE28D147B061A732088C2D3EDC9D1CC3872763075A9E8D4E28DEFAF1220D81AD7C014A7B09196C037B039E496996E92AE17BEC62A4B96F8F33394E2007CD75C83689E8770B851CBFFEFF186411D8AF663C947A5ECBBBCFECF6B8A52F9F0558C43B7F4C70B01FA4F67944A14CE3CF8F9867CC5300FE4EDCB6201FB3FFBAB0FD4EFCF940A8E7B29F7DC2A2E742434E461F06D92E006721C7DBACB2
	0DD572DA8F8838DFB9C25F596B6CD7F68D793DAE97B9DDA7605C6071A2BEB7706219AB53DE7A7DA309AF009C9F465F770B271B7F967A4263CBBE67EBB7766BDE3B3EDF4B6CA49F853CC17F1028755DE1FB9EAFD3A679E4A31638075154D1FE496E9F9143FCFF027233B2A3FEAB7DFEE05069F07A6593622CDD7E614DAD34DD8381B1C097D5CDF649AE37C7D5AB36C73364EDD43E157ACC391E5FEAF47C956A37590346FE7B42203E5F9E9EB476DB42E0E43FBDBEE86C370503117D7603C12338E6BECA8B71D18577
	1165201E0F2C537808491BFFD2AD2F124BFBD6D71EA322BC47A9353C03DC5E5D077464CDC61417572616F7084B5BDD25A7AFB6223CA5A1728EF3F94F9D5113979FD1DED43BDADE9517F7DF351E3C440872AACF2B659D6172860E6A49DB9CD15E4A532F2B649DA8637E2B5113179AD15E1CB3A1728E203C67EB75642D0EA84FEA8D11D70E72FE5D20A7AF7343C872B2BB54B93D1C631F8A41D7F0FCDCE7087E8A44F7F529698FF27A82DB88FDA562DF30295A2D1D64AE561F37B80CF3501B8E7D9C146FB04EC1BB9D
	11F3D00943B887D5B8A267209A4774B9283CF7A37AE6586FF5434849786E900D6BFA0F28DF57B922F1DD9F92A357B5C1B42E6BE3E2643AAE943563942B634B3D6A762F64F5FF34AF247D8FE23C769E560B573C08719A3C286E6F653C3FFFD029A72FA4223CA37DA1769DC6F95FEC5413D797D15E6FC3659DC2F9453A7D5396D15EF787C264D521BCFB3D1E3C5108721E5DCD3EDDF4592FF80312A02C7C104C3FDE665FA8CE4173AC0005GCBBFE46F65599C6F5189526E6444B704B61D44A67B89EF429E64AD0367
	A6G87C0B440DCBF1E531A22E77DD433C179BB3D68D2FA769002BCBBC0FED36681B7BC4AF2DFA9C539D6A8F3GC4834C85C88E205C070F1175CB3D4341BFB344976472797223649B0ACEF158AF86C66BDDAE657DB9ABB777EED3789B6D81D1D3FEF4B3518F0B836065C5F259D8A661ED8A7ADC4EA172738FDD9A5E5F6F4B213A5D5EF1D5A1AB7B97B761BDA1FAECDD1D224F6B17B43C7DEE622FB62F5D914AF77B3B1B39CC2DBD416EAD4C56AE27BB20113775D89E53C7FE6BC234B506AAE5B47FD6361973EBE5FF
	4DC96C69F60A9321BC1F4AA0BAE171C3169D9A1D59F45ECA56C236C63F26AC37FDC728BD50F2EA0BB0BF7EF01864AA1ED76535880B7F85C5532D8DD6840C4E609DAFA6F5D99B501E8D33D2FC1624BD9B36ECAF1E8D4B0332A98977407E3998774016F3DCFCB96E15E58691D7F090F1B99C77B24765F3DC42A14495F2DCCD9562AAB96E17C790D747F1732B915746F163C7915723482BC11C0363D655A24E47F1D90DFC4F0F637CF40FBF861EE7F3DCB75DF31F8B4F099C778C65DD844FCBB92E016EC9274273AA0E
	1B2CC75C5AA01EA35F7CBE71B571FF663849D2B30E396765521073B064D4E7F5F50317FCD35B4692197C9E627DAC19DF9A4735E5B41519G4F49G2963781D4E647B781DCE425FBDA82CEA547FF6EB396D3AF94DB80B253C8965CCDA327BC5B353E95A03BAEDD09EBBC1F0C2217A7CA8099D3209DD8AD7D6DE9ED2CE668E9F8A6AF2C7C8B931775B996A72FBC24A090F6E1079D95D1E5E0DF5CBFBFA3E60FABC5A70F731D264BA2D152FF1AB6ECB2F5AF2AD0349F936D7FDEEAF67FE17B95DC0CE59A4632F19D4FC
	447C33E5BF7A47B7097E295D2F754F3BA175776E577A275D2CAE7F62FE2DFFAA5255654F6D577AE7C276BF0B3D9F97E15F1DB74977C00B49D814421F3B69B3799612BD7FF46F1F621D009EA8BADF7B44F450B3E2F4DC7DF5B10E2BABC1EF9AC07E9568EBGFDGEC9CCEA053CD015DBF6BD36E377B157B9381657E32245C1B024A7DEF47157BFD134A3D750AF27F192B4AFD42B5C140B167B307C367864FCEA9E56E4A50325FDCD74AFE5E98DAF67F47CA599D7521E5CF880A6EDDE6F24F5AA1777D191D35FCEF06
	FC3DB74D47462C6EBDA437CDDDE1E3D6BA478FD0FC0C8A4F726B6DBC3F4E023235D7704C60068C9293FDD7CD16C900B800F9G0B81D6DC453D4E1F3BE1A9BC7DBBF11B5757860B9917EA7B11556F9F775CEC7DD87E796E864CE3098B3EACA47DD513613714EB2F4AB97A9A3B967E6C3EF4F2E5753BBF13542F854A9CGB1G71G09GCB2EE17D76F9647343E44AD2AFC97621D17AE5AD5F8C06590F02A693E0A67B897ADB09CCC640C3FECB265BFED9F7A778A48CDD6C139C1BFDC409357DB76B1BB557C8DB2EAC
	AA1806E6396C879677A746491C1F1E40BD007B305BCBB0AF57CCE1DEEEAE516665BB85F5DE1DA8516665741072C7CA34F979EFC34ADFA85166E564E73E68A3BF43A35F0B2A7B3877BF293C287441C7BED19DACBEF2B20C577B5BB374577BFF48505F6F5F19B17DBB5D628C635F46A94FD07FB68E0B632756133611A09662G9281968324CEE17FD814711EC07B07CE5FE8F60C107D86728BFC925F0B48B42E6B17B3A37FEE5FE319E45DBB6815A42F1B48C77C37B2C9FBC9DE9FC5B25CF7B2090F7C4E11D109114A
	7864CCBA0F9507A9DA7E5EEDAF698B0DA2797685734C0408F9A67FAB8F89796F5419B13643724C147CED4BF5E51E2344D0FE494D76E579DD4FFAE3DF7D537A483EFAE9BD7DADG28236D0ACF5E277945FAD2CF6F70F0C064ED32EAFD8E4735862C5E0013EC603076F7E4954035876AA5814C86188F108C108A309AA06BA30C050FB6105F72534DF300E493F39A8FFE3B93FF07481BE5DC47712C48F57CB8CBBF9EEE4B8A0F075B337463A1AECB9D8F8CF737AC574FDFF6B17C5FE411B9B35BA7D13EBBE53F176B98
	2A0760C8F8D5D8A789F9C6BF11A4891B6A9D0E823EB5A1895978CC369AA4E183AC368B6ADDAEC9584637915464EF6182D9C19630153BA43CCEB6878A6868AB896B590E00A4ECA192151FCC8A00F5A1B208DEAEA28308A5E1ABE9BBDAD85068878D60F5C948E1825546EC14F7D118417921E2E5C6AEFA3B3CA5D0F04EA90D82F684B8490FE0028B48B08A18C050A511DCA5FF9F7431FA1D317636A15D5F3F227DAD0A753B4AA86D77C12F0A24EDFD000F0D42B82D95779945A9E42DAE379D6AE2F70D55FB9CE4ECEE
	38A209CA7E7C0DC8648710C6AEE4E57FC577E6AF2BF87E9FD0CB8788005DA683C69BGG24D9GGD0CB818294G94G88G88GB9FBB0B6005DA683C69BGG24D9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG009BGGGG
**end of data**/
}
}
