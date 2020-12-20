<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<axsl:stylesheet xmlns:axsl="http://www.w3.org/1999/XSL/Transform" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:sch="http://www.ascc.net/xml/schematron" xmlns:sed="http://sed-ml.org/sed-ml/level1/version2" sed:dummy-for-xmlns="" xmlns:math="http://www.w3.org/1998/Math/MathML" math:dummy-for-xmlns="" version="1.0">
<!--Implementers: please note that overriding process-prolog or process-root is 
    the preferred method for meta-stylesheets to use where possible. -->
<axsl:param name="archiveDirParameter"/>
<axsl:param name="archiveNameParameter"/>
<axsl:param name="fileNameParameter"/>
<axsl:param name="fileDirParameter"/>

<!--PHASES-->


<!--PROLOG-->
<axsl:output xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" indent="yes" standalone="yes" omit-xml-declaration="no" method="xml"/>

<!--KEYS-->
<axsl:key name="modelid" match="sed:model[@id]" use="@id"/>
<axsl:key name="simid" match="sed:uniformTimeCourse[@id]" use="@id"/>
<axsl:key name="steadystateid" match="sed:steadyState[@id]" use="@id"/>
<axsl:key name="onestepid" match="sed:oneStep[@id]" use="@id"/>
<axsl:key name="dgid" match="sed:dataGenerator[@id]" use="@id"/>
<axsl:key name="taskid" match="sed:task[@id]" use="@id"/>
<axsl:key name="repeatedtaskid" match="sed:repeatedTask[@id]" use="@id"/>
<axsl:key name="allIds" match="sed:*[@id]" use="@id"/>

<!--DEFAULT RULES-->


