package org.vcell.optimization;

import java.io.IOException;

import org.vcell.optimization.thrift.OptProblem;

public interface OptService {

	String submit(OptProblem optProblem) throws IOException;

	OptRunContext getOptRunContextByOptimizationId(String optimizationId);

}