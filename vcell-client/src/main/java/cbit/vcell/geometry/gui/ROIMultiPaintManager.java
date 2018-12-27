/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.io.FilenameUtils;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.UtilCancelException;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageSizeInfo;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.VirtualMicroscopy.Image.ImageStatistics;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.surface.BoundingBox;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.OrigSurface;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.render.Vect3d;
import cbit.vcell.resource.OperatingSystemInfo;

public class ROIMultiPaintManager implements PropertyChangeListener{

	public static final int ENHANCE_NONE = 0;

	private static final String RESERVED_NAME_BACKGROUND = "background";
	private static final boolean B_DISPLAY_ZERO_INDEX_Z = false;
	
	private OverlayEditorPanelJAI overlayEditorPanelJAI;
	private BufferedImage[] roiComposite;
	private static IndexColorModel indexColorModel;
	private ImageDataset[] initImageDataSetChannels;
	private int imageDatasetChannel = 0;
	private ImageDataset[] enhancedImageDatasetChannels;
	private int enhanceImageAmount = ROIMultiPaintManager.ENHANCE_NONE;
	private boolean bHasOriginalData;
	private Extent originalExtent;
	private Origin originalOrigin;
	private ISize originalISize;
	private String originalAnnotation;
	private GeometryAttributes editedGeometryAttributes;
	private OverlayEditorPanelJAI.AllPixelValuesRange[] allPixelValuesRangeChannels;
	private TreeMap<Integer, Integer>[] condensedBinsMapChannels;
	
	private static final Extent DEFAULT_EXTENT = new Extent(1,1,1);
	private static final Origin DEFAULT_ORIGIN = new Origin(0,0,0);

	private AsynchProgressPopup progressWaitPopup;
	
	public static class SelectImgInfo{
		private MouseEvent mouseEvent;
//		private double zoom;
		private JList resolvedList;
		private Rectangle selectionRectangle;
		boolean bIgnoreIfSelected;
		public SelectImgInfo(MouseEvent mouseEvent,/*MouseEvent mouseEvent, double zoom,*/JList resolvedList,Rectangle selectionRectangle,boolean bIgnoreIfSelected) {
			super();
			this.mouseEvent = mouseEvent;
//			this.zoom = zoom;
			this.resolvedList = resolvedList;
			this.selectionRectangle = selectionRectangle;
			this.bIgnoreIfSelected = bIgnoreIfSelected;
		}
		public MouseEvent getMouseEvent() {
			return mouseEvent;
		}
//		public double getZoom() {
//			return zoom;
//		}
		public JList getResolvedList(){
			return resolvedList;
		}
		public Rectangle getRectangle(){
			return selectionRectangle;
		}
		public boolean isIgnoreIfSelected(){
			return bIgnoreIfSelected;
		}
	}
	public static class BoxBlurFilter{
		//This code originated from
		//http://www.jhlabs.com/ip/blurring.html
		public static int clamp(int x, int a, int b) {
			return (x < a) ? a : (x > b) ? b : x;
		}
//		public static int[] createDivideTable(int radius){
//	        int tableSize = 2*radius+1;
//	        int divide[] = new int[256*256*tableSize];
//	        for ( int i = 0; i < 256*256*tableSize; i++ ){
//	            divide[i] = i/tableSize;
//	        }
//	        return divide;
//		}
	    public static void blur( short[] ins, short[] outs, int width, int height, int radius/*,int[] divideTable*/) {
	    	int divisor = (2*radius+1);
	        int widthMinus1 = width-1;

	        int inIndex = 0;
	        
	        for ( int y = 0; y < height; y++ ) {
	            int outIndex = y;

	            int shortSum = 0;
	            for ( int i = -radius; i <= radius; i++ ) {
	            	shortSum+= ins[inIndex + clamp(i, 0, width-1)]&0x0000FFFF;
	            }

	            for ( int x = 0; x < width; x++ ) {
	                outs[ outIndex ] = (short)(shortSum/divisor);//(short)divideTable[shortSum];

	                int i1 = x+radius+1;
	                if ( i1 > widthMinus1 )
	                    i1 = widthMinus1;
	                int i2 = x-radius;
	                if ( i2 < 0 )
	                    i2 = 0;
	                int val1 = ins[inIndex+i1]&0x0000FFFF;
	                int val2 = ins[inIndex+i2]&0x0000FFFF;
	                
	                shortSum+= (val1-val2);
	                outIndex += height;
	            }
	            inIndex += width;
	        }
	    }
	}

	
	
	
	public static class ComboboxROIName {
		private String roiName;
		private boolean bNameEdit;
		private boolean bDeleteable;
		private int contrastColorIndex;
		public ComboboxROIName(String roiName,boolean bNameEdit,boolean bDeleteable,int contrastColorIndex){
			this.roiName = roiName;
			this.bNameEdit = bNameEdit;
			this.bDeleteable = bDeleteable;
			this.contrastColorIndex = contrastColorIndex;
		}
		public String getROIName(){
			return roiName;
		}
		public boolean isNameEditable(){
			return bNameEdit;
		}
		public Color getHighlightColor(){
			return OverlayEditorPanelJAI.CONTRAST_COLORS[contrastColorIndex];
		}
		public boolean isDeleteable(){
			return bDeleteable;
		}
		public String toString(){
			return getROIName();
		}
		public int getContrastColorIndex(){
			return contrastColorIndex;
		}
	}

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
	
	private void askInitialize(boolean bForceAddDistinct){

		final TreeSet<Integer> sortedPixVal = new TreeSet<Integer>();
		BitSet uniquePixelBS = new BitSet((int)Math.pow(2, Short.SIZE));
		for (int i = 0; i < getImageDataSetChannel().getAllImages().length; i++) {
			short[] dataToSegment = getImageDataSetChannel().getAllImages()[i].getPixels();
			for (int j = 0; j < dataToSegment.length; j++) {
				if((int)(dataToSegment[j]&0x0000FFFF) != 0){
					if(!uniquePixelBS.get((int)(dataToSegment[j]&0x0000FFFF))){
						sortedPixVal.add((int)(dataToSegment[j]&0x0000FFFF));
					}
					uniquePixelBS.set((int)(dataToSegment[j]&0x0000FFFF));
				}
			}
		}

		boolean bNotAddDomains = uniquePixelBS.cardinality() >= 256/*too many to add*/ || !bHasOriginalData/*no import*/ || uniquePixelBS.cardinality()==0/*blank, all pixels 0*/;
		final String addROIManual = "1. Add empty Domain";
		final String addAllDistinct = "2. Assume Pre-Segmented";
		final String cancel = "Cancel";
		String result = null;
		String distinctDescr =
			"The current image contains "+uniquePixelBS.cardinality()+" distinct non-zero pixel values.";
		if(!bForceAddDistinct){
			result = DialogUtils.showWarningDialog(overlayEditorPanelJAI, "Image Editor",
				distinctDescr+
				"  Segmenting an image begins with defining Domain(s) manually or automatically."+
				"  Editing tools are used to create/edit more Domains.  Choose an action:\n"+
				"1. Add an 'empty' Domain to begin segmenting manually."+
				(bNotAddDomains?"":"\n2. Pre-Segmented (add Domains for every distinct pixel value)."),
				(bNotAddDomains?new String[] {addROIManual,cancel}:new String[] {addROIManual,addAllDistinct,cancel}),
				cancel);
		
			if(result.equals(cancel)){
				return;//throw UserCancelException.CANCEL_GENERIC;
			}
		}else{
			if(uniquePixelBS.cardinality() == 0){
				DialogUtils.showWarningDialog(overlayEditorPanelJAI, 
						"Underlay contains no non-zero pixel values available for Domain assignment.");
				return;
			}
			boolean bHasExistingROIs =
				overlayEditorPanelJAI.getAllCompositeROINamesAndColors() != null &&
				overlayEditorPanelJAI.getAllCompositeROINamesAndColors().length > 0;
			result = DialogUtils.showWarningDialog(overlayEditorPanelJAI,
					(bHasExistingROIs?"Warning: Existing Domains may be overwritten.  ":"")+
					distinctDescr,
					new String[] {addAllDistinct,cancel}, addAllDistinct);

			if(result.equals(cancel)){
				return;//throw UserCancelException.CANCEL_GENERIC;
			}
		}
		try{
			if(result.equals(addAllDistinct)){//try add all distinct, fail if too many regions
				final String LOOKUP_KEY = "LOOKUP_KEY";
				AsynchClientTask createDistinctROI = new AsynchClientTask("Create distinct ROI...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Integer[] uniquePivValArr = sortedPixVal.toArray(new Integer[0]);
						int[] lookup = new int[uniquePivValArr[uniquePivValArr.length-1]+1];
						Arrays.fill(lookup, -1);
						for (int i = 0; i < uniquePivValArr.length; i++) {
							lookup[uniquePivValArr[i]] = i+1;
							overlayEditorPanelJAI.addROIName("roi_"+uniquePivValArr[i], false, "roi_"+uniquePivValArr[0], true, i+1);					
						}
						hashTable.put(LOOKUP_KEY, lookup);
					}
				};
				AsynchClientTask applyDistinctROI = new AsynchClientTask("Apply distinct ROI...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						int[] lookup = (int[])hashTable.get(LOOKUP_KEY);
						for (int i = 0; i < getImageDataSetChannel().getAllImages().length; i++) {
							short[] dataToSegment = getImageDataSetChannel().getAllImages()[i].getPixels();
							byte[] roiBytes = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
							for (int j = 0; j < dataToSegment.length; j++) {
								if((int)(dataToSegment[j]&0x0000FFFF) != 0){
									roiBytes[j] = (byte)lookup[(int)(dataToSegment[j]&0x0000FFFF)];
								}
							}
						}
					}
				};
				AsynchClientTask failTask = new AsynchClientTask("Check fail...",AsynchClientTask.TASKTYPE_SWING_BLOCKING,false,false,true) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Throwable throwable = (Throwable)hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR);
						if(throwable != null){
							//cleanup
							ComboboxROIName[] comboboxROINames = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
							for (int i = 0; i < comboboxROINames.length; i++) {
								overlayEditorPanelJAI.deleteROIName(comboboxROINames[i]);
							}
							for (int i = 0; i < roiComposite.length; i++) {
								byte[] roiBytes = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
								Arrays.fill(roiBytes, (byte)0);
							}
						}
					}
				};
				AsynchClientTask clearHighlightsTask = new AsynchClientTask("Apply distinct ROI...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						overlayEditorPanelJAI.setHighliteInfo(null,OverlayEditorPanelJAI.FRAP_DATA_INIT_PROPERTY);
					}
				};
				ClientTaskDispatcher.dispatch(overlayEditorPanelJAI,
						new Hashtable<String, Object>(),
						new AsynchClientTask[] {createDistinctROI,applyDistinctROI,createRegionImageTask,failTask,clearHighlightsTask,saveSortRegionImageTask,updateList},false,false,null,true);
			}else{
				try {
					addNewROI(overlayEditorPanelJAI.getAllCompositeROINamesAndColors(),null);
				} catch (Exception e) {
					e.printStackTrace();
					DialogUtils.showErrorDialog(overlayEditorPanelJAI, e.getMessage());
				}
			}
		}catch(UserCancelException e){
			//do nothing
		}
	}
	private ImageDataset getImageDataSetChannel(){
		if(enhancedImageDatasetChannels != null){
			return enhancedImageDatasetChannels[imageDatasetChannel];
		}
		return initImageDataSetChannels[imageDatasetChannel];
	}
	private ImageDataset[] getImageDataset(){
		if(enhancedImageDatasetChannels != null){
			return enhancedImageDatasetChannels;
		}
		return initImageDataSetChannels;
	}
	private static OverlayEditorPanelJAI.AllPixelValuesRange[] calculateAllPixelValuesRangeChannels0(ImageDataset[] sourceImageDatasetChannels){
		OverlayEditorPanelJAI.AllPixelValuesRange[] pixelValuesRangeChannels =
			new OverlayEditorPanelJAI.AllPixelValuesRange[sourceImageDatasetChannels.length];
		for (int c = 0; c < sourceImageDatasetChannels.length; c++) {
			UShortImage[] allImages = sourceImageDatasetChannels[c].getAllImages();
			double min = 0;
			double max = min;
			for (int i = 0; i < allImages.length; i++) {
				ImageStatistics imageStats = allImages[i].getImageStatistics();
				if(i==0 || imageStats.minValue < min){min = imageStats.minValue;}
				if(i==0 || imageStats.maxValue > max){max = imageStats.maxValue;}
			}
			pixelValuesRangeChannels[c] = new OverlayEditorPanelJAI.AllPixelValuesRange((int)min, (int)max);
		}
		return pixelValuesRangeChannels;
	}
	public void initROIData(FieldDataFileOperationSpec importedDataContainer) throws Exception{

		originalExtent = importedDataContainer.extent;
		originalOrigin = importedDataContainer.origin;
		originalISize = importedDataContainer.isize;
		bHasOriginalData = importedDataContainer.shortSpecData != null;
		
		enhancedImageDatasetChannels = null;
		enhanceImageAmount = ROIMultiPaintManager.ENHANCE_NONE;
		//
		//previouslyEditedVCImage and previousCrop3D can be null if this is the first time this method
		//has been called in an editing session. 
		//
		initImageDataSet((!bHasOriginalData?null:importedDataContainer.shortSpecData[0]),originalISize);
		initROIComposite();

		allPixelValuesRangeChannels = calculateAllPixelValuesRangeChannels0(getImageDataset());
	}
	
	private class GeometryAttributes {
		public String annotation;
		public Extent extent;
		public Origin origin;
		public int dimension;
	}

//	private void showDataValueSurfaceViewer(GeometryAttributes geomAttr) {
//
////		CartesianMesh mesh = new CartesianMesh();
////		mesh.setOrigin(orig);
////		mesh.setExtent(extent);
////		mesh.setSize(size.getX(), size.getY(), size.getZ());
////		
////		mesh.meshRegionInfo = new MeshRegionInfo();	
////		byte[] compressRegionBytes = BeanUtils.compress(regionImage.getShortEncodedRegionIndexImage());
////		mesh.meshRegionInfo.setCompressedVolumeElementMapVolumeRegion(compressRegionBytes, mesh.getNumVolumeElements());
////		RegionInfo[] regionInfos = regionImage.getRegionInfos();
////		for (int i = 0; i < regionInfos.length; i++) {
////			mesh.meshRegionInfo.mapVolumeRegionToSubvolume(regionInfos[i].getRegionIndex(), regionInfos[i].getPixelValue(), regionInfos[i].getNumPixels());		
////		}
//
//		try{
////		if(fieldDataValueSurfaceViewer == null){
//			progressWait("Surface Viewer", "Calculating surfaces...",true);
//			VCImage checkImage =
//				ROIMultiPaintManager.createVCImageFromBufferedImages(geomAttr.extent, roiComposite);
//			RegionImage regionImage =
//				new RegionImage(checkImage, geomAttr.dimension,
//						checkImage.getExtent(),geomAttr.origin, RegionImage.NO_SMOOTHING,
//						progressWaitPopup);
//			if(progressWaitPopup.isInterrupted()){
//				throw UserCancelException.CANCEL_GENERIC;
//			}
//			
////			ISize croppedISize = new ISize(mergedCrop3D.width, mergedCrop3D.height, mergedCrop3D.depth);
////			//Surfaces
////			CartesianMesh cartesianMesh =
////				CartesianMesh.createSimpleCartesianMesh(geomAttr.origin,
////						geomAttr.extent, croppedISize, regionImage);
////			MeshRegionSurfaces meshRegionSurfaces = new MeshDisplayAdapter(cartesianMesh).generateMeshRegionSurfaces();
////			SurfaceCollection surfaceCollection = meshRegionSurfaces.getSurfaceCollection();
//
//			//SurfaceNames
//			final String[] surfaceNames = new String[regionImage.getSurfacecollection().getSurfaceCount()];
//			for (int i = 0; i < regionImage.getSurfacecollection().getSurfaceCount(); i++){
////				MembraneElement me = //Get the first element, any will do, all have same inside/outside volumeIndex
////					cartesianMesh.getMembraneElements()[meshRegionSurfaces.getMembraneIndexForPolygon(i,0)];
//				surfaceNames[i] = i+"";
////				if(getSimulationModelInfo() != null){
////					surfaceNames[i] = getSimulationModelInfo().getMembraneName(
////						cartesianMesh.getSubVolumeFromVolumeIndex(me.getInsideVolumeIndex()),
////						cartesianMesh.getSubVolumeFromVolumeIndex(me.getOutsideVolumeIndex())
////					);
////				}else{
////					surfaceNames[i] = i+"";
////				}
//			}
//
//			//SurfaceAreas
//			final Double[] surfaceAreas = new Double[regionImage.getSurfacecollection().getSurfaceCount()];
//			for (int i = 0; i < regionImage.getSurfacecollection().getSurfaceCount(); i++){
//				surfaceAreas[i] = 0.0;//new Double(cartesianMesh.getRegionMembraneSurfaceAreaFromMembraneIndex(meshRegionSurfaces.getMembraneIndexForPolygon(i,0)));
//			}
//
//			DataValueSurfaceViewer fieldDataValueSurfaceViewer0 = new DataValueSurfaceViewer();
//
////			TaubinSmoothing taubinSmoothing = new TaubinSmoothingWrong();
////			TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.getInstance(.3);
////			taubinSmoothing.smooth(surfaceCollection,taubinSpec);
//			fieldDataValueSurfaceViewer0.init(
//				regionImage.getSurfacecollection(),
//				geomAttr.origin,
//				geomAttr.extent,
//				surfaceNames,
//				surfaceAreas,
//				geomAttr.dimension
//			);
//			progressWait(STOP_PROGRESS, STOP_PROGRESS,false);
//
//			final JDialog jDialog = new JDialog(JOptionPane.getFrameForComponent(parentComponent));
//			jDialog.setTitle("Surface Viewer");
//			jDialog.setModal(true);
//			jDialog.getContentPane().add(fieldDataValueSurfaceViewer0);
//			jDialog.setSize(500,500);
//			ZEnforcer.showModalDialogOnTop(jDialog, overlayEditorPanelJAI);
////		}
//		}catch(Exception e){
//			progressWait(STOP_PROGRESS, STOP_PROGRESS,false);
//			PopupGenerator.showErrorDialog(overlayEditorPanelJAI, e.getClass().getName()+"\n"+e.getMessage());
//		}finally{
//			progressWait(STOP_PROGRESS, STOP_PROGRESS,false);
//		}
//	}

	private Extent createResizeExtent(Extent prevExtent,ISize prevISize,ISize cropISize){
		return new Extent((prevExtent.getX()/prevISize.getX())*cropISize.getX(),
				(prevExtent.getY()/prevISize.getY())*cropISize.getY(),
				(prevExtent.getZ()/prevISize.getZ())*cropISize.getZ());
	}
	private GeometryAttributes showEditGeometryAttributes(Component parentComponent,GeometryAttributes currentGeometryAttributes) throws UserCancelException{
		final GeometryAttributes[] finalGeometryAttributesHolder = new GeometryAttributes[1];
		final boolean[] cancelHolder = new boolean[] {false};

		final CopyOfImageAttributePanel copyOfImageAttributePanel =
			new CopyOfImageAttributePanel();
		if(currentGeometryAttributes == null){
			copyOfImageAttributePanel.init(originalOrigin, createResizeExtent(originalExtent,originalISize,getImageDataSetChannel().getISize()),
					getImageDataSetChannel().getISize(), originalAnnotation);			
		}else{
			copyOfImageAttributePanel.init(currentGeometryAttributes.origin, currentGeometryAttributes.extent,
					getImageDataSetChannel().getISize(), currentGeometryAttributes.annotation);
		}
		
		final JDialog jDialog = new JDialog(JOptionPane.getFrameForComponent(parentComponent));
		if(OperatingSystemInfo.getInstance().isMac()) {
			jDialog.setAlwaysOnTop(true);
		}
		jDialog.setTitle("Edit Geometry Attributes");
		jDialog.setModal(true);
		
		JPanel okCancelJPanel = new JPanel(new FlowLayout());
		JButton okJButton = new JButton("OK");
		okJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					finalGeometryAttributesHolder[0] = new GeometryAttributes();
					finalGeometryAttributesHolder[0].annotation = copyOfImageAttributePanel.getEditedAnnotation();
					finalGeometryAttributesHolder[0].origin = copyOfImageAttributePanel.getEditedOrigin();
					finalGeometryAttributesHolder[0].extent = copyOfImageAttributePanel.getEditedExtent();
					
					jDialog.dispose();
				}catch(UserCancelException uce){
					
				}catch(Exception exc){
					DialogUtils.showErrorDialog(overlayEditorPanelJAI,
							"Error validating Geometry attributes.\n"+exc.getMessage(), exc);
				}
			}
		});
		JButton cancelJButton = new JButton("Cancel");
		cancelJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelHolder[0] = true;
				jDialog.dispose();
			}
		});
		okCancelJPanel.add(okJButton);
		okCancelJPanel.add(cancelJButton);
		jDialog.getContentPane().add(copyOfImageAttributePanel,BorderLayout.CENTER);
		jDialog.getContentPane().add(okCancelJPanel,BorderLayout.SOUTH);
		jDialog.setSize(500,300);
		DialogUtils.showModalJDialogOnTop(jDialog,overlayEditorPanelJAI);

		if(cancelHolder[0]){
			throw UserCancelException.CANCEL_GENERIC;
		}
		return finalGeometryAttributesHolder[0];
	}
	private void applyPixelClasses(VCPixelClass[] vcPixelClasses,Component parentComponent){
		if(vcPixelClasses != null){
			int backgroundIndex = -1;
			int[] pixelValMapPixelClassIndex = new int[256];
			Arrays.fill(pixelValMapPixelClassIndex, -1);
			String[][] rowData = new String[vcPixelClasses.length][1];
			for (int i = 0; i < vcPixelClasses.length; i++) {
				pixelValMapPixelClassIndex[vcPixelClasses[i].getPixel()] = i;
				rowData[i][0] = vcPixelClasses[i].getPixelClassName();
				if(vcPixelClasses[i].getPixel() == 0 && vcPixelClasses[i].getPixelClassName().equals(RESERVED_NAME_BACKGROUND)){
					//choose background automatically
					backgroundIndex = i;
				}
			}				
			//Create ROIs from VCPixelclasses
			int roiCount = 1;//start 1 after background index color
			int[] pixelClassIndexMaproiIndex = new int[vcPixelClasses.length];
			for (int i = 0; i < vcPixelClasses.length; i++) {
				if(i == backgroundIndex){
					pixelClassIndexMaproiIndex[i] = 0;//background
					continue;
				}
				overlayEditorPanelJAI.addROIName(vcPixelClasses[i].getPixelClassName(), true, vcPixelClasses[0].getPixelClassName(),true,/*true,true,*/roiCount);
				pixelClassIndexMaproiIndex[i] = roiCount;
				roiCount++;
			}
			
			//fill in rois using pixel value and VCPixelClass mappings
			for (int zindex = 0; zindex < roiComposite.length; zindex++) {
				byte[] zdata = ((DataBufferByte)roiComposite[zindex].getRaster().getDataBuffer()).getData();
				UShortImage uShortImage = initImageDataSetChannels[0].getImage(zindex, 0, 0);
				for (int xyindex = 0; xyindex < zdata.length; xyindex++) {
					int pixelval = uShortImage.getPixels()[xyindex] & 0x000000FF;//unsigned short
					zdata[xyindex] = (byte)pixelClassIndexMaproiIndex[pixelValMapPixelClassIndex[pixelval]];
				}
			}
		}
	}
	
	public Geometry showGUI(
			final String okButtonText,
			final String sourceDataName,
			final Component parentComponent,
			String initalAnnotation,
			final VCPixelClass[] vcPixelClasses,
			UserPreferences userPreferences){

		originalAnnotation = initalAnnotation;
		final Geometry[] finalGeometryHolder = new Geometry[1];
		
		if(overlayEditorPanelJAI == null){
			overlayEditorPanelJAI = new OverlayEditorPanelJAI();
			overlayEditorPanelJAI.setCalcCoords(new CalcCoords());
			overlayEditorPanelJAI.setUserPreferences(userPreferences);
			overlayEditorPanelJAI.setMinimumSize(new Dimension(700,600));
			overlayEditorPanelJAI.setPreferredSize(new Dimension(700,600));
			overlayEditorPanelJAI.addPropertyChangeListener(ROIMultiPaintManager.this);
		}
		overlayEditorPanelJAI.deleteROIName(null);//delete all names
		
		applyPixelClasses(vcPixelClasses, parentComponent);//when user selects image "from DB" or "from current geometry"
		
		if(getImageDataset().length > 1){
			String[] channelNames = new String[getImageDataset().length];
			for (int i = 0; i < channelNames.length; i++) {
				channelNames[i] = "channel "+i;
			}
			overlayEditorPanelJAI.setChannelNames(channelNames);		
		}else{
			overlayEditorPanelJAI.setChannelNames(null);
		}
		updateUnderlayHistogramDisplay();
		overlayEditorPanelJAI.setContrastToMinMax();
		overlayEditorPanelJAI.setAllROICompositeImage(roiComposite,OverlayEditorPanelJAI.FRAP_DATA_INIT_PROPERTY);
		final JDialog jDialog = new JDialog(JOptionPane.getFrameForComponent(parentComponent));
		jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		jDialog.setTitle("Geometry Editor ("+sourceDataName+")");
		jDialog.setModal(true);
		
		final JButton cancelJButton = new JButton("Cancel");
		cancelJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String QUIT_ANSWER = "Quit Geometry Editor";
				String result = DialogUtils.showWarningDialog(jDialog, "Confirm cancel","Quit geometry editor and lose all changes?", new String[] {QUIT_ANSWER,"back"}, QUIT_ANSWER);
				if(result != null && result.equals(QUIT_ANSWER)){
					jDialog.dispose();
				}
			}
		});
		jDialog.addWindowListener(
			new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent e) {
					super.windowOpened(e);
					if(bHasOriginalData){
						calculateHistogram();
					}
					updateUndoAfter(false);
					if(vcPixelClasses == null){
						askInitialize(false);
					}
				}

				@Override
				public void windowClosing(WindowEvent e) {
					// TODO Auto-generated method stub
					super.windowClosing(e);
					cancelJButton.doClick();
				}
				
			}
		);
		
		final JPanel okCancelJPanel = new JPanel(new FlowLayout());
		JButton okJButton = new JButton(okButtonText);
		okJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					VCImage finalImage = checkAll();
					finalImage.setDescription((editedGeometryAttributes!=null?editedGeometryAttributes.annotation:originalAnnotation));
					finalGeometryHolder[0] = 
						new Geometry((String)null, finalImage);
					finalGeometryHolder[0].getGeometrySpec().setOrigin((editedGeometryAttributes!=null?editedGeometryAttributes.origin:originalOrigin));
					finalGeometryHolder[0].getGeometrySpec().setExtent((editedGeometryAttributes!=null?editedGeometryAttributes.extent:originalExtent));
					finalGeometryHolder[0].setDescription((editedGeometryAttributes!=null?editedGeometryAttributes.annotation:originalAnnotation));
					jDialog.dispose();
				}catch(UserCancelException uce){
					
				}catch(Exception exc){
					DialogUtils.showErrorDialog(overlayEditorPanelJAI,
							"Error validating compartments.\n"+exc.getMessage(), exc);
				}
			}
		});
		JButton attributesJButton = new JButton("Attributes...");
		attributesJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					editedGeometryAttributes =
						showEditGeometryAttributes(jDialog,editedGeometryAttributes);
				}catch(UserCancelException uce){
					//ignore
				}
			}
		});
