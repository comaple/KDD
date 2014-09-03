#!/bin/sh
APP_NAME=uploadHdfs
APP_HOME=/home/bigdata/program/hdfsupload
LIB_DIR=$APP_HOME/lib
MAIN_CLASS=com.easyminning.hdfs.HDFSService
LOG_FILE=$APP_HOME/log/$APP_NAME.log
#cp -f $JAVA_HOME/bin/java $JAVA_HOME/bin/$APP_NAME
psid=`ps -ef | grep java| awk '/Dmyapp.name='$APP_NAME'/ {print $2}'`
classpath="$APP_HOME"/bin
  for jar in $LIB_DIR/*.jar
  do
    classpath="$classpath":"$jar"
  done
export classpath=.:$classpath:$APP_HOME/etc:$APP_HOME/conf
  echo "classpath is $classpath"
  echo "$psname in launching..."
  nohup java -classpath $classpath -Dmyapp.name=$APP_NAME  -XX:+UseParallelGC -XX:+UseParallelOldGC -server -Xms512m -Xmx1g -Xmn256m $MAIN_CLASS > $LOG_FILE&