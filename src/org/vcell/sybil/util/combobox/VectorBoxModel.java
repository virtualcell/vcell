package org.vcell.sybil.util.combobox;

/*   Vector box model  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   A combo box model build on a bounded list
 */

import java.util.HashSet;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.vcell.sybil.util.lists.BoundedList;

public class VectorBoxModel<E> implements ComboBoxModel, ListDataListener {

	protected BoundedList<E> list;
	protected Set<ListDataListener> listeners = new HashSet<ListDataListener>();
	protected Object selected;
	
	public VectorBoxModel() {}
	public VectorBoxModel(BoundedList<E> listNew) { setList(listNew); }
	
	public Object getSelectedItem() { return selected; }
	public void setSelectedItem(Object selectedNew) { selected = selectedNew; }
	public void addListDataListener(ListDataListener l) { listeners.add(l); }
	public Object getElementAt(int index) { return list != null ? list.get(index) : null; }
	public int getSize() { return list != null ? list.size() : 0; }
	public void removeListDataListener(ListDataListener l) { listeners.remove(l); }

	public void setList(BoundedList<E> listNew) {
		int sizeMax = 0;
		if(list != null) { 
			list.listeners().remove(this);
			sizeMax = list.size();
		}
		list = listNew;
		if(list != null) { 
			list.listeners().add(this); 
			if(list.size() > sizeMax) { sizeMax = list.size(); }
		}
		contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, sizeMax - 1));
	}
	
	public void contentsChanged(ListDataEvent e) {
		for(ListDataListener listener : listeners) { listener.contentsChanged(e); }
	}

	public void intervalAdded(ListDataEvent e) {
		for(ListDataListener listener : listeners) { listener.intervalAdded(e); }
	}

	public void intervalRemoved(ListDataEvent e) {
		for(ListDataListener listener : listeners) { listener.intervalRemoved(e); }
	}

}
