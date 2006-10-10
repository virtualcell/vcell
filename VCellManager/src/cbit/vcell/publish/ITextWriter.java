package cbit.vcell.publish;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.print.PageFormat;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import cbit.image.VCImage;
import cbit.util.Coordinate;
import cbit.util.Extent;
import cbit.util.ISize;
import cbit.util.Origin;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.dictionary.ReactionCanvasDisplaySpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
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
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;
import cbit.vcell.model.render.ReactionCartoon;
import cbit.vcell.model.render.ReactionRenderer;
import cbit.vcell.model.render.StructureCartoon;
import cbit.vcell.model.render.StructureMappingCartoon;
import cbit.vcell.modelapp.CurrentClampStimulus;
import cbit.vcell.modelapp.ElectricalStimulus;
import cbit.vcell.modelapp.Electrode;
import cbit.vcell.modelapp.FeatureMapping;
import cbit.vcell.modelapp.GeometryContext;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.ReactionContext;
import cbit.vcell.modelapp.ReactionSpec;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SpeciesContextSpec;
import cbit.vcell.modelapp.StructureMapping;
import cbit.vcell.modelapp.VoltageClampStimulus;
import cbit.vcell.parser.gui.ExpressionPrintFormatter;
import cbit.vcell.simulation.DefaultOutputTimeSpec;
import cbit.vcell.simulation.ErrorTolerance;
import cbit.vcell.simulation.ExplicitOutputTimeSpec;
import cbit.vcell.simulation.MathOverrides;
import cbit.vcell.simulation.MeshSpecification;
import cbit.vcell.simulation.OutputTimeSpec;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.SolverTaskDescription;
import cbit.vcell.simulation.UniformOutputTimeSpec;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLTags;

