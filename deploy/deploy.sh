#!/usr/bin/env bash

shopt -s -o nounset

if [ "$#" -ne 1 ]; then
    echo "usage:  deploy.sh includefile"
    exit -1
fi

includefile=$1
. $includefile

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

projectRootDir=$DIR/..
targetRootDir=$projectRootDir/target
targetMavenJarsDir=$targetRootDir/maven-jars

deployRootDir=$DIR

deployInstall4jDir=$deployRootDir/client/install4j

install4jWorkingDir=$DIR/../target/install4j-working
install4jDeploySettings=$install4jWorkingDir/DeploySettings.include

stagingRootDir=$DIR/../target/server-staging
stagingConfigsDir=$stagingRootDir/configs
stagingJarsDir=$stagingRootDir/jars
stagingVisToolDir=$stagingRootDir/visTool
stagingNativelibsDir=$stagingRootDir/nativelibs

installed_server_sitedir=$vcell_server_sitedir
installedConfigsDir=$installed_server_sitedir/configs
installedJarsDir=$installed_server_sitedir/jars
installedNativelibsDir=$installed_server_sitedir/nativelibs
installedVisToolDir=$installed_server_sitedir/visTool
installedSolversDir=$installed_server_sitedir/solvers
installedTmpDir=$installed_server_sitedir/tmp
installedLogDir=$installed_server_sitedir/log
installedJmsBlobFilesDir=$installed_server_sitedir/blobFiles
installedHtclogsDir=$installed_server_sitedir/htclogs
installedJavaprefsDir=$installed_server_sitedir/javaprefs
installedSystemPrefsDir=$installed_server_sitedir/javaprefs/.systemPrefs
installedPrimarydataDir=$vcell_primary_datadir
installedSecondarydataDir=$vcell_secondary_datadir
installedParalleldataDir=$vcell_parallel_datadir
installedExportDir=$vcell_export_dir
installedExportUrl=$vcell_export_url
installedMpichHomedir=$vcell_mpich_homedir

pathto_server_sitedir=$vcell_pathto_sitedir
pathto_ConfigsDir=$pathto_server_sitedir/configs
pathto_JarsDir=$pathto_server_sitedir/jars
pathto_NativelibsDir=$pathto_server_sitedir/nativelibs
pathto_VisToolDir=$pathto_server_sitedir/visTool
pathto_SolversDir=$pathto_server_sitedir/solvers
pathto_TmpDir=$pathto_server_sitedir/tmp
pathto_LogDir=$pathto_server_sitedir/log
pathto_JmsBlobFilesDir=$pathto_server_sitedir/blobFiles
pathto_HtclogsDir=$pathto_server_sitedir/htclogs
pathto_JavaprefsDir=$pathto_server_sitedir/javaprefs
pathto_SystemPrefsDir=$pathto_server_sitedir/javaprefs/.systemPrefs
#pathto_PrimarydataDir=$vcell_primary_datadir
#pathto_SecondarydataDir=$vcell_secondary_datadir
#pathto_ParalleldataDir=$vcell_parallel_datadir
#pathto_ExportDir=$vcell_export_dir
#pathto_ExportUrl=$vcell_export_url
#pathto_MpichHomedir=$vcell_mpich_homedir


installedVisitExe=/share/apps/vcell2/visit/visit2.9/visit2_9_0.linux-x86_64/bin/visit
installedPython=/share/apps/vcell2/vtk/usr/bin/vcellvtkpython

#--------------------------------------------------------------
# build maven
#--------------------------------------------------------------
cd $projectRootDir
#mvn verify
#if [ $? -ne 0 ]; then
#	echo "maven build failed"
#	exit -1
#fi
mvn dependency:copy-dependencies
if [ $? -ne 0 ]; then
	echo "failed to fetch jar files into target/maven-jars"
	exit -1
fi

#---------------------------------------------------------------
# build install4j platform specific installers for VCell client
# 
# cd to install4j directory which contains the Vagrant box 
# definition and scripts for building install4J installers 
# for VCell client.
#---------------------------------------------------------------
mkdir -p $install4jWorkingDir
if [ -e $install4jDeploySettings ]; then
	rm $install4jDeploySettings
