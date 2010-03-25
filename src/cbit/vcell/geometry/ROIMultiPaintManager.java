package cbit.vcell.geometry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.DataBufferByte;
import java.awt.image.FilteredImageSource;
import java.awt.image.IndexColorModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoableEditSupport;

import org.vcell.util.BeanUtils;
import org.vcell.util.Extent;
import org.vcell.util.Hex;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

import cbit.image.Neighbor;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.geometry.gui.ROIAssistPanel;

public class ROIMultiPaintManager implements PropertyChangeListener{

	private static final String RESERVED_NAME_BACKGROUND = "background";
	
//	private Container parentContainer;
	private OverlayEditorPanelJAI overlayEditorPanelJAI;
	private Rectangle mergedCropRectangle = new Rectangle();
	private BufferedImage[] roiComposite;
	private IndexColorModel indexColorModel;
	private Extent newExtent;
	private Origin newOrigin;
	
	private ImageDataset imageDataSet;
		
	private AsynchProgressPopup asynchProgressPopup;
	
	public ROIMultiPaintManager(){
		
	}
	
	public static int[] calculateEdgeIndexes(int xSize,int ySize,int zSize){
		if((xSize!=1 && xSize<3) || (ySize!=1 && ySize<3) ||(zSize!=1 && zSize<3)){
			throw new IllegalArgumentException("Sizes CANNOT be negative or 0 or 2");
		}
		int XYSIZE = xSize*ySize;
		int numEdgeIndexes = xSize*ySize*zSize - ((xSize==1?1:xSize-2)*(ySize==1?1:ySize-2)*(zSize == 1?1:zSize-2));
		if(numEdgeIndexes == 0){
			return new int[0];
		}
		int[] edgeIndexes = new int[numEdgeIndexes];
		int index = 0;
		for (int z = 0; z < zSize; z++) {
			boolean bZEdge = (z==0) || (z==(zSize-1));
			bZEdge = bZEdge && zSize!=1;
			for (int y = 0; y < ySize; y++) {
				boolean bYEdge = (y==0) || (y==ySize-1);
				bYEdge = bYEdge && ySize!=1;
				int xIncr = (bYEdge||bZEdge?1:xSize-1);
				for (int x = 0; x < xSize; x+= xIncr) {
					int edgeIndex = x+(y*xSize)+(z*XYSIZE);
					edgeIndexes[index] = edgeIndex;
					index++;
				}
			}
		}
		if(index != numEdgeIndexes){
			throw new RuntimeException("final count not match calulated");
		}
		return edgeIndexes;
	}
	public static VCImage createVCImageFromBufferedImages(Extent extent,BufferedImage[] bufferedImages) throws Exception{
		//collect z-sections into 1 array for VCImage
		ISize isize = new ISize(bufferedImages[0].getWidth(), bufferedImages[0].getHeight(), bufferedImages.length);
		int sizeXY = isize.getX()*isize.getY();
		byte[] segmentedData = new byte[isize.getXYZ()];
		int index = 0;
		for (int i = 0; i < bufferedImages.length; i++) {
			System.arraycopy(
					((DataBufferByte)bufferedImages[i].getRaster().getDataBuffer()).getData(),0,
					segmentedData, index,
					sizeXY);
			index+= sizeXY;
		}
		
		return new VCImageUncompressed(null,segmentedData, extent,isize.getX(),isize.getY(),isize.getZ());

	}
	public Hashtable<VCImage, Rectangle> showROIEditor(Container guiParent,
			final Origin uncroppedOrigin,final Extent uncroppedExtent,final ISize uncroppedISize,
			final short[] dataToSegment,VCImage previouslyEditedVCImage,Rectangle previouslyCropRectangle) throws Exception{

//		parentContainer = guiParent;
		if((previouslyCropRectangle == null && previouslyEditedVCImage != null) ||
				(previouslyCropRectangle != null && previouslyEditedVCImage == null)){
			throw new IllegalArgumentException("Previous VCImage and Crop must both be null or both be not null.");
		}
		overlayEditorPanelJAI = new OverlayEditorPanelJAI();
		overlayEditorPanelJAI.setModeRemoveROIWhenPainting(true);
		overlayEditorPanelJAI.setUndoableEditSupport(new UndoableEditSupport());
		overlayEditorPanelJAI.setROITimePlotVisible(false);
		initImageDataSet(dataToSegment, uncroppedOrigin, previouslyCropRectangle, uncroppedExtent, uncroppedISize);
		overlayEditorPanelJAI.setImages(imageDataSet, true, OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR, OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR);
		overlayEditorPanelJAI.setROITimePlotVisible(false);
		initROIComposite();
		
		
		
		
		overlayEditorPanelJAI.addPropertyChangeListener(this);
		
		//Crop with mergedCropRectangle
		if(previouslyCropRectangle != null){
			crop(previouslyCropRectangle);
		}else{
			mergedCropRectangle.setBounds(0, 0, uncroppedISize.getX(), uncroppedISize.getY());
		}
		
		//Sanity check crop
		if(mergedCropRectangle.width != roiComposite[0].getWidth() ||
				mergedCropRectangle.height != roiComposite[0].getHeight()){
			throw new Exception("initial cropping failed");
		}

		//initialize the new roicomposite with previouslyEditedVCImage
		int index = 0;
		if(previouslyEditedVCImage != null){
			//sanity check sizes
			int totalSize = roiComposite[0].getWidth()*roiComposite[0].getHeight()*roiComposite.length;
			if(previouslyEditedVCImage.getPixels().length != totalSize){
				throw new Exception("Initial ROI composite size does not match previouslyEditedVCImage");
			}
			//copy previous to current roicomposite
			for (int i = 0; i < roiComposite.length; i++) {
				byte[] pixdata = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
				System.arraycopy(previouslyEditedVCImage.getPixels(), index, pixdata, 0, pixdata.length);
				index+= pixdata.length;
			}
			VCPixelClass[] previousPixelClasses = previouslyEditedVCImage.getPixelClasses();
			String firstROI = null;
			for (int i = 0; i < previousPixelClasses.length; i++) {
				if(previousPixelClasses[i].getPixel() == 0){//don't add background
//					if(!previousPixelClasses[i].getPixelClassName().equals(RESERVED_NAME_BACKGROUND)){
//						DialogUtils.showWarningDialog(overlayEditorPanelJAI,
//							"ROI name "+previousPixelClasses[i].getPixelClassName()+" assumed to be background and will not be initialized.");
//					}
					continue;
				}
				String nextName = previousPixelClasses[i].getPixelClassName();
				if(nextName.equals(RESERVED_NAME_BACKGROUND)){
					//Change reserved background name that didn't have a value of 0
					nextName = "ROI"+(new Random().nextInt());
				}
				if(firstROI == null){
					firstROI = nextName;
				}
				overlayEditorPanelJAI.addROIName(
						previousPixelClasses[i].getPixelClassName(), true, firstROI,
						true, OverlayEditorPanelJAI.CONTRAST_COLORS[0x000000FF&previousPixelClasses[i].getPixel()]);
			}
		}
		
		do{
			int retCode = DialogUtils.showComponentOKCancelDialog(guiParent, overlayEditorPanelJAI, "segment image for geometry");
			if (retCode == JOptionPane.OK_OPTION){
				VCImage initImage = createVCImageFromBufferedImages(imageDataSet.getExtent(), roiComposite);
				
				//Check for unassigned "background" pixels
				boolean bHasUnassignedBackground = false;
				for (int i = 0; i < roiComposite.length; i++) {
					byte[] pixData = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
					for (int j = 0; j < pixData.length; j++) {
						if(pixData[j] == 0){
							bHasUnassignedBackground = true;
							break;
						}
					}
					if(bHasUnassignedBackground){
						break;
					}
				}
				//Create PixelClasses
				Hashtable<String, Color> roiNamesAndColorsHash = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
				VCPixelClass[] vcPixelClassesFromROINames = null;
				boolean bForceAssignBackground = false;
				if(bHasUnassignedBackground){
					final String assignToBackground = "Assign as default 'background'";
//					final String assignToNeighbors = "Merge gaps with neighboring ROI";
					final String cancelAssign = "Cancel, back to segmentation...";
					String result = DialogUtils.showWarningDialog(
							JOptionPane.getRootFrame(),
							"Warning: some areas of image segmentation have not been assigned to an ROI."+
							"  This can happen when small unintended gaps are left between adjacent ROIs"+
							" or areas around the edges were intentionally left as background.  Choose an action:\n"+
							"1.  Leave as is, unassigned areas should be treated as 'background'.\n"+
//							"2.  Merge unassigned gaps with a neighboring ROI.\n"+
							"2.  Go back to segmentation tool. (note: look for areas with no color)",
							new String[] {assignToBackground,/*assignToNeighbors,*/cancelAssign}, assignToBackground);
					if(result.equals(assignToBackground)){
						bForceAssignBackground = true;
					}
//					else if(result.equals(assignToNeighbors)){
//						RegionImage regionImage =
//							new RegionImage(initImage, 0 /*0 means generate no surfacecollection*/,
//									initImage.getExtent(),imageDatasetHolder[0].getAllImages()[0].getOrigin(), RegionImage.NO_SMOOTHING);
//						//Remove regions that are not background or that are adjacent to the edge of the dataset
//						RegionImage.RegionInfo[] allRegionInfos = regionImage.getRegionInfos();
//						int[] edgePixelIndexes =
//							ClientRequestManager.calculateEdgeIndexes(initImage.getNumX(),initImage.getNumY(),initImage.getNumZ());
//						for (int i = 0; i < allRegionInfos.length; i++) {
//							//exclude non-background
//							if(allRegionInfos[i].getPixelValue() == 0){
//								//exclude edge adjacent
//								for (int j = 0; j < edgePixelIndexes.length; j++) {
//									if(allRegionInfos[i] != null && allRegionInfos[i].isIndexInRegion(edgePixelIndexes[j])){
//										allRegionInfos[i] = null;
//										bForceAssignBackground = true;
//									}
//								}
//							}else{
//								allRegionInfos[i] = null;
//							}
//						}
//						//Fill with first non-zero neighbor we find
//						for (int i = 0; i < allRegionInfos.length; i++) {
//							if(allRegionInfos[i] != null){
//								Byte lastNonZeroValue = null;
//								for (int j = 0; j < initImage.getPixels().length; j++) {
//									if(allRegionInfos[i].isIndexInRegion(j)){
//										initImage.getPixels()[j] = lastNonZeroValue;
////										int z= j/(XYSIZE);
////										int y = (j-(z*XYSIZE))/initImage.getNumX();
////										int x = j-(z*XYSIZE)-y*initImage.getNumX();
//									}else if(lastNonZeroValue == null){
//										lastNonZeroValue = initImage.getPixels()[j];
//									}
//								}
//							}
//						}
//					}
					else{
						continue;
					}
					if(bForceAssignBackground){
						vcPixelClassesFromROINames = new VCPixelClass[roiNamesAndColorsHash.size()+1];
						vcPixelClassesFromROINames[0] = new VCPixelClass(null, RESERVED_NAME_BACKGROUND, 0);					
					}
				}else{
					vcPixelClassesFromROINames = new VCPixelClass[roiNamesAndColorsHash.size()];
				}
				

				//find pixel indexes corresponding to colors for ROIs
				index = (bForceAssignBackground?1:0);
				Enumeration<String> roiNameEnum = roiNamesAndColorsHash.keys();
				while(roiNameEnum.hasMoreElements()){
					String roiNameString = roiNameEnum.nextElement();
					Color roiColor = roiNamesAndColorsHash.get(roiNameString);
					int colorIndex = -1;
					for (int i = 0; i < indexColorModel.getMapSize(); i++) {
						if(indexColorModel.getRGB(i) == roiColor.getRGB()){
							colorIndex = i;
							break;
						}
					}
					if(colorIndex == -1){
						throw new Exception("Couldn't find colormap index for ROI "+roiNameString+" with color "+Hex.toString(roiColor.getRGB()));
					}
					vcPixelClassesFromROINames[index] =
						new VCPixelClass(null, roiNameString, colorIndex);
					index++;
				}
		
				//Sanity check VCImage vcPixelClassesFromROINames and new vcPixelClassesFromVCImage found same pixel values
				VCPixelClass[] vcPixelClassesFromVCImage = initImage.getPixelClasses();
				for (int i = 0; i < vcPixelClassesFromVCImage.length; i++) {
					boolean bFound = false;
					for (int j = 0; j < vcPixelClassesFromROINames.length; j++) {
						if(vcPixelClassesFromROINames[j].getPixel() == vcPixelClassesFromVCImage[i].getPixel()){
							bFound = true;
							break;
						}
					}
					if(!bFound){
						throw new Exception("Error processing ROI Image.  Pixels found having no matching ROI.");
					}
				}
				Vector<String> missingROINames = new Vector<String>();
				StringBuffer missingROISB = new StringBuffer();
				for (int i = 0; i < vcPixelClassesFromROINames.length; i++) {
					boolean bFound = false;
					for (int j = 0; j < vcPixelClassesFromVCImage.length; j++) {
						if(vcPixelClassesFromROINames[i].getPixel() == vcPixelClassesFromVCImage[j].getPixel()){
							bFound = true;
							break;
						}
					}
					if(!bFound){
						missingROISB.append((missingROINames.size()>0?",":"")+"'"+vcPixelClassesFromROINames[i].getPixelClassName()+"'");
						missingROINames.add(vcPixelClassesFromROINames[i].getPixelClassName());
					}
				}
				if(missingROINames.size() > 0){
					final String removeROI = "Remove ROI"+(missingROINames.size()>1?"s":"")+" and continue";
					final String backtoSegment = "Return to segmentation";
					String result = DialogUtils.showWarningDialog(
							JOptionPane.getRootFrame(), 
							"ROI"+(missingROINames.size()>1?"s":"")+" named "+missingROISB.toString()+" have no pixels defined",
							new String[] {removeROI,backtoSegment}, removeROI);
					if(result.equals(removeROI)){
						Vector<VCPixelClass> vcPixelClassV = new Vector<VCPixelClass>();
						vcPixelClassV.addAll(Arrays.asList(vcPixelClassesFromROINames));
						Hashtable<String, Color> allROINameAndColors = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
						roiNameEnum = allROINameAndColors.keys();
						while(roiNameEnum.hasMoreElements()){
							String roiName = roiNameEnum.nextElement();
							for (int i = 0; i < missingROINames.size(); i++) {
								if(missingROINames.elementAt(i).equals(roiName)){
									Color deleteThisColor = allROINameAndColors.get(roiName);
									clearROI(false, deleteThisColor);
									for (int j = 0; j < vcPixelClassV.size(); j++) {
										if(vcPixelClassV.elementAt(j).getPixelClassName().equals(roiName)){
											vcPixelClassV.remove(j);
											break;
										}
									}
									break;
								}
							}
						}
						vcPixelClassesFromROINames = vcPixelClassV.toArray(new VCPixelClass[0]);
					}else{
						//return to editing segmentation
						continue;
					}
				}
				
				initImage.setPixelClasses(vcPixelClassesFromROINames);
				
				Hashtable<VCImage, Rectangle> results = new Hashtable<VCImage, Rectangle>();
				results.put(initImage, mergedCropRectangle);
				//Crop rectangle included in case we need to return to this method and re-initialize the crop
				return results;
			} else{
				throw UserCancelException.CANCEL_GENERIC;
			}
		}while(true);


	}
	
