/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import org.vcell.util.DataAccessException;

import cbit.image.DisplayAdapterService;
import cbit.image.gui.DisplayAdapterServicePanel;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.data.DataSelectRetrieve;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.geometry.surface.Quadrilateral;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.solvers.MembraneElement;
/**
 * Insert the type's description here.
 * Creation date: (9/20/2005 9:13:34 AM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class DataValueSurfaceViewer extends javax.swing.JPanel implements java.awt.event.ActionListener {

	//
	private JLabel canvasDimensionsLabel;
	private JButton jButtonMakeMovie;
	private static final String DIRECTIONS_INFO_TEXT = "ROTATE(leftMB-drag)  MOVE(rightMB-drag)  ZOOM(leftMB-rigthMB-drag)";
	
	public interface SurfaceCollectionDataInfoProvider {
		public double getValue(int surfaceIndex,int polygonIndex);
		public String getValueDescription(int surfaceIndex,int polygonIndex);
		public int[][] getSurfacePolygonColors();
		public float getArea(int surfaceIndex,int polygonIndex);
		public cbit.vcell.render.Vect3d getNormal(int surfaceIndex,int polygonIndex);
		public int getMembraneIndex(int surfaceIndex,int polygonIndex);
		public void plotTimeSeriesData(int[][] indices,boolean bAllTimes,boolean bTimeStats,boolean bSpaceStats) throws DataAccessException;
		public org.vcell.util.Coordinate getCentroid(int surfaceIndex,int polygonIndex);
		public java.awt.Color getROIHighlightColor();
		public void makeMovie(SurfaceCanvas surfaceCanvas);
	}

	public class SurfaceCollectionDataInfo {
		private SurfaceCollection surfaceCollection;
		private org.vcell.util.Origin origin;
		private org.vcell.util.Extent extent;
		private String[] surfaceNames;
		private Double[] surfaceAreas;
		private int dimension;
		
		public SurfaceCollectionDataInfo(
			SurfaceCollection argSurfaceCollection,
			org.vcell.util.Origin argOrigin,
			org.vcell.util.Extent argExtent,
			String[] argSurfaceNames,
			Double[] argSurfaceAreas,
			int argDimension
			){
				surfaceCollection = argSurfaceCollection;
				origin = argOrigin;
				extent = argExtent;
				surfaceNames = argSurfaceNames;
				surfaceAreas = argSurfaceAreas;
				dimension = argDimension;
		}
		public SurfaceCollection getSurfaceCollection(){return surfaceCollection;}
		public org.vcell.util.Origin getSurfaceCollectionOrigin(){return origin;}
		public org.vcell.util.Extent getSurfaceCollectionExtent(){return extent;}
		public String[] getSurfaceNames(){return surfaceNames;}
		public Double[] getSurfaceAreas(){return surfaceAreas;}
		public int getDimension(){return dimension;}
		//public SurfaceCollectionDataInfoProvider getSurfaceCollectionDataInfoProvider(){return surfaceCollectionDataInfoProvider;}
	}

	private class DataValueSurfaceViewerTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener{

		private static final int NAME_COLUMN = 0;
		private static final int SIZE_COLUMN = 1;
		private static final int VISIBLE_COLUMN = 2;
		private static final int WIREFRAME_COLUMN = 3;
		private static final int TRANSPARENT_COLUMN = 4;
		private final String[] columnNames = new String[]{"Name","Size","Visible","Wireframe","Transparent"};
		
		private final String[] transparentValues = new String[] {"None","25%","50%","75%"};
		
		protected javax.swing.JComboBox transparentEditor = new javax.swing.JComboBox(transparentValues);
		
		public DataValueSurfaceViewerTableModel(){
			super();
			this.addTableModelListener(this);
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return 
				columnIndex == VISIBLE_COLUMN ||
				columnIndex == WIREFRAME_COLUMN ||
				columnIndex == TRANSPARENT_COLUMN;
		}		
		public int getColumnCount(){
			return columnNames.length;
		}
		public int getRowCount(){
			return (getSurfaceCollectionDataInfo() != null?getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaceCount():0);
		}
		public Class<?> getColumnClass(int col)	{
			if(col == NAME_COLUMN){
				return String.class;
			}else if(col == SIZE_COLUMN){
				return Double.class;
			}else if(col == VISIBLE_COLUMN){
				return Boolean.class;
			}else if(col == WIREFRAME_COLUMN){
				return Boolean.class;
			}else if(col == TRANSPARENT_COLUMN){
				return String.class;
			}
			return Object.class;
		}
		public java.lang.Object getValueAt(int row, int col){
			if(col == NAME_COLUMN){
				return (getSurfaceCollectionDataInfo() != null && getSurfaceCollectionDataInfo().getSurfaceNames() != null?
					getSurfaceCollectionDataInfo().getSurfaceNames()[row]:null);
			}else if(col == SIZE_COLUMN){
				return (getSurfaceCollectionDataInfo() != null && getSurfaceCollectionDataInfo().getSurfaceAreas() != null?
					getSurfaceCollectionDataInfo().getSurfaceAreas()[row]:null);
			}else if(col == VISIBLE_COLUMN){
				return (bDisplay != null?new Boolean(bDisplay[row]):null);
			}else if(col == WIREFRAME_COLUMN){
				return (bWireframe != null?new Boolean(bWireframe[row]):null);
			}else if(col == TRANSPARENT_COLUMN){
				return (transparentSelection != null?
					transparentValues[(int)((1-transparentSelection[row].getAlpha())*4)]:
					null);
			}
			return null;
		}
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex == VISIBLE_COLUMN){
				bDisplay[rowIndex] = ((Boolean)aValue).booleanValue();
			}else if(columnIndex == WIREFRAME_COLUMN){
				bWireframe[rowIndex] = ((Boolean)aValue).booleanValue();
			}else if(columnIndex == TRANSPARENT_COLUMN){
				int index = ((javax.swing.DefaultComboBoxModel)transparentEditor.getModel()).getIndexOf(aValue);
				if(index > 0){
					float alpha = 1.0f - (float)(.25*(index));
					transparentSelection[rowIndex] = java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER,alpha);
				}else{
					transparentSelection[rowIndex] = java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC);
				}
			}
			
			fireTableCellUpdated(rowIndex,columnIndex);
		}
		public void tableChanged(javax.swing.event.TableModelEvent e) {
			if(e.getType() == javax.swing.event.TableModelEvent.UPDATE){
				if(e.getColumn() == VISIBLE_COLUMN){
					getSurfaceCanvas1().setSurfacesShowing((boolean[])bDisplay.clone());
				}else if(e.getColumn() == WIREFRAME_COLUMN){
					getSurfaceCanvas1().setSurfacesWireframe((boolean[])bWireframe.clone());
				}else if(e.getColumn() == TRANSPARENT_COLUMN){
					getSurfaceCanvas1().setSurfaceTransparency((java.awt.AlphaComposite[])transparentSelection.clone());
				}
			}
			javax.swing.SwingUtilities.invokeLater(
				new Runnable (){
					public void run(){
						getSurfaceCanvas1().repaint();
					};
				}
			);
		}	
	}
	//
	private final cbit.vcell.client.data.DataSelectRetrieve dsr = new cbit.vcell.client.data.DataSelectRetrieve();
	//
	javax.swing.Timer mouseOverTimer = new javax.swing.Timer(500,
		new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e){updatePickInfoDisplay();}
		});
	//
	private boolean selectRangeMinMaxFlag = false;
	private java.awt.event.MouseEvent lastMouse = null;
	private boolean[][] bAOI = null;
	private java.util.Vector<SurfaceCanvas.SurfaceCollectionPick> polylinePicks = new java.util.Vector<SurfaceCanvas.SurfaceCollectionPick>();
	//
	private java.text.DecimalFormat df = new java.text.DecimalFormat();
	//
	private boolean[] bDisplay;
	private boolean[] bWireframe;
	private java.awt.AlphaComposite[] transparentSelection;
	private SurfaceCanvas ivjSurfaceCanvas1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private SurfaceViewerTool ivjSurfaceViewerTool1 = null;
	private SurfaceCollectionDataInfo fieldSurfaceCollectionDataInfo = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.table.TableModel ivjTableModel1 = null;
	private javax.swing.JLabel ivjJLabelDirections = null;
	private javax.swing.JLabel ivjJLabelInfo = null;
	private javax.swing.JButton ivjJButtonHomeView = null;
	private javax.swing.JPanel buttonPanel = null;
	private javax.swing.JCheckBox ivjJCheckBoxBoundingbox = null;
	private SurfaceCollectionDataInfoProvider fieldSurfaceCollectionDataInfoProvider = null;
	private javax.swing.JButton ivjJButtonDSR = null;
	private javax.swing.JButton ivjJButtonStats = null;

	private class IvjEventHandler implements 
	java.awt.event.ActionListener, java.awt.event.MouseListener, 
	java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener,
	ComponentListener{
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonHomeView()) 
				connEtoC4(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJCheckBoxBoundingbox()) 
				connEtoC13(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonDSR()) 
				connEtoC15(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonStats()) 
				connEtoC18(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == DataValueSurfaceViewer.this.getSurfaceCanvas1()) 
				connEtoC3(e);
		};
		public void mouseDragged(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {
			if (e.getSource() == DataValueSurfaceViewer.this.getSurfaceCanvas1()) 
				connEtoC17(e);
		};
		public void mouseMoved(java.awt.event.MouseEvent e) {
			if (e.getSource() == DataValueSurfaceViewer.this.getSurfaceCanvas1()) 
				connEtoC16(e);
		};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DataValueSurfaceViewer.this && 
				(evt.getPropertyName().equals("surfaceCollectionDataInfo") || evt.getPropertyName().equals("surfaceCollectionDataInfoProvider"))) 
				connEtoC2(evt);
			if (evt.getSource() == DataValueSurfaceViewer.this.getSurfaceViewerTool1() && (evt.getPropertyName().equals("currentManipulation"))) 
				connEtoC14(evt);
		}
		public void componentHidden(ComponentEvent e) {}
		public void componentMoved(ComponentEvent e) {}
		public void componentResized(ComponentEvent e) {
			getCanvasDimensionsLabel().setText("(w="+getSurfaceCanvas1().getSize().width+" h="+getSurfaceCanvas1().getSize().height + ")");
		}
		public void componentShown(ComponentEvent e) {
			componentResized(e);
		};
	};

/**
 * DataValueSurfaceViewer constructor comment.
 */
