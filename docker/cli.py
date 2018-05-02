#!/usr/bin/env python

import sys, os, paramiko, argparse, traceback, logging



#
# SSH Helper Class
#

class ssh:
    client = None
    address = None
    logger = None

    def __init__(self, address, username):
        assert isinstance(address, str)
        assert isinstance(username, str)
        self.address = address
        self.logger = logging.getLogger("paramiko")
        self.logger.info("======== Connecting to " + address + " as user " + username + " ========")

        self.client = paramiko.client.SSHClient()
        self.client.set_missing_host_key_policy(paramiko.client.AutoAddPolicy())

        key_path_dsa = os.path.join(os.path.expanduser("~"), ".ssh", "schaff_dsa")
        key_path_rsa = os.path.join(os.path.expanduser("~"), ".ssh", "schaff_rsa")
        if os.path.isfile(key_path_dsa):
            # look for an alternative DSA key file before trying default ~/.ssh/id_rsa
            k = paramiko.DSSKey.from_private_key_file(key_path_dsa)
            self.client.connect(address, username=username, pkey=k)
        elif os.path.isfile(key_path_rsa):
            # look for an alternative RSA key file before trying default ~/.ssh/id_rsa
            k = paramiko.RSAKey.from_private_key_file(key_path_rsa)
            self.client.connect(address, username=username, pkey=k)
        else:
            # try ~/.ssh/id_rsa
            self.client.connect(address, username=username, look_for_keys=True)

    def sendCommand(self, command):
        if (self.client != None):
            self.logger.info("=== command: " + command)
            stdin, stdout, stderr = self.client.exec_command(command)
            alldata = ''
            while not stdout.channel.exit_status_ready():
                alldata += stdout.channel.recv(1024)
                while stdout.channel.recv_ready():
                    alldata += stdout.channel.recv(1024)

            exitStatus = stdout.channel.recv_exit_status()
            self.logger.info("=== retcode: " + str(exitStatus))
            self.logger.info("=== stdout: " + str(alldata))
            return exitStatus, str(alldata)
        else:
            raise RuntimeError("Connection not opened")

    def close(self):
        if (self.client != None):
            self.client.close()
            self.logger.info("======== Closing connection to " + self.address + " ========")



class Dockerservice:
    user = None
    name = None
    host = None
    container_name = None
    env = None
    ports = None
    volumes = None
    image_name = None

    def __init__(self, user, name, host, container_name, env, ports, volumes, image_name):
        assert isinstance(user, str)
        assert isinstance(name, str)
        assert isinstance(host, str)
        assert isinstance(container_name, str)
        assert isinstance(env, dict)
        assert isinstance(ports, dict)
        assert isinstance(volumes, dict)
        assert isinstance(image_name, str)
        self.user = user
        self.name = name
        self.host = host
        self.container_name = container_name
        self.env = env
        self.ports = ports
        self.volumes = volumes
        self.image_name = image_name

    def start(self):
        cmd = 'bash -l -c "sudo docker container start ' + self.container_name + '"'
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand(cmd)
            if retcode != 0:
                raise RuntimeError(
                    "docker container start failed: {0}: ret={1}, stdout=\'{2}\'".format(self.container_name,
                                                                                         str(retcode), str(stdout)))
            print ("started docker container " + self.container_name + " on " + self.host)
        finally:
            if connection is not None:
                connection.close()

    def stop(self):
        cmd = 'bash -l -c "sudo docker container stop ' + self.container_name + '"'
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand(cmd)
            if retcode != 0:
                raise RuntimeError(
                    "docker container stop failed: {0}: ret={1}, stdout=\'{2}\'".format(self.container_name,
                                                                                        str(retcode), str(stdout)))
            print ("stopped docker container " + self.container_name + " on " + self.host)
        finally:
            if connection is not None:
                connection.close()

    def status(self):
        cmd = 'bash -l -c "sudo docker container inspect ' \
              '--format=\'status: {{.State.Status}}, startedAt: {{.State.StartedAt}}\' ' \
              + self.container_name + '"'
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand(cmd)
            if retcode != 0:
                raise RuntimeError(
                    "docker container creation failed: {0}: ret={1}, stdout=\'{2}\'".format(self.container_name,
                                                                                            str(retcode), str(stdout)))
            print ("status of docker container " + self.container_name + " on " + self.host + " is " + str(stdout))
        finally:
            if connection is not None:
                connection.close()

    def create(self):
        cmd = 'bash -l -c "sudo docker create --name=' + self.container_name
        for varname, value in self.env.items():
            cmd += " -e " + varname + '=' + value
        for hostdir, containerdir in self.volumes.items():
            cmd += " -v " + hostdir + ':' + containerdir
        for hostport, containerport in self.ports.items():
            cmd += " -p " + hostport + ':' + containerport
        cmd += " "+self.image_name
        cmd += '"'

        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand(cmd)
            if retcode != 0:
                raise RuntimeError(
                    "docker container creation failed: {0}: ret={1}, stdout=\'{2}\'".format(self.container_name,
                                                                                            str(retcode), str(stdout)))
            print ("created docker container " + self.container_name + " on " + self.host)
        finally:
            if connection is not None:
                connection.close()

    def destroy(self):
        cmd = 'bash -l -c "sudo docker container rm ' + self.container_name + '"'
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand(cmd)
            if retcode != 0:
                raise RuntimeError(
                    "docker container remove failed: {0}: ret={1}, stdout=\'{2}\'".format(self.container_name,
                                                                                        str(retcode), str(stdout)))
            print ("removed docker container " + self.container_name + " on " + self.host)
        finally:
            if connection is not None:
                connection.close()


