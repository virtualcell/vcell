package org.vcell.client.viz;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

import cbit.vcell.math.VariableType;
import cbit.vcell.server.DataSetController;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMeshMovingBoundary;

/**
 * A stand-in for a MovingBoundary (mbsolver) run, served through the same VTU seam the real ones use.
 * This fakes the data server's side of the seam (a real local run through the real seam is
 * {@link FieldViewerServerMovingBoundaryLocalRunTest}): a
 * {@link DataSetController} whose mesh is a {@link CartesianMeshMovingBoundary} (which is how
 * {@link FieldViewerServer} recognises the mode), with a different body-fitted 2D mesh at every saved time.
 * <p>
 * The domain {@code cell} is a disk of radius 3 moving along x, centre {@code (5 + 4t, 5)}, over
 * {@code t = 0, 0.25, …, 1}. Each time's mesh is the squares of a 0.5 µm lattice whose centres lie in the
 * disk, written as {@code VTK_POLYGON} cells (the real writer labels a 4-gon {@code VTK_QUAD}; the viewer reads both alike). The
 * one variable {@code C} is {@code x + 2y + 10t} at each cell's centre, so a value names where and when it was
 * read.
 */
final class FakeMovingBoundaryRun {

	static final String SIM = "444";
	static final String DOMAIN = "cell";
	static final String VAR = "C";
	static final double[] TIMES = { 0, 0.25, 0.5, 0.75, 1.0 };
	static final double RADIUS = 3;
	static final double H = 0.5;

	private FakeMovingBoundaryRun() {
	}

	static double centreX(double t) {
		return 5 + 4 * t;
	}

	static double value(double x, double y, double t) {
		return x + 2 * y + 10 * t;
	}

	/** Registers the run with the field viewer under {@link #SIM}, job 0. */
	static void register() {
		User owner = new User("fake", new KeyValue("1"));
		VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue(SIM), owner), 0);
		FieldViewerServer.register(vcdID, dataManager(), (org.vcell.vis.vcell.SubdomainInfo) null, "mb::moving disk");
	}

	/** the lattice squares inside the disk at time {@code t}, by lower-left corner */
	private static List<double[]> squares(double t) {
		List<double[]> out = new ArrayList<>();
		for (double x = 0; x < 16; x += H) {
			for (double y = 0; y < 10; y += H) {
				double cx = x + H / 2 - centreX(t), cy = y + H / 2 - 5;
				if (cx * cx + cy * cy < RADIUS * RADIUS) {
					out.add(new double[] { x, y });
				}
			}
		}
		return out;
	}

	/** An ASCII {@code .vtu} of the time's mesh: four points per square (unshared, which the viewer accepts). */
	static byte[] vtu(double t) {
		List<double[]> squares = squares(t);
		StringBuilder points = new StringBuilder();
		StringBuilder connectivity = new StringBuilder();
		StringBuilder offsets = new StringBuilder();
		StringBuilder types = new StringBuilder();
		int p = 0;
		for (double[] s : squares) {
			double[][] corners = { { s[0], s[1] }, { s[0] + H, s[1] }, { s[0] + H, s[1] + H }, { s[0], s[1] + H } };
			for (double[] c : corners) {
				points.append(String.format(Locale.ROOT, "%.6f %.6f 0 ", c[0], c[1]));
				connectivity.append(p++).append(' ');
			}
			offsets.append(p).append(' ');
			types.append("7 ");
		}
		String xml = "<?xml version=\"1.0\"?>\n"
				+ "<VTKFile type=\"UnstructuredGrid\" version=\"0.1\" byte_order=\"LittleEndian\" header_type=\"UInt32\">\n"
				+ "<UnstructuredGrid><Piece NumberOfPoints=\"" + p + "\" NumberOfCells=\"" + squares.size() + "\">\n"
				+ "<Points><DataArray type=\"Float64\" NumberOfComponents=\"3\" format=\"ascii\">" + points + "</DataArray></Points>\n"
				+ "<Cells>\n"
				+ "<DataArray type=\"Int64\" Name=\"connectivity\" format=\"ascii\">" + connectivity + "</DataArray>\n"
				+ "<DataArray type=\"Int64\" Name=\"offsets\" format=\"ascii\">" + offsets + "</DataArray>\n"
				+ "<DataArray type=\"UInt8\" Name=\"types\" format=\"ascii\">" + types + "</DataArray>\n"
				+ "</Cells></Piece></UnstructuredGrid></VTKFile>\n";
		return xml.getBytes(StandardCharsets.UTF_8);
	}

	/** {@link #VAR} per cell of the time's mesh, in the mesh's cell order (the seam's contract) */
	static double[] values(double t) {
		List<double[]> squares = squares(t);
		double[] v = new double[squares.size()];
		for (int c = 0; c < v.length; c++) {
			v[c] = value(squares.get(c)[0] + H / 2, squares.get(c)[1] + H / 2, t);
		}
		return v;
	}

	private static int timeIndex(double t) {
		for (int i = 0; i < TIMES.length; i++) {
			if (TIMES[i] == t) {
				return i;
			}
		}
		throw new IllegalArgumentException("no saved time " + t);
	}

	private static VCDataManager dataManager() {
		VtuVarInfo var = new VtuVarInfo(VAR, VAR, DOMAIN, VariableType.VariableDomain.VARIABLEDOMAIN_VOLUME, null,
				VtuVarInfo.DataType.CellData, false);
		CartesianMeshMovingBoundary mesh = new CartesianMeshMovingBoundary();
		DataSetController controller = (DataSetController) Proxy.newProxyInstance(
				FakeMovingBoundaryRun.class.getClassLoader(), new Class<?>[] { DataSetController.class },
				(proxy, method, args) -> switch (method.getName()) {
					case "getMesh" -> mesh;
					case "getDataSetTimes", "getVtuTimes" -> TIMES.clone();
					case "getVtuVarInfos" -> new VtuVarInfo[] { var };
					case "getEmptyVtuMeshFiles" -> {
						double t = TIMES[(Integer) args[1]];
						VtuFileContainer container = new VtuFileContainer();
						container.addVtuMesh(new VtuFileContainer.VtuMesh(DOMAIN, t, vtu(t)));
						yield container;
					}
					case "getVtuMeshData" -> values(TIMES[timeIndex((Double) args[3])]);
					case "hashCode" -> System.identityHashCode(proxy);
					case "equals" -> proxy == args[0];
					case "toString" -> "FakeMovingBoundaryRun";
					default -> throw new UnsupportedOperationException(method.getName() + " is not part of the fake");
				});
		return new VCDataManager(() -> controller);
	}
}