public DataValueSurfaceViewer() {
	super();
	initialize();
}

	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {

	if(e.getSource() == dsr){
		if(e.getID() == cbit.vcell.client.data.DataSelectRetrieve.SELECTION_CLEAR){
			for(int i=0;i<bAOI.length;i+= 1){
				java.util.Arrays.fill(bAOI[i],false);
			}
			//polylinePicks.clear();
			updateAreaOfInterest();
		}else if(e.getID() == cbit.vcell.client.data.DataSelectRetrieve.SELECTION_APPLY_AREA_ANALYTIC){
			pickByAnalytic();
		}else if(e.getID() == cbit.vcell.client.data.DataSelectRetrieve.SELECTION_APPLY_AREA_RANGE){
			pickByRange(dsr.getDSRState().selectAreaRange);
		}else if(e.getID() == cbit.vcell.client.data.DataSelectRetrieve.SELECTION_APPLY_AREA_REGION){
			pickByRegion(dsr.getDSRState().selectAreaRegionName);
		}
		//else if(e.getID() == cbit.vcell.client.data.DataSelectRetrieve.SELECTION_TOAOI_LINE){
			//if(polylinePicks.size() > 0 && bAOI != null){
				//boolean pickOP = (dsr.getDSRState().selectionType == cbit.vcell.client.data.DataSelectRetrieve.SELECT_TYPE_ADD?true:false);
				//for(int i=0;i<polylinePicks.size();i+= 1){
					//SurfaceCanvas.SurfaceCollectionPick lineElement = (SurfaceCanvas.SurfaceCollectionPick)polylinePicks.elementAt(i);
					//bAOI[lineElement.surfaceIndex][lineElement.polygonIndex] = pickOP;
				//}
				//polylinePicks.clear();
				//updateAreaOfInterest();
			//}
			
		//}
		//else if(e.getID() == cbit.vcell.client.data.DataSelectRetrieve.RETRIEVE_DATA){
			//if(bAOI == null || bAOI.length == 0){
				//return;
			//}
			//int count = 0;
			//for(int i=0;i<bAOI.length;i+= 1){
				//for(int j=0;j<bAOI[i].length;j+= 1){
					//if(bAOI[i][j]){
						//count+= 1;
					//}
				//}
			//}
			//int[][] indices = new int[1][count];
			//count = 0;
			//for(int i=0;i<bAOI.length;i+= 1){
				//for(int j=0;j<bAOI[i].length;j+= 1){
					//if(bAOI[i][j]){
						//indices[0][count] = getSurfaceCollectionDataInfoProvider().getMembraneIndex(i,j);
						//count+= 1;
					//}
				//}
			//}
			//try{
				//cbit.vcell.client.data.DataSelectRetrieve.DSRState dsrState = dsr.getDSRState();
				//cbit.util.TimeSeriesJobResults tsjr =
					//getSurfaceCollectionDataInfoProvider().getTimeSeriesData(indices,dsrState.bRetrieveTimeData,dsrState.bTimeStats,dsrState.bSpatialStats);
					

				////if(tsjr instanceof cbit.util.TSJobResultsTimeStats){
					////double[][] mins = ((cbit.util.TSJobResultsTimeStats)tsjr).getMinimums();
					////for(int i=0;i<mins.length;i+= 1){
						////for(int j=0;j<mins[i].length;j+= 1){
							////System.out.println("min="+mins[i][j]);
						////}
					////}
				////}
			//}catch(Exception e2){
				//cbit.vcell.client.PopupGenerator.showErrorDialog(e2.getMessage());
			//}
		//}
	}
	
}


