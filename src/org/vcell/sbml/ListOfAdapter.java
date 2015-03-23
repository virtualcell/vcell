package org.vcell.sbml;

import java.util.AbstractCollection;
import java.util.Iterator;

import org.sbml.libsbml.ListOf;
import org.sbml.libsbml.SBase;
import org.vcell.util.VCAssert;
/**
 * convert libSbml {@link ListOf} to Java
 * unmodifiable collection
 * @author gweatherby
 *
 * @param <T>
 */
public class ListOfAdapter<T extends SBase> extends AbstractCollection<T> {
	private ListOf list;

	public ListOfAdapter(ListOf list, Class<T> clzz) {
		super();
		this.list = list;
		VCAssert.assertFalse(list.size( ) > (long)Integer.MAX_VALUE, 
				"SBML list size greater than " + Integer.MAX_VALUE);
	}

	@Override
	public Iterator<T> iterator() {
		return new ListOfIterator<T>(list);
	}
	
	
	@Override
	public int size() {
		return (int) list.size( );
	}


	private static class ListOfIterator<T extends SBase> implements Iterator<T> {
		private ListOf list;
		private long cursor = 0;

		ListOfIterator(ListOf list) {
			this.list = list;
		}

		@Override
		public boolean hasNext() {
			return cursor < list.size( );
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			return (T) list.get(cursor++);
		}

		/**
		 * @throws UnsupportedOperationException
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}
}