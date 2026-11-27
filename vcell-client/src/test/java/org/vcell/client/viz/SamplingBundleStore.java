package org.vcell.client.viz;

import java.io.File;
import java.io.IOException;

import org.vcell.solver.fenics.BundleStore;
import org.vcell.solver.fenics.FenicsBundle;
import org.vcell.solver.fenics.FenicsSamples;

/**
 * A bundle directory served as the desktop sees a cluster run's bundle from a data server that samples:
 * whole files from the directory, and sampled reads gathered from it ({@link FenicsBundle#gather}), as
 * {@code DataSetControllerImpl.getFenicsBundleSamples} answers them. Wrapped in {@link BundleStore#cached},
 * as the desktop wraps its data-server store.
 */
final class SamplingBundleStore implements BundleStore {

	private final BundleStore disk;

	private SamplingBundleStore(File dir) {
		this.disk = BundleStore.directory(dir);
	}

	static BundleStore of(File dir) {
		return BundleStore.cached(new SamplingBundleStore(dir));
	}

	@Override
	public byte[] read(String relativePath) throws IOException {
		return disk.read(relativePath);
	}

	@Override
	public FenicsSamples sampleRows(String[] arrayPaths, int[] rows, int[] indices) throws IOException {
		return FenicsBundle.gather(disk, arrayPaths, indices, rows, FenicsBundle.MAX_SAMPLE_VALUES);
	}

	@Override
	public String describe() {
		return "sampled " + disk.describe();
	}
}
