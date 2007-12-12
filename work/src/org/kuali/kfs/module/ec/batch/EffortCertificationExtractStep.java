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
package org.kuali.module.effort.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.effort.service.EffortCertificationExtractService;
import org.kuali.module.labor.service.LaborScrubberService;

/**
 * Batch Step that executes the Effort Certification Extract Process.
 */
public class EffortCertificationExtractStep extends AbstractStep {
    private EffortCertificationExtractService effortCertificationExtractService;

    /**
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName) {
        effortCertificationExtractService.extract();
        return true;
    }

    /**
     * Sets the effortCertificationExtractService attribute value.
     * @param effortCertificationExtractService The effortCertificationExtractService to set.
     */
    public void setEffortCertificationExtractService(EffortCertificationExtractService effortCertificationExtractService) {
        this.effortCertificationExtractService = effortCertificationExtractService;
    }
}
