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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<kul:page showDocumentInfo="false" headerTitle="Cash Drawer Correction" docTitle="Cash Drawer Correction" htmlFormAction="cashDrawerCorrection" transactionalDocument="false">

    <html:hidden property="campusCode"/>

    <kul:tabTop tabTitle="Cash Drawer Corrections" defaultOpen="true" tabErrorKey="cashDrawerErrors">
        <div class="tab-container" align="center">
            <h3>Cash Drawer for ${KualiForm.cashDrawer.campusCode}</h3>
            <fp:cashDrawerCurrencyCoin cashDrawerProperty="cashDrawer" readOnly="false" showCashDrawerSummary="false"/>
        </div>
    </kul:tabTop>

    <div id="globalbuttons" class="globalbuttons">
        <html:submit styleClass="btn btn-default" property="methodToCall.saveCashDrawer" alt="save cash drawer corrections" title="save corrections" value="Save"/>
        <html:submit styleClass="btn btn-default" property="methodToCall.cancelCorrections" alt="cancel" title="cancel" value="Cancel"/>
    </div>
</kul:page>