//		JButton surfaceButton = new JButton("View Surfaces...");
//		surfaceButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				showDataValueSurfaceViewer(geomAttr);
//			}
//		});
		
		
		JButton exportJButton = new JButton("Export...");
		exportJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		
		JButton importJButton = new JButton("Import stl...");
		importJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser importJFC = new JFileChooser(ClientRequestManager.getPreferredPath(UserPreferences.getLastUserPreferences()));
				importJFC.setDialogTitle("Choose .stl file(s) to import");
				importJFC.setMultiSelectionEnabled(true);
				int result = importJFC.showOpenDialog(overlayEditorPanelJAI);
				if(result == JFileChooser.APPROVE_OPTION){
					File[] selectedFiles = importJFC.getSelectedFiles();
					if(selectedFiles != null && selectedFiles.length > 0){
						ClientRequestManager.setPreferredPath(UserPreferences.getLastUserPreferences(), selectedFiles[0]);
						Vect3d sampleSize = new Vect3d(getImageDataset()[0].getISize().getX(), getImageDataset()[0].getISize().getY(), getImageDataset()[0].getISize().getZ());
						ArrayList<AsynchClientTask> stlImportTasks = getImportSTLtasks(selectedFiles,sampleSize,new Vect3d(0,0,0));
						ClientTaskDispatcher.dispatch(overlayEditorPanelJAI, new Hashtable<>(), stlImportTasks.toArray(new AsynchClientTask[0]));
					}else{
						DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Select at least 1 .stl file for import.");
					}
				}
			}
		});

		okCancelJPanel.add(okJButton);
		okCancelJPanel.add(attributesJButton);
		okCancelJPanel.add(exportJButton);
		okCancelJPanel.add(importJButton);
//		okCancelJPanel.add(surfaceButton);
		okCancelJPanel.add(cancelJButton);
		
		jDialog.getContentPane().add(overlayEditorPanelJAI,BorderLayout.CENTER);
		jDialog.getContentPane().add(okCancelJPanel,BorderLayout.SOUTH);
		jDialog.setSize(700,600);
		DialogUtils.showModalJDialogOnTop(jDialog,parentComponent);
		
		if(finalGeometryHolder[0] == null){
			throw UserCancelException.CANCEL_GENERIC;
		}
		return finalGeometryHolder[0];
	}

//	private File lastImportDir;
	private ArrayList<AsynchClientTask> getImportSTLtasks(File[] selectedFiles,Vect3d primarySampleSizes,Vect3d subSampleOffset){
//		try {
		final RegionImage currRegionImage = regionImage;
		final TreeMap<Integer,String> newROIindexes = new TreeMap<>();
		final TreeMap<BoundingBox,Object[]> regionBounds = new TreeMap<>(new Comparator<BoundingBox>() {
			@Override
			public int compare(BoundingBox arg0, BoundingBox arg1) {
				if(arg1 == arg0){
					return 0;
				}
				if(arg0.getLoX() == arg1.getLoX() &&
						arg0.getLoY() == arg1.getLoY() &&
						arg0.getLoZ() == arg1.getLoZ() &&
						arg0.getHiX() == arg1.getHiX() &&
						arg0.getHiY() == arg1.getHiY() &&
						arg0.getHiZ() == arg1.getHiZ()){
					return 0;
				}
				return (arg0.getLoX() >= arg1.getLoX() &&
						arg0.getLoY() >= arg1.getLoY() &&
						arg0.getLoZ() >= arg1.getLoZ() &&
						arg0.getHiX() <= arg1.getHiX() &&
						arg0.getHiY() <= arg1.getHiY() &&
						arg0.getHiZ() <= arg1.getHiZ()?-1:+1);
			}
		});
		
		
		AsynchClientTask sampleSTLtask = new AsynchClientTask("importSTLtask",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
					StatsHelper statsHelper = calcMinMax(selectedFiles);
					
					Vect3d worldOrigin = new Vect3d(statsHelper.xmin, statsHelper.ymin, statsHelper.zmin);
					Vect3d worldCollapsedBoundingBox = new Vect3d(statsHelper.xmax-statsHelper.xmin, statsHelper.ymax-statsHelper.ymin, statsHelper.zmax-statsHelper.zmin);
					Vect3d scale = new Vect3d(worldCollapsedBoundingBox.getX()/(2.0*primarySampleSizes.getX()), worldCollapsedBoundingBox.getY()/(2.0*primarySampleSizes.getY()), worldCollapsedBoundingBox.getZ()/(2.0*primarySampleSizes.getZ()));
//					if(scale.getX() !=0 && scale.getX() >= (scale.getY()==0?scale.getX():scale.getY()) && scale.getX() >= (scale.getZ()==0?scale.getX():scale.getZ())){
//						scale = new Vect3d(scale.getX(), scale.getX(), scale.getX());
//					}else if(scale.getY() !=0 && scale.getY() >= (scale.getX()==0?scale.getY():scale.getX()) && scale.getY() >= (scale.getZ()==0?scale.getY():scale.getZ())){
//						scale = new Vect3d(scale.getY(), scale.getY(), scale.getY());
//					}else if(scale.getZ() !=0 && scale.getZ() >= (scale.getY()==0?scale.getZ():scale.getY()) && scale.getZ() >= (scale.getX()==0?scale.getZ():scale.getX())){
//						scale = new Vect3d(scale.getZ(), scale.getZ(), scale.getZ());
//					}
					if(scale.getX() !=0 && scale.getX() <= (scale.getY()==0?scale.getX():scale.getY()) && scale.getX() <= (scale.getZ()==0?scale.getX():scale.getZ())){
						scale = new Vect3d(scale.getX(), scale.getX(), scale.getX());
					}else if(scale.getY() !=0 && scale.getY() <= (scale.getX()==0?scale.getY():scale.getX()) && scale.getY() <= (scale.getZ()==0?scale.getY():scale.getZ())){
						scale = new Vect3d(scale.getY(), scale.getY(), scale.getY());
					}else if(scale.getZ() !=0 && scale.getZ() <= (scale.getY()==0?scale.getZ():scale.getY()) && scale.getZ() <= (scale.getX()==0?scale.getZ():scale.getX())){
						scale = new Vect3d(scale.getZ(), scale.getZ(), scale.getZ());
					}
					for (int j = 0; j < selectedFiles.length; j++) {
						SurfaceCollection surfaceCollection = statsHelper.recalSurfs.get(j);//ClientRequestManager.createSurfaceCollectionFromSurfaceFile(selectedFile);
						for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
							Surface surface = surfaceCollection.getSurfaces(i);
//							System.out.println("surface "+i);
//							Vect3d xyzLow = new Vect3d(surface.getPolygons(0).getNodes(0).getX(),surface.getPolygons(0).getNodes(1).getX(),surface.getPolygons(0).getNodes(2).getX());
//							Vect3d xyzHigh = new Vect3d(xyzLow);
							ComboboxROIName[] existingRoiNames = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
							int newIndex = getUnusedROIColorIndex(existingRoiNames,newROIindexes.keySet());
							newROIindexes.put(newIndex, selectedFiles[j].getName());
							byte value = (byte)newIndex;
//							String roiName = FilenameUtils.getBaseName(selectedFiles[j].getName());
//							roiName = TokenMangler.fixTokenStrict(roiName, 6);
//							roiName+= "_"+i;
//							overlayEditorPanelJAI.addROIName(roiName, true, roiName, true, value);
							for (int k = 0; k < surface.getPolygonCount(); k++){
								Polygon polygon = surface.getPolygons(k);
//								for (Node pnode:polygon.getNodes()) {
//									xyzLow.set(Math.min(xyzLow.getX(), pnode.getX()),Math.min(xyzLow.getY(), pnode.getY()),Math.min(xyzLow.getZ(), pnode.getZ()));
//									xyzHigh.set(Math.max(xyzHigh.getX(), pnode.getX()),Math.max(xyzHigh.getY(), pnode.getY()),Math.max(xyzHigh.getZ(), pnode.getZ()));
//								}
//								if(k%10000 == 0){
//									System.out.println("progress= file "+(j+1)+" of "+selectedFiles.length+" "+((k*100)/surface.getPolygonCount())+"%");
//								}
//								System.out.println("  polygon "+j+" "+polygon.getNodes(0)+" "+polygon.getNodes(1)+" "+polygon.getNodes(2));
								Vect3d line1 = new Vect3d(polygon.getNodes(0));
								Vect3d end1 = new Vect3d(polygon.getNodes(2));
								Vect3d incr1 = Vect3d.sub(end1,line1);
								incr1.unit();
								incr1.set(incr1.getX()*scale.getX(), incr1.getY()*scale.getY(), incr1.getZ()*scale.getZ());
								Vect3d line2 = new Vect3d(line1);
								Vect3d end2 = new Vect3d(polygon.getNodes(1));
								Vect3d incr2 = Vect3d.sub(end2,line2);
								incr2.unit();
								incr2.set(incr2.getX()*scale.getX(), incr2.getY()*scale.getY(), incr2.getZ()*scale.getZ());
								while(true){
									Vect3d line3 = new Vect3d(line1);
									Vect3d end3 = new Vect3d(line2);
									Vect3d incr3 = Vect3d.sub(end3,line3);
									if(incr3.length() != 0){
										incr3.unit();
										incr3.set(incr3.getX()*scale.getX(), incr3.getY()*scale.getY(), incr3.getZ()*scale.getZ());
									}
									while(true){
										calcXYZ(line3, worldOrigin, worldCollapsedBoundingBox, primarySampleSizes,subSampleOffset,value);
										line3.add(incr3);
										Vect3d check =  Vect3d.sub(end3, line3);
										if((check.length()==0) || Math.signum(check.getX()) != Math.signum(incr3.getX()) || Math.signum(check.getY()) != Math.signum(incr3.getY()) || Math.signum(check.getZ()) != Math.signum(incr3.getZ())){
											calcXYZ(end3, worldOrigin, worldCollapsedBoundingBox, primarySampleSizes,subSampleOffset,value);
											break;
										}
									}
									if(line1.equals(end1) && line2.equals(end2)){
										break;
									}
									
									line1.add(incr1);
									Vect3d check =  Vect3d.sub(end1, line1);
									if(check.length()==0 || Math.signum(check.getX()) != Math.signum(incr1.getX()) || Math.signum(check.getY()) != Math.signum(incr1.getY()) || Math.signum(check.getZ()) != Math.signum(incr1.getZ())){
										line1 = new Vect3d(end1);
									}
									line2.add(incr2);
									check =  Vect3d.sub(end2, line2);
									if(check.length()==0 || Math.signum(check.getX()) != Math.signum(incr2.getX()) || Math.signum(check.getY()) != Math.signum(incr2.getY()) || Math.signum(check.getZ()) != Math.signum(incr2.getZ())){
										line2 = new Vect3d(end2);
									}
								}
							}
//							regionBounds.put(new BoundingBox(xyzLow.getX(), xyzHigh.getX(),xyzLow.getY(), xyzHigh.getY(),xyzLow.getZ(), xyzHigh.getZ()),new Object[] {new int[] {j,i},roiName});
						}
					}
//					for(BoundingBox bb:regionBounds.keySet()){
//						System.out.println(((int[])regionBounds.get(bb)[0])[0]+","+((int[])regionBounds.get(bb)[0])[1]+" name="+((String)regionBounds.get(bb)[1])+" bound="+bb.getLoX()+","+bb.getHiX()+" "+bb.getLoY()+","+bb.getHiY()+" "+bb.getLoZ()+","+bb.getHiZ());
//					}
					updateAuxiliaryInfo(originalISize, getClientTaskStatusSupport());
//				}
			}
		};
		final int[] checkHolder = new int[1];
		final String[] cleanupHolder= new String[1];
		final String YES = "Yes";
		final ComboboxROIName[] cbxHolder = new ComboboxROIName[1];
		final String CHECK= "check";
		AsynchClientTask addROInamesTask = new AsynchClientTask("Add new ROI names...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				int i = 0;
				for(Integer newIndex:newROIindexes.keySet()){
					String roiName = FilenameUtils.getBaseName(newROIindexes.get(newIndex));
					roiName = TokenMangler.fixTokenStrict(roiName, 6);
					roiName+= "_" + i++;
					overlayEditorPanelJAI.addROIName(roiName, true, roiName, true, newIndex);
				}
				cleanupHolder[0] = DialogUtils.showWarningDialog(overlayEditorPanelJAI, "Cleanup imported geometry?", new String[] {YES,"No"}, YES);
				if(!YES.equals(cleanupHolder[0])){
					return;
				}
				checkHolder[0] = getUnusedROIColorIndex(overlayEditorPanelJAI.getAllCompositeROINamesAndColors(), null);
				overlayEditorPanelJAI.addROIName(CHECK, true,CHECK, true, checkHolder[0]);
				ComboboxROIName[] temp = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
				for(ComboboxROIName cbx:temp){
					if(cbx.getROIName().equals(CHECK)){
						cbxHolder[0] = cbx;
						break;
					}
				}
			}
		};
//		AsynchClientTask removeCheckROI = new AsynchClientTask("Remove check...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
//			
//			@Override
//			public void run(Hashtable<String, Object> hashTable) throws Exception {
//				ComboboxROIName[] regNames = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
//				for(ComboboxROIName cbx:regNames){
//					if(cbx.get)
//				}
//			}
//		};
		final AsynchClientTask mergeBackgroundRegions = new AsynchClientTask("Merge background regions...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if(!YES.equals(cleanupHolder[0])){
					return;
				}

//				long start = System.currentTimeMillis();
//				while(currRegionImage == regionImage){
//					int dur = (int)((System.currentTimeMillis()-start)/1000);
//					if(dur > 10){
//						return;
//					}
//					System.out.println("trying..."+dur);
//					Thread.sleep(1000);
//				}
				final TreeMap<RegionInfo, Vect3d[]> regTobb = new TreeMap<>(new Comparator<RegionInfo>() {
					@Override
					public int compare(RegionInfo arg0, RegionInfo arg1) {
						return arg0.getRegionIndex()-arg1.getRegionIndex();
					}
				});
				
				Hashtable<String, Object> newRegionImgHolder = new Hashtable<>();
				createRegionImageTask.run(newRegionImgHolder);
				RegionImage newRegionImage = (RegionImage)newRegionImgHolder.get(LOCAL_REGION_IMAGE);
				
//				ArrayList<RegionInfo> remainingRegionInfos = new ArrayList<>(Arrays.asList(newRegionImage.getRegionInfos()));
//				BoundingBox bb = new BoundingBox(0, newRegionImage.getNumX()-1, 0, newRegionImage.getNumY()-1, 0, newRegionImage.getNumZ()-1);
//				int currIndex = 0;
//				for (int z = 0; z <= bb.getHiZ(); z++) {
//					for (int y = 0; y <= bb.getHiY(); y++) {
//						for (int x = 0; x <= bb.getHiX(); x++) {
//							ArrayList<RegionInfo> tempRegionInfos = (ArrayList<RegionInfo>)remainingRegionInfos.clone();
//							if(x==0 || x == bb.getHiX() || y==0 || y == bb.getHiY() || z==0 || z == bb.getHiZ()){
//								for(RegionInfo reg:tempRegionInfos){
//									if(reg.getPixelValue() == 0 && reg.isIndexInRegion(currIndex)){
//										remainingRegionInfos.remove(reg);
//										break;
//									}
//								}
//							}else{
//								for(RegionInfo reg:tempRegionInfos){
//									if(reg.getPixelValue() == 0 && reg.isIndexInRegion(currIndex)){
//										remainingRegionInfos.remove(reg);
//										break;
//									}
//								}								
//							}
//							currIndex++;
//						}
//					}
//				}
				
				//Remove unfilled regions (background) that touch the whole region border
				int allPixCnt = 0;
				ArrayList<RegionInfo> excluded = new ArrayList<>();
				for (int z = 0; z < newRegionImage.getNumZ(); z++) {
					for (int y = 0; y < newRegionImage.getNumY(); y++) {
						for (int x = 0; x < newRegionImage.getNumX(); x++) {
							RegionInfo regInfo = newRegionImage.getRegionInfoFromOffset(allPixCnt);
							allPixCnt+=1;
							if(excluded.contains(regInfo)){
								continue;
							}
							if(regInfo.getPixelValue() == 0 && (x==0 || x == newRegionImage.getNumX()-1 || y==0 || y == newRegionImage.getNumY()-1 || z==0 || z == newRegionImage.getNumZ()-1)){
								excluded.add(regInfo);
								if(regTobb.containsKey(regInfo)){
									regTobb.remove(regInfo);
								}
								continue;
							}
							Vect3d[] bounds = regTobb.get(regInfo);
							if(bounds == null){
								bounds = new Vect3d[] {new Vect3d(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY),new Vect3d(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY)};
								regTobb.put(regInfo, bounds);
							}
							bounds[0].set(Math.min(bounds[0].getX(), x),Math.min(bounds[0].getY(), y),Math.min(bounds[0].getZ(), z));
							bounds[1].set(Math.max(bounds[1].getX(), x),Math.max(bounds[1].getY(), y),Math.max(bounds[1].getZ(), z));

						}
					}
				}
				
