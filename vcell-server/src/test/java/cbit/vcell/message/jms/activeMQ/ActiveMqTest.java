package cbit.vcell.message.jms.activeMQ;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.network.NetworkConnector;


public class ActiveMqTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BrokerService broker = new BrokerService();
			broker.setBrokerName("fred");
			broker.setUseShutdownHook(false);
			broker.setPersistent(false);
			//Add plugin
			//broker.setPlugins(new BrokerPlugin[]{new JaasAuthenticationPlugin()});
			//Add a network connection
			NetworkConnector connector = broker.addNetworkConnector("static://"+"tcp://localhost:61616");
			connector.setDuplex(true);
			TransportConnector transportConnector = broker.addConnector("tcp://localhost:61616");
			broker.start();	
			
			//transportConnector.
			
			ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}