fi
touch $install4jDeploySettings
echo "compiler_softwareVersionString=Test4_Version_6.2_build_54"	>> $install4jDeploySettings
echo "compiler_Site=$vcell_site_camel"								>> $install4jDeploySettings
echo "compiler_vcellVersion=$vcell_version"							>> $install4jDeploySettings
echo "compiler_vcellBuild=$vcell_build"								>> $install4jDeploySettings
echo "compiler_rmiHosts=$vcell_rmihosts"							>> $install4jDeploySettings
echo "compiler_bioformatsJarFile=$vcell_bioformatsJarFile"			>> $install4jDeploySettings
echo "compiler_bioformatsJarDownloadURL=$vcell_bioformatsJarDownloadURL" >> $install4jDeploySettings
echo "compiler_vcellAllJarFileSourcePath=$vcell_vcellAllJarFileSourcePath" >> $install4jDeploySettings
echo "compiler_applicationId=$vcell_applicationId"					>> $install4jDeploySettings

cd $deployInstall4jDir
./build.sh
if [ $? -eq 0 ]; then
	echo "client-installers built"
else
	echo "client-installer build failed"
	exit -1;
fi
cd $DIR

#-------------------------------------------------------
# build server-staging area
#-------------------------------------------------------
#
# build stagingDir/configs
#
mkdir -p $stagingRootDir
mkdir -p $stagingConfigsDir
mkdir -p $stagingJarsDir
mkdir -p $stagingVisToolDir
mkdir -p $stagingNativelibsDir