	private void initImageDataSet(short[] dataToSegment,
			Origin uncroppedOrigin,Rectangle previousCropRectangle,
			Extent uncroppedExtent,ISize uncroppedISize) throws Exception{
		
		UShortImage[] zImageSet = new UShortImage[uncroppedISize.getZ()];
		newExtent = new Extent(uncroppedExtent.getX(),uncroppedExtent.getY(),uncroppedExtent.getZ()/uncroppedISize.getZ());
		if(previousCropRectangle != null){
			newExtent = new Extent(previousCropRectangle.width*(uncroppedExtent.getX()/uncroppedISize.getX()),
					previousCropRectangle.height*(uncroppedExtent.getY()/uncroppedISize.getY()),
					uncroppedExtent.getZ()/uncroppedISize.getZ());
		}
		for (int i = 0; i < zImageSet.length; i++) {
			newOrigin = new Origin(uncroppedOrigin.getX(),uncroppedOrigin.getY(),uncroppedOrigin.getZ()+i*newExtent.getZ());
			short[] shortData = new short[uncroppedISize.getX()*uncroppedISize.getY()];
			System.arraycopy(dataToSegment, shortData.length*i, shortData, 0, shortData.length);
			zImageSet[i] = new UShortImage(shortData,newOrigin,newExtent,uncroppedISize.getX(),uncroppedISize.getY(),1);
		}
		    
		imageDataSet = new ImageDataset(zImageSet, new double[] { 0.0 }, uncroppedISize.getZ());

	}
	private void initROIComposite(){
		//------------------------Create ROI composite image array
		int[] cmap = new int[256];
		for(int i=0;i<256;i+= 1){
			cmap[i] = OverlayEditorPanelJAI.CONTRAST_COLORS[i].getRGB();
			if(i==0){
				cmap[i] = new Color(0, 0, 0, 0).getRGB();
			}
		}
		indexColorModel =
			new java.awt.image.IndexColorModel(
				8, cmap.length,cmap,0,
				false /*false means NOT USE alpha*/   ,
				-1/*NO transparent single pixel*/,
				java.awt.image.DataBuffer.TYPE_BYTE);
		roiComposite = new BufferedImage[imageDataSet.getISize().getZ()];
		for (int i = 0; i < roiComposite.length; i++) {
			roiComposite[i] = 
				new BufferedImage(imageDataSet.getISize().getX(), imageDataSet.getISize().getY(),
						BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
		}
		overlayEditorPanelJAI.setAllROICompositeImage(roiComposite);
	//------------------------

	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
			crop((Rectangle)evt.getNewValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CURRENTROI_PROPERTY)){
			
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_DELETEROI_PROPERTY)){
			deleteROI((OverlayEditorPanelJAI.ComboboxROIName)evt.getOldValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_ADDNEWROI_PROPERTY)){
			addNewROI((OverlayEditorPanelJAI.ComboboxROIName[])evt.getOldValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_SHOWROIASSIST_PROPERTY)){
			showROIAssistant((OverlayEditorPanelJAI.ComboboxROIName)evt.getNewValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CLEARROI_PROPERTY)){
			clearROI(true,((OverlayEditorPanelJAI.ComboboxROIName)evt.getOldValue()).getHighlightColor());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_BLEND_PROPERTY)){
			overlayEditorPanelJAI.setBlendPercent((Integer)evt.getNewValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CHECKROI_PROPERTY)){
			checkROI();
		}
	}

	private void crop(Rectangle cropRectangle){
		try{
			//crop underlying image
			imageDataSet = imageDataSet.crop(cropRectangle);
			//Crop Composite ROI zsections
			for (int i = 0; i < roiComposite.length; i++) {
				Image croppedROI = 
					Toolkit.getDefaultToolkit().createImage(
						new FilteredImageSource(roiComposite[i].getSource(),
							new CropImageFilter(cropRectangle.x, cropRectangle.y, cropRectangle.width, cropRectangle.height))
					);
				roiComposite[i] =
					new BufferedImage(cropRectangle.width, cropRectangle.height,
							BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
				roiComposite[i].getGraphics().drawImage(croppedROI, 0, 0, null);
			}
			//Crop highlight ROI if it exists
			ROI croppedHighlightROI = overlayEditorPanelJAI.getROI();
			if(croppedHighlightROI != null){
				overlayEditorPanelJAI.setROI(null);
				croppedHighlightROI = croppedHighlightROI.crop(cropRectangle);
			}
			//Update display with cropped images
			overlayEditorPanelJAI.setAllROICompositeImage(roiComposite);
			overlayEditorPanelJAI.setImages(imageDataSet, true,
					OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR, OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR);
			overlayEditorPanelJAI.setROI(croppedHighlightROI);
			
			mergedCropRectangle.setBounds(
					mergedCropRectangle.x+cropRectangle.x,
					mergedCropRectangle.y+cropRectangle.y,
					cropRectangle.width,cropRectangle.height);
		}catch(Exception e){
			DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Crop failed:\n"+e.getMessage());
		}

	}
	private void deleteROI(OverlayEditorPanelJAI.ComboboxROIName currentComboboxROIName){
		final String deleteCurrentROI = "Delete only current ROI";
		final String deleteAllROI = "Delete all ROIs";
		final String cancel = "Cancel";
		String result =
			DialogUtils.showWarningDialog(overlayEditorPanelJAI, "Choose delete option.",
					new String[] {deleteCurrentROI,deleteAllROI,cancel}, deleteCurrentROI);
		
		if(result.equals(deleteCurrentROI)){
			clearROI(false, currentComboboxROIName.getHighlightColor());
			overlayEditorPanelJAI.deleteROIName(currentComboboxROIName);

		}else if(result.equals(deleteAllROI)){
			for (int i = 0; i < roiComposite.length; i++) {
				Arrays.fill(((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData(), (byte)0);
			}
			overlayEditorPanelJAI.deleteROIName(null);
			overlayEditorPanelJAI.setROI(null);//force update
		}

	}
	private void addNewROI(OverlayEditorPanelJAI.ComboboxROIName[] comboboxROINameArr){
		try{
			String newROIName = null;
			boolean bNameOK;
			do{
				bNameOK = true;
				if(newROIName == null){
					newROIName = PopupGenerator.showInputDialog0(overlayEditorPanelJAI, "New ROI Name", "");
				}
				if(newROIName == null || newROIName.length() == 0){
					bNameOK = false;
					PopupGenerator.showErrorDialog(overlayEditorPanelJAI, "No ROI Name entered, try again.");
				}else{
					if(newROIName.equals(RESERVED_NAME_BACKGROUND)){
						DialogUtils.showWarningDialog(overlayEditorPanelJAI,
								"Cannot use the name '"+RESERVED_NAME_BACKGROUND+"'.  That name is reserved by the system to refer to unassigned pixels");
						newROIName = null;
						continue;
					}
					for (int i = 0; i < comboboxROINameArr.length; i++) {
						if(comboboxROINameArr[i].getROIName().equals(newROIName)){
							bNameOK = false;
							break;
					}
				}
				if(bNameOK){
//						JColorChooser jColorChooser = new JColorChooser();
//						DialogUtils.showComponentOKCancelDialog(JOptionPane.getRootFrame(), jColorChooser, "Select ROI Color");
					Color newROIColor = Color.black;
					for (int i = 1; i < OverlayEditorPanelJAI.CONTRAST_COLORS.length; i++) {
						boolean bColorUsed = false;
						for (int j = 0; j < comboboxROINameArr.length; j++) {
							Color nextColor = comboboxROINameArr[j].getHighlightColor();
							if(nextColor.equals(OverlayEditorPanelJAI.CONTRAST_COLORS[i])){
								bColorUsed = true;
								break;
							}
						}
						if(!bColorUsed){
							newROIColor = OverlayEditorPanelJAI.CONTRAST_COLORS[i];
							break;
						}
					}

					overlayEditorPanelJAI.addROIName(newROIName, true, newROIName,true,/*true,true,*/newROIColor);
				}else{
					PopupGenerator.showErrorDialog(overlayEditorPanelJAI, "ROI Name "+newROIName+" already used, try again.");
					newROIName = null;
				}
					
				}
			}while(!bNameOK);
		}catch(UtilCancelException cancelExc){
			//do Nothing
		}
	}
	private void showROIAssistant(OverlayEditorPanelJAI.ComboboxROIName currentComboboxROIName){
		if(imageDataSet == null){
			DialogUtils.showErrorDialog(JOptionPane.getRootFrame(), "No ImageData available for ROIAssistant.");
			return;
		}
		try {
			Color roiColor = currentComboboxROIName.getHighlightColor();
			int roiColorIndex = -1;
			for (int i = 0; i < OverlayEditorPanelJAI.CONTRAST_COLORS.length; i++) {
				if(OverlayEditorPanelJAI.CONTRAST_COLORS[i].equals(roiColor)){
					roiColorIndex = i;
					break;
				}
			}

			//Create empty ROI
			UShortImage[] roiZ = new UShortImage[imageDataSet.getSizeZ()];
			for (int i = 0; i < roiZ.length; i++) {
				short[] pixels = new short[imageDataSet.getISize().getX()*imageDataSet.getISize().getY()];
				Origin origin = imageDataSet.getImage(i, 0, 0).getOrigin();
				Extent extent = imageDataSet.getImage(i, 0, 0).getExtent();
				roiZ[i] = new UShortImage(pixels,origin,extent,
						imageDataSet.getISize().getX(),imageDataSet.getISize().getY(),1);
			}
			ROI originalROI = new ROI(roiZ,"Assist ROI");
			ROI finalROI = overlayEditorPanelJAI.showAssistDialog(originalROI,null, false/*, false, false*/);
			
			if(finalROI != null){
				boolean bOverWrite = true;
				roiZ = finalROI.getRoiImages();
				//Check for existing ROI
				final String OVERWRITE_ALL = "Overwrite any existing ROIs";
				final String KEEP_EXISTING = "Keep existing ROIs when overlapping";
				final String CANCEL_ROI_UPDATE = "Cancel";
				for (int i = 0; i < roiZ.length; i++) {
					boolean bDone = false;
					short[] pixels = roiZ[i].getPixels();
					byte[] compositePixels = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
					for (int j = 0; j < compositePixels.length; j++) {
						if(compositePixels[j] != 0 && pixels[j] != 0/* && compositePixels[j] != (byte)roiColorIndex*/){
							bDone = true;
							String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI,
									"Some areas of the new ROI overlap with existing ROIs.",
									new String[] {OVERWRITE_ALL,KEEP_EXISTING,CANCEL_ROI_UPDATE},OVERWRITE_ALL);
							if(result.equals(KEEP_EXISTING)){
								bOverWrite = false;
							}else if(result.equals(CANCEL_ROI_UPDATE)){
								overlayEditorPanelJAI.setROI(null);//Clear highlight ROI leftover from ROIAssistPanel
								return;
							}
							break;
						}
					}
					if(bDone){
						break;
					}
				}
				//Update composite ROI
				for (int i = 0; i < roiZ.length; i++) {
					short[] pixels = roiZ[i].getPixels();
					byte[] compositePixels = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
					for (int j = 0; j < pixels.length; j++) {
						if(pixels[j] != 0){
							compositePixels[j] =
								(bOverWrite?
								(byte)roiColorIndex:
									(compositePixels[j] == 0?(byte)roiColorIndex:compositePixels[j]));
						}
					}
				}
			}
			overlayEditorPanelJAI.setROI(null);//Clear highlight ROI leftover from ROIAssistPanel

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Error in ROI Assistant:\n"+e.getMessage());
		}

	}
	private void clearROI(boolean bAskClearAll,Color roiColor){
		int roiCount = overlayEditorPanelJAI.getAllCompositeROINamesAndColors().size();
		if(bAskClearAll && roiCount > 1){
			final String clearAll = "Clear all ROIs";
			final String clearCurrentOnly = "Clear only current ROI";
			final String cancel = "Cancel";
			String result = DialogUtils.showWarningDialog(
					JOptionPane.getRootFrame(),
					"Clear only current ROI or clear all ROIs?",
					new String[] {clearCurrentOnly,clearAll,cancel},
					clearCurrentOnly);
			if(result == null){return;}
			if(result.equals(clearAll)){
				for (int i = 0; i < roiComposite.length; i++) {
					byte[] roiData = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
					Arrays.fill(roiData, (byte)0);
				}
				overlayEditorPanelJAI.setROI(null);
				return;
			}else if (result.equals(cancel)){
				return;
			}
		}

		int roiColorIndex = -1;
		for (int i = 0; i < OverlayEditorPanelJAI.CONTRAST_COLORS.length; i++) {
			if(OverlayEditorPanelJAI.CONTRAST_COLORS[i].equals(roiColor)){
				roiColorIndex = i;
				break;
			}
		}
		for (int i = 0; i < roiComposite.length; i++) {
			byte[] roiData = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
			for (int j = 0; j < roiData.length; j++) {
				if((roiData[j]&0x000000FF) == roiColorIndex){
					roiData[j] = 0;
				}
			}
		}
		overlayEditorPanelJAI.setROI(null);

	}
	private void checkROI(){

		AsynchClientTask warnExistingCheck = new AsynchClientTask("Warn existing check",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if(overlayEditorPanelJAI.getROI() != null){
					final String recheck = "Redo region check";
//					final String fixCheck = "Suggest fixes...";
					final String clear = "Clear region check";
					final String cancel = "Cancel";
					String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI,
							"A Check region highlight currently exists.",
							new String[] {recheck,/*fixCheck,*/clear,cancel},
							recheck);
					if(result.equals(recheck)){
						overlayEditorPanelJAI.setROI(null);
					}/*else if(result.equals(fixCheck)){
						
					}*/else if(result.equals(clear)){
						overlayEditorPanelJAI.setROI(null);
						throw UserCancelException.CANCEL_GENERIC;
					}else if(result.equals(cancel)){
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
			}
		};

		AsynchClientTask regionTask = new AsynchClientTask("Finding distinct regions",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {				

				try{
					progressWait("Check Regions", "Finding distinct regions...");
					VCImage checkImage = ROIMultiPaintManager.createVCImageFromBufferedImages(imageDataSet.getExtent(), roiComposite);
					RegionImage regionImage =
						new RegionImage(checkImage, 0 /*0 means generate no surfacecollection*/,
								checkImage.getExtent(),imageDataSet.getAllImages()[0].getOrigin(), RegionImage.NO_SMOOTHING);
					RegionImage.RegionInfo[] allRegionInfos = regionImage.getRegionInfos();
										
					//Assumes allRegionInfos array position == regionInfo.regionIndex
					progressWait(null, "Generating neighbor info...");
					HighlightROIInfo highlightROIInfo =
						generateHighlightROIInfo(regionImage,
								RegionAction.createCheckNeighborsOnlyRegionAction(allRegionInfos));			
					
					//sort region/neighbors list for display
					TreeMap<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsTreeMap =
					new TreeMap<RegionImage.RegionInfo,TreeSet<Integer>>(new Comparator<RegionImage.RegionInfo>() {
						public int compare(RegionImage.RegionInfo o1, RegionImage.RegionInfo o2) {
							int retVal = 0;
							if(o1.getPixelValue() == o2.getPixelValue()){
								retVal = -(o1.getNumPixels() - o2.getNumPixels());
							}else{
								retVal =  o1.getPixelValue() - o2.getPixelValue();
							}
							if(retVal == 0){
								retVal = o1.getRegionIndex() - o2.getRegionIndex();
							}
							return retVal;
						}
					});
					neighborsForRegionsTreeMap.putAll(highlightROIInfo.neighborsForRegionsMap);
					
					//construct Region list
					RegionImage.RegionInfo[] sortedRegionInfoArr =
						(RegionImage.RegionInfo[])neighborsForRegionsTreeMap.keySet().toArray(new RegionImage.RegionInfo[0]);
					
					Vector<String> colROIName = new Vector<String>();
					final Vector<RegionImage.RegionInfo> colRegionInfo = new Vector<RegionImage.RegionInfo>();
					Vector<String> colNeighbors = new Vector<String>();
					for (int i = 0; i < sortedRegionInfoArr.length; i++) {
						if(sortedRegionInfoArr[i].getPixelValue() == 0){
							colROIName.add(RESERVED_NAME_BACKGROUND);
							colRegionInfo.add(sortedRegionInfoArr[i]);
						}else{
							colROIName.add(getRoiNameFromPixelValue(sortedRegionInfoArr[i].getPixelValue()));
							colRegionInfo.add(sortedRegionInfoArr[i]);
						}
						StringBuffer sb = new StringBuffer();
						
						Iterator<Integer> neighborIter = highlightROIInfo.neighborsForRegionsMap.get(sortedRegionInfoArr[i]).iterator();
						while(neighborIter.hasNext()){
							int pixVal = neighborIter.next();
							sb.append((sb.length()==0?"":", ")+getRoiNameFromPixelValue(pixVal));
						}
						colNeighbors.add(sb.toString());
					}
					progressWait(null, null);
					//Show list
					String[][] rowData = new String[colROIName.size()][3];
					for (int i = 0; i < rowData.length; i++) {
						rowData[i][0] = colROIName.elementAt(i);
						rowData[i][1] = colRegionInfo.elementAt(i).getNumPixels()+"";
						rowData[i][2] = colNeighbors.elementAt(i);
					}
					
					final Vector<RegionImage.RegionInfo> selectedRegionsV = new Vector<RegionImage.RegionInfo>();
					final String highlightSelectedRegions = "Highlight selected in display";
					final String mergeWithNeighbor = "Merge selected with neighbor";
					final String cancel = "Cancel";
					DialogUtils.TableListResult tableListResult = DialogUtils.showComponentOptionsTableList(
						overlayEditorPanelJAI,
						"Select regions and action",
						new String[] {"Region Name","Region size (pixels)","neighbors"},
						rowData,
						ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,
						new ListSelectionListener() {
							public void valueChanged(ListSelectionEvent e) {
								if(!e.getValueIsAdjusting()){
									selectedRegionsV.clear();
									DefaultListSelectionModel defaultListSelectionModel = (DefaultListSelectionModel)e.getSource();										
									for (int i = defaultListSelectionModel.getMinSelectionIndex(); i <= defaultListSelectionModel.getMaxSelectionIndex(); i++) {
										if(defaultListSelectionModel.isSelectedIndex(i)){
											selectedRegionsV.add(colRegionInfo.elementAt(i));
										}
									}
								}
							}
						},
						new String[] {highlightSelectedRegions,mergeWithNeighbor,cancel},highlightSelectedRegions
					);

					if(tableListResult.selectedOption.equals(cancel) || selectedRegionsV.size() == 0){
						throw UserCancelException.CANCEL_GENERIC;
					}else if(tableListResult.selectedOption.equals(highlightSelectedRegions)){
						progressWait(null, "Generating highlight info...");
						highlightROIInfo = generateHighlightROIInfo(regionImage,
								RegionAction.createHighlightRegionAction(allRegionInfos, selectedRegionsV));
						hashTable.put("highlightROI", highlightROIInfo.highlightROI);				
					}else if(tableListResult.selectedOption.equals(mergeWithNeighbor)){
						for (int i = 0; i < selectedRegionsV.size(); i++) {
							if(highlightROIInfo.neighborsForRegionsMap.get(selectedRegionsV.elementAt(i)).size() > 1){
								final String proceed = "Ok, Proceed with merge";
								String result = 
									DialogUtils.showWarningDialog(overlayEditorPanelJAI,
											"Some selected regions have more than 1 neighbor."+
											"  Regions with more than 1 neighbor will be ignored during the merge.",
											new String[] {proceed,cancel}, cancel);
								if(result.equals(cancel)){
									throw UserCancelException.CANCEL_GENERIC;
								}
								break;
							}
						}
						generateHighlightROIInfo(regionImage,
								RegionAction.createMergeSelectedWithNeighborsRegionAction(
										allRegionInfos,
										selectedRegionsV,
										highlightROIInfo.neighborsForRegionsMap));
					}
				}finally{
					progressWait(null, null);
				}
			}//run
		};//task
		
		AsynchClientTask updateROITaks = new AsynchClientTask("Updating ROI",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ROI highlightROI = (ROI)hashTable.get("highlightROI");
				overlayEditorPanelJAI.getRoiSouceData().addReplaceRoi(highlightROI);
				if(highlightROI != null){
					DialogUtils.showInfoDialog(overlayEditorPanelJAI,
							"Note: Use the 'blend' value selector (found near the 'Check...' button)"+
							" to increase the displayed intensity of the chosen ROI regions."+
							" Lower values highlight the chosen ROI regions more.");
				}
			}
		};

		ClientTaskDispatcher.dispatch(
				overlayEditorPanelJAI, new Hashtable<String, Object>(),
				new AsynchClientTask[] {warnExistingCheck,regionTask,updateROITaks}, false,false,false,null,true);
	}
	
	private String getRoiNameFromPixelValue(int pixelValue){
		if(pixelValue == 0){
			return RESERVED_NAME_BACKGROUND;
		}
		Hashtable<String, Color> allROINamesAndColors = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
		Enumeration<String> roiNameEnum2 = allROINamesAndColors.keys();
		while(roiNameEnum2.hasMoreElements()){
			String roiName = roiNameEnum2.nextElement();
			Color roiColor = allROINamesAndColors.get(roiName);
			if(OverlayEditorPanelJAI.CONTRAST_COLORS[pixelValue].equals(roiColor)){
				return roiName;
			}
		}
		throw new RuntimeException("No color found for pixelvalue="+pixelValue);
	}
	
	private static class RegionAction{
		public static final int REGION_ACTION_HIGHLIGHT = 0;
		public static final int REGION_ACTION_CHECKNEIGHBORSONLY = 1;
		public static final int REGION_ACTION_MERGESELECTEDWITHNEIGHBORS = 2;
		
		private int allRegionsInfosSize;
		private Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsMap;
		private List<RegionImage.RegionInfo> selectedRegionsV;
		private int action;
		private RegionAction(){
			
		}
		public int getAction(){
			return action;
		}
		public int getAllRegionInfosSize(){
			return allRegionsInfosSize;
		}
		public List<RegionImage.RegionInfo> getSelectedRegionInfos(){
			return selectedRegionsV;
		}
		public Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> getNeighborsForRegionMap(){
			return neighborsForRegionsMap;
		}
		public static RegionAction createHighlightRegionAction(RegionImage.RegionInfo[] allRegionInfos,List<RegionImage.RegionInfo> selectedRegionsV){
			RegionAction regionAction = new RegionAction();
			regionAction.allRegionsInfosSize = allRegionInfos.length;
			regionAction.selectedRegionsV = selectedRegionsV;
			regionAction.action = REGION_ACTION_HIGHLIGHT;
			return regionAction;
		}
		public static RegionAction createCheckNeighborsOnlyRegionAction(RegionImage.RegionInfo[] allRegionInfos){
			RegionAction regionAction = new RegionAction();
			regionAction.allRegionsInfosSize = allRegionInfos.length;
			regionAction.selectedRegionsV = Arrays.asList(allRegionInfos);
			regionAction.action = REGION_ACTION_CHECKNEIGHBORSONLY;
			return regionAction;
		}
		public static RegionAction createMergeSelectedWithNeighborsRegionAction(
				RegionImage.RegionInfo[] allRegionInfos,
				List<RegionImage.RegionInfo> selectedRegionsV,
				Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsMap){
			RegionAction regionAction = new RegionAction();
			regionAction.allRegionsInfosSize = allRegionInfos.length;
			regionAction.selectedRegionsV = selectedRegionsV;
			regionAction.neighborsForRegionsMap = neighborsForRegionsMap;
			regionAction.action = REGION_ACTION_MERGESELECTEDWITHNEIGHBORS;
			return regionAction;
		}

	}
	private static class HighlightROIInfo{
		public ROI highlightROI;
		public Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsMap =
			new Hashtable<RegionInfo, TreeSet<Integer>>();
	}
	private HighlightROIInfo generateHighlightROIInfo(RegionImage regionImage,RegionAction regionAction) throws Exception{
		
		HighlightROIInfo highlightROIInfo = new HighlightROIInfo();

		//Create lookup map to speedup highlighting operation for large dataset
		RegionImage.RegionInfo[] selectedRegionMap = new RegionImage.RegionInfo[regionAction.getAllRegionInfosSize()];
		Iterator<RegionImage.RegionInfo> selectedIter = regionAction.getSelectedRegionInfos().iterator();
		while(selectedIter.hasNext()){
			RegionImage.RegionInfo nextRegion = selectedIter.next();
			selectedRegionMap[nextRegion.getRegionIndex()] = nextRegion;
		}
		byte[] shortEncodedRegionIndexes = regionImage.getShortEncodedRegionIndexImage();
		
		final int XSIZE = imageDataSet.getISize().getX();
		final int XYSIZE = XSIZE*imageDataSet.getISize().getY();
		if(regionAction.getAction() == RegionAction.REGION_ACTION_HIGHLIGHT){
			//Highlight selected regions
			UShortImage[] ushortRegionHighlightArr = new UShortImage[roiComposite.length];
			for (int i = 0; i < ushortRegionHighlightArr.length; i++) {
				ushortRegionHighlightArr[i] =
					new UShortImage(
							new short[XYSIZE],
							new Origin(0, 0, 0),new Extent(1,1,1),
							imageDataSet.getISize().getX(),
							imageDataSet.getISize().getY(),
							1);
			}
			highlightROIInfo.highlightROI = new ROI(ushortRegionHighlightArr,"highlightRegion");
		}

		int allIndex = 0;
		final int ZMAX = roiComposite.length-1;
		final int XMAX = roiComposite[0].getWidth()-1;
		final int YMAX = roiComposite[0].getHeight()-1;
		for (int z = 0; z < roiComposite.length; z++) {
			int index = 0;
			for (int y = 0; y < roiComposite[0].getHeight(); y++) {
				for (int x = 0; x < roiComposite[0].getWidth(); x++) {
					int regionIndex =
						(shortEncodedRegionIndexes[allIndex]&0x000000FF) |
						(shortEncodedRegionIndexes[allIndex+1]&0x000000FF)<<8;
					if(selectedRegionMap[regionIndex] != null){
						if(regionAction.getAction() == RegionAction.REGION_ACTION_CHECKNEIGHBORSONLY){
							//Find neighbors
							int[] neighbors = new int[6];
							Arrays.fill(neighbors, -1);
							if(z>0){//top neighbor
								neighbors[0] = ((DataBufferByte)roiComposite[z-1].getRaster().getDataBuffer()).getData()[index];
							}
							if(z<ZMAX){//bottom neighbor
								neighbors[1] = ((DataBufferByte)roiComposite[z+1].getRaster().getDataBuffer()).getData()[index];
							}
							if(x>0){//left neighbor
								neighbors[2] = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index-1];
							}
							if(x<XMAX){//right neighbor
								neighbors[3] = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index+1];
							}
							if(y>0){//front neighbor
								neighbors[4] = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index-XSIZE];
							}
							if(y<YMAX){//back neighbor
								neighbors[5] = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index+XSIZE];
							}
							if(!highlightROIInfo.neighborsForRegionsMap.containsKey(selectedRegionMap[regionIndex])){
								highlightROIInfo.neighborsForRegionsMap.put(selectedRegionMap[regionIndex],new TreeSet<Integer>());
							}
							TreeSet<Integer> neighborTreeSet = highlightROIInfo.neighborsForRegionsMap.get(selectedRegionMap[regionIndex]);
							for (int i = 0; i < neighbors.length; i++) {
								if(neighbors[i] != -1 && neighbors[i] != selectedRegionMap[regionIndex].getPixelValue()){
									neighborTreeSet.add(neighbors[i]);
								}
							}
						}else if(regionAction.getAction() == RegionAction.REGION_ACTION_HIGHLIGHT){
							highlightROIInfo.highlightROI.getRoiImages()[z].getPixels()[index] = 1;
						}else if(regionAction.getAction() == RegionAction.REGION_ACTION_MERGESELECTEDWITHNEIGHBORS){
							if(regionAction.getNeighborsForRegionMap().get(selectedRegionMap[regionIndex]).size()==1){
								((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index] =
									(byte)regionAction.getNeighborsForRegionMap().get(selectedRegionMap[regionIndex]).first().intValue();
							}
						}
					}
					index++;
					allIndex+=2;
				}
			}
		}
		return highlightROIInfo;
	}
	private void progressWait(String title,String message){
		if(message == null){
			if(asynchProgressPopup != null){
				asynchProgressPopup.stop();
				asynchProgressPopup = null;
			}
			return;
		}
		if(asynchProgressPopup == null){
			asynchProgressPopup = new AsynchProgressPopup(overlayEditorPanelJAI,
					(title==null?"Wait...":title), message, null, true, false);
			asynchProgressPopup.startKeepOnTop();
		}
		asynchProgressPopup.setMessage(message);
	}
}
