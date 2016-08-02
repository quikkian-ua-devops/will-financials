package org.kuali.kfs.pdp.batch;

import java.util.Date;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.batch.service.AutoCheckFormatService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;

public class FormatCheckACHByCustomerProfileStep extends AbstractStep {

	private AutoCheckFormatService autoCheckFormatService;
	private ParameterService parameterService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kuali.kfs.kns.bo.Step#execute(java.lang.String, java.util.Date)
	 */
	@Override
	public boolean execute(String jobName, Date jobRunDate)
			throws InterruptedException {

		// checks selected will be based on customer profile set in parameter.
		// If value is null, then ALL customer profiles will be selected.
		String customerProfileId = getParameterService()
				.getParameterValueAsString(
						KFSConstants.CoreModuleNamespaces.PDP,
						PdpConstants.FormatCheckACHParameters.PDP_FORMAT_CHECK_ACH_BY_CUST_PROF_STEP,
						PdpConstants.FORMAT_CUSTOMER_PROFILE_ID);

		return autoCheckFormatService
				.processChecksByCustomerProfile(customerProfileId);
	}

	/**
	 * @return
	 */
	public AutoCheckFormatService getAutoCheckFormatService() {
		return autoCheckFormatService;
	}

	/**
	 * @param autoCheckFormatService
	 */
	public void setAutoCheckFormatService(
			AutoCheckFormatService autoCheckFormatService) {
		this.autoCheckFormatService = autoCheckFormatService;
	}

	/**
	 * @return the parameterService
	 */
	public ParameterService getParameterService() {
		return parameterService;
	}

	/**
	 * @param parameterService
	 *            the parameterService to set
	 */
	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}