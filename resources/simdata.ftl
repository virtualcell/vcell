<html>
<head>
<title>Simulation Tasks</title>
</head>
<body>
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="/simtask">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;Simulation Data&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid})<#else>(not logged in)</#if></h2></center><br/><center>

</center>
<h2><a href='./${simdata.simkey}/jobindex/0'>data values</a></h2>
<table border='1'>
<tr>
<th>Variable</th>
</tr>
<#list simdata.variables as variable>
<tr>
<td>${variable.name!""}</td>
</tr>
</#list>
</table>
<br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

</html>
