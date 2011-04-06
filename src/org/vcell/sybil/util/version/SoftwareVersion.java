package org.vcell.sybil.util.version;

/*   Version  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   A class for storing version information, plus the current version
 */

public class SoftwareVersion {
	
	public static abstract class Product {		
		@Override
		public abstract boolean equals(Object object);
		@Override
		public abstract int hashCode();
	}
	
	public static enum Edition { 
		Alpha("Alpha", "a"), 
		Beta("Beta", "b"), 
		Release("Release", ""); 

		protected String letter;
		protected String text;
		private Edition(String text, String letter) {
			this.text = text;
			this.letter = letter;
		}
		public String letter() { return letter; }
		public String text() { return text; }
	}

	protected Product product;
	protected int major;
	protected int minor;
	protected Edition edition;
	protected int build;
	protected String name;
	
	public SoftwareVersion(Product product, int major, int minor, Edition edition, int build) {
		this(product, major, minor, edition, build, null);
	}
	
	public SoftwareVersion(Product product, int major, int minor, Edition edition, int build, String name) {
		this.product = product;
		this.major = major;
		this.minor = minor;
		this.edition = edition;
		this.build = build;
		this.name = name;
	}
	
	public boolean isSameBranchAs(SoftwareVersion version) { 
		return product.equals(version.product) && major == version.major 
		&& minor == version.minor; 
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof SoftwareVersion) {
			SoftwareVersion version = (SoftwareVersion) object;
			return isSameBranchAs(version) && edition == version.edition 
			&& build == version.build;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return product.hashCode() + build + 31*edition.ordinal() + 31*31*minor 
		+ 31*31*31*major;
	}
	
	@Override
	public String toString() { return shortText(); }

	public String shortText() { 
		String shortText = "V" + major + "." + minor + "." + edition.letter() + build;
		if(name != null && name.length() > 0) { shortText = shortText + " (" + name + ")"; }
		return shortText; 
	}

	public String text() { 
		String text = "Version " + major + "." + minor + " " + edition.text() + ", Build " + build;
		if(name != null && name.length() > 0) { text = text + " (" + name + ")"; }
		return text; 
	}
	
	
}
