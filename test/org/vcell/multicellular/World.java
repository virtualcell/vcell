package org.vcell.multicellular;

import java.util.ArrayList;
import java.util.List;

import org.sbml.libsbml.AdjacentDomains;
import org.sbml.libsbml.BoundaryMax;
import org.sbml.libsbml.BoundaryMin;
import org.sbml.libsbml.CSGObject;
import org.sbml.libsbml.CSGPrimitive;
import org.sbml.libsbml.CSGScale;
import org.sbml.libsbml.CSGTranslation;
import org.sbml.libsbml.CSGeometry;
import org.sbml.libsbml.CoordinateComponent;
import org.sbml.libsbml.Domain;
import org.sbml.libsbml.DomainType;
import org.sbml.libsbml.Geometry;
import org.sbml.libsbml.InteriorPoint;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.Parameter;
import org.sbml.libsbml.RequiredElementsSBasePlugin;
import org.sbml.libsbml.SBase;
import org.sbml.libsbml.SBasePlugin;
import org.sbml.libsbml.SpatialModelPlugin;
import org.sbml.libsbml.SpatialParameterPlugin;
import org.sbml.libsbml.SpatialSymbolReference;
import org.vcell.sbml.SBMLUtils;

public class World {
	public final double xmin = 0;
	public final double ymin = 0;
	public final double zmin = 0;
	public final double xmax = 20;
	public final double ymax = 20;
	public final double zmax = 20;
	public final Geometry geometry;
	public final CSGeometry csGeometry;
	public final Model model;
	public ArrayList<CellInstance> cellInstances = new ArrayList<CellInstance>();
	
	public World(Model model){
		this.model = model;
		
		SBasePlugin plugin = model.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
	    SpatialModelPlugin mplugin = (SpatialModelPlugin)plugin;

	    // Creates a geometry object via SpatialModelPlugin object.
	    geometry = mplugin.getGeometry();
		geometry.setCoordinateSystem("Cartesian");
		
		// add x coordinate component
		CoordinateComponent xComp = geometry.createCoordinateComponent();
		xComp.setSpatialId("x");
		xComp.setComponentType("cartesianX");
		xComp.setSbmlUnit("um");
		xComp.setIndex(0);
		BoundaryMin minX = xComp.createBoundaryMin();
		minX.setSpatialId("Xmin");
		minX.setValue(xmin);
		BoundaryMax maxX = xComp.createBoundaryMax();
		maxX.setSpatialId("Xmax");
		maxX.setValue(xmax);
	
		createParamForSpatialElement(xComp, xComp.getSpatialId());
		
		CoordinateComponent yComp = geometry.createCoordinateComponent();
		yComp.setSpatialId("y");
		yComp.setComponentType("cartesianY");
		yComp.setSbmlUnit("um");
		yComp.setIndex(1);
		BoundaryMin minY = yComp.createBoundaryMin();
		minY.setSpatialId("Ymin");
		minY.setValue(ymin);
		BoundaryMax maxY = yComp.createBoundaryMax();
		maxY.setSpatialId("Ymax");
		maxY.setValue(ymax);
		
		createParamForSpatialElement(yComp, yComp.getSpatialId());

		CoordinateComponent zComp = geometry.createCoordinateComponent();
		zComp.setSpatialId("z");
		zComp.setComponentType("cartesianZ");
		zComp.setSbmlUnit("um");
		zComp.setIndex(2);
		BoundaryMin minZ = zComp.createBoundaryMin();
		minZ.setSpatialId("Zmin");
		minZ.setValue(zmin);
		BoundaryMax maxZ = zComp.createBoundaryMax();
		maxZ.setSpatialId("Zmax");
		maxZ.setValue(zmax);

		createParamForSpatialElement(zComp, zComp.getSpatialId());
		
		csGeometry = geometry.createCSGeometry();
		
	}
	
	public CellInstance createCellInstance(CellModel cellModel, String name, double x, double y, double z, double radius){
		Domain domain = geometry.createDomain();
		domain.setDomainType(cellModel.domainType.getSpatialId());
		domain.setId(name);
		domain.setSpatialId(name);
		InteriorPoint interiorPoint = domain.createInteriorPoint();
		interiorPoint.setCoord1(x);
		interiorPoint.setCoord2(y);
		interiorPoint.setCoord3(z);
		CSGObject csgObject = csGeometry.createCSGObject();
		csgObject.setOrdinal(cellModel.ordinal);
		CSGTranslation translation = csgObject.createCSGTranslation();
		translation.setTranslateX(x);
		translation.setTranslateY(y);
		translation.setTranslateZ(z);
		CSGScale scale = translation.createCSGScale();
		scale.setScaleX(radius);
		scale.setScaleY(radius);
		scale.setScaleZ(radius);
		CSGPrimitive sphere = scale.createCSGPrimitive();
		sphere.setPrimitiveType("SOLID_SPHERE");
		csgObject.setDomainType(cellModel.domainType.getSpatialId());
		csgObject.setSpatialId("CSGObject_"+name); // this is wrong
		CellInstance cellInstance = new CellInstance(this, cellModel, domain);
		cellInstances.add(cellInstance);
		return cellInstance;
	}
	
