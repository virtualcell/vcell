package cbit.vcell.webparser;

import java.util.Hashtable;
import java.util.Vector;
import org.apache.regexp.RE;
/**
 * Insert the type's description here.
 * Creation date: (9/18/2002 2:42:51 PM)
 * @author: Jim Schaff
 */
public class WebSiteParser implements java.io.Serializable{

	private final org.w3c.tidy.Tidy tidy = new org.w3c.tidy.Tidy();
	private final org.apache.regexp.RECompiler reCompiler = new org.apache.regexp.RECompiler();
	//items for 1 value push-pop stack
	RE reStateRE = null;
	int reStateMatchFlags = -1;
	org.apache.regexp.REProgram reStateProgram = null;
	//
	private final org.apache.regexp.REProgram[] reProgram;

	private static final int LAST_MATCH_START	= 0;
	private static final int LAST_MATCH_END		= 1;
	
	//REProgram Text that can be pre-compiled
	private static final String[] reProgramText	=
	{
		"\\n",											// 0 newline, used to split text at line boundaries
		"[,\\s]+",										// 1 any comma or space, used to sep items in list
		"KM Value:\\D*",								// 2 BRENDA_enzyme KM Data block
		"References:\\D*",								// 3 BRENDA_enzyme References Data block
		"^\\s*([,\\d]+).*?<([\\d\\s,]+)>\\s*$",			// 4 BRENDA_enzyme KM Values and References
		"list_uids=(\\d+)",								// 5 BRENDA_Enzyme PubMed id from PubMed href
		"^([^,])",										// 6 BRENDA_Enzyme title from citation
		"^\\s*[\\d]+\\s+(.*?)\\D*$",					// 7 BRENDA_Enzyme citation from reference
		"^.*(PubMed)$",									// 8 BRENDA_Enzyme PubMed from reference
		"EC Number:\\D*([\\d\\.]+)"+					// 9 BRENDA_Enzyme EC Number,Name,CASRN
			".*?Recommended Name:\\s*(.*?)\\s*\\n+"+
			".*?CAS Registry Number:\\D*([\\d\\-]+)\\s*\\n",
		"ENTRY\\s*(R\\d+).*?\\n\\S",					//10 KEGG_REACTION id
		"NAME\\s*(.+?)\\n\\S",							//11 KEGG_REACTION name
		"DEFINITION\\s*(.+?)\\n\\S",					//12 KEGG_REACTION definition text
		"EQUATION\\s*(.+?)\\n\\S",						//13 KEGG_REACTION equation text
		"PATHWAY\\s*(.+?)\\n\\S",						//14 KEGG_REACTION Pathway ID
		"ENZYME\\s*(\\S+).*?\\n\\S",					//15 KEGG_REACTION enzyme EC#
		"\\s{2,}",										//16 trim spaces for RE.subst
		"[+\\s]+",										//17 any space or +(plus), used to sep equation parts
		"KM Value\\[mM\\]/Substrate/Organism\\D*",		//18 BRENDA_KMDATA Data Block Location
		"^\\s*([\\d\\.]+)\\s*,\\s*(.*?),(.*?)\\s*$",	//19 BRENDA_KMDATA (km,substrate,organism)
		"EC Number\\D*([\\d\\.]+)",						//20 BRENDA_KMDATA EC number
		"\\s*\\n\\s*",									//21 leading trailing space around newlines
		"(^.*?$\\n?)",									//22 Whole row(line delimited by newline) include newline
		"(^\\s*[\\d]+\\s+.*?$\\n?)"						//23 BRENDA_ENZYME reference row
	};

	//Indexes for REProgams that are precompiled
	private static final int NEWLINE					= 0;
	private static final int COMMA_SPACE_SEPARATOR		= 1;
	private static final int BRENDA_ENZYME_KM			= 2;
	private static final int BRENDA_ENZYME_REFS			= 3;
	private static final int BRENDA_ENZYME_KMVAL_REF	= 4;
	private static final int BRENDA_ENZYME_PUBMEDID		= 5;
	private static final int BRENDA_ENZYME_TITLE		= 6;
	private static final int BRENDA_ENZYME_CITATION		= 7;
	private static final int BRENDA_ENZYME_PUBMED		= 8;
	private static final int BRENDA_ENZYME_BASIC		= 9;
	private static final int KEGG_REACTION_ENTRY		= 10;
	private static final int KEGG_REACTION_NAME			= 11;
	private static final int KEGG_REACTION_DEFINITION	= 12;
	private static final int KEGG_REACTION_EQUATION		= 13;
	private static final int KEGG_REACTION_PATHWAY		= 14;
	private static final int KEGG_REACTION_ENZYME		= 15;
	private static final int TRIM_SPACE					= 16;
	private static final int EQUATION_SEPARATOR			= 17;
	private static final int BRENDA_KMDATA_BLOCK		= 18;
	private static final int BRENDA_KMDATA_VALSUBORG	= 19;
	private static final int BRENDA_KMDATA_ECNUMBER		= 20;
	private static final int TRIM_AROUND_NEWLINE		= 21;
	private static final int WHOLE_ROW					= 22;
	private static final int BRENDA_ENZYME_REF_ROW		= 23;

	
//EMP Enzyme list by EC number
//http://wit.mcs.anl.gov/EMP/emp_by_ec.cgi?ec=1.7.7.1
//EMP Enzyme entries by EMP id
//http://wit.mcs.anl.gov/EMP/emp_by_id.cgi?id=LN90189-05

//Brenda result by EC Number (web format)
//http://brenda.bc.uni-koeln.de/php/result_flat.php3?ecno=1.7.7.1
//Brenda result by EC Number (Preferred format for parsing)
//http://brenda.bc.uni-koeln.de/flatfiles_neu/flats/1.7.7.1.php4


	
	//Sites
	public static final int SITE_ID_KEGG	= 0;
	public static final int SITE_ID_EMP		= 1;
	public static final int SITE_ID_BRENDA	= 2;
	public static final int SITE_ID_DOQCS	= 3;
	
