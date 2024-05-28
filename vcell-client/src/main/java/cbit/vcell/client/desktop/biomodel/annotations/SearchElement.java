package cbit.vcell.client.desktop.biomodel.annotations;


import org.biojava.nbio.ontology.Synonym;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SearchElement {
    private final String entityName;
    private final List<String> synonyms;
    private final String definition;
    private final URI dbLink;

    public SearchElement(String entityName, List<String> synonyms, String definition, String dbLink) {
        this.entityName = entityName;
        this.synonyms = new ArrayList<>(synonyms);
        this.definition = definition;

        if (dbLink.contains("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#")) {
            String newDbLink = dbLink.replace("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#",
                    "https://ncit.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&ns=ncit&code=");
            this.dbLink = URI.create(newDbLink);
        } else  {
            this.dbLink = URI.create(dbLink);
        }

    }

    public SearchElement(String name, String id) {
        this.entityName = name;
        this.synonyms = new ArrayList<>();
        this.definition = "";
        this.dbLink = URI.create("https://www.uniprot.org/uniprot/"+id);
    }

    public String getEntityName() {
        return entityName;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public String getDefinition() {
        return definition;
    }

    public URI getDbLink() {
        return dbLink;
    }

    @Override
    public String toString() {
        String result = "";
        if (!entityName.isEmpty())
            result += "Name: " + entityName;
        if (!synonyms.isEmpty())
            result += "\nSynonyms: " + synonyms;
        if (!definition.isEmpty())
            result += "\nDefinition: " + definition;
        if (!dbLink.toString().isEmpty())
            result += "\nLink: " + dbLink;

        return result;
//        return  "Name: " + entityName +
//                "\nSynonyms: " + synonyms.toString() +
//                "\nDefinition: " + definition +
//                "\nLink: " + dbLink;
    }
}

