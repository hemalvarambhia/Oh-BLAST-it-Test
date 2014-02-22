package com.bioinformaticsapp.test.testhelpers;

import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;

public class StubbedEMBLService implements BLASTSearchEngine {

	public String submit(BLASTQuery query) throws IllegalBLASTQueryException {
		return "ncbiblast-R20140222-155703-0120-63596495-oy";
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
