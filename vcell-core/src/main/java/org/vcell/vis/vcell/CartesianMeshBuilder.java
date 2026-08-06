package org.vcell.vis.vcell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import org.vcell.vis.core.Vect3D;

import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.SubDomain;

/**
 * Builds the visualization mesh in memory instead of reading the simulation's files.
 * <p>
 * {@link org.vcell.vis.io.CartesianMeshFileReader} needs the {@code .mesh} and {@code .subdomains}
 * files, which only exist where the simulation ran. A remote run is reachable only through the
 * {@code DataSetController} interface, which hands back a {@link cbit.vcell.solvers.CartesianMesh}
 * and never a file path. Converting that mesh here lets one visualization path serve local and
 * remote runs alike.
 * <p>
 * Volume domains only: membrane and contour elements come back empty, because the solver mesh
 * does not carry them and the volume-field pipeline never asks for them.
 */
public class CartesianMeshBuilder {

	private CartesianMeshBuilder() {
	}

	/**
	 * @param solverMesh the mesh as {@code DataSetController.getMesh} returns it
	 * @param subdomainInfo domain names against subvolume handles. The solver mesh knows subvolume
	 *        numbers but not names, so it cannot supply this on its own.
	 */
	public static CartesianMesh fromSolverMesh(cbit.vcell.solvers.CartesianMesh solverMesh,
			SubdomainInfo subdomainInfo) throws IOException, MathException {
		MeshRegionInfo meshRegionInfo = new MeshRegionInfo();
		for (Map.Entry<Integer, Integer> regionToSubvolume : solverMesh.getVolumeRegionMapSubvolume().entrySet()) {
			int volumeRegionID = regionToSubvolume.getKey();
			int subvolumeID = regionToSubvolume.getValue();
			// the per-region volume is recorded but never read back by the volume-domain mapping
			meshRegionInfo.mapVolumeRegionToSubvolume(volumeRegionID, subvolumeID, 0.0,
					subdomainInfo.getCompartmentSubdomainName(subvolumeID));
		}
		int numVolumeElements = solverMesh.getNumVolumeElements();
		meshRegionInfo.setCompressedVolumeElementMapVolumeRegion(
				compressedRegionIndices(solverMesh, numVolumeElements), numVolumeElements);

		return new CartesianMesh(
				"in-memory",
				subdomainInfo,
				new MembraneElement[0],
				new ContourElement[0],
				meshRegionInfo,
				solverMesh.getISize(),
				new Vect3D(solverMesh.getExtent().getX(), solverMesh.getExtent().getY(), solverMesh.getExtent().getZ()),
				new Vect3D(solverMesh.getOrigin().getX(), solverMesh.getOrigin().getY(), solverMesh.getOrigin().getZ()),
				solverMesh.getGeometryDimension());
	}

	/**
	 * The domain naming the solver mesh lacks, taken from the math description the client already
	 * holds. Mirrors what {@link SubdomainInfo#write} records in the {@code .subdomains} file.
	 */
	public static SubdomainInfo fromMathDescription(MathDescription mathDesc) throws MathException {
		List<SubdomainInfo.CompartmentSubdomainInfo> compartments = new ArrayList<>();
		List<SubdomainInfo.MembraneSubdomainInfo> membranes = new ArrayList<>();
		Enumeration<SubDomain> subDomains = mathDesc.getSubDomains();
		while (subDomains.hasMoreElements()) {
			SubDomain subDomain = subDomains.nextElement();
			if (subDomain instanceof CompartmentSubDomain) {
				CompartmentSubDomain compartment = (CompartmentSubDomain) subDomain;
				compartments.add(new SubdomainInfo.CompartmentSubdomainInfo(
						compartment.getName(), mathDesc.getHandle(compartment)));
			} else if (subDomain instanceof MembraneSubDomain) {
				MembraneSubDomain membrane = (MembraneSubDomain) subDomain;
				CompartmentSubDomain inside = membrane.getInsideCompartment();
				CompartmentSubDomain outside = membrane.getOutsideCompartment();
				membranes.add(new SubdomainInfo.MembraneSubdomainInfo(
						membrane.getName(),
						new SubdomainInfo.CompartmentSubdomainInfo(inside.getName(), mathDesc.getHandle(inside)),
						new SubdomainInfo.CompartmentSubdomainInfo(outside.getName(), mathDesc.getHandle(outside))));
			}
		}
		return new SubdomainInfo(
				compartments.toArray(new SubdomainInfo.CompartmentSubdomainInfo[0]),
				membranes.toArray(new SubdomainInfo.MembraneSubdomainInfo[0]));
	}

	/**
	 * {@link MeshRegionInfo} only accepts this map in the compressed form the {@code .mesh} file
	 * stores. Always written as unsigned shorts: the reader chooses its decoding from the array
	 * length, so a fixed two bytes per element keeps that unambiguous at any region count.
	 */
	private static byte[] compressedRegionIndices(cbit.vcell.solvers.CartesianMesh solverMesh,
			int numVolumeElements) throws IOException {
		byte[] regionIndices = new byte[2 * numVolumeElements];
		for (int volumeIndex = 0; volumeIndex < numVolumeElements; volumeIndex++) {
			int regionIndex = solverMesh.getVolumeRegionIndex(volumeIndex);
			regionIndices[2 * volumeIndex] = (byte) (regionIndex & 0xff);
			regionIndices[2 * volumeIndex + 1] = (byte) ((regionIndex >> 8) & 0xff);
		}
		ByteArrayOutputStream compressed = new ByteArrayOutputStream();
		try (DeflaterOutputStream deflater = new DeflaterOutputStream(compressed)) {
			deflater.write(regionIndices);
		}
		return compressed.toByteArray();
	}
}
