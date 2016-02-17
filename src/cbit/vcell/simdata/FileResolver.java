package cbit.vcell.simdata;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.vcell.util.BeanUtils;
import org.vcell.util.LineStringBuilder;
import org.vcell.util.VCAssert;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCKeyDataIdentifier;

import cbit.vcell.solvers.MovingBoundaryFileWriter;
import cbit.vcell.solvers.MovingBoundarySolver;


/**
 * stop gap API to add Moving Boundary to the mix of files found
 */
public class FileResolver {

	/**
	 * return value
	 */
	public static class LocInfo {
		/**
		 * simulation key
		 */
		public final KeyValue simulationKey;
		/**
		 * identifier appropriate for data found
		 */
		public final VCDataIdentifier dataIdent;
		/**
		 * directory located in
		 */
		public final File directory;
		public final int jobIndex;
		/**
		 * optional determination of whether it's spatial
		 */
		public final Boolean isSpatial;

		LocInfo(KeyValue simulationKey, VCDataIdentifier dataIdent, File directory, int jobIndex, Boolean isSpatial) {
			super();
			this.simulationKey = simulationKey;
			this.dataIdent = dataIdent;
			this.directory = directory;
			this.jobIndex = jobIndex;
			this.isSpatial = isSpatial;
		}
	}

	private static FileResolver INSTANCE;
	/**
	 * places to look
	 */
	private final Set<File> directories;
	private FileResolver( ) {
		directories = new HashSet<>( );
	}

	public static FileResolver get( ) {
		if (INSTANCE == null) {
			INSTANCE = new FileResolver();

		}
		return INSTANCE;
	}

	public void registerDirectory(File f) {
		directories.add(f);
	}

	public LocInfo getLocation(VCDataIdentifier di) {
		Objects.requireNonNull(di);
		VCKeyDataIdentifier kdi = BeanUtils.downcast(VCKeyDataIdentifier.class, di);
		if (kdi != null) {
			KeyValue simKey = kdi.getSimulationKey();
			Collection<NameResolve> sources = names(kdi,simKey);
			for (NameResolve nr : sources) {
				for (File d :directories) {
					File candidate = new File(d,nr.name);
					if (candidate.exists()) {
						VCKeyDataIdentifier dataId = nr.convert.apply(kdi);
						return new LocInfo(simKey, dataId, d, kdi.getJobIndex(),nr.knownSpatial);
					}
				}
			}
			LineStringBuilder lsb = new LineStringBuilder("No simulation found for ");
			lsb.append(di.toString());
			lsb.append(" simulation key ");
			lsb.append(simKey.toString());
			lsb.append(" searched");
			lsb.newline();
			for (NameResolve nr : sources) {
				for (File d :directories) {
					lsb.append(d.toString());
					lsb.append('/');
					lsb.append(nr.name);
					lsb.newline();
				}
			}
			throw new RuntimeException(lsb.toString());
		}
		throw new RuntimeException(di + " of type " + di.getClass().getName() + " not VCKeyDataIdentifier");
	}

	/**
	 * name and id conversion info
	 */
	private static class NameResolve {
		final String name;
		final Function<VCKeyDataIdentifier, VCKeyDataIdentifier> convert;
		final Boolean knownSpatial;


		/**
		 * spatial attribute unknown
		 * @param name
		 * @param convert
		 */
		NameResolve(String name, Function<VCKeyDataIdentifier, VCKeyDataIdentifier> convert) {
			this.name = name;
			this.convert = convert;
			knownSpatial = null;
		}

		NameResolve(String name, Function<VCKeyDataIdentifier, VCKeyDataIdentifier> convert, boolean isSpatial) {
			this.name = name;
			this.convert = convert;
			this.knownSpatial = isSpatial;
		}

	}

	/**
	 * @param kdi
	 * @return kdi to old style id
	 */
	private static VCKeyDataIdentifier toOldStyle(VCKeyDataIdentifier kdi) {
		VCDataIdentifier os = SimulationData.AmplistorHelper.convertVCDataIDToOldStyle(kdi);
		return (VCKeyDataIdentifier) os;
	}

	/**
	 * @param kdi non null
	 * @param sk = kdi.getSimuationKey
	 * @return name information to look for
	 */
	private static Collection<NameResolve> names(VCKeyDataIdentifier kdi, KeyValue sk) {
		VCAssert.assertTrue(kdi.getSimulationKey() == sk);
		ArrayList<NameResolve> rval = new ArrayList<>(3);
		int ji = kdi.getJobIndex();
		Function<VCKeyDataIdentifier, VCKeyDataIdentifier> sameId = Function.identity();
		rval.add( new NameResolve(SimulationData.createCanonicalSimLogFileName(sk,ji,false),sameId) );

		String end = MovingBoundarySolver.MOVING_BOUNDARY_FILE_END;
		NameResolve nr = new NameResolve(String.join("_","SimID",sk.toString(),Integer.toString(ji), end),sameId,true);
		rval.add( nr);

		rval.add( new NameResolve(SimulationData.createCanonicalSimLogFileName(sk,ji,true),FileResolver::toOldStyle) );
		return rval;
	}
}
