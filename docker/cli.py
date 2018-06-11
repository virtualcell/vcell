#!/usr/bin/env python

import sys, os, paramiko, argparse, traceback, logging

# class slurm_job:
#     fields = {
#         "jobid": None,
#         "name": None,
#         "state": None,
#         "submit_time": None,
#         "start_time": None,
#         "exec_host": None,
#         "reason": None
#     }
#
# class vcell_job:
#     fields = {
#         "owner_userid": None,
#         "slurm_name": None,
#         "simulationKey": None,
#         "jobIndex": None,
#         "taskID": None,
#         "schedulerStatus": None,
#         "hasData": None,
#         "vcellServerID": None,
#         "queueDate": None,
#         "simexe_startDate": None,
#         "simexe_latestUpdateDate": None,
#         "computeHost": None,
#         "detailedStateMessage": None
#     }


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

def health(query, host_port):
    assert(isinstance(host_port,str))
    import requests
    r = requests.get('https://'+host_port+'/health', params=query.params())
    logger = logging.getLogger("paramiko")
    logger.debug(r.url)
    json = r.json()
    return json


def simquery(query, host_port):
    """
    :param bool debug: whether to show debug information
    :param str host_port: vcell api host:port separated by colon
    :returns list of dict:
    """
    assert(isinstance(host_port,str))

    import requests
    r = requests.get('https://'+host_port+'/admin/jobs', params=query.params())
    logger = logging.getLogger("paramiko")
    logger.debug(r.url)
    json = r.json()
    """
    [{ u'computeHost' = {unicode} u'shangrila14'
       u'detailedState' = {unicode} u'WORKEREVENT_PROGRESS'
       u'detailedStateMessage' = {unicode} u'0.270433'
       u'hasData' = {bool} True
       u'jobIndex' = {int} 0
       u'onwer_userkey' = {unicode} u'120840072'
       u'owner_userid' = {unicode} u'975464864'
       u'queueDate' = {unicode} u'May 10, 2018 6:40:20 AM'
       u'queueId' = {unicode} u'QUEUE_ID_NULL'
       u'queuePriority' = {int} 5
       u'schedulerStatus' = {unicode} u'RUNNING'
       u'simexe_latestUpdateDate' = {unicode} u'May 10, 2018 2:07:32 PM'
       u'simexe_startDate' = {unicode} u'May 10, 2018 6:41:27 AM'
       u'simulationKey' = {unicode} u'130250352'
       u'submitDate' = {unicode} u'May 10, 2018 6:40:20 AM'
       u'taskID' = {int} 0
       u'vcellServerID' = {unicode} u'REL'}
    ]
    """
    return json


def slurmquery(debug, slurmhost, partitions):
    """
    :param bool debug: whether to show debug information
    :param str slurmhost: slurm node for running queries
    :param str partitions: comma separated list of slurm partitions
    :returns list of dict:
    """
    assert(isinstance(debug,bool))
    assert(isinstance(slurmhost,str))
    assert(isinstance(partitions,str))

    import subprocess
    # if args.debug:
    #     print "sudo docker run --rm "+args.vcellbatch+" "+options+" SendErrorMsg"
    if debug:
        print "ssh " + slurmhost + " squeue -p " + partitions + " -O jobid:25,name:25,state:13,submittime,starttime,batchhost,reason"
    proc = subprocess.Popen(['ssh', slurmhost,
                          'squeue', '-p', partitions,
                          '-O', 'jobid:25,name:25,state:13,submittime,starttime,batchhost,reason' ],
                         stdout=subprocess.PIPE)
    # return_code = subprocess.call(
    #     "ssh " + slurmhost + " squeue -p " + partitions + " -O jobid:25,name:25,state:13,submittime,starttime,batchhost,reason",
    #     shell=True)
    stdout, stderr = proc.communicate()
    return parseslurm(stdout)


