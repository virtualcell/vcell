package cbit.vcell.math;

import java.util.List;

/**
 * BNGL strings for the math namespace's particle entities.
 *
 * <p>The {@link RuleAnalysis} contract requires BNGL - {@code RuleEntry.getReactionBNGLShort()},
 * {@code MolecularTypeEntry.toBngl()}, {@code getMolecularTypeBNGL()} - so the math adapter has to be
 * able to produce it. These methods were the math-typed half of
 * {@code org.vcell.model.rbm.RbmUtils}, which carries a biological and a math overload of every
 * conversion; keeping them there meant the math layer importing from the biological one to describe
 * its own entities.
 *
 * <p>The biological overloads stay in {@code RbmUtils}: they take {@code MolecularType},
 * {@code SpeciesPattern} and friends, and several need a {@code Model}, a {@code Structure} or a
 * {@code CompartmentMode} that has no meaning here. The two halves were only ever related by name.
 *
 * <p>BNGL is a modelling language rather than a particular solver's input format, which is why this
 * does not fall foul of the rule that keeps solver formats out of the math layer
 * (docs/architecture-layers.md, P4) - the analysis contract itself is expressed in terms of it.
 *
 * <p>These strings are consumed inside rule analysis as identifiers and labels; nothing here is
 * exercised as a BNGL <em>export</em>. See the BNGL follow-up in docs/nfsim-abstractions.md.
 */
public class ParticleBnglStringWriter {

	private ParticleBnglStringWriter() {
	}

	public static String toBnglString(ParticleComponentStatePattern componentStatePattern) {
		if(componentStatePattern.getParticleComponentStateDefinition() != null) {
			return "~" + componentStatePattern.getParticleComponentStateDefinition().getName();
		} else if(componentStatePattern.isAny()) {
			return "";
		} else {
			throw new RuntimeException("Unexpected state for ComponentStatePattern " + componentStatePattern);
		}
	}

	public static String toBnglString(ParticleComponentStateDefinition componentStateDefinition) {
		if(componentStateDefinition == null) {
			return "";
		} else {
			return "~" + componentStateDefinition.getName();
		}
	}

	public static String toBnglString(ParticleMolecularComponent molecularComponent) {
		StringBuilder buffer = new StringBuilder(molecularComponent.getName());
		for (ParticleComponentStateDefinition componentStateDefinition : molecularComponent.getComponentStateDefinitions()) {
			buffer.append(toBnglString(componentStateDefinition));
		}
		return buffer.toString();
	}

	public static String toBnglString(ParticleMolecularType molecularType) {
		StringBuilder buffer = new StringBuilder(molecularType.getName());
		buffer.append("(");
		List<ParticleMolecularComponent> componentList = molecularType.getComponentList();
		for (int i = 0; i < componentList.size(); ++ i) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(toBnglString(componentList.get(i)));
		}
		buffer.append(")");
		return buffer.toString();
	}

	public static String toBnglString(ParticleSpeciesPattern speciesPattern) {
		if (speciesPattern == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		List<ParticleMolecularTypePattern> molecularTypePatterns = speciesPattern.getParticleMolecularTypePatterns();
		for (int i = 0; i < molecularTypePatterns.size(); ++ i) {
			if (i > 0) {
				buffer.append(".");
			}
			buffer.append(toBnglString(molecularTypePatterns.get(i)));
		}
		return buffer.toString();
	}

	public static String toBnglString(ParticleMolecularTypePattern molecularTypePattern) {
		StringBuilder buffer = new StringBuilder(molecularTypePattern.getMolecularType().getName());
		buffer.append("(");
		List<ParticleMolecularComponentPattern> componentPatterns = molecularTypePattern.getMolecularComponentPatternList();
		boolean bAddComma = false;
		for (ParticleMolecularComponentPattern mcp : componentPatterns) {
//			if (mcp.isImplied()) {
//				continue;
//			}
			if (bAddComma) {
				buffer.append(",");
			}
			buffer.append(toBnglString(mcp));
			bAddComma = true;
		}
		buffer.append(")");
		if(molecularTypePattern.hasExplicitParticipantMatch()) {
			buffer.append("%" + molecularTypePattern.getMatchLabel());
		}
		return buffer.toString();
	}

	public static String toBnglString(ParticleMolecularComponentPattern molecularComponentPattern) {
		StringBuilder buffer = new StringBuilder(molecularComponentPattern.getMolecularComponent().getName());
		if (molecularComponentPattern.getComponentStatePattern() != null) {
			buffer.append(toBnglString(molecularComponentPattern.getComponentStatePattern()));
		}
		switch (molecularComponentPattern.getBondType()) {
		case Exists:
			buffer.append("!+");
			break;
		case None:
			break;
		case Possible:
			buffer.append("!?");
			break;
		case Specified:
			buffer.append("!" + molecularComponentPattern.getBondId());
			break;
		}
		return buffer.toString();
	}

	public static String toBnglStringShort(ParticleJumpProcess particleJumpProcess, List<ParticleSpeciesPattern> reactantSpeciesPatterns, List<ParticleSpeciesPattern> productSpeciesPatterns) {
		StringBuilder buffer = new StringBuilder();

		for (int i=0; i<reactantSpeciesPatterns.size(); i++){
			ParticleSpeciesPattern reactantSpeciesPattern = reactantSpeciesPatterns.get(i);
			if (i>0) {
				buffer.append(" + ");
			}
			buffer.append(toBnglString(reactantSpeciesPattern));
		}
		// particleJumpProcesses are not reversible
		boolean bReversible = false;
		buffer.append(bReversible ? " <-> " : " -> ");
		
		for (int i=0; i<productSpeciesPatterns.size(); i++){
			ParticleSpeciesPattern productSpeciesPattern = productSpeciesPatterns.get(i);
			if (i>0) {
				buffer.append(" + ");
			}
			buffer.append(toBnglString(productSpeciesPattern));
		}
		return buffer.toString();
	}
}
