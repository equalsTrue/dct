<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<style>table {
        font-size: 14px;
        text-align: center;
        vertical-align: middle
    } </style>

<body>
<div align="center"><font color="#525252">
        <table border="1" bordercolor="black" cellspacing="0px" cellpadding="4px">
            <tbody>
            <tr style="background-color: green;color: white">
                <td><b>天</b></td>
                <td><b>产品代号</b></td>
                <td><b>花费</b></td>
                <td><b>收入</b></td>
                <td><b>利润</b></td>
                <td><b>ROI</b></td>
                <td><b>安装</b></td>
                <td><b>CPI</b></td>
                <td><b>Ecpm</b></td>
                <td><b>美国Ecpm</b></td>
                <td><b>pv</b></td>
                <td><b>美国pv</b></td>
                <td><b>展示</b></td>
                <td><b>美国展示</b></td>
                <td><b>美国展示比</b></td>

            </tr>
            <#assign totalSpend = 0>
            <#assign totalIncome = 0>
            <#assign totalUsIncomeByAdType = 0>
            <#assign totalProfit = 0>
            <#assign totalInstall = 0>
            <#assign totalImpressions = 0>
            <#assign totalImpressionsByAdType = 0>
            <#assign totalUsImpressionsByAdType = 0>
            <#assign totalUserNum = 0>
            <#assign totalIncomeByAdType = 0>
            <#list data as user>
                <tr>

                    <td style="text-align:left;vertical-align:middle"><b>${user.time}</b></td>
                    <td style="text-align:left;vertical-align:middle">
                        <b>${user.appCode}&nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle"><b>${user.spend?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle"><b>${(user.income / 1000000)?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle">
                        <#if user.profit lt 0>
                            <b style="color: red">${user.profit?string("#.##")}</b>
                        <#else>
                            <b>${user.profit?string("#.##")}</b>
                        </#if>
                    </td>
                    <td style="text-align:left;vertical-align:middle">
                        <#if (user.roi/ 1000000) lt 0>
                            <b style="color: red">${(user.roi/ 1000000) ?string("#.##")}</b>
                        <#else>
                            <b>${(user.roi/ 1000000) ?string("#.##")}</b>

                        </#if>
                    </td>
                    <td style="text-align:left;vertical-align:middle"><b>${user.install}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle"><b>${user.cpi?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle"><b>${(user.ecpmByAdType / 1000000)?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle">
                        <b>${(user.usEcpmByAdType / 1000000)?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle"><b>${user.pvByAdType?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle"><b>${user.usPvByAdType?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle"><b>${user.impressionsByAdType?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle"><b>${user.usImpressionsByAdType?string("#.##")}
                            &nbsp;</b></td>
                    <td style="text-align:left;vertical-align:middle">
                        <#if user.impressionsByAdType != 0>
                            <b>${(user.usImpressionsByAdType/user.impressionsByAdType)?string("#.##")}&nbsp;</b>
                        <#else>
                            <b>0.00</b>
                        </#if>
                    </td>
                    <#assign totalSpend = totalSpend + user.spend>
                    <#assign totalIncome = totalIncome + user.income>
                    <#assign totalIncomeByAdType = totalIncomeByAdType + user.incomeByAdType>
                    <#assign totalUsIncomeByAdType = totalUsIncomeByAdType + user.usIncomeByAdType>
                    <#assign totalProfit = totalProfit + user.profit>
                    <#assign totalInstall = totalInstall + user.install>
                    <#assign totalImpressions = totalImpressions + user.impressions>
                    <#assign totalImpressionsByAdType = totalImpressionsByAdType + user.impressionsByAdType>
                    <#assign totalUsImpressionsByAdType = totalUsImpressionsByAdType + user.usImpressionsByAdType>
                    <#assign totalUserNum = totalUserNum + user.userNum>
                </tr>
            </#list>
            <tr style="background-color: yellow">
                <td colspan="2"><b>合计</b></td>
                <#--                花费-->
                <td style="text-align:left;vertical-align:middle"><b>${totalSpend ?string("#.##")}</b></td>
                <#--                收入-->
                <td style="text-align:left;vertical-align:middle"><b>${(totalIncome/1000000) ?string("#.##")}</b></td>
                <#--                利润-->
                <td style="text-align:left;vertical-align:middle"><b>${totalProfit ?string("#.##")}</b></td>
                <#--                ROI-->
                <td style="text-align:left;vertical-align:middle">
                    <#if totalSpend != 0>
                        <b>${((totalIncome/1000000)/totalSpend) ?string("#.##")}</b>
                    <#else>
                        <b>0.00</b>
                    </#if>
                </td>
                <#--                安装-->
                <td style="text-align:left;vertical-align:middle"><b>${totalInstall}</b></td>
                <#--                CPI-->
                <td style="text-align:left;vertical-align:middle">
                    <#if totalInstall != 0>
                        <b>${(totalSpend/totalInstall) ?string("#.##")}</b>
                    <#else>
                        <b>0.00</b>
                    </#if>
                </td>
                <#--  ECPM              -->
                <td style="text-align:left;vertical-align:middle">
                    <#if totalImpressionsByAdType != 0>
                        <b>${((totalIncomeByAdType/1000)/totalImpressionsByAdType) ?string("#.##")}</b>
                    <#else>
                        <b>0.00</b>
                    </#if>
                </td>
                <#--   美国ECPM              -->
                <td style="text-align:left;vertical-align:middle">
                    <#if totalUsImpressionsByAdType != 0>
                        <b>${((totalUsIncomeByAdType/1000)/totalUsImpressionsByAdType) ?string("#.##")}</b>
                    <#else>
                        <b>0.00</b>
                    </#if>
                </td>
                <#-- Pv                -->
                <td style="text-align:left;vertical-align:middle">
                    <#if totalUserNum != 0>
                        <b>${(totalImpressionsByAdType/totalUserNum) ?string("#.##")}</b>
                    <#else>
                        <b>0.00</b>
                    </#if>
                </td>
                <#-- usPv                -->
                <td style="text-align:left;vertical-align:middle">
                    <#if totalUserNum != 0>
                        <b>${(totalUsImpressionsByAdType/totalUserNum) ?string("#.##")}</b>
                    <#else>
                        <b>0.00</b>
                    </#if>
                </td>
                <#--  展示               -->
                <td style="text-align:left;vertical-align:middle"><b>${totalImpressionsByAdType}</b></td>
                <#--  美国展示               -->
                <td style="text-align:left;vertical-align:middle"><b>${totalUsImpressionsByAdType}</b></td>
                <#--  美国展示比              -->
                <td style="text-align:left;vertical-align:middle">
                    <#if totalImpressionsByAdType != 0>
                        <b>${totalUsImpressionsByAdType/totalImpressionsByAdType}</b>
                    <#else>
                        <b>0.00</b>
                    </#if>
                </td>

            </tr>
            </tbody>
        </table>
        <p>此邮件为系统自动发送，请勿回复</p>
</body>
</html>