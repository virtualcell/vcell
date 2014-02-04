package org.vcell.vis.vcell;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.vcell.util.ISize;
import org.vcell.vis.core.Box3D;
import org.vcell.vis.core.Vect3D;
import org.vcell.vis.vcell.SubdomainInfo.CompartmentSubdomainInfo;
import org.vcell.vis.vcell.SubdomainInfo.MembraneSubdomainInfo;

public class CartesianMesh {

	private final String version;
	private final SubdomainInfo subdomainInfo;
	private final MembraneElement[] membraneElements;
	private final ContourElement[] contourElements;
	private final MeshRegionInfo meshRegionInfo;
	private final ISize size;
	private final Vect3D extent;
	private final Vect3D origin;
	private final int dimension;
	
	public CartesianMesh(String version, SubdomainInfo subdomainInfo,
			MembraneElement[] membraneElements,
			ContourElement[] contourElements, MeshRegionInfo meshRegionInfo,
			ISize size, Vect3D extent, Vect3D origin, int dimension) {
		super();
		this.version = version;
		this.subdomainInfo = subdomainInfo;
		this.membraneElements = membraneElements;
		this.contourElements = contourElements;
		this.meshRegionInfo = meshRegionInfo;
		this.size = size;
		this.extent = extent;
		this.origin = origin;
		this.dimension = dimension;
	}

	public Box3D getVolumeElementBox(int i, int j, int k) {
		return new Box3D(getOrigin().x + i*getExtent().x/getSize().getX(), getOrigin().y + j*getExtent().y/getSize().getY(), getOrigin().z + k*getExtent().z/getSize().getZ(), 
						 getOrigin().x + (i+1)*getExtent().x/getSize().getX(), getOrigin().y + (j+1)*getExtent().y/getSize().getY(), getOrigin().z + (k+1)*getExtent().z/getSize().getZ());
	}

	public int getDimension() {
		return dimension;
	}

	public Vect3D getExtent() {
		return extent;
	}

	public Vect3D getOrigin() {
		return origin;
	}
	
	public List<Integer> getVolumeRegionIDs(String domainName){
		return meshRegionInfo.getVolumeRegionIDs(domainName);
	}
	
	public int getVolumeRegionIndex(int volumeIndex){
		return meshRegionInfo.getVolumeElementMapVolumeRegion(volumeIndex);
	}

	public ISize getSize() {
		return size;
	}

	public int getNumVolumeRegions() {
		return meshRegionInfo.getNumVolumeRegions();
	}
	
	public int getNumMembraneRegions() {
		return meshRegionInfo.getNumMembraneRegions();
	}
	
	public List<String> getVolumeDomainNames() {
		return meshRegionInfo.getVolumeDomainNames();
	}
	
	public List<String> getMembraneDomainNames() {
		return subdomainInfo.getMembraneDomainNames();
	}

	public List<MembraneElement> getMembraneElements(String domainName) {
		// get set of membrane regions that are part of domain
		MembraneSubdomainInfo membraneSubdomainInfo = subdomainInfo.getMembraneSubdomainInfo(domainName);
		CompartmentSubdomainInfo insideCompartmentInfo = membraneSubdomainInfo.getInsideCompartmentSubdomainInfo();
		CompartmentSubdomainInfo outsideCompartmentInfo = membraneSubdomainInfo.getOutsideCompartmentSubdomainInfo();
		
		List<Integer> insideVolumeRegionIds = getVolumeRegionIDs(insideCompartmentInfo.getName());
		List<Integer> outsideVolumeRegionIds = getVolumeRegionIDs(outsideCompartmentInfo.getName());
		
		// find set of all volume elements which are "inside"
		BitSet insideVolumeSet = new BitSet();
		for (int insideVolumeRegionId : insideVolumeRegionIds){
			BitSet volRegionBitset = meshRegionInfo.getVolumeROIFromVolumeRegionID(insideVolumeRegionId);
			insideVolumeSet.or(volRegionBitset);
		}
		
		// find set of all volume elements which are "outside"
		BitSet outsideVolumeSet = new BitSet();
		for (int outsideVolumeRegionId : outsideVolumeRegionIds){
			BitSet volRegionBitset = meshRegionInfo.getVolumeROIFromVolumeRegionID(outsideVolumeRegionId);
			outsideVolumeSet.or(volRegionBitset);
		}

		// find all membrane elements which touch the volume elements in the "inside" and "outside" set for this membrane domain.
		ArrayList<MembraneElement> domainMembraneElements = new ArrayList<MembraneElement>();
		for (MembraneElement membraneElement : membraneElements){
			int insideVolumeIndex = membraneElement.getInsideVolumeIndex();
			if (insideVolumeSet.get(insideVolumeIndex)){
				int outsideVolumeIndex = membraneElement.getOutsideVolumeIndex();
				if (outsideVolumeSet.get(outsideVolumeIndex)){
					domainMembraneElements.add(membraneElement);
				}
			}
		}
		return domainMembraneElements;
	}

	public int getMembraneRegionIndex(int membraneIndex){
		return meshRegionInfo.getMembraneRegionForMembraneElement(membraneIndex);
	}

}
