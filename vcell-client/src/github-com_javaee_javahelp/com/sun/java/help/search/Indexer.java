/*
 * @(#)Indexer.java	1.46 06/10/30
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
 * @(#) Indexer.java 1.46 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.io.*;
import java.text.*;
import java.net.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Locale;
import javax.help.search.IndexerKit;
import javax.help.search.ConfigFile;
import com.sun.java.help.impl.HeaderParser;

/**
 * This class provides ...
 *
 *
 * @version	1.46	10/30/06
 * @author	Roger D. Brinkley
 */

public class Indexer {

    /** The database directory name. */
    String dbName = "JavaHelpSearch";

    /** The stream for printing syntax errors */
    PrintStream verbose=null;
    PrintStream logStream=null;

    /** The number of documents correctly parsed and compiled. */
    short docNumber;

    private DefaultIndexBuilder indexBuilder = null;

    private ConfigFile config;
    private String title;
    private String header;
    private boolean firstHeader=false;
    String lastPCdata;
    int currentPos;
    boolean openTagSeen = false;
    boolean inPre = false;
    boolean inTitle = false;
    boolean inFirstHeader = false;
    boolean inOption = false;
    int inBlock = 0;
    String sourcepath;
    /**
     * Starts the application.
     * @param args[]   the command line arguments
     **/

    public static void main(String args[]) throws Exception {
	Indexer compiler = new Indexer();
    compiler.compile(args);
    }

    /**
     * Creates an initialised compiler.
     **/

    public Indexer() {
	// Make sure the last character is a file separator
	if (dbName.lastIndexOf("/")
	    != dbName.length() - 1) {
	    dbName = dbName.concat("/");
	}
	docNumber = 0;
    }

    /**
     * Parses the <tt>.html</tt> files, and compiles the search database.
     * @param args[]   the command line arguments
     **/

    public void compile(String args[]) throws Exception {
	long parseTime, compileTime, startTime = System.currentTimeMillis();
	int words;
	String configFile = null;
	Vector tmpfiles = new Vector();
	Vector files = new Vector();
	String file;
	int size;
	sourcepath="";
	boolean nostopwords = false;
	
	for (int i=0; i < args.length ; i++) { 
	    if(args[i].equals("-db")) {
		if ((i + 1) < args.length) {
		    dbName = args[++i];
		    // Make sure the last character is a file separator
		    if (dbName.lastIndexOf("/")
			!= dbName.length() - 1) {
			dbName = dbName.concat("/");
		    }
		} else {
		    System.out.println (args[i] + "-db requires argument");
		}
	    }
	    else if(args[i].equals("-sourcepath")) {
		if ((i + 1) < args.length) {
		    sourcepath = args[++i];
		} else {
		    System.out.println (args[i] + "-sourcepath requires argument");
		}
	    }	   
	    else if(args[i].equals("-locale")) {
		if ((i + 1) < args.length) {
		    defaultLang= args[++i];
		} else {
		    System.out.println (args[i] + "-locale requires argument");
		}
	    }	   
	    else if(args[i].equals("-logfile")) {
		if ((i + 1) < args.length) {
		    String logFile= args[++i];
		    try {
			logStream = new PrintStream(new FileOutputStream(logFile)
						   ,true);
			System.setErr(logStream);
			System.setOut(logStream);
			verbose = logStream;
		    } catch (java.io.FileNotFoundException ex) {
			System.out.println("Couldn't create logFile " + logFile);
		    }
		} else {
		    System.out.println (args[i] + "-logfile requires argument");
		}
	    }	   
	    else if(args[i].equals("-verbose")) verbose = System.out;
	    else if(args[i].equals("-nostopwords")) nostopwords = true;
	    else if(args[i].equals("-c")) {
		if ((i + 1) < args.length) {
		    configFile = args[++i];
		} else {
		    System.out.println (args[i] + "-c requires argument");
		}
	    }
	    else if(args[i].startsWith("-")) {
		System.out.println("Unknown argument '" + args[i] + "'");
		showUsage();
		return;
	    } else {
	        tmpfiles.addElement(args[i]);
	    }
	}

	// read the config file
	config = new ConfigFile (configFile, files, nostopwords);
	// get the files from the config file
	files = config.getFiles();

	// adjust the passed with config parameters
	size = tmpfiles.size();
	for (int i=0; i < size; i++) {
	    files = loadFiles ((String)tmpfiles.elementAt(i), files);
	}

	size = files.size();
	if (size == 0) {
		System.out.println("No files specified to index");
		showUsage();
		return;
	}

	indexBuilder= new DefaultIndexBuilder(dbName);
	// set the stopwords in the indexBuilder
	indexBuilder.storeStopWords(config.getStopWords());
    
	for (int i=0; i < size; i++) {
	    file = (String) files.elementAt(i);
	    URL url = new URL("file", "", sourcepath+file);
	    InputStream in = url.openStream();
	    URLConnection conn = url.openConnection();
	    String type = conn.getContentType();
	    setContentType(type);
	    if (kit != null) {
		try {
		    if (verbose != null) {
			verbose.println("   File: '" + file + "'");
			verbose.println("    URL: '" + config.getURLString(file) + "'");
		    }
		    parseFile(in, file, false);
		    in.close();
		} catch (UnsupportedEncodingException e1) {
		    System.out.println("File: '" + file + "' encoding " +
				       charSetName + " not supported");
		    in.close();
		    continue;
		} catch (IOException e) {
		    if (debugFlag) e.printStackTrace();
		    System.out.println("I/O exception occurred in file '" + sourcepath+file + "'");
		    in.close();
		    continue;
		}
	    }
	}
	parseTime = System.currentTimeMillis() - startTime;

	// show some final statistics

	compileTime = System.currentTimeMillis() - startTime - parseTime;
	if (verbose != null) {
	    verbose.println("        Parse time: " + (float)parseTime / 1000.0 + " s");
	}
	indexBuilder.close();
    }

  

