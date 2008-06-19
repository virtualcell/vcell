package cbit.vcell.microscopy;

import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import cbit.image.ImageException;
import cbit.util.Issue;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.microscopy.ROI.RoiType;

/**
 */
public abstract class AnnotatedImageDataset {

	private ImageDataset imageDataset = null;
	private ArrayList<ROI> rois = new ArrayList<ROI>();
	private transient ROI currentlyDisplayedROI = null;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public static final String PROPERTY_NAME_CURRENTLY_DISPLAYED_ROI = "currentlyDisplayedROI";
	/**
	 * Constructor for AnnotatedImageDataset.
	 * @param argImageDataset ImageDataset
	 * @param argROIs ROI[]
	 */
	public AnnotatedImageDataset(ImageDataset argImageDataset, ROI[] argROIs) {
		// Error checking
		if (argImageDataset.getAllImages().length == 0) {
			throw new RuntimeException("image dataset is empty");
		}
		// Now initialize
		this.imageDataset = argImageDataset;
		rois = new ArrayList<ROI>(Arrays.asList(argROIs));	
		verifyROIdimensions(imageDataset,rois);
		if (rois.size()>0){
			setCurrentlyDisplayedROI(rois.get(0));
		}
	}

	/**
	 * Constructor for AnnotatedImageDataset.
	 * @param argImageDataset ImageDataset
	 * @param argROITypes ROI.RoiType[]
	 */
	public AnnotatedImageDataset(ImageDataset argImageDataset, ROI.RoiType[] argROITypes) {
		// Error checking
		if (argImageDataset.getAllImages().length == 0) {
			throw new RuntimeException("image dataset is empty");
		}
		// Now initialize
		this.imageDataset = argImageDataset;
		
		rois = new ArrayList<ROI>();
		for (int i = 0;argROITypes!=null && i < argROITypes.length; i++) {
			UShortImage[] roiImages = new UShortImage[imageDataset.getSizeZ()];
			try {
				for (int j = 0; j < roiImages.length; j++) {
					roiImages[j] = new UShortImage(imageDataset.getImage(j,0,0));//getImage(z,c,t), it gets a 2D image at z=j 
					java.util.Arrays.fill(roiImages[j].getPixels(),(short)0);
				}
			} catch (ImageException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			//comment added in Feb, 2008. Each roi contains images, whoes size equals to size of z slices.
			cbit.vcell.microscopy.ROI roi = new cbit.vcell.microscopy.ROI(roiImages,argROITypes[i]);
			rois.add(roi);//rois contains different types of roi, 11 types. 3 primary + 8 generated.
		}
		verifyROIdimensions(imageDataset, rois);
		if (rois.size()>0){
			setCurrentlyDisplayedROI(rois.get(0));
		}
	}

	/**
	 * Method verifyROIdimensions.
	 * @param argImageDataset ImageDataset
	 * @param argROIs ArrayList<ROI>
	 */
	private void verifyROIdimensions(ImageDataset argImageDataset, ArrayList<ROI> argROIs){
		if (rois!=null){
			int imgNumX = argImageDataset.getImage(0,0,0).getNumX();
			int imgNumY = argImageDataset.getImage(0,0,0).getNumY();
			int imgNumZ = argImageDataset.getSizeZ();
			for (int i = 0; i < argROIs.size(); i++) {
				UShortImage firstROIImage = argROIs.get(i).getRoiImages()[0];
				int roiNumX = firstROIImage.getNumX();
				int roiNumY = firstROIImage.getNumY();
				int roiNumZ = argROIs.get(i).getRoiImages().length;
				if (roiNumX!=imgNumX || roiNumY!=imgNumY || roiNumZ!=imgNumZ){
					throw new RuntimeException("ROI size ("+roiNumX+","+roiNumY+","+roiNumZ+") doesn't match image size ("+imgNumX+","+imgNumY+","+imgNumZ+")");
				}
			}
		}
	}
	
	/**
	 * Method getRoi.
	 * @param roiType RoiType
	 * @return ROI
	 */
	public ROI getRoi(RoiType roiType) {
		for (int i = 0;i<rois.size(); i++) {
			if (rois.get(i).getROIType()==roiType){
				return rois.get(i);
			}
		}
		return null;
	}

	/**
	 * Method getAverageUnderROI.
	 * @param channelIndex int
	 * @param timeIndex int
	 * @param roi ROI
	 * @return double
	 */
	public double getAverageUnderROI(int channelIndex, int timeIndex, ROI roi){
		double averageROIIntensity = 0;
		double intensityVal = 0.0;
		long numPixelsInMask = 0;
		for (int z = 0; z < imageDataset.getSizeZ(); z++) {
			short[] bleachedRegionPixels = roi.getRoiImages()[z].getPixels();
			short[] imagePixels = getImageDataset().getImage(z, channelIndex, timeIndex).getPixels();
			for (int i = 0; i < imagePixels.length; i++) {
				int bleachedPixel = (bleachedRegionPixels[i])&0xffff;
				int imagePixel = (imagePixels[i])&0xffff;
				if (bleachedPixel > 0){
					intensityVal += imagePixel;
					numPixelsInMask++;
				}
			}
			if (numPixelsInMask==0){
				averageROIIntensity = 0.0;
			}else{
				averageROIIntensity = intensityVal/numPixelsInMask;
			}
		}
		return averageROIIntensity;
	}
	
	/**
	 * Method getNumRois.
	 * @return int
	 */
	public int getNumRois() {
		return rois.size();
	}

	/**
	 * Method addRoi.
	 * @param roi ROI
	 */
	public void addReplaceRoi(ROI roi) {
		ROI[] oldROIs = getRois();
		rois = new ArrayList<ROI>();
		for (int i = 0; i < oldROIs.length; i++) {
			if(!oldROIs[i].getROIType().equals(roi.getROIType())){
				rois.add(oldROIs[i]);
			}
		}
		rois.add(roi);
		ROI[] newROIs = getRois();
		propertyChangeSupport.firePropertyChange("rois", oldROIs, newROIs);

//		if (!rois.contains(roi)){
//			ROI[] oldROIs = getRois();
//			rois.add(roi);
//			ROI[] newROIs = getRois();
//			propertyChangeSupport.firePropertyChange("rois", oldROIs, newROIs);
//		}
	}

	/**
	 * Method crop.
	 * @param rect Rectangle
	 * @return AnnotatedImageDataset
	 * @throws ImageException
	 */
	public abstract AnnotatedImageDataset crop(Rectangle rect) throws ImageException;

	/**
	 * Method removeRoi.
	 * @param roi ROI
	 */
	public void removeRoi(ROI roi) {
		if (rois.contains(roi)){
			ROI[] oldROIs = getRois();
			rois.remove(roi);
			ROI[] newROIs = getRois();
			propertyChangeSupport.firePropertyChange("rois", oldROIs, newROIs);
		}
	}

	/**
	 * Method addPropertyChangeListener.
	 * @param listener PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Method removePropertyChangeListener.
	 * @param listener PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Method getImageDataset.
	 * @return ImageDataset
	 */
	public ImageDataset getImageDataset() {
		return imageDataset;
	}

	/**
	 * Method gatherIssues.
	 * @param issueList Vector<Issue>
	 */
	public void gatherIssues(Vector<Issue> issueList) {
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/24/2007 4:40:44 PM)
	 * @return java.lang.Float
	 */
	public ROI[] getRois() {
		return rois.toArray(new ROI[rois.size()]);
	}

	/**
	 * Method getCurrentlyDisplayedROI.
	 * @return ROI
	 */
	public ROI getCurrentlyDisplayedROI() {
		return currentlyDisplayedROI;
	}

	/**
	 * Method setCurrentlyDisplayedROI.
	 * @param argCurrentlyDisplayedROI ROI
	 */
	public void setCurrentlyDisplayedROI(ROI argCurrentlyDisplayedROI) {
		ROI oldDisplayedROI = this.currentlyDisplayedROI;
		this.currentlyDisplayedROI = argCurrentlyDisplayedROI;
		propertyChangeSupport.firePropertyChange(PROPERTY_NAME_CURRENTLY_DISPLAYED_ROI, oldDisplayedROI, currentlyDisplayedROI);
	}

}