<html>
<head>
<title>Edit Publication</title>
</head>
<body>
<form action="/publication/new" method="post">
<table>
	<input type="hidden" value="applyEdit" name="pubop" />
	<tr><td>pubId</td><td><input type='text' name='pubId' value='${publicationRepr.pubKey}' readonly/></td></tr>
	<tr><td>curated (T/F)</td><td><input style="width:100px;" type='text' name='bcurated' <#if publicationRepr.isCurated()>value='T'<#else>value='F'</#if>/></td></tr>
	<tr><td>title</td><td><input style="width:800px;" type='text' name='title' value='${publicationRepr.title!""}'/></td></tr>
	<tr><td>authors (sep=;)</td><td><input style="width:800px;" type='text' name='authors'<#if publicationRepr.authors??>value='${publicationRepr.authors?join("; ")}'<#else>value=''</#if>/></td></tr>
	<tr><td>year</td><td><input type='text' name='year'<#if publicationRepr.year??>value='${publicationRepr.year?string.computer}'<#else>value=''</#if>/></td></tr>
	<tr><td>pubdate (MM/dd/yyyy)</td><td><input type='text' name='pubdate'<#if publicationRepr.date??>value='${publicationRepr.date?string["MM/dd/yyyy"]}'<#else>value=''</#if>/></td></tr>
	<tr><td>citation</td><td><input style="width:600px;" type='text' name='citation' value='${publicationRepr.citation!""}'/></td></tr>
	<tr><td>pubmedid</td><td><input type='text' name='pubmedid' value='${publicationRepr.pubmedid!""}'/></td></tr>
	<tr><td>doi</td><td><input style="width:400px;" type='text' name='doi' value='${publicationRepr.doi!""}'/></td></tr>
	<tr><td>endnoteid</td><td><input type='text' name='endnoteid' value='${publicationRepr.endnoteid!""}'/></td></tr>
	<tr><td>url</td><td><input style="width:600px;" type='text' name='url' value='${publicationRepr.url!""}'/></td></tr>
	<tr><td>wittid</td><td><input type='text' name='wittid' value='${publicationRepr.wittid!""}'/></td></tr>
	<tr><td>biomodelReferences (sep= ,)</td><td><input type='text' style="width:800px;" name='biomodelReferences'<#if publicationRepr.biomodelReferences??>value='<#list publicationRepr.biomodelReferences as biomodelReference>${biomodelReference.bmKey}, </#list>'<#else>value=''</#if>/></td></tr>
	<tr><td>mathmodelReferences (sep= ,)</td><td><input type='text' style="width:800px;" name='mathmodelReferences'<#if publicationRepr.mathmodelReferences??>value='<#list publicationRepr.mathmodelReferences as mathmodelReference>${mathmodelReference.mmKey}, </#list>'<#else>value=''</#if>/></td></tr>
</table>
<input type='submit' value='Save' style='font-size:large'>
</form>
</body>
</html>
