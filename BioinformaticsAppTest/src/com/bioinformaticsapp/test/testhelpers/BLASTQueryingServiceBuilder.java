package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.web.BLASTSequenceQueryingService;

public class BLASTQueryingServiceBuilder {

	public BLASTSequenceQueryingService buildServiceThatReturnsJobIdentifier(final String jobIdentifier){
		
		BLASTSequenceQueryingService mockService = new BLASTSequenceQueryingService() {
			
			public String submit(BLASTQuery query) throws IllegalBLASTQueryException {
				return jobIdentifier;
			}
			
			public String retrieveBLASTResults(String jobId, String format) {
				// Not tested
				
				return null;
			}
			
			public Status pollQuery(String jobId) {
				// Not tested
				return null;
			}

			public void close() {
				
			}
			
		};
		
		return mockService;
		
	}
	
}
