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
						<input type='radio' name='orderBy' value='year_asc' <#if orderBy?? && orderBy == "date_asc">checked=on</#if>>Year (oldest)</input>
						</td></tr>
	</tbody></table>
	<input type='submit' value='Search' style='font-size:large'>
</form>
</center>

<br/><h3>query returned ${publications?size} results</h3>
<table border='1'>
<tr>
<th>pub&nbsp;ids</th>
<th>pub&nbsp;title</th>
<th>pub&nbsp;year</th>
<th>biomodels</th>
</tr>
<#list publications as pub>
<tr>
<td><#if pub.pubKey??><a href="/publication/${pub.pubKey}">${pub.pubKey}</a><#else>-</#if></td>
<td>${pub.title!""}</td>
<td>${pub.year!""}</td>
<!-- <td><#if pub.biomodels??><#list pub.biomodels as biomodel><a href='./biomodel/${biomodel.bmKey}'>${biomodel.name}</a>&nbsp;&nbsp; </#list><#else>--</#if></td> -->


<td>
<#if pub.biomodels??>
<#list pub.biomodels as biomodel>
&nbsp;<a href='/biomodel/${biomodel.bmKey}'>${biomodel.name}</a>
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
