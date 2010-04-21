package org.vcell.sybil.models.miriam;

/*   MIRIAMQualifier  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Wrapper for a MIRIAM property
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.AnnotationQualifier;
import org.vcell.sybil.rdf.schemas.MIRIAM;
import org.vcell.sybil.rdf.RDFBox;

import com.hp.hpl.jena.rdf.model.Property;

public class MIRIAMQualifier extends RDFBox.PropertyWrapper implements AnnotationQualifier {
	public static final MIRIAMQualifier BIO_encodes = new MIRIAMQualifier(MIRIAM.BioProperties.encodes,"(bio) encodes");
	public static final MIRIAMQualifier BIO_hasPart = new MIRIAMQualifier(MIRIAM.BioProperties.hasPart,"(bio) hasPart");
	public static final MIRIAMQualifier BIO_hasProperty = new MIRIAMQualifier(MIRIAM.BioProperties.hasProperty,"(bio) hasProperty");
	public static final MIRIAMQualifier BIO_hasVersion = new MIRIAMQualifier(MIRIAM.BioProperties.hasVersion,"(bio) hasVersion");
	public static final MIRIAMQualifier BIO_is = new MIRIAMQualifier(MIRIAM.BioProperties.is,"(bio) is");
	public static final MIRIAMQualifier BIO_isDescribedBy = new MIRIAMQualifier(MIRIAM.BioProperties.isDescribedBy,"(bio) isDescribedBy");
	public static final MIRIAMQualifier BIO_isEncodedBy = new MIRIAMQualifier(MIRIAM.BioProperties.isEncodedBy,"(bio) isEncodedBy");
	public static final MIRIAMQualifier BIO_isHomologTo = new MIRIAMQualifier(MIRIAM.BioProperties.isHomologTo,"(bio) isHomologOf");
	public static final MIRIAMQualifier BIO_isPartOf = new MIRIAMQualifier(MIRIAM.BioProperties.isPartOf,"(bio) isPartOf");
	public static final MIRIAMQualifier BIO_isPropertyOf = new MIRIAMQualifier(MIRIAM.BioProperties.isPropertyOf,"(bio) isPropertyOf");
	public static final MIRIAMQualifier BIO_isVersionOf = new MIRIAMQualifier(MIRIAM.BioProperties.isVersionOf,"(bio) isVersionOf");
	public static final MIRIAMQualifier BIO_occursIn = new MIRIAMQualifier(MIRIAM.BioProperties.occursIn,"(bio) occursIn");
	public static final MIRIAMQualifier MODEL_is = new MIRIAMQualifier(MIRIAM.ModelProperties.is,"(model) is");
	public static final MIRIAMQualifier MODEL_isDerivedFrom = new MIRIAMQualifier(MIRIAM.ModelProperties.isDerivedFrom,"(model) isDerivedBy");
	public static final MIRIAMQualifier MODEL_isDescribedBy = new MIRIAMQualifier(MIRIAM.ModelProperties.isDescribedBy,"(model) isDescribedBy");
	public static final Set<MIRIAMQualifier> MODEL_all;
	public static final Set<MIRIAMQualifier> BIO_all;
	public static final Set<MIRIAMQualifier> all;
	static {
		BIO_all = new HashSet<MIRIAMQualifier>();
		BIO_all.add(BIO_encodes);
		BIO_all.add(BIO_hasPart);
		BIO_all.add(BIO_hasProperty);
		BIO_all.add(BIO_hasVersion);
		BIO_all.add(BIO_is);
		BIO_all.add(BIO_isDescribedBy);
		BIO_all.add(BIO_isEncodedBy);
		BIO_all.add(BIO_isHomologTo);
		BIO_all.add(BIO_isPartOf);			
		BIO_all.add(BIO_isPropertyOf);			
		BIO_all.add(BIO_isVersionOf);			
		BIO_all.add(BIO_occursIn);			
		MODEL_all = new HashSet<MIRIAMQualifier>();
		MODEL_all.add(MODEL_is);
		MODEL_all.add(MODEL_isDerivedFrom);
		MODEL_all.add(MODEL_isDescribedBy);
		all = new HashSet<MIRIAMQualifier>();
		all.addAll(BIO_all);
		all.addAll(MODEL_all);
	}

	private String description = null;
	
	private MIRIAMQualifier(Property property) {
		this(property,property.getLocalName());
	}
	
	private MIRIAMQualifier(Property property, String description) {
		super(property);
		this.description = description;
	}
	
	public URI getURI() throws URISyntaxException {
		return new URI(property().getURI());
	}
	
	public String getLocalName(){
		return property().getLocalName();
	}

	public String getNameSpace(){
		return property().getNameSpace();
	}
	
	public String getDescription(){
		return description;
	}

}