	//Elements
	public static final int SITE_TYPE_ENZYME	= 0;
	public static final int SITE_TYPE_REACTION	= 1;
	public static final int SITE_TYPE_COMPOUND	= 2;
	public static final int SITE_TYPE_KMDATA	= 3;
	public static final int SITE_TYPE_KINETICS	= 4;
	
	//Site specific data
	//KEGG
	//private static final String KEGG_COMPOUND_ELEMENT_ENTRY		=	"ENTRY";
	//private static final String KEGG_COMPOUND_ELEMENT_NAME		=	"NAME";
	//private static final String KEGG_COMPOUND_ELEMENT_FORMULA		=	"FORMULA";
	
	//private static final String KEGG_REACTION_ELEMENT_ENTRY			=	"ENTRY";
	//private static final String KEGG_REACTION_ELEMENT_NAME			=	"NAME";
	//private static final String KEGG_REACTION_ELEMENT_EQUATION		=	"EQUATION";
	//private static final String KEGG_REACTION_ELEMENT_ENZYME		=	"ENZYME";
	//private static final String KEGG_REACTION_ELEMENT_DEFINITION	=	"DEFINITION";
	//private static final String[] KEGG_REACTION_ELEMENTS = {
	//				KEGG_REACTION_ELEMENT_ENTRY,
	//				KEGG_REACTION_ELEMENT_NAME,
	//				KEGG_REACTION_ELEMENT_EQUATION,
	//				KEGG_REACTION_ELEMENT_ENZYME,
	//				KEGG_REACTION_ELEMENT_DEFINITION
	//				};

	//EMP
	//Other
	
	//Indexes for "table" based parsing
	//private static final int ROW_INDEX		= 0;
	//private static final int COLUMN_INDEX	= 1;
	//private static final int CHARINDEX_INDEX= 2;

	//XML parse constants
	//public static final String XML_PARSE_SOURCE_BRENDA		=	"BRENDA";
	
	////XML parse tags
	////public static final String XML_PARSE_ATTR_ID		=	"id";			//common
	//public static final String XML_PARSE_ATTR_SOURCE	=	"source";		//common (e.g. KEGG,BRENDA,EMP)
	////public static final String XML_PARSE_ATTR_PUBMEDID=	"pubmedid";	//common (PubMed ID)
	////public static final String XML_PARSE_ATTR_CASRN	=	"casrn";	//CAS registry Number
	//public static final String XML_PARSE_ATTR_NAME		=	"name";			//common
	//public static final String XML_PARSE_ATTR_STOICH	=	"stoich";		//reaction_part
	//public static final String XML_PARSE_ATTR_DEFINITION=	"definition";	//reaction
	//public static final String XML_PARSE_ATTR_VALUE		=	"value";		//common
	//public static final String XML_PARSE_ATTR_ORGANISM	=	"organism";		//common
	//public static final String XML_PARSE_ATTR_SUBSTRATE	=	"substrate";	//km
	//public static final String XML_PARSE_ATTR_REACTANT	=	"reactant";		//reaction_part
	//public static final String XML_PARSE_ATTR_PRODUCT	=	"product";		//reaction_part

	//public static final String XML_PARSE_ATTR_TYPE	=		"type";			//id
	////ID Type index
	//private static final String[] idTypes = {"EC","KEGG","CASRN","PUBMED"};
	//public static final int XML_ID_TYPE_EC 		= 0;
	//public static final int XML_ID_TYPE_KEGG 	= 1;
	//public static final int XML_ID_TYPE_CASRN 	= 2;
	//public static final int XML_ID_TYPE_PUBMED 	= 3;
	////reactionPart type
	//private static final String[] reactionPartTypes = {"reactant","product"};
	//public static final int XML_REACTIONPART_TYPE_REACTANT 	= 0;
	//public static final int XML_REACTIONPART_TYPE_PRODUCT	= 1;
	
	//public static final String XML_PARSE_TYPE_REACTION		=	"Reaction";
	//public static final String XML_PARSE_TYPE_REACTION_PART	=	"ReactionPart";
	//public static final String XML_PARSE_TYPE_ENZYME	=	"Enzyme";
	////public static final String XML_PARSE_TYPE_SPECIES	=	"Species";
	//public static final String XML_PARSE_TYPE_KM		=	"Km";
	//public static final String XML_PARSE_TYPE_CITATION	=	"Citation";
	//public static final String XML_PARSE_TYPE_ID		=	"Id";
	//public static final String XML_PARSE_TYPE_COMPOUND	=	"Compound";


	public class ElementToTextIndex implements java.io.Serializable{
		private final Hashtable textAndElement = new Hashtable();

		public org.w3c.dom.Element getElement(int textIndex){
			java.util.Enumeration indexes = textAndElement.keys();
			while(indexes.hasMoreElements()){
				int[] indexBE = (int[])indexes.nextElement();
				if(textIndex >= indexBE[0] && textIndex <= indexBE[1]){
					return (org.w3c.dom.Element)textAndElement.get(indexBE);
				}
			}
			return null;
		}
		public void putElement(int textIndexBegin,int textIndexEnd,org.w3c.dom.Element element){
			int[] textIndex = new int[2];
			textIndex[0] = textIndexBegin;
			textIndex[1] = textIndexEnd;
			textAndElement.put(textIndex,element);
		}
		public String getAttributeValue(int textIndex,String attributeName){
			org.w3c.dom.Element element = getElement(textIndex);
			if(element != null){
				return element.getAttribute(attributeName);
			}
			return null;
		}
	}

	public class TextOffsetComparator implements java.util.Comparator,java.io.Serializable{
		
