package cbit.vcell.solvers.mb;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import ncsa.hdf.object.DataFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * setup logging, load HDF5 native
 * @author GWeatherby
 *
 */
public class MovingBoundaryH5Client {

//	protected static String FILE = "nformat.h5";
	protected static String FILE = "nformat2.h5";
//	protected static String FILE = "fig43-10-1.h5";

	public MovingBoundaryH5Client() {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, ".");
    	NativeLib.HDF5.load();
	}

	@SuppressWarnings("unchecked")
	public static String parseMeta(DataFormat df) {
		try {
			List<Object> meta = null;
			try {
				meta = df.getMetadata();
			} catch (NullPointerException npe) {
				//HDF Java 5.11 throws these sometimes
			}
			if (meta != null) {
				return StringUtils.join(meta);
			}
			else {
				return "no meta";
			}
		} catch (Exception e) {
			throw new RuntimeException("printMeta", e);
		}


	}

}
