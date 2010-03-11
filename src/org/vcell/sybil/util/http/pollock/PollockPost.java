package org.vcell.sybil.util.http.pollock;

/*   PollockPost  --- by Oliver Ruebenacker, UCHC --- February 2010
 *   Make an HTTP POST request to pollock, a script that accepts POST requests
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.util.text.StringUtil;

public class PollockPost {
	
	public static final String urlPollock = "http://vcell.org/sybil/php/pollock.php";
	public static final String encoding = "UTF-8";
	public static final String separator = "&";
	
	public static void request(Map<String, String> data) 
	throws MalformedURLException, UnsupportedEncodingException, IOException {
		Set<String> pairs = new HashSet<String>();
		for(Map.Entry<String, String> entry : data.entrySet()) {
			String keyEncoded = URLEncoder.encode(entry.getKey(), encoding);
			String valueEncoded = URLEncoder.encode(entry.getValue(), encoding);
			pairs.add(keyEncoded + "=" + valueEncoded);
		}
		String dataEncoded = StringUtil.concat(pairs, separator);
		URL url = new URL(urlPollock);
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(dataEncoded);
		writer.flush();
		BufferedReader reader = 
			new BufferedReader(new InputStreamReader(connection.getInputStream())); 
		String line; while ((line = reader.readLine()) != null) { 
			System.out.println(line);
		} 
		writer.close(); 
		reader.close(); 
	}
	
	public static void main(String[] args) {
		System.out.println("This is PollockPost");
		Map<String, String> data = new HashMap<String, String>();
		data.put("subject", "SyBiL User Feedback");
		data.put("blub", "I love to flup");
		data.put("sun", "shining on me");
		data.put("grass", "the grass is always greener \n on the other side");
		try {
			request(data);
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
