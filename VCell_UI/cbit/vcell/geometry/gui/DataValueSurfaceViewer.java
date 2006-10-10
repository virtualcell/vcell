package cbit.vcell.geometry.gui;
import org.vcell.expression.IExpression;

import cbit.render.SurfaceCanvas;
import cbit.render.SurfaceViewerTool;
import cbit.render.*;
import cbit.render.objects.Node;
import cbit.render.objects.Quadrilateral;
import cbit.render.objects.Surface;
import cbit.render.objects.SurfaceCollection;
import cbit.render.objects.Vect3d;
import cbit.util.Coordinate;
/**
 * Insert the type's description here.
 * Creation date: (9/20/2005 9:13:34 AM)
 * @author: Frank Morgan
 */
public class DataValueSurfaceViewer extends javax.swing.JPanel implements java.awt.event.ActionListener {

	//
	public interface SurfaceCollectionDataInfoProvider {
		public double getValue(int surfaceIndex,int polygonIndex);
		public String getValueDescription(int surfaceIndex,int polygonIndex);
		public int[][] getSurfacePolygonColors();
		public float getArea(int surfaceIndex,int polygonIndex);
		public Vect3d getNormal(int surfaceIndex,int polygonIndex);
		public int getMembraneIndex(int surfaceIndex,int polygonIndex);
		public cbit.util.TimeSeriesJobResults getTimeSeriesData(int[][] indices,boolean bAllTimes,boolean bTimeStats,boolean bSpaceStats) throws cbit.util.DataAccessException;
		public Vect3d getCentroid(int surfaceIndex,int polygonIndex);
		public void showComponentInFrame(java.awt.Component comp,String title);
		public java.awt.Color getROIHighlightColor();
	}

	public class SurfaceCollectionDataInfo {
		private SurfaceCollection surfaceCollection;
		private cbit.util.Origin origin;
		private cbit.util.Extent extent;
		private String[] surfaceNames;
		private Double[] surfaceAreas;
		private int dimension;
		
