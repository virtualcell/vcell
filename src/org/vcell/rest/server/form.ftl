<html>
<head>
<title>Simulation Tasks</title>
</head>
<body>
<center><h2>Simulation Tasks</h2></center><br/><center>
<form>
<table><tbody>
<tr><td>Begin Timestamp</td><td><input type='text' name='submitLow' value='${submitLow!""}'/></td></tr>
<tr><td>End Timestamp</td><td><input type='text' name='submitHigh' value='${submitHigh!""}'/></td></tr>
<tr><td>max num rows</td><td><input type='text' name='maxRows' value='${maxRows}'/></td></tr>
<tr><td>ServerID</td><td><input type='text' name='serverId' value='${serverId!""}'/></td></tr>
<tr><td>Compute Host</td><td><input type='text' name='computeHost value='${computeHost!""}'/></td></tr>
<tr><td>Simulation ID</td><td><input type='text' name='simId' value='${simId!""}'/></td></tr>
<tr><td>Job ID (parameter scan index)</td><td><input type='text' name='jobId' value='${jobId!""}'/></td></tr>
<tr><td>Task ID (retry index)</td><td><input type='text' name='taskId' value='${taskId!""}'/></td></tr>
</tbody></table>
<br/>Simulation Status (choose at least one)<br/>
<table><tbody>
<tr><td>waiting</td><td><input type='checkbox' name='waiting' checked='<#if waiting>on<#else>off</#if>'/></td><td>queued</td><td><input type='checkbox' name='queued' checked='<#if queued>on<#else>off</#if>'/></td></tr>
<tr><td>dispatched</td><td><input type='checkbox' name='dispatched' checked='<#if dispatched>on<#else>off</#if>'/></td><td>running</td><td><input type='checkbox' name='running' checked='<#if running>on<#else>off</#if>'/></td></tr>
<tr><td>completed</td><td><input type='checkbox' name='completed' checked='<#if completed>on<#else>off</#if>'/></td><td>failed</td><td><input type='checkbox' name='failed' checked='<#if failed>on<#else>off</#if>'/></td></tr>
<tr><td>stopped</td><td><input type='checkbox' name='stopped' checked='<#if stopped>on<#else>off</#if>'/></td></tr>
</tbody></table>
<input type='submit' value='Search' style='font-size:large'>
</form></center>
<br/><h3>query returned ${simTasks?size} results</h3>
<table border='1'>
<tr>
<th>Model type:id</th>
<th>SimKey</th>
<th>SimName</th>
<th>UserName</th>
<th>UserKey</th>
<th>Job Index</th>
<th>Task ID</th>
<th>HTC JobID</th>
<th>has data</th>
<th>Status</th>
<th>Start Date</th>
<th>Message</th>
<th>Site</th>
<th>ComputeHost</th>
</tr>
<#list simTasks as simTask>
<tr>
<td>${simTask.modelType} ${simTask.modelID}</td>
<td>${simTask.simKey}</td>
<td>${simTask.simName}</td>
<td>${simTask.userName}</td>
<td>${simTask.userKey}</td>
<td>${simTask.jobIndex}</td>
<td>${simTask.taskId}</td>
<td>${simTask.htcJobId}</td>
<td>${simTask.hasData}</td>
<td>${simTask.status}</td>
<td>${simTask.startdate}</td>
<td>${simTask.message}</td>
<td>${simTask.site}</td>
<td>${simTask.computeHost}</td>
</tr>
</#list>
</table>
</html>
