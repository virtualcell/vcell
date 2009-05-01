package cbit.image.gui;

import java.awt.event.*;
import java.awt.*;
import cbit.plot.*;
import cbit.image.*;
/**
 * This type was created in VisualAge.
 */
public class ImageContainerPanelTool implements CommandListener, MouseListener, MouseMotionListener {
	protected ImageContainerPanel imageContainerPanel = null;
	protected ImageContainer imageContainer = null;
	protected CommandCache commandCache = null;
	private Point beginPick = null;
	private Point endPick = null;
	private boolean bPickMode = false;

	protected transient java.beans.PropertyChangeSupport propertyChange;
	private org.vcell.util.CoordinateIndex fieldClickedPoint = new org.vcell.util.CoordinateIndex();
	private org.vcell.util.CoordinateIndex fieldBeginLine = new org.vcell.util.CoordinateIndex();
	private org.vcell.util.CoordinateIndex fieldEndLine = new org.vcell.util.CoordinateIndex();
	private int fieldSliceNumber = 0;
	private int fieldSlicePlane = 2;
/**
 * ImageContainerTool constructor comment.
 */
public ImageContainerPanelTool(ImageContainerPanel imageContainerPanel, ImageContainer imageContainer) {
	this.imageContainerPanel = imageContainerPanel;
	imageContainerPanel.getImagePaneScroller().getImagePaneView().addMouseListener(this);
	imageContainerPanel.getImagePaneScroller().getImagePaneView().addMouseMotionListener(this);
	this.imageContainer = imageContainer;
	commandCache = new CommandCache(this);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * This method was created in VisualAge.
 * @param bBusy boolean
 */
public void busy(boolean bBusy) {
}
/**
 * This method was created in VisualAge.
 * @param command cbit.image.Command
 */
public void command(Command command) {
	if (command instanceof AxisCommand){
		setAxis(((AxisCommand)command).getAxis());
	}else if (command instanceof SliceCommand){
		setSlice(((SliceCommand)command).getSliceOffset());
	}
}
/**
 * This method was created in VisualAge.
 */
public void decrementSlice() {
	commandCache.queueCommand(new SliceCommand(-1));
}
/**
 * This method was created in VisualAge.
 */
public void decrementSlice10() {
	commandCache.queueCommand(new SliceCommand(-10));
}
/**
 * This method was created in VisualAge.
 */
protected void finalize() {
	commandCache.killThread();
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the beginLine property (cbit.vcell.math.CoordinateIndex) value.
 * @return The beginLine property value.
 * @see #setBeginLine
 */
public org.vcell.util.CoordinateIndex getBeginLine() {
	return fieldBeginLine;
}
/**
 * Gets the clickedPoint property (cbit.vcell.math.CoordinateIndex) value.
 * @return The clickedPoint property value.
 * @see #setClickedPoint
 */
public org.vcell.util.CoordinateIndex getClickedPoint() {
	return fieldClickedPoint;
}
/**
 * Gets the endLine property (cbit.vcell.math.CoordinateIndex) value.
 * @return The endLine property value.
 * @see #setEndLine
 */
public org.vcell.util.CoordinateIndex getEndLine() {
	return fieldEndLine;
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.ImageContainer
 */
public ImageContainer getImageContainer() {
	return imageContainer;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Gets the sliceNumber property (int) value.
 * @return The sliceNumber property value.
 * @see #setSliceNumber
 */
public int getSliceNumber() {
	return fieldSliceNumber;
}
/**
 * Gets the slicePlane property (int) value.
 * @return The slicePlane property value.
 * @see #setSlicePlane
 */
public int getSlicePlane() {
	return fieldSlicePlane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * This method was created in VisualAge.
 */
public void incrementSlice() {
	commandCache.queueCommand(new SliceCommand(+1));
}
/**
 * This method was created in VisualAge.
 */
public void incrementSlice10() {
	commandCache.queueCommand(new SliceCommand(+10));
}
/**
 * mouseClicked method comment.
 */
public void mouseClicked(java.awt.event.MouseEvent e) {
	try {
		Point currentPick = new Point(e.getX(), e.getY());
		Point currentImage = imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(currentPick);
		if(currentImage == null){
			return;
		}
		org.vcell.util.CoordinateIndex currentCI = getImageContainer().getCoordinateIndexFromDisplay(currentImage.x, currentImage.y);
		setClickedPoint(currentCI);
	} catch (Exception exc) {
		exc.printStackTrace();
	}
}
/**
 * mouseDragged method comment.
 */
public void mouseDragged(java.awt.event.MouseEvent event) {
	if (imageContainerPanel.getSpatialPlotEnabled()){
		if (beginPick==null){
			System.out.println("ImageContainerPanelTool.mouseMoved(), in pick mode but beginPick is null");
			return;
		}
		if (endPick==null){
			System.out.println("ImageContainerPanelTool.mouseMoved(), in pick mode but endPick is null");
			return;
		}
		java.awt.Graphics canvasGc = imageContainerPanel.getImagePaneScroller().getImagePaneView().getGraphics();
		canvasGc.setXORMode(Color.white);
		canvasGc.setColor(Color.red);
		if (imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(new Point(event.getX(),event.getY()))!=null){	
			canvasGc.drawLine(beginPick.x,beginPick.y,endPick.x,endPick.y);
			endPick.x = event.getX();
			endPick.y = event.getY();
			canvasGc.drawLine(beginPick.x,beginPick.y,endPick.x,endPick.y);
		}	
	}	
	refreshStatus(event.getPoint());
}
/**
 * mouseEntered method comment.
 */
public void mouseEntered(java.awt.event.MouseEvent e) {
}
/**
 * mouseExited method comment.
 */
public void mouseExited(java.awt.event.MouseEvent e) {
}
/**
 * mouseMoved method comment.
 */
public void mouseMoved(java.awt.event.MouseEvent event) {
	refreshStatus(event.getPoint());
}
/**
 * mousePressed method comment.
 */
public void mousePressed(java.awt.event.MouseEvent event) {
	if (imageContainerPanel.getSpatialPlotEnabled()){
		//
		// if old line, erase it first
		//
		if ((beginPick != null) && (endPick != null)){
			imageContainerPanel.repaintImage();
		}
		beginPick = null;
		endPick = null;
					
		if (imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(new Point(event.getX(),event.getY()))!= null){
			bPickMode = true;
			beginPick = new Point(event.getX(),event.getY());
			endPick = new Point(event.getX(),event.getY());
		}else{
//			getStatusLabel().setText("pick missed");
//			plotFrame.setVisible(false);
			return;
		}		
		java.awt.Graphics canvasGc = imageContainerPanel.getImagePaneScroller().getImagePaneView().getGraphics();
		canvasGc.setXORMode(Color.white);
		canvasGc.setColor(Color.red);
		canvasGc.drawLine(beginPick.x,beginPick.y,endPick.x,endPick.y);
	}	
}
/**
 * mouseReleased method comment.
 */
public void mouseReleased(java.awt.event.MouseEvent event) {
	if (imageContainerPanel.getSpatialPlotEnabled()){
		if (bPickMode){
			bPickMode = false;
			//
			// givin screen coordinates, get coordinates of current projection corrected for current zoom
			//
			Point beginImage = imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(beginPick);

			//
			//?????????change this when you rubber band the lines
			//
			//
			endPick = new Point(event.getX(),event.getY());
			Point endImage = imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(endPick);
			if (endImage==null){
				//
				// end point is outside the image;
				//
				imageContainerPanel.refreshImage();
				return;
			}
			try {
				//
				// get real image coordinates from display coordinates
				//
				org.vcell.util.CoordinateIndex beginCI = getImageContainer().getCoordinateIndexFromDisplay(beginImage.x,beginImage.y);
				org.vcell.util.CoordinateIndex endCI = getImageContainer().getCoordinateIndexFromDisplay(endImage.x,endImage.y);
				cbit.plot.PlotData lineScan = getImageContainer().getLineScan(beginImage,endImage);
				String beginCoordString = getImageContainer().getCoordinateString(beginCI.x,beginCI.y,beginCI.z);
				String endCoordString = getImageContainer().getCoordinateString(endCI.x,endCI.y,endCI.z);
				if (lineScan == null){
					System.out.println("error getting line scan from "+beginCoordString+" to "+endCoordString);
					return;
				}
			// everything OK so update bound fields
				setBeginLine(beginCI);
				setEndLine(endCI);
			//
				//plotFrame.setTitle("line scan of "+getImageContainer().getValueLabel()+" from "+beginCoordString+" to "+endCoordString);
				//plotFrame.setXLabel(getImageContainer().getDisplacementLabel());
				//String yLabel = getImageContainer().getValueLabel();
				//if (getImageContainer().getUnits().length()>0){
					//yLabel += " ("+getImageContainer().getUnits()+")";
				//}
				//plotFrame.setYLabel(yLabel);
				//plotFrame.setPlotData(lineScan);
				//plotFrame.setVisible(true);
			}catch (Exception e){
				e.printStackTrace();
			}
		}	
	}	
}
/**
 * This method was created by a SmartGuide.
 */
public void refreshStatus(java.awt.Point canvasPoint) {
	java.awt.Point imagePoint = imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(canvasPoint);
	String statusText = new String("");
	double value;
	org.vcell.util.CoordinateIndex ci;
	if (imagePoint!=null){	
		try {
			ci = getImageContainer().getCoordinateIndexFromDisplay(imagePoint.x,imagePoint.y);
			if (ci==null){
				statusText="";
			}else{
				statusText = getImageContainer().getPointString(ci.x,ci.y,ci.z);
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			imageContainerPanel.setStatusText("error communicating with the DataSetController");
			return;
		}			
	}
	imageContainerPanel.setStatusText(statusText);
	return;
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * This method was created in VisualAge.
 * @param axis int
 */
private void setAxis(int axis) {
	try {
		getImageContainer().setImagePlane(axis,getImageContainer().getSlice());
		imageContainerPanel.setSliceLabelText(Integer.toString(getImageContainer().getSlice()));
		imageContainerPanel.refreshImage();
	// also the bound fields
		setSlicePlane(axis);
		setSliceNumber(getImageContainer().getSlice());
	//
	}catch (Exception e){
		e.printStackTrace(System.out);
	}		
}
/**
 * This method was created in VisualAge.
 */
public void setAxisX() {
	commandCache.queueCommand(new AxisCommand(ImageContainer.X_AXIS));
}
/**
 * This method was created in VisualAge.
 */
public void setAxisY() {
	commandCache.queueCommand(new AxisCommand(ImageContainer.Y_AXIS));
}
/**
 * This method was created in VisualAge.
 */
public void setAxisZ() {
	commandCache.queueCommand(new AxisCommand(ImageContainer.Z_AXIS));
}
/**
 * Sets the beginLine property (cbit.vcell.math.CoordinateIndex) value.
 * @param beginLine The new value for the property.
 * @see #getBeginLine
 */
private void setBeginLine(org.vcell.util.CoordinateIndex beginLine) {
	fieldBeginLine = beginLine;
}
/**
 * Sets the clickedPoint property (cbit.vcell.math.CoordinateIndex) value.
 * @param clickedPoint The new value for the property.
 * @see #getClickedPoint
 */
private void setClickedPoint(org.vcell.util.CoordinateIndex clickedPoint) {
	org.vcell.util.CoordinateIndex oldValue = fieldClickedPoint;
	fieldClickedPoint = clickedPoint;
	firePropertyChange("clickedPoint", oldValue, clickedPoint);
}
/**
 * Sets the endLine property (cbit.vcell.math.CoordinateIndex) value.
 * @param endLine The new value for the property.
 * @see #getEndLine
 */
private void setEndLine(org.vcell.util.CoordinateIndex endLine) {
	org.vcell.util.CoordinateIndex oldValue = fieldEndLine;
	fieldEndLine = endLine;
	firePropertyChange("endLine", oldValue, endLine);
}
/**
 * This method was created in VisualAge.
 * @param imageContainer cbit.image.ImageContainer
 */
public void setImageContainer(ImageContainer imageContainer) {
	this.imageContainer = imageContainer;
}
/**
 * This method was created in VisualAge.
 * @param offset int
 */
private void setSlice(int offset) {
	try {
		int currSlice = imageContainer.getSlice();
		imageContainer.setImagePlane(imageContainer.getNormalAxis(),imageContainer.getSlice()+offset);
		if (imageContainer.getSlice() == currSlice){
			return;
		}
		imageContainerPanel.setSliceLabelText(Integer.toString(imageContainer.getSlice()));
		busy(true);
		imageContainerPanel.refreshImage();
	// also the bound field
		setSliceNumber(imageContainer.getSlice());
	//
		busy(false);
	}catch (Exception e){
		e.printStackTrace(System.out);
	}		
}
/**
 * Sets the sliceNumber property (int) value.
 * @param sliceNumber The new value for the property.
 * @see #getSliceNumber
 */
private void setSliceNumber(int sliceNumber) {
	int oldValue = fieldSliceNumber;
	fieldSliceNumber = sliceNumber;
	firePropertyChange("sliceNumber", new Integer(oldValue), new Integer(sliceNumber));
}
/**
 * Sets the slicePlane property (int) value.
 * @param slicePlane The new value for the property.
 * @see #getSlicePlane
 */
private void setSlicePlane(int slicePlane) {
	int oldValue = fieldSlicePlane;
	fieldSlicePlane = slicePlane;
	firePropertyChange("slicePlane", new Integer(oldValue), new Integer(slicePlane));
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA5FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D5CBE8D3C194C5500DF4D185BF080BA28A81BFA88A0A3890F1D5043AD0F1D1C5349BB7DAED5205381844E81B54CF54AA968BD691ACD26A9714D015B6C1ADED3158DAA238F0120C6D336F17F97312963B704E1BF96FC529E297868EF7F26F1DBBF74EBD132056BBD5674311D6044267117C3C88A33432892119C07C186BC3A7EBE2286CF397F095BDACC441A6A05664FC3F983F8876B22093FC5CB23089
	F4BFFECB7A87255FC5B802D0C7BD4FF993EF173EED1033D766B5577B792201854917727B17C581942C2611CB3CCED9CD9793F2EF05349D68CC7879A9FE560453A75F339BE2E7817C72675011CFDDE0FB06A747DF67328A6BD6B4BCCE3A47EDA5196C674B1421B32C68048EE31D282306216EF46A2DF16ACD413A9290G08DEDFBD68113DE4FEECC57B1EEA51ABFF68B5FADA7458F55A6F55548FC55A9CDE3FD34E5F8444AAGD5005D00BAC03421A6F69BE257D1217090ECEF16258CCDB374DE536CD373F4484893
	955649A4C4246FA34999B40F1018199E6F1A251F3FC16C1B1973BE41C96019BB3FF59FB7205A4DCBA3CD33F0D29B7891E9EF90DCBCEF70B9795A4079EEEB61FC0F42BAAC778A7FBD473F3A4CAF663D561B77C60835865D79E4AE3B3D8CC6D7214413E5337632FCD7C5E4AAA8FA288C7A3D085974B02E5B9D60BF882887B407EAE21D00B4A093F267333D70689F67B30CA96B3395B5CFA892FBADFA93AC19E294776B9643FA0E0CE12A602CCAACA4F3E6A86FBD7B4B3EF90EEEF2F046BB822A45CCB1F4280EB54288
	35C44EC2FA1567D062C4C7950D40811A095C3A0B69F530AA3658A7B30F9971B3C4FC8565DC967198D4048230963CA83673A6607E1500AA0F079B962FB7C0A83C20E4F250B0A6EDF3145FC46A6ACE8B7234F2A2634EA7FDE9BD6A5A3FE4567924679FB837A5A46632A7644FC7546BF46A097526F8E60E1A1D7B3B287E2FE788EEEB0B1C5BA05CB551D893FB5C68F2BBECF5880D75E10B382A7AC0C190A41FD6F2FC4018CE0B5C370C7F9611A251D91A18C44F434212B39DE2B1DFEF5F8CCF8BA30E5F84FD26C09016
	677F4C3806B0E3D4495A0C0C904694FD5C0FE367AC4B4E1A3E345D33A6987F6DB5CA20CFAFDFE3579C5D6ABAF11A7636483861E80B28C40326D3062DB36137ED7EFFB4C9262536276D8B0EB6D51B3F93415D61925F7B9EB87B82E09EF7A13B2DCC17123414212A22852F372345843F8BA958841EA1976E00F75E7162355FE2E372EC91BFD564E2D44BB4AF7CF525E67FBEEE2DD29C6CB705DA2C9F06B36556B8D90A4977A85D68A7D0CB878849D048641E83GG4487GGD0CB818294G94G88G88GA5FBB0B6
	49D048641E83GG4487GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5883GGGG
**end of data**/
}
}
