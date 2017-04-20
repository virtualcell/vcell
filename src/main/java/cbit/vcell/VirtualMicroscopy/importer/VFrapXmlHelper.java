/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.VirtualMicroscopy.importer;

import static cbit.vcell.data.VFrapConstants.tokenNames;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

import cbit.image.ImageException;
import cbit.image.VCImageUncompressed;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.data.VFrapConstants.SymbolEquivalence;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.field.db.FieldDataDBOperationResults;
import cbit.vcell.field.db.FieldDataDBOperationSpec;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.util.AmplistorUtils;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XmlReader;



/**
Useful stuff for importing vFrap biomodels and the associated images into vCell
 * Creation date: (09/02/2010 11:54:27)
 * 
 * @author: Dan Vasilescu
 */
public class VFrapXmlHelper {
	
	private VFrapXmlHelper() {}		//no instances allowed
	
//	double[] firstPostBleach = null;
//	double[] prebleachAvg = null;
//	BufferedImage[] roiComposite = null;
//	
//	public double[] getFirstPostBleach() {
//		if(firstPostBleach == null) {
//			throw new RuntimeException("Missing vFrap FirstPostbleach Image");
//		} else {
//			return firstPostBleach;
//		}
//	}
//	public double[] getPrebleachAvg() {
//		if(prebleachAvg == null) {
//			throw new RuntimeException("Missing vFrap PrebleachAverage Image");
//		} else {
//			return prebleachAvg;
//		}
//	}
//	public BufferedImage[] getRoiComposite() {
//		if(roiComposite == null) {
//			throw new RuntimeException("Missing vFrap RoiComposite Image");
//		} else {
//			return roiComposite;
//		}
//	}

