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

/*   ARQUtil  --- by Oliver Ruebenacker, UCHC --- June 2008 to March 2009
 *   Methods useful for building ARQ queries
 */

import java.util.HashMap;
import java.util.Map;
import com.hp.hpl.jena.sparql.core.Var;

public class ARQUtil {

	public static Var var(String name) { return Var.alloc(name); }
	
	static public interface VarMap extends Map<String, Var> {
		public boolean add(String name);
	}
	
	static public class VarHashMap extends HashMap<String, Var>  implements VarMap {

		private static final long serialVersionUID = -1643993222557360267L;

		public boolean add(String name) {
			Var var = get(name);
			if(var == null) { 
				var = var(name);
				put(name, var);
				return true;
			}
			return false;
		}

	}
	
}
