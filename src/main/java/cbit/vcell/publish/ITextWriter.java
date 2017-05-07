/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.publish;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;

import cbit.gui.graph.GraphContainerLayout;
import cbit.gui.graph.GraphContainerLayoutReactions;
import cbit.gui.graph.GraphContainerLayoutVCellClassical;
import cbit.image.DisplayAdapterService;
import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.graph.ReactionCartoon;
import cbit.vcell.graph.StructureMappingCartoon;
import cbit.vcell.graph.structures.AllStructureSuite;
import cbit.vcell.graph.structures.MembraneStructureSuite;
import cbit.vcell.graph.structures.SingleStructureSuite;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.FastInvariant;
import cbit.vcell.math.FastRate;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FilamentRegionEquation;
import cbit.vcell.math.FilamentSubDomain;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VCML;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionCanvas;
import cbit.vcell.model.ReactionCanvasDisplaySpec;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.gui.ExpressionCanvas;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.units.VCUnitDefinition;

/**
This is the root class that handles publishing of models in the Virtual Cell. It supports the publishing of BioModels, MathModels, 
and Geometries. This class should receive the object to be published, an output stream, a page format, as well as the 
publishing preferences object. The same ITextWriter instance can be reused to publish different models with different preferences.

 * Creation date: (4/18/2003 2:09:05 PM)
 * @author: John Wagner & Rashad Badrawi
*/
 
public abstract class ITextWriter {

	public static final String PDF_WRITER = "PDF_WRITER";
	public static final String HTM_WRITER = "HTML_WRITER";
	public static final String RTF_WRITER = "RTF_WRITER";
	protected static int DEF_IMAGE_WIDTH = 400;
	protected static int DEF_IMAGE_HEIGHT = 400;
	protected static final int DEF_GEOM_WIDTH = 150;
	protected static final int DEF_GEOM_HEIGHT = 150;
	

	//image resolution settings, for saving individual reaction and structure images.
	public static final String HIGH_RESOLUTION = "high resolution";               //default_scale*2.5
	public static final String MEDIUM_RESOLUTION = "medium resolution";           //default_scale*1.5
	public static final String LOW_RESOLUTION = "low resolution";                 //default_scale
	
	private static int DEF_FONT_SIZE = 9;
	private static int DEF_HEADER_FONT_SIZE = 11;
	private Font fieldFont = null;
	private Font fieldBold = null;
	
	protected Document document;
	

/**
	 * Comment
	 */
/*	private void createRegionImageIcon() throws Exception{
	
		final int DISPLAY_DIM_MAX = 256;
	
		if(getImage() == null){
			throw new Exception("CreateRegionImageIcon error No Image");
		}
		
		try{
			// int RGB interpretation as follows:
			// int bits(32): (alpha)31-24,(red)23-16,(green)15-8,(blue)7-0
			// for alpha: 0-most transparent(see-through), 255-most opaque(solid)
			
			//Reset colormap (grayscale)
			for(int i=0;i<cmap.length;i+= 1){
				int iv = (int)(0x000000FF&i);
				cmap[i] = 0xFF<<24 | iv<<16 | iv<<8 | i;
			}
			//stretch cmap grays
			if(getImage() != null && getImage().getPixelClasses().length < 32){
				for(int i=0;i< getImage().getPixelClasses().length;i+= 1){
					int stretchIndex = (int)(0xFF&getImage().getPixelClasses()[i].getPixel());
					int newI = 32+(i*((256-32)/getImage().getPixelClasses().length));
					cmap[stretchIndex] = 0xFF<<24 | newI<<16 | newI<<8 | newI;
				}
			}
			//Highlight the current region
			if(getImage() != null && getCurrentPixelClassIndex() != null){
				int index = getImage().getPixelClasses(getCurrentPixelClassIndex().intValue()).getPixel();
				if(index > 254){throw new Exception("PixelClass indexes must be less than 255");}//need to save last(255) for (grid,blanck,etc...)
				cmap[index] = java.awt.Color.red.getRGB();
			}
			//Set grid color
			cmap[cmap.length-1] = 0xFFFFFFFF; //white
	
			//Make ColorModel, re-use colormap
			java.awt.image.IndexColorModel icm =
					new java.awt.image.IndexColorModel(8, cmap.length,cmap,0,  false ,-1/, java.awt.image.DataBuffer.TYPE_BYTE);
	
			
			//Initialize image data
			if(pixelWR == null){
				//cbit.vcell.geometry.GeometrySpec gs = new cbit.vcell.geometry.GeometrySpec((cbit.sql.Version)null,getImage());
				//cbit.image.VCImage sampledImage = gs.getSampledImage();
				//if(sampledImage.getNumX() != getImage().getNumX() ||
					//sampledImage.getNumY() != getImage().getNumY() ||
					//sampledImage.getNumZ() != getImage().getNumZ()){
						//cbit.vcell.client.PopupGenerator.showInfoDialog(
							//"Image was too large ("+getImage().getNumX()+","+getImage().getNumY()+","+getImage().getNumZ()+") and has been down-sampled.\n"+
							//"The new size will be "+sampledImage.getNumX()+","+sampledImage.getNumY()+","+sampledImage.getNumZ()+")\n"+
							//"Features may have been distorted or removed.  If displayed image is not acceptable"+
							//"To prevent sampling, image length should be less than "+cbit.vcell.geometry.GeometrySpec.GS_3D_MAX
							//);
				//}
				cbit.image.VCImage sampledImage = getImage();
				double side = Math.sqrt(sampledImage.getNumX()*sampledImage.getNumY()*sampledImage.getNumZ());
				xSide = (int)Math.round(side/(double)sampledImage.getNumX());
				if(xSide == 0){xSide = 1;}
				if(xSide > sampledImage.getNumZ()){
					xSide = sampledImage.getNumZ();
				}
				ySide = (int)Math.ceil((double)sampledImage.getNumZ()/(double)xSide);
				if(ySide == 0){ySide = 1;}
				if(ySide > sampledImage.getNumZ()){
					ySide = sampledImage.getNumZ();
				}
				pixelWR = icm.createCompatibleWritableRaster(xSide*sampledImage.getNumX(),ySide*sampledImage.getNumY());
				byte[] sib = sampledImage.getPixels();
	
				//write the image to buffer
				int rowStride = xSide*sampledImage.getNumX()*sampledImage.getNumY();
				int ystride = sampledImage.getNumX();
				int zstride = sampledImage.getNumX()*sampledImage.getNumY();
				for(int row=0;row < ySide;row+= 1){
					for(int col=0;col<xSide;col+= 1){
						int xoffset = col*sampledImage.getNumX();
						int yoffset = (row*sampledImage.getNumY());
						int zoffset = (col+(row*xSide))*zstride;
						if(zoffset >= sib.length){
							for(int x=0;x<sampledImage.getNumX();x+= 1){
								for(int y=0;y<sampledImage.getNumY();y+= 1){
									pixelWR.setSample(x+xoffset,y+yoffset,0,cmap.length-1);
								}
							}
						}else{
							for(int x=0;x<sampledImage.getNumX();x+= 1){
								for(int y=0;y<sampledImage.getNumY();y+= 1){
									pixelWR.setSample(x+xoffset,y+yoffset,0,(int)(0xFF&sib[x+(ystride*y)+zoffset]));
								}
							}
						}
					}
				}
				// scale if necessary
				displayScale = 1.0;
				if(pixelWR.getWidth() < DISPLAY_DIM_MAX || pixelWR.getHeight() < DISPLAY_DIM_MAX){
					displayScale = (int)Math.min((DISPLAY_DIM_MAX/pixelWR.getWidth()),(DISPLAY_DIM_MAX/pixelWR.getHeight()));
					if(displayScale == 0){displayScale = 1;}
				}
				if((displayScale == 1) && (pixelWR.getWidth() > DISPLAY_DIM_MAX || pixelWR.getHeight() > DISPLAY_DIM_MAX)){
					displayScale = Math.max((pixelWR.getWidth()/DISPLAY_DIM_MAX),(pixelWR.getHeight()/DISPLAY_DIM_MAX));
					//displayScale = Math.min(((double)DISPLAY_DIM_MAX/(double)pixelWR.getWidth()),((double)DISPLAY_DIM_MAX/(double)pixelWR.getHeight()));
					if(displayScale == 0){displayScale = 1;}
					displayScale = 1.0/displayScale;
				}
				if(displayScale != 1){
					java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
					at.setToScale(displayScale,displayScale);
					java.awt.image.AffineTransformOp ato = new java.awt.image.AffineTransformOp(at,java.awt.image.AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					smallPixelWR = ato.createCompatibleDestRaster(pixelWR);
					ato.filter(pixelWR,smallPixelWR);
					getFullSizeJCheckBox().setEnabled(true);
				}else{
					getFullSizeJCheckBox().setEnabled(false);
				}
			}
	
			//Create display image, re-use image data and colormap
			// draw labels and grid
			if(pixelWR != null){
				java.awt.image.BufferedImage bi = null;
				if(!getFullSizeJCheckBox().isEnabled() || getFullSizeJCheckBox().isSelected()){
					bi = new java.awt.image.BufferedImage(icm,pixelWR,false,null);
				}else{
					bi = new java.awt.image.BufferedImage(icm,smallPixelWR,false,null);
				}
	
				if(xSide > 0 || ySide > 0){
					int gridXBlockLen = (bi.getWidth()/xSide);
					int gridYBlockLen = (bi.getHeight()/ySide);
					
					java.awt.Graphics g = bi.getGraphics();
					g.setColor(java.awt.Color.white);
					// horiz lines
					for(int row=0;row < ySide;row+= 1){
						if(row > 0){
							g.drawLine(0,row*gridYBlockLen,bi.getWidth(),row*gridYBlockLen);
						}
					}
					// vert lines
					for(int col=0;col<xSide;col+= 1){
						if(col > 0){
							g.drawLine(col*gridXBlockLen,0,col*gridXBlockLen,bi.getHeight());
						}
					}
					// z markers
					if(xSide > 1 || ySide > 1){
						for(int row=0;row < xSide;row+= 1){
							for(int col=0;col<ySide;col+= 1){
								g.drawString(""+(1+row+(col*xSide)),row*gridXBlockLen+3,col*gridYBlockLen+12);
							}
						}
					}
				}
				
				javax.swing.ImageIcon rii = new javax.swing.ImageIcon(bi);
	
				getPixelClassImageLabel().setText(null);
				getPixelClassImageLabel().setIcon(rii);
			}else{
				getPixelClassImageLabel().setIcon(null);
				getPixelClassImageLabel().setText("No Image");
			}
		}catch(Throwable e){
			throw new Exception("CreateRegionImageIcon error\n"+(e.getMessage()!=null?e.getMessage():e.getClass().getName()));
		}
	}
*/
protected ITextWriter() {
	super();
}

//helper method. 
	private void addImage(Section container, ByteArrayOutputStream bos) throws Exception {
		com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(Toolkit.getDefaultToolkit().createImage(bos.toByteArray()), null);
		//com.lowagie.text.Image structImage = com.lowagie.text.Image.getInstance(bos.toByteArray());
		//Gif structImage = new Gif(bos.toByteArray());
		//setNewPage(container, image);
		image.setBackgroundColor(java.awt.Color.white);           //?
		Table imageTable = getTable(1,100, 2, 0, 0);
		Cell imageCell = new Cell();
		imageCell.setLeading(0); 
		imageCell.add(image);
		imageTable.addCell(imageCell);
		imageTable.setTableFitsPage(true);
		imageTable.setCellsFitPage(true);
		container.add(imageTable);
		/*
		com.lowagie.text.pdf.PdfPTable imageTable = new com.lowagie.text.pdf.PdfPTable(1);
		imageTable.setTotalWidth(image.width());
		com.lowagie.text.pdf.PdfPCell imageCell = new com.lowagie.text.pdf.PdfPCell(image);
		imageCell.setBorderWidth(1);
		*/
	}


protected Cell createCell(String text, Font font) throws DocumentException {
	
	return createCell(text, font, 1, 1, Element.ALIGN_LEFT, false);
}


protected Cell createCell(String text, Font font, int colspan) throws DocumentException {

	return createCell(text, font, colspan, 1, Element.ALIGN_LEFT, false);
}


	protected Cell createCell(String text, Font font, int colspan, int borderWidth, int alignment, boolean isHeader) throws DocumentException {

		Cell cell = new Cell(new Paragraph(text, font));
		cell.setBorderWidth(borderWidth);
		cell.setHorizontalAlignment(alignment);
		if (colspan > 1) {
			cell.setColspan(colspan);
		}
		if (isHeader) {
			cell.setHeader(true);
		}	
		return(cell);
	}


public abstract DocWriter createDocWriter(FileOutputStream fileOutputStream) throws DocumentException;

protected Cell createHeaderCell(String text, Font font, int colspan) throws DocumentException {
	
	return createCell(text, font, colspan, 1, Element.ALIGN_LEFT, true);
}

