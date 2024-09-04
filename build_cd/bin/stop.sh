#!/bin/bash
#author ivan

function stop(){
    #Get PID
    COMPONENT_PID=`ps -ef| grep "dct"|grep -v grep|awk '{print $2}' `
    echo "dct component COMPONENT_PID ${COMPONENT_PID}"
    if [[ ${COMPONENT_PID} != "" ]] ; then
        
        for ((k=0; k<3; k++))
        do
            #1.����ɱ����ѡһ��
            kill -9 ${COMPONENT_PID}
            sleep 3
            #2.���ɱ����ѡһ��
            #�Զ����߼�
            
            TMP_PID=`ps -ef| grep "dct"|grep -v grep|awk '{print $2}' `
            
            if [[ ${COMPONENT_PID} = ${TMP_PID} ]] ; then
                if [[ $k -ge 2 ]] ; then
                    #�ʺ�"���ɱ"��ɱ������һ��
                    #kill -9 ${COMPONENT_PID}
                    #sleep 2
                    continue
                fi
            else
                break
            fi
        done
    fi
}
stop