def parseslurm(squeue_output):
    assert(isinstance(squeue_output,str))
    """
    JOBID                    NAME                     STATE        SUBMIT_TIME         START_TIME          EXEC_HOST           REASON              
    418774                   V_REL_130234420_7_0      RUNNING      2018-05-09T23:34:36 2018-05-10T09:41:36 shangrila01         None                
    """
    table = [[str(i) for i in line.split()] for line in squeue_output.splitlines()]
    """
    [['JOBID', 'NAME', 'STATE', 'SUBMIT_TIME', 'START_TIME', 'EXEC_HOST', 'REASON'], 
     ['418774', 'V_REL_130234420_7_0', 'RUNNING', '2018-05-09T23:34:36', '2018-05-10T09:41:36', 'shangrila01', 'None']]
    """
    list_of_dict = [dict(zip(table[0], row)) for row in table[1:]]
    """
    [{'NAME': 'V_REL_130234420_7_0', 
      'START_TIME': '2018-05-10T09:41:36', 
      'REASON': 'None', 
      'JOBID': '418774', 
      'STATE': 'RUNNING', 
      'SUBMIT_TIME': '2018-05-09T23:34:36', 
      'EXEC_HOST': 'shangrila01'}
    ]
    """
    return list_of_dict

def merge_slurm_vcell(showjobs_ouptput, slurmjobs_output):
    merged_list = []
    for vcelljob in showjobs_ouptput:
        merged_job = vcelljob.copy()
        site = str(vcelljob['vcellServerID'])
        simKey = str(vcelljob['simulationKey'])
        jobIndex = str(vcelljob['jobIndex'])
        taskID = str(vcelljob['taskID'])
        slurm_jobname = 'V_' + site + '_' + simKey + '_' + jobIndex + '_' + taskID
        merged_job['vcSimID'] = slurm_jobname
        merged_job['found'] = 'vcell'
        matching_slurmjobs = filter(lambda slurmjob: slurmjob['NAME'] == slurm_jobname, slurmjobs_output)
        if matching_slurmjobs is not None and len(matching_slurmjobs)>0 and matching_slurmjobs[0] is not None:
            merged_job.update(matching_slurmjobs[0])
            merged_job['found'] += ',slurm'
        merged_list.append(merged_job)
    for slurmjob in slurmjobs_output:
        matching_vcelljob = filter(lambda merged_job: merged_job.get('vcSimID') == slurmjob['NAME'], merged_list)
        if matching_vcelljob is None or len(matching_vcelljob)==0:
            merged_job = slurmjob.copy()
            merged_job['found'] = 'slurm'
            merged_list.append(merged_job)

    return merged_list


class simjobquery:
    fields = {}

    def __init__(self):
        # type: () -> None
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

