/*
 * @(#)XmlReader.java	1.5 06/10/30
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
 * @(#) XmlReader.java 1.5 - last change made 10/30/06
 */

package com.sun.java.help.impl;

import java.io.*;
import java.net.URLConnection;

/**
 * This handles several XML-related tasks that normal java.io Readers
 * don't support, inluding use of IETF standard encoding names and
 * automatic detection of most XML encodings.  The former is needed
 * for interoperability; the latter is needed to conform with the XML
 * spec.  This class also optimizes reading some common encodings by
 * providing low-overhead unsynchronized Reader support.
 *
 * <P> Note that the autodetection facility should be used only on
 * data streams which have an unknown character encoding.  For example,
 * it should never be used on MIME text/xml entities.
 *
 * <P> Note that XML processors are only required to support UTF-8 and
 * UTF-16 character encodings.  Autodetection permits the underlying Java
 * implementation to provide support for many other encodings, such as
 * ISO-8859-5, Shift_JIS, EUC-JP, and ISO-2022-JP.
 *
 * @author David Brownell
 * @author Roger D. Brinkley
 * @version 1.24
 */

final public class XmlReader extends Reader
{
    private boolean		closed;
    private InputStreamReader	in;

    //
    // This class either handles reading itself, in common encodings
    // (US-ASCII, ISO-Latin-1, UTF-8) or delegates to another Reader.
    //
    // Autodetection requires reading/buffering part of the XML declaration,
    // then potentially switching _after entire characters are read_ to
    // delegate further operations to such a Reader.  The reader will get
    // no header (e.g. no UTF-16 Byte Order Mark).  This can work since,
    // XML declarations contain only ASCII characters, which are a subset
    // of many encodings (Shift_JIS, ISO-8859-*, EUC-*, ISO-2022-*, more).
    // 
    // It's got do this efficiently:  character I/O is solidly on the
    // critical path.  Synchronization isn't needed, and buffering must
    // as always be done only with care.  (So keep buffer length over
    // 2 Kbytes to avoid excess buffering, since most URL handlers stuff
    // a BufferedInputStream between here and the real data source.  That
    // class tries to be smart enough not to try buffering if you ask for
    // more data than it could buffer for you.)
    //
    private InputStream		raw;
    private byte		buffer [];
    private boolean		isASCII, isLatin1;
    private int			offset, length;

    // 2nd half of UTF-8 surrogate pair
    private char		nextChar;
    
    private int			switchover;
    private String		encodingAssigned;

    /**
     * Constructs the reader from a URLConnection. Uses the encoding 
     * specified in the HTTP header or autodetects
     * the encoding to use according to the heuristic specified
     * in the XML 1.0 recommendation.
     *
     * @param uc the URLConnection from which the reader is constructed
     * @exception IOException on error
     * @exception UnsupportedEncodingException when the input stream
     *  is not in an encoding which is supported; this is a fatal XML
     *  error.
     */
    public static Reader createReader (URLConnection uc) throws IOException
    {
	String encoding = getEncodingFromContentType(uc.getContentType());
	if (encoding == null) {
	    return createReader (uc.getInputStream());
	}
	return createReader (uc.getInputStream(), encoding);
    }

    /**
     * Gets the encoding from the content type string.
     * If there is a charset definition specified as a parameter
     * of the content type specification, it will be used when
     * loading input streams using the associated XmlReader.
     * For example if the type is specified as 
     * <code>text/xml; charset=EUC-JP</code> the Reader will
     * use the <code>EUC-JP</code> charset for translating
     * to unicode.
     * 
     * @param type the non-null mime type for the content editing
     *   support.
     */
    private static String getEncodingFromContentType(String type) {

	debug ("type=" + type);
	// The type could have optional info is part of it,
	// for example some charset info.  We need to strip that
	// of and save it.
	if (type == null) {
	    return null;
	}
	int parm = type.indexOf(";");
	if (parm > -1) {
	    // Save the paramList.
	    String paramList = type.substring(parm);
	    // update the content type string.
	    type = type.substring(0, parm).trim();
	    if (type.compareTo("text/xml") == 0) {
		// Set the charset name from the paramlist
		return getCharsetFromContentTypeParameters(paramList);
	    }
	}
	return null;
    }

    /**
     * This method get's the charset information specified as part
     * of the content type in the http header information.
     */
    private static String getCharsetFromContentTypeParameters(String paramlist) {
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
		return charset;
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
	return charset;
    }

