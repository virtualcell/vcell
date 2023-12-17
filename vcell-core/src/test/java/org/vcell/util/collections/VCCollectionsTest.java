package org.vcell.util.collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.collections.VCCollections.Delta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class VCCollectionsTest {
	private List<Integer> a = new ArrayList<>();
	private List<Integer> b = new ArrayList<>();
	private Comparator<Integer> cmp = Comparator.naturalOrder( );
	
	@BeforeEach
	public void setup( ) {
		int n[] = {3,4,5};
		for (int i : n) {
			a.add(i);
		}
		b.clear();
	}
	
	@Test
	public <T> void ctest( ) {
		ArrayList<Delta<Integer>> dt = new ArrayList<Delta<Integer>>();
		b.addAll(a);
		assertTrue(VCCollections.equal(a, b, cmp, null));
		assertTrue(VCCollections.equal(a, b, cmp, dt));
		for (int i = 0; i < 10; i++) {
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
//		for (Delta<Integer> d : dt) {
//			System.out.println(d);
//		}


	}

}
