package org.vcell.sbml.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * biomodels.net model naming support 
 * @author gweatherby
 *
 */
public class BiomodelsNetEntry implements Comparable<BiomodelsNetEntry>{
	public static final String PREFIX = "BIOMD";
	private final Integer id;
	private String name;
	
	/**
	 * @param name_ Name starting with {@link #PREFIX} or integer in String
	 */
	public BiomodelsNetEntry(String name_) {
		this(parseString(name_));
	}
	
	public BiomodelsNetEntry(int id_) {
		id = id_;
		name = null;
	}
	
	public int number( ) {
		return id;
	}
	
	public String toString( ) {
		if (name == null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			ps.printf("%s%010d",PREFIX,id);
			name = baos.toString();
		}
		return name;
	}
	
	public String filename( ) {
		return toString( ) + ".sbml";
	}
	
	@Override
	public int hashCode() {
		return id.hashCode(); 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BiomodelsNetEntry other = (BiomodelsNetEntry) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(BiomodelsNetEntry o) {
		return id.compareTo(o.id);
	}
	
	private static int parseString(String s) {
		if (s.startsWith(PREFIX)) { 
			return Integer.parseInt(s.substring(s.length( ) - PREFIX.length()) );
		}
		return Integer.parseInt(s);
	}

	public static void main(String[] args)  {
		String sample = "BIOMD0000000001";
		BiomodelsNetEntry a = new BiomodelsNetEntry(sample);
		BiomodelsNetEntry b = new BiomodelsNetEntry(1);
		assert(a.equals(b));
		String bString = b.toString();
		assert(bString.equals(sample));
		BiomodelsNetEntry c = new BiomodelsNetEntry(2);
		assert(c.compareTo(b) > 0);
		assert(a.compareTo(b) == 0);
	}
}