cp -p $targetMavenJarsDir/*.jar $stagingJarsDir
cp -p $targetRootDir/$vcell_vcellAllJarFileName $stagingJarsDir
cp -p $projectRootDir/nativelibs/linux64/* $stagingNativelibsDir
cp -p -R $projectRootDir/visTool/* $stagingVisToolDir

#
# build stagingDir/configs
#
cp server/deployInfo/* $stagingConfigsDir

#
# substitute values within vcell.include template from 
# 
stagingVCellInclude=$stagingConfigsDir/vcell.include
sed -i "" "s/GENERATED-SITE/$vcell_site_lower/g"						$stagingVCellInclude
sed -i "" "s/GENERATED-RMIHOST/$vcell_rmihost/g"						$stagingVCellInclude
sed -i "" "s/GENERATED-RMIPORT-LOW/$vcell_rmiport_low/g"				$stagingVCellInclude
sed -i "" "s/GENERATED-RMIPORT-HIGH/$vcell_rmiport_high/g"				$stagingVCellInclude
sed -i "" "s/GENERATED-VCELLSERVICE-JMXPORT/$vcell_vcellservice_jmxport/g"	$stagingVCellInclude
sed -i "" "s/GENERATED-RMISERVICEHIGH-JMXPORT/$vcell_rmiservice_high_jmxport/g"	$stagingVCellInclude
sed -i "" "s/GENERATED-RMISERVICEHTTP-JMXPORT/$vcell_rmiservice_http_jmxport/g"	$stagingVCellInclude
sed -i "" "s/GENERATED-SERVICEHOST/$vcell_servicehost/g"				$stagingVCellInclude
sed -i "" "s/GENERATED-NAGIOSPW/nagcmd/g" 								$stagingVCellInclude
sed -i "" "s/GENERATED-MONITOR-PORT/33336/g"							$stagingVCellInclude
sed -i "" "s/GENERATED-JVMDEF/test.monitor.port/g"						$stagingVCellInclude
sed -i "" "s+GENERATED-COMMON-JRE+$vcell_common_jre+g"					$stagingVCellInclude
sed -i "" "s+GENERATED-RMISERVICE-JRE+$vcell_common_jre_rmi+g"			$stagingVCellInclude
sed -i "" "s+GENERATED-NATIVELIBSDIR+$installedNativelibsDir+g"			$stagingVCellInclude
sed -i "" "s+GENERATED-JARSDIR+$installedJarsDir+g"						$stagingVCellInclude
sed -i "" "s+GENERATED-LOGDIR+$installedLogDir+g"						$stagingVCellInclude
sed -i "" "s+GENERATED-CONFIGSDIR+$installedConfigsDir+g"				$stagingVCellInclude
sed -i "" "s+GENERATED-TMPDIR+$installedTmpDir+g"						$stagingVCellInclude
sed -i "" "s+GENERATED-JAVAPREFSDIR+$installedJavaprefsDir+g"			$stagingVCellInclude
sed -i "" "s+GENERATED-JARS+$installedJarsDir/*+g"						$stagingVCellInclude

sed -i "" "s/GENERATED-HTC-USESSH/$vcell_htc_usessh/g"					$stagingVCellInclude
if [ "$vcell_htc_usessh" = true ]; then
	sed -i "" "s/GENERATED-HTC-SSH-HOST/$vcell_htc_sshhost/g"			$stagingVCellInclude
	sed -i "" "s/GENERATED-HTC-SSH-USER/$vcell_htc_sshuser/g"			$stagingVCellInclude
	sed -i "" "s+GENERATED-HTC-SSH-DSAKEYFILE+$vcell_htc_sshDsaKeyFile+g"	$stagingVCellInclude
else
	sed -i "" "s/GENERATED-HTC-SSH-HOST/NOT-DEFINED/g"					$stagingVCellInclude
	sed -i "" "s/GENERATED-HTC-SSH-USER/NOT-DEFINED/g"					$stagingVCellInclude
	sed -i "" "s/GENERATED-HTC-SSH-DSAKEYFILE/NOT-DEFINED/g"			$stagingVCellInclude
fi

if grep -Fq "GENERATED" $stagingVCellInclude
then
    echo "failed to replace all GENERATED tokens in $stagingVCellInclude"
    grep "GENERATED" $stagingVCellInclude
    exit -1
fi

#
# build generated vcell64.properties file - VCell System properties read by PropertyLoader
#
propfile=$stagingConfigsDir/vcell64.properties
if [ -e $propfile ]; then
	rm $propfile
fi
touch $propfile

echo "vcell.server.id = $vcell_site_upper" 									>> $propfile
echo "vcell.softwareVersion = $vcell_softwareVersionString" 				>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#JMS Info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.jms.provider = ActiveMQ"										>> $propfile
echo "vcell.jms.url = $vcell_jms_url" 										>> $propfile
echo "vcell.jms.user = $vcell_jms_user"										>> $propfile
echo "vcell.jms.password = $vcell_jms_pswd"									>> $propfile
echo "vcell.jms.queue.simReq = simReq$vcell_site_camel"						>> $propfile
echo "vcell.jms.queue.dataReq = simDataReq$vcell_site_camel"				>> $propfile
echo "vcell.jms.queue.dbReq = dbReq$vcell_site_camel"						>> $propfile
echo "vcell.jms.queue.simJob = simJob$vcell_site_camel"						>> $propfile
echo "vcell.jms.queue.workerEvent = workerEvent$vcell_site_camel"			>> $propfile
echo "vcell.jms.topic.serviceControl = serviceControl$vcell_site_camel"		>> $propfile
echo "vcell.jms.topic.daemonControl = daemonControl$vcell_site_camel"		>> $propfile
echo "vcell.jms.topic.clientStatus = clientStatus$vcell_site_camel"			>> $propfile
echo "vcell.jms.blobMessageMinSize = 100000"								>> $propfile
echo "vcell.jms.blobMessageTempDir = $installedJmsBlobFilesDir"				>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Oracle Database Info"												>> $propfile
echo "#"																	>> $propfile
echo "vcell.server.dbConnectURL = $vcell_database_url"						>> $propfile
echo "vcell.server.dbDriverName = $vcell_database_driver"					>> $propfile
echo "vcell.server.dbUserid = $vcell_database_user"							>> $propfile
echo "vcell.server.dbPassword = $vcell_database_pswd"						>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Amplistor Info"														>> $propfile
echo "#"																	>> $propfile
echo "vcell.amplistor.vcellserviceurl = $vcell_amplistor_url"				>> $propfile
echo "vcell.amplistor.vcellservice.user = $vcell_amplistor_user"			>> $propfile
echo "vcell.amplistor.vcellservice.password = $vcell_amplistor_pswd"		>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Mongo Info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.mongodb.host = $vcell_mongodb_host"								>> $propfile
echo "vcell.mongodb.port = $vcell_mongodb_port"								>> $propfile
echo "vcell.mongodb.database = $vcell_mongodb_database"						>> $propfile
echo "vcell.mongodb.loggingCollection = $vcell_mongodb_collection"			>> $propfile
echo "vcell.mongodb.threadSleepMS = 10000"									>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Mail Server Info (lost password func)"								>> $propfile
echo "#"																	>> $propfile
echo "vcell.smtp.hostName = $vcell_smtp_host"								>> $propfile
echo "vcell.smtp.port = $vcell_smtp_port"									>> $propfile
echo "vcell.smtp.emailAddress = $vcell_smtp_email"							>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Visit Info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.visit.smoldynscript = $installedConfigsDir/convertSmoldyn.py"	>> $propfile
echo "vcell.visit.smoldynvisitexecutable = $installedVisitExe"				>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Finite Volume Standalone"											>> $propfile
echo "#"																	>> $propfile
echo "vcell.finitevolume.executable = $installedSolversDir/FiniteVolume_x64"	>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Chombo Refined Grids PDE Solver"									>> $propfile
echo "#"																	>> $propfile
echo "vcell.chombo.executable.2d = $installedSolversDir/VCellChombo2D_x64"		>> $propfile
echo "vcell.chombo.executable.3d = $installedSolversDir/VCellChombo3D_x64"		>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# stiff ODE solver library"											>> $propfile
echo "#"																	>> $propfile
echo "vcell.sundialsSolver.executable = $installedSolversDir/SundialsSolverStandalone_x64"	>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# java simulation executable"											>> $propfile
echo "#"																	>> $propfile
echo "vcell.javaSimulation.executable = $installedConfigsDir/JavaSimExe64"	>> $propfile
echo "vcell.simulation.preprocessor = $installedConfigsDir/JavaPreprocessor64"	>> $propfile
echo "vcell.simulation.postprocessor = $installedConfigsDir/JavaPostprocessor64"	>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# stochastic solver"													>> $propfile
echo "#"																	>> $propfile
echo "vcell.stoch.executable = $installedSolversDir/VCellStoch_x64"		>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# hybrid stochastic solvers"											>> $propfile
echo "#"																	>> $propfile
echo "vcell.hybridEM.executable = $installedSolversDir/Hybrid_EM_x64"		>> $propfile
echo "vcell.hybridMil.executable = $installedSolversDir/Hybrid_MIL_x64"	>> $propfile
echo "vcell.hybridMilAdaptive.executable = $installedSolversDir/Hybrid_MIL_Adaptive_x64"	>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# smoldyn spatial stochastic solver"									>> $propfile
echo "#"																	>> $propfile
echo "vcell.smoldyn.executable = $installedSolversDir/smoldyn_x64"			>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# NFSim"																>> $propfile
echo "#"																	>> $propfile
echo "vcell.nfsim.executable = $installedSolversDir/NFsim_x64"				>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# MovingBoundary executable"											>> $propfile
echo "#"																	>> $propfile
echo "vcell.mb.executable = $installedSolversDir/MovingBoundary_x64"		>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Server configuration"												>> $propfile
echo "#"																	>> $propfile
echo "vcell.primarySimdatadir = $installedPrimarydataDir"					>> $propfile
echo "vcell.secondarySimdatadir = $installedSecondarydataDir"				>> $propfile
echo "vcell.parallelDatadir = $installedParalleldataDir"					>> $propfile
echo "vcell.databaseThreads = 5"											>> $propfile
echo "vcell.exportdataThreads = 3"											>> $propfile
echo "vcell.simdataThreads = 5"												>> $propfile
echo "vcell.htcworkerThreads = 10"											>> $propfile
echo "vcell.export.baseURL = $installedExportUrl"							>> $propfile
echo "vcell.export.baseDir = $installedExportDir/"							>> $propfile
echo "vcell.databaseCacheSize = 50000000"									>> $propfile
echo "vcell.simdataCacheSize = 200000000"									>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Limits"																>> $propfile
echo "#"																	>> $propfile
echo "vcell.limit.jobMemoryMB = 20000"										>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Quota info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.server.maxOdeJobsPerUser = 20"									>> $propfile
echo "vcell.server.maxPdeJobsPerUser = 20"									>> $propfile
echo "vcell.server.maxJobsPerScan = 100"									>> $propfile
echo "vcell.server.maxJobsPerSite = 300"									>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# HTC info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.htc.logdir = $installedHtclogsDir/"								>> $propfile
echo "vcell.htc.jobMemoryOverheadMB = 70"									>> $propfile
echo "vcell.htc.user = vcell"												>> $propfile
echo "vcell.htc.queue ="													>> $propfile
echo "#vcell.htc.pbs.home ="												>> $propfile
echo "#vcell.htc.sge.home ="												>> $propfile
echo "#vcell.htc.sgeModulePath ="											>> $propfile
echo "#vcell.htc.pbsModulePath ="											>> $propfile
echo "vcell.htc.mpi.home = $vcell_mpich_homedir/"							>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# uncomment to specify which PBS or SGE submission queue to use."		>> $propfile
echo "# when not specified, the default queue is used."						>> $propfile
echo "#vcell.htc.queue = myQueueName"										>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# uncomment to change the Simulation Job Timeout."					>> $propfile
echo "# useful if restart takes a long time, units are in milliseconds"		>> $propfile
echo "#"																	>> $propfile
echo "# here 600000 = 60 * 1000 * 10 = 10 minutes"							>> $propfile
echo "# the default hard-coded in "											>> $propfile
echo "# MessageConstants.INTERVAL_SIMULATIONJOBSTATUS_TIMEOUT_MS"			>> $propfile
echo "#"																	>> $propfile
echo "# vcell.htc.htcSimulationJobStatusTimeout=600000"						>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Client Timeout in milliseconds"										>> $propfile
echo "#"																	>> $propfile
echo "vcell.client.timeoutMS = 600000"										>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# vtk python"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.vtkPythonExecutablePath = $installedPython"						>> $propfile
echo "vcell.visToolPath = $installedVisToolDir"								>> $propfile
echo "##set if needed to pick up python modules"							>> $propfile
echo "#vcell.vtkPythonModulePath ="											>> $propfile

if [ "$vcell_server_sitedir" == "vcell_pathto_sitedir" ]
then
	mkdir -p $installedSolversDir
	mkdir -p $installedConfigsDir
	mkdir -p $installedVisToolDir
	mkdir -p $installedJarsDir
	mkdir -p $installedNativelibsDir
	mkdir -p $installedHtclogsDir
	mkdir -p $installedJmsBlobFilesDir
	mkdir -p $installedLogDir
	mkdir -p $installedTmpDir
	mkdir -p $installedJavaprefsDir
	mkdir -p $installedSystemPrefsDir
	mkdir -p $installedPrimarydataDir
	mkdir -p $installedSecondarydataDir
	mkdir -p $installedParalleldataDir
	mkdir -p $installedExportDir
	cp -p $stagingConfigsDir/*		$installedConfigsDir
	cp -p $stagingJarsDir/*			$installedJarsDir
	cp -p -R $stagingVisToolDir/*	$installedVisToolDir
	cp -p $stagingNativelibsDir/*	$installedNativelibsDir
else
	#
	# remote filesystem
	#   don't bother trying to create primary/secondary/parallel data dirs
	#   dont create export directory - probably uses common export directory
	#
	mkdir -p $pathto_installedSolversDir
	mkdir -p $pathto_installedConfigsDir
	mkdir -p $pathto_installedVisToolDir
	mkdir -p $pathto_installedJarsDir
	mkdir -p $pathto_installedNativelibsDir
	mkdir -p $pathto_installedHtclogsDir
	mkdir -p $pathto_installedJmsBlobFilesDir
	mkdir -p $pathto_installedLogDir
	mkdir -p $pathto_installedTmpDir
	mkdir -p $pathto_installedJavaprefsDir
	mkdir -p $pathto_installedSystemPrefsDir
#	mkdir -p $pathto_installedPrimarydataDir
#	mkdir -p $pathto_installedSecondarydataDir
#	mkdir -p $pathto_installedParalleldataDir
#	mkdir -p $pathto_installedExportDir
	cp -p $stagingConfigsDir/*		$pathto_installedConfigsDir
	cp -p $stagingJarsDir/*			$pathto_installedJarsDir
	cp -p -R $stagingVisToolDir/*	$pathto_installedVisToolDir
	cp -p $stagingNativelibsDir/*	$pathto_installedNativelibsDir
fi