<html>
<head>
<title>Simulation Tasks</title>
</head>
<body>
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="/simtask">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid})<#else>(not logged in)</#if></h2></center><br/><center>
</center>
<table border='1'>
<tr>
<th>BioModel</th>
<th>BioModel App</th>
<th>MathModel</th>
<th>Simulation Name</th>
<th>Solver Name</th>
<th>User Name</th>
<th>Num Jobs</th>
<th>Jobs</th>
<th>Data</th>
</tr>
<tr>
<td><#if simulation.bioModelLink??><a href='/biomodel/${simulation.bioModelLink.bioModelKey}'>${simulation.bioModelLink.bioModelName!""}</a><#else>unknown</#if></td>
<td><#if simulation.bioModelLink??>${simulation.bioModelLink.simContextName!""}<#else>unknown</#if></td>
<td><#if simulation.mathModelLink??>"${simulation.mathModelLink.mathModelName!""}" (id=${simulation.mathModelLink.mathModelKey}) (branch=${simulation.mathModelLink.mathModelBranchId})<#else>unknown</#if></td>
<td>
	<#if simulation.name??>
		<#if simulation.bioModelLink??>
			<a href="/biomodel/${simulation.bioModelLink.bioModelKey}/simulation/${simulation.key}">${simulation.name}</a>
		<#else>
			<#if simTask.mathModelLink??>
				<a href="/biomodel/${simulation.mathModelLink.mathModelKey}/simulation/${simulation.key}">${simulation.name}</a>
			<#else>
				${simulation.name}
			</#if>
		</#if>
    <#else>
    	unknown
    </#if>
</td>
<td>${simulation.solverName!""}</td>
<td>${simulation.ownerName!""}</td>
<td>${simulation.scanCount!""}</td>
<td><a href="/simtask?simId=${simulation.key}&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on&startRow=1&maxRows=200">check jobs</a></td>
<td><a href="/simdata/${simulation.key}">metadata</a></td>
</tr>
</table>
<br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

</html>
