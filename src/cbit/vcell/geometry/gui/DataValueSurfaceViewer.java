package cbit.vcell.geometry.gui;
import cbit.vcell.geometry.surface.SurfaceCollection;
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
		public cbit.vcell.render.Vect3d getNormal(int surfaceIndex,int polygonIndex);
		public int getMembraneIndex(int surfaceIndex,int polygonIndex);
		public cbit.util.TimeSeriesJobResults getTimeSeriesData(int[][] indices,boolean bAllTimes,boolean bTimeStats,boolean bSpaceStats) throws cbit.vcell.server.DataAccessException;
		public cbit.vcell.geometry.Coordinate getCentroid(int surfaceIndex,int polygonIndex);
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE7FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DD8D4673920C9C9A4A9C94896931250AEC9CC2FE9C9EADA5A50AD3957F43D37A6A12DA5E4CB1E552DF9EAEEEDAF59509B5B7536A6B982C6D0D4D40C24A189EDB4410A88020A08BF7C2820E3A4B09A7C09B24C9C18E14E7CB8F30081B16C7BFD5FF93FF3E64EB90034696E736C7ABCAFE74E7B7F3D5F7F5FD1A8FEBFF1650A1CE2C1D831D2D07E6DDBA1887E91C108BFDEB287F1F59F2597881AFFFF
	8258A01C494903E7AB5066A7269794892BED7970FE915E7D896985DB6077A6E13DADFFGFE1858D9A48B42BF7C6B568B635B39D001F6D6517727569442F39DC0B76048B369A3E27F45B5A58ABECA41A32C48910418F24233FCCD29024B861EFC403582DCACE77CB2BC1983B75557A9FC976F1CA5ACFDBA103316685168C448D0E417A94FB26177AB923793DB5B8A8B9519AAF8F7821044678A8FED2C05E7C33B3B6F103D47A5D78E5AC5C92A6C93FD9ED18E8CD7768D386A6BDBEC326D2CCD9A90BB87823DB63BF8
	56A58E0901523F903D1E42FC2AC79096034ED99477080B586A065FA100583FA0FE50CD70D970BBEFC7FAC1698ECC5F410F9A947F3B2EBEAEE46C76642DB7C9DF6D8E162E0BBB5474ADF0AF4CF91B6645FDBBC94C6301168AB09FE0A9C0F1C5FAC1AD40BB348CBCD07197F8B676484DBE0F4767ED747B1BA4C760144FA1CA703B3E9EA88A2EC3347B828EC1E0793C233042401F7100496D3FEEB99613D9B51867C99D0F8A8B3EF3D74EAA0318AC18F5B547D241E26128D0E342EC87CCEDC7D5041749F05B1F8ADA5B
	21EFBD1F3B5240F6DC427339710A1EA40D6D2C8A1257FF6AA7F91D863F095C9A126F943F0B622BF62AF8D6BE3FC34B8CF9EF87DA72CEAC9BDD6B5097CB5E83C2D1755D06F1087FE1CCCEC9A573212AB25217FF11086E0B154CE6B577651794BFCF03E73E0C52F2CA5EEBF725974C29CAAFE065EB66AED2DFAD3B217EGCC830887C88148GD8329B4B583FD56C1AE499EB37856426811764908382131D3973ADF80AA1B9E0EB7386E51B972A1BAD603275C8E2D0D0F89E59C9E25093A677EDDA766DE06338689583
	B659657302F69BD4E3B190E4BCBF5839017084C4CA353ABCA298707885547BDFF7EE0227E48B4AE77C8E1BAC2A9C0C7E4CCE12A703363320919440EF96171FD611F8655724971481D455E09C0ED592FDA74500D7146A6BADBE1FFB40EFA5A991265510B85FC25B0E9678FD3186E37CECB5414D057785DC4F354A0D9ABDADAEBBF15F969846BA76D1BFB466C2B6709757C2DD8730G78EB318C7D4FAEACC3ADEB9F91AA1E1FE1D806723F8A653916151D593511F5E98F35C17C4C845AB20012BD6985F5FB309F783E
	2BCB230B78D2DB9BDE477C3546F54EE202AF5660190D74B09B398ADEAB3346243E971860D7EAFE33789F28A1F959856912816276E09E24D41631B25DE48B0AD80ACF8720G0A8EB5C7181FE9D237925FDC4546AA4587629FE95BBF20FCC42FA0EC86582258A7365E85F8DFE07D6DBE00EDG9F816CGB88C3093E0970F494B54D692781981309CE06BDE48FFG99A081E0AEC096C0713E740286G87C08CC0B24082003C3A74028AGAB40A840CC00B40025G257B538BDA76E39E5F681E3642BCDD6B1434CDA6DD
	9FE85235CEC99B26CB1BBF7BA25E8F1B64637FCB407262A51A97C67930D613971349078DCABEEC57648549076A883B5BA25EF77EDF900B7F53405A06C7F711D80B0322D7EE917D225781BF028A6D3B3BC83EA87DD92F0F4B7C00627D507FB5C307936478A7F7117C3049F24055B3A00B1D22AC3B3CFDAA7DA9EAABB85063D73BCE34B5FF97A98B3EA1687FB87F733BC85FE177F93DA2ED243808C27F996D3BC4C97400534D3E812F4C708B2B48B85FAD8EABFD670BD5EF8A246F03D1F0101539DCE1360D14B7FF40
	6501260FF465C26AFE522FA59616E8E2A4810E4C81E261B9F3BF76C9C39FE01F34F564E1E14EE5FF0E5198AB7E21BBF2726AD9BFD0D22F76C94C2774EAE35FB3E99F2A60034C5774EDE4DCE1BC12E775E813E05BC1E295C6327AFCD2B552BD6F1174529E1C8EE62A953C4FC66AD85300AC7B3C47833E81FFA758364B47863CF681E5C36F7E51082751616890BD3EC1917B9F391624298E52EA81F054E37FE35DCD68B886D0BA1EE61FA431EC85DACABD9D3B51FE9A63B7DA4FE2CFFE379EA074D91CAE40FB498115
	4E645FF3EB65E3F5749326F466E7E3CD91A91F6215819106EEA4D4A7F999EA2D5998C1BB2B14A7C6EF2FA17115B463245286E2F30F876709290A3D8C6519F50059CE26FC2FF0BEE78323CFEBE0631578864657FD10707D500BFC42C156EF16403382209D2092504C6E088F79128EB2FDF315E756C12D5DBABF72659DE2E323D2F8D6G349FE2FCCC5F2AAB4867B8446C0D9EE27AD242781E66FC59004FBF1CDED0FE1871B98FEB79EE8FA0DFA260D3CC791A0248178978ECD33E5FF13E22A340F3440C6F9BB27235
	82CF37A95F2581641B86BC7126FC9B8711AF85F8669B7031B87F8B674B861E7CC66083F082C483A482B03EB5A164CB86DCDA84DF9667EB98666520894AC0D3B8DF9D3CB37FBCA348E7E9E2F4C713D1BA2A2FA25F4CA6D66ED2953E4CA6ADDF96675B5A8CE3B2000B4D0CAF21D94BB7FB9479321A193E3C96463735C54BA7DDC33EA3AD2CDCF5A9FC71E1FCD51FA35F8240AF85A8BE4A782CC735FCCBB9DF42D126AFD9614B8A637B5E9872D59DCBAFE885F09EE3FC330EB1BE56763E3A1D7487F40D255FB6E88BD5
	860720CF2B2FBF51EE03560C2D232CDE1B2F70E79F4375123DC524ADACBF1EDEF084206BB8769DBFDD0B6BA5F16ED902F5F6C62E59FA107FB873A36E385AF714478C653236769F77901B99C0DBFC9C5B59E7EA34F33646E050E7F751692252C60D54EA7B96069B2BDD8D4FDED7A0A837C1679F42766C26DA62AF19CFAAE8A59EABBAF789932DB969FAA1E5ED625AB1ED5AD357DF4BD9E31276E5CA1ACBDA553457457CAE174D71FF520F6BE1DDC00FED457530B9FF7A33C1DED54A24B92E2FD77BBEDCAB315720BF
	31A38F895DB36EB575E71E6247920DBF4B8A7664B23B2FD53EEBD8C6B4BD26006B9317BC05CAAC4B5A20DCG30BE761FA95E65F549AE1B649A914B37B35DABBF59CA5ADC433E7FF21B067DACCB53831B0EA8BE27BD7A0050B0F6A947E8BDAEFA3DB3A736CD699B5B54B44DCFDF1833980079DA62AD945898A06F84547593686B18B752D706FF58CB4AF2103958EC738E5A48580F797548FBDA3F72EDB7997A95732B3E9C798473A76104A66F1FDB18E3F98E7D7AB76E57E260A9BD0979DFFC08BC69B88F1EECBC77
	870FF6BCE7BD4956ABE26019F8925363701171226CF485192F23675057E9E949C249265B8CFD2531BB49FCDCF4D275B56F4505B9A5AF222F8F7AB03F2BAC5046D85066EA7F5B1AD87131909964A9E3ED1626184FC87D8C083D81B16804DC46F2E473235EC4501949754E73E3DAD87A1FF4EB53DFF4AA3D2096208BC0BA05B259FE2247432A06223F6A8A6A0F823ED44EFB77151D14F7A0A8D6523F4D124B6E969D652464D75A0664CABA1728BCC5E8C7494F6DBCAD72DB64379F785BFD52F01F4FDBEEB115BAC012
	2A7872AA77E5A978D171B10C9768F8AE653CF6BC67GFC8C406C0F515773015D70345140D632C73B98687585BCFAFF9BA9999DE67E7EF49FD9030579C72F2BEFA0C0AB1D4EE30D9C384C7CFDBC007E4E85DF16F3FFEBFCC67E561E0632C8655CC114EB855C3453A8F7D72F11DC8A17FBDFC639B4406535235CFC2F11DCC3BB4ABD47655A81974D6566380C6466F0396983A817813842CFD06E11BEA33916CFD06E40A04AF581AE164B5D6DB4121B4765DE61F28BGD75C01F22EB0B9CB871313BBB07F4F8ECDB57F
	197EF40711DF519D585E442EA67D0543C72BC0F99B14D8B69E18B1047BAA899D18065A900F99608ABBB18D0B7A0C525082F4266B69906A6A6AC4DD62B06A0A855CBC2EEB39E1BC84AB6A7A43B06AAA36222E3BC6D01785F0D1D60C5DEBA3D30D9D53FB7EAA6ACD851DCB395EA72F12B956A0D425C6EF1FA4F6589CAE1BD719677E6742A3EA1B281943DEBE9B41DEEE510CBD82E451A2D0F9969A509A872F031BC751FE459998731E4198C9F6ED0C924E30984DBD037E0D0E7EF5ED4667E34D706CEFF60AF6F713AF
	5484B3FB878C86FAFC214B0D915A46EFCA14B83E40531189BE961DC5BF0FDCA3E3BD7BC0G12AF1F32F9DD7E81090D75189F5E92B61715490827F9A09074854AB1B6B32E71F68674CD63BA17DF2B46367D140F86C079BDB83976FD7176FDC2C47FDFFD79142AE27C561E79F66EF374AD857C4ABE0B79761AE1DDAC7F94FD1F7379DF17EF070A7713FCE3C336164E0E6B4DADAC776F0E217F8D601B4C7D0B9A2B886FB70F06DC7238E1E5FE6D58DCE792D3C54138C1E5BE7D1C7B14847EAC7E94E35AEFB70AE9E997
	7AFCCC786BE23A731383EACCBBE51B9C3C5E2832756275EBA377C22B3A547D15964DEF8436C7788DBA876B6A0A5C47CC2178D0D764BEE6C3916EE34683EDF9976EEB5DD4C746D05D67528BC68162G12G52G324E615CEB6D1E9AE102FD43B62F8CD3B31B549936G0A7BF7FBC84C5D6230727E23BD5A31AC7363557DA43F724F439C9A2081408AA01FC79FBE5EBB218FC7C306BE30B8BF5FBF35FD72DFDC379CCBCF7B81121EB870FFB640BC00C5G4285CCCF7C5E0A0952037B2BCAC218BF8ECF24BFE597424B
	C8C3443B32672E132B0CF86F0E10E36978FA83D9CB8881AD8EA089E09EC096C07EC5982FDE44743C3477600479C3CA3E55578C38D61F4F5D9C39764DFDFD4FBBD14C0D53D8B1211C6522D19A5FBDC45268G5AB400F800E400CC0025GA51FE19A776EBBB049B4322A2ECFA17373B7D3CC5F9B934A55FD96AE475A2A3CB132F7B429F58DE5E64B46D47778F0CC557E992E7D3CC9677251701E8810729936A3DFBDCCE2B89F5E33G72AE2597148134GF483C4DD4298C66FEB1AA8067CF8G449177DB8C8A8B0B49
	433E29550747F5F21175A1611211DC1FFFEA7686F4F26FC53CA7DFB24A3B7E2BA6EBF4F449074D03DFAD524E0353AE31F500E570AC3C0C63637FF0E3DEEE3D0CF959CA7372A23C4BGB117F1BFF96F15292531C5A797194619170D647EE30A76D685A63217FA59A8266BBB5FB60EA96B4D85DCDF793EA7F23F0C44B583F4AE61F1FD65BC2E0F8A5D983FA7F4FECDAEBD4FCC181E3A6EF0B966C33F8FFDE867BEDC89CE4D07B11DDC248F5106BEBC5B0BBEA4F29F3649D37321C6A79769C326218F2BF89C32398FAF
	CC5107DFCE68C3394D48077F65C29F0E5850075B86266643FDBA39C89FC206BEAC6EC39FE2398F0D03D3736134CEAE5207F906BE7C5809BEAC62BE7CF60ABE648ECD54D61774985573ED6524CFB2EABB4510FC4CA5CA8E66EB22935B462A9E2C5785A27A6F6CC17F6F25FEA4C6589A8F18DCFDE8EA69EE5549C526BB3947A876DBF8FA52386FBF213A726C576B3BFBF8EA3E8F6B64F4F9E6B7723D464573420E3E8BA3C417733AFDCF98191A6F5F5049C51679D13B11DC77552959736A64A26D45995AFBE7F4EA76
	366B64A26DA5590D6A532133E48CE7D01FF0C5C84943876D790A2D39BC8FFFBEC5DFDF5649C51623650631396F5A546CBD2C130B0CCD3143C86E7267D333675149C55A2BF0985513DA5EBE35B8B0469F0CCD4D07EA1D1C2E3C9B267925A95AFBF5C2FBF106E93E5F0EE9CE82BABB2B7BA15D1FDE8C6F4BB8AEF7BD41150A698535A246E6F691190FF7413B9FA086A091A015537F1152497E9159D7A7EBC599C0DB4A69CDC5640CAB61DF97612B99208DAD8E6DC772B29B97288D333332C600678B35B8BCFB98494B
	66DF3757191F091BD1E7FCA66E3EBA63B3F17757EDE0F85021BDB10E74076B46BFB377D81D791939BF6CB777B3275E584F7CFAE3BF57550F6F67DB75637B59F5405C174B870CFD918F987B62BEB03EAF4183E63E3CCB7376293756D379E0D08C524586322B2A50FEA91373EE3AB3059F43ECDA4073EE77F98A941E23322F391A5B1D5DE01E46479B0C5378C4835D93778D846CE2E4BA66B614F01ABAF9EF13C58FF10771BCCB65E5DB20CF5445A1034AB31A197C428633D831341E71EA52FACACD6BC7074C53DAF9
	48B82D35074C53FA605044E9EDB9E41E561307A6CE2B755078E9AD772DD1525A5E9B70F95A2B15B665EB3AF50E303E2C57ECFDF9042E8D147732F564959C7FA50A2F5560597AF221874F2D9C815A4CDEDC6F7907436631FE7430F12C138F1B477A8707A70E755C436631FE667044314EB8CCE25963F238824AA9CE9663C5BEED5D594DE37FB53F361C91BC9BAF759DB5BEBBF322539E70C992B9E5A5E05F36E3836933081E7CBEDC1FA92477FD0E40FB97003F8F7B22575D6365E9F41FD91E2652BCCA680B4CD3F6
	BEE7D6DFE41EFE4C73B499E8A5CE5CBB71DC514621064721AA20455772347D46037D76289353F6789C511D8C6FE9G0B1D183625670D52365C4952D46452270D59DE9AE6FB0F00FD2A4F307CEFD57454196A1B95546A5B4B7525779A6933A8FA3ACD75DD14357A76F1FD05067E490A1EE8D3FDC583DAFDF5DC5FF217113E84C54FECD3FDF343746D677AFE55E724EF2E22A753D4DFE8F035C6DFF5956AFB4EE924AFCB51A3741B692B985256319A6E5FC3067AF87BE6200F492D930D644A957E8683B9B636FC0C1E
	FFE887FAF7BF5607ABA12DEF355CC669383E4926B67EB504B622390D870635B65472DDE7185FBC5FCDEDEC99C19B33390D67C642720C1F2F3F7BEA98FEB762BBC64370B56853ACBB71E9EE046DCC65BD4BB40F5F3A96262F96716961783D083F6B73B07C9EFE3755B0EF85B733DB6CB633FFFEACCC5FBE44178AE178BA3473F53BE199D274B7185AB938165C5910EC433E81996694EE129F563538565C8E6FCE00D80019EE36664C7A134706C83F549B30F9209B69740BF6A1DEA232FFDC0F32F970DE82D0875085
	9012B0EFFFF20434371A1E271A6163C3C4AEEC770767FB0B1F24131D862ED1F0598749590B9396DB0FA835E07F98E4347D7BD6F39A6B5015B3D0E1F79F96C9ACCDF99E5C2BFF5BE5C4AF7160FC6E3BBE1C4FD5F8301F186691F89D38C573FB23C7FB9771EF8D4A1D6B3F030D2A8DD836C6A14D311E7032B41344AE82477A72E9F44C1066E1FDF9AE475FCA718BB4F85647EFF463F953AC2035FBB1661BA50CF93297E37EC3CD1C1F517C367E9D626179BB58781DEE0FBC532B46B84BEBB62E6ABCCFE2A878D8ECBF
	62F89F45F7E970AC66C76958073C4F86DA169FE33E668209F9F9G6AEFGF17E8B188FAD9CF7338F7962B86E173C0EA4F25C2FFA11AF1B630A7A11EFB947FD4075D902080B627A3AB8EED3BF724D62B8196BCB66B84109FCF9B262ECCE64AB6238DFF33EEE0EBB69C2BE0963D239ECAA47ED236BDF058369852583BCAEA272158F70B343C3644CB08C772B2387884F7F207D77ADGA1AED7437527810E712D9FC63E52C164BB4DFD2B98C43EA3A348B74A797E092ED34540FBA24765BB505FE40EF3B9D05F9C0EEB
	27E9A89ACAAFA89B626D9FCFD78547BDC1ED0C42FB8C477D196B0B9F42B31023EBF18C3F04688CE1DFD25D9D36B7C20EA955579F8F389CCD36BE8B6D195858FDAEDDDF237791C2B8F67FF72AB3163C834C89617D0F5007F867A0696B8F882DC94677BF96D92F65E406D8DDDA9ED24F16E73B9766B01BC756234D3AE118778C931CF73D360FA0F9AB2EE4EB410C1E98C6A7F3E5F2F6CF25270751093F5F4C5752730A34F49223D794F91636E736E15A16BFB3DB103E9BE57A2D1314E11626854AB3F671BDAB967F77
	9165BE452B3DA67B793457D4764E7AF13ED4BC027BD16DAE1C9FDAG17B6C2F0E93E9645E6540871D8E30500F3D1C108871EA4004D4A7B1C91F53E474A4FF1BA87AD3E8A6D4FD5ACD3E7B7921C855EE50E1B3D914BD9B447E5D11FB361BD7BAA7697B73BC95D4B9B4577D87ACE6E5F3177923736CDA7FCFF7BB63D5A9D79ED023F258DB67E2A10F07CD586E92885308248GF123981F35DE921FE4F81F47F1B13C0D4D62B1BAF75EC24A47B5E8F781E400D800A400F9GCBG72BECFAF588A508260G888144GA4
	82A4834C83D88210BF8675FD8C758ED03D79F89FD13977C76E9CF6ABF77B48BDBBF2A70F5C5F2B8339AE0090C0BCC04A983BDFC76E44117BE964CE9953FB72DC9594A06297AD6B70ECD0FE939F460BDDE882BA0D679863499F1BFC59E8D162B3D97EA4455F98C5A6D909E51702172FCDCE97491FF99358A579C572CF59CF2631DBAF28ED92164F8FA6307576A47C41EFB8C4FE9FE0BCA0ED43266B60473D0AF714A779FE81D973427D07486F001881E9CB7FB4814F9601356B2CDF7F7385FA97C88C4919E364FBD8
	7B0E432B405A45699730DDAC1D264F0F950ABDEC972B000740E68557B2CDED97196E732D2A6E57F91B5BEE22FB53B83A9DBA5D575AD45D9F7523EE3F096E4D63680E5269CEBC216A3E0F7B9DEB22FB7DB83AE76AF43FF4D2557DE56E77EC935D9B4651BDD727BB7F142ABB64C45D73CDF4EF99C777E21D6E9D9F2B3AFBDC28BB5BC4775B63684E3BC1554D74BD47FDAD3A4138BF5642D660293BC155E75168E3E57C713E70B27E71E955772F0AE82B4B409651D120713DCEE30B50A41D2D5F8E6540F3EF9D0EF792
	07345FF62BDBFD2361F7A08804947BB14AF37966FBD7B09FCF70F84CB509877AC7A0B3A29ED9BA9FBF9CC69F197EDE1EFF4BCC742F5268AF3CB1DCFF590D117ABF9DC17D6C7D3F51B1F610FE90C2F430717E4BF6BC1FD28172ACDF56FE22664BB4877A54F023F13E3C23C9F7BB709040FCF168FC6A93434BC0D507EAAB0D17813F092DF7B5362200A76AC6B57D71BADB9976F0DB2ECE5556FE1E2E19A6365E5758CA811E940D2DB40DAD651BC53AFB8F9301B2061C225CD357AD476670F3571139CE660D11FBA64F
	D27C629BA377CC167179D6B6502CDF42735196AB99A32640FB86C096C0DE940CD3G0EC461DE594011AAE1023351D61F5FA28E0A12E66B18256F687A6BCD9F2B67CF2E42F9CD72FF790610F2F30B69BDF1EB14B29ED21E653F38E785F932747D7C8CC95FCC204D81D88C309C2068A698EB5E0469FB6208F267612843A5B74A324D6E243775790587F2950E2349822732E7E0F8872361A666C77BCDE6EB7CE78C57469D8AFF881EB1G099AF9B657FA0D1FA3CD865A7B826B5373857C17FD5888476A4C931F8ABC36
	AF96E2ECCB3F7920507233161C8FCCE21B257850F033BA9F0E73166532FAF9E98369638645006C325BA436045BE48B347BA417FDD8E06D52B01D3B117B4B724DFC1FEEA3499B27AF609A71F9E53D2420485EC267F30DA43DB7635C72C79B313EAF3D59383E2F92547A1EBF9D6629535539E555744836A557CD4A154765F5F986BC1D2E91D1317D8EBD175EG7C2D5379DA7588990B28CB554AB7E7E85EF4C838B6A0CF473501E9D2645A401DAB34F37BEFCB11EB83C4DE25AF16A2578642655F72C42E8D20BC73F7
	2BD73F1EFF400BF3422469ECBDFFAEBCB3273333C5AC468F72B1D976F46398E37C68F78A22E10E964D3E99CB6FFEC6C746787663A4EE4AA976D61F075EB3D5E829BDC5AA6DD8G4A83A1AAFBCB1B5729B4E8E12818A27759A60DCE0B58ABEBF41E7BC4C3EB32595D9A5A0B7BB7E8E8BE786BE1EA957A4AF20DBF9D2EBE27461FAD7BC83C55D33A67799DD925DC6F88271F0B20AFF707533BA26801E23A2FC1D7DC70DBD18BBFD5F13B713BD32BD59C6EFFB43C2F62EA95DC1DC3456DD1F02D1BB629696B94E56E02
	C27FA13D77FE4260AEAD234F5E3DDA15EF696C40387D60EC21C6AF39F74876D0CE75C56E2110F2D6970DFBAC5FB324DBB8FDD72F913DFB82F9F9827968E8DC9B7C49856DF83268DEA9874BEEE439CF08E6F5E1D6345A96323A1FF2815B658551C66DB20E7125F364B713596567EBE93760F86D0E73AA3D0B2F35D9B87D5B97D4FA9A6F8B22B87DEB1F2974D4AE1F4A69CFDED2698F73F370CBB9FD71E5157E95FEDF2062D6242F322974BCCEF75CBA593E886370179ED54F0F79F95184EE67141D50D97F7DFBBA2E
	4DGDA762D58DFFF65B439B7F656562F0CC4C8376D8B505E9A106D811F9F3A24E10625DFF0D1ECDD65E9AE7A32314F4C3EDBD47D3B1B2733F5DC195BFB499B2B8BF98EBADE8D8C08CC47E38E54B14DD087466531BE556EEBBCAEA9634A88AED5A61B7B1ABD2ECCE5BF794D46A2CF2FD3FFBF27797D7A5AAFFEDFEB4B3AAF5286ABABE72C66673CAEDA0D4FF975D80D4FC8BA2D631F4774DB4D4F0D8ED9355FDAE4E57BF53A7FDC9E13DE508260849885080D41F23E728C793692797A31FECCFA5451C74E1911AF9E
	4BE8FF499973343EF2E6626FA4AFBBC35A769E5AA9927D08FF7D8C49AF596727C8064B255FBE8A108E1231AA780233243CC950A19334321F46776712E278FE1A9B5718D3B8CEF2A1DF7EED08FB166FED95F35C2DD41F835EC7B96E4FF47C958B6F33EE43F54C1C303B0D29001FFF5B786B20ACDFAECAE47C3398F8055BE1AE8450G508E6084988508851889108A10813084E0B9C0519D6985E5G35G2DF7E0BE66F411BD9F3376CA595C2219993039C8F6AA6D48310D64EC287633DA1AF39215E70FF6D85B1A9B
	AD17BB1BBBCEDBACCD0D9D179BAD1FB61E6B6472099BEB4611EFBD5D51F67E7447D6A38D2CBDD936915B130BF7DCEF9BFF50277E16482738941FDE69AF0327D79CAA63DFA1E8F1C149A69F48AE2F5DDBD7D1D6DDD5351D655F2752754E9D59DC576E4539EE9C781DF6876ED5FD530BE3743CD85CB3FC0172B6403B95C00AC55E8C35FF0DB5EECB4D7B02BB2E10B726A70A5FF9CB653ADF6638259C574B6FA9D65C2967F35C2967CB389367708D24C9D16ADFEB57B63332862D46890879691EFE51AEDBED2433D16E
	05DE5046B98374AE39F37CFA4264EA436A57F210A97ACF13117BACEC5E3C95E46AA6A5775330B3099610691E145C13E176E410091E145C23E1676492C0E676246462436677F3C1A6F3D2F285E1F2D9A0A3FCE5B2F24F051DABAB8619728964D8D9F943CEFE737E0C8D15945A1B745BAF7CFBF374B3B71D40686DD34EB9054526816C35CF4A570430B4BAC0A6B4A9396FB934F2B1A013B8A9391C30F33149A0B3EFD2F23F8D73F38148E4CFAA26EF3A4D7B60356E097B6037E83FA8C25FE99D76AB673E5FF113F93C
	2F37B7A8723E3E4CDD44F1ED41F6DF50C5F20835BF2BC99702FB89EE1C4B6745192F7D9B6DA510F632A4AEF21FA2B0206ADE4C5739371A68562E2357818F813E2F909739CEF0FFBF7DAE26AFA07685483C9357013ECF7B7BAE60F74461DA4CFE49F85D6693BA8E08823E58B89C9B3CCD67DA29705E38824F943CC7F199007B8D4FBB3FE41EF7C35244F9B7CDB29EBF5DAC6947CFDF160C47CFF7CA5A7193435DA511F87541B8210B735D47F167B8AE116AA438CFDD8E594971DF27FE91FC2BC88CA2BE097AB5C4F1
	DDF877C903BB27603EA7117287C9F67A827C3E03C466B83DAEC9E26F7FC4F9DCDEA80EBC9EF3290FAE3FCD6377D39CF70E573557B6EA6BDAB664C95E0C4954D157045F9DB1169B1A225C6C6B16E3EB72276877A8CBE6C42E551F25782DB3A2576AFF4E47AFD5C00B1B413F9990567FAD83FC61DD130953AD06E7654B9459DA839DCC6E5F8D65DA957E0B26F2EE433B88FE05BF6ABA6D45AB7CC957E9EF0E421FE1A0475A000C8B58AE28E70DDE38006D020ABB695773357B75FC6C4CE1B85FF03F1EEF09CF4F7732
	CF4F17E920EF21013EFFF66A791EF66A793E655273A53B74FCE586FC5B8C78FEAD6A79DE9175FCF79B7817E0605FF78C6C269858FD562167CBF768795E6E55733D572B67ABB408730630B8ABEBE2EE9CFBAF398B572AE68C8A1C3E192F39155DBD5E7ACB4A107A7B92596ED07A3FBFCA64FB77FCDE546C13FC014A0068C05E9BC6D45ED897391BECB20F5ADD855328DA7ECDB85B0724BDD146F234EBE0785250DA950FDD03127E62CD7A183C3DC99B93D677BA69BC0F7D3E28797D12770BDFBFFA7D8B3541521CED
	D0D7FFEBD0D7937B709CED575DB86ED8494F6FBAB82E40494F5BF2DCBF671B43F1B776A2DFD1BC62FE510BFC2571FAFDD23CDEDFA8DE2FEF1E013E0CF86CE362436E70ED3D8746EC77CC260F79E3589C4B82B25D934831B2BD73AAA9B3ECBCD94A4FE54886325ABDA72333A35177C40ED5FFB42A6A3E016B0EB3513DF29C5D33F43AFFFDCD557D7BDE541DEC22FB55B83A6769F42F9DB372BB43C4375677A577046BDE2E53CDF67275FE675FEB2CFB0DC6F7245F2577C61E537D165B20FFF16B6BCA35C1FB3B5720
	3DBD45576B4B6F4576361310957A37B93D1B532F6629749DFC7DA41E533F3652C8FE7E3D633557CF3EE924B37FBE3EEFC232D729879DE17362A340E33DEFB2756771B0B9A7480CCECA2EAAEC4C948BB2B327E0AF85E45226E0EF9148AC1DC0CE39D3E0B086709A0C81FEEC40771C815F71BEBD1F256F0B54477ECF54B746BE019216743230D29672DACF1FBAAA8BAB6936B53C122DE6D948357A7C3270A65BDB86AC591C86A63A178C4FC63FDF9A1605B538335A648B49425A96315765954BBACE37D5D6D6827514
	686929AC237B284100301E7771EDF40235E83026BE77978F666688F9ED421BEC4259A66411D9E31B3012CEAA5B04DC18F006484AEB1E9602D7044FE9357E7F583F969B90B66A47DE0D373F7033244BAD45B9C28EE40DD3E4F931F2080C3AE8EE40E8EEC50631C6D9E86AB8EDED349EADA7B9F92AE927A3E06B5BDED6F66A74592365B4D7D55C795327CFA19B10E4A712E4E1C559F5A6E3DD7810F345C3DB3ED4F4C74DC00E10FFCCF83357A6791DB6C8827DAE2EC5D853C3BD918340AB8BC574E3423BC0F69715BD
	E091F26CC1D9F8C7455B891ED1BB7DB63B88AC858EDB40CDB5901736DE908AFA83FDBD5400AC64DCB8F6814C52B7C27E50A22C161DE4330B1CG1405AD2A6AF632DD22908E50688AEF3C81B3CD7A0FA6977F4319E2505BD58FFF4E11BF6761CF91B979D0C60FBE1415BBC4923842760088552B0CD002FF5F42A460592C6DE35A73884BEE321B1EEFAD3EDFB96FF63FFA9EC1B95317CB7747GBF6DFE5C03CA48A1E7304C7685BABDB6C912ED123B516BA0FB84CD4332286EEBFE12CB748711C7A1AA5F2049A5FE77
	EBE47EB7D0CB87889986DD8312A4GG18FAGGD0CB818294G94G88G88GE7FBB0B69986DD8312A4GG18FAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4CA4GGGG
**end of data**/
}
}