/**
 * Comment
 */
private void configureView(java.awt.event.ActionEvent actionEvent) {
	
	if(actionEvent.getSource() == getJButtonHomeView()){
		getSurfaceViewerTool1().resetView();
	}

	//getSurfaceCanvas1().getTrackball().zoom_z(0, 0);
	getSurfaceCanvas1().repaint();
}


/**
 * connEtoC1:  (DataValueSurfaceViewer.initialize() --> DataValueSurfaceViewer.dataValueSurfaceViewer_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.dataValueSurfaceViewer_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (JCheckBoxBoundingbox.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.jCheckBoxBoundingbox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxBoundingbox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC14:  (SurfaceViewerTool1.currentManipulation --> DataValueSurfaceViewer.setModeCursor()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setModeCursor();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (JButtonDSR.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.jButtonDSR_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDSR_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC16:  (SurfaceCanvas1.mouseMotion.mouseMoved(java.awt.event.MouseEvent) --> DataValueSurfaceViewer.surfaceCanvas1_MouseMoved(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.surfaceCanvas1_MouseMoved(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC17:  (SurfaceCanvas1.mouse.mouseExited(java.awt.event.MouseEvent) --> DataValueSurfaceViewer.surfaceCanvas1_MouseExited(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.surfaceCanvas1_MouseExited(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC18:  (JButtonStats.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.jButtonStats_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonStats_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (DataValueSurfaceViewer.surfaceCollectionDataInfo --> DataValueSurfaceViewer.refresh()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refresh();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (SurfaceCanvas1.mouse.mouseClicked(java.awt.event.MouseEvent) --> DataValueSurfaceViewer.pickPolygon(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.pickPolygon(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JButtonHomeView.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.configureView(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureView(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (DataValueSurfaceViewer.initialize() --> SurfaceViewerTool1.surfaceCanvas)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getSurfaceViewerTool1().setSurfaceCanvas(getSurfaceCanvas1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (TableModel1.this --> ScrollPaneTable.model)
 * @param value javax.swing.table.TableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(javax.swing.table.TableModel value) {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable().setModel(getTableModel1());
		getScrollPaneTable().createDefaultColumnsFromModel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void dataValueSurfaceViewer_Initialize() {
	
	DataValueSurfaceViewerTableModel dvsvtm = new DataValueSurfaceViewerTableModel();
	setTableModel1(dvsvtm);
	getScrollPaneTable().getColumnModel().getColumn(
		DataValueSurfaceViewerTableModel.TRANSPARENT_COLUMN).setCellEditor(
			new javax.swing.DefaultCellEditor(dvsvtm.transparentEditor));
		
	getScrollPaneTable().setPreferredScrollableViewportSize(new java.awt.Dimension(100,100));

	dsr.addActionListener(this);
	mouseOverTimer.setRepeats(false);
	
	//getButtonGroupSelectAddRemove().add(getJRadioButtonSelectAdd());
	//getButtonGroupSelectAddRemove().add(getJRadioButtonSelectRemove());

	//getButtonGroupSelectFunc().add(getJRadioButtonPoints());
	//getButtonGroupSelectFunc().add(getJRadioButtonLine());
	//getButtonGroupSelectFunc().add(getJRadioButtonAOIManual());
	//getButtonGroupSelectFunc().add(getJRadioButtonAOIRange());

	////getJComboBoxAreaRadius().removeActionListener(ivjEventHandler);
	//for(int i=0;i<26;i+= 1){
		//getJComboBoxAreaRadius().addItem(""+i);
	//}
	////getJComboBoxAreaRadius().addActionListener(ivjEventHandler);
	
	//Disable elements until they are fixed
}


/**
 * Return the JButtonDSR property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDSR() {
	if (ivjJButtonDSR == null) {
		try {
			ivjJButtonDSR = new javax.swing.JButton();
			ivjJButtonDSR.setName("JButtonDSR");
			ivjJButtonDSR.setText("Define ROI...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDSR;
}

/**
 * Return the JButtonReset property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonHomeView() {
	if (ivjJButtonHomeView == null) {
		try {
			ivjJButtonHomeView = new javax.swing.JButton();
			ivjJButtonHomeView.setName("JButtonHomeView");
			ivjJButtonHomeView.setText("Reset View");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonHomeView;
}

/**
 * Return the JButtonStats property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonStats() {
	if (ivjJButtonStats == null) {
		try {
			ivjJButtonStats = new javax.swing.JButton();
			ivjJButtonStats.setName("JButtonStats");
			ivjJButtonStats.setText("Statistics");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonStats;
}

/**
 * Return the JCheckBoxBoundingbox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxBoundingbox() {
	if (ivjJCheckBoxBoundingbox == null) {
		try {
			ivjJCheckBoxBoundingbox = new javax.swing.JCheckBox();
			ivjJCheckBoxBoundingbox.setName("JCheckBoxBoundingbox");
			ivjJCheckBoxBoundingbox.setSelected(true);
			ivjJCheckBoxBoundingbox.setText("BoundingBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxBoundingbox;
}

/**
 * Return the JLabelInfo property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDirections() {
	if (ivjJLabelDirections == null) {
		try {
			ivjJLabelDirections = new javax.swing.JLabel();
			ivjJLabelDirections.setName("JLabelDirections");
			ivjJLabelDirections.setText(DIRECTIONS_INFO_TEXT);
			ivjJLabelDirections.setForeground(Color.blue);
			ivjJLabelDirections.setFont(ivjJLabelDirections.getFont().deriveFont(ivjJLabelDirections.getFont().getSize2D() - 1));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDirections;
}

/**
 * Return the JLabelInfo property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelInfo() {
	if (ivjJLabelInfo == null) {
		try {
			ivjJLabelInfo = new javax.swing.JLabel("info");
			ivjJLabelInfo.setName("JLabelInfo");
			ivjJLabelInfo.setBorder(BorderFactory.createEtchedBorder());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelInfo;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getButtonPanel() {
	if (buttonPanel == null) {
		try {
			buttonPanel = new javax.swing.JPanel();
			buttonPanel.add(getJButtonHomeView());
			buttonPanel.add(getJCheckBoxBoundingbox());
			buttonPanel.add(getJButtonDSR());
			buttonPanel.add(getJButtonStats());
			buttonPanel.add(getJButtonMakeMovie());
			buttonPanel.add(getCanvasDimensionsLabel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return buttonPanel;
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
			ivjJScrollPane1.setMinimumSize(new java.awt.Dimension(468, 100));
			getJScrollPane1().setViewportView(getScrollPaneTable());
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
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
//			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}


/**
 * Return the SurfaceCanvas1 property value.
 * @return cbit.vcell.geometry.gui.SurfaceCanvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SurfaceCanvas getSurfaceCanvas1() {
	if (ivjSurfaceCanvas1 == null) {
		try {
			ivjSurfaceCanvas1 = new cbit.vcell.geometry.gui.SurfaceCanvas();
			ivjSurfaceCanvas1.setName("SurfaceCanvas1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSurfaceCanvas1;
}

/**
 * Gets the surfaceCollectionDataInfo property (java.lang.String) value.
 * @return The surfaceCollectionDataInfo property value.
 * @see #setSurfaceCollectionDataInfo
 */
