import requests
import hashlib


class BiomodelsQuerySpec(object):
    def __init__(self):
        self.bmName="";
        self.bmId="";
        self.category="all";
        self.owner="";
        self.savedLow="";
        self.savedHigh="";
        self.startRow="";
        self.maxRows="10";
        self.orderBy="date_desc";

    def getQueryString(self):
        return "bmName="+self.bmName+"&"+        \
                "bmId="+self.bmId+"&"+           \
				"category="+self.category+"&"+   \
				"owner="+self.owner+"&"+         \
				"savedLow="+self.savedLow+"&"+   \
				"savedHigh="+self.savedHigh+"&"+ \
				"startRow="+self.startRow+"&"+   \
				"maxRows="+self.maxRows+"&"+     \
				"orderBy="+self.orderBy;

class SimulationTasksQuerySpec(object):
    def __init__(self):
        self.submitLow = "";
        self.submitHigh = "";
        self.startRow = "1";
        self.maxRows = "10";
        self.serverId = "";		# "alpha"
        self.computeHost = "";	# "signode10"
        self.simId = "";
        self.jobId = "";
        self.taskId = "";
        self.hasData = "all";	# "all", "yes", "no"
        self.waiting = "on";
        self.queued = "on";
        self.dispatched = "on";
        self.running = "on";
        self.completed = "on";
        self.failed = "on";
        self.stopped = "on";

    def getQueryString(self):
        return "submitLow"+"="+self.submitLow+"&"+          \
				"submitHigh"+"="+self.submitHigh+"&"+       \
				"startRow"+"="+self.startRow+"&"+           \
				"maxRows"+"="+self.maxRows+"&"+             \
				"serverId"+"="+self.serverId+"&"+           \
				"computeHost"+"="+self.computeHost+"&"+     \
				"simId"+"="+self.simId+"&"+                 \
				"jobId"+"="+self.jobId+"&"+                 \
				"taskId"+"="+self.taskId+"&"+               \
				"hasData"+"="+self.hasData+"&"+             \
				"waiting"+"="+self.waiting+"&"+             \
				"queued"+"="+self.queued+"&"+               \
				"dispatched"+"="+self.dispatched+"&"+       \
				"running"+"="+self.running+"&"+             \
				"completed"+"="+self.completed+"&"+         \
				"failed"+"="+self.failed+"&"+               \
				"stopped"+"="+self.stopped;


class AccessToken(object):
    def __init__(self):
        self.token = ""
        self.creationDateSeconds = 0;
        self.expireDateSeconds = 0;
        self.userId = "";
        self.userKey = "";

    def loadJSON(self, accessToken_dict):
        self.__dict__.update(accessToken_dict)

class MathModelLink(object):
    def __init__(self):
        self.mathModelKey = "";
        self.mathModelBranchId = "";
        self.mathModelName = "";

class BioModelLink(object):
    def __init__(self):
        self.bioModelKey = "";
        self.bioModelBranchId = "";
        self.bioModelName = "";
        self.simContextKey = "";
        self.simContextBranchId = "";
        self.simContextName = "";

class Application(object):
    def __init__(self):
        self.key = "";
        self.branchId = "";
        self.name = "";
        self.ownerName = "";
        self.ownerKey = "";
        self.mathKey = "";

    def loadJSON(self, json_dictionary):
       self.__dict__.update(json_dictionary)

class Simulation(object):
    def __init__(self):
        self.key = "";
        self.branchId = "";
        self.name = "";
        self.ownerName = "";
        self.ownerKey = "";
        self.mathKey = "";
        self.solverName = "";
        self.scanCount = -1;
        self.mathModelLink = None;
        self.bioModelLink = None;

    def loadJSON(self,simulation_dict):
        self.__dict__.update(simulation_dict)

        if (self.bioModelLink != None):
            link = BioModelLink()
            link.__dict__.update(self.bioModelLink)
            self.bioModelLink = link
        
        if (self.mathModelLink != None):
            link = MathModelLink()
            link.__dict__.update(s.mathModelLink)
            self.mathModelLink = link

class SimulationTask(object):
    def __init__(self):
        self.simKey = "";
        self.simName = "";
        self.userName = "";
        self.userKey = "";
        self.htcJobId = "";
        self.status = "";
        self.startdate = None;
        self.jobIndex = None;
        self.taskId = None;
        self.message = "";
        self.site = "";
        self.computeHost = "";
        self.schedulerStatus = "";
        self.hasData = None;
        self.scanCount = None;
        self.mathModelLink = None;
        self.bioModelLink = None;

    def loadJSON(self, simTask_dict):
        self.__dict__.update(simTask_dict)

        if (self.bioModelLink != None):
            link = BioModelLink()
            link.__dict__.update(self.bioModelLink)
            self.bioModelLink = link
        
        if (self.mathModelLink != None):
            link = MathModelLink()
            link.__dict__.update(s.mathModelLink)
            self.mathModelLink = link


