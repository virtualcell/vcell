package cbit.vcell.mapping.spatial;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.SimulationContext;

/**
 * VolumeObjects will be used to specify the velocity of the volume.
 * Multiple VolumeObjects can be used to specify multiple phases.
 * 
 * @author schaff
 *
 */
public class VolumeRegionObject extends SpatialObject {
	private SubVolume subVolume;
	private Integer regionID;
	
	private SpatialQuantity centroidX = new SpatialQuantity(QuantityCategory.Centroid,QuantityComponent.X);
	private SpatialQuantity centroidY = new SpatialQuantity(QuantityCategory.Centroid,QuantityComponent.Y);
	private SpatialQuantity centroidZ = new SpatialQuantity(QuantityCategory.Centroid,QuantityComponent.Z);
	private SpatialQuantity size = new SpatialQuantity(QuantityCategory.VolumeSize,QuantityComponent.Scalar);


	public VolumeRegionObject(VolumeRegionObject argVolumeObject, SimulationContext argSimContext) {
		super(argVolumeObject, argSimContext);
		this.setSubVolume(argSimContext.getGeometry().getGeometrySpec().getSubVolume(argVolumeObject.getSubVolume().getName()));
		this.setRegionID(argVolumeObject.getRegionID());
	}

	public VolumeRegionObject(String name, SubVolume subVolume, Integer regionID, SimulationContext simContext) {
		super(name, simContext,
				new QuantityCategory[] {
					QuantityCategory.Centroid,
					QuantityCategory.VolumeSize},
				new Boolean[] {
					new Boolean(false),
					new Boolean(true)}
		);

		this.setSubVolume(subVolume);
		this.setRegionID(regionID);
	}

	public SubVolume getSubVolume() {
		return subVolume;
	}

	public void setSubVolume(SubVolume subVolume) {
		this.subVolume = subVolume;
	}

	public Integer getRegionID() {
		return regionID;
	}

	public void setRegionID(Integer regionID) {
		this.regionID = regionID;
	}
	
	public VolumeGeometricRegion getVolumeRegion(Geometry geometry){
		GeometricRegion[] regions = geometry.getGeometrySurfaceDescription().getGeometricRegions();
		for (GeometricRegion region : regions){
			if (region instanceof VolumeGeometricRegion){
				VolumeGeometricRegion volumeGeometricRegion = (VolumeGeometricRegion) region;
				if (volumeGeometricRegion.getSubVolume() == subVolume && volumeGeometricRegion.getRegionID() == regionID){
					return volumeGeometricRegion;
				}
			}
		}
		return null;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof VolumeRegionObject){
			VolumeRegionObject other = (VolumeRegionObject) obj;
			if (!compareEqual0(obj)){
				return false;
			}
			if (!Compare.isEqualOrNull(subVolume, other.subVolume)){
				return false;
			}
			if (!Compare.isEqualOrNull(regionID, other.regionID)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return "Volume Object for "+getSubVolume().getName()+"["+getRegionID()+"]";
	}
	
	@Override
	public SpatialQuantity[] getSpatialQuantities() {
		return new SpatialQuantity[] { centroidX, centroidY, centroidZ, size };
	}
	
}