public SurfaceCollectionDataInfo getSurfaceCollectionDataInfo() {
	return fieldSurfaceCollectionDataInfo;
}


/**
 * Gets the surfaceCollectionDataInfoProvider property (java.lang.Object) value.
 * @return The surfaceCollectionDataInfoProvider property value.
 * @see #setSurfaceCollectionDataInfoProvider
 */
public SurfaceCollectionDataInfoProvider getSurfaceCollectionDataInfoProvider() {
	return fieldSurfaceCollectionDataInfoProvider;
}


/**
 * Return the SurfaceViewerTool1 property value.
 * @return cbit.vcell.geometry.gui.SurfaceViewerTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SurfaceViewerTool getSurfaceViewerTool1() {
	if (ivjSurfaceViewerTool1 == null) {
		try {
			ivjSurfaceViewerTool1 = new cbit.vcell.geometry.gui.SurfaceViewerTool();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSurfaceViewerTool1;
}


/**
 * Return the TableModel1 property value.
 * @return javax.swing.table.TableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.table.TableModel getTableModel1() {
	// user code begin {1}
	// user code end
	return ivjTableModel1;
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
 * Insert the method's description here.
 * Creation date: (9/21/2005 1:51:15 PM)
 */
public void init(
			SurfaceCollection argSurfaceCollection,
			org.vcell.util.Origin argOrigin,
			org.vcell.util.Extent argExtent,
			String[] argSurfaceNames,
			Double[] argSurfaceAreas,
			int argDimension
) {
	

	setSurfaceCollectionDataInfoProvider(null);
	setSurfaceCollectionDataInfo(
		new SurfaceCollectionDataInfo(
		argSurfaceCollection,
		argOrigin,
		argExtent,
		argSurfaceNames,
		argSurfaceAreas,
		argDimension
		)
	);

	getSurfaceViewerTool1().resetView();
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
	getSurfaceCanvas1().addMouseListener(ivjEventHandler);
	getJButtonHomeView().addActionListener(ivjEventHandler);
	getSurfaceViewerTool1().addPropertyChangeListener(ivjEventHandler);
	getJCheckBoxBoundingbox().addActionListener(ivjEventHandler);
	getSurfaceCanvas1().addMouseMotionListener(ivjEventHandler);
	getJButtonDSR().addActionListener(ivjEventHandler);
	getJButtonStats().addActionListener(ivjEventHandler);
	getSurfaceCanvas1().addComponentListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DataValueSurfaceViewer");
		setLayout(new java.awt.GridBagLayout());
		setSize(1109, 690);

		java.awt.GridBagConstraints constraintsSurfaceCanvas1 = new java.awt.GridBagConstraints();
		constraintsSurfaceCanvas1.gridx = 0; constraintsSurfaceCanvas1.gridy = 0;
		constraintsSurfaceCanvas1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsSurfaceCanvas1.weightx = 1.0;
		constraintsSurfaceCanvas1.weighty = 1.0;
		constraintsSurfaceCanvas1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSurfaceCanvas1(), constraintsSurfaceCanvas1);
		
		displayAdapterServicePanel = new DisplayAdapterServicePanel();
		
		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 1; constraintsJPanel3.gridy = 0;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel3.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsJPanel3.weighty = 1.0;
		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(displayAdapterServicePanel, constraintsJPanel3);

		
		java.awt.GridBagConstraints constraintsJLabelInfo = new java.awt.GridBagConstraints();
		constraintsJLabelInfo.gridx = 0; constraintsJLabelInfo.gridy = 1;
		constraintsJLabelInfo.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelInfo.insets = new java.awt.Insets(4, 4, 4, 4);
		constraintsJLabelInfo.gridwidth = 2;
		add(getJLabelInfo(), constraintsJLabelInfo);
				
		java.awt.GridBagConstraints constraintsJLabelDirections = new java.awt.GridBagConstraints();
		constraintsJLabelDirections.gridx = 0; constraintsJLabelDirections.gridy = 2;
		constraintsJLabelDirections.insets = new java.awt.Insets(4, 4, 4, 4);
		constraintsJLabelDirections.gridwidth = 2;
		constraintsJLabelDirections.anchor = GridBagConstraints.LINE_START;
		add(getJLabelDirections(), constraintsJLabelDirections);		

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; 
		constraintsJScrollPane1.gridy = 3;
		constraintsJScrollPane1.gridwidth = 2;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getButtonPanel(), gbc);
		
		initConnections();
		connEtoC1();
		connEtoM1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void jButtonDSR_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	dsr.setModeInstruction("(CTRL-CLICK on surface to add/remove from selection)");

	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(DataValueSurfaceViewer.this);
	ChildWindow childWindow = childWindowManager.getChildWindowFromContentPane(dsr);
	if (childWindow==null){
		childWindow = childWindowManager.addChildWindow(dsr,dsr,"Define Region Of Interest");
		childWindow.pack();
		childWindow.setIsCenteredOnParent();
	}
	childWindow.show();	
}


