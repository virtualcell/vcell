package org.vcell.sybil.models.probe;

/*   Feedback  --- by Oliver Ruebenacker, UCHC --- February 2010
 *   Feedback to be sent by the application to the developers
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.vcell.sybil.util.http.pollock.PollockPost;

public class Feedback {

	Map<String, String> content = new HashMap<String, String>();
	
	public void add(String key, Probe probe) { content.put(key, ProbeUtil.prettyPrint(probe)); }
	public void add(String key, String value) { content.put(key, value); }
	
	public Feedback() {
		add("Special Message By SyBiL", "This was sent by SyBiL");
		add("subject", "SyBiL Feedback");		
	}
	
	public void send() throws MalformedURLException, UnsupportedEncodingException, IOException {
		PollockPost.request(content);
	}
	
	
	public static void main(String[] args) {
		Feedback feedback = new Feedback();
		feedback.add("default probes", ProbesDefault.probesAll);
		feedback.add("message", "some nice feedback");
		try {
			feedback.send();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
