<html>
<head>
<script type="text/javascript" src="/scripts/dygraph-combined.js"></script>
<title>Simulation Tasks</title>
</head>
<body onload='showGraph()'>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/publication">Publications</a>&nbsp;&nbsp;&nbsp;<a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;Simulation Data&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>


</center>
<table border="0">
<tr>
<td>
<div id="graphdiv"></div>
</td>
<td>
<b>Select Variables</b>
<p>
<#assign count=0>
<#list simdatavalues.variables as variable>
	<#if variable.name != "t">
		<input type='checkbox' id='${count}' onclick='change(this)' <#if count==0>checked='checked'</#if> />
		<label for='${count}'>${variable.name!""}</label><br/>
		<#assign count=count+1>
	</#if>
</#list>
</td>
</table>

<script type="text/javascript">
  function getData() {
  	return ${csvdata};
  }
  function showGraph() {
      var visibilityArray = new Array(0);
      <#list simdatavalues.variables as variable>
      	visibilityArray.push(false);
      </#list>
      visibilityArray[0] = true;
      	console.log(JSON.stringify(visibilityArray));

	  g = new Dygraph(
		  document.getElementById("graphdiv"),
		  getData(),
	 	 { xlabel: 'Time (s)',
	 	   title: 'Results for {simName}',
	 	   visibility: visibilityArray
	 	 }
	  );
  }
  function change(el) {
     g.setVisibility(parseInt(el.id), el.checked);
  }
  
</script>


<br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>
</body>
</html>
