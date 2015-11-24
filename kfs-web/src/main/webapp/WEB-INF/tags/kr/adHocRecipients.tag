<%--
 Copyright 2005-2007 The Kuali Foundation

 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.opensource.org/licenses/ecl2.php

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>


<c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS] and not KualiForm.suppressAllButtons}">
    <kul:tab tabTitle="Ad Hoc Recipients" defaultOpen="false" tabErrorKey="${Constants.AD_HOC_ROUTE_ERRORS}">
        <div class="tab-container">
            <h3>Person Requests</h3>
            <table class="datatable adhoc person standard" summary="view/edit ad hoc recipients">
                <tr class="header">
                    <kul:htmlAttributeHeaderCell
                            attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.id}"
                            scope="col"
                            />
                    <kul:htmlAttributeHeaderCell
                            attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.actionRequested}"
                            scope="col"
                            />
                    <kul:htmlAttributeHeaderCell
                            literalLabel="Actions"
                            scope="col"
                            />
                </tr>
                <tr>
                    <kul:checkErrors keyMatch="newAdHocRoutePerson.id" />
                    <td class="infoline">
                        <kul:user userIdFieldName="newAdHocRoutePerson.id"
                                  userId="${KualiForm.newAdHocRoutePerson.id}"
                                  universalIdFieldName=""
                                  universalId=""
                                  userNameFieldName="newAdHocRoutePerson.name"
                                  userName="${KualiForm.newAdHocRoutePerson.name}"
                                  readOnly="${displayReadOnly}"
                                  fieldConversions="principalName:newAdHocRoutePerson.id,name:newAdHocRoutePerson.name"
                                  lookupParameters="newAdHocRoutePerson.id:principalName"
                                  hasErrors="${hasErrors}"
                                />
                    </td>
                    <td class="infoline" >
                        <html:hidden property="newAdHocRoutePerson.type"/>
                        <c:set var="accessibleTitle" value="${DataDictionary.AdHocRoutePerson.attributes.actionRequested.label}"/>
                        <c:set var="accessibleTitle2" value="${DataDictionary.AdHocRouteWorkgroup.attributes.actionRequested.label}"/>
                        <c:if test="${(DataDictionary.AdHocRoutePerson.attributes.actionRequested.required == true) && readOnly != true}">
                            <c:set var="accessibleTitle" value="${Constants.REQUIRED_FIELD_SYMBOL} ${accessibleTitle}"/>
                        </c:if>
                        <c:if test="${(DataDictionary.AdHocRouteWorkgroup.attributes.actionRequested.required == true) && readOnly != true}">
                            <c:set var="accessibleTitle2" value="${Constants.REQUIRED_FIELD_SYMBOL} ${accessibleTitle2}"/>
                        </c:if>
                        <html:select title="${accessibleTitle}" property="newAdHocRoutePerson.actionRequested">
                            <c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
                            <html:options collection="actionRequestCodes" property="key" labelProperty="value"/>
                        </html:select>
                    </td>
                    <td class="infoline">
                        <html:submit property="methodToCall.insertAdHocRoutePerson" title="Insert Additional Ad Hoc Person" alt="Insert Additional Ad Hoc Person" styleClass="tinybutton btn btn-green" value="Add"/>
                    </td>
                </tr>
                <logic:iterate name="KualiForm" id="person" property="adHocRoutePersons" indexId="ctr">
                    <tr>
                        <td class="datacell">
                            <kul:checkErrors keyMatch="adHocRoutePerson[${ctr}].id" />
                            <kul:user userIdFieldName="adHocRoutePerson[${ctr}].id"
                                      userId="${KualiForm.document.adHocRoutePersons[ctr].id}"
                                      universalIdFieldName=""
                                      universalId=""
                                      userNameFieldName="adHocRoutePerson[${ctr}].name"
                                      userName="${KualiForm.document.adHocRoutePersons[ctr].name}"
                                      readOnly="${displayReadOnly}"
                                      fieldConversions="principalName:adHocRoutePerson[${ctr}].id,name:adHocRoutePerson[${ctr}].name"
                                      lookupParameters="adHocRoutePerson[${ctr}].id:principalName"
                                      hasErrors="${hasErrors}"
                                    />
                        </td>
                        <td class="datacell" >
                            <html:hidden property="adHocRoutePerson[${ctr}].type"/>
                            <html:hidden property="adHocRoutePerson[${ctr}].versionNumber"/>
                            <html:select title="${accessibleTitle}" property="adHocRoutePerson[${ctr}].actionRequested">
                                <c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
                                <html:options collection="actionRequestCodes" property="key" labelProperty="value"/>
                            </html:select>
                        </td>
                        <td class="datacell" >
                            <html:submit property="methodToCall.deleteAdHocRoutePerson.line${ctr}" title="Delete Additional Ad Hoc Person" alt="Delete Additional Ad Hoc Person" styleClass="tinybutton btn btn-red" value="Delete"/>
                        </td>
                    </tr>
                </logic:iterate>
            </table>

            <h3>Ad Hoc Group Requests</h3>
            <table class="datatable adhoc group standard" summary="view/edit ad hoc recipients">
                <tr class="header">
                    <kul:htmlAttributeHeaderCell
                            attributeEntry="${DataDictionary.PersonDocumentGroup.attributes.namespaceCode}"
                            scope="col" forceRequired="true"
                            />
                    <kul:htmlAttributeHeaderCell
                            attributeEntry="${DataDictionary.PersonDocumentGroup.attributes.groupName}"
                            scope="col" forceRequired="true"
                            />
                    <kul:htmlAttributeHeaderCell
                            attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.actionRequested}"
                            scope="col"
                            />
                    <kul:htmlAttributeHeaderCell
                            literalLabel="Actions"
                            scope="col"
                            />
                </tr>
                <tr>
                    <td class="infoline" >
                        <kul:htmlControlAttribute
                                property="newAdHocRouteWorkgroup.recipientNamespaceCode"
                                attributeEntry="${DataDictionary.PersonDocumentGroup.attributes.namespaceCode}"
                                readOnly="${displayReadOnly}"
                                />
                    </td>
                    <td class="infoline" >
                        <kul:htmlControlAttribute
                                property="newAdHocRouteWorkgroup.recipientName"
                                attributeEntry="${DataDictionary.PersonDocumentGroup.attributes.groupName}"
                                readOnly="${displayReadOnly}"
                                />
                        <kul:workflowWorkgroupLookup
                                fieldConversions="namespaceCode:newAdHocRouteWorkgroup.recipientNamespaceCode,name:newAdHocRouteWorkgroup.recipientName"
                                lookupParameters="newAdHocRouteWorkgroup.recipientNamespaceCode:namespaceCode,newAdHocRouteWorkgroup.recipientName:name"
                                />
                    </td>
                    <td class="infoline" >
                        <html:hidden property="newAdHocRouteWorkgroup.type"/>
                        <html:select title="${accessibleTitle2}" property="newAdHocRouteWorkgroup.actionRequested">
                            <c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
                            <html:options collection="actionRequestCodes" property="key" labelProperty="value"/>
                        </html:select>
                    </td>
                    <td class="infoline">
                        <html:submit property="methodToCall.insertAdHocRouteWorkgroup" title="Insert Additional Ad Hoc Workgroup" alt="Insert Additional Ad Hoc Workgroup" styleClass="tinybutton btn btn-green" value="Add"/>
                    </td>
                </tr>
                <logic:iterate name="KualiForm" id="workgroup" property="adHocRouteWorkgroups" indexId="ctr">
                    <tr>
                        <td class="datacell">
                            <kul:htmlControlAttribute
                                    property="adHocRouteWorkgroup[${ctr}].recipientNamespaceCode"
                                    attributeEntry="${DataDictionary.PersonDocumentGroup.attributes.namespaceCode}"
                                    readOnly="displayReadOnly"
                                    />
                        </td>
                        <td class="datacell">
                            <kul:htmlControlAttribute
                                    property="adHocRouteWorkgroup[${ctr}].recipientName"
                                    attributeEntry="${DataDictionary.PersonDocumentGroup.attributes.groupName}"
                                    readOnly="displayReadOnly"
                                    />
                            <kul:workflowWorkgroupLookup
                                    fieldConversions="namespaceCode:adHocRouteWorkgroup[${ctr}].recipientNamespaceCode,name:adHocRouteWorkgroup[${ctr}].recipientName"
                                    lookupParameters="adHocRouteWorkgroup[${ctr}].recipientNamespaceCode:namespaceCode,adHocRouteWorkgroup[${ctr}].recipientName:groupName"
                                    />
                        </td>
                        <td class="datacell" >
                            <html:hidden property="adHocRouteWorkgroup[${ctr}].type"/>
                            <html:hidden property="adHocRouteWorkgroup[${ctr}].versionNumber"/>
                            <html:select title="${accessibleTitle2}" property="adHocRouteWorkgroup[${ctr}].actionRequested">
                                <c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
                                <html:options collection="actionRequestCodes" property="key" labelProperty="value"/>
                            </html:select>
                        </td>
                        <td class="datacell">
                            <html:submit
                                    property="methodToCall.deleteAdHocRouteWorkgroup.line${ctr}"
                                    title="Delete Additional Ad Hoc Workgroup"
                                    alt="Delete Additional Ad Hoc Workgroup"
                                    styleClass="tinybutton btn btn-red"
                                    value="Delete"
                                    />
                        </td>
                    </tr>
                </logic:iterate>
            </table>
        </div>
    </kul:tab>
</c:if>
