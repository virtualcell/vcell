package org.vcell.vis.vtk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vis.mapping.chombo.ChomboVtkFileWriter;
import org.vcell.vis.vismesh.thrift.ChomboIndexData;

import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;

/**
 * The Chombo VTU seam ({@link ChomboVtkFileWriter}) over two real Chombo results ({@link ChomboRunFixture}), with
 * its grids written in Java ({@link VtkService#writeChomboVolumeVtkGridAndIndexData},
 * {@link VtkService#writeChomboMembraneVtkGridAndIndexData}): each domain's cells are the mesh's cells in the index
 * data's order, and each value the seam pairs with a cell is the value of that cell — the code of the voxel the
 * cell lies in, which the fixture's initial conditions put into {@code RanC_nuc} and {@code s2}.
 */
@Tag("Fast")
public class ChomboVtuWriterTest {

	private static final OutputContext NONE = new OutputContext(new AnnotatedFunction[0]);

	@Test
	public void theThreeDimensionalVolumeAndMembraneAreWrittenAndPaired(@TempDir Path dir) throws Exception {
		check(ChomboRunFixture.SIM_3D, 3, dir);
	}

	@Test
	public void theTwoDimensionalVolumeAndMembraneAreWrittenAndPaired(@TempDir Path dir) throws Exception {
		check(ChomboRunFixture.SIM_2D, 2, dir);
	}

	private static void check(String sim, int dimension, Path dir) throws Exception {
		ChomboFiles chomboFiles = ChomboRunFixture.chomboFiles(sim, dir.resolve("data"));
		File out = Files.createDirectories(dir.resolve("vtu")).toFile();
		ChomboVtkFileWriter writer = new ChomboVtkFileWriter();
		File[] meshFiles = writer.writeEmptyMeshFiles(chomboFiles, out, null);
		Assertions.assertEquals(2, meshFiles.length, "a volume and its membrane: " + List.of(meshFiles));

		// the volume: polygons in 2D; whole voxels, then the polyhedra cut at the boundary, in 3D
		VtuTestReader volume = VtuTestReader.read(new File(out, "SimID_" + sim + "_0_" + ChomboRunFixture.VOLUME + ".vtu"));
		ChomboIndexData volumeIndex = VisMeshUtils.readChomboIndexData(
				new File(out, "SimID_" + sim + "_0_" + ChomboRunFixture.VOLUME + ".chomboindex"));
		Assertions.assertEquals(volume.numberOfCells, volumeIndex.getChomboVolumeIndicesSize());
		Assertions.assertFalse(volumeIndex.isSetChomboSurfaceIndices());
		int previous = 0;
		boolean polyhedra = false;
		for (int c = 0; c < volume.numberOfCells; c++) {
			int type = volume.types[c];
			if (dimension == 2) {
				Assertions.assertTrue(type == VtuWriter.VTK_QUAD || type == VtuWriter.VTK_TRIANGLE || type == VtuWriter.VTK_POLYGON,
						"cell " + c + " type " + type);
			} else {
				Assertions.assertTrue(type == VtuWriter.VTK_VOXEL || type == VtuWriter.VTK_POLYHEDRON, "cell " + c + " type " + type);
				Assertions.assertTrue(type >= previous, "voxels first, then polyhedra");
				polyhedra |= type == VtuWriter.VTK_POLYHEDRON;
				Assertions.assertEquals(type == VtuWriter.VTK_POLYHEDRON, volume.faces != null && volume.faces[c] != null,
						"a polyhedron, and only a polyhedron, carries its faces");
			}
			previous = type;
		}
		Assertions.assertEquals(dimension == 3, polyhedra, "3D cut cells are polyhedra");

		VtuVarInfo ranC = new VtuVarInfo(ChomboRunFixture.VOLUME_VAR, ChomboRunFixture.VOLUME_VAR, ChomboRunFixture.VOLUME,
				VariableDomain.VARIABLEDOMAIN_VOLUME, null, VtuVarInfo.DataType.CellData, false);
		double[] volumeValues = writer.getVtuMeshData(chomboFiles, NONE, out, 0.0, ranC, 0);
		Assertions.assertEquals(volume.numberOfCells, volumeValues.length);
		for (int c = 0; c < volume.numberOfCells; c++) {
			Assertions.assertEquals(codeOfCell(volume, c, dimension), Math.rint(volumeValues[c] / ChomboRunFixture.RANC_NUC_SCALE),
					"RanC_nuc of volume cell " + c);
		}

		// the membrane: segments in 2D, triangles in 3D, on the circle or sphere
		VtuTestReader membrane = VtuTestReader.read(new File(out, "SimID_" + sim + "_0_" + ChomboRunFixture.MEMBRANE + ".vtu"));
		ChomboIndexData membraneIndex = VisMeshUtils.readChomboIndexData(
				new File(out, "SimID_" + sim + "_0_" + ChomboRunFixture.MEMBRANE + ".chomboindex"));
		Assertions.assertTrue(membrane.numberOfCells > 0);
		Assertions.assertEquals(membrane.numberOfCells, membraneIndex.getChomboSurfaceIndicesSize());
		Assertions.assertFalse(membraneIndex.isSetChomboVolumeIndices());
		int expectedType = dimension == 2 ? VtuWriter.VTK_LINE : VtuWriter.VTK_TRIANGLE;
		for (int c = 0; c < membrane.numberOfCells; c++) {
			Assertions.assertEquals(expectedType, membrane.types[c], "membrane cell " + c);
		}
		double radius = dimension == 2 ? ChomboRunFixture.RADIUS_2D : ChomboRunFixture.RADIUS_3D;
		for (int p = 0; p < membrane.numberOfPoints; p++) {
			double r2 = 0;
			for (int a = 0; a < dimension; a++) {
				double d = ChomboRunFixture.physical(membrane.points[3 * p + a], a, dimension) - ChomboRunFixture.CENTRE[a];
				r2 += d * d;
			}
			Assertions.assertEquals(radius, Math.sqrt(r2), 0.1 * ChomboRunFixture.H, "membrane point " + p + " is on the boundary");
		}

		VtuVarInfo s2 = new VtuVarInfo(ChomboRunFixture.MEMBRANE_VAR, ChomboRunFixture.MEMBRANE_VAR, ChomboRunFixture.MEMBRANE,
				VariableDomain.VARIABLEDOMAIN_MEMBRANE, null, VtuVarInfo.DataType.CellData, false);
		for (int t = 0; t < chomboFiles.getTimeIndices().size(); t++) {
			double[] membraneValues = writer.getVtuMeshData(chomboFiles, NONE, out, 0.0, s2, t);
			Assertions.assertEquals(membrane.numberOfCells, membraneValues.length);
			for (int c = 0; c < membrane.numberOfCells; c++) {
				Assertions.assertEquals(codeOfCell(membrane, c, dimension), membraneValues[c], 1e-9,
						"s2 of membrane cell " + c + " at time index " + t);
			}
		}
	}

	/** {@link ChomboRunFixture#code} of the centre of the cell's bounding box, in microns */
	static double codeOfCell(VtuTestReader grid, int c, int dimension) {
		double[] lo = { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
		double[] hi = { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
		for (int p : grid.cells[c]) {
			for (int a = 0; a < 3; a++) {
				double v = ChomboRunFixture.physical(grid.points[3 * p + a], a, dimension);
				lo[a] = Math.min(lo[a], v);
				hi[a] = Math.max(hi[a], v);
			}
		}
		return ChomboRunFixture.code((lo[0] + hi[0]) / 2, (lo[1] + hi[1]) / 2, (lo[2] + hi[2]) / 2);
	}
}
