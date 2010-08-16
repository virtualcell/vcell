package org.vcell.smoldyn.junittesting;

import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.test.FoxSheepExample;
import org.vcell.smoldyn.test.SimulationExample;

public class TestFixturing {

	static Model [] getModels() {
		Model [] models = {new SimulationExample().getExample().getModel(), new FoxSheepExample().getExample().getModel()};
		return models;
	}
}
