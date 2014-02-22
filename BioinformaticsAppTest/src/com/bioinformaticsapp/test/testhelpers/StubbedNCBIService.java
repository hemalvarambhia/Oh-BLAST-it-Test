package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;

public class StubbedNCBIService implements BLASTSearchEngine {

	public String submit(BLASTQuery query) throws IllegalBLASTQueryException {
		return "GHJBCNKR014";
	}

	public SearchStatus pollQuery(String jobId) {
		return SearchStatus.RUNNING;
	}

	public String retrieveBLASTResults(String jobId, String format) {
		return "";
	}

	public void close() {
	}

}
