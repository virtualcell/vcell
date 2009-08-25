package cbit.vcell.solver;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.util.xml.XmlUtil;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.parser.ASTFuncNode;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XMLTags;

public final class DataProcessingInstructions implements Matchable, Serializable {
	private String scriptName;
	private String scriptInput;

	public DataProcessingInstructions(String scriptName, String scriptInput){
		this.scriptName = scriptName;
		this.scriptInput = scriptInput;
	}

	public String getScriptName() {
		return scriptName;
	}
	public static DataProcessingInstructions getVFrapInstructions(int[] volumePoints, int[] membranePoints, int numRegions, int zSlice, KeyValue fieldDataKey, FieldFunctionArguments fd, boolean bStoreEnabled) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("VolumePoints " + volumePoints.length + "\n");
		for (int i = 0; i < volumePoints.length; i++) {
			sb.append(volumePoints[i] + " ");
			if ((i+1) % 20 == 0) {
				sb.append("\n");
			}
		}
		sb.append("\n");
		if (membranePoints != null && membranePoints.length > 0) {	
			sb.append("MembranePoints " + membranePoints.length + "\n");
			for (int i = 0; i < membranePoints.length; i++) {
				sb.append(membranePoints[i] + " ");
				if ((i+1) % 20 == 0) {
					sb.append("\n");
				}
			}
			sb.append("\n");
		}
		sb.append("SampleImage " + numRegions + " " + zSlice + " " + fieldDataKey + " " + ASTFuncNode.getFunctionNames()[ASTFuncNode.FIELD] + "(" + fd.toCSVString() + ")\n");
		sb.append("StoreEnabled " + bStoreEnabled + "\n");

		return new DataProcessingInstructions("VFRAP", sb.toString());
	}
	
	public String getScriptInput() {
		return scriptInput;
	}

	public static DataProcessingInstructions fromDbXml(String xmlString){
		Element root = XmlUtil.stringToXML(xmlString, null).getRootElement();
		if (!root.getName().equals(XMLTags.DataProcessingInstructionsTag)){
			throw new RuntimeException("expected root element to be named "+XMLTags.DataProcessingInstructionsTag);
		}
		String scriptName = root.getAttributeValue(XMLTags.DataProcessingScriptNameAttrTag);
		String scriptInput = root.getText();
		return new DataProcessingInstructions(scriptName,scriptInput); 
	}
	
	public String getDbXml(){
		Element root = new Element(XMLTags.DataProcessingInstructionsTag);
		root.setAttribute(XMLTags.DataProcessingScriptNameAttrTag, scriptName);
		root.setText(scriptInput);
		return XmlUtil.xmlToString(root);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof DataProcessingInstructions){
			DataProcessingInstructions dpi = (DataProcessingInstructions)obj;
			if (!Compare.isEqual(dpi.scriptName,scriptName)){
				return false;
			}
			if (!Compare.isEqual(dpi.scriptInput,scriptInput)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public FieldDataIdentifierSpec getSampleImageFieldData(User user) {
		if (scriptInput != null) {
			int index = scriptInput.indexOf("SampleImage");
			StringTokenizer st = new StringTokenizer(scriptInput.substring(index));
			st.nextToken();
			st.nextToken();
			st.nextToken();
			String key = st.nextToken();
			
			index = scriptInput.indexOf(ASTFuncNode.getFunctionNames()[ASTFuncNode.FIELD]);
			if (index >= 0) {
				st = new StringTokenizer(scriptInput.substring(index), "\n");
				if (st.hasMoreTokens()) {
					String fieldFunction = st.nextToken();
					try {
						Expression exp = new Expression(fieldFunction);					
						FieldFunctionArguments[] ffa = exp.getFieldFunctionArguments();
						return new FieldDataIdentifierSpec(ffa[0], new ExternalDataIdentifier(KeyValue.fromString(key), user, ffa[0].getFieldName()));
					} catch (ExpressionException e) {
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());
					}
				}
			}
		}
		return null;
	}

}
