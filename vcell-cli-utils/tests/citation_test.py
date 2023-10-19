from typing import Optional
from vcell_pipeline.citation import getCitation, CitationInfo, getSuggestedProjectName


def test_citation() -> None:
    citation: Optional[CitationInfo] = getCitation(pubmedid="35367415")
    assert citation is not None
    assert citation.author == "Nosbisch, Jamie L"
    assert citation.title == "A kinetic model of phospholipase C-γ1 linking structure-based insights to dynamics of enzyme autoinhibition and activation"
    assert citation.year == 2022
    assert citation.journal == "J Biol Chem"

    suggestedProjectName = getSuggestedProjectName(bm_key="12345", pub_info=None, citation_info=citation)
    assert suggestedProjectName == "VCDB_12345_Nosbisch_JBiolChem_2022"
