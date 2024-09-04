<html lang="en">
<body>
<#if openAccount??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>Androdid开户提醒：</p>

        <#list openAccount?keys as key>
            <tr>
                <td colspan="4">账号</td>
                <td colspan="4">${key}</td>
            </tr>
            <tr>
                <td colspan="4">app</td>
                <td colspan="4">${openAccount[key]}</td>
            </tr>
        </#list>
        <tr>
            <td>通知内容</td>
            <td colspan="4">推广账号异常，请修复</td>
        </tr>
        </tbody>
    </table>
</#if>

<#if keepShow??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>IOS展示维持提醒：</p>

        <#list keepShow?keys as key>
            <tr>
                <td colspan="4">账号</td>
                <td colspan="4">${key}</td>
            </tr>
            <tr>
                <td colspan="4">app</td>
                <td colspan="4">${keepShow[key]}</td>
            </tr>
        </#list>
        <tr>
            <td>通知内容</td>
            <td colspan="4">已累计7天收入到达5刀，账号正常(累计7天正常)，可以开始买1000展示</td>
        </tr>
        </tbody>
    </table>
</#if>

<#if access_android??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>接入安卓提醒：</p>

        <#list access_android?keys as key>
            <tr>
                <td colspan="4">账号</td>
                <td colspan="4">${key}</td>
            </tr>
            <tr>
                <td colspan="4">app</td>
                <td colspan="4">${access_android[key]}</td>
            </tr>
        </#list>
        <tr>
            <td>通知内容</td>
            <td colspan="4">已累计7天1000展示，请开始准备安卓产品</td>
        </tr>
        </tbody>
    </table>
</#if>

<#if start_buy_flow_1??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>IOS买量通知：</p>

        <#list start_buy_flow_1?keys as key>
            <tr>
                <td colspan="4">账号</td>
                <td colspan="4">${key}</td>
            </tr>
            <tr>
                <td colspan="4">app</td>
                <td colspan="4">${start_buy_flow_1[key]}</td>
            </tr>
        </#list>
        <tr>
            <td>通知内容</td>
            <td colspan="4">自然量周已到，需维持7天每天5刀收入</td>
        </tr>
        </tbody>
    </table>
</#if>

<#if start_buy_flow_2??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>安卓买量通知：</p>

        <#--        <tr>-->
        <#--            <td>邮件标题：</td>-->
        <#--        </tr>-->
        <#list start_buy_flow_2?keys as key>
            <tr>
                <td colspan="4">账号</td>
                <td colspan="4">${key}</td>
            </tr>
            <tr>
                <td colspan="4">app</td>
                <td colspan="4">${start_buy_flow_2[key]}</td>
            </tr>
        </#list>
        <tr>
            <td>通知内容</td>
            <td colspan="4">已连续买10刀一周，账号正常(累计7天正常)，可以开始买1000展示</td>
        </tr>
        </tbody>
    </table>
</#if>


<#if start_buy_flow_3??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>纯Androdi买量通知：</p>

        <#list start_buy_flow_3?keys as key>
            <tr>
                <td colspan="4">账号</td>
                <td colspan="4">${key}</td>
            </tr>
            <tr>
                <td colspan="4">app</td>
                <td colspan="4">${start_buy_flow_3[key]}</td>
            </tr>
        </#list>
        <tr>
            <td>通知内容</td>
            <td colspan="4">产品已维持1000展示七天，请调整量级</td>
        </tr>
        </tbody>
    </table>

</#if>

<#if accountFlowLimited??>
<table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
    <tbody>
    <p>账号限流</p>
    <tr>
        <td colspan="4">账号</td>
        <td colspan="4">${accountFlowLimited}</td>
    </tr>
    <tr>
        <td>通知内容</td>
        <td colspan="4">已被限流</td>
    </tr>
    </#if>
    </tbody>
</table>

<#if marketingAccountClose??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>推广账号封户提醒：</p>

        <#list marketingAccountClose?keys as key>
            <tr>
                <td colspan="4">账号</td>
                <td colspan="4">${key}</td>
            </tr>
            <tr>
                <td colspan="4">app</td>
                <td colspan="4">${marketingAccountClose[key]}</td>
            </tr>
        </#list>
        <tr>
            <td>通知内容</td>
            <td colspan="4">该推广账号已封户</td>
        </tr>
        </tbody>
    </table>
</#if>

<#if verifyPin??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>验证PIN 提醒：</p>

        <tr>
            <td colspan="4">账号</td>
            <td colspan="4">${verifyPin}</td>

        </tr>
        <tr>
            <td>通知内容</td>
            <td colspan="4">pin已发出3周，请尽快查收！！！</td>
        </tr>
        </tbody>
    </table>
</#if>

<#if quicklySubmitFile??>
    <table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
        <tbody>
        <p>上传表格提醒：</p>

        <tr>
            <td colspan="4">APP</td>
            <td colspan="4" style="color: #F00">${quicklySubmitFile}</td>

        </tr>
        <tr>
            <td>通知内容</td>
            <td colspan="4" style="color: #F00">请尽快传表格！！！</td>
        </tr>
        </tbody>
    </table>

</#if>

</br>
</br>


</body>
</html>