<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2017 Kuali, Inc.
   -
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   -
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   -
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>Login</title>
    <c:forEach items="${fn:split(ConfigProperties.portal.css.files, ',')}" var="cssFile">
        <c:if test="${fn:length(fn:trim(cssFile)) > 0}">
            <link href="${pageContext.request.contextPath}/${fn:trim(cssFile)}" rel="stylesheet" type="text/css"/>
        </c:if>
    </c:forEach>
    <c:forEach items="${fn:split(ConfigProperties.portal.javascript.files, ',')}" var="javascriptFile">
        <c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
            <script language="JavaScript" type="text/javascript" src="${ConfigProperties.application.url}/${fn:trim(javascriptFile)}"></script>
        </c:if>
    </c:forEach>

    <style type="text/css">
        div.body {
            background-image: url("${ConfigProperties.application.url}/rice-portal/images/os-guy.gif");
            background-repeat: no-repeat;
            padding-top: 5em;
        }

        table#login {
            margin: auto;
            background-color: #dfdda9;
            border: .5em solid #fffdd8;
            /* simple rounded corners for mozilla & webkit */
            -moz-border-radius: 10px;
            -webkit-border-radius: 10px;
        }

        table#login th {
            height: 30px;
            padding-top: .8em;
            padding-bottom: .8em;
            color: #a02919;
            font-size: 2em;
        }

        #login td {
            padding: .2em;
            height: 20px;
        }

        #login .rightTd {
            padding-right: 1.2em;
        }

        #login .leftTd {
            padding-left: 1.2em;
        }

        table#login td#buttonRow {
            padding-top: 1em;
            padding-bottom: .6em;
        }

        div.build {
            float: right;
            color: #dfdda9;
            margin: .3em;
        }

    </style>
</head>

<body OnLoad="document.loginForm.__login_user.focus();">

<div class="build">${ConfigProperties.version} (${ConfigProperties.datasource.ojb.platform})</div>

<form name="loginForm" action="" method="post">

    <kul:csrf/>

    <div class="body">
        <table id="login" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <th colspan="2">Login</th>
            </tr>
            <tr>
                <td class="leftTd" align="right" width="Infinity%">
                    <label>Username:&nbsp;</label>
                </td>
                <td class="rightTd" align="left">
                    <input type="text" name="__login_user" value="" size="20"/>
                </td>
            </tr>
            <c:set var="invalidAuthMsg" value="Invalid username"/>
            <c:if test="${requestScope.showPasswordField}">
                <c:set var="invalidAuthMsg" value="Invalid username or password"/>
                <tr>
                    <td class="leftTd" width="Infinity%" align="right">
                        <label>Password:&nbsp;</label>
                    </td>
                    <td class="rightTd" align="left"><input type="password" name="__login_pw" value="" size="20"/></td>
                </tr>
            </c:if>
            <c:if test="${requestScope.invalidAuth}">
                <tr>
                    <td align="center" colspan="2"><strong>${invalidAuthMsg}</strong></td>
                </tr>
            </c:if>
            <tr>
                <td id="buttonRow" height="30" colspan="2" align="center"><input type="submit" value="Login"/>
                    <!-- input type="image" title="Click to login." value="login" name="imageField" src="${pageContext.request.contextPath}/rice-portal/images/tinybutton-login.gif"/ -->
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</form>
</body>
