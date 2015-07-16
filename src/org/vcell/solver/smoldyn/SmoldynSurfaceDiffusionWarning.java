package org.vcell.solver.smoldyn;

import java.awt.Component;
import java.util.Objects;

import org.vcell.util.UserCancelException;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;

/**
 * temporary class to standardized Smoldyn inaccuracy warning; delete after Smoldyn updated (est Sep 2015)
 * @author GWeatherby
 */
public class SmoldynSurfaceDiffusionWarning {
	private static final String TEMP_SMOLDYN_HYBRID_WARNING = 
			"<html>An inaccuracy has been found in the Smoldyn simulator for spatial stochastic models that require membrane (surface) diffusion.<br>" 
			+ "This is being repaired by our collaborator Steve Andrews, the developer of Smoldyn, and should be fixed by September, 2015.<br>" 
//			+ "Please contact Virtual Cell Support <a href=\"mailto:vcell_support@uchc.edu\">vcell_support@uchc.edu</a> if you have any questions.</html>";
			+ "Please contact Virtual Cell Support vcell_support@uchc.edu if you have any questions.</html>";
	private static final ThreadLocal<Component> guiComponent = new ThreadLocal<>();
	
	public static void acknowledgeWarning(Component requester)  {
		if (! PopupGenerator.queryOKCancelWarningDialog(requester, "Warning",TEMP_SMOLDYN_HYBRID_WARNING) ) {
			throw new UserCancelException("user cancelled");
		}
	}
	
	/**
	 * avoid requiring graphics code in MathModel et. al.
	 * set component, in case needed for popup deeper in call stack
	 * @param component
	 */
	public static void setGUIContext(Component component) {
		guiComponent.set(component);
	}
	
	/**
	 * avoid requiring graphics code in MathModel et. al.
	 * call {@link #acknowledgeWarning()} with previously stored {@link Component}
	 * @throws NullPointerException if component not previously set
	 */
	public static void acknowledgeWarning( )  {
		Component cmpt = guiComponent.get();
		Objects.requireNonNull(cmpt);
		acknowledgeWarning(cmpt);
	}
	
	/**
	 * @param sc not null
	 * @return true if stoch, not rule based, and spatial
	 */
	public static boolean isSmoldynOrHybrid(SimulationContext sc) {
		if (!sc.isStoch() ) {
			return false;
		}
		Geometry g = sc.getGeometry();
		return g.getDimension() > 0;
	}
	/**
	 * @param md not null
	 * @return true warning not already accepted 
	 */
	public static boolean isUnacknowledgedSmoldynOrHybrid(MathModel.TempSmoldynWarningAPI warningAPI, MathDescription md) {
		if ( !warningAPI.isWarningAcknowledged() && ( md.isSpatialHybrid() || md.isSpatialStoch()) ){
			//if there is existing Smoldyn / Hybrid simulation, consider acknowledged already
			for (Simulation existing : warningAPI.getModel().getSimulations()) {
				SolverTaskDescription td = existing.getSolverTaskDescription();
				SolverDescription sd = td.getSolverDescription();
				if (sd.supports(SolverDescription.SpatialHybridFeatureSet) || sd.supports(SolverDescription.SpatialStochasticFeatureSet) ) {
					warningAPI.acknowledge();
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
}
