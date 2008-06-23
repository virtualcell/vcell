package cbit.vcell.microscopy.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.PlotPane;
import cbit.vcell.VirtualMicroscopy.ImageLoadingProgress;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.microscopy.AnnotatedImageDataset;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.ROI;
import cbit.vcell.microscopy.ROI.RoiType;

//comments added in Jan, 2008. This panel is with the first tab that users can see when VFrap is just started.
//This panel displays the images base on time serials or Z serials. In addtion, Users can mark ROIs and manipulate
//ROIs in this panel.
public class FRAPDataPanel extends JPanel implements PropertyChangeListener{

	private static final long serialVersionUID = 1L;
	private OverlayEditorPanelJAI overlayEditorPanel = null;
	private FRAPStudy frapStudy = null;  //  @jve:decl-index=0:
	private EventHandler eventHandler = new EventHandler();
	private LocalWorkspace localWorkspace = null;
	
	private class EventHandler implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (frapStudy!=null){
				if (evt.getSource()==frapStudy.getFrapData()){
					if (evt.getPropertyName().equals(AnnotatedImageDataset.PROPERTY_NAME_CURRENTLY_DISPLAYED_ROI)){
						//Save user changes from viewer to ROI
						getOverlayEditorPanelJAI().saveUserChangesToROI();
						//Set new ROI on viewer
						getOverlayEditorPanelJAI().setROI(frapStudy.getFrapData().getCurrentlyDisplayedROI());
					}
				}
			}
		}
		
	}// end of class EventHandler
	
	//implementation of propertychange as a propertyChangeListener
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() instanceof  ImageLoadingProgress && e.getPropertyName().equals("ImageLoadingProgress"))
		{
			int prog = ((Integer)e.getNewValue()).intValue();
			VirtualFrapMainFrame.updateProgress(prog);
		}
	}
	/**
	 * This is the default constructor
	 */
	public FRAPDataPanel() {
		super();
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
							PopupGenerator.showErrorDialog("Error Time Plot ROI:\n"+e.getMessage());
						}
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CURRENTROI_PROPERTY)){
						try {
							ROI.RoiType roiType = (ROI.RoiType)evt.getNewValue();
							saveROI();
							getFrapStudy().getFrapData().setCurrentlyDisplayedROI(
									getFrapStudy().getFrapData().getRoi(roiType));
						} catch (Exception e) {
							PopupGenerator.showErrorDialog("Error Setting Current ROI:\n"+e.getMessage());
						}						
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_UNDOROI_PROPERTY)){
						try {
							ROI undoableROI = (ROI)evt.getNewValue();
							getFrapStudy().getFrapData().addReplaceRoi(undoableROI);
//							getFrapStudy().getFrapData().setCurrentlyDisplayedROI(getFrapStudy().getFrapData().getCurrentlyDisplayedROI());
						} catch (Exception e) {
							PopupGenerator.showErrorDialog("Error Setting Current ROI:\n"+e.getMessage());
						}						
					}
				}
			}
		);
		
	}

	public OverlayEditorPanelJAI getOverlayEditorPanelJAI(){
		if (overlayEditorPanel==null){
			overlayEditorPanel = new OverlayEditorPanelJAI();
		}
		return overlayEditorPanel;
	}

	private FRAPStudy getFrapStudy() {
		return frapStudy;
	}

	public void setFrapStudyNew(FRAPStudy argFrapStudy,boolean isNew) {
		FRAPData oldFrapData = (frapStudy!=null)?(frapStudy.getFrapData()):(null);
		FRAPStudy oldFrapStudy = this.frapStudy;
		if (oldFrapStudy!=null){
			oldFrapStudy.removePropertyChangeListener(eventHandler);
		}
		if (oldFrapData!=null){
			oldFrapData.removePropertyChangeListener(eventHandler);
		}
		this.frapStudy = argFrapStudy;
		if (frapStudy!=null){
			frapStudy.addPropertyChangeListener(eventHandler);
		}
		FRAPData frapData = ((frapStudy!=null)?(frapStudy.getFrapData()):(null));
		if (frapData!=null){
			frapData.addPropertyChangeListener(eventHandler);
		}
		overlayEditorPanel.setImages(
			(frapData==null?null:frapData.getImageDataset()),isNew,
			(frapData==null?null:frapData.getOriginalGlobalScaleInfo()));
		if(frapData != null){
			frapData.setCurrentlyDisplayedROI(frapData.getRoi(RoiType.ROI_CELL));
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
		RoiType roiType = RoiType.ROI_BLEACHED;
		if (getFrapStudy().getFrapData().getCurrentlyDisplayedROI()!=null){
			roiType = getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getROIType();
		}
		double[] averageFluor = FRAPDataAnalysis.getAverageROIIntensity(getFrapStudy().getFrapData(), roiType);
		FRAPDataPanel.showCurve(null, new String[] { "f" }, getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps(),new double[][] { averageFluor });
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
	public static void showCurve(WindowListener windowListener, String[] varNames, double[] independent, double[][] dependents){
		PlotPane plotter = new PlotPane();
		PlotData[] plotDatas = new PlotData[dependents.length];
		for (int i = 0; i < plotDatas.length; i++) {
			plotDatas[i] = new PlotData(independent, dependents[i]);
		}
		Plot2D plot2D = new Plot2D(null, varNames, plotDatas);
		
		plotter.setPlot2D(plot2D);
		
		JFrame frame = new JFrame();
		frame.addWindowListener(windowListener);
		frame.getContentPane().add(plotter);
		frame.setLocation(new Point(300,300));
		frame.pack();
		frame.setSize(new Dimension(400,400));
		frame.setVisible(true);
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
