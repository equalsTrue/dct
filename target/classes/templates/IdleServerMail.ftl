<html lang="en">
<body>
<table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
    <tbody>
    <tr>
        <td colspan="4"><b>服务器</b></td>
        <td colspan="4"><b>描述</b></td>
    </tr>
    <#if result?exists>
        <#list result as server>
            <tr>
            <td colspan="4">${server.ip}</td>
            <#if server.description='0'>
                <td colspan="4">连接数小于10</td>
            </#if>
            <#if server.description='1'>
                <td colspan="4">流量小于100M</td>
            </#if>
            <#if server.description='2'>
                <td colspan="4">连接数小于10,流量小于100M</td>
            </#if>
            </tr>
        </#list>
    </#if>
    </tbody>
</table>
</body>
</body>
</html>