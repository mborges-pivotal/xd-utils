/**
 * job-status - parses the output of a Job listener Tap and 
 *              stops the stream unless the Job Status is COMPLETED
 *
 * @Author Marcelo Borges
 *
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 */

import org.apache.commons.logging.LogFactory;

def jobStatus = payload.getStatus().toString();
boolean jobCompleted = jobStatus.equals('COMPLETED') ? true : false

logger = LogFactory.getLog("job-status");
logger.info(payload)

if (!jobCompleted) {
	return null
}

return "{\"jobStatus\":\"${jobStatus}\"}"