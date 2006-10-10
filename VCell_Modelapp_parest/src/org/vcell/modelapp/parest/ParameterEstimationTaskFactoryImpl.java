package org.vcell.modelapp.parest;

import java.beans.PropertyVetoException;

import org.jdom.Element;
import org.vcell.expression.ExpressionException;
import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskFactory;
import org.vcell.modelapp.analysis.IAnalysisTaskView;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.math.MathException;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.gui.OptTestPanel;

public class ParameterEstimationTaskFactoryImpl implements IAnalysisTaskFactory {

	private static IAnalysisTaskView parameterEstimationTaskView;
	
	public IAnalysisTask create(Element rootElement, SimulationContext simContext) {
		try {
			return cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence.getParameterEstimationTask(rootElement,simContext);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (MappingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (MathException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public IAnalysisTask createNew(SimulationContext simContext) throws Exception {
		return new ParameterEstimationTask(simContext);
	}

	public IAnalysisTaskView getView() {
		if (parameterEstimationTaskView==null){
			parameterEstimationTaskView = new ParameterEstimationTaskView();
		}
		return parameterEstimationTaskView;
	}

	public String getDisplayName() {
		return "Time-series Parameter Estimation";
	}

	public String getShortDescription() {
		return "Time-series Parameter Estimation";
	}

}
