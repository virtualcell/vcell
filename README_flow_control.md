#Flow-Control

#Remote Login (client-server)
Client-Side  
-cbit.vcell.client.test.VCellClientTest  
--cbit.vcell.client.VCellClient  
---cbit.vcell.client.server.ClientServerManager  
----cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory  
-----cbit.vcell.server.VCellConnection (Connection to outside for all of VCell)  
------cbit.vcell.message.server.bootstrap.client.LocalVCellConnectionMessaging (Presents restful VCellApiClient services)  
-------cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyRpcSender (manages VCellAiClient)  
--------org.vcell.api.client.VCellApiClient (.authenticate(...), and all VCell client calls (with AccessTokenRepresentation) leave host from here)  
Server-Side  
--------org.vcell.rest.VCellApiMain (entrypoint, Starts http restlet server)  
-------org.vcell.rest.VCellApiApplication (Registers handlers with the restlet server)  
------org.vcell.rest.auth.AuthenticationTokenRestlet (login authentication, return AccessTokenRepresentation)  

#Populate Client Database metadata
Client-Side  
...
-cbit.vcell.message.server.bootstrap.client.LocalVCellConnectionMessaging.getUserMetaDbServer()
--cbit.vcell.message.server.bootstrap.client.LocalUserMetaDbServerMessaging.getVCInfoContainer()
---cbit.vcell.message.server.bootstrap.client.RpcDbServerProxy.getVCInfoContainer()
----cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyRpcSender.sendRpcMessage(...)
-----org.vcell.api.client.VCellApiClient.sendRpcMessage(...)
Server-Side
-----org.vcell.rest.rpc.RpcRestlet.handle(...) (blocking call)
----org.vcell.rest.rpc.RpcService
---cbit.vcell.message.VCRpcMessageHandler.onQueueMessage(...)
--cbit.vcell.message.VCRpcRequest.rpc(...)
-cbit.vcell.modeldb.DatabaseServerImpl.getVCInfoContainer()