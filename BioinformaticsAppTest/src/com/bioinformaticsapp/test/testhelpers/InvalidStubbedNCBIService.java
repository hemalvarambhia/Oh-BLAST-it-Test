package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;

public class InvalidStubbedNCBIService implements BLASTSearchEngine {

	public String submit(BLASTQuery query) throws IllegalBLASTQueryException {
		throw new IllegalBLASTQueryException("Query was invalid");
	}

	public SearchStatus pollQuery(String jobId) {
		return null;
	}

	public String retrieveBLASTResults(String jobId, String format) {
		return null;
	}

	public void close() {
		
	}

}
