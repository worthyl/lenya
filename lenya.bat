:: Licensed to the Apache Software Foundation (ASF) under one or more
:: contributor license agreements.  See the NOTICE file distributed with
:: this work for additional information regarding copyright ownership.
:: The ASF licenses this file to You under the Apache License, Version 2.0
:: (the "License"); you may not use this file except in compliance with
:: the License.  You may obtain a copy of the License at
::
::     http://www.apache.org/licenses/LICENSE-2.0
::
:: Unless required by applicable law or agreed to in writing, software
:: distributed under the License is distributed on an "AS IS" BASIS,
:: WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
:: See the License for the specific language governing permissions and
:: limitations under the License.

@echo off
:: -----------------------------------------------------------------------------
:: Lenya Win32 Shell Script
::
:: $Id$
:: -----------------------------------------------------------------------------

:: Configuration variables
::
:: LENYA_HOME
::   Folder that points to the root of the Lenya distribution
::
:: LENYA_LIB
::   Folder containing all the library files needed by the Lenya CLI
::
:: JAVA_HOME
::   Home of Java installation.
::
:: JAVA_OPTIONS
::   Extra options to pass to the JVM
::
:: JAVA_DEBUG_PORT
::   The location where the JVM debug server should listen to
::
:: JETTY_PORT
::   Override the default port for Jetty
::
:: JETTY_ADMIN_PORT
::   The port where the jetty web administration should bind
::
:: JETTY_WEBAPP
::   The directory where the webapp that jetty has to execute is located
::

:: ----- Verify and Set Required Environment Variables -------------------------

if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end
:gotJavaHome

:: ----- Check System Properties -----------------------------------------------

if not "%EXEC%" == "" goto gotExec
if not "%OS%" == "Windows_NT" goto noExecNT
set EXEC=start "Apache Lenya" /D. /MAX
goto gotExec
:noExecNT
set EXEC=
:gotExec

set LENYA_HOME=.
:: echo lenya.bat: using %LENYA_HOME% as home

set LENYA_LIB=%LENYA_HOME%\build\lenya\webapp\WEB-INF\lib
:: echo lenya.bat: using %LENYA_LIB% as lib

set JETTY_PORT=8888

set JETTY_ADMIN_PORT=8889
set JETTY_WEBAPP=%LENYA_HOME%\build\lenya\webapp
:: echo lenya.bat: using %JETTY_WEBAPP% as the webapp directory

set JAVA_DEBUG_PORT=8000

:: ----- Set Up The Classpath --------------------------------------------------

set CP=%LENYA_HOME%\tools\loader

:: ----- Unpack WAR file if needed --------------------------------------------------

if exist build\lenya\webapp goto action

echo        ###############################################
echo        #   First start of Lenya. Unpacking now...    #
echo        ###############################################

mkdir build\lenya\webapp
cd build\lenya\webapp
"%JAVA_HOME%\bin\jar.exe" -xf ..\..\..\lenya.war
cd ..\..\..
mkdir build\lenya\webapp\WEB-INF\logs

:: ----- Check action ----------------------------------------------------------
:action
if ""%1"" == ""cli"" goto doCli
if ""%1"" == ""servlet"" goto doServlet
if ""%1"" == ""servlet-admin"" goto doAdmin
if ""%1"" == ""servlet-debug"" goto doDebug
IF ""%1"" == ""servlet-profile"" goto doProfile
goto doServlet

echo Usage: lenya (action)
echo actions:
echo   cli             Run Lenya from command line
echo   servlet         Run Lenya in a servlet container
echo   servlet-admin   Run Lenya in a servlet container and turn container web administration on
echo   servlet-debug   Run Lenya in a servlet container and turn on remote JVM debug
echo   servlet-profile Run Lenya in a servlet container and turn on JVM profiling
goto end

:: ----- Cli -------------------------------------------------------------------

:doCli
set param=
shift
:cliLoop
if "%1"=="" goto cliLoopEnd
if not "%1"=="" set param=%param% %1
shift
goto cliLoop

:cliLoopEnd

"%JAVA_HOME%\bin\java.exe" %JAVA_OPTIONS% -classpath %CP% -Djava.endorsed.dirs=%LENYA_LIB%\endorsed -Dloader.jar.repositories=%LENYA_LIB% -Dloader.main.class=org.apache.cocoon.Main Loader %param%
goto end

:: ----- Servlet ---------------------------------------------------------------

:doServlet
%EXEC% "%JAVA_HOME%\bin\java.exe" %JAVA_OPTIONS% -classpath %CP% -Djava.endorsed.dirs=%LENYA_LIB%\endorsed -Dwebapp=%JETTY_WEBAPP% -Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser -Djetty.port=%JETTY_PORT% -Djetty.admin.port=%JETTY_ADMIN_PORT% -Dhome=%LENYA_HOME% -Dloader.jar.repositories=%LENYA_HOME%\tools\jetty\lib,%LENYA_LIB%\endorsed -Dloader.main.class=org.mortbay.jetty.Server Loader %LENYA_HOME%\tools\jetty\conf\main.xml
goto end

:: ----- Servlet with Administration Web Interface -----------------------------

:doAdmin
%EXEC% "%JAVA_HOME%\bin\java.exe" %JAVA_OPTIONS% -classpath %CP% -Djava.endorsed.dirs=%LENYA_LIB%\endorsed -Dwebapp=%JETTY_WEBAPP% -Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser -Djetty.port=%JETTY_PORT% -Djetty.admin.port=%JETTY_ADMIN_PORT% -Dhome=%LENYA_HOME% -Dloader.jar.repositories=%LENYA_HOME%\tools\jetty\lib,%LENYA_LIB%\endorsed -Dloader.main.class=org.mortbay.jetty.Server Loader %LENYA_HOME%\tools\jetty\conf\main.xml %LENYA_HOME%\tools\jetty\conf\admin.xml
goto end

:: ----- Servlet Debug ---------------------------------------------------------

:doDebug
%EXEC% "%JAVA_HOME%\bin\java.exe" %JAVA_OPTIONS% -Xdebug -Xrunjdwp:transport=dt_socket,address=%JAVA_DEBUG_PORT%,server=y,suspend=n  -classpath %CP% -Djava.endorsed.dirs=%LENYA_LIB%\endorsed -Dwebapp=%JETTY_WEBAPP% -Dhome=%LENYA_HOME% -Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser -Djetty.port=%JETTY_PORT% -Djetty.admin.port=%JETTY_ADMIN_PORT% -Dloader.jar.repositories=%LENYA_HOME%\tools\jetty\lib,%LENYA_LIB%\endorsed -Dloader.main.class=org.mortbay.jetty.Server Loader %LENYA_HOME%\tools\jetty\conf\main.xml
goto end

:: ----- Servlet Profile ---------------------------------------------------------

:doProfile
%EXEC% "%JAVA_HOME%\bin\java.exe" %JAVA_OPTIONS% -Xrunhprof:heap=all,cpu=samples,thread=y,depth=3 -classpath %CP% -Djava.endorsed.dirs=%LENYA_LIB%\endorsed -Dwebapp=%JETTY_WEBAPP% -Dhome=%LENYA_HOME% -Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser -Djetty.port=%JETTY_PORT% -Djetty.admin.port=%JETTY_ADMIN_PORT% -Dloader.jar.repositories=%LENYA_HOME%\tools\jetty\lib,%LENYA_LIB%\endorsed -Dloader.main.class=org.mortbay.jetty.Server Loader %LENYA_HOME%\tools\jetty\conf\main.xml

:: ----- End -------------------------------------------------------------------

:end
set CP=
set EXEC=

