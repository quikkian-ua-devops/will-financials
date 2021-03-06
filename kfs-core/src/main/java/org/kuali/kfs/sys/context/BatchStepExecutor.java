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
package org.kuali.kfs.sys.context;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.bo.Step;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * BatchStepExecutor executes a Step in its own Thread and writes either a .success or .error file after execution.
 * This class notifies the ContainerStepListener when a Step has started and when it has completed.
 * <p>
 * BatchStepExecutor adds a ConsoleAppender to its Logger if one hasn't been configured.
 */
public class BatchStepExecutor implements Runnable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchStepExecutor.class);

    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private BatchContainerDirectory batchContainerDirectory;
    private BatchStepFileDescriptor batchStepFile;
    private Step step;
    private int stepIndex;

    private Appender ndcAppender;
    private boolean ndcSet;
    private String logFileName;

    private List<ContainerStepListener> containerStepListeners;

    /**
     * @param parameterService        the ParameterService used by Job
     * @param dateTimeService         the DateTimeService used by Job
     * @param batchContainerDirectory the batch container directory
     * @param batchStepFile           the descriptor containing information about the step to execute
     * @param step                    the Step to execute
     * @param stepIndex               the index of the step in the job
     */
    public BatchStepExecutor(ParameterService parameterService, DateTimeService dateTimeService, BatchContainerDirectory batchContainerDirectory, BatchStepFileDescriptor batchStepFile, Step step, int stepIndex) {
        this.parameterService = parameterService;
        this.dateTimeService = dateTimeService;

        this.batchContainerDirectory = batchContainerDirectory;
        this.batchStepFile = batchStepFile;
        this.step = step;
        this.stepIndex = stepIndex;

        this.containerStepListeners = new ArrayList<ContainerStepListener>();

        LOG.info("Initialized thread executor for " + batchStepFile);
    }

    /**
     * Execute the Step via Job.runStep(). Setup NDC logging so the Step has its own log file. Remove the NDC logging once the step is finished executing.
     * Notify the ContainerStepListeners when the step starts and finishes.
     */
    @Override
    public void run() {
        Date stepRunDate = dateTimeService.getCurrentDate();
        batchStepFile.setStartedDate(stepRunDate);
        batchStepFile.setStepIndex(new Integer(stepIndex));

        setupNDCLogging();
        notifyStepStarted();

        try {
            LOG.info("Running " + batchStepFile);

            boolean result = Job.runStep(parameterService, batchStepFile.getJobName(), stepIndex, step, stepRunDate);

            if (result) {
                LOG.info("Step returned true");
                batchContainerDirectory.writeBatchStepSuccessfulResultFile(batchStepFile);
            } else {
                LOG.info("Step returned false");
                batchContainerDirectory.writeBatchStepErrorResultFile(batchStepFile);
            }

        } catch (Exception throwable) {
            //LOG.info("Step threw an error: ", throwable);
            LOG.warn(batchStepFile + " threw " + throwable.getClass().getName() + ". Look at the step log to see the details. throwable.getMessage(): " + throwable.getMessage());
            batchContainerDirectory.writeBatchStepErrorResultFile(batchStepFile, throwable);

        } finally {

            notifyStepFinished();
            resetNDCLogging();
        }
    }

    /**
     * Adds a ContainerStepListener for step start and completion notifications
     *
     * @param listener the ContainerStepListener
     */
    public void addContainerStepListener(ContainerStepListener listener) {
        this.containerStepListeners.add(listener);
    }

    /**
     * Add a new appender and context to the NDC for this execution of the step
     */
    private void setupNDCLogging() {
        String nestedDiagnosticContext = getNestedDiagnosticContext();
        logFileName = getLogFileName(nestedDiagnosticContext);

        ndcAppender = null;
        ndcSet = false;
        try {
            ndcAppender = new FileAppender(KFSConstants.BATCH_LOGGER_DEFAULT_PATTERN_LAYOUT, logFileName);
            ndcAppender.addFilter(new NDCFilter(nestedDiagnosticContext));
            Logger.getRootLogger().addAppender(ndcAppender);
            NDC.push(nestedDiagnosticContext);
            ndcSet = true;
        } catch (Exception ex) {
            LOG.warn("Could not initialize custom logging for step: " + step.getName(), ex);
        }
    }

    /**
     * Remove the appender and context from the NDC
     */
    private void resetNDCLogging() {
        if (ndcSet) {
            ndcAppender.close();
            Logger.getRootLogger().removeAppender(ndcAppender);
            NDC.pop();
        }
    }

    /**
     * Constructs the name of the log file to write to for this execution of the step
     *
     * @param nestedDiagnosticContext the context returned by getNestedDiagnosticContext() for this step
     * @return the name of the log file
     */
    private String getLogFileName(String nestedDiagnosticContext) {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.REPORTS_DIRECTORY_KEY)
            + File.separator
            + nestedDiagnosticContext + ".log";
    }

    /**
     * @return the nested diagnostic context string for this step's log file
     */
    @SuppressWarnings("unchecked")
    private String getNestedDiagnosticContext() {
        Step unProxiedStep = (Step) ProxyUtils.getTargetIfProxied(step);
        Class stepClass = unProxiedStep.getClass();
        ModuleService module = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(stepClass);

        String nestedDiagnosticContext =
            StringUtils.substringAfter(module.getModuleConfiguration().getNamespaceCode(), "-").toLowerCase()
                + File.separator + step.getName()
                + "-" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());

        return nestedDiagnosticContext;
    }

    /**
     * Notify the ContainerStepListeners that the Step has started
     */
    private void notifyStepStarted() {
        String shortLogFileName = getShortLogFileName();

        for (ContainerStepListener listener : this.containerStepListeners) {
            listener.stepStarted(batchStepFile, shortLogFileName);
        }
    }

    /**
     * Notify the ContainerStepListeners that the Step has completed
     */
    private void notifyStepFinished() {
        BatchStepFileDescriptor resultFile = batchContainerDirectory.getResultFile(batchStepFile);
        resultFile.setCompletedDate(dateTimeService.getCurrentDate());
        resultFile.setStepIndex(new Integer(stepIndex));

        String shortLogFileName = getShortLogFileName();

        for (ContainerStepListener listener : this.containerStepListeners) {
            listener.stepFinished(resultFile, shortLogFileName);
        }
    }

    /**
     * Returns just the name of the log file without the absolute path
     *
     * @return just the name of the log file (not the entire path)
     */
    private String getShortLogFileName() {
        String shortLogFileName = logFileName;

        File logFile = new File(logFileName);
        if (logFile.exists()) {
            shortLogFileName = logFile.getName();
        }
        return shortLogFileName;
    }
}
