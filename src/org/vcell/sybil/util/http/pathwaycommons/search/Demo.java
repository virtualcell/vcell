package org.vcell.sybil.util.http.pathwaycommons.search;

/*   Demo  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Demonstrate Pathway Commons query capabilities
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;

public class Demo {
	
	public static void main(String[] args) {
		while(true) {
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
			String keyword = "quit";
			try { 
				System.out.println("Enter a keyword (type 'quit' to quit): ");
				keyword = cin.readLine();
				if(keyword.equals("quit")) { break; }
				PCKeywordRequest request = new PCKeywordRequest(keyword);
				PathwayCommonsResponse response = request.response();
				if(response instanceof PCKeywordResponse) {
					PCKeywordResponse keywordResponse = (PCKeywordResponse) response;
					System.out.println(keywordResponse.toString());
					if(keywordResponse.hits() != null) {
						for(Hit hit : keywordResponse.hits()) {
							for(Pathway pathway : hit.pathways()) {
								String pcId = pathway.primaryId();
								if(pcId != null && pcId.length() > 0) {
									String substanceName = hit.names().size() > 0 ? hit.names().get(0) : 
										"[no name]";
									String pathwayName = pathway.name();
									if(pathwayName == null || pathwayName.length() < 1) {
										
									}
									System.out.println(pcId + "  " + substanceName + "   " + pathwayName);								
								}
							}
						}					
					}					
				} else if (response instanceof PCErrorResponse) {
					PCErrorResponse errorResponse = (PCErrorResponse) response;
					if(errorResponse.error != null) { 
						System.out.println(errorResponse.error().toString()); 
						continue;
					}
				}
			} catch (MalformedURLException e) { e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }
		}
	}
}
