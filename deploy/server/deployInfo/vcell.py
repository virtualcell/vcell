#!/usr/bin/env python

import sys, os, paramiko, argparse, traceback

#
# SSH Helper Class
#

class ssh:
    client = None
    debug = False
    address = None

    def __init__(self, address, username):
        assert isinstance(address, str)
        assert isinstance(username, str)
        self.address = address
        if (ssh.debug):
            print("======== Connecting to "+address+" as user "+username+" ========")

        self.client = paramiko.client.SSHClient()
        self.client.set_missing_host_key_policy(paramiko.client.AutoAddPolicy())

        # look for an alternative DSA key file before trying default ~/.ssh/id_rsa
        schaff_key_path = os.path.join(os.path.expanduser("~"),".ssh","schaff_dsa")
        if os.path.isfile(schaff_key_path):
            k = paramiko.DSSKey.from_private_key_file(schaff_key_path)
            self.client.connect(address, username=username, pkey = k)
        else:
            # try ~/.ssh/id_rsa
            self.client.connect(address, username=username, look_for_keys=True)

    def sendCommand(self, command):
        if (self.client != None):
            if (ssh.debug):
                print("=== command: "+command)
            stdin, stdout, stderr = self.client.exec_command(command)
            alldata = ''
            while not stdout.channel.exit_status_ready():
                alldata += stdout.channel.recv(1024)
                while stdout.channel.recv_ready():
                    alldata += stdout.channel.recv(1024)

            exitStatus = stdout.channel.recv_exit_status()
            if (ssh.debug):
                print("=== retcode: "+str(exitStatus))
                print("=== stdout: "+str(alldata))
            return exitStatus, str(alldata)
        else:
            raise RuntimeError("Connection not opened")

    def close(self):
        if (self.client != None):
            self.client.close()
            if (ssh.debug):
                print("======== Closing connection to "+self.address+" ========")
                print("")

class vcellservice:
    host = None
    label = None
    name = None
    user = None
    script = None
    def __init__(self, user, name, host, label, script):
        assert isinstance(user, str)
        assert isinstance(name, str)
        assert isinstance(host, str)
        assert isinstance(label, str)
        assert isinstance(script, str)
        self.user = user
        self.name = name
        self.host = host
        self.label = label
        self.script = script

    def start(self):
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("bash -c '"+self.script+"'")
            if (retcode != 0):
                raise RuntimeError("start failed: "+service.name+": ret="+str(retcode)+", stdout='"+str(stdout)+"'")
            print ("started "+self.name+" ("+self.script+") on "+self.host)
        finally:
            if connection is not None: connection.close()

    def stop(self):
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("ps -ef | grep java")
            if (retcode != 0):
                raise RuntimeError("ps failed: "+self.name+": ret="+str(retcode)+", stdout='"+str(stdout)+"'")
            for line in str(stdout).splitlines():
                if (self.label in line):
                    processId = int(line.split()[1])
                    retcode, stdout = connection.sendCommand("kill "+str(processId))
                    if (retcode != 0):
                        raise RuntimeError("kill failed "+service.name+", pid="+str(processId)+", "
                                            "ret="+str(retcode)+", stdout='"+str(stdout)+"'")
                    print ("killed "+self.name+" pid="+str(processId)+" label="+self.label+" on "+self.host)
        finally:
            if connection is not None: connection.close()

    def status(self):
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("ps -ef | grep java")
            if (retcode != 0):
                raise RuntimeError("status failed: "+self.name+": ret="+str(retcode)+", stdout='"+str(stdout)+"'")
            for line in str(stdout).splitlines():
                if (self.label in line):
                    processId = int(line.split()[1])
                    print ("found "+self.name+" ('"+self.label+"') with process id "+str(processId)+" on "+self.host)
        finally:
            if connection is not None: connection.close()