		public SurfaceCollectionDataInfo(
			SurfaceCollection argSurfaceCollection,
			cbit.util.Origin argOrigin,
			cbit.util.Extent argExtent,
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
		public cbit.util.Origin getSurfaceCollectionOrigin(){return origin;}
		public cbit.util.Extent getSurfaceCollectionExtent(){return extent;}
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
		
		private static final int TRANSPARENT_NONE = 0;
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
		public Class getColumnClass(int col)	{
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
	private java.util.Vector polylinePicks = new java.util.Vector();
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
	private javax.swing.ButtonGroup ivjButtonGroupSelectAddRemove = null;
	private javax.swing.ButtonGroup ivjButtonGroupSelectFunc = null;
	private javax.swing.JButton ivjJButtonDSR = null;
	private javax.swing.JButton ivjJButtonStats = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener {
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
		getSurfaceViewerTool1().setViewAngleRadians(new Vect3d(-Math.PI/2,0,0));
	}else if(actionEvent.getSource() == getJButtonBackView()){
		getSurfaceViewerTool1().setViewAngleRadians(new Vect3d(-Math.PI/2,0,0));
		
	}else if(actionEvent.getSource() == getJButtonTopView()){
		getSurfaceViewerTool1().setViewAngleRadians(new Vect3d(0,0,0));
	}else if(actionEvent.getSource() == getJButtonBottomView()){
		getSurfaceViewerTool1().setViewAngleRadians(new Vect3d(0,Math.PI,0));
		
	}else if(actionEvent.getSource() == getJButtonLeftView()){
		getSurfaceViewerTool1().setViewAngleRadians(new Vect3d(-Math.PI/2,-Math.PI/2,0));
		
	}else if(actionEvent.getSource() == getJButtonRightView()){
		getSurfaceViewerTool1().setViewAngleRadians(new Vect3d(0,Math.PI/2,0));
	}else if(actionEvent.getSource() == getJButtonSetViewAngle()){
		try{
			double angleX = Double.parseDouble(getJTextFieldXViewAngle().getText())*2*Math.PI/360;
			double angleY = Double.parseDouble(getJTextFieldYViewAngle().getText())*2*Math.PI/360;
			double angleZ = Double.parseDouble(getJTextFieldZViewAngle().getText())*2*Math.PI/360;
			getSurfaceViewerTool1().setViewAngleRadians(new Vect3d(angleX,angleY,angleZ));
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
 * Return the ButtonGroupSelectAddRemove property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroupSelectAddRemove() {
	if (ivjButtonGroupSelectAddRemove == null) {
		try {
			ivjButtonGroupSelectAddRemove = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupSelectAddRemove;
}


/**
 * Return the ButtonGroup1 property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroupSelectFunc() {
	if (ivjButtonGroupSelectFunc == null) {
		try {
			ivjButtonGroupSelectFunc = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupSelectFunc;
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
			ivjJLabelDirections.setText("ROTATE(leftMB-drag)  MOVE(rightMB-drag)  ZOOM(leftMB-rigthMB-drag)");
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
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

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
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
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
			ivjSurfaceCanvas1 = new cbit.render.SurfaceCanvas();
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
			ivjSurfaceViewerTool1 = new cbit.render.SurfaceViewerTool();
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
			cbit.util.Origin argOrigin,
			cbit.util.Extent argExtent,
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
					getSurfaceCollectionDataInfoProvider().getTimeSeriesData(indices,true,false,true);	
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
 * Creation date: (2/22/2006 5:28:13 PM)
 */
public void pickByAnalytic() {

	try{
		String[] surfNames = getSurfaceCollectionDataInfo().getSurfaceNames();
		String surfSelect = (String)cbit.vcell.client.PopupGenerator.showListDialog(this,surfNames,"Apply Analytic Area to Surface");

		cbit.vcell.client.data.DataSelectRetrieve.DSRState dsrState = dsr.getDSRState();
		boolean pickOP = (dsrState.selectionType == cbit.vcell.client.data.DataSelectRetrieve.SELECT_TYPE_ADD?true:false);
		IExpression xyzExp = dsrState.selectAreaAnalytic;
		
		if(xyzExp != null){
			for(int surf=0;surf <surfNames.length;surf+= 1){
				if(surfNames[surf].equals(surfSelect)){
					int polygonCount = getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(surf).getPolygonCount();
					for(int i=0;i<polygonCount;i+= 1){
						Vect3d coord = getSurfaceCollectionDataInfoProvider().getCentroid(surf,i);
						if(coord == null){
							coord = ((Quadrilateral)getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(surf).getPolygons(i)).calculateCentroid();
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
private void pickByRange(cbit.util.Range pickRange) {

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
private int[] pickNeighbors(Surface surf,int[] targets,boolean[] bPicked) {

	int[] newNeighbors = new int[10];
	int newNeighborCount = 0;
	for(int t=0;t<targets.length;t+= 1){
		Node[] pickedNodes = surf.getPolygons(targets[t]).getNodes();
		for(int i=0;i<surf.getPolygonCount();i+= 1){
			if(bPicked[i]){
				continue;
			}
			Node[] surfNodes = surf.getPolygons(i).getNodes();
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
					SurfaceCollection sc = getSurfaceCollectionDataInfo().getSurfaceCollection();
					Surface surf = sc.getSurfaces(lastPick.surfaceIndex);

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
							SurfaceCollection sc = getSurfaceCollectionDataInfo().getSurfaceCollection();
							Surface surf = sc.getSurfaces(lastPick.surfaceIndex);
							Vect3d destination = ((Quadrilateral)surf.getPolygons(lastPick.polygonIndex)).calculateCentroid();
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
								Vect3d vertex = ((Quadrilateral)surf.getPolygons(prevPickPolyIndex)).calculateCentroid();
								for(int i=0;i<neighbors.length;i+= 1){
									Vect3d neighborCoord = ((Quadrilateral)surf.getPolygons(neighbors[i])).calculateCentroid();
									double angle = cbit.vcell.geometry.Curve.getAngle(new Coordinate(vertex.q[0],vertex.q[1],vertex.q[2]),
											new Coordinate(destination.q[0],destination.q[1],destination.q[2]),
											new Coordinate(neighborCoord.q[0],neighborCoord.q[1],neighborCoord.q[2]));
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
private void surfaceViewerTool1_ViewAngleRadians(Vect3d arg1) {

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
			Vect3d centroid = ((Quadrilateral)getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(currentPick.surfaceIndex).getPolygons(currentPick.polygonIndex)).calculateCentroid();
			df.setMaximumFractionDigits(5);
			double area = getSurfaceCollectionDataInfoProvider().getArea(currentPick.surfaceIndex,currentPick.polygonIndex);
			getJLabelInfo().setText(
				" ("+getSurfaceCollectionDataInfoProvider().getValueDescription(currentPick.surfaceIndex,currentPick.polygonIndex)+
				"["+getSurfaceCollectionDataInfoProvider().getMembraneIndex(currentPick.surfaceIndex,currentPick.polygonIndex)+"]"+")"+
				" val= "+ (float)getSurfaceCollectionDataInfoProvider().getValue(currentPick.surfaceIndex,currentPick.polygonIndex) +
				" area="+ (area != cbit.vcell.mesh.MembraneElement.AREA_UNDEFINED?area+"":"No Calc")+
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
							if(area == cbit.vcell.mesh.MembraneElement.AREA_UNDEFINED){
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
				Vect3d lastCentroid = null;
				for(int i=0;i<polylinePicks.size();i+= 1){
					SurfaceCanvas.SurfaceCollectionPick lineElement = (SurfaceCanvas.SurfaceCollectionPick)polylinePicks.elementAt(i);
					Quadrilateral quad =
						(Quadrilateral)
						getSurfaceCollectionDataInfo().getSurfaceCollection().getSurfaces(lineElement.surfaceIndex).getPolygons(lineElement.polygonIndex);
					Vect3d centroid = quad.calculateCentroid();
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G670171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DD8D5D53AB095B5BAD1D2B125C549B5AAB2EA281CE2E61822F20A0A12CA4BA6AAAA6F9D1A6B3D51F33D0D5F7DEC5A82AA8A99AA0EBAD151CD938A9104C4D1CBFEACAC8894C2D4948E67ECB8878E679C4F5960C1296E3B565A6F5A7B1C33B6A07373FD4F73FDBE4F4BBE6BDD6F7F7A7F59DBA96F0308E5CB5373A4E969B2C97DD739D412DCE7A469630B1C3B91976AE84B16F47FBE84D8A35DF4B693
	1E9110D7185A161DABF59C4B0274ACC8A73C52163D81FE2F13CC4732866107011E94C512BADE7EFA4A68FA6E5E02FAD650F456319CF82E8608859CF9E6BA097E02E39BD5FC320AC7D81AAEC98BA84D1EE379AA2E9E52DDG91G331C0CBE911EAB29668B2BF629F49D17C5C90B9EF1273FCD6468E4A2943B98EF337AAC107EEB69E29751E521F6921EC9100E87A04EE7C8B72C2D00675E667E5E4F4D5DB625E448AC5B6DA53D32F3C0D65C43A53D03362A2ABA13E2EAB359876516C1F70F49AC3759645332DBB9C5
	64C653B8E7909910165C84375ECCF445C2BA89A01563DF3090FC9D7C6EGD05C685F3E2DFBD57B5B4F5EADAD687D6D4AF78C7C8BF1B33FE639B57F8ABE694E58C44B620F9F131827C1DE1E876A81C083009DA08460DDDA877E346593F8D6F7AB354E018127235A652A31DB5C0DCE0BEC075FD5D51023628E4BE6275BA2C92C1C7B0E958B6C998D981F771C79D8CC224A314C238F5FA6A55FF9E57A8AC1CC12234E26C7FAD8AC62BCDACC186E5B8DF5A7FBFC6B242F6EA312DE37746A0E954B843A731F5E31A2CD15
	1329E83A6B94D25613E99D588C3F899FE9C88CFF9545CF5261D97DFC1762C9BA8A72F28631EE342FC6DB9AB2E7C839E5D78963B0757E10F457A033E152203FADA5BDC4762CC126330C5B52C47185C39A1E59320056D3128E013C0D5E36ECD63F24A252DEA307217DG4C87C88348BD5316DD8C307F8C56312FBE5EBE4EBA56ECF2ABB503B63BC5F6CB0CD75A7AE7F84ADE45EDBA6870A8A687B4B7135BE66A364B9EC925D9391544205B07EF75D6D2774D2063AB59A13BCD0A4D69G69A6E846325B43E8B6ECDDC3
	E85CB24DED358D4820E040A5215C8F3602FE49EE72A85F39ACA6C556A8D87E562D24CC06CCEDA0918440EF9617ED1F10F8F5017FDE00C9BC8EB3283CEFE437C336D7D5B5B81D7D032ED66209B47F8C09F3826DBB4261772CB3986322CF89AE7FECDBF645D914736C5635BAB9F5B6B3B15F649E46B6B6458C1D39D48F74F6009000C8000433D8077E3D9D6BD05D5B37CA4573FEA62CC32124BE1FE5F5A7EB443FADDDCBF590BBCBA12F99408590BA0263400766F61D2CFA558E3D7C043362B697E900376B70CCC715
	0F0E8C952F6749B6E86FAB8D704BF43FD97C2FAAA6E59983FEA582AC9C41B248ABACE0F53A4664113196B73921824A962DC4181D95BDDDEAFCB3D49DABD49B08FD24EF7F0835A7D25F2575G9BD47DC457FBG9FC8EC3C2D845882309560D300AFG3681EC67B1790E6A4A7C9E5AA0C0AB4088C0A4C0A240A2007C9F5A326BG6CGE1GF1G8BGF2FE04BA865081908C908DB097E0894066A098BBG940070A0AC63BFD802169A7B35DA75EDBCFEFD2473EB356A9B7A25AF1FCA3F74978665787F923032E814C9
	D9084A61EDDDD90C279C562865D024AB8BD28EE5FEFA377825377DDF900B7F53407A06D5C5A45672106CD06AE4176C30408F0F1A372608140BBA1E75B8B94F860AF74178D78B830E07635FABA265E1D294372DFBD011DBE4C531B9FA357CADD417E7305B258D1D282B3808548567E9987FB8FDD99199BB4CCE07C3261D94E7D1734B6958A55B6581B03A56B968D098FE77A7E41E5FAF8F2BE3673ECF16CBE46C03D9300755398CA946C46A1B4BED9B002E0F8C65D25EF9E4DCFB114F7D894C067E032C81D26139A4
	884724539F611834794C4DD2FC27ABDDB4471AFA4314740E7358B860BACF9B1318CDA51F0AED2F20E3280A77B0DB1F5CC266956219BCEBC730BE7A14444AA72B55693417E17E407B44DFBA025349CC190AF71AC89B2B99D4942763AB37F35055823A4D4A17038E33043C5E777EA2222936D88E4B834EA199471F24ED4427D070B592A06EBC9CFFE2BEA579B887D0871EDA275D4E0A9572F24F27F3B7BACEE37C661E47E2CFFEC7507CDC8B6637C35AF53E164F78AF326879D383726FB04CE7F6DEDB1CCB6A27FCEA
	D006299B8955B73C8EC5962F754BEBD36B934B0FAEA671356B66C94A85C46735BD38CE4C3B0069ABD61FF597B05DB941046EC8AF52452B7973AFE07315B415AE1652FDE2C53A76E0B6EE3A60998C9085D082F82677E5CE17F9A113172FBE6BAE546B0D30A1DD4705ECEE2440B398A0CA25E3721CFDC897F7A153B7D71517FB111EEEC7BFDF97833E8BA068A2C6976FC3778627DB8C785C1F98514D34A3DDA950549B521D9CC0BAAB21B124DB6BC03A882009B524FB10534D871AB4C33AB3CE244B1D846B56C901F4
	AC4EA5AE9E972069A2F4G71GA9G0B81981D67945265CC06394CE4DF3A3A49C897696675GF0AEBF3AD0C8B37B66F910AEF2B24B0F1BAC72E31A02F4CBA633FA1777D3C6D77AD3BDDD6BA0520DGBE8CE016CA374807EE63905255DD4C64F5DC4C68C6AE56534D65F4D3AFE675AAC625CB73211BFA9A69AAC2E0FE87E08FE1F451A1FA3A8EAF52AD8AE172F2AED16B7DA5FA3A770711EE9260A3G62D5BA69D2C6477A5E7FA8A263815DE369B38D193CA51E53B026D5D5FD5DEC025E0C6D239CBC1E255257DF0A
	7BA503A7C9DF98846929GB117625871445B38DF92569FA5A53FDA3642E8BFA8C135E361255A58B16B356A652C2F7D4BF62233F8CADB765EA9584FEE5D26DF33D5FBBCCE330DAE9755BE6A69ED7A3105611E5B368A1EBDB637C7B988033F977B33173691FB49FAD2C52B71D85A32DD9AEB4FA9E094D277A63E7FD26FFB5CBB5F27E79B785EBCC59DB727E83EEF9EE8DD41567815965C8F0B017C54A9389F967F61FF8B4A2AC4A15DF1D515B67661DE09399C6D89BDF3035475332B8D6DA988E5F66C8F556CA97EE1
	71B226774D127704F5C4B7E2CA38BF71A05D37A0FCDE109583404658ED3D84EFF358941B49EEBBA39796B1594B8EED0627C5B876F79E541137B11FE62C5B2F5A1CF85B8CE96F0FA753C57BF1135E3126272A3E64DE2679D4FFF9F7C687G33B5085AC46C6BG1A304B50565F87584A70EB2C24AEFB180935A64710094C7D18DD373E2F37AB4BF421502E10D7FB5393D5FB96696C0A811BE6F33BDA2CE8575E4B211F3B9C4BBF6FF3722473BCF832795CA92BFEBE97FDB95B2FD8G4F451723BF09D6B2DFD42CB68F
	33F564A85A9A14B8CB5A386E92212DB4F6D7B09BF7DD2159BA92511D919A0136E65B303CA781CD7C952853ED5B240B951F8B11C91EBA57E6BE051CA06D53AD7738E50F95CA996B5143DC6EE210D99A06F2CB7B509766FFE50F5EFFAB5005G44G4C66BC6DFDC44EGEB9A2A7C29FDA8BF996872FE0634EF75ED2334039E39047E2D355B4C7D322510547C9253E925042EA5CA9AC95E817233087B227C197CF681FD3353BE5C6BF494B698F26DA12E2A369C6FC7DB9A400EE0GB61F1BF7D8BF1F0B837C82002CAB
	51567B7BF74053C483DB429E4D323B47699E8834371AE623414C5EA7AA499EAC2CBFFAEC3D03EE5A6882AC567101494C5E8F6CE8EFBE5852FAA55ADB50AD32B78472995FD31CAF82F073B9DF1710AF77AA647B49G72ED86DC47D548B7E8937105F13EBD8E640B82DC8A675BE89671ED1C0AFC2FF13EE240D926A25FF21308AF1C73DD6FC43E984025F23E73ADA23E02E948F74405FC950033CFC33EA49FFD115398DF62B4AC7FFBDC93ADFFA6FF5FC911DDA953303F89DDC5468B0B13B6014203D0E359FC60CD97
	1E2BAC62BE041F62B13B9AE2F6B57AB057AC72A19C72192C9DEE1495F3B54AFA4C0D32D281D7F08D4A6A6D9649EA3F86E519BDA84BFE8D4A7A1387E5C582AE799A0C1D2CCCB4F6CC6E7D8A4A4D8B0779DAB84AAD9AA4EB2CA1E8CA550EDE3BFC58E431199C6ABAF7FA4EFE2DCF542DE1BB5B7C488B9BF4F38FB75934F01734C187DAEE61ED707BC1549F8C3A6342B1C607BA75B1DA944EE214FFAD5A37E0702F6BB3FE7831961EFD35D6595CDF637456404A5E821301EE2737335ACF5A68DD099A476AA1742394EC
	34F2BB230648DC4FBC68867715C61343669A3433399E334331112DE595B263299DF4FB1C6EC20C4D1BDCE684481B4FE5DACE17E15F5E682481D0FF8F0D2FFFDFD0D4A9790D7FE51D0D1A0851FBFBE65BFD2751365C6BE03DF79D16DB2330AD86DD073697FA7F3AF27BBC0F9CD27731A9DBDD4B61F3ADAD2C7797FA517EB030AD115B3750DB6CBBEE9E705A14D1434A6C7AF47DAE03182A82C68DAA33692BE134A9F3BA2C9726E3CCDD1D2218AA5351665B067F3A18EEBB34C70BE90BE2D2BC679AD536DF7C6DF17F
	3350C953357315F05DEF84F6C678AADD0345CC77BF477CA345A7CD77BF47BCFB924FB153A02FB5824F35DEAAA3F328D8C84F85D8881079CF30B687287BA7DCFB59CA4A25B14E8D8FBA94D81A196CADBE9B20F8FE371D443CDF9ED6539FED574FE5199DCD65243C3AC06788C098C0B4C0A237E16676B1EDB860955A40623C4FB231F372EF4E190F7993DDC17CD98876E74D00F18CE097C07B8C74E7517662317C4173D55591E6CFE20F3FBD5E993EF5A44CAF4D78FE9740D762170E7563E3FE246FA4FB89C9103790
	A073FA7085208E208BA078FA74273AF45F1865C3EAFE2B3396F075CEE7FF2D7F5EB73775225E31E2AE76B1ECCC3E486BC5BE8ED7929F63A0EFBEC09AC0CEA42CBBG9AGDC1168636525FB4669A3EB6A019EB2BB0FDAA766DF6F18FC21113EFC2C2F4A7C111C9D0DEBDFC3DD5932B955BBFDB8270A0A443D1F5DF4FD1C826945G39B7E0BF723FF6119896C23A8E2083408B908E908B10FC8346F0C1E94DD8B1645783A00EF85EA228ACACA6EBEC93EB8F9F8470793707C5B7087866CCD05F7381FC6F7B25F3EE94
	15DD5FD903BDBA3A65435641BBBA756B604DB732FD00E6F8DAEE44793133874BF264C6AC4BFBE8D94E02F4A240029B71BC793A3E0979989D40676F63129BC5FC267E0969F38670796B4B3BC99453F7DAB609E34AC6F38977D796767A1F17113896034C7DB7E1DCF3DAF0FF347DA60C5F967B447CA98F607377A7544F9FE64311EE34A10A5B706C84ED787D18B62488ED380A5B30185BF0494044EC38A6004F5F0652A8118D51B63ED79D05B6D4BBA6E6C3D3G1F3F8DC1C29B1A4CE843D4EE43FFCE50068C67D8B6
	A489ED58E7C29BD2398DB787489A1F8DF70FE9C3414DA29BFEE9C19BF65D0CB61CF0CD4C067EG3E923F346BE6D1BB5FD2C846A4D15FA9FB15AFED325D42ECDD510DFD63241B31DDABA7517EF8EEFF7AA9E247E2BFDD2381631BF1EAE2FE478470797B1DB3D3947BEC8B7A33F9A65ADE69A632BAE61E2B6DCF39A7E67BCB81FC81E5A6347DBA2DAC386DD68F1195FF4E36E7AA9333FDEDG1FFF1D1FBBD34417B0C1FDCF0F29EF21D05F85039353F7D9G1F3F3E4CDBC46D6973B6B2079334A75C91D24BF0D2E716
	2AAB7F96AC432F06A6E66BF781FC7E75287596E15FF9FAE27A568470794746AE54775084754D9BD3DF70AD22F6924C7B27F09E630B3D9333E1DAGDFC0FD977A5CB8BCB1FD478378826A3B50671BBA51674CE8D82B513B3AB2BD1F5E8B69E60E2BB9C1F08A24C322B1B62F51BD3698C8A7GACGD88C10F7AB663FC973497991B9D7A7FBC545105740734FBBC96E38927A55FE369A816A5063D03FBFAD53F1B555E1F4D7D684F83FD00F433B077E34EC7D7507B263BBF12F17096F447D31CCFCA76E4D32B58C8FB2
	74B746B1FF590E516F4C3D3D43784E5C11F2E3BBCF160B6D34140B6D6CAB9F5DCEF77968F64E2AB036655795E2DB6E2B905B72D04568364C29B03265BDDA368F7F799D4A6F71489E3A59C0CED5553C5FAB643EDB401D42EFE1B5AD61FD370C5E15AA4D8145D9DB4675666CB476F15DCE310F1BF652B3F167205BAC7B7BD130F3A34F53966F8795F9001843E80AA83FE2F2774A81F1A8257CAC4F08FF77CE23D8B1DFFF237735D1737502CAE3DFFFDAA976B5345258572BAA4776B5225258571BAA4776B526F2F4DF
	C32D592A2F4DBDEE67C0F3195A2764852C49FD46B2C1FF467611EF25FB83C13732FD64259CFF9745076870ECFF790BDE3C37B29572165C0A7BBD993B0DE33DEA37B856393B0DE33DE1775831AE58ED9C6B2D3B470EF569EE924BEE1B4566D6EFF13298575B74EDE7870F7D72BEFDBDA3F8B6DF6ABDA03EBB73F50B596D345B49ADAB894736BEBA3691B9DD37617E4C79B20969D4C847GA45C06E3119230172B07145B0C4AF4873DDB3268B67FB25DC371524F7D4BF4B6AF539C48F37D9C4FCE66697D2DAD67F118
	5A2F47D7F05FB6DBF15C1E7BF3742D6390111D9353163D99E0EF8C7A3626C564DBEB8C73499A93689B535DE1576B7ECC42B13523DBA4EFC41593EAA8AFF7C0AFEFA71797AE1497294A09B514B75BC7DEA517F7FB403EA001C4D5CE0A21BC2FC3AFEF971757EA9649DB244A493A5DC8DE31D3AFEFB717D7E392494BD765149A4ACBF12E5249ABABC5F977884FD36AD4B96D067242DC7AB6D64E6D4B9116876F5F8472985F70C991DF10CA9FA660E3F34BCD27C8DD0E027C585B31BDBCFBCAEFDB855711BB6A59D122
	210EEF5D28A3056BD865516B506A7775237A3F48D0470F9E5411F5876A58237814993FDF7F16AFFE8762FFB56803AFC71BAE69A4B6655F612B3BD4CD575DE1D446BF8C79482BC0FC55E99F7CCE447F49977F996A1FACAC5BF6D52F5DD07F7DDE9FF915088F9E76416F627B5D1D42BA244A8FB354B3F21C3C33E1B78DBB8795D8D35CC94A630A6338579C85FC71G29GCB6EE0FB4EECBC397DB49917FA5C268198C6DADC32D9CA233CC7CFA0EF8724DDG21GB1GC9F7E2594E59CF7ADB5D48D3267659DE4267F3
	7A434B3D4EC57CE437014BD5DC5ABEF277626B86D337EC2F4371514372F6D72E62F9ECC0E7E7710BFC6E816D1A45FC6A1805E7751932A85FB58B57F31F7661FAAEF8960E93F776CA3C8D4469FE9F68553F0B783786266364BFC00713572D3960F36AAC5F3A3404446E973EB8B6163FC1678C1BFF4146728C0E17685D5C8A9D1E0D7113E43CEFDA87F9D13F4018DF5C03B14F398BE33E4B2A4539DA777B11FFC0BCDE798768A88AB8DFAD3DCB0BF15DDDC673AA57E19243763BD8EC37F2FC8845476870AC665DF46E
	C352D9F737E55745E24C8B69D9D250AF217D7E92F11734E0B904F31C279B6996F25C5CEE24DB4CF1F7F23A7ADFA16EF7B25235F2DC028D69A2B92E083739980E6BB7A31D74EB44A51AF9BD613823A6246B6038DF19104E4AF173ACC8974BF1BB39FD33B96EA36EDBDE9C62C2697E1785528A473DFE92690262781D61D3644EB0CC774BD262884D17F47C1E8C10447936DA10EF73EFE04C25F4275DC8277C067B406D8866F4D19E241B4B69A269DCFE812497F35C3FF5223DB977A06E5BAE14371163A2288FD6C8FB
	B9EED9A7574BF18FD05CDCC8AF6038C2AEAF6D9E3C83796B63B8075FFFAF483C97471232AE1F3391F2CD292A6AAB374DD2E36AED20A3931B3BE7F4E4297CB177625C3D2C0348CCA569F830BD9E5F7F70FE0C6F9CC45FB8C3CA7AE7717B9FA92D5F2717463336549A2F5DAD2F7824BB03697C71846A8C057C04F83A9EBD219FA3C8594A4B58DEB04BFF51A71F2C15495DBDADFF0D4F9DBBE26FADD97A7CDA1FFC922357D4FE665B23DB5037A50F46C876DF849BFE6BA477BE66D3057AACF8FC5A525C294D4B55F7EE
	FA8C4E736928497C7F141F1D596F437328BDB22E8FA3813779FE3A4F59D7276ACC3ECFBC57D8AA61DAD4125200A69378562B690D77EB6BBDD6FFAE22F54A8E78507B31CE7D0C5629C8C8A7F25CEB3C1E25F05CB0352F748168EB9E4071E209CC5ADE87CFAF6FA1E9727E9DCBDFAA6B7BF4C277376F53EF127D3FCD7037544166DFB378FB88DE70A184A09AA091E06183981F7DF48D1CB33BAD3BE0B6626E35E19C6BE6E3CC068EB740B38C52B1G09G29G193F85BE007DG9DGA3G61GF1GC9G8BG968364
	BC88F4G7B813AG3C8F225C4B295CAEFC9FD1FD6F2FDEFDCF103C5B67D25F49A36F6F0582DF8CC092C09AC06EC36C7DBA72CE9CF9BF0D3CD3261E4D9DAA058AC46CA27E93DB096D64B731E13458A57DF66C72762AB23A46102507F0B5BE63254FD4E99728CF92C3924B98550F44596313C54A27E08CBB635472D34F56E96C5E1134BE896B67C7E3685AB48EFB709B8E7E5F87988DC85F306E9C68712C62DD75C93EDFC0763C707C417FBBA0C6C07A528F472059A031FE1D0D6BDF3450F701E42FD27A90799E5695
	7BA1A931FEB132857BC565214072D82A6A43FEF1925090D82F62429F527AC5A67B2EEACD763FF6236CA883596BC6119D97A07B599ACD766BB24ACEB0103DFE94594981321754EA32777331A255C076BB2348DE92A03B7A00A67B95B34A4ECA904BDEB30A6C7C84FF5916AFB559CF18D0F621016C8D23485E9BA07BA7DFE9329BF93C6B8DE4EF9AC5F607CEB613D7456B06D5A0AF438FC600A6B4C113972913476A78F433EF9D1FD92F591E55093AE28CECDF2933BDD4270B644D8E50F562D4BABCF76E4279CE44E9
	7D375DF62DBADF789D8882C92A7E856AB3642DE9CB190D1FDB50467C074571C07B8814BE6C9B0F3A077DED3C540DB62AF503473B59C07E8A1DFC0B1FFCEF007CFBBDA81F25FFCD67C39E7AC1885942667BCFF6627D14E060E765E27BDAAB97398BED8A9358C4F43D2B73BB8AE888E039448558F477C95FBA90720D26AB1F57018483DD6F69F4A583CD72431A7FE9813AFE57692B6B378D1A2EAF38DFCB8CF4FD2053157B8870BC22695A7C08260B0DFD77853C77B0962877EFA7481755F12EFCEC8D1FF1022CF5CA
	9F71BFB34921783D0F781F19D4F660FA2B9E72229F417B51D10DE40E187BE8DBF6B1C09DC08740884054C7712C6C095D255298F723DB1D2E86F9C8366B0E0E19FF41A74E55BF564E6FDB016B1AD93F1AA945DED4E7781EF874236AFCC8FD46A6CEDBCA1E4C3FBDCD443FA51037B131ADFBAFC0AB0095A0A8917D5BD4253E73F040E2D32A9545E436523775798B8F64D5B80EA69B1C4C37D261BEFAD8A233A3AA51E80FFF3FAC620BD369136039GE0310E1F2D35BCFD387E4AF92CAD7B83090D69D9927ECB7BC204
	E3EDE653A1094776259C0CED7EAD57CBF54F5425FFE4905B3A4754BD63473475F0F0C745CA56AE2F6DA4E35C106CD6ECE6131DED6156185C4DCE3B4DBCAC31FEA91676CB647D654447F06FA33A0B140D5569361DF1BA14C0CEC965CD206BB9B24949F99C57164B79385B7038383D2F1034765E85B44A635A5AF252637EFDCB28CC6A55004DE19B989CE8311D11D55DD1F45D988674910F733D6AB3E4AE22EDD52B5F1C21E5135003FB83090F635E409BBD7EFB8317AD552F6D8BFB7D7786883F16FF2C57FFEF4017
	BF2457FFEFG79193DC72C017B798E7EEEFE669C361F1F8F4F52B96CEE910B7153B6BE7F18A30EB1460FFE27GE8DCF358B7E3693B1FF38246472FC85C54DB6C754E817A1E291A97571D2B65FD69067AC0B2D95E0C752B35BC68E1A89B4E8B5669E4B648BD0ACE6651C33A3C9A1339DF1777526EB53ABCA77C9DE0E2557CE505BAFB8E5BFA2DBAFBB6D412F8EB37F40F71F7E4556FBDFD6A1BFF54AFFFC93FEFFE3BDF3EBB0F1EEB509D977CD65473C7B45C8E7C6E54AA8D07679FFBBF50F095AAEE17C543FD06F7
	85562D537CEB1195EE821EB551BA7B3560DDDA169F33E31546DF57F2184FC95AF2F4F249FB076C8C653300B396D24FC26760994BDA13A8BF1267EF69966547EA79C27EC41E1FA764CF1903FB03F30E6B671339D75B53316E7A577BC5EADB1012343E10357DF97CDD0B0AA4D13F0CF37C044364B763496777716DC9B8DF7B5796AD7F3FFBB0BF12672F7ECE4BBF454702E41E7FDFEDDAFE9E5F2B4BFB82735FEB57725379FB918DBCFF7FD1AD7F87997303F93E7918165F4F79631E986FD804F1386838A6E7B73FBB
	39086B3939036433713B0D6E3996BF5916DD7FA40E572F551377465ACCFD6ACC048C5BCEB79D2D81596CF63AE0C89AE6D87A8597D5171347447A2458E6267F21133AB871F20A9815678F1DA4455A422BEC3E6A9E14190CAB3BD046FC218C0C4B5BA6CDEFA30FCB6ED32371F4F4EBBCEF70B22B9F1567C7B3794D66A2ABCFE83F576AFE6F6B787B1FEB357DDDF5303AF2FB2371BD2F58C671BD2FFB9A45F7A4E7B70EFE9FB33151785E68D30D7AEFAD2A6F9653EFF084C1D9058344834C85C8FD8A6B3969DB72EDA5
	72756340B96981CBAF39E7C63EF82C207E3AA6E3DF3FE99A7BBB492DCD24EF6F2603A2110F78E3CD243C94270BA2992E33094448CD86C8C62A6265A63AD78F83B2C1337D642F78F9DA66DC5CE30E62E7DB399CF7C4C63AAE0E0B6567EEF60E7B975AA663A0BD17638A69BEF6AA2425F9380F697539E318877842F9236F0332F239156E3F6C855AF6009100B000A800F80039G29GCBG721E06F59340FE00D6GAB0097A084A062E9AC47130748190FD1FF259EEE5142F41BEC24B855FE6442AEF2B7D47FD9AD5D
	BD0912368307DB8F56D6B7F436549EEEEAE82829BE5CD95DF0247AE88B67FF312BFC947E7A2643870FB5FD5BAA12407A13CA7E3E562C274F350F3F542A7D7E0DCD62B649F281BC9D7269827E9502BA9B54EC72016C420A1D3B0A8B4ACACB0BD879BDB66AFDC29130356E15D6DC6BAE843BB7BF03E7D5AFD8F10E5E718C1E996650EFBA05C1BA9AE0B627DD210D2F4F087BD26331A0250F24189CBB9FBF7266236C03BC2E8D9C57414753E08D476962B86E9BCE37E8BE2E61AF6917F87B0BBC3445282EC12F71B544
	3C293BCFB6AB2DA6F2B9C5FD875F67EEE37133D01F1F9D3D1DB03E8FFD6E7435820FF5DCFCDBFD4AF584F8C24745B764531EA301A7F6DCFC1BFC5669094013B2AE3E95BEF7589681CF566F4643F7014F7ABE9FF8CA4745677251D787BC6D63627B5C673E179DF8024660E3F5657889721B0FE7EC2A2466F553F3EB7E3DB97A191B96A0F4742AF7FDFDE29A867A2246E56B8A1FBA9387BCC963623B26CB4F37GF8960F0B6F30CFECF21EEB4BAEF8EEBCFC7F5B278CAB0027FE8CBE757D904BA86F07D8469E03FB69
	38A8435859BA6CD26FFD5BADE49D6F6C697148FC2CBFE5496538031EE62747C6CA0875BF4787A0CB5D9F3D176FF5F7886C47B53C68AC0174132E677C4FA99EF0E8326770F30A9183597AFD74D020A1404F951E735FA7F853CC3F0B69F44B3DEE326E44FD2075B4A6B1C09F779C6E45FCE1956F5B9C21FDFCB25025BE07F303A934AF4FCBEE4B2EDE0AF78A42A82E98F02F73323BCFB6AE3B84F96C32FBC2964F1F1E118367CF4932F87E74322C1FBFB15C6F491592299766896D1CEEA1479D65385729CC02BBE233
	A8D60E7FA3350B606BE522103FA7C56DBACDF16DAAAE3DC7439DD5F1ABFAC87D83172DCEB7FFAF075E196831596D6A3B91144666006A4863313E0758E8F319F4F67F05630E7236365767AEF7BD14C9C772F85AE878185F9D91734D1CA05FAB674C47766403689C43156C3FD7BF1962C7127D776A575AF07EB2897296A663FCB647E77CEBFE3EAD5B727CF862F4CAF8775E2B720688E4B03EEF05F76CA3D47AD906FCA6615D6F8415BE5918CF28AFCD254FFC41082FD928EF23CADFAC60D36F6535E03F205DB5FA23
	857B858D374B9CC8374F9CC877F9CFA0DDEDCFA05DA5FD01F41777855215DA8269BE3384523D5F9DC8476EA67A52DDAD8752CD178369FEA21097A21077DDE7A05D314EC03A0B85FE040A7C904479A3C11C6F688A243B3BAB104EA6304FA130EF2D205CB678149B1BFB27717D237DAF60DE553F10ED83B5DF62FBF65E97C65BFFD9F9CA7B7DF3329D200EFF85D6723DFB3EAE2AF55A1D6E9237ECC15AA7BD9A6D62EE72EE3241BAEAC7A9AC23AA78B761CC9F137EC41D4B51210161733DEFEBF89C9AD47FDBCC01B1
	E9B769E3425A5EC5F4ED47FECF517D5EE87B7B6F9F157DDDF52875CF50D68B85EDF512996F51463C406F5672BB48F19CA71B78FD5B979177A03F033C11633C5DC8E76538E779BDDFC5236372E6EBF4DCDE924775F2F985A97CFEB517D71C423F8963B32F9F81FCE84AF84618E5BEEB3AC860099D030F55697FA4D5CE1DCFE67239EA2200D7FF66A43ABB121262BFD75DB82449FE1E1F9BAEB4103DEC945952CB7E32771C56E427717B9EB9AF991FC99A49AE8810FD522B134D6DAEB6102D37FD7FCB3E32DB83E41F
	B72C49FE115B5DE5A0BBDBA75B5FEEC5A71B55279C417832C6B03E648B7A5B8F857D6DE73C3F8DFA897B5B48B3924FDF4D77C2E2F9FE6277DA7E07FC6FA50D673F7603AE1F73973EBCDAFF3DD652F8F2394C2E17D166E7340A30F6D06553FE2682CD744B63E9BF6BFC66B07140B3F7DCFC9F7B4C6DD201E7497878FC74653E52163D791571702D7751378BF89A4660E365FBC8B087E8934C81BE914CBD368B669E7B84F3059A41DCE11FC0DE0DC0DE31C0DE190FBC76FF229E9ABE848B1F68FF1316A9D2E6FDD363
	81C5DAC60F2DA1C90E1A95A9235569D224656CEC993064F09A0868D9B2BC2BDDAE7B30A2E56349EA0D532BC8EF5749BDB607DCF03869E0C9C98964B64A835DA585749C5563165E61E37CC13A40CA99AA2F4AF8657A0CF4A97320341CADB88FCA19E455F8D0DAC6971587258CD8F0FA49FF021029874FA969875A2C7FBF762F453A253501F32F6ACB1FFDA63A33AEAFDDCA0722314A2CAC161DA633AEDA9AB01BDBBAD7ACD111EA8EB735D6379EA8A4A559D8334D62B675969594B4B6359DA824252A610EB5B5B5A2
	99E4A9D64CD2242585676846EA5FA967529BB6DC10BB25FCB0DD4A7AD2DA5EE3323B2CA6F001FE9737C14A6E261648EE20D524DC7AB1616D403B1D726EE910524D9EC5FAD7431B891E653638CCE699C8D6DACC6EFEAA01183479383432475D5BCD95A8D27A71AF0F03DA1AA2599FB7C82B94ABB96CA2B7G95E903A63A199C172899FBE8F425375E0215B67DC75D657F919448ED2F02BFC7491FE370A7175CFCA820D79F8A8AADB289DCCE33DB0666D5C0F2BC7F584AA4615D2C229F75779196DFE8B63C5FEACFE5
	7DEED02AF69F011D87DCB7C24F47GBFBF954F207E7DAC3903E5F4AE50B2E0325B95133D3F5AE1A1E784B5430A2C1DEBDEBFC264FB10C64DD43FFFBCC26C6E53717C8FD0CB878848A02B9751A4GG18FAGGD0CB818294G94G88G88G670171B448A02B9751A4GG18FAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8BA5GGGG
**end of data**/
}
}