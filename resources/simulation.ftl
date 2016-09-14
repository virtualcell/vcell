<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html"; charset=UTF-8" />
<title>Simulation Description</title>
</head>
<body>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on">
<#assign simstatuslink="/simstatus?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&active=on&running=on&completed=on&stopped=on&failed=on">
<center><h2><a href="/publication">Publications</a>&nbsp;&nbsp;&nbsp;<a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simstatuslink}">Simulation Status</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
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
<tr><td>num jobs</td><td>${simulation.scanCount!""}</td></tr>
<tr><td>check status</td><td><a href="/simstatus?simId=${simulation.key}&hasData=all&neverran=on&active=on&&completed=on&failed=on&stopped=on&startRow=1&maxRows=200">check status</a></td></tr>
<tr><td>check jobs</td><td><a href="/simtask?simId=${simulation.key}&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on&startRow=1&maxRows=200">check jobs</a></td></tr>
<tr><td>data</td><td><a href="/simdata/${simulation.key}">metadata</a></td></tr>
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
</table>
<form name="start" action="${simlink}/startSimulation" method="post"><input type='submit' value='Start'/></form>
<br/>
<br/>

<h3>Parameters</h3>
 <form class="tryme" name="tryme" ><input type='submit' value='Save'/></form>
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
<tr id="overrideRow:${parameter.name}">
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
<#if simulation.getOverride(parameter.name)??>
<#assign overrideType = simulation.getOverride(parameter.name).type>
<#else>
<#assign overrideType = "none">
</#if>
<select class="overrideType" id="type:${parameter.name}">
<option  value="none"           <#if overrideType == "none">selected="selected"</#if>            >none</option>
<option  value="Single"         <#if overrideType == "Single">selected="selected"</#if>          >single</option>
<option  value="List"           <#if overrideType == "List">selected="selected"</#if>            >list</option>
<option  value="LinearInterval" <#if overrideType == "LinearInterval">selected="selected"</#if>  >linearInterval</option>
<option  value="LogInterval"    <#if overrideType == "LogInterval">selected="selected"</#if>     >logInterval</option>
<option  value="Variable"       <#if overrideType == "Variable">selected="selected"</#if>        >variable</option>
</select>
</td></tr>
<tr id="row:expression:${parameter.name}"><td>expression</td><td><input id="input:expression:${parameter.name}" type="text"><#if override??>${override.expression}</#if></input></td></tr>
<tr id="row:cardinality:${parameter.name}"><td>cardinality</td><td><input id="input:cardinality:${parameter.name}" min="0" type="number"><#if override??>${override.cardinality}</#if></input></td></tr>
<tr id="row:values:${parameter.name}"><td>values</td><td><input id="input:values:${parameter.name}" type="text"><#if override??>${override.values}</#if></input></td></tr>
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


<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