class otherservice:
    host = None
    label = None
    name = None
    user = None
    script = None
    def __init__(self, user, name, host, label, script):
        assert isinstance(user, str)
        assert isinstance(name, str)
        assert isinstance(host, str)
        assert isinstance(label, str)
        assert isinstance(script, str)
        self.user = user
        self.name = name
        self.host = host
        self.label = label
        self.script = script

    def start(self):
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("bash -c '"+self.script+"'")
            if (retcode != 0):
                raise RuntimeError("start failed: "+service.name+": ret="+str(retcode)+", stdout='"+str(stdout)+"'")
            print ("started "+self.name+" ("+self.script+") on "+self.host)
        finally:
            if connection is not None: connection.close()

    def stop(self):
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("ps -ef | grep java")
            if (retcode != 0):
                raise RuntimeError("ps failed: "+self.name+": ret="+str(retcode)+", stdout='"+str(stdout)+"'")
            for line in str(stdout).splitlines():
                if (self.label in line):
                    processId = int(line.split()[1])
                    retcode, stdout = connection.sendCommand("kill "+str(processId))
                    if (retcode != 0):
                        raise RuntimeError("kill failed "+service.name+", pid="+str(processId)+", "
                                            "ret="+str(retcode)+", stdout='"+str(stdout)+"'")
                    print ("killed "+self.name+" pid="+str(processId)+" label="+self.label+" on "+self.host)
        finally:
            if connection is not None: connection.close()

    def status(self):
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("ps -ef | grep java")
            if (retcode != 0):
                raise RuntimeError("status failed: "+self.name+": ret="+str(retcode)+", stdout='"+str(stdout)+"'")
            for line in str(stdout).splitlines():
                if (self.label in line):
                    processId = int(line.split()[1])
                    print ("found "+self.name+" ('"+self.label+"') with process id "+str(processId)+" on "+self.host)
        finally:
            if connection is not None: connection.close()


def getenv(varname):
    value = os.environ.get(varname,None)
    if (value is None):
        raise EnvironmentError("undefined environment var '"+varname+"', hint: invoke using ./vcell script");
    assert isinstance(value, str)
    return value;


def main():

    try:
        #
        # gather server configuration from environment
        #
        user = getenv("common_vcell_user")
        config_dir = getenv("common_siteCfgDir")

        master_name = "master"
        master_host = getenv("vcellservice_host")
        master_processLabel = getenv("vcellservice_processLabel")
        master_script = os.path.join(config_dir,"vcellservice")

        rmihttp_name = "rmi-http"
        rmihttp_host = getenv("bootstrap_rmihost")
        rmihttp_processLabel = getenv("bootstrap_http_processLabel")
        rmihttp_script = os.path.join(config_dir,"vcellbootstrap_http")

        rmihigh_name = "rmi-high"
        rmihigh_host = getenv("bootstrap_rmihost")
        rmihigh_processLabel = getenv("bootstrap_high_processLabel")
        rmihigh_script = os.path.join(config_dir,"vcellbootstrap_high")

        api_name = "api"
        api_host = getenv("vcellapi_host")
        api_processLabel = getenv("vcellapi_processLabel")
        api_script = os.path.join(config_dir,"vcellapi")

        activemq_host = "code2.cam.uchc.edu"
        activemq_bindir = "/usr/local/apache-activemq-5.11.1/bin"
        activemq_startcmd = "./activemq start"
        activemq_stopcmd = "./activemq start"

        services = [
            vcellservice(user,master_name,master_host,master_processLabel,master_script),
            vcellservice(user,rmihttp_name,rmihttp_host,rmihttp_processLabel,rmihttp_script),
            vcellservice(user,rmihigh_name,rmihigh_host,rmihigh_processLabel,rmihigh_script),
            vcellservice(user,api_name,api_host,api_processLabel,api_script),
            ]
    except EnvironmentError as enverr:
        print "config error: ", enverr
        sys.exit(-1)

    parser = argparse.ArgumentParser()
    parser.add_argument("cmd", help="command", choices=["status", "stop", "start"])
    parser.add_argument("service",help="service",
                        choices=[master_name, rmihttp_name, rmihigh_name, api_name, "all"])
    parser.add_argument("--debug",help="debug commands",action="store_true")
    parser.set_defaults(debug=False)
    args = parser.parse_args()

    try:
        ssh.debug = args.debug
        if (args.cmd == "status"):
            for s in services:
                if (args.service == "all") or (args.service == s.name):
                    s.status()
        elif (args.cmd == "stop"):
            for s in services:
                if (args.service == "all") or (args.service == s.name):
                    s.stop()
        elif (args.cmd == "start"):
            for s in services:
                if (args.service == "all") or (args.service == s.name):
                    s.start()

    except paramiko.SSHException:
        print "SSHException: ", sys.exc_info()[0]
        sys.exit(-1)
    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0],e_info[1],e_info[2],file=sys.stdout)
        sys.stderr.write("exception: "+str(e_info[0])+": "+str(e_info[1])+"\n")
        sys.stderr.flush()
        sys.exit(-1)
    else:
        sys.exit(0)


if __name__ == "__main__":
    main()