class Biomodel(object):
    def __init__(self):
        self.bmKey = "";
        self.name = "";
        self.privacy = -1;
        self.groupUsers = { "" };
        self.savedDate = -1;
        self.annot = "";
        self.branchID = "";
        self.modelKey = "";
        self.ownerName = ""
        self.ownerKey = "";
        self.simulations = [];
        self.applications = [];

    def loadJSON(self,biomodel_dict):
        self.__dict__.update(biomodel_dict)

        if (self.applications != None):
            apps = [];
            for application_dict in self.applications:
                a = Application()
                a.loadJSON(application_dict)
                apps.append(a)
            self.applications = apps

        if (self.simulations != None):
            sims = [];
            for simulation_dict in self.simulations:
                s = Simulation()
                s.loadJSON(simulation_dict)
                sims.append(s)
            self.simulations = sims


class VCellApi(object):

    def __init__(self, host, port, clientID): # (self, host, port, clientID, bIgnoreCertProblem, bIgnoreHostMismatch):
        self.host = host
        self.port = port
        self.clientID = clientID
        #self.bIgnoreCertProblem = bIgnoreCertProblem
        #self.bIgnoreHostMismatch = bIgnoreHostMismatch
        self.userid = None;
        self.passowrd = None;
        self.access_token = None;   # '30fead75-4f3e-40af-88c5-3623e3228858';
        
    def _getResponse(self,url,bRequiresAuth):
        if (self.access_token == None):
            if (bRequiresAuth == False):
                response = requests.get(url)
            else:
                raise AssertionError("call requires authentication")
        else:
            response = requests.get(url, auth=('access_token', self.access_token))

        if (response.status_code != 200):
            print("url "+url+" returned with unexpected status "+str(response.status_code))
            raise Exception("url "+url+" returned with unexpected status "+str(response.status_code))

        return response

    def _post(self,url,bRequiresAuth):
        if (self.access_token == None):
            if (bRequiresAuth == False):
                response = requests.post(url)
            else:
                raise AssertionError("call requires authentication")
        else:
            response = requests.get(url, auth=('access_token', self.access_token))

        if (response.status_code != 200):
            print("url "+url+" returned with unexpected status "+str(response.status_code))
            raise Exception("url "+url+" returned with unexpected status "+str(response.status_code))

        return response


    def authenticate(self,userid,clearTextPassword):
        m = hashlib.sha1()
        m.update(clearTextPassword);
        digestedPassword = m.hexdigest().upper()
        url = 'https://'+self.host+":"+str(self.port)+"/access_token?user_id="+userid+"&user_password="+digestedPassword+"&client_id="+self.clientID
        accessTokenResponse = requests.get(url);
        accessToken = AccessToken()
        accessToken.loadJSON(accessTokenResponse.json())
        self.access_token = accessToken.token


    def logout(self):
        self.access_token = null;


    def getBiomodel(self,biomodelID):
        biomodelResponse = self._getResponse('https://'+self.host+":"+str(self.port)+'/biomodel/'+str(biomodelID),False) 
        biomodel = Biomodel()
        biomodel.loadJSON(biomodelResponse.json())
        assert isinstance(biomodel,Biomodel)
        return biomodel


    def getBiomodels(self,biomodelsQuerySpec):
        assert isinstance(biomodelsQuerySpec,BiomodelsQuerySpec) or biomodelsQuerySpec == None
        if (biomodelsQuerySpec == None):
           biomodelsQuerySpec = BiomodelsQuerySpec()
        biomodelsResponse = self._getResponse('https://'+self.host+":"+str(self.port)+'/biomodel?'+biomodelsQuerySpec.getQueryString(),False) 
        biomodels = [];
        for biomodel_dict in biomodelsResponse.json():
            b = Biomodel()
            b.loadJSON(biomodel_dict)
            biomodels.append(b)
        return biomodels


    def getSimulation(self, biomodelID, simKey):
        simulationResponse = self._getResponse('https://'+self.host+":"+str(self.port)+'/biomodel/'+str(biomodelID)+'/simulation/'+str(simKey), False)
        s = Simulation()
        s.loadJSON(simulationResponse.json());
        assert isinstance(s,Simulation)
        return s
 

    def getSimulationTasks(self, simTasksQuerySpec):
        assert isinstance(simTasksQuerySpec,SimulationTasksQuerySpec) or simTasksQuerySpec == None
        if (simTasksQuerySpec == None):
           simTasksQuerySpec = SimulationTasksQuerySpec()
        simTasksResponse = self._getResponse('https://'+self.host+":"+str(self.port)+'/simtask?'+simTasksQuerySpec.getQueryString(), True)
        simTasks = [];
        for simTask_dict in simTasksResponse.json():
            s = SimulationTask()
            s.loadJSON(simTask_dict)
            simTasks.append(s)
        return simTasks



    def startSimulation(self, biomodelID, simKey):
        simulationStartResponse = self._post('https://'+self.host+":"+str(self.port)+'/biomodel/'+str(biomodelID)+'/simulation/'+str(simKey)+'/startSimulation', True)
        simTasks = [];
        for simTask_dict in simulationStartResponse.json():
            s = SimulationTask()
            s.loadJSON(simTask_dict)
            simTasks.append(s)
        return simTasks


    def stopSimulation(self, biomodelID, simKey):
        simulationStopResponse = self._post('https://'+self.host+":"+str(self.port)+'/biomodel/'+str(biomodelID)+'/simulation/'+str(simKey)+'/stopSimulation', True)
        simTasks = [];
        for simTask_dict in simulationStartResponse.json():
            s = SimulationTask()
            s.loadJSON(simTask_dict)
            simTasks.append(s)
        return simTasks
 