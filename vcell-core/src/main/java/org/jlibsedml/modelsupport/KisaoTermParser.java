package org.jlibsedml.modelsupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class KisaoTermParser {
    final String Kisao_OBO = "kisao_rev28_vSat_Jan_24_18-58-35_2009_UTC-2.obo";

    final String TERM_PATTERN = "\\[Term\\]";

    final Pattern ID_PATTERN = Pattern.compile("id:\\s*(.+)");

    final Pattern NAME_PATTERN = Pattern.compile("name:\\s*(.+)");

    final Pattern DEF_PATTERN = Pattern.compile("def:\\s*(.+)");

    final Pattern ISA_PATTERN = Pattern.compile("is_a:\\s*(\\S+)");

    final Pattern IS_OBSOLETE = Pattern.compile("is_obsolete:");

    final Pattern EXACT_SYN = Pattern.compile("exact_synonym:\\s\"(.+)\"");

    final Pattern NARROW_SYN = Pattern.compile("narrow_synonym:\\s\"(.+)\"");

    KisaoOntology parse() {
        InputStream is2 = KisaoTermParser.class.getClassLoader()
                .getResourceAsStream(Kisao_OBO);
        BufferedReader isr = new BufferedReader(new InputStreamReader(is2));
        String line = null;
        boolean inPreamble = true;
        boolean inState = false;
        KisaoOntology ontology = new KisaoOntology();
        KisaoTerm curr = null;
        new KisaoTerm();
        try {
            while ((line = isr.readLine()) != null) {
                if (line.matches(TERM_PATTERN)) {
                    inState = true;
                    curr = new KisaoTerm();
                }
                if (line.matches("^$") && curr != null) {
                    inState = false;
                    inPreamble = false;
                    ontology.add(curr);
                    curr = null;

                }
                if (inState) {
                    Matcher matcher = ID_PATTERN.matcher(line);
                    if (matcher.find()) {
                        curr.setId(matcher.group(1));
                    }

                    matcher = NAME_PATTERN.matcher(line);
                    if (matcher.find()) {
                        curr.setName(matcher.group(1));
                    }

                    matcher = DEF_PATTERN.matcher(line);
                    if (matcher.find()) {
                        curr.setDef(matcher.group(1));
                    }

                    matcher = EXACT_SYN.matcher(line);
                    if (matcher.find()) {
                        curr.addExactSynonym(matcher.group(1));
                    }
                    matcher = NARROW_SYN.matcher(line);
                    if (matcher.find()) {
                        curr.addNarrowSynonym(matcher.group(1));
                    }
                    matcher = ISA_PATTERN.matcher(line);
                    if (matcher.find()) {
                        curr.addIsaRef(matcher.group(1));
                    }

                    matcher = IS_OBSOLETE.matcher(line);
                    if (matcher.find()) {
                        curr.setObsolete(true);
                    }

                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ontology.createRelations();
        return ontology;
    }

}
