#!/usr/bin/env bash
#DIR变量为当前shell脚本的目录
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
#使用source命令导入平级目录下的配置文件,引入以后就可以直接用配置中的变量名来获取文件中的值了
source $DIR"/"common.sh
#! 请在 Java 项目根目录下执行

mvn clean package -Dmaven.test.skip=true

echo
if [ $?  -eq 0 ]; then
    echo $SPLIT_STR
    echo -e "[$(__green "dct打包成功")]"
    echo $SPLIT_STR
    echo
else
    echo $SPLIT_STR
    echo -e "[$(__red "dct打包失败")]"
    echo $SPLIT_STR
    echo
    exit 1
fi