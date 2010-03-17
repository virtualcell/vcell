package org.vcell.sybil.rdf;

/*   TokenCat  --- by Oliver Ruebenacker, UCHC --- January 2009
 *   The three categories of statement tokens: subject, object and predicate
 */


import org.vcell.sybil.util.category.Category;
import org.vcell.sybil.util.category.CategoryNoParams;

public interface TokenCategory extends Category<TokenCategory> {

	public class SubjectToken extends CategoryNoParams<TokenCategory> implements TokenCategory {

		public SubjectToken() { super(SubjectToken.class); }
		public int rank() { return 0; }
		public String toString() { return "Subject"; }
	}
	
	public class PredicateToken extends CategoryNoParams<TokenCategory> implements TokenCategory {

		public PredicateToken() { super(PredicateToken.class); }
		public int rank() { return 1; }
		public String toString() { return "Predicate"; }
	}
	
	public class ObjectToken extends CategoryNoParams<TokenCategory> implements TokenCategory {

		public ObjectToken() { super(ObjectToken.class); }
		public int rank() { return 2; }
		public String toString() { return "Object"; }
	}
	
	public static SubjectToken SUBJECT = new SubjectToken();
	public static PredicateToken PREDICATE = new PredicateToken();
	public static ObjectToken OBJECT = new ObjectToken();
	
}
