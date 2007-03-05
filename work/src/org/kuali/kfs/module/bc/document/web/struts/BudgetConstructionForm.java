/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.budget.web.struts.form;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.dao.ojb.BudgetConstructionDaoOjb;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.core.util.SpringServiceLocator;


public class BudgetConstructionForm extends KualiTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionForm.class);
    
    public BudgetConstructionForm() {
        super();
        setDocument(new BudgetConstructionDocument());
        LOG.debug("creating BudgetConstructionForm");
    }
    
    public BudgetConstructionDocument getBudgetConstructionDocument(){
        return (BudgetConstructionDocument) getDocument();
    }

    public void setBudgetConstructionDocument(BudgetConstructionDocument budgetConstructionDocument){
        setDocument(budgetConstructionDocument);
    }

   
}
