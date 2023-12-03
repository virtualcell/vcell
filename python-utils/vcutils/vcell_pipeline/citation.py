from typing import Optional
import requests
import time
from vcutils.vcell_pipeline.datamodels import Publication


class CitationInfo:
    abstract: str
    title: str
    year: int
    journal: str
    author: str


def parse_RIS_entry(lines: str) -> dict[str, str]:
    currentLabel: Optional[str] = None
    currentContent: str = ""
    citationFields = dict[str, str]()
    for line in lines.split("\n"):
        if line.startswith("ER"):
            break;
        if line.startswith("  "):
            # continuation of previous line
            currentContent += " " + line[6:]
        else:
            # new line
            if currentLabel:
                # save previous line but choose the first one (e.g.if multiple authors)
                if not citationFields.get(currentLabel):
                    citationFields[currentLabel] = currentContent
            # start new line
            currentLabel = line[0:2]
            currentContent = line[6:]
    return citationFields


def getCitation(pubmedid: Optional[str]) -> Optional[CitationInfo]:
    time.sleep(0.5) # to rate limit the API calls (they will fail if > 3 per second)
    """
    Get RIS citation for a pubmed id
    """
    if not pubmedid:
        return None
    """
    https://api.ncbi.nlm.nih.gov/lit/ctxp/v1/pubmed/?format=ris&id=35367415
    """
    url = f"https://api.ncbi.nlm.nih.gov/lit/ctxp/v1/pubmed/?format=ris&id={pubmedid}"
    # post to url with payload and retrieve results
    r = requests.get(url, headers={'Content-Type': 'text/plain'})
    # print results
    if (r.status_code != 200):
        raise Exception(f"Error: status code {r.status_code}")

    RIS_text: str = r.text
    entry = parse_RIS_entry(RIS_text)
    citationInfo = CitationInfo()
    citationInfo.abstract = entry["AB"].rstrip('\r\n')
    citationInfo.author = entry["AU"].rstrip('\r\n')
    citationInfo.title = entry["T1"].rstrip('\r\n')
    year = entry["Y1"].rstrip('\r\n').split('/', 1)[0]
    citationInfo.year = int(year)
    citationInfo.journal = entry["J2"].rstrip('\r\n')
    return citationInfo


def getSuggestedProjectName(bm_key: str, pub_info: Optional[Publication], citation_info: Optional[CitationInfo]) -> str:
    """
    Get suggested project name from citation info
    """
    if citation_info is not None:
        author_last_name = citation_info.author.split(",")[0].replace(" ", "")
        year = citation_info.year
        journal_name_short = citation_info.journal.replace(" ", "").replace(".", "").replace(",", "").replace(":", "")
    elif pub_info is not None:
        author_last_name = pub_info.authors[0].split(",")[0].replace(" ", "")
        year = pub_info.year

        journal_name_short = ""
        if pub_info.citation is not None:
            journal_name_short = pub_info.citation
            # clip to first character which is not a space or letter
            for i in range(len(journal_name_short)):
                if not journal_name_short[i].isalpha() and not journal_name_short[i].isspace():
                    journal_name_short = journal_name_short[0:i]
                    break
            journal_name_short = journal_name_short.replace(" ", "").replace(".", "").replace(",", "").replace(":", "")
    else:
        author_last_name = "unknown"
        year = 0
        journal_name_short = "unknown"
    return f"VCDB_{bm_key}_{author_last_name}_{journal_name_short}_{year}"