    /**
     * This method invokes the indexerKit to initiate parsing.  In the 
     * case where a ChangedCharSetException is thrown this exception
     * will contain the new CharSet.  Therefore the parse() operation
     * is then restarted after building a new Reader with the new charset.
     *
     * @param the inputstream to use.
     * @param the document to load.
     *
     */
    void parseFile(InputStream in, String file, boolean ignoreCharset) throws IOException {
	try {
	    kit.parse(new InputStreamReader(in, charSetName), file, 
		      ignoreCharset, indexBuilder, config);
	} catch (com.sun.java.help.search.ChangedCharSetException e1) {
	    String charSetSpec = e1.getCharSetSpec();
	    if (e1.keyEqualsCharSet()) {
		charSetName = charSetSpec;
	    } else {
		setCharsetFromContentTypeParameters(charSetSpec);
	    }
	    in.close();
	    URL url = new URL("file", "", sourcepath+file);
	    in = url.openStream();
	    parseFile(in, file, true);
	}
    }


    public Vector loadFiles (String file, Vector files) {
	File tstfile = new File (file);
	if (tstfile.isDirectory()) {
	    String list[] = tstfile.list();
	    for (int i=0; i < list.length; i++) {
		files = loadFiles (tstfile.getPath() + 
				   File.separator +
				   list[i], files);
	    }
	} else {
	    files.addElement(file);
	}
	return files;
    }

    /**
     * Shows the usage message.
     **/

    public void showUsage() {
	System.out.println(" Usage:   java JavaHelp.Index options file ...");
	System.out.println(" Options: -c file   config file");
	System.out.println("          -db file  generated database file name");
	System.out.println("          -verbose  verbose documentation");
	System.out.println("          -nostopwords ignore stop words");
	System.out.println("          -locale language_country_variant");
	System.out.println("          -logfile log file name");
	System.out.println("Note: config file composition:");
	System.out.println("          IndexRemove /public_html/JavaHelp/demo");
	System.out.println("          IndexPrepend ..");
	System.out.println("          StopWords word1 ... wordN");
	System.out.println("          StopWordsFile stopWordFileName");
	System.out.println("          File /public_html/JavaHelp/demo/first.html");
	System.out.println("          File=/public_html/JavaHelp/demo/second.html");
	System.out.println("          ...");
    }

    /**
     * Current content binding of the indexer.
     */
    private IndexerKit kit;

    /**
     * Table of registered type handlers for this indexer.
     */
    private Hashtable typeHandlers;

    private String defaultCharSetName = "ISO8859_1";
    private String charSetName;
    private String defaultLang = Locale.getDefault().toString();
    private String lang;


    /*
     * Private AppContext keys for this class's static variables.
     */
    private static final Hashtable kitRegistry = new Hashtable();
    private static final Hashtable kitTypeRegistry = new Hashtable();
    private static final Hashtable kitLoaderRegistry = new Hashtable();

    static {
        // set the default bindings
        registerIndexerKitForContentType("text/plain",
					 "com.sun.java.help.search.PlainTextIndexerKit",
					 null);
	registerIndexerKitForContentType("text/html", 
					 "com.sun.java.help.search.HTMLIndexerKit",
					 null);

    }

    
    /**
     * Sets the type of content for the current document that this indexer
     * handles.  This calls <code>getIndexerKitForContentType</code>.
     * <p>
     * If there is a charset definition specified as a parameter
     * of the content type specification, it will be used when
     * loading input streams using the associated IndexerKit.
     * For example if the type is specified as 
     * <code>text/html; charset=EUC-JP</code> the content
     * will be loaded using the IndexerKit registered for
     * <code>text/html</code> and the Reader provided to
     * the IndexerKit to load unicode into the document will
     * use the <code>EUC-JP</code> charset for translating
     * to unicode.
     * 
     * @param type the non-null mime type for the content editing
     *   support.
     */
    public final void setContentType(String type) {
	// Set a charSetName to default incase it isn't defined
	charSetName = defaultCharSetName;
	// Set the lang to default lang incase it isn't defined
	lang = defaultLang;

	debug ("type=" + type);
	// The type could have optional info is part of it,
	// for example some charset info.  We need to strip that
	// of and save it.
	int parm = type.indexOf(";");
	if (parm > -1) {
	    // Save the paramList.
	    String paramList = type.substring(parm);
	    // update the content type string.
	    type = type.substring(0, parm).trim();
	    // Set the charset name from the paramlist
	    setCharsetFromContentTypeParameters(paramList);
	}
        if ((kit == null) || (! type.equals(kit.getContentType()))) {
            IndexerKit k = getIndexerKitForContentType(type);
            if (k != null) {
		kit = k;
            }
        }
	kit.setLocale(lang);
    }

