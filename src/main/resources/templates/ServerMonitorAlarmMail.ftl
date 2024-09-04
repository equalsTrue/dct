<html lang="en">
<body>
<table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
    <tbody>
    <tr>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b>产品</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b >服务器IP</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b >时间</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b >用户国家</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b >成功率</b></td>
    </tr>

    <#list data as user>
        <tr>
            <#if user.packageName??>
                <td rowspan="${user.rowSpan}" ><b>${user.packageName}</b></td>
            <#else>
            </#if>
            <td colspan="4" style="text-align:center;vertical-align:middle"><b>${user.ip}&nbsp;&nbsp;</b></td>
            <td colspan="4" style="text-align:center;vertical-align:middle"><b>${user.time}&nbsp;&nbsp;</b></td>
            <td colspan="4" style="text-align:center;vertical-align:middle"><b>${user.userCountry}&nbsp;&nbsp;</b></td>
            <td colspan="4" style="text-align:center;vertical-align:middle"><b>${user.connectRate}%&nbsp;&nbsp;</b></td>
        </tr>
    </#list>

    </tbody>
</table>
</body>
</body>
</html>