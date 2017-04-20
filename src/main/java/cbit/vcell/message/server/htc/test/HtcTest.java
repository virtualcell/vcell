package cbit.vcell.message.server.htc.test;

import org.vcell.util.PropertyLoader;

import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.sge.SgeProxy;
import cbit.vcell.message.server.htc.slurm.SlurmProxy;

public class HtcTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandServiceSsh cmdssh = null;
		try {
			if (args.length != 4){
				System.out.println("Usage: HtcTest remotehost username password (PBS|SGE|SLURM)");
				System.exit(1);
			}
			String host = args[0];
			String username = args[1];
			String password = args[2];
			String htcType = args[3];
			cmdssh = new CommandServiceSsh(host, username, password);
			HtcProxy htcProxy = null;
			if (htcType.equalsIgnoreCase("PBS")){
				htcProxy = new PbsProxy(cmdssh,username);
			}else if (htcType.equalsIgnoreCase("SGE")){
				htcProxy = new SgeProxy(cmdssh,username);
			}else if (htcType.equalsIgnoreCase("SLURM")){
				htcProxy = new SlurmProxy(cmdssh,username);
			}else{
				throw new RuntimeException("unrecognized htc type = "+htcType);
			}
			System.setProperty(PropertyLoader.vcellServerIDProperty,"BETA");
//			testHtcProxy1cmd(htcProxy);
//			testHtcProxy2cmd(htcProxy);
//			testServices(htcProxy, VCellServerID.getServerID("TEST2"));
//			testGetServiceJobInfos(htcProxy, VCellServerID.getServerID("TEST2"));
//			htcProxy.getCommandService().close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cmdssh != null){cmdssh.close();}
		}
	}


}
