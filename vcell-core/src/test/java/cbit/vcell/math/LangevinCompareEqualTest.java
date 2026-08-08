package cbit.vcell.math;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Coordinate;
import org.vcell.util.springsalad.Colors;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every SpringSaLaD-specific field must take part in {@code compareEqual}.
 * <p>
 * These fields were previously invisible to comparison - all three Langevin overrides were
 * {@code if(false)} stubs that delegated straight to the superclass - so two maths differing in a
 * site's radius, diffusion rate or location, or a reaction's bond length, subtype or transition
 * condition, reported as <em>identical</em>. That is the dangerous direction: {@code Simulation}
 * and {@code MathModel} both decide "has the math changed?" through {@code compareEqual}, so a
 * false "identical" can leave stale simulation results attached to an edited model.
 * <p>
 * Each test changes exactly one field and asserts the difference is seen, so a regression names the
 * field it lost. Comparison here is the <em>identical</em> tier and is exact; tolerance belongs to
 * the equivalence tier, as it does for expressions
 * ({@code ExpressionUtils.functionallyEquivalent}).
 */
@Tag("Fast")
public class LangevinCompareEqualTest {

	private static LangevinParticleMolecularComponent component(String name) {
		final LangevinParticleMolecularComponent component = new LangevinParticleMolecularComponent("mt_" + name, name);
		component.setRadius(1.0);
		component.setDiffusionRate(2.0);
		component.setLocation("Intracellular");
		component.setCoordinate(new Coordinate(1, 2, 3));
		component.setColor(Colors.RED);
		return component;
	}

	private static LangevinParticleMolecularType molecularType() {
		final LangevinParticleMolecularType molecularType = new LangevinParticleMolecularType("A");
		molecularType.addMolecularComponent(component("s0"));
		return molecularType;
	}

	private static LangevinParticleJumpProcess jumpProcess() {
		final LangevinParticleJumpProcess jumpProcess = new LangevinParticleJumpProcess(
				"jp", new ArrayList<ParticleVariable>(),
				new MacroscopicRateConstant(new cbit.vcell.parser.Expression(1.0)),
				new ArrayList<Action>(), null);
		jumpProcess.setSubtype(LangevinParticleJumpProcess.ParticleSubtype.BINDING);
		jumpProcess.setBondLength(1.5);
		return jumpProcess;
	}

	@Test
	public void identicalComponentsCompareEqual() {
		assertTrue(component("s0").compareEqual(component("s0")), "identical components must compare equal");
	}

	@Test
	public void componentRadiusIsCompared() {
		final LangevinParticleMolecularComponent changed = component("s0");
		changed.setRadius(9.0);
		assertFalse(component("s0").compareEqual(changed), "a different radius must be detected");
	}

	@Test
	public void componentDiffusionRateIsCompared() {
		final LangevinParticleMolecularComponent changed = component("s0");
		changed.setDiffusionRate(9.0);
		assertFalse(component("s0").compareEqual(changed), "a different diffusion rate must be detected");
	}

	@Test
	public void componentLocationIsCompared() {
		final LangevinParticleMolecularComponent changed = component("s0");
		changed.setLocation("Extracellular");
		assertFalse(component("s0").compareEqual(changed), "a different location must be detected");
	}

	@Test
	public void componentCoordinateIsCompared() {
		final LangevinParticleMolecularComponent changed = component("s0");
		changed.setCoordinate(new Coordinate(9, 9, 9));
		assertFalse(component("s0").compareEqual(changed), "a different coordinate must be detected");
	}

	@Test
	public void componentColorIsCompared() {
		final LangevinParticleMolecularComponent changed = component("s0");
		changed.setColor(Colors.BLUE);
		assertFalse(component("s0").compareEqual(changed), "a different colour must be detected");
	}

	@Test
	public void molecularTypeIs2DIsCompared() {
		final LangevinParticleMolecularType changed = molecularType();
		changed.setIs2D(true);
		assertFalse(molecularType().compareEqual(changed), "a different is2D flag must be detected");
	}

	@Test
	public void jumpProcessSubtypeIsCompared() {
		final LangevinParticleJumpProcess changed = jumpProcess();
		changed.setSubtype(LangevinParticleJumpProcess.ParticleSubtype.TRANSITION);
		assertFalse(jumpProcess().compareEqual(changed), "a different subtype must be detected");
	}

	@Test
	public void jumpProcessTransitionConditionIsCompared() {
		final LangevinParticleJumpProcess changed = jumpProcess();
		changed.setTransitionCondition(LangevinParticleJumpProcess.ParticleTransitionCondition.BOUND);
		assertFalse(jumpProcess().compareEqual(changed), "a different transition condition must be detected");
	}

	@Test
	public void jumpProcessBondLengthIsCompared() {
		final LangevinParticleJumpProcess changed = jumpProcess();
		changed.setBondLength(9.0);
		assertFalse(jumpProcess().compareEqual(changed), "a different bond length must be detected");
	}

	/**
	 * The superclass accepts any {@code ParticleMolecularType}, so an {@code instanceof} test in the
	 * subclass made the answer depend on which side the call started from: {@code plain.compareEqual(langevin)}
	 * could be true while {@code langevin.compareEqual(plain)} was false. {@code Compare.isEqual(List,List)}
	 * only ever evaluates {@code v1[i].compareEqual(v2[i])}, so that asymmetry silently made list
	 * comparison depend on argument order.
	 */
	@Test
	public void comparisonIsSymmetricAcrossTheClassHierarchy() {
		final LangevinParticleMolecularType langevin = new LangevinParticleMolecularType("A");
		final ParticleMolecularType plain = new ParticleMolecularType("A");
		assertFalse(langevin.compareEqual(plain), "a Langevin type must not equal a plain one");
		assertFalse(plain.compareEqual(langevin), "...and the same must hold in the other direction");

		final List<ParticleMolecularType> left = List.of(langevin);
		final List<ParticleMolecularType> right = List.of(plain);
		assertFalse(org.vcell.util.Compare.isEqual(left, right), "list comparison must agree, either order");
		assertFalse(org.vcell.util.Compare.isEqual(right, left), "list comparison must agree, either order");
	}
}