<script type="text/JavaScript" src="/scripts/jquery_1_3_2.min.js"></script>
<script type="text/JavaScript">
function getOverride(simulation,parameterName){
	for (var i = 0; i < simulation.overrides.length; i++){
		if (simulation.overrides[i].name === parameterName){
			return simulation.overrides[i];
		}
	}
	return null;
}
function setVisible(selectElement,bDefaults,simulation){
	var overrideType = selectElement.val();
	var paramName = selectElement.attr("id").replace("type:","");
	var expressionRow = $("[id=\"row:expression:"+paramName+"\"]");
	var cardinalityRow = $("[id=\"row:cardinality:"+paramName+"\"]");
	var valuesRow = $("[id=\"row:values:"+paramName+"\"]");
	var expressionInput = $("[id=\"input:expression:"+paramName+"\"]");
	var cardinalityInput = $("[id=\"input:cardinality:"+paramName+"\"]");
	var valuesInput = $("[id=\"input:values:"+paramName+"\"]");
	var overrideRow = $("[id=\"overrideRow:"+paramName+"\"]");
	var override = getOverride(simulation,paramName);
//	if (bDefaults){
//	   expressionInput.find('input').val("");
//	   cardinalityInput.find('input').val("");
//	   valuesInput.find('input').val("");
//	}
	if (override != null){
		if (override.hasOwnProperty('expression') && override.expression!=null){
			expressionInput.val(override.expression);
		}else{
			expressionInput.val("");
		}
		if (override.hasOwnProperty('cardinality') && override.cardinality!=null){
			cardinalityInput.val(override.cardinality);
		}else{
			cardinalityInput.val("");
		}
		if (override.hasOwnProperty('values') && override.values!=null){
			valuesInput.val(override.values);
		}else{
			valuesInput.val("");
		}
	}
	switch(overrideType){
	case "none":{
	   overrideRow.css('color','grey');
	   selectElement.css('color','grey');
	   expressionRow.css('display', 'none');
	   cardinalityRow.css('display', 'none');
	   valuesRow.css('display', 'none');
	   break;
	   }
	case "Single":{
	   overrideRow.css('color','red');
	   selectElement.css('color','red');
	   expressionRow.css("display", "none");
	   cardinalityRow.css('display', 'none');
	   valuesRow.css('display', 'table-row');
	   valuesRow.css('display', 'table-row');
	   break;
	   }
	case "LinearInterval":{
	   overrideRow.css('color','red');
	   selectElement.css('color','red');
	   expressionRow.css('display', 'none');
	   cardinalityRow.css('display', 'table-row');
	   valuesRow.css('display', 'table-row');
	   break;
	   }
	case "LogInterval":{
	   overrideRow.css('color','red');
	   selectElement.css('color','red');
	   expressionRow.css('display', 'none');
	   cardinalityRow.css('display', 'table-row');
	   valuesRow.css('display', 'table-row');
	   break;
	   }
	case "List":{
	   overrideRow.css('color','red');
	   selectElement.css('color','red');
	   expressionRow.css('display', 'none');
	   cardinalityRow.css('display', 'none');
	   valuesRow.css('display', 'table-row');
	   break;
	   }
	case "Variable":{
	   overrideRow.css('color','red');
	   selectElement.css('color','red');
	   expressionRow.css("display", "table-row");
	   cardinalityRow.css('display', 'none');
	   valuesRow.css('display', 'none');
	   break;
	   }
	}
}
function parseOverrideFromElement(selectElement){
	var overrideType = selectElement.val();
	var paramName = selectElement.attr("id").replace("type:","");
	var expressionInput = $("[id=\"input:expression:"+paramName+"\"]");
	var cardinalityInput = $("[id=\"input:cardinality:"+paramName+"\"]");
	var valuesInput = $("[id=\"input:values:"+paramName+"\"]");

	switch(overrideType){
	case "none":{
		return null;
	   }
	case "Single":{
		return {name: paramName,
				type: overrideType,
				values: JSON.parse("["+valuesInput.val()+"]"),
				cardinality: 1};
	   }
	case "LinearInterval":{
		var valuesStringArray = valuesInput.val().replace("/,/g"," ").split(" ");
		return {name: paramName,
				type: overrideType,
				values: JSON.parse("["+valuesStringArray+"]"),
				cardinality: JSON.parse(cardinalityInput.val())};
	   }
	case "LogInterval":{
		var valuesStringArray = valuesInput.val().replace("/,/g"," ").split(" ");
		return {name: paramName,
				type: overrideType,
				values: JSON.parse("["+valuesStringArray+"]"),
				cardinality: JSON.parse(cardinalityInput.val())};
	   }
	case "List":{
		var valuesStringArray = valuesInput.val().replace("/,/g"," ").split(" ");
		return {name: paramName,
				type: overrideType,
				values: JSON.parse("["+valuesStringArray+"]"),
				cardinality: JSON.parse(valuesStringArray.length)};
	   }
	case "Variable":{
		return {name: paramName,
				type: overrideType,
				expression: expressionInput.val(), // keep expression as a string
				cardinality: 1};
	   }
	}
}
function formatOverride(pName,overrideType,expression,valuesArray,cardinality){
	switch(overrideType){
	case "none":{
	   return "";
	}
	case "Single":{
	   return "["+pName+" = "+values+"]";
	}
	case "LinearInterval":{
	   return "["+pName+" = linearInterval(length="+cardinality+",min="+values[0]+",max="+values[1]+")]";
	}
	case "LogInterval":{
	   return "["+pName+" = logInterval(length="+cardinality+",min="+values[0]+",max="+values[1]+")]";
	}
	case "List":{
	   var listString = "";
	   for (var i = 0; i<valuesArray.length;i++){
			listString += valuesArray[i];
			if (i < valuesArray.length-1){
				listString += " ";
			}
	   }
	   return "["+pName+" = "+listString+"]";
	}
	case "Variable":{
	   return "["+pName+" = "+expression+"]";
	}
	}
}
	


	var simulation = JSON.parse("${jsonResponse?js_string}");
	jQuery("[id^=row:cardinality:]").css("display","none");
	jQuery("[id^=row:expression:]").css("display","none");
	jQuery("[id^=row:values:]").css("display","none");
	jQuery("select.overrideType").each(function() { setVisible($(this),false,simulation); return this; });
	//
	// event handlers for changing override type
	//
	$("select.overrideType").change(function() { 
		var overrideType = $(this).val();
		var id = $(this).attr("id");
		setVisible($(this),true,simulation);
    });
	$("form.tryme").submit(function(e) { 
		//
		// gather new list of overrides and pop it up.
		//
		var overridesArray = [];
		jQuery("select.overrideType").each(function() { 
				var overrideObj = parseOverrideFromElement($(this));
				if (overrideObj != null){
					overridesArray.push(overrideObj);
				}
				return this; 
			});
		
		var jsonData = JSON.stringify( {overrides: overridesArray} );
		var object = JSON.parse(jsonData);
		$.ajax({
			type: "POST",
			url: "${simlink}/save",
			data: jsonData,
			contentType: "application/json",
			dataType: "json",
			success: function(data, textStatus, jqXHR)   {
				if (data==null){
					alert("it worked, data is null");
					return;
				}
				var newURL = "/biomodel/"+data.bioModelLink.bioModelKey+"/simulation/"+data.key;
				alert("it worked: oldSimKey="+JSON.stringify(simulation.key)+" newSimKey="+JSON.stringify(data.key)+" origURL=${simlink}  newURL="+newURL);
				window.location.href = newURL;
			},
			failure: function(jqXHR, textStatus, errorThrown) { 
				alert("it failed: "+textStatus);
			},
			async:false
		});
    	e.preventDefault(); //STOP default action
    });
   // alert("simulation = "+simulation.overrides[0].name);
    
</script>
</body>
</html>
