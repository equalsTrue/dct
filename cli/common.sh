#! /bin/bash
PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin
export PATH

#===============================================================================================
#   System Required:  CentOS6.x/7 (32bit/64bit) or Ubuntu
#   Description:  Install install_info_collection.sh for CentOS and Ubuntu
#   Author: vic.zhao
#   Intro:
#===============================================================================================

#clear
VER=1.0.0
echo "#############################################################"
echo "# dachuan-manager Develop Shells"
echo "#"
echo "# Author:vic.zhao"
echo "#"
echo "# Version:$VER"
echo "#############################################################"
echo ""

SPLIT_STR="===================="

__INTERACTIVE=""
if [ -t 1 ] ; then
    __INTERACTIVE="1"
fi

__green(){
    if [ "$__INTERACTIVE" ] ; then
        printf '\033[1;31;32m'
    fi
    printf -- "$1"
    if [ "$__INTERACTIVE" ] ; then
        printf '\033[0m'
    fi
}

__red(){
    if [ "$__INTERACTIVE" ] ; then
        printf '\033[1;31;40m'
    fi
    printf -- "$1"
    if [ "$__INTERACTIVE" ] ; then
        printf '\033[0m'
    fi
}

__yellow(){
    if [ "$__INTERACTIVE" ] ; then
        printf '\033[1;31;33m'
    fi
    printf -- "$1"
    if [ "$__INTERACTIVE" ] ; then
        printf '\033[0m'
    fi
}