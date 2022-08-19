package cbit.vcell.client.desktop.biomodel.annotations;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BioPortalSearch {
    // base URL for class search
    private final String REST_URL = "http://data.bioontology.org/search?q=";
    private final String API_KEY = "d77db9ae-3fb4-4cc9-b95a-5ea18b1bfda4";
    private String format;
    private final List<searchElement> searchElements = new ArrayList<>();

    public List<searchElement> search(String searchTerm) throws IOException, URISyntaxException, ParserConfigurationException, InterruptedException, SAXException {
        return search(searchTerm, 5000, "", false);
    }

    public List<searchElement> search(String searchTerm, int pageSize, String ontologyNicknames, boolean requireExactMatch) throws IOException, SAXException, URISyntaxException, ParserConfigurationException, InterruptedException {
        //Advanced Search Attributes
        String includeAttr = "prefLabel,synonym,definition"; //,notation,cui,semanticType,properties
        format = "xml";
//        String pageSize = "500"; //max page size
//        boolean requireExactMatch = false;

        // GET request returns xml string for processing
//        HttpResponse<String> response = get(REST_URL +
        String response = get(REST_URL +
                searchTerm +
                "&include=" + includeAttr +
                "&pagesize=" + pageSize +
                "&display_context=false" +
                "&display_links=false" +
                "&ontologies=" + ontologyNicknames +
                "&require_exact_match=" + requireExactMatch
        );
//        System.out.println("Status Code: " + response.statusCode());
//        System.out.println("Response Body:\n" + response.body());
        System.out.println();

        SAXParserFactory fact = SAXParserFactory.newInstance();
        SAXParser saxParser = fact.newSAXParser();

        //List to hold all search results


//        boolean collectionEnds = false;
        DefaultHandler handle = new DefaultHandler() {
            boolean bTotalCount=false, bPrefLabel=false, bSynonym=false, bDefinition=false, bId=false, collectionEnds=false;
            String prefLabel, definition, id;
            final ArrayList<String> synonyms = new ArrayList<>();
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                //System.out.println("Start Element: " + qName);
                switch (qName) {
                    case "totalCount":
                        bTotalCount = true;
                        break;
                    case "class":
                        prefLabel = "";
                        synonyms.clear();
                        definition = "";
                        id = "";
                        break;
                    case "prefLabel":
                        bPrefLabel = true;
                        break;
                    case "synonym":
                        bSynonym = true;
                        break;
                    case "definition":
                        bDefinition = true;
                        break;
                    case "id":
                        bId = true;
                        break;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                //System.out.println("End Element: " + qName);
                if (qName.equals("collection")) collectionEnds = true;
//                if (searchElements.size()-1 < noOfElements) {
//                    if (qName.equals("class")) {
                if (qName.equals("class") && !collectionEnds) {
                    searchElements.add(new searchElement(prefLabel, synonyms, definition, id));
//                    }
                }

            }

            @Override
            public void characters(char[] ch, int start, int length) {
//                if (bTotalCount) {
//                    noOfElements = Integer.parseInt(new String(ch,start,length));
//                    System.out.println("totalCount: " + noOfElements +"\n");
//                    bTotalCount = false;
//                }
                if (bPrefLabel) {
                    prefLabel = new String(ch,start,length);
                    //System.out.println("prefLabel: " + prefLabel);
                    bPrefLabel = false;
                }
                if (bSynonym) {
                    String synonym = new String(ch,start,length);
                    synonyms.add(synonym);
                    //System.out.println("synonym: " + synonym);
                    bSynonym = false;
                }
                if (bDefinition) {
                    definition = new String(ch,start,length);
                    //System.out.println("definition: " + definition);
                    bDefinition = false;
                }
                if (bId) {
                    id = new String(ch,start,length);
                    //System.out.println("id: " + id);
                    bId = false;
                    //System.out.println();
                }

            }
        };

        saxParser.parse(new InputSource(new StringReader(response)), handle);
        for (searchElement element: searchElements) {
            System.out.println(element.toString());
        }
        return searchElements;
    }

    //    private HttpResponse<String> get(String urlToGet) throws URISyntaxException, IOException, InterruptedException {
    private String get(String urlToGet) throws IOException {
        //build request
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        StringBuilder result = new StringBuilder();

        url = new URL(urlToGet);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
        conn.setRequestProperty("Accept", "application/" + format);
        System.out.println("Status Code: " + conn.getResponseCode());

        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        return result.toString();
        /////////////////////////////////////////////////////////////////////////////////////
//        HttpRequest GET_Request = HttpRequest.newBuilder()
//                .uri(new URI(urlToGet))
//                .header("Authorization", "apikey token=" + API_KEY)
//                .header("Accept", "application/" + format)
//                .GET()
//                .build();
//        System.out.println("GET Request Built: " + GET_Request);
//
//        //instantiate client to send request
//        HttpClient httpClient = HttpClient.newHttpClient();
//
//        //use client to send request and store response
//        return httpClient.send(GET_Request, HttpResponse.BodyHandlers.ofString());
    }
}