//				ArrayList<Object[]> tree = new ArrayList<>();//arraylist<object[]>,regionInfo
//				ArrayList<RegionInfo> bucket = new ArrayList<>(regTobb.keySet());
//				tree.add(new Object[] {null,regTobb.firstKey()});
////				ArrayList<Object[]> stack = new ArrayList<>();
////				stack.add(tree.get(0));
//				while(bucket.size() > 0){
////					Object[] next = stack.remove(0);
//					RegionInfo next = bucket.remove(0);
//					Vect3d[] o2v = (Vect3d[])regTobb.get(regTobb.get(next));
//					for(Object[] objArr:tree){
////						if(((RegionInfo)next[1]).getRegionIndex() != reg.getRegionIndex()){
////								for(RegionInfo bucketReg:bucket){
//									Vect3d[] o1v = (Vect3d[])regTobb.get(objArr[1]);
//									if(	o1v[0].getX() >= o2v[0].getX() && o1v[0].getX() <= o2v[1].getX() &&
//											o1v[0].getY() >= o2v[0].getY() && o1v[0].getY() <= o2v[1].getY() &&
//											o1v[0].getZ() >= o2v[0].getZ() && o1v[0].getZ() <= o2v[1].getZ() 
//											){
//										
//									}
////								}
////						}
//					}
//				}
				//Sort filled/unfilled regions by boundingbox
				ArrayList<RegionInfo> sortedRegionInfos = new ArrayList<>(Arrays.asList(regTobb.keySet().toArray(new RegionInfo[0])/*newRegionImage.getRegionInfos()*/));
				Collections.sort(sortedRegionInfos,new Comparator<RegionInfo>() {
					@Override
					public int compare(RegionInfo o1, RegionInfo o2) {
						
						Vect3d[] o1v = (Vect3d[])regTobb.get(o1);
						Vect3d[] o2v = (Vect3d[])regTobb.get(o2);
//						if(o1.getRegionIndex() == 3/*(o1.getRegionIndex() == 3 || o2.getRegionIndex() == 5) && (o1.getRegionIndex() == 5 || o2.getRegionIndex() == 3)*/){
//							System.out.println("here");
//						}
						if(o1v[0].getX() == o2v[0].getX() && o1v[1].getX() == o2v[1].getX() &&
								o1v[0].getY() == o2v[0].getY() && o1v[1].getY() == o2v[1].getY() &&
								o1v[0].getZ() >= o2v[0].getZ() && o1v[1].getZ() == o2v[1].getZ()){
							System.out.println(o1.getRegionIndex()+" == "+o2.getRegionIndex());
								return 0;
						}else if(	o1v[0].getX() >= o2v[0].getX() && o1v[0].getX() <= o2v[1].getX() &&
//									o1v[1].getX() >= o2v[0].getX() && o1v[1].getX() <= o2v[1].getX() &&
									o1v[0].getY() >= o2v[0].getY() && o1v[0].getY() <= o2v[1].getY() &&
//									o1v[1].getY() >= o2v[0].getY() && o1v[1].getY() <= o2v[1].getY() &&
									o1v[0].getZ() >= o2v[0].getZ() && o1v[0].getZ() <= o2v[1].getZ() 
//									&& o1v[1].getZ() >= o2v[0].getZ() && o1v[1].getZ() <= o2v[1].getZ()
									){
							System.out.println(o1.getRegionIndex()+" > "+o2.getRegionIndex());
							return 1;
						}else if(	o1v[0].getX() <= o2v[0].getX() && o1v[1].getX() >= o2v[1].getX() &&
									o1v[0].getY() <= o2v[0].getY() && o1v[1].getY() >= o2v[1].getY() &&
									o1v[0].getZ() <= o2v[0].getZ() && o1v[1].getZ() >= o2v[1].getZ()
//								(o1v[0].getX() < o2v[0].getX() && o1v[1].getX() < o2v[0].getX()) ||
//									(o1v[0].getX() > o2v[1].getX() && o1v[1].getX() > o2v[1].getX()) ||
//									
//									(o1v[0].getY() < o2v[0].getY() && o1v[1].getY() < o2v[0].getY()) ||
//									(o1v[0].getY() > o2v[1].getY() && o1v[1].getY() > o2v[1].getY()) ||
//									
//									(o1v[0].getZ() < o2v[0].getZ() && o1v[1].getZ() < o2v[0].getZ()) ||
//									(o1v[0].getZ() > o2v[1].getZ() && o1v[1].getZ() > o2v[1].getZ())
//									
//								/*	(o1v[0].getX() < o2v[0].getX() && o1v[0].getX() > o2v[1].getX() &&
//									o1v[1].getX() < o2v[0].getX() && o1v[1].getX() > o2v[1].getX()) ||
//									(o1v[0].getY() < o2v[0].getY() && o1v[0].getY() > o2v[1].getY() &&
//									o1v[1].getY() < o2v[0].getY() && o1v[1].getY() > o2v[1].getY()) ||
//									(o1v[0].getZ() < o2v[0].getZ() && o1v[0].getZ() > o2v[1].getZ() && 
//									o1v[1].getZ() < o2v[0].getZ() && o1v[1].getZ() > o2v[1].getZ())*/
							){
							System.out.println(o1.getRegionIndex()+" <a "+o2.getRegionIndex());
						return -1;
					}else if ((o1v[0].getX() < o2v[0].getX() && o1v[1].getX() < o2v[0].getX()) ||
							(o1v[0].getX() > o2v[1].getX() && o1v[1].getX() > o2v[1].getX()) ||
							
							(o1v[0].getY() < o2v[0].getY() && o1v[1].getY() < o2v[0].getY()) ||
							(o1v[0].getY() > o2v[1].getY() && o1v[1].getY() > o2v[1].getY()) ||
							
							(o1v[0].getZ() < o2v[0].getZ() && o1v[1].getZ() < o2v[0].getZ()) ||
							(o1v[0].getZ() > o2v[1].getZ() && o1v[1].getZ() > o2v[1].getZ())){
						System.out.println(o1.getRegionIndex()+" <b "+o2.getRegionIndex());
						return -1;
						
					}
						System.out.println("inconclusive "+o1.getRegionIndex()+" == "+o2.getRegionIndex());
						return 0;
//						System.out.println("inconclusive "+o1.getRegionIndex()+" "+(o1.getNumPixels() - o2.getNumPixels()<0?"<":(o1.getNumPixels() - o2.getNumPixels()>0?">":"=="))+" "+o2.getRegionIndex());
//						return o1.getNumPixels() - o2.getNumPixels();
//						System.out.println("inconclusive "+o1.getRegionIndex()+" "+(o1.getRegionIndex()-o2.getRegionIndex()<0?"<":(o1.getRegionIndex()-o2.getRegionIndex()>0?">":"=="))+" "+o2.getRegionIndex());
//						return o1.getRegionIndex()-o2.getRegionIndex();
//						if(o1v[0].getZ() != o2v[0].getZ()){
//							return (int)(o1v[0].getZ() - o2v[0].getZ());
//						}else if(o1v[0].getY() != o2v[0].getY()){
//							return (int)(o1v[0].getY() - o2v[0].getY());
//						}else if(o1v[0].getX() != o2v[0].getX()){
//							return (int)(o1v[0].getX() - o2v[0].getX());
//						}else
//						return 0;
					}
				});
				
				//Convert unfilled regions into parent regions
				int currColorIndex = -1;
//				for (int i = sortedRegionInfos.size()-1; i >= 0; i--) {
				for (int i = 0; i< sortedRegionInfos.size(); i++) {
					RegionInfo regInfo = sortedRegionInfos.get(i);
					Vect3d[] o1v = (Vect3d[])regTobb.get(regInfo);
					ComboboxROIName cbxroiName = overlayEditorPanelJAI.getComboboxROIName(regInfo);
//					System.out.println(cbxroiName+" colorIndex="+regInfo.getPixelValue()+" regIndex="+regInfo.getRegionIndex()+" pixels="+regInfo.getNumPixels()+" "+o1v[0]+" "+o1v[1]);
					if(cbxroiName != null){
						currColorIndex = cbxroiName.getContrastColorIndex();
						continue;
					}
					allPixCnt = 0;
					for (int z = 0; z < newRegionImage.getNumZ(); z++) {
						for (int y = 0; y < newRegionImage.getNumY(); y++) {
							for (int x = 0; x < newRegionImage.getNumX(); x++) {
								if(regInfo.isIndexInRegion(allPixCnt)){
									BufferedImage plane = roiComposite[z];
									byte[] data = ((DataBufferByte)plane.getRaster().getDataBuffer()).getData();
									data[x + (plane.getWidth()*y)] = (byte)currColorIndex;
								}
								allPixCnt++;
							}
						}
					}
					
				}
				
//				for(RegionInfo regInfo:regTobb.keySet()){
//					Vect3d[] varr = regTobb.get(regInfo);
//					ComboboxROIName cbxroiName = overlayEditorPanelJAI.getComboboxROIName(regInfo);
//					regionBounds.put(new BoundingBox(varr[0].getX(), varr[1].getX(),varr[0].getY(), varr[1].getY(),varr[0].getZ(), varr[1].getZ()),new Object[] {cbxroiName});
//				}
//				for(BoundingBox bb:regionBounds.keySet()){
//					ComboboxROIName cbxroiName = ((ComboboxROIName)regionBounds.get(bb)[0]);
//					System.out.println(cbxroiName+" bound="+bb.getLoX()+","+bb.getHiX()+" "+bb.getLoY()+","+bb.getHiY()+" "+bb.getLoZ()+","+bb.getHiZ());
//
//				}
				
				//Make sure every pixel has only 1 kind of neighbor
				
//				createRegionImageTask.run(newRegionImgHolder);
//				newRegionImage = (RegionImage)newRegionImgHolder.get(LOCAL_REGION_IMAGE);
				boolean bHasCheck = false;
				VCImage checkImage = ROIMultiPaintManager.createVCImageFromBufferedImages(ROIMultiPaintManager.DEFAULT_EXTENT, roiComposite);
				newRegionImage =
					new RegionImage(checkImage, 0 /*0 means generate no surfacecollection*/,
							checkImage.getExtent(),ROIMultiPaintManager.DEFAULT_ORIGIN, RegionImage.NO_SMOOTHING,
							null);

				TreeSet<Integer> diffNeighbors = new TreeSet<>();
				int[][] offsets = new int[][] {{-1,0,0},{1,0,0},{0,-1,0},{0,1,0},{0,0,-1},{0,0,1}};
				for (int z = 0; z < newRegionImage.getNumZ(); z++) {
					BufferedImage plane = roiComposite[z];
					byte[] data = ((DataBufferByte)plane.getRaster().getDataBuffer()).getData();
					allPixCnt = 0;
					for (int y = 0; y < newRegionImage.getNumY(); y++) {
						for (int x = 0; x < newRegionImage.getNumX(); x++) {
							diffNeighbors.clear();
							RegionInfo regInfo = newRegionImage.getRegionInfoFromOffset(allPixCnt);
							if(regInfo.getPixelValue() == 0){
								allPixCnt++;
								continue;
							}
							if(x==19 && y==15){
								System.out.println("here");
							}
							
							for (int i = 0; i < offsets.length; i++) {
								int tmpx = offsets[i][0]+x;
								if(tmpx < 0 || tmpx >= newRegionImage.getNumX()){
									continue;
								}
								int tmpy = offsets[i][1]+y;
								if(tmpy < 0 || tmpy >= newRegionImage.getNumY()){
									continue;
								}
								int tmpz = offsets[i][2]+z;
								if(tmpz < 0 || tmpz >= newRegionImage.getNumZ()){
									continue;
								}
								BufferedImage plane0 = roiComposite[tmpz];
								byte[] data0 = ((DataBufferByte)plane.getRaster().getDataBuffer()).getData();
								byte shiftb = data0[tmpx+(tmpy*newRegionImage.getNumX())];
								if(shiftb != regInfo.getPixelValue()){
									diffNeighbors.add(0x000000FF&shiftb);
								}

							}
							
							
//							for (int z0 = -1; z0 <= 1; z0+=2) {
//								int tmpz = z+z0;
//								if(tmpz < 0 || tmpz >= newRegionImage.getNumZ()){
//									continue;
//								}
////								tmpz = allPixCnt+(z0*newRegionImage.getNumXY());
//								BufferedImage plane0 = roiComposite[z+z0];
//								byte[] data0 = ((DataBufferByte)plane.getRaster().getDataBuffer()).getData();
//								for (int y0 = -1; y0 <= 1; y0+=2) {
//									int tmpy = y+y0;
//									if(tmpy < 0 || tmpy >= newRegionImage.getNumY()){
//										continue;
//									}
////									tmpy = tmpz+(y0*newRegionImage.getNumX());
//									for (int x0 = -1; x0 <= 1; x0+=2) {
//										int tmpx = x+x0;
//										if(tmpx < 0 || tmpx >= newRegionImage.getNumX()){
//											continue;
//										}
////										tmpx = tmpy+(x0);
//										byte shiftb = data0[tmpx+(tmpy*newRegionImage.getNumX())];
//										if(shiftb != regInfo.getPixelValue()){
//											diffNeighbors.add(0x000000FF&shiftb);
//										}
//									}
//								}
//							}
							allPixCnt++;
							if(diffNeighbors.size() > 1){
								bHasCheck = true;
								data[x+y*newRegionImage.getNumX()] = (byte)checkHolder[0];
							}
						}
					}
				}
				if(!bHasCheck && cbxHolder[0] != null){
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							overlayEditorPanelJAI.deleteROIName(cbxHolder[0]);
						}
					});
				}
			}
		};
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				try {
//					mergeBackgroundRegions.run(new Hashtable<>());
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}).start();
		return new ArrayList<>(Arrays.asList(new AsynchClientTask[] {sampleSTLtask,addROInamesTask,mergeBackgroundRegions,getUpdateDisplayAfterCropTask()}));
		
