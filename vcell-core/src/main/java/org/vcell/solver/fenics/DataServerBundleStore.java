package org.vcell.solver.fenics;

import java.io.IOException;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.simdata.VCDataManager;

/**
 * A FEniCSx results bundle read file by file from the data server
 * ({@link cbit.vcell.server.DataSetController#getFenicsBundleFile}), for a simulation that ran on the
 * cluster. Wrap it in {@link BundleStore#cached} so each immutable file crosses the wire once.
 */
public final class DataServerBundleStore implements BundleStore {

	private final VCDataManager dataManager;
	private final VCDataIdentifier vcdID;

	public DataServerBundleStore(VCDataManager dataManager, VCDataIdentifier vcdID) {
		this.dataManager = dataManager;
		this.vcdID = vcdID;
	}

	@Override
	public byte[] read(String relativePath) throws IOException {
		try {
			return dataManager.getFenicsBundleFile(vcdID, BundleStore.checkRelativePath(relativePath));
		} catch (DataAccessException e) {
			throw new IOException("reading " + relativePath + " of " + describe() + ": " + e.getMessage(), e);
		}
	}

	@Override
	public String describe() {
		return "FEniCSx results of " + vcdID.getID() + " (data server)";
	}
}
