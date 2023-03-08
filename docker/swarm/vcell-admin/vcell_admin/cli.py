#!/usr/bin/env python

import logging
from pprint import pprint
from subprocess import call, PIPE, Popen
from typing import List, Dict
from typing import Optional

import typer
from pymongo import MongoClient
from pymongo.collection import Collection
from pymongo.cursor import Cursor
from pymongo.database import Database
from requests import get, Response
from tabulate import tabulate

app = typer.Typer()


class SimJobQuery:
    fields = {}

    def __init__(self):
        # type: () -> None
        self.fields = {
            "submitLowMS": None,
            "submitHighMS": None,
            "startLowMS": None,
            "startHighMS": None,
            "endLowMS": None,
            "endHighMS": None,
            "startRow": 1,
            "maxRows": 100,
            "serverId": None,
            "computeHost": None,
            "userid": None,
            "simId": None,
            "jobId": None,
            "taskId": None,
            "hasData": None,
            "waiting": None,
            "queued": None,
            "dispatched": None,
            "running": None,
            "completed": None,
            "failed": None,
            "stopped": None,
        }

    def params(self):
        return {k: v for k, v in self.fields.items() if v is not None}


class HealthQuery:
    fields = {}

    def __init__(self):
        # type: () -> None
        self.fields = {
            "check": "all",
        }

    def params(self):
        return {k: v for k, v in self.fields.items() if v is not None}


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


def set_debug(debug: bool) -> None:
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


def slurmquery(debug: bool, slurmhost: str, partitions: str):
    """
    :param bool debug: whether to show debug information
    :param str slurmhost: slurm node for running queries
    :param str partitions: comma separated list of slurm partitions
    :returns list of dict:
    """

    # if args.debug:
    #     print "sudo docker run --rm "+args.vcellbatch+" "+options+" SendErrorMsg"
    if debug:
        print(f"ssh {slurmhost} squeue -p {partitions}"
              f" -O jobid:25,name:25,state:13,submittime,starttime,batchhost,reason")
    proc = Popen(['ssh', slurmhost,
                  'squeue', '-p', partitions,
                  '-O', 'jobid:25,name:25,state:13,submittime,starttime,batchhost,reason'],
                 stdout=PIPE)

    stdout, stderr = proc.communicate()
    return parse_slurm(stdout.decode('utf-8'))


def parse_slurm(squeue_output: str) -> List[Dict]:
    """
    JOBID     NAME                     STATE        SUBMIT_TIME         START_TIME          EXEC_HOST           REASON
    418774    V_REL_130234420_7_0      RUNNING      2018-05-09T23:34:36 2018-05-10T09:41:36 shangrila01         None
    """
    table = [[str(i) for i in line.split()] for line in squeue_output.splitlines()]
    """
    [['JOBID', 'NAME', 'STATE', 'SUBMIT_TIME', 'START_TIME', 'EXEC_HOST', 'REASON'], 
     ['418774', 'V_REL_130234420_7_0', 'RUNNING', '2018-05-09T23:34:36', '2018-05-10T09:41:36', 'shangrila01', 'None']]
    """
    list_of_dict: List[Dict] = [dict(zip(table[0], row)) for row in table[1:]]
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
    for vcell_job in showjobs_ouptput:
        merged_job = vcell_job.copy()
        site = str(vcell_job['vcellServerID'])
        sim_key = str(vcell_job['simulationKey'])
        job_index = str(vcell_job['jobIndex'])
        task_id = str(vcell_job['taskID'])
        slurm_job_name = 'V_' + site + '_' + sim_key + '_' + job_index + '_' + task_id
        merged_job['vcSimID'] = slurm_job_name
        merged_job['found'] = 'vcell'
        matching_slurmjobs = list(filter(lambda slurmjob: slurmjob['NAME'] == slurm_job_name, slurmjobs_output))
        if matching_slurmjobs is not None and len(matching_slurmjobs) > 0 and matching_slurmjobs[0] is not None:
            merged_job.update(matching_slurmjobs[0])
            merged_job['found'] += ',slurm'
        merged_list.append(merged_job)
    for slurm_job in slurmjobs_output:
        matching_vcell_job = list(filter(lambda job: job.get('vcSimID') == slurm_job['NAME'], merged_list))
        if matching_vcell_job is None or len(matching_vcell_job) == 0:
            merged_job = slurm_job.copy()
            merged_job['found'] = 'slurm'
            merged_list.append(merged_job)

    return merged_list


def sim_query(query: SimJobQuery, host_port: str) -> List[Dict]:
    """
    :param SimJobQuery query: query parameters for /admin/jobs
    :param str host_port: vcell api host:port separated by colon
    :returns list of dict:
    """

    r = get('https://' + host_port + '/admin/jobs', params=query.params())
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


