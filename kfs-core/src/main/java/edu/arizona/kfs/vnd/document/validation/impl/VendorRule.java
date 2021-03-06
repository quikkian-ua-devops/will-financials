package edu.arizona.kfs.vnd.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorSupplierDiversity;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.service.DataDictionaryService;

import edu.arizona.kfs.vnd.VendorPropertyConstants;

public class VendorRule extends org.kuali.kfs.vnd.document.validation.impl.VendorRule {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
    	boolean valid = super.processCustomApproveDocumentBusinessRules(document);
    	return valid & processSupplierDiversityValidation(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
    	boolean valid = super.processCustomRouteDocumentBusinessRules(document);
    	return valid & processSupplierDiversityValidation(document);
    }
    
    private boolean processSupplierDiversityValidation(MaintenanceDocument document) {
    	boolean valid = true;
    	List<VendorSupplierDiversity> vendorSupplierDiversities = ((VendorDetail) document.getNewMaintainableObject().getBusinessObject()).getVendorHeader().getVendorSupplierDiversities();
    	DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    	
    	if(vendorSupplierDiversities == null || vendorSupplierDiversities.isEmpty()) {
    		valid = false;
    		String fieldName = VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_CODE_FIELD_NAME;
    		String attributeLabel = dataDictionaryService.getAttributeLabel(VendorSupplierDiversity.class, fieldName);
    		String shortLabel = dataDictionaryService.getAttributeShortLabel(VendorSupplierDiversity.class, fieldName);
    		
    		putFieldError(VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_CODE, RiceKeyConstants.ERROR_REQUIRED, attributeLabel + " (" + shortLabel + ")");
    	}
    	
    	else {
    		int count = 0;
    		for(VendorSupplierDiversity vsd : vendorSupplierDiversities) {
    			if(vsd.isActive()) {
    				count++;
    			}
    		}
    		
    		if(count == 0) {
    			valid = false;
    			String fieldName = VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_ACTIVE_FIELD_NAME;
    			String attributeLabel = dataDictionaryService.getAttributeLabel(VendorSupplierDiversity.class, fieldName);
        		String shortLabel = dataDictionaryService.getAttributeShortLabel(VendorSupplierDiversity.class, fieldName);
        		
        		putFieldError(VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_ACTIVE, RiceKeyConstants.ERROR_REQUIRED, attributeLabel + " (" + shortLabel + ")");
    		}
    	}
    	
		return valid;
	}
      
    @Override
    protected boolean validateParentVendorTaxNumber(VendorDetail vendorDetail) {
    	String taxNumber = vendorDetail.getVendorHeader().getVendorTaxNumber();

        // no parent vendor tax number validation needed if the tax number is blank
        if (StringUtils.isBlank(taxNumber)) {
            return true;
        }

        return super.validateParentVendorTaxNumber(vendorDetail);
    }
    
    @Override
    protected boolean validateTaxTypeAndTaxNumberBlankness(VendorDetail vendorDetail) {
        boolean valid = true;
        boolean isParent = vendorDetail.isVendorParentIndicator();
        String vendorTaxTypeCode = vendorDetail.getVendorHeader().getVendorTaxTypeCode();
        if (!StringUtils.isBlank(vendorDetail.getVendorHeader().getVendorTaxNumber()) && VendorConstants.NONE.equals(vendorTaxTypeCode)) {
        	if (isParent) {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE, VendorKeyConstants.ERROR_VENDOR_TAX_TYPE_CANNOT_BE_BLANK);
            }
            valid &= false;
        }
        else if (StringUtils.isBlank(vendorDetail.getVendorHeader().getVendorTaxNumber()) && !VendorConstants.NONE.equals(vendorTaxTypeCode)) {
        	if (isParent) {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE, VendorKeyConstants.ERROR_VENDOR_TAX_TYPE_CANNOT_BE_SET);
            }
            valid &= false;
        }

        if (!valid && !isParent) {
            putFieldError(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE, VendorKeyConstants.ERROR_VENDOR_PARENT_NEEDS_CHANGED);
        }

        return valid;
    }

   
}