class healthquery:
    fields = {}

    def __init__(self):
        # type: () -> None
        self.fields = {
            "check" : "all",
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
    parser_showjobs.add_argument("--status", type=str, default='wqdr', 
        help='job status (default wqdr) a-all, w-waiting, q-queued, d-dispatched, r-running, c-completed, s-stopped')
    parser_showjobs.add_argument("--maxRows", type=int, default=500)
    parser_showjobs.add_argument("--host", type=str, default='vcellapi.cam.uchc.edu', help='host of server')
    parser_showjobs.add_argument("--apiport", type=int, default='443', help='port of api server')
    parser_showjobs.add_argument("--withslurm", type=bool, default=False)
    parser_showjobs.add_argument("--slurmhost", type=str, default="vcell-service.cam.uchc.edu")
    parser_showjobs.add_argument("--partition", type=str, default="vcell2,vcell")
    parser_showjobs.set_defaults(which='showjobs')

    parser_showjobs = subparsers.add_parser('logjobs', help='show simulation job logs (see logjobs --help)')
    parser_showjobs.add_argument("--userid", type=str, default=None)
    parser_showjobs.add_argument("--simId", type=int, default=None)
    parser_showjobs.add_argument("--jobId", type=int, default=None)
    parser_showjobs.add_argument("--taskId", type=int, default=None)
    parser_showjobs.add_argument("--host", type=str, default='vcellapi.cam.uchc.edu', help='host of server')
    parser_showjobs.add_argument("--mongoport", type=int, default='27017', help='port of mongodb service')
    parser_showjobs.set_defaults(which='logjobs')

    parser_killjobs = subparsers.add_parser('killjob', help='kill simulation job (see killjob --help)')
    parser_killjobs.add_argument("--userid", type=str, default=None)
    parser_killjobs.add_argument("--simId", type=int, default=None)
    parser_killjobs.add_argument("--jobId", type=int, default=0)
    parser_killjobs.add_argument("--taskId", type=int, default=0)
    parser_killjobs.add_argument("--host", type=str, default='vcellapi.cam.uchc.edu', help='host of server')
    parser_killjobs.add_argument("--msgport", type=int, default='8161', help='port of message server')
    parser_killjobs.add_argument("--vcellbatch", type=str, default=None) # or "schaff/vcell-batch:latest"
    parser_killjobs.set_defaults(which='killjob')

    parser_slurmjobs = subparsers.add_parser('slurmjobs', help='slurm running jobs (see slurmjobs --help)')
    parser_slurmjobs.add_argument("--slurmhost", type=str, default="vcell-service.cam.uchc.edu")
    parser_slurmjobs.add_argument("--partition", type=str, default="vcell2,vcell")
    parser_slurmjobs.set_defaults(which='slurmjobs')

    parser_health = subparsers.add_parser('health', help='site status (see health --help)')
    parser_health.add_argument("--host", type=str, default='vcellapi.cam.uchc.edu', help='host of server')
    parser_health.add_argument("--apiport", type=int, default='443', help='port of api server')
    parser_health.add_argument('--monitor', type=str, default='sle', help='monitor type (default sle) s-simulation, l-login, e-events')
    parser_health.set_defaults(which='health')

    parser.set_defaults(debug=False)
    #main_args = parser.parse_args(['showjobs','--userid','schaff'])
    args = parser.parse_args()
    try:
        setdebug(args.debug)
        if args.which == "logjobs":
            import pymongo
            import pprint
            client = pymongo.MongoClient("mongodb://"+args.host+":"+str(args.mongoport))
            db = client['test']
            collection = db['logging']
            query = {}
            if args.simId is not None:
                query["simId"] = str(args.simId)
            if args.jobId is not None:
                query["jobIndex"] = str(args.jobId)
            if args.taskId is not None:
                query["taskId"] = str(args.taskId)
            pprint.pprint(query)
            resultSet = collection.find(query)
            table = []
            col_names = ["computeHost", "destination", "simId", "jobIndex", "taskId", "serviceName", "simMessageMsg"]
            rowcount=0
            for record in resultSet:
                table.append([record.get(col_name,'') for col_name in col_names])
                rowcount+=1
                if rowcount<10:
                    pprint.pprint(record)

            from tabulate import tabulate
            print tabulate(table, headers=col_names)

        elif args.which == "showjobs":
            query = simjobquery()
            query.fields['userid'] = args.userid
            query.fields['simId'] = args.simId
            query.fields['jobId'] = args.jobId
            query.fields['taskId'] = args.taskId
            query.fields['waiting'] = True if ('w' in args.status or 'a' in args.status) else False
            query.fields['queued'] = True if ('q' in args.status or 'a' in args.status) else False
            query.fields['dispatched'] = True if ('d' in args.status or 'a' in args.status) else False
            query.fields['running'] = True if ('r' in args.status or 'a' in args.status) else False
            query.fields['completed'] = True if ('c' in args.status or 'a' in args.status) else False
            query.fields['failed'] = True if ('f' in args.status or 'a' in args.status) else False
            query.fields['stopped'] = True if ('s' in args.status or 'a' in args.status) else False
            query.fields['maxRows'] = args.maxRows
            apihostport = args.host+":"+str(args.apiport)
            json_response = simquery(query,apihostport)
            slurmjobs = slurmquery(args.debug, args.slurmhost, args.partition)
            merged_jobs = merge_slurm_vcell(json_response, slurmjobs)
            col_names = ["JOBID", "STATE", "NAME", "EXEC_HOST", "found", "owner_userid", "vcSimID", "schedulerStatus", "hasData",
                         "queueDate", "simexe_startDate", "simexe_latestUpdateDate",
                         "computeHost", "detailedStateMessage"]
            table = [[row.get(col_name,'') for col_name in col_names] for row in merged_jobs]
            from tabulate import tabulate
            print tabulate(table, headers=col_names)
            # for job in json_response:
            #     print(job)

        elif args.which == "slurmjobs":
            # 'NAME': 'V_REL_130234420_7_0',
            # 'START_TIME': '2018-05-10T09:41:36',
            # 'REASON': 'None',
            # 'JOBID': '418774',
            # 'STATE': 'RUNNING',
            # 'SUBMIT_TIME': '2018-05-09T23:34:36',
            # 'EXEC_HOST':
            slurmjobs = slurmquery(args.debug, args.slurmhost, args.partition)
            col_names = ["JOBID", "STATE", "NAME", "EXEC_HOST", "START_TIME", "SUBMIT_TIME", "REASON"]
            table = [[row.get(col_name, '') for col_name in col_names] for row in slurmjobs]
            from tabulate import tabulate
            print tabulate(table, headers=col_names)

        elif args.which == "killjob":
            import subprocess
            vcellbatch = args.vcellbatch
            if vcellbatch is None:
                p1 = subprocess.Popen(['sudo', 'docker', 'image', 'ls'], stdout=subprocess.PIPE)
                p2 = subprocess.Popen(['grep', 'vcell-batch'], stdin=p1.stdout, stdout=subprocess.PIPE)
                p3 = subprocess.Popen(['head', '-1'], stdin=p2.stdout, stdout=subprocess.PIPE)
                stdout, stderr = p3.communicate()
                parts = stdout.strip().split()
                if len(parts)<2:
                    raise Exception("didn't find local vcell-batch docker image, specify using --vcellbatch")
                vcellbatch = parts[0]+':'+parts[1]
                print 'found vcell-batch image: ' + vcellbatch
            options =  " --msg-userid admin"
            options += " --msg-password admin"
            options += " --msg-host "+args.host
            options += " --msg-port "+str(args.msgport)
            options += " --msg-job-userid "+args.userid
            options += " --msg-job-simkey "+str(args.simId)
            options += " --msg-job-jobindex "+str(args.jobId)
            options += " --msg-job-taskid "+str(args.taskId)
            options += " --msg-job-errmsg "+"killed"
            if args.debug:
                print "sudo docker run --rm "+vcellbatch+" "+options+" SendErrorMsg"
            return_code = subprocess.call("sudo docker run --rm "+vcellbatch+" "+options+" SendErrorMsg", shell=True)
            if return_code == 0:
                print "kill message sent"
            else:
                print "unable to send kill message"

        elif args.which == "health":
            apihostport = args.host+":"+str(args.apiport)
            query = healthquery()
            if 'e' in args.monitor:
                # {"timestamp_MS":1525270930943,"transactionId":5,"eventType":"LOGIN_SUCCESS","message":"login success (5)"}
                query.fields['check'] = 'all'
                json_response = health(query,apihostport)
                col_names = ["timestamp_MS", "transactionId", "eventType", "message"]
                table = [[row.get(col_name,'') for col_name in col_names] for row in json_response]
                from tabulate import tabulate
                print tabulate(table, headers=col_names)
            if 's' in args.monitor:
                # {"nagiosStatusName":"OK","nagiosStatusCode":0,"elapsedTime_MS":7400}
                query.fields['check'] = 'sim'
                json_response = health(query,apihostport)
                message = str(json_response.get('message',"''"))
                print 'Simulation(status=' + str(json_response.get('nagiosStatusName')) + \
                    ', elapsedTime=' + str(int(json_response.get('elapsedTime_MS'))/1000.0) + ' seconds' + \
                    ', message=' + message + ')'
            if 'l' in args.monitor:
                # {"nagiosStatusName":"OK","nagiosStatusCode":0,"elapsedTime_MS":7400}
                query.fields['check'] = 'login'
                json_response = health(query,apihostport)
                message = str(json_response.get('message',"''"))
                print 'Login(status=' + str(json_response.get('nagiosStatusName')) + \
                    ', elapsedTime=' + str(int(json_response.get('elapsedTime_MS'))/1000.0) + ' seconds' + \
                    ', message=' + message + ')'
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


if __name__ == "__main__":
    #test_connection()
    main()
