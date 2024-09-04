<html lang="en">
<head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></head>
<style>table{font-size:14px;text-align:center;vertical-align:middle} </style>

<body>
<div align="center"><font color="#525252"><table border="1"  bordercolor="black" cellspacing="0px" cellpadding="4px">
            <table border="1"  bordercolor="black" cellspacing="0px" cellpadding="4px">
                <tbody>
                <tr>
                    <td style="color: blue;"><b>产品</b></td>
                    <td style="color: blue;"><b>IP</b></td>
                    <td style="color: blue;"><b>国家</b></td>
                    <td style="color: blue;"><b>详情</b></td>
                </tr>

                <#if data?? && data?size != 0>
                    <#list data as info>
                        <tr>
                            <#if info.packageName??>
                                <td rowspan="${info.rowSpan}" ><b>${info.packageName}</b></td>
                            <#else>
                            </#if>

                            <td><b>${info.ip}</b></td>
                            <td><b>${info.country}</b></td>
                            <#if info.status == 0>
                                <td style="background-color: yellow">
                                    <span>异常端口:
                                    <#if info.aberrantPortList??>
<#--                                        <#list info.aberrantPortList as data>-->
<#--                                            ${(data)}&nbsp;&nbsp;-->
<#--                                        </#list>-->
                                        ${info.aberrantPortList?join(", ")}
                                    </#if>
                                    </span>
                                </td>
                            <#elseif info.status == 1>
                                <td style="background-color: red">
                                    <b> <span>宕机</span></b>
                                </td>
                            <#elseif info.status == 3>
                                <td style="background-color: red">
                                    <span>异常端口:
                                    <#if info.aberrantPortList??>
                                        ${info.aberrantPortList?join(", ")}
                                    </#if>
                                    </span>
                                </td>
                            </#if>
                        </tr>
                    </#list>
                </#if>
                </tbody>
            </table>
        </table><p>此邮件为监控平台自动发送，请勿回复!</p>
</body>
</html>