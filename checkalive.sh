#!/bin/bash
tomcat_path=`ls /home/work| grep tomcat | grep -v tar | grep -v zip`
function checkalive(){
  process=`ps -ef| grep tomcat | grep -v grep | grep -v checkalive`
  if [ "$process" == "" ];
    then
      return 0;
    else
      return 1;
  fi
}

function startup(){
  echo "`date "+%y-%m-%d %H:%M:%S"`  startup()"
  sh /home/work/${tomcat_path}/bin/startup.sh
}

while true
do
	checkalive
	if [ $? -eq 1 ];
	  then
	    echo "`date "+%y-%m-%d %H:%M:%S"`  服务正常启动"
    else
      echo "`date "+%y-%m-%d %H:%M:%S"`  服务已停止, 正在启动"
      startup
      echo "`date "+%y-%m-%d %H:%M:%S"`  服务启动完成"
  fi
  sleep 60
done