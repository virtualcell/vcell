/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.publish;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;

/**
 * Note: Two incomplete methods for publishing an awt component were deleted (writeComponent(), writeSnapshot()).
 * Creation date: (4/18/2003 2:16:53 PM)
 * @author: John Wagner & Rashad Badrawi
 */

 public class PDFWriter extends ITextWriter {

public PDFWriter() {
	super();
}


public DocWriter createDocWriter(FileOutputStream fileOutputStream) throws DocumentException {
	
	return PdfWriter.getInstance(this.document, fileOutputStream);
}


protected void writeHeaderFooter(String headerStr) throws DocumentException {

	//did not work
	/*Paragraph headerParagraph = new Paragraph(headerStr);
	headerParagraph.add(" (produced by ");
	com.lowagie.text.Anchor vcLink = new com.lowagie.text.Anchor("The Virtual Cell");
	vcLink.setReference("http://www.nrcam.uchc.edu");
	headerParagraph.add(vcLink);
	headerParagraph.add(")");
	*/
	if (headerStr.trim().length() > 0) {
		HeaderFooter headerFooter = new HeaderFooter(new Phrase(headerStr), false);
		headerFooter.setBorder(Rectangle.BOTTOM);
		document.setHeader(headerFooter);
	}
	HeaderFooter headerFooter = new HeaderFooter(new Phrase("Page "), true);
	headerFooter.setBorder(Rectangle.TOP);
	document.setFooter(headerFooter);
}

}
