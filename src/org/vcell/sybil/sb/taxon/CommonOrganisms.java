package org.vcell.sybil.sb.taxon;

/*   CommonOrganisms  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   An organism reference according to the NCBI taxonomy
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommonOrganisms {

	public static final Set<OrganismRef> all = new HashSet<OrganismRef>();
	public static final Map<String, OrganismRef> byName = new HashMap<String, OrganismRef>();
	public static final Map<Integer, OrganismRef> byID = new HashMap<Integer, OrganismRef>();	
	
	private static  OrganismRef register(OrganismRef ref) {
		all.add(ref);
		for(String name : ref.names()) { byName.put(name, ref); }
		byID.put(new Integer(ref.id()), ref);
		return ref;
	}
	
	private static OrganismRef create(int id, String taxonName) {
		return register(new OrganismRef.Imp(id, taxonName));
	}

	private static OrganismRef create(int id, String taxonName, String name) {
		return register(new OrganismRef.Imp(id, taxonName, name));
	}

	private static OrganismRef create(int id, String taxonName, String name1, String name2) {
		return register(new OrganismRef.Imp(id, taxonName, name1, name2));
	}

	private static OrganismRef create(int id, String taxonName, String name1, String name2, String name3) {
		return register(new OrganismRef.Imp(id, taxonName, name1, name2, name3));
	}

	public static final OrganismRef arabidopsisThaliana = 
		create(3702, "Arabidopsis thaliana", "thale-cress", "mouse-ear cress");
	public static final OrganismRef bosTaurus = 
		create(9913, "Bos taurus", "cow", "cattle");
	public static final OrganismRef caenorhabditisElegans = 
		create(6239, "Caenorhabditis elegans", "nematodes", "C elegans");	
	public static final OrganismRef chlamydomonasReinhardtii = 
		create(3055, "Chlamydomonas reinhardtii");
	public static final OrganismRef danioRerio = 
		create(7955, "Danio rerio", "zebra fish");
	public static final OrganismRef dictyosteliumDiscoideum = 
		create(44689, "Dictyostelium discoideum", "slime mold");
	public static final OrganismRef drosophilaMelanogaster = 
		create(7227, "Drosophila melanogaster", "fruit fly");
	public static final OrganismRef escherichiaColi = 
		create(562, "Escherichia coli", "E coli");
	public static final OrganismRef hepatitisCVirus = 
		create(11103, "Hepatitis C virus");
	public static final OrganismRef homoSapiens = 
		create(9606, "Homo sapiens", "human", "man");
	public static final OrganismRef magnaportheGrisea = 
		create(148305, "Magnaporthe grisea", "rice blast fungus");
	public static final OrganismRef musMusculus = 
		create(10090, "Mus musculus", "mouse", "house mouse");
	public static final OrganismRef mycoplasmaPneumoniae = 
		create(2104, "Mycoplasma pneumoniae", "Schizoplasma pneumoniae");
	public static final OrganismRef neurosporaCrassa = 
		create(5141, "Neurospora crassa");
	public static final OrganismRef oryzaSativa = 
		create(4530, "Oryza sativa", "rice", "red rice");
	public static final OrganismRef plasmodiumFalciparum = 
		create(5833, "Plasmodium falciparum", "Malaria parasite");
	public static final OrganismRef pneumocystisCarinii = 
		create(4754, "Pneumocystis carinii", "");
	public static final OrganismRef rattusNorvegicus = 
		create(10116, "Rattus norvegicus", "rat", "brown rat", "Norway rat");
	public static final OrganismRef saccharomycesCerevisiae = 
		create(4932, "Saccharomyces cerevisiae", "yeast", "baker's yeast", "brewer's yeast");
	public static final OrganismRef schizosaccharomycesPombe = 
		create(4896, "Schizosaccharomyces pombe", "fission yeast");
	public static final OrganismRef takifuguRubripes = 
		create(31033, "Takifugu rubripes", "tiger puffer");
	public static final OrganismRef xenopusLaevis = 
		create(8355, "Xenopus laevis", "African clawed frog", "clawed frog");
	public static final OrganismRef zeaMays = 
		create(4577, "Zea mays", "maize", "corn");
	
}
