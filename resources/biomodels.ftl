<html>
<head>
<title>Biomodels</title>
</head>
<body>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2>BioModels&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid})<#else>(not logged in)</#if></h2></center><br/><center>

<form>
	<table><tbody>
	<tr><td>BioModel ID</td><td><input type='text' name='bmId' value='${bmId!""}'/></td></tr>
	<tr><td>Begin Time</td><td><input type='text' name='savedLow' value='${savedLow!""}'/></td></tr>
	<tr><td>End Timestamp</td><td><input type='text' name='savedHigh' value='${savedHigh!""}'/></td></tr>
	<tr><td>start row</td><td><input type='text' name='startRow' value='${startRow}'/></td></tr>
	<tr><td>max num rows</td><td><input type='text' name='maxRows' value='${maxRows}'/></td></tr>
	</tbody></table>
	<input type='submit' value='Search' style='font-size:large'>
</form>

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
