<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html"; charset=UTF-8" />
<title>Simulation Tasks</title>
</head>
<body>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
</center>

<#if simulation.bioModelLink??>
	<#assign simlink="/biomodel/${simulation.bioModelLink.bioModelKey}/simulation/${simulation.key}"/>
</#if>
<#if simulation.mathModelLink??>
	<#assign simlink="/biomodel/${simulation.mathModelLink.mathModelKey}/simulation/${simulation.key}"/>
</#if>


<br/>

<table>
<tr><td>Name</td><td><input type="text" name="simname" value="${simulation.name}"></input></td></tr>
<tr><td>Model</td><td>
<#if simulation.bioModelLink??>BioModel <a href='/biomodel/${simulation.bioModelLink.bioModelKey}'>&quot;${simulation.bioModelLink.bioModelName!""}&quot;</a>, Application &quot;${simulation.bioModelLink.simContextName!""}&quot;
<#else>
<#if simulation.mathModelLink??>MathModel "${simulation.mathModelLink.mathModelName!""}" (id=${simulation.mathModelLink.mathModelKey}) (branch=${simulation.mathModelLink.mathModelBranchId})
<#else>
unknown
</#if>
</#if>
<tr><td>Owner</td><td>${simulation.ownerName!""}</td></tr>
</td></tr>
<tr><td>Solver</td><td>&quot;${simulation.solverName!""}&quot;</td></tr>
<tr><td>Overrides</td><td>
<#if (simulation.overrides?size>0)>
<#list simulation.overrides as override>
[${override.name}&nbsp;=&nbsp;<#if override.type == "Single">${override.values[0]}</#if>
							  <#if override.type == "Variable">${override.expression}</#if>
							  <#if override.type == "List"><#list override.values as value>${value} </#list></#if>
							  <#if override.type == "LinearInterval">linearInterval(length=${override.cardinality},min=${override.values[0]},max=${override.values[1]})</#if>
							  <#if override.type == "LogInterval">logInterval(length=${override.cardinality},min=${override.values[0]},max=${override.values[1]})</#if>] 
</#list>
<#else>
none
</#if>
</td></tr>
<tr><td>Parameters</td><td>

<#if (simulation.parameters??)>
<table>
<tr>
<th>name</th>
<th>default value</th>
<th>override</th>
<th>unit</th>
<th>description</th>
<th>context</th>
</tr>
<#list simulation.parameters as parameter>
<tr>
<td>
${parameter.name}
</td>
<td>
${parameter.defaultValue}
</td>
<td>

<table>
<#--
	public final String name;
	public final String type;
	public final String expression;
	public final double[] values;
	public final int cardinality;
-->
<tr><td>type</td><td>
<#if simulation.getOverride(parameter.name)??><#assign overrideAA = simulation.getOverride(parameter.name)></#if>
<#if override??>
<#assign overrideType = override.type>
<#else>
<#assign overrideType = "none">
</#if>
<select class="overrideType" id="type:${parameter.name}">
<option  value="none"           <#if overrideType == "none">selected="selected"</#if>            >none</option>
<option  value="Single"         <#if overrideType == "Single">selected="selected"</#if>          >single</option>
<option  value="List"           <#if overrideType == "List">selected="selected"</#if>            >list</option>
<option  value="LinearInterval" <#if overrideType == "LinearInterval">selected="selected"</#if>  >linearInterval</option>
<option  value="LogInterval"    <#if overrideType == "LogInterval">selected="selected"</#if>     >logInterval</option>
<option  value="Dependent"       <#if overrideType == "Variable">selected="selected"</#if>        >dependent</option>
</select>
</td></tr>
<tr id="row:expression:${parameter.name}"><td>expression</td><td><input id="expression:${parameter.name}" type="text"><#if override??>${override.expression}</#if></input></td></tr>
<tr id="row:cardinality:${parameter.name}"><td>cardinality</td><td><input min="0" type="number"><#if override??>${override.cardinality}</#if></input></td></tr>
<tr id="row:values:${parameter.name}"><td>values</td><td><input id="values:${parameter.name}" type="text"><#if override??>${override.values}</#if></input></td></tr>
</table>

</td>
<td><#if parameter.modelSymbolUnit??>[${parameter.modelSymbolUnit}]</#if>
</td>
<td>
<#if parameter.modelSymbolContext??>${parameter.modelSymbolDesc}</#if>
</td>
<td>
<#if parameter.modelSymbolContext??>${parameter.modelSymbolContext}</#if>
</td>
</tr>
</#list>
</table>
<#else>
none
</#if>

</td></tr>
<tr><td>num jobs</td><td>${simulation.scanCount!""}</td></tr>
<tr><td>check jobs</td><td><a href="/simtask?simId=${simulation.key}&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on&startRow=1&maxRows=200">check jobs</a></td></tr>
<tr><td>data</td><td><a href="/simdata/${simulation.key}">metadata</a></td></tr>

</table>
<form name="save" action="${simlink}/save" method="post"><input type='submit' value='Save'/></form>
<br/>
<form name="start" action="${simlink}/startSimulation" method="post"><input type='submit' value='Start'/></form>
<form name="stop" action="${simlink}/stopSimulation" method="post"><input type='submit' value='Stop'/></form>
<br/>
<br/>

<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

<script type="text/JavaScript" src="/scripts/jquery_1_3_2.min.js"></script>
<script type="text/JavaScript">
function setVisible(selectElement,bDefaults){
	var overrideType = selectElement.val();
	var paramName = selectElement.attr("id").replace("type:","");
	var expressionInput = $("[id=\"row:expression:"+paramName+"\"]");
	var cardinalityInput = $("[id=\"row:cardinality:"+paramName+"\"]");
	var valuesInput = $("[id=\"row:values:"+paramName+"\"]");
	switch(overrideType){
	case "none":{
	   selectElement.css('color','red');
	   expressionInput.css('display', 'none');
	   expressionInput.find('input').val("");
	   
	   cardinalityInput.css('display', 'none');
	   cardinalityInput.find('input').val("");
	   
	   valuesInput.css('display', 'none');
	   valuesInput.find('input').val("");
	   break;
	   }
	case "Single":{
	   selectElement.css('color','orange');
	   expressionInput.css("display", "table-row");
	   
	   cardinalityInput.css('display', 'none');
	   cardinalityInput.find('input').val("");
	   
	   valuesInput.css('display', 'none');
	   valuesInput.find('input').val("");
	   break;
	   }
	case "LinearInterval":{
	   selectElement.css('color','grey');
	   expressionInput.css('display', 'none');
	   expressionInput.find('input').val("");
	   
	   cardinalityInput.css('display', 'table-row');

	   valuesInput.css('display', 'table-row');
	   break;
	   }
	case "LogInterval":{
	   selectElement.css('color','green');
	   expressionInput.css('display', 'none');
	   expressionInput.find('input').val("");
	   
	   cardinalityInput.css('display', 'table-row');
	   
	   valuesInput.css('display', 'table-row');
	   break;
	   }
	case "List":{
	   selectElement.css('color','blue');
	   expressionInput.css('display', 'none');
	   expressionInput.find('input').val("");
	   
	   cardinalityInput.css('display', 'none');
	   cardinalityInput.find('input').val("");
	   
	   valuesInput.css('display', 'table-row');
	   break;
	   }
	case "Dependent":{
	   selectElement.css('color','violet');
	   expressionInput.css("display", "table-row");
	   
	   cardinalityInput.css('display', 'none');
	   cardinalityInput.find('input').val("");
	   
	   valuesInput.css('display', 'none');
	   valuesInput.find('input').val("");
	   break;
	   }
	}
//	alert("expression = "+expressionInput);
}
</script>
<script type="text/JavaScript">
	jQuery("[id^=row:cardinality:]").css("display","none");
	jQuery("[id^=row:expression:]").css("display","none");
	jQuery("[id^=row:values:]").css("display","none");
	jQuery("select.overrideType").each(function() { setVisible($(this),false); return this; });
	//
	// event handlers for changing override type
	//
	$("select.overrideType").change(function() { 
		var overrideType = $(this).val();
		var id = $(this).attr("id");
		setVisible($(this),true);
    });
</script>
</body>
</html>
