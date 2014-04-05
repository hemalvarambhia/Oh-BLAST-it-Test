package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.models.BLASTQuery;

public class StubbedBLASTSearchEngine implements BLASTSearchEngine {

	public static final String QUERY_ID = "GHJBCNKR014";
	
	public String submit(BLASTQuery query) {
		return QUERY_ID;
	}

	public SearchStatus pollQuery(String jobId) {
		return SearchStatus.RUNNING;
	}

	public String retrieveBLASTResults(String jobId, String format) {
		return "<blasthits></blasthits>";
	}

	public void close() {
	}
}
