<html>
<head>
<title>Biomodels</title>
</head>
<body>
<#assign publicationslink="/publication?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on">
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on">
<#assign simstatuslink="/simstatus?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&active=on&running=on&completed=on&stopped=on&failed=on">
<center><h2><a href="${publicationslink}">Publications</a>&nbsp;&nbsp;&nbsp;BioModels&nbsp;&nbsp;&nbsp;<a href="${simstatuslink}">Simulation Status</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>

<form>
	<table><tbody>
	<tr><td>BioModel Name</td><td><input type='text' name='bmName' value='${bmName!""}'/></td></tr>
	<tr><td>BioModel ID</td><td><input type='text' name='bmId' value='${bmId!""}'/></td></tr>
	<tr><td>Category</td><td>
						<input type='radio' name='category' value='all' <#if (!category??) || (category == "all")>checked=on</#if>>all</input>
						<input type='radio' name='category' value='public' <#if category?? && category == "public">checked=on</#if>>public</input>
						<input type='radio' name='category' value='shared' <#if category?? && category == "shared">checked=on</#if>>shared</input>
						<input type='radio' name='category' value='mine' <#if category?? && category == "mine">checked=on</#if>>mine</input>
						<input type='radio' name='category' value='tutorial' <#if category?? && category == "tutorial">checked=on</#if>>tutorials</input>
						<input type='radio' name='category' value='education' <#if category?? && category == "education">checked=on</#if>>educational</input>
						</td></tr>
	<tr><td>Owner</td><td><input type='text' name='owner' value='${ownerName!""}'/></td></tr>
	<tr><td>Begin Date (yyyy/mm/dd)</td><td><input type='text' name='savedLow' value='${savedLow!""}'/></td></tr>
	<tr><td>End   Date (yyyy/mm/dd)</td><td><input type='text' name='savedHigh' value='${savedHigh!""}'/></td></tr>
	<tr><td>start row</td><td><input type='text' name='startRow' value='${startRow}'/></td></tr>
	<tr><td>max num rows</td><td><input type='text' name='maxRows' value='${maxRows}'/></td></tr>
	<tr><td>Order By</td><td>
						<input type='radio' name='orderBy' value='date_desc' <#if (!orderBy??) || (orderBy == "date_desc")>checked=on</#if>>Date (newest)</input>
						<input type='radio' name='orderBy' value='date_asc' <#if orderBy?? && orderBy == "date_asc">checked=on</#if>>Date (oldest)</input>
						<input type='radio' name='orderBy' value='name_asc' <#if orderBy?? && orderBy == "name_asc">checked=on</#if>>Name (A-Z)</input>
						<input type='radio' name='orderBy' value='name_desc' <#if orderBy?? && orderBy == "name_desc">checked=on</#if>>Name (Z-A)</input>
						</td></tr>
	</tbody></table>
	<input type='submit' value='Search' style='font-size:large'>
</form>
(Owner also accepts all_public, shared, Education, and tutorial)
</center>

<br/><h3>query returned ${biomodels?size} results</h3>
<table border='1'>
<tr>
<th>bioModel&nbsp;ids</th>
<th>bioModel&nbsp;name</th>
<th>Owner</th>
<th>Date&nbsp;Saved</th>
<!-- <th>Simulations</th> -->
<th>&quot;Applications&quot; - Simulations</th>
</tr>
<#list biomodels as biomodel>
<tr>
<td><#if biomodel.bmKey??><a href="/biomodel/${biomodel.bmKey}">${biomodel.bmKey}</a><#else>-</#if></td>
<td>${biomodel.name!""}</td>
<td>${biomodel.ownerName!""}</td>
<td>${biomodel.savedDate?number_to_date!""}</td>
<!-- <td><#if biomodel.simulations??><#list biomodel.simulations as simulation><a href='./simulation/${simulation.key}'>${simulation.name}</a>&nbsp;&nbsp; </#list><#else>--</#if></td> -->
<!-- <td><#if biomodel.applications??><#list biomodel.applications as application><a href='./application/${application.key}'>${application.name}</a>&nbsp;&nbsp; </#list><#else>--</#if></td> -->


<td>
<#if biomodel.applications??>
<#list biomodel.applications as application>
&quot;${application.name}&quot; - 
<#if biomodel.simulations??><#list biomodel.simulations as simulation><#if simulation.mathKey=application.mathKey>&nbsp;&nbsp;<a href='/biomodel/${biomodel.bmKey}/simulation/${simulation.key}'>${simulation.name}</a></#if></#list><#else>--</#if>
<br/>
</#list>

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