@app.command(name="showjobs", help="show simulation jobs (using vcell database and slurm)")
def showjobs_command(userid: Optional[str] = typer.Option(None, "--userid", "-u", help="job owner"),
                     sim_id: Optional[int] = typer.Option(None, "--simId", "-s", help="simulation id"),
                     job_id: Optional[int] = typer.Option(None, "--jobId", "-j",
                                                          help="sim job id (index for scans/trials)"),
                     task_id: Optional[int] = typer.Option(None, "--taskId", "-t",
                                                           help="sim task id (for job restarts)"),
                     status: str = typer.Option("wqdr", help="job status (default wqdr) "
                                                             "a-all, w-waiting, q-queued, d-dispatched, "
                                                             "r-running, c-completed, s-stopped"),
                     max_rows: int = typer.Option(500, "--maxRows", "-m"),
                     host: str = typer.Option("vcellapi.cam.uchc.edu", "--host", help='host of server'),
                     api_port: int = typer.Option(443, "--apiport", help='port of api service'),
                     slurm_host: str = typer.Option("vcell-service.cam.uchc.edu", "--slurmhost"),
                     partition: str = typer.Option("vcell2,vcell", help="slurm partition"),
                     debug: bool = typer.Option(False, is_flag=True)
                     ) -> None:

    set_debug(debug)

    query = SimJobQuery()
    query.fields['userid'] = userid
    query.fields['simId'] = sim_id
    query.fields['jobId'] = job_id
    query.fields['taskId'] = task_id
    query.fields['waiting'] = True if ('w' in status or 'a' in status) else False
    query.fields['queued'] = True if ('q' in status or 'a' in status) else False
    query.fields['dispatched'] = True if ('d' in status or 'a' in status) else False
    query.fields['running'] = True if ('r' in status or 'a' in status) else False
    query.fields['completed'] = True if ('c' in status or 'a' in status) else False
    query.fields['failed'] = True if ('f' in status or 'a' in status) else False
    query.fields['stopped'] = True if ('s' in status or 'a' in status) else False
    query.fields['maxRows'] = max_rows
    api_host_port = host + ":" + str(api_port)
    json_response = sim_query(query, api_host_port)
    slurm_jobs = slurmquery(debug, slurm_host, partition)
    merged_jobs = merge_slurm_vcell(json_response, slurm_jobs)
    col_names = ["JOBID", "STATE", "NAME", "EXEC_HOST", "found", "owner_userid", "vcSimID", "schedulerStatus",
                 "hasData",
                 "queueDate", "simexe_startDate", "simexe_latestUpdateDate",
                 "computeHost", "detailedStateMessage"]
    table = [[row.get(col_name, '') for col_name in col_names] for row in merged_jobs]
    print(tabulate(table, headers=col_names))


@app.command(name="slurmjobs", help="query slurm running jobs")
def slurmjobs_command(slurm_host: str = typer.Option("vcell-service.cam.uchc.edu", "--slurmhost", help="slurm host"),
                      slurm_partition: str = typer.Option("vcell2,vcell", "--partition", "-p", help="slurm partition"),
                      debug: bool = typer.Option(False, is_flag=True)) -> None:

    set_debug(debug)
    # 'NAME': 'V_REL_130234420_7_0',
    # 'START_TIME': '2018-05-10T09:41:36',
    # 'REASON': 'None',
    # 'JOBID': '418774',
    # 'STATE': 'RUNNING',
    # 'SUBMIT_TIME': '2018-05-09T23:34:36',
    # 'EXEC_HOST':
    slurm_jobs: List[Dict] = slurmquery(debug, slurm_host, slurm_partition)
    col_names = ["JOBID", "STATE", "NAME", "EXEC_HOST", "START_TIME", "SUBMIT_TIME", "REASON"]
    table = [[row.get(col_name, '') for col_name in col_names] for row in slurm_jobs]
    print(tabulate(table, headers=col_names))


@app.command(name="logjobs", help="show simulation job logs (from MongoDB)")
def logjobs_command(sim_id: Optional[int] = typer.Option(None, "--simId", "-s", help="simulation id"),
                    job_id: Optional[int] = typer.Option(None, "--jobId", "-j",
                                                         help="sim job id (index for scans/trials)"),
                    task_id: Optional[int] = typer.Option(None, "--taskId", "-t",
                                                          help="sim task id (for job restarts)"),
                    host: str = typer.Option("vcellapi.cam.uchc.edu", "--host", help='host of server'),
                    mongo_port: int = typer.Option(27017, "--mongoport", help='port of mongodb service'),
                    max_rows: int = typer.Option(1000, "--maxRows", "-m", help="max records returned from Mongo"),
                    debug: bool = typer.Option(False, is_flag=True)):
    print("Mongo logs are gone: re-implement as ElasticSearch query (move this to vcell-su for ES credentials")
#
#     set_debug(debug)
#     client = MongoClient("mongodb://" + host + ":" + str(mongo_port))
#     db: Database = client['test']
#     collection: Collection = db['logging']
#     query = {}
#     if sim_id is not None:
#         query["simId"] = str(sim_id)
#     if job_id is not None:
#         query["jobIndex"] = str(job_id)
#     if task_id is not None:
#         query["taskId"] = str(task_id)
#     pprint(query)
#     result_set: Cursor = collection.find(query).limit(max_rows)
#     table = []
#     col_names = ["computeHost", "destination", "simId", "jobIndex", "taskId", "serviceName", "simMessageMsg"]
#     rowcount = 0
#     for record in result_set:
#         table.append([record.get(col_name, '') for col_name in col_names])
#         rowcount += 1
#         if rowcount < 10 and debug:
#             pprint(record)
#
#     print(tabulate(table, headers=col_names))


