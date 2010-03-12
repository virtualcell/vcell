package org.vcell.sybil.models.miriam.demo;

/*   MIRIAMizerImp  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Handling MIRIAM annotations
 */

import org.vcell.sybil.models.miriam.MIRIAMRef;
import org.vcell.sybil.models.miriam.MIRIAMizer;
import org.vcell.sybil.models.miriam.MIRIAMQualifier.BioQualifier;
import org.vcell.sybil.models.miriam.imp.MIRIAMizerImp;
import org.vcell.sybil.rdf.RDFBox;
import org.vcell.sybil.rdf.RDFBox.RDFThing;
import org.vcell.sybil.rdf.schemas.MIRIAM.BioProperties;
import org.vcell.sybil.rdf.schemas.MIRIAM.ModelProperties;

import com.hp.hpl.jena.shared.PrefixMapping;

public class MIRIAMizerDemo {

	private static final String nsEx = "http://example.org/";

	public static void setPrefixes(RDFBox.Default box) {
		box.getRdf().setNsPrefixes(PrefixMapping.Standard);
		box.getRdf().setNsPrefix("ex", nsEx);
		box.getRdf().setNsPrefix("bqbiol", BioProperties.ns);
		box.getRdf().setNsPrefix("bqmodel", ModelProperties.ns);
	}
	
	public static void main(String[] args) {
		RDFBox.Default box = new RDFBox.Default();
		RDFThing<RDFBox.Default> egfr = box.createThing(nsEx + "egfr");
		MIRIAMizer miriamizer = new MIRIAMizerImp();
		MIRIAMRef refEGFR = new MIRIAMRef("uniprot", "P00533");
		miriamizer.newRefGroup(egfr, BioQualifier.is, refEGFR);
		setPrefixes(box);
		box.getRdf().write(System.out, "N3");
	}

}
