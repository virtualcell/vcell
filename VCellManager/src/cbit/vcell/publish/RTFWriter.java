package cbit.vcell.publish;
import java.io.FileOutputStream;

import com.lowagie.text.DocWriter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.rtf.RtfWriter;

/**
 * RTF implementation. 
 * Creation date: (4/18/2003 4:18:38 PM)
 * @author: John Wagner
 */
public class RTFWriter extends ITextWriter {

public RTFWriter() {
	super();
}


public DocWriter createDocWriter(FileOutputStream fileOutputStream) throws DocumentException {
	return RtfWriter.getInstance(this.document, fileOutputStream);
}


protected void writeHeaderFooter(String headerStr) throws DocumentException {

	if (headerStr.trim().length() > 0) {
		HeaderFooter headerFooter = new HeaderFooter(new Phrase(headerStr), false);
		document.setHeader(headerFooter);
	}
	HeaderFooter headerFooter = new HeaderFooter(new Phrase("Page "), true);
	document.setFooter(headerFooter);
}
}