package org.vcell.sedml;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Fast")
public class PublicationMetadataTest {

    private final static String examplePubmedEntry =
        "TY  - JOUR\n" +
        "DB  - PubMed\n" +
        "AU  - Nosbisch, Jamie L\n" +
        "AU  - Bear, James E\n" +
        "AU  - Haugh, Jason M\n" +
        "T1  - A kinetic model of phospholipase C-γ1 linking structure-based insights to dynamics of enzyme autoinhibition and activation\n" +
        "LA  - eng\n" +
        "SN  - 1083-351X\n" +
        "SN  - 0021-9258\n" +
        "Y1  - 2022/05/\n" +
        "ET  - 2022/03/31\n" +
        "AB  - Phospholipase C-γ1 (PLC-γ1) is a receptor-proximal enzyme that promotes signal transduction through PKC in mammalian cells. Because of the complexity of PLC-γ1 regulation, a two-state (inactive/active) model does not account for the intricacy of activation and inactivation steps at the plasma membrane. Here, we introduce a structure-based kinetic model of PLC-γ1, considering interactions of its regulatory Src homology 2 (SH2) domains and perturbation of those dynamics upon phosphorylation of Tyr783, a hallmark of activation. For PLC-γ1 phosphorylation to dramatically enhance enzyme activation as observed, we found that high intramolecular affinity of the C-terminal SH2 (cSH2) domain-pTyr783 interaction is critical, but this affinity need not outcompete the autoinhibitory interaction of the cSH2 domain. Under conditions for which steady-state PLC-γ1 activity is sensitive to the rate of Tyr783 phosphorylation, maintenance of the active state is surprisingly insensitive to the phosphorylation rate, since pTyr783 is well protected by the cSH2 domain while the enzyme is active. In contrast, maintenance of enzyme activity is sensitive to the rate of PLC-γ1 membrane (re)binding. Accordingly, we found that hypothetical PLC-γ1 mutations that either weaken autoinhibition or strengthen membrane binding influence the activation kinetics differently, which could inform the characterization of oncogenic variants. Finally, we used this newly informed kinetic scheme to refine a spatial model of PLC/PKC polarization during chemotaxis. The refined model showed improved stability of the polarized pattern while corroborating previous qualitative predictions. As demonstrated here for PLC-γ1, this approach may be adapted to model the dynamics of other receptor- and membrane-proximal enzymes.\n" +
        "SP  - 101886\n" +
        "EP  - 101886\n" +
        "VL  - 298\n" +
        "IS  - 5\n" +
        "AN  - 35367415\n" +
        "UR  - https://pubmed.ncbi.nlm.nih.gov/35367415\n" +
        "DO  - 10.1016/j.jbc.2022.101886\n" +
        "L2  - https://www.ncbi.nlm.nih.gov/pmc/articles/PMC9097458/\n" +
        "U1  - 35367415[pmid]\n" +
        "U2  - PMC9097458[pmcid]\n" +
        "U4  - S0021-9258(22)00326-X[PII]\n" +
        "J2  - J Biol Chem\n" +
        "JF  - The Journal of biological chemistry\n" +
        "KW  - mathematical model\n" +
        "KW  - phosphoinositide\n" +
        "KW  - phospholipase C\n" +
        "KW  - protein kinase C\n" +
        "KW  - receptor tyrosine kinase\n" +
        "KW  - Animals\n" +
        "KW  - Carrier Proteins/metabolism\n" +
        "KW  - *Isoenzymes/metabolism\n" +
        "KW  - Kinetics\n" +
        "KW  - Mammals/metabolism\n" +
        "KW  - Phospholipase C gamma/genetics/metabolism\n" +
        "KW  - Phosphorylation\n" +
        "KW  - Protein-Tyrosine Kinases/metabolism\n" +
        "KW  - *Type C Phospholipases/metabolism\n" +
        "KW  - src Homology Domains/genetics\n" +
        "CY  - United States\n" +
        "ER  -\n";
    final static String expectedPubmedID = "35367415";
    final static String expectedAbstract = "Phospholipase C-γ1 (PLC-γ1) is a receptor-proximal enzyme that promotes signal transduction through PKC in mammalian cells. Because of the complexity of PLC-γ1 regulation, a two-state (inactive/active) model does not account for the intricacy of activation and inactivation steps at the plasma membrane. Here, we introduce a structure-based kinetic model of PLC-γ1, considering interactions of its regulatory Src homology 2 (SH2) domains and perturbation of those dynamics upon phosphorylation of Tyr783, a hallmark of activation. For PLC-γ1 phosphorylation to dramatically enhance enzyme activation as observed, we found that high intramolecular affinity of the C-terminal SH2 (cSH2) domain-pTyr783 interaction is critical, but this affinity need not outcompete the autoinhibitory interaction of the cSH2 domain. Under conditions for which steady-state PLC-γ1 activity is sensitive to the rate of Tyr783 phosphorylation, maintenance of the active state is surprisingly insensitive to the phosphorylation rate, since pTyr783 is well protected by the cSH2 domain while the enzyme is active. In contrast, maintenance of enzyme activity is sensitive to the rate of PLC-γ1 membrane (re)binding. Accordingly, we found that hypothetical PLC-γ1 mutations that either weaken autoinhibition or strengthen membrane binding influence the activation kinetics differently, which could inform the characterization of oncogenic variants. Finally, we used this newly informed kinetic scheme to refine a spatial model of PLC/PKC polarization during chemotaxis. The refined model showed improved stability of the polarized pattern while corroborating previous qualitative predictions. As demonstrated here for PLC-γ1, this approach may be adapted to model the dynamics of other receptor- and membrane-proximal enzymes.";
    final static String expectedTitle = "A kinetic model of phospholipase C-γ1 linking structure-based insights to dynamics of enzyme autoinhibition and activation";
    final static String expectedAuthor = "Nosbisch, Jamie L";
    final static String expectedFirstAuthorsLastName = "Nosbisch";
    final static Integer expectedYear = 2022;
    final static KeyValue biomodelKey = new KeyValue("303029392");
    final static String expectedCompactJournalName = "JBiolChem";

    // VCDB_303029392_Nosbisch_JBiolChem_2022
    final static String expectedSuggestedProjectName =
            "VCDB_"+biomodelKey+"_"+expectedFirstAuthorsLastName+"_"+expectedCompactJournalName+"_"+expectedYear;


    @Test
    public void test_buildPublicationMetadata() throws IOException {
        PublicationInfo publicationInfo1 = new PublicationInfo(
                new KeyValue("12345"), // publication key
                new KeyValue("123434332"), // version key
                "my title", // title
                new String[]{"author1", "author2"}, // authors
                "citation", // citation
                expectedPubmedID, // pubmedid
                "doi", // doi
                "url", // url
                VCDocument.VCDocumentType.BIOMODEL_DOC, // document type
                new User("user1", new KeyValue("12345")), // user
                new Date() // date
        );

        PublicationMetadata publicationMetadata = PublicationMetadata.fromPublicationInfoAndWeb(publicationInfo1);
        assertEquals(expectedAbstract, publicationMetadata.abstractText);
        assertEquals(expectedCompactJournalName, publicationMetadata.compactJournalName);
        assertEquals(expectedYear, publicationMetadata.year);
        assertEquals(expectedFirstAuthorsLastName, publicationMetadata.firstAuthorsLastName);
        assertEquals(expectedSuggestedProjectName, publicationMetadata.getSuggestedProjectName(biomodelKey));
        System.out.println(publicationMetadata);

    }
}
