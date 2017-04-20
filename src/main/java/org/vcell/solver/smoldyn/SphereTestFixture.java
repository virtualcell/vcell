package org.vcell.solver.smoldyn;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.vcell.util.ProgrammingException;

import cbit.vcell.geometry.concept.PolygonImmutable;
import cbit.vcell.geometry.concept.ThreeSpacePoint;
import cbit.vcell.geometry.concept.ThreeSpacePointImmutable;
import cbit.vcell.geometry.surface.IcoSphere;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Triangle;
import cbit.vcell.solver.Simulation;

/**
 * class to replace Virtual Cell tessellation of surface with spherical.
 * Detects Simulations that:
 * <ul>
 * <li> have "sphereN" in name of Simulation</li>
 * <li> have comment line specifying sphere: /* sphere = (5,5,5) r=4.5   ...   </li>
 * </ul>
 */
public class SphereTestFixture {
	private final Simulation sim;
	private Boolean bSphereTest;
	private int level;
	private Pattern sphereLine;
	private static Logger LG = Logger.getLogger(SphereTestFixture.class);
	private double center[];
	private double radius;

	public SphereTestFixture(Simulation sim) {
		super();
		this.sim = sim;
		bSphereTest = null;
	}

	/**
	 * @return test spherical tessellation
	 * @throws ProgrammingException if not {@link #isSphereTest()}
	 */
	public List<? extends Polygon> getTestSphere(  ) {
		if (isSphereTest()) {
			IcoSphere icoSphere = IcoSphere.get( );
			List<PolygonImmutable> tessel = icoSphere.getTessellation(level);
			ThreeSpacePoint centerPoint = new ThreeSpacePointImmutable(center[0],center[1],center[2]);
			List<Triangle> tl = Triangle.scale(tessel, radius, centerPoint); 
			return tl;
		}
		throw new ProgrammingException("getTestSphere called on non test Simulation");
	}

	/**
	 * @return true if Simulation is set to use test sphere
	 */
	public boolean isSphereTest( ) {
		if (bSphereTest != null) {
			return bSphereTest;
		}
		try {
			sphereLine = Pattern.compile(".*sphere\\s*=\\s*\\((.*)\\)\\s*r\\s*=\\s*(\\d+)*.*",Pattern.CASE_INSENSITIVE);
			bSphereTest = false;
			String sname = sim.getName();
			Pattern p = Pattern.compile(".*sphere(\\d).*",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(sname);
			if (m.matches()) {
				String lvlStr = m.group(1);
				level = Integer.parseInt(lvlStr);
				String vcml = sim.getMathDescription().getVCML_database(true);
				String[] lines = StringUtils.split(vcml, '\n');
				for (String ln : lines) {
					if (parseSphereParams(ln)) {
						return (bSphereTest = true) ; //assignment intentional
					}
				}
			}
		} catch (Exception e) {
			LG.warn("error parsing Simulation",e);
		}
		finally {
			sphereLine = null;  //only needed for parseSphereParams
		}

		return bSphereTest;

	}

	/**
	 * @param line not null
	 * @return true matches required comment for center and radius
	 */
	private boolean parseSphereParams(String line) {
		try {
			Matcher m = sphereLine.matcher(line);
			if (m.matches()) {
				String coords = m.group(1);
				String[] parts = StringUtils.split(coords,',');
				if (parts.length != 3) {
					throw new IllegalArgumentException(coords + " not of format x,y,z");
				}
				center = new double[3];
				for (int i = 0; i < 3; i++) {
					center[i] = parseD(parts[i], "center " + (i + 1) );
				}
				radius = parseD(m.group(2),"radius");
				return true;
			}
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("Error parseing line " + line + nfe.getMessage(),nfe);
		}
		return false;
	}

	/**
	 * convert String to double with context 
	 * @param string
	 * @param context
	 * @return converted double
	 * @throws NumberFormatException if not valid
	 */
	private double parseD(String string, String context) {
		try {
			return Double.parseDouble(string);
		}
		catch (NumberFormatException nfe) {
			throw new NumberFormatException("Can't convert "  +context + ' ' + string  + " to double");
		}
	}

}