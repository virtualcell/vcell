<html>
<head>
<title>Publication</title>
</head>
<body>
<#assign simstatuslink="/simstatus?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&active=on&running=on&completed=on&stopped=on&failed=on">
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/publication">Publications</a>&nbsp;&nbsp;&nbsp;<a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simstatuslink}">Simulation Status</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
</center>
<center><h3>Publication <a href="/publication/${publication.pubKey}">${publication.pubKey}</a></h3></center>
<br>title: &quot;${publication.title!""}&quot;</br>
<br>year: ${publication.year!""}</br>
<br>authors: <#list publication.authors as author>${author}&nbsp;&nbsp;</#list></br>

<br/><h3>BioModels</h3>
<#if publication.biomodels??>
<#list publication.biomodels as biomodel><a href='/biomodel/${biomodel.bmKey}'>${biomodel.name}</a>&nbsp;&nbsp;
</#list>
<#else>--</#if>

<br/><br/><br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>
</body>
</html>
