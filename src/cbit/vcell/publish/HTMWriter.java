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
import java.io.FileOutputStream;

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
