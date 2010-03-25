package cbit.vcell.microscopy.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.BitSet;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageException;
import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.PlotPane;
import cbit.vcell.VirtualMicroscopy.ImageLoadingProgress;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.geometry.gui.ROISourceData;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;

//comments added in Jan, 2008. This panel is with the first tab that users can see when VFrap is just started.
//This panel displays the images base on time serials or Z serials. In addtion, Users can mark ROIs and manipulate
//ROIs in this panel.
public class FRAPDataPanel extends JPanel implements PropertyChangeListener{

	private static final long serialVersionUID = 1L;
	private OverlayEditorPanelJAI overlayEditorPanel = null;
	private FRAPSingleWorkspace frapWorkspace = null;  
//	private EventHandler eventHandler = new EventHandler();
	private LocalWorkspace localWorkspace = null;
	//The frap data panel can be editable or not. e.g. the frapData panel in the main frame is not editable.
	//However the frap data panel in define ROI wizard is editable
	private boolean isEditable = true;
		
	public void showROIAssistDialog(){
		FRAPData frapData = frapWorkspace.getWorkingFrapStudy().getFrapData();
		ROI currentROI = frapData.getCurrentlyDisplayedROI();
		
		if(currentROI.getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()) &&
				frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getNonzeroPixelsCount()<1){
			DialogUtils.showInfoDialog(this,"Cell ROI must be defined before using ROI Assist Tool to create Bleached ROI.");
			return;
		}

