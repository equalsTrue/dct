
<html lang="en">
<head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></head>
<body>
<style>table{font-size:14px;text-align:center;vertical-align:middle} </style>
<div align="center"><font color="#525252"><table border="1"  bordercolor="black" cellspacing="0px" cellpadding="4px">
            <table border="1"  bordercolor="black" cellspacing="0px" cellpadding="4px">
                <tbody>
                <tr>
                    <td>产品</td>
                    <td>手机号</td>
                    <td>归属地</td>
                    <td>运营商</td>
                    <td>认证人</td>
                </tr>

                <#list data as user>
                    <tr>
                        <td><b>${user.productName}</b></td>
                        <td><b>${user.phoneNumber}&nbsp;&nbsp;</b></td>
                        <td><b>${user.attributablePlace}&nbsp;&nbsp;</b></td>
                        <td><b>${user.carier}&nbsp;&nbsp;</b></td>
                        <td><b>${user.owner}&nbsp;&nbsp;</b></td>
                    </tr>
                </#list>


                </tbody>
            </table>
        </table><p>此邮件为监控平台自动发送，请勿回复!</p>
</body>
</body>
</html>