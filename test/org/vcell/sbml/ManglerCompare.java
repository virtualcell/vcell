package org.vcell.sbml;

import java.util.Random;

import org.junit.Test;
import org.vcell.util.TokenMangler;

public class ManglerCompare {
	public final int TRIALS = 100000;
	
	Random random = new Random(23);
	@Test
	public void findDelta( ) {
		int same = 0;
		for (int c = 0; c < TRIALS; c++) {
		String s= rString();
		String tm = TokenMangler.mangleToSName(s);
		String sm = SBMLUtils.mangleToSName(s);
		if (tm.equals(sm)) {
			same++;
		}
		else {
			System.out.println("S: " + s + " TM: " + tm + " SM: " + sm);
		}
		}
		System.out.println("Same " + same);
	}
	
	private String rString( ) {
		final int length = random.nextInt(20) + 5;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = (char) random.nextInt(255);
			sb.append(c);
		}
		return sb.toString();
	}
	
	

}