    /**
     * Constructs the reader from an input stream, autodetecting
     * the encoding to use according to the heuristic specified
     * in the XML 1.0 recommendation.
     *
     * @param in the input stream from which the reader is constructed
     * @exception IOException on error
     * @exception UnsupportedEncodingException when the input stream
     *  is not in an encoding which is supported; this is a fatal XML
     *  error.
     */
    public static Reader createReader (InputStream in) throws IOException
    {
	return new XmlReader (in);
    }

    /**
     * Creates a reader supporting the given encoding, mapping
     * from standard encoding names to ones that understood by
     * Java where necessary.
     *
     * @param in the input stream from which the reader is constructed
     * @param encoding the IETF standard name of the encoding to use;
     *	if null, autodetection is used.
     * @exception IOException on error
     * @exception UnsupportedEncodingException when the input stream
     *  is not in an encoding which is supported; this is a fatal XML
     *  error.
     */
    public static Reader createReader (InputStream in, String encoding)
    throws IOException
    {
	if (encoding == null)
	    return new XmlReader (in);

	// UTF-16 == ISO-10646-UCS-2 plus surrogates.
	// The sun.io "Unicode" encoders/decoders don't check
	// for correctly paired surrogates, so they accept UTF-16.

	if ("UTF-16".equalsIgnoreCase (encoding)
		|| "ISO-106460-UCS-2".equalsIgnoreCase (encoding))
	    encoding = "Unicode";
	else if ("UTF-8".equalsIgnoreCase (encoding))
	    return new XmlReader (in, "UTF-8");
	else if ("EUC-JP".equalsIgnoreCase (encoding))
	    encoding = "EUCJIS";	// works on JDK 1.1 and 1.2
	else if (isAsciiName (encoding))
	    return new XmlReader (in, "US-ASCII");
	else if (isLatinName (encoding))
	    return new XmlReader (in, "ISO-8859-1");

	// XXX we should provide provide better "unsupported encoding"
	// diagnostics than this produces...
	return new InputStreamReader (in, encoding);
    }

    private XmlReader (InputStream in, String encoding)
    throws IOException
    {
	buffer = new byte [8 * 1024];
	length = 0;
	raw = in;
	if ("US-ASCII".equals (encoding))
	    setASCII ();
	else if ("ISO-8859-1".equals (encoding))
	    setLatin1 ();
	else if (!"UTF-8".equals (encoding))
	    throw new UnsupportedEncodingException (encoding);
	else
	    setUTF8 ();
    }

    private static boolean isAsciiName (String encoding)
    {
	return "US-ASCII".equalsIgnoreCase (encoding)
		|| "ASCII".equalsIgnoreCase (encoding);
    }

    private static boolean isLatinName (String encoding)
    {
	return "ISO-8859-1".equalsIgnoreCase (encoding)
		|| "Latin1".equalsIgnoreCase (encoding)
		|| "8859_1".equalsIgnoreCase (encoding);
    }

    private void setASCII ()
    {
	encodingAssigned = "US-ASCII";
	isASCII = true;
	isLatin1 = false;
	offset = 0;
    }

    private void setLatin1 ()
    {
	encodingAssigned = "ISO-8859-1";
	isASCII = false;
	isLatin1 = true;
	offset = 0;
    }

    private void setUTF8 ()
    {
	encodingAssigned = "UTF-8";
	isASCII = false;
	isLatin1 = false;
	offset = 0;
    }

    /** Returns the (non)standard name of the encoding in use */
    public String getEncoding ()
    {
	return encodingAssigned;
    }

