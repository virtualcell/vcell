package cbit.util.kisao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KisaoTerm {
	
	private String id, name;
	private List<KisaoTerm> isaList = new ArrayList<KisaoTerm>();
	private List<String> isaRef = new ArrayList<String>();	// used only during parsing, as proxy for still unknown terms
	
	KisaoTerm() {
	}

	@Override
	public String toString() {
		return "KisaoTerm [id=" + id + ", name=" + name + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		KisaoTerm other = (KisaoTerm) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	public String getId() {
		return id;
	}
	void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	
	public List<KisaoTerm> getIsa() {
		return Collections.unmodifiableList(isaList);
	}
	void addIsa(KisaoTerm is_a) {
		isaList.add(is_a);
	}

	void addIsaRef(String synonym) {
		isaRef.add(synonym);
	}
	List<String> getIsaRef() {
		return isaRef;
	}

	public boolean is_a(KisaoTerm otherTerm) {
		if (this.equals(otherTerm)) {
			return true;
		}
		for (KisaoTerm isa : isaList) {
			if (isa.equals(otherTerm)) {
				return true;
			}
		}
		for (KisaoTerm isa : isaList) {
			return isa.is_a(otherTerm);
		}
		return false;
	}

	public static void main(String[] args) {
	
		KisaoTerm sedmlSolverKisaoTerm = KisaoOntology.getInstance().getTermById("KISAO_0000000");
//		KisaoTerm sedmlSolverKisaoTerm = KisaoOntology.getInstance().getTermById("KISAO_0000027");
		List<KisaoTerm> isaTerms = sedmlSolverKisaoTerm.getIsa();
		System.out.println(sedmlSolverKisaoTerm);
		System.out.println("Done");
	}

	
}