/**
 * Comment
 */
private void jButtonStats_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
			if(bAOI == null || bAOI.length == 0){
				return;
			}
			int count = 0;
			for(int i=0;i<bAOI.length;i+= 1){
				for(int j=0;j<bAOI[i].length;j+= 1){
					if(bAOI[i][j]){
						count+= 1;
					}
				}
			}
			if(count > 0){
				int[][] indices = new int[1][count];
				count = 0;
				for(int i=0;i<bAOI.length;i+= 1){
					for(int j=0;j<bAOI[i].length;j+= 1){
						if(bAOI[i][j]){
							indices[0][count] = getSurfaceCollectionDataInfoProvider().getMembraneIndex(i,j);
							count+= 1;
						}
					}
				}
				try{
					getSurfaceCollectionDataInfoProvider().plotTimeSeriesData(indices,true,false,true);	
				}catch(Exception e2){
					PopupGenerator.showErrorDialog(this, e2.getMessage(), e2);
				}
			}else{
				PopupGenerator.showErrorDialog(this, "Region of Interest must be defined to calculate Statistics");
			}
}


/**
 * Comment
 */
private void jCheckBoxBoundingbox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getSurfaceCanvas1().setShowBoundingBox(getJCheckBoxBoundingbox().isSelected());
	getSurfaceViewerTool1().fullRepaint();
	//getSurfaceCanvas1().repaint();
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DataValueSurfaceViewer aDataValueSurfaceViewer;
		aDataValueSurfaceViewer = new DataValueSurfaceViewer();
		frame.setContentPane(aDataValueSurfaceViewer);
		frame.setSize(aDataValueSurfaceViewer.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
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
 * Insert the method's description here.
 * Creation date: (2/22/2006 5:28:13 PM)
 */
public void pickByAnalytic() {

	try{
		String[] surfNames = getSurfaceCollectionDataInfo().getSurfaceNames();
		String surfSelect = (String)PopupGenerator.showListDialog(this,surfNames,"Apply Analytic Area to Surface");

		DataSelectRetrieve.DSRState dsrState = dsr.getDSRState();
		boolean pickOP = (dsrState.selectionType == DataSelectRetrieve.SELECT_TYPE_ADD?true:false);
		cbit.vcell.parser.Expression xyzExp = dsrState.selectAreaAnalytic;
		
		if(xyzExp != null){
			for(int surf=0;surf <surfNames.length;surf+= 1){
				if(surfNames[surf].equals(surfSelect)){
					int polygonCount = getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(surf).getPolygonCount();
					for(int i=0;i<polygonCount;i+= 1){
						org.vcell.util.Coordinate coord = getSurfaceCollectionDataInfoProvider().getCentroid(surf,i);
						if(coord == null){
							coord = ((cbit.vcell.geometry.surface.Quadrilateral)getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(surf).getPolygons(i)).calculateCentroid();
						}
						if(xyzExp.evaluateVector(new double[]{coord.getX(),coord.getY(),coord.getZ()}) == 1.0){
							bAOI[surf][i] = pickOP;
						}
					}
				}
			}
			
			updateAreaOfInterest();
		}
	}catch(Throwable e){
		PopupGenerator.showErrorDialog(this, "Error while picking Analytic AreaOfInterest\n"+e.getMessage(), e);
	}

}


/**
 * Insert the method's description here.
 * Creation date: (2/19/2006 2:52:44 PM)
 */
private void pickByRange(org.vcell.util.Range pickRange) {

	String[] surfNames = getSurfaceCollectionDataInfo().getSurfaceNames();
	String surfSelect = (String)cbit.vcell.client.PopupGenerator.showListDialog(this,surfNames,"Pick Surface");

	boolean pickOP = (dsr.getDSRState().selectionType == cbit.vcell.client.data.DataSelectRetrieve.SELECT_TYPE_ADD?true:false);
	
	for(int surf=0;surf <surfNames.length;surf+= 1){
		if(surfNames[surf].equals(surfSelect)){
			int polygonCount = getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(surf).getPolygonCount();
			for(int i=0;i<polygonCount;i+= 1){
				double value = getSurfaceCollectionDataInfoProvider().getValue(surf,i);
				if(value >= pickRange.getMin() && value <= pickRange.getMax()){
					bAOI[surf][i] = pickOP;
				}
			}
		}
	}
	
	updateAreaOfInterest();
}


/**
 * Insert the method's description here.
 * Creation date: (2/19/2006 2:52:44 PM)
 */
private void pickByRegion(String regionName) {
	
	String[] surfNames = getSurfaceCollectionDataInfo().getSurfaceNames();

	boolean pickOP = (dsr.getDSRState().selectionType == cbit.vcell.client.data.DataSelectRetrieve.SELECT_TYPE_ADD?true:false);
	
	for(int surf=0;surf <surfNames.length;surf+= 1){
		if(surfNames[surf].equals(regionName)){
			java.util.Arrays.fill(bAOI[surf],pickOP);
		}
	}
	
	updateAreaOfInterest();

}


/**
 * Comment
 */
private int[] pickNeighbors(cbit.vcell.geometry.surface.Surface surf,int[] targets,boolean[] bPicked) {

	int[] newNeighbors = new int[10];
	int newNeighborCount = 0;
	for(int t=0;t<targets.length;t+= 1){
		cbit.vcell.geometry.surface.Node[] pickedNodes = surf.getPolygons(targets[t]).getNodes();
		for(int i=0;i<surf.getPolygonCount();i+= 1){
			if(bPicked[i]){
				continue;
			}
			cbit.vcell.geometry.surface.Node[] surfNodes = surf.getPolygons(i).getNodes();
			boolean bFound = false;
			for(int j=0;j<surfNodes.length;j+= 1){
				for(int k=0;k<pickedNodes.length;k+= 1){
					if(surfNodes[j].equals(pickedNodes[k])){
						bPicked[i] = true;
						if(newNeighbors.length >= newNeighborCount){
							int[] temp = new int[newNeighbors.length + 10];
							System.arraycopy(newNeighbors,0,temp,0,newNeighborCount);
							newNeighbors = temp;
						}
						newNeighbors[newNeighborCount] = i;
						newNeighborCount+= 1;
						bFound=true;
						break;
					}
				}
				if(bFound){
					break;
				}
			}
		}
	}

	int[] results = new int[newNeighborCount];
	System.arraycopy(newNeighbors,0,results,0,newNeighborCount);
	return results;
	
}


/**
 * Comment
 */
private void pickPolygon(java.awt.event.MouseEvent mouseEvent) {

	if((mouseEvent.getModifiers() & java.awt.event.InputEvent.CTRL_MASK) == java.awt.event.InputEvent.CTRL_MASK){
		
		cbit.vcell.client.data.DataSelectRetrieve.DSRState dsrState = dsr.getDSRState();
		boolean pickOP = (dsrState.selectionType == cbit.vcell.client.data.DataSelectRetrieve.SELECT_TYPE_ADD?true:false);
		
		SurfaceCanvas.SurfaceCollectionPick lastPick = getSurfaceCanvas1().pickPolygon(mouseEvent.getX(),mouseEvent.getY());
		if(lastPick != null){
			if(dsrState.selectionMode == cbit.vcell.client.data.DataSelectRetrieve.SELECT_MODE_AREA_RANGE){
				if(selectRangeMinMaxFlag){
					dsr.setSelectMax(getSurfaceCollectionDataInfoProvider().getValue(lastPick.surfaceIndex,lastPick.polygonIndex));
				}else{
					dsr.setSelectMin(getSurfaceCollectionDataInfoProvider().getValue(lastPick.surfaceIndex,lastPick.polygonIndex));
				}
				selectRangeMinMaxFlag = !selectRangeMinMaxFlag;
				
			}else if(dsrState.selectionMode == cbit.vcell.client.data.DataSelectRetrieve.SELECT_MODE_AREA_MANUAL){
				//SurfaceCanvas.SurfaceCollectionPick lastPick = getSurfaceCanvas1().pickPolygon(mouseEvent.getX(),mouseEvent.getY());
				//if(lastPick != null){
					cbit.vcell.geometry.surface.SurfaceCollection sc = getSurfaceCollectionDataInfo().getSurfaceCollection();
					cbit.vcell.geometry.surface.Surface surf = sc.getSurfaces(lastPick.surfaceIndex);

					bAOI[lastPick.surfaceIndex][lastPick.polygonIndex] = pickOP;
					
					boolean[] bPicked = new boolean[surf.getPolygonCount()];
					java.util.Arrays.fill(bPicked,false);
					bPicked[lastPick.polygonIndex] = true;

					int[] targets = new int[1];
					targets[0] = lastPick.polygonIndex;

					for(int i=0;i<dsrState.selectAreaRadius;i+= 1){//Find all neighbors around target out to finalRadius
						int[] neighbors = pickNeighbors(surf,targets,bPicked);
						for(int j=0;j<neighbors.length;j+= 1){
							bAOI[lastPick.surfaceIndex][neighbors[j]] = pickOP;
						}
						targets = neighbors;
					}

					updateAreaOfInterest();
				//}
			}else if(dsrState.selectionMode == cbit.vcell.client.data.DataSelectRetrieve.SELECT_MODE_LINE){
				//SurfaceCanvas.SurfaceCollectionPick lastPick = getSurfaceCanvas1().pickPolygon(mouseEvent.getX(),mouseEvent.getY());
				//if(lastPick != null){
					if(!pickOP){
						int selectedIndex = -1;
						for(int i=0;i<polylinePicks.size();i+= 1){
							SurfaceCanvas.SurfaceCollectionPick ppElement = (SurfaceCanvas.SurfaceCollectionPick)polylinePicks.elementAt(i);
							if(ppElement.surfaceIndex == lastPick.surfaceIndex && ppElement.polygonIndex == lastPick.polygonIndex){
								selectedIndex = i;
								break;
							}
						}
						if(selectedIndex != -1){
							polylinePicks.setSize(selectedIndex);
							updateAreaOfInterest();
						}
					}else{
						if(polylinePicks.size() == 0){
							polylinePicks.add(lastPick);
						}else{
							int prevPickPolyIndex = ((SurfaceCanvas.SurfaceCollectionPick)polylinePicks.lastElement()).polygonIndex;
							cbit.vcell.geometry.surface.SurfaceCollection sc = getSurfaceCollectionDataInfo().getSurfaceCollection();
							cbit.vcell.geometry.surface.Surface surf = sc.getSurfaces(lastPick.surfaceIndex);
							org.vcell.util.Coordinate destination = ((cbit.vcell.geometry.surface.Quadrilateral)surf.getPolygons(lastPick.polygonIndex)).calculateCentroid();
							boolean[] bPicked = new boolean[surf.getPolygonCount()];
							while(true){
								java.util.Arrays.fill(bPicked,false);
								bPicked[prevPickPolyIndex] = true;
								int[] neighbors = pickNeighbors(surf,new int[] {prevPickPolyIndex},bPicked);
								boolean bDone = false;
								for(int i=0;i<neighbors.length;i+= 1){
									if(neighbors[i] == lastPick.polygonIndex){
										bDone = true;
										break;
									}
								}
								if(bDone){break;}
								int lowAnglePolyIndex = -1;
								double lowAngle = Double.POSITIVE_INFINITY;
								org.vcell.util.Coordinate vertex = ((cbit.vcell.geometry.surface.Quadrilateral)surf.getPolygons(prevPickPolyIndex)).calculateCentroid();
								for(int i=0;i<neighbors.length;i+= 1){
									org.vcell.util.Coordinate neighborCoord = ((cbit.vcell.geometry.surface.Quadrilateral)surf.getPolygons(neighbors[i])).calculateCentroid();
									double angle = cbit.vcell.geometry.Curve.getAngle(vertex,destination,neighborCoord);
									if(Math.abs(angle) < lowAngle){
										lowAngle = Math.abs(angle);
										lowAnglePolyIndex = neighbors[i];
									}
								}
								polylinePicks.add(new SurfaceCanvas.SurfaceCollectionPick(lastPick.surfaceIndex,lowAnglePolyIndex));
								prevPickPolyIndex = lowAnglePolyIndex;
							}
							polylinePicks.add(lastPick);
							updateAreaOfInterest();
						}
					}
				//}
			}
		}
	}
}


/**
 * Comment
 */
private void refresh() {

	bAOI = null;
	dsr.setSelectAreaRegionNames(null);
	
	if(getSurfaceCollectionDataInfo() == null){
		return;
	}else{
		if(getSurfaceCollectionDataInfo().getSurfaceCollection() != null){
			bAOI = new boolean[getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaceCount()][];
			for(int i=0;i<bAOI.length;i+= 1){
				bAOI[i] = new boolean[getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(i).getPolygonCount()];
				java.util.Arrays.fill(bAOI[i],false);
			}
		}
		dsr.setSelectAreaRegionNames(getSurfaceCollectionDataInfo().getSurfaceNames());
	}
	
	if(bDisplay == null){
	bDisplay = (getSurfaceCollectionDataInfo() != null?new boolean[getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaceCount()]:null);
	if(bDisplay != null){java.util.Arrays.fill(bDisplay,true);}
	bWireframe = (getSurfaceCollectionDataInfo() != null?new boolean[getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaceCount()]:null);
	if(bWireframe != null){java.util.Arrays.fill(bWireframe,false);}

	//init with NO transparency
	transparentSelection = (getSurfaceCollectionDataInfo() != null?new java.awt.AlphaComposite[getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaceCount()]:null);
	if(transparentSelection != null){java.util.Arrays.fill(transparentSelection,java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC));}
	}
	
	getScrollPaneTable().repaint();
	

	getSurfaceCanvas1().setOrigin(getSurfaceCollectionDataInfo().getSurfaceCollectionOrigin());
	getSurfaceCanvas1().setExtent(getSurfaceCollectionDataInfo().getSurfaceCollectionExtent());
	getSurfaceCanvas1().setSurfaceCollection(getSurfaceCollectionDataInfo().getSurfaceCollection());
	getSurfaceCanvas1().setSurfacesShowing(bDisplay);
	getSurfaceCanvas1().setSurfacesWireframe(bWireframe);
	getSurfaceCanvas1().setWireFrameThickness((getSurfaceCollectionDataInfo().getDimension() < 3?2.0:1.0));
	getSurfaceCanvas1().setSurfaceTransparency(transparentSelection);
	getSurfaceCanvas1().setAreaOfInterest(null);
	
	if(getSurfaceCollectionDataInfoProvider() != null){
		getSurfaceCanvas1().setSurfacesColors(getSurfaceCollectionDataInfoProvider().getSurfacePolygonColors());
	}

	

}


