package neuroml.util;

import java.util.*;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class NeuroMLNamespacePrefixMapper extends NamespacePrefixMapper
{
	protected Map<String, String> namespaceToPrefixMap;
	
	public NeuroMLNamespacePrefixMapper()
	{		
		namespaceToPrefixMap = new HashMap<String, String>();
		namespaceToPrefixMap.put("http://morphml.org/metadata/schema", "meta");
		namespaceToPrefixMap.put("http://morphml.org/morphml/schema", "morph");
		namespaceToPrefixMap.put("http://morphml.org/channelml/schema", "chan");
		namespaceToPrefixMap.put("http://morphml.org/biophysics/schema", "bio");
		namespaceToPrefixMap.put("http://morphml.org/channelml/schema", "chan");
		namespaceToPrefixMap.put("http://morphml.org/neuroml/schema", "nml");
		namespaceToPrefixMap.put("http://morphml.org/networkml/schema", "net");
	}
	
	
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix)	
	{
		if (namespaceToPrefixMap.containsKey(namespaceUri)) {
			return namespaceToPrefixMap.get(namespaceUri);
		}
		return suggestion;
	}
}