//			JFileChooser importJFC = new JFileChooser(ClientRequestManager.getPreferredPath(UserPreferences.getLastUserPreferences()));
//			importJFC.setDialogTitle("Choose .stl file to import");
//			importJFC.setMultiSelectionEnabled(true);
//			int result = importJFC.showOpenDialog(overlayEditorPanelJAI);
//			if(result == JFileChooser.APPROVE_OPTION){
//				File[] selectedFiles = importJFC.getSelectedFiles();
//				if(statsHelper == null){
//					statsHelper = calcMinMax(selectedFiles);
//				}
//				ClientRequestManager.setPreferredPath(UserPreferences.getLastUserPreferences(), selectedFiles[0]);
//				
//				Vect3d worldOrigin = new Vect3d(statsHelper.xmin, statsHelper.ymin, statsHelper.zmin);
//				Vect3d worldCollapsedBoundingBox = new Vect3d(statsHelper.xmax-statsHelper.xmin, statsHelper.ymax-statsHelper.ymin, statsHelper.zmax-statsHelper.zmin);
//				Vect3d scale = new Vect3d(worldCollapsedBoundingBox.getX()/(2.0*primarySampleSizes.getX()), worldCollapsedBoundingBox.getY()/(2.0*primarySampleSizes.getY()), worldCollapsedBoundingBox.getZ()/(2.0*primarySampleSizes.getZ()));
////				if(scale.getX() !=0 && scale.getX() >= (scale.getY()==0?scale.getX():scale.getY()) && scale.getX() >= (scale.getZ()==0?scale.getX():scale.getZ())){
////					scale = new Vect3d(scale.getX(), scale.getX(), scale.getX());
////				}else if(scale.getY() !=0 && scale.getY() >= (scale.getX()==0?scale.getY():scale.getX()) && scale.getY() >= (scale.getZ()==0?scale.getY():scale.getZ())){
////					scale = new Vect3d(scale.getY(), scale.getY(), scale.getY());
////				}else if(scale.getZ() !=0 && scale.getZ() >= (scale.getY()==0?scale.getZ():scale.getY()) && scale.getZ() >= (scale.getX()==0?scale.getZ():scale.getX())){
////					scale = new Vect3d(scale.getZ(), scale.getZ(), scale.getZ());
////				}
//				if(scale.getX() !=0 && scale.getX() <= (scale.getY()==0?scale.getX():scale.getY()) && scale.getX() <= (scale.getZ()==0?scale.getX():scale.getZ())){
//					scale = new Vect3d(scale.getX(), scale.getX(), scale.getX());
//				}else if(scale.getY() !=0 && scale.getY() <= (scale.getX()==0?scale.getY():scale.getX()) && scale.getY() <= (scale.getZ()==0?scale.getY():scale.getZ())){
//					scale = new Vect3d(scale.getY(), scale.getY(), scale.getY());
//				}else if(scale.getZ() !=0 && scale.getZ() <= (scale.getY()==0?scale.getZ():scale.getY()) && scale.getZ() <= (scale.getX()==0?scale.getZ():scale.getX())){
//					scale = new Vect3d(scale.getZ(), scale.getZ(), scale.getZ());
//				}
//				TreeMap<BoundingBox,Object[]> regionBounds = new TreeMap<>(new Comparator<BoundingBox>() {
//					@Override
//					public int compare(BoundingBox arg0, BoundingBox arg1) {
//						if(arg1 == arg0){
//							return 0;
//						}
//						if(arg0.getLoX() == arg1.getLoX() &&
//								arg0.getLoY() == arg1.getLoY() &&
//								arg0.getLoZ() == arg1.getLoZ() &&
//								arg0.getHiX() == arg1.getHiX() &&
//								arg0.getHiY() == arg1.getHiY() &&
//								arg0.getHiZ() == arg1.getHiZ()){
//							return 0;
//						}
//						return (arg0.getLoX() >= arg1.getLoX() &&
//								arg0.getLoY() >= arg1.getLoY() &&
//								arg0.getLoZ() >= arg1.getLoZ() &&
//								arg0.getHiX() <= arg1.getHiX() &&
//								arg0.getHiY() <= arg1.getHiY() &&
//								arg0.getHiZ() <= arg1.getHiZ()?-1:+1);
//					}
//				});
//				for (int j = 0; j < selectedFiles.length; j++) {
//					SurfaceCollection surfaceCollection = statsHelper.recalSurfs.get(j);//ClientRequestManager.createSurfaceCollectionFromSurfaceFile(selectedFile);
//					for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
//						Surface surface = surfaceCollection.getSurfaces(i);
//						System.out.println("surface "+i);
//						Vect3d xyzLow = new Vect3d(surface.getPolygons(0).getNodes(0).getX(),surface.getPolygons(0).getNodes(1).getX(),surface.getPolygons(0).getNodes(2).getX());
//						Vect3d xyzHigh = new Vect3d(xyzLow);
//						ComboboxROIName[] existingRoiNames = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
//						byte value = (byte)getUnusedROIColorIndex(existingRoiNames);
//						String roiName = FilenameUtils.getBaseName(selectedFiles[j].getName());
//						roiName = TokenMangler.fixTokenStrict(roiName, 6);
//						roiName+= "_"+i;
//						overlayEditorPanelJAI.addROIName(roiName, true, roiName, true, value);
//						for (int k = 0; k < surface.getPolygonCount(); k++){
//							Polygon polygon = surface.getPolygons(k);
////							for (Node pnode:polygon.getNodes()) {
////								xyzLow.set(Math.min(xyzLow.getX(), pnode.getX()),Math.min(xyzLow.getY(), pnode.getY()),Math.min(xyzLow.getZ(), pnode.getZ()));
////								xyzHigh.set(Math.max(xyzHigh.getX(), pnode.getX()),Math.max(xyzHigh.getY(), pnode.getY()),Math.max(xyzHigh.getZ(), pnode.getZ()));
////							}
//							if(k%10000 == 0){
//								System.out.println("progress= file "+(j+1)+" of "+selectedFiles.length+" "+((k*100)/surface.getPolygonCount())+"%");
//							}
////							System.out.println("  polygon "+j+" "+polygon.getNodes(0)+" "+polygon.getNodes(1)+" "+polygon.getNodes(2));
//							Vect3d line1 = new Vect3d(polygon.getNodes(0));
//							Vect3d end1 = new Vect3d(polygon.getNodes(2));
//							Vect3d incr1 = Vect3d.sub(end1,line1);
//							incr1.unit();
//							incr1.set(incr1.getX()*scale.getX(), incr1.getY()*scale.getY(), incr1.getZ()*scale.getZ());
//							Vect3d line2 = new Vect3d(line1);
//							Vect3d end2 = new Vect3d(polygon.getNodes(1));
//							Vect3d incr2 = Vect3d.sub(end2,line2);
//							incr2.unit();
//							incr2.set(incr2.getX()*scale.getX(), incr2.getY()*scale.getY(), incr2.getZ()*scale.getZ());
//							while(true){
//								Vect3d line3 = new Vect3d(line1);
//								Vect3d end3 = new Vect3d(line2);
//								Vect3d incr3 = Vect3d.sub(end3,line3);
//								if(incr3.length() != 0){
//									incr3.unit();
//									incr3.set(incr3.getX()*scale.getX(), incr3.getY()*scale.getY(), incr3.getZ()*scale.getZ());
//								}
//								while(true){
//									calcXYZ(line3, worldOrigin, worldCollapsedBoundingBox, primarySampleSizes,subSampleOffset,value);
//									line3.add(incr3);
//									Vect3d check =  Vect3d.sub(end3, line3);
//									if((check.length()==0) || Math.signum(check.getX()) != Math.signum(incr3.getX()) || Math.signum(check.getY()) != Math.signum(incr3.getY()) || Math.signum(check.getZ()) != Math.signum(incr3.getZ())){
//										calcXYZ(end3, worldOrigin, worldCollapsedBoundingBox, primarySampleSizes,subSampleOffset,value);
//										break;
//									}
//								}
//								if(line1.equals(end1) && line2.equals(end2)){
//									break;
//								}
//								
//								line1.add(incr1);
//								Vect3d check =  Vect3d.sub(end1, line1);
//								if(check.length()==0 || Math.signum(check.getX()) != Math.signum(incr1.getX()) || Math.signum(check.getY()) != Math.signum(incr1.getY()) || Math.signum(check.getZ()) != Math.signum(incr1.getZ())){
//									line1 = new Vect3d(end1);
//								}
//								line2.add(incr2);
//								check =  Vect3d.sub(end2, line2);
//								if(check.length()==0 || Math.signum(check.getX()) != Math.signum(incr2.getX()) || Math.signum(check.getY()) != Math.signum(incr2.getY()) || Math.signum(check.getZ()) != Math.signum(incr2.getZ())){
//									line2 = new Vect3d(end2);
//								}
//							}
//						}
////						regionBounds.put(new BoundingBox(xyzLow.getX(), xyzHigh.getX(),xyzLow.getY(), xyzHigh.getY(),xyzLow.getZ(), xyzHigh.getZ()),new Object[] {new int[] {j,i},roiName});
//					}
//				}
////				for(BoundingBox bb:regionBounds.keySet()){
////					System.out.println(((int[])regionBounds.get(bb)[0])[0]+","+((int[])regionBounds.get(bb)[0])[1]+" name="+((String)regionBounds.get(bb)[1])+" bound="+bb.getLoX()+","+bb.getHiX()+" "+bb.getLoY()+","+bb.getHiY()+" "+bb.getLoZ()+","+bb.getHiZ());
////				}
//				updateAuxiliaryInfo(originalISize, null);
//				getUpdateDisplayAfterCropTask().run(null);
//
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						while(ClientTaskDispatcher.isBusy()){
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//						TreeMap<RegionInfo, Vect3d[]> regTobb = new TreeMap<>(new Comparator<RegionInfo>() {
//							@Override
//							public int compare(RegionInfo arg0, RegionInfo arg1) {
//								return arg0.getRegionIndex()-arg1.getRegionIndex();
//							}
//						});
//						int allPixCnt = regionImage.getNumX()*regionImage.getNumY()*regionImage.getNumZ();
//						for (int z = 0; z < regionImage.getNumZ(); z++) {
//							for (int y = 0; y < regionImage.getNumY(); y++) {
//								for (int x = 0; x < regionImage.getNumX(); x++) {
//									allPixCnt-=1;
//									RegionInfo regInfo = regionImage.getRegionInfoFromOffset(allPixCnt);
//									Vect3d[] bounds = regTobb.get(regInfo);
//									if(bounds == null){
//										bounds = new Vect3d[] {new Vect3d(),new Vect3d()};
//										regTobb.put(regInfo, bounds);
//									}
//									bounds[0].set(Math.min(bounds[0].getX(), x),Math.min(bounds[0].getY(), y),Math.min(bounds[0].getZ(), z));
//									bounds[1].set(Math.max(bounds[1].getX(), x),Math.max(bounds[1].getY(), y),Math.max(bounds[1].getZ(), z));
//
//								}
//							}
//						}
//						for(RegionInfo regInfo:regTobb.keySet()){
//							Vect3d[] varr = regTobb.get(regInfo);
//							ComboboxROIName cbxroiName = overlayEditorPanelJAI.getComboboxROIName(regInfo);
//							regionBounds.put(new BoundingBox(varr[0].getX(), varr[1].getX(),varr[0].getY(), varr[1].getY(),varr[0].getZ(), varr[1].getZ()),new Object[] {cbxroiName});
//						}
//						for(BoundingBox bb:regionBounds.keySet()){
//							ComboboxROIName cbxroiName = ((ComboboxROIName)regionBounds.get(bb)[0]);
//							System.out.println(cbxroiName+" bound="+bb.getLoX()+","+bb.getHiX()+" "+bb.getLoY()+","+bb.getHiY()+" "+bb.getLoZ()+","+bb.getHiZ());
//
//						}
//					}
//				}).start();
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//			DialogUtils.showErrorDialog(overlayEditorPanelJAI, e.getMessage());
//		}
	}
	
	private void calcXYZ(Vect3d line3,Vect3d worldOrigin,Vect3d worldCollapsedBoundingBox,Vect3d primarySampleSizes,Vect3d subSampleOffset,byte value)throws Exception{
		double x = (((line3.getX()-worldOrigin.getX())/worldCollapsedBoundingBox.getX())*(primarySampleSizes.getX()-1));
		double y = (((line3.getY()-worldOrigin.getY())/worldCollapsedBoundingBox.getY())*(primarySampleSizes.getY()-1));
		double z = (((line3.getZ()-worldOrigin.getZ())/worldCollapsedBoundingBox.getZ())*(primarySampleSizes.getZ()-1));
		int subX = (int)(x-subSampleOffset.getX());
		int subY = (int)(y-subSampleOffset.getY());
		int subZ = (int)(z-subSampleOffset.getZ());
//		if(subZ==23 && subX>=32 && subX<=34 && subY==60/*>=59 && subY<=61*/ && (line3.getX() == -39.12749559761843 || line3.getX() == -39.98640441894531)){
//			System.out.println("z,y,x"+subZ+" "+subY+" "+subX+" "+line3);
////			subX = 33;
//		}
//		if(subZ==21 && subX>=30 && subX<=32 && subY>=59 && subY<=61/* && line3.getX() == -40.045438479618916*/ /* && (line3.getX() == -39.12749559761843 || line3.getX() == -39.98640441894531)*/){
//			System.out.println("z,y,x"+subZ+" "+subY+" "+subX+" "+line3);
////			subX = 33;
//		}

		if(subX >= 0 && subX < roiComposite[0].getWidth() &&
			subY >= 0 && subY < roiComposite[0].getHeight() &&
			subZ >= 0 && subZ < roiComposite.length){
			
			BufferedImage plane = roiComposite[subZ];
			byte[] data = ((DataBufferByte)plane.getRaster().getDataBuffer()).getData();
			data[subX + (plane.getWidth()*subY)] = value;

		}
//		else{
//			System.out.println(subX+","+subY+","+subZ);
//			System.out.println("?");
//		}
		
	}
	
	private static class StatsHelper{
		public double xmin;
		public double ymin;
		public double zmin;
		public double xmax;
		public double ymax;
		public double zmax;
		public ArrayList<SurfaceCollection> recalSurfs;
	}

	private static StatsHelper calcMinMax(File[] selectedFiles) throws Exception{
		
		ArrayList<SurfaceCollection> allSurfCollections = new ArrayList<>();
		TreeMap<String, TreeMap<Integer, ArrayList<TreeMap<Integer, TreeSet<Integer>>>>> fileMapSurfMapSubsurf = new TreeMap<>();
		for (int j = 0; j < selectedFiles.length; j++) {
			File selectedfiFile = selectedFiles[j];
			SurfaceCollection surfaceCollection = ClientRequestManager.createSurfaceCollectionFromSurfaceFile(selectedfiFile);
			if(surfaceCollection == null){
				throw new Exception("Expecting .stl or .mesh(salk) from file '"+selectedfiFile+"'");
			}
			TreeMap<Integer, ArrayList<TreeMap<Integer, TreeSet<Integer>>>> fileSurf = new TreeMap<>();
			fileMapSurfMapSubsurf.put(selectedfiFile.getAbsolutePath(), fileSurf);
//			nodeMapFace.add(treeMap);
			TreeSet<Integer> allNodes = new TreeSet<>();
			for (int k = 0; k < surfaceCollection.getNodeCount(); k++) {
				allNodes.add(k);
			}
			TreeMap<Integer,ArrayList<TreeSet<Integer>>> allSubSurf = new TreeMap<>();
			int surfOutCount = 0;
			for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
				ArrayList<TreeMap<Integer, TreeSet<Integer>>> surfMap = new ArrayList<>();
				fileSurf.put(i, surfMap);
				TreeMap<Integer, TreeSet<Integer>> treeMap = new TreeMap<>();
				surfMap.add(treeMap);
				Surface surface = surfaceCollection.getSurfaces(i);
				for (int k = 0; k < surface.getPolygonCount(); k++) {
					Polygon polygon = surface.getPolygons(k);
					for(Node node:polygon.getNodes()){
						TreeSet<Integer> PolygonIndexes = treeMap.get(node.getGlobalIndex());
						if(PolygonIndexes == null){
							PolygonIndexes = new TreeSet<Integer>();
							treeMap.put(node.getGlobalIndex(), PolygonIndexes);
						}
						PolygonIndexes.add(k);
					}
				}
				allSubSurf.put(i, new ArrayList<>());
				while(allNodes.size() > 0){
					surfOutCount+= 1;
					TreeSet<Integer> searchNodes = new TreeSet<>(Arrays.asList(new Integer[] {allNodes.iterator().next()}));
					TreeSet<Integer> alreadySearched = new TreeSet<>();
					TreeSet<Integer> subSurf = new TreeSet<>();
					allSubSurf.get(i).add(subSurf);
					while(searchNodes.size() > 0){
						Integer currentNode = searchNodes.iterator().next();
						searchNodes.remove(currentNode);
						alreadySearched.add(currentNode);
						allNodes.remove(currentNode);
						TreeSet<Integer> facesForNode = treeMap.get(surfaceCollection.getNodes(currentNode).getGlobalIndex());
						Iterator<Integer> facesIter = facesForNode.iterator();
						while(facesIter.hasNext()){
							Integer faceIndex = facesIter.next();
							subSurf.add(faceIndex);
							Polygon poly = surfaceCollection.getSurfaces(i).getPolygons(faceIndex);
							for (int k = 0; k < poly.getNodes().length; k++) {
								if(poly.getNodes()[k].getGlobalIndex() != currentNode && !alreadySearched.contains(poly.getNodes()[k].getGlobalIndex())){
									searchNodes.add(poly.getNodes()[k].getGlobalIndex());
								}
							}
						}
					}
				}
			}
			if(surfOutCount > surfaceCollection.getSurfaceCount()){
				SurfaceCollection newSurfCollection = new SurfaceCollection();
				newSurfCollection.setNodes(surfaceCollection.getNodes());
				for(Integer origSurfIndex:allSubSurf.keySet()){
					ArrayList<TreeSet<Integer>> newSubSurfaces = allSubSurf.get(origSurfIndex);
					for(TreeSet<Integer> subSurf:newSubSurfaces){
						OrigSurface os = new OrigSurface(0, 1);
						Iterator<Integer> polyIter = subSurf.iterator();
						while(polyIter.hasNext()){
							Polygon poly = surfaceCollection.getSurfaces(origSurfIndex).getPolygons(polyIter.next());
							os.addPolygon(poly);
						}						
						newSurfCollection.addSurface(os);
					}
				}
				allSurfCollections.add(newSurfCollection);
			}else{
				allSurfCollections.add(surfaceCollection);
			}
//			fileMapSurfMapSubsurf.get(selectedfiFile.getAbsolutePath()).get(i).add(treeMap);

		}
		
		
		
		
		StatsHelper statsHelper = new StatsHelper();
		statsHelper.recalSurfs = allSurfCollections;
		for (int j = 0; j < statsHelper.recalSurfs.size(); j++) {
//			File selectedfiFile = selectedFiles[j];
//			SurfaceCollection surfaceCollection = ClientRequestManager.createSurfaceCollectionFromSurfaceFile(selectedfiFile);
			SurfaceCollection surfaceCollection = statsHelper.recalSurfs.get(j);
			for (int i = 0; i < surfaceCollection.getNodes().length; i++) {
				if(j == 0 && i==0){
					statsHelper.xmin = surfaceCollection.getNodes()[i].getX();
					statsHelper.xmax = statsHelper.xmin;
					statsHelper.ymin = surfaceCollection.getNodes()[i].getY();
					statsHelper.ymax = statsHelper.ymin;
					statsHelper.zmin = surfaceCollection.getNodes()[i].getZ();
					statsHelper.zmax = statsHelper.zmin;
				}
				statsHelper.xmin = Math.min(statsHelper.xmin, surfaceCollection.getNodes()[i].getX());
				statsHelper.ymin = Math.min(statsHelper.ymin, surfaceCollection.getNodes()[i].getY());
				statsHelper.zmin = Math.min(statsHelper.zmin, surfaceCollection.getNodes()[i].getZ());
				statsHelper.xmax = Math.max(statsHelper.xmax, surfaceCollection.getNodes()[i].getX());
				statsHelper.ymax = Math.max(statsHelper.ymax, surfaceCollection.getNodes()[i].getY());
				statsHelper.zmax = Math.max(statsHelper.zmax, surfaceCollection.getNodes()[i].getZ());
			}
		}
		return statsHelper;
	}

	
	private void export(){
		final String PNG_FILETYPE = "png";
		try{
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Choose output directory and enter basename (z-slices will be numbered)");
			while(true){
				int result = jfc.showSaveDialog(overlayEditorPanelJAI);
				if(result == JFileChooser.APPROVE_OPTION){
					File destination = jfc.getSelectedFile();
					if(destination.getName().contains(".")){
						DialogUtils.showWarningDialog(overlayEditorPanelJAI, "Export basename should contain only letters and numbers.");
						continue;
					}
					if(destination.isDirectory()){
						DialogUtils.showWarningDialog(overlayEditorPanelJAI, "select destination directory and enter basename for export.");
						continue;
					}
					boolean bExists = false;
					for (int i = 0; i < roiComposite.length; i++) {
						String name = makeNumberedName(destination.getName(), i,PNG_FILETYPE);
						File checkFile = new File(destination.getParent(),name);
						if(checkFile.exists()){
							DialogUtils.showWarningDialog(overlayEditorPanelJAI, "Export slice file "+checkFile.getAbsolutePath()+" already exists, choose another basename.");
							bExists = true;
							break;					
							
						}
					}
					if(bExists){
						continue;
					}
					for (int i = 0; i < roiComposite.length; i++) {
						String name = makeNumberedName(destination.getName(), i,PNG_FILETYPE);
						ImageIO.write(roiComposite[i], PNG_FILETYPE, new File(destination.getParent(),name));
					}
					break;
				}else{
					break;
				}
			}
		}catch(Exception e){
			DialogUtils.showErrorDialog(overlayEditorPanelJAI, e.getMessage());
		}
	}
	private String makeNumberedName(String baseName,int indexNumber,String suffix){
		return baseName+"_"+(indexNumber<10?"0":"")+(indexNumber<100?"0":"")+(indexNumber<1000?"0":"")+indexNumber+"."+suffix;
	}
	
	private VCImage checkAll() throws Exception{
		if(!overlayEditorPanelJAI.isHistogramSelectionEmpty()/*overlayEditorPanelJAI.getHighliteInfo() != null*/){
			final String highlightDiscard = "discard, continue";
			final String cancelAssign = "Cancel";
			String result = DialogUtils.showWarningDialog(
					overlayEditorPanelJAI,
					"Warning: Selections from the 'Histogram Tool' are present.  Choose an action:\n"+
					"1.  Discard selection without applying.\n"+
					"2.  Cancel, go back to Geometry Editor. (hint: Use 'Histogram Tool' apply)",
					new String[] {highlightDiscard,cancelAssign}, highlightDiscard);
			if(result.equals(highlightDiscard)){
				overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_END_PROPERTY);
			}else{
				throw UserCancelException.CANCEL_GENERIC;
			}	
		}
		ComboboxROIName[] roiNamesAndColors = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
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
			final String cancelAssign = "Cancel";
			String result = DialogUtils.showWarningDialog(
					overlayEditorPanelJAI,
					"Warning: some areas of image segmentation have not been assigned to a Domain.  "+
					"Choose an action:\n"+
					"1.  Leave as is, unassigned areas should be treated as 'background'.\n"+
					"2.  Cancel, back to Geometry Editor. (hint: look at 'Domain Regions' list for 'bkgrnd' entries)",
					new String[] {assignToBackground,/*assignToNeighbors,*/cancelAssign}, assignToBackground);
			if(result.equals(assignToBackground)){
				bForceAssignBackground = true;
			}else{
				throw UserCancelException.CANCEL_GENERIC;
			}
			if(bForceAssignBackground){
				vcPixelClassesFromROINames = new VCPixelClass[roiNamesAndColors.length+1];
				vcPixelClassesFromROINames[0] = new VCPixelClass(null, RESERVED_NAME_BACKGROUND, 0);					
			}
		}else{
			vcPixelClassesFromROINames = new VCPixelClass[roiNamesAndColors.length];
		}
		

		//find pixel indexes corresponding to colors for ROIs
		int index = (bForceAssignBackground?1:0);
		for (int j = 0; j < roiNamesAndColors.length; j++) {
			String roiNameString = roiNamesAndColors[j].getROIName();
			vcPixelClassesFromROINames[index] =
				new VCPixelClass(null, roiNameString, roiNamesAndColors[j].getContrastColorIndex());
			index++;
		}

		VCImage initImage = createVCImageFromBufferedImages(ROIMultiPaintManager.DEFAULT_EXTENT, roiComposite);

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
				throw new Exception("Error processing Domain Image.  Pixels found having no matching Domain.");
			}
		}
		Vector<VCPixelClass> missingDomainVCPixelClasses = new Vector<VCPixelClass>();
		Vector<VCPixelClass> foundDomainVCPixelClasses = new Vector<VCPixelClass>();
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
				missingROISB.append((missingDomainVCPixelClasses.size()>0?",":"")+"'"+vcPixelClassesFromROINames[i].getPixelClassName()+"'");
				missingDomainVCPixelClasses.add(vcPixelClassesFromROINames[i]);
			}else{
				foundDomainVCPixelClasses.add(vcPixelClassesFromROINames[i]);
			}
		}
		if(missingDomainVCPixelClasses.size() > 0){
			final String removeROI = "Remove Domain"+(missingDomainVCPixelClasses.size()>1?"s":"")+" and continue";
			final String backtoSegment = "Return to segmentation";
			String result = DialogUtils.showWarningDialog(
					overlayEditorPanelJAI, 
					"Domain"+(missingDomainVCPixelClasses.size()>1?"s":"")+" named "+missingROISB.toString()+" have no pixels defined",
					new String[] {removeROI,backtoSegment}, removeROI);
			if(result.equals(removeROI)){
				vcPixelClassesFromROINames = foundDomainVCPixelClasses.toArray(new VCPixelClass[0]);
			}else{
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
		//Check borders
		VCImage temp = checkBorders(initImage);
		if(temp != null){
			initImage = temp;
		}
		
		//Check if we added a border and don't have a VCPixelClass for background
		boolean bHasBackgroundVCPixel = false;
		for (int j = 0; j < vcPixelClassesFromROINames.length; j++) {
			if(vcPixelClassesFromROINames[j].getPixel() == 0){
				bHasBackgroundVCPixel = true;
				break;
			}
		}
		if(!bHasBackgroundVCPixel){
			for (int i = 0; i < initImage.getPixels().length; i++) {
				if(initImage.getPixels()[i] == 0){
					VCPixelClass[] tempvcp = new VCPixelClass[vcPixelClassesFromROINames.length+1];
					tempvcp[0] = new VCPixelClass(null, RESERVED_NAME_BACKGROUND, 0);
					System.arraycopy(vcPixelClassesFromROINames, 0, tempvcp, 1, vcPixelClassesFromROINames.length);
					vcPixelClassesFromROINames = tempvcp;
					break;
				}
			}
		}

		initImage.setPixelClasses(vcPixelClassesFromROINames);
		updateExtent(initImage,originalExtent,originalISize);
		return initImage;
	}
		
	public static final String ROI_AND_CROP = "ROI_AND_CROP";
	public static final String SHOW_ROI_PANEL_TASK_NAME = "Show Domain display";
	
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
	private VCImage checkBorders(VCImage checkThisVCImage) throws Exception{
		boolean bAddBorder = false;
		BorderInfo borderInfo = checkBorderInfo(checkThisVCImage);
		
		if(borderInfo.bXYTouch || borderInfo.bZTouch){
			boolean b3DTouch = borderInfo.bXYTouch && borderInfo.bZTouch;
			String edgeDescrFrag = "on the "+(b3DTouch?"XY and Z":(borderInfo.bXYTouch?"XY":"Z"))+" border.";
			final String addBorder = "Add empty border";
			final String keep = "Keep as is";
			final String cancel = "Cancel";
			String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI,
					"One or more Domain Regions touches the outer boundary "+edgeDescrFrag+"\n"+
					"Choose an option:\n"+
					"1. Keep as is, do not change.\n"+
					"2. Add empty 'background' border around outer boundary so no Domain Region touches an outer edge.",
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
		boolean bxy = borderInfo.bXYTouch;
		boolean bz = borderInfo.bZTouch;
		ROIMultiPaintManager.PaddedInfo paddedInfo = copyToPadded(
				checkThisVCImage.getPixels(),checkThisVCImageISize,null,checkThisVCImage.getExtent(),
				new ISize((bxy?1:0), (bxy?1:0), (bz?1:0)),new ISize((bxy?1:0), (bxy?1:0), (bz?1:0)));
		
		VCImage newVCImage = new VCImageUncompressed(
				null,
				(byte[])paddedInfo.paddedArray, DEFAULT_EXTENT/*paddedInfo.paddedExtent*/,
				paddedInfo.paddedISize.getX(),paddedInfo.paddedISize.getY(),paddedInfo.paddedISize.getZ());
		return newVCImage;
	}
	private void initImageDataSet(short[][] dataToSegmentChannels,
			ISize uncroppedISize) throws Exception{
		
		initImageDataSetChannels = new ImageDataset[(dataToSegmentChannels != null?dataToSegmentChannels.length:1)];
		for (int c = 0; c < initImageDataSetChannels.length; c++) {	
			UShortImage[] zImageSet = new UShortImage[uncroppedISize.getZ()];
			for (int i = 0; i < zImageSet.length; i++) {
				short[] shortData = new short[uncroppedISize.getX()*uncroppedISize.getY()];
				if(dataToSegmentChannels != null){
					System.arraycopy(dataToSegmentChannels[c], shortData.length*i, shortData, 0, shortData.length);
				}
				zImageSet[i] = new UShortImage(shortData,DEFAULT_ORIGIN,DEFAULT_EXTENT,uncroppedISize.getX(),uncroppedISize.getY(),1);
			}
			initImageDataSetChannels[c] = new ImageDataset(zImageSet, new double[] { 0.0 }, uncroppedISize.getZ());
		}

	}
	public static IndexColorModel getContrastIndexColorModel(){
		if(indexColorModel == null){
			int[] cmap = new int[256];
			for(int i=0;i<OverlayEditorPanelJAI.CONTRAST_COLORS.length;i+= 1){
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

		}
		return indexColorModel;
	}
	private void initROIComposite(){

		roiComposite = new BufferedImage[getImageDataSetChannel().getISize().getZ()];
		for (int i = 0; i < roiComposite.length; i++) {
			roiComposite[i] = 
				new BufferedImage(getImageDataSetChannel().getISize().getX(), getImageDataSetChannel().getISize().getY(),
						BufferedImage.TYPE_BYTE_INDEXED, getContrastIndexColorModel());
		}
	}
	private static final String RESOLVED_WAIT_MESSG = "Updating...";
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
			try{
				overlayEditorPanelJAI.cropDrawAndConfirm((Rectangle)evt.getNewValue());
				Rectangle rect2D = (Rectangle)evt.getNewValue();
				ROIMultiPaintManager.Crop3D crop3D = new ROIMultiPaintManager.Crop3D();
				crop3D.setBounds(rect2D.x, rect2D.y, 0, rect2D.width, rect2D.height, roiComposite.length);
				cropROIData(crop3D/*,true*/);
			}catch(UserCancelException e){
				updateUndoAfter(null);
			}
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CURRENTROI_PROPERTY)){
			
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_DELETEROI_PROPERTY)){
			try {
				deleteROI((ComboboxROIName)evt.getOldValue());
				updateUndoAfter(false);
			} catch (Exception e) {
				updateUndoAfter(null);
			}
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_ADDNEWROI_PROPERTY)){
			try{
				addNewROI((ComboboxROIName[])evt.getOldValue(),(String)evt.getNewValue());
				updateUndoAfter(false);
			}catch(UserCancelException e){
				updateUndoAfter(null);
			}catch (Exception e) {
				e.printStackTrace();
				DialogUtils.showErrorDialog(overlayEditorPanelJAI, e.getMessage());
				updateUndoAfter(null);
			}

		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CLEARROI_PROPERTY)){
			try {
				FLAG_CLEAR_ROI flag = askClearROI();
				updateUndo(UNDO_INIT.ALLZ);
				clearROI(flag,((ComboboxROIName)evt.getOldValue()).getContrastColorIndex(),OverlayEditorPanelJAI.FRAP_DATA_CLEARROI_PROPERTY);
				updateUndoAfter(true);
			} catch (UserCancelException e) {
				updateUndoAfter(null);
			}
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_BLEND_PROPERTY)){
			overlayEditorPanelJAI.setBlendPercent((Integer)evt.getNewValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_RESOLVEDHIGHLIGHT_PROPERTY)){
			highliteImageWithResolvedSelections((RegionInfo[])evt.getNewValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_RESOLVEDMERGE_PROPERTY)){
			try {
				mergeResolvedSelections((RegionInfo[])evt.getNewValue());
			} catch (Exception e) {
				e.printStackTrace();
				DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Merge failed\n"+e.getMessage());
			}
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_AUTOCROP_PROPERTY)){
			try {
				autoCropQuestion();
			} catch (UserCancelException e) {
				updateUndoAfter(null);
			}
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_HISTOGRAM_PROPERTY)){
			overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_HISTOGRAM_PROPERTY);
			calculateHistogram();
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_HISTOUPDATEHIGHLIGHT_PROPERTY)){
			highlightHistogramPixels((DefaultListSelectionModel)evt.getNewValue());
			wantBlendSetToEnhance();
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_UPDATEROI_WITHHIGHLIGHT_PROPERTY)){
			try {
				updateROIWithHighlight();
				wantBlendSetToEnhance();
			} catch (Exception e) {
				updateUndoAfter(null);
			}
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_UNDERLAY_SMOOTH_PROPERTY)){
			if(!overlayEditorPanelJAI.isHistogramSelectionEmpty()){
				overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_UNDERLAY_SMOOTH_PROPERTY);
			}
			enhanceImageAmount = (Integer)evt.getNewValue();
			smoothUnderlay();
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_DISCARDHIGHLIGHT_PROPERTY)){
			overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_DISCARDHIGHLIGHT_PROPERTY);
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_ADDANALYTIC_PROPERTY)){
			updateUndo(UNDO_INIT.ALLZ);
			sampleAnalyticIntoImage((OverlayEditorPanelJAI.ImgSubVolHelper)evt.getNewValue());
			updateUndoAfter(true);
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_FILL_PROPERTY)){
			updateUndo(UNDO_INIT.ONEZ);
			fillFromPoint((Point)evt.getNewValue());
			updateUndoAfter(true);
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CHANNEL_PROPERTY)){
			imageDatasetChannel = (Integer)evt.getNewValue();
			if(!overlayEditorPanelJAI.isHistogramSelectionEmpty()){
				overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_CHANNEL_PROPERTY);
			}
			updateUnderlayHistogramDisplay();
		}
