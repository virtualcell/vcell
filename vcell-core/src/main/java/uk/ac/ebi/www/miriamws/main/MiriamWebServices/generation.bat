REM  set classpath
set CLASSPATH=%CLASSPATH%;axis.jar;axis-ant.jar;commons-discovery-0.2.jar;commons-logging-1.0.4.jar;jaxrpc.jar;log4j-1.2.8.jar;saaj.jar;wsdl4j-1.5.1.jar;activation.jar;mailapi_1_3_1.jar

REM  assume JAVA_HOME is set in your environment
%JAVA_HOME%\bin\java org.apache.axis.wsdl.WSDL2Java MiriamWebServices.wsdl