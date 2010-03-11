package org.vcell.sybil.models.miriam;

/*   MIRIAMQualifier  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Wrapper for a MIRIAM property
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.rdf.schemas.MIRIAM;
import org.vcell.sybil.rdf.RDFBox;
import com.hp.hpl.jena.rdf.model.Property;

public class MIRIAMQualifier extends RDFBox.PropertyWrapper {

	public MIRIAMQualifier(Property property) { super(property); }

	public static final Set<MIRIAMQualifier> all = new HashSet<MIRIAMQualifier>();
	
	public static class ModelQualifier extends MIRIAMQualifier {

		public ModelQualifier(Property property) { super(property); }

		public static Set<ModelQualifier> all = new HashSet<ModelQualifier>();
		
		static protected ModelQualifier newQualifier(Property property) {
			ModelQualifier qualifier = new ModelQualifier(property);
			MIRIAMQualifier.all.add(qualifier);
			all.add(qualifier);
			return qualifier;
		}
		
		public static final ModelQualifier is = newQualifier(MIRIAM.ModelProperties.is);
		public static final ModelQualifier isDerivedFrom = 
			newQualifier(MIRIAM.ModelProperties.isDerivedFrom);
		public static final ModelQualifier isDescribedBy = 
			newQualifier(MIRIAM.ModelProperties.isDescribedBy);
		
	}
	
	public static class BioQualifier extends MIRIAMQualifier  {

		public BioQualifier(Property property) { super(property); }

		public static Set<BioQualifier> all = new HashSet<BioQualifier>();
		
		static protected BioQualifier newQualifier(Property property) {
			BioQualifier qualifier = new BioQualifier(property);
			MIRIAMQualifier.all.add(qualifier);
			all.add(qualifier);
			return qualifier;
		}
		
		public static final BioQualifier encodes = newQualifier(MIRIAM.BioProperties.encodes);
		public static final BioQualifier hasPart = newQualifier(MIRIAM.BioProperties.hasPart);
		public static final BioQualifier hasProperty = newQualifier(MIRIAM.BioProperties.hasProperty);
		public static final BioQualifier hasVersion = newQualifier(MIRIAM.BioProperties.hasVersion);
		public static final BioQualifier is = newQualifier(MIRIAM.BioProperties.is);
		public static final BioQualifier isDescribedBy = newQualifier(MIRIAM.BioProperties.isDescribedBy);
		public static final BioQualifier isEncodedBy = newQualifier(MIRIAM.BioProperties.isEncodedBy);
		public static final BioQualifier isHomologTo = newQualifier(MIRIAM.BioProperties.isHomologTo);
		public static final BioQualifier isPartOf = newQualifier(MIRIAM.BioProperties.isPartOf);
		public static final BioQualifier isPropertyOf = newQualifier(MIRIAM.BioProperties.isPropertyOf);
		public static final BioQualifier isVersionOf = newQualifier(MIRIAM.BioProperties.isVersionOf);
		public static final BioQualifier occursIn = newQualifier(MIRIAM.BioProperties.occursIn);
		
	}


	
	
}
