package org.vcell.solver.comsol.service;

public interface ComsolServiceFactory {
	
	public static ComsolServiceFactory instance = new ComsolServiceScriptingFactory();

	ComsolService newComsolService();

}
