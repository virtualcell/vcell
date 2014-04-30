package org.vcell.multicellular;

import java.util.ArrayList;

import org.sbml.libsbml.CSGObject;
import org.sbml.libsbml.CSGPrimitive;
import org.sbml.libsbml.CSGScale;
import org.sbml.libsbml.CSGTranslation;
import org.sbml.libsbml.Domain;
import org.sbml.libsbml.DomainType;
import org.sbml.libsbml.Geometry;
import org.sbml.libsbml.InteriorPoint;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLDocumentPlugin;
import org.sbml.libsbml.SBMLNamespaces;
import org.sbml.libsbml.SBMLWriter;
import org.vcell.sbml.SBMLUtils;
import org.vcell.util.TokenMangler;

public class MulticellularModel {
	
	private SBMLDocument sbmlDocument = null;
	public final Model sbmlModel;
	private final ArrayList<CellModel> cellModels = new ArrayList<CellModel>();
	public final World world;
	
	public MulticellularModel(String modelName){

		// SBMLNamespaces of SBML Level 3 Version 1 with Spatial Version 1
		SBMLNamespaces sbmlns = new SBMLNamespaces(3,1,SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX,1);
		sbmlns.addPkgNamespace(SBMLUtils.SBML_SPATIAL_NS_PREFIX,1);
			
		// create the L3V1 document with spatial package
		sbmlDocument = new SBMLDocument(sbmlns);
			
	    // set 'required' attribute on document for 'spatial' and 'req' packages to 'T'??
		SBMLDocumentPlugin dplugin = (SBMLDocumentPlugin)sbmlDocument.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
		dplugin.setRequired(true);
		dplugin = (SBMLDocumentPlugin)sbmlDocument.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		dplugin.setRequired(true);
			
		sbmlModel = sbmlDocument.createModel(TokenMangler.mangleToSName(modelName));
		sbmlModel.setName(modelName);

		this.world = new World(sbmlModel);
	}
	
	public void addBackgroundDomain(){
		DomainType backgroundDomainType = world.geometry.createDomainType();
		backgroundDomainType.setSpatialDimensions(3);
		backgroundDomainType.setSpatialId("background_domainType");
		backgroundDomainType.setId("background_domainType");
		Domain backgroundDomain = world.geometry.createDomain();
		backgroundDomain.setDomainType(backgroundDomainType.getSpatialId());
		backgroundDomain.setSpatialId("background_domain");
		backgroundDomain.setId("background_domain");
		InteriorPoint interiorPoint = backgroundDomain.createInteriorPoint();
		interiorPoint.setCoord1(0.01);
		interiorPoint.setCoord2(0.01);
		interiorPoint.setCoord3(0.01);

		CSGObject backgroundObject = world.csGeometry.createCSGObject();
		backgroundObject.setOrdinal(cellModels.size());
		CSGTranslation translation = backgroundObject.createCSGTranslation();
		translation.setTranslateX((world.xmin+world.xmax)/2.0);
		translation.setTranslateY((world.ymin+world.ymax)/2.0);
		translation.setTranslateZ((world.zmin+world.zmax)/2.0);
		CSGScale scale = translation.createCSGScale();
		scale.setScaleX(world.xmax-world.xmin);
		scale.setScaleY(world.ymax-world.ymin);
		scale.setScaleZ(world.zmax-world.zmin);
		CSGPrimitive cube = scale.createCSGPrimitive();
		cube.setPrimitiveType("SOLID_CUBE");
		backgroundObject.setDomainType(backgroundDomainType.getSpatialId());
		backgroundObject.setSpatialId("CSGObject_background"); // this is wrong
	}
	

	
	public SBMLDocument getSBMLDocument(){
		return sbmlDocument;
	}
	
	public CellModel createCellModel(String cellModelName){
		DomainType domainType = world.geometry.createDomainType();
		domainType.setId(cellModelName);
		domainType.setSpatialId(cellModelName);
		domainType.setSpatialDimensions(3);
		CellModel cellModel = new CellModel(sbmlModel,domainType,cellModels.size());
		cellModels.add(cellModel);
		return cellModel;
	}
	
	public String getSBMLText(){
		SBMLWriter sbmlWriter = new SBMLWriter();
		String sbmlStr = sbmlWriter.writeToString(sbmlDocument);
		sbmlWriter.delete();
		return sbmlStr;
	}

	public void close() {
		sbmlModel.delete();
		sbmlDocument.delete();
	}

	public void writeSBML(String filename) {
		SBMLWriter sbmlWriter = new SBMLWriter();
		sbmlWriter.writeSBMLToFile(sbmlDocument, filename);
	}

}
