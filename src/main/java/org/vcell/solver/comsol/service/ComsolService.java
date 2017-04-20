package org.vcell.solver.comsol.service;

import java.io.File;

import org.vcell.solver.comsol.model.VCCModel;

public interface ComsolService {
	
	void disconnectComsol();

	void solve(VCCModel vccModel, File reportFile, File javaFile, File mphFile);

	void connectComsol();

}