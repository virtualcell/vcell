echo on

set JDK="c:\Program Files\Java\jdk1.6.0_32\bin\jar"

REM Win32 nativelibs JAR



cd win32

%JDK% -cvf nativelibWindows.jar *.dll

copy nativelibWindows.jar ..\..\..\VCell_Admin_Scripts\Fei\JavaWebStart\SignJarUtility

 

REM Win64 nativelib JAR

cd ..\win64

%JDK% -cvf nativelibWindows_x64.jar *.dll

copy nativelibWindows_x64.jar ..\..\..\VCell_Admin_Scripts\Fei\JavaWebStart\SignJarUtility

 

REM Mac32 nativelib JAR

cd ..\mac32

%JDK% -cvf nativelibMac.jar *.jnilib

copy nativelibMac.jar ..\..\..\VCell_Admin_Scripts\Fei\JavaWebStart\SignJarUtility

 

REM Mac64 nativelib JAR

cd ..\mac64

%JDK% -cvf nativelibMac_x64.jar *.jnilib

copy nativelibMac_x64.jar ..\..\..\VCell_Admin_Scripts\Fei\JavaWebStart\SignJarUtility

 

REM MacPPC nativelib JAR

cd ..\macppc

%JDK% -cvf nativelibMac_ppc.jar *.jnilib

copy nativelibMac_ppc.jar ..\..\..\VCell_Admin_Scripts\Fei\JavaWebStart\SignJarUtility

 

REM Linux32 nativelib JAR

cd ..\linux32

%JDK% -cvf nativelibLinux.jar *.so

copy nativelibLinux.jar ..\..\..\VCell_Admin_Scripts\Fei\JavaWebStart\SignJarUtility

 

REM Linux64 nativelib JAR

cd ..\linux64

%JDK% -cvf nativelibLinux_x64.jar *.so

copy nativelibLinux_x64.jar ..\..\..\VCell_Admin_Scripts\Fei\JavaWebStart\SignJarUtility