    private XmlReader (InputStream in) throws IOException
    {
        int                 c;

	//
	// Set up buffering ... we buffer at least the XML text
	// declaration (if it's there), and for some encodings we
	// manage bulk character I/O.  We can reset within this
	// buffer so long as we've not assigned the encoding.
	//
        raw = in;
	switchover = -1;
	buffer = new byte [8 * 1024];
	offset = length = 0;
	isLatin1 = true;

        //
        // See if we can figure out the character encoding used
        // in this file by peeking at the first few bytes.  If not,
        // we may be able to look at the whole XML declaration.
        //
        switch ((c = read ())) {
            case 0:
              // 00 3c 00 3f == illegal UTF-16 big-endian
              if ((c = read ()) == 0x3c
                      && (c = read ()) == 0x00
                      && (c = read ()) == 0x3f) {
		  setSwitchover ("UnicodeBig");
		  // no BOM to ignore
                  return;
              }

              // 00 00 00 3c == UCS-4 big endian
              // 00 00 3c 00 == UCS-4 unusual "2143" order
              // 00 3c 00 00 == UCS-4 unusual "3412" order
              // ... or something else!  note that only some parts
	      // of UCS-4 work with UNICODE based systems.
	      throw new UnsupportedEncodingException ("UCS-4 (?)");

            case '<':      // 0x3c: the most common cases!
              switch ((c = read ())) {
                // First character is '<'; could be XML without
		// an XML directive such as "<hello>", "<!-- ...",
		// and so on.  Default intelligently; the byte we
		// just read could be _part_ of a UTF-8 character!
                default:
                  break;

                // 3c 00 3f 00 == illegal UTF-16 little endian
                // 3c 00 00 00 == UCS-4 little endian
                case 0x00:
                  if (read () == 0x3f && read () == 0x00) {
		      setSwitchover ("UnicodeLittle");
		      // no BOM to ignore
                      return;
                  }
                  throw new UnsupportedEncodingException ("UCS-4");

                // 3c 3f 78 6d == ASCII and supersets '<?xm'
                case '?': 
                  if (read () != 'x' || read () != 'm'
			  || read () != 'l' || read () != ' ')
		      break;
		  //
		  // One of several encodings could be used:
                  // Shift-JIS, ASCII, UTF-8, ISO-8859-*, etc
		  //
		  guessEncoding ();
                  return;
              }
	      break;

            // UTF-16 big-endian
            case 0xfe:
              if ((c = read ()) != 0xff)
                  break;
	      setSwitchover ("UnicodeBig");
	      offset = 2;	// skip BOM
              return;

            // UTF-16 little-endian
            case 0xff:
              if ((c = read ()) != 0xfe)
                  break;
	      setSwitchover ("UnicodeLittle");
	      offset = 2;	// skip BOM
              return;

            // EOF
            case -1:
	      return;

            // default ... no XML declaration
            default:
              break;
        }

	//
        // If all else fails, assume XML without a declaration, and
        // using UTF-8 encoding.  We must be prepared to re-interpret
	// bytes we've already read as parts of UTF-8 characters, so
	// we can't use the "switchover" technique (which works only
	// "between" characters).
	//
	setUTF8 ();
    }

    // When the buffered input is done, switch to a reader
    // that can decode data in that encoding.  Only call this
    // routine after entire characters have been scanned.
    private void setSwitchover (String encoding) throws IOException
    {
	switchover = offset;
	encodingAssigned = encoding;
	offset = 0;
    }

    // we've consumed our buffer, now switch over to a reader
    // which will decode the rest of the (non-ASCII) input
    private void doSwitchover () throws IOException
    {
	if (offset != switchover)
	    throw new InternalError ();
	in = new InputStreamReader (raw, encodingAssigned);
	buffer = null;
	switchover = -1;
    }


