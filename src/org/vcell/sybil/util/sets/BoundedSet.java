package org.vcell.sybil.util.sets;

/*   BoundedSet  --- by Oliver Ruebenacker, UCHC --- June 2008 to August 2009
 *   A set with listeners for changes
 */

import java.util.Collection;
import java.util.Set;

public interface BoundedSet<E> extends Set<E> {

	public static class Event<E>  {
		protected BoundedSet<E> set;
		public Event(BoundedSet<E> set) { this.set = set; }
		public BoundedSet<E> set() { return set; }
	}
	
	public static class EventMinus<E> extends Event<E> {
		public EventMinus(BoundedSet<E> set) { super(set); }
	}
	
	public static class EventPlus<E> extends Event<E> {
		public EventPlus(BoundedSet<E> set) { super(set); }
	}
	
	public static class EventClear<E> extends EventMinus<E> {
		public EventClear(BoundedSet<E> set) { super(set); }
	}
	
	public static class EventAdd<E> extends EventPlus<E> {
		protected E element;
		public EventAdd(BoundedSet<E> set, E element) { super(set); this.element = element; }
		public E element() { return element; }
	}
	
	public static class EventAddAll<E> extends EventPlus<E> {
		protected Collection<? extends E> elements;
		public EventAddAll(BoundedSet<E> set, Collection<? extends E> elements) { 
			super(set); this.elements = elements; 
		}
		public Collection<? extends E> elements() { return elements; }
	}
	
	public static class EventRemove<E> extends EventMinus<E> {
		protected Object object;
		public EventRemove(BoundedSet<E> set, Object object) { super(set); this.object = object; }
		public Object element() { return object; }
	}
	
	public static class EventRemoveAll<E> extends EventMinus<E> {
		protected Collection<?> objects;
		public EventRemoveAll(BoundedSet<E> set, Collection<?> objects) { 
			super(set); this.objects = objects; 
		}
		public Collection<?> elements() { return objects; }
	}
	
	public static class EventRetainAll<E> extends EventMinus<E> {
		protected Collection<?> objects;
		public EventRetainAll(BoundedSet<E> set, Collection<?> objects) { 
			super(set); this.objects = objects; 
		}
		public Collection<?> elements() { return objects; }
	}
	
	public static interface Listener<E> { public void fireEvent(Event<E> event); }

	public Set<Listener<E>> listeners();
	
}
