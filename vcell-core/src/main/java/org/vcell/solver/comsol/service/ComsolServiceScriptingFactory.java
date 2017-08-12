package org.vcell.solver.comsol.service;

public class ComsolServiceScriptingFactory implements ComsolServiceFactory {

	public ComsolServiceScriptingFactory() {
	}

	@Override
	public ComsolService newComsolService() {
		return new ComsolServiceScripting();
	}

}
