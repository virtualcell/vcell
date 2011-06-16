/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf;

/*   TokenCat  --- by Oliver Ruebenacker, UCHC --- January 2009
 *   The three categories of statement tokens: subject, object and predicate
 */


import org.vcell.sybil.util.category.Category;
import org.vcell.sybil.util.category.CategoryNoParams;

public interface TokenCategory extends Category<TokenCategory> {

	public class SubjectToken extends CategoryNoParams<TokenCategory> implements TokenCategory {

		public SubjectToken() { super(SubjectToken.class); }
		@Override
		public int rank() { return 0; }
		@Override
		public String toString() { return "Subject"; }
	}
	
	public class PredicateToken extends CategoryNoParams<TokenCategory> implements TokenCategory {

		public PredicateToken() { super(PredicateToken.class); }
		@Override
		public int rank() { return 1; }
		@Override
		public String toString() { return "Predicate"; }
	}
	
	public class ObjectToken extends CategoryNoParams<TokenCategory> implements TokenCategory {

		public ObjectToken() { super(ObjectToken.class); }
		@Override
		public int rank() { return 2; }
		@Override
		public String toString() { return "Object"; }
	}
	
	public static SubjectToken SUBJECT = new SubjectToken();
	public static PredicateToken PREDICATE = new PredicateToken();
	public static ObjectToken OBJECT = new ObjectToken();
	
}
