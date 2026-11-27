package org.vcell.client.viz;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

import cbit.vcell.math.VariableType;
import cbit.vcell.server.DataSetController;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMeshChombo;

/**
 * A stand-in for a Chombo run, served through the same VTU seam the real ones use (Chombo is retired and
 * server-only, so there is no local fixture). It fakes the data server's side: a {@link DataSetController}
 * whose mesh is a {@link CartesianMeshChombo} (which is how {@link FieldViewerServer} recognises the mode) and
 * ONE embedded-boundary mesh for every saved time, written in Chombo's doubled-index coordinates
 * ({@code v = (p − origin)·N/extent·2 − 1}, which the server maps back to microns).
 * <ul>
 * <li><b>2D</b> ({@link #SIM_2D}): the squares of a 0.5 µm lattice over an 8 × 6 µm box whose centres lie in
 * a disk of radius 2.5 at (4, 3). A boundary square with one corner outside the disk is cut there, into a
 * pentagon ({@code VTK_POLYGON}); the others are {@code VTK_QUAD}s.</li>
 * <li><b>3D</b> ({@link #SIM_3D}): the voxels of a 0.5 µm lattice over a 6 µm cube whose centres lie in a ball
 * of radius 2.2 at (3, 3, 3). A voxel with a corner outside the ball is written as a {@code VTK_POLYHEDRON}
 * (its six faces), the way Chombo writes its cut cells; the others are {@code VTK_VOXEL}s.</li>
 * </ul>
 * The one variable {@link #VAR} is {@code x + 2y + 3z + 10t} at each cell's centre, so a value names where and
 * when it was read.
 */
final class FakeChomboRun {

	static final String SIM_2D = "666";
	static final String SIM_3D = "667";
	static final String DOMAIN = "cyt";
	static final String VAR = "C";
	static final double[] TIMES = { 0, 0.5, 1.0, 1.5 };
	static final double H = 0.5;

	/** the 2D disk and the 3D ball */
	static final double[] CENTRE_2D = { 4, 3 };
	static final double RADIUS_2D = 2.5;
	static final double[] CENTRE_3D = { 3, 3, 3 };
	static final double RADIUS_3D = 2.2;

	private final int dim;
	private final double[] extent;
	private final int[] n;

	private FakeChomboRun(int dim) {
		this.dim = dim;
		this.extent = dim == 2 ? new double[] { 8, 6, 1 } : new double[] { 6, 6, 6 };
		this.n = dim == 2 ? new int[] { 16, 12, 1 } : new int[] { 12, 12, 12 };
	}

	static double value(double x, double y, double z, double t) {
		return x + 2 * y + 3 * z + 10 * t;
	}

	/** Registers the 2D run under {@link #SIM_2D} and the 3D one under {@link #SIM_3D}, job 0. */
	static void register() {
		new FakeChomboRun(2).registerAs(SIM_2D, "chombo::disk 2d");
		new FakeChomboRun(3).registerAs(SIM_3D, "chombo::ball 3d");
	}