/**
 * Insert the method's description here.
 * Creation date: (9/24/2005 12:45:17 PM)
 */
private void setModeCursor() {

	mouseOverTimer.stop();
	if(!getJLabelInfo().getText().equals("info")){
		getJLabelInfo().setText("info");
	}
	lastMouse = null;

	
	if (getSurfaceViewerTool1().isZooming()){
		if(getSurfaceCanvas1().getCursor() == null || getSurfaceCanvas1().getCursor().getType() != java.awt.Cursor.NE_RESIZE_CURSOR){
			getSurfaceCanvas1().setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.NE_RESIZE_CURSOR));
		}
	}else if (getSurfaceViewerTool1().isRotating()){
		if(getSurfaceCanvas1().getCursor() == null || getSurfaceCanvas1().getCursor().getType() != java.awt.Cursor.MOVE_CURSOR){
			getSurfaceCanvas1().setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.MOVE_CURSOR));
		}
	}else if (getSurfaceViewerTool1().isPanning()){
		if(getSurfaceCanvas1().getCursor() == null || getSurfaceCanvas1().getCursor().getType() != java.awt.Cursor.HAND_CURSOR){
			getSurfaceCanvas1().setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		}
	}else{
		if(getSurfaceCanvas1().getCursor() != null){
			getSurfaceCanvas1().setCursor(null);
		}
	}
}


