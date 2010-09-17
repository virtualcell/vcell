package cbit.vcell.xml;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.cellml.CellQuanVCTranslator;
import org.vcell.sbml.vcell.MathModel_SBMLExporter;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.VCDocument;

import cbit.image.VCImage;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.importer.AnnotatedImageDataset;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXmlReader;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.xml.XMLMetaDataReader;
import cbit.vcell.biomodel.meta.xml.XMLMetaDataWriter;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelDbTreePanel;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.xml.merge.NodeInfo;
import cbit.xml.merge.TMLPanel;
import cbit.xml.merge.XmlTreeDiff;

/**
Useful stuff for importing vFrap biomodels and the associated images into vCell
 * Creation date: (09/02/2010 11:54:27)
 * 
 * @author: Dan Vasilescu
 */
public class VFrapXmlHelper {
	
	double[] firstPostBleach = null;
	double[] prebleachAvg = null;
	BufferedImage[] roiComposite = null;
	
	public double[] getFirstPostBleach() {
		if(firstPostBleach == null) {
			throw new RuntimeException("Missing vFrap FirstPostbleach Image");
		} else {
			return firstPostBleach;
		}
	}
	public double[] getPrebleachAvg() {
		if(prebleachAvg == null) {
			throw new RuntimeException("Missing vFrap PrebleachAverage Image");
		} else {
			return prebleachAvg;
		}
	}
	public BufferedImage[] getRoiComposite() {
		if(roiComposite == null) {
			throw new RuntimeException("Missing vFrap RoiComposite Image");
		} else {
			return roiComposite;
		}
	}

	public boolean isAlreadyImported(String candidateName, DocumentManager documentManager) throws DataAccessException
	{
		FieldDataDBOperationSpec fdos = FieldDataDBOperationSpec.createGetExtDataIDsSpec(documentManager.getUser());
		FieldDataDBOperationResults fieldDataDBOperationResults = documentManager.fieldDataDBOperation(fdos);
		ExternalDataIdentifier[] externalDataIdentifierArr = fieldDataDBOperationResults.extDataIDArr;
		
		for(ExternalDataIdentifier edi : externalDataIdentifierArr) {
			if(candidateName.equals(edi.getName())) {
				return true;
			}
		}
		return false;
	}
	// load and compute prebleach average and first postbleach images
	public void LoadVFrapSpecialImages(AnnotatedImageDataset annotatedImages, int startingIndexRecovery) 
	{
		// unnormalized prebleach average
		prebleachAvg = new double[annotatedImages.getImageDataset().getImage(0, 0, startingIndexRecovery).getNumXYZ()];
		for(int j = 0; j < prebleachAvg.length; j++)
		{
			double pixelTotal = 0;
			for(int i = 0 ; i < startingIndexRecovery; i++)
			{
				pixelTotal = pixelTotal + (annotatedImages.getImageDataset().getImage(0, 0, i).getPixels()[j] & 0x0000FFFF);
			}
			prebleachAvg[j] = pixelTotal/startingIndexRecovery;
		}
	
		// unnormalized first post bleach
		firstPostBleach = new double[annotatedImages.getImageDataset().getImage(0, 0, startingIndexRecovery).getNumXYZ()];
		short[] pixels = annotatedImages.getImageDataset().getImage(0, 0, startingIndexRecovery).getPixels();
		for(int i = 0; i< pixels.length; i++)
		{
			firstPostBleach[i] = pixels[i] & 0x0000FFFF;
		}
	}
	
	// load ROI images
	public void LoadVFrapRoiCompositeImages(AnnotatedImageDataset annotatedImages, ROI rois[])
	{
		//make roi composite
		int[] cmap = new int[256];
		for(int i=0;i<256;i+= 1){
			cmap[i] = OverlayEditorPanelJAI.CONTRAST_COLORS[i].getRGB();
			if(i==0){
				cmap[i] = new Color(0, 0, 0, 0).getRGB();
			}
		}
		IndexColorModel indexColorModel =
			new java.awt.image.IndexColorModel(
					8, cmap.length,cmap,0,
					false, 				// false means NOT USE alpha
					-1,					// NO transparent single pixel
					java.awt.image.DataBuffer.TYPE_BYTE);
		roiComposite = new BufferedImage[annotatedImages.getImageDataset().getSizeZ()];
		for (int i = 0; i < roiComposite.length; i++) {
			roiComposite[i] = 
				new BufferedImage(annotatedImages.getImageDataset().getISize().getX(), annotatedImages.getImageDataset().getISize().getY(),
					BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
		}
		int xysize = annotatedImages.getImageDataset().getISize().getX()*annotatedImages.getImageDataset().getISize().getY();
		for (int i = 0; i < rois.length; i++) {
			short[] roiBits = rois[i].getBinaryPixelsXYZ(1);
			for (int j = 0; j < roiBits.length; j++) {
				int roiZindex = j/(xysize);
				byte[] roiData = ((DataBufferByte)roiComposite[roiZindex].getRaster().getDataBuffer()).getData();
				if(roiBits[j] != 0 && (rois[i].getROIName().equals(AnnotatedImageDataset.VFRAP_ROI_ENUM.ROI_BLEACHED) || roiData[j%xysize] == 0)){
					roiData[j%xysize] = (byte)(i+1);
					//					if(i!= 0){
					//						System.out.println("roi="+i+" j="+j+" j%="+(j%xysize)+" z="+roiZindex+" roidata="+roiData[j%xysize]);
					//					}
				}
			}
		}
	}

	
	
}
	
	
	