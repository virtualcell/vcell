package cbit.vcell.geometry.gui;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.server.DataAccessException;
/**
 * Insert the type's description here.
 * Creation date: (9/20/2005 9:13:34 AM)
 * @author: Frank Morgan
 */
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
		public cbit.vcell.geometry.Coordinate getCentroid(int surfaceIndex,int polygonIndex);
		public void showComponentInFrame(java.awt.Component comp,String title);
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
	private cbit.vcell.client.data.DataSelectRetrieve dsr = new cbit.vcell.client.data.DataSelectRetrieve();
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
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.table.TableModel ivjTableModel1 = null;
	private javax.swing.JLabel ivjJLabelDirections = null;
	private javax.swing.JLabel ivjJLabelInfo = null;
	private javax.swing.JButton ivjJButtonHomeView = null;
	private javax.swing.JButton ivjJButtonBottomView = null;
	private javax.swing.JButton ivjJButtonFrontView = null;
	private javax.swing.JButton ivjJButtonLeftView = null;
	private javax.swing.JButton ivjJButtonRightView = null;
	private javax.swing.JButton ivjJButtonTopView = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JButton ivjJButtonBackView = null;
	private javax.swing.JCheckBox ivjJCheckBoxBoundingbox = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JButton ivjJButtonSetViewAngle = null;
	private javax.swing.JTextField ivjJTextFieldXViewAngle = null;
	private javax.swing.JTextField ivjJTextFieldYViewAngle = null;
	private javax.swing.JTextField ivjJTextFieldZViewAngle = null;
	private SurfaceCollectionDataInfoProvider fieldSurfaceCollectionDataInfoProvider = null;
	private javax.swing.JButton ivjJButtonDSR = null;
	private javax.swing.JButton ivjJButtonStats = null;

