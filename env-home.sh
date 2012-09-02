#!/bin/sh
#SDK_VERSION=1.6.3.1
SDK_VERSION=1.7.0
APPENGINE_HOME=../../appengine/appengine-java-sdk-${SDK_VERSION}
#SDK_VERSION=SNAPSHOT
#APPENGINE_HOME=/home/arley/Downloads/googleappengine/googleappengine-read-only/java/build/dist/appengine-java-sdk-SNAPSHOT
APPENGINE_EMAIL=johnarleyburns@gmail.com
ANT_HOME=/home/arley/work/MyWeb/apache-ant-1.8.2
#APPENGINE_ADDRESS=172.16.1.2
#JAVA_TOOL_OPTIONS="-Dsun.net.client.defaultConnectTimeout=10000 -Dsun.net.client.defaultReadTimeout=10000 -Dhttp.proxyHost=proxy10.gps.internal.vodafone.com -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=localhost -Dhttps.proxyHost=proxy10.gps.internal.vodafone.com -Dhttps.proxyPort=8080 -Dhttp.proxySet=true -Dhttps.proxySet=true"
export SDK_VERSION APPENGINE_HOME APPENGINE_EMAIL APPENGINE_ADDRESS ANT_HOME
