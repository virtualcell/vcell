package org.vcell.sedml;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PublicationInfo;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

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
        int year = 0;

        String[] citationLines = new String[0];
        try {
            citationLines = retrieveRISCitationFromPubmed(publicationInfo);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // AB  - Phospholipase C-γ1 (PLC-γ1) is a receptor-proximal ...
        Optional<String> abstractEntry = Arrays.stream(citationLines).filter(line -> line.startsWith("AB  - ")).findFirst();
        if (abstractEntry.isPresent()) {
            abstractText = abstractEntry.get().replace("AB  - ","").trim();
        }

        // J2  - J Biol Chem
        Optional<String> journalEntry = Arrays.stream(citationLines).filter(line -> line.startsWith("J2  - ")).findFirst();
        if (journalEntry.isPresent()) {
            compactJournal = journalEntry.get().replace("J2  - ","").trim();
            // replace all spaces, tabs, commas, periods, semicolons with empty string
            compactJournal = compactJournal.replaceAll("[\\s,.;]", "");
        }

        // Y1  - 2022/05/
        Optional<String> yearEntry = Arrays.stream(citationLines).filter(line -> line.startsWith("Y1  - ")).findFirst();
        if (yearEntry.isPresent()) {
            year = Integer.parseInt(yearEntry.get().replace("Y1  - ","").split("/")[0].trim());
        }

        // AU  - Nosbisch, Jamie L
        Optional<String> firstAuthorEntry = Arrays.stream(citationLines).filter(line -> line.startsWith("AU  - ")).findFirst();
        if (firstAuthorEntry.isPresent()) {
            firstAuthorsLastName = firstAuthorEntry.get().replace("AU  - ","").split(",")[0].trim();
        }
        return new PublicationMetadata(publicationInfo, abstractText, compactJournal, firstAuthorsLastName, year);
    }

    private static String[] retrieveRISCitationFromPubmed(PublicationInfo publicationInfo) throws IOException, InterruptedException {
        Thread.sleep(500); // to avoid rate limiting

        //
        // retrieve citation in RIS format from a PMID (Pubmed ID) from Paperpile API
        //
        // curl -H 'Content-Type: plain/text' -X GET 'https://api.ncbi.nlm.nih.gov/lit/ctxp/v1/pubmed/?format=ris&id=35367415'
        //
        URL url = new URL("https://api.ncbi.nlm.nih.gov/lit/ctxp/v1/pubmed/?format=ris&id=" + publicationInfo.getPubmedid());
        GetMethod getMethod = new GetMethod(url.toString());
        getMethod.addRequestHeader("Content-Type", "text/plain");

        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(getMethod);
        if (getMethod.getStatusCode() != 200) {
            throw new IOException("Failed to retrieve citation from Pubmed: " + getMethod.getStatusText());
        }
        String responseString = getMethod.getResponseBodyAsString();

        // extract citation in RIS format (lines of text)
        return responseString.split("\n");
    }

}
