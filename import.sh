#!/bin/bash

if [ -n "$HTTP_PROXY"  ] ; then
  # http_proxy variables are in format http://localhost:11111
  # we need to extract host and port from it
  proxyhost=localhost
  proxyport=11111

  proxypattern=h*/
  proxy=${HTTP_PROXY##$proxypattern}
  proxyhost="${proxy%:*}"
  proxyport="${proxy#*:}"

  echo Using proxy settings, host: $proxyhost , port: $proxyport "(from \$HTTP_PROXY variable)"
else
  echo Not using proxy
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
mingw=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
           fi
           ;;
  MINGW*) mingw=true ;;
esac

# For Cygwin and Mingw, ensure paths are in UNIX format before
# anything is touched
if $cygwin ; then
  [ -n "$APPENGINE_HOME" ] &&
    APPENGINE_HOME=`cygpath --unix "$APPENGINE_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
fi
if $mingw ; then
  [ -n "$APPENGINE_HOME" ] &&
    APPENGINE_HOME="`(cd "$APPENGINE_HOME"; pwd)`"
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME="`(cd "$JAVA_HOME"; pwd)`"
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    # IBM's JDK on AIX uses strange locations for the executables
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      JAVACMD="$JAVA_HOME/jre/sh/java"
    elif [ -x "$JAVA_HOME/jre/bin/java" ] ; then
      JAVACMD="$JAVA_HOME/jre/bin/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

CLASSPATH=./war/WEB-INF/classes
for i in ./lib/*.jar
do
  CLASSPATH="$CLASSPATH:$i"
done
for i in ./war/WEB-INF/lib/*.jar
do
  CLASSPATH="$CLASSPATH:$i"
done

if [ -n "$HTTP_PROXY"  ] ; then
  ant_exec_command="exec \"$JAVACMD\" -Dhttp.proxyHost=\"$proxyhost\" -Dhttp.proxyPort=\"$proxyport\" -Dhttps.proxyHost=\"$proxyhost\" -Dhttps.proxyPort=\"$proxyport\" -Dhttp.nonProxyHosts=local\*\|127\* -classpath \"$CLASSPATH\" com.startupbidder.cli.DataImport $*"
else
  ant_exec_command="exec \"$JAVACMD\" -classpath \"$CLASSPATH\" com.startupbidder.cli.DataImport $*"
fi
#echo $ant_exec_command

eval $ant_exec_command

#$JAVA_HOME/bin/java -classpath war/WEB-INF/classes;lib/appengine-api.jar;lib/appengine-remote-api.jar;lib/commons-cli-1.2.jar com.startupbidder.cli.DataImport $*


