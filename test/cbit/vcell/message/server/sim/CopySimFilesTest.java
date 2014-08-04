package cbit.vcell.message.server.sim;

import org.junit.Test;

import cbit.vcell.tools.PortableCommand;
import cbit.vcell.tools.PortableCommandWrapper;

public class CopySimFilesTest {
	
	@Test
	public void print( ) {
		String obj  = null;
		{
			CopySimFiles csf = new CopySimFiles("SimID_89748914","d:\\test\\gerardw","d:/test/out");
			PortableCommandWrapper jcp = new PortableCommandWrapper(csf);
			obj = jcp.asJson();
			System.out.println(obj);
		}
		
		PortableCommand pc = PortableCommandWrapper.fromJson(obj);
		int rcode = pc.execute(); 
		System.out.println(rcode);
		if (rcode != 0) {
			pc.exception().printStackTrace();
		}
	}

}
