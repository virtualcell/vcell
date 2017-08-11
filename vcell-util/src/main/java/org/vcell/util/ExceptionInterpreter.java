package org.vcell.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * repository for translating low lever "programmer-ese" exceptions into possible user action
 * @author gweatherby
 *
 */
public class ExceptionInterpreter {
	
	private static class Interpretation{
		final String key;
		final String regex;
		/**
		 * lazily evaluated pattern #regex:w
		 * 
		 */
		Pattern pattern = null;
		final String suggestions[];
		public Interpretation(String key, String regex, String[] suggestions) {
			super();
			this.key = key;
			this.regex = regex;
			this.suggestions = suggestions;
		}
		
		Pattern getPattern( ) {
			if (pattern == null) {
				pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
			}
			return pattern;
		}
	}
	
	private Collection<Interpretation> interpretations = new ArrayList<ExceptionInterpreter.Interpretation>();
	
	private ExceptionInterpreter() {}
	
	private static ExceptionInterpreter inst = new ExceptionInterpreter(); 
	
	/**
	 * @return Singleton instance
	 */
	public static ExceptionInterpreter instance( ) {
		return inst;
	}
	
	/**
	 * @param key simple matching key for quick scanning
	 * @param regex possibly more complex regular expression to match (case-insensitive)
	 * @param suggestions one or more suggested solutions
	 * @throws IllegalArgumentException if any argument null
	 */
	public void add(String key, String regex, String ... suggestions) {
		if (key == null || regex == null || suggestions == null || suggestions.length == 0) {
			throw new IllegalArgumentException("invalid null reference(s)");
		}
			
		Interpretation i = new Interpretation(key, regex, suggestions);
		interpretations.add(i);
	}
	
	/**
	 * return any suggestions that correspond to error message
	 * @param errorMessage
	 * @return suggestions, if any, or null if not
	 */
	public Collection<String> suggestions(String errorMessage) {
		Collection<String> rval = null;
		for (Interpretation iprt: interpretations) {
			if (errorMessage.contains(iprt.key)) {
				Matcher m = iprt.getPattern().matcher(errorMessage);
				if (m.matches()) {
					if (rval == null) {
						rval = new ArrayList<String>();
					}
					for (String s : iprt.suggestions) {
						rval.add(s);
					}
				}
			}
		}
		return rval;
	}
}
	
	
	
	
	
	
