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
	private cbit.vcell.math.CoordinateIndex fieldClickedPoint = new cbit.vcell.math.CoordinateIndex();
	private cbit.vcell.math.CoordinateIndex fieldBeginLine = new cbit.vcell.math.CoordinateIndex();
	private cbit.vcell.math.CoordinateIndex fieldEndLine = new cbit.vcell.math.CoordinateIndex();
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
public cbit.vcell.math.CoordinateIndex getBeginLine() {
	return fieldBeginLine;
}
/**
 * Gets the clickedPoint property (cbit.vcell.math.CoordinateIndex) value.
 * @return The clickedPoint property value.
 * @see #setClickedPoint
 */
public cbit.vcell.math.CoordinateIndex getClickedPoint() {
	return fieldClickedPoint;
}
/**
 * Gets the endLine property (cbit.vcell.math.CoordinateIndex) value.
 * @return The endLine property value.
 * @see #setEndLine
 */
public cbit.vcell.math.CoordinateIndex getEndLine() {
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
		cbit.vcell.math.CoordinateIndex currentCI = getImageContainer().getCoordinateIndexFromDisplay(currentImage.x, currentImage.y);
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
				cbit.vcell.math.CoordinateIndex beginCI = getImageContainer().getCoordinateIndexFromDisplay(beginImage.x,beginImage.y);
				cbit.vcell.math.CoordinateIndex endCI = getImageContainer().getCoordinateIndexFromDisplay(endImage.x,endImage.y);
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
	cbit.vcell.math.CoordinateIndex ci;
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
private void setBeginLine(cbit.vcell.math.CoordinateIndex beginLine) {
	fieldBeginLine = beginLine;
}
/**
 * Sets the clickedPoint property (cbit.vcell.math.CoordinateIndex) value.
 * @param clickedPoint The new value for the property.
 * @see #getClickedPoint
 */
private void setClickedPoint(cbit.vcell.math.CoordinateIndex clickedPoint) {
	cbit.vcell.math.CoordinateIndex oldValue = fieldClickedPoint;
	fieldClickedPoint = clickedPoint;
	firePropertyChange("clickedPoint", oldValue, clickedPoint);
}
/**
 * Sets the endLine property (cbit.vcell.math.CoordinateIndex) value.
 * @param endLine The new value for the property.
 * @see #getEndLine
 */
private void setEndLine(cbit.vcell.math.CoordinateIndex endLine) {
	cbit.vcell.math.CoordinateIndex oldValue = fieldEndLine;
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
	D0CB838494G88G88G360171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D5DBC894D1986E21FA12A830140810684120A268219E02A20C201E02EE8F8685C1BDC47450C374F4E6D7534DB2AD89A5A1FB8ACA3AD810ACF6F3370B174AEA9DF74140595D138D4E4E994F1C3135887A4FE5E6ACAC7221010F7F4C7F1F7FF27E7FBBB32865FAC52D96E9C1C82BC56AF924A134228E21311ED8192FDBEC6B8DE846F383500CC6D08D488A305509772E6285100D004D20633206688DCD28
	3F582A7455CA6FC30BA034C16C4145F625FB8D6FE3C46EABA7617E18483CA05E2D5EC717D422533BE864A20FB3A3260FB34A375D1132831D552A9C1EEB1128137B24603D84408F9FC50707BBC1768CCD0CBF49E5CDF647AC9863784E38E74663FDFC19A0B6B3CC9B53A14346D60A90EBE812475BAD6255433A83508D1035BE3E5923EAC9FF5F04DA5D9D31CBFF2835F4D25638E5B22C553A1D0D36093E16E93CFFE74056CE7506FB00940081CA815740F6852DC437C0BE4F3284A994087D5CF1FA2DBCFDC3725802
	F5BC8E96257B00F304669112B3ABADF64ED24F5FA07DD64D59CF76247201BF3F359F56236A0D4BA2F53374243A72FBE4AD153D2822E1CFCED15E6F27533C5FC7E12DA9DF29FFAD748F5DD0AF673DA71877B3302DF67D79249B7DDA86EBD621267BE53356D23E3DA4D24FD48DAC2CC54E460CF25E8E027E8BE09E209C30993097F01C7973B9066E7E63FC068C4AFABD534AE30A24EF05F695A4AEB2EA7459AEB36C9C568DEA9AD98B3BC86DD923715A333F782D973C49C10ED798D8EBB01358905DA8E00629AB77EC
	521A799E0A05B5E596B0A4A8B8480F3BD53B8252B2DCB66C648D06439D523ED3633D1CB2F4088881E0AD7B32C797F74203BB81386779FD680D70F88318428D0A47878919701C94BF0962550BE994F065E45A1FCF7262BA5439EF692C73D93C3F2BAE6949390CFA61FCE43C37A21EDC9FA826674859395F0BD37FB5076CEDB24DFBBB9F4EDA8158967476C724C3F22C57F0314F2AF794880173C9B34787EC50E939570D72EF911E42B6CBE2875BF9D838EA265F223110EF1FC94005F9C26FGBF93C088B750AF0CF2
	8E990CD1B36BB17C91B3E65A6321FD114865FAD9A724361FEBE914FFFB49D760E730FFF97432602DEDE3D1F460226C87843730058BD0F402F8B6137AC3BA7F9FCD60E9456DA37AB941CD4B6377C4766EE5067B9E041E1DG54CC793D7BB452B603172AE989E2D9320420367EF493BF8B1E74B0DCC3CE5C01A05F7B74655FEC3A4AAD6D19B47F09DAB3B8AF7503197A70BCFE2C4CF9101FCD6A32BE98CE510FB11209297B285468A7D0CB8788519E25541E83GG4487GGD0CB818294G94G88G88G360171B4
	519E25541E83GG4487GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5883GGGG
**end of data**/
}
}