		BitSet maskBitSet = null;
		boolean bIsCurrent_CellROI = currentROI.getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		boolean bIsCurrent_BackgroundROI = currentROI.getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
		if(!bIsCurrent_CellROI){
			short[] maskArr = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getPixelsXYZ();
			maskBitSet = new BitSet();
			for (int i = 0; i < maskArr.length; i++) {
				if(maskArr[i] != 0){
					maskBitSet.set(i);
				}
			}
			if(bIsCurrent_BackgroundROI){
				maskBitSet.flip(0,maskArr.length);
			}
		}
		overlayEditorPanel.showAssistDialog(currentROI,maskBitSet, !bIsCurrent_CellROI/*, bIsCurrent_BackgroundROI, !bIsCurrent_BackgroundROI*/);
	}
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
		else if (e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITH_SAVE)){
			//Save user changes from viewer to ROI
			//To save only when the image is editable in this panel
			if(isEditable)
			{
				getOverlayEditorPanelJAI().saveUserChangesToROI();
			}
			//Set new ROI on viewer
			getOverlayEditorPanelJAI().setROI(getFrapWorkspace().getWorkingFrapStudy().getFrapData().getCurrentlyDisplayedROI());
		}
		else if (e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITHOUT_SAVE)){
			//Set new ROI on viewer
			getOverlayEditorPanelJAI().setROI(getFrapWorkspace().getWorkingFrapStudy().getFrapData().getCurrentlyDisplayedROI());
		}
		else if (e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_NEW) ||
				e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_UPDATE) ||
				e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_BATCHRUN))
		{
			if(e.getNewValue() instanceof FRAPStudy)
			{
				FRAPStudy fStudy = (FRAPStudy)e.getNewValue();
				FRAPStudy oldFrapStudy = (FRAPStudy)e.getOldValue();
				
				FRAPData fData = ((fStudy!=null)?(fStudy.getFrapData()):(null));
				FRAPData oldFrapData = (oldFrapStudy!=null)?(oldFrapStudy.getFrapData()):(null);
				
				if (oldFrapData!=null){
					oldFrapData.removePropertyChangeListener(this);
				}
				
				if (fData!=null){
					fData.addPropertyChangeListener(this);
				}
				
				if(e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_NEW)||
				   e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_BATCHRUN))
				{
					overlayEditorPanel.setImages(
						(fData==null?null:fData.getImageDataset()),true,
						(fData==null || fData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:fData.getOriginalGlobalScaleInfo().originalScaleFactor),
						(fData==null || fData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:fData.getOriginalGlobalScaleInfo().originalOffsetFactor));
				}
				else
				{
					overlayEditorPanel.setImages(
							(fData==null?null:fData.getImageDataset()),false,
							(fData==null || fData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:fData.getOriginalGlobalScaleInfo().originalScaleFactor),
							(fData==null || fData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:fData.getOriginalGlobalScaleInfo().originalOffsetFactor));
				}

				if(fData != null && fData.getROILength() > 0)
				{
					overlayEditorPanel.setRoiSouceData(fData);
					if(e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_BATCHRUN))
					{
						fData.setCurrentlyDisplayedROIForBatchRun(fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
					}
					else
					{
						fData.setCurrentlyDisplayedROI(fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
					}
				}
			}
		}else if (e.getPropertyName().equals(FRAPSingleWorkspace.FRAPDATA_VERIFY_INFO_PROPERTY)){
			FRAPData fData = (FRAPData)e.getNewValue();
			overlayEditorPanel.setImages(
					(fData==null?null:fData.getImageDataset()),true,
					(fData==null || fData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:fData.getOriginalGlobalScaleInfo().originalScaleFactor),
					(fData==null || fData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:fData.getOriginalGlobalScaleInfo().originalOffsetFactor));
			overlayEditorPanel.setRoiSouceData(fData);
			fData.setCurrentlyDisplayedROI(fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
			
		}else if(e.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_TIMEPLOTROI_PROPERTY)){
			try {
				plotROI();
			} catch (Exception e2) {
				DialogUtils.showErrorDialog(FRAPDataPanel.this, "Error Time Plot ROI:\n"+e2.getMessage());
			}
		}else if(e.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CURRENTROI_PROPERTY)){
			try {
				String roiName = (String)e.getNewValue();
//				saveROI();
				if(roiName != null)
				{
					getFrapWorkspace().getWorkingFrapStudy().getFrapData().setCurrentlyDisplayedROI(getFrapWorkspace().getWorkingFrapStudy().getFrapData().getRoi(roiName), false);
				}
			} catch (Exception e2) {
				DialogUtils.showErrorDialog(FRAPDataPanel.this, "Error Setting Current ROI:\n"+e2.getMessage());
			}						
		}else if(e.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_UNDOROI_PROPERTY)){
			try {
				ROI undoableROI = (ROI)e.getNewValue();
				getFrapWorkspace().getWorkingFrapStudy().getFrapData().addReplaceRoi(undoableROI);
//				getFrapStudy().getFrapData().setCurrentlyDisplayedROI(getFrapStudy().getFrapData().getCurrentlyDisplayedROI());
			} catch (Exception e2) {
				PopupGenerator.showErrorDialog(FRAPDataPanel.this, "Error Setting Current ROI:\n"+e2.getMessage());
			}						
		}else if(e.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_SHOWROIASSIST_PROPERTY)){
			showROIAssistDialog();
		}

	}
	//There are two FRAPDataPanel instances, one is in MainFrame and antoher is in DefineROIWizard
	//The crop function is called in DefineROIWizard, the image change will only be reflected in the
	//FRAPDataPanel in DefineROIWizard. The newFrapStudy will only be set to FrapdataPanel in Mainframe
	//when FINISH button is pressed.
	protected void crop(Rectangle cropRectangle) throws ImageException {
		FRAPStudy fStudy = getFrapWorkspace().getWorkingFrapStudy();
		if (fStudy == null || fStudy.getFrapData()==null){
			return;
		}
		getOverlayEditorPanelJAI().saveUserChangesToROI();
		FRAPData frapData = fStudy.getFrapData();
		FRAPData newFrapData = frapData.crop(cropRectangle);
		FRAPStudy newFrapStudy = new FRAPStudy();
		newFrapStudy.setFrapData(newFrapData);
		newFrapStudy.setXmlFilename(fStudy.getXmlFilename());
		newFrapStudy.setFrapDataExternalDataInfo(fStudy.getFrapDataExternalDataInfo());
		newFrapStudy.setRoiExternalDataInfo(fStudy.getRoiExternalDataInfo());
		newFrapStudy.setStoredRefData(fStudy.getStoredRefData());
		newFrapStudy.setModels(fStudy.getModels());
		getFrapWorkspace().setFrapStudy(newFrapStudy,false);
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
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),false,null);
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),false,null);
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),false,null);
			overlayEditorPanel.addPropertyChangeListener(this);
		}
		return overlayEditorPanel;
	}


	protected void plotROI()
	{
		FRAPStudy fStudy = getFrapWorkspace().getWorkingFrapStudy();
		if (fStudy == null || fStudy.getFrapData() == null){
			return;
		}
		saveROI();
		double[] averageFluor =
			FRAPDataAnalysis.getAverageROIIntensity(fStudy.getFrapData(),
					fStudy.getFrapData().getCurrentlyDisplayedROI(),null,null);
		FRAPDataPanel.showCurve(new String[] { "f" }, fStudy.getFrapData().getImageDataset().getImageTimeStamps(),new double[][] { averageFluor });
	}
	
	public void saveROI(){
		getOverlayEditorPanelJAI().saveUserChangesToROI();
	}

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
	
	public void setFRAPWorkspace(FRAPSingleWorkspace arg_frapWorkspace) {
		FRAPSingleWorkspace oldWorkspace = frapWorkspace;
		if(oldWorkspace != null)
		{
			oldWorkspace.removePropertyChangeListener(this);
		}
		frapWorkspace = arg_frapWorkspace;
		frapWorkspace.addPropertyChangeListener(this);
	}
	
	public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
	
	private static void showCurve(String[] varNames, double[] independent, double[][] dependents){
		PlotPane plotter = new PlotPane();
		PlotData[] plotDatas = new PlotData[dependents.length];
		for (int i = 0; i < plotDatas.length; i++) {
			plotDatas[i] = new PlotData(independent, dependents[i]);
		}
		Plot2D plot2D = new Plot2D(null, varNames, plotDatas);
		
		plotter.setPlot2D(plot2D);
		
		JDialog plotDialog = new JDialog();
		plotDialog.setTitle("ROI time course");
		plotDialog.getContentPane().add(plotter);
		plotDialog.setLocation(new Point(300,300));
		plotDialog.setSize(new Dimension(400,400));
		plotDialog.setModal(true);
		plotDialog.setVisible(true);
	}
	
