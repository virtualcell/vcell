<html>
<head>
<title>Optimization</title>
</head>
<body>
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid} <a href='${logouturl}'>Log out</a>)<#else>(not logged in <a href='${loginurl}'>sign in</a>)</#if></h2></center><br/><center>
</center>
<center><h3>Optimization <a href="/optimization/${optId!""}">${optId!""}</a></h3></center>
<br>optimization id: &quot;${optId!""}&quot;</br>

<br/><br/><br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>
</body>
</html>
