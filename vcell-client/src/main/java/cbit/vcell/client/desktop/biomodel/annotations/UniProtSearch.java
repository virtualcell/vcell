package cbit.vcell.client.desktop.biomodel.annotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.*;

public class UniProtSearch {

    private final String REST_URL = "https://rest.uniprot.org/uniprotkb/search?query=";
    private final List<SearchElement> searchElements = new ArrayList<>();
//    private String proteinName = "egfr";
//    private String gene = "grb2";
//    private String organismName = "homo";
//    private int size = 50;

    public List<SearchElement> search(String searchTerm, int searchSize, String organismName) throws IOException {
        //https://rest.uniprot.org/uniprotkb/search?query=
        //protein_name:egfr+OR+gene:grb2+AND+organism_name:homo&format=json&fields=accession,protein_name&size=1
        StringBuilder request = new StringBuilder();
        request.append(REST_URL);
        request.append(searchTerm);
        if (organismName.length() > 0) request.append("+AND+organism_name:").append(organismName);
        request.append("&format=json");
        request.append("&display_links=false");
        request.append("&fields=accession,protein_name");
        request.append("&size=").append(searchSize);

        String response = get(request.toString());
//        String response = get(REST_URL +
//                searchTerm +
//                "+AND+" +
//                "organism_name:" + organismName +
//                "&format=json" +
//                "&display_links=false" +
//                "&fields=accession,protein_name" +
//                "&size=" + searchSize
//        );

        System.out.println(response);
        System.out.println("Parsing JSON\n");
        JSONObject obj = new JSONObject(response);
//        String accession = obj.getJSONObject("pageInfo").getString("pageName");
        JSONArray results = obj.getJSONArray("results"); // notice that `"posts": [...]`
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String accession = result.getString("primaryAccession");

            System.out.println("Accession: " + accession);
            String name;
            if (result.toString().contains("submissionNames")) {
                name = result
                        .getJSONObject("proteinDescription")
                        .getJSONArray("submissionNames")
                        .getJSONObject(0)
                        .getJSONObject("fullName")
                        .getString("value");
            } else {
                name = results.getJSONObject(i)
                        .getJSONObject("proteinDescription")
                        .getJSONObject("recommendedName")
                        .getJSONObject("fullName")
                        .getString("value");
            }
            System.out.println("Name: " + name+"\n");

            //Add searchElements to list
            searchElements.add(new SearchElement(name, accession));
        }

//        System.out.println("\nSearch Elements:");
//        for (SearchElement element: searchElements) {
//            System.out.println(element.toString()+"\n");
//        }

        //        System.out.println("Status Code: " + response.statusCode());
//        System.out.println("Response Body:\n" + response.body());
        return searchElements;
    }

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
//        conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
//        conn.setRequestProperty("Accept", "application/" + format);
        System.out.println("Status Code: " + conn.getResponseCode());

        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        return result.toString();
    }
}

