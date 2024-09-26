<html lang="en">
<body>
<table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
    <tbody>
    <tr>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b>产品名</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b>服务器变更前</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b>需要删除服务器列表</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b>添加新服务器列表</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b>服务器变更后</b></td>
        <td colspan="4" style="text-align:center;vertical-align:middle"><b>失败服务器列表</b></td>
    </tr>

    <#list data as user>
        <tr>
            <td colspan="4" style="text-align:center;vertical-align:middle">
                <#if user.account??>
                    <b>${user.account}</b>
                <#else>
                    <b>N/A</b>
                </#if>
            </td>
            <td colspan="4" style="text-align:center;vertical-align:middle">
                <b>
                    <#if user.serverListBeforeChange?has_content>
                        <#list user.serverListBeforeChange as server>
                            <#if server.ip?has_content && server.ecpm?has_content>
                                ${server.ip}:${server.ecpm}<br>
                            </#if>
                        </#list>
                    <#else>
                        <b>N/A</b>
                    </#if>
                </b>
            </td>
            <td colspan="4" style="text-align:center;vertical-align:middle">
                <b>
                    <#if user.needDeleteServerList?has_content>
                        <#list user.needDeleteServerList as server>
                            <#if server.ip?has_content && server.ecpm?has_content>
                                ${server.ip}:${server.ecpm}<br>
                            </#if>
                        </#list>
                    <#else>
                        <b>N/A</b>
                    </#if>
                </b>
            </td>
            <td colspan="4" style="text-align:center;vertical-align:middle">
                <b>
                    <#if user.addNewServerList?has_content>
                        <#list user.addNewServerList as server>
                            <#if server.ip?has_content && server.ecpm?has_content>
                                ${server.ip}:${server.ecpm}<br>
                            </#if>
                        </#list>
                    <#else>
                        <b>N/A</b>
                    </#if>
                </b>
            </td>
            <td colspan="4" style="text-align:center;vertical-align:middle">
                <b>
                    <#if user.serverListAfterChange?has_content>
                        <#list user.serverListAfterChange as server>
                            <#if server.ip?has_content && server.ecpm?has_content>
                                ${server.ip}:${server.ecpm}<br>
                            </#if>
                        </#list>
                    <#else>
                        <b>N/A</b>
                    </#if>
                </b>
            </td>
            <td colspan="4" style="text-align:center;vertical-align:middle">
                <b>
                    <#if user.failedIpList?has_content>
                        ${user.failedIpList?join(", ")}
                    <#else>
                        <b>N/A</b>
                    </#if>
                </b>
            </td>
        </tr>
    </#list>


    </tbody>
</table>
</body>
</body>
</html>