	public static BufferedImage generateDocReactionsImage(Model model,Integer width) throws Exception {
			                                                       
//	    if (model == null || !isValidResolutionSetting(resolution)) {
//	    	throw new IllegalArgumentException("Invalid parameters for generating reactions image for  model: " + model.getName());
//	    }
		ReactionCartoon rcartoon = new ReactionCartoon();
		rcartoon.setModel(model);
		StructureSuite structureSuite = new AllStructureSuite(new Model.Owner() {
			@Override
			public Model getModel() {
				return model;
			}
		});
		rcartoon.setStructureSuite(structureSuite);
		rcartoon.refreshAll();
		//dummy settings to get the real dimensions.
		BufferedImage dummyBufferedImage = new BufferedImage(DEF_IMAGE_WIDTH, DEF_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D dummyGraphics = (Graphics2D)dummyBufferedImage.getGraphics();
		Dimension prefDim = rcartoon.getPreferedCanvasSize(dummyGraphics);
		dummyGraphics.dispose();
//		double width = prefDim.getWidth();
//		double height = prefDim.getHeight();
		double widthHeightRatio = (double)prefDim.getWidth()/(double)prefDim.getHeight();
		if(width == null){
			width = ITextWriter.DEF_IMAGE_WIDTH;
		}
		int height = (int)((double)width/widthHeightRatio);
		
//		int MAX_IMAGE_HEIGHT = 532;
//		if (width < ITextWriter.DEF_IMAGE_WIDTH) {
//			width = ITextWriter.DEF_IMAGE_WIDTH;
//		} 
//		height= height * width/prefDim.getWidth();
//		if (height < ITextWriter.DEF_IMAGE_HEIGHT) {
//			height = ITextWriter.DEF_IMAGE_HEIGHT;
//		} else if (height > MAX_IMAGE_HEIGHT) {
//			height = MAX_IMAGE_HEIGHT;
//		}
//		width= width * height/prefDim.getHeight();
		Dimension newDim = new Dimension((int)width,(int)height);

		rcartoon.getResizeManager().setZoomPercent((int)(100*width/prefDim.getWidth()));

		BufferedImage bufferedImage = new BufferedImage(newDim.width,newDim.height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		GraphContainerLayoutReactions containerLayout = new GraphContainerLayoutReactions();
		containerLayout.layout(rcartoon, g, prefDim);
		
		rcartoon.paint(g);
		g.dispose();
		return bufferedImage;
	}



	public static ByteArrayOutputStream encodeJPEG(BufferedImage bufferedImage) throws Exception{
		ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
		imageWriter.setOutput(imageOutputStream);
		ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
		imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		imageWriteParam.setCompressionQuality(1.0f);  // quality 0(very compressed, lossy) -> 1.0(less compressed,loss-less)
		IIOImage iioImage = new IIOImage(bufferedImage, null, null);
		imageWriter.write(null, iioImage, imageWriteParam);
		imageOutputStream.close();
		imageWriter.dispose();
		return byteArrayOutputStream;
	}

//pretty similar to its static counterpart
	/*
	protected ByteArrayOutputStream generateDocStructureImage(Model model, String resolution) throws Exception {

		if (model == null || !isValidResolutionSetting(resolution)) {
	    	throw new IllegalArgumentException("Invalid parameters for generating structure image for model:" + model.getName());
        }
		ByteArrayOutputStream bos;

		// Create a new model and clone the structures only
		// Getting rid of species so that the image created will not have a problem being added to the document
		// when there are more than 15 species in the model.
		Model sparseModel = new Model(model.getName());
		Structure[] oldStructures = (Structure[])BeanUtils.cloneSerializable(model.getStructures());
		sparseModel.setStructures(oldStructures);
		 
		StructureCartoon scartoon = new StructureCartoon();
		scartoon.setModel(sparseModel);
		scartoon.refreshAll();
		//scartoon.setZoomPercent(scartoon.getZoomPercent()*3);
		BufferedImage dummyBufferedImage = new BufferedImage(DEF_IMAGE_WIDTH, DEF_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D dummyGraphics = (Graphics2D)dummyBufferedImage.getGraphics();
		Dimension prefDim = scartoon.getPreferedCanvasSize(dummyGraphics);
		
		int width = (int)prefDim.getWidth()*110/100;
		int height = (int)prefDim.getHeight()*110/100;
		if (width < ITextWriter.DEF_IMAGE_WIDTH) {
			width = ITextWriter.DEF_IMAGE_WIDTH;
		}
		if (height < ITextWriter.DEF_IMAGE_HEIGHT) {
			height = ITextWriter.DEF_IMAGE_HEIGHT;
		}

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		while (true) {
			GraphContainerLayout containerLayout = new GraphContainerLayoutVCellClassical();
			containerLayout.layout(scartoon, g, new Dimension(width,height));
			break;
		}
		scartoon.paint(g, null);
		bos = encodeJPEG(bufferedImage);
		return bos;
	}
*/

	protected ByteArrayOutputStream generateGeometryImage(Geometry geom) throws Exception{

		GeometrySpec geomSpec = geom.getGeometrySpec();
		IndexColorModel icm = DisplayAdapterService.getHandleColorMap();
		geom.precomputeAll(new GeometryThumbnailImageFactoryAWT());
		VCImage geomImage = geomSpec.getSampledImage().getCurrentValue();		
		if(geomImage == null){
			throw new Exception("generateGeometryImage error : No Image");
		}
		int x = geomImage.getNumX(); 
		int y = geomImage.getNumY();
		int z = geomImage.getNumZ();

		BufferedImage bufferedImage = null;
		WritableRaster pixelWR = null;
		Image adjImage = null;
		BufferedImage newBufferedImage = null;
		
		if (geom.getDimension() > 0 && geom.getDimension() < 3) {
			bufferedImage = new BufferedImage(x, y, BufferedImage.TYPE_BYTE_INDEXED, icm);
			pixelWR = bufferedImage.getRaster();
			for (int i = 0; i < x; i++){
				for (int j = 0; j < y; j++){
					pixelWR.setSample(i , j, 0, geomImage.getPixel(i, j, 0));
				}
				
			}
			// Adjust the image width and height 
			// retaining the aspect ratio. Start by adjusting the height, then adjust width to maintain aspect ratio.
	
			double scaleFactor = 1.0;
		    if (x * scaleFactor > DEF_GEOM_WIDTH) {
		        scaleFactor = ((double) DEF_GEOM_WIDTH) / x;
		    }
		    if (y * scaleFactor > DEF_GEOM_HEIGHT) {
		        scaleFactor = ((double) DEF_GEOM_HEIGHT) / y;
		    }
			int adjX = (int)Math.ceil(x*scaleFactor);
			int adjY = (int)Math.ceil(y*scaleFactor);
			
			adjImage = bufferedImage.getScaledInstance(adjX, adjY, BufferedImage.SCALE_REPLICATE);
			newBufferedImage = new BufferedImage(adjX, adjY, BufferedImage.TYPE_BYTE_INDEXED, icm);
			newBufferedImage.getGraphics().drawImage(adjImage, 0, 0, null);
		} else if (geom.getDimension() == 3) {			
			WritableRaster smallPixelWR = null;
			int[] cmap = new int[256];
			final int DISPLAY_DIM_MAX = 256;
			
			try{
				// int RGB interpretation as follows:
				// int bits(32): (alpha)31-24,(red)23-16,(green)15-8,(blue)7-0
				// for alpha: 0-most transparent(see-through), 255-most opaque(solid)
				
				//Reset colormap (grayscale)
				for(int i = 0; i < cmap.length; i += 1){
					int iv = (int)(0x000000FF&i);
					cmap[i] = 0xFF<<24 | iv<<16 | iv<<8 | i;
				}
				//stretch cmap grays
				if(geomImage != null && geomImage.getPixelClasses().length < 32){
					for(int i=0;i< geomImage.getPixelClasses().length;i+= 1){
						int stretchIndex = (int)(0xFF&geomImage.getPixelClasses()[i].getPixel());
						int newI = 32+(i*((256-32)/geomImage.getPixelClasses().length));
						cmap[stretchIndex] = 0xFF<<24 | newI<<16 | newI<<8 | newI;
					}
				}
				//Set grid color
				cmap[cmap.length-1] = 0xFFFFFFFF; //white
		
				//Initialize image data
				int xSide = 0;
				int ySide = 0;
				if(pixelWR == null){
					VCImage sampledImage = geomImage;
					double side = Math.sqrt(x*y*z);
					xSide = (int)Math.round(side/(double)x);
					if(xSide == 0){xSide = 1;}
					if(xSide > z){
						xSide = z;
					}
					ySide = (int)Math.ceil((double)z/(double)xSide);
					if(ySide == 0){ySide = 1;}
					if(ySide > z){
						ySide = z;
					}
					pixelWR = icm.createCompatibleWritableRaster(xSide*x,ySide*y);
					byte[] sib = sampledImage.getPixels();
		
					//write the image to buffer
					int ystride = x;
					int zstride = x*y;
					for(int row = 0; row < ySide; row += 1){
						for(int col = 0; col < xSide; col += 1){
							int xoffset = col*x;
							int yoffset = (row*y);
							int zoffset = (col+(row*xSide))*zstride;
							if(zoffset >= sib.length){
								for(int xi = 0; xi < x; xi += 1){
									for(int yi = 0; yi < y; yi += 1){
										pixelWR.setSample(xi + xoffset, yi + yoffset, 0, cmap.length-1);
									}
								}
							}else{
								for(int xi = 0; xi < x; xi += 1){
									for(int yi = 0; yi < y; yi += 1){
										pixelWR.setSample(xi + xoffset, yi + yoffset,0,(int)(0xFF&sib[xi + (ystride*yi) + zoffset]));
									}
								}
							}
						}
					}
					
					// scale if necessary
					double displayScale = 1.0;
					if(pixelWR.getWidth() < DISPLAY_DIM_MAX || pixelWR.getHeight() < DISPLAY_DIM_MAX){
						displayScale = (int)Math.min((DISPLAY_DIM_MAX/pixelWR.getWidth()),(DISPLAY_DIM_MAX/pixelWR.getHeight()));
						if(displayScale == 0){displayScale = 1;}
					}
					if((displayScale == 1) && (pixelWR.getWidth() > DISPLAY_DIM_MAX || pixelWR.getHeight() > DISPLAY_DIM_MAX)){
						displayScale = Math.max((pixelWR.getWidth()/DISPLAY_DIM_MAX),(pixelWR.getHeight()/DISPLAY_DIM_MAX));
						//displayScale = Math.min(((double)DISPLAY_DIM_MAX/(double)pixelWR.getWidth()),((double)DISPLAY_DIM_MAX/(double)pixelWR.getHeight()));
						if(displayScale == 0) {displayScale = 1;}
						displayScale = 1.0/displayScale;
					}
					if(displayScale != 1){
						java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
						at.setToScale(displayScale, displayScale);
						java.awt.image.AffineTransformOp ato = new java.awt.image.AffineTransformOp(at,java.awt.image.AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
						smallPixelWR = ato.createCompatibleDestRaster(pixelWR);
						ato.filter(pixelWR, smallPixelWR);
					}
				}
		
				//Create display image, re-use image data and colormap
				// draw labels and grid
				if(pixelWR != null){
					bufferedImage = new java.awt.image.BufferedImage(icm,smallPixelWR,false,null);
	
					if(xSide > 0 || ySide > 0){
						float gridXBlockLen = ((float)(bufferedImage.getWidth())/xSide);
						float gridYBlockLen = ((float)(bufferedImage.getHeight())/ySide);
						
						java.awt.Graphics g = bufferedImage.getGraphics();
						g.setColor(java.awt.Color.white);
						// horiz lines
						for(int row=0;row < ySide;row+= 1){
							if(row > 0){
								g.drawLine(0,(int)(row*gridYBlockLen),bufferedImage.getWidth(),(int)(row*gridYBlockLen));
							}
						}
						// vert lines
						for(int col=0;col<xSide;col+= 1){
							if(col > 0){
								g.drawLine((int)(col*gridXBlockLen),0,(int)(col*gridXBlockLen),bufferedImage.getHeight());
							}
						}
						// z markers
						if(xSide > 1 || ySide > 1){
							for(int row=0;row < xSide;row+= 1){
								for(int col=0;col<ySide;col+= 1){
									g.drawString(""+(1+row+(col*xSide)),(int)(row*gridXBlockLen)+3,(int)(col*gridYBlockLen)+12);
								}
							}
						}
					}
				}		
			}catch(Throwable e){
				throw new Exception("CreateGeometryImageIcon error\n"+(e.getMessage()!=null?e.getMessage():e.getClass().getName()));
			}
			
			// Adjust the image width and height 
			adjImage = bufferedImage.getScaledInstance(smallPixelWR.getWidth(), smallPixelWR.getHeight(), BufferedImage.SCALE_REPLICATE);
			newBufferedImage = new BufferedImage(smallPixelWR.getWidth(), smallPixelWR.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, icm);
			newBufferedImage.getGraphics().drawImage(adjImage, 0, 0, null);
		}
		
		
		ByteArrayOutputStream bos = null;
		bos = encodeJPEG(newBufferedImage);
		return bos;
	}


//	public static ByteArrayOutputStream generateReactionsImage(ReactionCartoonTool reactionCartoonToolIN) throws Exception {
//		
//		Shape selectedShape = null;
////		try {
//			Point relPosition = reactionCartoonToolIN.getReactionCartoon().getTopShape().getRelPos();
//			int zoomPercent = reactionCartoonToolIN.getReactionCartoon().getResizeManager().getZoomPercent();
//			
//			//unselect reactioncontainershape to remove highlights
//			selectedShape = reactionCartoonToolIN.getReactionCartoon().getSelectedShape();
//			if((selectedShape instanceof ReactionContainerShape)){
//				reactionCartoonToolIN.getReactionCartoon().clearSelection();
//			}
//			ByteArrayOutputStream bos;
//			//dummy settings to get the real dimensions.
//			BufferedImage dummyBufferedImage = new BufferedImage(DEF_IMAGE_WIDTH, DEF_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
//			Graphics2D dummyGraphics = (Graphics2D)dummyBufferedImage.getGraphics();
//			Dimension prefDim = reactionCartoonToolIN.getReactionCartoon().getPreferedCanvasSize(dummyGraphics);
//			BufferedImage bufferedImage;
//			Graphics2D g;
//			bufferedImage = new BufferedImage(prefDim.width/*-relPosition.x*/, prefDim.height/*-relPosition.y*/, BufferedImage.TYPE_3BYTE_BGR);
//			g = (Graphics2D)bufferedImage.getGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//			g.setFont(reactionCartoonToolIN.getGraphPane().getFont());
//			AffineTransform xform = g.getTransform();
//			xform.translate(-relPosition.x*zoomPercent/100,-relPosition.y*zoomPercent/100);
//			g.setTransform(xform);
//			reactionCartoonToolIN.getReactionCartoon().paint(g, reactionCartoonToolIN.getGraphPane());
//			bos = encodeJPEG(bufferedImage);
//			return bos;
////		}finally{
////			if(selectedShape != null){reactionCartoonToolIN.getReactionCartoon().selectShape(selectedShape);}
////		}
//	}

/*
	public static ByteArrayOutputStream generateStructureImage(Model model, String resolution) throws Exception {

		final int SIZE_INC = 10; 
		if (model == null || !isValidResolutionSetting(resolution)) {
	    	throw new IllegalArgumentException("Invalid parameters for generating structure image for model:" + model.getName());
        }
		ByteArrayOutputStream bos;
		StructureCartoon scartoon = new StructureCartoon();
		scartoon.setModel(model);
		scartoon.refreshAll();
		int zoom = ITextWriter.getZoom(resolution);
		System.out.println(resolution + " " + zoom);
		scartoon.getResizeManager().setZoomPercent(scartoon.getResizeManager().getZoomPercent()*zoom);
		//dummy settings to get the real dimensions.
		BufferedImage dummyBufferedImage = new BufferedImage(DEF_IMAGE_WIDTH, DEF_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D dummyGraphics = (Graphics2D)dummyBufferedImage.getGraphics();
		Dimension prefDim = scartoon.getPreferedCanvasSize(dummyGraphics);
		int width = (int)prefDim.getWidth()*110/100 + SIZE_INC*30;                 //too generous
		int height = (int)prefDim.getHeight()*110/100 + SIZE_INC*30;
		BufferedImage bufferedImage;
		Graphics2D g;
		while (true) {
			System.out.println("Image width: " + width + " height: " + height);
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			g = (Graphics2D)bufferedImage.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			GraphContainerLayout containerLayout = new GraphContainerLayoutVCellClassical();
			containerLayout.layout(scartoon, g, new Dimension(width,height));
			break;
		}
		scartoon.paint(g, null);
		bos = encodeJPEG(bufferedImage);
		return bos;
	}
*/

	protected ByteArrayOutputStream generateStructureMappingImage(SimulationContext simContext, int width, int height) throws Exception {
             
		StructureMappingCartoon structMapCartoon = new StructureMappingCartoon();
		structMapCartoon.setSimulationContext(simContext);
		structMapCartoon.refreshAll();                           
	    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
		while (true) {
			GraphContainerLayout containerLayout = new GraphContainerLayoutVCellClassical();
			containerLayout.layout(structMapCartoon, g, new Dimension(width,height));
			break;
		}
		structMapCartoon.paint(g);
		java.io.ByteArrayOutputStream bos = encodeJPEG(bufferedImage);
		return bos;
	}


protected Font getBold() {

	return getBold(DEF_FONT_SIZE);
}


	protected Font getBold(int fontSize) {

		if (fieldBold == null || (fontSize != DEF_FONT_SIZE)) {              //not the default font.
			try {
				BaseFont boldBaseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				fieldBold = new Font(boldBaseFont, DEF_FONT_SIZE, Font.NORMAL);
			} catch (Exception e) {
				System.out.println("ITextWriter.getBold() threw an unexpected exception!!!");
				e.printStackTrace();
			}
		}
		return(fieldBold);
	}


protected Font getFont() {
	if (fieldFont == null) {
		try {
			BaseFont fontBaseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			fieldFont = new Font(fontBaseFont, DEF_FONT_SIZE, Font.NORMAL);
		} catch (Exception e) {
			System.out.println("ITextWriter.getFont() threw an unexpected exception!!!");
			e.printStackTrace();
		}
	}
	return(fieldFont);
}


	public static ITextWriter getInstance(String writerType) {

		ITextWriter newWriter;
		if (ITextWriter.PDF_WRITER.equals(writerType)) {
			newWriter = new PDFWriter();			
		} else if (ITextWriter.HTM_WRITER.equals(writerType)) {
			newWriter = new HTMWriter();
		} else if (ITextWriter.RTF_WRITER.equals(writerType)) {
			newWriter = new RTFWriter();
		} else {
			throw new IllegalArgumentException("Unsupported writer type: " + writerType);
		}

		return newWriter;
	}


	protected Table getTable(int colNum, int width, int borderWidth, int cellPadding, int cellSpacing) throws DocumentException {

		Table genericTable = new Table(colNum);
		genericTable.setWidth(width);
		genericTable.setBorderWidth(borderWidth);
		genericTable.setPadding(cellPadding);
 		//genericTable.setSpacing(cellSpacing);
 		genericTable.setSpacing(0);                            //a hack to improve tables spanning multiple pages.
 		genericTable.setAutoFillEmptyCells(true);
 		//genericTable.setTableFitsPage(true);
 		//genericTable.setCellsFitPage(true);
 		return genericTable;
	} 


	private static int getZoom (String resolution) {

		if (!isValidResolutionSetting(resolution)) {
			throw new RuntimeException("Invalid image resolution type: " + resolution);
		}
		if (HIGH_RESOLUTION.equals(resolution)) {
			return 300/100;
		} else if (MEDIUM_RESOLUTION.equals(resolution)) {
			return 200/100;
		} else {                                 //LOW-RESOLUTION
			return 100/100;
		} 
	}


	//helper method
	private boolean hasReactions(Model model, Structure struct) {

		ReactionStep rs [] = model.getReactionSteps();
		for (int i = 0; i < rs.length; i++) {
			if (rs[i].getStructure().getName().equals(struct.getName())) {
				return true;
			}
		}

		return false;
	}


	public static final boolean isValidResolutionSetting(String resolution) {

		if (HIGH_RESOLUTION.equals(resolution) || MEDIUM_RESOLUTION.equals(resolution) || LOW_RESOLUTION.equals(resolution)) {
			return true;
		}

		return false;
	}


	protected void createDocument(PageFormat pageFormat) {

		Rectangle pageSize = new Rectangle((float) pageFormat.getWidth(), (float) pageFormat.getHeight());
		double marginL = pageFormat.getImageableX();
		double marginT = pageFormat.getImageableY();
		double marginR = pageFormat.getWidth()  - pageFormat.getImageableWidth()  - marginL;
		double marginB = pageFormat.getHeight() - pageFormat.getImageableHeight() - marginT;
		//System.out.println(pageFormat.getWidth() +  " " + pageFormat.getHeight() + " " + marginL + " " + marginT);
		ITextWriter.DEF_IMAGE_WIDTH = (int)pageFormat.getImageableWidth();
		//ITextWriter.DEF_IMAGE_HEIGHT = (int)pageFormat.getImageableHeight();
		//can also use some of the built-in PageSize objects, like PageSize.A4, PageSize.LETTER
		this.document = new Document(pageSize, (float) marginL, (float) marginR, (float) marginT, (float) marginB);
	}


	public void writeBioModel(BioModel biomodel, FileOutputStream fos, PageFormat pageFormat) throws Exception {

		writeBioModel(biomodel, fos, pageFormat, PublishPreferences.DEFAULT_BIO_PREF);
	}


public void writeBioModel(BioModel bioModel, FileOutputStream fos, PageFormat pageFormat, PublishPreferences preferences) throws Exception {

	if (bioModel == null || fos == null || pageFormat == null || preferences == null) {
		throw new IllegalArgumentException("One or more null params while publishing BioModel.");
	}
	try {
		createDocument(pageFormat);
		createDocWriter(fos);
		// Add metadata before you open the document...
		String name = bioModel.getName().trim();
		String userName = "Unknown";
		if (bioModel.getVersion() != null) {
			userName = bioModel.getVersion().getOwner().getName();
		}
		document.addTitle(name + "[owned by " + userName + "]");
		document.addCreator("Virtual Cell");
		document.addCreationDate();
		//writeWatermark(document, pageFormat);
		writeHeaderFooter("BioModel: " + name);
		document.open();
		//
		Section introSection = null;
		int chapterNum = 1;
		if (preferences.includePhysio()) {
			Chapter physioChapter = new Chapter("Physiology For " + name, chapterNum++);
			introSection = physioChapter.addSection("General Info", physioChapter.numberDepth() + 1);      
			String freeTextAnnotation = bioModel.getVCMetaData().getFreeTextAnnotation(bioModel);
			writeMetadata(introSection, name, freeTextAnnotation, userName, "BioModel");
			writeModel(physioChapter, bioModel.getModel());
			document.add(physioChapter);
		}
		if (preferences.includeApp()) {
			SimulationContext simContexts [] = bioModel.getSimulationContexts();
			if (simContexts.length > 0) {
				Chapter simContextsChapter = new Chapter("Applications For " + name, chapterNum++);
				if (introSection == null) {
					introSection = simContextsChapter.addSection("General Info", simContextsChapter.numberDepth() + 1);         
					String freeTextAnnotation = bioModel.getVCMetaData().getFreeTextAnnotation(bioModel);
					writeMetadata(introSection, name, freeTextAnnotation, userName, "BioModel");
				}
				for (int i = 0; i < simContexts.length; i++) {
					writeSimulationContext(simContextsChapter, simContexts[i], preferences);                           
				}
				document.add(simContextsChapter);
			} else {
				System.err.println("Bad Request: No applications to publish for Biomodel: " + bioModel.getName());
			}
		}
		document.close();
	} catch (DocumentException e) {
		System.err.println("Unable to publish BioModel.");
		e.printStackTrace();
		throw e;
	}
}

	protected void writeEquation(Section container, Equation eq) throws DocumentException {

		if (eq instanceof FilamentRegionEquation) {
			writeFilamentRegionEquation(container, (FilamentRegionEquation)eq);
		} else if (eq instanceof MembraneRegionEquation) {
			writeMemRegionEquation(container, (MembraneRegionEquation)eq);
		} else if (eq instanceof OdeEquation) {
			writeOdeEquation(container, (OdeEquation)eq);
		} else if (eq instanceof PdeEquation) {
			writePdeEquation(container, (PdeEquation)eq);
		} else if (eq instanceof VolumeRegionEquation) {
			writeVolumeRegionEquation(container, (VolumeRegionEquation)eq);
		}
	}


	protected void writeFastSystem(Section container, FastSystem fs) throws DocumentException {
		
		Table eqTable = getTable(2, 100, 1, 2, 2);
		eqTable.addCell(createCell(VCML.FastSystem, 
						getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
		eqTable.endHeaders();
		Enumeration<FastInvariant> enum_fi = fs.getFastInvariants();
		while (enum_fi.hasMoreElements()){
			FastInvariant fi = enum_fi.nextElement();
			eqTable.addCell(createCell(VCML.FastInvariant, getFont()));
			eqTable.addCell(createCell(fi.getFunction().infix(), getFont()));
		}		
		Enumeration<FastRate> enum_fr = fs.getFastRates();
		while (enum_fr.hasMoreElements()){
			FastRate fr = enum_fr.nextElement();
			eqTable.addCell(createCell(VCML.FastRate, getFont()));
			eqTable.addCell(createCell(fr.getFunction().infix(), getFont()));
		}
		
		container.add(eqTable);	
	}


	protected void writeFilamentRegionEquation(Section container, FilamentRegionEquation eq) throws DocumentException {

		Table eqTable = getTable(2, 100, 1, 2, 2);
		eqTable.addCell(createCell(VCML.FilamentRegionEquation + " " + eq.getVariable().getName(), 
						getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
		eqTable.endHeaders();
		String exp = "0.0";
		eqTable.addCell(createCell(VCML.FilamentRate, getFont()));
		if (eq.getFilamentRateExpression() != null) {
			exp = eq.getFilamentRateExpression().infix();
		}
		eqTable.addCell(createCell(exp, getFont()));
		if (eq.getInitialExpression() != null) {
			eqTable.addCell(createCell(VCML.Initial, getFont()));
			eqTable.addCell(createCell(eq.getInitialExpression().infix(), getFont()));
		}
		int solutionType = eq.getSolutionType();
		switch (solutionType) {
			case Equation.UNKNOWN_SOLUTION:{
				if (eq.getInitialExpression() == null) {
					eqTable.addCell(createCell(VCML.Initial, getFont()));
					eqTable.addCell(createCell("0.0", getFont()));
				}
				break;
			}
			case Equation.EXACT_SOLUTION:{
				eqTable.addCell(createCell(VCML.Exact, getFont()));
				eqTable.addCell(createCell(eq.getExactSolution().infix(), getFont()));
				break;
			}
		}
		
		container.add(eqTable);	
	}


//Section used can be a chapter or a section of one, based on the document type. 
	protected void writeGeom(Section container, Geometry geom, GeometryContext geomCont) throws Exception {
		
		try {
			Section geomSection = container.addSection("Geometry: " + geom.getName(), container.numberDepth() + 1);
			if (geom.getDimension() == 0) {
				Paragraph p = new Paragraph(new Phrase("Non spatial geometry."));
				p.setAlignment(Paragraph.ALIGN_CENTER);
				geomSection.add(p);
				return;
			}
			ByteArrayOutputStream bos = generateGeometryImage(geom);                 
			com.lowagie.text.Image geomImage = com.lowagie.text.Image.getInstance(java.awt.Toolkit.getDefaultToolkit().createImage(bos.toByteArray()), null);
			geomImage.setAlignment(Table.ALIGN_LEFT);
			geomSection.add(geomImage);
			//addImage(geomSection, bos);
			Table geomTable = getTable(2, 50, 1, 2, 2);
			geomTable.setAlignment(Table.ALIGN_LEFT);
			Extent extent = geom.getExtent();
			String extentStr = "(" + extent.getX() + ", " + extent.getY() + ", " + extent.getZ() + ")";
			Origin origin = geom.getOrigin();
			String originStr = "(" + origin.getX() + ", " + origin.getY() + ", " + origin.getZ() + ")";
			geomTable.addCell(createCell("Size", getFont()));
			geomTable.addCell(createCell(extentStr, getFont()));
			geomTable.addCell(createCell("Origin", getFont()));
			geomTable.addCell(createCell(originStr, getFont()));
			geomSection.add(geomTable);
		} catch (Exception e) {
			System.err.println("Unable to add geometry image to report.");
			e.printStackTrace();
		}
	}


	public void writeGeometry(Geometry geom, FileOutputStream fos, PageFormat format) throws Exception {

		writeGeometry(geom, fos, format, PublishPreferences.DEFAULT_GEOM_PREF);
	}


	//for now, the preferences for a geometry is a dummy. 
	public void writeGeometry(Geometry geom, FileOutputStream fos, PageFormat pageFormat, PublishPreferences preferences) throws Exception {

		if (geom == null || fos == null || pageFormat == null || preferences == null) {
			throw new IllegalArgumentException("One or more null params while publishing Geometry.");
		}
		try {
			createDocument(pageFormat);
			createDocWriter(fos);
			//Add metadata before you open the document...
			String name = geom.getName().trim();
			String userName = "Unknown";
			if (geom.getVersion() != null) {
				userName = geom.getVersion().getOwner().getName();
			}
			document.addTitle(name + "[owned by " + userName + "]");
			document.addCreator("Virtual Cell");
			document.addCreationDate();
			//writeWatermark(document, pageFormat);
			writeHeaderFooter("Geometry: " + name);
			document.open();
			//
			Section introSection = null;
			int chapterNum = 1;
			Chapter geomChapter = new Chapter("Geometry", chapterNum++);
			introSection = geomChapter.addSection("General Info", geomChapter.numberDepth() + 1);                 
			writeMetadata(introSection, name, geom.getDescription(), userName, "Geometry");
			Section geomSection = geomChapter.addSection("Geometry", geomChapter.numberDepth() + 1);                  //title?
			writeGeom(geomSection, geom, null);
			document.add(geomChapter);
			document.close();
		} catch (DocumentException e) {
			System.err.println("Unable to publish BioModel.");
			e.printStackTrace();
			throw e;
		}
	}


/**
 * Default is no header or footer...
 */
protected void writeHeaderFooter(String headerStr) throws DocumentException {

}


/**
 * writeHorizontalLine comment.
 */
protected void writeHorizontalLine() throws DocumentException {
	// Default is no horizontal line...
}


	protected void writeJumpCondition(Section container, JumpCondition eq) throws DocumentException {

		Table eqTable = getTable(2, 100, 1, 2, 2);
		eqTable.addCell(createCell(VCML.JumpCondition + " " + eq.getVariable().getName(), 
						getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
		eqTable.endHeaders();
		String exp = "0.0";
		eqTable.addCell(createCell(VCML.InFlux, getFont()));
		if (eq.getInFluxExpression() != null) {
			exp = eq.getInFluxExpression().infix();
		}
		eqTable.addCell(createCell(exp, getFont()));
		exp = "0.0";
		eqTable.addCell(createCell(VCML.OutFlux, getFont()));
		if (eq.getOutFluxExpression() != null) {
			exp = eq.getOutFluxExpression().infix();
		}
		eqTable.addCell(createCell(exp, getFont()));
		
		container.add(eqTable);
	}


	protected void writeKineticsParams(Section reactionSection, ReactionStep rs) throws DocumentException {

		Kinetics.KineticsParameter kineticsParameters[] = rs.getKinetics().getKineticsParameters();
		Table paramTable = null;
		int widths [] = {1, 4, 3, 2};
		if (kineticsParameters.length > 0) {
			paramTable = getTable(4, 100, 1, 3, 3);
			paramTable.addCell(createCell("Kinetics Parameters", getBold(DEF_HEADER_FONT_SIZE), 4, 1, Element.ALIGN_CENTER, true));
			paramTable.addCell(createHeaderCell("Name", getBold(), 1));
			paramTable.addCell(createHeaderCell("Expression", getBold(), 1));
			paramTable.addCell(createHeaderCell("Role", getBold(), 1));
			paramTable.addCell(createHeaderCell("Unit", getBold(), 1));
			paramTable.endHeaders();
			for (int k = 0; k < kineticsParameters.length; k++) {
				String name = kineticsParameters[k].getName();
				Expression expression = kineticsParameters[k].getExpression();
				String role = rs.getKinetics().getDefaultParameterDesc(kineticsParameters[k].getRole());
				VCUnitDefinition unit = kineticsParameters[k].getUnitDefinition();
				paramTable.addCell(createCell(name, getFont()));
				paramTable.addCell(createCell((expression == null ? "": expression.infix()), getFont()));
				paramTable.addCell(createCell(role, getFont()));
				paramTable.addCell(createCell((unit == null ? "": unit.getSymbolUnicode()), getFont()));      //dimensionless will show as '1'.
			}
		}
		if (paramTable != null) {
			paramTable.setWidths(widths);
			reactionSection.add(paramTable);
		}
	}


//container can be a chapter or a section of a chapter.
//MathDescription.description ignored.
//currently not used.
	protected void writeMathDescAsImages(Section container, MathDescription mathDesc) throws DocumentException {

		if (mathDesc == null) {
			return;
		}
		Section mathDescSection = container.addSection("Math Description: " + mathDesc.getName(), container.depth() + 1);
		Section mathDescSubSection = null;
		Expression expArray [] = null;
		BufferedImage dummy = new BufferedImage(500, 50, BufferedImage.TYPE_3BYTE_BGR);
		int scale = 1;
		int viewableWidth = (int)(document.getPageSize().width() - document.leftMargin() - document.rightMargin());
		//add Constants
		Enumeration<Constant> constantsList = mathDesc.getConstants();
		while (constantsList.hasMoreElements()) {
			Constant constant = constantsList.nextElement();
			Expression exp = constant.getExpression();
			try {
				expArray = new Expression[] { Expression.assign(new Expression(constant.getName()), exp.flatten()) };
			} catch(ExpressionException ee) {
				System.err.println("Unable to process constant " + constant.getName() + " for publishing");
				ee.printStackTrace();
				continue;
			}
			try {
				Dimension dim = ExpressionCanvas.getExpressionImageSize(expArray, (Graphics2D)dummy.getGraphics());
				BufferedImage bufferedImage = new BufferedImage((int)dim.getWidth()*scale, (int)dim.getHeight()*scale, BufferedImage.TYPE_3BYTE_BGR);
				ExpressionCanvas.getExpressionAsImage(expArray, bufferedImage, scale);		           
				com.lowagie.text.Image expImage = com.lowagie.text.Image.getInstance(bufferedImage, null);
				expImage.setAlignment(com.lowagie.text.Image.ALIGN_LEFT);
				if (mathDescSubSection == null) {
					mathDescSubSection =  mathDescSection.addSection("Constants", mathDescSection.depth() + 1);
				}
				if (viewableWidth < Math.floor(expImage.scaledWidth())) {
					expImage.scaleToFit(viewableWidth, expImage.plainHeight());
					System.out.println("Constant After scaling: " + expImage.scaledWidth());
				}
				mathDescSubSection.add(expImage);
			} catch (Exception e) {
				System.err.println("Unable to add structure mapping image to report.");
				e.printStackTrace();
			}
		}
		mathDescSubSection = null;
		//add functions
		Enumeration<Function> functionsList = mathDesc.getFunctions();
		while (functionsList.hasMoreElements()) {
			Function function = functionsList.nextElement();
			Expression exp = function.getExpression();
			try {
				expArray = new Expression[] { Expression.assign(new Expression(function.getName()), exp.flatten()) };
			} catch(ExpressionException ee) {
				System.err.println("Unable to process function " + function.getName() + " for publishing");
				ee.printStackTrace();
				continue;
			}
			try {    
				Dimension dim = ExpressionCanvas.getExpressionImageSize(expArray, (Graphics2D)dummy.getGraphics());
				BufferedImage bufferedImage = new BufferedImage((int)dim.getWidth()*scale, (int)dim.getHeight()*scale, BufferedImage.TYPE_3BYTE_BGR);
				ExpressionCanvas.getExpressionAsImage(expArray, bufferedImage, scale);     
				com.lowagie.text.Image expImage = com.lowagie.text.Image.getInstance(bufferedImage, null);
				expImage.setAlignment(com.lowagie.text.Image.ALIGN_LEFT);
				if (mathDescSubSection == null) {
					mathDescSubSection =  mathDescSection.addSection("Functions", mathDescSection.depth() + 1);
				}
				if (viewableWidth < Math.floor(expImage.scaledWidth())) {
					expImage.scaleToFit(viewableWidth, expImage.height());
					System.out.println("Function After scaling: " + expImage.scaledWidth());
				}
				mathDescSubSection.add(expImage);
			} catch (Exception e) {
				System.err.println("Unable to add structure mapping image to report.");
				e.printStackTrace();
			}
		}
		writeSubDomainsEquationsAsImages(mathDescSection, mathDesc);
	}


	//container can be a chapter or a section of a chapter.
	//MathDescription.description ignored.
	protected void writeMathDescAsText(Section container, MathDescription mathDesc) throws DocumentException {

		if (mathDesc == null) {
			return;
		}
		Section mathDescSection = container.addSection("Math Description: " + mathDesc.getName(), container.depth() + 1);
		Section mathDescSubSection = null;
		Table expTable = null;
		int widths [] = {2, 8};
		//add Constants
		Enumeration<Constant> constantsList = mathDesc.getConstants();
		while (constantsList.hasMoreElements()) {
			Constant constant = constantsList.nextElement();
			Expression exp = constant.getExpression();
			if (expTable == null) {
				expTable = getTable(2, 100, 1, 2, 2);
				expTable.addCell(createHeaderCell("Constant Name", getBold(), 1));
				expTable.addCell(createHeaderCell("Expression", getBold(), 1));
				expTable.setWidths(widths);
				expTable.endHeaders();
			}
			//widths[0] = Math.max(constant.getName().length(), widths[0]);       
			//widths[1] = Math.max(exp.infix().length(), widths[1]);
			expTable.addCell(createCell(constant.getName(), getFont()));
			expTable.addCell(createCell(exp.infix(), getFont()));
		}
		//expTable.setWidths(widths);    breaks the contents of the cell, also, widths[1] = widths[1]/widths[0], widths[0] = 1
		if (expTable != null) {
			mathDescSubSection =  mathDescSection.addSection("Constants", mathDescSection.depth() + 1);
			mathDescSubSection.add(expTable);
			expTable = null;
		}
		mathDescSubSection = null;
		//add functions
		Enumeration<Function> functionsList = mathDesc.getFunctions();
		while (functionsList.hasMoreElements()) {
			Function function = functionsList.nextElement();
			Expression exp = function.getExpression();
			if (expTable == null) {
				expTable = getTable(2, 100, 1, 2, 2);
				expTable.addCell(createHeaderCell("Function Name", getBold(), 1));
				expTable.addCell(createHeaderCell("Expression", getBold(), 1));
				expTable.endHeaders();
				expTable.setWidths(widths);
			}
			expTable.addCell(createCell(function.getName(), getFont()));
			expTable.addCell(createCell(exp.infix(), getFont()));
		}
		if (expTable != null) {
			mathDescSubSection =  mathDescSection.addSection("Functions", mathDescSection.depth() + 1);
			mathDescSubSection.add(expTable);
		}
		writeSubDomainsEquationsAsText(mathDescSection, mathDesc);
	}


	public void writeMathModel(MathModel mathModel, FileOutputStream fos, PageFormat pageFormat) throws Exception {

		writeMathModel(mathModel, fos, pageFormat, PublishPreferences.DEFAULT_MATH_PREF);
	}


	public void writeMathModel(MathModel mathModel, FileOutputStream fos, PageFormat pageFormat, PublishPreferences preferences) throws Exception {

		if (mathModel == null || fos == null || pageFormat == null || preferences == null) {
			throw new IllegalArgumentException("One or more null params while publishing MathModel.");
		}
		try {
			createDocument(pageFormat);
			createDocWriter(fos);
			// Add metadata before you open the document...
			String name = mathModel.getName().trim();
			String userName = "Unknown";
			if (mathModel.getVersion() != null) {
				userName = mathModel.getVersion().getOwner().getName();
			}
			document.addTitle(name + "[owned by " + userName + "]");
			document.addCreator("Virtual Cell");
			document.addCreationDate();
			//writeWatermark(document, pageFormat);
			writeHeaderFooter("MathModel: " + name);
			document.open();
			//
			int chapterNum = 1;
			Section introSection = null;
			if (preferences.includeMathDesc()) {
				MathDescription mathDesc = mathModel.getMathDescription();
				Chapter mathDescChapter = new Chapter("Math Description", chapterNum++);
				introSection = mathDescChapter.addSection("General Info", mathDescChapter.numberDepth() + 1);                 
				writeMetadata(introSection, name, mathModel.getDescription(), userName, "MathModel");
				writeMathDescAsText(mathDescChapter, mathDesc);	
				document.add(mathDescChapter);		
			}  
			if (preferences.includeSim()) {                                   //unlike biomodels, simulations are chapters, not chapter sections.
				Simulation [] sims = mathModel.getSimulations();
				if (sims != null) {
					Chapter simChapter = new Chapter("Simulations", chapterNum++);
					if (introSection == null) {
						introSection = simChapter.addSection("General Info", simChapter.numberDepth() + 1);                 
						writeMetadata(introSection, name, mathModel.getDescription(), userName, "MathModel");
					}
					for (int i = 0; i < sims.length; i++) {
						writeSimulation(simChapter, sims[i]);
					}
					document.add(simChapter);
				} else {
					System.err.println("Bad Request: No simulations to publish for Mathmodel: " + name);
				}
			} 
			if (preferences.includeGeom()) { 								  //unlike biomodels, geometry is a chapter, not a chapter section.
				Geometry geom = mathModel.getMathDescription().getGeometry();
				if (geom != null) {
					Chapter geomChapter = new Chapter("Geometry", chapterNum++);
					if (introSection == null) {
						introSection = geomChapter.addSection("General Info", geomChapter.numberDepth() + 1);                 
						writeMetadata(introSection, name, mathModel.getDescription(), userName, "MathModel");
					}
					writeGeom(geomChapter, geom, null);
					document.add(geomChapter);
				} else {
					System.err.println("Bad Request: No geometry to publish for Mathmodel: " + name);
				}
			}
			document.close();
		} catch (DocumentException e) {
			System.err.println("Unable to publish MathModel.");
			e.printStackTrace();
			throw e;
		}
	}


	protected void writeMembraneMapping(Section simContextSection, SimulationContext simContext) throws DocumentException {

		GeometryContext geoContext = simContext.getGeometryContext();
		if (geoContext == null) {
			return;
		}
		Section memMapSection = null;
		Table memMapTable = null;
		StructureMapping structMappings [] = geoContext.getStructureMappings();
		for (int i = 0; i < structMappings.length; i++) {
			MembraneMapping memMapping = null;
			if (structMappings[i] instanceof FeatureMapping) {
				continue;
			} else {
				memMapping = (MembraneMapping)structMappings[i];
			}
			String structName = memMapping.getStructure().getName();
			String initVoltage = "";
			Expression tempExp = memMapping.getInitialVoltageParameter().getExpression();
			VCUnitDefinition tempUnit = memMapping.getInitialVoltageParameter().getUnitDefinition();
			if (tempExp != null) {
				initVoltage = tempExp.infix();
				if (tempUnit != null) {
					initVoltage += "   " + tempUnit.getSymbolUnicode();
				}
			}
			String spCap = "";
			tempExp = memMapping.getSpecificCapacitanceParameter().getExpression();
			tempUnit = memMapping.getSpecificCapacitanceParameter().getUnitDefinition();
			if (tempExp != null) {
				spCap = tempExp.infix();
				if (tempUnit != null) {
					spCap += "   " + tempUnit.getSymbolUnicode();
				}
			}
			if (memMapTable == null) {
				memMapTable = getTable(4, 100, 1, 3, 3);
				memMapTable.addCell(createCell("Electrical Mapping - Membrane Potential", getBold(DEF_HEADER_FONT_SIZE), 4, 1, Element.ALIGN_CENTER, true));
				memMapTable.addCell(createHeaderCell("Membrane", getBold(), 1));
				memMapTable.addCell(createHeaderCell("Calculate V (T/F)", getBold(), 1));
				memMapTable.addCell(createHeaderCell("V initial", getBold(), 1));
				memMapTable.addCell(createHeaderCell("Specific Capacitance", getBold(), 1));
				memMapTable.endHeaders();
			}
			memMapTable.addCell(createCell(structName, getFont()));
			memMapTable.addCell(createCell((memMapping.getCalculateVoltage() ? " T ": " F "), getFont()));
			memMapTable.addCell(createCell(initVoltage, getFont()));
			memMapTable.addCell(createCell(spCap, getFont()));
		}
		if (memMapTable != null) {
			memMapSection = simContextSection.addSection("Membrane Mapping For " + simContext.getName(), simContextSection.numberDepth() + 1);
			memMapSection.add(memMapTable);
		}
		int[] widths = {1, 1, 1, 5, 8};
		Table electTable = null;
		ElectricalStimulus[] electricalStimuli = simContext.getElectricalStimuli();
		for (int j = 0; j < electricalStimuli.length; j++) {
			if (j == 0) {
				electTable = getTable(5, 100, 1, 3, 3);
				electTable.addCell(createCell("Electrical Mapping - Electrical Stimulus", getBold(DEF_HEADER_FONT_SIZE), 5, 1, Element.ALIGN_CENTER, true));
				electTable.addCell(createHeaderCell("Stimulus Name", getBold(), 1));
				electTable.addCell(createHeaderCell("Current Name", getBold(), 1));
				electTable.addCell(createHeaderCell("Clamp Type", getBold(), 1));
				electTable.addCell(createHeaderCell("Voltage/Current Density", getBold(), 1));
				electTable.addCell(createHeaderCell("Clamp Device", getBold(), 1));
				electTable.endHeaders();
			}
			String stimName = electricalStimuli[j].getName();
			String currName = "";
			String clampType = "", expStr = "";
			Expression tempExp = null;
			VCUnitDefinition tempUnit = null;
			if (electricalStimuli[j] instanceof CurrentDensityClampStimulus) {
				CurrentDensityClampStimulus stimulus = (CurrentDensityClampStimulus) electricalStimuli[j];
				LocalParameter currentDensityParameter = stimulus.getCurrentDensityParameter();
				tempExp = currentDensityParameter.getExpression();
				tempUnit = currentDensityParameter.getUnitDefinition();
				clampType = "Current Density (deprecated)";
			} else if (electricalStimuli[j] instanceof TotalCurrentClampStimulus) {
				TotalCurrentClampStimulus stimulus = (TotalCurrentClampStimulus) electricalStimuli[j];
				LocalParameter totalCurrentParameter = stimulus.getCurrentParameter();
				tempExp = totalCurrentParameter.getExpression();
				tempUnit = totalCurrentParameter.getUnitDefinition();
				clampType = "Current";
			} else if (electricalStimuli[j] instanceof VoltageClampStimulus) {
				VoltageClampStimulus stimulus = (VoltageClampStimulus) electricalStimuli[j];
				Parameter voltageParameter = stimulus.getVoltageParameter();
				tempExp = voltageParameter.getExpression();
				tempUnit = voltageParameter.getUnitDefinition();
				clampType = "Voltage";
			}
			if (tempExp != null) {
				expStr = tempExp.infix();
				if (tempUnit != null) {
					expStr += "   " + tempUnit.getSymbolUnicode();
				}
			}
			electTable.addCell(createCell(stimName, getFont()));
			electTable.addCell(createCell(currName, getFont()));
			electTable.addCell(createCell(clampType, getFont()));
			electTable.addCell(createCell(expStr, getFont()));
			//add electrode info
			Electrode electrode = electricalStimuli[j].getElectrode();
			if (electrode == null) {
				electTable.addCell(createCell("N/A", getFont()));
			} else {
				Coordinate c = electrode.getPosition();
				String location = c.getX() + ", " + c.getY() + ", " + c.getZ();
				String featureName = electrode.getFeature().getName();
				electTable.addCell(createCell("(" + location + ") in " + featureName, getFont()));
			}
		}
		if (electTable != null) {
			if (memMapSection == null) {
				memMapSection = simContextSection.addSection("Membrane Mapping For " + simContext.getName(), 1);
			}
			electTable.setWidths(widths);
			memMapSection.add(electTable);
		}
		//add temperature
		Table tempTable = getTable(1, 75, 1, 3, 3);
		tempTable.setAlignment(Table.ALIGN_LEFT);
		tempTable.addCell(createCell("Temperature: " + simContext.getTemperatureKelvin() + " K", getFont()));
		if (memMapSection != null) {
			memMapSection.add(tempTable);
		}
	}


	protected void writeMemRegionEquation(Section container, MembraneRegionEquation eq) throws DocumentException {
		
		Table eqTable = getTable(2, 100, 1, 2, 2);
		eqTable.addCell(createCell(VCML.MembraneRegionEquation + " " + eq.getVariable().getName(), 
						getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
		eqTable.endHeaders();
		String exp = "0.0";
		eqTable.addCell(createCell(VCML.UniformRate, getFont()));
		if (eq.getUniformRateExpression() != null) {
			exp = eq.getUniformRateExpression().infix();
		}
		exp = "0.0";
		eqTable.addCell(createCell(VCML.MembraneRate, getFont()));
		if (eq.getMembraneRateExpression() != null) {
			exp = eq.getMembraneRateExpression().infix();
		}
		eqTable.addCell(createCell(exp, getFont()));
		if (eq.getInitialExpression() != null) {
			eqTable.addCell(createCell(VCML.Initial, getFont()));
			eqTable.addCell(createCell(eq.getInitialExpression().infix(), getFont()));
		}
		int solutionType = eq.getSolutionType();
		switch (solutionType) {
			case Equation.UNKNOWN_SOLUTION:{
				if (eq.getInitialExpression() == null) {
					eqTable.addCell(createCell(VCML.Initial, getFont()));
					eqTable.addCell(createCell("0.0", getFont()));
				}
				break;
			}
			case Equation.EXACT_SOLUTION:{
				eqTable.addCell(createCell(VCML.Exact, getFont()));
				eqTable.addCell(createCell(eq.getExactSolution().infix(), getFont()));
				break;
			}
		}
		
		container.add(eqTable);
	}


protected void writeMetadata(Section metaSection, String name, String description, String userName, String type) throws DocumentException {

	Table metaTable = getTable(1, 100, 0, 3, 3);
	//
	if (name != null && name.trim().length() > 0) {
		metaTable.addCell(createCell(type + " Name: " + name.trim(), getBold(DEF_HEADER_FONT_SIZE)));
	}
	if (description != null && description.trim().length() > 0) {
		metaTable.addCell(createCell(type + " Description: " + description.trim(), getBold(DEF_HEADER_FONT_SIZE)));
	}
	if (userName != null) {
		metaTable.addCell(createCell("Owner: " + userName, getBold(DEF_HEADER_FONT_SIZE)));
	}
	//
	metaSection.add(metaTable);
}


//model description ignored. 
protected void writeModel(Chapter physioChapter, Model model) throws DocumentException {

	Section structSection = null;
	//add structures image
//	if (model.getNumStructures() > 0) {
//		try {
//			ByteArrayOutputStream bos = generateDocStructureImage(model, ITextWriter.LOW_RESOLUTION);
//			structSection = physioChapter.addSection("Structures For: " + model.getName(), physioChapter.numberDepth() + 1);
//			addImage(structSection, bos);
//		} catch(Exception e) {
//			System.err.println("Unable to add structures image for model: " + model.getName());
//			e.printStackTrace();
//		}
//	}
	//write structures
	Table structTable = null;
	for (int i = 0; i < model.getNumStructures(); i++) {
		if (structTable == null) {
			structTable = getTable(4, 100, 1, 3, 3);
			structTable.addCell(createCell("Structures", getBold(DEF_HEADER_FONT_SIZE), 4, 1, Element.ALIGN_CENTER, true));
			structTable.addCell(createHeaderCell("Name", getFont(), 1));
			structTable.addCell(createHeaderCell("Type", getFont(), 1));
			structTable.addCell(createHeaderCell("Inside", getFont(), 1));
			structTable.addCell(createHeaderCell("Outside", getFont(), 1));
			structTable.endHeaders();
		}
		writeStructure(model, model.getStructure(i), structTable);
	}
	
//	if (structTable != null) {
//		structSection.add(structTable);
//	}
	//write reactions
	writeReactions(physioChapter, model);
}

		protected void writeOdeEquation(Section container, OdeEquation eq) throws DocumentException {

		Table eqTable = getTable(2, 100, 1, 2, 2);
		eqTable.addCell(createCell(VCML.OdeEquation + " " + eq.getVariable().getName(), 
						getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
		eqTable.endHeaders();
		String exp = "0.0";
		eqTable.addCell(createCell(VCML.Rate, getFont()));
		if (eq.getRateExpression() != null) {
			exp = eq.getRateExpression().infix();
		}
		eqTable.addCell(createCell(exp, getFont()));
		if (eq.getInitialExpression() != null) {
			eqTable.addCell(createCell(VCML.Initial, getFont()));
			eqTable.addCell(createCell(eq.getInitialExpression().infix(), getFont()));
		}
		int solutionType = eq.getSolutionType();
		switch (solutionType) {
			case Equation.UNKNOWN_SOLUTION:{
				if (eq.getInitialExpression() == null) {
					eqTable.addCell(createCell(VCML.Initial, getFont()));
					eqTable.addCell(createCell("0.0", getFont()));
				}
				break;
			}
			case Equation.EXACT_SOLUTION:{
				eqTable.addCell(createCell(VCML.Exact, getFont()));
				eqTable.addCell(createCell(eq.getExactSolution().infix(), getFont()));
				break;
			}
		}
		
		container.add(eqTable);	
	}


	protected void writePdeEquation(Section container, PdeEquation eq) throws DocumentException {

		Table eqTable = getTable(2, 100, 1, 2, 2);
		eqTable.addCell(createCell(VCML.PdeEquation + " " + eq.getVariable().getName(), 
						getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
		eqTable.endHeaders();
		if (eq.getBoundaryXm() != null) {
			eqTable.addCell(createCell(VCML.BoundaryXm, getFont()));
			eqTable.addCell(createCell(eq.getBoundaryXm().infix(), getFont()));
		}
		if (eq.getBoundaryXp() != null) {
			eqTable.addCell(createCell(VCML.BoundaryXp, getFont()));
			eqTable.addCell(createCell(eq.getBoundaryXp().infix(), getFont()));
		}
		if (eq.getBoundaryYm() != null) {
			eqTable.addCell(createCell(VCML.BoundaryYm, getFont()));
			eqTable.addCell(createCell(eq.getBoundaryYm().infix(), getFont()));
		}
		if (eq.getBoundaryYp() != null) {
			eqTable.addCell(createCell(VCML.BoundaryYp, getFont()));
			eqTable.addCell(createCell(eq.getBoundaryYp().infix(), getFont()));
		}
		if (eq.getBoundaryZm() != null) {
			eqTable.addCell(createCell(VCML.BoundaryZm, getFont()));
			eqTable.addCell(createCell(eq.getBoundaryZm().infix(), getFont()));
		}
		if (eq.getBoundaryZp() != null) {
			eqTable.addCell(createCell(VCML.BoundaryZp, getFont()));
			eqTable.addCell(createCell(eq.getBoundaryZp().infix(), getFont()));
		}
		if (eq.getVelocityX() != null) {
			eqTable.addCell(createCell(VCML.VelocityX, getFont()));
			eqTable.addCell(createCell(eq.getVelocityX().infix(), getFont()));
		}
		if (eq.getVelocityY() != null) {
			eqTable.addCell(createCell(VCML.VelocityY, getFont()));
			eqTable.addCell(createCell(eq.getVelocityY().infix(), getFont()));
		}
		if (eq.getVelocityZ() != null) {
			eqTable.addCell(createCell(VCML.VelocityZ, getFont()));
			eqTable.addCell(createCell(eq.getVelocityZ().infix(), getFont()));
		}
		String exp = "0.0";
		if (eq.getRateExpression() != null) {
			exp = eq.getRateExpression().infix();
		}
		eqTable.addCell(createCell(VCML.Rate, getFont()));
		eqTable.addCell(createCell(exp, getFont()));
		exp = "0.0";
		if (eq.getDiffusionExpression() != null) {
			exp = eq.getDiffusionExpression().infix();
		}
		eqTable.addCell(createCell(VCML.Diffusion, getFont()));
		eqTable.addCell(createCell(exp, getFont()));
		if (eq.getInitialExpression() != null) {
			eqTable.addCell(createCell(VCML.Initial, getFont()));
			eqTable.addCell(createCell(eq.getInitialExpression().infix(), getFont()));
		}
		int solutionType = eq.getSolutionType();
		switch (solutionType) {
			case Equation.UNKNOWN_SOLUTION:{
				if (eq.getInitialExpression() == null) {
					eqTable.addCell(createCell(VCML.Initial, getFont()));
					eqTable.addCell(createCell("0.0", getFont()));
				}
				break;
			}
			case Equation.EXACT_SOLUTION:{
				eqTable.addCell(createCell(VCML.Exact, getFont()));
				eqTable.addCell(createCell(eq.getExactSolution().infix(), getFont()));
				break;
			}
		}
		
		container.add(eqTable);	
	}


//ReactionContext - SpeciesContextSpec: ignored boundary conditions.
	protected void writeReactionContext(Section simContextSection, SimulationContext simContext) throws DocumentException {

		ReactionContext rc = simContext.getReactionContext();
		if (rc == null) {
			return;
		}
		Section rcSection = null;
		//add reaction specs	
		ReactionSpec reactionSpecs [] = rc.getReactionSpecs();
		Table reactionSpecTable = null;
		for (int i = 0; i < reactionSpecs.length; i++) {
			if (i == 0) {
				reactionSpecTable = getTable(4, 100, 1, 3, 3);
				//reactionSpecTable.setTableFitsPage(true);
				reactionSpecTable.addCell(createCell("Reaction Mapping", getBold(DEF_HEADER_FONT_SIZE), 4, 1, Element.ALIGN_CENTER, true));
				reactionSpecTable.addCell(createHeaderCell("Name", getBold(), 1));
				reactionSpecTable.addCell(createHeaderCell("Type", getBold(), 1));
				reactionSpecTable.addCell(createHeaderCell("Enabled (T/F)", getBold(), 1));
				reactionSpecTable.addCell(createHeaderCell("Fast (T/F)", getBold(), 1));
				reactionSpecTable.endHeaders();
			}
			String reactionName = reactionSpecs[i].getReactionStep().getName();
			String reactionType = reactionSpecs[i].getReactionStep().getDisplayType();
			reactionSpecTable.addCell(createCell(reactionName, getFont()));
			reactionSpecTable.addCell(createCell(reactionType, getFont()));
			reactionSpecTable.addCell(createCell((reactionSpecs[i].isExcluded() ? " F ": " T "), getFont()));
			reactionSpecTable.addCell(createCell((reactionSpecs[i].isFast() ? " T ": " F "), getFont()));
		}
		if (reactionSpecTable != null) {
			rcSection = simContextSection.addSection("Reaction Mapping For " + simContext.getName(), simContextSection.numberDepth() + 1);
			rcSection.add(reactionSpecTable);
		}
		
		//add species context specs
		SpeciesContextSpec speciesContSpecs [] = rc.getSpeciesContextSpecs();
		Table speciesSpecTable = null;
		int widths [] = {2, 2, 4, 4, 1};
		for (int i = 0; i < speciesContSpecs.length; i++) {
			if (i == 0) {
				speciesSpecTable = getTable(5, 100, 1, 3, 3);
				speciesSpecTable.addCell(createCell("Initial Conditions", getBold(DEF_HEADER_FONT_SIZE), 5, 1, Element.ALIGN_CENTER, true));
				speciesSpecTable.addCell(createHeaderCell("Species", getBold(), 1));
				speciesSpecTable.addCell(createHeaderCell("Structure", getBold(), 1));
				speciesSpecTable.addCell(createHeaderCell("Initial Conc.", getBold(), 1));
				speciesSpecTable.addCell(createHeaderCell("Diffusion Const.", getBold(), 1));
				speciesSpecTable.addCell(createHeaderCell("Fixed (T/F)", getBold(), 1));
				speciesSpecTable.endHeaders();
			}
			String speciesName = speciesContSpecs[i].getSpeciesContext().getSpecies().getCommonName();
			String structName = speciesContSpecs[i].getSpeciesContext().getStructure().getName();
			String diff = speciesContSpecs[i].getDiffusionParameter().getExpression().infix();
			VCUnitDefinition diffUnit = speciesContSpecs[i].getDiffusionParameter().getUnitDefinition();
			SpeciesContextSpecParameter initParam = speciesContSpecs[i].getInitialConditionParameter();
			String initConc = initParam == null? "" :initParam.getExpression().infix();
			VCUnitDefinition initConcUnit = initParam == null? null : initParam.getUnitDefinition();
			speciesSpecTable.addCell(createCell(speciesName, getFont()));
			speciesSpecTable.addCell(createCell(structName, getFont()));
			speciesSpecTable.addCell(createCell(initConc + (initConcUnit == null ? "": "   " + initConcUnit.getSymbolUnicode()), getFont()));
			speciesSpecTable.addCell(createCell(diff + (diffUnit == null ? "": "   " + diffUnit.getSymbolUnicode()), getFont()));
			speciesSpecTable.addCell(createCell((speciesContSpecs[i].isConstant() ? " T ": " F "), getFont()));	
		}
		if (speciesSpecTable != null) {
			if (rcSection == null) {
				rcSection = simContextSection.addSection("Reaction Mapping For " + simContext.getName(), simContextSection.numberDepth() + 1);
			}
			speciesSpecTable.setWidths(widths);
			rcSection.add(speciesSpecTable);
		}
	}


	private Cell getReactionArrowImageCell(boolean bReversible) throws DocumentException {
		// Create image for arrow(s)
		int imageWidth = 150;
		int imageHeight = 50;
		BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D g = (Graphics2D)bufferedImage.getGraphics(); 
		g.setClip(0, 0, imageWidth, imageHeight);
		g.setColor(Color.white);
	  	g.fillRect(0, 0, imageWidth, imageHeight);
		g.setColor(Color.black);
		int fontSize = 12;
		g.setFont(new java.awt.Font("SansSerif", Font.BOLD, fontSize));
		
		// get image for reaction equation arrows
		// Draw the arrows on canvas/image
		if (bReversible){
			// Forward  *AND* Reverse (bi-directional) arrow
			java.awt.Polygon arrow = new java.awt.Polygon(  new int[] {20, 40, 40, 110, 110, 130, 110, 110, 40, 40},
									 						new int[] {25, 14, 22, 22, 14, 25, 36, 28, 28, 36}, 10);
			g.fill(arrow);
		} else {
			// Only Forward Arrow
			java.awt.Polygon arrow = new java.awt.Polygon(  new int[] {20, 110, 110, 130, 110, 110, 20},
															new int[] {22, 22, 14, 25, 36, 28, 28}, 7);
			g.fill(arrow);
		}	 
		
		Cell imageCell = null;
		try {
			com.lowagie.text.Image rpImage = com.lowagie.text.Image.getInstance(bufferedImage, null);
			rpImage.setAlignment(com.lowagie.text.Image.MIDDLE);
			imageCell = new Cell();
			imageCell.add(rpImage);
		} catch (Exception e) {
			System.err.println("Unable to add structure mapping image to report.");
			e.printStackTrace();
		}

		return imageCell;
	}

	
//each reaction has its own table, ordered by the structures.
	protected void writeReactions(Chapter physioChapter, Model model) throws DocumentException {

		if (model == null) {
			return;
		}
		Paragraph reactionParagraph = new Paragraph();
		reactionParagraph.add(new Chunk("Structures and Reactions Diagram").setLocalDestination(model.getName()));
		Section reactionDiagramSection = physioChapter.addSection(reactionParagraph, physioChapter.numberDepth() + 1);
		try{
			addImage(reactionDiagramSection, encodeJPEG(generateDocReactionsImage(model,null)));
		}catch(Exception e){
			e.printStackTrace();
			throw new DocumentException(e.getClass().getName()+": "+e.getMessage());
		}

		
		for (int i = 0; i < model.getNumStructures(); i++) {
			ReactionStep[] reactionSteps = model.getReactionSteps();
			ReactionStep rs = null;
			Table modifierTable = null;
			Table reactionTable = null;
			boolean firstTime = true;
			Section reactStructSection = null;
			for (int j = 0; j < reactionSteps.length; j++) {
				if (reactionSteps[j].getStructure() == model.getStructure(i)) {         //can also use structureName1.equals(structureName2)
					if (firstTime) {
						Paragraph linkParagraph = new Paragraph();
						linkParagraph.add(new Chunk("Reaction(s) in " + model.getStructure(i).getName()).setLocalDestination(model.getStructure(i).getName()));
						reactStructSection = physioChapter.addSection(linkParagraph, physioChapter.numberDepth() + 1);
						firstTime = false;
					}
					rs = reactionSteps[j];
					String type;
					if (rs instanceof SimpleReaction) {
						type = "Reaction";
					} else {
						type = "Flux";
					}
					//write Reaction equation as a table

					// Get the image arrow cell depending on type of reactionStep : MassAction => double arrow, otherwise, forward arrow
					boolean bReversible = false;
					if (rs.getKinetics() instanceof MassActionKinetics) {
						bReversible = true;
					}
					Cell arrowImageCell = getReactionArrowImageCell(bReversible);
					
					// Get reactants and products strings
					ReactionCanvas rc = new ReactionCanvas();
					rc.setReactionStep(rs);
					ReactionCanvasDisplaySpec rcdSpec = rc.getReactionCanvasDisplaySpec();
					String reactants = rcdSpec.getLeftText();
					String products = rcdSpec.getRightText();
					
					// Create table and add cells for reactants, arrow(s) images, products
					int widths [] = {8, 1, 8};
					reactionTable = getTable(3, 100, 0, 2, 2);
					
					// Add reactants as cell
					Cell tableCell = createCell(reactants, getBold());
					tableCell.setHorizontalAlignment(Cell.ALIGN_RIGHT);
					tableCell.setBorderColor(Color.white);
					reactionTable.addCell(tableCell);
					// add arrow(s) image as cell
					if (arrowImageCell != null) {
						arrowImageCell.setHorizontalAlignment(Cell.ALIGN_CENTER);
						arrowImageCell.setBorderColor(Color.white);
						reactionTable.addCell(arrowImageCell);
					}
					// add products as cell
					tableCell = createCell(products, getBold());
					tableCell.setBorderColor(Color.white);
					reactionTable.addCell(tableCell);
					
					// reactionTable.setBorderColor(Color.white);
					reactionTable.setWidths(widths);
					
					// Identify modifiers, 
					ReactionParticipant[] rpArr = rs.getReactionParticipants();
					Vector<ReactionParticipant> modifiersVector = new Vector<ReactionParticipant>();  
					for(int k = 0; k < rpArr.length; k += 1){
						if (rpArr[k] instanceof Catalyst) {
							modifiersVector.add(rpArr[k]);
						}
					}

					// Write the modifiers in a separate table, if present
					if (modifiersVector.size() > 0) {
						modifierTable = getTable(1, 50, 0, 1, 1);
						modifierTable.addCell(createCell("Modifiers List", getBold(DEF_HEADER_FONT_SIZE), 1, 1, Element.ALIGN_CENTER, true));
						StringBuffer modifierNames = new StringBuffer();
						for (int k = 0; k < modifiersVector.size(); k++) {
							modifierNames.append(((Catalyst)modifiersVector.elementAt(k)).getName() + "\n");
						}
						modifierTable.addCell(createCell(modifierNames.toString().trim(), getFont()));
						modifiersVector.removeAllElements();
					}
					
					Section reactionSection = reactStructSection.addSection(type + " " + rs.getName(), reactStructSection.numberDepth() + 1);
					//Annotation
					VCMetaData vcMetaData = rs.getModel().getVcMetaData();
					if (vcMetaData.getFreeTextAnnotation(rs) != null) {
						Table annotTable = getTable(1, 100, 1, 3, 3);
						annotTable.addCell(createCell("Reaction Annotation", getBold(DEF_HEADER_FONT_SIZE), 1, 1, Element.ALIGN_CENTER, true));
						annotTable.addCell(createCell(vcMetaData.getFreeTextAnnotation(rs),getFont()));
						reactionSection.add(annotTable);
						//reactionSection.add(new Paragraph("\""+rs.getAnnotation()+"\""));
					}
					// reaction table
					if (reactionTable != null) {
						reactionSection.add(reactionTable);
						reactionTable = null;	// re-set reactionTable
					}
					if (modifierTable != null) {
						reactionSection.add(modifierTable);
						modifierTable = null;
					}
					// Write kinetics parameters, etc. in a table
					writeKineticsParams(reactionSection, rs);
				}
			}
		}
	}


//container can be a chapter or a section of a chapter. 
	protected void writeSimulation(Section container, Simulation sim) throws DocumentException {

		if (sim == null) {
			return;
		}
		Section simSection = container.addSection(sim.getName(), container.numberDepth() + 1);
		writeMetadata(simSection, sim.getName(), sim.getDescription(), null, "Simulation ");
		//add overriden params
		Table overParamTable = null;
		MathOverrides mo = sim.getMathOverrides();
		if (mo != null) {
			String constants [] = mo.getOverridenConstantNames();
			for (int i = 0; i < constants.length; i++) {
				String actualStr = "", defStr = "";
				Expression tempExp = mo.getDefaultExpression(constants[i]);
				if (tempExp != null) {
					defStr = tempExp.infix();
				}
				if (mo.isScan(constants[i])) {
					actualStr = mo.getConstantArraySpec(constants[i]).toString();
				} else {
					tempExp = mo.getActualExpression(constants[i], 0);
					if (tempExp != null) {
						actualStr = tempExp.infix();
					}
				}
				if (overParamTable == null) {
					overParamTable = getTable(3, 75, 1, 3, 3);
					overParamTable.setAlignment(Table.ALIGN_LEFT);
					overParamTable.addCell(createCell("Overriden Parameters", getBold(DEF_HEADER_FONT_SIZE), 3, 1, Element.ALIGN_CENTER, true));
					overParamTable.addCell(createHeaderCell("Name", getBold(), 1));
					overParamTable.addCell(createHeaderCell("Actual Value", getBold(), 1));
					overParamTable.addCell(createHeaderCell("Default Value", getBold(), 1));
				}
				overParamTable.addCell(createCell(constants[i], getFont()));
				overParamTable.addCell(createCell(actualStr, getFont()));
				overParamTable.addCell(createCell(defStr, getFont()));
			}
		}
		if (overParamTable != null) {
			simSection.add(overParamTable);
		}
		//add spatial details
		//sim.isSpatial();
		Table meshTable = null;
		MeshSpecification mesh = sim.getMeshSpecification();
		if (mesh != null) {		
			Geometry geom = mesh.getGeometry();
			Extent extent = geom.getExtent();
			String extentStr = "(" + extent.getX() + ", " + extent.getY() + ", " + extent.getZ() + ")";
			ISize meshSize = mesh.getSamplingSize();
			String meshSizeStr = "(" + meshSize.getX() + ", " + meshSize.getY() + ", " + meshSize.getZ() + ")";
			meshTable = getTable(2, 75, 1, 3, 3);
			meshTable.setAlignment(Table.ALIGN_LEFT);
			meshTable.addCell(createCell("Geometry Setting", getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
			meshTable.addCell(createCell("Geometry Size (um)", getFont()));
			meshTable.addCell(createCell(extentStr, getFont()));
			meshTable.addCell(createCell("Mesh Size (elements)", getFont()));
			meshTable.addCell(createCell(meshSizeStr, getFont()));
		}
		if (meshTable != null) {
			simSection.add(meshTable);
		}
		//write advanced sim settings
		Table simAdvTable = null;
		SolverTaskDescription solverDesc = sim.getSolverTaskDescription();
		if (solverDesc != null) {
			String solverName = solverDesc.getSolverDescription().getDisplayLabel();
			simAdvTable = getTable(2, 75, 1, 3, 3);
			simAdvTable.setAlignment(Table.ALIGN_LEFT);
			simAdvTable.addCell(createCell("Advanced Settings", getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
			simAdvTable.addCell(createCell("Solver Name", getFont()));
			simAdvTable.addCell(createCell(solverName, getFont()));
			simAdvTable.addCell(createCell("Time Bounds - Starting", getFont()));
			simAdvTable.addCell(createCell("" + solverDesc.getTimeBounds().getStartingTime(), getFont()));
			simAdvTable.addCell(createCell("Time Bounds - Ending", getFont()));
			simAdvTable.addCell(createCell("" + solverDesc.getTimeBounds().getEndingTime(), getFont()));
			simAdvTable.addCell(createCell("Time Step - Min", getFont()));
			simAdvTable.addCell(createCell("" + solverDesc.getTimeStep().getMinimumTimeStep(), getFont()));
			simAdvTable.addCell(createCell("Time Step - Default", getFont()));
			simAdvTable.addCell(createCell("" + solverDesc.getTimeStep().getDefaultTimeStep(), getFont()));
			simAdvTable.addCell(createCell("Time Step - Max", getFont()));
			simAdvTable.addCell(createCell("" + solverDesc.getTimeStep().getMaximumTimeStep(), getFont()));
			ErrorTolerance et = solverDesc.getErrorTolerance();
			if (et != null) {
				simAdvTable.addCell(createCell("Error Tolerance - Absolute", getFont()));
				simAdvTable.addCell(createCell("" + et.getAbsoluteErrorTolerance(), getFont()));
				simAdvTable.addCell(createCell("Error Tolerance - Relative", getFont()));
				simAdvTable.addCell(createCell("" + et.getRelativeErrorTolerance(), getFont()));
			}
			OutputTimeSpec ots = solverDesc.getOutputTimeSpec();
			if (ots.isDefault()) {
				simAdvTable.addCell(createCell("Keep Every", getFont()));
				simAdvTable.addCell(createCell("" + ((DefaultOutputTimeSpec)ots).getKeepEvery(), getFont()));
				simAdvTable.addCell(createCell("Keep At Most", getFont()));
				simAdvTable.addCell(createCell("" + ((DefaultOutputTimeSpec)ots).getKeepAtMost(), getFont()));
			} else if (ots.isUniform()) {
				simAdvTable.addCell(createCell("Output Time Step", getFont()));
				simAdvTable.addCell(createCell("" + ((UniformOutputTimeSpec)ots).getOutputTimeStep(), getFont()));
			} else if (ots.isExplicit()) {
				simAdvTable.addCell(createCell("Output Time Points", getFont()));
				simAdvTable.addCell(createCell("" + ((ExplicitOutputTimeSpec)ots).toCommaSeperatedOneLineOfString(), getFont()));
			}
			simAdvTable.addCell(createCell("Use Symbolic Jacobian (T/F)", getFont()));	
			simAdvTable.addCell(createCell((solverDesc.getUseSymbolicJacobian() ? " T ": " F "), getFont()));
			Constant sp = solverDesc.getSensitivityParameter();
			if (sp != null) {
				simAdvTable.addCell(createCell("Sensitivity Analysis Param", getFont()));
				simAdvTable.addCell(createCell(sp.getName(), getFont()));
			}
		}
		if (simAdvTable != null) {
			simSection.add(simAdvTable);
		}
	}


//SimulationContext: ignored the constraints (steady/unsteady).
//Electrical Stimulus: ignored the Ground Electrode,   
protected void writeSimulationContext(Chapter simContextsChapter, SimulationContext simContext, PublishPreferences preferences) throws Exception {

	Section simContextSection = simContextsChapter.addSection("Application: " + simContext.getName(), simContextsChapter.numberDepth() + 1);
	writeMetadata(simContextSection, simContext.getName(), simContext.getDescription(), null, "Application ");
	//add geometry context (structure mapping)
	writeStructureMapping(simContextSection, simContext);
	//add reaction context (Reaction Mapping)
	writeReactionContext(simContextSection, simContext); 
	//add Membrane Mapping & electrical stimuli
	writeMembraneMapping(simContextSection, simContext);
	//
	if (preferences.includeGeom()) {
		writeGeom(simContextSection, simContext.getGeometry(), simContext.getGeometryContext());
	}
	if (preferences.includeMathDesc()) {
		writeMathDescAsText(simContextSection, simContext.getMathDescription());
		//writeMathDescAsImages(simContextSection, simContext.getMathDescription());
	}
	if (preferences.includeSim()) {
		Section simSection = simContextSection.addSection("Simulation(s)", simContextSection.depth() + 1);
		Simulation sims [] = simContext.getSimulations();
		for (int i = 0; i < sims.length; i++) {
			writeSimulation(simSection, sims[i]);	
		}
	}
}


//not used for now...
protected void writeSpecies(Species[] species) throws DocumentException {

	if (species.length > 0) {
		Table table = new Table(2);
		table.setWidth(100);
		table.setBorderWidth(0);                 //for now...
		//
		int[] widths = new int[] { 1, 1 };
		//
		table.addCell(createHeaderCell("Species", getBold(), 2));
		for(int i = 0; i < species.length/2 + (species.length % 2); i++) {
			int n = species.length/2 + (species.length % 2) + i;
			table.addCell(createCell(species[i].getCommonName(), getFont()));
			widths[0] = Math.max(widths[0], species[i].getCommonName().length());
			if (n < species.length) {
				table.addCell(createCell(species[n].getCommonName(), getFont()));
				widths[1] = Math.max(widths[1], species[n].getCommonName().length());
			} else {
				table.addCell(createCell("", getFont()));
			}
		}
		table.setWidths(widths);
		document.add(table);
	}
}


	protected void writeStructure(Model model, Structure struct, Table structTable) throws DocumentException {

		//If this structure has any reactions in it, add its name as a hyperlink to the reactions' list.
		if (hasReactions(model, struct)) {
			Paragraph linkParagraph = new Paragraph();
			Font linkFont;
			try {
				BaseFont fontBaseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				linkFont = new Font(fontBaseFont, DEF_FONT_SIZE, Font.NORMAL, new java.awt.Color(0, 0, 255));
			} catch (Exception e) {
				linkFont = getFont();
				e.printStackTrace();
			}
			linkParagraph.add(new Chunk(struct.getName(), linkFont).setLocalGoto(struct.getName()));  
			Cell structLinkCell = new Cell(linkParagraph);
			structLinkCell.setBorderWidth(1);
			structLinkCell.setHorizontalAlignment(Element.ALIGN_LEFT);	
			structTable.addCell(structLinkCell);
		} else {
			structTable.addCell(createCell(struct.getName(), getFont()));
		}
		StructureTopology structTopology = model.getStructureTopology();
		if (struct instanceof Membrane) {
			structTable.addCell(createCell("Membrane", getFont()));
			Feature outsideFeature = structTopology.getOutsideFeature((Membrane)struct);
			Feature insideFeature = structTopology.getInsideFeature((Membrane)struct);
			structTable.addCell(createCell((insideFeature != null ? insideFeature.getName() : "N/A"), getFont()));
			structTable.addCell(createCell((outsideFeature != null ? outsideFeature.getName() : "N/A"), getFont()));
		} else {
			structTable.addCell(createCell("Feature", getFont()));
			String outsideStr = "N/A", insideStr = "N/A";
			Membrane enclosingMem = (Membrane)structTopology.getParentStructure(struct);
			if (enclosingMem != null) {
				outsideStr = enclosingMem.getName();
			}
			//To do:  retrieve the 'child' membrane here...
			structTable.addCell(createCell(insideStr, getFont()));	
			structTable.addCell(createCell(outsideStr, getFont()));	
		}
	}


//boundary types ignored.
	protected void writeStructureMapping(Section simContextSection, SimulationContext sc) throws DocumentException {

		GeometryContext gc = sc.getGeometryContext();
		if (gc == null) {
			return;
		}
		Section structMappSection = null;
		/*try {
			ByteArrayOutputStream bos = generateStructureMappingImage(sc);
			com.lowagie.text.Image structMapImage = com.lowagie.text.Image.getInstance(Toolkit.getDefaultToolkit().createImage(bos.toByteArray()), null);
			structMappSection = simContextSection.addSection("Structure Mapping For " + sc.getName(), simContextSection.numberDepth() + 1);
			structMappSection.add(structMapImage);
		} catch (Exception e) {
			System.err.println("Unable to add structure mapping image to report.");
			e.printStackTrace();
		}*/
		StructureMapping structMap [] = gc.getStructureMappings();
		Table structMapTable = null;
		ModelUnitSystem modelUnitSystem = sc.getModel().getUnitSystem();
		for (int i = 0; i < structMap.length; i++) {
			if (!(structMap[i] instanceof FeatureMapping)) {
				continue;
			}
			if (i == 0) {
				structMapTable = getTable(5, 100, 1, 3, 3);
				structMapTable.addCell(createCell("Structure Mapping", getBold(DEF_HEADER_FONT_SIZE), 5, 1, Element.ALIGN_CENTER, true));
				structMapTable.addCell(createHeaderCell("Structure", getBold(), 1));
				structMapTable.addCell(createHeaderCell("Subdomain", getBold(), 1));
				structMapTable.addCell(createHeaderCell("Resolved (T/F)", getBold(), 1));
				structMapTable.addCell(createHeaderCell("Surf/Vol", getBold(), 1));
				structMapTable.addCell(createHeaderCell("VolFract", getBold(), 1));
				structMapTable.endHeaders();
			}
			String structName = structMap[i].getStructure().getName();
			SubVolume subVol = (SubVolume)((FeatureMapping)structMap[i]).getGeometryClass();
			String subDomain = "";
			if (subVol != null) {
				subDomain = subVol.getName();
			}
			boolean isResolved = false; // ((FeatureMapping)structMap[i]).getResolved();
			String surfVolStr = "", volFractStr = "";
			MembraneMapping mm = (MembraneMapping)gc.getStructureMapping(sc.getModel().getStructureTopology().getParentStructure(structMap[i].getStructure()));
			if (mm != null) {
				StructureMapping.StructureMappingParameter smp = mm.getSurfaceToVolumeParameter();
				if (smp != null) {
					Expression tempExp = smp.getExpression();
					VCUnitDefinition tempUnit = smp.getUnitDefinition();
					if (tempExp != null) {
						surfVolStr = tempExp.infix();
						if (tempUnit != null && !modelUnitSystem.getInstance_DIMENSIONLESS().compareEqual(tempUnit)) {  //no need to add '1' for dimensionless unit
							surfVolStr += " " + tempUnit.getSymbolUnicode();
						}
					}
				}
				smp = mm.getVolumeFractionParameter();
				if (smp != null) {
					Expression tempExp = smp.getExpression();
					VCUnitDefinition tempUnit = smp.getUnitDefinition();
					if (tempExp != null) {
						volFractStr = tempExp.infix();
						if (tempUnit != null && !modelUnitSystem.getInstance_DIMENSIONLESS().compareEqual(tempUnit)) { 
							volFractStr += " " + tempUnit.getSymbolUnicode();
						}
					}
				}
			}
			structMapTable.addCell(createCell(structName, getFont()));
			structMapTable.addCell(createCell(subDomain, getFont()));
			structMapTable.addCell(createCell((isResolved ? " T ": " F "), getFont()));
			structMapTable.addCell(createCell(surfVolStr, getFont()));
			structMapTable.addCell(createCell(volFractStr, getFont()));
		}
		if (structMapTable != null) {
			if (structMappSection == null) {
				structMappSection = simContextSection.addSection("Structure Mapping For " + sc.getName(), simContextSection.numberDepth() + 1);
			}
			structMappSection.add(structMapTable);
		}
	}


//currently not used. 
	protected void writeSubDomainsEquationsAsImages(Section mathDescSection, MathDescription mathDesc) {

		Enumeration<SubDomain> subDomains = mathDesc.getSubDomains();
		Expression expArray[];
		Section volDomains = mathDescSection.addSection("Volume Domains", mathDescSection.depth() + 1);
		Section memDomains = mathDescSection.addSection("Membrane Domains", mathDescSection.depth() + 1);
		int scale = 1, height = 200;                            //arbitrary
		int viewableWidth = (int)(document.getPageSize().width() - document.leftMargin() - document.rightMargin());
		BufferedImage dummy = new BufferedImage(viewableWidth, height, BufferedImage.TYPE_3BYTE_BGR);
		while(subDomains.hasMoreElements()) {
			SubDomain subDomain = subDomains.nextElement();
			Enumeration<Equation> equationsList = subDomain.getEquations();
			ArrayList<Expression> expList = new ArrayList<Expression>();
			while (equationsList.hasMoreElements()) {
				Equation equ = equationsList.nextElement();
				try {
					Enumeration<Expression> enum_equ = equ.getTotalExpressions();
					while (enum_equ.hasMoreElements()){
						Expression exp = new Expression(enum_equ.nextElement());	
						expList.add(exp.flatten());
					}
				} catch (ExpressionException ee) {
					System.err.println("Unable to process the equation for subdomain: " + subDomain.getName());
					ee.printStackTrace();
					continue;
				}
			}
			expArray = (Expression [])expList.toArray(new Expression[expList.size()]);
			Section tempSection = null;
			if (subDomain instanceof CompartmentSubDomain) {
				tempSection = volDomains.addSection(subDomain.getName(), volDomains.depth() + 1);
			} else if (subDomain instanceof MembraneSubDomain) {
				tempSection = memDomains.addSection(subDomain.getName(), memDomains.depth() + 1);
			}
			try { 
				Dimension dim = ExpressionCanvas.getExpressionImageSize(expArray, (Graphics2D)dummy.getGraphics());
				System.out.println("Image dim: " + dim.width + " " + dim.height);
				BufferedImage bufferedImage = new BufferedImage((int)dim.getWidth()*scale, (int)dim.getHeight()*scale, BufferedImage.TYPE_3BYTE_BGR);
				ExpressionCanvas.getExpressionAsImage(expArray, bufferedImage, scale);
				//Table imageTable = null;;            
				com.lowagie.text.Image expImage = com.lowagie.text.Image.getInstance(bufferedImage, null);
				expImage.setAlignment(com.lowagie.text.Image.LEFT);
				if (viewableWidth < expImage.scaledWidth()) {
					expImage.scaleToFit(viewableWidth, expImage.height());
					System.out.println("SubDomain expresions After scaling: " + expImage.scaledWidth());
				}
				/*Cell imageCell = new Cell();
				imageCell.add(expImage);
				if (imageTable == null) {
					imageTable = getTable(1, 100, 1, 1, 0);
				}
				imageTable.setTableFitsPage(false);
 				imageTable.setCellsFitPage(false);
				imageTable.addCell(imageCell);
				imageTable.setWidth(100);
				tempSection.add(imageTable);*/
				tempSection.add(expImage);
			} catch (Exception e) {
				System.err.println("Unable to add subdomain equation image to report.");
				e.printStackTrace();
			}
		}
		if (volDomains.isEmpty()) {
			mathDescSection.remove(volDomains);
		}
		if (memDomains.isEmpty()) {
			mathDescSection.remove(memDomains);
		}
	}


	protected void writeSubDomainsEquationsAsText(Section mathDescSection, MathDescription mathDesc) throws DocumentException {

		Enumeration<SubDomain> subDomains = mathDesc.getSubDomains();
		Section volDomains = mathDescSection.addSection("Volume Domains", mathDescSection.depth() + 1);
		Section memDomains = mathDescSection.addSection("Membrane Domains", mathDescSection.depth() + 1);
		Section filDomains = mathDescSection.addSection("Filament Domains", mathDescSection.depth() + 1);
		while(subDomains.hasMoreElements()) {
			Section tempSection = null;
			SubDomain subDomain = subDomains.nextElement();
			if (subDomain instanceof CompartmentSubDomain) {
				tempSection = volDomains.addSection(subDomain.getName(), volDomains.depth() + 1);
			} else if (subDomain instanceof MembraneSubDomain) {
				tempSection = memDomains.addSection(subDomain.getName(), memDomains.depth() + 1);
			} else if (subDomain instanceof FilamentSubDomain) {
				tempSection = filDomains.addSection(subDomain.getName(), filDomains.depth() + 1);
			}
			Enumeration<Equation> equationsList = subDomain.getEquations();
			while (equationsList.hasMoreElements()) {
				Equation equ = equationsList.nextElement();
				writeEquation(tempSection, equ);
			}
			if (subDomain.getFastSystem() != null) {
				writeFastSystem(tempSection, subDomain.getFastSystem());
			}
			if (subDomain instanceof MembraneSubDomain) {
				Enumeration<JumpCondition> jcList = ((MembraneSubDomain)subDomain).getJumpConditions();
				while (jcList.hasMoreElements()) {
					JumpCondition jc = jcList.nextElement();
					writeJumpCondition(tempSection, jc);
				}
			}
		}
		if (volDomains.isEmpty()) {
			mathDescSection.remove(volDomains);
		}
		if (memDomains.isEmpty()) {
			mathDescSection.remove(memDomains);
		}
		if (filDomains.isEmpty()) {
			mathDescSection.remove(filDomains);
		}
	}


	protected void writeVolumeRegionEquation(Section container, VolumeRegionEquation eq) throws DocumentException {

		Table eqTable = getTable(2, 100, 1, 2, 2);
		eqTable.addCell(createCell(VCML.VolumeRegionEquation + " " + eq.getVariable().getName(), 
						getBold(DEF_HEADER_FONT_SIZE), 2, 1, Element.ALIGN_CENTER, true));
		eqTable.endHeaders();
		String exp = "0.0";
		eqTable.addCell(createCell(VCML.UniformRate, getFont()));
		if (eq.getUniformRateExpression() != null) {
			exp = eq.getUniformRateExpression().infix();
		}
		eqTable.addCell(createCell(exp, getFont()));
		exp = "0.0";
		eqTable.addCell(createCell(VCML.VolumeRate, getFont()));
		if (eq.getVolumeRateExpression() != null) {
			exp = eq.getVolumeRateExpression().infix();
		}
		eqTable.addCell(createCell(exp, getFont()));
		
//		exp = "0.0";
//		eqTable.addCell(createCell(VCML.MembraneRate, getFont()));
//		if (eq.getMembraneRateExpression() != null) {
//			exp = eq.getMembraneRateExpression().infix();
//		}
//		eqTable.addCell(createCell(exp, getFont()));
		
		if (eq.getInitialExpression() != null) {
			eqTable.addCell(createCell(VCML.Initial, getFont()));
			eqTable.addCell(createCell(eq.getInitialExpression().infix(), getFont()));
		}
		int solutionType = eq.getSolutionType();
		switch (solutionType) {
			case Equation.UNKNOWN_SOLUTION:{
				if (eq.getInitialExpression() == null) {
					eqTable.addCell(createCell(VCML.Initial, getFont()));
					eqTable.addCell(createCell("0.0", getFont()));
				}
				break;
			}
			case Equation.EXACT_SOLUTION:{
				eqTable.addCell(createCell(VCML.Exact, getFont()));
				eqTable.addCell(createCell(eq.getExactSolution().infix(), getFont()));
				break;
			}
		}
		
		container.add(eqTable);	
	}
}