@app.command(name="killjobs", help="kill simulation job (from a vcell-batch container)")
def killjobs_command(userid: Optional[str] = typer.Option(None, "--userid", "-u", help="job owner"),
                     sim_id: Optional[int] = typer.Option(None, "--simId", "-s", help="simulation id"),
                     job_id: Optional[int] = typer.Option(None, "--jobId", "-j",
                                                          help="sim job id (index for scans/trials)"),
                     task_id: Optional[int] = typer.Option(None, "--taskId", "-t",
                                                           help="sim task id (for job restarts)"),
                     host: str = typer.Option("vcellapi.cam.uchc.edu", "--host", help='host of server'),
                     msg_port: int = typer.Option(8161, "--msgport", help='port of message server'),
                     vcell_batch: Optional[str] = typer.Option(None, "--vcellbatch"),  # or "schaff/vcell-batch:latest"
                     debug: bool = typer.Option(False, is_flag=True)
                     ) -> None:

    set_debug(debug)

    if vcell_batch is None:
        p1 = Popen(['sudo', 'docker', 'image', 'ls'], stdout=PIPE)
        p2 = Popen(['grep', 'vcell-batch'], stdin=p1.stdout, stdout=PIPE)
        p3 = Popen(['head', '-1'], stdin=p2.stdout, stdout=PIPE)
        stdout, stderr = p3.communicate()
        parts = stdout.decode('utf-8').strip().split()
        if len(parts) < 2:
            raise Exception("didn't find local vcell-batch docker image, specify using --vcellbatch")
        vcell_batch = parts[0] + ':' + parts[1]
        print('found vcell-batch image: ' + vcell_batch)
    options = " --msg-userid admin"
    options += " --msg-password admin"
    options += " --msg-host " + host
    options += " --msg-port " + str(msg_port)
    options += " --msg-job-userid " + userid
    options += " --msg-job-simkey " + str(sim_id)
    options += " --msg-job-jobindex " + str(job_id)
    options += " --msg-job-taskid " + str(task_id)
    options += " --msg-job-errmsg " + "killed"
    if debug:
        print("sudo docker run --rm " + vcell_batch + " " + options + " SendErrorMsg")
    return_code = call("sudo docker run --rm " + vcell_batch + " " + options + " SendErrorMsg", shell=True)
    if return_code == 0:
        print("kill message sent")
    else:
        print("unable to send kill message")


def health_query(query: HealthQuery, host_port: str):
    r: Response = get('https://' + host_port + '/health', params=query.params())
    logger = logging.getLogger("paramiko")
    logger.debug(r.url)
    json = r.json()
    return json


@app.command(name="health", help="site status - as formerly queried by Nagios")
def health_command(host: str = typer.Option("vcellapi.cam.uchc.edu", "--host", help="host of server"),
                   api_port: int = typer.Option(443, "--apiport", help="port of api server"),
                   monitor: str = typer.Option("sle", "--monitor",
                                               help="monitor type (default 'sle') s-simulation, l-login, e-events")
                   ) -> None:

    apihostport = host + ":" + str(api_port)
    query = HealthQuery()
    if 'e' in monitor:
        # {"timestamp_MS":1525270930943,"transactionId":5,
        # "eventType":"LOGIN_SUCCESS","message":"login success (5)"}
        query.fields['check'] = 'all'
        json_response = health_query(query, apihostport)
        col_names = ["timestamp_MS", "transactionId", "eventType", "message"]
        table = [[row.get(col_name, '') for col_name in col_names] for row in json_response]
        print(tabulate(table, headers=col_names))
    if 's' in monitor:
        # {"nagiosStatusName":"OK","nagiosStatusCode":0,"elapsedTime_MS":7400}
        query.fields['check'] = 'sim'
        json_response = health_query(query, apihostport)
        message = str(json_response.get('message', "''"))
        print('Simulation(status=' + str(json_response.get('nagiosStatusName')) +
              ', elapsedTime=' + str(int(json_response.get('elapsedTime_MS')) / 1000.0) + ' seconds' +
              ', message=' + message + ')')
    if 'l' in monitor:
        # {"nagiosStatusName":"OK","nagiosStatusCode":0,"elapsedTime_MS":7400}
        query.fields['check'] = 'login'
        json_response = health_query(query, apihostport)
        message = str(json_response.get('message', "''"))
        print('Login(status=' + str(json_response.get('nagiosStatusName')) +
              ', elapsedTime=' + str(int(json_response.get('elapsedTime_MS')) / 1000.0) + ' seconds' +
              ', message=' + message + ')')


def main():
    app()


if __name__ == "__main__":
    main()
