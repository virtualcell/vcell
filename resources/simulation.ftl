<html>
<head>
<title>Simulation Tasks</title>
</head>
<body>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
</center>

<#if simulation.bioModelLink??>
	<#assign simlink="/biomodel/${simulation.bioModelLink.bioModelKey}/simulation/${simulation.key}"/>
</#if>
<#if simulation.mathModelLink??>
	<#assign simlink="/biomodel/${simulation.mathModelLink.mathModelKey}/simulation/${simulation.key}"/>
</#if>


<table border='1'>
<tr>
<th>BioModel</th>
<th>BioModel App</th>
<th>MathModel</th>
<th>Simulation Name</th>
<th>Solver Name</th>
<th>Overrides</th>
<th>User Name</th>
<th>Num Jobs</th>
<th>Jobs</th>
<th>Data</th>
<th>operations</th>
</tr>
<tr>
<td><#if simulation.bioModelLink??><a href='/biomodel/${simulation.bioModelLink.bioModelKey}'>${simulation.bioModelLink.bioModelName!""}</a><#else>unknown</#if></td>
<td><#if simulation.bioModelLink??>${simulation.bioModelLink.simContextName!""}<#else>unknown</#if></td>
<td><#if simulation.mathModelLink??>"${simulation.mathModelLink.mathModelName!""}" (id=${simulation.mathModelLink.mathModelKey}) (branch=${simulation.mathModelLink.mathModelBranchId})<#else>unknown</#if></td>
<td>
	<#if simulation.name??>
		<#if simLink??>
			<a href="${simlink}">${simulation.name}</a>
		<#else>
			${simulation.name}
		</#if>
    <#else>
    	unknown
    </#if>
</td>
<td>${simulation.solverName!""}</td>
<td>
<#if simulation.overrides??>
<#list simulation.overrides as override>
[${override.name}&nbsp;=&nbsp;<#if override.type == "Single">${override.values[0]}</#if>
							  <#if override.type == "Variable">${override.expression}</#if>
							  <#if override.type == "List"><#list override.values as value>${value} </#list></#if>
							  <#if override.type == "LinearInterval">linearInterval(length=${override.cardinality},min=${override.values[0]},max=${override.values[1]})</#if>
							  <#if override.type == "LogInterval">logInterval(length=${override.cardinality},min=${override.values[0]},max=${override.values[1]})</#if>] 
</#list>
<#else>
${simulation}
</#if>
</td>
<td>${simulation.ownerName!""}</td>
<td>${simulation.scanCount!""}</td>
<td><a href="/simtask?simId=${simulation.key}&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on&startRow=1&maxRows=200">check jobs</a></td>
<td><a href="/simdata/${simulation.key}">metadata</a></td>
<td>
<form name="start" action="${simlink}/startSimulation" method="post"><input type='submit' value='Start'/></form>
<form name="stop" action="${simlink}/stopSimulation" method="post"><input type='submit' value='Stop'/></form>
</td>
</tr>
</table>
<br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

</html>
