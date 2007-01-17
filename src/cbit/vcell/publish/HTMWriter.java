package cbit.vcell.publish;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.DocWriter;
import com.lowagie.text.html.HtmlWriter;

/**
 * HTML implementation. 
 * Creation date: (4/18/2003 4:18:38 PM)
 * @author: John Wagner
 */
public class HTMWriter extends ITextWriter {
public HTMWriter() {
	super();
}


public DocWriter createDocWriter(FileOutputStream fileOutputStream) throws DocumentException {
	return HtmlWriter.getInstance(this.document, fileOutputStream);
}
}