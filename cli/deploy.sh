#!/usr/bin/env bash
#DIR变量为当前shell脚本的目录
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
#使用source命令导入平级目录下的配置文件,引入以后就可以直接用配置中的变量名来获取文件中的值了
source $DIR"/"common.sh
#! 请在 Java 项目根目录下执行
SOURCE_PATH=../

echo
if [ $1 = "prod" ]; then
  echo "develop readwrite" > ${SOURCE_PATH}/jmxremote.access
  echo "develop starp12354" > ${SOURCE_PATH}/jmxremote.password
  sudo chmod 600 ${SOURCE_PATH}/jmxremote.access
  sudo chmod 600 ${SOURCE_PATH}/jmxremote.password
  public_IP=$(ec2metadata |grep public-ipv4|cut -d ':' -f2|sed 's/ //g')
  nohup java -server  -Dcom.sun.management.jmxremote.access.file=${SOURCE_PATH}/jmxremote.access -Dcom.sun.management.jmxremote.password.file=${SOURCE_PATH}/jmxremote.password -Djava.rmi.server.hostname=${public_IP} -Dcom.sun.management.jmxremote.rmi.port=6011 -Dcom.sun.management.jmxremote.authenticate=true  -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=6011 -jar  $DIR"/../"target/dct-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --server.port=8080 >/dev/null 2>&1 &
elif [ $1 = "dev" ]; then
    nohup java -jar  $DIR"/../"target/dct-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=8688 >/dev/null 2>&1 &
fi

if [ $? -eq 0 ]; then
    echo $SPLIT_STR
    echo -e "[$(__green "dct部署成功")]"
    echo $SPLIT_STR
    echo
else
    echo $SPLIT_STR
    echo -e "[$(__red "dct部署失败")]"
    echo $SPLIT_STR
    exit 1
fi