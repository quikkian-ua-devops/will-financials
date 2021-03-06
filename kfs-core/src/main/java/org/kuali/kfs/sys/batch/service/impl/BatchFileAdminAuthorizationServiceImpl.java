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
package org.kuali.kfs.sys.batch.service.impl;

import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchFile;
import org.kuali.kfs.sys.batch.BatchFileUtils;
import org.kuali.kfs.sys.batch.service.BatchFileAdminAuthorizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.IdentityManagementService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchFileAdminAuthorizationServiceImpl implements BatchFileAdminAuthorizationService {

    private static final String RESEARCH_PARTICIPANTS_PERMISSION = "Download Research Participant Check File(s)";
    private IdentityManagementService identityManagementService;
    private KualiModuleService kualiModuleService;

    @Override
    public boolean canDownload(BatchFile batchFile, Person user) {

        boolean isAuthorized = false;
        if (batchFile.getFileName().indexOf(PdpConstants.RESEARCH_PARTICIPANT_FILE_PREFIX) >= 0) {
            isAuthorized = getIdentityManagementService().hasPermissionByTemplateName(user.getPrincipalId(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name, generateDownloadCheckPermissionDetails(batchFile, user));
        } else {
            isAuthorized = getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(),
                KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.namespace, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name,
                generateDownloadCheckPermissionDetails(batchFile, user), generateDownloadCheckRoleQualifiers(batchFile, user));
        }
        return isAuthorized;
    }

    @Override
    public boolean canDelete(BatchFile batchFile, Person user) {
        boolean isAuthorized = false;
        if (batchFile.getFileName().indexOf(PdpConstants.RESEARCH_PARTICIPANT_FILE_PREFIX) >= 0) {
            isAuthorized = getIdentityManagementService().hasPermissionByTemplateName(user.getPrincipalId(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name, generateDownloadCheckPermissionDetails(batchFile, user));
        } else {
            isAuthorized = getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(),
                KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.namespace, KFSConstants.PermissionTemplate.VIEW_BATCH_FILES.name,
                generateDownloadCheckPermissionDetails(batchFile, user), generateDownloadCheckRoleQualifiers(batchFile, user));
        }
        return isAuthorized;
    }

    protected String determineNamespaceCode(BatchFile batchFile) {
        for (ModuleService moduleService : getKualiModuleService().getInstalledModuleServices()) {
            ModuleConfiguration moduleConfiguration = moduleService.getModuleConfiguration();
            if (moduleConfiguration instanceof FinancialSystemModuleConfiguration) {
                List<String> batchFileDirectories = ((FinancialSystemModuleConfiguration) moduleConfiguration).getBatchFileDirectories();
                for (String batchFileDirectoryName : batchFileDirectories) {
                    File directory = new File(batchFileDirectoryName).getAbsoluteFile();
                    if (BatchFileUtils.isSuperDirectoryOf(directory, batchFile.retrieveFile())) {
                        return moduleConfiguration.getNamespaceCode();
                    }
                }
            }
        }
        return null;
    }

    protected Map<String, String> generateDownloadCheckPermissionDetails(BatchFile batchFile, Person user) {
        return generatePermissionDetails(batchFile, user);
    }

    protected Map<String, String> generateDownloadCheckRoleQualifiers(BatchFile batchFile, Person user) {
        return generateRoleQualifiers(batchFile, user);
    }

    protected Map<String, String> generateDeleteCheckPermissionDetails(BatchFile batchFile, Person user) {
        return generatePermissionDetails(batchFile, user);
    }

    protected Map<String, String> generateDeleteCheckRoleQualifiers(BatchFile batchFile, Person user) {
        return generateRoleQualifiers(batchFile, user);
    }

    protected Map<String, String> generatePermissionDetails(BatchFile batchFile, Person user) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, determineNamespaceCode(batchFile));
        permissionDetails.put(KfsKimAttributes.FILE_PATH, replaceSlashes(batchFile.getPath() + File.separator + batchFile.getFileName()));
        return permissionDetails;
    }

    /**
     * The permissions for the filePath will be added using '/' directory separators.
     * This method will replace any '\\' directory separators with '/'
     *
     * @param filePath
     * @return
     */
    private String replaceSlashes(String filePath) {

        if (File.separatorChar == '\\') {
            filePath = filePath.replace(File.separatorChar, '/');
        }

        return filePath;
    }

    protected Map<String, String> generateRoleQualifiers(BatchFile batchFile, Person user) {
        return new HashMap<String, String>();
    }

    protected IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    public KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        }
        return kualiModuleService;
    }
}
