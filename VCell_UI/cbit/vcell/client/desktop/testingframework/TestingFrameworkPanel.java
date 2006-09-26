package cbit.vcell.client.desktop.testingframework;

import cbit.util.MathModelInfo;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.simulation.SimulationInfo;
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
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
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
	private cbit.vcell.client.database.DocumentManager ivjdocumentManager1 = null;
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
private void connEtoM1(cbit.vcell.client.database.DocumentManager value) {
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
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}
/**
 * Return the documentManager1 property value.
 * @return cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.client.database.DocumentManager getdocumentManager1() {
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
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}
/**
 * Set the documentManager1 to a new value.
 * @param newValue cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocumentManager1(cbit.vcell.client.database.DocumentManager newValue) {
	if (ivjdocumentManager1 != newValue) {
		try {
			cbit.vcell.client.database.DocumentManager oldValue = getdocumentManager1();
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
	D0CB838494G88G88G470171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD89457352492C393920992C322CD08A50698341A58E892E20CA1DFCC43FB312D4DB36F11D71A52971327F9BE43EBEDC35B91B07E000ACA9585E50908A06BAABFAAA2B1A0E0B085DDE45D1056173230832CAC6CE6F79092B1765C39F36E4E4E4E303AB65FD73E6FB0B3671E3FFB6E39677E4C1D653273A3572CCE4D663855EBB8696F58EA0E9B791C637A3F5ED40D383081F386A77BABG58421DA9
	CF07EBA414D53EE44E48627E38F39DBC4F066724974D99BB60FE9B173E2B70B338515013E266B84770BDEBC757731DBD28E72D783C72CF19F05D84B097F0641A6EA07AB7EC4F117089929EE1F5AA472D92E976ED4F13F0CD705C81B09BE0210352A742F53D2879566AAA096E625D51DC720FDC299B099C19CC04DAA76535CAD79D773B5519DF92DDE737E7CABC1321ECB1G29FC9A973D7EB4DCEB0CFDBDA73BBAED0261F297EF379B3A6CB6FEC0B0D8F9F71F60F09A845EAD5886FA3ADD16FEFE48616A2B2EB6D1
	4CE94498AD833C1D63C2C4DD93372F93E5F31C911E33DD84973507680F0767A40094060FBFC6706DF06F8488F1E11DBF5ADFA355297D4A9CAE296745751BB56A9C6922F5DD687254B978C0E75ACE31FD966413F6C8F71BB30A81EAGBAG02GA281F24538F8F94F813856F58A750E7EFE47C01D53F94AEEF535B82C3C9D6E2B2B21C4421D673B9CAEAB475136B7ED572B58B39AD03E4E9B6623BE092E40B808B9BF03CBF8E2E26ADA951FA4C4DFC91D6B263ED8647678046A1E2429FB055BBBCE3DF5FF4649F5F3
	EF172FDD2322BB6F35723569821513A3F8F45B8552564D79242D2B601E707D8900627FA662E74A70B4E62709B1C31E1320ACEC90E323FD935A521CBE054BAA3BD7558F918BC2D3EBAED39BCC171536949FA73203AED31D954C16CFC57C8A991E5A7293B1CE49F34790446F30B9034657AB85248FC7FC8DFD97A09EA089A07D0AB9C387D0F985E3ECEB7E216B0CB12345A51C9A34592D3C0B233C2F64EF07ABBFAC38ACE7865C02E5200BB7DBDCB6CB271DF7F3924D62FC6203CEAF3E5745586F829D1F70833C4BA2
	589C83A09D3A2C403B5C14664D7CAD0446450B25A6DBBFF4ECCB3F13C3396F64EFA33E3138058BCE2BC560BD9434FCE5BEE9134B96B3C88481F0CF7D326AC362AF93545F8EB0427C502FA3727E4C3BA0DFD4D7B7BB9CFD03CE93298997FF057879F1B1F70441FD749574F172DE024B9A01BEBC02F222F4DBE5F29AECDD44FC0B6BAB6CE367730B61DA83749DG41G11G73819281382B660CBC00A6G6BD50C2BF75BB12E9AB6CE6774BF3BC7B52E42A046C32E52F80A3C2A6CDF3C2817583E904A1682E4FEE34E
	B88CE082988608G188B30649B9CC3E66E0914E4937B1AA41944EEF28D9E716E17115233752AEFFF45FA61F322C4DBB3225E37EFBCF77D8733B5C3EC87FA3FC6C6234C9D1B24BE2C1437D91467F95EA8DD53EE524ECDAC86BB771198DC85FE4E3E86FD719A464E4FF5BA5A97CFD95CBC763EF3AE68B83C5593C9343E0663D6A98652247A2D176A3ED1D607EDD29DF7C83A091E5DGA5G25G878176816CA7FA819A81E0DAA3669312974A18FF0D223EE630D5G188C9087308CA0BB8846AFGABC0A840EC008400
	74DB4C99FAG934088C094C0BCC0B2C05EF74C998DGF600F000D800A4004CB198FBCB0E052D7654EFD3GF5147B9D6BA82FB7562FE214762A132E0D57593E1F7F83E2641F99E85BFCDCC55A06340B3CFF5DEC5B64F3F4AE3BD756AE07C63125C43A16DE276D877F897C77ED824DA1BFABA03E67AF43B43901F77283D638F1CBE58985240D24713A5B41F85E9471CE985F6BE1C0F5B37C5282520696C1F059BA87853E0D97485C5AD33EC254659E6CF4FA2686286BB785A4B69CC3B03EB37A1482B2B6F6B98686F8
	B119B196297C8F62584C5B79FEB03A5EB1B8A0D0FC7ADE323669633F1266866B76FE4011315DBEC86699F43DB053C2E24F693275C30AA4D395AEF88C9937DFE66B9D821500A36B1EF6385A476078BA34974757422F9F65667F4D192AB6070CF8F8DCEA7C98E98DB446B33ED21BFE3BD75D76BF0AF384896F2636BE320B74890D250A582F36F15D0D446E0141FE5EE56BF2935AFAD8G1DA78D6A62DD142672A869EBAD965B0051619CF4367083030CFF62292D120E36C11B404B88E879B7BB370A3CC22FB85773A9
	7F70C352F75BEC7D03F6BABFE384347C0E26AC38DA9DDD0324E2AD1681CB8F6FAA23E57B9A7640D5D0546FB35B0055B1041428674EAA62C7B7B8C88C895198AC3BF6E13BDCCE7FD01F494573A2C9990E25130A085F39E09807G7441B816DEA9A465B88F138651FA07DD5283E51141A4BEE665E74B5A38A11846C7A8DCE97CCCAE10474FD2291C5CE75D0AB4538A35E80A994D6983DAB4359E9A0396CDBB50C45CCAE8920E203DCEF88E3955C3C34BEFB90A6509703C4A277C40B1ACDFB7965682E3BD65549F1147
	647EE81715D33F766E4B925B6B4BC19E267BE4027CE756AF9D7B36AA4A4CD29F2565C37BC89F354B66561937911D779D477D061831F4FE9A875745G49GA41632C2881D2D9A695A014F8990F69B35EF51EDD21B08F487CFA05DC5F896C23468FE41684261B9C6136E1E9A240B0767A4CD3AB39FA1DD7AF7E10E73DDAD3A5FB33A9A20B1E9524DBA09F4825004EA52719FB37F81CD1CA65D4EDA24CB821A94CD3AFFE1F4F937437C74F6AD3ADB6A104E84B4F695BA5A3EC7CEA1DD686D34BC6AF63A6608135A1752
	3D4F6866CBF4CBEE276B12B82FB8F83E1E79798E9AA7F9F7C8F97D8E395E6086240B1068E65EC157B85E72DA9BD97BCA65C912BCAE144A93C289DD66E924BB9CCA6DEF8A256B25F609EE19C857554C6A91CA673BCBE47486E65F0E96242B3A134AA16544CE631DF27B5EE4F4D6400FGCC3ED32DBD261EC53A84A8CF86483ECB0D6E52B956EED0EE8788D525B39819DF20BC89A0FD1C9A5DCAC657G659747795251F16F3D92125F44BD3DCB164B16E103FB8892FBF575276250C77768AAF62C1368C74661DE5C5F
	F6119CBF9B1E6381124661387D630D3897975E974DA53CDD36D6EB7FB1E51CB4DE07F9466D3095F59F50B164D77B094E8EA8F306617831F21FFCBF204E6DF6F4594401D04AE58F954947F50A1BDA34812E5DB617DBB8836374B0663D18A2E2AF592B105012BF92DB2BB95FBDCE9C734938E7689934D5D71F551885603E6652FC74D9F0987A666AE939EFE2B7DFCD4D5070CD2C64138419EFEA9D263554468FF272B45A8CA784340DDEAEA0ED14F7B7648B00663B316D7E2D10602D70BC82904E706E83849F834FF1
	G899E3C01601361B9F33CB92390006EF3259D4D64683C2086F082C04CF8022FBDCA6606A41F50FAF7D5E03D433EFE186B3867FE4DFA2F98AF457084CF3D755F2CDAC375C5B3FDCDD09EB481757DCA54C7729225EBBD067B57F301E645845C3F9EECAFD1711BC10089CCF5B5194634A926BCD2CCB4EE3839FDEF79A4897788979C473A94071BB3BAG687C2A241A54C568353969583FE737FC6C0FG3AD800C4C65FDACD64F449A2107AFCE5DDAD660999D33839EBC3D3557644FB9F09C8CD8E273E4E3B47637308
	07BA53E2G282D63C59D4497F62009822036A56D155B16F08F5D43CA1F08367D363A9C2E96512386FAB1722EEE072B1F37961396B0D806840338AEB25409450D643E847D5EBAD489D7978F3D172FD330B7EB731FA8B1D39FCC59A677C14D35AFD4FD903239B7B5E722B46F1B6871C14C784E347963518766936803D020091B08BE3096A9FDC07670D2A6E2ACEE28C13E2CFBE1AC3997798E6CD7638B3A977966B13EF04045B33E952AFC699148677A8879F2GE70CC03E2092B53E9046D7F49279A683EEB163B314
	AA79487EE466FD48776F0CCF8738767B10AFD915AF0C715D75B172C581AE0171BD2DD7634B3E9F79CE55A2DFB160BA6EE775BB2846974178FE4D78E2G1744788E9FD272113D573CC948B72D8E798E832EF79272B5D72A71C5B23EAFCEE17F1E8D38E51330CF180F1298D42C51241CD0D80FBC0513E14C1E0CBC6F9FA57BB3232E5DB84CAB6F543338817EF8A62335016C3574BB865D3CC17CDFEF37F57579764396D2065DD06A07A939E52C9F3B4F8D34B0F61FDE686106CE48EC1A5E0075CA7F1EB92366FBE8D3
	F6235ABA1372FC5D08759000BE06717CC2649164495A00F62E9BAE04EB8FAFF865726292EA255D82397EDC67A5401E20BA0E1CC63B12C0FE5E8328E372E94C4174B96E34DA1B519CB3FD0FBC472C335C2A1AE3C25F6EC9B5BDC0F34B48831E9C13BD21B32DF882661850E634A78EE8D29E40F1AB7BA4390AFBAB9C3EE39B799C7586454F66F2365D292A17C06503D45F4587BDFA93A3BB5312A3D1EFED8B6A0D811A2487518F2F342C91E3060E0514B663AC36CBDEA444FCA47607A70B5572CBE8A44AF21D8DF4AC
	20FADFE5FAA3C1669226777EFDEAFA339EC23DC74F5D1C5E8967D0EFA1483C78906A5DFAC42D7F07B33D3F3BC93D1F99D1EFB448CCE4FA47D628694D1902FA6799EFCEEFEEAB6A5503CC6B94549BDB2E26F77294FCB7D035C3792EF866944FFB24B859BD82FD077AD3F19D31E48AFD071A4670EF087825B2BC350F5F05313A8A4AAEFE9F5F7BF125E49DB39F1E93G1281B2234C99FA0066A8DCBFD46EA77B57233ED7BDB3A0407242E2EF735AC045771B7B095EBE7EAB69795D7D72FEC97D30BCDF6D1DF9EF1454
	7723FC7DC06D9F90473DB0A80F810883C8GC8E1368F967B353DF1D855F66A4F6F0BFB21F98FAB7DBCDD446B9FD67A795582B6370732C8GCAFFCEFCCF16BE1552EFE4F23E907145B2BC1573063856A04F4DD0B6F7AA4AF9CBDCEB94BEC26957B3B949A23ED60627F23E92716479A2144DFF04499157A6055114BE034979BF915FA043CBF2C4BCF936C25942E80C5F75C7C93B699F3D7EF71F14EF074857F083FCB4FF177CC5FECEA7FD4B27294457EA79DB10E487BF6A495FB4E6F698C84C4481BE91E095C056B4
	1877CC4318F92ED86FAFE670EC0094AC34CEB38274C56C8D7B0272FDF6AC3033B1FC00FC8B0F7B63B3CE732E8365FB4DAF1F2060135E0318483BB61575BA1927C86FC432F3657B4C215368190639F00D8720FB8A97F663D92924E918777A8F113D003C474C99D5GC6GBBC098C08C402247B096B6161C72978BF578D24964E037754A97C3680B08EA25AF16BE66DDF7F59FCE75615B23F8CE0FD1736142B5396ABEA45B9DE7843EDF5AF749ADC49F6545200F5EBCCCFCE402E7A7C088C09440C200A400F55351C7
	9DA59F797311B8D81A9C7500EBF2B87AD45CA355736209407CF34907CF691F62695EFC345E130A305E3553B127E55584E6439E9FBE258D432AB6D876238DE14C0638GED7809DF9B962A5A303F98EDC8E4B68CFF94188DE3FD7814B6644CD033E1CE895AF0F8865A20BF99188D55BEFCCA9BFAD5ED60CB5106A0E6C3E200B63C6B5706392AB6146A5106C54C06700F8333E10A8F1F5206F5BFD03361FF198D3A9F208DCD350159E07661D35A502EEA431C03E80313591092208D9B6A7C59902DEAC34FA134E1BE33
	E1260F2C6B33E11EDF9BD6295AB02692ED481A09B6D8CF85B6963B7C72B54DD463BBD09F183EAA3FFC9D2A7AD67870DD1F0F7F6043671303D575BD5190D87D1E774B97262AAF28B1307A5D6543E7D0BCC72B6ABBF1BA307A1D714BB7DF210F4E9DBEAFA7F307A5D0168C10B94B1CD18C508C508B90BA8B678E33CACEF057B7F7202BEE5F198335F3FCF3E0750B744B97B3CB2DFE2FD7107A45C1D9A240AA006C47E1AE89E08298FE9C6B17F6406F7C119D973E1E79636916406AE9724B9776389A5F16330145E93E
	8F1FCF3EFDDCAD57ADE77310790FE32E7BD900B67C522F8D2BD4ED7871BE16EF1FC09B6EBB97188D0F7870A9EDE8FAC24D06B3C750068EE6437F9B8333214F07CFE9C3042A8D4FD6208DB3198D852D0159F04807CFE943D2D59B5EABC79B38593026934F2F1F935F2BD741F3B3436945EF111C709CB29BEDFDFD97590F0EE14F3F941F49FBC049AF3B5A95B68482C43E9A1E6A887E076850BA2FCDF34C7AD26DF312D9256A67A437172A1F134CAB5DC271A0C37E15841697160EFE0ED2DF2AF60E1236D13A0E34D1
	9C78EBB1EBB71B8E5BADF1B6664C4BFA6D7ADC552B57E70CDE3DBE5F550FDE1F3B75DA75216D7728F8DEB37D0798B3CF084F643C16141B8AC6EBDF42F7B36DAE2DA3C655F133E779290E8B7B48BECAA1585BGE0858882088408E5F5370AB464AC0EA01DB1A3674748999EF2BECC6AE3059D12DC42077234F4870DD29629A8334A1E25399E53351173BE432D866BC7A94BD07109372E658748D9418430A199A06BC9F3067EC92C736F44B213F42EADCEBA0FC64B2E9DA0EDC7682F37BD129575A6BA759A7C521E12
	28C379DD48E82074D35AA87E116AAF6E199A418EBBC0B0C0A4C0AC73417BE2B9B9DFC74ECC1173D5648C9DB90FA57941A00FE142EF14E4A87559D5F031FE7CA77941CB475AD16A2596A74A98D042EED15EEB29E49F5CB5B8A0587AF9B15550FDB6FE181C7707595C794181BA55A359462D5CAF66F07E38638849A3C9CFE24EABBED84E794E939B2DB64161C2A1A65E62BA575D4DA609EDCE3E8B73D83EC1BBE7969954F3265E201EB34B8D23674CEA03D64E24BEFACBA0E742FD4E461F05D9AE0767217FF8FC3DC4
	E3949C46B28E77ABFB9A098C157D4A36AE1743EEA7276BB81C9BB4673239419CBCF3E54B933FFF04E7A3C06F9C0C3F3F6C9EED7E9BB447B73E24F33E07347D7A4DA1F53F869FA279486D98F4F571CA5F5DF1A80715F9A679E4A316380752040B7C024555437B78FE1248CF4B3478238E11D86E34D9EDAE6904B8ED171737EF965B556D665D62222A41A2D82476F8359A5B23D1F054FB3E1552952A79ADDC45DF5EFEDBF6D85BEF6F9DD6775BEF8FEB7B0DAB736F3775E55AFE4BAA736F371DE5DAF1CDFDB461045C
	C7954CC711C5EABE0A9E45C7D45EA7B5F2F915CCDE77FEB5F931FE653D6FA56F881327AFD61337482F3C9FFCA417F714490BADD1131764D7DE57C9393CE3CC5E25D2B5F92B7C4A5B7131DCDE9513F7D82FA6AFF32EBFF92FF849BB4E647DDAD51E4E2F3CEB359BE4724A8EB37F9DD413D769D7DEC51D173CB214E7BF24A62F492F3C373C6515233C10CAB5F9977D4A1BF4CA1E534B995ED2AF47D7B07C7E86AF7D9508FF27C1CEFF04514FE974222FC43C6B342C5DDAC86E227D392BDCBB87DDAAD74FC14EF26D9C
	B4D46EBF878515EB67205B4A7D6720307251F3504B4D99681BEE17239FC6CE4A774B8A6D3A3ED321DE5795955AF57DCD057F3AF2155AF5DDDB693F2ED9154AF10A5671EA0B3C7DABD95D1B4EFA357F910C57152A794D69B7DEB34F497BFBB96B6F2F6DD313976CD75E22F3DE769DC3F9E70F28490B70ABAF5C68A56FA84ADBD021A6AF462F3C76D6AFF9D5A8EFD5391A3C79FE65F56FA05FAE5AADDFB986850E2BFD0A4C3F2E6E4077710B01FF99C0B6600B1F226F65699CEF5690E9F7F2629BC2DB1C4466BDCDF8
	6BF7A2EF8850CF860885C8GC8F98A4FE90D7C054C6FE433C169BB3DDC1DF87690025C7AB479CD1915E4DBD9129B24C33951709C8B10G108210778C4AFD78A4D93F54D92D6CB3C3FCC1CE4B471FA05FD01C673BDD3C3B374EEE773CBF276529FF5D66612F3738F9C57909CDC4BFAC8E001795C9E5FD690437E150E937F5111FFFE8D3707E7EE016283B5FF1D9D6C84BFE5ECFF8BF6187CCED67F92743A5A8F83F68A77EEAF6D82C5EFC3797ECE2B215768C1AB7D3DB5BEC7DEE053C2DA7F328BE72DB973C295EDB
	A925796F431B983FD2768C9B3147EC63073CF93E13C6F4426207ACBB94BAB345F9ABD98B196ADD0A326C16DCEFFB20656496E1FE4CBDC2F2954B2B52DA0446FFEB6EE8EB031AE7344E603DA12EB50C4FA84F063DA562BB1ED11E8DF3646159B0A714ADFE8677403E5C0DFBE0D53108BBD608FBE59799EECE9162FA996E6AFE44898CD7DF0C38A0067BE98962C2996E16D244C5B05CA7FA44C5B15C4603081B691147F0318CB766906296B25C0315EC4F0F61560B36A442738A06DBAE6E392740F36633086345BD70
	9CF8AEE6385745BD69CAF8EEE0384D8CE7FC964F11BF3C03781A787FA260421F455C73DF862F73B064D4E7F575A98779263619A6B269BD043847CC78631F4535E5EE0EF806EF9E648D004AF9781D4EF091FE2793B3F58A3768D76A5FEEA51A2E26B64F2331641CA77BE664C0E79A55E95B09BAE320BCE99E415D3BD3FEFE1444CEC9BAFD67C74BDF76AAA7F307AFB9F9797BDE65445EE752646587F7494B090F46C97C34EE2F6C433A253CB213337FB0D873F731744F51BA99252B6EDF6FDB3DAC42788115736ADF
	656776326E37272E561093A27107BF677191754FD49D7AE761F3681F45BA25FF8A3C6A7FC10152BFAD2972722A8225FFAAD64B4BAF95A87DB3A4791F46DEEFAE76DDEEBE5B834DA3E3111EBDAF971F49371074F9B06F9F71CEC08DBCBA6F7A56F408E74444F1B5E4B70E2B35205784A0G04834C862063B0F71234BE3D5F7B31673E2F56F3BF294EF3BF6F14673E2841F3FF2651F3FF4BE94FFD54A71E7BB71ABC775C279C07E30E2E44FBEE30774F1E3274A26F32534D1E3211A35EE56F36F84A0AAA3C4B361C73
	689EB612FB5A8E0F791C6F7687146F29G79D65EB09F9D33160B39ED71FCBAE62DE678DF0B78C4991E6657029C4C2F4B204C78BC1E997CEBAB090959703C88E0A9G37401C218328D900FB1D47EBE0A9BC7ABBF11343598C0B993BEC7B1156EFE0670D560F661F6757E29E1B7D74B4EE6E58864DEFA90D8B249CADDD67465F371ADCE97D5EBDCF6A9789E531G09G2BGB2DFB0E7943E00753B25C6BABFCC26ACF502E0696A953F2CE51B41B07B71204984184A5EE2A216D299AF55105F12B1DBAE296E84FF8AC3
	977DA447E46971441A6EC6FD537C82E94BEB393AD1E82ADE20FE10762CAE13B93FF882378C77E1E76C463C9C7E826665045D4A3CFCB7A74F2B1B76A87372EA2F725ABD4A3C7CBD2FF2579EE5DEC6FE6A0B77C8B3C977A9327B7DB95FD6DE7474415AEFD5870D8FEB2B76FA3F2FD5FD3D7FE52B7AFE7FD06B686FF42F35EA7FB64E58D679EF6350B85EF30134CDBC4442D2004CB8687FG15F158BF324FFF48097DC325EFB4DAFB48FE837985BE016DC51C572E6B9F4E7B7F5D3E35EDE45D5B699084C7BF110F780D
	ED243D8407D3C4CA3FA158C6FC643275748A14D4426734119837735DA2DA7A5EAD177405FA1EFC7B02F9E60847BC13775877393C7C5AD40CED1FBC93A7FD5B92671967F8E26809D15FD7E847C55185EDDF9D3C605FD7C7AF083F85GF5B4FD651476E9CEDCA075F4F4F73BF956A6359732986E0C5B68F05B48868EED7F39E69DDCE7C23D6281D6G2CFB91664CG15GCDG9DAFE2AC7C648279ADBF55BC87C8BAB1976341E531616F903DE0562E630F4C7E6B78D333FABC3CEE760D077FB42B47C312D99E8F9477
	36A45745DEF6D17C7F18456FFD446505673BD37AFB39F38E57C1F044AC675688DC0E56CFA4895C46BA2BD54736A684AE931F49D603402D0545362E4EEE9738EDEC9BC1CE3E9397489E240E2E5C85EE83599C5009232F40FDC0F78484EEB311687949A4B7D867A50368E5A25200D860361236938BF575AE50GDE9738ACAAD0EECC06340BC28D4E7396ABB1B251BB24AD815DE7B6219730BD40C9FE8493DCC006D14038876D8249D55277C157646B0CD537F6297E7E95016117E83F8BFDC979FDD02D381EDC887824
	17B0CE434A8FEA45A9E42D36FE8B5445E26F2B9B3012317954D7826F491F0D95C43E9BE924C2DA56AC6E4DDE12717C9DD0CB8788EF13D7F3DE9BGG24D9GGD0CB818294G94G88G88G470171B4EF13D7F3DE9BGG24D9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG189BGGGG
**end of data**/
}
}
