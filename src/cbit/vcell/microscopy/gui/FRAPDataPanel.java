package cbit.vcell.microscopy.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageException;
import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.PlotPane;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageLoadingProgress;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.microscopy.AnnotatedImageDataset;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.simdata.DataSetControllerImpl;

//comments added in Jan, 2008. This panel is with the first tab that users can see when VFrap is just started.
//This panel displays the images base on time serials or Z serials. In addtion, Users can mark ROIs and manipulate
//ROIs in this panel.
public class FRAPDataPanel extends JPanel implements PropertyChangeListener{

	private static final long serialVersionUID = 1L;
	private OverlayEditorPanelJAI overlayEditorPanel = null;
	private FRAPStudy frapStudy = null;  //  @jve:decl-index=0:
//	private EventHandler eventHandler = new EventHandler();
	private LocalWorkspace localWorkspace = null;
	//The frap data panel can be editable or not. e.g. the frapData panel in the main frame is not editable.
	//However the frap data panel in define ROI wizard is editable
	private boolean isEditable = true;
		
//	private class EventHandler implements PropertyChangeListener {
//
//		public void propertyChange(PropertyChangeEvent evt) {
//			if (frapStudy!=null){
//				if (evt.getSource()==frapStudy.getFrapData()){
//					
//				}
//			}
//		}
//		
//	}// end of class EventHandler
	
	//implementation of propertychange as a propertyChangeListener
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() instanceof  ImageLoadingProgress && e.getPropertyName().equals("ImageLoadingProgress"))
		{
			int prog = ((Integer)e.getNewValue()).intValue();
			VirtualFrapMainFrame.updateProgress(prog);
		}
		else if(e.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
			try {
				crop((Rectangle) e.getNewValue());
			} catch (Exception ex) {
				PopupGenerator.showErrorDialog(this, "Error Cropping:\n"+ex.getMessage());
			}
		}
		else if (e.getPropertyName().equals(AnnotatedImageDataset.PROPERTY_NAME_CURRENTLY_DISPLAYED_ROI)){
			//Save user changes from viewer to ROI
			//To save only when the image is editable in this panel
			if(isEditable)
			{
				getOverlayEditorPanelJAI().saveUserChangesToROI();
			}
			//Set new ROI on viewer
			getOverlayEditorPanelJAI().setROI(frapStudy.getFrapData().getCurrentlyDisplayedROI());
		}
	}
	//There are two FRAPDataPanel instances, one is in MainFrame and antoher is in DefineROIWizard
	//The crop function is called in DefineROIWizard, the image change will only be reflected in the
	//FRAPDataPanel in DefineROIWizard. The newFrapStudy will only be set to FrapdataPanel in Mainframe
	//when FINISH button is pressed.
	protected void crop(Rectangle cropRectangle) throws ImageException {
		if (getFrapStudy() == null || getFrapStudy().getFrapData()==null){
			return;
		}
		getOverlayEditorPanelJAI().saveUserChangesToROI();
		FRAPData frapData = getFrapStudy().getFrapData();
		FRAPData newFrapData = frapData.crop(cropRectangle);
		FRAPStudy newFrapStudy = new FRAPStudy();
		newFrapStudy.setFrapData(newFrapData);
		newFrapStudy.setXmlFilename(getFrapStudy().getXmlFilename());
		newFrapStudy.setFrapDataExternalDataInfo(getFrapStudy().getFrapDataExternalDataInfo());
		newFrapStudy.setRoiExternalDataInfo(getFrapStudy().getRoiExternalDataInfo());
		newFrapStudy.setStoredRefData(getFrapStudy().getStoredRefData());
		setFrapStudy(newFrapStudy,false);
	}
	/**
	 * This is the default constructor
	 */
	public FRAPDataPanel() {
		this(true);
	}
	
	/**
	 * The constructor which specifies whether the image is editable or not is the panel.
	 * Added in August, 2009
	 */
	public FRAPDataPanel(boolean arg_isEditable) {
		super();
		isEditable = arg_isEditable;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.ipadx = 0;
		gridBagConstraints1.ipady = 0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weighty = 1.0D;
		gridBagConstraints1.weightx = 1.0D;
		gridBagConstraints1.gridy = 0;
		this.setSize(653, 492);
		this.setLayout(new GridBagLayout());
		this.add(getOverlayEditorPanelJAI(),gridBagConstraints1);
		getOverlayEditorPanelJAI().addPropertyChangeListener(
			new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_TIMEPLOTROI_PROPERTY)){
						try {
							plotROI();
						} catch (Exception e) {
							DialogUtils.showErrorDialog(FRAPDataPanel.this, "Error Time Plot ROI:\n"+e.getMessage());
						}
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CURRENTROI_PROPERTY)){
						try {
							String roiName = (String)evt.getNewValue();
//							saveROI();
							if(roiName != null)
							{
								getFrapStudy().getFrapData().setCurrentlyDisplayedROI(getFrapStudy().getFrapData().getRoi(roiName));
							}
						} catch (Exception e) {
							DialogUtils.showErrorDialog(FRAPDataPanel.this, "Error Setting Current ROI:\n"+e.getMessage());
						}						
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_UNDOROI_PROPERTY)){
						try {
							ROI undoableROI = (ROI)evt.getNewValue();
							getFrapStudy().getFrapData().addReplaceRoi(undoableROI);
//							getFrapStudy().getFrapData().setCurrentlyDisplayedROI(getFrapStudy().getFrapData().getCurrentlyDisplayedROI());
						} catch (Exception e) {
							PopupGenerator.showErrorDialog(FRAPDataPanel.this, "Error Setting Current ROI:\n"+e.getMessage());
						}						
					}
				}
			}
		);
		
	}
	