//		else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_ADDALLDISTINCT_PROPERTY)){
//			askInitialize(true);
//		}
		else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_PAD_PROPERTY)){
			try {
				padROIDataAsk();
			} catch (Exception e) {
				updateUndoAfter(null);
			}
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_DUPLICATE_PROPERTY)){
			try {
				duplicateROIDataAsk();
			} catch (Exception e) {
				updateUndoAfter(null);
			}
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_PAINTERASE_PROPERTY)){
			wantBlendSetToEnhance();
			updateUndo(UNDO_INIT.ONEZ);
			updateUndoAfterPrivate(true,false);
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_UNDOROI_PROPERTY)){
			recoverUndo();
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_PAINTERASE_FINISH_PROPERTY)){
			refreshObjects();
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_FINDROI_PROPERTY)){
			findROI((RegionInfo)evt.getNewValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_SELECTIMGROI_PROPERTY)){
			pickImgROI((SelectImgInfo)evt.getNewValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CONVERTDOMAIN_PROPERTY)){
			convertDomain((Integer)evt.getNewValue(),(RegionInfo[])evt.getOldValue());
		}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_SEPARATE_PROPERTY)){
			updateUndo(UNDO_INIT.ALLZ);
			separateDomains();
			updateUndoAfter(true);
		}
	}
	
	private void sampleAnalyticIntoImage(OverlayEditorPanelJAI.ImgSubVolHelper imgSubVolHelper) {
		try{
			Extent extent = (editedGeometryAttributes==null?originalExtent:editedGeometryAttributes.extent);
			Origin orig = (editedGeometryAttributes==null?originalOrigin:editedGeometryAttributes.origin);
			int numX = roiComposite[0].getWidth();
			int numY = roiComposite[0].getHeight();
			int numZ = roiComposite.length;
			
			int dim = (roiComposite.length>1?1:0) + (roiComposite[0].getHeight()>1?1:0) + 1;
			double cX = calcCoord(imgSubVolHelper.getMousePoint().x, numX, orig.getX(), extent.getX());
			double cY = calcCoord(imgSubVolHelper.getMousePoint().y, numY, orig.getY(), extent.getY());
			double cZ = calcCoord(imgSubVolHelper.getZCenter(), numZ, orig.getZ(), extent.getZ());
			Coordinate center = new Coordinate(cX,cY,cZ);
			AnalyticSubVolume tempSV = GeometrySubVolumePanel.createAnalyticSubVolume(overlayEditorPanelJAI,dim,center,"tempSV");
			tempSV.rebind();
			for (int k=0;k<numZ;k++){
				double coordZ = calcCoord(k, numZ, orig.getZ(), extent.getZ());
				for (int j=0;j<numY;j++){
					double coordY = calcCoord(j, numY, orig.getY(), extent.getY());
					for (int i=0;i<numX;i++){
						double coordX = calcCoord(i, numX, orig.getX(), extent.getX());
						if (tempSV.isInside(coordX,coordY,coordZ,null)){
							((DataBufferByte)roiComposite[k].getRaster().getDataBuffer()).getData()[j*numX+i] = (byte)(imgSubVolHelper.getCurrentSubVolHandle().getContrastColorIndex());
						}
					}
				}
			}
		}catch(UserCancelException uce){
			//ignore
		}catch(Exception e){
			DialogUtils.showErrorDialog(overlayEditorPanelJAI, e.getClass().getName()+" "+e.getMessage());
		}
	}
	
	public class CalcCoords {
		public CalcCoords(){
			
		}
		public double calcX(int xIndex){
			return calcCoord(xIndex, roiComposite[0].getWidth(), (editedGeometryAttributes==null?originalOrigin:editedGeometryAttributes.origin).getX(), (editedGeometryAttributes==null?originalExtent:editedGeometryAttributes.extent).getX());
		}
		public double extentX(){
			return (editedGeometryAttributes==null?originalExtent:editedGeometryAttributes.extent).getX();
		}
		public double calcY(int yIndex){
			return calcCoord(yIndex, roiComposite[0].getHeight(), (editedGeometryAttributes==null?originalOrigin:editedGeometryAttributes.origin).getY(), (editedGeometryAttributes==null?originalExtent:editedGeometryAttributes.extent).getY());
		}
		public double extentY(){
			return (editedGeometryAttributes==null?originalExtent:editedGeometryAttributes.extent).getY();
		}
		public double calcZ(int zIndex){
			return calcCoord(zIndex, roiComposite.length, (editedGeometryAttributes==null?originalOrigin:editedGeometryAttributes.origin).getZ(), (editedGeometryAttributes==null?originalExtent:editedGeometryAttributes.extent).getZ());
		}
		public double extentZ(){
			return (editedGeometryAttributes==null?originalExtent:editedGeometryAttributes.extent).getZ();
		}
	}
	private static double calcCoord(int index,int total,double origin,double extent){
		double unit = (total>1) ? ((double)index)/(total-1):0.5;
		double coord = (total>1) ? origin + extent * unit : 0;
		return coord;
	}
	private class NeighborLocation{
		public int zslice;
		public int xyIndex;
		public NeighborLocation(int zslice, int xyIndex) {
			this.zslice = zslice;
			this.xyIndex = xyIndex;
		}
	}
	enum NEIGHBORS {ZM,ZP,YM,YP,XM,XP};
	private void separateDomains(){
//		ROI highlight = overlayEditorPanelJAI.getHighliteInfo();
//		if(highlight == null){
//			DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Must select domain region(s) before separating.");
//			return;
//		}
		int width = roiComposite[0].getWidth();
		for (int z = 0; z < roiComposite.length; z++) {
			byte[] sliceData = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData();
//			short[] selectedPixels = highlight.getRoiImages()[z].getPixels();
			for (int y = 0; y < roiComposite[0].getHeight(); y++) {
				for (int x = 0; x < roiComposite[0].getWidth(); x++) {
					byte currentByte = sliceData[y*width+x];
					if(currentByte != 0/* && selectedPixels[y*width+x] != 0*/){
						for(NEIGHBORS neighbors : NEIGHBORS.values()){
							NeighborLocation neighborLocation = getNeighborIndex(x,y,z,neighbors);
							if(neighborLocation != null){
								byte neighborByte = ((DataBufferByte)roiComposite[neighborLocation.zslice].getRaster().getDataBuffer()).getData()[neighborLocation.xyIndex];
								if(neighborByte != 0 && neighborByte != currentByte){
//									((DataBufferByte)roiComposite[neighborLocation.zslice].getRaster().getDataBuffer()).getData()[neighborLocation.xyIndex] = 0;
									((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData()[y*width+x] = 0;
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	private NeighborLocation getNeighborIndex(int x,int y, int z,NEIGHBORS neighbor){
		int width = roiComposite[0].getWidth();
		int height = roiComposite[0].getHeight();
		NeighborLocation neighborLocation = null;
		if(neighbor == NEIGHBORS.ZM){
			if(z > 0){
				neighborLocation = new NeighborLocation(z-1, y*width+x);
			}
		}else if(neighbor == NEIGHBORS.ZP){
			if(z<roiComposite.length-1){
				neighborLocation = new NeighborLocation(z+1, y*width+x);
			}
		}else if(neighbor == NEIGHBORS.YM){
			if(y > 0){
				neighborLocation = new NeighborLocation(z, (y-1)*width+x);
			}
		}else if(neighbor == NEIGHBORS.YP){
			if(y < height-1){
				neighborLocation = new NeighborLocation(z, (y+1)*width+x);
			}
		}else if(neighbor == NEIGHBORS.XM){
			if(x > 0){
				neighborLocation = new NeighborLocation(z, y*width+x-1);
			}
		}else if(neighbor == NEIGHBORS.XP){
			if(x < width-1){
				neighborLocation = new NeighborLocation(z, y*width+x+1);
			}
		}
		return neighborLocation;
	}
	private void convertDomain(int convertToContrastIndex,RegionInfo[] selectedRegionInfos){
		if(selectedRegionInfos == null || selectedRegionInfos.length == 0){
			return;
		}
		updateUndo(UNDO_INIT.ALLZ);
		TreeSet<Integer> selectedRegionIndexesTS = new TreeSet<Integer>();
		for (int i = 0; i < selectedRegionInfos.length; i++) {
			selectedRegionIndexesTS.add(selectedRegionInfos[i].getRegionIndex());
		}

		byte[] shortEncodedRegionIndexArr = regionImage.getShortEncodedRegionIndexImage();
		int count = 0;
		for (int z = 0; z < roiComposite.length; z++) {
			byte[] roiBytes = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData();
			for (int j = 0; j < roiBytes.length; j++) {
				int regionIndex = (int)((0x000000ff & shortEncodedRegionIndexArr[2 * count]) | ((0x000000ff & shortEncodedRegionIndexArr[2 * count + 1]) << 8));
				if(selectedRegionIndexesTS.contains(regionIndex)){
					roiBytes[j] = (byte)convertToContrastIndex;
				}
				count++;
			}
		}
		updateUndoAfter(true);
	}
	public static enum SELECT_FUNC{
		REMOVE,
		ADD,
		REPLACE
	};
	private Comparator<RegionInfo> regionInfoComparator = new Comparator<RegionImage.RegionInfo>() {
		public int compare(RegionInfo o1, RegionInfo o2) {
			return o1.getRegionIndex()-o2.getRegionIndex();
		}
	};
	private void pickImgROI(SelectImgInfo selectImgInfo){
		int[] mapOrigToSort = new int[sortedRegionInfos.length];
		for (int i = 0; i < mapOrigToSort.length; i++) {
			mapOrigToSort[sortedRegionInfos[i].getRegionIndex()] = i;
		}
		RegionInfo[] sortedSelectedRegionInfos = Arrays.asList(selectImgInfo.getResolvedList().getSelectedValues()).toArray(new RegionInfo[0]);
		Arrays.sort(sortedSelectedRegionInfos, regionInfoComparator);

		int z = overlayEditorPanelJAI.getZ();//(B_DISPLAY_ZERO_INDEX_Z?overlayEditorPanelJAI.getZ():overlayEditorPanelJAI.getZ()-1);
//		RegionInfo[] regionInfoSortedByRegionIndex = regionImage.getRegionInfos();
		for (int height = 0; height <= selectImgInfo.getRectangle().height; height++) {
			int y = selectImgInfo.getRectangle().y+height;//(int)(selectImgInfo.getMouseEvent().getPoint().getY()/selectImgInfo.getZoom());				
			for (int width = 0; width <= selectImgInfo.getRectangle().width; width++) {
				int x = selectImgInfo.getRectangle().x+width;//(int)(selectImgInfo.getMouseEvent().getPoint().getX()/selectImgInfo.getZoom());
				if(x<0 || x >= roiComposite[0].getWidth() || y<0 || y>=roiComposite[0].getHeight()){
					return;
				}
				int currentIndex = (z*roiComposite[0].getWidth()*roiComposite[0].getHeight()) + (roiComposite[0].getWidth()*y) + x;
				RegionInfo foundRegion = regionImage.getRegionInfoFromOffset(currentIndex);
				int foundsortIndex = Arrays.binarySearch(sortedSelectedRegionInfos, foundRegion, regionInfoComparator);
				boolean isAlreadySelected = (foundsortIndex >= 0);
				if(isAlreadySelected && selectImgInfo.isIgnoreIfSelected()){
					return;
				}
//				if(SwingUtilities.isLeftMouseButton(selectImgInfo.getMouseEvent())){
					if(selectImgInfo.getMouseEvent().isControlDown()){
						if(isAlreadySelected){
							overlayEditorPanelJAI.resolvedSelectionChange(SELECT_FUNC.REMOVE, mapOrigToSort[foundRegion.getRegionIndex()]);
						}else{
							overlayEditorPanelJAI.resolvedSelectionChange(SELECT_FUNC.ADD, mapOrigToSort[foundRegion.getRegionIndex()]);
						}
					}else{
						overlayEditorPanelJAI.resolvedSelectionChange(SELECT_FUNC.REPLACE, mapOrigToSort[foundRegion.getRegionIndex()]);
					}
//				}
			}
		}
	}
	
	private static class InterruptCalc implements ClientTaskStatusSupport{
		private boolean bInterrupt = false;
		private Thread thread;
		public InterruptCalc(Thread thread) {
			this.thread = thread;
		}
		public void interrupt(){
			bInterrupt = true;
		}
		public void setMessage(String message) {
		}
		public void setProgress(int progress) {
		}
		public int getProgress() {
			return 0;
		}
		public boolean isInterrupted() {
			return bInterrupt;
		}
		public void addProgressDialogListener(
				ProgressDialogListener progressDialogListener) {
		}
		public Thread getThread(){
			return thread;
		}
	}
	private InterruptCalc[] lastInterruptRegionCalculation = new InterruptCalc[1];
	private RegionImage regionImage;
	private RegionImage.RegionInfo[] sortedRegionInfos;
	private synchronized InterruptCalc getInterruptCalc(InterruptCalc[] checkThisInterruptCalc) throws Exception{
		if(checkThisInterruptCalc[0] != null){
			//interrupt incoming InterruptCalc and wait for it to die
			checkThisInterruptCalc[0].interrupt();
			checkThisInterruptCalc[0].getThread().join(30000);//wait up to 30 seconds for thread to die then throw error
			if(checkThisInterruptCalc[0].getThread().isAlive()){
				throw new Exception("Waiting InterruptCalc thread did not return.");
			}
		}
		checkThisInterruptCalc[0] =  new InterruptCalc(Thread.currentThread());
		return checkThisInterruptCalc[0];
	}
	private void refreshObjects(){
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI, new Hashtable<String, Object>(), getRefreshObjectsTasks());
	}
	private static final String LOCAL_REGION_IMAGE = "LOCAL_REGION_IMAGE";
	private AsynchClientTask createRegionImageTask = new AsynchClientTask("create RegionImage",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			InterruptCalc localInterruptCalc = getInterruptCalc(lastInterruptRegionCalculation);
			VCImage checkImage = ROIMultiPaintManager.createVCImageFromBufferedImages(ROIMultiPaintManager.DEFAULT_EXTENT, roiComposite);
			RegionImage localRegionImage =
				new RegionImage(checkImage, 0 /*0 means generate no surfacecollection*/,
						checkImage.getExtent(),ROIMultiPaintManager.DEFAULT_ORIGIN, RegionImage.NO_SMOOTHING,
						localInterruptCalc);
			if(localInterruptCalc.isInterrupted()){
				throw UserCancelException.CANCEL_GENERIC;
			}
			hashTable.put(LOCAL_REGION_IMAGE, localRegionImage);
		}		
	};
	private AsynchClientTask saveSortRegionImageTask = new AsynchClientTask("create RegionImage",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			regionImage = (RegionImage)hashTable.get(LOCAL_REGION_IMAGE);
			sortedRegionInfos = regionImage.getRegionInfos();//returns copy
			Arrays.sort(sortedRegionInfos,new Comparator<RegionInfo>() {
				public int compare(RegionInfo o1, RegionInfo o2) {
					int retval = o2.getNumPixels() - o1.getNumPixels();
					if(retval == 0){
						retval =  o1.getPixelValue() - o2.getPixelValue();
					}
					return retval;
				}
			});
		}
	};
	private AsynchClientTask updateList = new AsynchClientTask("update List",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			overlayEditorPanelJAI.setResolvedList(sortedRegionInfos);
		}
	};

	private AsynchClientTask[] getRefreshObjectsTasks(){
		return new AsynchClientTask[] {createRegionImageTask,saveSortRegionImageTask,updateList};
	}
	//-----UNDO methods-------------------------
	private BufferedImage[] undoROIComposite;
	private enum UNDO_INIT {ALLZ,ONEZ};
	private void updateUndoAfterPrivate(Boolean bUndoable,boolean bRefresh){
		if(bUndoable != null && !bUndoable){
			//Remove undo because caller of this method did something that CANNOT be undone
			undoROIComposite = null;
		}
		//update GUI with undo info
		overlayEditorPanelJAI.setUndoAndFocus(bUndoable);
		if(bUndoable != null){
			overlayEditorPanelJAI.setResolvedList(new String[] {RESOLVED_WAIT_MESSG});
		}
		if(bUndoable != null && bRefresh){
			refreshObjects();
		}
	}
	private void updateUndoAfter(Boolean bUndoable){
		updateUndoAfterPrivate(bUndoable,true);	
	}
	private void updateUndo(UNDO_INIT initType){
		//Caller of this method is going to do something that CAN be undone so save undo info
		if(initType == UNDO_INIT.ALLZ){
			undoROIComposite = new BufferedImage[roiComposite.length];
			for (int i = 0; i < roiComposite.length; i++) {
				undoROIComposite[i] =
					new BufferedImage(roiComposite[0].getWidth(),roiComposite[0].getHeight(),
							BufferedImage.TYPE_BYTE_INDEXED, getContrastIndexColorModel());
				System.arraycopy((byte[])((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData(), 0,
						(byte[])((DataBufferByte)undoROIComposite[i].getRaster().getDataBuffer()).getData(), 0,
						roiComposite[0].getWidth()*roiComposite[0].getHeight());
			}
		}
		if(initType == UNDO_INIT.ONEZ){
			//Some operations (paint,erase,fill) only need 1 zslice saved for undo
			if(undoROIComposite == null || undoROIComposite[overlayEditorPanelJAI.getZ()] == null){
				undoROIComposite = new BufferedImage[roiComposite.length];
				undoROIComposite[overlayEditorPanelJAI.getZ()] =
						new BufferedImage(roiComposite[0].getWidth(),roiComposite[0].getHeight(),
								BufferedImage.TYPE_BYTE_INDEXED, getContrastIndexColorModel());
			}
			System.arraycopy((byte[])((DataBufferByte)roiComposite[overlayEditorPanelJAI.getZ()].getRaster().getDataBuffer()).getData(), 0,
				(byte[])((DataBufferByte)undoROIComposite[overlayEditorPanelJAI.getZ()].getRaster().getDataBuffer()).getData(), 0,
				roiComposite[0].getWidth()*roiComposite[0].getHeight());
		}
	}
	private void recoverUndo(){
		try {
			if(roiComposite == null || undoROIComposite == null || 
					roiComposite.length != undoROIComposite.length){
				throw new Exception("Undo operation has wrong undo information");
			}else{
				//Reset to last undo
				for (int i = 0; i < roiComposite.length; i++) {
					if(undoROIComposite[i] != null){
						if(roiComposite[i].getWidth() != undoROIComposite[i].getWidth() ||
							roiComposite[i].getHeight() != undoROIComposite[i].getHeight()){
							throw new Exception("Undo operation z-slice size not match");
						}
						System.arraycopy((byte[])((DataBufferByte)undoROIComposite[i].getRaster().getDataBuffer()).getData(), 0,
							(byte[])((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData(), 0,
							roiComposite[0].getWidth()*roiComposite[0].getHeight());
					}
				}
				undoROIComposite = null;
				overlayEditorPanelJAI.setAllROICompositeImage(roiComposite,OverlayEditorPanelJAI.FRAP_DATA_FILL_PROPERTY);
				overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_FILL_PROPERTY);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//Something is wrong, throw out undo
			undoROIComposite = null;
			System.out.println("Error: undoROIComposite inconsistent state.\n"+e.getMessage());

		}
		updateUndoAfter(false);
	}
	//-----End UNDO methods-------------------------
	
	private void findROI(final RegionInfo findRegionInfo){
		if(findRegionInfo == null){
			return;
		}
		System.out.println("regIndex="+findRegionInfo.getRegionIndex()+" pixVal="+findRegionInfo.getPixelValue()+" numPix="+findRegionInfo.getNumPixels());
		final String COORDINDEX = "COORDINDEX";
		final String START_THREAD = "START_THREAD";
		AsynchClientTask findCoordTask = new AsynchClientTask("Find coordinate...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				hashTable.put(START_THREAD, Thread.currentThread());
				int regionImageSize = regionImage.getNumXY()*regionImage.getNumZ();
				for (int i = 0; i < regionImageSize; i++) {
					if(findRegionInfo.isIndexInRegion(i)){
						int z = i/regionImage.getNumXY();
						int y = (i%regionImage.getNumXY())/regionImage.getNumX();
						int x = i%regionImage.getNumX();
						CoordinateIndex coordinateIndex =
							new CoordinateIndex(x, y, (B_DISPLAY_ZERO_INDEX_Z?z:z+1));
						hashTable.put(COORDINDEX, coordinateIndex);
						break;
					}
				}
			}
		};
		AsynchClientTask drawStartTask = new AsynchClientTask("drawStar",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				CoordinateIndex coordinateIndex = (CoordinateIndex)hashTable.get(COORDINDEX);
				if(coordinateIndex == null){
					throw new Exception("Couldn't find coordinate of first pixel for regionInfo");
				}
				overlayEditorPanelJAI.placeMarkerOverResolved(coordinateIndex,7);
			}
		};
		AsynchClientTask waitTask = new AsynchClientTask("wait",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Thread.sleep(1000);
			}
		};
		AsynchClientTask clearStarTask = new AsynchClientTask("drawStar",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				overlayEditorPanelJAI.placeMarkerOverResolved(null,0);
			}
		};
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI,new Hashtable<String, Object>(), new AsynchClientTask[] {findCoordTask,drawStartTask,waitTask,clearStarTask});

	}
	private void duplicateROIDataAsk(){
		String extraInfo = null;
		while(true){
			try{
				String zCountS = DialogUtils.showInputDialog0(overlayEditorPanelJAI, "Convert 2D to 3D. Enter desired Z count:"+(extraInfo==null?"":"\n"+extraInfo), "1");
				int zCount = Integer.parseInt(zCountS);
				if(zCount == 1){
					throw UtilCancelException.CANCEL_GENERIC;
				}
				duplicateROIData(zCount);
				break;
			}catch(UtilCancelException uce){
				throw UserCancelException.CANCEL_GENERIC;
			}catch(Exception e){
				extraInfo = "Error: Z count must be >= 1:\n'"+e.getMessage()+"'";
			}
		}
	}
	
	public static double getProperScale(int newSize,int oldSize){
		double fac = (double)newSize/(double)oldSize;
		int newSize2 = ((int)((double)oldSize*fac));
		if(newSize2 != newSize){
			fac = Math.nextAfter(fac,fac+Math.signum(newSize-newSize2));
			newSize2 = ((int)((double)oldSize*fac));
			if(newSize2 != newSize){
				throw new RuntimeException("Unexpected scaling error");
			}
		}
		return fac;
	}
	public static class ResizeInfo{
		public short[][][] underlayC;
		public BufferedImage[] roiComposite;
		
		private ISize origISize;
		private ISize newISize;
		private CoordinateIndex lowPadCrop;
		private CoordinateIndex highPadCrop;
		private ResizeInfo(ISize origISize, ISize newISize, CoordinateIndex lowPadCrop,CoordinateIndex highPadCrop) {
			if((newISize == null && (lowPadCrop==null || highPadCrop==null)) ||
				(newISize != null && (lowPadCrop!=null || highPadCrop!=null)) ||
				(lowPadCrop==null && highPadCrop!= null) || (lowPadCrop!= null && highPadCrop==null)){
				throw new IllegalArgumentException("Unexpected args for "+ResizeInfo.class.getName());
			}
			this.origISize = origISize;
			this.newISize = newISize;
			this.lowPadCrop = lowPadCrop;
			this.highPadCrop = highPadCrop;
			
			check();
		}
		private void check(){
			//check
			ISize isize = getNewSize();
			if(isize.getX() == 0 || isize.getY() == 0 || isize.getZ() == 0){
				throw new IllegalArgumentException("New ISize cannot have 0");
			}
			if(isPadCrop() == isScaling()){
				throw new IllegalArgumentException("padcrop and scaling flags can't be the same");
			}

		}
		public static ResizeInfo createScaleResizeInfo(ISize origISize, ISize newISize){
			return new ResizeInfo(origISize, newISize, null,null);
		}
		public static ResizeInfo createPadCropResizeInfo(ISize origISize,CoordinateIndex lowPadCrop,CoordinateIndex highPadCrop){
			return new ResizeInfo(origISize, null, lowPadCrop,highPadCrop);
		}
		public boolean isScaling(){
			return newISize != null;
		}
		public boolean isPadCrop(){
			return lowPadCrop!=null && highPadCrop!=null;
		}
		public ISize getOriginalSize(){
			return origISize;
		}
		public ISize getNewSize(){
			if(isScaling()){
				return newISize;
			}
			return new ISize(origISize.getX()+lowPadCrop.x+highPadCrop.x,origISize.getY()+lowPadCrop.y+highPadCrop.y,origISize.getZ()+lowPadCrop.z+highPadCrop.z);
		}
		public CoordinateIndex getLowPadCrop(){
			return lowPadCrop;
		}
		public CoordinateIndex getHighPadCrop(){
			return highPadCrop;
		}
		public void setResizedData(short[][][] underlayC,BufferedImage[] roiComposite){
			this.underlayC = underlayC;
			this.roiComposite = roiComposite;
		}
	}
	
	public static void resizeImpl0(ResizeInfo resizeInfo,ImageDataset[] originalData,BufferedImage[] roiComposite){
		ISize newISize = resizeInfo.getNewSize();
		ISize oldISize = resizeInfo.getOriginalSize();
		if(originalData != null && !originalData[0].getISize().compareEqual(oldISize)){
			throw new IllegalArgumentException("Original size not match original data");
		}
		if(roiComposite.length != oldISize.getZ() || roiComposite[0].getWidth() != oldISize.getX() || roiComposite[0].getHeight() != oldISize.getY()){
			throw new IllegalArgumentException("Original size not match roiComposite");
		}
		short[][][] newUnderlayZ = (originalData!=null?new short[originalData.length][newISize.getZ()][newISize.getX()*newISize.getY()]:null);;
		BufferedImage[] newROIZ = new BufferedImage[newISize.getZ()];
		if(resizeInfo.isScaling()){
			int oldXY = oldISize.getX()*oldISize.getY();
			double oldIncrX = getProperScale(oldISize.getX(),newISize.getX());
			double oldIncrY = getProperScale(oldISize.getY(),newISize.getY());
			double oldIncrZ = getProperScale(oldISize.getZ(),newISize.getZ());
			for (int z = 0; z < newISize.getZ(); z++) {
				int oldIndexZ = (int)(z*oldIncrZ);
				newROIZ[z] = new BufferedImage(newISize.getX(), newISize.getY(), BufferedImage.TYPE_BYTE_INDEXED, getContrastIndexColorModel());
				byte[] newROIZArr = ((DataBufferByte)newROIZ[z].getRaster().getDataBuffer()).getData();
				byte[] oldROIZArr = ((DataBufferByte)roiComposite[oldIndexZ].getData().getDataBuffer()).getData();
				int newXYIndex = 0;
				for (int y = 0; y < newISize.getY(); y++) {
					int oldXYIndex = (((int)(oldIncrY*y))*oldISize.getX());
					for (int x = 0; x < newISize.getX(); x++) {
						int oldXIndex = oldXYIndex + (int)(x*oldIncrX);
						if(originalData != null){
							for (int c = 0; c < originalData.length; c++) {
								short[] oldUnderlayZ = originalData[c].getAllImages()[oldIndexZ].getPixels();
//								if(true){//smoothing
//									int cnt = (x>0?1:0)+(x<(newISize.getX()-1)?1:0)+(y>0?1:0)+(y<(newISize.getY()-1)?1:0)+(z>0?1:0)+(x<(newISize.getZ()-1)?1:0);
//									int zm = (int)((z-1)*oldIncrZ);
//									int zp = (int)((z+1)*oldIncrZ);
//									int ym = (((int)(oldIncrY*(y-1)))*oldISize.getX())+(int)(x*oldIncrX);
//									int yp = (((int)(oldIncrY*(y+1)))*oldISize.getX())+(int)(x*oldIncrX);
//									int xm = oldXYIndex + (int)((x-1)*oldIncrX);
//									int xp = oldXYIndex + (int)((x+1)*oldIncrX);
////									System.out.println("cnt="+cnt+" newXYIndex="+newXYIndex+"   base "+x+" "+y+" "+z+"   m "+xm+" "+ym+" "+zm+"   p "+xp+" "+yp+" "+zp);
//									int sum = oldUnderlayZ[oldXIndex]+
//											(x>0?oldUnderlayZ[xm]:0)+
//											(x<(newISize.getX()-1)?oldUnderlayZ[xp]:0)+
//											(y>0?oldUnderlayZ[ym]:0)+
//											(y<(newISize.getY()-1)?oldUnderlayZ[yp]:0)+
//											(z>0?originalData[c].getAllImages()[zm].getPixels()[oldXIndex]:0)+
//											(z<(newISize.getZ()-1)?originalData[c].getAllImages()[zp].getPixels()[oldXIndex]:0);
//									newUnderlayZ[c][z][newXYIndex] = (short)((double)sum/(double)(cnt+1));
//								}else{//nearest neighbor
//									newUnderlayZ[c][z][newXYIndex] = oldUnderlayZ[oldXIndex];
//								}
								newUnderlayZ[c][z][newXYIndex] = oldUnderlayZ[oldXIndex];
							}
						}
						newROIZArr[newXYIndex] = oldROIZArr[oldXIndex];
						newXYIndex++;
					}
				}
			}
		}else if(resizeInfo.isPadCrop()){
			CoordinateIndex low = resizeInfo.getLowPadCrop();
			CoordinateIndex high = resizeInfo.getHighPadCrop();
			for (int z = 0; z < newISize.getZ(); z++) {
				newROIZ[z] = new BufferedImage(newISize.getX(), newISize.getY(), BufferedImage.TYPE_BYTE_INDEXED, getContrastIndexColorModel());
				byte[] newROIZArr = ((DataBufferByte)newROIZ[z].getRaster().getDataBuffer()).getData();
				int oldZIndex = z-low.z;
				byte[] oldROIZArr = (oldZIndex<0 || (z+high.z)>=newISize.getZ()?null:((DataBufferByte)roiComposite[oldZIndex].getData().getDataBuffer()).getData());
				if(oldROIZArr == null){
					continue;
				}
				for (int y = 0; y < newISize.getY(); y++) {
					int oldYIndex = y-low.y;
					if(oldYIndex<0 || (y+high.y)>=newISize.getY()){
						continue;
					}
					int oldXYIndex = (((int)oldYIndex)*oldISize.getX());
					int newXYIndex = y*newISize.getX();
					for (int x = 0; x < newISize.getX(); x++) {
						int oldXIndex = x-low.x;
						if(oldXIndex<0 || (x+high.x)>=newISize.getX()){
							continue;
						}
						int oldFinalIndex = oldXYIndex+oldXIndex;
						int newfinalIndex = newXYIndex+x;
						if(originalData != null){
							for (int c = 0; c < originalData.length; c++) {
								short[] oldUnderlayZ = originalData[c].getAllImages()[(int)oldZIndex].getPixels();
								newUnderlayZ[c][z][newfinalIndex] = oldUnderlayZ[oldFinalIndex];
							}
						}
						newROIZArr[newfinalIndex] = oldROIZArr[oldFinalIndex];
//						System.out.println(oldXIndex+" "+oldYIndex+" "+oldZIndex+" -- "+x+" "+y+" "+z+" newIndx="+newXYIndex+" oldIndex="+oldFinalIndex);
					}
				}
			}
		}else{
			throw new RuntimeException("Unexpected "+ResizeInfo.class.getName()+" state.");
		}
		
		resizeInfo.setResizedData(newUnderlayZ,newROIZ);

	}
	private void resizeImpl(ResizeInfo resizeInfo,ClientTaskStatusSupport clientTaskStatusSupport) throws Exception{		
		resizeImpl0(resizeInfo, (bHasOriginalData?initImageDataSetChannels:null), roiComposite);
		ISize newISize = resizeInfo.getNewSize();
		ISize oldISize = resizeInfo.getOriginalSize();
		Extent resizeExtent = createResizeExtent(originalExtent, oldISize, newISize);

		if(bHasOriginalData){
			for (int c = 0; c < initImageDataSetChannels.length; c++) {
				UShortImage[] zImageSet = new UShortImage[newISize.getZ()];
				for (int z = 0; z < zImageSet.length; z++) {
					zImageSet[z] = new UShortImage(resizeInfo.underlayC[c][z],originalOrigin,resizeExtent,newISize.getX(),newISize.getY(),1);
				}
				initImageDataSetChannels[c] = new ImageDataset(zImageSet, new double[] { 0.0 },newISize.getZ());
			}
		}else{
			initImageDataSet(null, newISize);
		}
		
		roiComposite = resizeInfo.roiComposite;

		updateAuxiliaryInfo(originalISize,clientTaskStatusSupport);

	}
	private void resizeDataset(){				
		final ISize currentISize = new ISize(roiComposite[0].getWidth(), roiComposite[0].getHeight(), roiComposite.length);
		ImageSizeInfo origImageSizeInfo = new ImageSizeInfo(null, currentISize, 0, new double[] {0}, 0);
		ImageSizeInfo newImageSizeInfo = null;
		
		try{
			newImageSizeInfo = ClientRequestManager.queryImageResize(overlayEditorPanelJAI, origImageSizeInfo, false);
		}catch(UserCancelException e){
			return;
		}
		
		final ISize newImageSize = newImageSizeInfo.getiSize();
		AsynchClientTask resizeTask = new AsynchClientTask("Resizing...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				resizeImpl(ResizeInfo.createScaleResizeInfo(currentISize, newImageSize),getClientTaskStatusSupport());
			}
		};
		final AsynchClientTask updatePanelTask = getUpdateDisplayAfterCropTask();
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI,new Hashtable<String, Object>(),
				new AsynchClientTask[] {resizeTask,updatePanelTask},false,false,null,true);

	}
	
//	private void scaleZ(ISize currentISize,float scale){
//		if(scale == 1.0f){
//			return;
//		}
//		int newZSize = (int)(currentISize.getZ()*scale);
//		if(bHasOriginalData){
//			for (int c = 0; c < initImageDataSetChannels.length; c++) {
//				UShortImage[] zImageSet = new UShortImage[newZSize];
//				//extract plane along y axis
//				short[] xzPlane = new short[currentISize.getX()*currentISize.getZ()];
//				for (int y = 0; y < currentISize.getY(); y++) {
//					for (int z = 0; z < initImageDataSetChannels[0].getAllImages().length; z++) {
//						short[] zslice = initImageDataSetChannels[0].getAllImages()[z].getPixels();
//						System.arraycopy(zslice, y*currentISize.getX(), xzPlane, z*currentISize.getX(), currentISize.getX());
//					}
//					PlanarImage planarImage = resizeXYUShort(xzPlane, currentISize.getX(), currentISize.getZ(), 1.0f, scale);
//				}
//			}
//		}
//	}
	private void padROIDataAsk(){
		final String CANCEL = "Cancel";
		final String RESIZE = "Resize...";
		final String BORDERS = "Borders...";
		String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI, "Add/Crop borders or Resize whole dataset?", new String[] {RESIZE,BORDERS,CANCEL}, RESIZE);
		if(result != null && result.equals(RESIZE)){
			resizeDataset();
		}else if(result != null && result.equals(BORDERS)){
			padCropDataset();
		}else{
			return;
		}
	}
	private void fillFromPoint(Point fillPoint){
		ROI.fillAtPoint(fillPoint.x,fillPoint.y,
				roiComposite[overlayEditorPanelJAI.getZ()],
				overlayEditorPanelJAI.getCurrentROIInfo().getHighlightColor().getRGB());
		overlayEditorPanelJAI.setAllROICompositeImage(roiComposite,OverlayEditorPanelJAI.FRAP_DATA_FILL_PROPERTY);
		overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_FILL_PROPERTY);
	}
	private void smoothUnderlay(){
		AsynchClientTask smoothTask = new AsynchClientTask("Processing Image...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ClientTaskStatusSupport localClientTaskStatusSupport = 
					new ClientTaskStatusSupport() {
						public void setProgress(int progress) {
							if(getClientTaskStatusSupport() != null){
								getClientTaskStatusSupport().setProgress((int)(.9*progress));
							}
						}
						public void setMessage(String message) {}
						public boolean isInterrupted() {
							return (getClientTaskStatusSupport()==null?false:getClientTaskStatusSupport().isInterrupted());
						}
						public int getProgress() {return 0;}
						public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
							throw new RuntimeException("not yet implemented");
						}
					};
				enhancedImageDatasetChannels = smoothImageDataset(initImageDataSetChannels,enhanceImageAmount,localClientTaskStatusSupport);
				if(localClientTaskStatusSupport.isInterrupted()){
					throw UserCancelException.CANCEL_GENERIC;
				}
				localClientTaskStatusSupport.setMessage("Calculating histogram...");
				condensedBinsMapChannels = calculateCondensedBinsChannels0(getImageDataset());
				allPixelValuesRangeChannels = calculateAllPixelValuesRangeChannels0(getImageDataset());
			}
		};
		AsynchClientTask updateDisplayTask = new AsynchClientTask("Updating display...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if(getClientTaskStatusSupport() != null){
					getClientTaskStatusSupport().setProgress(100);
				}
				updateUnderlayHistogramDisplay();
			}
		};
		if(calculateCurrentSize() > 2000000){//show update progress only if large
			ClientTaskDispatcher.dispatch(overlayEditorPanelJAI, new Hashtable<String, Object>(),
					new AsynchClientTask[] {smoothTask,updateDisplayTask},
					true, true, null, true);
			
		}else{
			ClientTaskDispatcher.dispatch(overlayEditorPanelJAI, new Hashtable<String, Object>(),
					new AsynchClientTask[] {smoothTask,updateDisplayTask});

		}
	}
	private int calculateCurrentSize(){
		int size = 0;
		for (int i = 0; i < roiComposite.length; i++) {
			size+= roiComposite[i].getWidth()*roiComposite[i].getHeight();
		}
		return size;
	}
	private void updateUnderlayHistogramDisplay(){
		overlayEditorPanelJAI.setImages(getImageDataSetChannel(),
				OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR, OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR,
				allPixelValuesRangeChannels[imageDatasetChannel]);
		if(condensedBinsMapChannels != null){
			overlayEditorPanelJAI.setHistogram(condensedBinsMapChannels[imageDatasetChannel]);
		}
		overlayEditorPanelJAI.setUnderlayState(!bHasOriginalData);
	}
	private ImageDataset[] smoothImageDataset(ImageDataset[] origImageDatasetChannels,int enhanceImageAmount,ClientTaskStatusSupport clientTaskStatusSupport) throws Exception{
		if(enhanceImageAmount == ROIMultiPaintManager.ENHANCE_NONE){
			return null;
		}
		ImageDataset[] smoothedImageDatasetChannels = new ImageDataset[origImageDatasetChannels.length];
		int allCount = origImageDatasetChannels.length*origImageDatasetChannels[0].getSizeZ();
		int progress = 0;
		for (int c = 0; c < origImageDatasetChannels.length; c++) {
			UShortImage[] smoothedZSections = new UShortImage[origImageDatasetChannels[c].getISize().getZ()];
			int radius = enhanceImageAmount;//*2+1;
//			int[] divideTable = BoxBlurFilter.createDivideTable(radius);
			short[] intermediateArr = new short[origImageDatasetChannels[c].getAllImages()[0].getPixels().length];
			for (int z = 0; z < origImageDatasetChannels[c].getAllImages().length; z++) {
				smoothedZSections[z] =
					new UShortImage(new short[origImageDatasetChannels[c].getISize().getX()*origImageDatasetChannels[c].getISize().getY()],
							ROIMultiPaintManager.DEFAULT_ORIGIN,ROIMultiPaintManager.DEFAULT_EXTENT,
							origImageDatasetChannels[c].getISize().getX(),origImageDatasetChannels[c].getISize().getY(),1);
				short[] enhancedData = smoothedZSections[z].getPixels();
				short[] roiSourceData = origImageDatasetChannels[c].getAllImages()[z].getPixels();
				
				BoxBlurFilter.blur(roiSourceData, intermediateArr, origImageDatasetChannels[c].getISize().getX(), origImageDatasetChannels[c].getISize().getY(), radius/*,divideTable*/);
				BoxBlurFilter.blur(intermediateArr, enhancedData, origImageDatasetChannels[c].getISize().getY(), origImageDatasetChannels[c].getISize().getX(), radius/*,divideTable*/);
				if(clientTaskStatusSupport != null){
					if(clientTaskStatusSupport.isInterrupted()){
						return null;
					}
					progress++;
					clientTaskStatusSupport.setProgress(progress*100/allCount);
				}
			}
			ImageDataset smoothedImageDataset =
				new ImageDataset(smoothedZSections, new double[]{0}, origImageDatasetChannels[c].getISize().getZ());
			smoothedImageDatasetChannels[c] = smoothedImageDataset;
		}

		return smoothedImageDatasetChannels;

	}
	private void updateROIWithHighlight(){
		if(overlayEditorPanelJAI.getHighliteInfo() != null){
			final String applyROI = "Update Domain";
			final String createROI = "Create Domain";
			final String cancel = "Cancel";
			String result = null;
			
			if(overlayEditorPanelJAI.getCurrentROIInfo() != null){
				result = DialogUtils.showWarningDialog(overlayEditorPanelJAI,
					"Apply histogram highlighted regions. Choose an action:\n"+
					"1. Update the current Domain '"+overlayEditorPanelJAI.getCurrentROIInfo().getROIName()+"' using the histogram highlight.\n"+
					"2. Create a new Domain with the histogram highlight.\n",
					new String[]{applyROI,createROI,cancel},
					applyROI);
			}else{
				result = createROI;	
			}
			if(result.equals(cancel)){
				throw UserCancelException.CANCEL_GENERIC;
			}
			try{
				if(result.equals(createROI)){
					boolean bOverWrite = askApplyHighlightToROI();
					addNewROI(overlayEditorPanelJAI.getAllCompositeROINamesAndColors(),null);
					applyHighlightToROI(overlayEditorPanelJAI.getCurrentROIInfo(),bOverWrite);
					updateUndoAfter(false);
				}else if(result.equals(applyROI)){
					boolean bOverWrite = true;
					if(overlayEditorPanelJAI.getAllCompositeROINamesAndColors().length > 1){
						bOverWrite = askApplyHighlightToROI();
					}
					updateUndo(UNDO_INIT.ALLZ);
					applyHighlightToROI(overlayEditorPanelJAI.getCurrentROIInfo(),bOverWrite);
					updateUndoAfter(true);
				}
			}catch(UserCancelException e){
				return;
			}catch (Exception e) {
				e.printStackTrace();
				DialogUtils.showErrorDialog(overlayEditorPanelJAI, e.getMessage());
			}

		}else{
			DialogUtils.showWarningDialog(overlayEditorPanelJAI, "No highlighted regions exist to update Domains with");
		}

	}
	private void highlightHistogramPixels(final DefaultListSelectionModel histSelection){
		final String HISTO_HIGHLIGHT = "HISTO_HIGHLIGHT";
		AsynchClientTask histoROITask = new AsynchClientTask("Calculating histogram highlight...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ROI highlight = overlayEditorPanelJAI.getHighliteInfo();
				if(highlight == null){
					highlight = createEmptyROI(new ISize(roiComposite[0].getWidth(),roiComposite[0].getHeight(),roiComposite.length));
				}
				for (int i = 0; i < highlight.getRoiImages().length; i++) {
					short[] pixels = highlight.getRoiImages()[i].getPixels();
					for (int j = 0; j < pixels.length; j++) {
						int currPixelVal = getImageDataSetChannel().getAllImages()[i].getPixels()[j]&0x0000FFFF;
						if(histSelection != null && histSelection.isSelectedIndex(currPixelVal)){
							pixels[j] = (short)0xFFFF;							
						}else{
							pixels[j] = 0;
						}
					}
				}
				hashTable.put(HISTO_HIGHLIGHT, highlight);
			}
		};
		AsynchClientTask updatedisplayTask = new AsynchClientTask("Updating display...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				overlayEditorPanelJAI.setHighliteInfo((ROI)hashTable.get(HISTO_HIGHLIGHT),OverlayEditorPanelJAI.FRAP_DATA_HISTOUPDATEHIGHLIGHT_PROPERTY);
			}
		};
		
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI, new Hashtable<String, Object>(),
				new AsynchClientTask[] {histoROITask,updatedisplayTask}/*,false,false,null,true*/);
	}
	
	private void autoCropQuestion(){
		final String useUnderlying = "Use Underlying...";
		final String useROI = "Use all Domain Regions...";
		final String cancel = "Cancel";
		String result = useUnderlying;
		if(overlayEditorPanelJAI.getAllCompositeROINamesAndColors().length!= 0){
			result = DialogUtils.showWarningDialog(overlayEditorPanelJAI, 
					"Auto-crop will find the smallest box that encloses all non-background data values and allow you to 'crop' your data to that size. Choose an action:\n"+
					"1. Use the 'underlying' image to calculate an auto-cropping boundary.\n"+
					"2. Use all the user Domain Regions to calculate an auto-cropping boundary.",
					new String[] {useUnderlying,useROI,cancel}, useUnderlying);				
		}
		if(result.equals(cancel)){
			throw UserCancelException.CANCEL_GENERIC;
		}else if(result.equals(useUnderlying)){
			autoCrop(false);
		}else{
			autoCrop(true);
		}
	}
	private void calculateHistogram(){
		AsynchClientTask calcBinsTask = new AsynchClientTask("Calulating Histogram",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				condensedBinsMapChannels = calculateCondensedBinsChannels0(getImageDataset());
			}
		};
		AsynchClientTask updatePanelTask = new AsynchClientTask("Updating Display",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				overlayEditorPanelJAI.setHistogram(condensedBinsMapChannels[imageDatasetChannel]);
				overlayEditorPanelJAI.showHistogram();
			}
		};
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI, new Hashtable<String, Object>(),
				new AsynchClientTask[] {calcBinsTask,updatePanelTask},false,false,null,true);
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
			UShortImage[] images = getImageDataSetChannel().getAllImages();
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
						nonZeroBoundingBox3D.depth == getImageDataSetChannel().getISize().getZ());
			boolean isAutoCroppable2D = 
				!(nonZeroBoundingBox3D.low.x == 0 && 
						nonZeroBoundingBox3D.low.y == 0 && 
						nonZeroBoundingBox3D.width == getImageDataSetChannel().getISize().getX() &&
						nonZeroBoundingBox3D.height == getImageDataSetChannel().getISize().getY());
	
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
					int highZlower = ((nonZeroBoundingBox3D.low.z+nonZeroBoundingBox3D.depth) != getImageDataSetChannel().getISize().getZ()
							?(B_DISPLAY_ZERO_INDEX_Z
								?(nonZeroBoundingBox3D.low.z+nonZeroBoundingBox3D.depth)
								:nonZeroBoundingBox3D.low.z+nonZeroBoundingBox3D.depth+1):
							-1);
					int highZupper = ((nonZeroBoundingBox3D.low.z+nonZeroBoundingBox3D.depth) != getImageDataSetChannel().getISize().getZ()
							?(B_DISPLAY_ZERO_INDEX_Z
								?(getImageDataSetChannel().getISize().getZ()-1)
								:getImageDataSetChannel().getISize().getZ()-1+1)
							:-1);
					String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI, 
							"Auto-crop using "+(bUseROI?"ROIs":"underlying image")+" has detected empty Z Sections from"+
							(lowZlower != -1?" "+lowZlower+" to "+lowZupper:"")+
							(highZlower != -1?(lowZlower != -1?" and ":" ")+highZlower+" to "+highZupper:"")+
							(defaultOption == cropOnlyZ?"\nThere are no empty XY border pixels.":"")+
							"\nDo you want to include the empty Z-sections in the crop?",
							options, defaultOption);
					if(result.equals(cancel)){
						throw UserCancelException.CANCEL_GENERIC;
					}else if(result.equals(cropOnlyZ)){
						bIncludeXY = false;
					}else if(result.equals(cropOnlyXY)){
						bIncludeZ = false;
					}
				}
				if(isAutoCroppable2D && bIncludeXY){
					Rectangle crop2D =  new Rectangle();
					crop2D.setBounds(nonZeroBoundingBox3D.low.x, nonZeroBoundingBox3D.low.y, nonZeroBoundingBox3D.width, nonZeroBoundingBox3D.height);
					overlayEditorPanelJAI.cropDrawAndConfirm(crop2D);
				}
				if(!bIncludeZ){
					nonZeroBoundingBox3D.low.z = 0;
					nonZeroBoundingBox3D.depth = getImageDataSetChannel().getISize().getZ();
				}
				if(!bIncludeXY){
					nonZeroBoundingBox3D.low.x = 0;
					nonZeroBoundingBox3D.low.y = 0;
					nonZeroBoundingBox3D.width = getImageDataSetChannel().getISize().getX();
					nonZeroBoundingBox3D.height = getImageDataSetChannel().getISize().getY();
				}
				cropROIData(nonZeroBoundingBox3D/*,true*/);
			}else{
				DialogUtils.showWarningDialog(overlayEditorPanelJAI, "No non-zero bounding border in the "+(bUseROI?"user Domain Regions":"underlay image")+" was found to auto-crop.  Use manual crop tool.");
				return;
			}
		}else{
			DialogUtils.showWarningDialog(overlayEditorPanelJAI, "All pixels in the "+(bUseROI?"user Domain Regions":"underlay image")+" are background, auto-crop ignored.  Use manual crop tool.");
			return;
		}
	}
	public static class PaddedInfo {
		public Object paddedArray;
		public ISize paddedISize;
	}
	public static PaddedInfo copyToPadded(
			Object origArr,ISize origISize,Origin origOrigin,Extent origExtent,
			ISize padMinus,ISize padPlus){
		
		int newSizeX = origISize.getX()+(padMinus.getX()+padPlus.getX());
		int newSizeY = (origISize.getY()>1?origISize.getY()+(padMinus.getY()+padPlus.getY()):origISize.getY());
		int newSizeZ =  (origISize.getZ()>1?origISize.getZ()+(padMinus.getZ()+padPlus.getZ()):origISize.getZ());
		
		Object newArr = Array.newInstance(origArr.getClass().getComponentType(), newSizeX*newSizeY*newSizeZ);
		Object allZSections = origArr;
		int origXYSize =  origISize.getX()*origISize.getY();
		Object currZSection = Array.newInstance(origArr.getClass().getComponentType(),origXYSize);
		for (int z0 = 0; z0 < newSizeZ; z0++) {
			Object paddedCurrZSection = null;
			if(!(z0 < (padMinus.getZ()) || z0 >= (padMinus.getZ()+origISize.getZ()))){
				int z = z0-padMinus.getZ();
				System.arraycopy(allZSections, origXYSize*z, currZSection, 0, origXYSize);
				if(origArr instanceof short[]){
					paddedCurrZSection = padXYUShort((short[])currZSection,origISize.getX(),origISize.getY(),padMinus.getX(),padPlus.getX(),padMinus.getY(),padPlus.getY());
				}else if(origArr instanceof byte[]){
					paddedCurrZSection = padXYByte((byte[])currZSection,origISize.getX(),origISize.getY(),padMinus.getX(),padPlus.getX(),padMinus.getY(),padPlus.getY());
				}else{
					throw new IllegalArgumentException(origArr.getClass().getName() +"not implement for 'copyToPadded'");
				}
			}
			if(paddedCurrZSection != null){
				System.arraycopy(paddedCurrZSection, 0, newArr, z0*newSizeX*newSizeY, newSizeX*newSizeY);
			}
		}
		
		ROIMultiPaintManager.PaddedInfo paddedInfo = new ROIMultiPaintManager.PaddedInfo();
		paddedInfo.paddedArray = newArr;
		paddedInfo.paddedISize = new ISize(newSizeX, newSizeY, newSizeZ);
		return paddedInfo;
	}
	public static byte[] padXYByte(byte[] byteArr,int numX,int numY,int left,int right,int top,int bottom){
		BufferedImage bufferedImage = new BufferedImage(numX, numY, BufferedImage.TYPE_BYTE_GRAY);
		byte[] byteData = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
		System.arraycopy(byteArr, 0, byteData, 0, byteArr.length);
		PlanarImage planarImage = BorderDescriptor.create(bufferedImage,left,right,top,bottom,null, null).getRendering();
		return ((DataBufferByte)planarImage.getData().getDataBuffer()).getData();
	}
	public static short[] padXYUShort(short[] shortArr,int numX,int numY,int left,int right,int top,int bottom){
		BufferedImage bufferedImage = new BufferedImage(numX, numY, BufferedImage.TYPE_USHORT_GRAY);
		short[] shortData = ((DataBufferUShort)bufferedImage.getRaster().getDataBuffer()).getData();
		System.arraycopy(shortArr, 0, shortData, 0, shortArr.length);
		PlanarImage planarImage = BorderDescriptor.create(bufferedImage,left,right,top,bottom,null, null).getRendering();
		return ((DataBufferUShort)planarImage.getData().getDataBuffer()).getData();
	}
	
	public static PlanarImage resizeXYByte(byte[] byteArr,int numX,int numY,Float scaleX,Float scaleY){
		BufferedImage bufferedImage = new BufferedImage(numX, numY, BufferedImage.TYPE_BYTE_INDEXED, getContrastIndexColorModel());
		byte[] byteData = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
		System.arraycopy(byteArr, 0, byteData, 0, byteArr.length);
		PlanarImage planarImage = ScaleDescriptor.create(bufferedImage, scaleX, scaleY, 0f, 0f, new InterpolationNearest(), null);
		return planarImage;
//		PaddedInfo paddedInfo = new PaddedInfo();
//		paddedInfo.paddedArray = ((DataBufferByte)planarImage.getData().getDataBuffer()).getData();
//		paddedInfo.paddedISize = new ISize(planarImage.getWidth(), planarImage.getHeight(), 1);
//		return paddedInfo;
	}
	public static PlanarImage resizeXYUShort(short[] shortArr,int numX,int numY,Float scaleX,Float scaleY){
		BufferedImage bufferedImage = new BufferedImage(numX, numY, BufferedImage.TYPE_USHORT_GRAY);
		short[] shortData = ((DataBufferUShort)bufferedImage.getRaster().getDataBuffer()).getData();
		System.arraycopy(shortArr, 0, shortData, 0, shortArr.length);
		PlanarImage planarImage = ScaleDescriptor.create(bufferedImage, scaleX, scaleY, 0f, 0f, new InterpolationNearest(), null);
		return planarImage;
//		PaddedInfo paddedInfo = new PaddedInfo();
//		paddedInfo.paddedArray = ((DataBufferUShort)planarImage.getData().getDataBuffer()).getData();
//		paddedInfo.paddedISize = new ISize(planarImage.getWidth(), planarImage.getHeight(), 1);
//		return paddedInfo;
	}

	public static class Crop3D {
		@Override
		public String toString() {
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
	private void duplicateROIData(final int newZSize){
		final AsynchClientTask extrudeTask = new AsynchClientTask("Extruding...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ISize origSize = getImageDataSetChannel().getISize();
				if(origSize.getZ() != 1 || newZSize < 1){
					throw new IllegalArgumentException("Extrude assumes starting with 2D and new Z size >= 1.");
				}
				UShortImage[][] newUnderLayImageArr = new UShortImage[initImageDataSetChannels.length][newZSize];
				BufferedImage[] newROICompositeArr =  new BufferedImage[newZSize];
				
				for (int i = 0; i < newZSize; i++) {
					for (int c = 0; c < initImageDataSetChannels.length; c++) {
//						short[] zslice = new short[initImageDataSetChannels[c].getAllImages()[0].getPixels().length];
//						System.arraycopy(initImageDataSetChannels[c].getAllImages()[0].getPixels(), 0, zslice, 0, zslice.length);
						newUnderLayImageArr[c][i] =
							new UShortImage(
								initImageDataSetChannels[c].getAllImages()[0].getPixels().clone(),//	zslice,
								DEFAULT_ORIGIN, DEFAULT_EXTENT,
								initImageDataSetChannels[c].getAllImages()[0].getNumX(),
								initImageDataSetChannels[c].getAllImages()[0].getNumY(),1);
					}
					newROICompositeArr[i] =
						new BufferedImage(roiComposite[0].getWidth(),roiComposite[0].getHeight(),
							BufferedImage.TYPE_BYTE_INDEXED, getContrastIndexColorModel());
					System.arraycopy((byte[])((DataBufferByte)roiComposite[0].getRaster().getDataBuffer()).getData(), 0,
							(byte[])((DataBufferByte)newROICompositeArr[i].getRaster().getDataBuffer()).getData(), 0,
							origSize.getX()*origSize.getY());

				}
				for (int c = 0; c < initImageDataSetChannels.length; c++) {
					initImageDataSetChannels[c] = new ImageDataset(newUnderLayImageArr[c], null, newZSize);
				}
				roiComposite = newROICompositeArr;
		
				if(!(enhanceImageAmount == ROIMultiPaintManager.ENHANCE_NONE)){
					getClientTaskStatusSupport().setMessage("smoothing...");
				}
				updateAuxiliaryInfo(origSize,getClientTaskStatusSupport());
			}
		};
		final AsynchClientTask updatePanelTask = getUpdateDisplayAfterCropTask();
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI,new Hashtable<String, Object>(),
				new AsynchClientTask[] {extrudeTask,updatePanelTask},false,false,null,true);

	}
	
	private void padCropDataset(){
		int xm = 0;
		int ym = 0;
		int zm = 0;
		int xp = 0;
		int yp = 0;
		int zp = 0;
		String result = null;
		do{
			try{
				result = DialogUtils.showInputDialog0(overlayEditorPanelJAI, "Enter the number of pixels to add or crop (negative number) at each border: (xlow,ylow,zlow,xhigh,yhigh,zhigh)", xm+","+ym+","+zm+","+xp+","+yp+","+zp);
				if(result != null){
					final String SEP = ",";
					result = result.trim();
					StringTokenizer st = new StringTokenizer(result,SEP);
					xm = Integer.parseInt(st.nextToken());
					ym = Integer.parseInt(st.nextToken());
					zm = Integer.parseInt(st.nextToken());
					xp = Integer.parseInt(st.nextToken());
					yp = Integer.parseInt(st.nextToken());
					zp = Integer.parseInt(st.nextToken());
					if(st.hasMoreElements() || result.endsWith(SEP)){
						throw new Exception("Some input was not parsed, check input.");
					}
					break;
				}
			}catch(UtilCancelException e){
				throw UserCancelException.CANCEL_GENERIC;
			}catch(Exception e){
				DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Error parsing '"+result+"' Resetting to valid values.  Enter 6 comma separated integers.");
			}
		}while(true);

		final CoordinateIndex low = new CoordinateIndex(xm,ym,zm);
		final CoordinateIndex high = new CoordinateIndex(xp,yp,zp);
		
		final AsynchClientTask padTask = new AsynchClientTask("Changeing borders...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {				
				resizeImpl(ResizeInfo.createPadCropResizeInfo(getImageDataSetChannel().getISize(),low,high),getClientTaskStatusSupport());
			}
		};
		final AsynchClientTask updatePanelTask = getUpdateDisplayAfterCropTask();
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI,new Hashtable<String, Object>(),
				new AsynchClientTask[] {padTask,updatePanelTask},false,false,null,true);
	}
	private void cropROIData(final Crop3D cropRectangle3D/*,boolean bThread*/){
		final AsynchClientTask cropTask = new AsynchClientTask("Cropping...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ISize origISize = getImageDataSetChannel().getISize();
				CoordinateIndex cropLow = new CoordinateIndex(-cropRectangle3D.low.x,-cropRectangle3D.low.y,-cropRectangle3D.low.z);
				CoordinateIndex cropHigh =
					new CoordinateIndex(cropRectangle3D.low.x+cropRectangle3D.width-origISize.getX(),
						cropRectangle3D.low.y+cropRectangle3D.height-origISize.getY(),
						cropRectangle3D.low.z+cropRectangle3D.depth-origISize.getZ());
				resizeImpl(ResizeInfo.createPadCropResizeInfo(origISize, cropLow, cropHigh), getClientTaskStatusSupport());
				updateAuxiliaryInfo(origISize,getClientTaskStatusSupport());
			}
		};
		final AsynchClientTask updatePanelTask = getUpdateDisplayAfterCropTask();
		final Hashtable<String, Object> taskHash = new Hashtable<String, Object>();
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI,taskHash,new AsynchClientTask[] {cropTask,updatePanelTask},false,false,null,true);

