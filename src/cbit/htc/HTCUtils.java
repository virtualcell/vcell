package cbit.htc;

import java.io.File;
import java.util.StringTokenizer;

import org.vcell.util.PropertyLoader;


public class HTCUtils {

	/**
	 * Insert the method's description here.
	 * Creation date: (2/8/2007 8:40:06 AM)
	 * @return java.io.File
	 */
	public static java.io.File getJobSubmitTemplate(String computeResource) {
		File submitTemplate = null;
		String templates = PropertyLoader.getRequiredProperty(PropertyLoader.htcSubmitTemplates);
		StringTokenizer st = new StringTokenizer(templates, " ,");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (computeResource == null || token.startsWith(computeResource)) {
				StringTokenizer st1 = new StringTokenizer(token, "@");
				token = st1.nextToken();
				token = st1.nextToken();
				submitTemplate = new java.io.File(token);
				break;
			}
		}
		if (submitTemplate == null && !submitTemplate.exists()) {
			throw new RuntimeException("Condor submit template doesn't exist");
		}
		return submitTemplate;
	}

}
