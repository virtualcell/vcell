package cbit.vcell.message.server.htc.slurm;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A solver family: one container image (an ORAS SIF reference) and the solvers
 * ({@code SolverDescription} names) that run in it, read from the pair of properties
 * {@code vcell.htc.vcell<family>.apptainer.image} and {@code vcell.htc.vcell<family>.solver.list}
 * (environment {@code VCELL_HTC_VCELL<FAMILY>_APPTAINER_IMAGE} / {@code _SOLVER_LIST}).
 *
 * <p>{@link #FAMILIES} is searched in order and the first family whose list names the solver
 * wins. A family is skipped when its list is empty or its image is unset, so a site configures
 * only the families it runs, and moving a solver to its own image is a configuration change:
 * take its name off the legacy {@code solvers} list and put it on its family's list.
 *
 * <p>A solver named on more than one list is not an error: the first family in order wins and a
 * warning is logged (once per solver and set of families). Existing deployments already name some
 * solvers on several lists (the batch list overlaps the others), so refusing such a configuration
 * would stop them submitting.
 *
 * <p>See docs/plan-solver-repos.md (PR B1) and docs/apptainer-image-build.md.
 */
public final class SolverImageFamily {

	private static final Logger lg = LogManager.getLogger(SolverImageFamily.class);

	/** Families in search order; the legacy {@code solvers} image and then {@code batch} come last. */
	public static final List<SolverImageFamily> FAMILIES = List.of(
			new SolverImageFamily("fenics", PropertyLoader.htc_vcellfenics_apptainer_image, PropertyLoader.htc_vcellfenics_solver_list),
			new SolverImageFamily("fvsolver", PropertyLoader.htc_vcellfvsolver_apptainer_image, PropertyLoader.htc_vcellfvsolver_solver_list),
			new SolverImageFamily("ode", PropertyLoader.htc_vcellode_apptainer_image, PropertyLoader.htc_vcellode_solver_list),
			new SolverImageFamily("stochastic", PropertyLoader.htc_vcellstochastic_apptainer_image, PropertyLoader.htc_vcellstochastic_solver_list),
			new SolverImageFamily("nfsim", PropertyLoader.htc_vcellnfsim_apptainer_image, PropertyLoader.htc_vcellnfsim_solver_list),
			new SolverImageFamily("mbsolver", PropertyLoader.htc_vcellmbsolver_apptainer_image, PropertyLoader.htc_vcellmbsolver_solver_list),
			new SolverImageFamily("hy3s", PropertyLoader.htc_vcellhy3s_apptainer_image, PropertyLoader.htc_vcellhy3s_solver_list),
			new SolverImageFamily("chombo", PropertyLoader.htc_vcellchombo_apptainer_image, PropertyLoader.htc_vcellchombo_solver_list),
			new SolverImageFamily("solvers", PropertyLoader.htc_vcellsolvers_apptainer_image, PropertyLoader.htc_vcellsolvers_solver_list),
			new SolverImageFamily("batch", PropertyLoader.htc_vcellbatch_apptainer_image, PropertyLoader.htc_vcellbatch_solver_list)
	);

	private static final Set<String> warned = ConcurrentHashMap.newKeySet();

	public final String name;
	public final String imageProperty;
	public final String solverListProperty;

	private SolverImageFamily(String name, String imageProperty, String solverListProperty) {
		this.name = name;
		this.imageProperty = imageProperty;
		this.solverListProperty = solverListProperty;
	}

	/** The configured solver names, trimmed; empty when the list is unset or blank. */
	List<String> solverList() {
		String value = PropertyLoader.getProperty(solverListProperty, "");
		if (value == null || value.isBlank()) {
			return List.of();
		}
		return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
	}

	/** The configured image, or null when unset or blank. */
	String image() {
		String value = PropertyLoader.getProperty(imageProperty, "");
		return (value == null || value.isBlank()) ? null : value.trim();
	}

	/**
	 * The image for {@code solverName}: that of the first family, in {@link #FAMILIES} order, whose
	 * image is set and whose list names the solver.
	 *
	 * @throws RuntimeException when no configured family names the solver
	 */
	static String resolveImage(String solverName) {
		SolverImageFamily chosen = null;
		String chosenImage = null;
		List<String> matching = new ArrayList<>();
		List<String> skippedNoImage = new ArrayList<>();
		StringBuilder notFound = new StringBuilder("solverName=" + solverName + " not in ");
		boolean first = true;
		for (SolverImageFamily family : FAMILIES) {
			List<String> solverList = family.solverList();
			notFound.append(first ? "" : " or ").append("vcell").append(family.name).append("_solverList=").append(solverList);
			first = false;
			if (!solverList.contains(solverName)) {
				continue;
			}
			String image = family.image();
			if (image == null) {
				skippedNoImage.add(family.name);
				continue;
			}
			matching.add(family.name);
			if (chosen == null) {
				chosen = family;
				chosenImage = image;
			}
		}
		if (!skippedNoImage.isEmpty() && warned.add("noimage:" + solverName + skippedNoImage)) {
			lg.warn("solverName=" + solverName + " is listed by solver famil" + (skippedNoImage.size() == 1 ? "y " : "ies ")
					+ skippedNoImage + " whose apptainer image is unset; skipping " + (skippedNoImage.size() == 1 ? "it" : "them"));
		}
		if (chosen == null) {
			if (!skippedNoImage.isEmpty()) {
				notFound.append(" (listed, but skipped because the image is unset: ").append(skippedNoImage).append(")");
			}
			throw new RuntimeException(notFound.toString());
		}
		if (matching.size() > 1 && warned.add("dup:" + solverName + matching)) {
			lg.warn("solverName=" + solverName + " is listed by several solver families " + matching
					+ "; using the first, '" + chosen.name + "' (" + chosenImage + ")");
		}
		return chosenImage;
	}
}