//	public void setCurrentROI(String roiName)
//	{
//		saveROI();
//		if(roiName != null)
//		{
//			getFrapStudy().getFrapData().setCurrentlyDisplayedROI(getFrapStudy().getFrapData().getRoi(roiName));
//		}
//	}

	public OverlayEditorPanelJAI getOverlayEditorPanelJAI(){
		if (overlayEditorPanel==null){
			overlayEditorPanel = new OverlayEditorPanelJAI();
			overlayEditorPanel.setROITimePlotVisible(true);
			overlayEditorPanel.setAllowAddROI(false);
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			overlayEditorPanel.addPropertyChangeListener(this);
		}
		return overlayEditorPanel;
	}

	public FRAPStudy getFrapStudy() {
		return frapStudy;
	}

	public void setFrapStudy(final FRAPStudy argFrapStudy,boolean isNew) {
		FRAPData oldFrapData = (frapStudy!=null)?(frapStudy.getFrapData()):(null);
		FRAPStudy oldFrapStudy = this.frapStudy;
		if (oldFrapStudy!=null){
			oldFrapStudy.removePropertyChangeListener(this);
		}
		if (oldFrapData!=null){
			oldFrapData.removePropertyChangeListener(this);
		}
		this.frapStudy = argFrapStudy;
		if (frapStudy!=null){
			frapStudy.addPropertyChangeListener(this);
		}
		FRAPData frapData = ((frapStudy!=null)?(frapStudy.getFrapData()):(null));
		if (frapData!=null){
			frapData.addPropertyChangeListener(this);
		}
		overlayEditorPanel.setImages(
			(frapData==null?null:frapData.getImageDataset()),isNew,
			(frapData==null || frapData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:frapData.getOriginalGlobalScaleInfo().originalScaleFactor),
			(frapData==null || frapData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:frapData.getOriginalGlobalScaleInfo().originalOffsetFactor));
		
		if(frapData != null && frapData.getRois().length > 0 /*&& !frapData.getRois()[0].isAllPixelsZero()*/)
		{
			frapData.setCurrentlyDisplayedROI(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
		}

//		overlayEditorPanel.setROI((frapData==null)?null:frapData.getCurrentlyDisplayedROI());
	}

//	protected void reloadROI(){
//		if(frapStudy.getFrapData() != null && frapStudy.getFrapData().getCurrentlyDisplayedROI() != null)
//		{
//			getOverlayEditorPanelJAI().setROI(frapStudy.getFrapData().getCurrentlyDisplayedROI());
//		}
//	}
	
//	protected void clearROI(){
//		getOverlayEditorPanelJAI().clearROI();
//	}

	protected void plotROI(){
		if (getFrapStudy() == null || getFrapStudy().getFrapData() == null){
			return;
		}
		saveROI();
//		RoiType roiType = RoiType.ROI_BLEACHED;
//		if (getFrapStudy().getFrapData().getCurrentlyDisplayedROI()!=null){
//			roiType = getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getROIType();
//		}
		double[] averageFluor =
			FRAPDataAnalysis.getAverageROIIntensity(getFrapStudy().getFrapData(),
				getFrapStudy().getFrapData().getCurrentlyDisplayedROI(),null,null);
		FRAPDataPanel.showCurve(new String[] { "f" }, getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps(),new double[][] { averageFluor });
	}
	
	public void saveROI(){
		getOverlayEditorPanelJAI().saveUserChangesToROI();
	}

//	public void refreshDependentROIs_later(){
//		//Generates Rings
//		saveROI();
//		frapStudy.refreshDependentROIs();
//		reloadROI();
//	}

	public void adjustComponents(int choice)
	{
		getOverlayEditorPanelJAI().adjustComponentsForVFRAP(choice);
	}
		
	public void main(String args[]){
		try {
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}

	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
	}
	private static void showCurve(String[] varNames, double[] independent, double[][] dependents){
		PlotPane plotter = new PlotPane();
		PlotData[] plotDatas = new PlotData[dependents.length];
		for (int i = 0; i < plotDatas.length; i++) {
			plotDatas[i] = new PlotData(independent, dependents[i]);
		}
		Plot2D plot2D = new Plot2D(null, varNames, plotDatas);
		
		plotter.setPlot2D(plot2D);
		plotter.selectStepView(false, false);

		
		JDialog plotDialog = new JDialog();
		plotDialog.setTitle("ROI time course");
		plotDialog.getContentPane().add(plotter);
		plotDialog.setLocation(new Point(300,300));
		plotDialog.setSize(new Dimension(400,400));
		plotDialog.setModal(true);
		plotDialog.setVisible(true);
	}
	
} 
