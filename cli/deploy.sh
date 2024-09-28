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
elif [ $1 = "dev" ]; then
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