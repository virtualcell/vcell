package org.vcell.sbml.vcell;

import org.apache.log4j.Logger;
import org.sbml.libsbml.AdvectionCoefficient;
import org.sbml.libsbml.Boundary;
import org.sbml.libsbml.BoundaryCondition;
import org.sbml.libsbml.CoordinateComponent;
import org.sbml.libsbml.DiffusionCoefficient;
import org.sbml.libsbml.SpatialParameterPlugin;
import org.sbml.libsbml.libsbmlConstants;
import org.vcell.sbml.BoundaryTypeAdapter;
import org.vcell.sbml.SBMLHelper;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SpatialAdapter;
import org.vcell.util.ProgrammingException;
import org.vcell.util.VCAssert;

import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;


/**
 * connects {@link SpeciesContextSpec} parameters by role into SBML
 */
public abstract class SpeciesContextSpecSBMLBridge {
	private static Logger lg = Logger.getLogger(SpeciesContextSpecSBMLBridge.class);
	
	/**
	 * package 3D coordinates
	 */
	public static class Coordinates {
		public final CoordinateComponent ccX;
		public final CoordinateComponent ccY;
		public final CoordinateComponent ccZ;

		public Coordinates(org.sbml.libsbml.Geometry geometry, Model vcModel){
			ccX = geometry.getCoordinateComponent(vcModel.getX().getName());
			ccY = geometry.getCoordinateComponent(vcModel.getY().getName());
			ccZ = geometry.getCoordinateComponent(vcModel.getZ().getName());
		}
	}
	
	/**
	 * {@link SpeciesContextSpec} ROLE_ supports
	 */
	public final int roleCode;

	protected SpeciesContextSpecSBMLBridge(int roleCode) {
		super();
		this.roleCode = roleCode;
		if (bridges[roleCode] == null) {
			bridges[roleCode] = this;
		}
		else {
			throw new ProgrammingException("duplicate role code " + roleCode);
		}
	}

	/**
	 * add child if appropriate
	 * @param exporter
	 * @param sCtx
	 * @param specParam
	 * @param sm
	 * @param coords
	 */
	public abstract void addSBMLChildIf(SBMLExporter exporter, SpeciesContext sCtx,  SpeciesContextSpecParameter specParam, StructureMapping sm, Coordinates coords);
	
	/**
	 * does this support diffusion or advection?
	 * @return false by default
	 */
	public boolean isDiffusionOrAdvection( ) {
		return false;
	}

	/**
	 * return specific object for role
	 * @param role
	 * @return concrete {@link SpeciesContextSpecSBMLBridge}  instance
	 * @throws ProgrammingException if invalid role code
	 */
	public static SpeciesContextSpecSBMLBridge forRole(int role)  {
		if (role < bridges.length) {
			SpeciesContextSpecSBMLBridge b = bridges[role];
			if (b != null) {
				if (lg.isDebugEnabled()) {
					lg.debug("Bridging role " + role + ' ' + SpeciesContextSpec.RoleDescriptions[role] + " with " + b); 
				}
				return b;
			}
		}
		throw new ProgrammingException("Unsupported role " + role);
	}

