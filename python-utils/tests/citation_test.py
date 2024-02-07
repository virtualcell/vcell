from typing import Optional
from vcutils.vcell_pipeline.citation import getCitation, CitationInfo, getSuggestedProjectName


def test_citation() -> None:
    citation: Optional[CitationInfo] = getCitation(pubmedid="35367415")
    assert citation is not None
    assert citation.author == "Nosbisch, Jamie L"
    assert citation.title == ("A kinetic model of phospholipase C-γ1 linking structure-based insights to dynamics of "
                              "enzyme autoinhibition and activation")
    assert citation.year == 2022
    assert citation.journal == "J Biol Chem"

    suggestedProjectName = getSuggestedProjectName(bm_key="12345", pub_info=None, citation_info=citation)
    assert suggestedProjectName == "VCDB_12345_Nosbisch_JBiolChem_2022"


def test_get_citation():
    pubmdeid = "35367415"
    expected_title = ("A kinetic model of phospholipase C-γ1 linking structure-based insights to dynamics of "
                      "enzyme autoinhibition and activation")
    expected_year = 2022
    expected_journal = "J Biol Chem"
    expected_author = "Nosbisch, Jamie L"
    expected_abstract = ("Phospholipase C-γ1 (PLC-γ1) is a receptor-proximal enzyme that promotes signal transduction "
                         "through PKC in mammalian cells. Because of the complexity of PLC-γ1 regulation, a two-state "
                         "(inactive/active) model does not account for the intricacy of activation and inactivation "
                         "steps at the plasma membrane. Here, we introduce a structure-based kinetic model of PLC-γ1, "
                         "considering interactions of its regulatory Src homology 2 (SH2) domains and perturbation of "
                         "those dynamics upon phosphorylation of Tyr783, a hallmark of activation. For PLC-γ1 "
                         "phosphorylation to dramatically enhance enzyme activation as observed, we found that high "
                         "intramolecular affinity of the C-terminal SH2 (cSH2) domain-pTyr783 interaction is critical, "
                         "but this affinity need not outcompete the autoinhibitory interaction of the cSH2 domain. "
                         "Under conditions for which steady-state PLC-γ1 activity is sensitive to the rate of Tyr783 "
                         "phosphorylation, maintenance of the active state is surprisingly insensitive to the "
                         "phosphorylation rate, since pTyr783 is well protected by the cSH2 domain while the enzyme is "
                         "active. In contrast, maintenance of enzyme activity is sensitive to the rate of PLC-γ1 "
                         "membrane (re)binding. Accordingly, we found that hypothetical PLC-γ1 mutations that either "
                         "weaken autoinhibition or strengthen membrane binding influence the activation kinetics "
                         "differently, which could inform the characterization of oncogenic variants. Finally, we used "
                         "this newly informed kinetic scheme to refine a spatial model of PLC/PKC polarization during "
                         "chemotaxis. The refined model showed improved stability of the polarized pattern while "
                         "corroborating previous qualitative predictions. As demonstrated here for PLC-γ1, this "
                         "approach may be adapted to model the dynamics of other receptor- and membrane-proximal "
                         "enzymes.")
    citation = getCitation(pubmedid=pubmdeid)
    assert citation is not None
    assert citation.abstract == expected_abstract
    assert citation.author == expected_author
    assert citation.title == expected_title
    assert citation.year == expected_year
    assert citation.journal == expected_journal
