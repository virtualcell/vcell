package cbit.vcell.VirtualMicroscopy;

import java.io.File;

/**
 * abstract file name patterns which may appear as "hidden" files in system folders.
 * Used to validate exclusion from list of images files
 * @author gweatherby
 *
 */
public class HiddenNonImageFile {
	/**
	 * known hidden names 
	 * could be extended to be regex patterns
	 */
	private static final String [] names = {"thumbs.db", ".ds_store"};
	
	/**
	 * identify as hidden based on known hidden name patterns
	 * @param f
	 * @return true if is hidden
	 * @throws IllegalArgumentException if f is null
	 */
	public static boolean isHidden(File f) throws IllegalArgumentException {
		if (f != null) {
			String lcName = f.getName().toLowerCase();
			for (String n : names) {
				if (lcName.endsWith(n)) {
					return true;
				}
			}
			return false;
		}
		throw new IllegalArgumentException("Null pointer passed to isHidden");
	}
	
	/**
	 * identify as hidden based on known hidden name patterns
	 * @param name 
	 * @return true if is hidden
	 * @throws IllegalArgumentException if name is null
	 */
	public static boolean isHidden(String name) throws IllegalArgumentException {
		if (name != null) {
			String lcName = name.toLowerCase();
			for (String n : names) {
				if (lcName.endsWith(n)) {
					return true;
				}
			}
			return false;
		}
		throw new IllegalArgumentException("Null pointer passed to isHidden");
	}
}
