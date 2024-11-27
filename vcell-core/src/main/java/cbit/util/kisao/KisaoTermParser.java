package cbit.util.kisao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;


public class KisaoTermParser {
	private final static Logger lg = LogManager.getLogger(KisaoTermParser.class);
	
//	final String Kisao_OBO = "kisao_2020_12_13.obo";
	final String Kisao_OBO = "kisao_algs.obo";
	
	final String TERM_PATTERN = "\\[Term\\]";
	final Pattern ID_PATTERN = Pattern.compile("id:\\s*(.+)");
	final Pattern NAME_PATTERN = Pattern.compile("name:\\s*(.+)");
	final Pattern ISA_PATTERN = Pattern.compile("is_a:\\s*(\\S+)");
	
	KisaoOntology parse() {
		KisaoOntology ontology = new KisaoOntology();
		KisaoTerm curr = null;
		boolean inState = false;

		InputStream is = KisaoTermParser.class.getClassLoader().getResourceAsStream(Kisao_OBO);
		if (is == null) throw new RuntimeException("Unable to load OBO file necessary for KiSAO!");

		try {
			for (String line : (new BufferedReader(new InputStreamReader(is))).lines().toList()){
				if (line.matches(TERM_PATTERN)) {
					inState = true;
					curr = new KisaoTerm();
				}
				if (line.matches("^$") && curr != null) {
					inState = false;
					ontology.addTerm(curr);
					curr = null;
				}

				if(inState) {
					Matcher matcher = ID_PATTERN.matcher(line);
					if (matcher.find()) {
						String group1 = matcher.group(1);
						curr.setId(group1.startsWith("kisao:") ? group1.substring(6) : group1);
					}

					matcher = NAME_PATTERN.matcher(line);
					if (matcher.find()) {
						curr.setName(matcher.group(1));
					}
					matcher = ISA_PATTERN.matcher(line);
					if (matcher.find()) {
						String group1 = matcher.group(1);
						curr.addIsaRef(group1.startsWith("kisao:") ? group1.substring(6) : group1);
					}
				}
			}
		} catch (Exception e) {
			lg.error(e.getMessage(), e);
		}
		
		ontology.createRelations();
		return ontology;
	}
}
