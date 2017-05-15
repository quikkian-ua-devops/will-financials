<%--

    The Kuali Financial System, a comprehensive financial management system for higher education.

    Copyright 2005-2017 Kuali, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="methodToCall" required="true" description="A name of the methodToCall for this invisibly rendered button (typically used for the default action)." %>

<c:set var="methodToCallParam" value="methodToCall.${methodToCall}" />

${kfunc:registerEditableProperty(KualiForm, methodToCallParam)}
<input type="image" style="float:left;" class="tinybutton" name="${methodToCallParam}" src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" width="0" height="0" border="0"/>
