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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="control" classname="org.kuali.kfs.krad.uif.control.CheckboxGroupControl"/>
<tiles:useAttribute name="field" classname="org.kuali.kfs.krad.uif.field.InputField"/>

<%--
    Group of HTML Checkbox Inputs

 --%>

<form:checkboxes id="${field.id}" path="${field.bindingInfo.bindingPath}" disabled="${control.disabled}"
                 items="${control.options}" itemValue="key" itemLabel="value"
                 cssClass="${control.styleClassesAsString}" delimiter="${control.delimiter}"
                 tabindex="${control.tabIndex}"/>
