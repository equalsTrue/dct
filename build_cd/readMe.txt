1. ��չ��������׼��
   a. ȷ����װ java 1.8
   b. ȷ����װ codedeploy �ͻ���
   c. ȷ������ ��Ӧ��������DEPLOY_ENV(/etc/profile): --spring.profiles.active=${DEPLOY_ENV}
2. ������������Ŀ�������ļ���gitlabÿ����Ŀ��Ŀ¼�£�
    build_cd/
    ������ bin
    ��   ������ checkhealth.sh #��ؼ��ű�
    ��   ������ start.sh #�����ű�(��Ŀ�˿ںŶ���)
    ��   ������ stop.sh #ֹͣ�ű�
    ������ build.sh #����ű������ݲ�ͬ��Ŀ����
    ������ readMe.txt
    ������ S3_Revision #AWS codedeploy-agent ���ýű���һ�㲻��Ҫ�ı�
        ������ appspec.yml
        ������ scripts
            ������ AfterInstall.sh
            ������ ApplicationStart.sh
            ������ ApplicationStop.sh
            ������ DeleteWar.sh
            ������ UpdateApiDoc.sh
            ������ ValidateService.sh
3.  ���������������������ļ���http://gitlab.starpavilion-digital.com/mobilecontent-cloud/cd.git��
    cd/
    ������ {project}
    ��   ������ conf.sh #��Ҫ������Ŀgitlab��ַ������ű�·������ͬ������ͬregion��Ӧʵ��
    
4.  �ݲ���������������
5.  ����Ŀ¼�滮
    ����·����/feige/deploy/${PROJECT_NAME}/${component}
    ����·����/feige/deploy/deployfile/${PROJECT_NAME}/bak/tar
    ��־·����/pdata1/${PROJECT_NAME}  #����ο�log4j����
    ������־��/opt/codedeploy-agent/deployment-root/deployment-logs/codedeploy-agent-deployments.log