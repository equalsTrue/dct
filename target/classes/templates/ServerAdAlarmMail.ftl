<html lang="en">
<head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></head>
<body>
<style>table{font-size:14px;text-align:center;vertical-align:middle} </style>
<div align="center"><font color="#525252"><table border="1"  bordercolor="black" cellspacing="0px" cellpadding="4px">
            <table border="1"  bordercolor="black" cellspacing="0px" cellpadding="4px">
                <tbody>
                <tr>
                    <td>产品</td>
                    <td>服务器国家</td>
                    <td>IP</td>
                    <td>展示</td>
                    <td>ECPM</td>
                </tr>

                <#list data as user>
                    <tr>
                    <#if user.appId??>
                        <td rowspan="${user.rowSpan}" ><b>${user.appId}</b></td>
                    <#else>
                    </#if>
<#--                        <td rowspan="${user.rowSpan}" ><b>${user.appId?if_exists}</b></td>-->
                        <td><b>${user.serverCountry}</b></td>
                        <td><b>${user.serverIp}&nbsp;&nbsp;</b></td>
                        <td><b>${user.showCount}&nbsp;&nbsp;</b></td>
                        <td><b>${(user.avgIncome)* 1000}&nbsp;</b></td>
                    </tr>
                </#list>


                </tbody>
            </table>
        </table><p>此邮件为监控平台自动发送，请勿回复!</p>
</body>
</body>
</html>