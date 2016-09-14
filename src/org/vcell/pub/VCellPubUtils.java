package org.vcell.pub;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.util.xml.XmlUtil;

import com.google.gson.Gson;

public class VCellPubUtils {
	
	public static class PubMedData {
		public Record[] variables;
	}
	
	public static class Record {
		String modelKey;
		String pubMedId;
		String URL;
		String pubTitle;
		String[] authors;
		String pubCitation;
	}

	private static void read() throws IOException {
		System.out.println("starting to read");
		Gson gson = new Gson();
		FileReader reader = new FileReader("src/org/vcell/pub/pubMedData.json");
		PubMedData pubMedData = null;
		try {
			pubMedData = gson.fromJson(reader, PubMedData.class);
		}finally{
			reader.close();
		}
		HashMap<Publication,PublishedModels> publishedModels = new HashMap<Publication,PublishedModels>();
		int countModels = 0;
		for (Record r : pubMedData.variables){
			Publication pub = new Publication(null,r.pubTitle,r.authors,r.pubCitation,r.pubMedId,null,null);
			PublishedModels pubModels = publishedModels.get(pub);
			if (pubModels==null){
				pubModels = new PublishedModels(pub, new KeyValue[] { new KeyValue(r.modelKey) }, new KeyValue[0] );
			}else{
				pubModels = new PublishedModels(pub, BeanUtils.addElement(pubModels.bioModelKeys, new KeyValue(r.modelKey)), pubModels.mathModelKeys );
			}
			publishedModels.put(pub, pubModels);  // add or replace (new model reference).
			countModels++;
		}
		int countPubs = 0;
		HashSet<String> uniqueCitations = new HashSet<String>();
		for (PublishedModels pm : publishedModels.values()){
			uniqueCitations.add(pm.vcellPub.title);
			System.out.println("citation="+pm.vcellPub.citation+", authors=("+pm.vcellPub.authors.length+")"+Arrays.asList(pm.vcellPub.authors)+", bmkeys="+Arrays.asList(pm.bioModelKeys)+", mmKeys="+Arrays.asList(pm.mathModelKeys));
			countPubs++;
		}
		System.out.println(countModels+" models, "+countPubs+" publications, "+uniqueCitations.size()+" unique titles");
		ArrayList<PublishedModels> pubModels = new ArrayList<PublishedModels>(publishedModels.values());
		for (int i=0;i<pubModels.size();i++){
			for (int j=0;j<pubModels.size();j++){
				if (i!=j){
					if (pubModels.get(i).vcellPub.title.equals(pubModels.get(j).vcellPub.title)){
						System.out.println("found two which are the same:");
						PublishedModels pm1 = pubModels.get(i);
						PublishedModels pm2 = pubModels.get(j);
						System.out.println("i="+i+", citation="+pm1.vcellPub.citation+", authors=("+pm1.vcellPub.authors.length+")"+Arrays.asList(pm1.vcellPub.authors)+", bmkeys="+Arrays.asList(pm1.bioModelKeys)+", mmKeys="+Arrays.asList(pm1.mathModelKeys));
						System.out.println("j="+j+", citation="+pm2.vcellPub.citation+", authors=("+pm2.vcellPub.authors.length+")"+Arrays.asList(pm2.vcellPub.authors)+", bmkeys="+Arrays.asList(pm2.bioModelKeys)+", mmKeys="+Arrays.asList(pm2.mathModelKeys));
						boolean equal = pm1.vcellPub.equals(pm2.vcellPub);
						System.out.println("vcellPubs are equal = "+equal);
						System.out.println("");
						System.out.println(pm1.vcellPub);
						System.out.println("");
						System.out.println(pm2.vcellPub);
					}
				}
			}
		}
		/**
		 * 	public final Field id					= new Field("title",			"VARCHAR2(4000)",	"")
		 * 	public final Field title				= new Field("title",			"VARCHAR2(4000)",	"")
		 *  public final Field authors				= new Field("authors",			"VARCHAR2(4000)",	"");
		 *  public final Field year					= new Field("year",				"Integer",			"");
		 *  public final Field citation				= new Field("citation",			"VARCHAR2(4000)",	"");
		 *  public final Field pubmedid				= new Field("pubmedid",			"VARCHAR2(32)",		"");
		 *  public final Field doi					= new Field("doi",				"VARCHAR2(50)",		"");
		 *  public final Field endnodeid			= new Field("endnoteid",		"VARCHAR2(50)",		"");
		 *  public final Field url					= new Field("url",				"VARCHAR2(128)",	"");
		 *  public final Field wittid				= new Field("wittid",			"Integer",			"");
		 */
		ArrayList<Publication> sortedWittPubs = new ArrayList<Publication>(publishedModels.keySet());
		sortedWittPubs.sort((pub1,pub2) -> (pub1.toString().compareTo(pub2.toString())));
		
		for (Publication pub : sortedWittPubs){
			String title = 		"'"+TokenMangler.getSQLEscapedString(pub.title)+"'";
			String authors = 	"'"+TokenMangler.getSQLEscapedString(String.join(";",pub.authors))+"'";
			String citation = 	"'"+TokenMangler.getSQLEscapedString(pub.citation)+"'";
			String pubmedid = 	"NULL";
//			if (pub.pubmedid != null){
//				pubmedid = 		"'"+TokenMangler.getSQLEscapedString(pub.pubmedid)+"'";
//			}
			String doi = 		"NULL";
//			if (pub.doi != null){
//				doi =	 		"'"+TokenMangler.getSQLEscapedString(pub.doi)+"'";
//			}
			String endnoteid = "NULL";
			String url = "NULL";
			String wittid = Integer.toString(sortedWittPubs.indexOf(pub)); 
			String sql = "insert into vc_publication (id, title, authors, citation, pubmedid, doi, endnoteid, url, wittid) "
						+ "values (NewSeq.NEXTVAL,"+title+","+authors+","+citation+","+pubmedid+","+doi+","+endnoteid+","+url+","+wittid+")";
			System.out.println(sql+";");
		}
		Document doc = XmlUtil.readXML(new File("src/org/vcell/pub/NRCAMpublishedmodelsConvertedCopy.xml"));
		List<Element> recordList = doc.getRootElement().getChild("records").getChildren("record");
		int foundCount = 0;
		for (Element record : recordList){
			String titleString = record.getChild("titles").getChild("title").getChild("style").getText();
			ArrayList<String> authorsArray = new ArrayList<String>();
			List<Element> authorElements = (List<Element>)record.getChild("contributors").getChild("authors").getChildren("author");
			for (Element authorElement : authorElements){
				authorsArray.add(authorElement.getChild("style").getText());
			}
			boolean bFound = false;
			for (Publication pub : publishedModels.keySet()){
				if (pub.title.toUpperCase().trim().equals(titleString.toUpperCase().trim())){
					bFound = true;
					break;
				}
			}
			if (bFound){
				foundCount++;
			}
			String title = 		"'"+TokenMangler.getSQLEscapedString(titleString)+"'";
			String authors = 	"'"+TokenMangler.getSQLEscapedString(String.join(";",authorsArray))+"'";
			String citation = 	"NULL";
			String pubmedid = 	"NULL";
			String year = "NULL";
			try {
				year = ""+Integer.parseInt(record.getChild("dates").getChild("year").getChild("style").getText());
			}catch (Exception e){
				System.err.println("failed to parse year : "+e.getMessage());
			}
			try {
				pubmedid = "'"+record.getChild("accession-num").getChild("style").getText()+"'";
			}catch (Exception e){}
			String doi = 		"NULL";
			try {
				doi = "'"+record.getChild("electronic-resource-num").getChild("style").getText()+"'";
			}catch (Exception e){}
			String endnoteid =	record.getChild("rec-number").getText();
			String url = 		"NULL";
			try {
				url = "'"+record.getChild("urls").getChild("related-urls").getChild("url").getChild("style").getText()+"'";
			}catch (Exception e){}
			int wittid = -1;
			for (Publication pub : sortedWittPubs){
				String wittTitle = 		"'"+TokenMangler.getSQLEscapedString(pub.title)+"'";
				if (wittTitle.toUpperCase().trim().equals(title.toUpperCase().trim())){
					wittid = sortedWittPubs.indexOf(pub);
					citation = "'"+TokenMangler.getSQLEscapedString(pub.citation)+"'";
				}
			}
			String sql = "insert into vc_publication (id, title, authors, year, citation, pubmedid, doi, endnoteid, url, wittid) "
						+ "values (NewSeq.NEXTVAL,"+title+","+authors+","+year+","+citation+","+pubmedid+","+doi+","+endnoteid+","+url+","+wittid+")";
//			System.out.println(sql+";");
		}
		System.out.println("number of anns records found = "+foundCount);
		
		int wittCount=0;
		int linkRecordCount=0;
		for (Publication pub : sortedWittPubs){
			PublishedModels pm = publishedModels.get(pub);
			for (KeyValue pmKey : pm.bioModelKeys){
				String sql = "insert into vc_publicationmodellink (id, pubRef, bioModelRef, mathModelRef) "
						+ "values (NewSeq.NEXTVAL,(select id from vc_publication where wittid = "+wittCount+" and year is not null),"+pmKey+",NULL);";
				System.out.println(sql);
				linkRecordCount++;
			}
			wittCount++;
		}
		System.out.println("number of link records found = "+linkRecordCount);

		
	}

	public static void main(String[] args) {
		try {
			read();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
