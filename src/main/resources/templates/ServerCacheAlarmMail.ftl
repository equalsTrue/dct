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
            <table border="1" bordercolor="black" cellspacing="0px" cellpadding="4px">
                <tbody>
                <tr>
                    <td style="color: blue;"><b>产品</b></td>
                    <td style="color: blue;"><b>期望值</b></td>
                    <td style="color: blue;"><b>实际值</b></td>
                </tr>

                <#list data as user>
                    <tr>

                        <td style="text-align:center;vertical-align:middle"><b>${user.appInfoName}</b></td>
                        <td style="text-align:center;vertical-align:middle">
                            <b>${user.serverCacheAlertQuantifier}&nbsp;</b></td>
                        <td style="text-align:center;vertical-align:middle"><b>${user.serverCacheNowNum}
                                &nbsp;</b></td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </table>
        <p>此邮件为监控平台自动发送，请勿回复!</p>
</body>
</html>