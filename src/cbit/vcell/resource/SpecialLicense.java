package cbit.vcell.resource;


/**
 * encapsulate specially (non MIT) licensed items
 * @author gweatherby
 *
 */
public enum SpecialLicense {
	CYGWIN(Category.GPL, "Cygwin runtime provided under GNU General Public License v3.", "CYGWIN_LICENSE"); 
	enum Category {
		GPL;
	}
	/**
	 * number of enums
	 */
	public static final int size = values( ).length;
	/**
	 * category of license
	 */
	public final Category category;
	/**
	 * license text 
	 */
	public final String licenseText;
	/**
	 * filename to download to display license 
	 */
	final String filename;

	/**
	 * @param cat for classification / location
	 * @param licenseText in case filename can't be downloaded
	 * @param filename full license text from website
	 */
	private SpecialLicense(SpecialLicense.Category cat, String licenseText, String filename) {
		category = cat;
		this.licenseText = licenseText;
		this.filename = filename;
	}
}