/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.external.kc.service.impl;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.integration.cg.dto.KcObjectCode;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.external.kc.service.KcObjectCodeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Transactional
public class KcObjectCodeServiceImpl implements KcObjectCodeService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KcObjectCodeServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected ObjectCodeService objectCodeService;

    /**
     * @see org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService#lookupObjectCodes(java.util.HashMap)
     */
    @Override
    public List<KcObjectCode> lookupObjectCodes(java.util.List<HashMapElement> searchCriteria) {
        HashMap<String, String> hashMap = new HashMap();
        List<ObjectCode> objCodeList = new ArrayList<ObjectCode>();

        if ((searchCriteria == null) || (searchCriteria.size() == 0)) {
            objCodeList = (List<ObjectCode>) businessObjectService.findAll(ObjectCode.class);

        } else {
            for (HashMapElement hashMapElement : searchCriteria) {
                hashMap.put(hashMapElement.getKey(), hashMapElement.getValue());
            }
            objCodeList = (List<ObjectCode>) (businessObjectService.findMatching(ObjectCode.class, hashMap));
        }
        List<KcObjectCode> kcObjectCodeList = new ArrayList();
        for (ObjectCode objectCode : objCodeList) {
            kcObjectCodeList.add(createKcObjectCode(objectCode));
        }
        return kcObjectCodeList;
    }

    /**
     * @see org.kuali.kfs.module.external.kc.service.getObjectCode(String, String, String)
     */
    @Override
    public KcObjectCode getObjectCode(String universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        Integer fiscalYear = new Integer(universityFiscalYear);
        ObjectCode objectCode = objectCodeService.getByPrimaryId(fiscalYear, chartOfAccountsCode, financialObjectCode);
        return createKcObjectCode(objectCode);
    }

    protected KcObjectCode createKcObjectCode(ObjectCode objectCode) {
        KcObjectCode kcObjectCode = new KcObjectCode();
        kcObjectCode.setObjectCodeName(objectCode.getCode());
        kcObjectCode.setDescription(objectCode.getName());
        return kcObjectCode;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

}
