package org.vcell.util.collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.vcell.util.collections.VCCollections.Delta;

public class VCCollectionsTest {
	private List<Integer> a = new ArrayList<>();
	private List<Integer> b = new ArrayList<>();
	private Comparator<Integer> cmp = Comparator.naturalOrder( );
	
	@Before
	public void setup( ) {
		int n[] = {3,4,5};
		for (int i : n) {
			a.add(i);
		}
		b.clear();
	}
	
	@Test
	public <T> void ctest( ) {
		ArrayList<Delta<Integer>> dt = new ArrayList<VCCollections.Delta<Integer>>( );
		b.addAll(a);
		assertTrue(VCCollections.equal(a, b, cmp, null));
		assertTrue(VCCollections.equal(a, b, cmp, dt));
		for (int i = 0; i < 10 ;i++) {
			Collections.shuffle(b);
			assertTrue(VCCollections.equal(a, b, cmp, null));
			assertTrue(VCCollections.equal(a, b, cmp, dt));
		}
		b.add(7);
		assertFalse(VCCollections.equal(a, b, cmp, null));
		assertFalse(VCCollections.equal(a, b, cmp, dt));
		a.add(8);
		assertFalse(VCCollections.equal(a, b, cmp, null));
		assertFalse(VCCollections.equal(a, b, cmp, dt));
		for (Delta<Integer> d : dt) {
			System.out.println(d);
		}
		
		
		
	}

}
