package org.vcell.data;

import java.util.Set;

/**
 * Strategy for what a {@link Dataset}'s spatial dimensions mean geometrically: which dimensions are index spaces of
 * places, and (in implementations) how a position maps to coordinates, regions and neighbours.
 * <p>
 * Nonspatial results use {@link #NONSPATIAL}. Mesh-based results get a topology per mesh kind (Cartesian,
 * Chombo AMR, moving boundary, unstructured), and particle results a point-cloud topology.
 */
public interface Topology {

	Topology NONSPATIAL = new Nonspatial();

	/** @return a short name for the kind of topology, for logs and file metadata */
	String kind();

	/** @return names of the dimensions whose positions are places in space; empty when nonspatial */
	Set<String> spatialDimensions();

	record Nonspatial() implements Topology {
		@Override
		public String kind() {
			return "nonspatial";
		}

		@Override
		public Set<String> spatialDimensions() {
			return Set.of();
		}
	}
}
