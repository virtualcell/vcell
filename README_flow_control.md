#Flow-Control

#Remote Login (client-server)
Client-Side  
-cbit.vcell.client.test.VCellClientTest  
--cbit.vcell.client.VCellClient  
---cbit.vcell.client.server.ClientServerManager  
----cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory  
-----cbit.vcell.server.VCellConnection (Connection to outside for all of VCell)  
------cbit.vcell.message.server.bootstrap.client.LocalVCellConnectionMessaging (Presents restful VCellApiClient services)  
-------cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyRpcSender (manages VCellApiClient)  
--------org.vcell.api.client.VCellApiClient (.authenticate(...), and all VCell client calls (with AccessTokenRepresentation) leave host from here)  
Server-side
vcell-api container 
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
-----org.vcell.api.client.VCellApiClient.sendRpcMessage(...)  (blocking call, waits for http response)
Server-Side  
vcell-api container  
------org.vcell.rest.rpc.RpcRestlet.handle(...) (blocking call)  
-------org.vcell.rest.rpc.RpcService  (sends request to AMQint) (blocking call, waits for message response on temporary queue)  
activemqint container  
-------recieves rpc request 'dbReq' queue  
-------notifies vcell-db container that a rpc request is ready (traditional point to point message queue rather than publish/subscribe)  
vcell-db container  
--------cbit.vcell.message.VCRpcMessageHandler.onQueueMessage(...)  
---------cbit.vcell.message.VCRpcRequest.rpc(...)  
----------cbit.vcell.modeldb.DatabaseServerImpl.getVCInfoContainer()  (sends data response to AMQint on temp queue)  
activemqint container  
-----------receives rpcresponse  
-----------notifies vcell-api (point to point queue)  
vcell-api container  
-----org.vcell.api.client.VCellApiClient.sendRpcMessage(...)  (return from blicking call started above)  


#Server solver send status to Server database
**SOLVER executables**, c++ code  
each solver calls vcell-solver/VCellMessaging/src/SimulationMessaging.cpp.start(...) once at startup (sets AMQ host and port)  
all solvers call vcell-solver/VCellMessaging/src/SimulationMessaging.cpp.setWorkerEvent(...) to put messages in queue  
-SimulationMessaging.sendStatus(...) (polling thread to get status from queue, sends http POST using curl to AMQsim)  
**activemqsim container**  (runs ActiveMQ)  
-recieves sim status messages on 'workerEvent queue'  
--notifies sched container that a workerEvent is ready (traditional point to point message queue rather than publish/subscribe)  
**sched container**  (AMQsim java, contains entire database driver code, does not use vcell-db container)  
-SimulationDispatcher.onWorkerEventMessage(..)  
--SimulationDispatcherEngine.onworkerEvent(...)  
---SimulationStateMachine.onWorkerEvent(...)  
----SimulationDatabaseDirect.updateSimulationJobStatus(...)  
-----AdminDBToplevel.updateSimulationJobStatus(...)  (**updates Oracle** real database host vcell-db.cam.uchc.edu, NOT vcell-db container)  
----StatusMessage.sendToClient(...) (sends to AMQint container clientStatus topic)  
**activemqint container**  
-recieves sim status message on the 'clientStatus' topic  
--notifies all vcell-api container subscribers  
**VCell-Api container**  
-RestEventService.newEventMessage(...)  (add to local cache called 'events' simulationJobStatus,ExportEvent,DataJobEvent)  

