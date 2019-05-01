<html>
<head>
<title>Publications</title>
</head>
<body>
<#assign biomodelslink="/biomodel?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on">
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on">
<#assign simstatuslink="/simstatus?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&active=on&running=on&completed=on&stopped=on&failed=on">
<center><h2>Publications&nbsp;&nbsp;&nbsp;<a href="${biomodelslink}">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simstatuslink}">Simulation Status</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>

<form>
	<table><tbody>
	<tr><td>Publication ID</td><td><input type='text' name='pubId' value='${pubId!""}'/></td></tr>
	<tr><td>Order By</td><td>
						<input type='radio' name='orderBy' value='year_desc' <#if (!orderBy??) || (orderBy == "year_desc")>checked=on</#if>>Year (newest)</input>
						<input type='radio' name='orderBy' value='year_asc' <#if orderBy?? && orderBy == "year_asc">checked=on</#if>>Year (oldest)</input>
						</td></tr>
	</tbody></table>
	<input type='submit' value='Search' style='font-size:large'>
	
</form>

<form action="/publication/new" method="post">
<input type="hidden" value="editNew" name="pubop" />
<input type='submit' value='Create New...' style='font-size:large'>
</form>
</center>

<br/><h3>query returned ${publications?size} results</h3>
<table border='1'>
<tr>
<th>pub&nbsp;ids</th>
<th>pub&nbsp;curated</th>
<th>pub&nbsp;title</th>
<th>pub&nbsp;year</th>
<th>citation</th>
<th>authors</th>
<th>pubmed id</th>
<th>endnote id</th>
<th>witt id</th>
<th>biomodels</th>
<th>mathmodels</th>
</tr>
<#list publications as pub>
<tr>
<td><#if pub.pubKey??><a href="/publication/${pub.pubKey}">${pub.pubKey}</a><#else>-</#if></td>
<td><#if pub.isCurated()>T<#else>F</#if></td>
<td>${pub.title!""}</td>
<td>${pub.year!""}</td>
<td>${pub.citation!""}</td>
<td><#if pub.authors??><#list pub.authors as author> ${author!""}; </#list></#if></td>
<td>${pub.pubmedid!""}</td>
<td>${pub.endnoteid!""}</td>
<td>${pub.wittid!""}</td>
<!-- <td><#if pub.biomodels??><#list pub.biomodels as biomodel><a href='./biomodel/${biomodel.bmKey}'>${biomodel.name}</a>&nbsp;&nbsp; </#list><#else>--</#if></td> -->


<td>
<#if pub.biomodelReferences??>
<table>
<#list pub.biomodelReferences as biomodelReference>
<tr>
<td>
&nbsp;<a href='/biomodel/${biomodelReference.bmKey}'>${biomodelReference.name}</a>
</td>
</tr>
</#list>
</table>
<br/>

<#else>
--
</#if>
</td>

<td>
<#if pub.mathmodelReferences??>
<#list pub.mathmodelReferences as mathmodelReference>
&nbsp;${mathmodelReference.name}
</#list>
<br/>

<#else>
--
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