<!--MODE: SCHEMATRON-SELECT-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<axsl:template mode="schematron-select-full-path" match="*">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<axsl:template mode="schematron-get-full-path" match="*">
<axsl:apply-templates mode="schematron-get-full-path" select="parent::*"/>
<axsl:text>/</axsl:text>
<axsl:choose>
<axsl:when test="namespace-uri()=''">
<axsl:value-of select="name()"/>
<axsl:variable select="1+    count(preceding-sibling::*[name()=name(current())])" name="p_1"/>
<axsl:if test="$p_1&gt;1 or following-sibling::*[name()=name(current())]">[<axsl:value-of select="$p_1"/>]</axsl:if>
</axsl:when>
<axsl:otherwise>
<axsl:text>*[local-name()='</axsl:text>
<axsl:value-of select="local-name()"/>
<axsl:text>' and namespace-uri()='</axsl:text>
<axsl:value-of select="namespace-uri()"/>
<axsl:text>']</axsl:text>
<axsl:variable select="1+   count(preceding-sibling::*[local-name()=local-name(current())])" name="p_2"/>
<axsl:if test="$p_2&gt;1 or following-sibling::*[local-name()=local-name(current())]">[<axsl:value-of select="$p_2"/>]</axsl:if>
</axsl:otherwise>
</axsl:choose>
</axsl:template>
<axsl:template mode="schematron-get-full-path" match="@*">
<axsl:text>/</axsl:text>
<axsl:choose>
<axsl:when test="namespace-uri()=''">@<axsl:value-of select="name()"/>
</axsl:when>
<axsl:otherwise>
<axsl:text>@*[local-name()='</axsl:text>
<axsl:value-of select="local-name()"/>
<axsl:text>' and namespace-uri()='</axsl:text>
<axsl:value-of select="namespace-uri()"/>
<axsl:text>']</axsl:text>
</axsl:otherwise>
</axsl:choose>
</axsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-2-->
<!--This mode can be used to generate prefixed XPath for humans-->
<axsl:template mode="schematron-get-full-path-2" match="node() | @*">
<axsl:for-each select="ancestor-or-self::*">
<axsl:text>/</axsl:text>
<axsl:value-of select="name(.)"/>
<axsl:if test="preceding-sibling::*[name(.)=name(current())]">
<axsl:text>[</axsl:text>
<axsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/>
<axsl:text>]</axsl:text>
</axsl:if>
</axsl:for-each>
<axsl:if test="not(self::*)">
<axsl:text/>/@<axsl:value-of select="name(.)"/>
</axsl:if>
</axsl:template>

<!--MODE: GENERATE-ID-FROM-PATH -->
<axsl:template mode="generate-id-from-path" match="/"/>
<axsl:template mode="generate-id-from-path" match="text()">
<axsl:apply-templates mode="generate-id-from-path" select="parent::*"/>
<axsl:value-of select="concat('.text-', 1+count(preceding-sibling::text()), '-')"/>
</axsl:template>
<axsl:template mode="generate-id-from-path" match="comment()">
<axsl:apply-templates mode="generate-id-from-path" select="parent::*"/>
<axsl:value-of select="concat('.comment-', 1+count(preceding-sibling::comment()), '-')"/>
</axsl:template>
<axsl:template mode="generate-id-from-path" match="processing-instruction()">
<axsl:apply-templates mode="generate-id-from-path" select="parent::*"/>
<axsl:value-of select="concat('.processing-instruction-', 1+count(preceding-sibling::processing-instruction()), '-')"/>
</axsl:template>
<axsl:template mode="generate-id-from-path" match="@*">
<axsl:apply-templates mode="generate-id-from-path" select="parent::*"/>
<axsl:value-of select="concat('.@', name())"/>
</axsl:template>
<axsl:template priority="-0.5" mode="generate-id-from-path" match="*">
<axsl:apply-templates mode="generate-id-from-path" select="parent::*"/>
<axsl:text>.</axsl:text>
<axsl:value-of select="concat('.',name(),'-',1+count(preceding-sibling::*[name()=name(current())]),'-')"/>
</axsl:template>
<!--MODE: SCHEMATRON-FULL-PATH-3-->
<!--This mode can be used to generate prefixed XPath for humans 
	(Top-level element has index)-->
<axsl:template mode="schematron-get-full-path-3" match="node() | @*">
<axsl:for-each select="ancestor-or-self::*">
<axsl:text>/</axsl:text>
<axsl:value-of select="name(.)"/>
<axsl:if test="parent::*">
<axsl:text>[</axsl:text>
<axsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/>
<axsl:text>]</axsl:text>
</axsl:if>
</axsl:for-each>
<axsl:if test="not(self::*)">
<axsl:text/>/@<axsl:value-of select="name(.)"/>
</axsl:if>
</axsl:template>

<!--MODE: GENERATE-ID-2 -->
<axsl:template mode="generate-id-2" match="/">U</axsl:template>
<axsl:template priority="2" mode="generate-id-2" match="*">
<axsl:text>U</axsl:text>
<axsl:number count="*" level="multiple"/>
</axsl:template>
<axsl:template mode="generate-id-2" match="node()">
<axsl:text>U.</axsl:text>
<axsl:number count="*" level="multiple"/>
<axsl:text>n</axsl:text>
<axsl:number count="node()"/>
</axsl:template>
<axsl:template mode="generate-id-2" match="@*">
<axsl:text>U.</axsl:text>
<axsl:number count="*" level="multiple"/>
<axsl:text>_</axsl:text>
<axsl:value-of select="string-length(local-name(.))"/>
<axsl:text>_</axsl:text>
<axsl:value-of select="translate(name(),':','.')"/>
</axsl:template>
<!--Strip characters-->
<axsl:template priority="-1" match="text()"/>

<!--SCHEMA METADATA-->
<axsl:template match="/">
<svrl:schematron-output xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" schemaVersion="ISO19757-3" title="SED-ML Schematron schema">
<axsl:comment>
<axsl:value-of select="$archiveDirParameter"/>   
		 <axsl:value-of select="$archiveNameParameter"/>  
		 <axsl:value-of select="$fileNameParameter"/>  
		 <axsl:value-of select="$fileDirParameter"/>
</axsl:comment>
<svrl:text>
		This schema is not a full replacement for the SED-ML XML schema, but
		instead provides
		validation that is not possible in XML schema. This includes:

		1. Non-unique values for 'id' attributes across a document.
		2. Validation of cross-references between elements.
		3. Validation of the relations between attributes of the
		UniformTimeCourse element that configure a simulation.
		4. Validation that variables referred to in a MathML expression are
		defined as variables or parameters.
	</svrl:text>
<svrl:ns-prefix-in-attribute-values prefix="sed" uri="http://sed-ml.org/sed-ml/level1/version2"/>
<svrl:ns-prefix-in-attribute-values prefix="math" uri="http://www.w3.org/1998/Math/MathML"/>
<svrl:active-pattern>
<axsl:attribute name="id">UniformTimeCourse</axsl:attribute>
<axsl:attribute name="name">Checking numeric values of uniform time courses</axsl:attribute>
<axsl:apply-templates/>
</svrl:active-pattern>
<axsl:apply-templates mode="M4" select="/"/>
<svrl:active-pattern>
<axsl:attribute name="id">targetOrSymbol</axsl:attribute>
<axsl:attribute name="name"> Checking a variable has a target or a symbol, but not both.
		</axsl:attribute>
<axsl:apply-templates/>
</svrl:active-pattern>
<axsl:apply-templates mode="M13" select="/"/>
<svrl:active-pattern>
<axsl:attribute name="id">duplicates</axsl:attribute>
<axsl:attribute name="name">Checking no duplicate IDs </axsl:attribute>
<axsl:apply-templates/>
</svrl:active-pattern>
<axsl:apply-templates mode="M14" select="/"/>
<svrl:active-pattern>
<axsl:attribute name="id">xreferences</axsl:attribute>
<axsl:attribute name="name">Checking cross references </axsl:attribute>
<axsl:apply-templates/>
</svrl:active-pattern>
<axsl:apply-templates mode="M15" select="/"/>
</svrl:schematron-output>
</axsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema">SED-ML Schematron schema</svrl:text>

<!--PATTERN UniformTimeCourseChecking numeric values of uniform time courses-->
<svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema">Checking numeric values of uniform time courses</svrl:text>

	<!--RULE -->
<axsl:template mode="M4" priority="1000" match="sed:uniformTimeCourse">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:uniformTimeCourse"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="@initialTime &gt;= 0 "/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="@initialTime &gt;= 0">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>Initial time must be &gt;= than 0.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="@outputStartTime &gt;= @initialTime"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="@outputStartTime &gt;= @initialTime">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>Output start time must be &gt;= than initialTime.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="@outputEndTime &gt;= @outputStartTime"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="@outputEndTime &gt;= @outputStartTime">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>Output end time must be &gt;= than output start
				time.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="@numberOfPoints &gt;= 0"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="@numberOfPoints &gt;= 0">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>There must be more than zero points output.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M4" select="@*|*"/>
</axsl:template>
<axsl:template mode="M4" priority="-1" match="text()"/>
<axsl:template mode="M4" priority="-2" match="@*|node()">
<axsl:apply-templates mode="M4" select="@*|*"/>
</axsl:template>

<!--PATTERN targetOrSymbol Checking a variable has a target or a symbol, but not both.
		-->
<svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema"> Checking a variable has a target or a symbol, but not both.
		</svrl:text>

	<!--RULE -->
<axsl:template mode="M13" priority="1001" match="sed:variable[@target]">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:variable[@target]"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="not(@symbol)"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="not(@symbol)">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text> Variable cannot have
				target and symbol defined.</svrl:text> <svrl:diagnostic-reference diagnostic="notTargetAndSymbol">

			The
			<axsl:text/>
<axsl:value-of select="name(.)"/>
<axsl:text/>
			with id '
			<axsl:text/>
<axsl:value-of select="@id"/>
<axsl:text/>
			' must have either a target XPath expression, identifying a model
			variable,
			or a symbol, representing an implicit variable, but not both.
		</svrl:diagnostic-reference>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M13" select="@*|*"/>
</axsl:template>

	<!--RULE -->
<axsl:template mode="M13" priority="1000" match="sed:variable">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:variable"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="@target | @symbol"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="@target | @symbol">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text> Variable must
				have either a target or a symbol attribute.</svrl:text> <svrl:diagnostic-reference diagnostic="targetOrSymbol">

			The
			<axsl:text/>
<axsl:value-of select="name(.)"/>
<axsl:text/>
			with id '
			<axsl:text/>
<axsl:value-of select="@id"/>
<axsl:text/>
			' must have either a target XPath expression, identifying a model
			variable,
			or a symbol, representing an implicit variable.
		</svrl:diagnostic-reference>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M13" select="@*|*"/>
</axsl:template>
<axsl:template mode="M13" priority="-1" match="text()"/>
<axsl:template mode="M13" priority="-2" match="@*|node()">
<axsl:apply-templates mode="M13" select="@*|*"/>
</axsl:template>

<!--PATTERN duplicatesChecking no duplicate IDs -->
<svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema">Checking no duplicate IDs </svrl:text>

	<!--RULE -->
<axsl:template mode="M14" priority="1000" match="sed:*[@id and not(ancestor::sed:newXML)]">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:*[@id and not(ancestor::sed:newXML)]"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('allIds', @id)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('allIds', @id)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Duplicated id in element "
				<axsl:text/>
<axsl:value-of select="name(.)"/>
<axsl:text/>
				" - "
				<axsl:text/>
<axsl:value-of select="@id"/>
<axsl:text/>
				".
			</svrl:text> <svrl:diagnostic-reference diagnostic="failedUniqueIds">

			The Id '
			<axsl:text/>
<axsl:value-of select="@id"/>
<axsl:text/>
			' for the element
			<axsl:text/>
<axsl:value-of select="name(.)"/>
<axsl:text/>
			is not unique in this document. To be valid SED-ML,
			all Id attribute values should be unique.
		</svrl:diagnostic-reference>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M14" select="@*|*"/>
</axsl:template>
<axsl:template mode="M14" priority="-1" match="text()"/>
<axsl:template mode="M14" priority="-2" match="@*|node()">
<axsl:apply-templates mode="M14" select="@*|*"/>
</axsl:template>

<!--PATTERN xreferencesChecking cross references -->
<svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema">Checking cross references </svrl:text>

	<!--RULE -->
<axsl:template mode="M15" priority="1005" match="sed:task">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:task"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('modelid', @modelReference)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('modelid', @modelReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Model reference '
				<axsl:text/>
<axsl:value-of select="@modelReference"/>
<axsl:text/>
				' did not refer to the id of a model element.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>

		<!--REPORT -->
<axsl:if test="count(key('modelid', @modelReference)) = 1">
<svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('modelid', @modelReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Model reference '
				<axsl:text/>
<axsl:value-of select="@modelReference"/>
<axsl:text/>
				' matched to a model in task '
				<axsl:text/>
<axsl:value-of select="@id"/>
<axsl:text/>
				'.
			</svrl:text>
</svrl:successful-report>
</axsl:if>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('simid', @simulationReference))               +count(key('steadystateid', @simulationReference))              +count(key('onestepid', @simulationReference))               = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('simid', @simulationReference)) +count(key('steadystateid', @simulationReference)) +count(key('onestepid', @simulationReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Simulation reference '
				<axsl:text/>
<axsl:value-of select="@simulationReference"/>
<axsl:text/>
				' did not refer to the id of a simulation element.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>

		<!--REPORT -->
<axsl:if test="count(key('simid', @simulationReference)) = 1">
<svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('simid', @simulationReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Simulation reference '
				<axsl:text/>
<axsl:value-of select="@simulationReference"/>
<axsl:text/>
				' matched to a simulation in task '
				<axsl:text/>
<axsl:value-of select="@id"/>
<axsl:text/>
				'.
			</svrl:text>
</svrl:successful-report>
</axsl:if>
<axsl:apply-templates mode="M15" select="@*|*"/>
</axsl:template>

	<!--RULE -->
<axsl:template mode="M15" priority="1004" match="sed:variable">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:variable"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('taskid', @taskReference)) +count(key('repeatedtaskid', @taskReference)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('taskid', @taskReference)) +count(key('repeatedtaskid', @taskReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Variable '
				<axsl:text/>
<axsl:value-of select="@id"/>
<axsl:text/>
				' referred to unknown task '
				<axsl:text/>
<axsl:value-of select="@taskReference"/>
<axsl:text/>
				'.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M15" select="@*|*"/>
</axsl:template>

	<!--RULE -->
<axsl:template mode="M15" priority="1003" match="math:ci | math:csymbol">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="math:ci | math:csymbol"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test=" (count (ancestor::sed:dataGenerator/sed:listOfVariables/sed:variable[@id = current()/text()]) ) +       (count (ancestor::sed:dataGenerator/sed:listOfParameters/sed:parameter[@id = current()/text()]) ) +       (count (ancestor::sed:repeatedTask/sed:listOfRanges/sed:*[@id = current()/text()]) )       = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="(count (ancestor::sed:dataGenerator/sed:listOfVariables/sed:variable[@id = current()/text()]) ) + (count (ancestor::sed:dataGenerator/sed:listOfParameters/sed:parameter[@id = current()/text()]) ) + (count (ancestor::sed:repeatedTask/sed:listOfRanges/sed:*[@id = current()/text()]) ) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Variable '
				<axsl:text/>
<axsl:value-of select="text()"/>
<axsl:text/>
				' not found in
				.
				for
				<axsl:text/>
<axsl:value-of select="text()"/>
<axsl:text/>
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M15" select="@*|*"/>
</axsl:template>

	<!--RULE -->
<axsl:template mode="M15" priority="1002" match="sed:dataSet">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:dataSet"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('dgid', @dataReference)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('dgid', @dataReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Data reference '
				<axsl:text/>
<axsl:value-of select="@dataReference"/>
<axsl:text/>
				' did not refer to the id of a DataGenerator element.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M15" select="@*|*"/>
</axsl:template>

	<!--RULE -->
<axsl:template mode="M15" priority="1001" match="sed:curve">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:curve"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('dgid', @xDataReference)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('dgid', @xDataReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				X Data reference '
				<axsl:text/>
<axsl:value-of select="@xDataReference"/>
<axsl:text/>
				' did not refer to the id of a DataGenerator element.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('dgid', @yDataReference)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('dgid', @yDataReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Y Data reference '
				<axsl:text/>
<axsl:value-of select="@yDataReference"/>
<axsl:text/>
				' did not refer to the id of a DataGenerator element.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M15" select="@*|*"/>
</axsl:template>

	<!--RULE -->
<axsl:template mode="M15" priority="1000" match="sed:surface">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" context="sed:surface"/>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('dgid', @xDataReference)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('dgid', @xDataReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				X Data reference '
				<axsl:text/>
<axsl:value-of select="@xDataReference"/>
<axsl:text/>
				' did not refer to the id of a DataGenerator element.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('dgid', @yDataReference)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('dgid', @yDataReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Y Data reference '
				<axsl:text/>
<axsl:value-of select="@yDataReference"/>
<axsl:text/>
				' did not refer to the id of a DataGenerator element.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>

		<!--ASSERT -->
<axsl:choose>
<axsl:when test="count(key('dgid', @yDataReference)) = 1"/>
<axsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" test="count(key('dgid', @yDataReference)) = 1">
<axsl:attribute name="location">
<axsl:apply-templates mode="schematron-get-full-path" select="."/>
</axsl:attribute>
<svrl:text>
				Z Data reference '
				<axsl:text/>
<axsl:value-of select="@zDataReference"/>
<axsl:text/>
				' did not refer to the id of a DataGenerator element.
			</svrl:text>
</svrl:failed-assert>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M15" select="@*|*"/>
</axsl:template>
<axsl:template mode="M15" priority="-1" match="text()"/>
<axsl:template mode="M15" priority="-2" match="@*|node()">
<axsl:apply-templates mode="M15" select="@*|*"/>
</axsl:template>
</axsl:stylesheet>
