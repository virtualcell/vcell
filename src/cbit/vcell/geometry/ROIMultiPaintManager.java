package cbit.vcell.geometry;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputEvent;
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
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.DefaultListSelectionModel;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoableEditSupport;

import loci.formats.ImageTools;

import org.vcell.util.BeanUtils;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.Extent;
import org.vcell.util.Hex;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.Range;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ProgressDialogListener;
import org.vcell.util.gui.UtilCancelException;

import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.VirtualMicroscopy.Image.ImageStatistics;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.EditImageAttributes;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.modeldb.ServerDocumentManager;

public class ROIMultiPaintManager implements PropertyChangeListener{

	private static final String RESERVED_NAME_BACKGROUND = "background";
	private static final boolean B_DISPLAY_ZERO_INDEX_Z = false;
	
	private OverlayEditorPanelJAI overlayEditorPanelJAI;
	private ROIMultiPaintManager.Crop3D mergedCrop3D;
	private BufferedImage[] roiComposite;
	private IndexColorModel indexColorModel;
	private ImageDataset imageDataSet;
	private static final Extent DEFAULT_EXTENT = new Extent(1,1,1);
	private static final Origin DEFAULT_ORIGIN = new Origin(0,0,0);
		
	private AsynchProgressPopup progressWaitPopup;
	
	public ROIMultiPaintManager(){
		super();
	}
	
	public static class EdgeIndexInfo {
		public static final byte XM_EDGE = 1;//00000001
		public static final byte XP_EDGE = 2;//00000010
		public static final byte YM_EDGE = 4;//00000100
		public static final byte YP_EDGE = 8;//00001000
		public static final byte ZM_EDGE = 16;//0010000
		public static final byte ZP_EDGE = 32;//0100000
		
		public int[] allEdgeIndexes;
		public byte[] edgeFlag;
		public int xSize;
		public int ySize;
		public int zSize;
		