		public int compare(Object o1, Object o2){
			if(o1 instanceof int[] && o2 instanceof int[]){
				int[] to1 = (int[])o1;
				int[] to2 = (int[])o2;
				if(to1[0] <= to1[1] && to2[0] <= to2[1]){
					if(to1[0] > to2[1]){
						return 1;
					}else if(to2[0] > to1[1]){
						return -1;
					}else if(to1[0] == to2[0] && to1[1] == to2[1]){
						return 0;
					}
				}
			}
			throw new Error("TextOffset Illegal");
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 4:30:06 PM)
 */
public WebSiteParser() {
	
	reProgram = new org.apache.regexp.REProgram[reProgramText.length];
	try{
		for(int i = 0;i < reProgramText.length;i+= 1){
			reProgram[i] = reCompiler.compile(reProgramText[i]);
		}
	}catch(org.apache.regexp.RESyntaxException e){
		throw new Error("Error Initializing "+this.getClass().getName()+": "+e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2002 7:50:14 PM)
 * @return java.lang.String
 * @param nodeType short
 */
static String domNodeDescFromType(short nodeType) {

	String desc = null;
	switch(nodeType){
		case org.w3c.dom.Node.ELEMENT_NODE:{
			desc = "ELEMENT_NODE";
			break;
		}
		case org.w3c.dom.Node.ATTRIBUTE_NODE:{
			desc = "ATTRIBUTE_NODE";
			break;
		}
		case org.w3c.dom.Node.TEXT_NODE:{
			desc = "TEXT_NODE";
			break;
		}
		case org.w3c.dom.Node.CDATA_SECTION_NODE:{
			desc = "CDATA_SECTION_NODE";
			break;
		}
		case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:{
			desc = "ENTITY_REFERENCE_NODE";
			break;
		}
		case org.w3c.dom.Node.ENTITY_NODE:{
			desc = "ENTITY_NODE";
			break;
		}
		case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:{
			desc = "PROCESSING_INSTRUCTION_NODE";
			break;
		}
		case org.w3c.dom.Node.COMMENT_NODE:{
			desc = "COMMENT_NODE";
			break;
		}
		case org.w3c.dom.Node.DOCUMENT_NODE:{
			desc = "DOCUMENT_NODE";
			break;
		}
		case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:{
			desc = "DOCUMENT_TYPE_NODE";
			break;
		}
		case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE:{
			desc = "DOCUMENT_FRAGMENT_NODE";
			break;
		}
		case org.w3c.dom.Node.NOTATION_NODE:{
			desc = "NOTATION_NODE";
			break;
		}
		default:{
			desc = "Unknown";
			break;
		}
	}
	return desc;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 4:26:02 PM)
 * @return org.jdom.Document
 * @param siteType int
 * @param elementType int
 * @param is java.io.InputStream
 */
public ParsedElement getParsedElement(int siteID, int siteType, java.io.InputStream is,java.net.URL siteURL) throws java.io.IOException,java.text.ParseException{

	//Fix adjacent lt and gt (confuses tidy)
	java.io.BufferedInputStream bis = new java.io.BufferedInputStream(is);
	StringBuffer sb = new StringBuffer();
	int iChar = -1;
	boolean wasLastGT = false;
	while((iChar = bis.read()) != -1){
		if(iChar == '>'){
			if(wasLastGT){
				sb.append("&gt;");
			}else{
				sb.append('>');
			}
			wasLastGT = true;
		}else{
			if(iChar == '<'){
				bis.mark(1);
				int nextChar = bis.read();
				if(nextChar == iChar){
					sb.append("&lt;");
				}else{
					sb.append('<');
				}
				bis.reset();
			}else{
				sb.append((char)iChar);
			}
			wasLastGT = false;
		}
	}

	/*
	String s = sb.toString();
	int begin = 0;
	int end = s.indexOf('\n');
	int count = 0;
	do{
		String nextS = s.substring(begin,end);
		System.out.println(count+" "+nextS);
		count+= 1;
		begin = end+1;
		end = s.indexOf('\n',begin);
		if(end == -1){
			System.out.println(s.substring(begin));
			break;
		}
	}while(true);
	*/
	java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(sb.toString().getBytes());
	
	org.w3c.dom.Document tidySiteDocument = null;
	tidySiteDocument = tidy.parseDOM(bais,null);
	is.close();
	return getParsedElement(siteID,siteType,tidySiteDocument,siteURL);
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2002 12:32:52 PM)
 * @return org.jdom.Document
 * @param siteID int
 * @param siteURL java.net.URL
 * @param siteContent java.lang.String
 */
public ParsedElement getParsedElement(int siteID, int siteType, String siteContent,java.net.URL siteURL) throws java.io.IOException,java.text.ParseException{

	return getParsedElement(siteID,siteType,new java.io.ByteArrayInputStream(siteContent.getBytes()),siteURL);
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2002 12:32:52 PM)
 * @return org.jdom.Document
 * @param siteID int
 * @param siteURL java.net.URL
 * @param siteContent java.lang.String
 */
public ParsedElement getParsedElement(int siteID, int siteType,java.net.URL siteURL) throws java.io.IOException,java.text.ParseException{

	return getParsedElement(siteID,siteType,siteURL.openStream(),siteURL);
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 4:48:25 PM)
 * @return org.jdom.Document
 * @param siteID int
 * @param siteType int
 * @param doc org.w3c.dom.Document
 */
private ParsedElement getParsedElement(int siteID, int siteType, org.w3c.dom.Document tidySiteDocument,java.net.URL siteURL) throws java.text.ParseException{

	ParsedElement result = null;
	String contentString = null;
	ElementToTextIndex etti = new ElementToTextIndex();

try{

	switch(siteID){
		
		//KEGG
		case SITE_ID_KEGG:{
			org.w3c.dom.Node pre_Node = tidySiteDocument.getElementsByTagName("pre").item(0);
			//contentString = getTextFromContent(pre_Node,etti);
			contentString = getTextFromContent2(pre_Node,etti,0);
			switch(siteType){
				//case SITE_TYPE_ENZYME:{
					//result = parse_KEGG_Scan(pre_Node,siteType);
					//break;
				//}
				case SITE_TYPE_REACTION:{
					result = parse_KEGG_Reaction(contentString);
					break;
				}
				//case SITE_TYPE_COMPOUND:{
					//result = parse_KEGG_Scan(pre_Node,siteType);
				//	break;
				//}
			}
			break;
		}
		
		case SITE_ID_DOQCS:{
			org.w3c.dom.Document doc =  org.w3c.tidy.Tidy.createEmptyDocument();
			org.w3c.dom.Node singleTables = doc.createElement("SingleTables");
			org.w3c.dom.NodeList nl = tidySiteDocument.getElementsByTagName("*");
			for(int i = 0;i < nl.getLength();i+= 1){
				org.w3c.dom.Element currentElement = (org.w3c.dom.Element)nl.item(i);
				if(currentElement.getNodeName().equalsIgnoreCase("H2")){
					singleTables.appendChild(currentElement.cloneNode(true));
				}
				if(currentElement.getNodeName().equalsIgnoreCase("table")){
					org.w3c.dom.NodeList currentNL = currentElement.getElementsByTagName("table");
					if(currentNL.getLength() == 1){
							singleTables.appendChild(currentElement.cloneNode(true));
					}
				}
			}
			contentString = getTextFromContent2(singleTables,etti,0);

			switch(siteType){
				case SITE_TYPE_KINETICS:{
					result = parse_DOQCS_Kinetics(contentString);
					break;
				}
			}
			break;
		}
		/*//EMP
		case SITE_ID_EMP:{
			org.w3c.dom.Node pre_Node = tidySiteDocument.getElementsByTagName("table").item(0);
			switch(siteType){
				case SITE_TYPE_ENZYME:{
					result = parse_EMP(pre_Node,siteType);
					break;
				}
			}
			break;
		}
		*/

		//BRENDA
		case SITE_ID_BRENDA:{
			org.w3c.dom.Node pre_Node = tidySiteDocument.getElementsByTagName("body").item(0);
			//contentString = getTextFromContent(pre_Node,etti);
			contentString = getTextFromContent2(pre_Node,etti,0);
			switch(siteType){
				case SITE_TYPE_ENZYME:{
					result = parse_BRENDA_Enzyme(contentString,etti,siteURL);
					break;
				}
				case SITE_TYPE_KMDATA:{
					result = parse_BRENDA_KMData(contentString,etti);
					//result = parse_BRENDA(pre_Node,siteType);
					break;
				}
			}
			break;
		}



		default:{
			break;
		}
		
	}
	

}catch(org.apache.regexp.RESyntaxException e){
	e.printStackTrace(System.out);
}//catch(java.text.ParseException e){
//	e.printStackTrace(System.out);
//}
	return result;

}
/**
 * Insert the method's description here.
 * Creation date: (10/11/2002 5:44:28 PM)
 * @return java.lang.String[]
 * @param rowBegin org.apache.regexp.REProgram
 * @param rowIdentify org.apache.regexp.REProgram
 */
private java.util.TreeMap getRowsOfData(org.apache.regexp.RE regexp, String content,int rowBeginProgramREPI, int rowIdentifyProgramREPI,boolean bContiguous) {

	//
	// Find row based data separated by newlines.
	// Beginning of rows is located by match of reProgram[rowBeginProgramREPI].
	// Rows are selected for inclusion if matched by reProgram[rowIdentifyProgramREPI]
	// Offsets within content saved with each matched row for use in possible etti lookups
	//
	
	//if(!reProgramText[rowBeginProgramREPI].startsWith("(") || !reProgramText[rowBeginProgramREPI].endsWith(")")){
	//	throw new Error(this.getClass().getName()+".getRowsOfData Error: rowBegin REProgram must be contained completely in subexpression ()");
	//}
	
	java.util.TreeMap result = null;
		
	pushRE(regexp);
	regexp.setMatchFlags(RE.MATCH_SINGLELINE | RE.MATCH_NORMAL);
	regexp.setProgram(reProgram[rowBeginProgramREPI]);
	if(regexp.match(content)){
		//if(regexp.getParenCount() != 2){//getParen(0) is always the match itself, getParen(1) is the subexpression
		//	throw new Error(this.getClass().getName()+".getRowsOfData Error: rowBegin match should have only 1 subexpression");			
		//}
		//int offsetToRowStart = regexp.getParenEnd(1);
		int offsetToRowStart = regexp.getParenEnd(0);
		String dataStartingWithRow = content.substring(offsetToRowStart);
		int rowOffset = 0;
		regexp.setMatchFlags(RE.MATCH_MULTILINE | RE.MATCH_NORMAL);
		do{
			int[] position = null;
			String row = null;
			//try{
				regexp.setProgram(reProgram[WHOLE_ROW]);
				if(regexp.match(dataStartingWithRow,rowOffset)){
					if(regexp.getParenStart(1) == rowOffset){
						row = regexp.getParen(1);
						regexp.setProgram(reProgram[rowIdentifyProgramREPI]);
						//if(regexp.match(regexp.getParen(1))){
						if(regexp.match(row)){
							if(result == null){
								result = new java.util.TreeMap(new TextOffsetComparator());
							}
							position = new int[2];
							position[0] = offsetToRowStart+rowOffset;
							position[1] = position[0]+row.length()-1;
							result.put(position,row);
							rowOffset+= row.length();
						}else{
							if(bContiguous){
								break;
							}
						}
					}else{
						throw new Error(this.getClass().getName()+".getRows(...) Expected 0 offset to new line");
					}
				}else{
					break;
				}
		}while(true);
	}
	
	popRE(regexp);

	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/2002 9:54:45 AM)
 * @return java.lang.String
 * @param siteNdoe org.w3c.dom.Node
 */
private String getTextFromContent2(org.w3c.dom.Node siteNode,ElementToTextIndex etti,int currentTextLength) {

	String result = null;
	if(siteNode.hasChildNodes()){
		org.w3c.dom.Node childNode = siteNode.getFirstChild();
		StringBuffer sb = new StringBuffer();
		
		do{
			sb.append(getTextFromContent2(childNode,etti,currentTextLength+sb.length()));
		}while((childNode = childNode.getNextSibling()) != null);

		result = sb.toString();
		
		if(siteNode.getNodeName().equalsIgnoreCase("td")){
			while(result.endsWith("\n")){
				result = result.substring(0,result.length()-1);
			}
			result = result.replace('\n',',');
			if(result.length() < 20){
				result = org.vcell.util.BeanUtils.forceStringSize(result,20," ",true);
			}else{
				result = " "+result+" ";
			}
		}	
		else if(siteNode.getNodeName().equalsIgnoreCase("tr") || siteNode.getNodeName().equalsIgnoreCase("h2")){
			result = result+"\n";
		}
		
	}else{
		if(siteNode.getNodeName().equals("br")){
			result = "\n";
		}else{
			result = siteNode.getNodeValue();
		}
		
	}
	if(siteNode.getNodeName().equals("a")){//Save anchors
		etti.putElement(currentTextLength,currentTextLength+result.length(),(org.w3c.dom.Element)siteNode);
	}
	
	// Note:
	// 0x00A0 = 160 is "no-break space" (&nbsp;)
	// Character.isWhitespace('\u00A0') = false
	// change all '\u00A0' to '\u0020' (regular space)
	
	if(result != null){
		result = result.replace('\u00A0',' ');
	}
	return result;

}
/**
 * Insert the method's description here.
 * Creation date: (9/26/2002 3:38:53 PM)
 * @return java.lang.String
 * @param ecNumber java.lang.String
 */
static String getURL_BRENDA_ECNumber(String ecNumber) {
	return "http://brenda.bc.uni-koeln.de/flatfiles_neu/flats/"+ecNumber+".php4";
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 5:57:43 PM)
 * @return java.lang.String
 * @param content java.lang.String
 * @param repIndex int
 */
private boolean match(org.apache.regexp.RE regexp,int reMatchFlags,String content, int REPIndex) {
	pushRE(regexp);
	regexp.setProgram(reProgram[REPIndex]);
	regexp.setMatchFlags(reMatchFlags);
	boolean result = regexp.match(content);
	popRE(regexp);
	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/2002 9:42:31 AM)
 * @return org.jdom.Document
 * @param content java.lang.String
 */
private Enzyme parse_BRENDA_Enzyme(String content,ElementToTextIndex etti,java.net.URL url) throws java.text.ParseException,org.apache.regexp.RESyntaxException{

	org.apache.regexp.RE regexp = new org.apache.regexp.RE();

	//org.jdom.Element xmlEnzyme = new org.jdom.Element(XML_PARSE_TYPE_ENZYME);

	String enzName = null;
	Id ecNumber = null;
	Vector idV = new Vector();
	Vector citV = new Vector();
	Vector kmV = new Vector();
	
	regexp.setMatchFlags(RE.MATCH_SINGLELINE);
	regexp.setProgram(reProgram[BRENDA_ENZYME_BASIC]);//Find EC,Name,CAS
	if(regexp.match(content) && regexp.getParenCount() == 4){
		enzName = regexp.getParen(2);
		ecNumber = new cbit.vcell.webparser.Id(Id.XML_ID_TYPE_EC,regexp.getParen(1));
		idV.add(ecNumber);
		idV.add(new cbit.vcell.webparser.Id(Id.XML_ID_TYPE_CASRN,regexp.getParen(3)));
	}else{
		throw new java.text.ParseException("Error parsing BRENDA Enzyme: KM (EC,Name,CAS) Not found",0);
	}

	java.util.TreeMap kmDataBlock = getRowsOfData(regexp,content,BRENDA_ENZYME_KM,BRENDA_ENZYME_KMVAL_REF,true);
	if(kmDataBlock == null){
		throw new java.text.ParseException("Error parsing BRENDA Enzyme: KM DataBlock Not found",0);
	}

	java.util.TreeMap refsBlock = getRowsOfData(regexp,content,BRENDA_ENZYME_REFS,BRENDA_ENZYME_REF_ROW,true);
	if(refsBlock == null){
		throw new java.text.ParseException("Error parsing BRENDA Enzyme: References DataBlock Not found",0);
	}


	java.util.TreeSet uniqueKmRefs = new java.util.TreeSet();
	regexp.setMatchFlags(RE.MATCH_MULTILINE);
	java.util.Iterator kmDataRows = kmDataBlock.values().iterator();
	while(kmDataRows.hasNext()){
		String kmDataRow = (String)kmDataRows.next();
		regexp.setProgram(reProgram[BRENDA_ENZYME_KMVAL_REF]);
		if(regexp.match(kmDataRow) && regexp.getParenCount() == 3){//Parse value and reference indexes
			//String kmValue = regexp.getParen(1).replace(',','.');//Value (fix non-US number style)
			String kmRawRef = regexp.getParen(2);//Reference indexes
			regexp.setProgram(reProgram[COMMA_SPACE_SEPARATOR]);
			String[] kmRefs = regexp.split(kmRawRef);//Separate each reference Index
			for(int j = 0;j<kmRefs.length;j+= 1){
				uniqueKmRefs.add(kmRefs[j].trim());
			}
			
		}
	}






	
	java.util.Iterator uniqueKmRefsIter = uniqueKmRefs.iterator();
	while(uniqueKmRefsIter.hasNext()){
		String refNum = (String)uniqueKmRefsIter.next();
		String reference = null;
		int[] referenceOffset = null;
		java.util.Iterator refsRows = refsBlock.entrySet().iterator();
		while(refsRows.hasNext()){
			java.util.Map.Entry refsRowTemp = (java.util.Map.Entry)refsRows.next();
			if(((String)refsRowTemp.getValue()).startsWith(refNum)){
				reference = (String)refsRowTemp.getValue();
				referenceOffset = (int[])refsRowTemp.getKey();
				break;
			}
		}
		if(reference != null && referenceOffset != null){
			//------------------------------------------------------------------
				PEContainer kmContainer = null;
				String citation = null;
				regexp.setProgram(reProgram[BRENDA_ENZYME_CITATION]);//Find Citation
				if(regexp.match(reference)){
					citation = regexp.getParen(1);
					String organismSubstrateLink = etti.getAttributeValue(referenceOffset[0]+regexp.getParenStart(1),"href");
					if(organismSubstrateLink != null){
						try{
							kmContainer = (PEContainer)getParsedElement(SITE_ID_BRENDA,SITE_TYPE_KMDATA,new java.net.URL(url,organismSubstrateLink));
						}catch(Exception e){
							throw new java.text.ParseException("Error parsing BRENDA Enzyme: "+e.getMessage(),0);
						}
					}else{
						throw new java.text.ParseException("Error parsing BRENDA Enzyme: No Link to Organism and Substrate Found",0);
					}
				}else{
					throw new java.text.ParseException("Error parsing BRENDA Enzyme: No CITATION Found for Reference="+reference,0);
				}
				regexp.setProgram(reProgram[BRENDA_ENZYME_PUBMED]);//Find PubMed
				String pubmedID = null;
				if(regexp.match(reference)){
					String pubmed_href = etti.getAttributeValue(referenceOffset[0]+regexp.getParenStart(1),"href");
					regexp.setProgram(reProgram[BRENDA_ENZYME_PUBMEDID]);//find pubmedID from href
					if(pubmed_href != null && regexp.match(pubmed_href) && regexp.getParenCount() == 2){
						pubmedID = regexp.getParen(1);
					}else{
						throw new java.text.ParseException("Error parsing BRENDA Enzyme: Found PubMed but no ID",0);
					}
				}

				//Add citation to Enzyme
				Id pmid = (pubmedID != null?new Id(Id.XML_ID_TYPE_PUBMED,pubmedID):null);
				Citation cit = new Citation(citation,pmid);
				citV.add(cit);
				//Add citation id to each kmValue
				for(int i = 0; i < kmContainer.size();i+= 1){
					Param p = (Param)kmContainer.get(i);
					p = new Param(p,(Vector)null);
					//if(pmid != null){
					//	p = new Param(p,pmid);
					//}
					kmV.add(p);
				}
//Km kmObject = new Km(ecNumber,kmValue,null,null,
//new Citation(citation,(pubmedID != null?new Id(regexp.getParen(1),Id.XML_ID_TYPE_PUBMED):null)));
//Km mergedKm = kmObject.merge(referenceKm);
//if(mergedKm == null){
//throw new RuntimeException("Km merged null");
//}
//kmV.add(mergedKm);


			//------------------------------------------------------------------
		}else{
			throw new java.text.ParseException("Error parsing BRENDA Enzyme: No REFERENCE Found for Index="+refNum,0);
		}
	}

	return new Enzyme(enzName,idV,citV,kmV);
/*
	regexp.setMatchFlags(RE.MATCH_MULTILINE);
	java.util.Iterator kmDataRows = kmDataBlock.values().iterator();
	while(kmDataRows.hasNext()){
		String kmDataRow = (String)kmDataRows.next();
		regexp.setProgram(reProgram[BRENDA_ENZYME_KMVAL_REF]);
		if(regexp.match(kmDataRow) && regexp.getParenCount() == 3){//Parse value and reference indexes
			String kmValue = regexp.getParen(1).replace(',','.');//Value (fix non-US number style)
			//org.jdom.Element xmlKM = new org.jdom.Element(XML_PARSE_TYPE_KM);//Start new KM Value
			//xmlKM.addAttribute(XML_PARSE_ATTR_VALUE,kmValue);//Set Km Value
			//xmlEnzyme.addContent(xmlKM);
			String kmRawRef = regexp.getParen(2);//Reference indexes
//System.out.println(kmValue+"  "+kmRawRef);
			regexp.setProgram(reProgram[COMMA_SPACE_SEPARATOR]);
			String[] kmRefs = regexp.split(kmRawRef);//Separate each reference Index
			for(int j = 0;j<kmRefs.length;j+= 1){
				//org.jdom.Element xmlCitation = new org.jdom.Element(XML_PARSE_TYPE_CITATION);
				String reference = null;
				int[] referenceOffset = null;
				java.util.Iterator refsRows = refsBlock.entrySet().iterator();
				while(refsRows.hasNext()){
					java.util.Map.Entry refsRowTemp = (java.util.Map.Entry)refsRows.next();
					if(((String)refsRowTemp.getValue()).startsWith(kmRefs[j]+"")){
						reference = (String)refsRowTemp.getValue();
						referenceOffset = (int[])refsRowTemp.getKey();
						break;
					}
				}
				if(reference != null && referenceOffset != null){
					PEContainer referenceKm = null;
					String citation = null;
					regexp.setProgram(reProgram[BRENDA_ENZYME_CITATION]);//Find Citation
					if(regexp.match(reference)){
						citation = regexp.getParen(1);
						//xmlCitation.addAttribute(XML_PARSE_ATTR_VALUE,regexp.getParen(1));
						String organismSubstrateLink = etti.getAttributeValue(referenceOffset[0]+regexp.getParenStart(1),"href");
						if(organismSubstrateLink != null){
							//xmlCitation.addAttribute(XML_PARSE_ATTR_SOURCE,organismSubstrateLink);
							try{
								referenceKm = (PEContainer)getParsedElement(SITE_ID_BRENDA,SITE_TYPE_KMDATA,new java.net.URL(url,organismSubstrateLink));
							}catch(Exception e){
								throw new java.text.ParseException("Error parsing BRENDA Enzyme: "+e.getMessage(),0);
							}
						}else{
							throw new java.text.ParseException("Error parsing BRENDA Enzyme: No Link to Organism and Substrate Found",0);
						}
					}else{
						throw new java.text.ParseException("Error parsing BRENDA Enzyme: No CITATION Found for Reference="+reference,0);
					}
					regexp.setProgram(reProgram[BRENDA_ENZYME_PUBMED]);//Find PubMed
					String pubmedID = null;
					if(regexp.match(reference)){
						String pubmed_href = etti.getAttributeValue(referenceOffset[0]+regexp.getParenStart(1),"href");
						regexp.setProgram(reProgram[BRENDA_ENZYME_PUBMEDID]);//find pubmedID from href
						if(pubmed_href != null && regexp.match(pubmed_href) && regexp.getParenCount() == 2){
							//xmlCitation.addAttribute(XML_PARSE_ATTR_PUBMEDID,regexp.getParen(1));
							//xmlCitation.addContent(new cbit.vcell.webparser.Id(regexp.getParen(1),cbit.vcell.webparser.Id.XML_ID_TYPE_PUBMED).getElement());
							pubmedID = regexp.getParen(1);
						}else{
							throw new java.text.ParseException("Error parsing BRENDA Enzyme: Found PubMed but no ID",0);
						}
					}
Km kmObject = new Km(ecNumber,kmValue,null,null,
	new Citation(citation,(pubmedID != null?new Id(regexp.getParen(1),Id.XML_ID_TYPE_PUBMED):null)));
Km mergedKm = kmObject.merge(referenceKm);
if(mergedKm == null){
	throw new RuntimeException("Km merged null");
}
kmV.add(mergedKm);

				}else{
					throw new java.text.ParseException("Error parsing BRENDA Enzyme: No REFERENCE Found for Index="+kmRefs[j],0);
				}
				
				//xmlKM.addContent(xmlCitation);
			}
		}
	}

	return new Enzyme(enzName,idV,null,kmV);
	//org.jdom.Document parsedDocument = new org.jdom.Document(xmlEnzyme);
	//return parsedDocument;
*/
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 10:38:24 PM)
 * @return org.jdom.Document
 * @param content java.lang.String
 */
private ParsedElement parse_BRENDA_KMData(String content,ElementToTextIndex etti) throws java.text.ParseException,org.apache.regexp.RESyntaxException{

	org.apache.regexp.RE regexp = new org.apache.regexp.RE();
	regexp.setMatchFlags(org.apache.regexp.RE.MATCH_SINGLELINE);

	java.util.TreeMap kmBlock = getRowsOfData(regexp,content,BRENDA_KMDATA_BLOCK,BRENDA_KMDATA_VALSUBORG,true);
	if(kmBlock == null){
		throw new java.text.ParseException("Error parsing BRENDA KMData: KM data block not found",0);		
	}

	if(!match(regexp,RE.MATCH_SINGLELINE | RE.MATCH_NORMAL,content,BRENDA_KMDATA_ECNUMBER)){
		throw new java.text.ParseException("Error parsing BRENDA KMData: EC Number not found",0);				
	}
	String ecNumber = regexp.getParen(1).trim();

	PEContainer kmPEC = new PEContainer();
	regexp.setProgram(reProgram[NEWLINE]);
	java.util.Iterator kmValSubOrg = kmBlock.values().iterator();
		while(kmValSubOrg.hasNext()){
			String row = (String)kmValSubOrg.next();
			regexp.setProgram(reProgram[BRENDA_KMDATA_VALSUBORG]);
			if(regexp.match(row) && regexp.getParenCount() == 4){
				Hashtable paramAttrs = new Hashtable();
				paramAttrs.put(ParsedElement.XML_PARSE_ATTR_SUBSTRATE,regexp.getParen(2).trim());
				paramAttrs.put(ParsedElement.XML_PARSE_ATTR_ORGANISM,regexp.getParen(3).trim());
				Vector paramContent = new Vector();
				paramContent.add(new Id(Id.XML_ID_TYPE_EC,ecNumber));
				kmPEC.add(new Param(Param.XML_PARAM_TYPE_KM,
								regexp.getParen(1).trim().replace(',','.'),
								paramAttrs,paramContent));
			}else{
				throw new java.text.ParseException("Error parsing BRENDA KMData: Cannot parse KM data",0);		
			}
		}

		return kmPEC;
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 1:09:14 PM)
 * @return cbit.vcell.webparser.ParsedElement
 * @param siteContent java.lang.String
 */
private ParsedElement parse_DOQCS_Kinetics(String content) {
	System.out.println(content);
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2002 5:16:19 PM)
 * @return org.jdom.Document
 * @param node org.w3c.dom.Node
 */
private Reaction parse_KEGG_Reaction(String content) throws java.text.ParseException{

	org.apache.regexp.RE regexp = new org.apache.regexp.RE();

	if(!match(regexp,RE.MATCH_SINGLELINE | RE.MATCH_NORMAL,content,KEGG_REACTION_ENTRY)){
		throw new java.text.ParseException("Error KEGG Reaction: ENTRY not found",0);
	}
	String dataEntry = regexp.getParen(1);
	//if(!match(regexp,RE.MATCH_SINGLELINE | RE.MATCH_NORMAL,content,KEGG_REACTION_DEFINITION)){
		//throw new java.text.ParseException("Error KEGG Reaction: DEFINITION not found",0);
	//}
	//String dataDefinition = subst(regexp,RE.MATCH_SINGLELINE | RE.MATCH_NORMAL,regexp.getParen(1),TRIM_SPACE," ");
	if(!match(regexp,RE.MATCH_SINGLELINE | RE.MATCH_NORMAL,content,KEGG_REACTION_ENZYME)){
		throw new java.text.ParseException("Error KEGG Reaction: ENZYME not found",0);
	}
	String dataEnzyme = regexp.getParen(1);
	if(!match(regexp,RE.MATCH_SINGLELINE | RE.MATCH_NORMAL,content,KEGG_REACTION_EQUATION)){
		throw new java.text.ParseException("Error KEGG Reaction: EQUATION not found",0);
	}
	String dataEquation = regexp.getParen(1);
	if(!match(regexp,RE.MATCH_SINGLELINE | RE.MATCH_NORMAL,content,KEGG_REACTION_NAME)){
		throw new java.text.ParseException("Error KEGG Reaction: NAME not found",0);
	}
	String dataName = regexp.getParen(1);
	//if(!match(regexp,RE.MATCH_SINGLELINE | RE.MATCH_NORMAL,content,KEGG_REACTION_PATHWAY)){
		//throw new java.text.ParseException("Error KEGG Reaction: PATHWAY not found",0);
	//}
	//String dataPathway = regexp.getParen(1);


	
	//org.jdom.Element xmlReaction = new org.jdom.Element(XML_PARSE_TYPE_REACTION);

	//xmlReaction.addContent(new cbit.vcell.webparser.Id(dataEntry,cbit.vcell.webparser.Id.XML_ID_TYPE_KEGG).getElement());
	////xmlReaction.addAttribute(XML_PARSE_ATTR_ID,dataEntry);
	//xmlReaction.addAttribute(XML_PARSE_ATTR_NAME,dataName);
	//xmlReaction.addAttribute(XML_PARSE_ATTR_DEFINITION,dataDefinition);
	
	//org.jdom.Element xmlEnzyme = new org.jdom.Element(XML_PARSE_TYPE_ENZYME);
	//xmlEnzyme.addContent(new cbit.vcell.webparser.Id(dataEnzyme,cbit.vcell.webparser.Id.XML_ID_TYPE_EC).getElement());
	////xmlEnzyme.addAttribute(XML_PARSE_ATTR_ID,dataEnzyme);
	//xmlReaction.addContent(xmlEnzyme);
	
	//Separate pieces of reaction
	regexp.setMatchFlags(org.apache.regexp.RE.MATCH_SINGLELINE);
	regexp.setProgram(reProgram[EQUATION_SEPARATOR]);
	String[] equation = regexp.split(dataEquation);
	boolean isProduct = false;
	String stoich = "1";
	Vector reactionPartsV = new Vector();
	for(int i = 0;i < equation.length;i+= 1){
		if(equation[i].charAt(0) == 'C'){
			//String reactionPartType = (isProduct?getReactionPartType(XML_REACTIONPART_TYPE_PRODUCT):getReactionPartType(XML_REACTIONPART_TYPE_REACTANT));
			/*xmlReaction.addContent*/reactionPartsV.add(new ReactionPart(
					(isProduct?ReactionPart.XML_REACTIONPART_TYPE_PRODUCT:ReactionPart.XML_REACTIONPART_TYPE_REACTANT),stoich,
						new Compound(null,new Id(Id.XML_ID_TYPE_KEGG,equation[i]))));
			//org.jdom.Element xmlRP = (!isProduct?new org.jdom.Element(XML_PARSE_TYPE_REACTANT):new org.jdom.Element(XML_PARSE_TYPE_PRODUCT));
			//xmlRP.addAttribute(XML_PARSE_ATTR_ID,equation[i]);
			//if(stoich != null){
			//	xmlRP.addAttribute(XML_PARSE_ATTR_STOICH,stoich);
			//}
			//xmlReaction.addContent(xmlRP);
			stoich = "1";
		}else if(equation[i].equals("<=>")){
			isProduct = true;
		}else{
			stoich = equation[i];
			//try{
				//Integer.parseInt(equation[i]);
				//stoich = equation[i];
			//}catch(NumberFormatException e){
				//throw new java.text.ParseException("Error KEGG Reaction evaluating Equation = "+dataEquation,0);
			//}
		}
	}

	return new Reaction(dataName,
						reactionPartsV,
						new cbit.vcell.webparser.Id(Id.XML_ID_TYPE_KEGG,dataEntry),
						new Enzyme(null,new cbit.vcell.webparser.Id(Id.XML_ID_TYPE_EC,dataEnzyme)),
						null);
	//org.jdom.Document parsedDocument = new org.jdom.Document(xmlReaction);
	//return parsedDocument;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/2002 5:46:05 PM)
 */
private void popRE(org.apache.regexp.RE regexp) {
	// Check consistency of stack, pop RE off stack,
	if(reStateMatchFlags != -1 && reStateRE != null && reStateRE == regexp){
		regexp.setMatchFlags(reStateMatchFlags);
		regexp.setProgram(reStateProgram);
	}else{
		throw new Error("popRE Error: RE stack inconsistent");
	}
	//Reset stack
	reStateMatchFlags = -1;
	reStateProgram = null;
	reStateRE = null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/2002 5:46:05 PM)
 */
private void pushRE(org.apache.regexp.RE regexp) {

	//
	// Simple state that makes sure RE use is atomic.
	// Each RE.match(...) uses known settings
	// 
	//
	if(reStateMatchFlags == -1 && reStateProgram == null && reStateRE == null){
		reStateRE = regexp;
		reStateMatchFlags = regexp.getMatchFlags();
		reStateProgram = regexp.getProgram();
	}else{
		throw new Error("pushRE Error: RE stack inconsistent");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 5:57:43 PM)
 * @return java.lang.String
 * @param content java.lang.String
 * @param repIndex int
 */
private String subst(org.apache.regexp.RE regexp,int reMatchFlags,String content, int REPIndex,String replaceWith) {
	pushRE(regexp);
	regexp.setProgram(reProgram[REPIndex]);
	regexp.setMatchFlags(reMatchFlags);
	String result = regexp.subst(content,replaceWith);
	popRE(regexp);
	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 9:02:19 PM)
 * @return java.lang.String
 * @param toTrim java.lang.String
 */
private String trimAroundNewlines(RE regexp,String toTrim) {
	
	//Remove leading-trailing space around newlines
	
	pushRE(regexp);
	
	regexp.setMatchFlags(RE.MATCH_SINGLELINE | RE.MATCH_NORMAL);
	regexp.setProgram(reProgram[TRIM_AROUND_NEWLINE]);
	String results = regexp.subst(toTrim,"\n");
	
	popRE(regexp);
	
	return results;
}
}
