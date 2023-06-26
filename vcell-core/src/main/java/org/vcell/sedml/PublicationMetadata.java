package org.vcell.sedml;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PublicationInfo;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class PublicationMetadata {
    public final PublicationInfo publicationInfo;
    public final String abstractText;
    public final String compactJournalName;
    public final String firstAuthorsLastName;
    public final Integer year;

    private PublicationMetadata(PublicationInfo publicationInfo,
                                String abstractText,
                                String compactJournalName,
                                String firstAuthorsLastName,
                                int year){
        this.publicationInfo = publicationInfo;
        this.abstractText = abstractText;
        this.compactJournalName = compactJournalName;
        this.firstAuthorsLastName = firstAuthorsLastName;
        this.year = year;
    }

    public String getSuggestedProjectName(KeyValue bioModelKey){
        String suggestedProjectName = "VCDB_"+bioModelKey;
        if (firstAuthorsLastName != null && year != null && compactJournalName != null) {
            suggestedProjectName += "_" + firstAuthorsLastName + "_" + compactJournalName+"_"+year;
        }
        return suggestedProjectName;
    }

    public static PublicationMetadata fromPublicationInfo(PublicationInfo publicationInfo) throws IOException {
        String abstractText = null;
        String compactJournal = null;
        String firstAuthorsLastName = null;
        Integer year = null;
        return new PublicationMetadata(publicationInfo, abstractText, compactJournal, firstAuthorsLastName, year);
    }

    public static PublicationMetadata fromPublicationInfoAndWeb(PublicationInfo publicationInfo) throws IOException {
        String abstractText = null;
        String compactJournal = null;
        String firstAuthorsLastName = null;
        Integer year = null;

        String[] citationLines = retrieveRISCitationFromPaperpile(publicationInfo);
        HashMap<String, String> citationFieldsRIS = extractCitationRIS(citationLines);

        if (citationFieldsRIS.containsKey("AB")) {
            abstractText = citationFieldsRIS.get("AB");
        }
        if (citationFieldsRIS.containsKey("T2")) {
            compactJournal = citationFieldsRIS.get("T2");
            // replace all spaces, tabs, commas, periods, semicolons with empty string
            compactJournal = compactJournal.replaceAll("[\\s,.;]", "");
        }
        if (citationFieldsRIS.containsKey("PY")) {
            year = Integer.parseInt(citationFieldsRIS.get("PY"));
        }
        if (citationFieldsRIS.containsKey("AU")) {
            firstAuthorsLastName = citationFieldsRIS.get("AU").split(",")[0];
        }
        return new PublicationMetadata(publicationInfo, abstractText, compactJournal, firstAuthorsLastName, year);
    }

    private static String[] retrieveRISCitationFromPaperpile(PublicationInfo publicationInfo) throws IOException {
        //
        // retrieve citation in RIS format from a PMID (Pubmed ID) from Paperpile API
        //
        // curl -X POST -H 'Content-Type: application/json' \
        //    -d '{"fromIds": true, "input": "34765087", "targetFormat": "Ris"}' \
        //    https://api.paperpile.com/api/public/convert
        //
        URL url = new URL("https://api.paperpile.com/api/public/convert");
        String payloadJson = "{\"fromIds\": true, \"input\": \"" + publicationInfo.getPubmedid() + "\", \"targetFormat\": \"Ris\"}";
        PostMethod postMethod = new PostMethod(url.toString());
        postMethod.addRequestHeader("Content-Type", "application/json");
        postMethod.setRequestEntity(new StringRequestEntity(payloadJson, "application/json", "UTF-8"));

        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(postMethod);
        String responseString = postMethod.getResponseBodyAsString();

        // parse response
        Gson gson = new Gson();
        PaperpileResponse paperpileResponse = gson.fromJson(responseString, PaperpileResponse.class);
        if (paperpileResponse.error != null && paperpileResponse.error.length() > 0) {
            throw new IOException("Paperpile API returned error: " + paperpileResponse.error);
        }

        // extract citation in RIS format (lines of text)
        String risCitation = paperpileResponse.output;
        String[] citationLines = risCitation.split("\n");
        return citationLines;
    }

    static HashMap<String, String> extractCitationRIS(String[] citationLines) {
        /**
         * Example citation in RIS format:
         *
         * TY  - JOUR
         * AU  - Nosbisch, Jamie L
         * AU  - Bear, James E
         * AU  - Haugh, Jason M
         * AD  - Biomathematics Graduate Program, North Carolina State University, Raleigh,
         *       North Carolina, USA.; <<clipped>>
         * TI  - A kinetic model of phospholipase C-γ1 linking structure-based insights to
         *       dynamics of enzyme autoinhibition and activation
         * T2  - J. Biol. Chem.
         * VL  - 298
         * IS  - 5
         * SP  - 101886
         * PY  - 2022
         * DA  - 2022/5
         * PB  - Elsevier BV
         * AB  - Phospholipase C-γ1 (PLC-γ1) is a receptor-proximal enzyme that promotes
         *       signal transduction through PKC in mammalian cells. Because of the
         *       <<clipped>>
         *       membrane-proximal enzymes.
         * SN  - 0021-9258
         * DO  - 10.1016/j.jbc.2022.101886
         * C2  - PMC9097458
         * UR  - http://dx.doi.org/10.1016/j.jbc.2022.101886
         * UR  - https://www.ncbi.nlm.nih.gov/pubmed/35367415
         * UR  - https://www.ncbi.nlm.nih.gov/pmc/articles/PMC9097458
         * KW  - mathematical model
         * KW  - phosphoinositide
         * KW  - phospholipase C
         * KW  - protein kinase C
         * KW  - receptor tyrosine kinase
         * ER  -
         */
        String currentLabel = null;
        String currentContent = "";
        HashMap<String, String> citationFields = new HashMap<>();
        for (String line : citationLines) {
            if (line.startsWith("ER")) {
                // end of record
                break;
            }
            if (line.startsWith("  ")) {
                // continuation of previous line
                currentContent += " "+line.substring(6);
            } else {
                // new line
                if (currentLabel != null) {
                    // save previous line but choose the first one (e.g. if multiple authors)
                    if (!citationFields.containsKey(currentLabel)) {
                        citationFields.put(currentLabel, currentContent);
                    }
                }
                // start new line
                currentLabel = line.substring(0, 2);
                currentContent = line.substring(6);
            }
        }
        return citationFields;
    }

    private static class PaperpileResponse {
        public String output;
        public String token;
        public String[] tags;
        public Boolean withErrors;
        public String error;
    }

}
