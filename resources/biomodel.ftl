<html>
<head>
<title>Biomodel</title>
</head>
<body>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
</center>
<br/><h3>query returned <#if biomodel??>1<#else>0</#if> results</h3>
<table border='1'>
<tr>
<th>bioModel id</th>
<th>bioModel name</th>
<th>Owner</th>
<th>Date Saved</th>
</tr>
<tr>
<td><#if biomodel.bmKey??><a href="/biomodel/${biomodel.bmKey}">${biomodel.bmKey}</a><#else>-</#if></td>
<td>${biomodel.name!""}</td>
<td>${biomodel.ownerName!""}</td>
<td>${biomodel.savedDate?number_to_date!""}</td>
</tr>
</table>

<br/><h3>Applications</h3>
<#if biomodel.applications??>
<table border="1">
<tr>
<th>Application Name</th>
<th>Simulations for this Application</th>
</tr>
<#list biomodel.applications as application>
<tr>
<td>${application.name}</td>
<td><#if biomodel.simulations??><#list biomodel.simulations as simulation><#if simulation.mathKey=application.mathKey><a href='/biomodel/${biomodel.bmKey}/simulation/${simulation.key}'>${simulation.name}</a>&nbsp;&nbsp;</#if></#list><#else>--</#if></td>
</tr>
</#list>
</table>
</#if>

<br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

</html>