//	public void setFrapStudy(final FRAPStudy argFrapStudy,boolean isNew) {
//		FRAPStudy oldFrapStudy = getFrapWorkspace().getFrapStudy();
//		FRAPData oldFrapData = (oldFrapStudy!=null)?(oldFrapStudy.getFrapData()):(null);
//		
//		if (oldFrapStudy!=null){
//			oldFrapStudy.removePropertyChangeListener(this);
//		}
//		if (oldFrapData!=null){
//			oldFrapData.removePropertyChangeListener(this);
//		}
//		
////		this.frapStudy = argFrapStudy;
//		if (argFrapStudy!=null){
//			argFrapStudy.addPropertyChangeListener(this);
//		}
//		FRAPData frapData = ((argFrapStudy!=null)?(argFrapStudy.getFrapData()):(null));
//		if (frapData!=null){
//			frapData.addPropertyChangeListener(this);
//		}
//		overlayEditorPanel.setImages(
//			(frapData==null?null:frapData.getImageDataset()),isNew,
//			(frapData==null || frapData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:frapData.getOriginalGlobalScaleInfo().originalScaleFactor),
//			(frapData==null || frapData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:frapData.getOriginalGlobalScaleInfo().originalOffsetFactor));
//		
//		if(frapData != null && frapData.getRois().length > 0 /*&& !frapData.getRois()[0].isAllPixelsZero()*/)
//		{
//			overlayEditorPanel.setRoiSouceData(frapData);
//			frapData.setCurrentlyDisplayedROI(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
//		}
//
////		overlayEditorPanel.setROI((frapData==null)?null:frapData.getCurrentlyDisplayedROI());
//		
//		
//		
//	}
	
	
} 