		public boolean isZM(int index){
			return (edgeFlag[index] & ZM_EDGE) != 0;
		}
		public boolean isZP(int index){
			return (edgeFlag[index] & ZP_EDGE) != 0;
		}
		public boolean isYM(int index){
			return (edgeFlag[index] & YM_EDGE) != 0;
		}
		public boolean isYP(int index){
			return (edgeFlag[index] & YP_EDGE) != 0;
		}
		public boolean isXM(int index){
			return (edgeFlag[index] & XM_EDGE) != 0;
		}
		public boolean isXP(int index){
			return (edgeFlag[index] & XP_EDGE) != 0;
		}
		public boolean isZ(int index){
			return isZM(index) || isZP(index);
		}
		public boolean isXY(int index){
			return isXM(index) || isXP(index) || isYM(index) || isYP(index);
		}
	}
	public static EdgeIndexInfo calculateEdgeIndexes(int xSize,int ySize,int zSize){
		if((xSize!=1 && xSize<3) || (ySize!=1 && ySize<3) ||(zSize!=1 && zSize<3)){
			throw new IllegalArgumentException("Sizes CANNOT be negative or 0 or 2");
		}
		int XYSIZE = xSize*ySize;
		int numEdgeIndexes = xSize*ySize*zSize - ((xSize==1?1:xSize-2)*(ySize==1?1:ySize-2)*(zSize == 1?1:zSize-2));
		int[] edgeIndexes = new int[numEdgeIndexes];
		byte[] edgeFlag = new byte[numEdgeIndexes];
		if(numEdgeIndexes != 0){
			int index = 0;
			for (int z = 0; z < zSize; z++) {
				boolean bZM = (z==0);
				boolean bZP = (z==(zSize-1));
				boolean bZEdge = (bZM || bZP) && zSize!=1;
				for (int y = 0; y < ySize; y++) {
					boolean bYM = (y==0);
					boolean bYP = (y==ySize-1);
					boolean bYEdge = (bYM || bYP) && ySize!=1;
					int xIncr = (bYEdge||bZEdge?1:xSize-1);
					for (int x = 0; x < xSize; x+= xIncr) {
						int edgeIndex = x+(y*xSize)+(z*XYSIZE);
						edgeIndexes[index] = edgeIndex;
						edgeFlag[index] =
							(byte)(
								(bZM?ROIMultiPaintManager.EdgeIndexInfo.ZM_EDGE:(byte)0) |
								(bZP?ROIMultiPaintManager.EdgeIndexInfo.ZP_EDGE:(byte)0) |
								(bYM?ROIMultiPaintManager.EdgeIndexInfo.YM_EDGE:(byte)0) |
								(bYP?ROIMultiPaintManager.EdgeIndexInfo.YP_EDGE:(byte)0) |
								(x==0?ROIMultiPaintManager.EdgeIndexInfo.XM_EDGE:(byte)0) |
								((x==xSize-1)?ROIMultiPaintManager.EdgeIndexInfo.XP_EDGE:(byte)0)
							);
							
						index++;
					}
				}
			}
			if(index != numEdgeIndexes){
				throw new RuntimeException("final count not match calculated");
			}
		}
		
		EdgeIndexInfo edgeIndexInfo = new EdgeIndexInfo();
		edgeIndexInfo.allEdgeIndexes = edgeIndexes;
		edgeIndexInfo.edgeFlag = edgeFlag;
		edgeIndexInfo.xSize = xSize;
		edgeIndexInfo.ySize = ySize;
		edgeIndexInfo.zSize = zSize;
		return edgeIndexInfo;
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
	
	public static final String IMPORT_SOURCE_NAME = "IMPORT_SOURCE_NAME";
	public static final String IMPORTED_DATA_CONTAINER = "fdfos";
	public static final String CROPPED_ROI = "vcImage";
	public static final String CROP_3D = "previousCrop3D";
	public static final String ROI_AND_CROP = "ROI_AND_CROP";
	public static final String SHOW_ROI_PANEL_TASK_NAME = "showROIPanelTask";
	public static final String INIT_ROI_DATA_TASK_NAME = "initROIDataTask";

	public AsynchClientTask[] getROIEditTasks(){
		AsynchClientTask testTask = new AsynchClientTask("testTask",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				new Thread(new Runnable() {
					public void run(){
						try{
							Robot robot = new Robot();
						while(overlayEditorPanelJAI == null || !overlayEditorPanelJAI.isDisplayable()){
							System.out.println("waiting for overlay...");
							Thread.sleep(500);
						}
						System.out.println("----------overlayEditor init");
//						Thread.sleep(10000);
						Vector<Component> componentsV = new Vector<Component>();
						Window parentWindow = (Window)BeanUtils.findTypeParentOfComponent(overlayEditorPanelJAI, Window.class);
						componentsV.add(parentWindow);
						while(componentsV.size() > 0){
							Component comp = componentsV.remove(0);
							if(comp instanceof AbstractButton){
								System.out.println("----------btn="+
									"name="+((AbstractButton)comp).getName()+
									" txt="+((AbstractButton)comp).getText()+
									" parent="+comp.getParent()+
									" type="+comp.getClass().getSimpleName());
//								if("roiAddBtn".equals(((AbstractButton)comp).getName())){
//									Point roiAddBtnScrnPoint = new Point(0,0);
//									SwingUtilities.convertPointToScreen(roiAddBtnScrnPoint, comp);
//									robot.mouseMove(roiAddBtnScrnPoint.x,roiAddBtnScrnPoint.y);
//									robot.mousePress(InputEvent.BUTTON1_MASK);
//									robot.mouseRelease(InputEvent.BUTTON1_MASK);
//								}
								if("OptionPane.button".equals(((AbstractButton)comp).getName()) &&
										"OK".equals(((AbstractButton)comp).getText())){
								Point okBtnScrnPoint = new Point(0,0);
								SwingUtilities.convertPointToScreen(okBtnScrnPoint, comp);
								robot.mouseMove(okBtnScrnPoint.x,okBtnScrnPoint.y);
								robot.mousePress(InputEvent.BUTTON1_MASK);
								robot.mouseRelease(InputEvent.BUTTON1_MASK);
							}

							}
							if(comp instanceof Container && ((Container)comp).getComponentCount() != 0){
								componentsV.addAll(Arrays.asList(((Container)comp).getComponents()));
							}
						}
//						ROIMultiPaintManager.Crop3D crop3D = new ROIMultiPaintManager.Crop3D();
//						crop3D.setBounds(20, 20, 0, 100, 50, roiComposite.length);
//						cropROIData(crop3D,true);

//						ROIMultiPaintManager.this.propertyChange(
//							new PropertyChangeEvent(this, OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY, null, newValue));
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}).start();
			}
		};
		AsynchClientTask initROIDataTask = new AsynchClientTask(INIT_ROI_DATA_TASK_NAME,AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				getClientTaskStatusSupport().setMessage("Initializing ROI data.");
//				System.out.println("----------task "+getTaskName());
				mergedCrop3D = new ROIMultiPaintManager.Crop3D();
				//
				//previouslyEditedVCImage and previousCrop3D can be null if this is the first time this method
				//has been called in an editing session. 
				//They are removed from the hash because later tasks will resave with different crop if necessary
				//
				VCImage previouslyEditedVCImage = (VCImage)hashTable.get(CROPPED_ROI);
				ROIMultiPaintManager.Crop3D previousCrop3D = (ROIMultiPaintManager.Crop3D)hashTable.remove(CROP_3D);
				if((previousCrop3D == null && previouslyEditedVCImage != null) ||
						(previousCrop3D != null && previouslyEditedVCImage == null)){
					throw new IllegalArgumentException("Previous VCImage and Crop must both be null or both be not null.");
				}
				FieldDataFileOperationSpec originalDataContainer =
					(FieldDataFileOperationSpec)hashTable.get(IMPORTED_DATA_CONTAINER);
//				System.out.println("previous Crop before border fix= "+previousCrop3D);
				ROIMultiPaintManager.fixBorderProblemInPlace(originalDataContainer, previouslyEditedVCImage,previousCrop3D);
				initImageDataSet(originalDataContainer.shortSpecData[0][0],originalDataContainer.isize);
				initROIComposite();
				//Crop with mergedCropRectangle
//				System.out.println("previous Crop crop= "+previousCrop3D);
				if(previousCrop3D != null){
					cropROIData(previousCrop3D,false);
				}else{
					mergedCrop3D.setBounds(0, 0, 0,
							originalDataContainer.isize.getX(), originalDataContainer.isize.getY(), originalDataContainer.isize.getZ());
				}
//				System.out.println("merge Crop after apply previous= "+mergedCrop3D);

				//Sanity check crop
				if(mergedCrop3D.width != roiComposite[0].getWidth() ||
						mergedCrop3D.height != roiComposite[0].getHeight() ||
						mergedCrop3D.depth != roiComposite.length){
					String message =
						"initial cropping failed.\n"+
						"previousCrop3D="+(previousCrop3D!= null?previousCrop3D.toString():null)+"\n"+
						"previouslyEditedVCImage x="+(previouslyEditedVCImage!= null?previouslyEditedVCImage.getNumX():null)+
						",y="+(previouslyEditedVCImage!= null?previouslyEditedVCImage.getNumY():null)+
						",z="+(previouslyEditedVCImage!= null?previouslyEditedVCImage.getNumZ():null)+"\n"+
						"mergeCrop3D="+mergedCrop3D.toString()+"\n"+
						"roiComposite x="+roiComposite[0].getRaster().getWidth()+
						",y="+roiComposite[0].getRaster().getHeight()+
						",z="+roiComposite.length;
					System.out.println(message);
					throw new Exception(message);
				}
				//initialize the roiComposite with previouslyEditedVCImage pixel values
				if(previouslyEditedVCImage != null){
					int index = 0;
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
				}
			}
		};

		AsynchClientTask initROIPanelTask = new AsynchClientTask("initROIPanelTask",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				getClientTaskStatusSupport().setMessage("Initializing ROI editor.");
//				System.out.println("----------task "+getTaskName());
				if(overlayEditorPanelJAI == null){
					overlayEditorPanelJAI = new OverlayEditorPanelJAI();
					overlayEditorPanelJAI.setMinimumSize(new Dimension(700,500));
					overlayEditorPanelJAI.setPreferredSize(new Dimension(700,500));
					overlayEditorPanelJAI.setModeRemoveROIWhenPainting(true);
					overlayEditorPanelJAI.setUndoableEditSupport(new UndoableEditSupport());
					overlayEditorPanelJAI.setROITimePlotVisible(false);
					overlayEditorPanelJAI.setROITimePlotVisible(false);
					overlayEditorPanelJAI.addPropertyChangeListener(ROIMultiPaintManager.this);
				}
				overlayEditorPanelJAI.deleteROIName(null);//delete all names
				if(hashTable.containsKey(CROPPED_ROI)){
					//initialize the ROI names with previouslyEditedVCImage pixelClass names
					VCImage previouslyEditedVCImage = (VCImage)hashTable.remove(CROPPED_ROI);
					VCPixelClass[] previousPixelClasses = previouslyEditedVCImage.getPixelClasses();
					String firstROI = null;
					for (int i = 0; i < previousPixelClasses.length; i++) {
						if(previousPixelClasses[i].getPixel() == 0){//don't add background
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
				overlayEditorPanelJAI.setImages(imageDataSet, true,
						OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR, OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR);
				overlayEditorPanelJAI.setAllROICompositeImage(roiComposite);
			}
		};

		AsynchClientTask showROIPanelTask = new AsynchClientTask(SHOW_ROI_PANEL_TASK_NAME,AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				getClientTaskStatusSupport().setMessage("Showing ROI editor.");
//				System.out.println("----------task "+getTaskName());

				String sourceName = (String)hashTable.get(IMPORT_SOURCE_NAME);
				int retCode = DialogUtils.showComponentOKCancelDialog(
						(Component)hashTable.get(ClientRequestManager.GUI_PARENT),
						overlayEditorPanelJAI, "segment image for geometry "+(sourceName==null?"(No Name)":"("+sourceName+")"));
				if (retCode != JOptionPane.OK_OPTION){
					throw UserCancelException.CANCEL_GENERIC;
				}
			}
		};
		AsynchClientTask checkROI = new AsynchClientTask("checkROI",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
//				System.out.println("----------task "+getTaskName());

				getClientTaskStatusSupport().setMessage("Checking ROI.");
				Hashtable<String, Color> roiNamesAndColorsHash = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
				if(roiNamesAndColorsHash == null || roiNamesAndColorsHash.size() == 0){
					//No ROI defined
					final String reloadImage = "Reload Image";
					final String cancel = "Back to segmentation";
					String result = DialogUtils.showWarningDialog(
							(Component)hashTable.get(ClientRequestManager.GUI_PARENT),
							"User has defined no ROIs.  Choose an action:\n"+
							"1.  Reload image and choose '"+ClientRequestManager.SEGMENT_KEEP_IMPORTED+"'.\n"+
							"3.  Cancel, go back to segmentation and add 1 or more ROI.",
							new String[] {reloadImage,cancel}, cancel);
					if(result.equals(cancel)){
						hashTable.put(ClientTaskDispatcher.TASK_REWIND, SHOW_ROI_PANEL_TASK_NAME);
						return;
					}else{
						hashTable.put(ClientTaskDispatcher.TASK_REWIND, ClientRequestManager.PARSE_IMAGE_TASK_NAME);
						return;
					}
				}
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
				VCPixelClass[] vcPixelClassesFromROINames = null;
				boolean bForceAssignBackground = false;
				if(bHasUnassignedBackground){
					final String assignToBackground = "Assign as default 'background'";
					final String cancelAssign = "Cancel, back to segmentation...";
					String result = DialogUtils.showWarningDialog(
							(Component)hashTable.get(ClientRequestManager.GUI_PARENT),
							"Warning: some areas of image segmentation have not been assigned to an ROI."+
							"  This can happen when small unintended gaps are left between adjacent ROIs"+
							" or areas around the edges were intentionally left as background.  Choose an action:\n"+
							"1.  Leave as is, unassigned areas should be treated as 'background'.\n"+
							"2.  Go back to segmentation tool. (note: look for areas with no color)",
							new String[] {assignToBackground,/*assignToNeighbors,*/cancelAssign}, assignToBackground);
					if(result.equals(assignToBackground)){
						bForceAssignBackground = true;
					}else{
						hashTable.put(ClientTaskDispatcher.TASK_REWIND, SHOW_ROI_PANEL_TASK_NAME);
						return;
					}
					if(bForceAssignBackground){
						vcPixelClassesFromROINames = new VCPixelClass[roiNamesAndColorsHash.size()+1];
						vcPixelClassesFromROINames[0] = new VCPixelClass(null, RESERVED_NAME_BACKGROUND, 0);					
					}
				}else{
					vcPixelClassesFromROINames = new VCPixelClass[roiNamesAndColorsHash.size()];
				}
				

				//find pixel indexes corresponding to colors for ROIs
				int index = (bForceAssignBackground?1:0);
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
		
				VCImage initImage = createVCImageFromBufferedImages(imageDataSet.getExtent(), roiComposite);

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
							(Component)hashTable.get(ClientRequestManager.GUI_PARENT), 
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
						hashTable.put(ClientTaskDispatcher.TASK_REWIND, SHOW_ROI_PANEL_TASK_NAME);
						return;
					}
				}
				//Check borders
				try{
					Hashtable<VCImage, ROIMultiPaintManager.Crop3D> temp = checkBorders(initImage/*,mergedCrop3D*/);
					if(temp != null){
						initImage = temp.keySet().toArray(new VCImage[0])[0];
						mergedCrop3D = temp.values().toArray(new ROIMultiPaintManager.Crop3D[0])[0];
					}
				}catch(UserCancelException uce){
					hashTable.put(ClientTaskDispatcher.TASK_REWIND, SHOW_ROI_PANEL_TASK_NAME);
					return;
				}
				initImage.setPixelClasses(vcPixelClassesFromROINames);
				updateExtent(initImage,
						((FieldDataFileOperationSpec)hashTable.get(IMPORTED_DATA_CONTAINER)).extent,
						((FieldDataFileOperationSpec)hashTable.get(IMPORTED_DATA_CONTAINER)).isize);
				hashTable.put(CROPPED_ROI, initImage);
				hashTable.put(CROP_3D, mergedCrop3D);
			}
		};
		return new AsynchClientTask[] {initROIDataTask,/*testTask,*/initROIPanelTask,showROIPanelTask,checkROI};
	}
	
	private static class BorderInfo {
		public boolean bXYTouch = false;
		public boolean bZTouch = false;
	}
	private BorderInfo checkBorderInfo(VCImage checkThisVCImage) throws Exception{
		EdgeIndexInfo edgeIndexInfo =
			ROIMultiPaintManager.calculateEdgeIndexes(checkThisVCImage.getNumX(), checkThisVCImage.getNumY(), checkThisVCImage.getNumZ());
		BorderInfo borderInfo = new BorderInfo();
		for (int i = 0; i < edgeIndexInfo.allEdgeIndexes.length; i++) {
			if(checkThisVCImage.getPixels()[edgeIndexInfo.allEdgeIndexes[i]] != 0){
				borderInfo.bXYTouch = borderInfo.bXYTouch || edgeIndexInfo.isXY(i);
				borderInfo.bZTouch = borderInfo.bZTouch || edgeIndexInfo.isZ(i);
				if(borderInfo.bXYTouch && borderInfo.bZTouch){
					break;
				}
			}
		}
		borderInfo.bZTouch = borderInfo.bZTouch && checkThisVCImage.getNumZ()>1;
		return borderInfo;
	}
	private Hashtable<VCImage, ROIMultiPaintManager.Crop3D> checkBorders(VCImage checkThisVCImage/*,ROIMultiPaintManager.Crop3D currentMergedCrop3D*/) throws Exception{
		boolean bAddBorder = false;
		BorderInfo borderInfo = checkBorderInfo(checkThisVCImage);
		
		if(borderInfo.bXYTouch || borderInfo.bZTouch){
			boolean b3DTouch = borderInfo.bXYTouch && borderInfo.bZTouch;
			String edgeDescrFrag = "on the "+(b3DTouch?"XY and Z":(borderInfo.bXYTouch?"XY":"Z"))+" border.";
			final String addBorder = "Add empty border";
			final String keep = "Keep as is";
			final String cancel = "Go back to segmentation tool";
			String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI,
					"One or more ROIs touches the outer boundary "+edgeDescrFrag+"\n"+
					"Choose an option:\n"+
					"1. Keep as is, do not change.\n"+
					"2. Add empty border around outer boundary so no ROI touches an outer edge.",
					new String[] {keep,addBorder,cancel}, keep);
			if(result.equals(cancel)){
				throw UserCancelException.CANCEL_GENERIC;
			}else if(result.equals(addBorder)){
				bAddBorder = true;;
			}
		}
		if(!bAddBorder){
			return null;
		}
		ISize checkThisVCImageISize = new ISize(checkThisVCImage.getNumX(), checkThisVCImage.getNumY(), checkThisVCImage.getNumZ());
		ROIMultiPaintManager.PaddedInfo paddedInfo = copyToPadded(
				checkThisVCImage.getPixels(),checkThisVCImageISize,null,checkThisVCImage.getExtent(),
				borderInfo.bXYTouch, borderInfo.bZTouch);
		
		VCImage newVCImage = new VCImageUncompressed(
				null,
				(byte[])paddedInfo.paddedArray, DEFAULT_EXTENT/*paddedInfo.paddedExtent*/,
				paddedInfo.paddedISize.getX(),paddedInfo.paddedISize.getY(),paddedInfo.paddedISize.getZ());
		ROIMultiPaintManager.Crop3D newCrop3D = new ROIMultiPaintManager.Crop3D();
		newCrop3D.setBounds(mergedCrop3D/*previousCrop3D*/);
		if(borderInfo.bXYTouch){
			newCrop3D.width = newVCImage.getNumX();
			newCrop3D.height = newVCImage.getNumY();
			newCrop3D.low.x-= 1;
			newCrop3D.low.y-= 1;
		}
		if(borderInfo.bZTouch){
			newCrop3D.depth = newVCImage.getNumZ();
			newCrop3D.low.z-= 1;
		}
		Hashtable<VCImage, ROIMultiPaintManager.Crop3D> result = new Hashtable<VCImage, ROIMultiPaintManager.Crop3D>();
		result.put(newVCImage, newCrop3D);
		return result;
	}
	private void initImageDataSet(short[] dataToSegment,
			ISize uncroppedISize) throws Exception{
		
		UShortImage[] zImageSet = new UShortImage[uncroppedISize.getZ()];
		for (int i = 0; i < zImageSet.length; i++) {
			short[] shortData = new short[uncroppedISize.getX()*uncroppedISize.getY()];
			System.arraycopy(dataToSegment, shortData.length*i, shortData, 0, shortData.length);
			zImageSet[i] = new UShortImage(shortData,DEFAULT_ORIGIN,DEFAULT_EXTENT,/*newOrigin,newExtent,*/uncroppedISize.getX(),uncroppedISize.getY(),1);
		}
		    
		imageDataSet = new ImageDataset(zImageSet, new double[] { 0.0 }, uncroppedISize.getZ());

	}
	private void initROIComposite(){

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
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
			if(overlayEditorPanelJAI.cropDrawAndConfirm((Rectangle)evt.getNewValue())){
				//2D crop
				Rectangle rect2D = (Rectangle)evt.getNewValue();
				ROIMultiPaintManager.Crop3D crop3D = new ROIMultiPaintManager.Crop3D();
				crop3D.setBounds(rect2D.x, rect2D.y, 0, rect2D.width, rect2D.height, roiComposite.length);
				cropROIData(crop3D,true);

			}
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
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_AUTOCROP_PROPERTY)){

			final String useUnderlying = "Use Underlying";
			final String useROI = "Use all ROI";
			final String cancel = "Cancel";
			String result = useUnderlying;
			if(overlayEditorPanelJAI.getAllCompositeROINamesAndColors().size()!= 0){
				result = DialogUtils.showWarningDialog(overlayEditorPanelJAI, 
						"Auto-crop will find the smallest box that encloses all non-background data values and allow you to 'crop' your data to that size. Choose an action:\n"+
						"1. Use the 'underlying' image to calculate an auto-cropping boundary.\n"+
						"2. Use all the user ROIs to calculate an auto-cropping boundary.",
						new String[] {useUnderlying,useROI,cancel}, useUnderlying);				
			}
			if(result.equals(cancel)){
				return;
			}else if(result.equals(useUnderlying)){
				autoCrop(false);
			}else{
				autoCrop(true);
			}
		}
	}

	private ROIMultiPaintManager.Crop3D getNonZeroBoundingBox(boolean bUseROI){
		Rectangle bounding2D = null;
		int lowZ = Integer.MAX_VALUE;
		int highZ = -1;
		
		if(bUseROI){
			int lowX = Integer.MAX_VALUE;
			int lowY = Integer.MAX_VALUE;
			int highX = -1;
			int highY = -1;
			for (int z = 0; z < roiComposite.length; z++) {
				int xyIndex = 0;
				byte[] zSectData = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData();
				for (int y = 0; y < roiComposite[0].getHeight(); y++) {
					for (int x = 0; x < roiComposite[0].getWidth(); x++) {
						if(zSectData[xyIndex] != 0){
							lowX = Math.min(lowX, x);
							lowY = Math.min(lowY, y);
							highX = Math.max(highX, x);
							highY = Math.max(highY, y);
							lowZ = Math.min(lowZ, z);;
							highZ = Math.max(highZ, z);
						}
						xyIndex++;
					}
				}
			}
			if(lowX != Integer.MAX_VALUE){
				bounding2D = new Rectangle(lowX,lowY,highX-lowX+1,highY-lowY+1);
			}
		}else{
			UShortImage[] images = imageDataSet.getAllImages();
			for (int z = 0; z < images.length; z++) {
				Rectangle boundingRect = images[z].getNonzeroBoundingBox();
				if(boundingRect != null){
					lowZ = Math.min(lowZ, z);;
					highZ = Math.max(highZ, z);
					if(bounding2D == null){
						bounding2D = boundingRect;
					}else{
						bounding2D = bounding2D.union(boundingRect);
					}
				}
			}
		}
		if(bounding2D == null){
			return null;
		}
		ROIMultiPaintManager.Crop3D bounding3D = new ROIMultiPaintManager.Crop3D();
		bounding3D.setBounds(bounding2D.x, bounding2D.y, lowZ, bounding2D.width, bounding2D.height, highZ-lowZ+1);
		return bounding3D;
	}
	private void autoCrop(boolean bUseROI){
		ROIMultiPaintManager.Crop3D nonZeroBoundingBox3D = getNonZeroBoundingBox(bUseROI);
		if(nonZeroBoundingBox3D != null){
			boolean isAutoCroppable3D = 
				!(nonZeroBoundingBox3D.low.z == 0 && 
						nonZeroBoundingBox3D.depth == imageDataSet.getISize().getZ());
			boolean isAutoCroppable2D = 
				!(nonZeroBoundingBox3D.low.x == 0 && 
						nonZeroBoundingBox3D.low.y == 0 && 
						nonZeroBoundingBox3D.width == imageDataSet.getISize().getX() &&
						nonZeroBoundingBox3D.height == imageDataSet.getISize().getY());
	
			if(isAutoCroppable3D || isAutoCroppable2D){
				
				boolean bIncludeZ = true;
				boolean bIncludeXY = true;
				if(isAutoCroppable3D){
					final String cropOnlyXY = "Crop only XY, not Z";
					final String cropOnlyZ = "Crop only Z, not XY";
					final String cropAll = "Crop all XYZ";
					final String cancel = "Cancel";
					String[] options = new String[] {cropOnlyZ,cancel};
					String defaultOption = cropOnlyZ;
					if(isAutoCroppable2D){
						options = new String[] {cropAll,cropOnlyXY,cropOnlyZ,cancel};
						defaultOption = cropAll;
					}
					int lowZlower = (nonZeroBoundingBox3D.low.z != 0
							?(B_DISPLAY_ZERO_INDEX_Z
								?0
								:1)
							:-1);
					int lowZupper = (nonZeroBoundingBox3D.low.z != 0
							?(B_DISPLAY_ZERO_INDEX_Z
								?nonZeroBoundingBox3D.low.z-1
								:nonZeroBoundingBox3D.low.z-1+1)
							:-1);
					int highZlower = ((nonZeroBoundingBox3D.low.z+nonZeroBoundingBox3D.depth) != imageDataSet.getISize().getZ()
							?(B_DISPLAY_ZERO_INDEX_Z
								?(nonZeroBoundingBox3D.low.z+nonZeroBoundingBox3D.depth)
								:nonZeroBoundingBox3D.low.z+nonZeroBoundingBox3D.depth+1):
							-1);
					int highZupper = ((nonZeroBoundingBox3D.low.z+nonZeroBoundingBox3D.depth) != imageDataSet.getISize().getZ()
							?(B_DISPLAY_ZERO_INDEX_Z
								?(imageDataSet.getISize().getZ()-1)
								:imageDataSet.getISize().getZ()-1+1)
							:-1);
					String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI, 
							"Auto-crop using "+(bUseROI?"ROIs":"underlying image")+" has detected empty Z Sections from"+
							(lowZlower != -1?" "+lowZlower+" to "+lowZupper:"")+
							(highZlower != -1?(lowZlower != -1?" and ":" ")+highZlower+" to "+highZupper:"")+
							(defaultOption == cropOnlyZ?"\nThere are no empty XY border pixels.":"")+
							"\nDo you want to include the empty Z-sections in the crop?",
							options, defaultOption);
					if(result.equals(cancel)){
						return;
					}else if(result.equals(cropOnlyZ)){
						bIncludeXY = false;
					}else if(result.equals(cropOnlyXY)){
						bIncludeZ = false;
					}
				}
				if(isAutoCroppable2D && bIncludeXY){
					Rectangle crop2D =  new Rectangle();
					crop2D.setBounds(nonZeroBoundingBox3D.low.x, nonZeroBoundingBox3D.low.y, nonZeroBoundingBox3D.width, nonZeroBoundingBox3D.height);
					if(!overlayEditorPanelJAI.cropDrawAndConfirm(crop2D)){
						return;
					}
				}
				if(!bIncludeZ){
					nonZeroBoundingBox3D.low.z = 0;
					nonZeroBoundingBox3D.depth = imageDataSet.getISize().getZ();
				}
				if(!bIncludeXY){
					nonZeroBoundingBox3D.low.x = 0;
					nonZeroBoundingBox3D.low.y = 0;
					nonZeroBoundingBox3D.width = imageDataSet.getISize().getX();
					nonZeroBoundingBox3D.height = imageDataSet.getISize().getY();
				}
				cropROIData(nonZeroBoundingBox3D,true);
			}else{
				DialogUtils.showWarningDialog(overlayEditorPanelJAI, "No non-zero bounding border in the "+(bUseROI?"user ROI":"underlay image")+" was found to auto-crop.  Use manual crop tool.");
				return;
			}
		}else{
			DialogUtils.showWarningDialog(overlayEditorPanelJAI, "All pixels in the "+(bUseROI?"user ROI":"underlay image")+" are background, auto-crop ignored.  Use manual crop tool.");
			return;
		}
	}
	public static class PaddedInfo {
		public Object paddedArray;
		public ISize paddedISize;
	}
	public static PaddedInfo copyToPadded(
			Object origArr,ISize origISize,Origin origOrigin,Extent origExtent,
			boolean bXYChanged,boolean bZChanged){
		
		int newSizeX = origISize.getX();;
		int newSizeY = origISize.getY();
		if(bXYChanged){
			newSizeX = (origISize.getX()+2);
			newSizeY = (origISize.getY()>1?origISize.getY()+2:origISize.getY());
		}
		int newSizeZ =  origISize.getZ();
		if(bZChanged){
			newSizeZ =  (origISize.getZ()>1?origISize.getZ()+2:origISize.getZ());
		}

		Object newArr = Array.newInstance(origArr.getClass().getComponentType(), newSizeX*newSizeY*newSizeZ);
		//pad shortData
		Object allZSections = origArr;
		int origXYSize =  origISize.getX()*origISize.getY();
		Object currZSection = Array.newInstance(origArr.getClass().getComponentType(),origXYSize);
		for (int z = 0; z < origISize.getZ(); z++) {
			System.arraycopy(allZSections, origXYSize*z, currZSection, 0, origXYSize);
			Object paddedCurrZSection = null;
			if(bXYChanged){
				if(origArr instanceof short[]){
					paddedCurrZSection = ImageTools.padImage((short[])currZSection, false, 1, origISize.getX(), newSizeX, newSizeY);
				}else if(origArr instanceof byte[]){
					paddedCurrZSection = ImageTools.padImage((byte[])currZSection, false, 1, origISize.getX(), newSizeX, newSizeY);
				}else{
					throw new IllegalArgumentException(origArr.getClass().getName() +"not implement for 'copyToPadded'");
				}
			}else{
				paddedCurrZSection = currZSection;
			}
			if(bZChanged){
				System.arraycopy(paddedCurrZSection, 0, newArr, (z+1)*newSizeX*newSizeY, newSizeX*newSizeY);
			}else{
				System.arraycopy(paddedCurrZSection, 0, newArr, (z)*newSizeX*newSizeY, newSizeX*newSizeY);
			}
		}
		
		ROIMultiPaintManager.PaddedInfo paddedInfo = new ROIMultiPaintManager.PaddedInfo();
		paddedInfo.paddedArray = newArr;
		paddedInfo.paddedISize = new ISize(newSizeX, newSizeY, newSizeZ);
		return paddedInfo;
	}
	public static class Crop3D {
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return (low==null?super.toString():low.x+","+low.y+","+low.z+" ["+width+" "+height+" "+depth+"]");
		}
		public CoordinateIndex low  = new CoordinateIndex();
		public int width;
		public int height;
		public int depth;
		public void setBounds(ROIMultiPaintManager.Crop3D crop3D){
			low.x = crop3D.low.x;
			low.y = crop3D.low.y;
			low.z = crop3D.low.z;
			this.width = crop3D.width;
			this.height = crop3D.height;
			this.depth = crop3D.depth;
		}

		public void setBounds(int x,int y,int z,int width,int height,int depth){
			low.x = x;
			low.y = y;
			low.z = z;
			this.width = width;
			this.height = height;
			this.depth = depth;
		}
		public boolean bXYBigger(int origWidth,int origHeight){
			return
			(low.x < 0 ||
			low.y < 0 ||
			(low.x+width) > origWidth ||
			(low.y+height) > origHeight);
		}
		public boolean bZBigger(int origDepth){
			return
			(low.z < 0 || (low.z+depth) > origDepth);
		}
		public boolean bXYSmaller(int origWidth,int origHeight){
			return
			(low.x > 0 ||
			low.y > 0 ||
			(low.x+width) < origWidth ||
			(low.y+height) < origHeight);
		}
		public boolean bZSmaller(int origDepth){
			return
			(low.z > 0 || (low.z+depth) < origDepth);
		}

	}
	public static void  fixBorderProblemInPlace(
			FieldDataFileOperationSpec origFDFOS,VCImage previousVCImage,Crop3D previousCrop3D) throws Exception{

		//
		//this method fixes the original dataset (makes it larger) in case we added a blank border around an uncropped ROI dataset
		//
		
		boolean bXYBigger = true;
		boolean bZBigger = true;
		if(previousCrop3D == null ||
			!previousCrop3D.bXYBigger(origFDFOS.isize.getX(), origFDFOS.isize.getY())){
			bXYBigger = false;
		}
		
		if(previousCrop3D == null ||
			!previousCrop3D.bZBigger(origFDFOS.isize.getZ())){
			bZBigger = false;
		}
		if(!bXYBigger && !bZBigger){
			return;
		}
		
			ROIMultiPaintManager.PaddedInfo paddedInfo =
				copyToPadded(
						origFDFOS.shortSpecData[0][0],
						origFDFOS.isize,origFDFOS.origin,origFDFOS.extent,
						bXYBigger, bZBigger);

			origFDFOS.shortSpecData = new short[][][] {{(short[])paddedInfo.paddedArray}};
			origFDFOS.isize = paddedInfo.paddedISize;
			
			//Reset crop to match
			if(bXYBigger){
				previousCrop3D.low.x+= 1;
				previousCrop3D.low.y+= 1;
				previousCrop3D.width = previousVCImage.getNumX();
				previousCrop3D.height = previousVCImage.getNumY();
			}
			if(bZBigger){
				previousCrop3D.low.z+= 1;
				previousCrop3D.depth = previousVCImage.getNumZ();
			}
	}
	
	private void cropROIData(final Crop3D cropRectangle3D,boolean bThread){
		final String CROP_HIGHLIGHT_KEY = "CROP_HIGHLIGHT_KEY";
		final AsynchClientTask cropTask = new AsynchClientTask("cropTask",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ISize origSize = imageDataSet.getISize();
				ROI croppedHighlightROI = (overlayEditorPanelJAI != null?overlayEditorPanelJAI.getROI():null);
				//
				//Crop 2D
				//
				if(cropRectangle3D.bXYSmaller(origSize.getX(), origSize.getY())){
					Rectangle cropRectangle =
						new Rectangle(cropRectangle3D.low.x,cropRectangle3D.low.y,cropRectangle3D.width,cropRectangle3D.height);
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
					if(croppedHighlightROI != null){
						croppedHighlightROI = croppedHighlightROI.crop(cropRectangle);
					}
				}
				//
				//Crop3D
				//
				if(cropRectangle3D.bZSmaller(origSize.getZ())){
					UShortImage[] newUnderLayImageArr = new UShortImage[cropRectangle3D.depth];
					UShortImage[] newROIHighlightArr = null;
					if(croppedHighlightROI != null){
						newROIHighlightArr = new UShortImage[cropRectangle3D.depth];
					}
					BufferedImage[] newROICompositeArr =  new BufferedImage[cropRectangle3D.depth];
					int index = 0;
					for (int i = 0; i < origSize.getZ(); i++) {
						if(i >= cropRectangle3D.low.z && i < (cropRectangle3D.low.z + cropRectangle3D.depth)){
							newUnderLayImageArr[index] = imageDataSet.getAllImages()[i];
							newROICompositeArr[index] = roiComposite[i];
							if(newROIHighlightArr != null){
								newROIHighlightArr[index] = overlayEditorPanelJAI.getROI().getRoiImages()[i];
							}
							index+=1;
						}
					}
					imageDataSet = new ImageDataset(newUnderLayImageArr, null, cropRectangle3D.depth);
					if(newROIHighlightArr != null){
						croppedHighlightROI = new ROI(newROIHighlightArr, croppedHighlightROI.getROIName());
						hashTable.put(CROP_HIGHLIGHT_KEY, croppedHighlightROI);
					}
					roiComposite = newROICompositeArr;

				}
				mergedCrop3D.setBounds(
						mergedCrop3D.low.x+cropRectangle3D.low.x,
						mergedCrop3D.low.y+cropRectangle3D.low.y,
						mergedCrop3D.low.z+cropRectangle3D.low.z,
						cropRectangle3D.width,cropRectangle3D.height,cropRectangle3D.depth);
			}
		};
		final AsynchClientTask updatePanelTask = new AsynchClientTask("updatePanelTask",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				//Update display with cropped images
				if(overlayEditorPanelJAI != null){
					int currentContrast = overlayEditorPanelJAI.getDisplayContrastFactor();
					overlayEditorPanelJAI.setROI(null);
					overlayEditorPanelJAI.setAllROICompositeImage(null);
					overlayEditorPanelJAI.setImages(imageDataSet, true,
							OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR, OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR);
					overlayEditorPanelJAI.setAllROICompositeImage(roiComposite);
					overlayEditorPanelJAI.setROI((ROI)hashTable.get(CROP_HIGHLIGHT_KEY));
					overlayEditorPanelJAI.setDisplayContrastFactor(currentContrast);
				}
			}
		};
		final Hashtable<String, Object> taskHash = new Hashtable<String, Object>();
		if(bThread){
			ClientTaskDispatcher.dispatch(overlayEditorPanelJAI,taskHash,
				new AsynchClientTask[] {cropTask,updatePanelTask},false);
		}else{
			new Thread(new Runnable() {
				public void run(){
					try{
						cropTask.run(taskHash);
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								try {
									updatePanelTask.run(taskHash);
								} catch (Exception e) {
									e.printStackTrace();
									throw new RuntimeException(e);
								}
							}
						});
					}catch(Exception e){
						e.printStackTrace();
						DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Crop failed:\n"+e.getMessage()+
								(e.getCause()!= null?"\n"+e.getCause().getMessage():""));
					}
				}
			}).run();
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
					newROIName = PopupGenerator.showInputDialog0(overlayEditorPanelJAI, "New ROI Name", "cell");
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
			wantBlendSetToEnhance();
			ROI finalROI = overlayEditorPanelJAI.showAssistDialog(originalROI,null, false,false);
			
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
	private void clearROI(boolean bAskClear,Color roiColor){
		int roiCount = overlayEditorPanelJAI.getAllCompositeROINamesAndColors().size();
		boolean bHighlightOnly = false;
		if(bAskClear/* && roiCount > 1*/){
			final String clearAll = "Clear all ROIs";
			final String clearCurrentOnly = "Clear current ROI";
			final String clearHighlight = "Clear current ROI under highlight";
			final String cancel = "Cancel";
			Vector<String> optionListV = new Vector<String>();// String[] {clearCurrentOnly,clearAll,cancel};
			optionListV.add(clearCurrentOnly);
			StringBuffer sb = new StringBuffer();
			sb.append("ROI will be set to background (cleared). Choose action:\n1. Clear current ROI.\n");
			if(roiCount > 1){
				optionListV.add(clearAll);
				sb.append("2. Clear all roiS.");
			}
			if(overlayEditorPanelJAI.getROI() != null){
				optionListV.add(clearHighlight);
				sb.append((roiCount>1?"3. ":"2. ")+"Clear only the highlighted region in the current ROI.");
			}
			optionListV.add(cancel);
			String result = DialogUtils.showWarningDialog(
					JOptionPane.getRootFrame(),
					"Choose action:\n"+
					sb.toString(),
					optionListV.toArray(new String[0]),
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
			}else if(result.equals(clearHighlight)){
				bHighlightOnly = true;
			}
		}

		int roiColorIndex = -1;
		for (int i = 0; i < OverlayEditorPanelJAI.CONTRAST_COLORS.length; i++) {
			if(OverlayEditorPanelJAI.CONTRAST_COLORS[i].equals(roiColor)){
				roiColorIndex = i;
				break;
			}
		}
		for (int z = 0; z < roiComposite.length; z++) {
			byte[] roiData = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData();
			for (int xy = 0; xy < roiData.length; xy++) {
				if((roiData[xy]&0x000000FF) == roiColorIndex){
					if(bHighlightOnly){
						if(overlayEditorPanelJAI.getROI().getRoiImages()[z].getPixels()[xy]==0){
							continue;
						}
					}
					roiData[xy] = 0;
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
					final String clear = "Clear region check";
					final String cancel = "Cancel";
					String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI,
							"A Check region highlight currently exists.",
							new String[] {recheck,/*fixCheck,*/clear,cancel},
							recheck);
					if(result.equals(recheck)){
						overlayEditorPanelJAI.setROI(null);
					}else if(result.equals(clear)){
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
					progressWait("Check Regions", "Finding distinct regions...",true);
					VCImage checkImage = ROIMultiPaintManager.createVCImageFromBufferedImages(imageDataSet.getExtent(), roiComposite);
					RegionImage regionImage =
						new RegionImage(checkImage, 0 /*0 means generate no surfacecollection*/,
								checkImage.getExtent(),imageDataSet.getAllImages()[0].getOrigin(), RegionImage.NO_SMOOTHING,
								progressWaitPopup);
					if(progressWaitPopup.isInterrupted()){
						throw UserCancelException.CANCEL_GENERIC;
					}
					RegionImage.RegionInfo[] allRegionInfos = regionImage.getRegionInfos();
										
					//Assumes allRegionInfos array position == regionInfo.regionIndex
					progressWait(null, "Generating neighbor info...",false);
					final HighlightROIInfo highlightROIInfo =
						generateHighlightROIInfo(regionImage,
								RegionAction.createCheckNeighborsOnlyRegionAction(allRegionInfos));			
					
					if(highlightROIInfo.neighborsForRegionsMap.size() != allRegionInfos.length ||
							highlightROIInfo.coordIndexForRegionsMap.size() != allRegionInfos.length){
						throw new Exception("generateHighlightROIInfo returned different region count than allRegions");
					}
					//sort region/neighbors list for display
					TreeMap<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsTreeMap =
					new TreeMap<RegionImage.RegionInfo,TreeSet<Integer>>(new Comparator<RegionImage.RegionInfo>() {
						public int compare(RegionImage.RegionInfo o1, RegionImage.RegionInfo o2) {
							int retVal = 0;
							if(o1.getPixelValue() == o2.getPixelValue()){
								if(o1.getNumPixels() == o2.getNumPixels()){
									CoordinateIndex o1CI = highlightROIInfo.coordIndexForRegionsMap.get(o1);
									CoordinateIndex o2CI = highlightROIInfo.coordIndexForRegionsMap.get(o2);
									return compareCoordinateIndex(o1CI, o2CI);
								}else{
									retVal = -(o1.getNumPixels() - o2.getNumPixels());
								}
							}else{
								retVal =  o1.getPixelValue() - o2.getPixelValue();
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
							colROIName.add(getRoiNameFromPixelValue((int)(sortedRegionInfoArr[i].getPixelValue())));
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
					progressWait(STOP_PROGRESS, STOP_PROGRESS,false);
					//Show list
					class DisplayCoordIndex{
						private CoordinateIndex coordIndex;
						public DisplayCoordIndex(CoordinateIndex coordIndex){
							this.coordIndex = coordIndex;
						}
						@Override
						public String toString() {
							// TODO Auto-generated method stub
							return "X="+coordIndex.x+" Y="+coordIndex.y+" Z="+(coordIndex.z+(B_DISPLAY_ZERO_INDEX_Z?0:1));
						}
						public CoordinateIndex getCoordiCoordinateIndex(){
							return coordIndex;
						}
					}
					Object[][] rowData = new Object[colROIName.size()][4];
					for (int i = 0; i < rowData.length; i++) {
						rowData[i][0] = colROIName.elementAt(i);
						rowData[i][1] = colRegionInfo.elementAt(i).getNumPixels();
						rowData[i][2] = new DisplayCoordIndex(highlightROIInfo.coordIndexForRegionsMap.get(sortedRegionInfoArr[i]));
						rowData[i][3] = colNeighbors.elementAt(i);
					}
					
					final String highlightSelectedRegions = "Highlight selected in display";
					final String mergeWithNeighbor = "Merge selected with neighbor";
					final String cancel = "Cancel";
					DialogUtils.TableListResult tableListResult = DialogUtils.showComponentOptionsTableList(
						overlayEditorPanelJAI,
						"Select 1 or more regions and choose an action (click column to sort)",
						new String[] {"Region part of ROI","Region size (pixels)","Starts at (x,y,z)","Touches ROIs"},
						rowData,
						ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,
						null,
						new String[] {highlightSelectedRegions,mergeWithNeighbor,cancel},highlightSelectedRegions,
						new Comparator<Object>() {
							public int compare(Object o1, Object o2) {
								if(o1 instanceof DisplayCoordIndex){
									return compareCoordinateIndex(
											((DisplayCoordIndex)o1).getCoordiCoordinateIndex(),
											((DisplayCoordIndex)o2).getCoordiCoordinateIndex());
								}else if(o1 instanceof Integer){
									return ((Integer) o1).compareTo((Integer)o2);
								}
								return ((String)o1).compareToIgnoreCase((String)o2);
							}
						}
					);

					if(tableListResult.selectedOption.equals(cancel) ||
							tableListResult.selectedTableRows == null ||
							tableListResult.selectedTableRows.length == 0){
						throw UserCancelException.CANCEL_GENERIC;
					}
					
					Vector<RegionImage.RegionInfo> selectedRegionsV = new Vector<RegionImage.RegionInfo>();
					for (int i = 0; i < tableListResult.selectedTableRows.length; i++) {
						selectedRegionsV.add(colRegionInfo.elementAt(tableListResult.selectedTableRows[i]));
					}
					if(tableListResult.selectedOption.equals(highlightSelectedRegions)){
						progressWait(null, "Generating highlight info...",false);
						HighlightROIInfo highlightROIInfo1 = generateHighlightROIInfo(regionImage,
								RegionAction.createHighlightRegionAction(allRegionInfos, selectedRegionsV));
						hashTable.put("highlightROI", highlightROIInfo1.highlightROI);				
					}else if(tableListResult.selectedOption.equals(mergeWithNeighbor)){
						boolean bLeaveMultiNeighborUnchanged = true;
						for (int i = 0; i < selectedRegionsV.size(); i++) {
							if(highlightROIInfo.neighborsForRegionsMap.get(selectedRegionsV.elementAt(i)).size() > 1){
								final String skip = "Leave multi unchanged";
								final String pickAny = "Merge multi with default";
								String result = 
									DialogUtils.showWarningDialog(overlayEditorPanelJAI,
											"Some selected regions have more than 1 neighbor.\n"+
											"Choose an action:\n"+
											"1. Leave multi-neighbor regions unchanged while merging.\n"+
											"2. Merge multi-neighbor regions with default neighbor.",
											new String[] {skip,pickAny,cancel}, cancel);
								if(result.equals(cancel)){
									throw UserCancelException.CANCEL_GENERIC;
								}else if(result.equals(pickAny)){
									bLeaveMultiNeighborUnchanged = false;
								}
								break;
							}
						}
						generateHighlightROIInfo(regionImage,
								RegionAction.createMergeSelectedWithNeighborsRegionAction(
										allRegionInfos,
										selectedRegionsV,
										highlightROIInfo.neighborsForRegionsMap,
										bLeaveMultiNeighborUnchanged));
					}
				}finally{
					progressWait(STOP_PROGRESS, STOP_PROGRESS,false);
				}
			}//run
		};//task
		
		AsynchClientTask updateROITaks = new AsynchClientTask("Updating ROI",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ROI highlightROI = (ROI)hashTable.get("highlightROI");
				overlayEditorPanelJAI.getRoiSouceData().addReplaceRoi(highlightROI);
				if(highlightROI != null){
					wantBlendSetToEnhance();
				}
//				if(highlightROI != null && overlayEditorPanelJAI.getBlendPercent() > 20){
//					final String setSelector = "Yes, Enhance roi region display";
//					final String leaveAsIs = "No, Leave 'blend' unchanged";
//					String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI, 
//							"Do you want to set the 'blend' value (found near the 'Check...' button)"+
//							" to enhance the display of the selected ROI regions?\n"+
//							"Note: Lower values highlight the chosen ROI regions more.",
//							new String[] {setSelector,leaveAsIs}, setSelector);
//					if(result.equals(setSelector)){
//						overlayEditorPanelJAI.setBlendPercent(20);
//					}
//				}
			}
		};

		ClientTaskDispatcher.dispatch(
				overlayEditorPanelJAI,
				new Hashtable<String, Object>(),
				new AsynchClientTask[] {warnExistingCheck,regionTask,updateROITaks},
				true,
				false,
				false,
				null,
				true);
	}
	
	private static final int BLEND_ENHANCE_THRESHOLD = 20;
	private void wantBlendSetToEnhance(){
		if(overlayEditorPanelJAI.getBlendPercent() > BLEND_ENHANCE_THRESHOLD){
			final String setSelector = "Yes, Enhance roi region display";
			final String leaveAsIs = "No, Leave 'blend' unchanged";
			String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI, 
					"Do you want to set the ROI/Underlay 'blend' slider"+
					" to enhance the display of highlighted ROI regions?\n"+
					"Note: Moving the slider left highlights the chosen ROI regions more."+
					"  Moving the slider right enhances the display of the underlying image.",
					new String[] {setSelector,leaveAsIs}, setSelector);
			if(result.equals(setSelector)){
				overlayEditorPanelJAI.setBlendPercent(BLEND_ENHANCE_THRESHOLD);
//				return true;
			}
		}
//		return false;
	}
	private int compareCoordinateIndex(CoordinateIndex o1CI,CoordinateIndex o2CI){
		if(o1CI.z != o2CI.z){
			return o1CI.z - o2CI.z;
		}else if(o1CI.y != o2CI.y){
			return o1CI.y - o2CI.y;
		}else{
			return o1CI.x - o2CI.x;
		}

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
		private boolean bLeaveMultiNeighborUnchanged = true;
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
				Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsMap,
				boolean bLeaveMultiNeighborUnchanged){
			RegionAction regionAction = new RegionAction();
			regionAction.allRegionsInfosSize = allRegionInfos.length;
			regionAction.selectedRegionsV = selectedRegionsV;
			regionAction.neighborsForRegionsMap = neighborsForRegionsMap;
			regionAction.action = REGION_ACTION_MERGESELECTEDWITHNEIGHBORS;
			regionAction.bLeaveMultiNeighborUnchanged = bLeaveMultiNeighborUnchanged;
			return regionAction;
		}

	}
	private static class HighlightROIInfo{
		public ROI highlightROI;
		public Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsMap =
			new Hashtable<RegionInfo, TreeSet<Integer>>();
		public Hashtable<RegionImage.RegionInfo,CoordinateIndex> coordIndexForRegionsMap =
			new Hashtable<RegionInfo, CoordinateIndex>();
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
							DEFAULT_ORIGIN,DEFAULT_EXTENT,
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
								neighbors[0] = 0x000000FF&((DataBufferByte)roiComposite[z-1].getRaster().getDataBuffer()).getData()[index];
							}
							if(z<ZMAX){//bottom neighbor
								neighbors[1] = 0x000000FF&((DataBufferByte)roiComposite[z+1].getRaster().getDataBuffer()).getData()[index];
							}
							if(x>0){//left neighbor
								neighbors[2] = 0x000000FF&((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index-1];
							}
							if(x<XMAX){//right neighbor
								neighbors[3] = 0x000000FF&((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index+1];
							}
							if(y>0){//front neighbor
								neighbors[4] = 0x000000FF&((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index-XSIZE];
							}
							if(y<YMAX){//back neighbor
								neighbors[5] = 0x000000FF&((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[index+XSIZE];
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
							if(!highlightROIInfo.coordIndexForRegionsMap.containsKey(selectedRegionMap[regionIndex])){
								highlightROIInfo.coordIndexForRegionsMap.put(selectedRegionMap[regionIndex],new CoordinateIndex(x,y,z));
							}
						}else if(regionAction.getAction() == RegionAction.REGION_ACTION_HIGHLIGHT){
							highlightROIInfo.highlightROI.getRoiImages()[z].getPixels()[index] = 1;
						}else if(regionAction.getAction() == RegionAction.REGION_ACTION_MERGESELECTEDWITHNEIGHBORS){
							if(!regionAction.bLeaveMultiNeighborUnchanged || regionAction.getNeighborsForRegionMap().get(selectedRegionMap[regionIndex]).size()==1){
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
	private static final String STOP_PROGRESS = "STOP_PROGRESS";
	private void progressWait(String title,String message,boolean bCancelable){
		if(STOP_PROGRESS.equals(title) || STOP_PROGRESS.equals(message)){
			if(progressWaitPopup != null){
				progressWaitPopup.stop();
				progressWaitPopup = null;
			}
			return;
		}
		if(progressWaitPopup == null){
			if(bCancelable){
				progressWaitPopup = new AsynchProgressPopup(overlayEditorPanelJAI,
					(title==null?"Wait...":title), message, new Thread(), true, false,
					true,null);
			}else{
				progressWaitPopup = new AsynchProgressPopup(overlayEditorPanelJAI,
						(title==null?"Wait...":title), message, null, true, false);
			}
			progressWaitPopup.startKeepOnTop();
		}
		progressWaitPopup.setMessage(message);
	}
	private void updateExtent(VCImage updateThisVCImage,Extent origExtent,ISize origIsISize){
		updateThisVCImage.setExtent(
			new Extent(
				updateThisVCImage.getNumX()*origExtent.getX()/origIsISize.getX(),
				updateThisVCImage.getNumY()*origExtent.getY()/origIsISize.getY(),
				updateThisVCImage.getNumZ()*origExtent.getZ()/origIsISize.getZ())
		);
	}}
