package org.vcell.sybil.util.lists;

/*   BoundedList  --- by Oliver Ruebenacker, UCHC --- June to July 2008
 *   A list with change listeners, useful for combo box and list models
 */

import java.util.List;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public interface BoundedList<E> extends List<E> {

	public static class Event<E> extends ListDataEvent {

		private static final long serialVersionUID = 6414938096772063758L;

		public static class Type  { 
			protected int type;
			public Type(int typeNew) { type = typeNew; }
			public int type() { return type; }
		}
		
		public static final Type CONTENTS_CHANGED = new Type(ListDataEvent.CONTENTS_CHANGED);
		public static final Type INTERVAL_ADDED = new Type(ListDataEvent.INTERVAL_ADDED);
		public static final Type INTERVAL_REMOVED = new Type(ListDataEvent.INTERVAL_REMOVED);
	
		protected Type type;
		
		public Event(BoundedList<E> list, Type typeNew, int ind0, int ind1) {
			super(list, typeNew.type(), ind0, ind1);
			type = typeNew;
		}
		
		public Type type() { return type; }
		@SuppressWarnings("unchecked")
		public BoundedList<E> getSource() { return (BoundedList<E>) super.getSource(); }
		
	}
	
	public Set<ListDataListener> listeners();
	public void fireContentsChanged(int ind1, int ind2);
	public void fireIntervalAdded(int ind1, int ind2);
	public void fireIntervalRemoved(int ind1, int ind2);
}
