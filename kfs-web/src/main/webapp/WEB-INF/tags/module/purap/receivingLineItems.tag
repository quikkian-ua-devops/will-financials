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
<%@ tag import="org.kuali.kfs.sys.util.Guid" %>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>

<script language="JavaScript" type="text/javascript" src="dwr/interface/ItemUnitOfMeasureService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/purap/objectInfo.js"></script>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentTypeName}" />
<c:set var="colCount" value="13"/>

<c:if test="${KualiForm.stateFinal}">
	<c:set var="colCount" value="11"/>
</c:if>
<c:set var="tabindexOverrideBase" value="20" />

<kul:tab tabTitle="Items" defaultOpen="true" tabErrorKey="${PurapConstants.LINEITEM_TAB_ERRORS}">
	<div class="tab-container">
		<table class="standard acct-lines side-margins" summary="Items Section">
			<c:if test="${fullEntryMode}">
				<tr>
					<th></th>
					<td colspan="${colCount - 1}" class="datacell" align="right" nowrap="nowrap">
						<div align="right">
							<c:if test="${KualiForm.ableToShowClearAndLoadQtyButtons}">
								<html:submit
										property="methodToCall.loadQty"
										alt="load qty received"
										title="load qty received"
										styleClass="btn btn-default"
										value="Load Qty Received"/>
								<html:submit
										property="methodToCall.clearQty"
										alt="clear qty received"
										title="clear qty received"
										styleClass="btn btn-default"
										value="Clear Qty Received"/>
							</c:if>
							<c:if test="${KualiForm.hideAddUnorderedItem}">
								<html:submit
										property="methodToCall.showAddUnorderedItem"
										alt="add unordered item"
										title="add unordered item"
										styleClass="btn btn-default"
										value="Add Unordered Item"/>
							</c:if>
							<c:if test="${!KualiForm.hideAddUnorderedItem}">
								<button alt="add unordered item" border="0" class="btn btn-default disabled" disabled>
									Add Unordered Item
								</button>
							</c:if>
						</div>
					</td>
				</tr>
			</c:if>

			<tr class="header top">
				<th></th>
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" colspan="2" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemOrderedQuantity}" />
				<kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" useShortLabel="true" noColon="true"/>
				</kul:htmlAttributeHeaderCell>

				<c:if test="${KualiForm.stateFinal == false}">
					<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedPriorQuantity}" />
					<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedToBeQuantity}" />
				</c:if>

				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedTotalQuantity}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReturnedTotalQuantity}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDamagedTotalQuantity}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReasonAddedCode}" />
				<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
			</tr>

			<c:if test="${fullEntryMode and !KualiForm.hideAddUnorderedItem}">
				<tr class="top new">
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemLineNumber}" property="newLineItemReceivingItemLine.itemLineNumber" readOnly="${true}"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemCatalogNumber}" property="newLineItemReceivingItemLine.itemCatalogNumber" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline relative" colspan="2">
						<kul:htmlControlAttribute
								attributeEntry="${itemAttributes.itemDescription}"
								property="newLineItemReceivingItemLine.itemDescription"
								tabindexOverride="${tabindexOverrideBase + 0}"
								styleClass="fullwidth"/>
						<kul:expandedTextArea
								textAreaFieldName="newLineItemReceivingItemLine.itemDescription"
								action="purapItemLine"
								textAreaLabel="description"
								addClass="embed textarea"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemOrderedQuantity}" property="newLineItemReceivingItemLine.itemOrderedQuantity" readOnly="${true}"/>
					</td>
					<td class="infoline" nowrap="nowrap">
						<c:set var="itemUnitOfMeasureCodeField"  value="newLineItemReceivingItemLine.itemUnitOfMeasureCode" />
						<c:set var="itemUnitOfMeasureDescriptionField"  value="newLineItemReceivingItemLine.itemUnitOfMeasure.itemUnitOfMeasureDescription" />
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
							property="${itemUnitOfMeasureCodeField}"
							onblur="loadItemUnitOfMeasureInfo( '${itemUnitOfMeasureCodeField}', '${itemUnitOfMeasureDescriptionField}' );${onblur}" tabindexOverride="${tabindexOverrideBase + 0}"/>
						<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.UnitOfMeasure"
							fieldConversions="itemUnitOfMeasureCode:newLineItemReceivingItemLine.itemUnitOfMeasureCode"
							lookupParameters="'Y':active"/>
						<div id="newLineItemReceivingItemLine.itemUnitOfMeasure.itemUnitOfMeasureDescription.div" class="fineprint">
							<html:hidden write="true" property="${itemUnitOfMeasureDescriptionField}"/>&nbsp;
						</div>
					</td>

					<c:if test="${KualiForm.stateFinal == false}">

					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedPriorQuantity}" property="newLineItemReceivingItemLine.itemReceivedPriorQuantity" readOnly="${true}"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedToBeQuantity}" property="newLineItemReceivingItemLine.itemReceivedToBeQuantity" readOnly="${true}"/>
					</td>
					</c:if>

					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedTotalQuantity}" property="newLineItemReceivingItemLine.itemReceivedTotalQuantity" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReturnedTotalQuantity}" property="newLineItemReceivingItemLine.itemReturnedTotalQuantity" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDamagedTotalQuantity}" property="newLineItemReceivingItemLine.itemDamagedTotalQuantity" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReasonAddedCode}" property="newLineItemReceivingItemLine.itemReasonAddedCode" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline">
						<div class="actions">
							<html:html-button
									property="methodToCall.addItem"
									alt="Insert an Item"
									title="Add an Item"
									styleClass="btn btn-green skinny"
									value="Add"
									innerHTML="<span class=\"fa fa-plus\"></span>"/>
						</div>
					</td>
				</tr>
			</c:if>

			<c:set var="itemCount" value="0"/>
			<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
				<c:if test="${itemLine.itemType.lineItemIndicator == true}">
					<c:set var="itemCount" value="${itemCount + 1}"/>
				</c:if>
			</logic:iterate>

			<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
				<tr class="top line" style="${ctr == itemCount - 1 ? 'border-bottom:1px solid #BBBBBB;' : ''}">
					<th class="infoline" nowrap="nowrap">
						<bean:write name="KualiForm" property="document.item[${ctr}].itemLineNumber"/>
					</th>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemCatalogNumber}"
							property="document.item[${ctr}].itemCatalogNumber"
							extraReadOnlyProperty="document.item[${ctr}].itemCatalogNumber"
							readOnly="${((itemLine.itemTypeCode eq 'ITEM') or not (fullEntryMode))}" />
					</td>
					<td class="infoline" colspan="2">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemDescription}"
							property="document.item[${ctr}].itemDescription"
							readOnly="${((itemLine.itemTypeCode eq 'ITEM') or not (fullEntryMode))}" />
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemOrderedQuantity}"
							property="document.item[${ctr}].itemOrderedQuantity"
							readOnly="${true}" />
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
							property="document.item[${ctr}].itemUnitOfMeasureCode"
							onblur="loadItemUnitOfMeasureInfo( 'document.item[${ctr}].itemUnitOfMeasureCode', 'document.item[${ctr}].itemUnitOfMeasure.itemUnitOfMeasureDescription' );${onblur}"
							readOnly="${((itemLine.itemTypeCode eq 'ITEM') or not (fullEntryMode))}"
							tabindexOverride="${tabindexOverrideBase + 0}"/>
							<c:if test="${((!itemLine.itemTypeCode eq 'ITEM') && fullEntryMode)}">
								<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.UnitOfMeasure"
										fieldConversions="itemUnitOfMeasureCode:document.item[${ctr}].itemUnitOfMeasureCode"
										lookupParameters="'Y':active"/>
							</c:if>
							<div id="document.item[${ctr}].itemUnitOfMeasure.itemUnitOfMeasureDescription.div" class="fineprint">
								<html:hidden write="true" property="document.item[${ctr}].itemUnitOfMeasure.itemUnitOfMeasureDescription"/>&nbsp;
							</div>

					</td>

					<c:if test="${KualiForm.stateFinal == false}">
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemReceivedPriorQuantity}"
							property="document.item[${ctr}].itemReceivedPriorQuantity"
							readOnly="${true}" />
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemReceivedToBeQuantity}"
							property="document.item[${ctr}].itemReceivedToBeQuantity"
							readOnly="${true}" />
					</td>
					</c:if>

					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemReceivedTotalQuantity}"
							property="document.item[${ctr}].itemReceivedTotalQuantity"
							readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemReturnedTotalQuantity}"
							property="document.item[${ctr}].itemReturnedTotalQuantity"
							readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemDamagedTotalQuantity}"
							property="document.item[${ctr}].itemDamagedTotalQuantity"
							readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute
							attributeEntry="${itemAttributes.itemReasonAddedCode}"
							property="document.item[${ctr}].itemReasonAddedCode"
							extraReadOnlyProperty="document.item[${ctr}].itemReasonAdded.itemReasonAddedDescription"
							readOnly="${not (fullEntryMode) or itemLine.itemLineNumber != null}" tabindexOverride="${tabindexOverrideBase + 0}"/>
					</td>
					<td class="infoline">
						<div class="actions">&nbsp;</div>
					</td>
				</tr>
			</logic:iterate>
		</table>
	</div>
</kul:tab>
