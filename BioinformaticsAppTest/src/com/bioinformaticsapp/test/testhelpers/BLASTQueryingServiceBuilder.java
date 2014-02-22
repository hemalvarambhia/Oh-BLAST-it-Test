package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;

public class BLASTQueryingServiceBuilder {

	public BLASTSearchEngine buildServiceThatReturnsJobIdentifier(final String jobIdentifier){
		
		BLASTSearchEngine mockService = new BLASTSearchEngine() {
			
			public String submit(BLASTQuery query) throws IllegalBLASTQueryException {
				return jobIdentifier;
			}
			
			public String retrieveBLASTResults(String jobId, String format) {
				// Not tested
				
				return null;
			}
			
			public SearchStatus pollQuery(String jobId) {
				// Not tested
				return null;
			}

			public void close() {
				
			}
			
		};
		
		return mockService;
		
	}
	
}