    /**
     * This method get's the charset information specified as part
     * of the content type in the http header information.
     */
    private void setCharsetFromContentTypeParameters(String paramlist) {
	String charset = null;
	try {
	    // paramlist is handed to us with a leading ';', strip it.
	    int semi = paramlist.indexOf(';');
	    if (semi > -1 && semi < paramlist.length()-1) {
		paramlist = paramlist.substring(semi + 1);
	    }

	    if (paramlist.length() > 0) {
		// parse the paramlist into attr-value pairs & get the
		// charset pair's value
		HeaderParser hdrParser = new HeaderParser(paramlist);
		charset = hdrParser.findValue("charset");
		if (charset != null) {
		    charSetName = charset;
		}
	    }
	}
	catch (IndexOutOfBoundsException e) {
	    // malformed parameter list, use charset we have
	}
	catch (NullPointerException e) {
	    // malformed parameter list, use charset we have
	}
	catch (Exception e) {
	    // malformed parameter list, use charset we have; but complain
	    System.err.println("Indexer.getCharsetFromContentTypeParameters failed on: " + paramlist);
	    e.printStackTrace();
	}
    }

    /**
     * Fetches the indexer kit to use for the given type
     * of content.  This is called when a type is requested
     * that doesn't match the currently installed type.
     * If the component doesn't have an IndexerKit registered
     * for the given type, it will try to create an 
     * IndexerKit from the default IndexerKit registry.
     * If that fails, a PlainIndexerKit is used on the
     * assumption that all text documents can be represented
     * as plain text.
     * <p>
     * This method can be reimplemented to use some
     * other kind of type registry.  This can
     * be reimplemented to use the Java Activation
     * Framework for example.
     *
     * @param type the non-null content type
     * @return the indexer kit
     */  
    public IndexerKit getIndexerKitForContentType(String type) {
        if (typeHandlers == null) {
            typeHandlers = new Hashtable(3);
        }
        IndexerKit k = (IndexerKit) typeHandlers.get(type);
        if (k == null) {
            k = createIndexerKitForContentType(type);
            if (k != null) {
                setIndexerKitForContentType(type, k);
            }
        }
        if (k == null) {
            k = new DefaultIndexerKit();
	    setIndexerKitForContentType(type, k);
        }
        return k;
    }

    /**
     * Directly set the indexer kit to use for the given type.
     *
     * @param type the non-null content type
     * @param k the indexer kit to be set
     */
    public void setIndexerKitForContentType(String type, IndexerKit k) {
        if (typeHandlers == null) {
            typeHandlers = new Hashtable(3);
        }
        typeHandlers.put(type, k);
    }

    /**
     * Create a handler for the given type from the default registry
     * of indexer kits.  The registry is created if necessary.  An attempt
     * is made to dynamically load the prototype of the given kit.  If
     * successful it is cloned and returned.
     *
     * @param type the content type
     * @return the indexer kit, or null if one cannot be created
     */
    public static IndexerKit createIndexerKitForContentType(String type) {
	debug ("Getting IndexerKit for " + type);
        IndexerKit k = null;
	k = (IndexerKit) kitRegistry.get(type);
        if (k == null) {
            // try to dynamically load the support 
            String classname = (String) kitTypeRegistry.get(type);
	    ClassLoader loader = (ClassLoader) kitLoaderRegistry.get(type);
	    try {
		Class c;
		if (loader != null) {
		    c = loader.loadClass(classname);
		} else {
		    c = Class.forName(classname);
		}
                k = (IndexerKit) c.newInstance();
                kitRegistry.put(type, k);
            } catch (Throwable e) {
                if (debugFlag) e.printStackTrace();
                k = null;
            }
        }

        // create a copy of the prototype or null if there
        // is no prototype.
        if (k != null) {
            return (IndexerKit) k.clone();
        }
        return null;
    }

    /**
     * Establishes the default bindings of type to name.  
     * The class will be dynamically loaded later when actually
     * needed, and can be safely changed before attempted uses
     * to avoid loading unwanted classes.
     *
     * @param type the non-null content type
     * @param classname the class to load later
     */
    public static void registerIndexerKitForContentType(String type, String classname, ClassLoader loader) {
        kitTypeRegistry.put(type, classname);
	if (loader != null) {
	    kitLoaderRegistry.put(type, loader);
	}
    }


    /**
     * For printf debugging.
     */
    private static boolean debugFlag = false;
    private static void debug(String str) {
        if( debugFlag ) {
            System.out.println("Indexer: " + str);
        }
    }
}
