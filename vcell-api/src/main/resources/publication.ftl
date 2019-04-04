<html>
<head>
<title>Publication</title>
</head>
<body>
<#assign simstatuslink="/simstatus?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&active=on&running=on&completed=on&stopped=on&failed=on">
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/publication">Publications</a>&nbsp;&nbsp;&nbsp;<a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simstatuslink}">Simulation Status</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
</center>

<center>
<h3>Publication <a href="/publication/${publication.pubKey}">${publication.pubKey}</a></h3>
<form action="/publication/${publication.pubKey}" method="post">
<input type="hidden" value="editWithKey" name="pubop" />
<input type='submit' value='edit...' style='font-size:large'>
</form>
</center>
<br>title: &quot;${publication.title!""}&quot;</br>
<br>year: ${publication.year!""}</br>
<br>authors:  <#if publication.authors??><#list publication.authors as author> ${author!""}; </#list></#if></br>
<br>citation: ${publication.citation!""}</br>
<br>pubmedid: ${publication.pubmedid!""}</br>
<br>citation: ${publication.citation!""}</br>
<br>doi: ${publication.doi!""}</br>
<br>endnoteid: ${publication.endnoteid!""}</br>
<br>wittid: ${publication.wittid!""}</br>
<br>pubdate: <#if publication.date??>${publication.date?string["MM/dd/yyyy"]}<#else> </#if></br>

<#assign needspub = 0>
<form action="/publication/${publication.pubKey}" method="post">
<br/><h3>BioModels</h3>
<#if publication.biomodelReferences??>
	<#list publication.biomodelReferences as biomodel>
		<a href='/biomodel/${biomodel.bmKey}'>${biomodel.name}(${biomodel.bmKey})</a>
		<#if biomodel.versionFlag != "3">
			<#assign needspub = needspub+1>
			<input type="checkbox" name="bmpublic" value="${biomodel.bmKey}">Make published
		<#else>&nbsp;(published)</#if>
		</br>
	</#list>

<#else>--</#if>
<br/><br/><h3>MathModels</h3>
<#if publication.mathmodelReferences??>
	<#list publication.mathmodelReferences as mathmodel>
		<a href='/mathmodel/${mathmodel.mmKey}'>${mathmodel.name}(${mathmodel.mmKey})</a>
		<#if mathmodel.versionFlag != "3">
			<#assign needspub = needspub+1>
			<input type="checkbox" name="mmpublic" value="${mathmodel.mmKey}">Make published
		<#else>&nbsp;(published)</#if>
		</br>
	</#list>
<#else>--</#if>
<br/>
<input type="hidden" value="makepublic" name="pubop" />
<#if (needspub > 0) >
	<br/>
	<input type='submit' value='Apply Publish' style='font-size:large'>
</#if>
</form>
<br/><br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>
</body>
</html>