import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Jpeg;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.Watermark;
import com.lowagie.text.pdf.BaseFont;
import com.sun.imageio.plugins.jpeg.JPEGImageWriter;


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
	
	private static final String WATERMARK_IMAGE_URI = "/images/watermark.jpg";
	private static int DEF_FONT_SIZE = 9;
	private static int DEF_HEADER_FONT_SIZE = 11;
	private Font fieldFont = null;
	private Font fieldBold = null;
	private int reactEqFontSize;
	
	protected Document document;
	

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
		Table imageTable = getTable(1, 100, 2, 0, 0);
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


	//pretty similar to its static counterpart
	protected ByteArrayOutputStream generateDocReactionsImage(Model model, Structure struct, String resolution) throws Exception {
			                                                       
        if (model == null || struct == null || !model.contains(struct) || !isValidResolutionSetting(resolution)) {
	    	throw new IllegalArgumentException("Invalid parameters for generating reactions image for structure: " + struct.getName() + 
		    									" in model: " + model.getName());
        }
	    ByteArrayOutputStream bos;
		ReactionCartoon rcartoon = new ReactionCartoon();
		rcartoon.setModel(model);
		rcartoon.setStructure(struct);
		rcartoon.refreshAll();
		//dummy settings to get the real dimensions.
		BufferedImage dummyBufferedImage = new BufferedImage(DEF_IMAGE_WIDTH, DEF_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D dummyGraphics = (Graphics2D)dummyBufferedImage.getGraphics();
		Dimension prefDim = rcartoon.getPreferedSize(dummyGraphics);
		int width = (int)prefDim.getWidth()*110/100;
		int height = (int)prefDim.getHeight()*110/100;
		
		int MAX_IMAGE_HEIGHT = 532;
		
		if (width < ITextWriter.DEF_IMAGE_WIDTH) {
			width = ITextWriter.DEF_IMAGE_WIDTH;
		} 
		
		if (height < ITextWriter.DEF_IMAGE_HEIGHT) {
			height = ITextWriter.DEF_IMAGE_HEIGHT;
		} else if (height > MAX_IMAGE_HEIGHT) {
			height = MAX_IMAGE_HEIGHT;
		}
			
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		while (true) {
			try {
				new cbit.gui.graph.GraphLayout().layout(rcartoon, g, new Dimension(width,height));
				break;
			} catch (cbit.gui.graph.LayoutException e) {
				System.out.println("Layout error: " + e.getMessage());
				rcartoon.setZoomPercent(rcartoon.getZoomPercent()*90/100);
			}
		}
		rcartoon.paint(g, null);
		bos = new ByteArrayOutputStream();
		ImageOutputStream imageOut = null;

		try {
			imageOut = ImageIO.createImageOutputStream(bos);
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		
		JPEGImageWriter imageWriter = new JPEGImageWriter(null);
		imageWriter.setOutput(imageOut);
		imageWriter.write(bufferedImage);
		
		bos.flush();
		
		return bos;
	}


//pretty similar to its static counterpart
	protected ByteArrayOutputStream generateDocStructureImage(Model model, String resolution) throws Exception {

		if (model == null || !isValidResolutionSetting(resolution)) {
	    	throw new IllegalArgumentException("Invalid parameters for generating structure image for model:" + model.getName());
        }
		ByteArrayOutputStream bos;

		// Create a new model and clone the structures only
		// Getting rid of species so that the image created will not have a problem being added to the document
		// when there are more than 15 species in the model.
		Model sparseModel = new Model(model.getName());
		Structure[] oldStructures = (Structure[])cbit.util.BeanUtils.cloneSerializable(model.getStructures());
		sparseModel.setStructures(oldStructures);
		 
		StructureCartoon scartoon = new StructureCartoon();
		scartoon.setModel(sparseModel);
		scartoon.refreshAll();
		//scartoon.setZoomPercent(scartoon.getZoomPercent()*3);
		BufferedImage dummyBufferedImage = new BufferedImage(DEF_IMAGE_WIDTH, DEF_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D dummyGraphics = (Graphics2D)dummyBufferedImage.getGraphics();
		Dimension prefDim = scartoon.getPreferedSize(dummyGraphics);
		
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
			try {
				new cbit.gui.graph.GraphLayout().layout(scartoon, g, new Dimension(width,height));
				break;
			} catch (cbit.gui.graph.LayoutException e) {
				System.out.println("Layout error: " + e.getMessage());
				scartoon.setZoomPercent(scartoon.getZoomPercent()*90/100);
			}
		}
		scartoon.paint(g, null);
		bos = new ByteArrayOutputStream();
		ImageOutputStream imageOut = null;

		try {
			imageOut = ImageIO.createImageOutputStream(bos);
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		
		JPEGImageWriter imageWriter = new JPEGImageWriter(null);
		imageWriter.setOutput(imageOut);
		imageWriter.write(bufferedImage);

		bos.flush();

		// *** Writing the image into a file - testing if the image is generated.
		
		//File gifFile = new File("\\Temp\\BIOMODEL.GIF");
		//java.io.FileOutputStream osGif = null;
		//try {
			//osGif = new java.io.FileOutputStream(gifFile);
		//}catch (java.io.IOException e){
			//e.printStackTrace(System.out);
			//throw new RuntimeException("error writing image to file '"+gifFile+": "+e.getMessage());
		//}	
		//bos.writeTo(osGif);

		return bos;
	}


	protected ByteArrayOutputStream generateGeometryImage(Geometry geom) throws Exception{

		GeometrySpec geomSpec = geom.getGeometrySpec();
		IndexColorModel icm = cbit.vcell.simdata.DisplayAdapterService.getHandleColorMap();
		VCImage geomImage = geomSpec.getSampledImage();
		byte [] pixels = geomImage.getPixels();			
		int x = geomImage.getNumX(); 
		int y = geomImage.getNumY();
		int z = geomImage.getNumZ();

		BufferedImage bufferedImage = new BufferedImage(x, y, BufferedImage.TYPE_BYTE_INDEXED, icm);
		java.awt.image.WritableRaster wr = bufferedImage.getRaster();
		for (int i = 0; i < x; i++){
			for (int j = 0; j < y; j++){
				wr.setSample(i , j, 0, geomImage.getPixel(i, j, 0));
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
		
		Image adjImage = bufferedImage.getScaledInstance(adjX, adjY, BufferedImage.SCALE_REPLICATE);
		BufferedImage newBufferedImage = new BufferedImage(adjX, adjY, BufferedImage.TYPE_BYTE_INDEXED, icm);
		newBufferedImage.getGraphics().drawImage(adjImage, 0, 0, null);
		
		ByteArrayOutputStream bos = null;
		bos = new ByteArrayOutputStream();

		ImageOutputStream imageOut = null; 
		try {
			imageOut = ImageIO.createImageOutputStream(bos);
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		
		JPEGImageWriter imageWriter = new JPEGImageWriter(null);
		imageWriter.setOutput(imageOut);
		imageWriter.write(newBufferedImage);
		
		//for (int k = 0; k < z; k++) { 
			//int intPixels [] = new int[pixels.length/z];
			//for (int j = 0; j < pixels.length/z; j++) {
				//intPixels[j] = icm.getRGB(((int)pixels[j + (k*x*y)]) & 0x000000FF);
			//}
			////bos = new ByteArrayOutputStream();

			////ImageOutputStream imageOut = null;
			////try {
				////imageOut = ImageIO.createImageOutputStream(bos);
			////} catch (java.io.IOException ioe) {
				////ioe.printStackTrace(System.out);
			////}
			
			////JPEGImageWriter imageWriter = new JPEGImageWriter(null);
			////imageWriter.setOutput(imageOut);
			////imageWriter.write(bufferedImage);
			
				////GIFUtils.GIFOutputStream gifOut = new GIFUtils.GIFOutputStream(bos);
				////try {
					////GIFUtils.GIFImage gifImage = new GIFUtils.GIFImage(intPixels, x);
					////gifImage.write(gifOut);
				////} catch (GIFUtils.GIFFormatException gfe) {      //try reducing to 256 colors.
					////System.err.println("Error in image retrieval. Will try reducing the colors to 256.");
					//////gfe.printStackTrace();
					////int pixels2D[][] = new int[x][y];
			        ////for (int m = x; m-- > 0; ) {
			            ////for (int n = y; n-- > 0; ) {
			                ////pixels2D[m][n] = intPixels[n * x + m];
			            ////}
			        ////}
			        ////int palette[] = Quantize.quantizeImage(pixels2D, 256);
		        	////intPixels = Quantize.getPixels(palette, pixels2D);      
					////GIFUtils.GIFImage gifImage = new GIFUtils.GIFImage(intPixels, x);
					////gifImage.write(gifOut);
				////}
		//}

		bos.flush();
		//printImageToFile(bos);
		
		return bos;
	}


	public static ByteArrayOutputStream generateReactionsImage(Model model, Structure struct, String resolution) throws Exception {
			      
		final int SIZE_INC = 10; 
        if (model == null || struct == null || !model.contains(struct) || !isValidResolutionSetting(resolution)) {
	    	throw new IllegalArgumentException("Invalid parameters for generating reactions image for structure: " + struct.getName() + 
		    									" in model: " + model.getName());
        }
	    ByteArrayOutputStream bos;
		ReactionCartoon rcartoon = new ReactionCartoon();
		rcartoon.setModel(model);
		rcartoon.setStructure(struct);
		rcartoon.refreshAll();
		int zoom = ITextWriter.getZoom(resolution);
		System.out.println(resolution + " " + zoom);
		rcartoon.setZoomPercent(rcartoon.getZoomPercent()*zoom);
		//dummy settings to get the real dimensions.
		BufferedImage dummyBufferedImage = new BufferedImage(DEF_IMAGE_WIDTH, DEF_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D dummyGraphics = (Graphics2D)dummyBufferedImage.getGraphics();
		Dimension prefDim = rcartoon.getPreferedSize(dummyGraphics);
		int width = (int)prefDim.getWidth()*110/100 + SIZE_INC;           
		int height = (int)prefDim.getHeight()*110/100 + SIZE_INC;
		BufferedImage bufferedImage;
		Graphics2D g;
		while (true) {
			try {
				System.out.println("Image width: " + width + " height: " + height);
				bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
				g = (Graphics2D)bufferedImage.getGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				new cbit.gui.graph.GraphLayout().layout(rcartoon, g, new Dimension(width,height));
				break;
			} catch (cbit.gui.graph.LayoutException e) {
				System.out.println("Layout error in " + struct.getName() + ": " + e.getMessage());
				//rcartoon.setZoomPercent(rcartoon.getZoomPercent()*90/100);
				width = width + SIZE_INC*5;
				height = height + SIZE_INC*5;
			}
		}
		g.setBackground(java.awt.Color.white);
		g.clearRect(0, 0, width, height);
		g.setColor(java.awt.Color.red);
		rcartoon.paint(g, null);
		bos = new ByteArrayOutputStream();

		ImageOutputStream imageOut = null;

		try {
			imageOut = ImageIO.createImageOutputStream(bos);
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		
		JPEGImageWriter imageWriter = new JPEGImageWriter(null);
		imageWriter.setOutput(imageOut);
		imageWriter.write(bufferedImage);
		
		bos.flush();
		
		return bos;
	}


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
		scartoon.setZoomPercent(scartoon.getZoomPercent()*zoom);
		//dummy settings to get the real dimensions.
		BufferedImage dummyBufferedImage = new BufferedImage(DEF_IMAGE_WIDTH, DEF_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D dummyGraphics = (Graphics2D)dummyBufferedImage.getGraphics();
		Dimension prefDim = scartoon.getPreferedSize(dummyGraphics);
		int width = (int)prefDim.getWidth()*110/100 + SIZE_INC*30;                 //too generous
		int height = (int)prefDim.getHeight()*110/100 + SIZE_INC*30;
		BufferedImage bufferedImage;
		Graphics2D g;
		while (true) {
			try {
				System.out.println("Image width: " + width + " height: " + height);
			    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
				g = (Graphics2D)bufferedImage.getGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				new cbit.gui.graph.GraphLayout().layout(scartoon, g, new Dimension(width,height));
				break;
			} catch (cbit.gui.graph.LayoutException e) {
				System.out.println("Layout error: " + e.getMessage());
				width = width + SIZE_INC*5;
				height = height + SIZE_INC*5;
			}
		}
		scartoon.paint(g, null);
		bos = new ByteArrayOutputStream();
		
		ImageOutputStream imageOut = null;
		try {
			imageOut = ImageIO.createImageOutputStream(bos);
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		
		JPEGImageWriter imageWriter = new JPEGImageWriter(null);
		imageWriter.setOutput(imageOut);
		imageWriter.write(bufferedImage);
		bos.flush();

		return bos;
	}


	protected ByteArrayOutputStream generateStructureMappingImage(SimulationContext simContext, int width, int height) throws Exception {
             
		StructureMappingCartoon structMapCartoon = new StructureMappingCartoon();
		structMapCartoon.setSimulationContext(simContext);
		structMapCartoon.refreshAll();                           
	    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
		while (true) {
			try {
				new cbit.gui.graph.GraphLayout().layout(structMapCartoon, g, new Dimension(width,height));
				break;
			} catch (cbit.gui.graph.LayoutException e) {
				System.out.println("Layout error: " + e.getMessage());
				structMapCartoon.setZoomPercent(structMapCartoon.getZoomPercent()*90/100);
			}
		}
		structMapCartoon.paint(g, null);
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		
		ImageOutputStream imageOut = null;
		try {
			imageOut = ImageIO.createImageOutputStream(bos);
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		
		JPEGImageWriter imageWriter = new JPEGImageWriter(null);
		imageWriter.setOutput(imageOut);
		imageWriter.write(bufferedImage);

		bos.flush();

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


	// for debugging only, temp
	private static void printImageToFile(ByteArrayOutputStream bos) {

		final String FILE_PREFIX = "c:\\publish\\temp_";
		final String FILE_SUFFIX = ".gif";
		int i = 0;
		while (new java.io.File(FILE_PREFIX + i + FILE_SUFFIX).exists()) {
			i++;
		}
		try {
			FileOutputStream fos = new FileOutputStream(FILE_PREFIX + i + FILE_SUFFIX);
			bos.flush();
			bos.writeTo(fos);
			fos.flush();
			fos.close();
		} catch (java.io.IOException e) {
			System.err.println("Unable to print image to file.");
			e.printStackTrace();
		}
	}


	protected void setDocument(PageFormat pageFormat) {

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


	//this method will set a new page for a section
	private void setNewPage(Section section, com.lowagie.text.Image image) {

		Chunk chunk = new Chunk("");
		chunk.setNewPage();
		Paragraph newPage = new Paragraph(chunk);
		section.add(newPage);
	}


	public void writeBioModel(BioModel biomodel, FileOutputStream fos, PageFormat pageFormat) throws Exception {

		writeBioModel(biomodel, fos, pageFormat, PublishPreferences.DEFAULT_BIO_PREF);
	}


public void writeBioModel(BioModel bioModel, FileOutputStream fos, PageFormat pageFormat, PublishPreferences preferences) throws Exception {

	if (bioModel == null || fos == null || pageFormat == null || preferences == null) {
		throw new IllegalArgumentException("One or more null params while publishing BioModel.");
	}
	try {
		setDocument(pageFormat);
		DocWriter docWriter = createDocWriter(fos);
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
			writeMetadata(introSection, name, bioModel.getDescription(), userName, "BioModel");
			writeModel(physioChapter, bioModel.getModel());
			document.add(physioChapter);
		}
		if (preferences.includeApp()) {
			SimulationContext simContexts [] = bioModel.getSimulationContexts();
			if (simContexts.length > 0) {
				Chapter simContextsChapter = new Chapter("Applications For " + name, chapterNum++);
				if (introSection == null) {
					introSection = simContextsChapter.addSection("General Info", simContextsChapter.numberDepth() + 1);                      
					writeMetadata(introSection, name, bioModel.getDescription(), userName, "BioModel");
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
		Enumeration enum_fi = fs.getFastInvariants();
		while (enum_fi.hasMoreElements()){
			FastInvariant fi = (FastInvariant)enum_fi.nextElement();
			eqTable.addCell(createCell(VCML.FastInvariant, getFont()));
			eqTable.addCell(createCell(fi.getFunction().infix(), getFont()));
		}		
		Enumeration enum_fr = fs.getFastRates();
		while (enum_fr.hasMoreElements()){
			FastRate fr = (FastRate)enum_fr.nextElement();
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
			Section geomSection = container.addSection("Geoemtry: " + geom.getName(), container.numberDepth() + 1);
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
			setDocument(pageFormat);
			DocWriter docWriter = createDocWriter(fos);
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
			paramTable.addCell(createHeaderCell("IExpression", getBold(), 1));
			paramTable.addCell(createHeaderCell("Role", getBold(), 1));
			paramTable.addCell(createHeaderCell("Unit", getBold(), 1));
			paramTable.endHeaders();
			for (int k = 0; k < kineticsParameters.length; k++) {
				String name = kineticsParameters[k].getName();
				IExpression expression = kineticsParameters[k].getExpression();
				String role = rs.getKinetics().getDefaultParameterDesc(kineticsParameters[k].getRole());
				VCUnitDefinition unit = kineticsParameters[k].getUnitDefinition();
				paramTable.addCell(createCell(name, getFont()));
				paramTable.addCell(createCell((expression == null ? "": expression.infix()), getFont()));
				paramTable.addCell(createCell(role, getFont()));
				paramTable.addCell(createCell((unit == null ? "": unit.getSymbol()), getFont()));      //dimensionless will show as '1'.
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
		IExpression expArray [] = null;
		Table imageTable = null;
		BufferedImage dummy = new BufferedImage(500, 50, BufferedImage.TYPE_3BYTE_BGR);
		int scale = 1;
		int viewableWidth = (int)(document.getPageSize().width() - document.leftMargin() - document.rightMargin());
		//add Constants
		Enumeration constantsList = mathDesc.getConstants();
		while (constantsList.hasMoreElements()) {
			Constant constant = ((Constant)constantsList.nextElement());
			IExpression exp = constant.getExpression();
			try {
				expArray = new IExpression[] { ExpressionFactory.assign(ExpressionFactory.createExpression(constant.getName()), exp.flatten()) };
			} catch(org.vcell.expression.ExpressionException ee) {
				System.err.println("Unable to process constant " + constant.getName() + " for publishing");
				ee.printStackTrace();
				continue;
			}
			try {
				Dimension dim = ExpressionPrintFormatter.getExpressionImageSize(expArray, (Graphics2D)dummy.getGraphics());
				BufferedImage bufferedImage = new BufferedImage((int)dim.getWidth()*scale, (int)dim.getHeight()*scale, BufferedImage.TYPE_3BYTE_BGR);
				ExpressionPrintFormatter.getExpressionAsImage(expArray, bufferedImage, scale);		           
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
		Enumeration functionsList = mathDesc.getFunctions();
		while (functionsList.hasMoreElements()) {
			Function function = ((Function)functionsList.nextElement());
			IExpression exp = function.getExpression();
			try {
				expArray = new IExpression[] { ExpressionFactory.assign(ExpressionFactory.createExpression(function.getName()), exp.flatten()) };
			} catch(org.vcell.expression.ExpressionException ee) {
				System.err.println("Unable to process function " + function.getName() + " for publishing");
				ee.printStackTrace();
				continue;
			}
			try {    
				Dimension dim = ExpressionPrintFormatter.getExpressionImageSize(expArray, (Graphics2D)dummy.getGraphics());
				BufferedImage bufferedImage = new BufferedImage((int)dim.getWidth()*scale, (int)dim.getHeight()*scale, BufferedImage.TYPE_3BYTE_BGR);
				ExpressionPrintFormatter.getExpressionAsImage(expArray, bufferedImage, scale);     
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
		Enumeration constantsList = mathDesc.getConstants();
		while (constantsList.hasMoreElements()) {
			Constant constant = ((Constant)constantsList.nextElement());
			IExpression exp = constant.getExpression();
			if (expTable == null) {
				expTable = getTable(2, 100, 1, 2, 2);
				expTable.addCell(createHeaderCell("Constant Name", getBold(), 1));
				expTable.addCell(createHeaderCell("IExpression", getBold(), 1));
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
		Enumeration functionsList = mathDesc.getFunctions();
		while (functionsList.hasMoreElements()) {
			Function function = ((Function)functionsList.nextElement());
			IExpression exp = function.getExpression();
			if (expTable == null) {
				expTable = getTable(2, 100, 1, 2, 2);
				expTable.addCell(createHeaderCell("Function Name", getBold(), 1));
				expTable.addCell(createHeaderCell("IExpression", getBold(), 1));
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
			setDocument(pageFormat);
			DocWriter docWriter = createDocWriter(fos);
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
			IExpression tempExp = memMapping.getInitialVoltageParameter().getExpression();
			VCUnitDefinition tempUnit = memMapping.getInitialVoltageParameter().getUnitDefinition();
			if (tempExp != null) {
				initVoltage = tempExp.infix();
				if (tempUnit != null) {
					initVoltage += "   " + tempUnit.getSymbol();
				}
			}
			String spCap = "";
			tempExp = memMapping.getSpecificCapacitanceParameter().getExpression();
			tempUnit = memMapping.getSpecificCapacitanceParameter().getUnitDefinition();
			if (tempExp != null) {
				spCap = tempExp.infix();
				if (tempUnit != null) {
					spCap += "   " + tempUnit.getSymbol();
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
			String currName = electricalStimuli[j].getCurrentParameter().getName();
			String clampType = "", expStr = "";
			IExpression tempExp = null;
			VCUnitDefinition tempUnit = null;
			if (electricalStimuli[j] instanceof CurrentClampStimulus) {
				CurrentClampStimulus stimulus = (CurrentClampStimulus) electricalStimuli[j];
				tempExp = stimulus.getCurrentParameter().getExpression();
				tempUnit = stimulus.getCurrentParameter().getUnitDefinition();
				clampType = "Current";
			} else if (electricalStimuli[j] instanceof VoltageClampStimulus) {
				VoltageClampStimulus stimulus = (VoltageClampStimulus) electricalStimuli[j];
				tempExp = stimulus.getVoltageParameter().getExpression();
				tempUnit = stimulus.getVoltageParameter().getUnitDefinition();
				clampType = "Voltage";
			}
			if (tempExp != null) {
				expStr = tempExp.infix();
				if (tempUnit != null) {
					expStr += "   " + tempUnit.getSymbol();
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
				memMapSection = simContextSection.addSection("Membrane Mapping For " + simContext.getName(), memMapSection.numberDepth() + 1);
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
	if (model.getNumStructures() > 0) {
		try {
			int width = (int)(document.getPageSize().width() - document.leftMargin() - document.rightMargin());  
			int height = (int)(document.getPageSize().height() - document.topMargin() - document.bottomMargin())/2;        //half the page size.    
			ByteArrayOutputStream bos = generateDocStructureImage(model, ITextWriter.LOW_RESOLUTION);
			structSection = physioChapter.addSection("Structures For: " + model.getName(), physioChapter.numberDepth() + 1);
			addImage(structSection, bos);
		} catch(Exception e) {
			System.err.println("Unable to add structures image for model: " + model.getName());
			e.printStackTrace();
		}
	}
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
		writeStructure(model, model.getStructures(i), structTable);
	}
	
	if (structTable != null) {
		structSection.add(structTable);
	}
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
			String reactionType = reactionSpecs[i].getReactionStep().getTerm();
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
			cbit.vcell.units.VCUnitDefinition diffUnit = speciesContSpecs[i].getDiffusionParameter().getUnitDefinition();
			String initConc = speciesContSpecs[i].getInitialConditionParameter().getExpression().infix();
			cbit.vcell.units.VCUnitDefinition initConcUnit = speciesContSpecs[i].getInitialConditionParameter().getUnitDefinition();
			speciesSpecTable.addCell(createCell(speciesName, getFont()));
			speciesSpecTable.addCell(createCell(structName, getFont()));
			speciesSpecTable.addCell(createCell(initConc + (initConcUnit == null ? "": "   " + initConcUnit.getSymbol()), getFont()));
			speciesSpecTable.addCell(createCell(diff + (diffUnit == null ? "": "   " + diffUnit.getSymbol()), getFont()));
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


	private void writeReactionParticipant(ReactionParticipant rp, Table table, String type) throws DocumentException {

		table.addCell(createCell(rp.getSpecies().getCommonName(), getFont()));
		table.addCell(createCell(type, getFont()));
		String stoichStr;
		if (!type.equals(XMLTags.CatalystTag)) {
			table.addCell(createCell("" + rp.getStoichiometry(), getFont()));
		} else {
			table.addCell(createCell("N/A", getFont()));
		}
	}


//each reaction has its own table, ordered by the structures.
	protected void writeReactions(Chapter physioChapter, Model model) throws DocumentException {

		if (model == null) {
			return;
		}
		for (int i = 0; i < model.getNumStructures(); i++) {
			ReactionStep[] reactionSteps = model.getReactionSteps();
			ReactionStep rs = null;
			Table reactTable = null;
			boolean firstTime = true;
			Section reactStructSection = null;
			for (int j = 0; j < reactionSteps.length; j++) {
				if (reactionSteps[j].getStructure() == model.getStructures(i)) {         //can also use structureName1.equals(structureName2)
					if (firstTime) {
						Paragraph linkParagraph = new Paragraph();
						linkParagraph.add(new Chunk("Reaction(s) in " + model.getStructures(i).getName()).setLocalDestination(model.getStructures(i).getName()));
						reactStructSection = physioChapter.addSection(linkParagraph, physioChapter.numberDepth() + 1);
						try {
							//int width = (int)(document.getPageSize().width() - document.leftMargin() - document.rightMargin());  
							//int height = (int)(document.getPageSize().height() - document.topMargin() - document.bottomMargin())/2;        //half the page size.    
							ByteArrayOutputStream bos = generateDocReactionsImage(model, model.getStructures(i), ITextWriter.LOW_RESOLUTION);               
							addImage(reactStructSection, bos);
						} catch (Exception e) {
							System.err.println("Unable to add structure mapping image to report.");
							e.printStackTrace();
						}
						firstTime = false;
					}
					rs = reactionSteps[j];
					String type;
					if (rs instanceof SimpleReaction) {
						type = "Reaction";
					} else {
						type = "Flux";
					}
					//write Reaction Participants.
					int height = 50;                                //arbitrary
					BufferedImage bufferedImage = new BufferedImage(ITextWriter.DEF_IMAGE_WIDTH, height, BufferedImage.TYPE_3BYTE_BGR);
//					reactEqFontSize = rc.getReactionAsImage(bufferedImage, ITextWriter.DEF_IMAGE_WIDTH, height, reactEqFontSize);
					ReactionCanvasDisplaySpec rcDisplaySpec = ReactionCanvasDisplaySpec.fromReactionStep(rs);
					ReactionRenderer.paint(rcDisplaySpec, bufferedImage, ITextWriter.DEF_IMAGE_WIDTH, height, reactEqFontSize, Color.lightGray);
					try {             
						com.lowagie.text.Image rpImage = com.lowagie.text.Image.getInstance(bufferedImage, null);
						rpImage.setAlignment(com.lowagie.text.Image.MIDDLE);
						Cell imageCell = new Cell();
						//imageCell.setBackgroundColor(new java.awt.Color(0xC0, 0xC0, 0xC0));
						imageCell.add(rpImage);
						reactTable = getTable(1, 100, 0, 1, 1);
						reactTable.addCell(imageCell);
					} catch (Exception e) {
						System.err.println("Unable to add structure mapping image to report.");
						e.printStackTrace();
					}
					if (reactTable != null) {
						Section reactionSection = reactStructSection.addSection(type + " " + rs.getName(), reactStructSection.numberDepth() + 1);
						reactionSection.add(reactTable);
						writeKineticsParams(reactionSection, rs);
					}
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
				IExpression tempExp = mo.getDefaultExpression(constants[i]);
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
			String solverName = solverDesc.getSolverDescription().getName();
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
		if (struct instanceof Membrane) {
			structTable.addCell(createCell("Membrane", getFont()));
			Feature outsideFeature = ((Membrane)struct).getOutsideFeature();
			Feature insideFeature = ((Membrane)struct).getInsideFeature();
			structTable.addCell(createCell((insideFeature != null ? insideFeature.getName() : "N/A"), getFont()));
			structTable.addCell(createCell((outsideFeature != null ? outsideFeature.getName() : "N/A"), getFont()));
		} else {
			structTable.addCell(createCell("Feature", getFont()));
			String outsideStr = "N/A", insideStr = "N/A";
			Membrane enclosingMem = (Membrane)struct.getParentStructure();
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
			cbit.vcell.geometry.SubVolume subVol = ((FeatureMapping)structMap[i]).getSubVolume();
			String subDomain = "";
			if (subVol != null) {
				subDomain = subVol.getName();
			}
			boolean isResolved = ((FeatureMapping)structMap[i]).getResolved();
			String surfVolStr = "", volFractStr = "";
			MembraneMapping mm = (MembraneMapping)gc.getStructureMapping(structMap[i].getStructure().getParentStructure());
			if (mm != null) {
				StructureMapping.StructureMappingParameter smp = mm.getSurfaceToVolumeParameter();
				if (smp != null) {
					IExpression tempExp = smp.getExpression();
					VCUnitDefinition tempUnit = smp.getUnitDefinition();
					if (tempExp != null) {
						surfVolStr = tempExp.infix();
						if (tempUnit != null && !VCUnitDefinition.UNIT_DIMENSIONLESS.compareEqual(tempUnit)) {  //no need to add '1' for dimensionless unit
							surfVolStr += " " + tempUnit.getSymbol();
						}
					}
				}
				smp = mm.getVolumeFractionParameter();
				if (smp != null) {
					IExpression tempExp = smp.getExpression();
					VCUnitDefinition tempUnit = smp.getUnitDefinition();
					if (tempExp != null) {
						volFractStr = tempExp.infix();
						if (tempUnit != null && !VCUnitDefinition.UNIT_DIMENSIONLESS.compareEqual(tempUnit)) { 
							volFractStr += " " + tempUnit.getSymbol();
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

		Enumeration subDomains = mathDesc.getSubDomains();
		IExpression expArray [];
		Table volDomainsTable = null;
		Table memDomainsTable = null;
		Section volDomains = mathDescSection.addSection("Volume Domains", mathDescSection.depth() + 1);
		Section memDomains = mathDescSection.addSection("Membrane Domains", mathDescSection.depth() + 1);
		int scale = 1, height = 200;                            //arbitrary
		int viewableWidth = (int)(document.getPageSize().width() - document.leftMargin() - document.rightMargin());
		BufferedImage dummy = new BufferedImage(viewableWidth, height, BufferedImage.TYPE_3BYTE_BGR);
		while(subDomains.hasMoreElements()) {
			SubDomain subDomain = (SubDomain)subDomains.nextElement();
			Enumeration equationsList = subDomain.getEquations();
			ArrayList expList = new ArrayList();
			while (equationsList.hasMoreElements()) {
				Equation equ = (Equation)equationsList.nextElement();
				try {
					Enumeration enum_equ = equ.getTotalExpressions();
					while (enum_equ.hasMoreElements()){
						IExpression exp = ExpressionFactory.createExpression((IExpression)enum_equ.nextElement());	
						expList.add(exp.flatten());
					}
				} catch (org.vcell.expression.ExpressionException ee) {
					System.err.println("Unable to process the equation for subdomain: " + subDomain.getName());
					ee.printStackTrace();
					continue;
				}
			}
			expArray = (IExpression [])expList.toArray(new IExpression[expList.size()]);
			Section tempSection = null;
			if (subDomain instanceof cbit.vcell.math.CompartmentSubDomain) {
				tempSection = volDomains.addSection(subDomain.getName(), volDomains.depth() + 1);
			} else if (subDomain instanceof cbit.vcell.math.MembraneSubDomain) {
				tempSection = memDomains.addSection(subDomain.getName(), memDomains.depth() + 1);
			}
			try { 
				Dimension dim = ExpressionPrintFormatter.getExpressionImageSize(expArray, (Graphics2D)dummy.getGraphics());
				System.out.println("Image dim: " + dim.width + " " + dim.height);
				BufferedImage bufferedImage = new BufferedImage((int)dim.getWidth()*scale, (int)dim.getHeight()*scale, BufferedImage.TYPE_3BYTE_BGR);
				ExpressionPrintFormatter.getExpressionAsImage(expArray, bufferedImage, scale);
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

		Enumeration subDomains = mathDesc.getSubDomains();
		Section volDomains = mathDescSection.addSection("Volume Domains", mathDescSection.depth() + 1);
		Section memDomains = mathDescSection.addSection("Membrane Domains", mathDescSection.depth() + 1);
		Section filDomains = mathDescSection.addSection("Filament Domains", mathDescSection.depth() + 1);
		while(subDomains.hasMoreElements()) {
			Enumeration equationsList;
			Section tempSection = null;
			SubDomain subDomain = (SubDomain)subDomains.nextElement();
			if (subDomain instanceof CompartmentSubDomain) {
				tempSection = volDomains.addSection(subDomain.getName(), volDomains.depth() + 1);
			} else if (subDomain instanceof MembraneSubDomain) {
				tempSection = memDomains.addSection(subDomain.getName(), memDomains.depth() + 1);
			} else if (subDomain instanceof FilamentSubDomain) {
				tempSection = filDomains.addSection(subDomain.getName(), filDomains.depth() + 1);
			}
			equationsList = subDomain.getEquations();
			while (equationsList.hasMoreElements()) {
				Equation equ = (Equation)equationsList.nextElement();
				writeEquation(tempSection, equ);
			}
			if (subDomain.getFastSystem() != null) {
				writeFastSystem(tempSection, subDomain.getFastSystem());
			}
			if (subDomain instanceof MembraneSubDomain) {
				Enumeration jcList = ((MembraneSubDomain)subDomain).getJumpConditions();
				while (jcList.hasMoreElements()) {
					JumpCondition jc = (JumpCondition)jcList.nextElement();
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


	private void writeWatermark(PageFormat pageFormat) {

		try {
			Jpeg jpg = new Jpeg(this.getClass().getResource(WATERMARK_IMAGE_URI));
			double x = (pageFormat.getWidth() - jpg.width())/2.0;
			double y = (pageFormat.getHeight() - jpg.height())/2.0;
			document.add(new Watermark(jpg, (int) x, (int) y));
		} catch (Exception e) {
			System.out.println("Error in adding watermark to document.");
			e.printStackTrace();
		}
	}
}