/**
 * Sets the surfaceCollectionDataInfo property (java.lang.String) value.
 * @param surfaceCollectionDataInfo The new value for the property.
 * @see #getSurfaceCollectionDataInfo
 */
private void setSurfaceCollectionDataInfo(SurfaceCollectionDataInfo surfaceCollectionDataInfo) {
	SurfaceCollectionDataInfo oldValue = fieldSurfaceCollectionDataInfo;
	fieldSurfaceCollectionDataInfo = surfaceCollectionDataInfo;
	firePropertyChange("surfaceCollectionDataInfo", oldValue, surfaceCollectionDataInfo);
}


/**
 * Sets the surfaceCollectionDataInfoProvider property (java.lang.Object) value.
 * @param surfaceCollectionDataInfoProvider The new value for the property.
 * @see #getSurfaceCollectionDataInfoProvider
 */
public void setSurfaceCollectionDataInfoProvider(SurfaceCollectionDataInfoProvider surfaceCollectionDataInfoProvider) {
	SurfaceCollectionDataInfoProvider oldValue = fieldSurfaceCollectionDataInfoProvider;
	fieldSurfaceCollectionDataInfoProvider = surfaceCollectionDataInfoProvider;

	updatePickInfoDisplay();
	if(fieldSurfaceCollectionDataInfoProvider != null){
		getSurfaceCanvas1().setSurfacesColors(fieldSurfaceCollectionDataInfoProvider.getSurfacePolygonColors());
		getSurfaceCanvas1().setAOIHighlightColor(fieldSurfaceCollectionDataInfoProvider.getROIHighlightColor());
		getSurfaceViewerTool1().fullRepaint();
		//getSurfaceCanvas1().repaint();
	}
	
	firePropertyChange("surfaceCollectionDataInfoProvider", oldValue, surfaceCollectionDataInfoProvider);
	((AbstractTableModel)getTableModel1()).fireTableDataChanged();
}


/**
 * Set the TableModel1 to a new value.
 * @param newValue javax.swing.table.TableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTableModel1(javax.swing.table.TableModel newValue) {
	if (ivjTableModel1 != newValue) {
		try {
			ivjTableModel1 = newValue;
			connEtoM2(ivjTableModel1);
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
 * Comment
 */
private void surfaceCanvas1_MouseExited(java.awt.event.MouseEvent mouseEvent) {
	if(!getJLabelInfo().getText().equals("info")){
		getJLabelInfo().setText("info");
	}
	lastMouse = null;
	mouseOverTimer.restart();
}


/**
 * Comment
 */