	private void registerAs(String sim, String name) {
		User owner = new User("fake", new KeyValue("1"));
		VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue(sim), owner), 0);
		FieldViewerServer.register(vcdID, dataManager(), (org.vcell.vis.vcell.SubdomainInfo) null, name);
	}

	/** Each cell's lower corner, in microns: the lattice cells whose centres are inside the disk or ball. */
	List<double[]> cells() {
		List<double[]> out = new ArrayList<>();
		for (int k = 0; k < (dim == 3 ? n[2] : 1); k++) {
			for (int j = 0; j < n[1]; j++) {
				for (int i = 0; i < n[0]; i++) {
					double[] lo = { i * H, j * H, dim == 3 ? k * H : 0 };
					if (inside(lo[0] + H / 2, lo[1] + H / 2, lo[2] + H / 2)) {
						out.add(lo);
					}
				}
			}
		}
		return out;
	}

	private boolean inside(double x, double y, double z) {
		if (dim == 2) {
			double dx = x - CENTRE_2D[0], dy = y - CENTRE_2D[1];
			return dx * dx + dy * dy < RADIUS_2D * RADIUS_2D;
		}
		double dx = x - CENTRE_3D[0], dy = y - CENTRE_3D[1], dz = z - CENTRE_3D[2];
		return dx * dx + dy * dy + dz * dz < RADIUS_3D * RADIUS_3D;
	}

	/** {@link #VAR} per cell at time {@code t}, in the mesh's cell order (the seam's contract), at each lattice cell's centre */
	double[] values(double t) {
		List<double[]> cells = cells();
		double[] v = new double[cells.size()];
		for (int c = 0; c < v.length; c++) {
			double[] lo = cells.get(c);
			v[c] = value(lo[0] + H / 2, lo[1] + H / 2, dim == 3 ? lo[2] + H / 2 : 0, t);
		}
		return v;
	}

	/** a micron coordinate in Chombo's doubled-index frame */
	private String chombo(double[] p) {
		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < 3; a++) {
			double v = a < dim ? p[a] * n[a] / extent[a] * 2 - 1 : 0;
			sb.append(String.format(Locale.ROOT, "%.6f ", v));
		}
		return sb.toString();
	}

	/** The ASCII {@code .vtu}: unshared points per cell, which the viewer accepts. */
	byte[] vtu() {
		StringBuilder points = new StringBuilder();
		StringBuilder connectivity = new StringBuilder();
		StringBuilder offsets = new StringBuilder();
		StringBuilder types = new StringBuilder();
		StringBuilder faces = new StringBuilder();
		StringBuilder faceOffsets = new StringBuilder();
		int p = 0;
		int faceEnd = 0;
		boolean polyhedra = false;
		List<double[]> cells = cells();
		for (double[] lo : cells) {
			if (dim == 2) {
				double[][] corners = { { lo[0], lo[1], 0 }, { lo[0] + H, lo[1], 0 }, { lo[0] + H, lo[1] + H, 0 }, { lo[0], lo[1] + H, 0 } };
				List<double[]> polygon = new ArrayList<>();
				int outside = 0;
				for (double[] c : corners) {
					outside += inside(c[0], c[1], 0) ? 0 : 1;
				}
				for (int v = 0; v < 4; v++) {
					double[] c = corners[v];
					if (outside == 1 && !inside(c[0], c[1], 0)) {
						// cut the one corner outside: the midpoints of its two edges instead
						double[] prev = corners[(v + 3) % 4], next = corners[(v + 1) % 4];
						polygon.add(new double[] { (prev[0] + c[0]) / 2, (prev[1] + c[1]) / 2, 0 });
						polygon.add(new double[] { (next[0] + c[0]) / 2, (next[1] + c[1]) / 2, 0 });
					} else {
						polygon.add(c);
					}
				}
				for (double[] c : polygon) {
					points.append(chombo(c));
					connectivity.append(p++).append(' ');
				}
				offsets.append(p).append(' ');
				types.append(polygon.size() == 4 ? "9 " : "7 ");
				faceOffsets.append("-1 ");
				continue;
			}
			// VTK_VOXEL point order: x fastest, then y, then z
			int base = p;
			boolean cut = false;
			for (int k = 0; k < 2; k++) {
				for (int j = 0; j < 2; j++) {
					for (int i = 0; i < 2; i++) {
						double[] c = { lo[0] + i * H, lo[1] + j * H, lo[2] + k * H };
						cut |= !inside(c[0], c[1], c[2]);
						points.append(chombo(c));
						connectivity.append(p++).append(' ');
					}
				}
			}
			offsets.append(p).append(' ');
			if (cut) {
				polyhedra = true;
				types.append("42 ");
				int[][] quads = { { 0, 2, 3, 1 }, { 4, 5, 7, 6 }, { 0, 1, 5, 4 }, { 2, 6, 7, 3 }, { 0, 4, 6, 2 }, { 1, 3, 7, 5 } };
				faces.append("6 ");
				faceEnd++;
				for (int[] q : quads) {
					faces.append("4 ");
					faceEnd++;
					for (int v : q) {
						faces.append(base + v).append(' ');
						faceEnd++;
					}
				}
				faceOffsets.append(faceEnd).append(' ');
			} else {
				types.append("11 ");
				faceOffsets.append("-1 ");
			}
		}
		String faceArrays = polyhedra
				? "<DataArray type=\"Int64\" Name=\"faces\" format=\"ascii\">" + faces + "</DataArray>\n"
						+ "<DataArray type=\"Int64\" Name=\"faceoffsets\" format=\"ascii\">" + faceOffsets + "</DataArray>\n"
				: "";
		String xml = "<?xml version=\"1.0\"?>\n"
				+ "<VTKFile type=\"UnstructuredGrid\" version=\"0.1\" byte_order=\"LittleEndian\" header_type=\"UInt32\">\n"
				+ "<UnstructuredGrid><Piece NumberOfPoints=\"" + p + "\" NumberOfCells=\"" + cells.size() + "\">\n"
				+ "<Points><DataArray type=\"Float64\" NumberOfComponents=\"3\" format=\"ascii\">" + points + "</DataArray></Points>\n"
				+ "<Cells>\n"
				+ "<DataArray type=\"Int64\" Name=\"connectivity\" format=\"ascii\">" + connectivity + "</DataArray>\n"
				+ "<DataArray type=\"Int64\" Name=\"offsets\" format=\"ascii\">" + offsets + "</DataArray>\n"
				+ "<DataArray type=\"UInt8\" Name=\"types\" format=\"ascii\">" + types + "</DataArray>\n"
				+ faceArrays
				+ "</Cells></Piece></UnstructuredGrid></VTKFile>\n";
		return xml.getBytes(StandardCharsets.UTF_8);
	}

	private static int timeIndex(double t) {
		for (int i = 0; i < TIMES.length; i++) {
			if (TIMES[i] == t) {
				return i;
			}
		}
		throw new IllegalArgumentException("no saved time " + t);
	}

	private VCDataManager dataManager() {
		VtuVarInfo var = new VtuVarInfo(VAR, VAR, DOMAIN, VariableType.VariableDomain.VARIABLEDOMAIN_VOLUME, null,
				VtuVarInfo.DataType.CellData, false);
		final Origin origin = new Origin(0, 0, 0);
		final Extent ext = new Extent(extent[0], extent[1], extent[2]);
		CartesianMeshChombo mesh = new CartesianMeshChombo() {
			private static final long serialVersionUID = 1L;

			@Override
			public Origin getOrigin() {
				return origin;
			}

			@Override
			public Extent getExtent() {
				return ext;
			}

			@Override
			public int getSizeX() {
				return n[0];
			}

			@Override
			public int getSizeY() {
				return n[1];
			}

			@Override
			public int getSizeZ() {
				return n[2];
			}

			@Override
			public int getGeometryDimension() {
				return dim;
			}
		};
		byte[] vtu = vtu();
		DataSetController controller = (DataSetController) Proxy.newProxyInstance(
				FakeChomboRun.class.getClassLoader(), new Class<?>[] { DataSetController.class },
				(proxy, method, args) -> switch (method.getName()) {
					case "getMesh" -> mesh;
					case "getDataSetTimes", "getVtuTimes" -> TIMES.clone();
					case "getVtuVarInfos" -> new VtuVarInfo[] { var };
					case "getEmptyVtuMeshFiles" -> {
						if ((Integer) args[1] != 0) {
							throw new IllegalArgumentException("a Chombo mesh is static: time index 0 only");
						}
						VtuFileContainer container = new VtuFileContainer();
						container.addVtuMesh(new VtuFileContainer.VtuMesh(DOMAIN, TIMES[0], vtu));
						yield container;
					}
					case "getVtuMeshData" -> values(TIMES[timeIndex((Double) args[3])]);
					case "hashCode" -> System.identityHashCode(proxy);
					case "equals" -> proxy == args[0];
					case "toString" -> "FakeChomboRun" + dim + "D";
					default -> throw new UnsupportedOperationException(method.getName() + " is not part of the fake");
				});
		return new VCDataManager(() -> controller);
	}
}
