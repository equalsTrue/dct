#!/usr/bin/env bash
#DIR变量为当前shell脚本的目录
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
#使用source命令导入平级目录下的配置文件,引入以后就可以直接用配置中的变量名来获取文件中的值了
source $DIR"/"common.sh
#! 请在 Java 项目根目录下执行
if [ $?  -eq 0 ]; then
    bash $DIR"/"package.sh
    if [ $?  -eq 0 ]; then
      bash $DIR"/"deploy.sh $1
    fi
fi