def getenv(varname):
    value = os.environ.get(varname, None)
    if (value is None):
        raise EnvironmentError("undefined environment var '" + varname + "', hint: invoke using ./vcell script")
    assert isinstance(value, str)
    return value

def setdebug(debug):
    assert isinstance(debug,bool)
    logger = logging.getLogger("paramiko")
    console_handler = logging.StreamHandler()
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    console_handler.setFormatter(formatter)
    logger.addHandler(console_handler)
    if debug:
        logger.setLevel(logging.DEBUG)
        console_handler.setLevel(logging.DEBUG)
    else:
        logger.setLevel(logging.WARN)
        console_handler.setLevel(logging.WARN)

def simquery(query, host_port):
    assert(isinstance(host_port,str))
    import requests
    r = requests.get('https://'+host_port+'/admin/jobs', params=query.params())
    print(r.url)
    json = r.json()
    return json


class simjobquery:
    fields = {}

    def __init__(self):
        # type: () -> object
        self.fields = {
            "submitLowMS" : None,
            "submitHighMS" : None,
            "startLowMS" : None,
            "startHighMS" : None,
            "endLowMS" : None,
            "endHighMS" : None,
            "startRow" : 1,
            "maxRows" : 100,
            "serverId" : None,
            "computeHost" : None,
            "userid" : None,
            "simId" : None,
            "jobId" : None,
            "taskId" : None,
            "hasData" : None,
            "waiting" : None,
            "queued" : None,
            "dispatched" : None,
            "running" : None,
            "completed" : None,
            "failed" : None,
            "stopped" : None,
        }

    def params(self):
        return {k: v for k, v in self.fields.items() if v is not None}



def main():

    parser = argparse.ArgumentParser("vcellcli")
    parser.add_argument("--debug", help="debug commands", action="store_true")
    subparsers = parser.add_subparsers()

    parser_showjobs = subparsers.add_parser('showjobs', help='show simulation jobs (see showjobs --help)')
    parser_showjobs.add_argument("--userid", type=str, default=None)
    parser_showjobs.add_argument("--simId", type=int, default=None)
    parser_showjobs.add_argument("--jobId", type=int, default=None)
    parser_showjobs.add_argument("--taskId", type=int, default=None)

    parser_showjobs.add_argument("--submit", type=str, default='')

    parser_showjobs.add_argument("--hasData", type=str, default=None)

    parser_showjobs.add_argument("--status", type=str, default='wqdr', 
        help='job status (default wqdr) a-all, w-waiting, q-queued, d-dispatched, r-running, c-completed, s-stopped')

    parser_showjobs.add_argument("--maxRows", type=int, default=100)
    parser_showjobs.add_argument("--hostport", type=str, default='vcellapi.cam.uchc.edu:443')
    parser_showjobs.set_defaults(which='showjobs')

    parser_killjobs = subparsers.add_parser('siminfo', help='detailed simulation info (see siminfo --help)')
    parser_killjobs.add_argument("--simId", type=int, )
    parser_killjobs.set_defaults(which='siminfo')

    parser.set_defaults(debug=False)
    #main_args = parser.parse_args(['showjobs','--userid','schaff'])
    args = parser.parse_args()
    try:
        setdebug(args.debug)
        if args.which == "showjobs":
            query = simjobquery()
            query.fields['userid'] = args.userid
            query.fields['simId'] = args.simId
            query.fields['jobId'] = args.jobId
            query.fields['waiting'] = True if 'w' in args.status else False
            query.fields['queued'] = True if 'q' in args.status else False
            query.fields['dispatched'] = True if 'd' in args.status else False
            query.fields['running'] = True if 'r' in args.status else False
            query.fields['completed'] = True if 'c' in args.status else False
            query.fields['failed'] = True if 'f' in args.status else False
            query.fields['stopped'] = True if 's' in args.status else False
            query.fields['maxRows'] = args.maxRows
            hostport = args.hostport
            json_response = simquery(query,hostport)
            col_names = ["owner_userid", "simulationKey", "jobIndex", "taskID", "schedulerStatus", "hasData",
                         "vcellServerID","queueDate", "simexe_startDate", "simexe_latestUpdateDate",
                         "computeHost", "detailedStateMessage"]
            table = [[row.get(col_name,'') for col_name in col_names] for row in json_response]
            from tabulate import tabulate
            print tabulate(table, headers=col_names)
            # for job in json_response:
            #     print(job)
        elif args.which == "siminfo":
            pass
        else:
            parser.print_help()
            raise Exception("unknown command " + args.cmd)

    except paramiko.SSHException:
        print "SSHException: ", sys.exc_info()[0]
        sys.exit(-1)
    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0], e_info[1], e_info[2], file=sys.stdout)
        sys.stderr.write("exception: " + str(e_info[0]) + ": " + str(e_info[1]) + "\n")
        sys.stderr.flush()
        sys.exit(-1)
    else:
        sys.exit(0)

def test_connection():
    #
    # TESTING ONLY
    #
    setdebug(True)

    c = ssh("vcell-service.cam.uchc.edu","vcell")
    print ("done")



if __name__ == "__main__":
    #test_connection()
    main()
