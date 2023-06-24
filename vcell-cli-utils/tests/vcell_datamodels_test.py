import json

from vcell_pipeline.vcell_datamodels import Publication

testdata = '''
[ 
  {
    "pubKey": "161520376",
    "title": "Increasing complexity of the ras signaling pathway",
    "authors": [
      "Vojtek, A. B.",
      "Der, C. J."
    ],
    "year": 1998,
    "citation": "J Biol Chem 273 (32): 19925-19928.",
    "pubmedid": "9685325",
    "doi": "10.1074/jbc.273.32.19925",
    "endnoteid": "8",
    "url": "https://www.ncbi.nlm.nih.gov/pubmed/9685325",
    "wittid": "-1",
    "biomodelReferences": [
      {
        "bmKey": "7681482",
        "name": "Generic_Ras_cascade_1998",
        "ownerName": "CMC",
        "ownerKey": "2794180",
        "versionFlag": "0"
      }
    ],
    "mathmodelReferences": [],
    "date": "1998-08-07T00:00:00.000-04:00"
  },
  {
    "pubKey": "161440204",
    "title": "A minimal cascade model for the mitotic oscillator involving cyclin and cdc2 kinase",
    "authors": [
      "Goldbeter, A."
    ],
    "year": 1991,
    "citation": "Proc Natl Acad Sci U S A 1991. 88 (20): 9107-9111.",
    "pubmedid": "1833774",
    "doi": "10.1073/pnas.88.20.9107",
    "endnoteid": "2",
    "url": "https://www.ncbi.nlm.nih.gov/pubmed/1833774",
    "wittid": "-1",
    "biomodelReferences": [
      {
        "bmKey": "84982814",
        "name": "Goldbeter 1991 explicit",
        "ownerName": "Education",
        "ownerKey": "30625624",
        "versionFlag": "0"
      }
    ],
    "mathmodelReferences": [],
    "date": "1991-10-15T00:00:00.000-04:00"
  }
]
'''

def test_publication_serializations():
    jsonData = json.loads(testdata)
    pubs = [Publication(**jsonDict) for jsonDict in jsonData]

    assert pubs[0].pubKey == "161520376"
    assert pubs[0].biomodelReferences[0].bmKey == "7681482"
    assert pubs[1].pubKey == "161440204"
    assert pubs[1].biomodelReferences[0].bmKey == "84982814"
