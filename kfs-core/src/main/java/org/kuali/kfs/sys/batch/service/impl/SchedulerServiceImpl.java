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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.bo.Step;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.batch.JobDescriptor;
import org.kuali.kfs.sys.batch.JobListener;
import org.kuali.kfs.sys.batch.ScheduleStep;
import org.kuali.kfs.sys.batch.SimpleTriggerDescriptor;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BatchModuleService;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.quartz.CronExpression;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Transactional
public class SchedulerServiceImpl implements SchedulerService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SchedulerServiceImpl.class);

    protected static final String SOFT_DEPENDENCY_CODE = "softDependency";
    protected static final String HARD_DEPENDENCY_CODE = "hardDependency";

    private Scheduler scheduler;
    private JobListener jobListener;
    private KualiModuleService kualiModuleService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    /**
     * Holds a list of job name to job descriptor mappings for those jobs that are externalized (i.e. the module service is responsible for reporting their status)
     */
    protected Map<String, JobDescriptor> externalizedJobDescriptors;

    protected static final List<String> jobStatuses = new ArrayList<>();

    static {
        jobStatuses.add(SCHEDULED_JOB_STATUS_CODE);
        jobStatuses.add(SUCCEEDED_JOB_STATUS_CODE);
        jobStatuses.add(CANCELLED_JOB_STATUS_CODE);
        jobStatuses.add(RUNNING_JOB_STATUS_CODE);
        jobStatuses.add(FAILED_JOB_STATUS_CODE);
    }

    public SchedulerServiceImpl() {
        externalizedJobDescriptors = new HashMap<>();
    }

    @Override
    public void initialize() {
        LOG.debug("initialize() started");

        jobListener.setSchedulerService(this);
        try {
            scheduler.getListenerManager().addJobListener(jobListener);
        } catch (SchedulerException e) {
            throw new RuntimeException("SchedulerServiceImpl encountered an exception when trying to register the global job listener", e);
        }
        for (ModuleService moduleService : kualiModuleService.getInstalledModuleServices()) {
            initializeJobsForModule(moduleService);
            initializeTriggersForModule(moduleService);
        }

        dropDependenciesNotScheduled();
    }

    /**
     * Initializes all of the jobs into Quartz for the given ModuleService
     *
     * @param moduleService the ModuleService implementation to initalize jobs for
     */
    protected void initializeJobsForModule(ModuleService moduleService) {
        LOG.info("Loading scheduled jobs for: " + moduleService.getModuleConfiguration().getNamespaceCode());

        JobDescriptor jobDescriptor;
        if (moduleService.getModuleConfiguration().getJobNames() != null) {
            for (String jobName : moduleService.getModuleConfiguration().getJobNames()) {
                try {
                    if (moduleService instanceof BatchModuleService && ((BatchModuleService) moduleService).isExternalJob(jobName)) {
                        jobDescriptor = new JobDescriptor();
                        jobDescriptor.setBeanName(jobName);
                        jobDescriptor.setGroup(SCHEDULED_GROUP);
                        jobDescriptor.setDurable(false);
                        externalizedJobDescriptors.put(jobName, jobDescriptor);
                    } else {
                        jobDescriptor = BatchSpringContext.getJobDescriptor(jobName);
                    }
                    jobDescriptor.setNamespaceCode(moduleService.getModuleConfiguration().getNamespaceCode());
                    loadJob(jobDescriptor);
                } catch (NoSuchBeanDefinitionException ex) {
                    LOG.error("unable to find job bean definition for job: " + ex.getBeanName());
                } catch (Exception ex) {
                    LOG.error("Unable to install " + jobName + " job into scheduler.", ex);
                }
            }
        }
    }

    /**
     * Loops through all the triggers associated with the given module service, adding each trigger to Quartz
     *
     * @param moduleService the ModuleService instance to initialize triggers for
     */
    protected void initializeTriggersForModule(ModuleService moduleService) {
        if (moduleService.getModuleConfiguration().getTriggerNames() != null) {
            for (String triggerName : moduleService.getModuleConfiguration().getTriggerNames()) {
                try {
                    addTrigger(BatchSpringContext.getTriggerDescriptor(triggerName).getTrigger());
                } catch (NoSuchBeanDefinitionException ex) {
                    LOG.error("unable to find trigger definition: " + ex.getBeanName());
                } catch (Exception ex) {
                    LOG.error("Unable to install " + triggerName + " trigger into scheduler.", ex);
                }
            }
        }
    }

    protected void loadJob(JobDescriptor jobDescriptor) {
        JobDetail jobDetail = jobDescriptor.getJobDetail();
        addJob(jobDetail);
        if (SCHEDULED_GROUP.equals(jobDetail.getKey().getGroup())) {
            addUnscheduled(jobDetail);
        }
    }

    /**
     * Remove dependencies that are not scheduled. Important for modularization if some
     * modules aren't loaded or if institutions don't schedule some dependencies
     */
    protected void dropDependenciesNotScheduled() {
            Map<String, JobDescriptor> descriptors = SpringContext.getBeansOfType(JobDescriptor.class);

            for (String jobName : descriptors.keySet()) {
                JobDescriptor jobDescriptor = descriptors.get(jobName);

                if (jobDescriptor != null && jobDescriptor.getGroup().equals(SCHEDULED_GROUP) && jobDescriptor.getDependencies() != null) {
                    // dependenciesToBeRemoved so to avoid ConcurrentModificationException
                    ArrayList<Entry<String, String>> dependenciesToBeRemoved = new ArrayList<>();
                    Set<Entry<String, String>> dependenciesSet = jobDescriptor.getDependencies().entrySet();
                    for (Entry<String, String> dependency : dependenciesSet) {
                        String dependencyJobName = dependency.getKey();
                        JobDescriptor dependent = descriptors.get(dependencyJobName);
                        if (dependent != null && !dependent.getGroup().equals(SCHEDULED_GROUP)) {
                            LOG.warn("Removing dependency " + dependencyJobName + " from " + jobName + " because it is not scheduled.");
                            dependenciesToBeRemoved.add(dependency);
                        }
                    }
                    dependenciesSet.removeAll(dependenciesToBeRemoved);
                }
            }
    }

    @Override
    public void initializeJob(String jobName, Job job) {
        LOG.debug("initializeJob() started");

        job.setSchedulerService(this);
        job.setParameterService(parameterService);
        job.setSteps(BatchSpringContext.getJobDescriptor(jobName).getSteps());
        job.setDateTimeService(dateTimeService);
    }

    @Override
    public boolean hasIncompleteJob() {
        LOG.debug("hasIncompleteJob() started");

        StringBuilder log = new StringBuilder("The schedule has incomplete jobs.");
        boolean hasIncompleteJob = false;
        for (String scheduledJobName : getJobNamesForScheduleJob()) {
            JobDetail scheduledJobDetail = getScheduledJobDetail(scheduledJobName);

            boolean jobIsIncomplete = isIncomplete(scheduledJobDetail);
            if (jobIsIncomplete) {
                log.append("\n\t").append(scheduledJobDetail.getKey().getName()+"-"+scheduledJobDetail.getKey().getGroup());
                hasIncompleteJob = true;
            }
        }
        if (hasIncompleteJob) {
            LOG.info(log);
        }
        return hasIncompleteJob;
    }

    protected boolean isIncomplete(JobDetail scheduledJobDetail) {
        if (scheduledJobDetail == null) {
            return false;
        }

        return !SCHEDULE_JOB_NAME.equals(scheduledJobDetail.getKey().getName()) && (isPending(scheduledJobDetail) || isScheduled(scheduledJobDetail));
    }

    @Override
    public boolean isPastScheduleCutoffTime() {
        LOG.debug("isPastScheduleCutoffTime() started");

        return isPastScheduleCutoffTime(dateTimeService.getCurrentCalendar(), true);
    }

    protected boolean isPastScheduleCutoffTime(Calendar dateTime, boolean log) {
        try {
            Date scheduleCutoffTimeTemp = scheduler.getTriggersOfJob(new JobKey(SCHEDULE_JOB_NAME, SCHEDULED_GROUP)).get(0).getPreviousFireTime();
            Calendar scheduleCutoffTime;
            if (scheduleCutoffTimeTemp == null) {
                scheduleCutoffTime = dateTimeService.getCurrentCalendar();
            } else {
                scheduleCutoffTime = dateTimeService.getCalendar(scheduleCutoffTimeTemp);
            }
            String cutoffParameter = parameterService.getParameterValueAsString(ScheduleStep.class, KFSConstants.SystemGroupParameterNames.BATCH_SCHEDULE_CUTOFF_TIME);
            String[] scheduleStepCutoffTime = StringUtils.split(cutoffParameter, ":");
            if (scheduleStepCutoffTime.length != 3 && scheduleStepCutoffTime.length != 4) {
                throw new IllegalArgumentException("Error! The " + KFSConstants.SystemGroupParameterNames.BATCH_SCHEDULE_CUTOFF_TIME + " parameter had an invalid value: " + cutoffParameter);
            }
            // if there are 4 components, then we have an AM/PM delimiter
            // otherwise, assume 24-hour time
            if (scheduleStepCutoffTime.length == 4) {
                int hour = Integer.parseInt(scheduleStepCutoffTime[0]);
                // need to adjust for meaning of hour
                if (hour == 12) {
                    hour = 0;
                } else {
                    hour--;
                }
                scheduleCutoffTime.set(Calendar.HOUR, hour);
                if (StringUtils.containsIgnoreCase(scheduleStepCutoffTime[3], "AM")) {
                    scheduleCutoffTime.set(Calendar.AM_PM, Calendar.AM);
                } else {
                    scheduleCutoffTime.set(Calendar.AM_PM, Calendar.PM);
                }
            } else {
                scheduleCutoffTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(scheduleStepCutoffTime[0]));
            }
            scheduleCutoffTime.set(Calendar.MINUTE, Integer.parseInt(scheduleStepCutoffTime[1]));
            scheduleCutoffTime.set(Calendar.SECOND, Integer.parseInt(scheduleStepCutoffTime[2]));
            if (parameterService.getParameterValueAsBoolean(ScheduleStep.class, KFSConstants.SystemGroupParameterNames.BATCH_SCHEDULE_CUTOFF_TIME_IS_NEXT_DAY)) {
                scheduleCutoffTime.add(Calendar.DAY_OF_YEAR, 1);
            }
            boolean isPastScheduleCutoffTime = dateTime.after(scheduleCutoffTime);
            if (log) {
                LOG.info(new StringBuilder("isPastScheduleCutoffTime=").append(isPastScheduleCutoffTime).append(" : ").append(dateTimeService.toDateTimeString(dateTime.getTime())).append(" / ").append(dateTimeService.toDateTimeString(scheduleCutoffTime.getTime())));
            }
            return isPastScheduleCutoffTime;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Caught exception while checking whether we've exceeded the schedule cutoff time", e);
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while checking whether we've exceeded the schedule cutoff time", e);
        }
    }

    @Override
    public void processWaitingJobs() {
        LOG.debug("processWaitingJobs() started");

        for (String scheduledJobName : getJobNamesForScheduleJob()) {
            JobDetail jobDetail = getScheduledJobDetail(scheduledJobName);
            if (isPending(jobDetail)) {
                if (shouldScheduleJob(jobDetail)) {
                    scheduleJob(SCHEDULED_GROUP, scheduledJobName, 0, 0, new Date(), null, Collections.singletonMap(Job.MASTER_JOB_NAME, SCHEDULE_JOB_NAME));
                }
                if (shouldCancelJob(jobDetail)) {
                    updateStatus(SCHEDULED_GROUP, scheduledJobName, CANCELLED_JOB_STATUS_CODE);
                }
            }
        }
    }

    @Override
    public void logScheduleResults() {
        LOG.debug("logScheduleResults() started");

        StringBuilder scheduleResults = new StringBuilder("The schedule completed.");
        for (String scheduledJobName : getJobNamesForScheduleJob()) {
            JobDetail jobDetail = getScheduledJobDetail(scheduledJobName);
            if (jobDetail != null && !SCHEDULE_JOB_NAME.equals(jobDetail.getKey().getName())) {
                scheduleResults.append("\n\t").append(jobDetail.getKey().getName()).append("=").append(getStatus(jobDetail));
            }
        }
        LOG.info(scheduleResults);
    }

    @Override
    public boolean shouldNotRun(JobDetail jobDetail) {
        LOG.debug("shouldNotRun() started");

        if (SCHEDULED_GROUP.equals(jobDetail.getKey().getGroup())) {
            if (isCancelled(jobDetail)) {
                LOG.info("Telling listener not to run job, because it has been cancelled: " + jobDetail.getKey().getName());
                return true;
            } else {
                for (String dependencyJobName : getJobDependencies(jobDetail.getKey().getName()).keySet()) {
                    if (!isDependencySatisfiedPositively(jobDetail, getScheduledJobDetail(dependencyJobName))) {
                        LOG.info("Telling listener not to run job, because a dependency has not been satisfied positively: " + jobDetail.getKey().getName() + " (dependency job = " + dependencyJobName + ")");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void updateStatus(JobDetail jobDetail, String jobStatus) {
        LOG.info("Updating status of job: " + jobDetail.getKey().getName() + "=" + jobStatus);

        jobDetail.getJobDataMap().put(JOB_STATUS_PARAMETER, jobStatus);
    }

    @Override
    public void runJob(String jobName, String requestorEmailAddress) {
        LOG.debug("runJob() started");

        runJob(jobName, 0, 0, new Date(), requestorEmailAddress);
    }

    @Override
    public void runJob(String jobName, int startStep, int stopStep, Date startTime, String requestorEmailAddress) {
        LOG.debug("runJob() started");

        runJob(UNSCHEDULED_GROUP, jobName, startStep, stopStep, startTime, requestorEmailAddress);
    }

    @Override
    public void runJob(String groupName, String jobName, int startStep, int stopStep, Date jobStartTime, String requestorEmailAddress) {
        LOG.info("Executing user initiated job: " + groupName + "." + jobName + " (startStep=" + startStep + " / stopStep=" + stopStep + " / startTime=" + jobStartTime + " / requestorEmailAddress=" + requestorEmailAddress + ")");

        try {
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(jobName, groupName));
            scheduleJob(groupName, jobName, startStep, stopStep, jobStartTime, requestorEmailAddress, null);
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to run a job directly", ex);
        }
    }

    public void runStep(String groupName, String jobName, String stepName, Date startTime, String requestorEmailAddress) {
        LOG.info("Executing user initiated step: " + stepName + " / requestorEmailAddress=" + requestorEmailAddress);

        // abort if the step is already running
        if (isJobRunning(jobName)) {
            LOG.warn("Attempt to run job already executing, aborting");
            return;
        }
        int stepNum = 1;
        boolean stepFound = false;
        BatchJobStatus job = getJob(groupName, jobName);
        for (Step step : job.getSteps()) {
            if (step.getName().equals(stepName)) {
                stepFound = true;
                break;
            }
            stepNum++;
        }
        if (stepFound) {
            runJob(groupName, jobName, stepNum, stepNum, startTime, requestorEmailAddress);
        } else {
            LOG.warn("Unable to find step " + stepName + " in job " + groupName + "." + jobName);
        }
    }

    @Override
    public boolean isJobRunning(String jobName) {
        LOG.debug("isJobRunning() started");

        List<JobExecutionContext> runningJobs = getRunningJobs();
        for (JobExecutionContext jobCtx : runningJobs) {
            if (jobCtx.getJobDetail().getKey().getName().equals(jobName)) {
                return true;
            }
        }
        return false;
    }

    protected void addJob(JobDetail jobDetail) {
        try {
            LOG.info("Adding job: " + jobDetail.getKey().getName()+"-"+jobDetail.getKey().getGroup());

            scheduler.addJob(jobDetail, true);
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while adding job: " + jobDetail.getKey().getName()+"-"+jobDetail.getKey().getGroup(), e);
        }
    }

    protected void addTrigger(Trigger trigger) {
        try {
            if (UNSCHEDULED_GROUP.equals(trigger.getKey().getGroup())) {
                LOG.error("Triggers should not be specified for jobs in the unscheduled group - not adding trigger: " + trigger.getKey().getName());
            } else {
                LOG.info("Adding trigger: " + trigger.getKey().getName());
                try {
                    scheduler.scheduleJob(trigger);
                } catch (ObjectAlreadyExistsException ex) {
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while adding trigger: " + trigger.getKey().getName()+"-"+trigger.getKey().getGroup(), e);
        }
    }

    protected void scheduleJob(String groupName, String jobName, int startStep, int endStep, Date startTime, String requestorEmailAddress, Map<String, String> additionalJobData) {
        try {
            updateStatus(groupName, jobName, SchedulerService.SCHEDULED_JOB_STATUS_CODE);
            SimpleTriggerDescriptor trigger = new SimpleTriggerDescriptor(jobName, groupName, jobName, dateTimeService);
            trigger.setStartTime(startTime);
            Trigger qTrigger = trigger.getTrigger();
            qTrigger.getJobDataMap().put(JobListener.REQUESTOR_EMAIL_ADDRESS_KEY, requestorEmailAddress);
            qTrigger.getJobDataMap().put(Job.JOB_RUN_START_STEP, String.valueOf(startStep));
            qTrigger.getJobDataMap().put(Job.JOB_RUN_END_STEP, String.valueOf(endStep));
            if (additionalJobData != null) {
                qTrigger.getJobDataMap().putAll(additionalJobData);
            }
            for (Trigger oldTrigger : scheduler.getTriggersOfJob(new JobKey(jobName, groupName))) {
                scheduler.unscheduleJob(oldTrigger.getKey());
            }
            scheduler.scheduleJob(qTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while scheduling job: " + jobName, e);
        }
    }

    protected boolean shouldScheduleJob(JobDetail jobDetail) {
        try {
            if (scheduler.getTriggersOfJob(new JobKey(jobDetail.getKey().getName(), SCHEDULED_GROUP)).size() > 0) {
                return false;
            }
            for (String dependencyJobName : getJobDependencies(jobDetail.getKey().getName()).keySet()) {
                JobDetail dependencyJobDetail = getScheduledJobDetail(dependencyJobName);
                if (dependencyJobDetail == null) {
                    LOG.error("Unable to get JobDetail for dependency of " + jobDetail.getKey().getName() + " : " + dependencyJobName);
                    return false;
                }
                if (!isDependencySatisfiedPositively(jobDetail, dependencyJobDetail)) {
                    return false;
                }
            }
        } catch (SchedulerException se) {
            throw new RuntimeException("Caught scheduler exception while determining whether to schedule job: " + jobDetail.getKey().getName(), se);
        }
        return true;
    }

    protected boolean shouldCancelJob(JobDetail jobDetail) {
        if (jobDetail == null) {
            return true;
        }
        LOG.info("shouldCancelJob:::::: " + jobDetail.getKey().getName()+"-"+jobDetail.getKey().getGroup());

        for (String dependencyJobName : getJobDependencies(jobDetail.getKey().getName()).keySet()) {
            LOG.info("dependencyJobName:::::" + dependencyJobName);
            JobDetail dependencyJobDetail = getScheduledJobDetail(dependencyJobName);
            if (isDependencySatisfiedNegatively(jobDetail, dependencyJobDetail)) {
                LOG.info("cancelling " + jobDetail.getKey().getName()+"-"+jobDetail.getKey().getGroup() + " because dependency " + dependencyJobName + " was \"satisfied negatively\"");
                return true;
            }
        }
        return false;
    }

    protected boolean isDependencySatisfiedPositively(JobDetail dependentJobDetail, JobDetail dependencyJobDetail) {
        if (dependentJobDetail == null || dependencyJobDetail == null) {
            return false;
        }
        return isSucceeded(dependencyJobDetail) || ((isFailed(dependencyJobDetail) || isCancelled(dependencyJobDetail)) && isSoftDependency(dependentJobDetail.getKey().getName(), dependencyJobDetail.getKey().getName()));
    }

    protected boolean isDependencySatisfiedNegatively(JobDetail dependentJobDetail, JobDetail dependencyJobDetail) {
        if (dependentJobDetail == null || dependencyJobDetail == null) {
            return true;
        }

        LOG.info("isDependencySatisfiedNegatively::::  dependentJobDetail::: " + dependencyJobDetail.getKey().getName()+"-"+dependencyJobDetail.getKey().getGroup() + " dependencyJobDetail    " + dependencyJobDetail.getKey().getName()+"-"+dependencyJobDetail.getKey().getGroup());
        return (isFailed(dependencyJobDetail) || isCancelled(dependencyJobDetail)) && !isSoftDependency(dependentJobDetail.getKey().getName(), dependencyJobDetail.getKey().getName());
    }

    protected boolean isSoftDependency(String dependentJobName, String dependencyJobName) {
        return SOFT_DEPENDENCY_CODE.equals(getJobDependencies(dependentJobName).get(dependencyJobName));
    }

    protected Map<String, String> getJobDependencies(String jobName) {
        LOG.info("getJobDependencies:::: for job " + jobName);
        return BatchSpringContext.getJobDescriptor(jobName).getDependencies();
    }

    protected boolean isPending(JobDetail jobDetail) {
        return getStatus(jobDetail) == null;
    }

    protected boolean isScheduled(JobDetail jobDetail) {
        return SCHEDULED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isSucceeded(JobDetail jobDetail) {
        return SUCCEEDED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isFailed(JobDetail jobDetail) {
        return FAILED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isCancelled(JobDetail jobDetail) {
        return CANCELLED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    @Override
    public String getStatus(JobDetail jobDetail) {
        LOG.debug("getStatus() started");

        if (jobDetail == null) {
            return FAILED_JOB_STATUS_CODE;
        }
        KfsModuleServiceImpl moduleService = (KfsModuleServiceImpl)
            SpringContext.getBean(KualiModuleService.class).getResponsibleModuleServiceForJob(jobDetail.getKey().getName());
        //If the module service has status information for a job, get the status from it
        //else get status from job detail data map
        return (moduleService != null && moduleService.isExternalJob(jobDetail.getKey().getName()))
            ? moduleService.getExternalJobStatus(jobDetail.getKey().getName())
            : jobDetail.getJobDataMap().getString(SchedulerServiceImpl.JOB_STATUS_PARAMETER);
    }

    protected JobDetail getScheduledJobDetail(String jobName) {
        LOG.info("getScheduledJobDetail ::::::: " + jobName);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(jobName, SCHEDULED_GROUP));
            if (jobDetail == null) {
                LOG.error("Unable to obtain the job details for the scheduled version of: " + jobName);
            }
            return jobDetail;
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught scheduler exception while getting job detail: " + jobName, e);
        }
    }

    @Override
    public List<BatchJobStatus> getJobs() {
        LOG.debug("getJobs() started");

        ArrayList<BatchJobStatus> jobs = new ArrayList<>();
        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
                    try {
                        JobDescriptor jobDescriptor = retrieveJobDescriptor(jobKey.getName());
                        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                        jobs.add(new BatchJobStatus(jobDescriptor, jobDetail));
                    } catch (NoSuchBeanDefinitionException ex) {
                        // do nothing, ignore jobs not defined in spring
                        LOG.warn("Attempt to find bean " + jobKey.getGroup() + "." + jobKey.getName() + " failed - not in Spring context");
                    }
                }
        } catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
        return jobs;
    }

    @Override
    public BatchJobStatus getJob(String groupName, String jobName) {
        LOG.debug("getJob() started");

        for (BatchJobStatus job : getJobs()) {
            if (job.getName().equals(jobName) && job.getGroup().equals(groupName)) {
                return job;
            }
        }
        return null;
    }

    @Override
    public List<BatchJobStatus> getJobs(String groupName) {
        LOG.debug("getJobs() started");

        ArrayList<BatchJobStatus> jobs = new ArrayList<>();
        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(groupName))) {
                try {
                    JobDescriptor jobDescriptor = retrieveJobDescriptor(jobKey.getName());
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    jobs.add(new BatchJobStatus(jobDescriptor, jobDetail));
                } catch (NoSuchBeanDefinitionException ex) {
                    // do nothing, ignore jobs not defined in spring
                    LOG.warn("Attempt to find bean " + groupName + "." + jobKey.getName() + " failed - not in Spring context");
                }
            }
        } catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
        return jobs;
    }

    @Override
    public List<JobExecutionContext> getRunningJobs() {
        LOG.debug("getRunningJobs() started");

        try {
            List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
            return jobContexts;
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to get list of running jobs.", ex);
        }
    }

    protected void updateStatus(String groupName, String jobName, String jobStatus) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(jobName, groupName));
            updateStatus(jobDetail, jobStatus);
            scheduler.addJob(jobDetail, true);
        } catch (SchedulerException e) {
            throw new RuntimeException(new StringBuilder("Caught scheduler exception while updating job status: ").append(jobName).append(", ").append(jobStatus).toString(), e);
        }
    }

    @Override
    public void removeScheduled(String jobName) {
        LOG.debug("removeScheduled() started");

        try {
            scheduler.deleteJob(new JobKey(jobName, SCHEDULED_GROUP));
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to remove scheduled job: " + jobName, ex);
        }
    }

    @Override
    public void addScheduled(JobDetail job) {
        LOG.debug("addScheduled() started");

        try {
            JobBuilder builder = JobBuilder.newJob();
            builder.usingJobData(job.getJobDataMap());
            builder.withIdentity(job.getKey().getName(), SCHEDULED_GROUP);
            builder.ofType(job.getJobClass());
            builder.storeDurably(job.isDurable());

            scheduler.addJob(builder.build(), true);
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to add job to scheduled group: " + job.getKey().getName(), ex);
        }
    }

    @Override
    public void addUnscheduled(JobDetail job) {
        LOG.debug("addUnscheduled() started");

        try {
            JobBuilder builder = JobBuilder.newJob();
            builder.usingJobData(job.getJobDataMap());
            builder.withIdentity(job.getKey().getName(), UNSCHEDULED_GROUP);
            builder.ofType(job.getJobClass());
            builder.storeDurably(job.isDurable());


            scheduler.addJob(builder.build(), true);
        } catch (SchedulerException ex) {
            throw new RuntimeException("Unable to add job to unscheduled group: " + job.getKey().getName(), ex);
        }
    }

    @Override
    public List<String> getSchedulerGroups() {
        LOG.debug("getSchedulerGroups() started");

        try {
            return scheduler.getJobGroupNames();
        } catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
    }

    @Override
    public List<String> getJobStatuses() {
        return jobStatuses;
    }

    @Override
    public void interruptJob(String jobName) {
        LOG.debug("interruptJob() started");

        List<JobExecutionContext> runningJobs = getRunningJobs();
        for (JobExecutionContext jobCtx : runningJobs) {
            if (jobName.equals(jobCtx.getJobDetail().getKey().getName())) {
                try {
                    ((Job) jobCtx.getJobInstance()).interrupt();
                } catch (UnableToInterruptJobException ex) {
                    LOG.warn("Unable to perform job interrupt", ex);
                }
                break;
            }
        }
    }

    @Override
    public Date getNextStartTime(BatchJobStatus job) {
        LOG.debug("getNextStartTime() started");

        try {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(job.getName(), job.getGroup()));
            Date nextDate = new Date(Long.MAX_VALUE);
            for (Trigger trigger : triggers) {
                if (trigger.getNextFireTime() != null) {
                    if (trigger.getNextFireTime().getTime() < nextDate.getTime()) {
                        nextDate = trigger.getNextFireTime();
                    }
                }
            }
            if (nextDate.getTime() == Long.MAX_VALUE) {
                nextDate = null;
            }
            return nextDate;
        } catch (SchedulerException ex) {

        }
        return null;
    }

    @Override
    public Date getNextStartTime(String groupName, String jobName) {
        LOG.debug("getNextStartTime() started");

        BatchJobStatus job = getJob(groupName, jobName);

        return getNextStartTime(job);
    }

    protected JobDescriptor retrieveJobDescriptor(String jobName) {
        if (externalizedJobDescriptors.containsKey(jobName)) {
            return externalizedJobDescriptors.get(jobName);
        }
        return BatchSpringContext.getJobDescriptor(jobName);
    }

    protected List<String> getJobNamesForScheduleJob() {
        List<String> jobNames = new ArrayList<>();
        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(SCHEDULED_GROUP))) {
                if (scheduler.getTriggersOfJob(jobKey).size() == 0) {
                    // jobs that have their own triggers will not be included in the master scheduleJob
                    jobNames.add(jobKey.getName());
                }
            }
        } catch (Exception ex) {
            LOG.error("Error occurred while initializing job name list", ex);
        }
        return jobNames;
    }

    @Override
    public void reinitializeScheduledJobs() {
        LOG.debug("reinitializeScheduledJobs() started");

        try {
            for (String scheduledJobName : getJobNamesForScheduleJob()) {
                updateStatus(SCHEDULED_GROUP, scheduledJobName, null);
            }
        } catch (Exception e) {
            LOG.error("Error occurred while trying to reinitialize jobs", e);
        }
    }

    @Override
    public boolean cronConditionMet(String cronExpressionString) {
        LOG.debug("cronConditionMet() started");

        boolean cronConditionMet = false;

        CronExpression cronExpression;
        try {
            cronExpression = new CronExpression(cronExpressionString);
            Date currentDate = dateTimeService.getCurrentDate();
            Date validTimeAfter = cronExpression.getNextValidTimeAfter(dateTimeService.getCurrentDate());
            if (validTimeAfter != null) {
                String cronDate = dateTimeService.toString(validTimeAfter, KFSConstants.MONTH_DAY_YEAR_DATE_FORMAT);
                if (cronDate.equals(dateTimeService.toString(currentDate, KFSConstants.MONTH_DAY_YEAR_DATE_FORMAT))) {
                    cronConditionMet = true;
                }
            } else {
                LOG.error("Null date returned when calling CronExpression.nextValidTimeAfter() for cronExpression: " + cronExpressionString);
            }
        } catch (ParseException ex) {
            LOG.error("Error parsing cronExpression: " + cronExpressionString, ex);
        }

        return cronConditionMet;
    }

    @Override
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setKualiModuleService(KualiModuleService moduleService) {
        this.kualiModuleService = moduleService;
    }

    public void setJobListener(JobListener jobListener) {
        this.jobListener = jobListener;
    }
}
