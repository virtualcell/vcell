package cbit.vcell.mapping.spatial;

import java.beans.PropertyVetoException;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.Issue.IssueCategory;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.OutputFunctionContext.OutputFunctionIssueSource;

public class SurfaceRegionObject extends SpatialObject {
	
	private SubVolume 	insideSubVolume;
	private Integer 	insideRegionID;
	private SubVolume 	outsideSubVolume;
	private Integer		outsideRegionID;

	private SpatialQuantity normalX = new SpatialQuantity(QuantityCategory.Normal, QuantityComponent.X);
	private SpatialQuantity normalY = new SpatialQuantity(QuantityCategory.Normal, QuantityComponent.Y);
	private SpatialQuantity normalZ = new SpatialQuantity(QuantityCategory.Normal, QuantityComponent.Z);
	private SpatialQuantity velocityX = new SpatialQuantity(QuantityCategory.SurfaceVelocity, QuantityComponent.X);
	private SpatialQuantity velocityY = new SpatialQuantity(QuantityCategory.SurfaceVelocity, QuantityComponent.Y);
	private SpatialQuantity velocityZ = new SpatialQuantity(QuantityCategory.SurfaceVelocity, QuantityComponent.Z);
	private SpatialQuantity distanceTo = new SpatialQuantity(QuantityCategory.DistanceToSurface, QuantityComponent.Scalar);
	private SpatialQuantity directionToX = new SpatialQuantity(QuantityCategory.DirectionToSurface, QuantityComponent.X);
	private SpatialQuantity directionToY = new SpatialQuantity(QuantityCategory.DirectionToSurface, QuantityComponent.Y);
	private SpatialQuantity directionToZ = new SpatialQuantity(QuantityCategory.DirectionToSurface, QuantityComponent.Z);
	private SpatialQuantity size = new SpatialQuantity(QuantityCategory.SurfaceSize, QuantityComponent.Scalar);

	public SurfaceRegionObject(SurfaceRegionObject argSurfaceObject, SimulationContext argSimContext) {
		super(argSurfaceObject, argSimContext);
		this.insideSubVolume = argSimContext.getGeometry().getGeometrySpec().getSubVolume(argSurfaceObject.getInsideSubVolume().getName());
		this.outsideSubVolume = argSimContext.getGeometry().getGeometrySpec().getSubVolume(argSurfaceObject.getOutsideSubVolume().getName());
		this.insideRegionID = argSurfaceObject.getInsideRegionID();
		this.outsideRegionID = argSurfaceObject.getOutsideRegionID();
	}

	public SurfaceRegionObject(SubVolume insideSubVolume, Integer insideRegionID, SubVolume outsideSubVolume, Integer outsideRegionID, SimulationContext simContext){
		this(getCannonicalName(insideSubVolume,insideRegionID,outsideSubVolume,outsideRegionID),insideSubVolume,insideRegionID,outsideSubVolume,outsideRegionID,simContext);
	}

	public static String getCannonicalName(SubVolume insideSubVolume, Integer insideRegionID, SubVolume outsideSubVolume, Integer outsideRegionID){
		String newName = "sobj_"+insideSubVolume.getName()+insideRegionID+"_"+outsideSubVolume.getName()+outsideRegionID;
		return newName;
	}
	
	@Override
	public void refreshName() throws PropertyVetoException {
		String newName = getCannonicalName(insideSubVolume, insideRegionID, outsideSubVolume, outsideRegionID);
		if (!newName.equals(getName())){
			setName(newName);
			for (SpatialQuantity spatialQuantity : getSpatialQuantities()){
				firePropertyChange(PROPERTY_NAME_NAME, null, spatialQuantity.getName());
			}
		}		
	}

	
	public SurfaceRegionObject(String name, 
			SubVolume insideSubVolume, Integer insideRegionID, 
			SubVolume outsideSubVolume, Integer outsideRegionID, 
			SimulationContext simContext) {
		super(name, simContext,
				new QuantityCategory[] {
					QuantityCategory.Normal,
					QuantityCategory.SurfaceVelocity,
					QuantityCategory.DistanceToSurface,
					QuantityCategory.DirectionToSurface,
					QuantityCategory.SurfaceSize},
				new Boolean[] {
					new Boolean(false),
					new Boolean(false),
					new Boolean(false),
					new Boolean(false),
					new Boolean(true)}
		);
		
		this.insideSubVolume = insideSubVolume;
		this.insideRegionID = insideRegionID;
		this.outsideSubVolume = outsideSubVolume;
		this.outsideRegionID = outsideRegionID;
	}

	public SubVolume getInsideSubVolume() {
		return insideSubVolume;
	}

