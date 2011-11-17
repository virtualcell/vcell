/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.text;

import java.util.Collection;

import org.sbpax.util.EnglishPluralizer;
import org.sbpax.util.StringUtil;

/*   NumberText  --- by Oliver Ruebenacker, UCHC --- January 2009
 *   Turns integers into text
 */

public class NumberText {

	static public String numberText(int i) {
		switch(i) {
		case 0: return "zero";
		case 1: return "one";
		case 2: return "two";
		case 3: return "three";
		case 4: return "four";
		case 5: return "five";
		case 6: return "six";
		case 7: return "seven";
		case 8: return "eight";
		case 9: return "nine";
		case 10: return "ten";
		case 11: return "eleven";
		case 12: return "twelve";
		default: return Integer.toString(i);
		}
	}
	
	static public String pluralIfNeeded(int i, String singular) {
		return pluralIfNeeded(i, singular, singular + "s");
	}
	
	static public String pluralIfNeeded(int i, String singular, String plural) {
		switch (i) {
		case 1: return singular;
		default: return plural;
		}
	}
	
	static public String soMany(int i, String singular) {
		return soMany(i, singular, EnglishPluralizer.pluralize(singular));
	}
	
	static public String soMany(int i, String singular, String plural) {
		switch (i) {
		case 0: return "no " + plural;
		case 1: return "one " + singular;
		default: return numberText(i) + " " + plural;
		}
	}
	
	static public String soManyThings(Collection<?> things, String singular) {
		return soManyThings(things, singular, EnglishPluralizer.pluralize(singular));
	}
		
	static public String soManyThings(Collection<?> things, 
				String singular, String plural) {
		int size = things.size();
		String thingsText = StringUtil.concat(things, ", ");
		switch (size) {
		case 0: return "no " + plural;
		case 1: return singular + ": " + thingsText;
		default: return numberText(size) + " " + plural + ": " + thingsText;
		}
	}
	
}
