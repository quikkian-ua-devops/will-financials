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
package org.kuali.kfs.sys.batch.service;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.batch.JobDescriptor;
import org.kuali.kfs.sys.batch.JobListener;
import org.kuali.kfs.sys.batch.SimpleTriggerDescriptor;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@ConfigureContext(session = UserNameFixture.kfs, initializeBatchSchedule = true)
public class SchedulerServiceImplTest extends KualiTestBase {

    private String batchDirectory;

    /**
     * Creates the directory for the batch files to go in
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // not sure why we have to do this, but it seems to be necessary for the some of the tests to work (and jobs to actually be run)
        Scheduler scheduler = (Scheduler) SpringContext.getService("scheduler");
        if (!scheduler.isStarted()) {
            scheduler.start();
        }

        batchDirectory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory") + "/gl/test_directory/originEntry";
        File batchDirectoryFile = new File(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory") + "/gl/test_directory/");
        batchDirectoryFile.mkdir();
        batchDirectoryFile = new File(batchDirectory);
        batchDirectoryFile.mkdir();

        File nightlyOutFile = new File(batchDirectory + File.separator + GeneralLedgerConstants.BatchFileSystem.NIGHTLY_OUT_FILE);
        if (!nightlyOutFile.exists()) {
            nightlyOutFile.createNewFile();
        }
    }

    /**
     * Removes the directory for the batch files to go in
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        File batchDirectoryFile = new File(batchDirectory);
        for (File f : batchDirectoryFile.listFiles()) {
            f.delete();
        }
        batchDirectoryFile.delete();
        batchDirectoryFile = new File(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory") + "/gl/test_directory");
        batchDirectoryFile.delete();
    }

    // tests added to make sure that the scheduler was available during the tests
    public void testGetJobs_unscheduled() {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        List<BatchJobStatus> jobs = s.getJobs("unscheduled");
        for (BatchJobStatus job : jobs) {
            System.out.println(job);
        }
    }

    public void testGetJobs_scheduled() {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        List<BatchJobStatus> jobs = s.getJobs("scheduled");
        for (BatchJobStatus job : jobs) {
            System.out.println(job);
        }
    }

    /*
     * // this job seems to be missing from the system during the test runs public void testScheduledJobTrigger() throws Exception {
     * SchedulerService s = SpringContext.getBean(SchedulerService.class); BatchJobStatus job = s.getJob( "scheduled", "scheduleJob"
     * ); assertNotNull( "job must not be null", job ); System.out.println( "scheduleJob Next Run Time: " + job.getNextRunDate() );
     * Calendar cal = Calendar.getInstance(); cal.setTime( job.getNextRunDate() ); assertEquals( "year must be 2099", 2099, cal.get(
     * Calendar.YEAR ) ); }
     */


    /**
     * Test the running of a job. There is not much to test on the results, except that this does not cause an error.
     */
    public void testRunJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob");
        assertNotNull("job must not be null", job);
        System.out.println(job);
        job.runJob(null);

