<html lang="en">
<body>
<table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
    <tbody>
    <tr>
        <td><b>告警时间</b></td>
        <td style="width: 360px;" colspan="4">${date}</td>
    </tr>
    <tr>
        <td><b>服务器</b></td>
        <td colspan="4">${ip}</td>
    </tr>
    <tr>
        <td><b>服务器状态</b></td>
        <#if status=0>
            <td colspan="4" style="background-color: forestgreen">正常</td>
        </#if>
        <#if status=1>
            <td colspan="4" style="background-color: red">下架</td>
        </#if>
    </tr>
    <tr>
        <td><b>描述</b></td>
        <td colspan="4">${description}</td>
    </tr>
    <tr>
        <td><b>告警id</b></td>
        <td colspan="4">${problemId}</td>
    </tr>
    </tbody>
</table>
</body>
</body>
</html>