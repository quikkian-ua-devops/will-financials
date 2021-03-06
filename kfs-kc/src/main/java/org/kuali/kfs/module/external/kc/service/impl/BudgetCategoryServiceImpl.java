/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.InstitutionalBudgetCategorySoapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kra.external.budget.service.BudgetCategoryService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

import javax.xml.ws.WebServiceException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BudgetCategoryServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetCategoryServiceImpl.class);

    protected ConfigurationService configurationService;

    protected BudgetCategoryService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        BudgetCategoryService institutionalBudgetCategoryService = (BudgetCategoryService) GlobalResourceLoader.getService(KcConstants.BudgetCategory.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (institutionalBudgetCategoryService == null) {
            LOG.warn("Couldn't get BudgetCategoryService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");

            InstitutionalBudgetCategorySoapService ss = null;
            try {
                ss = new InstitutionalBudgetCategorySoapService();
            } catch (MalformedURLException ex) {
                LOG.error("Could not intialize BudgetCategorySoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize BudgetCategorySoapService: " + ex.getMessage());
            }
            institutionalBudgetCategoryService = ss.getBudgetCategoryServicePort();
        }
        return institutionalBudgetCategoryService;
    }

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Collection budgetCategoryDTOs = findMatching(primaryKeys);
        if (budgetCategoryDTOs != null && budgetCategoryDTOs.iterator().hasNext()) {
            return (ExternalizableBusinessObject) budgetCategoryDTOs.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        List<HashMapElement> hashMapList = new ArrayList<HashMapElement>();
        List budgetCategoryDTOs = new ArrayList();

        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if (KcConstants.BudgetCategory.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key) && (val.length() > 0)) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val);
                hashMapList.add(hashMapElement);
            }
        }
        if (hashMapList.size() == 0) {
            hashMapList = null;
        }

        try {
            budgetCategoryDTOs = this.getWebService().lookupBudgetCategories(hashMapList);
        } catch (WebServiceException ex) {
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, getConfigurationService().getPropertyValueAsString(KFSConstants.KC_APPLICATION_URL_KEY));
        }
        return budgetCategoryDTOs;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