        System.err.println("testRunJob: Waiting for it to enter running status");
        // provide an "out" in case things fail badly
        int waitCount = 0;
        while (!job.isRunning() && waitCount < 500) {
            Thread.sleep(100);
            waitCount++;
        }

        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));

        System.err.println("testRunJob: Waiting for it to leave running status");

        waitCount = 0;
        while (job.isRunning() && waitCount < 500) {
            Thread.sleep(100);
            waitCount++;
        }
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
    }

    /**
     * Test that the unschedule job function works and removes the job from the standard scheduled group. Assumes: scrubberJob
     * exists as a job in the scheduled group.
     */
    public void testUnscheduleJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.SCHEDULED_GROUP, "autoDisapproveJob");
        assertNotNull("job must not be null", job);

        assertTrue("must return isScheduled == true", job.isScheduled());

        s.removeScheduled(job.getName());
        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "autoDisapproveJob");
        assertNull("new attempt to retrieve job must be null", job);

        job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "autoDisapproveJob");
        assertNotNull("job must not be null", job);
        assertFalse("must return isScheduled == false", job.isScheduled());
    }

    /**
     * Test that the schedule job function works and puts the job into the standard scheduled group. Also tests to make sure that
     * BatchJobStatus detects the scheduled status even if it is in the unscheduled group. Assumes: clearOldOriginEntriesJob exists
     * as a job in the unscheduled group.
     */
    public void testScheduleJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "manualPurgeJob");
        assertNotNull("job must not be null", job);

        assertFalse("must return isScheduled == false", job.isScheduled());

        job.schedule();

        job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "manualPurgeJob");
        assertNotNull("job (in unsched group) must not be null", job);
        assertTrue("must return isScheduled == true", job.isScheduled());

        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "manualPurgeJob");
        assertNotNull("job (in sched group) must not be null", job);
        assertTrue("must return isScheduled == true", job.isScheduled());

    }

    protected void scheduleJob(String groupName, String jobName, int startStep, int endStep, Date startTime, String requestorEmailAddress, Map<String, String> additionalJobData) {
        Scheduler scheduler = (Scheduler) SpringContext.getService("scheduler");
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
            if (jobDetail == null) {
                fail("Unable to retrieve JobDetail object for " + groupName + " : " + jobName);
            }
            if (jobDetail.getJobDataMap() == null) {
                jobDetail.setJobDataMap(new JobDataMap());
            }
            jobDetail.getJobDataMap().put(SchedulerService.JOB_STATUS_PARAMETER, SchedulerService.SCHEDULED_JOB_STATUS_CODE);
            scheduler.addJob(jobDetail, true);

            SimpleTriggerDescriptor trigger = new SimpleTriggerDescriptor(jobName + startTime, groupName, jobName, SpringContext.getBean(DateTimeService.class));
            trigger.setStartTime(startTime);
            Trigger qTrigger = trigger.getTrigger();
            qTrigger.getJobDataMap().put(JobListener.REQUESTOR_EMAIL_ADDRESS_KEY, requestorEmailAddress);
            qTrigger.getJobDataMap().put(Job.JOB_RUN_START_STEP, String.valueOf(startStep));
            qTrigger.getJobDataMap().put(Job.JOB_RUN_END_STEP, String.valueOf(endStep));
            if (additionalJobData != null) {
                qTrigger.getJobDataMap().putAll(additionalJobData);
            }
            scheduler.scheduleJob(qTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while scheduling job: " + jobName, e);
        }
    }


    /*
     * This test has problems with timing. It needs a job that it can rely on running for a specified period of time before being
     * included in the automated tests.
     */

    public void testJobInterrupt() throws Exception {
        // need to clear out the dependencies
        JobDescriptor jd = SpringContext.getBean(JobDescriptor.class, "scrubberJob");
        jd.getDependencies().clear();

        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        // need to ensure that the job does not already have a status (from prior tests)
//        s.updateStatus(jd.getJobDetail(), null);

        System.err.println("About to run scrubber job in testJobInterrupt");
        scheduleJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob", 0, 0, new Date(), null, null);
        BatchJobStatus job = s.getJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob");
        // wait for job to enter running status
        int waitCount = 0;
        System.err.println("Waiting for it to enter running status");
        // provide an "out" in case things fail badly
        while (!job.isRunning() && waitCount < 500) {
            Thread.sleep(20);
            waitCount++;
        }
        // stop the job
        System.err.println("Interrupting the job");
        job.interrupt();
        waitCount = 0;
        System.err.println("Waiting for it to exit running status");
        while (job.isRunning() && waitCount < 500) {
            Thread.sleep(20);
            waitCount++;
        }
        Thread.sleep(2000);
        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob");
        List<BatchJobStatus> jobs = s.getJobs();
        for (BatchJobStatus b : jobs) {
            if (b.getName().equals("scrubberJob")) {
                System.out.println(b);
            }
        }
        assertEquals("job status not correct", SchedulerService.CANCELLED_JOB_STATUS_CODE, job.getStatus());
    }

    /**
     * Verify that dropDependenciesNotScheduled drops unscheduled dependencies. It's worthwhile to note that spring-sys-test-xml was altered to include a fake
     * dependency of purgeReportsAndStagingJob on dailyEmailJob. This fake dependency was added to have a scheduled job dependend on an unscheduled one so that
     * we have something to test for.
     *
     * @throws Exception
     */
    public void testDropDependenciesNotScheduled() throws Exception {
        SchedulerService schedulerService = SpringContext.getBean(SchedulerService.class);

        BatchJobStatus purgeReportsAndStagingJob = schedulerService.getJob(SchedulerService.SCHEDULED_GROUP, "purgeReportsAndStagingJob");
        for (Entry<String, String> dependency : purgeReportsAndStagingJob.getDependencies().entrySet()) {
            String dependencyJobName = dependency.getKey();
            assertFalse("Job was expected to be unscheduled so dropDependenciesNotScheduled should have removed this dependency.", "dailyEmailJob".equals(dependencyJobName));
        }
    }

    public void testCronConditionMet() {
        SchedulerService schedulerService = SpringContext.getBean(SchedulerService.class);

        //Set year to 2199 for this cron expression so it won't match, at least not for almost two centuries
        // and if KFS is still running by then, I'm guessing there are larger concerns than just this test.
        String cronExpression = "0 * * ? * 5#3 2199";
        assertFalse(schedulerService.cronConditionMet(cronExpression));

        // The next valid date for this cron expression should be today so it should always match
        cronExpression = "0 * * ? * 1-7";
        assertTrue(schedulerService.cronConditionMet(cronExpression));

    }
}
