<html>
<head>
<title>Simulation Statuses</title>
</head>
<body>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on">
<center><h2><a href="/publication">Publications</a>&nbsp;&nbsp;&nbsp;<a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;Simulation Status&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
<form>
<table><tbody>
<tr><td>Begin Timestamp</td><td><input type='text' name='submitLow' value='${submitLow!""}'/></td></tr>
<tr><td>End Timestamp</td><td><input type='text' name='submitHigh' value='${submitHigh!""}'/></td></tr>
<tr><td>start row</td><td><input type='text' name='startRow' value='${startRow}'/></td></tr>
<tr><td>max num rows</td><td><input type='text' name='maxRows' value='${maxRows}'/></td></tr>
<tr><td>Simulation ID</td><td><input type='text' name='simId' value='${simId!""}'/></td></tr>
<tr><td>Has Data</td><td><input type='radio' name='hasData' value='all' <#if (!hasData??) || (hasData == "all")>checked=on</#if>>all</input>
						<input type='radio' name='hasData' value='yes' <#if hasData?? && hasData == "yes">checked=on</#if>>yes</input>
						<input type='radio' name='hasData' value='no' <#if hasData?? && hasData == "no">checked=on</#if>>no</input>
						</td></tr>


</tbody></table>
<br/>Simulation Status (choose at least one)<br/>
<table><tbody>
<tr><td>active</td><td><input type='checkbox' name='active' <#if active>checked='on'</#if>/></td>
    <td>completed</td><td><input type='checkbox' name='completed' <#if completed>checked='on'</#if>/></td></tr>
<tr><td>failed</td><td><input type='checkbox' name='failed' <#if failed>checked='on'</#if>'/></td>
    <td>stopped</td><td><input type='checkbox' name='stopped' <#if stopped>checked='on'</#if>/></td></tr>
</tbody></table>
<input type='submit' value='Search' style='font-size:large'>
</form></center>
<br/><h3>query returned ${simStatuses?size} results</h3>
<table border='1'>
<tr>
<th>BioModel</th>
<th>BioModel App</th>
<th>MathModel</th>
<th>Simulation</th>
<th>User Name</th>
<th>num jobs</th>
<th>num jobs done</th>
<th>progress</th>
<th>has data</th>
<th>Overall Status</th>
<th>Detailed Status</th>
<th>Message</th>
<th>operations</th>
</tr>
<#list simStatuses as simStatus>

<#if simStatus.simRep.bioModelLink??>
	<#assign simlink="/biomodel/${simStatus.simRep.bioModelLink.bioModelKey}/simulation/${simStatus.simRep.key}"/>
</#if>
<#if simStatus.simRep.mathModelLink??>
	<#assign simlink="/biomodel/${simStatus.simRep.mathModelLink.mathModelKey}/simulation/${simStatus.simRep.key}"/>
</#if>

<tr>
<td><#if simStatus.simRep.bioModelLink??><a href='/biomodel/${simStatus.simRep.bioModelLink.bioModelKey}'>${simStatus.simRep.bioModelLink.bioModelName!""}</a><#else>unknown</#if></td>
<td><#if simStatus.simRep.bioModelLink??>${simStatus.simRep.bioModelLink.simContextName!""}<#else>unknown</#if></td>
<td><#if simStatus.simRep.mathModelLink??>"${simStatus.simRep.mathModelLink.mathModelName!""}" (id=${simStatus.simRep.mathModelLink.mathModelKey}) (branch=${simStatus.simRep.mathModelLink.mathModelBranchId})<#else>unknown</#if></td>
<td>
	<#if simStatus.simRep.name??>
		<#if simlink??>
			<a href="${simlink}">${simStatus.simRep.name}</a>
		<#else>
			${simStatus.simRep.name}
		</#if>
    <#else>
    	unknown
    </#if>
</td>
<td>username ${simStatus.simRep.userName!""}</td>
<td>scanCount ${simStatus.simRep.scanCount!""}</td>
<td>numberOfJobsDone ${simStatus.numberOfJobsDone!""}</td>
<td>progress ${simStatus.progress!""}</td>
<td><#if simStatus.hasData><a href='./simdata/${simStatus.simRep.key}'>metadata</a><#else>false</#if></td>
<td>${simStatus.statusString!""}</td>
<td><a href='/simtask?simId=${simStatus.simRep.key}&startRow=1&maxRows=100&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on'>tasks</a></td>
<td>message ${simStatus.failedMessage!""}</td>
<td> operations
<#if simStatus.runnable>
	<form name="start" action="${simlink}/startSimulation" method="post"><input type='submit' value='Start'/></form>
</#if>
<#if simStatus.stoppable>
	<form name="stop" action="${simlink}/stopSimulation" method="post"><input type='submit' value='Stop'/></form>
</#if>
</td>
</tr>
</#list>
</table>
<br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

</html>
