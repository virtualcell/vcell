package org.vcell.solver.fenics;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reads two bundles written by vcell-fenics (sha-74e7386) from its cross-validation models; the
 * expected values come from vcell-fenics' own reader ({@code vcell_fenics.results.reader.Bundle}).
 */
@Tag("Fast")
public class FenicsBundleTest {

	@TempDir
	File tmp;

	static File fixture(String name) throws URISyntaxException {
		return new File(FenicsBundleTest.class.getResource("bundles/" + name + ".fenics/.zattrs").toURI()).getParentFile();
	}

	@Test
	public void testManifest2D() throws Exception {
		File root = fixture("membrane_efflux");
		assertTrue(FenicsBundle.isBundle(root));
		FenicsBundle b = FenicsBundle.open(root);
		assertEquals(1, b.getSchema());
		assertTrue(b.isFixed());
		assertEquals("completed", b.getStatus());
		assertEquals(List.of(0.0, 0.1, 0.2), b.getTimes());
		assertEquals(List.of("mean", "total", "min", "max"), b.getStatsColumns());
		assertEquals(1, b.getSegments().size());
		assertEquals("", b.getSegments().get(0).prefix());

		FenicsBundle.Domain d = b.domain("cytosol_dom");
		assertEquals("volume", d.kind());
		assertEquals(2, d.dim());
		assertEquals(2, d.gdim());
		assertEquals(403, d.numPoints());
		assertEquals(711, d.numCells());
		assertEquals(5, d.cellType());
		assertEquals("mesh/cytosol_dom.vtu", d.mesh());

		FenicsBundle.Variable u = b.variable("cytosol_dom", "u");
		assertEquals("point", u.assoc());
		assertEquals("P1", u.element());
		assertThrows(IllegalArgumentException.class, () -> b.variable("cytosol_dom", "nope"));
	}

	@Test
	public void testFieldAndStats2D() throws Exception {
		FenicsBundle b = FenicsBundle.open(fixture("membrane_efflux"));
		double[] u = b.field("cytosol_dom", "u", 2);
		assertEquals(403, u.length);
		assertEquals(0.9985452303304784, u[0], 0.0);
		assertEquals(0.9998503034149014, u[402], 0.0);
		assertEquals(402.6385248507088, sum(u), 1e-9);

		assertArrayEquals(new double[] { 0.9993355966701419, 0.7830285791419164, 0.9985433178755359, 0.9998755773268853 },
				b.stats("cytosol_dom", "u", 2), 0.0);
		assertThrows(IndexOutOfBoundsException.class, () -> b.field("cytosol_dom", "u", 3));

		String vtu = new String(b.meshBytes("cytosol_dom", 0), StandardCharsets.UTF_8);
		assertTrue(vtu.contains("<VTKFile type=\"UnstructuredGrid\""));
	}

	@Test
	public void testTwoDomains3D() throws Exception {
		FenicsBundle b = FenicsBundle.open(fixture("coupled_3d_small"));
		assertEquals(List.of("cyto_dom", "ext_dom"), List.copyOf(b.getDomains().keySet()));
		assertEquals(10, b.domain("ext_dom").cellType());
		assertEquals(3, b.domain("ext_dom").gdim());
		double[] s = b.field("ext_dom", "s_ext", 2);
		assertEquals(56, s.length);
		assertEquals(0.0046750773466812294, s[0], 0.0);
		assertEquals(0.04316155762712541, s[55], 0.0);
		assertEquals(1.7260872286202535, sum(s), 1e-12);
		assertArrayEquals(new double[] { 0.022234804552240577, 0.1701327273303082, -0.009595814653335914, 0.06008986014586033 },
				b.stats("ext_dom", "s_ext", 2), 0.0);
	}

	@Test
	public void testRunningBundle() throws Exception {
		// as the solver leaves it mid-run: the manifest lists two times, and row 1's chunk is not there yet
		File root = copy(fixture("membrane_efflux"), new File(tmp, "running.fenics"));
		editManifest(root, m -> {
			m.addProperty("status", "running");
			m.getAsJsonArray("times").remove(2);
		});
		Files.delete(new File(root, "cytosol_dom/u/1.0").toPath());

		FenicsBundle b = FenicsBundle.open(root);
		assertEquals("running", b.getStatus());
		assertEquals(2, b.getTimes().size());
		assertFalse(Double.isNaN(b.field("cytosol_dom", "u", 0)[0]));
		assertTrue(Double.isNaN(b.field("cytosol_dom", "u", 1)[0]), "an unwritten chunk reads as the NaN fill value");
		assertThrows(IndexOutOfBoundsException.class, () -> b.field("cytosol_dom", "u", 2), "row 2 exists on disk but not in the manifest");
	}

	@Test
	public void testNewerSchemaRefused() throws Exception {
		File root = copy(fixture("membrane_efflux"), new File(tmp, "future.fenics"));
		editManifest(root, m -> m.addProperty("schema", FenicsBundle.SUPPORTED_SCHEMA + 1));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.open(root));
	}

	@Test
	public void testNotABundle() {
		assertFalse(FenicsBundle.isBundle(tmp));
		assertThrows(IOException.class, () -> FenicsBundle.open(tmp));
	}

	private static double sum(double[] a) {
		double s = 0;
		for (double v : a) s += v;
		return s;
	}

	private static File copy(File from, File to) throws IOException {
		Path src = from.toPath();
		try (Stream<Path> paths = Files.walk(src)) {
			for (Path p : (Iterable<Path>) paths::iterator) {
				Path dest = to.toPath().resolve(src.relativize(p).toString());
				if (Files.isDirectory(p)) Files.createDirectories(dest); else Files.copy(p, dest);
			}
		}
		return to;
	}

	private static void editManifest(File root, java.util.function.Consumer<JsonObject> edit) throws IOException {
		Path attrs = new File(root, ".zattrs").toPath();
		JsonObject json = JsonParser.parseString(Files.readString(attrs)).getAsJsonObject();
		edit.accept(json.getAsJsonObject(FenicsBundle.MANIFEST_KEY));
		Files.writeString(attrs, json.toString());
	}
}
