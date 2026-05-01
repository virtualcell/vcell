package org.jlibsedml;

import org.jlibsedml.components.Notes;
import org.jlibsedml.components.Parameter;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.Variable;
import org.jlibsedml.components.algorithm.Algorithm;
import org.jlibsedml.components.algorithm.AlgorithmParameter;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.model.*;
import org.jlibsedml.components.output.Curve;
import org.jlibsedml.components.output.DataSet;
import org.jlibsedml.components.output.Output;
import org.jlibsedml.components.output.Surface;
import org.jlibsedml.components.simulation.Simulation;
import org.jlibsedml.components.task.*;

/**
 * Abstract class for any visitor to extend.
 * @author radams
 *
 */
public abstract class SEDMLVisitor {

    public abstract boolean visit(SedBase sedBase);
}