//		if(bThread){
//			ClientTaskDispatcher.dispatch(overlayEditorPanelJAI,taskHash,
//				new AsynchClientTask[] {cropTask,updatePanelTask},false,false,null,true);
//		}else{
//			new Thread(new Runnable() {
//				public void run(){
//					try{
//						cropTask.run(taskHash);
//						SwingUtilities.invokeAndWait(new Runnable() {
//							public void run() {
//								try {
//									updatePanelTask.run(taskHash);
//								} catch (Exception e) {
//									e.printStackTrace();
//									throw new RuntimeException(e);
//								}
//							}
//						});
//					}catch(Exception e){
//						e.printStackTrace();
//						DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Crop failed:\n"+e.getMessage()+
//								(e.getCause()!= null?"\n"+e.getCause().getMessage():""), e);
//					}
//				}
//			}).run();
//		}
			
	}
	private AsynchClientTask getUpdateDisplayAfterCropTask(){
		return new AsynchClientTask("Updating display...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				//Update display with cropped images
				if(overlayEditorPanelJAI != null){
					int currentContrast = overlayEditorPanelJAI.getDisplayContrastFactor();
					overlayEditorPanelJAI.setHighliteInfo(null,OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY);
					overlayEditorPanelJAI.setAllROICompositeImage(null,OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY);
					updateUnderlayHistogramDisplay();
					overlayEditorPanelJAI.setAllROICompositeImage(roiComposite,OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY);
					overlayEditorPanelJAI.setDisplayContrastFactor(currentContrast);
					updateUndoAfter(false);
				}
			}
		};

	}
	private void updateAuxiliaryInfo(ISize origSize,ClientTaskStatusSupport clientTaskStatusSupport) throws Exception{
		if(clientTaskStatusSupport != null){clientTaskStatusSupport.setMessage("Smothing...");}
		enhancedImageDatasetChannels = smoothImageDataset(initImageDataSetChannels, enhanceImageAmount,null);
		if(clientTaskStatusSupport != null){clientTaskStatusSupport.setMessage("Calculating histogram...");}
		condensedBinsMapChannels = calculateCondensedBinsChannels0(getImageDataset());
		allPixelValuesRangeChannels = calculateAllPixelValuesRangeChannels0(getImageDataset());
		if(editedGeometryAttributes == null){
			editedGeometryAttributes = new GeometryAttributes();
			editedGeometryAttributes.origin = originalOrigin;
			editedGeometryAttributes.extent = createResizeExtent(originalExtent, originalISize,getImageDataSetChannel().getISize());
		}else{
			editedGeometryAttributes.extent =
					createResizeExtent(editedGeometryAttributes.extent, origSize,getImageDataSetChannel().getISize());
		}

	}
	private void deleteROI(ComboboxROIName currentComboboxROIName){
		final String deleteCurrentROI = "Delete only current Domain";
		final String deleteAllROI = "Delete all Domains";
		final String cancel = "Cancel";
		String result =
			DialogUtils.showWarningDialog(overlayEditorPanelJAI, "Choose delete option.",
					new String[] {deleteCurrentROI,deleteAllROI,cancel}, deleteCurrentROI);
		if(result.equals(cancel)){
			throw UserCancelException.CANCEL_GENERIC;
		}
		overlayEditorPanelJAI.setResolvedList(new String[] {RESOLVED_WAIT_MESSG});
		if(result.equals(deleteCurrentROI)){
			clearROI(FLAG_CLEAR_ROI.CLEARCURRENT, currentComboboxROIName.getContrastColorIndex(),OverlayEditorPanelJAI.FRAP_DATA_DELETEROI_PROPERTY);
			overlayEditorPanelJAI.deleteROIName(currentComboboxROIName);

		}else if(result.equals(deleteAllROI)){
			for (int i = 0; i < roiComposite.length; i++) {
				Arrays.fill(((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData(), (byte)0);
			}
			overlayEditorPanelJAI.deleteROIName(null);
			overlayEditorPanelJAI.setHighliteInfo(null,OverlayEditorPanelJAI.FRAP_DATA_DELETEROI_PROPERTY);//force update
		}
		if(overlayEditorPanelJAI.getCurrentROIInfo() == null){
			//no rois so set blend so we can see underlay
			wantBlendSetToEnhance();
		}

		
	}
	private void addNewROI(ROIMultiPaintManager.ComboboxROIName[] comboboxROINameArr,String specialMessage) throws Exception{
		try{
			int unUsedColorIndex = getUnusedROIColorIndex(comboboxROINameArr,null);
			String newROIName = null;
			boolean bNameOK;
			int count = 0;
			do{
				bNameOK = true;
				if(newROIName == null){
					//find new name
					newROIName = "cell";
					while(true){
						boolean bNameFound = false;
						for (int i = 0; i < comboboxROINameArr.length; i++) {
							if(comboboxROINameArr[i].getROIName().equals(newROIName)){
								bNameFound = true;
								break;
							}
						}
						if(!bNameFound){
							break;
						}
						newROIName = TokenMangler.getNextEnumeratedToken(newROIName);
						count++;
						if(count > 255){
							break;
						}
					}
					newROIName = DialogUtils.showInputDialog0(overlayEditorPanelJAI, (specialMessage==null?"":specialMessage+"\n")+"Enter new Domain name:", newROIName);
				}
				if(newROIName == null || newROIName.length() == 0){
					bNameOK = false;
					newROIName = null;
					PopupGenerator.showErrorDialog(overlayEditorPanelJAI, "No Domain Name entered, try again.");
				}else{
					if(newROIName.equals(RESERVED_NAME_BACKGROUND)){
						DialogUtils.showWarningDialog(overlayEditorPanelJAI,
								"Cannot use the name '"+RESERVED_NAME_BACKGROUND+"'.  That name is reserved by the system to refer to unassigned pixels");
						newROIName = null;
						continue;
					}
					bNameOK = !isROINameUsed(comboboxROINameArr, newROIName);
//					for (int i = 0; i < comboboxROINameArr.length; i++) {
//						if(comboboxROINameArr[i].getROIName().equals(newROIName)){
//							bNameOK = false;
//							break;
//						}
//					}
				}
				if(bNameOK){
////						JColorChooser jColorChooser = new JColorChooser();
////						DialogUtils.showComponentOKCancelDialog(overlayEditorPanelJAI, jColorChooser, "Select ROI Color");
//					Color newROIColor = Color.black;
//					for (int i = 1; i < OverlayEditorPanelJAI.CONTRAST_COLORS.length; i++) {
//						boolean bColorUsed = false;
//						for (int j = 0; j < comboboxROINameArr.length; j++) {
//							Color nextColor = comboboxROINameArr[j].getHighlightColor();
//							if(nextColor.equals(OverlayEditorPanelJAI.CONTRAST_COLORS[i])){
//								bColorUsed = true;
//								break;
//							}
//						}
//						if(!bColorUsed){
//							newROIColor = OverlayEditorPanelJAI.CONTRAST_COLORS[i];
//							break;
//						}
//					}
					overlayEditorPanelJAI.addROIName(newROIName, true, newROIName,true,/*true,true,*/unUsedColorIndex);
				}else if(newROIName != null){
					PopupGenerator.showErrorDialog(overlayEditorPanelJAI, "ROI Name "+newROIName+" already used, try again.");
					newROIName = null;
				}
			}while(!bNameOK);
		}catch(UtilCancelException cancelExc){
			throw UserCancelException.CANCEL_GENERIC;
		}
	}
	private boolean isROINameUsed(ROIMultiPaintManager.ComboboxROIName[] comboboxROINameArr,String roiName){
		for (int i = 0; i < comboboxROINameArr.length; i++) {
			if(comboboxROINameArr[i].getROIName().equals(roiName)){
				return true;
			}
		}
		return false;
	}
	private int getUnusedROIColorIndex(ROIMultiPaintManager.ComboboxROIName[] comboboxROINameArr,Set<Integer> exclude) throws Exception{
	//		JColorChooser jColorChooser = new JColorChooser();
	//		DialogUtils.showComponentOKCancelDialog(overlayEditorPanelJAI, jColorChooser, "Select ROI Color");
		for (int i = 1; i < getContrastIndexColorModel().getMapSize(); i++) {
			boolean bColorUsed = false;
			if(exclude != null && exclude.contains(i)){
				continue;
			}
			for (int j = 0; j < comboboxROINameArr.length; j++) {
				if(comboboxROINameArr[j].getContrastColorIndex() == i){
					bColorUsed = true;
					break;
				}
			}
			if(!bColorUsed){
				return i;
			}
		}
		throw new Exception("No more unused colors");
	}
	private boolean askApplyHighlightToROI(){
		UShortImage[] roiZ = overlayEditorPanelJAI.getHighliteInfo().getRoiImages();
		boolean bOverWrite = true;
		//Check for existing ROI
		final String OVERWRITE_ALL = "Overwrite any existing Domain Regionss";
		final String KEEP_EXISTING = "Keep existing Domain Regions when overlapping";
		final String CANCEL_ROI_UPDATE = "Cancel";
//		boolean bHadAny = false;
		for (int i = 0; i < roiZ.length; i++) {
			boolean bDone = false;
			short[] pixels = roiZ[i].getPixels();
			byte[] compositePixels = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
			for (int j = 0; j < compositePixels.length; j++) {
//				bHadAny|= pixels[j] != 0;
				if(compositePixels[j] != 0 && pixels[j] != 0/* && compositePixels[j] != (byte)roiColorIndex*/){
					bDone = true;
					String result = DialogUtils.showWarningDialog(overlayEditorPanelJAI,
							"Some areas of the new Domain Regions overlap with existing Domain Regions.",
							new String[] {OVERWRITE_ALL,KEEP_EXISTING,CANCEL_ROI_UPDATE},OVERWRITE_ALL);
					if(result.equals(KEEP_EXISTING)){
						bOverWrite = false;
					}else if(result.equals(CANCEL_ROI_UPDATE)){
						throw UserCancelException.CANCEL_GENERIC;
					}
					break;
				}
			}
			if(bDone){
				break;
			}
		}
		return bOverWrite;

	}
	private void applyHighlightToROI(ROIMultiPaintManager.ComboboxROIName currentComboboxROIName,boolean bOverWrite){
		UShortImage[] roiZ = overlayEditorPanelJAI.getHighliteInfo().getRoiImages();
		//Update composite ROI
		int roiColorIndex = currentComboboxROIName.getContrastColorIndex();
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
		overlayEditorPanelJAI.setAllROICompositeImage(roiComposite,OverlayEditorPanelJAI.FRAP_DATA_UPDATEROI_WITHHIGHLIGHT_PROPERTY);
		overlayEditorPanelJAI.setHighliteInfo(null,OverlayEditorPanelJAI.FRAP_DATA_UPDATEROI_WITHHIGHLIGHT_PROPERTY);
	}
	
	private enum FLAG_CLEAR_ROI {CLEARALL,CLEARCURRENT,CLEARUNDERHILITE,CONVERT};
	private FLAG_CLEAR_ROI askClearROI(){
		FLAG_CLEAR_ROI flag = null;
		int roiCount = overlayEditorPanelJAI.getAllCompositeROINamesAndColors().length;
		final String clearAll = "Clear all Domains";
		final String clearCurrentOnly = "Clear current Domain";
		final String clearHighlight = "Clear current Domain under highlight";
		final String cancel = "Cancel";
		Vector<String> optionListV = new Vector<String>();// String[] {clearCurrentOnly,clearAll,cancel};
		optionListV.add(clearCurrentOnly);
		StringBuffer sb = new StringBuffer();
		sb.append("Domain will be set to background (cleared). Choose action:\n1. Clear current Domain.\n");
		if(roiCount > 1){
			optionListV.add(clearAll);
			sb.append("2. Clear all roiS.");
		}
		if(overlayEditorPanelJAI.getHighliteInfo() != null){
			optionListV.add(clearHighlight);
			sb.append((roiCount>1?"3. ":"2. ")+"Clear only the highlighted region in the current Domain.");
		}
		optionListV.add(cancel);
		String result = DialogUtils.showWarningDialog(
				overlayEditorPanelJAI,
				"Choose action:\n"+
				sb.toString(),
				optionListV.toArray(new String[0]),
				clearCurrentOnly);
		if(result.equals(clearAll)){
			return FLAG_CLEAR_ROI.CLEARALL;
		}else if (result.equals(cancel)){
			throw UserCancelException.CANCEL_GENERIC;
		}else if(result.equals(clearHighlight)){
			return FLAG_CLEAR_ROI.CLEARUNDERHILITE;
		}else if(result.equals(clearCurrentOnly)){
			return FLAG_CLEAR_ROI.CLEARCURRENT;
		}
		return flag;
	}
	private void clearROI(FLAG_CLEAR_ROI flag,int contrastColorIndex,String action){

		if(flag.equals(FLAG_CLEAR_ROI.CLEARALL)){
			for (int i = 0; i < roiComposite.length; i++) {
				byte[] roiData = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
				Arrays.fill(roiData, (byte)0);
			}
			overlayEditorPanelJAI.setHighliteInfo(null,action);
			return;
		}

		for (int z = 0; z < roiComposite.length; z++) {
			byte[] roiData = ((DataBufferByte)roiComposite[z].getRaster().getDataBuffer()).getData();
			for (int xy = 0; xy < roiData.length; xy++) {
				if((roiData[xy]&0x000000FF) == contrastColorIndex){
					if(flag.equals(FLAG_CLEAR_ROI.CLEARUNDERHILITE)){
						if(overlayEditorPanelJAI.getHighliteInfo().getRoiImages()[z].getPixels()[xy]==0){
							continue;
						}
					}
					roiData[xy] = 0;
				}
			}
		}
		overlayEditorPanelJAI.setHighliteInfo(null,action);

	}
	private InterruptCalc[] lastResolveHighlightInterruptCalc = new InterruptCalc[1];
	private void highliteImageWithResolvedSelections(final RegionInfo[] selectedRegionInfos){
		if(selectedRegionInfos == null || selectedRegionInfos.length == 0){
			overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_RESOLVEDHIGHLIGHT_PROPERTY);				
			return;
		}
		final String RESOLVE_HIGHLIGHT_INFO = "RESOLVE_HIGHLIGHT_INFO";
		AsynchClientTask hilightCalcTask = new AsynchClientTask("Calc highlight...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				InterruptCalc localInterruptCalc = getInterruptCalc(lastResolveHighlightInterruptCalc);
				List<RegionInfo> selectedRegionInfoList = Arrays.asList(selectedRegionInfos);
				HighlightROIInfo highlightROIInfo = generateHighlightROIInfo((byte)-1,roiComposite,regionImage,
						RegionAction.createHighlightRegionAction(sortedRegionInfos, selectedRegionInfoList),localInterruptCalc);
				if(localInterruptCalc.isInterrupted()){
					throw UserCancelException.CANCEL_GENERIC;
				}
				hashTable.put(RESOLVE_HIGHLIGHT_INFO, highlightROIInfo);
			}
		};
		AsynchClientTask udpateDisplayTask = new AsynchClientTask("Update display...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				HighlightROIInfo highlightROIInfo = (HighlightROIInfo)hashTable.get(RESOLVE_HIGHLIGHT_INFO);
				overlayEditorPanelJAI.setHighliteInfo(highlightROIInfo.highlightROI, OverlayEditorPanelJAI.FRAP_DATA_RESOLVEDHIGHLIGHT_PROPERTY);
				wantBlendSetToEnhance();
			}
		};
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI, new Hashtable<String, Object>(), new AsynchClientTask[] {hilightCalcTask,udpateDisplayTask});
	}
	private void mergeResolvedSelections(final RegionInfo[] selectedRegionInfos) throws Exception{
		if(selectedRegionInfos == null || selectedRegionInfos.length == 0){
			return;
		}
//		final String UNUSED_ROI_PIXVAL = "UNUSED_ROI_INDEX";
//		final String MULTI_NEIGHBOR_MERGE = "MultiNeighborMerge";
		AsynchClientTask mergeTask = new AsynchClientTask("Merging "+selectedRegionInfos.length+" regions...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				//make new RegionImage with all selections converted to a single temporary ROI
				//
//				if(isROINameUsed(overlayEditorPanelJAI.getAllCompositeROINamesAndColors(), MULTI_NEIGHBOR_MERGE)){
//					throw new Exception(MULTI_NEIGHBOR_MERGE+" exists, they must be reconciled before any new merge");
//				}

				//sort selected region index for fast lookup
				TreeSet<Integer> selectedRegionIndexesTS = new TreeSet<Integer>();
				for (int i = 0; i < selectedRegionInfos.length; i++) {
					selectedRegionIndexesTS.add(selectedRegionInfos[i].getRegionIndex());
				}
//				//find unused index we can use for temporary ROI
//				BitSet usedROIIndexes = new BitSet();
//				for (int i = 0; i < roiComposite.length; i++) {
//					byte[] sliceBytes = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
//					for (int j = 0; j < sliceBytes.length; j++) {
//						usedROIIndexes.set((int)(sliceBytes[j]&0x000000FF));
//					}
//				}
				int unusedROIPixelValue = getUnusedROIColorIndex(overlayEditorPanelJAI.getAllCompositeROINamesAndColors(),null);
//				if(usedROIIndexes.get(unusedROIPixelValue)){
//					throw new Exception("Error: Found unused color index but that ROI pixel value exists");
//				}
				//find image indexes of selected regions and fill new ROIImage with temporary ROI Index
				byte[] shortEncodedRegionIndexArr = regionImage.getShortEncodedRegionIndexImage();
				BufferedImage[] tempROI = new BufferedImage[roiComposite.length];
				int count = 0;
				for (int i = 0; i < tempROI.length; i++) {
					byte[] roiBytes = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
					tempROI[i] = new BufferedImage(roiComposite[i].getWidth(),roiComposite[i].getHeight(),BufferedImage.TYPE_BYTE_INDEXED, getContrastIndexColorModel());
					byte[] sliceBytes = ((DataBufferByte)tempROI[i].getRaster().getDataBuffer()).getData();
					System.arraycopy(roiBytes, 0, sliceBytes, 0, roiBytes.length);
					for (int j = 0; j < sliceBytes.length; j++) {
						int regionIndex = (int)((0x000000ff & shortEncodedRegionIndexArr[2 * count]) | ((0x000000ff & shortEncodedRegionIndexArr[2 * count + 1]) << 8));
						if(selectedRegionIndexesTS.contains(regionIndex)){
							sliceBytes[j] = (byte)unusedROIPixelValue;
						}
						count++;
					}
				}
				if(getClientTaskStatusSupport()!=null){getClientTaskStatusSupport().setProgress(10);}
				shortEncodedRegionIndexArr = null;//release memory
				//get new regionImage and new selectedRegionInfos
				VCImage tempImage = ROIMultiPaintManager.createVCImageFromBufferedImages(ROIMultiPaintManager.DEFAULT_EXTENT, tempROI);
				RegionImage tempRegionImage =
					new RegionImage(tempImage, 0 /*0 means generate no surfacecollection*/,
							tempImage.getExtent(),ROIMultiPaintManager.DEFAULT_ORIGIN, RegionImage.NO_SMOOTHING,
							null);
				tempImage = null;//release memory
				RegionInfo[] tempRegionInfos = tempRegionImage.getRegionInfos();
				if(tempRegionInfos.length == 1){
					throw new Exception("No unselected neighbors to merge with.");
				}
				if(getClientTaskStatusSupport()!=null){getClientTaskStatusSupport().setProgress(20);}
				Vector<RegionImage.RegionInfo> tempSelectedRegionInfos = new Vector<RegionImage.RegionInfo>();
				 HighlightROIInfo highlightROIInfo =
							generateHighlightROIInfo((byte)-1,tempROI,tempRegionImage,
									RegionAction.createCheckNeighborsOnlyRegionAction(tempRegionInfos),
									null);
					boolean bHasSelectionWithMoreThanOneNeighbor = false;
					for (int i = 0; i < tempRegionInfos.length; i++) {
						if (tempRegionInfos[i].getPixelValue() == unusedROIPixelValue) {
							tempSelectedRegionInfos.add(tempRegionInfos[i]);
//							if (highlightROIInfo.neighborsForRegionsMap.get(tempRegionInfos[i]).size() > 1) {
//								hashTable.put(UNUSED_ROI_PIXVAL, new Integer(unusedROIPixelValue));
//								bHasSelectionWithMoreThanOneNeighbor = true;
//							} else {
//								tempSelectedRegionInfos.add(tempRegionInfos[i]);
//							}
						}
					}
					if(getClientTaskStatusSupport()!=null){getClientTaskStatusSupport().setProgress(50);}
					//final merge
					updateUndo(UNDO_INIT.ALLZ);
					generateHighlightROIInfo((byte)unusedROIPixelValue,tempROI,tempRegionImage,
						RegionAction.createMergeSelectedWithNeighborsRegionAction(
								tempRegionInfos,
								tempSelectedRegionInfos,
								highlightROIInfo.neighborsForRegionsMap/*,
								true*/),
						null);
					//copy merged bytes back to ROI
					for (int i = 0; i < tempROI.length; i++) {
						byte[] roiBytes = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
						byte[] sliceBytes = ((DataBufferByte)tempROI[i].getRaster().getDataBuffer()).getData();
//						for (int j = 0; j < sliceBytes.length; j++) {
//							if(sliceBytes[j] == (byte)unusedROIPixelValue){
//								System.out.println("Bad");
//							}
//						}
						System.arraycopy(sliceBytes, 0, roiBytes, 0, roiBytes.length);
					}
					if(getClientTaskStatusSupport()!=null){getClientTaskStatusSupport().setProgress(90);}
			}
		};
		AsynchClientTask updateGUITask = new AsynchClientTask("Updating display...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
//				Integer unusedROIIndex = (Integer)hashTable.get(UNUSED_ROI_PIXVAL);
//				if(unusedROIIndex != null){
//					overlayEditorPanelJAI.addROIName(MULTI_NEIGHBOR_MERGE, true, MULTI_NEIGHBOR_MERGE,true,/*true,true,*/OverlayEditorPanelJAI.CONTRAST_COLORS[unusedROIIndex]);
//				}

				overlayEditorPanelJAI.setHighliteInfo(null, OverlayEditorPanelJAI.FRAP_DATA_RESOLVEDMERGE_PROPERTY);
				updateUndoAfterPrivate(true,false);
			}
		};
		Vector<AsynchClientTask> asynchClientTaskV = new Vector<AsynchClientTask>();
		asynchClientTaskV.add(mergeTask);
		asynchClientTaskV.add(updateGUITask);
		asynchClientTaskV.addAll(Arrays.asList(getRefreshObjectsTasks()));
		ClientTaskDispatcher.dispatch(overlayEditorPanelJAI, new Hashtable<String, Object>(), asynchClientTaskV.toArray(new AsynchClientTask[0]),
					true, false, null, true);
	}
	
	private static final int BLEND_ENHANCE_THRESHOLD = 70;
	private void wantBlendSetToEnhance(){
		if(overlayEditorPanelJAI.getBlendPercent() > BLEND_ENHANCE_THRESHOLD){
			overlayEditorPanelJAI.setBlendPercent(BLEND_ENHANCE_THRESHOLD);
		}
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
		ComboboxROIName[] allROINamesAndColors = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
		for (int i = 0; i < allROINamesAndColors.length; i++) {
			if(allROINamesAndColors[i].getContrastColorIndex() == pixelValue){
				return allROINamesAndColors[i].getROIName();
			}
		}
		throw new RuntimeException("No color found for pixelvalue="+pixelValue);
	}
	
	private static class RegionAction{
		public static final int REGION_ACTION_HIGHLIGHT = 0;
		public static final int REGION_ACTION_CHECKNEIGHBORSONLY = 1;
		public static final int REGION_ACTION_MERGESELECTEDWITHNEIGHBORS = 2;
		
		private RegionInfo[] actionAllRegionInfos;
		private Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsMap;
		private List<RegionImage.RegionInfo> selectedRegionsV;
		private int action;
//		private boolean bLeaveMultiNeighborUnchanged = true;
		private RegionAction(){
			
		}
		public int getAction(){
			return action;
		}
		public RegionInfo[] getAllRegionInfos(){
			return actionAllRegionInfos;
		}
		public List<RegionImage.RegionInfo> getSelectedRegionInfos(){
			return selectedRegionsV;
		}
		public Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> getNeighborsForRegionMap(){
			return neighborsForRegionsMap;
		}
		public static RegionAction createHighlightRegionAction(RegionImage.RegionInfo[] allRegionInfos,List<RegionImage.RegionInfo> selectedRegionsV){
			RegionAction regionAction = new RegionAction();
			regionAction.actionAllRegionInfos = allRegionInfos;
			regionAction.selectedRegionsV = selectedRegionsV;
			regionAction.action = REGION_ACTION_HIGHLIGHT;
			return regionAction;
		}
		public static RegionAction createCheckNeighborsOnlyRegionAction(RegionImage.RegionInfo[] allRegionInfos){
			RegionAction regionAction = new RegionAction();
			regionAction.actionAllRegionInfos = allRegionInfos;
			regionAction.selectedRegionsV = Arrays.asList(allRegionInfos);
			regionAction.action = REGION_ACTION_CHECKNEIGHBORSONLY;
			return regionAction;
		}
		public static RegionAction createMergeSelectedWithNeighborsRegionAction(
				RegionImage.RegionInfo[] allRegionInfos,
				List<RegionImage.RegionInfo> selectedRegionsV,
				Hashtable<RegionImage.RegionInfo,TreeSet<Integer>> neighborsForRegionsMap/*,
				boolean bLeaveMultiNeighborUnchanged*/){
			RegionAction regionAction = new RegionAction();
			regionAction.actionAllRegionInfos = allRegionInfos;
			regionAction.selectedRegionsV = selectedRegionsV;
			regionAction.neighborsForRegionsMap = neighborsForRegionsMap;
			regionAction.action = REGION_ACTION_MERGESELECTEDWITHNEIGHBORS;
//			regionAction.bLeaveMultiNeighborUnchanged = bLeaveMultiNeighborUnchanged;
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
	private static HighlightROIInfo generateHighlightROIInfo(byte debugValue,BufferedImage[] roiArr,RegionImage regionImage,RegionAction regionAction,ClientTaskStatusSupport clientTaskStatusSupport) throws Exception{
		
		HighlightROIInfo highlightROIInfo = new HighlightROIInfo();

		//Create lookup map to speedup highlighting operation for large dataset
		RegionImage.RegionInfo[] selectedRegionMap = new RegionImage.RegionInfo[regionAction.getAllRegionInfos().length];
		Iterator<RegionImage.RegionInfo> selectedIter = regionAction.getSelectedRegionInfos().iterator();
		while(selectedIter.hasNext()){
			RegionImage.RegionInfo nextRegion = selectedIter.next();
			selectedRegionMap[nextRegion.getRegionIndex()] = nextRegion;
		}
		byte[] shortEncodedRegionIndexes = regionImage.getShortEncodedRegionIndexImage();
		
		final int XSIZE = roiArr[0].getWidth();

		if(regionAction.getAction() == RegionAction.REGION_ACTION_HIGHLIGHT){
			highlightROIInfo.highlightROI = createEmptyROI(new ISize(roiArr[0].getWidth(),roiArr[0].getHeight(),roiArr.length));			
		}

		int allIndex = 0;
		final int ZMAX = roiArr.length-1;
		final int XMAX = roiArr[0].getWidth()-1;
		final int YMAX = roiArr[0].getHeight()-1;
		for (int z = 0; z < roiArr.length; z++) {
			if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
				return null;
			}
			int index = 0;
			byte[] zSlice = ((DataBufferByte)roiArr[z].getRaster().getDataBuffer()).getData();
			for (int y = 0; y < roiArr[0].getHeight(); y++) {
				for (int x = 0; x < XSIZE; x++) {
					int regionIndex =
						(shortEncodedRegionIndexes[allIndex]&0x000000FF) |
						(shortEncodedRegionIndexes[allIndex+1]&0x000000FF)<<8;
					if(selectedRegionMap[regionIndex] != null){
						RegionInfo currentRegionInfo = selectedRegionMap[regionIndex];
						if(regionAction.getAction() == RegionAction.REGION_ACTION_CHECKNEIGHBORSONLY){
							//Find neighbors
							int[] neighbors = new int[6];
							Arrays.fill(neighbors, -1);
							if(z>0){//top neighbor
								neighbors[0] = 0x000000FF&((DataBufferByte)roiArr[z-1].getRaster().getDataBuffer()).getData()[index];
							}
							if(z<ZMAX){//bottom neighbor
								neighbors[1] = 0x000000FF&((DataBufferByte)roiArr[z+1].getRaster().getDataBuffer()).getData()[index];
							}
							if(x>0){//left neighbor
								neighbors[2] = 0x000000FF&((DataBufferByte)roiArr[z].getRaster().getDataBuffer()).getData()[index-1];
							}
							if(x<XMAX){//right neighbor
								neighbors[3] = 0x000000FF&((DataBufferByte)roiArr[z].getRaster().getDataBuffer()).getData()[index+1];
							}
							if(y>0){//front neighbor
								neighbors[4] = 0x000000FF&((DataBufferByte)roiArr[z].getRaster().getDataBuffer()).getData()[index-XSIZE];
							}
							if(y<YMAX){//back neighbor
								neighbors[5] = 0x000000FF&((DataBufferByte)roiArr[z].getRaster().getDataBuffer()).getData()[index+XSIZE];
							}
							if(!highlightROIInfo.neighborsForRegionsMap.containsKey(currentRegionInfo)){
								highlightROIInfo.neighborsForRegionsMap.put(currentRegionInfo,new TreeSet<Integer>());
							}
							TreeSet<Integer> neighborTreeSet = highlightROIInfo.neighborsForRegionsMap.get(currentRegionInfo);
							for (int i = 0; i < neighbors.length; i++) {
								if(neighbors[i] != -1 && neighbors[i] != currentRegionInfo.getPixelValue()){
									neighborTreeSet.add(neighbors[i]);
								}
							}
							if(!highlightROIInfo.coordIndexForRegionsMap.containsKey(currentRegionInfo)){
								highlightROIInfo.coordIndexForRegionsMap.put(currentRegionInfo,new CoordinateIndex(x,y,z));
							}
						}else if(regionAction.getAction() == RegionAction.REGION_ACTION_HIGHLIGHT){
							highlightROIInfo.highlightROI.getRoiImages()[z].getPixels()[index] = 1;
						}else if(regionAction.getAction() == RegionAction.REGION_ACTION_MERGESELECTEDWITHNEIGHBORS){
							int numNeighbors = regionAction.getNeighborsForRegionMap().get(currentRegionInfo).size();
							if(/*!regionAction.bLeaveMultiNeighborUnchanged || */numNeighbors==1){
								zSlice[index] = (byte)regionAction.getNeighborsForRegionMap().get(currentRegionInfo).first().intValue();
							}else{
								boolean hasBG = false;
								Integer randomNeighbor = null;
								Iterator<Integer> pixelValIter = regionAction.getNeighborsForRegionMap().get(currentRegionInfo).iterator();
								while(pixelValIter.hasNext()){
									Integer pixelValue = pixelValIter.next();
									if(pixelValue == 0){
										hasBG = true;
										break;
									}else{
										randomNeighbor = pixelValue;
									}
								}
								if(hasBG){//merge with background
									zSlice[index] = 0;
								}else{//merge with random
									zSlice[index] = randomNeighbor.byteValue();
								}
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
	
	private static ROI createEmptyROI(ISize iSize){
		//Highlight selected regions
		try{
			UShortImage[] ushortRegionHighlightArr = new UShortImage[iSize.getZ()];
			for (int i = 0; i < ushortRegionHighlightArr.length; i++) {
				ushortRegionHighlightArr[i] =
					new UShortImage(
							new short[iSize.getX()*iSize.getY()],
							DEFAULT_ORIGIN,DEFAULT_EXTENT,
							iSize.getX(),
							iSize.getY(),
							1);
			}
			return new ROI(ushortRegionHighlightArr,"roi");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
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
	}
	private boolean fillVoids(BufferedImage[] roiPixels,boolean bCheckOnly) throws Exception{
		if(true){throw new IllegalArgumentException("Not yet implemented fully");}
		boolean bHadAnyInternalVoids = false;
		int xSize = roiPixels[0].getWidth();
		int ySize = roiPixels[0].getHeight();
		int zSize = roiPixels.length;
		int XYSIZE = xSize*ySize;
		boolean bUseZ = roiPixels.length>1;
		VCImage checkImage = ROIMultiPaintManager.createVCImageFromBufferedImages(getImageDataSetChannel().getExtent(), roiComposite);
		RegionImage regionImage =
			new RegionImage(checkImage, 0 /*0 means generate no surfacecollection*/,
					checkImage.getExtent(),getImageDataSetChannel().getAllImages()[0].getOrigin(), RegionImage.NO_SMOOTHING,
					progressWaitPopup);
		RegionInfo[] newRegionInfos = regionImage.getRegionInfos();
		for (int i = 0; i < newRegionInfos.length; i++) {
			if(newRegionInfos[i].getPixelValue() == 0){
				boolean bInternalVoid = true;
				for (int z = 0; z < zSize; z++) {
					int zOffset = z*XYSIZE;
					for (int y = 0; y < ySize; y++) {
						int yoffset = y*xSize;
						int zyOffset = zOffset+yoffset;
						for (int x = 0; x < xSize; x++) {
							if(newRegionInfos[i].isIndexInRegion(zyOffset+x)){
								if(x==0 || y==0 | (bUseZ && z==0) |
									x==(xSize-1) ||
									y==(ySize-1) ||
									(bUseZ && z==(zSize-1))){
									bInternalVoid = false;
									break;
								}
							}
						}
						if(!bInternalVoid){break;}
					}
					if(!bInternalVoid){break;}
				}
				if(bInternalVoid){
					bHadAnyInternalVoids = true;
					if(bCheckOnly){
						return bHadAnyInternalVoids;
					}
					for (int z = 0; z < zSize; z++) {
						byte[] roiPixelsBytes = 
							((DataBufferByte)roiPixels[z].getRaster().getDataBuffer()).getData();
						for (int xy = 0; xy < XYSIZE; xy++) {
//							if(newRegionInfos[i].isIndexInRegion(j)){
//								fillvoidPixels[j]|= 0xFFFF;
//							}
						}
					}
				}
			}
		}
		return bHadAnyInternalVoids;
	}

	private static TreeMap<Integer, Integer>[] calculateCondensedBinsChannels0(ImageDataset[] sourceImageDatasetChannels){
		final int MAX_SCALE = 0x0000FFFF;
		int[] tempLookup = new int[MAX_SCALE+1];
		for (int i = 0; i < tempLookup.length; i++) {
			tempLookup[i] = i;
		}
		TreeMap<Integer, Integer>[] condensedBinsMapChannels = new TreeMap[sourceImageDatasetChannels.length];
		for (int c = 0; c < sourceImageDatasetChannels.length; c++) {
			int[] bins = new int[MAX_SCALE+1];
			int binTotal = 0;
			for (int z = 0; z < sourceImageDatasetChannels[c].getSizeZ(); z++) {
				for (int xy = 0; xy < sourceImageDatasetChannels[c].getAllImages()[z].getPixels().length; xy++) {
					int index = (int)(sourceImageDatasetChannels[c].getAllImages()[z].getPixels()[xy]&0x0000FFFF);
					boolean bSet = isSet(sourceImageDatasetChannels[c].getAllImages()[z].getPixels()[xy], MAX_SCALE, tempLookup,true,false);
					bins[index]+= (bSet?1:0);
					binTotal+= (bSet?1:0);
	
				}
			}
			TreeMap<Integer, Integer> condensedBinsMap = new TreeMap<Integer, Integer>();
			for (int i = 0; i < bins.length; i++) {
				if(bins[i] != 0){
					condensedBinsMap.put(i, bins[i]);
				}
			}
			condensedBinsMapChannels[c] = condensedBinsMap;
		}

		return condensedBinsMapChannels;
	}

	public static boolean isSet(short roiSourceDataUnsignedShort,int thresholdIndex,int[] thresholdLookupArr,boolean maskArrState,boolean bInvertThreshold){
		if(bInvertThreshold){
			if(((int)(roiSourceDataUnsignedShort&0x0000FFFF)) > thresholdLookupArr[thresholdIndex]){
				return false;
			}else{
				return maskArrState;
			}
		}else{
			if(((int)(roiSourceDataUnsignedShort&0x0000FFFF)) < thresholdLookupArr[thresholdLookupArr.length-1-thresholdIndex]){
				return false;
			}else{
				return maskArrState;
			}
		}

	}

}
