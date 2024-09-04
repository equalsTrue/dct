<html lang="en">
<head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></head>
<body>
<style>table{font-size:14px;text-align:center;vertical-align:middle} </style>
<div align="center"><font color="#525252"><table border="1"  bordercolor="black" cellspacing="0px" cellpadding="4px">
            <table border="1"  bordercolor="black" cellspacing="0px" cellpadding="4px">
                <tbody>
                <tr>
                    <td><b>产品</b></td>
                    <td><b>国家</b></td>
                    <td><b>协议</b></td>
                    <td><b>预期数量</b></td>
                    <td><b>实际数量</b></td>
                </tr>

                <#if data?? && data?size != 0>
                    <#list data as info>
                        <tr>
                            <#if info.appName??>
                                <td rowspan="${info.rowSpan}" ><b>${info.appName}</b></td>
                            <#else>
                            </#if>

                            <td><b>${info.country}</b></td>
                            <td><b>${info.procType}</b></td>
                            <td><b>${info.expectedServerCount}</b></td>
                            <td><b>${info.actualServerCount}</b></td>
                        </tr>
                    </#list>
                </#if>
                </tbody>
            </table>
        </table><p>此邮件为监控平台自动发送，请勿回复!</p>
</body>
</body>
</html>