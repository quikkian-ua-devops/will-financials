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
package org.kuali.kfs.sys.web.servlet;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.AuthenticationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchFileUploadServlet extends HttpServlet {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchFileUploadServlet.class);

    protected void checkAuthorization(HttpServletRequest request) {
        boolean authorized = false;
        String principalName = ((AuthenticationService) GlobalResourceLoader.getResourceLoader().getService(new QName("kimAuthenticationService"))).getPrincipalName(request);
        if (LOG.isInfoEnabled()) {
            LOG.info("Logged In User: " + principalName);
        }
        if (StringUtils.isNotBlank(principalName)) {
            Person person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
            if (person != null) {
                String principalId = person.getPrincipalId();
                Map<String, String> permissionDetails = new HashMap<String, String>();
                DocumentAuthorizer da = SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer("GLCP");
                if (da != null) {
                    authorized = da.canInitiate("GLCP", person);
                }
                if (!authorized) {
                    da = SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer("LLCP");
                    if (da != null) {
                        authorized = da.canInitiate("LLCP", person);
                    }
                }
            }
        }
        if (!authorized) {
            throw new RuntimeException("You must be able to initiate the GLCP or LLCP documents to use this page.  (Backdoor users are not recognized.)");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkAuthorization(request);
        request.setAttribute("directories", getBatchDirectories());
        request.getRequestDispatcher("/WEB-INF/jsp/batchFileUpload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkAuthorization(request);
        ServletFileUpload upload = new ServletFileUpload();

        String destPath = null;
        String fileName = null;
        String tempDir = System.getProperty("java.io.tmpdir");
        // Parse the request
        try {
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                fileName = item.getName();
                LOG.info("Processing Item: " + item.getFieldName());
                if (item.isFormField()) {
                    if (item.getFieldName().equals("uploadDir")) {
                        Reader str = new InputStreamReader(item.openStream());
                        StringWriter sw = new StringWriter();
                        char buf[] = new char[100];
                        int len;
                        while ((len = str.read(buf)) > 0) {
                            sw.write(buf, 0, len);
                        }
                        destPath = sw.toString();
                    }
                } else {
                    InputStream stream = item.openStream();
                    fileName = item.getName();
                    LOG.info("Uploading to Directory: " + tempDir);
                    // Process the input stream
                    FileOutputStream fos = new FileOutputStream(new File(tempDir, fileName));
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024 * 1024);
                    byte buf[] = new byte[10240];
                    int len;
                    while ((len = stream.read(buf)) > 0) {
                        bos.write(buf, 0, len);
                    }
                    bos.close();
                    stream.close();
                }
            }
            LOG.info("Copying to Directory: " + destPath);

            if (!getBatchDirectories().contains(destPath)) {
                new File(tempDir, fileName).delete();
                throw new RuntimeException("Illegal Attempt to upload to an unauthorized path: '" + destPath + "'");
            }

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(tempDir, fileName)));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destPath, fileName)), 1024 * 1024);
            byte buf[] = new byte[10240];
            int len;
            while ((len = bis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.close();
            bis.close();
        } catch (FileUploadException ex) {
            LOG.error("Problem Uploading file", ex);
        }
        if (fileName != null) {
            request.setAttribute("message", "Successfully uploaded " + fileName + " to " + destPath);
        } else {
            request.setAttribute("message", "Upload Failed");
        }
        doGet(request, response);
    }

    protected List<String> getBatchDirectories() {
        List<String> dirs = new ArrayList<String>();
        for (ModuleService moduleService : SpringContext.getBean(KualiModuleService.class).getInstalledModuleServices()) {
            ModuleConfiguration moduleConfiguration = moduleService.getModuleConfiguration();
            if (moduleConfiguration instanceof FinancialSystemModuleConfiguration) {
                List<String> batchFileDirectories = ((FinancialSystemModuleConfiguration) moduleConfiguration).getBatchFileDirectories();
                for (String batchFileDirectoryName : batchFileDirectories) {
                    String directory = new File(batchFileDirectoryName).getAbsolutePath();
                    if (new File(directory, "originEntry").isDirectory()) {
                        dirs.add(new File(directory, "originEntry").getAbsolutePath());
                    }
                }
            }
        }
        return dirs;
    }

}
