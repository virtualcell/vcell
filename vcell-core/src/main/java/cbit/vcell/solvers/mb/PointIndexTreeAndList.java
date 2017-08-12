package cbit.vcell.solvers.mb;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * index Vect3D by objects and by index
 */
public class PointIndexTreeAndList implements PointIndex {
	private int nextIndex;
	private TreeSet<Vect3Didx> existing;
	private ArrayList<Vect3Didx> byIndex;

	public PointIndexTreeAndList() {
		nextIndex = 0;
		existing = new TreeSet<>( );
		byIndex = new ArrayList<>( );
	}

	public Vect3Didx index(double x, double y, double z) {
		Vect3Didx  t = new Vect3Didx(x, y, z, nextIndex);
		if (existing.contains(t)) {
			Vect3Didx  e = existing.floor(t);
			return e;
		}
		nextIndex++;
		existing.add(t);
		byIndex.add(t.index,t);
		return t;
	}

	/**
	 * @param index of existing point
	 * @return existing
	 * @throws IndexOutOfBoundsException
	 */
	@Override
	public Vect3Didx lookup(int index) {
		return byIndex.get(index);
	}

	@Override
	public int size() {
		return nextIndex;
	}

	/**
	 * reset to as new
	 */
	public void clear( ) {
		nextIndex = 0;
		existing.clear();
		byIndex.clear();

	}

}