private void surfaceCanvas1_MouseMoved(java.awt.event.MouseEvent mouseEvent) {
	if(!getJLabelInfo().getText().equals("info")){
		getJLabelInfo().setText("info");
	}
	lastMouse = mouseEvent;
	mouseOverTimer.restart();
}

/**
 * Insert the method's description here.
 * Creation date: (2/16/2006 11:33:51 AM)
 */
private void updateAreaOfInterest() {

	boolean[][] compositeAOI = new boolean[bAOI.length][];
	for(int i=0;i<compositeAOI.length;i+= 1){
		compositeAOI[i] = (boolean[])bAOI[i].clone();
	}
	
	if(polylinePicks.size() > 0){
		for(int i=0;i<polylinePicks.size();i+= 1){
			SurfaceCanvas.SurfaceCollectionPick lineElement = (SurfaceCanvas.SurfaceCollectionPick)polylinePicks.elementAt(i);
			compositeAOI[lineElement.surfaceIndex][lineElement.polygonIndex] = true;
		}
	}
	lastMouse = null;
	updatePickInfoDisplay();
	getSurfaceCanvas1().setAOIHighlightColor(getSurfaceCollectionDataInfoProvider().getROIHighlightColor());
	getSurfaceCanvas1().setAreaOfInterest(compositeAOI);
	getSurfaceViewerTool1().fullRepaint();
}


/**
 * Insert the method's description here.
 * Creation date: (10/2/2005 8:12:14 AM)
 */
private void updatePickInfoDisplay() {
	
	if(getSurfaceCollectionDataInfoProvider() != null){
		SurfaceCanvas.SurfaceCollectionPick currentPick = null;
		if(lastMouse != null){
			currentPick = getSurfaceCanvas1().pickPolygon(lastMouse.getX(),lastMouse.getY());
		}
		if(currentPick != null){
			org.vcell.util.Coordinate centroid = ((Quadrilateral)getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(currentPick.surfaceIndex).getPolygons(currentPick.polygonIndex)).calculateCentroid();
			df.setMaximumFractionDigits(5);
			double area = getSurfaceCollectionDataInfoProvider().getArea(currentPick.surfaceIndex,currentPick.polygonIndex);
			getJLabelInfo().setText(
				" index="+getSurfaceCollectionDataInfoProvider().getMembraneIndex(currentPick.surfaceIndex,currentPick.polygonIndex) +
				" val="+ (float)getSurfaceCollectionDataInfoProvider().getValue(currentPick.surfaceIndex,currentPick.polygonIndex) +
				" area="+ (area != MembraneElement.AREA_UNDEFINED?df.format(area)+"":"No Calc")+
				" centroid=("+df.format(centroid.getX())+","+df.format(centroid.getY())+","+df.format(centroid.getZ())+")");
			return;
		}else{
			String aoiString = " ";
			int count = 0;
			double aoiArea = 0;
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			double mean = 0;
			double wmean = 0;
			boolean isAreaUndefined = false;
			if(bAOI != null){
				for(int i=0;i<bAOI.length;i+= 1){
					for(int j=0;j<bAOI[i].length;j+= 1){
						if(bAOI[i][j]){
							count+= 1;
							double area = getSurfaceCollectionDataInfoProvider().getArea(i,j);
							if(area == cbit.vcell.solvers.MembraneElement.AREA_UNDEFINED){
								isAreaUndefined = true;
							}else{
								aoiArea+= area;
							}
							double val = getSurfaceCollectionDataInfoProvider().getValue(i,j);
							mean+= val;
							if(val < min){min = val;}
							if(val > max){max = val;}
							if(!isAreaUndefined){wmean+= val*area;}
						}
					}
				}
				if(count > 0){
					mean/= count;
					if(!isAreaUndefined){wmean/=aoiArea;}
				}
			}
			if(count > 0){
				aoiString =
					"AOI("+count+") Area="+(isAreaUndefined?"No Calc":""+(float)aoiArea)+" WMEAN="+(isAreaUndefined?"No Calc":""+wmean)+",Min="+(float)min+",Max="+(float)max+",Mean="+mean;
			}
			String lineString = "";
			if(polylinePicks.size() > 0){
				double lineLength = 0;
				org.vcell.util.Coordinate lastCentroid = null;
				for(int i=0;i<polylinePicks.size();i+= 1){
					SurfaceCanvas.SurfaceCollectionPick lineElement = (SurfaceCanvas.SurfaceCollectionPick)polylinePicks.elementAt(i);
					cbit.vcell.geometry.surface.Quadrilateral quad =
						(cbit.vcell.geometry.surface.Quadrilateral)
						getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(lineElement.surfaceIndex).getPolygons(lineElement.polygonIndex);
					org.vcell.util.Coordinate centroid = quad.calculateCentroid();
					if(i>0){
						lineLength+= centroid.distanceTo(lastCentroid);
					}
					lastCentroid = centroid;
				}
				lineString = "Line("+polylinePicks.size()+") Len="+(float)lineLength;
			}
			getJLabelInfo().setText(lineString+(lineString.length() > 0 && !aoiString.equals(" ")?" -- ":"")+aoiString);
			if(getJLabelInfo().getText().trim().equals("")){
				getJLabelInfo().setText("info");
			}
		}
	}else{
		getJLabelInfo().setText("No Information Provider for Pick");
	}
}


/**
	 * @return
	 */
	protected JButton getJButtonMakeMovie() {
		if (jButtonMakeMovie == null) {
			jButtonMakeMovie = new JButton();
			jButtonMakeMovie.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					getSurfaceCollectionDataInfoProvider().makeMovie(getSurfaceCanvas1());
				}
			});
			jButtonMakeMovie.setText("Make Movie...");
		}
		return jButtonMakeMovie;
	}
	/**
	 * @return
	 */
	protected JLabel getCanvasDimensionsLabel() {
		if (canvasDimensionsLabel == null) {
			canvasDimensionsLabel = new JLabel();
			canvasDimensionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
			canvasDimensionsLabel.setText("SurfCanv Dim");
		}
		return canvasDimensionsLabel;
	}
	private DisplayAdapterService displayAdapterService;
	private DisplayAdapterServicePanel displayAdapterServicePanel;
	public void setDisplayAdapterService(DisplayAdapterService newValue) {
		if (displayAdapterService == newValue) {
			return;
		}
		displayAdapterService = newValue;
		displayAdapterServicePanel.setDisplayAdapterService(displayAdapterService);
	}
}

