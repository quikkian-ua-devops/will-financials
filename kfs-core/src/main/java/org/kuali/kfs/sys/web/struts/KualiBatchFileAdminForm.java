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
package org.kuali.kfs.sys.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.kfs.krad.util.KRADConstants;

public class KualiBatchFileAdminForm extends KualiForm {
    private String filePath;

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        
        if (StringUtils.isBlank(getFilePath())&& 
                StringUtils.isNotBlank(request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME)) &&
                StringUtils.isNotBlank(request.getParameter(KRADConstants.QUESTION_CONTEXT))) {
            setFilePath(request.getParameter(KRADConstants.QUESTION_CONTEXT));
        }
        
        if (filePath != null && filePath.matches(".*\\.\\.[/\\\\].*")) {
            throw new RuntimeException("Cannot access parent directory");
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
