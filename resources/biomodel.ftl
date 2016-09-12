<html>
<head>
<title>Biomodel</title>
</head>
<body>
<#assign diagramlink="/biomodel/${biomodel.bmKey}/diagram">
<#assign vcmllink="/biomodel/${biomodel.bmKey}/vcml">
<#assign simstatuslink="/simstatus?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&active=on&running=on&completed=on&stopped=on&failed=on">
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simstatuslink}">Simulation Status</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
</center>
<center><h3>BioModel <a href="/biomodel/${biomodel.bmKey}">${biomodel.bmKey}</a></h3></center>
<br>name: &quot;${biomodel.name!""}&quot;</br>
<br>owner: ${biomodel.ownerName!""}</br>
<br>saved: ${biomodel.savedDate?number_to_date!""}</br>
<br><a href="${vcmllink}" type="application/vcml+xml" download="Biomodel_${biomodel.bmKey}.vcml">dowload vcml</a></br>
<br><img src="${diagramlink}" type="image/png"/></br>

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