    /*
     * Used for ASCII supersets ... including the default UTF-8 encoding.
     */
    private void guessEncoding () throws IOException
    {
	//
	// We know that "<?xml " has been seen; so we'll skip any
	// 	S? version="..." 	[or single quotes]
	// bit and get the 
	// 	S? encoding="..." 	[or single quotes]
	// parts.  We place an arbitrary limit on the amount of text we
	// expect to find in the XML declarations; excessive whitespace
	// will cause this to guess "UTF-8".
	//
	int		c;
	StringBuffer	buf = new StringBuffer ();
	StringBuffer	keyBuf = null;
	String		key = null;
	boolean		sawEq = false;
	char		quoteChar = 0;
	boolean		sawQuestion = false;

    XmlDecl:
	for (int i = 0; i < 100; ++i) {
	    if ((c = read ()) == -1) {
		setASCII ();
		return;
	    }

	    // ignore whitespace before/between "key = 'value'"
	    if (Character.isWhitespace ((char) c))
		continue;
	    
	    // terminate the loop ASAP
	    if (c == '?')
		sawQuestion = true;
	    else if (sawQuestion) {
		if (c == '>')
		    break;
		sawQuestion = false;
	    }
	    
	    // did we get the "key =" bit yet?
	    if (key == null || !sawEq) {
		if (keyBuf == null) {
		    if (Character.isWhitespace ((char) c))
			continue;
		    keyBuf = buf;
		    buf.setLength (0);
		    buf.append ((char)c);
		    sawEq = false;
		} else if (Character.isWhitespace ((char) c)) {
		    key = keyBuf.toString ();
		} else if (c == '=') {
		    if (key == null)
			key = keyBuf.toString ();
		    sawEq = true;
		    keyBuf = null;
		    quoteChar = 0;
		} else
		    keyBuf.append ((char)c);
		continue;
	    }

	    // space before quoted value
	    if (Character.isWhitespace ((char) c))
		continue;
	    if (c == '"' || c == '\'') {
		if (quoteChar == 0) {
		    quoteChar = (char) c;
		    buf.setLength (0);
		    continue;
		} else if (c == quoteChar) {
		    if ("encoding".equals (key)) {
			String	encoding = buf.toString ();

			// [81] Encname ::= [A-Za-z] ([A-Za-z0-9._]|'-')*
			for (i = 0; i < encoding.length(); i++) {
			    c = encoding.charAt (i);
			    if ((c >= 'A' && c <= 'Z')
				    || (c >= 'a' && c <= 'z'))
				continue;
			    if (i > 0 && (c == '-'
				    || (c >= '0' && c <= '9')
				    || c == '.' || c == '_'))
				continue;
			    // map errors to UTF-8 default
			    break XmlDecl;
			}

			// we handle ASCII directly
			if (isAsciiName (encoding)) {
			    setASCII ();
			    return;
			}

			// ditto ISO 8859/1
			if (isLatinName (encoding)) {
			    setLatin1 ();
			    return;
			}

			// and UTF-8
			if ("UTF-8".equalsIgnoreCase (encoding)
				|| "UTF8".equalsIgnoreCase (encoding)
				)
			    break XmlDecl;

			// JDK uses nonstandard names internally
			if ("EUC-JP".equalsIgnoreCase (encoding))
			    encoding = "EUCJIS";

			// other encodings ... use a reader.
			setSwitchover (encoding);
			return;
		    } else {
			key = null;
			continue;
		    }
		}
	    }
	    buf.append ((char) c);
	}

	setUTF8 ();
    }

    /*
     * Converts a UTF-8 character from the input buffer, optionally
     * restricting to the US-ASCII subset of UTF-8.
     */
    private char	utf8char () throws IOException
    {
    	char		retval;
    	int		character;
    	
	// return second half of a surrogate pair
    	if (nextChar != 0) {
    	    retval = nextChar;
    	    nextChar = 0;
    	    return retval;
    	}
    	
    	//
    	// Excerpted from RFC 2279:
    	//
	// UCS-4 range (hex.)    UTF-8 octet sequence (binary)
	// 0000 0000-0000 007F   0xxxxxxx
	// 0000 0080-0000 07FF   110xxxxx 10xxxxxx
	// 0000 0800-0000 FFFF   1110xxxx 10xxxxxx 10xxxxxx
	// 0001 0000-001F FFFF   11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
	// 0020 0000-03FF FFFF   111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
	// 0400 0000-7FFF FFFF   1111110x 10xxxxxx ... 10xxxxxx
	//
	// The last two encodings (5 and 6 bytes per char) aren't allowed
	// in XML documents since the characters are out of range.
	//
	retval = (char) buffer [offset];
	if ((retval & 0x80) == 0x00) {			// 1 byte
	    offset++;
	    return retval;
	}
	if (isASCII)
	    throw new CharConversionException ("Not US-ASCII:  0x"
			+ Integer.toHexString (retval & 0xff));
	
	//
	// Multibyte sequences -- check offsets optimistically,
	// ditto the "10xx xxxx" format for non-initial bytes
	//
	int		off = offset;
	
	try {
	    if ((buffer [off] & 0x0E0) == 0x0C0) {		// 2 bytes
		character  = (buffer [off++] & 0x1f) << 6;
		character +=  buffer [off++] & 0x3f;
		retval = (char) character;
		character = 0;
	    } else if ((buffer [off] & 0x0F0) == 0x0E0) {	// 3 bytes
		character  = (buffer [off++] & 0x0f) << 12;
		character += (buffer [off++] & 0x3f) << 6;
		character +=  buffer [off++] & 0x3f;
		retval = (char) character;
		character = 0;
	    } else if ((buffer [off] & 0x0f8) == 0x0F0) {	// 4 bytes
		character  = (buffer [off++] & 0x07) << 18;
		character += (buffer [off++] & 0x3f) << 12;
		character += (buffer [off++] & 0x3f) << 6;
		character +=  buffer [off++] & 0x3f;
		// Convert UCS-4 char to a Unicode surrogate pair
		character -= 0x10000;
		retval = (char) (0xD800 + (character >> 10));
		character = 0xDC00 + (character & 0x03ff);
	    } else
		// XXX actually a WF error ...
		throw new CharConversionException ("Illegal XML character"
		    + " 0x" + Integer.toHexString (buffer [offset] & 0xff)
		);

	} catch (ArrayIndexOutOfBoundsException e) {
	    // that is, off > length && length >= buffer.length
	    retval = (char) 0;
	    character = 0;
	}

	//
	// if the buffer held only a partial character, compact it
	// and try to read the rest of the character.  worst case
	// involves three single-byte reads.
	//
	if (off > length) {
	    System.arraycopy (buffer, offset, buffer, 0, length - offset);
	    length -= offset;
	    offset = 0;
	    off = raw.read (buffer, length, buffer.length - length);
	    if (off < 0)
		throw new CharConversionException ("Partial UTF-8 char");
	    length += off;
	    return utf8char ();
	}

	//
	// check the format of the non-initial bytes, and return
	//
	for (offset++; offset < off; offset++)
	    if ((buffer [offset] & 0xC0) != 0x80)
		throw new CharConversionException ("Malformed UTF-8 char");
	nextChar = (char) character;
	return retval;
    }