#Client asks for server side events includes (Solver status,Export, DataJob)
Client-side  
-AsynchMessageManager.poll() (feeds a single AnychMessageManager, client code add listeners to get events)  
--ClientServerManager.getmessageEvents()  
---LocalVCellConnectionMessaging.getMessageEvents()  
----cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyRpcSender (manages VCellApiClient)  
-----org.vcell.api.client.VCellApiClient.getEvents() (uses https://host:port/events?beginTimestamp=xxx)  
Server-side  
vcell-api container
-EventsRestlet.handle(...) (handles any uri of type '/events', defined in VCellApiApplication)  
--RestEventService.query(userid,lastTimestamp) (return json encoded events/timestamp since last timestamp)  

#Client to Server starting a simulation
Client-side  
-Green button -> SimulationListPanel.runSimulations(...)  
--Simulationworkspce.runSimulation(...)  
---ClientRequestManager.runSimulations(...)  
----RunSims.run()  
----ClientJobManager.startSimulation(...)  
-----LocalSimulationControllerMessaging.startSimulation(...)  
-----RpcSimServerProxy.startsimulation(...)  
------RemoteProxyRpcSender.sendRpcMessage(...)  
------VCellApiClient.sendRpcMessage(...)  
Server-side  
vcell-api container  
------org.vcell.rest.rpc.RpcRestlet.handle(...) (blocking call)  
-------org.vcell.rest.rpc.RpcService (sends request to AMQint) (blocking call, waits for message response on temporary queue)  
activemqint container  
-------recieves rpc request 'simReq' queue  
-------notifies vcell-sched container that a rpc request is ready (traditional point to point message queue rather than publish/subscribe)  
vcell-sched container  
--------cbit.vcell.message.VCRpcMessageHandler.onQueueMessage(...)  
---------cbit.vcell.message.VCRpcRequest.rpc(...)  
----------SimulationDispatcher.SimulationServiceImpl.startSimulation(...)  
-----------SimulationDispatcherEngine.onStartRequest(...)  
------------SimulationStateMachine.onStartRequest(...)  
-------------SimulationDatabseDirect.insertSimulationJobStatus(...) (save SimJobStatus to DB with status 'waiting'  
-------------StatusMessage.sendToClient(...) (send message to AMQint topic:clientStatus, then returns to client)  
=====Now Sim request waits to be processed in different separate thread according to scheduler=====  
vcell-sched container  
-SimulationDispatcher.DispatchThread.run(...) (run continuously, polls db)  
--SimulationDatabaseDirect.getActiveJobs(...)  
--BatchScheduler.schedule(...) (return list of jobs that should be done after deciding)  
--SimulationDispatcherEngine.onDispatch(...) {done for each job to be run, get simulation from db)  
---SimulationStateMachine.onDispatch(...) (create simulation task)  
----SimulationDatabseDirect.updateSimulationJobStatus(...) (SimJobStatus to DB with status 'dispatched')  
----SimulationTaskMessage.sendSimulationTask(...) (send message to AMQint queue 'simJob')  
----StatusMessage.sendToClient(...) (send message AMQint topic:clientStatus, return from processing)  
=====Now scheduled sim task is waiting in queue to be read by vcell-submit  
vcell-submit container/service  
-HtcSimulationWorker.initQueueConsumer().QueueListener.onQueueMessage(...) (receives sim task message)  
--HtcSimulationWorker.submit2PBS(...) (writes simtask.xml to the user directory)  
---SlurmProxy.submitJb(...) (generates .slurm.sub script, writes to htclogs dir (/htclogs inside container) (/share/apps/vcell/htclogs outside container))  
----SlurmProxy.submitJobFile(...) (ssh hpc-ext-1.cam.uchc.edu (because this is a slurm submit node), executes sbatch xxx.slurm.sub, parses stdout from sbatch to get slurm jobid)  
-----WorkerEventMessage.sendAccepted(...) (sends message to AMQsim queue 'workerEvent' type jobAccepted)  
=====Now submitted sim task waits to be processed by SLURM  
-SLURM read xxx.slurm.sub, parses #SBATCH directives, chooses a hpc node and executes bash script xxx.slurm.sub  
 


-Solvers running on hpc hosts inside singularity images named as vcell-docker.cam.uchc.edu_5000_schaff_vcell-batch_xxx.img  
-solvers write data to /share/apps/vcell3/users mapped to /simdata  
-Uses "Server solver send status to Server database" as described above  

#Client get simulation Data
Client-side  
-ClientServerManager.getVCDataManager().getDatasetController().xxx  
--LocalDatasetControllerMessaging.xxx  
---RpcDataServerProxy.xxx  
----RemoteProxyRpcSender.sendRpcMessage(...)  
-----VCellApiClient.sendRpcMessage(...) (blocking call, waits for http response)  
Server-side  
vcell-api container  
------org.vcell.rest.rpc.RpcRestlet.handle(...) (blocking call)  
-------org.vcell.rest.rpc.RpcService  (sends request to AMQint) (blocking call, waits for message response on temporary queue)  
activemqint container  
-------recieves rpc request 'dataReq' queue  
-------notifies vcell-data container that a rpc request is ready (traditional point to point message queue rather than publish/subscribe)  
vcell-data container  
--------cbit.vcell.message.VCRpcMessageHandler.onQueueMessage(...)  
---------cbit.vcell.message.VCRpcRequest.rpc(...)  
----------DataserverImpl.xxx  
----------cbit.vcell.modeldb.DatasetControllerImpl.xxx  (sends data response to AMQint on temp queue)  
activemqint container  
-----------receives rpcresponse  
-----------notifies vcell-api (point to point queue)  
vcell-api container  
-----org.vcell.api.client.VCellApiClient.sendRpcMessage(...)  (return from blocking call started above)  


#Client get Bio/Math Model
client-side