	public void setInsideSubVolume(SubVolume insideSubVolume) {
		this.insideSubVolume = insideSubVolume;
	}
	
	public SubVolume getOutsideSubVolume() {
		return outsideSubVolume;
	}

	public void setOutsideSubVolume(SubVolume outsideSubVolume) {
		this.outsideSubVolume = outsideSubVolume;
	}
	
	public Integer getInsideRegionID() {
		return insideRegionID;
	}

	public void setInsideRegionID(Integer insideRegionID) {
		this.insideRegionID = insideRegionID;
	}

	public Integer getOutsideRegionID() {
		return outsideRegionID;
	}

	public void setOutsideRegionID(Integer outsideRegionID) {
		this.outsideRegionID = outsideRegionID;
	}

	public SurfaceGeometricRegion getSurfaceRegion(Geometry geometry){
		GeometricRegion[] regions = geometry.getGeometrySurfaceDescription().getGeometricRegions();
		for (GeometricRegion region : regions){
			if (region instanceof SurfaceGeometricRegion){
				SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)region;
				GeometricRegion[] adjacentRegions = surfaceRegion.getAdjacentGeometricRegions();
				if (adjacentRegions.length == 2 && adjacentRegions[0] instanceof VolumeGeometricRegion && adjacentRegions[1] instanceof VolumeGeometricRegion){
					VolumeGeometricRegion adjVolumeRegion0 = (VolumeGeometricRegion) adjacentRegions[0];
					VolumeGeometricRegion adjVolumeRegion1 = (VolumeGeometricRegion) adjacentRegions[1];
					// match adjacent vol0 with inside and vol1 with outside
					if (adjVolumeRegion0.getSubVolume() == insideSubVolume && 
						adjVolumeRegion0.getRegionID() == insideRegionID &&
						adjVolumeRegion1.getSubVolume() == outsideSubVolume && 
						adjVolumeRegion1.getRegionID() == outsideRegionID) {
						return surfaceRegion;
					}
					// match adjacent vol1 with inside and vol0 with outside
					if (adjVolumeRegion1.getSubVolume() == insideSubVolume && 
						adjVolumeRegion1.getRegionID() == insideRegionID &&
						adjVolumeRegion0.getSubVolume() == outsideSubVolume && 
						adjVolumeRegion0.getRegionID() == outsideRegionID) {
						return surfaceRegion;
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof SurfaceRegionObject){
			SurfaceRegionObject other = (SurfaceRegionObject)obj;
			if (!compareEqual0(other)){
				return false;
			}
			if (!Compare.isEqualOrNull(insideSubVolume, other.insideSubVolume)){
				return false;
			}
			if (!Compare.isEqualOrNull(insideRegionID, other.insideRegionID)){
				return false;
			}
			if (!Compare.isEqualOrNull(outsideRegionID, other.outsideRegionID)){
				return false;
			}
			if (!Compare.isEqualOrNull(outsideRegionID, other.outsideRegionID)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return "Surface Object between "+insideSubVolume.getName()+"["+insideRegionID+"] and "+outsideSubVolume.getName()+"["+outsideRegionID+"]";
	}


	@Override
	public SpatialQuantity[] getSpatialQuantities() {
		return new SpatialQuantity[] { normalX, normalY, normalZ, size, velocityX, velocityY, velocityZ, distanceTo, directionToX, directionToY, directionToZ };
	}

	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
//		if (simulationContext.getGeometry().getGeometrySurfaceDescription() != null){
//			GeometricRegion[] regions = simulationContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions();
//			boolean bFound = false;
//			for (GeometricRegion region : regions){
//				if (region instanceof SurfaceGeometricRegion){
//					SurfaceGeometricRegion sr = (SurfaceGeometricRegion)region;
//					if (sr.getAdjacentGeometricRegions()!=null && sr.getAdjacentGeometricRegions().length==2){
//						VolumeGeometricRegion vr1 = (VolumeGeometricRegion)sr.getAdjacentGeometricRegions()[0];
//						VolumeGeometricRegion vr2 = (VolumeGeometricRegion)sr.getAdjacentGeometricRegions()[1];
//						if (vr1.getSubVolume()==insideSubVolume && vr1.getRegionID()==insideRegionID && vr2.getSubVolume()==outsideSubVolume && vr2.getRegionID()==outsideRegionID){
//							bFound = true;
//						}
//						if (vr1.getSubVolume()==outsideSubVolume && vr1.getRegionID()==outsideRegionID && vr2.getSubVolume()==insideSubVolume && vr2.getRegionID()==insideRegionID){
//							bFound = true;
//						}
//					}
//				}
//			}
//			if (!bFound){
//				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "could not find corresponding surface region in geometry", Issue.Severity.ERROR));
//			}
//		}
	}

}
