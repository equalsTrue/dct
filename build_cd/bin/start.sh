#!/bin/bash
#author ivan

source /etc/profile
echo "DEPLOY_ENV=${DEPLOY_ENV}"
curdir=$(cd "$(dirname "$0")"; pwd)
if [ "${DEPLOY_ENV}" = "fat" ];then
    PORT_STRING=8686
elif [ "${DEPLOY_ENV}" = "prod" ];then
    PORT_STRING=0000
fi
jvm_port="6017"
jar_name="dct-0.0.1-SNAPSHOT.jar"

parameterString=$1
parameters=(${parameterString//,/ })
for (( i=0;i<${#parameters[*]};i++ ))
  do
    key_value=${parameters[$i]}
    KV=(${key_value//=/ })
    if [[ ${KV[0]} = aws_access_key ]];then
        aws_access_key=${KV[1]}
    elif [[ ${KV[0]} = aws_secret_access_key ]];then
        aws_secret_access_key=${KV[1]}
    elif [[ ${KV[0]} = NOHUP_DEPLOY_LOG ]];then
        NOHUP_DEPLOY_LOG=${KV[1]}
    fi
done

echo "develop readwrite" > ${curdir}/../../jmxremote.access
echo "develop starp12354" > ${curdir}/../../jmxremote.password
sudo chmod 600 ${curdir}/../../jmxremote.access
sudo chmod 600 ${curdir}/../../jmxremote.password
public_IP=$(ec2metadata |grep local-ipv4|cut -d ':' -f2|sed 's/ //g')
nohup java -jar -Dcom.sun.management.jmxremote.access.file=${curdir}/../../jmxremote.access -Dcom.sun.management.jmxremote.password.file=${curdir}/../../jmxremote.password -Djava.rmi.server.hostname=${public_IP} -Dcom.sun.management.jmxremote.rmi.port=${jvm_port} -Dcom.sun.management.jmxremote.authenticate=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=${jvm_port} -Dali.access_key=LTAIoDMDgOssU1dM -Dali.secret_key=QtDZ6gX7lKrnVWQsR9wDywQKEAu1aP -Daws.access_key=${aws_access_key}  -Daws.secret_access_key=${aws_secret_access_key} ${curdir}/../../target/${jar_name} --spring.profiles.active=${DEPLOY_ENV} --server.port=${PORT_STRING} >/dev/null 2>&1 &

echo "Start.sh END."