	/**
	 * create the plugin
	 * @param exporter
	 * @param sCtx
	 * @param specParam
	 * @return plugin or null
	 */
	protected SpatialParameterPlugin createPlugin(SBMLExporter exporter, SpeciesContext sCtx, SpeciesContextSpecParameter specParam) {
		org.sbml.libsbml.Parameter sbmlParam = exporter.createSBMLParamFromSpeciesParam(sCtx, specParam);
		if (sbmlParam != null) {
			return SBMLHelper.checkedPlugin(SpatialParameterPlugin.class,sbmlParam,SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		}
		return null;
	}

	private static SpeciesContextSpecSBMLBridge bridges[] = new SpeciesContextSpecSBMLBridge[SpeciesContextSpec.NUM_ROLES];
	
	
	@Override
	public String toString() {
		return getClass().getName() + " role code "  + roleCode + " isDiffusionOrAdvection" + isDiffusionOrAdvection();
	}

	/*-******************************************
	 * roles begin 
	*********************************************/
	private static class NoOp extends SpeciesContextSpecSBMLBridge {

		protected NoOp(int roleCode) {
			super(roleCode);
		}

		@Override
		public void addSBMLChildIf(SBMLExporter exporter, SpeciesContext sCtx, SpeciesContextSpecParameter specParam, StructureMapping sm, Coordinates coords) { }
	}

	/**
	 * XML children which are always added
	 *
	 */
	private static abstract class AlwaysBridge extends SpeciesContextSpecSBMLBridge {

		AlwaysBridge(int roleCode) {
			super(roleCode);
		}

		@Override
		public void addSBMLChildIf(SBMLExporter exporter, SpeciesContext sCtx, SpeciesContextSpecParameter specParam, StructureMapping sm, Coordinates coords) { 
			SpatialParameterPlugin spplugin = createPlugin(exporter, sCtx, specParam);
			if (spplugin  != null) {
				createChild(spplugin,sCtx);
			}
		}

		abstract void createChild(SpatialParameterPlugin spplugin, SpeciesContext sCtx);
	}

	private static class DiffBridge extends AlwaysBridge {

		DiffBridge() {
			super(SpeciesContextSpec.ROLE_DiffusionRate);
		}

		@Override
		void createChild(SpatialParameterPlugin spplugin, SpeciesContext sCtx) {
			DiffusionCoefficient sbmlDiffCoeff = spplugin.createDiffusionCoefficient();
			sbmlDiffCoeff.setVariable(sCtx.getName());
			sbmlDiffCoeff.setType(libsbmlConstants.SPATIAL_DIFFUSIONKIND_ISOTROPIC);
		}
	}

	private static class VelocityBridge extends AlwaysBridge {
		final SpatialAdapter spatialAdapter;
		VelocityBridge(SpatialAdapter sa) {
			super(sa.velocityRole());
			spatialAdapter = sa;
		}

		@Override
		void createChild(SpatialParameterPlugin spplugin, SpeciesContext sCtx) {
			AdvectionCoefficient sbmlAdvCoeffX = spplugin.createAdvectionCoefficient();
			sbmlAdvCoeffX.setVariable(sCtx.getName());
			sbmlAdvCoeffX.setCoordinate(spatialAdapter.sbmlType());
		}
	}
	
	private interface CoordSelector {
		CoordinateComponent component(Coordinates coords);
	}
	
	private interface BoundarySelector {
		Boundary boundary(CoordinateComponent cc);
	}

	private static abstract class BoundaryBridge extends SpeciesContextSpecSBMLBridge {
		final CoordSelector cSelect;
		final BoundarySelector bSelect;

		protected BoundaryBridge(int roleCode, final CoordSelector cs, final BoundarySelector bSel) {
			super(roleCode);
			cSelect = cs;
			bSelect = bSel;
			VCAssert.assertFalse(cSelect ==null, "no csel");
			VCAssert.assertFalse(bSelect ==null, "no bsel");
		}

		@Override
		public void addSBMLChildIf(SBMLExporter exporter, SpeciesContext sCtx, SpeciesContextSpecParameter specParam, StructureMapping sm, Coordinates coords) {
			CoordinateComponent cc = cSelect.component(coords);
			if (cc != null) {
				SpatialParameterPlugin spp = createPlugin(exporter, sCtx, specParam);
				BoundaryCondition sbmlBCXm = spp.createBoundaryCondition();
				sbmlBCXm.setVariable(sCtx.getName());
				int sType = BoundaryTypeAdapter.fromBoundaryConditionType(getBct(sm));
				sbmlBCXm.setType(sType);
				sbmlBCXm.setCoordinateBoundary( bSelect.boundary(cc).getId() );
			}
		}
		
		
		/**
		 * @return true
		 */
		@Override
		public boolean isDiffusionOrAdvection() {
			return true; 
		}

		abstract BoundaryConditionType getBct(StructureMapping sm);
	}
	
	private static CoordSelector xSel = new CoordSelector() { public CoordinateComponent component(Coordinates coords) { return coords.ccX; } };
	private static CoordSelector ySel = new CoordSelector() { public CoordinateComponent component(Coordinates coords) { return coords.ccY; } };
	private static CoordSelector zSel = new CoordSelector() { public CoordinateComponent component(Coordinates coords) { return coords.ccZ; } };
	
	private static BoundarySelector minSel = new BoundarySelector() { public Boundary boundary(CoordinateComponent cc) { return cc.getBoundaryMin(); } }; 
	private static BoundarySelector maxSel = new BoundarySelector() { public Boundary boundary(CoordinateComponent cc) { return cc.getBoundaryMax(); } }; 
	
	private static class Xm extends BoundaryBridge {
		Xm() {
			super(SpeciesContextSpec.ROLE_BoundaryValueXm, xSel,minSel);
		}

		@Override
		BoundaryConditionType getBct(StructureMapping sm) {
			return sm.getBoundaryConditionTypeXm();
		}
	}
	private static class Xp extends BoundaryBridge {
		Xp() {
			super(SpeciesContextSpec.ROLE_BoundaryValueXp, xSel,maxSel);
		}

		@Override
		BoundaryConditionType getBct(StructureMapping sm) {
			return sm.getBoundaryConditionTypeXp();
		}
	}
	
	private static class Ym extends BoundaryBridge {
		Ym() {
			super(SpeciesContextSpec.ROLE_BoundaryValueYm, ySel,minSel);
		}

		@Override
		BoundaryConditionType getBct(StructureMapping sm) {
			return sm.getBoundaryConditionTypeYm();
		}
	}
	
	private static class Yp extends BoundaryBridge {
		Yp() {
			super(SpeciesContextSpec.ROLE_BoundaryValueYp, ySel,maxSel);
		}

		@Override
		BoundaryConditionType getBct(StructureMapping sm) {
			return sm.getBoundaryConditionTypeYp();
		}
	}
	
	private static class Zm extends BoundaryBridge {
		Zm() {
			super(SpeciesContextSpec.ROLE_BoundaryValueZm, zSel,minSel);
		}

		@Override
		BoundaryConditionType getBct(StructureMapping sm) {
			return sm.getBoundaryConditionTypeZm();
		}
	}
	
	private static class Zp extends BoundaryBridge {
		Zp() {
			super(SpeciesContextSpec.ROLE_BoundaryValueZp, zSel,maxSel);
		}

		@Override
		BoundaryConditionType getBct(StructureMapping sm) {
			return sm.getBoundaryConditionTypeZp();
		}
	}
	
	static {
		new NoOp(SpeciesContextSpec.ROLE_InitialCount);
		new NoOp(SpeciesContextSpec.ROLE_InitialConcentration);
		new DiffBridge();
		new VelocityBridge(SpatialAdapter.SPATIAL_X); 
		new VelocityBridge(SpatialAdapter.SPATIAL_Y); 
		new VelocityBridge(SpatialAdapter.SPATIAL_Z); 
		new Xm();
		new Xp();
		new Ym();
		new Yp();
		new Zp();
		new Zm();
	}
	
	
}
