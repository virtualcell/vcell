package cbit.vcell.solvers.mb;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.logging.Logging;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;
import ncsa.hdf.object.DataFormat;

/**
 * setup logging, load HDF5 native
 * @author GWeatherby
 *
 */
public class H5Client {

//	protected static String FILE = "nformat.h5";
	protected static String FILE = "nformat2.h5";
//	protected static String FILE = "fig43-10-1.h5";

	public H5Client() {
		Logging.init();
    	System.setProperty(PropertyLoader.installationRoot, ".");
    	ResourceUtil.setNativeLibraryDirectory();
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
