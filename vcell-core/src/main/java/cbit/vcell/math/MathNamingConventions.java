package cbit.vcell.math;

/**
 * Suffixes used to build the names of generated math variables and functions.
 * <p>
 * These are math-namespace facts: they describe how names inside a {@link MathDescription}
 * are formed, and {@link MathDescription} itself reads them when matching variables across
 * two maths. They previously lived on {@code cbit.vcell.mapping.AbstractMathMapping}, which
 * forced the math layer to import from the biological layer to recognise its own naming
 * scheme.
 * <p>
 * The math mapping still owns the act of <em>generating</em> these names; it just no longer
 * owns the vocabulary. {@code AbstractMathMapping} re-exports these constants under its
 * historical names, so existing callers are unaffected.
 */
public class MathNamingConventions {

	private MathNamingConventions() {
	}

	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT = "_initCount";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT_TEMPLATE_REPLACE = "_Count_initCount";

	public static final String PARAMETER_VELOCITY_X_SUFFIX = "_velocityX";
	public static final String PARAMETER_VELOCITY_Y_SUFFIX = "_velocityY";
	public static final String PARAMETER_VELOCITY_Z_SUFFIX = "_velocityZ";
	public static final String PARAMETER_DIFFUSION_RATE_SUFFIX = "_diffusionRate";
}
