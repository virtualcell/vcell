package cbit.vcell.mapping;

import java.io.Serializable;

import org.vcell.util.Matchable;

import cbit.vcell.model.ModelProcess;

public interface ModelProcessSpec extends Matchable, Serializable {

	ModelProcess getModelProcess();

	boolean isExcluded();

	boolean isFast();

}