class IvjEventHandler implements 
	java.awt.event.ActionListener, java.awt.event.MouseListener, 
	java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener,
	ComponentListener{
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonHomeView()) 
				connEtoC4(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonFrontView()) 
				connEtoC5(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonBackView()) 
				connEtoC6(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonTopView()) 
				connEtoC7(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonBottomView()) 
				connEtoC8(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonLeftView()) 
				connEtoC9(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonRightView()) 
				connEtoC10(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJTextFieldXViewAngle()) 
				connEtoM3(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJTextFieldYViewAngle()) 
				connEtoM4(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJTextFieldZViewAngle()) 
				connEtoM5(e);
			if (e.getSource() == DataValueSurfaceViewer.this.getJButtonSetViewAngle()) 
				connEtoC11(e);
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
			if (evt.getSource() == DataValueSurfaceViewer.this && (evt.getPropertyName().equals("surfaceCollectionDataInfo"))) 
				connEtoC2(evt);
			if (evt.getSource() == DataValueSurfaceViewer.this.getSurfaceViewerTool1() && (evt.getPropertyName().equals("viewAngleRadians"))) 
				connEtoC12(evt);
			if (evt.getSource() == DataValueSurfaceViewer.this.getSurfaceViewerTool1() && (evt.getPropertyName().equals("currentManipulation"))) 
				connEtoC14(evt);
		}
		public void componentHidden(ComponentEvent e) {}
		public void componentMoved(ComponentEvent e) {}
		public void componentResized(ComponentEvent e) {
			getCanvasDimensionsLabel().setText("w="+getSurfaceCanvas1().getSize().width+" h="+getSurfaceCanvas1().getSize().height);
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
	}else if(actionEvent.getSource() == getJButtonFrontView()){
		getSurfaceViewerTool1().setViewAngleRadians(new cbit.vcell.render.Vect3d(-Math.PI/2,0,0));
	}else if(actionEvent.getSource() == getJButtonBackView()){
		getSurfaceViewerTool1().setViewAngleRadians(new cbit.vcell.render.Vect3d(-Math.PI/2,0,0));
		
	}else if(actionEvent.getSource() == getJButtonTopView()){
		getSurfaceViewerTool1().setViewAngleRadians(new cbit.vcell.render.Vect3d(0,0,0));
	}else if(actionEvent.getSource() == getJButtonBottomView()){
		getSurfaceViewerTool1().setViewAngleRadians(new cbit.vcell.render.Vect3d(0,Math.PI,0));
		
	}else if(actionEvent.getSource() == getJButtonLeftView()){
		getSurfaceViewerTool1().setViewAngleRadians(new cbit.vcell.render.Vect3d(-Math.PI/2,-Math.PI/2,0));
		
	}else if(actionEvent.getSource() == getJButtonRightView()){
		getSurfaceViewerTool1().setViewAngleRadians(new cbit.vcell.render.Vect3d(0,Math.PI/2,0));
	}else if(actionEvent.getSource() == getJButtonSetViewAngle()){
		try{
			double angleX = Double.parseDouble(getJTextFieldXViewAngle().getText())*2*Math.PI/360;
			double angleY = Double.parseDouble(getJTextFieldYViewAngle().getText())*2*Math.PI/360;
			double angleZ = Double.parseDouble(getJTextFieldZViewAngle().getText())*2*Math.PI/360;
			getSurfaceViewerTool1().setViewAngleRadians(new cbit.vcell.render.Vect3d(angleX,angleY,angleZ));
		}catch(NumberFormatException e){
		}
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
 * connEtoC10:  (JButtonRightView.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.configureView(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
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
 * connEtoC11:  (JButtonSetViewAngle.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.configureView(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
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
 * connEtoC12:  (SurfaceViewerTool1.viewAngleRadians --> DataValueSurfaceViewer.surfaceViewerTool1_ViewAngleRadians(Lcbit.vcell.render.Vect3d;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.surfaceViewerTool1_ViewAngleRadians(getSurfaceViewerTool1().getViewAngleRadians());
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
 * connEtoC5:  (JButtonFrontView.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.configureView(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * connEtoC6:  (JButtonBackView.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.configureView(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
 * connEtoC7:  (JButtonTopView.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.configureView(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
 * connEtoC8:  (JButtonBottomView.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.configureView(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
 * connEtoC9:  (JButtonLeftView.action.actionPerformed(java.awt.event.ActionEvent) --> DataValueSurfaceViewer.configureView(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
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
 * connEtoM3:  (JTextField3.action.actionPerformed(java.awt.event.ActionEvent) --> JButtonSetViewAngle.doClick(I)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonSetViewAngle().doClick(68);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (JTextField2.action.actionPerformed(java.awt.event.ActionEvent) --> JButtonSetViewAngle.doClick(I)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonSetViewAngle().doClick(68);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> JButtonSetViewAngle.doClick(I)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonSetViewAngle().doClick(68);
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
	getJLabel4().setVisible(false);
	getJLabel1().setVisible(false);
	getJLabel2().setVisible(false);
	getJLabel3().setVisible(false);
	getJTextFieldXViewAngle().setVisible(false);
	getJTextFieldYViewAngle().setVisible(false);
	getJTextFieldZViewAngle().setVisible(false);
	getJButtonSetViewAngle().setVisible(false);
	getJButtonBackView().setVisible(false);
	getJButtonBottomView().setVisible(false);
	getJButtonFrontView().setVisible(false);
	getJButtonLeftView().setVisible(false);
	getJButtonRightView().setVisible(false);
	getJButtonTopView().setVisible(false);
	

}


/**
 * Return the JButtonBack property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonBackView() {
	if (ivjJButtonBackView == null) {
		try {
			ivjJButtonBackView = new javax.swing.JButton();
			ivjJButtonBackView.setName("JButtonBackView");
			ivjJButtonBackView.setText("Back");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonBackView;
}

/**
 * Return the JButtonBottomView property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonBottomView() {
	if (ivjJButtonBottomView == null) {
		try {
			ivjJButtonBottomView = new javax.swing.JButton();
			ivjJButtonBottomView.setName("JButtonBottomView");
			ivjJButtonBottomView.setText("Bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonBottomView;
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
 * Return the JButtonFrontView property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFrontView() {
	if (ivjJButtonFrontView == null) {
		try {
			ivjJButtonFrontView = new javax.swing.JButton();
			ivjJButtonFrontView.setName("JButtonFrontView");
			ivjJButtonFrontView.setText("Front");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFrontView;
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
			ivjJButtonHomeView.setText("HOME");
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
 * Return the JButtonLeftView property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonLeftView() {
	if (ivjJButtonLeftView == null) {
		try {
			ivjJButtonLeftView = new javax.swing.JButton();
			ivjJButtonLeftView.setName("JButtonLeftView");
			ivjJButtonLeftView.setText("Left");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonLeftView;
}


/**
 * Return the JButtonRightView property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRightView() {
	if (ivjJButtonRightView == null) {
		try {
			ivjJButtonRightView = new javax.swing.JButton();
			ivjJButtonRightView.setName("JButtonRightView");
			ivjJButtonRightView.setText("Right");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRightView;
}


/**
 * Return the JButtonSetViewAngle property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSetViewAngle() {
	if (ivjJButtonSetViewAngle == null) {
		try {
			ivjJButtonSetViewAngle = new javax.swing.JButton();
			ivjJButtonSetViewAngle.setName("JButtonSetViewAngle");
			ivjJButtonSetViewAngle.setText("Apply");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSetViewAngle;
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
			ivjJButtonStats.setText("Memb. Stats");
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
 * Return the JButtonTopView property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonTopView() {
	if (ivjJButtonTopView == null) {
		try {
			ivjJButtonTopView = new javax.swing.JButton();
			ivjJButtonTopView.setName("JButtonTopView");
			ivjJButtonTopView.setText("Top");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonTopView;
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("X:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Y:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}


/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Z:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}


/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("View Angle (deg)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}

/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("Preset Views");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
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
			ivjJLabelInfo = new javax.swing.JLabel();
			ivjJLabelInfo.setName("JLabelInfo");
			ivjJLabelInfo.setText(" ");
			ivjJLabelInfo.setForeground(java.awt.Color.red);
			ivjJLabelInfo.setMinimumSize(new java.awt.Dimension(20, 14));
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7};
			ivjJPanel3.setLayout(gridBagLayout);

			java.awt.GridBagConstraints constraintsJButtonHomeView = new java.awt.GridBagConstraints();
			constraintsJButtonHomeView.gridx = 0; constraintsJButtonHomeView.gridy = 6;
			constraintsJButtonHomeView.gridwidth = 2;
			constraintsJButtonHomeView.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonHomeView.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonHomeView(), constraintsJButtonHomeView);

			java.awt.GridBagConstraints constraintsJButtonFrontView = new java.awt.GridBagConstraints();
			constraintsJButtonFrontView.gridx = 0; constraintsJButtonFrontView.gridy = 9;
			constraintsJButtonFrontView.gridwidth = 2;
			constraintsJButtonFrontView.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonFrontView.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonFrontView(), constraintsJButtonFrontView);

			java.awt.GridBagConstraints constraintsJButtonTopView = new java.awt.GridBagConstraints();
			constraintsJButtonTopView.gridx = 0; constraintsJButtonTopView.gridy = 7;
			constraintsJButtonTopView.gridwidth = 2;
			constraintsJButtonTopView.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonTopView.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonTopView(), constraintsJButtonTopView);

			java.awt.GridBagConstraints constraintsJButtonLeftView = new java.awt.GridBagConstraints();
			constraintsJButtonLeftView.gridx = 0; constraintsJButtonLeftView.gridy = 11;
			constraintsJButtonLeftView.gridwidth = 2;
			constraintsJButtonLeftView.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonLeftView.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonLeftView(), constraintsJButtonLeftView);

			java.awt.GridBagConstraints constraintsJButtonBackView = new java.awt.GridBagConstraints();
			constraintsJButtonBackView.gridx = 0; constraintsJButtonBackView.gridy = 10;
			constraintsJButtonBackView.gridwidth = 2;
			constraintsJButtonBackView.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonBackView.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonBackView(), constraintsJButtonBackView);

			java.awt.GridBagConstraints constraintsJButtonBottomView = new java.awt.GridBagConstraints();
			constraintsJButtonBottomView.gridx = 0; constraintsJButtonBottomView.gridy = 8;
			constraintsJButtonBottomView.gridwidth = 2;
			constraintsJButtonBottomView.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonBottomView.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonBottomView(), constraintsJButtonBottomView);

			java.awt.GridBagConstraints constraintsJButtonRightView = new java.awt.GridBagConstraints();
			constraintsJButtonRightView.gridx = 0; constraintsJButtonRightView.gridy = 12;
			constraintsJButtonRightView.gridwidth = 2;
			constraintsJButtonRightView.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonRightView.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonRightView(), constraintsJButtonRightView);

			java.awt.GridBagConstraints constraintsJTextFieldZViewAngle = new java.awt.GridBagConstraints();
			constraintsJTextFieldZViewAngle.gridx = 1; constraintsJTextFieldZViewAngle.gridy = 3;
			constraintsJTextFieldZViewAngle.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldZViewAngle.weightx = 1.0;
			constraintsJTextFieldZViewAngle.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJTextFieldZViewAngle(), constraintsJTextFieldZViewAngle);

			java.awt.GridBagConstraints constraintsJTextFieldYViewAngle = new java.awt.GridBagConstraints();
			constraintsJTextFieldYViewAngle.gridx = 1; constraintsJTextFieldYViewAngle.gridy = 2;
			constraintsJTextFieldYViewAngle.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldYViewAngle.weightx = 1.0;
			constraintsJTextFieldYViewAngle.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJTextFieldYViewAngle(), constraintsJTextFieldYViewAngle);

			java.awt.GridBagConstraints constraintsJTextFieldXViewAngle = new java.awt.GridBagConstraints();
			constraintsJTextFieldXViewAngle.gridx = 1; constraintsJTextFieldXViewAngle.gridy = 1;
			constraintsJTextFieldXViewAngle.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldXViewAngle.weightx = 1.0;
			constraintsJTextFieldXViewAngle.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJTextFieldXViewAngle(), constraintsJTextFieldXViewAngle);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 1;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 2;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 3;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 0;
			constraintsJLabel4.gridwidth = 2;
			constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel4(), constraintsJLabel4);

			java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
			constraintsJLabel5.gridx = 0; constraintsJLabel5.gridy = 5;
			constraintsJLabel5.gridwidth = 2;
			constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel5(), constraintsJLabel5);

			java.awt.GridBagConstraints constraintsJButtonSetViewAngle = new java.awt.GridBagConstraints();
			constraintsJButtonSetViewAngle.gridx = 0; constraintsJButtonSetViewAngle.gridy = 4;
			constraintsJButtonSetViewAngle.gridwidth = 2;
			constraintsJButtonSetViewAngle.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonSetViewAngle(), constraintsJButtonSetViewAngle);

			java.awt.GridBagConstraints constraintsJCheckBoxBoundingbox = new java.awt.GridBagConstraints();
			constraintsJCheckBoxBoundingbox.gridx = 0; constraintsJCheckBoxBoundingbox.gridy = 13;
			constraintsJCheckBoxBoundingbox.gridwidth = 2;
			constraintsJCheckBoxBoundingbox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJCheckBoxBoundingbox(), constraintsJCheckBoxBoundingbox);

			java.awt.GridBagConstraints constraintsJButtonDSR = new java.awt.GridBagConstraints();
			constraintsJButtonDSR.gridx = 0; constraintsJButtonDSR.gridy = 14;
			constraintsJButtonDSR.gridwidth = 2;
			constraintsJButtonDSR.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonDSR.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonDSR(), constraintsJButtonDSR);

			java.awt.GridBagConstraints constraintsJButtonStats = new java.awt.GridBagConstraints();
			constraintsJButtonStats.gridx = 0; constraintsJButtonStats.gridy = 15;
			constraintsJButtonStats.gridwidth = 2;
			constraintsJButtonStats.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonStats.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonStats(), constraintsJButtonStats);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridy = 16;
			gridBagConstraints.gridx = 0;
			ivjJPanel3.add(getJButtonMakeMovie(), gridBagConstraints);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
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
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldXViewAngle() {
	if (ivjJTextFieldXViewAngle == null) {
		try {
			ivjJTextFieldXViewAngle = new javax.swing.JTextField();
			ivjJTextFieldXViewAngle.setName("JTextFieldXViewAngle");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldXViewAngle;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldYViewAngle() {
	if (ivjJTextFieldYViewAngle == null) {
		try {
			ivjJTextFieldYViewAngle = new javax.swing.JTextField();
			ivjJTextFieldYViewAngle.setName("JTextFieldYViewAngle");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldYViewAngle;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldZViewAngle() {
	if (ivjJTextFieldZViewAngle == null) {
		try {
			ivjJTextFieldZViewAngle = new javax.swing.JTextField();
			ivjJTextFieldZViewAngle.setName("JTextFieldZViewAngle");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldZViewAngle;
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
	getJButtonFrontView().addActionListener(ivjEventHandler);
	getJButtonBackView().addActionListener(ivjEventHandler);
	getJButtonTopView().addActionListener(ivjEventHandler);
	getJButtonBottomView().addActionListener(ivjEventHandler);
	getJButtonLeftView().addActionListener(ivjEventHandler);
	getJButtonRightView().addActionListener(ivjEventHandler);
	getJTextFieldXViewAngle().addActionListener(ivjEventHandler);
	getJTextFieldYViewAngle().addActionListener(ivjEventHandler);
	getJTextFieldZViewAngle().addActionListener(ivjEventHandler);
	getJButtonSetViewAngle().addActionListener(ivjEventHandler);
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

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 3;
		constraintsJScrollPane1.gridwidth = 2;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsSurfaceCanvas1 = new java.awt.GridBagConstraints();
		constraintsSurfaceCanvas1.gridx = 1; constraintsSurfaceCanvas1.gridy = 0;
		constraintsSurfaceCanvas1.gridwidth = 2;
		constraintsSurfaceCanvas1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsSurfaceCanvas1.weightx = 1.0;
		constraintsSurfaceCanvas1.weighty = 1.0;
		constraintsSurfaceCanvas1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSurfaceCanvas1(), constraintsSurfaceCanvas1);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 0;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel3.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsJPanel3.weighty = 1.0;
		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel3(), constraintsJPanel3);

		java.awt.GridBagConstraints constraintsJLabelDirections = new java.awt.GridBagConstraints();
		constraintsJLabelDirections.gridx = 1; constraintsJLabelDirections.gridy = 1;
		constraintsJLabelDirections.gridwidth = 2;
		constraintsJLabelDirections.insets = new java.awt.Insets(4, 4, 4, 4);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		add(getCanvasDimensionsLabel(), gridBagConstraints);
		add(getJLabelDirections(), constraintsJLabelDirections);

		java.awt.GridBagConstraints constraintsJLabelInfo = new java.awt.GridBagConstraints();
		constraintsJLabelInfo.gridx = 1; constraintsJLabelInfo.gridy = 2;
		constraintsJLabelInfo.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelInfo.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelInfo(), constraintsJLabelInfo);
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
	
	//if(dsrJDialog == null){
		dsr.setModeInstruction("(CNTRL-CLICK on surface to add/remove from selection)");
		//dsrJDialog = new javax.swing.JDialog((java.awt.Frame)cbit.util.BeanUtils.findTypeParentOfComponent(this,java.awt.Frame.class));
		//dsrJDialog.getContentPane().add(dsr);
		//dsrJDialog.pack();
		//cbit.util.BeanUtils.centerOnComponent(dsrJDialog,this);
	//}
	//dsrJDialog.show();

	getSurfaceCollectionDataInfoProvider().showComponentInFrame(dsr,"Define Region Of Interest");
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
					cbit.vcell.client.PopupGenerator.showErrorDialog(e2.getMessage());
				}
			}else{
				cbit.vcell.client.PopupGenerator.showErrorDialog("Region of Interest must be defined to calculate Statistics");
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
		String surfSelect = (String)cbit.vcell.client.PopupGenerator.showListDialog(this,surfNames,"Apply Analytic Area to Surface");

		cbit.vcell.client.data.DataSelectRetrieve.DSRState dsrState = dsr.getDSRState();
		boolean pickOP = (dsrState.selectionType == cbit.vcell.client.data.DataSelectRetrieve.SELECT_TYPE_ADD?true:false);
		cbit.vcell.parser.Expression xyzExp = dsrState.selectAreaAnalytic;
		
		if(xyzExp != null){
			for(int surf=0;surf <surfNames.length;surf+= 1){
				if(surfNames[surf].equals(surfSelect)){
					int polygonCount = getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(surf).getPolygonCount();
					for(int i=0;i<polygonCount;i+= 1){
						cbit.vcell.geometry.Coordinate coord = getSurfaceCollectionDataInfoProvider().getCentroid(surf,i);
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
		cbit.vcell.client.PopupGenerator.showErrorDialog("Error while picking Analytic AreaOfInterest\n"+e.getClass().getName()+"\n"+e.getMessage());
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
							cbit.vcell.geometry.Coordinate destination = ((cbit.vcell.geometry.surface.Quadrilateral)surf.getPolygons(lastPick.polygonIndex)).calculateCentroid();
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
								cbit.vcell.geometry.Coordinate vertex = ((cbit.vcell.geometry.surface.Quadrilateral)surf.getPolygons(prevPickPolyIndex)).calculateCentroid();
								for(int i=0;i<neighbors.length;i+= 1){
									cbit.vcell.geometry.Coordinate neighborCoord = ((cbit.vcell.geometry.surface.Quadrilateral)surf.getPolygons(neighbors[i])).calculateCentroid();
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
	
	bDisplay = (getSurfaceCollectionDataInfo() != null?new boolean[getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaceCount()]:null);
	if(bDisplay != null){java.util.Arrays.fill(bDisplay,true);}
	bWireframe = (getSurfaceCollectionDataInfo() != null?new boolean[getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaceCount()]:null);
	if(bWireframe != null){java.util.Arrays.fill(bWireframe,false);}

	//init with NO transparency
	transparentSelection = (getSurfaceCollectionDataInfo() != null?new java.awt.AlphaComposite[getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaceCount()]:null);
	if(transparentSelection != null){java.util.Arrays.fill(transparentSelection,java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC));}

	
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
	if(!getJLabelInfo().getText().equals(" ")){
		getJLabelInfo().setText(" ");
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
	if(!getJLabelInfo().getText().equals(" ")){
		getJLabelInfo().setText(" ");
	}
	lastMouse = null;
	mouseOverTimer.restart();
}


/**
 * Comment
 */
private void surfaceCanvas1_MouseMoved(java.awt.event.MouseEvent mouseEvent) {
	if(!getJLabelInfo().getText().equals(" ")){
		getJLabelInfo().setText(" ");
	}
	lastMouse = mouseEvent;
	mouseOverTimer.restart();
}


/**
 * Comment
 */
private void surfaceViewerTool1_ViewAngleRadians(cbit.vcell.render.Vect3d arg1) {

	df.setMaximumFractionDigits(2);

	getJTextFieldXViewAngle().setText(df.format(arg1.getX()*180/Math.PI));
	getJTextFieldYViewAngle().setText(df.format(arg1.getY()*180/Math.PI));
	getJTextFieldZViewAngle().setText(df.format(arg1.getZ()*180/Math.PI));
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
			cbit.vcell.geometry.Coordinate centroid = ((cbit.vcell.geometry.surface.Quadrilateral)getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(currentPick.surfaceIndex).getPolygons(currentPick.polygonIndex)).calculateCentroid();
			df.setMaximumFractionDigits(5);
			double area = getSurfaceCollectionDataInfoProvider().getArea(currentPick.surfaceIndex,currentPick.polygonIndex);
			getJLabelInfo().setText(
				" ("+getSurfaceCollectionDataInfoProvider().getValueDescription(currentPick.surfaceIndex,currentPick.polygonIndex)+
				"["+getSurfaceCollectionDataInfoProvider().getMembraneIndex(currentPick.surfaceIndex,currentPick.polygonIndex)+"]"+")"+
				" val= "+ (float)getSurfaceCollectionDataInfoProvider().getValue(currentPick.surfaceIndex,currentPick.polygonIndex) +
				" area="+ (area != cbit.vcell.solvers.MembraneElement.AREA_UNDEFINED?area+"":"No Calc")+
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
				cbit.vcell.geometry.Coordinate lastCentroid = null;
				for(int i=0;i<polylinePicks.size();i+= 1){
					SurfaceCanvas.SurfaceCollectionPick lineElement = (SurfaceCanvas.SurfaceCollectionPick)polylinePicks.elementAt(i);
					cbit.vcell.geometry.surface.Quadrilateral quad =
						(cbit.vcell.geometry.surface.Quadrilateral)
						getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(lineElement.surfaceIndex).getPolygons(lineElement.polygonIndex);
					cbit.vcell.geometry.Coordinate centroid = quad.calculateCentroid();
					if(i>0){
						lineLength+= centroid.distanceTo(lastCentroid);
					}
					lastCentroid = centroid;
				}
				lineString = "Line("+polylinePicks.size()+") Len="+(float)lineLength;
			}
			getJLabelInfo().setText(lineString+(lineString.length() > 0 && !aoiString.equals(" ")?" -- ":"")+aoiString);
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
			canvasDimensionsLabel.setBorder(new LineBorder(Color.black, 1, false));
			canvasDimensionsLabel.setText("SurfCanv Dim");
		}
		return canvasDimensionsLabel;
	}
}