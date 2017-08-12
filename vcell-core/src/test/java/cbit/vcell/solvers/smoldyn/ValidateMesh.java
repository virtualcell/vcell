package cbit.vcell.solvers.smoldyn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.vcell.util.VCAssert;

import cbit.vcell.solvers.mb.PointIndexTreeAndList;
import cbit.vcell.solvers.mb.Vect3Didx;

public class ValidateMesh {
	public static void main(String[] args) {
		if (args.length > 0) {
			ValidateMesh vm = new ValidateMesh();
			try {
				vm.process(args[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private PointIndexTreeAndList theIndex;
	private Map<String,TrianglePanel> panels = new HashMap<>();
	/**
	 * super duper paranoia? Verify exact same strings used
	 */
	private Map<Integer,String> sameStrings = new HashMap<>( );

	/**
	 * a fragile pattern; input is expected to be well formed
	 */
	private static final Pattern SPLITTER = Pattern.compile("\\s");
	/**
	 * scratch space
	 */
	private double vertices[] = new double[3];
	private StringBuilder combiner = new StringBuilder();

	//private int sideJoin = 0;
	//private int tipJoin = 0;

	public ValidateMesh() {
		theIndex  = new PointIndexTreeAndList();
	}

	public void process(String name) throws IOException {
		theIndex.clear();
		panels.clear();
		try (BufferedReader r = new BufferedReader( new FileReader(name) )) {
			String line;
			while ((line = r.readLine() ) != null ) {
				if (line.startsWith("panel tri")) {
					readPanel(line);
				}
				if (line.startsWith("neighbors")) {
					verifyNeighbors(line);
				}
			}
		}
		System.out.println("Read " + panels.size());
		int tips[] = new int[1024];
		for (TrianglePanel tp : panels.values()) {
			if (tp.getSideNeighbors() != 3) {
				System.out.println("weird stuff");
			}
			int tn = tp.getTipNeighbors();
			tips[tn]++;
		}
		System.out.println("tips distro ");
		for (int i = 0; i < tips.length; i++) {
			int count = tips[i];
			if (count != 0) {
				System.out.println("\t" + i + " = " + count);
			}
		}
	}


	private void readPanel(String line) {
		String parts[] = SPLITTER.split(line);
		VCAssert.assertTrue(parts.length == 12);
		VCAssert.assertTrue(parts[0].equals("panel"));
		VCAssert.assertTrue(parts[1].equals("tri"));
		int a = indexPoint(parts,2);
		int b = indexPoint(parts,5);
		int c = indexPoint(parts,8);
		String ident = parts[11];
		TrianglePanel tp = new TrianglePanel(a, b, c);
		VCAssert.assertFalse(panels.containsKey(ident), "already present");
		panels.put(ident, tp);
	}

	private int indexPoint(String parts[], int start) {
		combiner.setLength(0);

		for (int i = 0; i < 3; i++) {
			String s = parts[start + i];
			vertices[i] = Double.parseDouble( s );
			combiner.append(s);

		}
		Vect3Didx vi = theIndex.index(vertices[0],vertices[1],vertices[2]);
		final int idx = vi.getIndex();
		String prev = sameStrings.get(idx);
		if (prev == null) {
			sameStrings.put(idx, combiner.toString());
		}
		else {
			VCAssert.assertTrue(prev.equals(combiner.toString()),"not exact same string");
		}

		return idx;
	}

	private void verifyNeighbors(String line) {
		String nbors[] = SPLITTER.split(line);
		VCAssert.assertTrue(nbors.length > 2);
		VCAssert.assertTrue(nbors[0].equals("neighbors"));
		String base = nbors[1];
		TrianglePanel tp = panels.get(base);
		Objects.requireNonNull(tp);
		for (int i = 2; i < nbors.length; i++) {
			String them = nbors[i];
			TrianglePanel other = panels.get(them);
			Objects.requireNonNull(other);
			tp.analyzeNeighbor(other);
		}
	}
}
