/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.springxd.job;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameter.ParameterType;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * HdfsTasklet - JobTasklet that allows execution of hdfs data movement
 * 
 * TODO: Look at the spring hadoop script tasklet
 * 
 * @author mborges
 *
 */
public class NullTasklet implements Tasklet, StepExecutionListener {

	private static final Log logger = LogFactory.getLog(NullTasklet.class);
	private volatile AtomicInteger counter = new AtomicInteger(1); 

	private boolean shouldFail = false;
	
	public NullTasklet() {
		super();
	}

	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {

		shouldFail = false;
		
		logger.info("Started.....");

		if(counter.compareAndSet(2, 0)) {
			logger.info("counter reset to 0. Execution will succeed");
		} else {
			counter.incrementAndGet();
			throw new IllegalStateException("This job fails every other time");
		}
		
		StepExecution stepExecution = chunkContext.getStepContext()
				.getStepExecution();
		JobParameters jobParameters = stepExecution.getJobParameters();

		if (jobParameters != null && !jobParameters.isEmpty()) {

			final Set<Entry<String, JobParameter>> parameterEntries = jobParameters
					.getParameters().entrySet();

			logger.info(String.format(
					"The following %s Job Parameter(s) is/are present:",
					parameterEntries.size()));

			for (Entry<String, JobParameter> jobParameterEntry : parameterEntries) {

				String pName = jobParameterEntry.getKey();
				boolean pIsIdentifying = jobParameterEntry.getValue()
						.isIdentifying();
				ParameterType pType = jobParameterEntry.getValue().getType();
				Object pValue = jobParameterEntry.getValue().getValue();

				logger.info(String
						.format("Parameter name: %s; isIdentifying: %s; type: %s; value: %s",
								pName, pIsIdentifying, pType.toString(), pValue));
				
				if(pName.equalsIgnoreCase("jobStatus") && !pValue.equals("COMPLETED")) {
					shouldFail = true;
				}
			}

		}

		return RepeatStatus.FINISHED;

	}
	
	public void beforeStep(StepExecution arg0) {
		// TODO Auto-generated method stub		
	}

	public ExitStatus afterStep(StepExecution stepExecution) {
		if (shouldFail) {
			logger.info("Job is going to simulate a failure.");
			stepExecution.setStatus(BatchStatus.FAILED);
			return ExitStatus.FAILED;
		} else {
			logger.info("Job completed.");
			return ExitStatus.COMPLETED;
		}
	}

}
