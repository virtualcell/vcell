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


class Javaservice:
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
            dir, scriptname = os.path.split(os.path.abspath(self.script))
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("bash -l -c 'cd "+dir+" && ./" + scriptname + "'")
            if retcode != 0:
                raise RuntimeError(
                    "start failed: " + self.name + ": ret=" + str(retcode) + ", stdout='" + str(stdout) + "'")
            print ("started " + self.name + " (" + self.script + ") on " + self.host)
        finally:
            if connection is not None:
                connection.close()

    def stop(self):
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("ps -ef | grep java")
            if retcode != 0:
                raise RuntimeError(
                    "ps failed: " + self.name + ": ret=" + str(retcode) + ", stdout='" + str(stdout) + "'")
            for line in str(stdout).splitlines():
                if self.label in line:
                    processId = int(line.split()[1])
                    retcode, stdout = connection.sendCommand("kill " + str(processId))
                    if (retcode != 0):
                        raise RuntimeError(
                            "kill failed {0}, pid={1}, ret={2}, stdout=\'{3}\'".format(self.name, str(processId),
                                                                                       str(retcode), str(stdout)))
                    print (
                    "killed " + self.name + " pid=" + str(processId) + " label=" + self.label + " on " + self.host)
        finally:
            if connection is not None:
                connection.close()

    def status(self):
        connection = None
        try:
            connection = ssh(self.host, self.user)
            retcode, stdout = connection.sendCommand("ps -ef | grep java")
            if (retcode != 0):
                raise RuntimeError(
                    "status failed: " + self.name + ": ret=" + str(retcode) + ", stdout='" + str(stdout) + "'")
            for line in str(stdout).splitlines():
                if self.label in line:
                    process_id = int(line.split()[1])
                    print ("found " + self.name + " ('" + self.label + "') with process id " + str(
                        process_id) + " on " + self.host)
        finally:
            if connection is not None:
                connection.close()


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
        master_script = os.path.join(config_dir, "vcellservice")

        api_name = "api"
        api_host = getenv("vcellapi_host")
        api_processLabel = getenv("vcellapi_processLabel")
        api_script = os.path.join(config_dir, "vcellapi")

        jms_name = "jms"
        queues = "{0};{1};{2};{3};{4}".format(getenv("vcell_jms_queue_simReq"), getenv("vcell_jms_queue_dataReq"),
                                              getenv("vcell_jms_queue_dbreq"), getenv("vcell_jms_queue_simjob"),
                                              getenv("vcell_jms_queue_workervent"))
        topics = "{0};{1};{2}".format(getenv("vcell_jms_topic_servicecontrol"), getenv("vcell_jms_topic_daemoncontrol"),
                                      getenv("vcell_jms_topic_clientstatus"))
        jms_containername = getenv("vcell_jms_containername")
        jms_imagename = "webcenter/activemq:5.14.3"
        jms_logdir = getenv("vcell_jms_logdir")
        jms_datadir = getenv("vcell_jms_datadir")
        jms_url = getenv("vcell_jms_url")
        jms_host = getenv("vcell_jms_host")
        jms_port = getenv("vcell_jms_port")
        jms_webport = getenv("vcell_jms_webport")
        # jms_stomp_port = getenv("vcell_stompport")
        jms_env = dict()
        with open (getenv("vcell_jms_pswdfile"), "r") as jms_pswdfile:
           jms_pswd=jms_pswdfile.readline().rstrip()
        jms_env["ACTIVEMQ_ADMIN_PASSWORD"] = "'"+jms_pswd+"'"
        jms_env["ACTIVEMQ_WRITE_PASSWORD"] = "'"+jms_pswd+"'"
        jms_env["ACTIVEMQ_READ_PASSWORD"] = "'"+jms_pswd+"'"
        jms_env["ACTIVEMQ_JMX_PASSWORD"] = "'"+jms_pswd+"'"
        jms_env["ACTIVEMQ_NAME"] = 'amqp-srv1'
        jms_env["ACTIVEMQ_REMOVE_DEFAULT_ACCOUNT"] = 'true'
        jms_env["ACTIVEMQ_ADMIN_LOGIN"] = 'admin'
        jms_env["ACTIVEMQ_WRITE_LOGIN"] = "'"+getenv("vcell_jms_user")+"'"
        jms_env["ACTIVEMQ_READ_LOGIN"] = "'"+getenv("vcell_jms_user")+"'"
        jms_env["ACTIVEMQ_JMX_LOGIN"] = "'"+getenv("vcell_jms_user")+"'"
        jms_env["ACTIVEMQ_STATIC_TOPICS"] = "'"+topics+"'"
        jms_env["ACTIVEMQ_STATIC_QUEUES"] = "'"+queues+"'"
        jms_env["ACTIVEMQ_MIN_MEMORY"] = '1024'
        jms_env["ACTIVEMQ_MAX_MEMORY"] = '4096'
        jms_env["ACTIVEMQ_ENABLED_SCHEDULER"] = 'true'
        jms_volume_mappings = dict()
        # jms_volume_mappings[jms_datadir] = '/data/activemq'
        # jms_volume_mappings[jms_logdir] = '/var/log/activemq'
        jms_port_mappings = dict()
        jms_port_mappings[jms_webport] = '8161'
        jms_port_mappings[jms_port] = '61616'
        # jms_port_mappings[jms_stompport] = '61613'

        mongo_name = "mongodb"
        mongo_imagename = "mongo:3.4.10"
        mongo_containername = getenv("vcell_mongodb_containername")
        mongo_host = getenv("vcell_mongodb_host")
        mongo_port = getenv("vcell_mongodb_port")
        mongo_env = dict()
        mongo_volume_mappings = dict()
        mongo_port_mappings = dict()
        mongo_port_mappings[mongo_port] = '27017'

        services = [
            Javaservice(user, master_name, master_host, master_processLabel, master_script),
            Javaservice(user, api_name, api_host, api_processLabel, api_script),
            Dockerservice(user=user, name=jms_name, container_name=jms_containername, env=jms_env, host=jms_host,
                          ports=jms_port_mappings, volumes=jms_volume_mappings, image_name=jms_imagename),
            Dockerservice(user=user, name=mongo_name, container_name=mongo_containername, env=mongo_env, host=mongo_host,
                          ports=mongo_port_mappings, volumes=mongo_volume_mappings, image_name=mongo_imagename),
        ]
    except EnvironmentError as enverr:
        print "config error: ", enverr
        sys.exit(-1)

    parser = argparse.ArgumentParser()
    parser.add_argument("cmd", help="command", choices=["status", "stop", "start", "create", "destroy"])
    parser.add_argument("service", help="service",
                        choices=[master_name, api_name, jms_name, mongo_name, "all"])
    parser.add_argument("--debug", help="debug commands", action="store_true")
    parser.set_defaults(debug=False)
    args = parser.parse_args()

    try:
        setdebug(args.debug)

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
        elif (args.cmd == "create"):
            for s in services:
                if (args.service == "all") or (args.service == s.name):
                    if isinstance(s, Dockerservice):
                        s.create()
        elif (args.cmd == "destroy"):
            for s in services:
                if (args.service == "all") or (args.service == s.name):
                    if isinstance(s, Dockerservice):
                        s.destroy()

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
