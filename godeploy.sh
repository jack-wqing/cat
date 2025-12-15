#!/bin/bash
SHORT_SERVER_IP=$1
CURRDAY=$(date +%Y%m%d%H%M)
SERVICE_NAME=cat
IDENTITY_FILE=$2;

LOCAL_CAT_HOME_PATH=cat-home
LOCAL_WAR_PATH=cat-home/target
SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.66/webapps

case "${SHORT_SERVER_IP}" in
"dev")
    SERVER_IP=10.96.188.249
    SERVER_USER=root
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.66/webapps
    ;;
"test")
    SERVER_IP=10.96.62.20
    SERVER_USER=root
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.75/webapps
    ;;
"yufa")
    SERVER_IP=10.97.1.240
    SERVER_USER=root
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.75/webapps
    ;;
"prod81")
    SERVER_IP=10.98.18.81
    SERVER_USER=root
    SHORT_SERVER_IP=prod
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.60/webapps
    ;;
"prod70")
    SERVER_IP=10.98.3.70
    SERVER_USER=root
    SHORT_SERVER_IP=prod
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.60/webapps
    ;;
"prod61")
    SERVER_IP=10.98.33.61
    SERVER_USER=root
    SHORT_SERVER_IP=prod
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.60/webapps
    ;;
"prod172")
    SERVER_IP=10.98.44.172
    SERVER_USER=root
    SHORT_SERVER_IP=prod
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.60/webapps
    ;;
"prod58")
    SERVER_IP=10.98.46.58
    SERVER_USER=root
    SHORT_SERVER_IP=prod
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.60/webapps
  ;;
"prod105")
    SERVER_IP=10.98.51.105
    SERVER_USER=root
    SHORT_SERVER_IP=prod
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.60/webapps
    ;;
"prod152")
    SERVER_IP=10.98.9.152
    SERVER_USER=root
    SHORT_SERVER_IP=prod
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.60/webapps
    ;;
"prod166")
    SERVER_IP=10.98.51.166
    SERVER_USER=root
    SHORT_SERVER_IP=prod
    SERVER_WAR_PATH=/home/work/apache-tomcat-8.5.60/webapps
    ;;
esac

function checkSuccess() {
    if [ $? -eq 0 ]; then
        echo "\033[32m $1成功 \033[0m\n"
    else
        echo "\033[33m $1失败 \033[0m\n"
        exit 1
    fi
}

cd ${LOCAL_CAT_HOME_PATH}
mvn clean install -DskipTests
checkSuccess '编译'
cd ../
chmod 600 ${IDENTITY_FILE}

ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "cd $SERVER_WAR_PATH/../ && mkdir -p backup"
checkSuccess '创建备份文件夹'

scp -i ${IDENTITY_FILE} ${LOCAL_WAR_PATH}/cat-alpha-3.0.3-SNAPSHOT.war ${SERVER_USER}@${SERVER_IP}:${SERVER_WAR_PATH}/../backup/cat-${CURRDAY}.war
checkSuccess '文件发送到服务器'

ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "cd $SERVER_WAR_PATH && cp  ../backup/cat-${CURRDAY}.war  ./cat.war"
checkSuccess '拷贝war包'

echo "\033[32m 准备停止服务 \033[0m\n"
ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "ps -ef|grep tomcat|grep -v grep|awk '{print \$2}' | xargs kill -15"
ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "ps -ef|grep tomcat|grep checkalive|awk '{print \$2}' | xargs kill -9"
ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} 'while true ; do  process=`ps -ef| grep tomcat | grep -v grep`; if [ "$process" != "" ]; then sleep 1; echo -e "\033[33m   `date "+%Y-%m-%d %H:%M:%S"` tomcat 进程存在，或者此服务器上运行了多个tomcat进程。 \033[0m"; else echo -e "\033[32m 停止成功 \033[0m\n";break; fi ;done '
checkSuccess '停止服务'

echo "\033[32m 准备启动tomcat \033[0m\n"
ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "cd $SERVER_WAR_PATH && cd ../bin && sh startup.sh  start"
ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} 'while true ; do  process=`ps -ef| grep tomcat | grep -v grep`; if [ "$process" == "" ]; then sleep 1; echo -e "\033[33m   `date "+%Y-%m-%d %H:%M:%S"` 重启中....长时间未启动请检查服务 \033[0m"; else echo -e "\033[32m tomcat启动成功 \033[0m\n"; break; fi ;done '
ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "nohup sh $SERVER_WAR_PATH/../bin/checkalive.sh>$SERVER_WAR_PATH/../logs/alive.log 2>&1 &"
checkSuccess '启动服务'

#
#scp -i ${IDENTITY_FILE} ./checkalive.sh ${SERVER_USER}@${SERVER_IP}:${SERVER_WAR_PATH}/../bin/checkalive.sh
#checkSuccess '发送探活脚本到服务器'
#ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "ps -ef|grep tomcat|grep checkalive|awk '{print \$2}' | xargs kill -9"
#ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "nohup sh $SERVER_WAR_PATH/../bin/checkalive.sh>$SERVER_WAR_PATH/../logs/alive.log 2>&1 &"
#checkSuccess '执行探活脚本'
#
#scp -i ${IDENTITY_FILE} ./deletelog.sh ${SERVER_USER}@${SERVER_IP}:${SERVER_WAR_PATH}/../bin/deletelog.sh
#ssh -i ${IDENTITY_FILE} ${SERVER_USER}@${SERVER_IP} "chmod 777 ${SERVER_WAR_PATH}/../bin/deletelog.sh"
#checkSuccess '发送定时删除日志脚本到服务器'