	private void createParamForSpatialElement(SBase sBaseElement, String spatialId) {
		org.sbml.libsbml.Parameter p = model.createParameter();
		if (sBaseElement instanceof CoordinateComponent) {
			CoordinateComponent cc = (CoordinateComponent)sBaseElement;
			// coordComponent with index = 1 represents X-axis, hence set param id as 'x'
			if (cc.getIndex() == 0) {
				p.setId("x");
			} else if (cc.getIndex() == 1) {
				p.setId("y");
			} else if (cc.getIndex() == 2) {
				p.setId("z");
			}
		} else {
			p.setId(spatialId);
		}
		p.setValue(0.0);
		// since p is a parameter from 'spatial' package, need to set the
		// requiredElements attributes on parameter 
		SBasePlugin plugin = p.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
		RequiredElementsSBasePlugin reqPlugin = (RequiredElementsSBasePlugin)plugin;
		reqPlugin.setMathOverridden(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		reqPlugin.setCoreHasAlternateMath(false);
		// now need to create a SpatialSymbolReference for parameter
		plugin = p.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		SpatialParameterPlugin spPlugin = (SpatialParameterPlugin)plugin;
		SpatialSymbolReference spSymRef = spPlugin.getSpatialSymbolReference();
		spSymRef.setSpatialId(spatialId);
		spSymRef.setType(sBaseElement.getElementName());
	}


	public boolean isAdjacent(CellInstance cell1, CellInstance cell2){
		for (int i = 0; i < geometry.getNumAdjacentDomains(); i++){
			AdjacentDomains adjacentDomains = geometry.getAdjacentDomains(i);
			if (adjacentDomains.getDomain1().equals(cell1.domain.getId()) && adjacentDomains.getDomain2().equals(cell2.domain.getId())){
				return true;
			}
			if (adjacentDomains.getDomain2().equals(cell1.domain.getId()) && adjacentDomains.getDomain1().equals(cell2.domain.getId())){
				return true;
			}
		}
		return false;
	}
	
	private AdjacentDomains getAdjacentDomains(CellInstance cell1, CellInstance cell2){
		for (int i = 0; i < geometry.getNumAdjacentDomains(); i++){
			AdjacentDomains adjacentDomains = geometry.getAdjacentDomains(i);
			if (adjacentDomains.getDomain1().equals(cell1.domain.getId()) && adjacentDomains.getDomain2().equals(cell2.domain.getId())){
				return adjacentDomains;
			}
			if (adjacentDomains.getDomain2().equals(cell1.domain.getId()) && adjacentDomains.getDomain1().equals(cell2.domain.getId())){
				return adjacentDomains;
			}
		}
		return null;
	}
	
	public List<CellInstance> getAdjacentCellInstances(CellInstance cell){
		ArrayList<CellInstance> adjacentCells = new ArrayList<CellInstance>();
		for (int i = 0; i < geometry.getNumAdjacentDomains(); i++){
			AdjacentDomains adjacentDomains = geometry.getAdjacentDomains(i);
			if (adjacentDomains.getDomain1().equals(cell.domain.getId())){
				adjacentCells.add(getCellInstance(adjacentDomains.getDomain2()));
			}
			if (adjacentDomains.getDomain2().equals(cell.domain.getId())){
				adjacentCells.add(getCellInstance(adjacentDomains.getDomain1()));
			}
		}
		return adjacentCells;
	}
	
	public CellInstance getCellInstance(String domainName){
		for (CellInstance cell : cellInstances){
			if (cell.domain.getId().equals(domainName)){
				return cell;
			}
		}
		return null;
	}
	
	public void setAdjacent(CellInstance cell1, CellInstance cell2, boolean bAdjacent){
		AdjacentDomains existingAdjacentDomains = getAdjacentDomains(cell1, cell2);
		if (bAdjacent && existingAdjacentDomains==null){
			AdjacentDomains adjacentDomains = geometry.createAdjacentDomains();
			adjacentDomains.setDomain1(cell1.domain.getId());
			adjacentDomains.setDomain2(cell2.domain.getId());
		}else if (!bAdjacent && existingAdjacentDomains!=null){
			geometry.removeAdjacentDomains(existingAdjacentDomains.getId());
		}
	}

}