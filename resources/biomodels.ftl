<html>
<head>
<title>Biomodels</title>
</head>
<body>
<center><h2>BioModels&nbsp;&nbsp;&nbsp;<a href="/simtask">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid})<#else>(not logged in)</#if></h2></center><br/><center>
<form>

<table><tbody>
<tr><td>Begin Time</td><td><input type='text' name='savedLow' value='${savedLow!""}'/></td></tr>
<tr><td>End Timestamp</td><td><input type='text' name='savedHigh' value='${savedHigh!""}'/></td></tr>
<tr><td>max num rows</td><td><input type='text' name='maxRows' value='${maxRows}'/></td></tr>

</tbody></table>

<input type='submit' value='Search' style='font-size:large'>
</form></center>
<br/><h3>query returned ${biomodels?size} results</h3>
<table border='1'>
<tr>
<th>bioModel id (branch&nbsp;id)</th>
<th>bioModel name</th>
<th>Owner</th>
<th>Date Saved</th>
<th>Simulations</th>
<th>Applications</th>
</tr>
<#list biomodels as biomodel>
<tr>
<td>${biomodel.bmKey!""} (${biomodel.branchID!""})</td>
<td>${biomodel.name!""}</td>
<td>${biomodel.ownerName!""}</td>
<td>${biomodel.savedDate?number_to_date!""}</td>
<td><#if biomodel.simKeys??><#list biomodel.simKeys as simKey><a href='./simulation/${simKey}'>${simKey}</a> </#list><#else>--</#if></td>
<td><#if biomodel.simContextKeys??><#list biomodel.simContextKeys as simContextKey><a href='./application/${simContextKey}'>${simContextKey}</a> </#list><#else>--</#if></td>
</tr>
</#list>
</table>
<br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

</html>