	public static boolean isAlreadyImported(String candidateName, DocumentManager documentManager) throws DataAccessException
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
	public static void checkNameAvailability(Hashtable<String, Object> hashTable, boolean isMixedFieldRequest,
			DocumentManager documentManager, Component requesterComponent) throws DataAccessException, UtilCancelException {

		String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
		String mixedFieldDataName = (String)hashTable.get("mixedFieldDataName");
		boolean nameAccepted = false;
		while(nameAccepted == false) {
			String newName = null;
			String testName = null;
			if(isMixedFieldRequest) {
				testName = mixedFieldDataName;
			} else {
				testName = initialFieldDataName;
			}
			if(isAlreadyImported(testName, documentManager)) {
				newName = DialogUtils.showInputDialog0(requesterComponent, 
						"Name " + testName + " already in use. Please choose a different name.", initialFieldDataName);
				initialFieldDataName = newName;
				mixedFieldDataName = initialFieldDataName + "Mx";
			} else {
				nameAccepted = true;
			}
		}
		hashTable.put("initialFieldDataName",initialFieldDataName);
		hashTable.put("mixedFieldDataName",mixedFieldDataName);
	}
	
//	// load and compute prebleach average and first postbleach images
//	public void LoadVFrapSpecialImages(AnnotatedImageDataset annotatedImages, int startingIndexRecovery) 
//	{
//		// unnormalized prebleach average
//		prebleachAvg = new double[annotatedImages.getImageDataset().getImage(0, 0, startingIndexRecovery).getNumXYZ()];
//		for(int j = 0; j < prebleachAvg.length; j++)
//		{
//			double pixelTotal = 0;
//			for(int i = 0 ; i < startingIndexRecovery; i++)
//			{
//				pixelTotal = pixelTotal + (annotatedImages.getImageDataset().getImage(0, 0, i).getPixels()[j] & 0x0000FFFF);
//			}
//			prebleachAvg[j] = pixelTotal/startingIndexRecovery;
//		}
//	
//		// unnormalized first post bleach
//		firstPostBleach = new double[annotatedImages.getImageDataset().getImage(0, 0, startingIndexRecovery).getNumXYZ()];
//		short[] pixels = annotatedImages.getImageDataset().getImage(0, 0, startingIndexRecovery).getPixels();
//		for(int i = 0; i< pixels.length; i++)
//		{
//			firstPostBleach[i] = pixels[i] & 0x0000FFFF;
//		}
//	}
	//
	// Locate the special images within the vFrap files and load them in memory
	//
	public static boolean LoadVFrapSpecialImages(Hashtable<String, Object> hashTable, Element vFrapRoot) 
							throws IOException, DataAccessException, MathException, ImageException 
	{
		// ------ parse the vfrap file and the log/zip files referred within -----
		int NumTimePoints = 1;					// many channels of 1 timepoint each
		int NumChannels = tokenNames.length;	// the channels: prebleach, postbleach, roi1, roi2 ... roiN
		String[] channelNames = new String[NumChannels];
		VariableType[] channelTypes = new VariableType[NumChannels];
		DataSymbolType[] channelVFrapImageType = new DataSymbolType[NumChannels];
		double[][][] pixData = new double[NumTimePoints][NumChannels][]; 

		// get the path of the file tagged with "ROIExternalDataInfoTag" and open it
		Element roiExternalDataInfoElement  = vFrapRoot.getChild(MicroscopyXMLTags.ROIExternalDataInfoTag);
		if (roiExternalDataInfoElement == null){
			// can't load FieldData for some reason, fall back to importing the biomodel only
			return false;
		}
		//	<ROIExternalDataInfo Filename="c:\vFrap\VirtualMicroscopy\SimulationData\SimID_1282941232246_0_.log">
		//		<ExternalDataIdentifier Name="timeData" KeyValue="1282941232246" OwnerName="SimulationData" OwnerKey="0" />
		//	</ImageDatasetExternalDataInfo>
		String filename = (roiExternalDataInfoElement).getAttributeValue("Filename");	// c:\VirtualMicroscopy\SimulationData\SimID_1284149203811_0_.log
		Element childElement = (roiExternalDataInfoElement).getChild("ExternalDataIdentifier");
		if(childElement == null) {
			// can't load FieldData for some reason, fall back to importing the biomodel only
			return false;
		}
		StringTokenizer tokens = new StringTokenizer(filename,"/\\.");
		final ArrayList<String> tokenArray = new ArrayList<String>();
		while (tokens.hasMoreElements()){
			tokenArray.add(tokens.nextToken());
		}
		final String dataID = tokenArray.get(tokenArray.size()-2);
		final String userName = tokenArray.get(tokenArray.size()-3);
		VCDataIdentifier vcDataIdentifier = new VCDataIdentifier() {
			public String getID(){
				return dataID;
			}
			public User getOwner(){
				return new User(userName, new KeyValue("123345432334"));
			}
		};
		// ------- recover simulation data for this user name, load the images in memory ------------
		String userDirName = filename.substring(0,filename.indexOf(dataID)-1);	// ex  c:\\VirtualMicroscopy\\SimulationData
		File userDir = new File(userDirName);
		SimulationData.SimDataAmplistorInfo simDataAmplistorInfo = AmplistorUtils.getSimDataAmplistorInfoFromPropertyLoader();
		SimulationData simData = new SimulationData(vcDataIdentifier, userDir, null,simDataAmplistorInfo);

		CartesianMesh incompleteMesh = simData.getMesh();	// build a valid mesh in 2 steps, what we have in simData is incomplete
		Extent extent = incompleteMesh.getExtent();
		ISize isize = new ISize(incompleteMesh.getSizeX(), incompleteMesh.getSizeY(), incompleteMesh.getSizeZ());
		Origin origin = new Origin(0,0,0);
		CartesianMesh mesh = CartesianMesh.createSimpleCartesianMesh(origin, extent,isize, 
			new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, 
					isize.getX(),isize.getY(),isize.getZ()),0,null,null,RegionImage.NO_SMOOTHING));

		DataIdentifier[] dataIdentifiers = simData.getVarAndFunctionDataIdentifiers(null);
		double times[] = simData.getDataTimes();
		for (int i=0; i<dataIdentifiers.length; i++){
			// ex: prebleach_avg, postbleach_first, postbleach_last, bleached_mask, cell_mask, ring1_mask,... ring8_mask
			System.out.println(dataIdentifiers[i].getName());
			for (double time : times){			// this loops only once, we have just 1 timepoint for each "special" image
				SimDataBlock simDataBlock = simData.getSimDataBlock(null, dataIdentifiers[i].getName(), time);
				channelNames[i] = dataIdentifiers[i].getName();
				channelTypes[i] = VariableType.VOLUME;
				channelVFrapImageType[i] = SymbolEquivalence.typeFromToken(dataIdentifiers[i].getName());
				pixData[0][i] = simDataBlock.getData();
//				var = prebleach_avg, time = 0.0, data = { 1.0832530361887216 1.0832530361887216 1.0832530361887216 1.0 .... }
				System.out.print("var = " + dataIdentifiers[i].getName() + ", time = " + time + ", data = { ");
				for (int j=0; j<5; j++){ System.out.print(pixData[0][i][j] + " "); }; System.out.println(" ... ");	// show a few
			}
		}
		hashTable.put("mesh",mesh);
		hashTable.put("pixData",pixData);
		hashTable.put("channelNames",channelNames);
		hashTable.put("channelTypes",channelTypes);
		hashTable.put("channelVFrapImageType",channelVFrapImageType);
		return true;
	}
	
	//
	// save the special images in the database as field data
	//
	public static ExternalDataIdentifier SaveVFrapSpecialImagesAsFieldData(Hashtable<String, Object> hashTable, DocumentManager documentManager) throws DataAccessException {
		
		CartesianMesh mesh = (CartesianMesh)hashTable.get("mesh");
		double[][][] pixData = (double[][][])hashTable.get("pixData");
		String[] channelNames = (String[])hashTable.get("channelNames");
		VariableType[] channelTypes = (VariableType[])hashTable.get("channelTypes");
//		DataSymbolType[] channelVFrapImageType = (DataSymbolType[])hashTable.get("channelVFrapImageType");
		String mixedFieldDataName = (String)hashTable.get("mixedFieldDataName");

		FieldDataFileOperationSpec vfrapMiscFieldDataOpSpec = new FieldDataFileOperationSpec();
		vfrapMiscFieldDataOpSpec.opType = FieldDataFileOperationSpec.FDOS_ADD;
		vfrapMiscFieldDataOpSpec.cartesianMesh = mesh;
		vfrapMiscFieldDataOpSpec.doubleSpecData =  pixData;
		vfrapMiscFieldDataOpSpec.specEDI = null;
		vfrapMiscFieldDataOpSpec.varNames = channelNames;				// item name as it comes from vFrap
		vfrapMiscFieldDataOpSpec.owner = documentManager.getUser();
		vfrapMiscFieldDataOpSpec.times = new double[] { 0.0 };
		vfrapMiscFieldDataOpSpec.variableTypes = channelTypes;
		vfrapMiscFieldDataOpSpec.origin = new Origin(0,0,0);
		vfrapMiscFieldDataOpSpec.extent = mesh.getExtent();
		vfrapMiscFieldDataOpSpec.isize = new ISize(mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ());
		ExternalDataIdentifier vfrapMisc = documentManager.saveFieldData(vfrapMiscFieldDataOpSpec, mixedFieldDataName);
		return vfrapMisc;
	}
	
	public static void CreateSaveVFrapDataSymbols(Hashtable<String, Object> hashTable, BioModel bioModel, ExternalDataIdentifier vfrapMisc) {
		String[] channelNames = (String[])hashTable.get("channelNames");
		DataSymbolType[] channelVFrapImageType = (DataSymbolType[])hashTable.get("channelVFrapImageType");
		String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
		
		SimulationContext simContext = bioModel.getSimulationContexts()[0];
		ModelUnitSystem modelUnitSystem = bioModel.getModel().getUnitSystem();
		for (int i=0; i<channelNames.length; i++) {
			// TODO: construct dataSymbolName from vFrapConstants::nameFromToken()
			String dataSymbolName = channelNames[i] + "_" + initialFieldDataName;			// item name postfixed with field data name
			DataSymbol dataSymbol = new FieldDataSymbol(dataSymbolName, channelVFrapImageType[i],
					simContext.getDataContext(), modelUnitSystem.getInstance_TBD(),
					vfrapMisc, channelNames[i], VariableType.VOLUME.getTypeName(), 0D);
			simContext.getDataContext().addDataSymbol(dataSymbol);
		}	
	}
	
	//
	// replace vFrap default names in field function arguments with data symbol names -----
	//
	public static void ReplaceVFrapNamesWithSymbolNames(BioModel bioModel) throws ExpressionException {
		
		SimulationContext simContext = bioModel.getSimulationContexts()[0];
		SpeciesContextSpec[] scsArray = simContext.getReactionContext().getSpeciesContextSpecs();
		for (SpeciesContextSpec scs : scsArray){
			Expression exp = scs.getInitialConditionParameter().getExpression();	// vFrap('a','c',0.0,'volume')
			FieldFunctionArguments[] fieldFunctionArgs = FieldUtilities.getFieldFunctionArguments(exp);
			if (fieldFunctionArgs!=null && fieldFunctionArgs.length>0){
				for (FieldFunctionArguments args : fieldFunctionArgs){
					for (DataSymbol ds : simContext.getDataContext().getDataSymbols()){
						if (ds instanceof FieldDataSymbol){
							FieldDataSymbol fieldDataSymbol = (FieldDataSymbol)ds;
//							String extDataIdentName = fieldDataSymbol.getExternalDataIdentifier().getName();	// name of field data
//							String argsFieldName = args.getFieldName();				// roiData
//							fieldDataSymbol.getFieldDataVarTime() == args.getTime().evaluateConstant()	
							String dataSymbolName = fieldDataSymbol.getName();		// name of data symbol  ex: postbleach_first_ccccF
							String argsVariableName = args.getVariableName();		// name in expression as it comes from vFrap   ex: postbleach_first
							if ( dataSymbolName.startsWith(argsVariableName)) {
								String oldExpression = args.infix();				// vcField('roiData','postbleach_first',0.0,'Volume')
								exp.substituteInPlace(new Expression(oldExpression), new Expression(dataSymbolName));
								exp.bindExpression(simContext);
							}
						}
					}
				}
			}
		}
	}
	
	// load ROI images
	public static void LoadVFrapDisplayRoi(Hashtable<String, Object> hashTable, AnnotatedImageDataset annotatedImages, ROI rois[])
	{
		BufferedImage[] displayROI = null;		// was roiComposite
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
		displayROI = new BufferedImage[annotatedImages.getImageDataset().getSizeZ()];
		for (int i = 0; i < displayROI.length; i++) {
			displayROI[i] = 
				new BufferedImage(annotatedImages.getImageDataset().getISize().getX(), annotatedImages.getImageDataset().getISize().getY(),
					BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
		}
		int xysize = annotatedImages.getImageDataset().getISize().getX()*annotatedImages.getImageDataset().getISize().getY();
		for (int i = 0; i < rois.length; i++) {
			short[] roiBits = rois[i].getBinaryPixelsXYZ(1);
			for (int j = 0; j < roiBits.length; j++) {
				int roiZindex = j/(xysize);
				byte[] roiData = ((DataBufferByte)displayROI[roiZindex].getRaster().getDataBuffer()).getData();
				if(roiBits[j] != 0 && (rois[i].getROIName().equals(AnnotatedImageDataset.VFRAP_ROI_ENUM.ROI_BLEACHED) || roiData[j%xysize] == 0)){
					roiData[j%xysize] = (byte)(i+1);
					//					if(i!= 0){
					//						System.out.println("roi="+i+" j="+j+" j%="+(j%xysize)+" z="+roiZindex+" roidata="+roiData[j%xysize]);
					//					}
				}
			}
		}
		hashTable.put("displayROI", displayROI);
	}

	public static BioModel VFRAPToBioModel(Hashtable<String, Object> hashTable, XMLSource xmlSource, DocumentManager documentManager, final TopLevelWindowManager requester) 
			throws XmlParseException, IOException, DataAccessException, MathException, DivideByZeroException, ExpressionException, ImageException, UtilCancelException {
	
		Component requesterComponent = requester.getComponent();
		File vFrapFile = xmlSource.getXmlFile();
	// ---	
		String vFrapFileNameExtended = vFrapFile.getName();			// ex  ccc8.vfrap
		{	// we want to make sure to reload these strings from the hash later on
		String initialFieldDataName = vFrapFileNameExtended.substring(0, vFrapFileNameExtended.indexOf(".vfrap"));
		String mixedFieldDataName = initialFieldDataName + "Mx";	// we'll save here the "special" vFrap images (prebleach_avg, ...)
		hashTable.put("mixedFieldDataName",mixedFieldDataName);
		hashTable.put("initialFieldDataName",initialFieldDataName);
		}
		if(vFrapFileNameExtended.indexOf(".vfrap") < 0) {
			throw new RuntimeException("File extension must be .vfrap");
		}
	//	VFrapXmlHelper vFrapXmlHelper = new VFrapXmlHelper();
		checkNameAvailability(hashTable, true, documentManager, requesterComponent);
	
		System.out.println("Loading " + vFrapFileNameExtended + " ...");
	    String xmlString = XmlUtil.getXMLString(vFrapFile.getAbsolutePath());
		MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
		Element vFrapRoot = XmlUtil.stringToXML(xmlString, null).getRootElement();
		
		// ----- read the biomodel from Virtual FRAP xml file ----------
		BioModel bioModel = null;
		XmlReader vcellXMLReader = new XmlReader(true);
		Element bioModelElement = vFrapRoot.getChild(XMLTags.BioModelTag);
		if (bioModelElement == null){
			throw new RuntimeException("Unable to load biomodel.");
		}
		String docSoftwareVersion = vFrapRoot.getAttributeValue(XMLTags.SoftwareVersionAttrTag);
		bioModel = vcellXMLReader.getBioModel(bioModelElement,(docSoftwareVersion==null?null:VCellSoftwareVersion.fromString(docSoftwareVersion)));
		
		// ------ locate the special images within the vFrap files and load them in memory
		if(!LoadVFrapSpecialImages(hashTable, vFrapRoot)) {
			return bioModel;	// just return the biomodel if image loading fails for some reason
		}
	
		// ------- save the special images in the database as field data ------------
		ExternalDataIdentifier vfrapMisc = SaveVFrapSpecialImagesAsFieldData(hashTable, documentManager);
		
		// ------- create and save data symbols for the vFrap "special" images -----------
		CreateSaveVFrapDataSymbols(hashTable, bioModel, vfrapMisc);
		
		// -------- replace vFrap default names in field function arguments with data symbol names -----
		ReplaceVFrapNamesWithSymbolNames(bioModel);
		return bioModel;
	}
}
	
	
	
