#!/bin/bash
tomcat_path=`ls /home/work| grep tomcat | grep -v tar | grep -v zip`
echo "`date "+%y-%m-%d %H:%M:%S"`  开始删除日志"
echo "find /home/work/${tomcat_path}/logs -mtime +60"
find /home/work/${tomcat_path}/logs -mtime +60
find /home/work/${tomcat_path}/logs -mtime +60 -exec rm -rf {} \;
echo "`date "+%y-%m-%d %H:%M:%S"`  删除日志完成"