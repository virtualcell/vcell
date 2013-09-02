<html>
<head>
<script type="text/javascript" src="/scripts/dygraph-combined.js"></script>
<title>Simulation Tasks</title>
</head>
<body>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;Simulation Data&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>


</center>
<div id="graphdiv"></div>
<script type="text/javascript">
  g = new Dygraph(

    // containing div
    document.getElementById("graphdiv"),

    // CSV or path to a CSV file.
    //"Date,Temperature\n" +
    //"2008-05-07,75\n" +
    //"2008-05-08,70\n" +
    //"2008-05-09,80\n"
    <#if csvdata??>${csvdata}</#if>, {
    xlabel: 'Time (s)'
    <#if simName??>,title: 'Results for %{simName}'</#if>
	}
  );
</script>

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
</body>
</html>
