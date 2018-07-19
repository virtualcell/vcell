# vcell project servers

## production Docker Swarm Cluster (RELEASE)

```bash
[vcell@vcell-node1 /opt/build/vcell/docker/build  38] $ sudo docker node ls
ID                            HOSTNAME                   STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
0x290pfnvtq8gf9a8uzlih8jb *   vcell-node1.cam.uchc.edu   Ready               Active              Reachable           18.03.0-ce
pznhvvcqu89xzao84jud7ci9i     vcell-node2.cam.uchc.edu   Ready               Active              Leader              18.03.0-ce
y6bshmo0nrkm6skwk1d5k63a0     vcellapi.cam.uchc.edu      Ready               Active              Reachable           18.03.0-ce
```

## development Docker Swarm Cluster (ALPHA)

```bash
[vcell@vcell-node3 ~  1] $ sudo docker node ls
ID                            HOSTNAME                     STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
q3ghtrdt0qnz8i3gb56y4zbue *   vcell-node3.cam.uchc.edu     Ready               Active              Leader              18.03.0-ce
nmym8qspowiu1d850ok9vwtk2     vcell-node4.cam.uchc.edu     Ready               Active              Reachable           18.03.0-ce
kn3y3t1rw8skyua80j841ohng     vcellapi-beta.cam.uchc.edu   Ready               Active              Reachable           18.03.0-ce
```

## database machine vcell-db.cam.uchc.edu

## SLURM submit node vcell-service.cam.uchc.edu
Slurm partition "vcell2"
sbatch /share/apps/vcell3/htclogs/V_REL_39393939393_0_0.slurm.sub
squeue
sinfo
sdelete

Xanadu01 ...
Shangrila-01 ...


# Important Shared Directories
/share/apps/vcell3/htdocs
/share/apps/vcell3/htclogs
/share/apps/vcell3/users

# Important local directory on Docker Swarm nodes

## deploy directory (on DMZ machines ... vcellapi.cam.uchc.edu for RELEASE or vcellapi-beta.cam.uchc.edu for ALPHA).
/usr/local/deploy/config which stores the deployment configurations.

## local secrets directory /usr/local/deploy which holds clear text passwords and certificates and Java JRE images (.install4j subdir).




