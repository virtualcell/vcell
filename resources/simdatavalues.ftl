<html>
<head>
<title>Simulation Tasks</title>
</head>
<body>
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="/simtask">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;Simulation Data&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid})<#else>(not logged in)</#if></h2></center><br/><center>


</center>
<table border='1'>
<tr>
<th>Variable</th>
<th>Values</th>
</tr>
<#list simdatavalues.variables as variable>
<tr>
<td>${variable.name!""}</td>
<td>
<#list variable.values as value>
${value},
</#list>
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