    /**
     * Reads the number of characters read into the buffer, or -1 on EOF.
     */
    public int read (char buf [], int off, int len) throws IOException
    {
	int	i;

	if (closed)
	    return -1;
	if (switchover > 0 && offset == switchover)
	    doSwitchover ();
	if (in != null)
	    return in.read (buf, off, len);

	if (offset >= length) {
	    offset = 0;
	    length = raw.read (buffer, 0, buffer.length);
	}
	if (length <= 0)
	    return -1;
	if (encodingAssigned == null || isLatin1)
	    for (i = 0; i < len && offset < length; i++)
		buf [off++] = (char) (buffer [offset++] & 0xff);
	else
	    for (i = 0; i < len && offset < length; i++)
		buf [off++] = utf8char ();
	return i;
    }

    /**
     * Reads a single character.
     */
    public int read () throws IOException
    {
	if (closed)
	    return -1;
	if (switchover > 0 && offset == switchover)
	    doSwitchover ();
	if (in != null)
	    return in.read ();

	if (offset >= length) {
	    if (encodingAssigned == null) {
		// minimize readahead we might regret...
		if (length == buffer.length)
		    throw new InternalError ("too much peekahead");
		int len = raw.read (buffer, offset, 1);
		if (len <= 0)
		    return -1;
		length += len;
	    } else {
		offset = 0;
		length = raw.read (buffer, 0, buffer.length);
		if (length <= 0)
		    return -1;
	    }
	}

	if (isLatin1 || encodingAssigned == null)
	    return buffer [offset++] & 0x0ff;
	else
	    return utf8char ();
    }

    /**
     * Returns true iff the reader supports mark/reset.
     */
    public boolean markSupported ()
    {
	return in != null && in.markSupported ();
    }

    /**
     * Sets a mark allowing a limited number of characters to
     * be "peeked", by reading and then resetting.
     * @param value how many characters may be "peeked".
     */
    public void mark (int value) throws IOException
    {
	if (in != null)
	    in.mark (value);
    }

    /**
     * Resets the current position to the last marked position.
     */
    public void reset () throws IOException
    {
	if (in != null)
	    in.reset ();
    }

    /**
     * Skips a specified number of characters.
     */
    public long skip (long value) throws IOException
    {
	if (value < 0)
	    return 0;
	if (in != null)
	    return in.skip (value);
	long avail = length - offset;
	if (avail >= value) {
	    offset += (int) value;
	    return value;
	}
	offset += avail;
	return avail + raw.skip (value - avail);
    }

    /**
     * Returns true iff input characters are known to be ready.
     */
    public boolean ready () throws IOException
    {
	if (in != null)
	    return in.ready ();
	return (length > offset) || raw.available () != 0;
    }

    /**
     * Closes the reader.
     */
    public void close () throws IOException
    {
	if (closed)
	    return;
	if (in != null)
	    in.close ();
	else
	    raw.close ();
	closed = true;
    }

    /**
     * For printf debugging.
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("XmlReader: " + str);
        }
    }
}
