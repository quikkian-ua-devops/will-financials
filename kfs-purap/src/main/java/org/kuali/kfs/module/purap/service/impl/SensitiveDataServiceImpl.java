/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignmentDetail;
import org.kuali.kfs.module.purap.dataaccess.SensitiveDataDao;
import org.kuali.kfs.module.purap.document.dataaccess.PurchaseOrderDao;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.krad.service.BusinessObjectService;

public class SensitiveDataServiceImpl implements SensitiveDataService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SensitiveDataServiceImpl.class);
    
    private SensitiveDataDao sensitiveDataDao;
    private PurchaseOrderDao purchaseOrderDao;

    public void setPurchaseOrderDao(PurchaseOrderDao purchaseOrderDao) {
        this.purchaseOrderDao = purchaseOrderDao;
    }

    public SensitiveDataDao getSensitiveDataDao() {
        return sensitiveDataDao;
    }

    public void setSensitiveDataDao(SensitiveDataDao sensitiveDataDao) {
        this.sensitiveDataDao = sensitiveDataDao;
    }

    /**
     * @see org.kuali.kfs.integration.service.SensitiveDataService#getSensitiveDataByCode(java.lang.String)
     */
    public SensitiveData getSensitiveDataByCode(String sensitiveDataCode) {
        LOG.debug("getSensitiveDataByCode(String) started");        
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put("sensitiveDataCode", sensitiveDataCode);
        return (SensitiveData)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SensitiveData.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#getAllSensitiveDatas()
     */
    public List<SensitiveData> getAllSensitiveDatas() {
        LOG.debug("getSensitiveDataByPoId(Integer) started");     
        
        List<SensitiveData> sds = new ArrayList<SensitiveData>();
        Collection<SensitiveData> sdColl = SpringContext.getBean(BusinessObjectService.class).findAll(SensitiveData.class);
        for (Object sd: sdColl) {
            sds.add((SensitiveData)sd);
        }
        
        return sds;
    }
    
    public List<SensitiveData> getSensitiveDatasAssignedByRelatedDocId(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        Integer poId = purchaseOrderDao.getPurchaseOrderIdForCurrentPurchaseOrderByRelatedDocId(accountsPayablePurchasingDocumentLinkIdentifier);
        return getSensitiveDatasAssignedByPoId(poId);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#getSensitiveDatasAssignedByPoId()
     */
    public List<SensitiveData> getSensitiveDatasAssignedByPoId(Integer poId) {
        LOG.debug("getSensitiveDatasAssignedByPoId(Integer) started");        
        return sensitiveDataDao.getSensitiveDatasAssignedByPoId(poId);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#getSensitiveDatasAssignedByReqId(Integer)
     */
    public List<SensitiveData> getSensitiveDatasAssignedByReqId(Integer reqId) {
        LOG.debug("getSensitiveDatasAssignedByReqId(Integer) started");        
        return sensitiveDataDao.getSensitiveDatasAssignedByReqId(reqId);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#deletePurchaseOrderSensitiveDatas(Integer)
     */
    public void deletePurchaseOrderSensitiveDatas(Integer poId) {
        LOG.debug("deletePurchaseOrderSensitiveDatas(Integer) started");
        sensitiveDataDao.deletePurchaseOrderSensitiveDatas(poId);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#savePurchaseOrderSensitiveDatas(List<PurchaseOrderSensitiveData>)
     */
    public void savePurchaseOrderSensitiveDatas(List<PurchaseOrderSensitiveData> posds) {
        LOG.debug("savePurchaseOrderSensitiveDatas(List<PurchaseOrderSensitiveData>) started");
        SpringContext.getBean(BusinessObjectService.class).save(posds);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#getLastSensitiveDataAssignment(Integer)
     */
    public SensitiveDataAssignment getLastSensitiveDataAssignment(Integer poId) {
        LOG.debug("getLastSensitiveDataAssignment(Integer) started");
        return sensitiveDataDao.getLastSensitiveDataAssignment(poId);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#getLastSensitiveDataAssignmentId(Integer)
     */
    public Integer getLastSensitiveDataAssignmentId(Integer poId) {
        LOG.debug("getLastSensitiveDataAssignmentId(Integer) started");
        return getLastSensitiveDataAssignment(poId).getSensitiveDataAssignmentIdentifier();
    }
    
    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#saveSensitiveDataAssignment(SensitiveDataAssignment)
     */
    public void saveSensitiveDataAssignment(SensitiveDataAssignment sda) {
        LOG.debug("saveSensitiveDataAssignment(SensitiveDataAssignment) started");
        SpringContext.getBean(BusinessObjectService.class).save(sda);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#getLastSensitiveDataAssignmentDetails(Integer)
     */
    public List<SensitiveDataAssignmentDetail> getLastSensitiveDataAssignmentDetails(Integer poId) {
        LOG.debug("getLastSensitiveDataAssignmentDetails(Integer) started");
        Integer sdaId = getLastSensitiveDataAssignmentId(poId);
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("sensitiveDataAssignmentIdentifier", sdaId);
        return (List<SensitiveDataAssignmentDetail>)SpringContext.getBean(BusinessObjectService.class).findMatching(SensitiveDataAssignmentDetail.class, fieldValues);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail>)
     */
    public void saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail> sdads) {
        LOG.debug("saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail>) started");
        SpringContext.getBean(BusinessObjectService.class).save(sdads);
    }

    
}
