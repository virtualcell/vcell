/*
 * @(#)ConfigFile.java	1.8 06/10/30
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 * @(#) ConfigFile.java 1.8 - last change made 10/30/06
 */

package javax.help.search;

import java.io.*;
import java.text.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Describes and parses the configuration file for
 * the full-text search indexer.
 *
 * @see Indexer
 * @version	1.8	10/30/06
 * @author	Roger D. Brinkley
 */

public class ConfigFile {

    private String remove;

    private String prepend;

    private Hashtable stopWords;

    private String defStopWords[] = {
	"a", "all", "am", "an", "and", "any", "are", "as", "at", "be", 	"but",
	"by", "can", "could", "did", "do", "does", "etc", "for", "from",
	"goes", "got", 	"had", "has", "have", "he", "her", "him", "his",
	"how", "if", "in", "is", "it", "let", "me", "more", "much", "must",
	"my", "nor", "not", "now", "of", "off", "on", "or", "our", "own",
	"see", "set", "shall", "she", "should",  "so", "some", "than", "that",
	"the", "them", "then", "there", "these", "this", "those", "though",
	"to", "too", "us", "was", "way", "we", "what", "when", "where", 
	"which", "who", "why", "will", "would", "yes", "yet", "you"};

    private Vector files;

    /**
     * Creates a configuration file.
     * @params configFile The config file.
     * @params files A list of files to process in addition to any files in the config file.
     * @params noStopWords If true do not allow stopwords, if false allow the stopwords
     * defined in the config file or the default stop words.
     */
    public ConfigFile (String configFile, Vector files, boolean noStopWords) {
	this.files = files;
	LineNumberReader in;
	String line;
	String removeText = new String ("IndexRemove ");
	String prependText = new String ("IndexPrepend ");
	String fileText = new String ("File ");
	String stopWordsText = new String ("StopWords ");
	String stopWordsFileText = new String ("StopWordsFile ");
	BreakIterator boundary;
	int start;
	String url;

	stopWords = new Hashtable();

	if (configFile == null) {
	    if (!noStopWords) {
		useDefaultStopWords();
	    }
	    return;
	}
	try {
	    in = new LineNumberReader(new BufferedReader
				      (new FileReader(configFile)));
	    while ((line = in.readLine()) != null) {
		if (line.startsWith(removeText)) {
		    remove = line.substring (removeText.length(),
					     line.length());
		} else if (line.startsWith(prependText)) {
		    prepend = line.substring (prependText.length(),
					      line.length());
		} else if (line.startsWith(fileText)) {
		    String file = line.substring (fileText.length(),
						  line.length());
		    files.addElement(file);
		} else if (line.startsWith(stopWordsFileText)) {
		    String file = line.substring (stopWordsFileText.length(),
						  line.length());
		    addStopWordsFile(file);
		} else if (line.startsWith(stopWordsText)) {
		    if (noStopWords) {
			continue;
		    }
		    String words = line.substring (stopWordsText.length(),
						   line.length());
		    boundary = BreakIterator.getWordInstance();
		    boundary.setText(words);
		    start = boundary.first();
		    for (int end = boundary.next();
			 end != BreakIterator.DONE;
			 start = end, end = boundary.next()) {
			String word = words.substring(start,end).trim().toLowerCase();
			if (word.equals(",") || word.equals("")) {
			    continue;
			}
			stopWords.put(word, word);
		    }
		} else {
		    System.out.println ("Unknown Config Keyword at line " +
					in.getLineNumber());
		}
	    }
	    // If there aren't any stopwords then add the default stopwords
	    if (stopWords.isEmpty() && !noStopWords) {
		useDefaultStopWords();
	    }
	} catch (IOException e) {}
    }

    /**
     * Returns the URL filename of a file in String format.
     */
    public String getURLString (String file) {
	String url;

	if (remove != null && (file.startsWith(remove))) {
	    url = file.substring(remove.length(), file.length());
	} else {
	    url = file;
	}
	if (prepend != null) {
	    url = prepend + url;
	}
	if (File.separatorChar != '/') {
	    url = url.replace(File.separatorChar, '/');
	}
	return url;
    }

    /**
     * Returns the list of stopwords from a config file.
     */
    public Enumeration getStopWords() { return stopWords.elements(); }

    /**
     * Gets the list of files from a config file.
     */
    public Vector getFiles () { return files; }

    private void useDefaultStopWords() {
	for (int i=0; i < defStopWords.length; i++) {
	    stopWords.put(defStopWords[i], defStopWords[i]);
	}
    }

    // Add stopwords from a file
    // A single stop words exist per line in the file
    private void addStopWordsFile(String swfile) {
	String word;
	LineNumberReader in;

	if (swfile == null) {
	    return;
	}
	try {
	    in = new LineNumberReader(new BufferedReader
				      (new FileReader(swfile)));
	    while ((word = in.readLine()) != null) {
		word = word.trim();
		stopWords.put(word, word);
	    }
	} catch (IOException e) {}
    }
}
