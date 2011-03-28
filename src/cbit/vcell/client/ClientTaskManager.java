package cbit.vcell.client;

import java.util.Hashtable;

import javax.swing.JComponent;

import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;

public class ClientTaskManager {
	public static AsynchClientTask[] newApplication(final BioModel bioModel, final boolean isStoch) {		
		
		AsynchClientTask task0 = new AsynchClientTask("create application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String newApplicationName = bioModel.getFreeSimulationContextName();
				SimulationContext newSimulationContext = bioModel.addNewSimulationContext(newApplicationName, isStoch);
				hashTable.put("newSimulationContext", newSimulationContext);
			}
		};
		AsynchClientTask task1 = new AsynchClientTask("process geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
				newSimulationContext.getGeometry().precomputeAll();
			}
		};
		return new AsynchClientTask[] {task0, task1};
	}

	public static AsynchClientTask[] copyApplication(final JComponent requester, final BioModel bioModel, final SimulationContext simulationContext, final boolean bSpatial, final boolean bStochastic) {	
		//get valid application name
		String newApplicationName = null;
		String baseName = "Copy of " + simulationContext.getName();
		int count = 0;
		while (true) {
			if (count == 0) {
				newApplicationName = baseName;
			} else {
				newApplicationName = baseName + " " + count;
			}
			if (bioModel.getSimulationContext(newApplicationName) == null) {
				break;
			}
			count ++;
		}
		
		final String newName = newApplicationName;
		AsynchClientTask task1 = new AsynchClientTask("preparing to copy", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = ClientTaskManager.copySimulationContext(simulationContext, newName, bSpatial, bStochastic);
				newSimulationContext.getGeometry().precomputeAll();
				if (newSimulationContext.isSameTypeAs(simulationContext)) { 
					newSimulationContext.refreshMathDescription();
				}
				hashTable.put("newSimulationContext", newSimulationContext);
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("copying application and simulations", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
				bioModel.addSimulationContext(newSimulationContext);
				if (newSimulationContext.isSameTypeAs(simulationContext)) { 
					for (Simulation sim : simulationContext.getSimulations()) {
						Simulation clonedSimulation = new Simulation(sim, false);
						clonedSimulation.setMathDescription(newSimulationContext.getMathDescription());
						clonedSimulation.setName(simulationContext.getBioModel().getFreeSimulationName());
						newSimulationContext.addSimulation(clonedSimulation);
					}
				} else {
					if (simulationContext.getSimulations().length > 0) {
						DialogUtils.showWarningDialog(requester, "Simulations are not copied because new application is of different type.");
					}
				}
			}
		};
		return new AsynchClientTask[] { task1, task2};			
	}

	public static SimulationContext copySimulationContext(SimulationContext srcSimContext, String newSimulationContextName, boolean bSpatial, boolean bStoch) throws java.beans.PropertyVetoException, ExpressionException, MappingException, GeometryException, ImageException {
		Geometry newClonedGeometry = new Geometry(srcSimContext.getGeometry());
		newClonedGeometry.precomputeAll();
		//if stoch copy to ode, we need to check is stoch is using particles. If yes, should convert particles to concentraton.
		//the other 3 cases are fine. ode->ode, ode->stoch, stoch-> stoch 
		SimulationContext destSimContext = new SimulationContext(srcSimContext,newClonedGeometry, bStoch);
		if(srcSimContext.isStoch() && !srcSimContext.isUsingConcentration() && !bStoch)
		{
			try {
				destSimContext.convertSpeciesIniCondition(true);
			} catch (MappingException e) {
				e.printStackTrace();
				throw new java.beans.PropertyVetoException(e.getMessage(), null);
			}
		}
		if (srcSimContext.getGeometry().getDimension() > 0 && !bSpatial) { // copy the size over
			destSimContext.setGeometry(new Geometry("nonspatial", 0));
			StructureMapping srcStructureMappings[] = srcSimContext.getGeometryContext().getStructureMappings();
			StructureMapping destStructureMappings[] = destSimContext.getGeometryContext().getStructureMappings();
			for (StructureMapping destStructureMapping : destStructureMappings) {
				for (StructureMapping srcStructureMapping : srcStructureMappings) {
					if (destStructureMapping.getStructure() == srcStructureMapping.getStructure()) {
						if (srcStructureMapping.getUnitSizeParameter() != null) {
							Expression sizeRatio = srcStructureMapping.getUnitSizeParameter().getExpression();
							GeometryClass srcGeometryClass = srcStructureMapping.getGeometryClass();
							GeometricRegion[] srcGeometricRegions = srcSimContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(srcGeometryClass);
							if (srcGeometricRegions != null) {
								double size = 0;
								for (GeometricRegion srcGeometricRegion : srcGeometricRegions) {
									size += srcGeometricRegion.getSize();
								}
								destStructureMapping.getSizeParameter().setExpression(Expression.mult(sizeRatio, new Expression(size)));
							}
						}
						break;
					}
				}
			}
		}
		destSimContext.setName(newSimulationContextName);	
		return destSimContext;
	}
}
