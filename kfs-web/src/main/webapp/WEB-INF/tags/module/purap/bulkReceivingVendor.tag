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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="vendorReadOnly" value="${(not empty KualiForm.editingMode['lockVendorEntry'])}" />
<c:set var="tabindexOverrideBase" value="10" />

<kul:tab tabTitle="Vendor" defaultOpen="${true}" tabErrorKey="${PurapConstants.BULK_RECEIVING_VENDOR_TAB_ERRORS}">
    <div class="tab-container">
        <table class="standard" summary="Vendor Section">
            <tr>
                <td colspan="4" class="subhead"><h3>Vendor</h3></td>
            </tr>

	        <c:if test="${isPOAvailable}" >
        		<tr>
	        		<th class="right top">
	                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" />
	                </th>
	                <td>
	                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" property="document.vendorName" readOnly="true" /><br>
	                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" property="document.vendorLine1Address" readOnly="true" /><br>
	                   	<c:if test="${! empty KualiForm.document.vendorLine2Address}">
	                   		<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.vendorLine2Address" readOnly="true" /><br>
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorCityName}">
	    	           		<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.vendorCityName" readOnly="true" />,&nbsp;
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorStateCode}">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.vendorStateCode" readOnly="true" />&nbsp;
	                  	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorPostalCode}">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.vendorPostalCode" readOnly="true" />
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorAddressInternationalProvinceName}">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" property="document.vendorAddressInternationalProvinceName" readOnly="true" />
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorCountryCode}">
		            		<br><kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.vendorCountryCode" readOnly="true" />
	                   	</c:if>
	            	</td>

	            	<th class="right top">
	                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternateVendorName}" />
	                </th>
	                <td>
	                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.alternateVendorName}" property="document.alternateVendorName" readOnly="true" /><br>
	                	<c:if test="${! empty KualiForm.document.alternateVendorNumber}">
		                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" property="document.alternateVendorDetail.defaultAddressLine1" readOnly="true" /><br>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressLine2}">
		                   		<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.alternateVendorDetail.defaultAddressLine2" readOnly="true" /><br>
		                   	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressCity}">
		    	           		<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.alternateVendorDetail.defaultAddressCity" readOnly="true" />,&nbsp;
		                   	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressStateCode}">
			                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.alternateVendorDetail.defaultAddressStateCode" readOnly="true" />&nbsp;
		                  	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressPostalCode}">
			                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.alternateVendorDetail.defaultAddressPostalCode" readOnly="true" />
		                   	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressInternationalProvince}">
			                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" property="document.alternateVendorDetail.defaultAddressInternationalProvince" readOnly="true" />
		                   	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressCountryCode}">
			            		<br><kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.alternateVendorDetail.defaultAddressCountryCode" readOnly="true" />
		                   	</c:if>
		                 </c:if>
	            	</td>
            	</tr>
            	<tr>
            		<th class="right">
                        <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorContact}" />
                    </th>
                    <td>
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorContact}"
                        						  property="document.vendorContact"
                        						  readOnly="true"/>
                        <c:if test="${(not empty KualiForm.document.vendorNumber) and fullEntryMode}" >
	                        <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.VendorContact"
	                        		    readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier"
	                        		    autoSearch="yes"
	 			                        lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier"
	                        	        hideReturnLink="true"
	                        	        extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_return.gif" />
	                    </c:if>
                    </td>
                    <th class="right">
   	                    <bean:message key="${KualiForm.goodsDeliveredByLabel}" />:
       	            </th>
       	            <td width="25%">
        	          <c:if test="${(not empty KualiForm.document.alternateVendorNumber) and fullEntryMode}" >
            	 			<html:radio property="document.goodsDeliveredVendorNumber"
		            		    		value="${KualiForm.document.vendorNumber}">
		            		<c:out value="${KualiForm.document.vendorName}"/></html:radio>
		         			<html:radio property="document.goodsDeliveredVendorNumber"
		            		    		value="${KualiForm.document.alternateVendorNumber}">
		            		<c:out value="${KualiForm.document.alternateVendorName}"/>
		            		</html:radio>
					   </c:if>
			           <c:if test="${((empty KualiForm.document.alternateVendorNumber) and fullEntryMode) or not fullEntryMode}" >
			              	  <kul:htmlControlAttribute attributeEntry="${documentAttributes.goodsDeliveredVendorName}" property="document.goodsDeliveredVendorName" readOnly="true" />
		    	       </c:if>
		    	    </td>
                 </tr>
			</c:if>

            <c:if test="${!isPOAvailable}" >
	            <tr>
	                <th class="right" width="25%">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" />
	                </th>
	                <td width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}"
	                    						  property="document.vendorName"
	                    						  readOnly="${not (fullEntryMode) or vendorReadOnly}"
	                    						  tabindexOverride="${tabindexOverrideBase + 0}"/>
	                    <c:if test="${fullEntryMode}" >
	                        <kul:lookup  boClassName="org.kuali.kfs.vnd.businessobject.VendorDetail"
	                        			 lookupParameters="'Y':activeIndicator, 'PO':vendorHeader.vendorTypeCode"
	                        			 fieldConversions="vendorNumber:document.vendorNumber,vendorHeaderGeneratedIdentifier:document.vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:document.vendorDetailAssignedIdentifier,defaultAddressLine1:document.vendorLine1Address,defaultAddressLine2:document.vendorLine2Address,defaultAddressCity:document.vendorCityName,defaultAddressPostalCode:document.vendorPostalCode,defaultAddressStateCode:document.vendorStateCode,defaultAddressInternationalProvince:document.vendorAddressInternationalProvinceName,defaultAddressCountryCode:document.vendorCountryCode"/>
	                    </c:if>
                	</td>
	                <th class="right" width="25%">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCityName}" />
	                </th>
	                <td width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.vendorCityName" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </tr>
	            <tr>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNumber}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorNumber" readOnly="true" />
	                </td>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorStateCode}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.vendorStateCode" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </tr>
	            <tr>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine1Address}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}"
	                    						  property="document.vendorLine1Address"
	                    						  readOnly="${not fullEntryMode}"
	                    						  tabindexOverride="${tabindexOverrideBase + 0}"/>
	                    <c:if test="${fullEntryMode}">
	                        <kul:lookup  boClassName="org.kuali.kfs.vnd.businessobject.VendorAddress"
	                        			 readOnlyFields="active, vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier"
	                        			 lookupParameters="'Y':active,document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier"
	                        			 fieldConversions="vendorAddressGeneratedIdentifier:document.vendorAddressGeneratedIdentifier"/>
	                    </c:if>
	                </td>
	                <th class="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" property="document.vendorAddressInternationalProvinceName" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </tr>
	            <tr>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine2Address}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.vendorLine2Address" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPostalCode}" />
	                </th>
					<td>
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.vendorPostalCode" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
					</td>
	            </tr>
	            <tr>
	            	<th class="right">
                        <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorContact}" />
                    </th>
                    <td>
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorContact}"
                        						  property="document.vendorContact"
                        						  readOnly="true"/>
                        <c:if test="${fullEntryMode}" >
	                        <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.VendorContact"
	                        		    readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier"
	                        		    autoSearch="yes"
	 			                        lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier"
	                        	        hideReturnLink="true"
	                        	        extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_return.gif" />
	                    </c:if>
                    </td>
	            	<th class="right">
	            		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCountryCode}" />
	            	</th>
	            	<td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.vendorCountryCode"
	                    extraReadOnlyProperty="document.vendorCountry.name"
	                    readOnly="${not fullEntryMode}"
	                    tabindexOverride="${tabindexOverrideBase + 5}"/>
	            	</td>
	            </tr>
			</c:if>

			<tr>
                <td colspan="4" class="subhead"><h3>Shipment Information</h3></td>
            </tr>
			<tr>
				 <th class="right">
                   	 <kul:htmlAttributeLabel attributeEntry="${documentAttributes.trackingNumber}" />
                 </th>
                 <td>
                   	 <kul:htmlControlAttribute attributeEntry="${documentAttributes.trackingNumber}" property="document.trackingNumber" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                 </td>
				 <th class="right">
	                 <kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentReceivedDate}" />
	             </th>
	             <td>
	                 <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentReceivedDate}" property="document.shipmentReceivedDate" datePicker="true" readOnly="${not fullEntryMode}"/>
	             </td>
            </tr>
			<tr>
            	<th class="right">
                  		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" />
                </th>
   	            <td>
       	           <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" property="document.shipmentPackingSlipNumber" readOnly="${not fullEntryMode}"/>
           	    </td>
           	    <th class="right">
                  		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentReferenceNumber}" />
                </th>
   	            <td>
       	           <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentReferenceNumber}" property="document.shipmentReferenceNumber" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
           	    </td>
            </tr>
            <tr>
                <th class="right">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" />
                </th>
                <td>
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" property="document.shipmentBillOfLadingNumber" readOnly="${not fullEntryMode}"/>
                </td>
                <th class="right">
                  		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.carrierCode}" />
               	</th>
               	<td>
                  		<kul:htmlControlAttribute attributeEntry="${documentAttributes.carrierCode}" property="document.carrier.carrierDescription" readOnly="true"/>
               	</td>
            </tr>
            <tr>
                <th class="right">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentWeight}" />
                </th>
                <td>
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentWeight}" property="document.shipmentWeight" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th class="right">
                  		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.noOfCartons}" />
               	</th>
               	<td>
                  		<kul:htmlControlAttribute attributeEntry="${documentAttributes.noOfCartons}" property="document.noOfCartons" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
               	</td>
            </tr>
      	</table>
    </div>
</kul:tab>
