package cbit.vcell.math;

import cbit.vcell.math.MathCompareResults.Difference;
import org.vcell.util.Pair;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes <em>how</em> two math descriptions differ, once something else has established
 * <em>that</em> they do.
 * <p>
 * {@link MathCompareResults.Decision} says whether the math changed and broadly why; that is what gets
 * persisted, and what decides whether a simulation keeps its results. This adds the part a person
 * needs next: which object, which attribute, and the two values.
 *
 * <h2>compareEqual remains the only definition of difference</h2>
 *
 * This class never decides whether two objects differ. It asks {@code compareEqual} — the same method
 * the identical tier uses — and only descends to name an attribute once the answer is already "yes,
 * different". A second field-by-field comparison living beside {@code compareEqual} could drift from
 * it, and that drift is precisely how the Langevin {@code if(false)} stubs went unnoticed for so long.
 * <p>
 * The consequence is that this class cannot produce a false "no differences": if it fails to identify
 * the attribute, it still reports the object as differing, with {@code attribute = "(not identified)"}.
 * That is a prompt to extend the describer, not a silent gap.
 *
 * <h2>Matching</h2>
 *
 * Objects are matched <b>by name</b>, so a difference in emission order is not reported as a
 * difference in content — the same choice the rule-based particle comparison in
 * {@link MathDescription#compareEquivalentCanonicalMath} makes.
 *
 * <h2>Diagnostics must never change an outcome</h2>
 *
 * Every entry point is failure-tolerant. If describing throws, the caller keeps the decision it
 * already had and simply gets less detail.
 */
public class MathDescriptionDifferences {

	/** Reported when compareEqual says two objects differ but no attribute check accounts for it. */
	private static final String UNIDENTIFIED = "(not identified)";

	private MathDescriptionDifferences() {
	}

	/**
	 * @return every difference found, or an empty list — never {@code null}, and never throwing.
	 */
	public static List<Difference> describe(MathDescription math1, MathDescription math2) {
		final List<Difference> differences = new ArrayList<>();
		if (math1 == null || math2 == null) {
			return differences;
		}
		try {
			describeParticleMolecularTypes(math1, math2, differences);
			describeParticleJumpProcesses(math1, math2, differences);
		} catch (Throwable t) {
			// diagnostic only - a failure here must not affect the comparison result
			differences.add(new Difference("<describing differences>", "failed",
					t.getClass().getSimpleName() + ": " + t.getMessage(), null));
		}
		return differences;
	}

	private static void describeParticleMolecularTypes(MathDescription math1, MathDescription math2,
													   List<Difference> differences) {
		final Map<String, ParticleMolecularType> types1 = typesByName(math1);
		final Map<String, ParticleMolecularType> types2 = typesByName(math2);

		for (String name : union(types1.keySet(), types2.keySet())) {
			final ParticleMolecularType type1 = types1.get(name);
			final ParticleMolecularType type2 = types2.get(name);
			final String path = "molecularType '" + name + "'";
			if (type1 == null || type2 == null) {
				differences.add(present(path, type1, type2));
				continue;
			}
			if (type1.compareEqual(type2)) {           // authoritative: nothing to report
				continue;
			}
			final int before = differences.size();
			if (!type1.getClass().equals(type2.getClass())) {
				differences.add(new Difference(path, "type",
						type1.getClass().getSimpleName(), type2.getClass().getSimpleName()));
				continue;
			}
			if (type1 instanceof LangevinParticleMolecularType) {
				final LangevinParticleMolecularType langevin1 = (LangevinParticleMolecularType) type1;
				final LangevinParticleMolecularType langevin2 = (LangevinParticleMolecularType) type2;
				compare(differences, path, "is2D", langevin1.getIs2D(), langevin2.getIs2D());
				final Set<String> links1 = linkNames(langevin1.getInternalLinkSpec());
				final Set<String> links2 = linkNames(langevin2.getInternalLinkSpec());
				if (!links1.equals(links2)) {
					differences.add(new Difference(path, "internalLinks", links1, links2));
				}
			}
			describeComponents(type1, type2, path, differences);
			if (differences.size() == before) {
				differences.add(new Difference(path, UNIDENTIFIED, null, null));
			}
		}
	}

	private static void describeComponents(ParticleMolecularType type1, ParticleMolecularType type2,
										   String typePath, List<Difference> differences) {
		final Map<String, ParticleMolecularComponent> components1 = componentsByName(type1);
		final Map<String, ParticleMolecularComponent> components2 = componentsByName(type2);

		for (String name : union(components1.keySet(), components2.keySet())) {
			final ParticleMolecularComponent component1 = components1.get(name);
			final ParticleMolecularComponent component2 = components2.get(name);
			final String path = typePath + " / site '" + name + "'";
			if (component1 == null || component2 == null) {
				differences.add(present(path, component1, component2));
				continue;
			}
			if (component1.compareEqual(component2)) {
				continue;
			}
			final int before = differences.size();
			if (component1 instanceof LangevinParticleMolecularComponent
					&& component2 instanceof LangevinParticleMolecularComponent) {
				final LangevinParticleMolecularComponent site1 = (LangevinParticleMolecularComponent) component1;
				final LangevinParticleMolecularComponent site2 = (LangevinParticleMolecularComponent) component2;
				compareExpressions(differences, path, "radius", site1.getRadius(), site2.getRadius());
				compareExpressions(differences, path, "diffusionRate", site1.getDiffusionRate(), site2.getDiffusionRate());
				compare(differences, path, "location", site1.getLocation(), site2.getLocation());
				compare(differences, path, "coordinate", site1.getCoordinate(), site2.getCoordinate());
				compare(differences, path, "color",
						site1.getColor() == null ? null : site1.getColor().getName(),
						site2.getColor() == null ? null : site2.getColor().getName());
			}
			if (differences.size() == before) {
				differences.add(new Difference(path, UNIDENTIFIED, null, null));
			}
		}
	}

	private static void describeParticleJumpProcesses(MathDescription math1, MathDescription math2,
													  List<Difference> differences) {
		final Map<String, ParticleJumpProcess> processes1 = jumpProcessesByName(math1);
		final Map<String, ParticleJumpProcess> processes2 = jumpProcessesByName(math2);

		for (String name : union(processes1.keySet(), processes2.keySet())) {
			final ParticleJumpProcess process1 = processes1.get(name);
			final ParticleJumpProcess process2 = processes2.get(name);
			final String path = "particleJumpProcess '" + name + "'";
			if (process1 == null || process2 == null) {
				differences.add(present(path, process1, process2));
				continue;
			}
			if (process1.compareEqual(process2)) {
				continue;
			}
			final int before = differences.size();
			if (process1 instanceof LangevinParticleJumpProcess
					&& process2 instanceof LangevinParticleJumpProcess) {
				final LangevinParticleJumpProcess langevin1 = (LangevinParticleJumpProcess) process1;
				final LangevinParticleJumpProcess langevin2 = (LangevinParticleJumpProcess) process2;
				compare(differences, path, "subtype", langevin1.getSubtype(), langevin2.getSubtype());
				compare(differences, path, "transitionCondition",
						langevin1.getTransitionCondition(), langevin2.getTransitionCondition());
				compareExpressions(differences, path, "bondLength",
						langevin1.getBondLength(), langevin2.getBondLength());
			}
			if (differences.size() == before) {
				differences.add(new Difference(path, UNIDENTIFIED, null, null));
			}
		}
	}

	private static Difference present(String path, Object in1, Object in2) {
		return new Difference(path, "present", in1 == null ? null : "yes", in2 == null ? null : "yes");
	}

	private static void compare(List<Difference> differences, String path, String attribute,
								Object value1, Object value2) {
		final String string1 = value1 == null ? null : value1.toString();
		final String string2 = value2 == null ? null : value2.toString();
		if (string1 == null ? string2 != null : !string1.equals(string2)) {
			differences.add(new Difference(path, attribute, string1, string2));
		}
	}

	/**
	 * Expressions are compared for functional equivalence, matching how {@code compareEqual} treats
	 * them — otherwise this would report {@code r} and {@code r + 0} as a difference that the
	 * comparison itself does not see.
	 */
	private static void compareExpressions(List<Difference> differences, String path, String attribute,
										   cbit.vcell.parser.Expression value1, cbit.vcell.parser.Expression value2) {
		if (value1 != null && value2 != null
				&& cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(value1, value2)) {
			return;
		}
		compare(differences, path, attribute,
				value1 == null ? null : value1.infix(), value2 == null ? null : value2.infix());
	}

	private static Set<String> linkNames(Set<Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent>> links) {
		final Set<String> names = new LinkedHashSet<>();
		if (links != null) {
			for (Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent> link : links) {
				names.add(link.one.getName() + VCML.LinkSeparator + link.two.getName());
			}
		}
		return names;
	}

	private static Map<String, ParticleMolecularType> typesByName(MathDescription math) {
		final Map<String, ParticleMolecularType> byName = new LinkedHashMap<>();
		if (math.getParticleMolecularTypes() != null) {
			for (ParticleMolecularType type : math.getParticleMolecularTypes()) {
				byName.put(type.getName(), type);
			}
		}
		return byName;
	}

	private static Map<String, ParticleMolecularComponent> componentsByName(ParticleMolecularType type) {
		final Map<String, ParticleMolecularComponent> byName = new LinkedHashMap<>();
		if (type.getComponentList() != null) {
			for (ParticleMolecularComponent component : type.getComponentList()) {
				byName.put(component.getName(), component);
			}
		}
		return byName;
	}

	/** Jump processes across every subdomain, keyed by name; names are unique within a math. */
	private static Map<String, ParticleJumpProcess> jumpProcessesByName(MathDescription math) {
		final Map<String, ParticleJumpProcess> byName = new LinkedHashMap<>();
		for (Enumeration<SubDomain> subDomains = math.getSubDomains(); subDomains.hasMoreElements(); ) {
			final List<ParticleJumpProcess> processes = subDomains.nextElement().getParticleJumpProcesses();
			if (processes == null) {
				continue;
			}
			for (ParticleJumpProcess process : processes) {
				byName.put(process.getName(), process);
			}
		}
		return byName;
	}

	/** Names from both sides, first math's order first, so a report reads in a stable order. */
	private static Set<String> union(Set<String> names1, Set<String> names2) {
		final Set<String> union = new LinkedHashSet<>(names1);
		union.addAll(names2);
		return union;
	}
}
