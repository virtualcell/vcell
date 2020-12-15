package cbit.util.kisao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class KisaoOntology {
	
	private List<KisaoTerm> terms = new ArrayList<>();
	private static KisaoOntology instance;
	
	
	public static KisaoOntology getInstance() {
		if (instance == null) {
			instance = new KisaoTermParser().parse();
		}
		return instance;
	}
	
	public void addTerm(KisaoTerm curr) {
		terms.add(curr);
	}
	public List<KisaoTerm> getTerms() {
		return Collections.unmodifiableList(terms);
	}

	public KisaoTerm getTermById(String id) {
		for (KisaoTerm term : terms) {
			if (term.getId().equalsIgnoreCase(id)) {
				return term;
			}
		}
		return null;
	}
	
	public void createRelations() {
		for (KisaoTerm term : terms) {
			List<String> isas = term.getIsaRef();
			for (String isa : isas) {
				KisaoTerm termIsa = getTermById(isa);
				if (termIsa != null) {
					term.addIsa(termIsa);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		
		KisaoTerm sedmlSolverKisaoTerm = KisaoOntology.getInstance().getTermById("KISAO_0000027");
		System.out.println(sedmlSolverKisaoTerm);
		System.out.println("Done");
	}

}
