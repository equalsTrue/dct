<html lang="en">
<body>
<table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
    <tbody>
    <tr>
        <td colspan="4"><b>服务器</b></td>
        <td colspan="4"><b>到期时间</b></td>
    </tr>
    <#if result?exists>
        <#list result?keys as key>
            <tr>
            <td colspan="4">${key}</td>
            <td colspan="4">${result[key]}</td>
            </tr>
        </#list>
    </#if>
    </tbody>
</table>
</body>
</body>
</html>