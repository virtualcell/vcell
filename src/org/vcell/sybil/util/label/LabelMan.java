package org.vcell.sybil.util.label;

/*   LabelMan  --- by Oliver Ruebenacker, UCHC --- July 2009
 *   Creates and manages unique labels for items
 */

import java.util.HashMap;
import java.util.Map;

public class LabelMan<T> {

	public static interface LabelAttempt<T> {
		public T item();
		public String label();
	}
	
	public static interface LabelGenerator<T> {
		public LabelAttempt<T> firstAttempt(String suggestion);
		public LabelAttempt<T> firstAttempt(T item);
		public LabelAttempt<T> nextAttempt(LabelAttempt<T> attempt);
	}
	
	protected LabelGenerator<T> generator;
	protected Map<T, String> itemToLabel = new HashMap<T, String>();
	protected Map<String, T> labelToItem = new HashMap<String, T>();
	
	public LabelMan(LabelGenerator<T> generator) { this.generator = generator; }
	
	public T item(String label) { return labelToItem.get(label); }
	
	public String label(String suggestion) {
		String label;
		LabelAttempt<T> attempt = generator.firstAttempt(suggestion);
		while(labelToItem.containsKey(attempt.label())) { 
			attempt = generator.nextAttempt(attempt); 
		}
		label = attempt.label();
		return label;
	}

	public String label(T item) {
		String label = itemToLabel.get(item);
		if(label == null) {
			LabelAttempt<T> attempt = generator.firstAttempt(item);
			while(labelToItem.containsKey(attempt.label())) { 
				attempt = generator.nextAttempt(attempt); 
			}
			label = attempt.label();
			put(item, label);
		}
		return label;
	}

	public void put(T item, String label) {
		itemToLabel.put(item, label);
		labelToItem.put(label, item);
	}
	
	public T removeLabel(String label) {
		T item = labelToItem.remove(label);
		itemToLabel.remove(item);
		return item;
	}
	
	public String removeItem(T item) {
		String label = itemToLabel.remove(item);
		labelToItem.remove(label);
		return label;
	}
	
}
