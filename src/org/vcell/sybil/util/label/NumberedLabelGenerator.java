package org.vcell.sybil.util.label;

/*   NumberedLabelGenerator  --- by Oliver Ruebenacker, UCHC --- July to November 2009
 *   Creates unique labels for items by adding numbers at the end
 */

import org.vcell.sybil.util.keys.KeyOfTwo;
import org.vcell.sybil.util.label.LabelMan.LabelAttempt;

public abstract class NumberedLabelGenerator<T> implements LabelMan.LabelGenerator<T> {

	protected static class FirstAttempt<T> extends KeyOfTwo<T, String> 
	implements LabelMan.LabelAttempt<T> {
		public FirstAttempt(T item, String label) { super(item, label); }
		public T item() { return a(); }
		public String label() { return b(); }	
	}

	protected static class NumberedAttempt<T> implements LabelMan.LabelAttempt<T> {

		protected LabelMan.LabelAttempt<T> initialAttempt;
		protected int iAttempt;
		
		public NumberedAttempt(LabelMan.LabelAttempt<T> initialAttempt, int iAttempt) {
			this.initialAttempt = initialAttempt;
			this.iAttempt = iAttempt;
		}
		
		public LabelMan.LabelAttempt<T> initialAttempt() { return initialAttempt; }
		public int iAttempt() { return iAttempt; }
		public T item() { return initialAttempt.item(); }
		public String label() { return initialAttempt.label() + iAttempt; }
		
		
	}
	
	public abstract String baseLabel(T item);
	
	public LabelAttempt<T> firstAttempt(String suggestion) { 
		return new FirstAttempt<T>(null, suggestion); 
	}

	public LabelAttempt<T> firstAttempt(T item) { return new FirstAttempt<T>(item, baseLabel(item)); }

	public LabelAttempt<T> nextAttempt(LabelAttempt<T> attempt) {
		NumberedAttempt<T> nextAttempt = null;
		if(attempt instanceof NumberedAttempt<?>) {
			NumberedAttempt<T> numberedAttempt = (NumberedAttempt<T>) attempt;
			nextAttempt = 
				new NumberedAttempt<T>(numberedAttempt.initialAttempt(), numberedAttempt.iAttempt() + 1);
		} else {
			nextAttempt = new NumberedAttempt<T>(attempt, 2);
		}
		return nextAttempt;
	}

}
