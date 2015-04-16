package org.vcell.sbml.vcell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.vcell.util.VCAssert;

/**
 * independent unit representation; intermediary for translation between systems 
 * @author GWeatherby
 */
public class UnitRepresentation {
	
	/**
	 * base unit
	 */
	public static class Fundamental {
		public final String original;
		public final String unit;
		public final int power;
		Fundamental(String orig, String unit, int power) {
			this.original = orig;
			this.unit = unit;
			this.power = power;
		} 
		
		Fundamental(String orig, String unit) {
			this(orig,unit,1);
		}

		@Override
		public String toString() {
			if (power == 1) {
				return unit;
			}
			return unit + "^" + power;
		} 
	}
	
	/**
	 * power of ten to scale unit by
	 */
	private int scale;
	/**
	 * base units (may be empty if dimensionless)
	 */
	private List<Fundamental> fundamentals;
	
	
	/**
	 * matches leading numbers and units from String 
	 */
	private static Pattern unitPattern;
	static {
		//
		//(\\D+?) minimum number of non digits
		//(-?\\d+)? optional minus sign, then digits, minimally
		unitPattern = Pattern.compile("(\\D+?)(-?\\d+)?");
	}
	
	/**
	 * create representation from String 
	 * @param ucarString unit string
	 * @param dictionary mapping ucar to desired names (may be null)
	 * @throws IllegalArgumentException
	 */
	public static UnitRepresentation parseUcar(String ucarString, Map<String,String> dictionary) {
		if (dictionary != null) {
			return new UnitRepresentation(ucarString, dictionary);
		}
		Map<String,String> empty = Collections.emptyMap();
		return new UnitRepresentation(ucarString, empty);
	}
	
	/**
	 * create representation from ucar String  -- encapsulate for possible expansion later
	 * @param ucarString unit string
	 * @param dictionary mapping ucar to desired names not null 
	 * @throws IllegalArgumentException
	 */
	private UnitRepresentation(String ucarString, Map<String,String> dictionary) {
		scale = 0;
		fundamentals = new ArrayList<>();
		ucarParse(ucarString, dictionary);
	}

	/**
	 * @return unmodifiable list of {@link Fundamental} pieces
	 */
	public List<Fundamental> getFundamentals() {
		return Collections.unmodifiableList(fundamentals);
	}
	
	/**
	 * @return true if no units present
	 */
	public boolean isDimensionless( ) {
		return fundamentals.size() == 0;
	}
	/**
	 * @return power of ten to adjust unit by; may be 0
	 */
	public int getScale() {
		return scale;
	}
	
	/**
	 * parse a String; may be called recursively
	 * @param ucarString not null
	 * @param dictionary not null
	 * @throws IllegalArgumentException if not valid / expected String
	 */
	private void ucarParse(String ucarString, Map<String,String> dictionary) {
		String[] parts = StringUtils.split(ucarString);
		if (parts.length > 0) {
			determineFundamentals(parts[parts.length - 1],dictionary);
			for (int i = 0; i < parts.length - 1; i ++ ) {
				scale += determineScale(parts[i]);
			}
			return;
		}
		throw new IllegalArgumentException("string " + ucarString + " not valid unit representation");
	}
	
		
	/**
	 * @param number not null
	 * @return log base 10 of number
	 * @throws IllegalArgumentException if base ten log not an integer
	 * @throws NullPointerException 
	 */
	private int determineScale(String number) {
		double v = Double.parseDouble(number);
		double logV = Math.log10(v);
		int logVInt = (int) logV;
		if (logVInt != logV) {
			throw new IllegalArgumentException("log");
		}
		
		return logVInt;
	}
	
	/**
	 * @param key not null
	 * @param dictionary not null
	 * @return result from dictionary if present, key otherwise
	 */
	private String lookup(String key, Map<String, String> dictionary) {
		String r = dictionary.get(key);
		if (r == null) {
			return key;
		}
		return r;
	}
	
	/**
	 * @param in not null
	 * @return true if has . in it
	 */
	private static boolean containsDot(String in) {
		return in.indexOf('.') != -1;
	}
	
	/**
	 * parse out individual units from dot separated string
	 * @param unitString not null
	 * @param dictionary not null
	 */
	private void determineFundamentals(String unitString, Map<String, String> dictionary) {
		String unitPieces[] = StringUtils.split(unitString,'.');
		for (String u : unitPieces) {
			Matcher m = unitPattern.matcher(u);
			if (m.matches( )) {
				String orig = m.group(1);
				String powerStr = m.group(2);
				String unit = lookup(orig,dictionary);
				if (containsDot(unit)) {
					VCAssert.assertTrue(powerStr == null, "no power on dictionary result");
					ucarParse(unit, dictionary);
					continue;
				}
				if (powerStr == null) {
					fundamentals.add( new Fundamental(orig,unit));
					continue;
				}
				int power = Integer.parseInt(powerStr);
				fundamentals.add( new Fundamental(orig,unit,power));
			}
			else {
				throw new IllegalArgumentException("Unmatched unit fragment " + u + " from " + unitString);
			}
		}
	}

	@Override
	public String toString() {
		String rval;
		if (scale != 1) {
			rval = "10^" + scale + " ";
		}
		else {
			rval = "";
		}
		rval += StringUtils.join(fundamentals,'.');
		
		return rval; 
	}
}
